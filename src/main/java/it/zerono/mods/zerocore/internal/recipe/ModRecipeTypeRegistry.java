package it.zerono.mods.zerocore.internal.recipe;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.network.payload.splitter.PayloadSplitter;
import it.zerono.mods.zerocore.lib.recipe.IModRecipe;
import it.zerono.mods.zerocore.lib.recipe.IModRecipeType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

public class ModRecipeTypeRegistry {

    public static void initialize() {
        NeoForge.EVENT_BUS.addListener(ModRecipeTypeRegistry::onOnDatapackSyncEvent);
    }

    public static void registerPackets(PayloadRegistrar registrar) {

        registrar.playToClient(ClientRecipesClearPayload.TYPE, ClientRecipesClearPayload.STREAM_CODEC,
                ($, context) -> onClientRecipesClear(context));

        registrar.playToClient(ClientRecipesSyncPayload.TYPE,
                ClientRecipesSyncPayload.STREAM_CODEC,
                ModRecipeTypeRegistry::onClientRecipesSync);
    }

    public static <ModRecipe extends IModRecipe> IModRecipeType<ModRecipe> createType(ResourceLocation id) {

        final ModRecipeType<ModRecipe> type = new ModRecipeType<>(id);

        if (null != s_types.put(type.getKey(), type)) {
            throw new IllegalArgumentException("The specified mod recipe type is already registered");
        }

        return type;
    }

    //region internals

    private static void onOnDatapackSyncEvent(OnDatapackSyncEvent event) {

        final var players = event.getRelevantPlayers().toList();

        clearClientRecipesCaches(players);
        collectAndSendUpdatedRecipes(players);
    }

    private static void clearClientRecipesCaches(Iterable<ServerPlayer> players) {

        // ask all clients to clear their caches

        players.forEach(ClientRecipesClearPayload::send);
    }

    private static void collectAndSendUpdatedRecipes(Iterable<ServerPlayer> players) {

        // send updated recipes to all clients

        final var payloads = s_splitter.get().splitToPayloads(s_types.values().stream()
                .flatMap(IModRecipeType::stream)
                .map(RecipeHolder::value), ClientRecipesSyncPayload::new);
        players.forEach(player ->
                payloads.forEach(payload -> Lib.NETWORK_HANDLER.sendToPlayer(player, payload)));
    }

    private static void onClientRecipesClear(IPayloadContext context) {

        if (!context.flow().isClientbound()) {

            Log.LOGGER.warn(Log.CORE, "Ignoring recipes CLEAR request from the client");
            return;
        }

        s_types.values().forEach(ModRecipeType::invalidateCache);
    }

    private static void onClientRecipesSync(ClientRecipesSyncPayload payload, IPayloadContext context) {

        if (!context.flow().isClientbound()) {

            Log.LOGGER.warn(Log.CORE, "Ignoring recipes SYNC request from the client");
            return;
        }

        final var recipes = s_splitter.get().revertFromPayload(payload);

        for (final var recipe : recipes) {

            if (null != recipe && recipe.getType() instanceof IModRecipesCache recipesCache) {
                recipesCache.addRecipe(recipe);
            }
        }
    }

    private static Supplier<PayloadSplitter.HeadlessSplitter<Recipe<?>>> splitter() {
        return Suppliers.memoize(() -> {

            final var registry = CodeHelper.getRegistryAccess()
                    .orElseThrow(() -> new IllegalStateException("RegistryAccess not available"));

            return PayloadSplitter.headless(registry, Recipe.STREAM_CODEC);
        });
    }

    private static final Reference2ObjectMap<ResourceKey<RecipeType<?>>, ModRecipeType<? extends IModRecipe>> s_types = new Reference2ObjectArrayMap<>(32);
    private static final Supplier<PayloadSplitter.HeadlessSplitter<Recipe<?>>> s_splitter = splitter();

    //endregion
}

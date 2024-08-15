/*
 *
 * LibInit.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.internal;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.command.ZeroCoreCommand;
import it.zerono.mods.zerocore.internal.network.ErrorReportMessage;
import it.zerono.mods.zerocore.internal.network.InternalCommandMessage;
import it.zerono.mods.zerocore.internal.network.ModSyncableTileMessage;
import it.zerono.mods.zerocore.internal.network.TileCommandMessage;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.data.nbt.NBTBuilder;
import it.zerono.mods.zerocore.lib.item.TintedBucketItem;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ContainerDataHandler;
import it.zerono.mods.zerocore.lib.network.NetworkHandler;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import it.zerono.mods.zerocore.lib.tag.TagList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class Lib {

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();

    public static final ResourceLocationBuilder TEXTURES_LOCATION = ZeroCore.ROOT_LOCATION.appendPath("textures");
    public static final ResourceLocationBuilder GUI_TEXTURES_LOCATION = TEXTURES_LOCATION.appendPath("gui");

    public static final String NAME_INGREDIENT = "ingredient";
    public static final String NAME_TAG = "tag";
    public static final String NAME_COUNT = "count";

    public static void initialize(IEventBus modEventBus) {

        s_resourceReloaded = false;

        modEventBus.addListener(Lib::onRegisterPackets);
        modEventBus.addListener(Lib::onRegisterCapabilities);

        NeoForge.EVENT_BUS.addListener(Lib::onAddReloadListener);
        NeoForge.EVENT_BUS.addListener(Lib::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(Lib::onWorldTick);

        TagList.initialize();
    }

    public static boolean shouldInvalidateResourceCache() {
        return s_resourceReloaded;
    }

    public static void sendDebugGuiFrameCommand(boolean enable) {
        NETWORK_HANDLER.sendToAllPlayers(new InternalCommandMessage(InternalCommand.DebugGuiFrame,
                new NBTBuilder().addBoolean("enable", enable).build()));
    }

    public static void sendServerContainerDataSync(ServerPlayer player, CompoundTag data) {
        NETWORK_HANDLER.sendToPlayer(player, new InternalCommandMessage(InternalCommand.ContainerDataSync, data));
    }

    public static void sendBlockEntityMessage(AbstractModBlockEntity blockEntity, String name, CompoundTag data) {
        Lib.NETWORK_HANDLER.sendToServer(new TileCommandMessage(blockEntity, name, data));
    }

    public static void sendBlockEntityMessage(ServerPlayer player, AbstractModBlockEntity blockEntity, String name,
                                              CompoundTag data) {
        Lib.NETWORK_HANDLER.sendToPlayer(player, new TileCommandMessage(blockEntity, name, data));
    }

    //region internals

    private static void onRegisterPackets(RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(ZeroCore.MOD_ID).versioned("2.0.0");

        registrar.playBidirectional(TileCommandMessage.TYPE, TileCommandMessage.STREAM_CODEC, TileCommandMessage::handlePacket);
        registrar.playToClient(ModSyncableTileMessage.TYPE, ModSyncableTileMessage.STREAM_CODEC, ModSyncableTileMessage::handlePacket);
        registrar.playToClient(ErrorReportMessage.TYPE, ErrorReportMessage.STREAM_CODEC, ErrorReportMessage::handlePacket);
        registrar.playBidirectional(InternalCommandMessage.TYPE, InternalCommandMessage.STREAM_CODEC, InternalCommandMessage::handlePacket);
        ContainerDataHandler.registerPackets(registrar);
    }

    private static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(RECIPES_RELOADER);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        ZeroCoreCommand.register(event.getDispatcher());
    }

    private static void onWorldTick(LevelTickEvent.Post event) {

        if (!event.getLevel().isClientSide()) {
            s_resourceReloaded = false;
        }
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {

        for (final var item : BuiltInRegistries.ITEM) {

            if (TintedBucketItem.class == item.getClass()) {
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidBucketWrapper(stack), item);
            }
        }
    }

    private Lib() {
    }

    private static boolean s_resourceReloaded;

    private static final ResourceManagerReloadListener RECIPES_RELOADER = new ResourceManagerReloadListener() {

        @Override
        public void onResourceManagerReload(ResourceManager p_10758_) {

            s_resourceReloaded = true;
            ModRecipeType.invalidate();
        }
    };

    //endregion
}

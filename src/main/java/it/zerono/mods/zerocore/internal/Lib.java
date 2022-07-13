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

import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockRegistry;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import it.zerono.mods.zerocore.lib.tag.TagList;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;

public final class Lib {

    public static void initialize() {

        s_resourceReloaded = false;

        IEventBus bus;

        bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        bus.addListener(Lib::onRegisterRecipeSerializer);

        bus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();
        bus.addListener(Lib::onAddReloadListener);
        bus.addListener(Lib::onWorldTick);

        TagList.initialize();
    }

    public static boolean shouldInvalidateResourceCache() {
        return s_resourceReloaded;
    }

    public static <Controller extends IMultiblockController<Controller>> IMultiblockRegistry<Controller> createMultiblockRegistry() {
        return DistExecutor.safeRunForDist(() -> MultiblockRegistrySafeReferent::client, () -> MultiblockRegistrySafeReferent::server);
    }

    //region common constants
    //region field names

    public static final String NAME_RESULT = "result";
    public static final String NAME_INGREDIENT = "ingredient";
    public static final String NAME_ITEM = "item";
    public static final String NAME_FLUID = "fluid";
    public static final String NAME_TAG = "tag";
    public static final String NAME_COUNT = "count";
    public static final String NAME_NBT_TAG = "nbt";

    public static final String NAME_TYPE = "type";
    public static final String NAME_CONDITIONS = "conditions";

    //endregion
    //endregion
    //region event handlers

    @SubscribeEvent
    public static void onAddReloadListener(final AddReloadListenerEvent event) {

        event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                CompletableFuture.runAsync(() -> {

                    s_resourceReloaded = true;

                    ModRecipeType.invalidate();
                    Network.sendClearRecipeCommand();

                }, gameExecutor).thenCompose(stage::wait));
    }

    @SubscribeEvent
    public static void onRegisterRecipeSerializer(final RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_TYPES, helper -> ModRecipeType.onRegisterRecipes(helper::register));
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.LevelTickEvent event) {

        if (event.side.isServer() && TickEvent.Phase.END == event.phase) {
            s_resourceReloaded = false;
        }
    }

    //endregion
    //region internals

    private Lib() {
    }

    private static boolean s_resourceReloaded;

    //endregion
}

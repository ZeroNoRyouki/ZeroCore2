/*
 *
 * ClientProxy.java
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

package it.zerono.mods.zerocore.internal.proxy;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.model.BakedModelSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.Optional;

public class ClientProxy
        implements IProxy {

    public ClientProxy() {

        final IEventBus modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        modBus.register(this);
        modBus.register(BakedModelSupplier.INSTANCE);
        modBus.register(AtlasSpriteSupplier.INSTANCE);

        final IEventBus forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        forgeBus.addListener(this::onRenderTick);
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     *
     * @param event
     */
    @SubscribeEvent
    public void onClientInit(final FMLClientSetupEvent event) {
        CodeHelper.addResourceReloadListener(AtlasSpriteSupplier.INSTANCE);
    }

    @Override
    public Optional<World> getClientWorld() {
        return Optional.ofNullable(Minecraft.getInstance().world);
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        Minecraft.getInstance().worldRenderer.markBlockRangeForRenderUpdate(min.getX(), min.getY(), min.getZ(),
                max.getX(), max.getY(), max.getZ());
    }

    @Override
    public void sendPlayerStatusMessage(final PlayerEntity player, final ITextComponent message) {
            Minecraft.getInstance().ingameGUI.setOverlayMessage(message, false);
    }

    @Override
    public void addResourceReloadListener(ISelectiveResourceReloadListener listener) {

        final Minecraft mc = Minecraft.getInstance();

        // always check for a null here, there is not MC instance while running the datagens
        //noinspection ConstantConditions
        if (null != mc && mc.getResourceManager() instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).addReloadListener(listener);
        }
    }

    @Override
    public long getLastRenderTime() {
        return s_lastRenderTime;
    }

    //region internals

    private void onRenderTick(final TickEvent.RenderTickEvent event) {

        if (TickEvent.Phase.END == event.phase) {
            s_lastRenderTime = System.currentTimeMillis();
        }
    }

    private static long s_lastRenderTime = System.currentTimeMillis();

    //endregion
}

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

import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.internal.client.RenderTypes;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.GuiHelper;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ClientProxy
        implements IProxy {

    public ClientProxy() {

        this._guiErrorData = new GuiErrorData();

        final IEventBus modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        modBus.register(this);
//        modBus.register(BakedModelSupplier.INSTANCE);
        modBus.register(AtlasSpriteSupplier.INSTANCE);

        final IEventBus forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        forgeBus.addListener(this::onRenderTick);
        forgeBus.addListener(EventPriority.NORMAL, true, this::onGameOverlayRender);
        forgeBus.addListener(EventPriority.NORMAL, true, this::onGuiDrawScreenEventPost);
        forgeBus.addListener(EventPriority.NORMAL, true, this::onHighlightBlock);
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onClientInit(final FMLClientSetupEvent event) {
        CodeHelper.addResourceReloadListener(AtlasSpriteSupplier.INSTANCE);
    }

    @Override
    public Optional<Level> getClientWorld() {
        return Optional.ofNullable(Minecraft.getInstance().level);
    }

    @Override
    public Optional<Player> getClientPlayer() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(min.getX(), min.getY(), min.getZ(),
                max.getX(), max.getY(), max.getZ());
    }

    @Override
    public void sendPlayerStatusMessage(final Player player, final Component message) {
            Minecraft.getInstance().gui.setOverlayMessage(message, false);
    }

    @Override
    public void addResourceReloadListener(PreparableReloadListener listener) {

        final Minecraft mc = Minecraft.getInstance();

        // always check for a null here, there is not MC instance while running the datagens
        //noinspection ConstantConditions
        if (null != mc && mc.getResourceManager() instanceof ReloadableResourceManager) {
            ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
        }
    }

    @Override
    public long getLastRenderTime() {
        return s_lastRenderTime;
    }

    @Override
    public void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final Component... messages) {
        this._guiErrorData.addErrors(position, messages);
    }

    @Override
    public void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final List<Component> messages) {
        this._guiErrorData.addErrors(position, messages);
    }

    @Override
    public void clearErrorReport() {
        this._guiErrorData.resetErrors();
    }

    @Override
    public RecipeManager getRecipeManager() {

        if (EffectiveSide.get().isClient()) {

            final ClientPacketListener handler = Minecraft.getInstance().getConnection();

            return null != handler ? handler.getRecipeManager() : null;

        } else {

            return CodeHelper.getMinecraftServer().map(MinecraftServer::getRecipeManager).orElse(null);
        }
    }

    @Override
    public void handleInternalCommand(final InternalCommand command, final CompoundTag data, final NetworkDirection direction) {

        switch (command) {

            case ClearRecipes:
                ModRecipeType.invalidate();
                break;

            case DebugGuiFrame:
                GuiHelper.enableGuiDebugFrame(data.contains("enable") && data.getBoolean("enable"));
                break;

            case ContainerDataSync:
                this.getClientPlayer()
                        .map(p -> p.containerMenu)
                        .filter(c -> c instanceof ModContainer)
                        .map(c -> (ModContainer)c)
                        .ifPresent(mc -> mc.onContainerDataSync(data));
                break;

            default:
                IProxy.super.handleInternalCommand(command, data, direction);
                break;
        }
    }

    @Override
    public void debugUngrabMouse() {
        Minecraft.getInstance().mouseHandler.releaseMouse();
    }

    //region internals

    private void onRenderTick(final TickEvent.RenderTickEvent event) {

        if (TickEvent.Phase.END == event.phase) {

            s_lastRenderTime = System.currentTimeMillis();
            this._guiErrorData.tick();
        }
    }

    private void onGameOverlayRender(final RenderGuiOverlayEvent.Post event) {

        if (!isGuiOpen()) {
            this.paintErrorMessage(event.getPoseStack());
        }
    }

    private void onGuiDrawScreenEventPost(final ScreenEvent.Render.Post event) {

        if (isGuiOpen()) {
            this.paintErrorMessage(event.getPoseStack());
        }
    }

    private void onHighlightBlock(final RenderHighlightEvent.Block event) {

        final BlockHitResult result = event.getTarget();
        final BlockPos position = result.getBlockPos();

        if (HitResult.Type.BLOCK == result.getType() && this._guiErrorData.test(position)) {

            final Vec3 projectedView = event.getCamera().getPosition();

            ModRenderHelper.paintVoxelShape(Shapes.block(), event.getPoseStack(), event.getMultiBufferSource(),
                    RenderTypes.ERROR_BLOCK_HIGHLIGHT, position.getX() - projectedView.x(), position.getY() - projectedView.y(),
                    position.getZ() - projectedView.z(), ERROR_HIGHLIGHT1_COLOUR);

            event.setCanceled(true);
        }
    }

    private void paintErrorMessage(final PoseStack matrix) {

        final IRichText texts = this._guiErrorData.apply(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2);

        if (texts.isEmpty()) {
            return;
        }

        ModRenderHelper.paintMessage(matrix, texts, 5, 5, 300, ERROR_BORDER, ERROR_BACKGROUND_COLOUR,
                ERROR_HIGHLIGHT1_COLOUR, ERROR_HIGHLIGHT2_COLOUR);
    }

    private static boolean isGuiOpen() {
        return null != Minecraft.getInstance().screen;
    }

    private static final Colour ERROR_BACKGROUND_COLOUR = Colour.fromARGB(0x5f5e5e5e);
    private static final Colour ERROR_HIGHLIGHT1_COLOUR = Colour.fromARGB(0x50e8ee4d);
    private static final Colour ERROR_HIGHLIGHT2_COLOUR = Colour.fromARGB((0x50e8ee4d & 0xFEFEFE) >> 1 | 0x50e8ee4d & -16777216);
    private static final int ERROR_BORDER = 5;

    private static volatile long s_lastRenderTime = System.currentTimeMillis();

    private final GuiErrorData _guiErrorData;

    //endregion
}

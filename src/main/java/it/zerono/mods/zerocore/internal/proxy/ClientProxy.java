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

import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.internal.client.RenderTypes;
import it.zerono.mods.zerocore.internal.client.model.MissingModel;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.GuiHelper;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ClientProxy
        implements IForgeProxy {

    public ClientProxy() {
        this._guiErrorData = new GuiErrorData();
    }

    //region IForgeProxy

    @Override
    public void initialize(IEventBus modEventBus) {

        NeoForge.EVENT_BUS.addListener(this::onRenderTick);
        NeoForge.EVENT_BUS.addListener(ClientProxy::onRegisterReloadListeners);
        NeoForge.EVENT_BUS.addListener(ClientProxy::onRecipesUpdated);
        NeoForge.EVENT_BUS.addListener(this::onLoggedOut);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, this::onGameOverlayRender);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, this::onGuiDrawScreenEventPost);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, this::onHighlightBlock);
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
    public long getLastRenderTime() {
        return s_lastRenderTime;
    }

    @Override
    public void displayErrorToPlayer(final @Nullable BlockPos position, final Component... messages) {
        this._guiErrorData.addErrors(position, messages);
    }

    @Override
    public void displayErrorToPlayer(final @Nullable BlockPos position, final List<Component> messages) {
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
    public void handleInternalCommand(final InternalCommand command, final CompoundTag data, final PacketFlow flow) {

        switch (command) {

            case DebugGuiFrame:
                GuiHelper.enableGuiDebugFrame(data.contains("enable") && data.getBoolean("enable"));
                break;

            case ContainerDataSync: {

                final ModContainer container = this.getCurrentClientSideModContainer();
                final Player player = Minecraft.getInstance().player;

                if (null != container && null != player) {
                    container.onContainerDataSync(data, player.registryAccess());
                }

                break;
            }

            default:
                IForgeProxy.super.handleInternalCommand(command, data, flow);
                break;
        }
    }

    @Override
    public void debugUngrabMouse() {
        Minecraft.getInstance().mouseHandler.releaseMouse();
    }

    @Nullable
    @Override
    public ModContainer getCurrentClientSideModContainer() {

        final var player = Minecraft.getInstance().player;

        if (null != player && player.containerMenu instanceof ModContainer modContainer) {
            return modContainer;
        }

        return null;
    }

    //endregion
    //region internals

    private void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        this.clearErrorReport();
    }

    private static void onRegisterReloadListeners(AddReloadListenerEvent event) {

        event.addListener(AtlasSpriteSupplier.INSTANCE);
        event.addListener(MissingModel.INSTANCE);
    }

    private static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ModRecipeType.invalidate();
    }

    private void onRenderTick(final RenderFrameEvent.Post event) {

        s_lastRenderTime = System.currentTimeMillis();
        this._guiErrorData.tick();
    }

    private void onGameOverlayRender(final RenderGuiLayerEvent.Post event) {

        if (!isGuiOpen()) {
            this.paintErrorMessage(event.getGuiGraphics());
        }
    }

    private void onGuiDrawScreenEventPost(final ScreenEvent.Render.Post event) {

        if (isGuiOpen()) {
            this.paintErrorMessage(event.getGuiGraphics());
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

    private void paintErrorMessage(final GuiGraphics gfx) {

        final IRichText texts = this._guiErrorData.apply(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2);

        if (texts.isEmpty()) {
            return;
        }

        ModRenderHelper.paintMessage(gfx, texts, 5, 5, 300, ERROR_BORDER, ERROR_BACKGROUND_COLOUR,
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

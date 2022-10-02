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

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.internal.client.RenderTypes;
import it.zerono.mods.zerocore.internal.network.ErrorReportMessage;
import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.GuiHelper;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.model.BakedModelSupplier;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ClientProxy
        implements IProxy {

    public ClientProxy() {

        this._guiErrorData = new GuiErrorData();

        final IEventBus modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        modBus.register(this);
        modBus.register(BakedModelSupplier.INSTANCE);
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
    public Optional<World> getClientWorld() {
        return Optional.ofNullable(Minecraft.getInstance().level);
    }

    @Override
    public Optional<PlayerEntity> getClientPlayer() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(min.getX(), min.getY(), min.getZ(),
                max.getX(), max.getY(), max.getZ());
    }

    @Override
    public void sendPlayerStatusMessage(final PlayerEntity player, final ITextComponent message) {
            Minecraft.getInstance().gui.setOverlayMessage(message, false);
    }

    @Override
    public void addResourceReloadListener(ISelectiveResourceReloadListener listener) {

        final Minecraft mc = Minecraft.getInstance();

        // always check for a null here, there is not MC instance while running the datagens
        //noinspection ConstantConditions
        if (null != mc && mc.getResourceManager() instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
        }
    }

    @Override
    public long getLastRenderTime() {
        return s_lastRenderTime;
    }

    @Override
    public void displayErrorToPlayer(final @Nullable BlockPos position, final ITextComponent... messages) {
        this._guiErrorData.addErrors(position, messages);
    }

    @Override
    public void displayErrorToPlayer(final @Nullable BlockPos position, final List<ITextComponent> messages) {
        this._guiErrorData.addErrors(position, messages);
    }

    @Override
    public void clearErrorReport() {
        this._guiErrorData.resetErrors();
    }

    @Override
    public RecipeManager getRecipeManager() {

        if (EffectiveSide.get().isClient()) {

            final ClientPlayNetHandler handler = Minecraft.getInstance().getConnection();

            return null != handler ? handler.getRecipeManager() : null;

        } else {

            final MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);

            return null != server ? server.getRecipeManager() : null;
        }
    }

    @Override
    public void handleInternalCommand(final InternalCommand command, final CompoundNBT data, final NetworkDirection direction) {

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

    private void onGameOverlayRender(final RenderGameOverlayEvent.Post event) {

        if (!isGuiOpen()) {
            this.paintErrorMessage(event.getMatrixStack());
        }
    }

    private void onGuiDrawScreenEventPost(final GuiScreenEvent.DrawScreenEvent.Post event) {

        if (isGuiOpen()) {
            this.paintErrorMessage(event.getMatrixStack());
        }
    }

    private void onHighlightBlock(final DrawHighlightEvent.HighlightBlock event) {

        final BlockRayTraceResult result = event.getTarget();
        final BlockPos position = result.getBlockPos();

        if (RayTraceResult.Type.BLOCK == result.getType() && this._guiErrorData.test(position)) {

            final Vector3d projectedView = event.getInfo().getPosition();

            ModRenderHelper.paintVoxelShape(event.getMatrix(), VoxelShapes.block(),
                    event.getBuffers().getBuffer(RenderTypes.ERROR_BLOCK_HIGHLIGHT),
                    position.getX() - projectedView.x(), position.getY() - projectedView.y(),
                    position.getZ() - projectedView.z(), ERROR_HIGHLIGHT1_COLOUR);

            event.setCanceled(true);
        }
    }

    private void paintErrorMessage(final MatrixStack matrix) {

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

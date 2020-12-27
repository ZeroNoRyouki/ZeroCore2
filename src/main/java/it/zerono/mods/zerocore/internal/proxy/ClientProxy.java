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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.RichText;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.model.BakedModelSupplier;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nullable;
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
        forgeBus.addListener(EventPriority.NORMAL, true, this::onGameOverlayRender);
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

    @Override
    public void notifyMultiblockError(@Nullable PlayerEntity player, ITextComponent message,
                                      @Nullable BlockPos position) {

        this.resetMultiblockError();
        this._multiblockErrorMessage = message;
        this._multiblockErrorPosition = position;
        this._multiblockErrorTimeout = 60 * 100;
    }

    //region internals

    private void onRenderTick(final TickEvent.RenderTickEvent event) {

        if (TickEvent.Phase.END == event.phase) {

            s_lastRenderTime = System.currentTimeMillis();

            if (this._multiblockErrorTimeout > 0) {

                --this._multiblockErrorTimeout;

                if (0 == this._multiblockErrorTimeout) {
                    this.resetMultiblockError();
                }
            }
        }
    }

    private void onGameOverlayRender(final RenderGameOverlayEvent.Post event) {

        if (null == this._multiblockErrorMessage || this._multiblockErrorTimeout <= 0) {
            return;
        }

        final int maxTextWidth = Minecraft.getInstance().getMainWindow().getScaledWidth() / 2;

        if (null == this._multiblockErrorText) {
            this._multiblockErrorText = RichText.builder(maxTextWidth)
                    .textLines(Lists.newArrayList(this._multiblockErrorMessage))
                    .defaultColour(Colour.WHITE)
                    .build();
        }

        if (null != this._multiblockErrorPosition && null == this._multiblockErrorPositionText) {
            this._multiblockErrorPositionText = RichText.builder(maxTextWidth)
                    .textLines(Lists.newArrayList(new StringTextComponent(String.format("@0 %d, %d, %d",
                            this._multiblockErrorPosition.getX(),
                            this._multiblockErrorPosition.getY(),
                            this._multiblockErrorPosition.getZ()))))
                    .objects(Lists.newArrayList(Items.COMPASS))
                    .defaultColour(Colour.WHITE)
                    .build();
        }

        if (this._multiblockErrorText.isEmpty()) {
            return;
        }

        final Rectangle boxBounds;
        final int errorOffsetY;

        if (null != this._multiblockErrorPositionText) {

            final Rectangle errorBounds = this._multiblockErrorText.bounds();
            final Rectangle positionBounds = this._multiblockErrorPositionText.bounds();

            boxBounds = new Rectangle(10, 10,
                    Math.max(errorBounds.Width, positionBounds.Width) + MULTIBLOCK_ERROR_BORDER * 2,
                    errorBounds.Height + positionBounds.Height + MULTIBLOCK_ERROR_BORDER * 3);
            errorOffsetY = positionBounds.Height + MULTIBLOCK_ERROR_BORDER * 2;

        } else {

            boxBounds = this._multiblockErrorText.bounds().expand(MULTIBLOCK_ERROR_BORDER * 2, MULTIBLOCK_ERROR_BORDER * 2).offset(10, 10);
            errorOffsetY = MULTIBLOCK_ERROR_BORDER;
        }

        final MatrixStack matrix = event.getMatrixStack();
        final int z = 300;

        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX1(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, MULTIBLOCK_ERROR_BACKGROUND_COLOUR);
        ModRenderHelper.paintSolidRect(matrix, boxBounds.getX1() + 1, boxBounds.getY1(), boxBounds.getX2(), boxBounds.getY2() + 1, z, MULTIBLOCK_ERROR_BACKGROUND_COLOUR);
        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX2(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, MULTIBLOCK_ERROR_BACKGROUND_COLOUR);

        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX1() + 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, MULTIBLOCK_ERROR_HIGHLIGHT1_COLOUR, MULTIBLOCK_ERROR_HIGHLIGHT2_COLOUR);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY1() + 1, boxBounds.Width - 4, z, MULTIBLOCK_ERROR_HIGHLIGHT1_COLOUR, MULTIBLOCK_ERROR_HIGHLIGHT2_COLOUR);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY2() - 1, boxBounds.Width - 4, z, MULTIBLOCK_ERROR_HIGHLIGHT1_COLOUR, MULTIBLOCK_ERROR_HIGHLIGHT2_COLOUR);
        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX2() - 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, MULTIBLOCK_ERROR_HIGHLIGHT1_COLOUR, MULTIBLOCK_ERROR_HIGHLIGHT2_COLOUR);

        if (null != this._multiblockErrorPositionText) {
            this._multiblockErrorPositionText.paint(matrix, boxBounds.getX1() + MULTIBLOCK_ERROR_BORDER, boxBounds.getY1() + MULTIBLOCK_ERROR_BORDER, z + 1);
        }

        this._multiblockErrorText.paint(matrix, boxBounds.getX1() + MULTIBLOCK_ERROR_BORDER, boxBounds.getY1() + errorOffsetY, z + 1);
    }

    private void resetMultiblockError() {

        this._multiblockErrorMessage = null;
        this._multiblockErrorPosition = null;
        this._multiblockErrorTimeout = 0;
        this._multiblockErrorText = null;
        this._multiblockErrorPositionText = null;
    }

    private static final Colour MULTIBLOCK_ERROR_BACKGROUND_COLOUR = Colour.fromARGB(0x1f5e5e5e);
    private static final Colour MULTIBLOCK_ERROR_HIGHLIGHT1_COLOUR = Colour.fromARGB(0x50e8ee4d);
    private static final Colour MULTIBLOCK_ERROR_HIGHLIGHT2_COLOUR = Colour.fromARGB((0x50e8ee4d & 0xFEFEFE) >> 1 | 0x50e8ee4d & -16777216);
    private static final int MULTIBLOCK_ERROR_BORDER = 5;

    private static long s_lastRenderTime = System.currentTimeMillis();

    private ITextComponent _multiblockErrorMessage;
    private BlockPos _multiblockErrorPosition;
    private int _multiblockErrorTimeout;
    private RichText _multiblockErrorText;
    private RichText _multiblockErrorPositionText;

    //endregion
}

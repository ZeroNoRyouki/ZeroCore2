/*
 *
 * Static.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class Static
        extends AbstractControl {

    public Static(final ModContainerScreen<? extends ModContainer> gui, final int width, final int height) {

        super(gui, gui.nextGenericName());
        this.setDesiredDimension(width, height);
        this._customPainter = NO_PAINTER;
    }

    public Static setColor(final Colour colour) {

        this._customPainter = NO_PAINTER;
        this.setBackground(colour);
        return this;
    }

    public Static setColor(final Colour startColour, final Colour endColour) {

        this._customPainter = NO_PAINTER;
        this.setCustomBackgroundHandler(this.getDesiredDimension(DesiredDimension.Height) > this.getDesiredDimension(DesiredDimension.Width) ?
                (c, matrix) -> c.paintHorizontalGradientRect(matrix, startColour, endColour) :
                (c, matrix) -> c.paintVerticalGradientRect(matrix, startColour, endColour));
        return this;
    }

    public Static setStack(final ItemStack stack) {

        this.setStack(stack, false);
        return this;
    }

    public Static setStack(final ItemStack stack, final boolean highlight) {

        this.clearBackground();
        this._customPainter = (control, matrix) -> control.paintItemStack(matrix, stack, this.getPadding().getLeft(),
                this.getPadding().getTop(), highlight);
        return this;
    }

    public Static setStackWithCount(final ItemStack stack) {

        this.setStackWithCount(stack, false);
        return this;
    }

    public Static setStackWithCount(final ItemStack stack, final boolean highlight) {

        this.clearBackground();
        this._customPainter = (control, matrix) -> control.paintItemStackWithCount(matrix, stack, this.getPadding().getLeft(),
                this.getPadding().getTop(), highlight);
        return this;
    }

    public Static setStackAsInventory(final ItemStack stack) {

        if (stack.getCount() > 1) {
            this.setStackWithCount(stack);
        } else {
            this.setStack(stack);
        }

        if (!stack.isEmpty()) {
            this.setTooltips(Component.literal(String.format("%dx ", stack.getCount()))
                    .append(Component.translatable(stack.getItem().getDescriptionId()).setStyle(Style.EMPTY.withBold(true))));
        }

        return this;
    }

    //region AbstractControl

    @Override
    public void onPaint(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
        this._customPainter.accept(this, gfx);
    }

    //endregion
    //region internals

    private final static BiConsumer<Static, GuiGraphics> NO_PAINTER = (c, m) -> {};

    private BiConsumer<Static, GuiGraphics> _customPainter;

    //endregion
}

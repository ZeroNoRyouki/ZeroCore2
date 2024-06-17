/*
 *
 * ItemStackDisplay.java
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

import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.ItemStackData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemStackDisplay
        extends AbstractControl {

    public ItemStackDisplay(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this.setDesiredDimension(18, 18);
        this.setPadding(1);
        this.setStack(ItemStack.EMPTY);
    }

    public ItemStackDisplay(final ModContainerScreen<? extends ModContainer> gui) {
        this(gui, gui.nextGenericName());
    }

    public void setStack(final ItemStack stack) {

        this._stack = stack.copy();
        this.onStackChanged();
    }

    public void setStackAmount(final int amount) {

        if (amount <= 0) {
            this.setStack(ItemStack.EMPTY);
        } else {
            this._stack.setCount(amount);
        }

        this.onStackChanged();
    }

    public void bindStack(ItemStackData bindableStack) {

        bindableStack.bind(this::setStack);
        bindableStack.amount().bind(this::setStackAmount);
    }

    //region internals
    //region AbstractControl

    @Override
    public void onPaint(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
        this._painter.accept(gfx);
    }

    //endregion

    private void onStackChanged() {

        if (this._stack.isEmpty()) {

            this._painter = this::paintWithNoCount;
            this.clearTooltips();

        } else {

            if (this._stack.getCount() > 1) {
                this._painter = this::paintWithCount;
            } else {
                this._painter = this::paintWithNoCount;
            }

            this.setTooltips(TextHelper.literal("%dx ", this._stack.getCount())
                    .append(TextHelper.translatable(this._stack.getDescriptionId()).withStyle(ChatFormatting.BOLD)));
        }
    }

    private void paintWithNoCount(final GuiGraphics matrix) {
        this.paintItemStack(matrix, this._stack, false);
    }

    private void paintWithCount(final GuiGraphics matrix) {
        this.paintItemStackWithCount(matrix, this._stack, false);
    }

    private ItemStack _stack;
    private Consumer<@NotNull GuiGraphics> _painter;

    //endregion
}

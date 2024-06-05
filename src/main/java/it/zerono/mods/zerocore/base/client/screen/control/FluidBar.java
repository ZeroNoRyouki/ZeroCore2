/*
 *
 * FluidBar.java
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

package it.zerono.mods.zerocore.base.client.screen.control;

import com.google.common.base.Strings;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FluidStackData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidBar
        extends AbstractVerticalIconSingleValueGaugeBar {

    public FluidBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                    final int maxValue, final FluidStackData bindableStack,
                    final NonNullSupplier<ISprite> iconSprite) {

        super(gui, name, maxValue, bindableStack.amount(), Sprite.EMPTY_SUPPLIER, iconSprite);
        bindableStack.bind(this::setFluidSprite);
    }

    public FluidBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                    final int maxValue, final FluidStackData bindableStack,
                    final NonNullSupplier<ISprite> iconSprite, final String titleTooltipLine,
                    @Nullable final String optionalTooltipLine) {

        this(gui, name, maxValue, bindableStack, iconSprite);

        final BaseScreenToolTipsBuilder toolTips = new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle(titleTooltipLine)
                .addTranslatableAsValue("gui.zerocore.base.control.fluidbar.line2")
                .addTextAsValue(TextHelper.translatable("gui.zerocore.base.control.fluidbar.line3a"),
                        TextHelper.translatable("gui.zerocore.base.control.fluidbar.line3b",
                                CodeHelper.formatAsMillibuckets((float) maxValue)))
                .addTextAsValue(TextHelper.translatable("gui.zerocore.base.control.fluidbar.line4a"),
                        TextHelper.translatable("gui.zerocore.base.control.fluidbar.line4b"))
                .addBindableObjectAsValue(bindableStack, this::formatFluidText)
                .addBindableObjectAsValue(bindableStack.amount(), this::formatAmountText)
                .addBindableObjectAsValue(bindableStack.amount(), this::formatPercentageText);

        if (!Strings.isNullOrEmpty(optionalTooltipLine)) {

            toolTips.addEmptyLine();
            toolTips.addTranslatable(optionalTooltipLine);
        }

        this.setTooltips(toolTips);
    }

    public MutableComponent formatFluidText(final FluidStack stack) {
        return BaseHelper.getFluidNameOrEmpty(stack);
    }

    public MutableComponent formatAmountText(final int amount) {
        return TextHelper.literal(CodeHelper.formatAsMillibuckets(amount));
    }

    public MutableComponent formatPercentageText(final int amount) {
        return TextHelper.literal("%d", (int) ((amount / this._bar.getMaxValue()) * 100));
    }

    //region internals

    private void setFluidSprite(final FluidStack stack) {

        if (stack.isEmpty()) {

            this._bar.setBarSprite(Sprite.EMPTY);
            this._bar.setBarSpriteTint(Colour.WHITE);
            this._bar.setValue(0);

        } else {

            final Fluid fluid = stack.getFluid();
            final ISprite fluidSprite = ModRenderHelper.getStillFluidSprite(fluid);
            final Colour fluidTint = Colour.fromARGB(ModRenderHelper.getFluidTint(stack));

            this._bar.setBarSprite(fluidSprite);
            this._bar.setBarSpriteTint(fluidTint);
            this._bar.setValue(stack.getAmount());
        }
    }

    //endregion
}

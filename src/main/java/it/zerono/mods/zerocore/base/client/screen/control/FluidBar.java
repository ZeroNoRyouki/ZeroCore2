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
import com.google.common.collect.ImmutableList;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.client.text.BindableTextComponent;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FluidBar
        extends AbstractVerticalIconGaugeBar {

    public FluidBar(final ModContainerScreen<? extends ModContainer> gui, final String name, final double maxValue,
                    final Supplier<Double> valueSupplier, final Supplier<FluidStack> fluidSupplier,
                    final NonNullSupplier<ISprite> iconSprite, final String titleTooltipLine,
                    @Nullable final String optionalTooltipLine) {

        super(gui, name, maxValue, valueSupplier, () -> Sprite.EMPTY, iconSprite);

        final BindableTextComponent<Double> valueText = new BindableTextComponent<>(this::getValueText);
        final BindableTextComponent<Double> percentageText = new BindableTextComponent<>(this::getPercentageText);
        final BindableTextComponent<FluidStack> fluidNameText = new BindableTextComponent<>(BaseHelper::getFluidNameOrEmpty);

        final ImmutableList.Builder<Component> tipsBuilder = ImmutableList.builder();

        tipsBuilder.add(
                Component.translatable(titleTooltipLine).setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                Component.translatable("gui.zerocore.base.control.fluidbar.line2").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE),
                Component.translatable("gui.zerocore.base.control.fluidbar.line3a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                        .append(Component.translatable("gui.zerocore.base.control.fluidbar.line3b",
                                CodeHelper.formatAsMillibuckets((float)maxValue))),
                Component.translatable("gui.zerocore.base.control.fluidbar.line4a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                        .append(Component.translatable("gui.zerocore.base.control.fluidbar.line4b"))
        );

        if (!Strings.isNullOrEmpty(optionalTooltipLine)) {
            tipsBuilder.add(Component.translatable(optionalTooltipLine));
        }

        this._bar.setTooltips(tipsBuilder.build(), ImmutableList.of(fluidNameText, valueText, percentageText));
        gui.addDataBinding(valueSupplier, valueText, percentageText);
        gui.addDataBinding(fluidSupplier, fluidNameText, this::setFluidSprite);

        this._icon.useTooltipsFrom(this._bar);
    }

    //region internals

    private Component getValueText(final double amount) {
        return Component.literal(CodeHelper.formatAsMillibuckets((float)amount)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    }

    private Component getPercentageText(final double amount) {
        return Component.literal(String.format("%d", (int)((amount / this._bar.getMaxValue()) * 100))).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    }

    private void setFluidSprite(final FluidStack stack) {

        if (stack.isEmpty()) {

            this._bar.setBarSprite(Sprite.EMPTY);
            this._bar.setBarSpriteTint(Colour.WHITE);

        } else {

            final Fluid fluid = stack.getFluid();
            final ISprite fluidSprite = ModRenderHelper.getStillFluidSprite(fluid);
            final Colour fluidTint = Colour.fromARGB(ModRenderHelper.getFluidTint(stack));

            this._bar.setBarSprite(fluidSprite);
            this._bar.setBarSpriteTint(fluidTint);
        }
    }

    //endregion
}

/*
 *
 * EnergyBar.java
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
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.text.BindableTextComponent;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class EnergyBar
        extends AbstractVerticalIconGaugeBar {

    public EnergyBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                     final EnergySystem system, final WideAmount maxValue, final Supplier<WideAmount> valueSupplier,
                     @Nullable final String optionalTooltipLine) {

        super(gui, name, maxValue.doubleValue(), () -> valueSupplier.get().doubleValue(),
                BaseIcons.PowerBar, BaseIcons.PowerBattery);
        this._system = system;

        final BindableTextComponent<WideAmount> valueText = new BindableTextComponent<>(this::getValueText);
        final BindableTextComponent<WideAmount> percentageText = new BindableTextComponent<>(this::getPercentageText);

        final ImmutableList.Builder<Component> tipsBuilder = ImmutableList.builder();

        tipsBuilder.add(
                new TranslatableComponent("gui.zerocore.base.control.energybar.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.zerocore.base.control.energybar.line2a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.zerocore.base.control.energybar.line2b",
                                CodeHelper.formatAsHumanReadableNumber(maxValue.doubleValue(), system.getUnit()))),
                new TranslatableComponent("gui.zerocore.base.control.energybar.line3a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.zerocore.base.control.energybar.line3b"))
        );

        if (!Strings.isNullOrEmpty(optionalTooltipLine)) {
            tipsBuilder.add(new TranslatableComponent(optionalTooltipLine));
        }

        this._bar.setTooltips(tipsBuilder.build(), ImmutableList.of(valueText, percentageText));
        gui.addDataBinding(valueSupplier, valueText, percentageText);

        this._icon.useTooltipsFrom(this._bar);
    }

    //region internals

    private Component getValueText(final WideAmount amount) {
        return new TextComponent(CodeHelper.formatAsHumanReadableNumber(amount.doubleValue(),
                this._system.getUnit())).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    }

    private Component getPercentageText(final WideAmount amount) {

        final double percentage = amount.percentage(WideAmount.from(this._bar.getMaxValue()));

        return new TextComponent(String.format("%d", (int)(percentage * 100))).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    }

    private final EnergySystem _system;

    //endregion
}

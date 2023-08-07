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
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.WideAmountData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.util.text.IFormattableTextComponent;

import javax.annotation.Nullable;

public class EnergyBar
        extends AbstractVerticalIconSingleValueGaugeBar {

    public EnergyBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                     final EnergySystem system, final WideAmount maxValue, final WideAmountData bindableValue,
                     @Nullable final String optionalTooltipLine) {

        super(gui, name, maxValue.doubleValue(), bindableValue.asDouble(), BaseIcons.PowerBar, BaseIcons.PowerBattery);
        this._system = system;

        final BaseScreenToolTipsBuilder toolTips = new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.zerocore.base.control.energybar.line1")
                .addTextAsValue(TextHelper.translatable("gui.zerocore.base.control.energybar.line2a"),
                        TextHelper.translatable("gui.zerocore.base.control.energybar.line2b",
                                CodeHelper.formatAsHumanReadableNumber(maxValue.doubleValue(), system.getUnit())))
                .addTextAsValue(TextHelper.translatable("gui.zerocore.base.control.energybar.line3a"),
                        TextHelper.translatable("gui.zerocore.base.control.energybar.line3b"))
                .addBindableObjectAsValue(bindableValue, this::getValueText)
                .addBindableObjectAsValue(bindableValue, this::getPercentageText);

        if (!Strings.isNullOrEmpty(optionalTooltipLine)) {

            toolTips.addEmptyLine();
            toolTips.addTranslatable(optionalTooltipLine);
        }

        this.setTooltips(toolTips);
    }

    public void bindMaxValue(WideAmountData bindableValue) {
        bindableValue.bind(amount -> this._bar.setMaxValue(amount.doubleValue()));
    }

    //region internals

    private IFormattableTextComponent getValueText(final WideAmount amount) {
        return TextHelper.literal(CodeHelper.formatAsHumanReadableNumber(amount.doubleValue(), this._system.getUnit()));
    }

    private IFormattableTextComponent getPercentageText(final WideAmount amount) {

        final double percentage = amount.percentage(WideAmount.from(this._bar.getMaxValue()));

        return TextHelper.literal("%d", (int) (percentage * 100));
    }

    private final EnergySystem _system;

    //endregion
}

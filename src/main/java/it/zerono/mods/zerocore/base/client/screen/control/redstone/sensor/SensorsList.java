/*
 *
 * SensorsList.java
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

package it.zerono.mods.zerocore.base.client.screen.control.redstone.sensor;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.control.Label;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.Static;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.FlowLayoutEngine;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Map;

class SensorsList<Reader extends IMachineReader, Writer, SensorType extends Enum<SensorType> & ISensorType<Reader>,
                    SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
    extends Panel {

    public SensorsList(SensorGroupPanelBuilder<Reader, SensorType> builder, int columns, String sensorsListLabelKey,
                       NonNullConsumer<SensorType> onSensorChangedCallback) {

        super(builder.getGui(), "sensorsList");

        this._onSensorChangedCallback = onSensorChangedCallback;
        this._buttons = new Object2ObjectArrayMap<>(32);
        this._activeButton = null;

        final int listWidth = computeWidth(columns);

        this.setPadding(0);
        this.setLayoutEngine(new FlowLayoutEngine()
                .setZeroMargins()
                .setHorizontalMargin(HORIZONTAL_MARGIN)
                .setControlsSpacing(CONTROL_SPACING));
        this.setDesiredDimension(listWidth, builder.getHeight());

        final Label label = new Label(builder.getGui(), "sensorListLabel", TextHelper.translatable(sensorsListLabelKey));

        label.setPadding(0);
        label.setAutoSize(false);
        label.setDesiredDimension(listWidth, ClientBaseHelper.LABEL_HEIGHT);
        this.addControl(label);

        for (final SensorGroupPanelBuilder<Reader, SensorType>.BehaviorGroupBuilder groupBuilder : builder.getGroupsBuilders()) {

            if (null == groupBuilder) {
                this.addSeparator();
            } else {
                this.addSensor(groupBuilder);
            }
        }
    }

    static int computeWidth(int columns) {
        return HORIZONTAL_MARGIN + (columns * ClientBaseHelper.SQUARE_BUTTON_DIMENSION) + (columns - 1) * CONTROL_SPACING + HORIZONTAL_MARGIN;
    }

    public void setSettings(SensorSetting setting) {

        this.resetSettings();

        final SwitchPictureButton button = this._buttons.get(setting.Sensor);

        if (null != button) {
            button.setActive(true);
        }
    }

    public void resetSettings() {

        if (null != this._activeButton) {
            this._activeButton.setActive(false);
        }
    }

    //endregion
    //region internals

    private void addSensor(SensorGroupPanelBuilder<Reader, SensorType>.BehaviorGroupBuilder builder) {

        final SensorType sensor = builder.getSensor();
        final SwitchPictureButton button = new SwitchPictureButton(this.getGui(), sensor.name(), false, "sensortype");

        button.setTag(sensor);
        button.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle(sensor.getNameTranslationKey())
                .addText(sensor.isInput() ? DIRECTION_INPUT : DIRECTION_OUTPUT)
                .addEmptyLine()
                .addTranslatable(sensor.getDescriptionTranslationKey())
        );

        ClientBaseHelper.setButtonSpritesAndOverlayForState(button, ButtonState.Default, builder.getButtonOffSprite());
        ClientBaseHelper.setButtonSpritesAndOverlayForState(button, ButtonState.Active, builder.getButtonOnSprite());

        button.setDesiredDimension(ClientBaseHelper.SQUARE_BUTTON_DIMENSION, ClientBaseHelper.SQUARE_BUTTON_DIMENSION);
        button.setBackground(BaseIcons.ImageButtonBackground.get());
        button.enablePaintBlending(true);
        button.setPadding(1);

        button.Activated.subscribe(btn -> btn.<SensorType>getTag().ifPresent(newSensor -> {

            this._activeButton = btn;
            this._onSensorChangedCallback.accept(newSensor);
        }));

        this.addControl(button);
        this._buttons.put(sensor, button);
    }

    private void addSeparator() {

        final Static separator = new Static(this.getGui(), this.getDesiredDimension(DesiredDimension.Width), 1);

        separator.setColor(Colour.BLACK);
        this.addControl(separator);
    }

    private static final ITextComponent DIRECTION_INPUT = TextHelper.translatable("gui.zerocore.base.redstone.sensortype.input", ClientBaseHelper::formatAsInfo);
    private static final ITextComponent DIRECTION_OUTPUT = TextHelper.translatable("gui.zerocore.base.redstone.sensortype.output", ClientBaseHelper::formatAsInfo);
    private static final int HORIZONTAL_MARGIN = 3;
    private static final int CONTROL_SPACING = 4;
    private final NonNullConsumer<SensorType> _onSensorChangedCallback;
    private final Map<SensorType, SwitchPictureButton> _buttons;
    private SwitchPictureButton _activeButton;

    //endregion
}

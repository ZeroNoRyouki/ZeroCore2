/*
 *
 * SensorGroupPanel.java
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

import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingFactory;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.control.Label;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.PanelGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.text.TextHelper;

import java.util.List;

class SensorGroupPanel<Reader extends IMachineReader, Writer, SensorType extends Enum<SensorType> & ISensorType<Reader>,
                        SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends Panel {

    public SensorGroupPanel(SensorGroupPanelBuilder<Reader, SensorType> builder) {

        super(builder.getGui(), "sensorsGroup");

        this.setDesiredDimension(builder.getWidth(), builder.getHeight());
        this.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setHorizontalMargin(HORIZONTAL_MARGIN)
                .setControlsSpacing(CONTROL_SPACING));

        this._activeSensorName = new Label(builder.getGui(), "activename", "");
        this._activeSensorName.setDesiredDimension(builder.getWidth(), ClientBaseHelper.LABEL_HEIGHT);
        this._activeSensorName.setAutoSize(false);
        this.addControl(this._activeSensorName);

        final List<SensorType> sensors = builder.getSensors();

        this._group = new PanelGroup<>(builder.getGui(), "behaviors", sensors);
        this._group.setDesiredDimension(builder.getWidth(), builder.getHeight() - ClientBaseHelper.LABEL_HEIGHT - CONTROL_SPACING - 4);
        this.addControl(this._group);

        for (final SensorGroupPanelBuilder<Reader, SensorType>.BehaviorGroupBuilder groupBuilder : builder.getGroupsBuilders()) {

            if (null != groupBuilder) {
                this._group.setPanel(groupBuilder.getSensor(), new SensorBehaviorGroupPanel<>(groupBuilder));
            }
        }
    }

    public void setActiveSensor(SensorType sensor) {

        this._group.setActivePanel(sensor);
        this._activeSensorName.setText(TextHelper.translatable(sensor.getNameTranslationKey()));
    }

    @SuppressWarnings("unchecked")
    public SensorSetting getSettings(ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                                     SensorSetting defaultValue) {
        return this._group.getActivePanel()
                .filter(panel -> panel instanceof SensorBehaviorGroupPanel)
                .map(panel -> (SensorBehaviorGroupPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .map(panel -> panel.getSettings(sensorSettingFactory, defaultValue))
                .orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public void resetControls() {

        this._activeSensorName.setText("");
        this._group.clearActivePanel();
        this._group.stream()
                .filter(panel -> panel instanceof SensorBehaviorGroupPanel)
                .map(panel -> (SensorBehaviorGroupPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .forEach(SensorBehaviorGroupPanel::resetControls);
    }

    @SuppressWarnings("unchecked")
    public void setSettings(SensorSetting setting) {

        this._activeSensorName.setText(TextHelper.translatable(setting.Sensor.getNameTranslationKey()));
        this._group.setActivePanel(setting.Sensor);
        this._group.getActivePanel()
                .filter(panel -> panel instanceof SensorBehaviorGroupPanel)
                .map(panel -> (SensorBehaviorGroupPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .ifPresent(panel -> panel.setSettings(setting));
    }

    //region internals

    private final static int HORIZONTAL_MARGIN = 2;
    private final static int CONTROL_SPACING = 4;

    private final PanelGroup<SensorType> _group;
    private final Label _activeSensorName;

    //endregion
}

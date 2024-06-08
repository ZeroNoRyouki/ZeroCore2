/*
 *
 * SensorBehaviorGroupPanel.java
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

import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingFactory;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.control.ChoiceText;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.PanelGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.text.TextHelper;

import java.util.List;

class SensorBehaviorGroupPanel<Reader extends IMachineReader, Writer, SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends Panel {

    public SensorBehaviorGroupPanel(SensorGroupPanelBuilder<Reader, SensorType>.BehaviorGroupBuilder builder) {

        super(builder.getGui(), builder.getSensor().name() + "Behaviors");

        this.setDesiredDimension(builder.getWidth(), builder.getHeight());
        this.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(3)
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Top)
        );

        final List<SensorBehavior> behaviors = builder.getBehaviors();

        this._sensor = builder.getSensor();
        this._behaviorChoice = new ChoiceText<>(builder.getGui(), this._sensor.name() + "Behavior", behaviors);
        this._group = new PanelGroup<>(builder.getGui(), "group", behaviors);

        behaviors.forEach(behavior -> this._behaviorChoice.addText(behavior,
                TextHelper.translatable("gui.zerocore.base.redstone.sensorbehavior." +
                        CodeHelper.neutralLowercase(behavior.name()) + ".text")));

        if (behaviors.size() == 1) {
            this._behaviorChoice.setEnabled(false);
        }

        int height = builder.getHeight();

        this._behaviorChoice.Changed.subscribe((choice, behavior) -> this._group.setActivePanel(behavior));
        this._behaviorChoice.setSelectedIndex(behaviors.get(0));
        this._behaviorChoice.setDesiredDimension(builder.getWidth(), 16);
        this.addControl(this._behaviorChoice);
        height -= 16;

        this._group.setDesiredDimension(builder.getWidth(), height);
        this.addControl(this._group);

        for (final SensorBehavior behavior : behaviors) {
            this._group.setPanel(behavior, new SensorBehaviorPanel<>(builder.getBehaviorBuilder(behavior)));
        }
    }

    @SuppressWarnings("unchecked")
    public SensorSetting getSettings(ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                                     SensorSetting defaultValue) {
        return this._group.getActivePanel()
                .filter(panel -> panel instanceof SensorBehaviorPanel)
                .map(panel -> (SensorBehaviorPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .map(panel -> panel.getSettings(sensorSettingFactory, this._sensor))
                .orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public void resetControls() {

        this._behaviorChoice.setSelectedIndex(this._behaviorChoice.getValidIndices().get(0));
        this._group.clearActivePanel();
        this._group.stream()
                .filter(panel -> panel instanceof SensorBehaviorPanel)
                .map(panel -> (SensorBehaviorPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .forEach(SensorBehaviorPanel::resetControls);
    }

    @SuppressWarnings("unchecked")
    public void setSettings(SensorSetting setting) {

        this._behaviorChoice.setSelectedIndex(setting.Behavior);
        this._group.getActivePanel()
                .filter(panel -> panel instanceof SensorBehaviorPanel)
                .map(panel -> (SensorBehaviorPanel<Reader, Writer, SensorType, SensorSetting>)panel)
                .ifPresent(panel -> panel.setSettings(setting));
    }

    //region internals

    private final SensorType _sensor;
    private final ChoiceText<SensorBehavior> _behaviorChoice;
    private final PanelGroup<SensorBehavior> _group;

    //endregion
}

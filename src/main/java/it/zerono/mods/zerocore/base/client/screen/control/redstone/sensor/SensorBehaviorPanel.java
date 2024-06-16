/*
 *
 * SensorBehaviorPanel.java
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
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.TextInput;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

class SensorBehaviorPanel<Reader extends IMachineReader, Writer, SensorType extends Enum<SensorType> & ISensorType<Reader>,
                            SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends Panel {

    public SensorBehaviorPanel(SensorGroupPanelBuilder<Reader, SensorType>.BehaviorGroupBuilder.BehaviorBuilder builder) {

        super(builder.getGui(), builder.getBehavior().name() + "Panel");

        this.setDesiredDimension(builder.getWidth(), builder.getHeight());
        this.setLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalMargin(4)
                .setVerticalMargin(5)
                .setControlsSpacing(4)
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Top)
        );

        this._behavior = builder.getBehavior();
        this._inputs = builder.getInputs();

        if (this._inputs.length > 0) {

            this._validator = builder.getValidator();
            if (null != this._validator) {

                if (1 == this._inputs.length) {
                    this.setValidator(this::validateFirstInput);
                } else {
                    this.setValidator(this::validateAllInputs);
                }
            }

            for (int idx = 0; idx < this._inputs.length; ++idx) {
                this.addControl(builder.getInputLabel(idx), this._inputs[idx]);
            }

        } else {

            this._validator = null;
        }
    }

    public SensorSetting getSettings(ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                                     SensorType sensor) {

        assert this._inputs.length <= 2;

        final int[] values = new int[2];

        for (int idx = 0; idx < this._inputs.length; ++idx) {
            values[idx] = this._inputs[idx].intValue();
        }

        return sensorSettingFactory.createSetting(sensor, this._behavior, values[0], values[1]);
    }

    public void resetControls() {

        for (final TextInput input : this._inputs) {
            input.setText("0");
        }
    }

    public void setSettings(SensorSetting setting) {

        for (int idx = 0; idx < this._inputs.length; ++idx) {
            this._inputs[idx].setText(Integer.toString(setting.getValue(idx)));
        }
    }

    //region internals

    @SuppressWarnings("DataFlowIssue")
    private void validateFirstInput(IControlContainer container, Consumer<Component> errorReport) {
        this._validator.validate(errorReport::accept, this._inputs[0].intValue());
    }

    @SuppressWarnings("DataFlowIssue")
    private void validateAllInputs(IControlContainer container, Consumer<Component> errorReport) {
        this._validator.validate(errorReport::accept, this._inputs[0].intValue(), this._inputs[1].intValue());
    }

    private final SensorBehavior _behavior;
    private final TextInput[] _inputs;
    @Nullable
    private final ISensorValidator _validator;

    //endregion
}

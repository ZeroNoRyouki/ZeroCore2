/*
 *
 * SensorPanel.java
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
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.FlowLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraftforge.common.util.NonNullConsumer;

public class SensorPanel<Reader extends IMachineReader, Writer,
                         SensorType extends Enum<SensorType> & ISensorType<Reader>,
                         SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends Panel {

    @SafeVarargs
    public SensorPanel(ModContainerScreen<? extends ModContainer> gui, IBindableData<SensorSetting> bindableSettings,
                       int sensorsListColumns, String sensorsListLabelKey, int width, int height,
                       NonNullConsumer<ISensorBuilder<Reader, SensorType>> sensorBuilder,
                       Runnable onSave, Runnable onDisable, SensorSetting resetToSettings,
                       ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                       SensorType... validSensors) {

        super(gui, "sensorPanel");

        this._sensorSettingFactory = sensorSettingFactory;
        bindableSettings.bind(this::setSettings);

        this.setDesiredDimension(width, height);
        this.setLayoutEngine(new FlowLayoutEngine()
                .setZeroMargins()
                .setVerticalMargin(VERTICAL_MARGIN)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));

        final int sensorsListWidth = SensorsList.computeWidth(sensorsListColumns);
        final int sensorsGroupWidth = width - sensorsListWidth;
        final SensorGroupPanelBuilder<Reader, SensorType> builder = new SensorGroupPanelBuilder<>(gui, sensorsGroupWidth,
                height - ClientBaseHelper.PUSH_BUTTON_HEIGHT - VERTICAL_MARGIN * 2, validSensors);

        sensorBuilder.accept(builder);

        this._sensorsGroup = new SensorGroupPanel<>(builder);
        this._sensorsList = new SensorsList<>(builder, sensorsListColumns, sensorsListLabelKey, this::onSensorChanged);
        this.addControl(this._sensorsList, this._sensorsGroup);

        // disable / save / reset

        final Panel buttonsPanel = new Panel(this.getGui());

        buttonsPanel.setDesiredDimension(width, ClientBaseHelper.PUSH_BUTTON_HEIGHT);
        buttonsPanel.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());
        this.addControl(buttonsPanel);

        final Button disableButton;
        final int buttonWidth = 55;
        int buttonX = width - buttonWidth;

        this._resetButton = new Button(gui, "reset", TextHelper.translatable("gui.zerocore.base.generic.reset"));
        this._resetButton.Clicked.subscribe(($1, $2) -> this.setSettings(resetToSettings));
        this._resetButton.setLayoutEngineHint(FixedLayoutEngine.hint(buttonX, 0, buttonWidth, ClientBaseHelper.PUSH_BUTTON_HEIGHT));
        buttonX -= buttonWidth + 2;
        buttonsPanel.addControl(this._resetButton);

        this._saveButton = new Button(gui, "save", TextHelper.translatable("gui.zerocore.base.generic.save"));
        this._saveButton.Clicked.subscribe(($1, $2) -> onSave.run());
        this._saveButton.setLayoutEngineHint(FixedLayoutEngine.hint(buttonX, 0, buttonWidth, ClientBaseHelper.PUSH_BUTTON_HEIGHT));
        buttonsPanel.addControl(this._saveButton);

        disableButton = new Button(gui, "disable", TextHelper.translatable("gui.zerocore.base.generic.disable"));
        disableButton.Clicked.subscribe(($1, $2) -> onDisable.run());
        disableButton.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, sensorsListWidth, ClientBaseHelper.PUSH_BUTTON_HEIGHT));
        buttonsPanel.addControl(disableButton);
    }

    public SensorSetting getSettings(SensorSetting defaultValue) {
        return this._sensorsGroup.getSettings(this._sensorSettingFactory, defaultValue);
    }

     public void setSettings(SensorSetting setting) {

         this.resetSettings();

         if (!setting.Sensor.isDisabled()) {

             this._sensorsList.setSettings(setting);
             this._sensorsGroup.setSettings(setting);
         }
    }

    //region internals

    private void onSensorChanged(SensorType sensor) {

        final boolean sensorDisabled = sensor.isDisabled();

        this._saveButton.setVisible(!sensorDisabled);
        this._resetButton.setVisible(!sensorDisabled);
        this._sensorsGroup.setActiveSensor(sensor);
    }

    private void resetSettings() {

        this._sensorsList.resetSettings();
        this._sensorsGroup.resetControls();

    }

    private static final int VERTICAL_MARGIN = 4;
    private final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> _sensorSettingFactory;
    private final SensorsList<Reader, Writer, SensorType, SensorSetting> _sensorsList;
    private final SensorGroupPanel<Reader, Writer, SensorType, SensorSetting> _sensorsGroup;
    private final Button _saveButton, _resetButton;

    //endregion
}

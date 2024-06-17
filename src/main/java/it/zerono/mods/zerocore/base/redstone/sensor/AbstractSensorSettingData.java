/*
 *
 * AbstractSensorSettingData.java
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

package it.zerono.mods.zerocore.base.redstone.sensor;

import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.AbstractData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractSensorSettingData<Reader extends IMachineReader, Writer,
                                                SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends AbstractData<SensorSetting>
        implements IContainerData {

    protected AbstractSensorSettingData(Class<SensorType> sensorTypeClass,
                                        ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory,
                                        Supplier<SensorSetting> getter, Consumer<SensorSetting> clientSideSetter) {

        super(getter, clientSideSetter);
        this._sensorTypeClass = sensorTypeClass;
        this._factory = factory;
    }

    protected AbstractSensorSettingData(Class<SensorType> sensorTypeClass,
                                        ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory,
                                        Supplier<SensorSetting> getter) {

        super(getter);
        this._sensorTypeClass = sensorTypeClass;
        this._factory = factory;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final var current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return SensorSettingEntry.from(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return SensorSettingEntry.from(this._sensorTypeClass, buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof SensorSettingEntry) {

            @SuppressWarnings("unchecked")
            final var record = (SensorSettingEntry<Reader, Writer, SensorType, SensorSetting>) entry;

            final var settings = record.settings(this._factory);

            this.setClientSideValue(settings);
            this.notify(settings);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record SensorSettingEntry<Reader extends IMachineReader, Writer,
                                      SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                      SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
            (SensorType sensor, SensorBehavior behavior, int value1, int value2)
                implements ISyncedSetEntry {

        private static <Reader extends IMachineReader, Writer,
                        SensorType extends Enum<SensorType> & ISensorType<Reader>,
                        SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        SensorSettingEntry<Reader, Writer, SensorType, SensorSetting> from(Class<SensorType> sensorTypeClass, RegistryFriendlyByteBuf buffer) {

            final SensorType sensor = buffer.readEnum(sensorTypeClass);
            final SensorBehavior behavior = buffer.readEnum(SensorBehavior.class);
            final int value1 = buffer.readInt();
            final int value2 = buffer.readInt();

            return new SensorSettingEntry<>(sensor, behavior, value1, value2);
        }

        private static <Reader extends IMachineReader, Writer,
                        SensorType extends Enum<SensorType> & ISensorType<Reader>,
                        SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        SensorSettingEntry<Reader, Writer, SensorType, SensorSetting> from(SensorSetting setting) {
            return new SensorSettingEntry<>(setting.Sensor, setting.Behavior, setting.Value1, setting.Value1);
        }

        private SensorSetting settings(ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory) {
            return factory.createSetting(this.sensor, this.behavior, this.value1, this.value2);
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {

            buffer.writeEnum(this.sensor);
            buffer.writeEnum(this.behavior);
            buffer.writeInt(this.value1);
            buffer.writeInt(this.value2);
        }
    }

    //endregion

    private final Class<SensorType> _sensorTypeClass;
    private final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> _factory;
    private SensorSetting _lastValue;

    //endregion
}

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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public abstract class AbstractSensorSettingData<Reader extends IMachineReader, Writer,
                                                SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends AbstractData<SensorSetting>
        implements IContainerData {

    protected AbstractSensorSettingData(Class<SensorType> sensorTypeClass,
                                        ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory) {

        super();
        this._sensorTypeClass = sensorTypeClass;
        this._factory = factory;
    }

    protected AbstractSensorSettingData(Class<SensorType> sensorTypeClass,
                                        ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory,
                                        NonNullSupplier<Supplier<SensorSetting>> serverSideGetter) {

        super(serverSideGetter);
        this._sensorTypeClass = sensorTypeClass;
        this._factory = factory;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final SensorSetting current = this._getter.get();

        if (null == this._lastValue || !this._lastValue.equals(current)) {

            this._lastValue = current.copy();

            return buffer -> {

                buffer.writeEnum(current.Sensor);
                buffer.writeEnum(current.Behavior);
                buffer.writeInt(current.Value1);
                buffer.writeInt(current.Value2);
            };
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {

        final SensorType type = dataSource.readEnum(this._sensorTypeClass);
        final SensorBehavior behavior = dataSource.readEnum(SensorBehavior.class);
        final int value1 = dataSource.readInt();
        final int value2 = dataSource.readInt();

        this.notify(this._factory.createSetting(type, behavior, value1, value2));
    }

    //endregion
    //region internals

    private final Class<SensorType> _sensorTypeClass;
    private final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> _factory;
    private SensorSetting _lastValue;

    //endregion
}

/*
 *
 * AbstractSensorSetting.java
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
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.NonNullPredicate;

import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractSensorSetting<Reader extends IMachineReader, Writer,
                                            SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                            SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        implements NonNullPredicate<Reader>, InputSensorAction<Writer> {

    public final SensorType Sensor;
    public final SensorBehavior Behavior;
    public final int Value1;
    public final int Value2;

    protected AbstractSensorSetting(final SensorType sensor, final SensorBehavior behavior, final int v1, final int v2) {

        this.Sensor = sensor;
        this.Behavior = behavior;
        this.Value1 = v1;
        this.Value2 = v2;
    }

    protected AbstractSensorSetting(final CompoundTag data, final Function<CompoundTag, SensorType> sensorTypeGetter) throws IllegalArgumentException {

        if (!data.contains("behavior") || !data.contains("v1") || !data.contains("v2")) {
            throw new IllegalArgumentException("Invalid NBT data");
        }

        this.Sensor = sensorTypeGetter.apply(data);
        this.Behavior = NBTHelper.nbtGetEnum(data, "behavior", SensorBehavior::valueOf, SensorBehavior.Disabled);
        this.Value1 = data.getInt("v1");
        this.Value2 = data.getInt("v2");
    }

    public CompoundTag syncDataTo(CompoundTag data) {

        NBTHelper.nbtSetEnum(data, "behavior", this.Behavior);
        data.putInt("v1", this.Value1);
        data.putInt("v2", this.Value2);

        return data;
    }

    public abstract SensorSetting copy();

    public int getValue(int index) {
        return 0 == index ? this.Value1 : this.Value2;
    }

    //region NonNullPredicate<Reader>

    /**
     * Check if the current (output) setting match the machine state,
     *
     * @param reader the machine reader
     * @return {@code true} if the current sensor is an output sensor that match the machine state, otherwise {@code false}
     */
    @Override
    public boolean test(final Reader reader) {
        return this.Sensor.isOutput() && this.Behavior.outputTest(this.Sensor.applyAsInt(reader), this.Value1, this.Value2);
    }

    //endregion
    //region Object

    @Override
    public int hashCode() {
        return Objects.hash(this.Sensor, this.Behavior, this.Value1, this.Value2);
    }

    //endregion
}

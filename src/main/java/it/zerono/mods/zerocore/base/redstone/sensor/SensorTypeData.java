/*
 *
 * SensorTypeData.java
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

import com.google.common.collect.ImmutableList;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.ToIntFunction;

public final class SensorTypeData<Reader extends IMachineReader>
        implements ISensorType<Reader> {

    public static <Reader extends IMachineReader> SensorTypeData<Reader> input(final SensorBehavior... behaviors) {
        return new SensorTypeData<>(IoDirection.Input, SensorTypeData::inputSensorTest, behaviors);
    }

    public static <Reader extends IMachineReader> SensorTypeData<Reader> output(final ToIntFunction<@NotNull Reader> outputSupplier,
                                                                                final SensorBehavior... behaviors) {
        return new SensorTypeData<>(IoDirection.Output, outputSupplier, behaviors);
    }

    //region ISensorType

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isInput() {
        return this._direction.isInput();
    }

    @Override
    public boolean isOutput() {
        return this._direction.isOutput();
    }

    @Override
    public List<SensorBehavior> getBehaviors() {
        return this._validBehaviors;
    }

    @Override
    public int applyAsInt(final Reader reader) {
        return this._outputTestSupplier.applyAsInt(reader);
    }

    @Override
    public String getTranslationBaseName() {
        return "";
    }

    //endregion
    //region internals

    private SensorTypeData(final IoDirection direction, final ToIntFunction<@NotNull Reader> outputSupplier,
                           final SensorBehavior... behaviors) {

        this._direction = direction;
        this._outputTestSupplier = outputSupplier;
        this._validBehaviors = ImmutableList.copyOf(behaviors);
    }

    private static <Reader extends IMachineReader> int inputSensorTest(final Reader reader) {
        return 0;
    }

    private final IoDirection _direction;
    private final List<SensorBehavior> _validBehaviors;
    private final ToIntFunction<@NotNull Reader> _outputTestSupplier;

    //endregion
}

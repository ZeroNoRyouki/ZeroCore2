/*
 *
 * ISensorBuilder2.java
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

import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.control.TextInput;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.functional.NonNullBiConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface ISensorBuilder<Reader extends IMachineReader, SensorType extends Enum<SensorType> & ISensorType<Reader>> {

    interface IBehaviorGroupBuilder<Reader extends IMachineReader, SensorType extends Enum<SensorType> & ISensorType<Reader>> {

        IBehaviorBuilder<Reader, SensorType> addBehavior(SensorBehavior behavior);

        default IBehaviorGroupBuilder<Reader, SensorType> addInputLessBehavior(SensorBehavior behavior) {
            return this.addBehavior(behavior).build();
        }

        ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsNumbers(String suffix,
                                                                             String amountAboveLabelTranslationKey,
                                                                             String amountBelowLabelTranslationKey,
                                                                             String amountBetweenMinLabelTranslationKey,
                                                                             String amountBetweenMaxLabelTranslationKey,
                                                                             NonNullBiConsumer<SensorBehavior, TextInput> configurator);

        default ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsNumbers(String suffix,
                                                                                     String amountAboveLabelTranslationKey,
                                                                                     String amountBelowLabelTranslationKey,
                                                                                     String amountBetweenMinLabelTranslationKey,
                                                                                     String amountBetweenMaxLabelTranslationKey) {
            return this.addStandardOutputBehaviorsNumbers(suffix, amountAboveLabelTranslationKey, amountBelowLabelTranslationKey,
                    amountBetweenMinLabelTranslationKey, amountBetweenMaxLabelTranslationKey, ($1, $2) -> {});
        }

        ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsPercentages(String amountAboveLabelTranslationKey,
                                                                                 String amountBelowLabelTranslationKey,
                                                                                 String amountBetweenMinLabelTranslationKey,
                                                                                 String amountBetweenMaxLabelTranslationKey,
                                                                                 NonNullBiConsumer<SensorBehavior, TextInput> configurator);

        default ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsPercentages(String amountAboveLabelTranslationKey,
                                                                                         String amountBelowLabelTranslationKey,
                                                                                         String amountBetweenMinLabelTranslationKey,
                                                                                         String amountBetweenMaxLabelTranslationKey) {
            return this.addStandardOutputBehaviorsPercentages(amountAboveLabelTranslationKey, amountBelowLabelTranslationKey,
                    amountBetweenMinLabelTranslationKey, amountBetweenMaxLabelTranslationKey, ($1, $2) -> {});
        }

        ISensorBuilder<Reader, SensorType> build();
    }

    interface IBehaviorBuilder<Reader extends IMachineReader, SensorType extends Enum<SensorType> & ISensorType<Reader>> {

        IBehaviorBuilder<Reader, SensorType> addInputField(String controlLabelTranslationKey, TextInput control);

        default IBehaviorBuilder<Reader, SensorType> addNumberField(String controlLabelTranslationKey,
                                                                    String name, String suffix) {
            return this.addNumberField(controlLabelTranslationKey, name, suffix, ($1, $2) -> {});
        }

        IBehaviorBuilder<Reader, SensorType> addNumberField(String controlLabelTranslationKey, String name,
                                                            String suffix, NonNullBiConsumer<SensorBehavior, TextInput> configurator);

        default IBehaviorBuilder<Reader, SensorType> addPercentageField(String controlLabelTranslationKey,
                                                                        String name) {
            return this.addPercentageField(controlLabelTranslationKey, name, ($1, $2) -> {});
        }

        IBehaviorBuilder<Reader, SensorType> addPercentageField(String controlLabelTranslationKey,
                                                                String name, NonNullBiConsumer<SensorBehavior, TextInput> configurator);

        IBehaviorBuilder<Reader, SensorType> setValidator(ISensorValidator validator);

        IBehaviorGroupBuilder<Reader, SensorType> build();
    }

    IBehaviorGroupBuilder<Reader, SensorType> addSensor(SensorType sensor, Supplier<@NotNull ISprite> buttonOffSprite,
                                                        Supplier<@NotNull ISprite> buttonOnSprite);

    ISensorBuilder<Reader, SensorType> addSeparator();

    void build();
}

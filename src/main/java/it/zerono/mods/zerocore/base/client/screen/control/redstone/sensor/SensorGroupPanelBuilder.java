/*
 *
 * SensorGroupPanelBuilder.java
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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.Label;
import it.zerono.mods.zerocore.lib.client.gui.control.TextConstraints;
import it.zerono.mods.zerocore.lib.client.gui.control.TextInput;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class SensorGroupPanelBuilder<Reader extends IMachineReader, SensorType extends Enum<SensorType> & ISensorType<Reader>>
        implements ISensorBuilder<Reader, SensorType> {

    @SafeVarargs
    public SensorGroupPanelBuilder(ModContainerScreen<? extends ModContainer> gui, int width, int height,
                                   SensorType... validSensors) {

        this._gui = gui;
        this._width = width;
        this._height = height;
        this._validSensors = ObjectSets.unmodifiable(new ObjectArraySet<>(validSensors));
        this._behaviorGroupsOrder = new ObjectArrayList<>(32);
        this._behaviorGroupsBuilders = new Object2ObjectArrayMap<>(32);
    }

    public List<SensorType> getSensors() {
        return this._behaviorGroupsOrder.stream()
                .filter(Objects::nonNull)
                .map(BehaviorGroupBuilder::getSensor)
                .collect(Collectors.toList());
    }

    public List<BehaviorGroupBuilder> getGroupsBuilders() {
        return this._behaviorGroupsOrder;
    }

    public ModContainerScreen<? extends ModContainer> getGui() {
        return this._gui;
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    //region ISensorBuilder2<Reader, SensorType>

    @Override
    public IBehaviorGroupBuilder<Reader, SensorType> addSensor(SensorType sensor, Supplier<@NotNull ISprite> buttonOffSprite,
                                                               Supplier<@NotNull ISprite> buttonOnSprite) {

        Preconditions.checkArgument(this._validSensors.contains(sensor), "The specified sensor is not valid.");

        if (this._behaviorGroupsBuilders.containsKey(sensor)) {
            return this._behaviorGroupsBuilders.get(sensor);
        }

        final BehaviorGroupBuilder builder = new BehaviorGroupBuilder(sensor, buttonOffSprite, buttonOnSprite);

        this._behaviorGroupsBuilders.put(sensor, builder);
        this._behaviorGroupsOrder.add(builder);
        return builder;
    }

    @Override
    public ISensorBuilder<Reader, SensorType> addSeparator() {

        this._behaviorGroupsOrder.add(null);
        return this;
    }

    @Override
    public void build() {
        // nop
    }

    //endregion
    //region BehaviorGroupBuilder

    public class BehaviorGroupBuilder
            implements ISensorBuilder.IBehaviorGroupBuilder<Reader, SensorType> {

        public BehaviorGroupBuilder(SensorType sensor, Supplier<@NotNull ISprite> buttonOffSprite,
                                    Supplier<@NotNull ISprite> buttonOnSprite) {

            this._sensor = sensor;
            this._validBehaviors = sensor.getBehaviors();
            this._buttonOffSprite = buttonOffSprite;
            this._buttonOnSprite = buttonOnSprite;
            this._behaviorsBuilders = new Object2ObjectArrayMap<>(SensorBehavior.values().length);
            this._addedBehaviors = new ObjectArrayList<>(SensorBehavior.values().length);
        }

        public SensorType getSensor() {
            return this._sensor;
        }

        public Supplier<@NotNull ISprite> getButtonOnSprite() {
            return this._buttonOnSprite;
        }

        public Supplier<@NotNull ISprite> getButtonOffSprite() {
            return this._buttonOffSprite;
        }

        public List<SensorBehavior> getBehaviors() {
            return this._addedBehaviors;
        }

        public BehaviorBuilder getBehaviorBuilder(SensorBehavior behavior) {
            return this._behaviorsBuilders.get(behavior);
        }

        public ModContainerScreen<? extends ModContainer> getGui() {
            return SensorGroupPanelBuilder.this._gui;
        }

        public int getWidth() {
            return SensorGroupPanelBuilder.this._width;
        }

        public int getHeight() {
            return SensorGroupPanelBuilder.this._height;
        }

        //region ISensorBuilder2.IBehaviorGroupBuilder<Reader, SensorType>

        @Override
        public IBehaviorBuilder<Reader, SensorType> addBehavior(SensorBehavior behavior) {

            Preconditions.checkArgument(this._validBehaviors.contains(behavior), "The specified behavior is not valid for the sensor being built.");

            if (this._behaviorsBuilders.containsKey(behavior)) {
                return this._behaviorsBuilders.get(behavior);
            }

            final BehaviorBuilder builder = new BehaviorBuilder(behavior);

            this._addedBehaviors.add(behavior);
            this._behaviorsBuilders.put(behavior, builder);
            return builder;
        }

        @Override
        public ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsNumbers(String suffix,
                                                                                    String amountAboveLabelTranslationKey,
                                                                                    String amountBelowLabelTranslationKey,
                                                                                    String amountBetweenMinLabelTranslationKey,
                                                                                    String amountBetweenMaxLabelTranslationKey,
                                                                                    BiConsumer<@NotNull SensorBehavior, @NotNull TextInput> configurator) {

            this.addBehavior(SensorBehavior.ActiveWhileAbove)
                    .addNumberField(amountAboveLabelTranslationKey, "above", suffix, configurator);

            this.addBehavior(SensorBehavior.ActiveWhileBelow)
                    .addNumberField(amountBelowLabelTranslationKey, "below", suffix, configurator);

            this.addBehavior(SensorBehavior.ActiveWhileBetween)
                    .addNumberField(amountBetweenMinLabelTranslationKey, "betweenMin", suffix, configurator)
                    .addNumberField(amountBetweenMaxLabelTranslationKey, "betweenMax", suffix, configurator)
                    .setValidator(this::minMaxValidator);

            return this.build();
        }

        @Override
        public ISensorBuilder<Reader, SensorType> addStandardOutputBehaviorsPercentages(String amountAboveLabelTranslationKey,
                                                                                        String amountBelowLabelTranslationKey,
                                                                                        String amountBetweenMinLabelTranslationKey,
                                                                                        String amountBetweenMaxLabelTranslationKey,
                                                                                        BiConsumer<@NotNull SensorBehavior, @NotNull TextInput> configurator) {

            this.addBehavior(SensorBehavior.ActiveWhileAbove)
                    .addPercentageField(amountAboveLabelTranslationKey, "above", configurator);

            this.addBehavior(SensorBehavior.ActiveWhileBelow)
                    .addPercentageField(amountBelowLabelTranslationKey, "below", configurator);

            this.addBehavior(SensorBehavior.ActiveWhileBetween)
                    .addPercentageField(amountBetweenMinLabelTranslationKey, "betweenMin", configurator)
                    .addPercentageField(amountBetweenMaxLabelTranslationKey, "betweenMax", configurator)
                    .setValidator(this::minMaxValidator);

            return this.build();
        }

        @Override
        public ISensorBuilder<Reader, SensorType> build() {
            return SensorGroupPanelBuilder.this;
        }

        //endregion
        //region BehaviorBuilder

        public class BehaviorBuilder
                implements ISensorBuilder.IBehaviorBuilder<Reader, SensorType> {

            public BehaviorBuilder(SensorBehavior behavior) {

                this._behavior = behavior;
                this._inputs = new TextInput[2];
                this._inputLabels = new Label[2];
                this._nextInputIdx = 0;
            }

            public SensorBehavior getBehavior() {
                return this._behavior;
            }

            public ModContainerScreen<? extends ModContainer> getGui() {
                return SensorGroupPanelBuilder.this._gui;
            }

            public int getWidth() {
                return SensorGroupPanelBuilder.this._width;
            }

            public int getHeight() {
                return SensorGroupPanelBuilder.this._height;
            }

            public TextInput[] getInputs() {
                return Arrays.copyOf(this._inputs, this._nextInputIdx);
            }

            public Label getInputLabel(int idx) {
                return this._inputLabels[idx];
            }

            @Nullable
            public ISensorValidator getValidator() {
                return this._validator;
            }

            //region ISensorBuilder2.IBehaviorBuilder<Reader, SensorType>

            @Override
            public IBehaviorBuilder<Reader, SensorType> addInputField(String controlLabelTranslationKey, TextInput control) {

                Preconditions.checkState(this._nextInputIdx < 2, "Only up to two input fields can be added.");

                this._inputLabels[this._nextInputIdx] = this.createInputFieldLabel(control.getName() + "Label", controlLabelTranslationKey);
                this._inputs[this._nextInputIdx] = control;
                ++this._nextInputIdx;
                return this;
            }

            @Override
            public IBehaviorBuilder<Reader, SensorType> addNumberField(String controlLabelTranslationKey, String name,
                                                                       String suffix,
                                                                       BiConsumer<@NotNull SensorBehavior, @NotNull TextInput> configurator) {

                final TextInput input = this.createInputField(name, suffix);

                input.setMaxLength(10);
                input.addConstraint(TextConstraints.CONSTRAINT_POSITIVE_INTEGER_NUMBER);
                configurator.accept(this._behavior, input);

                return this.addInputField(controlLabelTranslationKey, input);
            }

            @Override
            public IBehaviorBuilder<Reader, SensorType> addPercentageField(String controlLabelTranslationKey,
                                                                           String name,
                                                                           BiConsumer<@NotNull SensorBehavior, @NotNull TextInput> configurator) {

                final TextInput input = this.createInputField(name, "%");

                input.setMaxLength(3);
                input.addConstraint(TextConstraints.CONSTRAINT_PERCENTAGE);
                input.setDesiredDimension(40, 14);
                configurator.accept(this._behavior, input);

                return this.addInputField(controlLabelTranslationKey, input);
            }

            @Override
            public IBehaviorBuilder<Reader, SensorType> setValidator(ISensorValidator validator) {

                this._validator = validator;
                return this;
            }

            @Override
            public IBehaviorGroupBuilder<Reader, SensorType> build() {
                return BehaviorGroupBuilder.this;
            }

            //endregion
            //region internals

            private TextInput createInputField(String name, String suffix) {

                final TextInput input = new TextInput(SensorGroupPanelBuilder.this._gui, name, "0");

                input.setDesiredDimension(70, 14);
                input.setFilter(TextConstraints.FILTER_NUMBERS);
                input.setDisplaySuffix(suffix);
                return input;
            }

            private Label createInputFieldLabel(String name, String translationKey) {

                final Label label = new Label(this.getGui(), name, TextHelper.translatable(translationKey));

                label.setPadding(0);
                label.setDesiredDimension(this.getWidth(), 10);
                return label;
            }

            private final SensorBehavior _behavior;
            private final TextInput[] _inputs;
            private final Label[] _inputLabels;
            private int _nextInputIdx;
            @Nullable
            private ISensorValidator _validator;

            //endregion
        }

        //endregion
        //region internals

        private void minMaxValidator(Consumer<@NotNull Component> errors, int... values) {

            if (values[0] >= values[1]) {
                errors.accept(TextHelper.translatable("gui.zerocore.base.redstone.validation.invalidminmax.text"));
            }
        }

        private final SensorType _sensor;
        private final List<SensorBehavior> _validBehaviors;
        private final Supplier<@NotNull ISprite> _buttonOffSprite, _buttonOnSprite;
        private final Map<SensorBehavior, BehaviorBuilder> _behaviorsBuilders;
        private final List<SensorBehavior> _addedBehaviors;

        //endregion
    }

    //endregion
    //region internals

    private final ModContainerScreen<? extends ModContainer> _gui;
    private final int _width, _height;
    private final Set<SensorType> _validSensors;
    private final Map<SensorType, BehaviorGroupBuilder> _behaviorGroupsBuilders;
    private final List<BehaviorGroupBuilder> _behaviorGroupsOrder;

    //endregion
}

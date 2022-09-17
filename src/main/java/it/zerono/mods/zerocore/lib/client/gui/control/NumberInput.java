/*
 *
 * ChoiceNumber.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public abstract class NumberInput<Type>
        extends AbstractCompositeControl
        implements NonNullSupplier<Type> {

    public final IEvent<BiConsumer<NumberInput<Type>, Type>> Changed; // 2nd arg: the current numeric value

    public void setFormatString(@Nullable final String formatString) {

        this._formatString = formatString;
        this.updateLabel();
    }

    public void setDisplaySuffix(@Nullable final String suffix) {

        this._paintingSuffix = suffix;
        this.updateLabel();
    }

    public Colour getColor() {
        return this._label.getColor();
    }

    public void setColor(final Colour color) {
        this._label.setColor(color);
    }

    public Colour getDisabledColor() {
        return this._label.getDisabledColor();
    }

    public void setDisabledColor(final Colour color) {
        this._label.setDisabledColor(color);
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this._label.getHorizontalAlignment();
    }

    public void setHorizontalAlignment(final HorizontalAlignment alignment) {
        this._label.setHorizontalAlignment(alignment);
    }

    public VerticalAlignment getVerticalAlignment() {
        return this._label.getVerticalAlignment();
    }

    public void setVerticalAlignment(final VerticalAlignment alignment) {
        this._label.setVerticalAlignment(alignment);
    }

    //region type subclasses

    protected abstract void increment(BooleanSupplier useLargeStep, BooleanSupplier toMax);

    protected abstract void decrement(BooleanSupplier useLargeStep, BooleanSupplier toMin);

    protected abstract String getAsString();

    //endregion
    //region AbstractCompoundControl

    @Override
    public void onWindowClosed() {

        super.onWindowClosed();
        this.Changed.unsubscribeAll();
    }

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        final Padding padding = this.getPadding();
        final int h = bounds.Height - padding.getBottom() - padding.getTop();
        final int valueWidth = bounds.Width - h - (padding.getRight() * 2) - padding.getLeft();

        this._label.setBounds(new Rectangle(padding.getLeft(), padding.getTop(), valueWidth, h));
        this._updown.setBounds(new Rectangle(valueWidth + padding.getLeft() + padding.getRight(), padding.getTop(), h, h));
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        super.onPaintBackground(matrix, partialTicks, mouseX, mouseY);
        this.paintHollowRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, this.getTheme().DARK_OUTLINE_COLOR);
    }

    //endregion
    //region internals

    protected NumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);

        this._updown = new UpDown(gui, "ud");
        this._updown.Clicked.subscribe(this::changeSelection);

        this._label = new Label(gui, "value", "0");
        this._label.setAutoSize(false);
        this._label.setColor(Colour.BLACK);

        this.setPadding(3, 2, 2, 2);
        this.setBackground(this.getTheme().FLAT_BACKGROUND_COLOR);

        this.addChildControl(this._label, this._updown);

        this.Changed = new Event<>();
    }

    private void changeSelection(final Direction.AxisDirection changeDirection, final Integer buttonClicked) {

        switch (changeDirection) {

            case POSITIVE:

                this.increment(Screen::hasControlDown, Screen::hasShiftDown);
                break;

            case NEGATIVE:
                this.decrement(Screen::hasControlDown, Screen::hasShiftDown);
                break;
        }

        this.updateLabel();
        this.Changed.raise(c -> c.accept(this, this.get()));
    }

    protected void updateLabel() {
        this._label.setText(this.suffixedText());
    }

    private String suffixedText() {
        return null != this._paintingSuffix ? this.formattedText() + this._paintingSuffix : this.formattedText();
    }

    private String formattedText() {
        return null != this._formatString ? String.format(this._formatString, this.get()) : this.getAsString();
    }

    private final UpDown _updown;
    private final Label _label;
    private String _formatString;
    private String _paintingSuffix;

    //endregion
    //region types implementations

    public static class IntNumberInput
            extends NumberInput<Integer> {

        public IntNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final int value) {
            this(gui, name, Integer.MIN_VALUE, Integer.MAX_VALUE, value);
        }

        public IntNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name,
                              final int min, final int max, final int value) {
            super(gui, name);
            this._min = min;
            this._max = max;
            this.setValue(value);
        }

        public void setValue(final int value) {

            this._value = MathHelper.clamp(value, this._min, this._max);
            this.updateLabel();
        }

        public void setMinValue(final int min) {

            this._min = min;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        public void setMaxValue(final int max) {

            this._max = max;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        /**
         * Set the standard increment/decrement step and the large one
         *
         * @param standard the standard increment/decrement step
         * @param large    the large increment/decrement step
         */
        public void setStep(final int standard, final int large) {

            this._step = standard;
            this._largeStep = large;
        }

        /**
         * Set both the standard increment/decrement step and the large one to the same value
         *
         * @param step the single increment/decrement step to use
         */
        public void setStep(final int step) {
            this._step = this._largeStep = step;
        }

        public int getStandardStep() {
            return this._step;
        }

        public int getLargeStep() {
            return this._largeStep;
        }

        @Override
        protected void increment(final BooleanSupplier useLargeStep, final BooleanSupplier toMax) {

            if (toMax.getAsBoolean()) {

                this._value = this._max;

            } else {

                final int step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final int v = this.getAsInt();

                if (v <= this._max - step) {
                    this._value = v + step;
                } else {
                    this._value = this._max;
                }
            }
        }

        @Override
        protected void decrement(final BooleanSupplier useLargeStep, final BooleanSupplier toMin) {

            if (toMin.getAsBoolean()) {

                this._value = this._min;

            } else {

                final int step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final int v = this.getAsInt();

                if (v >= this._min + step) {
                    this._value = v - step;
                } else {
                    this._value = this._min;
                }
            }
        }

        @Override
        protected String getAsString() {
            return Long.toString(this.getAsInt());
        }

        @Override
        public Integer get() {
            return this._value;
        }

        public int getAsInt() {
            return _value;
        }

        //region internals

        private int _value;
        private int _step;
        private int _largeStep;
        private int _min;
        private int _max;

        //endregion
    }

    public static class LongNumberInput
            extends NumberInput<Long> {

        public LongNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final long value) {
            this(gui, name, Long.MIN_VALUE, Long.MAX_VALUE, value);
        }

        public LongNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name,
                              final long min, final long max, final long value) {
            super(gui, name);
            this._min = min;
            this._max = max;
            this.setValue(value);
        }

        public void setValue(final long value) {

            this._value = MathHelper.clamp(value, this._min, this._max);
            this.updateLabel();
        }

        public void setMinValue(final long min) {

            this._min = min;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        public void setMaxValue(final long max) {

            this._max = max;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        /**
         * Set the standard increment/decrement step and the large one
         *
         * @param standard the standard increment/decrement step
         * @param large    the large increment/decrement step
         */
        public void setStep(final long standard, final long large) {

            this._step = standard;
            this._largeStep = large;
        }

        /**
         * Set both the standard increment/decrement step and the large one to the same value
         *
         * @param step the single increment/decrement step to use
         */
        public void setStep(final long step) {
            this._step = this._largeStep = step;
        }

        public long getStandardStep() {
            return this._step;
        }

        public long getLargeStep() {
            return this._largeStep;
        }

        @Override
        protected void increment(final BooleanSupplier useLargeStep, final BooleanSupplier toMax) {

            if (toMax.getAsBoolean()) {

                this._value = this._max;

            } else {

                final long step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final long v = this.getAsLong();

                if (v <= this._max - step) {
                    this._value = v + step;
                } else {
                    this._value = this._max;
                }
            }
        }

        @Override
        protected void decrement(final BooleanSupplier useLargeStep, final BooleanSupplier toMin) {

            if (toMin.getAsBoolean()) {

                this._value = this._min;

            } else {

                final long step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final long v = this.getAsLong();

                if (v >= this._min + step) {
                    this._value = v - step;
                } else {
                    this._value = this._min;
                }
            }
        }

        @Override
        protected String getAsString() {
            return Long.toString(this.getAsLong());
        }

        @Override
        public Long get() {
            return this._value;
        }

        public long getAsLong() {
            return _value;
        }

        //region internals

        private long _value;
        private long _step;
        private long _largeStep;
        private long _min;
        private long _max;

        //endregion
    }

    public static class FloatNumberInput
            extends NumberInput<Float> {

        public FloatNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final float value) {
            this(gui, name, Float.MIN_VALUE, Float.MAX_VALUE, value);
        }

        public FloatNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                final float min, final float max, final float value) {
            super(gui, name);
            this._min = min;
            this._max = max;
            this.setValue(value);
        }

        public void setValue(final float value) {

            this._value = MathHelper.clamp(value, this._min, this._max);
            this.updateLabel();
        }

        public void setMinValue(final float min) {

            this._min = min;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        public void setMaxValue(final float max) {

            this._max = max;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        /**
         * Set the standard increment/decrement step and the large one
         *
         * @param standard the standard increment/decrement step
         * @param large    the large increment/decrement step
         */
        public void setStep(final float standard, final float large) {

            this._step = standard;
            this._largeStep = large;
        }

        /**
         * Set both the standard increment/decrement step and the large one to the same value
         *
         * @param step the single increment/decrement step to use
         */
        public void setStep(final float step) {
            this._step = this._largeStep = step;
        }

        public float getStandardStep() {
            return this._step;
        }

        public float getLargeStep() {
            return this._largeStep;
        }

        @Override
        protected void increment(final BooleanSupplier useLargeStep, final BooleanSupplier toMax) {

            if (toMax.getAsBoolean()) {

                this._value = this._max;

            } else {

                final float step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final float v = this.getAsFloat();

                if (v <= this._max - step) {
                    this._value = v + step;
                } else {
                    this._value = this._max;
                }
            }
        }

        @Override
        protected void decrement(final BooleanSupplier useLargeStep, final BooleanSupplier toMin) {

            if (toMin.getAsBoolean()) {

                this._value = this._min;

            } else {

                final float step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final float v = this.getAsFloat();

                if (v >= this._min + step) {
                    this._value = v - step;
                } else {
                    this._value = this._min;
                }
            }
        }

        @Override
        protected String getAsString() {
            return Float.toString(this.getAsFloat());
        }

        @Override
        public Float get() {
            return this._value;
        }

        public float getAsFloat() {
            return _value;
        }

        //region internals

        private float _value;
        private float _step;
        private float _largeStep;
        private float _min;
        private float _max;

        //endregion
    }

    public static class DoubleNumberInput
            extends NumberInput<Double> {

        public DoubleNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final double value) {
            this(gui, name, Float.MIN_VALUE, Float.MAX_VALUE, value);
        }

        public DoubleNumberInput(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                final double min, final double max, final double value) {
            super(gui, name);
            this._min = min;
            this._max = max;
            this.setValue(value);
        }

        public void setValue(final double value) {

            this._value = MathHelper.clamp(value, this._min, this._max);
            this.updateLabel();
        }

        public void setMinValue(final double min) {

            this._min = min;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        public void setMaxValue(final double max) {

            this._max = max;
            this._value = MathHelper.clamp(this._value, this._min, this._max);
            this.updateLabel();
        }

        /**
         * Set the standard increment/decrement step and the large one
         *
         * @param standard the standard increment/decrement step
         * @param large    the large increment/decrement step
         */
        public void setStep(final double standard, final double large) {

            this._step = standard;
            this._largeStep = large;
        }

        /**
         * Set both the standard increment/decrement step and the large one to the same value
         *
         * @param step the single increment/decrement step to use
         */
        public void setStep(final double step) {
            this._step = this._largeStep = step;
        }

        public double getStandardStep() {
            return this._step;
        }

        public double getLargeStep() {
            return this._largeStep;
        }

        @Override
        protected void increment(final BooleanSupplier useLargeStep, final BooleanSupplier toMax) {

            if (toMax.getAsBoolean()) {

                this._value = this._max;

            } else {

                final double step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final double v = this.getAsDouble();

                if (v <= this._max - step) {
                    this._value = v + step;
                } else {
                    this._value = this._max;
                }
            }
        }

        @Override
        protected void decrement(final BooleanSupplier useLargeStep, final BooleanSupplier toMin) {

            if (toMin.getAsBoolean()) {

                this._value = this._min;

            } else {

                final double step = useLargeStep.getAsBoolean() ? this._largeStep : this._step;
                final double v = this.getAsDouble();

                if (v >= this._min + step) {
                    this._value = v - step;
                } else {
                    this._value = this._min;
                }
            }
        }

        @Override
        protected String getAsString() {
            return Double.toString(this.getAsDouble());
        }

        @Override
        public Double get() {
            return this._value;
        }

        public double getAsDouble() {
            return _value;
        }

        //region internals

        private double _value;
        private double _step;
        private double _largeStep;
        private double _min;
        private double _max;

        //endregion
    }

    //endregion
}

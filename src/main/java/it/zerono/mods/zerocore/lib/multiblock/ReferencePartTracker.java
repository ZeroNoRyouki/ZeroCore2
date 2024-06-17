/*
 *
 * ReferencePartTracker.java
 *
 * A multiblock library for making irregularly-shaped multiblock machines
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * Original author: Erogenous Beef
 * https://github.com/erogenousbeef/BeefCore
 *
 * Minecraft 1.9+ port and further development: ZeroNoRyouki
 * https://github.com/ZeroNoRyouki/ZeroCore2
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 "ZeroNoRyouki"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.zerono.mods.zerocore.lib.multiblock;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class ReferencePartTracker<Controller extends IMultiblockController<Controller>>
    implements Comparable<ReferencePartTracker<Controller>> {

    public ReferencePartTracker() {
        this._part = null;
    }

    public boolean isInvalid() {
        return null == this._part;
    }

    public void invalidate() {
        this._part = null;
    }

    @Nullable
    public IMultiblockPart<Controller> get() {
        return this._part;
    }

    public Optional<BlockPos> getPosition() {
        return null != this._part ? Optional.of(this._part.getWorldPosition()) : Optional.empty();
    }

    public void forfeitSaveDelegate() {

        if (null != this._part) {
            this._part.forfeitMultiblockSaveDelegate();
        }
    }

    public void accept(final IMultiblockPart<Controller> part) {

        if (part.isPartInvalid() || part == this._part) {
            return;
        }

        if (null == this._part) {

            this._part = part;
            this._part.becomeMultiblockSaveDelegate();

        } else if (part.getWorldPosition().compareTo(this._part.getWorldPosition()) < 0) {

            this._part.forfeitMultiblockSaveDelegate();
            this._part = part;
            this._part.becomeMultiblockSaveDelegate();

        } else {

            part.forfeitMultiblockSaveDelegate();
        }
    }

    public void accept(final Iterable<IMultiblockPart<Controller>> parts) {

        BlockPos referencePosition = null;
        BlockPos partPosition;

        this.invalidate();

        for (final IMultiblockPart<Controller> part : parts) {

            if (part.isPartInvalid()) {
                continue;
            }

            partPosition = part.getWorldPosition();

            //noinspection ConstantConditions
            if (null == this._part || partPosition.compareTo(referencePosition) < 0) {

                this._part = part;
                referencePosition = partPosition;
            }
        }

        if (null != this._part) {
            this._part.becomeMultiblockSaveDelegate();
        }
    }

    public void consume(final BiConsumer<IMultiblockPart<Controller>, BlockPos> consumer) {

        if (null != this._part) {
            consumer.accept(this._part, this._part.getWorldPosition());
        }
    }

    public boolean test(final IMultiblockPart<Controller> other) {
        return this._part == other;
    }

    //region Comparable<ReferencePartTracker<Controller>>

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(final ReferencePartTracker<Controller> other) {

        if (other.isInvalid()) {
            return this.isInvalid() ? 0 : 1;
        }

        return this._part.getWorldPosition().compareTo(other._part.getWorldPosition());
    }

    //endregion
    //region internals

    private IMultiblockPart<Controller> _part;

    //endregion
}

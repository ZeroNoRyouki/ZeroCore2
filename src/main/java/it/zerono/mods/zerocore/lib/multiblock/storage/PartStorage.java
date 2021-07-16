/*
 *
 * PartStorage.java
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

package it.zerono.mods.zerocore.lib.multiblock.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.world.NeighboringPositions;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PartStorage<Controller extends IMultiblockController<Controller>>
    implements IPartStorage<Controller> {

    public PartStorage() {

        this._parts = new Long2ObjectAVLTreeMap<>();
        this._parts.defaultReturnValue(null);
        this._values = this._parts.values();
    }

    //region IPartStorage

    @Override
    public boolean isEmpty() {
        return this._parts.isEmpty();
    }

    @Override
    public int size() {
        return this._parts.size();
    }

    @Override
    public boolean contains(final IMultiblockPart<Controller> part) {

        if (this._parts.isEmpty()) {
            return false;
        }

        final long firstHash = this._parts.firstLongKey();
        final long lastHash = this._parts.lastLongKey();
        final long positionHash = part.getWorldPositionHash();

        return (positionHash >= firstHash) && (positionHash <= lastHash) && this._parts.containsKey(positionHash);
    }

    @Override
    public boolean contains(final BlockPos[] positions) {

        if (this._parts.isEmpty()) {
            return false;
        }

        final long firstHash = this._parts.firstLongKey();
        final long lastHash = this._parts.lastLongKey();

        for (final BlockPos position : positions) {

            final long positionHash = position.toLong();

            if ((positionHash >= firstHash) && (positionHash <= lastHash) && this._parts.containsKey(positionHash)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(final long[] positionsHashes) {

        if (this._parts.isEmpty()) {
            return false;
        }

        final long firstHash = this._parts.firstLongKey();
        final long lastHash = this._parts.lastLongKey();

        for (final long positionHash : positionsHashes) {
            if ((positionHash >= firstHash) && (positionHash <= lastHash) && this._parts.containsKey(positionHash)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(final NeighboringPositions positions) {

        if (this._parts.isEmpty()) {
            return false;
        }

        final long firstHash = this._parts.firstLongKey();
        final long lastHash = this._parts.lastLongKey();

        for (int index = 0; index < positions.size(); ++index) {

            final long positionHash = positions.getHash(index);

            if ((positionHash >= firstHash) && (positionHash <= lastHash) && this._parts.containsKey(positionHash)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> get(final BlockPos position) {
        return this.get(position.toLong());
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> get(final long positionHash) {

        if (this._parts.isEmpty()) {
            return null;
        }

        final long firstHash = this._parts.firstLongKey();
        final long lastHash = this._parts.lastLongKey();

        return (positionHash >= firstHash) && (positionHash <= lastHash) ? this._parts.get(positionHash) : null;
    }

    @Override
    public void get(final NeighboringPositions positions, final List<IMultiblockPart<Controller>> foundParts) {

        final int size = positions.size();

        for (int idx = 0; idx < size; ++idx) {

            final IMultiblockPart<Controller> part = this.get(positions.getHash(idx));

            if (null != part) {
                foundParts.add(part);
            }
        }
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> getFirst() {
        return this.isEmpty() ? null : this._parts.get(this._parts.firstLongKey());
    }

    @Override
    public void addOrReplace(final IMultiblockPart<Controller> part) {
        this._parts.put(part.getWorldPositionHash(), part);
    }

    @Override
    public void addAll(final IPartStorage<Controller> parts) {

        if (parts instanceof PartStorage) {
            this._parts.putAll(((PartStorage<Controller>)parts)._parts);
        } else {
            IPartStorage.super.addAll(parts);
        }
    }

    @Override
    public void remove(final IMultiblockPart<Controller> part) {

        final long positionHash = part.getWorldPositionHash();

        if (this.get(positionHash) == part) {
            this._parts.remove(positionHash);
        }
    }

    @Override
    public void removeAll(final Collection<IMultiblockPart<Controller>> parts) {
        this._values.removeAll(parts);
    }

    @Override
    public void clear() {
        this._parts.clear();
    }

    @Override
    public Collection<IMultiblockPart<Controller>> unmodifiable() {

        if (null == this._partsUnmodifiable) {
            this._partsUnmodifiable = ObjectCollections.unmodifiable(this._values);
        }

        return this._partsUnmodifiable;
    }

    @Override
    public Stream<IMultiblockPart<Controller>> stream() {
        return this._values.stream();
    }

    @Override
    public Stream<IMultiblockPart<Controller>> parallelStream() {
        return this._values.parallelStream();
    }

    @Override
    public void forEach(final Consumer<IMultiblockPart<Controller>> consumer,
                         final Predicate<IMultiblockPart<Controller>> filter) {
        /*
        Long2ObjectMaps.fastForEach(this._parts, entry -> {

            final IMultiblockPart<Controller> part = entry.getValue();

            if (filter.test(part)) {
                consumer.accept(part);
            }
        });
        */

        for (final IMultiblockPart<Controller> part : this._values) {

            if (filter.test(part)) {
                consumer.accept(part);
            }
        }
    }

    @Override
    public void forEachValidPart(final Consumer<IMultiblockPart<Controller>> consumer) {

        /*
        Long2ObjectMaps.fastForEach(this._parts, entry -> {

            final IMultiblockPart<Controller> part = entry.getValue();

            if (!part.isPartInvalid()) {
                consumer.accept(part);
            }
        });
        */

        for (final IMultiblockPart<Controller> part : this._values) {

            if (!part.isPartInvalid()) {
                consumer.accept(part);
            }
        }
    }

    @Override
    public void forEachNotVisitedPart(final Consumer<IMultiblockPart<Controller>> consumer) {

        /*
        Long2ObjectMaps.fastForEach(this._parts, entry -> {

            final IMultiblockPart<Controller> part = entry.getValue();

            if (part.isNotVisited()) {
                consumer.accept(part);
            }
        });
        */

        for (final IMultiblockPart<Controller> part : this._values) {

            if (part.isNotVisited()) {
                consumer.accept(part);
            }
        }
    }

    @Override
    public CuboidBoundingBox boundingBox() {
        return new CuboidBoundingBox(BlockPos.fromLong(this._parts.firstLongKey()), BlockPos.fromLong(this._parts.lastLongKey()));
    }

    //endregion
    //region Iterable<IMultiblockPart<Controller>>

    @Override
    public Iterator<IMultiblockPart<Controller>> iterator() {
        return this._values.iterator();
    }

    @Override
    public void forEach(Consumer<? super IMultiblockPart<Controller>> action) {

        /*
        Long2ObjectMaps.fastForEach(this._parts, entry -> action.accept(entry.getValue()));
        */

        for (final IMultiblockPart<Controller> part : this._values) {
            action.accept(part);
        }
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        //noinspection AutoBoxing
        return String.format("%d parts", this._parts.size());
    }

    //endregion
    //region internals

    private final Long2ObjectAVLTreeMap<IMultiblockPart<Controller>> _parts;
    private final ObjectCollection<IMultiblockPart<Controller>> _values;

    private Collection<IMultiblockPart<Controller>> _partsUnmodifiable;

    //endregion
}

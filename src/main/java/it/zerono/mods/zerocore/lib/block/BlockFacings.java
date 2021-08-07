/*
 *
 * BlockFacings.java
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

package it.zerono.mods.zerocore.lib.block;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A general purpose class to track the state of all 6 faces of a block
 * <p>
 * Example usages:
 * - track which faces are exposed on the outside walls of a complex structure
 * - track which faces is connected to a face of a similar block
 */
public final class BlockFacings {

    public static final BlockFacings NONE;
    public static final BlockFacings ALL;
    public static final BlockFacings DOWN;
    public static final BlockFacings UP;
    public static final BlockFacings NORTH;
    public static final BlockFacings SOUTH;
    public static final BlockFacings WEST;
    public static final BlockFacings EAST;
    public static final BlockFacings VERTICAL;
    public static final BlockFacings HORIZONTAL;
    public static final BlockFacings AXIS_X;
    public static final BlockFacings AXIS_Y;
    public static final BlockFacings AXIS_Z;

    public static final BooleanProperty FACING_DOWN = BooleanProperty.create("downFacing");
    public static final BooleanProperty FACING_UP = BooleanProperty.create("upFacing");
    public static final BooleanProperty FACING_WEST = BooleanProperty.create("westFacing");
    public static final BooleanProperty FACING_EAST = BooleanProperty.create("eastFacing");
    public static final BooleanProperty FACING_NORTH = BooleanProperty.create("northFacing");
    public static final BooleanProperty FACING_SOUTH = BooleanProperty.create("southFacing");

    public byte value() {
        return this._value;
    }

    /**
     * Check if a specific face is "set"
     *
     * @param facing the face to check
     * @return true if the face is "set", false otherwise
     */
    public boolean isSet(final Direction facing) {
        return 0 != (this._value & (1 << facing.get3DDataValue()));
    }

    /**
     * Check if any face is "set" except the specified face
     *
     * @param facing the face to exclude
     * @return true if any face is "set" with the exception of the specified one, false otherwise
     */
    public boolean except(final Direction facing) {
        return this.any() && !this.isSet(facing);
    }

    public boolean none() {
        return 0 == this._value;
    }

    public boolean any() {
        return 0 != this._value;
    }

    public boolean one() {
        return 1 == this.countFacesIf(true);
    }

    public boolean some() {
        return 0 != this._value;
    }

    public boolean all() {
        return 0x3f == this._value;
    }

    public boolean down() {
        return this.isSet(Direction.DOWN);
    }

    public boolean up() {
        return this.isSet(Direction.UP);
    }

    public boolean north() {
        return this.isSet(Direction.NORTH);
    }

    public boolean south() {
        return this.isSet(Direction.SOUTH);
    }

    public boolean west() {
        return this.isSet(Direction.WEST);
    }

    public boolean east() {
        return this.isSet(Direction.EAST);
    }

    public void ifSet(final Direction facing, final Runnable task) {

        if (this.isSet(facing)) {
            task.run();
        }
    }

    public void ifNotSet(final Direction facing, final Runnable task) {

        if (!this.isSet(facing)) {
            task.run();
        }
    }

    public void ifSet(final Direction facing, final Consumer<Direction> consumer) {

        if (this.isSet(facing)) {
            consumer.accept(facing);
        }
    }

    public void ifNotSet(final Direction facing, final Consumer<Direction> consumer) {

        if (!this.isSet(facing)) {
            consumer.accept(facing);
        }
    }

    public BlockState toBlockState(final BlockState state) {
        //noinspection AutoBoxing
        return state.setValue(FACING_DOWN, this.isSet(Direction.DOWN))
                .setValue(FACING_UP, this.isSet(Direction.UP))
                .setValue(FACING_WEST, this.isSet(Direction.WEST))
                .setValue(FACING_EAST, this.isSet(Direction.EAST))
                .setValue(FACING_NORTH, this.isSet(Direction.NORTH))
                .setValue(FACING_SOUTH, this.isSet(Direction.SOUTH));
    }

    /**
     * Return a BlockFacing object that describe the current facing with the given face set or unset
     *
     * @param facing the face to modify
     * @param value  the new value for the state of the face
     * @return a BlockFacing object
     */
    public BlockFacings set(final Direction facing, final boolean value) {

        byte newHash = this._value;

        if (value) {
            newHash |= (1 << facing.get3DDataValue());
        } else {
            newHash &= ~(1 << facing.get3DDataValue());
        }

        return BlockFacings.from(newHash);
    }

    /**
     * Count the number of faces that are in the required state
     *
     * @param areSet specify if you are looking for "set" faces (true) or not (false)
     * @return the number of faces found in the required state
     */
    public int countFacesIf(final boolean areSet) {

        final int checkFor = areSet ? 1 : 0;
        int mask = this._value;
        int faces = 0;

        for (int i = 0; i < 6; ++i, mask = mask >>> 1) {
            if ((mask & 1) == checkFor) {
                ++faces;
            }
        }

        return faces;
    }

    /**
     * Return a BlockFacingsProperty for the current facing
     *
     * @return a BlockFacingsProperty value
     */
    public BlockFacingsProperty toProperty() {

        for (final BlockFacingsProperty value : BlockFacingsProperty.values()) {
            if (value.getHash() == this._value) {
                return value;
            }
        }

        return BlockFacingsProperty.None;
    }

    /**
     * Offset the given BlockPos in all direction set in this object
     *
     * @param originalPosition the original position
     * @return the new position
     */
    public BlockPos offsetBlockPos(final BlockPos originalPosition) {

        int x = 0, y = 0, z = 0;

        for (Direction facing : CodeHelper.DIRECTIONS) {
            if (this.isSet(facing)) {

                x += facing.getStepX();
                y += facing.getStepY();
                z += facing.getStepZ();
            }
        }

        return originalPosition.offset(x, y, z);
    }

    /**
     * Return the first face that is in the required state
     *
     * @param isSet specify if you are looking for "set" faces (true) or not (false)
     * @return the first face that match the required state or null if no face is found
     */
    public Optional<Direction> firstIf(final boolean isSet) {

        for (Direction facing : CodeHelper.DIRECTIONS) {
            if (isSet == this.isSet(facing)) {
                return Optional.of(facing);
            }
        }

        return Optional.empty();
    }

    public Stream<Direction> stream() {
        return Arrays.stream(CodeHelper.DIRECTIONS)
                .filter(this::isSet);
    }

    /**
     * Return a BlockFacing object that describe the passed in state
     *
     * @param down  the state of the "down" face
     * @param up    the state of the "up" face
     * @param north the state of the "north" face
     * @param south the state of the "south" face
     * @param west  the state of the "west" face
     * @param east  the state of the "east" face
     * @return a BlockFacing object
     */
    public static BlockFacings from(final boolean down, final boolean up, final boolean north, final boolean south,
                                    final boolean west, final boolean east) {
        return BlockFacings.from(BlockFacings.computeHash(down, up, north, south, west, east));
    }

    /**
     * Return a BlockFacing object with the passed in {@link Direction} set to true
     *
     * @param directions the {@link Direction}s to set to true
     * @return a BlockFacing object
     */
    public static BlockFacings from(final Direction... directions) {

        final boolean[] facings = {false, false, false, false, false, false};

        for (final Direction direction : directions) {
            facings[direction.ordinal()] = true;
        }

        return from(facings);
    }

    /**
     * Return a BlockFacing object that describe the passed in state
     *
     * @param facings an array describing the state. the elements of the array must be filled in following the order in Direction.VALUES
     * @return a BlockFacing object
     */
    public static BlockFacings from(final boolean[] facings) {
        return BlockFacings.from(BlockFacings.computeHash(facings));
    }

    public static BlockFacings from(final Direction.Axis axis) {

        switch (axis) {

            default:
            case X:
                return AXIS_X;

            case Y:
                return AXIS_Y;

            case Z:
                return AXIS_Z;
        }
    }

    public static BlockFacings from(final Direction.Plane plane) {

        switch (plane) {

            default:
            case VERTICAL:
                return VERTICAL;

            case HORIZONTAL:
                return HORIZONTAL;
        }
    }

    static BlockFacings from(final byte hash) {

        BlockFacings facings = s_cache[hash];

        if (null == facings) {
            return s_cache[hash] = new BlockFacings(hash);
        }

        return facings;
    }

    public static byte computeHash(final boolean down, final boolean up, final boolean north, final boolean south,
                                   final boolean west, final boolean east) {

        byte hash = 0;

        if (down) {
            hash |= (1 << Direction.DOWN.get3DDataValue());
        }

        if (up) {
            hash |= (1 << Direction.UP.get3DDataValue());
        }

        if (north) {
            hash |= (1 << Direction.NORTH.get3DDataValue());
        }

        if (south) {
            hash |= (1 << Direction.SOUTH.get3DDataValue());
        }

        if (west) {
            hash |= (1 << Direction.WEST.get3DDataValue());
        }

        if (east) {
            hash |= (1 << Direction.EAST.get3DDataValue());
        }

        return hash;
    }

    //region Object

    @Override
    public String toString() {
        return BlockFacings.NONE == this ? "Facings: NONE" :
                String.format("Facings: %s%s%s%s%s%s",
                        this.isSet(Direction.DOWN) ? "DOWN " : "",
                        this.isSet(Direction.UP) ? "UP " : "",
                        this.isSet(Direction.NORTH) ? "NORTH " : "",
                        this.isSet(Direction.SOUTH) ? "SOUTH " : "",
                        this.isSet(Direction.WEST) ? "WEST " : "",
                        this.isSet(Direction.EAST) ? "EAST " : "");
    }

    @Override
    public int hashCode() {
        return this._value;
    }

    //endregion
    //region internals

    private BlockFacings(final byte value) {
        this._value = value;
    }

    static byte computeHash(final boolean[] facings) {

        byte hash = 0;
        int len = facings.length;

        if (len > CodeHelper.DIRECTIONS.length) {
            throw new IllegalArgumentException("Invalid length of facings array");
        }

        for (int i = 0; i < len; ++i) {
            if (facings[i]) {
                hash |= (1 << i);
            }
        }

        return hash;
    }

    private final byte _value;

    private static final BlockFacings[] s_cache;

    static {

        byte hash;

        s_cache = new BlockFacings[256];

        hash = BlockFacings.computeHash(false, false, false, false, false, false);
        s_cache[hash] = NONE = new BlockFacings(hash);

        hash = BlockFacings.computeHash(true, true, true, true, true, true);
        s_cache[hash] = ALL = new BlockFacings(hash);

        hash = BlockFacings.computeHash(true, false, false, false, false, false);
        s_cache[hash] = DOWN = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, true, false, false, false, false);
        s_cache[hash] = UP = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, true, false, false, false);
        s_cache[hash] = NORTH = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, false, true, false, false);
        s_cache[hash] = SOUTH = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, false, false, true, false);
        s_cache[hash] = WEST = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, false, false, false, true);
        s_cache[hash] = EAST = new BlockFacings(hash);

        hash = BlockFacings.computeHash(true, true, false, false, false, false);
        s_cache[hash] = VERTICAL = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, true, true, true, true);
        s_cache[hash] = HORIZONTAL = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, false, false, true, true);
        s_cache[hash] = AXIS_X = new BlockFacings(hash);

        hash = BlockFacings.computeHash(false, false, true, true, false, false);
        s_cache[hash] = AXIS_Z = new BlockFacings(hash);

        AXIS_Y = VERTICAL;
    }

    //endregion
}

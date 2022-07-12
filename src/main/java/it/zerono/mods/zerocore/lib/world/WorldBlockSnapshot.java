///*
// *
// * WorldBlockSnapshot.java
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.zerocore.lib.world;
//
//import com.google.common.base.Predicates;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Lists;
//import net.minecraft.block.BlockState;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3i;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.ModelDataManager;
//import net.minecraftforge.client.model.data.ModelData;
//import net.minecraftforge.client.model.data.IModelData;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.function.Predicate;
//
//public class WorldBlockSnapshot {
//
//    public World TEMP_WORLD;
//    public BlockPos TEMP_START;
//    public BlockPos TEMP_END;
//
//    public static WorldBlockSnapshot from(final World world, final BlockPos position) {
//        return from(world, position, position, Predicates.alwaysTrue());
//    }
//
//    public static WorldBlockSnapshot from(final World world, final BlockPos start, final BlockPos end) {
//        return from(world, start, end, Predicates.alwaysTrue());
//    }
//
//    public static WorldBlockSnapshot from(final World world, final BlockPos start, final BlockPos end,
//                                          final Predicate<BlockState> filter) {
//
//        final List<Entry> entries = Lists.newArrayList();
//        final int countX = Math.abs(start.getX() - end.getX()) + 1;
//        final int countY = Math.abs(start.getY() - end.getY()) + 1;
//        final int countZ = Math.abs(start.getZ() - end.getZ()) + 1;
//
//        for (int y = 0; y < countY; ++y) {
//            for (int x = 0; x < countX; ++x) {
//                for (int z = 0; z <= countZ; ++z) {
//
//                    final Entry entry = entryFrom(world, start, new Vec3i(x, y, z), filter);
//
//                    if (null != entry) {
//                        entries.add(entry);
//                    }
//                }
//            }
//        }
//
//        WorldBlockSnapshot n = new WorldBlockSnapshot(entries, countX, countY, countZ);
//
//        n.TEMP_WORLD = world;
//        n.TEMP_START = start;
//        n.TEMP_END = end;
//
//        return n;
//    }
//
//    public List<Entry> getEntries() {
//        return this._entries;
//    }
//
//    public int getBlockCountX() {
//        return this._countX;
//    }
//
//    public int getBlockCountY() {
//        return this._countY;
//    }
//
//    public int getBlockCountZ() {
//        return this._countZ;
//    }
//
//    public static class Entry {
//
//        public final Vec3i Offset;
//        public final BlockState State;
//        public final IModelData ModelData;
//
//        //region internals
//
//        private Entry(final Vec3i offset, final BlockState state, final IModelData data) {
//
//            this.Offset = offset;
//            this.State = state;
//            this.ModelData = data;
//        }
//
//        //endregion
//    }
//
//    //region internals
//
//    private WorldBlockSnapshot(final List<Entry> entries, final int lengthX, final int lengthY, final int lengthZ) {
//
//        this._entries = ImmutableList.copyOf(entries);
//        this._countX = lengthX;
//        this._countY = lengthY;
//        this._countZ = lengthZ;
//    }
//
//    @Nullable
//    private static Entry entryFrom(final World world, final BlockPos origin, final Vec3i offset, final Predicate<BlockState> filter) {
//
//        final BlockPos position = origin.add(offset);
//        final BlockState state = world.getBlockState(position);
//
//        if (filter.test(state)) {
//            return new Entry(offset, state, getModelData(world, position));
//        } else {
//            return null;
//        }
//    }
//
//    private static IModelData getModelData(final World world, final BlockPos position) {
//
//        final IModelData data = ModelDataManager.getModelData(world, position);
//
//        return null != data ? data : ModelData.EMPTY;
//    }
//
//    private final List<Entry> _entries;
//    private final int _countX;
//    private final int _countY;
//    private final int _countZ;
//
//    //endregion
//}

///*
// *
// * OreWhitelistedConfig.java
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
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import com.mojang.datafixers.Dynamic;
//import com.mojang.datafixers.types.DynamicOps;
//import it.zerono.mods.zerocore.ZeroCore;
//import it.zerono.mods.zerocore.internal.Log;
//import it.zerono.mods.zerocore.lib.block.pattern.BlocksMatcher;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.gen.feature.IFeatureConfig;
//
//import java.util.Map;
//import java.util.function.Predicate;
//
//@SuppressWarnings({"WeakerAccess"})
//public class OreWhitelistedConfig implements IFeatureConfig, Predicate<BlockState> {
//
//    public static final ResourceLocation FILTER_GENERIC_STONE = ZeroCore.newID("generic_vanilla_stone");
//    public static final ResourceLocation FILTER_NETHERRACK = ZeroCore.newID("vanilla_netherrack");
//    public static final ResourceLocation FILTER_ENDSTONE = ZeroCore.newID("vanilla_endstone");
//
//    public static void addFilter(final ResourceLocation id, final Predicate<BlockState> filter) {
//        s_filters.put(id, filter);
//    }
//
//    public static Predicate<BlockState> getFilter(final ResourceLocation id) {
//
//        if (!s_filters.containsKey(id)) {
//
//            Log.LOGGER.debug("[OreWhitelistedConfig] Unknown filter requested: {}", id);
//            return blockState -> false;
//        }
//
//        return s_filters.get(id);
//    }
//
//    public OreWhitelistedConfig(final ResourceLocation filterId, final BlockState replaceWith, final int size,
//                                final IWorldGenWhiteList whiteList, final boolean useBlackListLogic) {
//
//        this._filterId = filterId;
//        this._filter = getFilter(filterId);
//        this._replaceWith = replaceWith;
//        this._size = size;
//        this._whiteList = whiteList;
//        this._useBlackListLogic = useBlackListLogic;
//    }
//
//    public boolean shouldGenerateIn(final IWorld world) {
//        return this._useBlackListLogic != this._whiteList.shouldGenerateIn(WorldHelper.getDimensionId(world));
//    }
//
//    public int getSize() {
//        return this._size;
//    }
//
//    public BlockState getReplacement() {
//        return this._replaceWith;
//    }
//
//    //region Predicate<BlockState>
//
//    @Override
//    public boolean test(BlockState blockState) {
//        return this._filter.test(blockState);
//    }
//
//    //region IFeatureConfig
//
//    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
//        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
//                ops.createString("white"), this._whiteList.serialize(ops).getValue(),
//                ops.createString("black"), ops.createBoolean(this._useBlackListLogic),
//                ops.createString("size"), ops.createInt(this._size),
//                ops.createString("filter"), ops.createString(this._filterId.toString()),
//                ops.createString("state"), BlockState.serialize(ops, this._replaceWith).getValue())));
//    }
//
//    public static OreWhitelistedConfig deserialize(final Dynamic<?> ops) {
//
//        final IWorldGenWhiteList whiteList = ops.get("white").map(WorldGenWhiteList::deserialize).orElseGet(WorldGenWhiteList::new);
//        final boolean blackList = ops.get("black").asBoolean(false);
//        final int size = ops.get("size").asInt(0);
//        final ResourceLocation id = new ResourceLocation(ops.get("filter").asString(""));
//        final BlockState blockstate = ops.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
//
//        return new OreWhitelistedConfig(id, blockstate, size, whiteList, blackList);
//    }
//
//    //region internals
//
//    private final IWorldGenWhiteList _whiteList;
//    private final boolean _useBlackListLogic;
//    private final Predicate<BlockState> _filter;
//    private final ResourceLocation _filterId;
//    private final int _size;
//    private final BlockState _replaceWith;
//
//    private static final Map<ResourceLocation, Predicate<BlockState>> s_filters;
//
//    static {
//
//        s_filters = Maps.newHashMap();
//        addFilter(FILTER_GENERIC_STONE, BlocksMatcher.forBlock(Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.STONE));
//        addFilter(FILTER_NETHERRACK, BlocksMatcher.forBlock(Blocks.NETHERRACK));
//        addFilter(FILTER_ENDSTONE, BlocksMatcher.forBlock(Blocks.END_STONE));
//    }
//}

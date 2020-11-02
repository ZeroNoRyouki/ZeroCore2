///*
// *
// * WorldGenWhiteList.java
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
//import com.google.common.collect.Sets;
//import com.mojang.datafixers.Dynamic;
//import com.mojang.datafixers.types.DynamicOps;
//import net.minecraft.world.World;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//@SuppressWarnings({"WeakerAccess"})
//public class WorldGenWhiteList implements IWorldGenWhiteList {
//
//    public WorldGenWhiteList() {
//        this(Sets.newCopyOnWriteArraySet());
//    }
//
//    @Override
//    public boolean shouldGenerateIn(final World world) {
//        return this._dimensionsWhiteList.contains(WorldHelper.getDimensionId(world));
//    }
//
//    @Override
//    public boolean shouldGenerateIn(int dimensionId) {
//        return this._dimensionsWhiteList.contains(dimensionId);
//    }
//
//    @Override
//    public void whiteListDimension(final int id) {
//        this._dimensionsWhiteList.add(id);
//    }
//
//    @Override
//    public void whiteListDimensions(final int[] dimensionIds) {
//
//        for (final int id : dimensionIds) {
//            this.whiteListDimension(id);
//        }
//    }
//
//    @Override
//    public void whiteListDimensions(final Collection<Integer> dimensionIds) {
//        this._dimensionsWhiteList.addAll(dimensionIds);
//    }
//
//    @Override
//    public void clearWhiteList() {
//        this._dimensionsWhiteList.clear();
//    }
//
//    //region IFeatureConfig
//
//    @Override
//    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
//        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
//                ops.createString("ids"), ops.createList(this._dimensionsWhiteList.stream().map((ops::createInt))))));
//    }
//
//    public static <T> IWorldGenWhiteList deserialize(final Dynamic<T> ops) {
//
//        final List<Integer> ids = ops.get("ids").asList(dynamic -> dynamic.asInt(Integer.MIN_VALUE));
//
//        return new WorldGenWhiteList(Sets.newCopyOnWriteArraySet(ids));
//    }
//
//    //region internals
//
//    private WorldGenWhiteList(final Set<Integer> whitelist) {
//        this._dimensionsWhiteList = whitelist;
//    }
//
//    private final Set<Integer> _dimensionsWhiteList;
//}

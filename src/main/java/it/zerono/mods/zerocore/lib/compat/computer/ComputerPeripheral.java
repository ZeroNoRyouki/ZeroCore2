/*
 *
 * ComputerPeripheral.java
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

package it.zerono.mods.zerocore.lib.compat.computer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public abstract class ComputerPeripheral<P extends ComputerPeripheral<P>> {

    public ComputerPeripheral(final TileEntity peripheral) {
        this._tile = peripheral;
    }

    public TileEntity getTileEntity() {
        return this._tile;
    }

    public P getPeripheral() {
        //noinspection unchecked
        return (P)this;
    }

    /**
     * Get the name of this ComputerPeripheral
     *
     * @return the name
     */
    public abstract String getPeripheralStaticName();

    /**
     * Collect the methods provided by this ComputerPeripheral
     *
     * @param methodConsumer pass your methods to this Consumer
     */
    public abstract void populateMethods(final NonNullConsumer<ComputerMethod<P>> methodConsumer);

    public Object[] invoke(final String methodName, final Object[] arguments) {
        return this.getCollection().invoke(this.getPeripheral(), methodName, arguments);
    }

    public Object[] invoke(final int methodId, final Object[] arguments) {
        return this.getCollection().invoke(this.getPeripheral(), methodId, arguments);
    }

    @SuppressWarnings("unused")
    protected String[] getMethodsNames() {
        return this.getCollection().getMethodsNames();
    }

    protected Optional<ComputerMethod<P>> getMethod(final String name) {
        return this.getCollection().getMethod(name);
    }

    protected Optional<ComputerMethod<P>> getMethod(final int index) {
        return this.getCollection().getMethod(index);
    }

    //region Object

    @Override
    public boolean equals(Object other) {
        //noinspection rawtypes
        return super.equals(other) && (other instanceof ComputerPeripheral) &&
                this.getTileEntity() == ((ComputerPeripheral)other).getTileEntity();
    }

    //endregion
    //region method wrappers and helpers

    protected static <P extends ComputerPeripheral<P>> IComputerMethodHandler<P> wrapValue(final Function<P, Object> code) {
        return (P peripheral, Object[] arguments) -> luaValueResult(code.apply(peripheral));
    }

    protected static <P extends ComputerPeripheral<P>> IComputerMethodHandler<P> wrapValue(final BiFunction<P, Object[], Object> code) {
        return (P peripheral, Object[] arguments) -> luaValueResult(code.apply(peripheral, arguments));
    }

    protected static <P extends ComputerPeripheral<P>> IComputerMethodHandler<P> wrapArray(final Function<P, Object[]> code) {
        return (P peripheral, Object[] arguments) -> luaArrayResult(code.apply(peripheral));
    }

    protected static <P extends ComputerPeripheral<P>> IComputerMethodHandler<P> wrapArray(final BiFunction<P, Object[], Object[]> code) {
        return (P peripheral, Object[] arguments) -> luaArrayResult(code.apply(peripheral, arguments));
    }

    protected static Object[] luaValueResult(@Nullable Object o) {
        return null != o ? new Object[]{o} : ComputerMethod.EMPTY_RESULT;
    }

    protected static Object[] luaArrayResult(@Nullable Object[] o) {
        return null != o ? o : ComputerMethod.EMPTY_RESULT;
    }

    //endregion
    //region internals

    @SuppressWarnings("unused")
    // Required Args: string (method name)
    private static <P extends ComputerPeripheral<P>> Object[] helpImp(final P peripheral, final Object[] arguments) {
        return new Object[] { "The help() method is not implemented yet" };
    }

    private static final class MethodCollection<P extends ComputerPeripheral<P>> {

        public static <P extends ComputerPeripheral<P>> MethodCollection<P> from(final NonNullConsumer<NonNullConsumer<ComputerMethod</*? extends */P>>> methodsGetter) {

            final List<ComputerMethod<P>> methods = Lists.newArrayList();

            // put in standard methods
            methods.add(new ComputerMethod<>("help", ComputerPeripheral::helpImp)); //TODO imp help
            methods.add(new ComputerMethod<>("isMethodAvailable", wrapValue((P p, Object[] arguments) ->
                    p.getMethod(LuaHelper.getStringFromArgs(arguments, 0)).isPresent())));

            // ask the peripheral to add it's own methods
            methodsGetter.accept(methods::add);

            methods.sort(Comparator.comparing(ComputerMethod::getName));

            final Map<String, ComputerMethod<P>> nameMap = Maps.newHashMap();
            final Map<Integer, ComputerMethod<P>> indexMap = Maps.newHashMap();
            final String[] names = new String[methods.size()];
            int index = 0;

            for (final ComputerMethod<P> method : methods) {

                if (null == method) {
                    continue;
                }

                names[index] = method.getName();
                nameMap.put(names[index], method);
                indexMap.put(index, method);
                ++index;
            }

            return new MethodCollection<>(names, nameMap, indexMap);
        }

        public String[] getMethodsNames() {
            return Arrays.copyOf(this._names, this._names.length);
        }

        public Optional<ComputerMethod<P>> getMethod(final String name) {
            return Optional.ofNullable(this._namesMap.get(name));
        }

        public Optional<ComputerMethod<P>> getMethod(final int index) {
            return Optional.ofNullable(this._indexMap.get(index));
        }

        public Object[] invoke(final P peripheral, final String methodName, final Object[] arguments) {
            return this._namesMap.getOrDefault(methodName, ComputerMethod.getEmptyMethod()).invoke(peripheral, arguments);
        }

        public Object[] invoke(final P peripheral, final int methodId, final Object[] arguments) {
            return this._indexMap.getOrDefault(methodId, ComputerMethod.getEmptyMethod()).invoke(peripheral, arguments);
        }

        //region internals

        private MethodCollection(final String[] names,
                                 final Map<String, ComputerMethod<P>> namesMap,
                                 final Map<Integer, ComputerMethod<P>> indexMap) {

            this._names = names;
            this._namesMap = Object2ObjectMaps.unmodifiable(new Object2ObjectArrayMap<>(namesMap));
            this._indexMap = Int2ObjectMaps.unmodifiable(new Int2ObjectArrayMap<>(indexMap));
        }

        private final String[] _names;
        private final Object2ObjectMap<String, ComputerMethod<P>> _namesMap;
        private final Int2ObjectMap<ComputerMethod<P>> _indexMap;

        //endregion

    }

    private MethodCollection<P> getCollection() {
        //noinspection unchecked
        return s_methods.computeIfAbsent(this.getPeripheralStaticName(), name -> MethodCollection.from(this::populateMethods));
    }

    @SuppressWarnings("rawtypes")
    private static final Map<String, MethodCollection> s_methods = new Object2ObjectArrayMap<>();

    private final TileEntity _tile;

    //endregion
}

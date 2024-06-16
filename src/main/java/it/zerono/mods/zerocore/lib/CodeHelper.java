/*
 *
 * CodeHelper.java
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

package it.zerono.mods.zerocore.lib;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.functional.NonNullBiFunction;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.common.util.NonNullFunction;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class CodeHelper {

    public static final String PATH_SEPARATOR = "/";

    public static final Object[] EMPTY_GENERIC_ARRAY = new Object[0];

    public static final Component TEXT_EMPTY_LINE = Component.empty();

    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Direction[] POSITIVE_DIRECTIONS;
    public static final Direction[] NEGATIVE_DIRECTIONS;

    public static final AABB EMPTY_BOUNDING_BOX = new AABB(0, 0, 0, 0, 0, 0);
    public static final BlockPos MIN_BLOCKPOS = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    public static final BlockPos MAX_BLOCKPOS = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public static final AABB EMPTY_AABB = new AABB(0, 0, 0, 0, 0, 0);

    public static final BooleanSupplier TRUE_SUPPLIER = () -> true;
    public static final BooleanSupplier FALSE_SUPPLIER = () -> false;
    public static final IntConsumer VOID_INT_CONSUMER = v -> {};
    public static final BooleanConsumer VOID_BOOL_CONSUMER = v -> {};
    public static final Runnable VOID_RUNNABLE = () -> {};

    /*
     * Mouse button constants
     */
    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    public static final int MOUSE_BUTTON_WHEEL_DOWN = -1;
    public static final int MOUSE_BUTTON_WHEEL_UP = -2;

    public static boolean isModLoaded(final String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static ModContainer getActiveMod() {
        return ModLoadingContext.get().getActiveContainer();
    }

    /**
     * Retrieve the ID of the mod from FML active mod container
     * Only call this method while processing a FMLEvent (or derived classes)
     */
    public static String getModIdFromActiveModContainer() {

        final String modId = getActiveMod().getModId();

        if ((null == modId) || modId.isEmpty()) {
            throw new RuntimeException("Cannot retrieve the MOD ID from FML");
        }

        return modId;
    }

    public static boolean isDevEnv() {
        return !FMLEnvironment.production;
    }

    //region misc

    public static ResourceLocation getObjectId(final Block object) {
        return Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(object));
    }

    public static ResourceLocation getObjectId(final Item object) {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(object));
    }

    public static ResourceLocation getObjectId(final Fluid object) {
        return Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(object));
    }

    /**
     * Return a reference time for the system
     */
    public static long getSystemTime() {
        return Util.getNanos();
    }

    public static RandomSource fakeRandom() {

        s_fakeRandom.setSeed(42);
        return s_fakeRandom;
    }

    public static String neutralLowercase(final String input) {
        return input.toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        return ZeroCore.getProxy().getRecipeManager();
    }

    public static RecipeManager getRecipeManager(final Level world) {
        return world.getRecipeManager();
    }

    public static boolean shouldInvalidateResourceCache() {
        return Lib.shouldInvalidateResourceCache();
    }

    //endregion
    //region Direction helpers

    public static Stream<Direction> directionStream() {
        return Arrays.stream(DIRECTIONS);
    }

    public static Direction.Plane perpendicularPlane(final Direction direction) {
        return perpendicularPlane(direction.getAxis().getPlane());
    }

    public static Direction.Plane perpendicularPlane(final Direction.Axis axis) {
        return perpendicularPlane(axis.getPlane());
    }

    public static Direction.Plane perpendicularPlane(final Direction.Plane plane) {
        return Direction.Plane.HORIZONTAL == plane ? Direction.Plane.VERTICAL : Direction.Plane.HORIZONTAL;
    }

    public static List<Direction> perpendicularDirections(final Direction direction) {
        return s_perpendicularDirections.get(direction);
    }

    /**
     * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
     * returns this facing without rotating.
     */
    public static Direction directionRotateAround(final Direction direction, Direction.Axis axis) {

        switch (axis) {

            case X:

                if (direction != Direction.WEST && direction != Direction.EAST) {
                    return directionRotateX(direction);
                }

                return direction;

            case Y:

                if (direction != Direction.UP && direction != Direction.DOWN) {
                    return directionRotateY(direction);
                }

                return direction;

            case Z:

                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return directionRotateZ(direction);
                }

                return direction;

            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    /**
     * Rotate the given Direction around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public static Direction directionRotateY(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.EAST;

            case EAST:
                return Direction.SOUTH;

            case SOUTH:
                return Direction.WEST;

            case WEST:
                return Direction.NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + direction);
        }
    }

    /**
     * Rotate the given Direction around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public static Direction directionRotateX(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.DOWN;

            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + direction);

            case SOUTH:
                return Direction.UP;

            case UP:
                return Direction.NORTH;

            case DOWN:
                return Direction.SOUTH;
        }
    }

    /**
     * Rotate the given Direction around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    public static Direction directionRotateZ(final Direction direction) {

        switch (direction) {

            case EAST:
                return Direction.DOWN;

            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + direction);

            case WEST:
                return Direction.UP;

            case UP:
                return Direction.EAST;

            case DOWN:
                return Direction.WEST;
        }
    }

    /**
     * Rotate the given Direction around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public static Direction directionRotateYCCW(final Direction direction) {

        switch (direction) {

            case NORTH:
                return Direction.WEST;

            case EAST:
                return Direction.NORTH;

            case SOUTH:
                return Direction.EAST;

            case WEST:
                return Direction.SOUTH;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + direction);
        }
    }

    //endregion
    //region Logical sides and deferred execution helpers

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByLogicalServer(final Level world) {
        return !world.isClientSide;
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByLogicalClient(final Level world) {
        return world.isClientSide;
    }

    public static void callOnLogicalSide(final Level world, final Runnable serverCode, final Runnable clientCode) {

        if (calledByLogicalServer(world)) {
            serverCode.run();
        } else {
            clientCode.run();
        }
    }

    public static <T> T callOnLogicalSide(final Level world, final Supplier<T> serverCode, final Supplier<T> clientCode) {

        if (calledByLogicalServer(world)) {
            return serverCode.get();
        } else {
            return clientCode.get();
        }
    }

    public static boolean callOnLogicalSide(final Level world, final BooleanSupplier serverCode, final BooleanSupplier clientCode) {

        if (calledByLogicalServer(world)) {
            return serverCode.getAsBoolean();
        } else {
            return clientCode.getAsBoolean();
        }
    }

    public static int callOnLogicalSide(final Level world, final IntSupplier serverCode, final IntSupplier clientCode) {

        if (calledByLogicalServer(world)) {
            return serverCode.getAsInt();
        } else {
            return clientCode.getAsInt();
        }
    }

    public static long callOnLogicalSide(final Level world, final LongSupplier serverCode, final LongSupplier clientCode) {

        if (calledByLogicalServer(world)) {
            return serverCode.getAsLong();
        } else {
            return clientCode.getAsLong();
        }
    }

    public static double callOnLogicalSide(final Level world, final DoubleSupplier serverCode, final DoubleSupplier clientCode) {

        if (calledByLogicalServer(world)) {
            return serverCode.getAsDouble();
        } else {
            return clientCode.getAsDouble();
        }
    }

    public static void callOnLogicalServer(final Level world, final Runnable code) {

        if (calledByLogicalServer(world)) {
            code.run();
        }
    }

    public static <T> T callOnLogicalServer(final Level world, final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return calledByLogicalServer(world) ? code.get() : invalidSideReturnValue.get();
    }

    public static boolean callOnLogicalServer(final Level world, final BooleanSupplier code) {
        return calledByLogicalServer(world) && code.getAsBoolean();
    }

    public static int callOnLogicalServer(final Level world, final IntSupplier code, final int invalidSideReturnValue) {
        return calledByLogicalServer(world) ? code.getAsInt() : invalidSideReturnValue;
    }

    public static long callOnLogicalServer(final Level world, final LongSupplier code, final long invalidSideReturnValue) {
        return calledByLogicalServer(world) ? code.getAsLong() : invalidSideReturnValue;
    }

    public static double callOnLogicalServer(final Level world, final DoubleSupplier code, final double invalidSideReturnValue) {
        return calledByLogicalServer(world) ? code.getAsDouble() : invalidSideReturnValue;
    }

    public static void callOnLogicalClient(final Level world, final Runnable code) {

        if (calledByLogicalClient(world)) {
            code.run();
        }
    }

    public static <T> T callOnLogicalClient(final Level world, final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return calledByLogicalClient(world) ? code.get() : invalidSideReturnValue.get();
    }

    public static boolean callOnLogicalClient(final Level world, final BooleanSupplier code) {
        return calledByLogicalClient(world) && code.getAsBoolean();
    }

    public static int callOnLogicalClient(final Level world, final IntSupplier code, final int invalidSideReturnValue) {
        return calledByLogicalClient(world) ? code.getAsInt() : invalidSideReturnValue;
    }

    public static long callOnLogicalClient(final Level world, final LongSupplier code, final long invalidSideReturnValue) {
        return calledByLogicalClient(world) ? code.getAsLong() : invalidSideReturnValue;
    }

    public static double callOnLogicalClient(final Level world, final DoubleSupplier code, final double invalidSideReturnValue) {
        return calledByLogicalClient(world) ? code.getAsDouble() : invalidSideReturnValue;
    }

    public static LogicalSide getWorldLogicalSide(final Level world) {
        return CodeHelper.calledByLogicalClient(world) ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    public static String getWorldSideName(Level world) {
        return CodeHelper.calledByLogicalClient(world) ? "CLIENT" : "SERVER";
    }

    public static BlockableEventLoop<?> getThreadTaskExecutor(final LogicalSide side) {
        return LogicalSidedProvider.WORKQUEUE.get(side);
    }

    public static BlockableEventLoop<?> getClientThreadTaskExecutor() {
        return getThreadTaskExecutor(LogicalSide.CLIENT);
    }

    public static BlockableEventLoop<?> getServerThreadTaskExecutor() {
        return getThreadTaskExecutor(LogicalSide.SERVER);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static CompletableFuture<Void> enqueueTask(final LogicalSide side, final Runnable runnable) {

        final BlockableEventLoop<?> executor = getThreadTaskExecutor(side);

        // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
        // Same logic as ThreadTaskExecutor#runImmediately without the join

        if (!executor.isSameThread()) {

            return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice

        } else {

            runnable.run();
            return CompletableFuture.completedFuture(null);
        }
    }

    public static Optional<MinecraftServer> getMinecraftServer() {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
    }

    public static Runnable delayedRunnable(final Runnable code, final int tickDelay) {
        return new Runnable() {

            @Override
            public void run() {

                ++this._ticks;

                if (this._ticks >= this._delay) {

                    this._ticks = 0;
                    this._code.run();
                }
            }

            private final Runnable _code = code;
            private final int _delay = tickDelay;
            private int _ticks = 0;
        };
    }

    public static BooleanSupplier tickCountdown(final int tickCountdown) {
        return new BooleanSupplier() {

            @Override
            public boolean getAsBoolean() {

                --this._countdown;

                if (this._countdown <= 0) {

                    this._countdown = tickCountdown;
                    return true;
                }

                return false;
            }

            private int _countdown = tickCountdown;
        };
    }

    //endregion
    //region error reporting helpers

    public static void reportErrorToPlayer(final Player player, final ValidationError error) {
        reportErrorToPlayer(player, error.getPosition(), error.getChatMessage());
    }

    public static void reportErrorToPlayer(final Player player, final @Nullable BlockPos position,
                                           final Component... errors) {
        ZeroCore.getProxy().reportErrorToPlayer(player, position, errors);
    }

    public static void reportErrorToPlayer(final Player player, final @Nullable BlockPos position,
                                           final List<Component> errors) {
        ZeroCore.getProxy().reportErrorToPlayer(player, position, errors);
    }

    public static void clearErrorReport() {
        ZeroCore.getProxy().clearErrorReport();
    }

    //endregion
    //region Filesystem helper functions

    public static boolean ioDirectoryExist(final Path path) {

        try {
            return Files.exists(path, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
        } catch (SecurityException ex) {
            return false;
        }
    }

    public static boolean ioCreateDirectory(final Path parent, final String subDirectoryName) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(subDirectoryName));

        final Path subDirectory = Paths.get(parent.toAbsolutePath().toString(), subDirectoryName);

        if (ioDirectoryExist(subDirectory)) {
            return true;
        }

        try {

            Files.createDirectory(subDirectory);
            return true;

        } catch (IOException ex) {

            Log.LOGGER.error("Failed to create directory {} : {}", subDirectory, ex);
            return false;
        }
    }

    public static boolean ioCreateModConfigDirectory(final String name) {
        return ioCreateDirectory(FMLPaths.CONFIGDIR.get(), name);
    }

    //endregion
    //region Non-null wrappers

    public static <T> NonNullSupplier<T> asNonNull(final Supplier<T> supplier, final NonNullSupplier<T> fallbackValue) {
        return () -> {

            final T current = supplier.get();

            return null != current ? current : fallbackValue.get();
        };
    }

    public static <T, R> NonNullFunction<T, R> asNonNull(final Function<T, R> function, final NonNullFunction<T, R> fallbackValue) {
        return (T v) -> {

            final R current = function.apply(v);

            return null != current ? current : fallbackValue.apply(v);
        };
    }

    //endregion
    //region Lazy helpers (from V3)

    public static <T> NonNullSupplier<T> lazy(NonNullSupplier<T> supplier) {
        return new NonNullSupplier<T>() {

            @Nonnull
            @Override
            public T get() {

                if (null == this._resolvedValue) {
                    this._resolvedValue = Preconditions.checkNotNull(supplier.get());
                }

                return this._resolvedValue;
            }

            @Nullable
            private T _resolvedValue;
        };
    }

    public static <T, R> NonNullFunction<T, R> lazy(NonNullFunction<T, R> function) {
        return new NonNullFunction<T, R>() {

            @Override
            public @Nonnull R apply(@Nonnull T arg1) {

                if (null == this._resolvedValue) {
                    this._resolvedValue = Preconditions.checkNotNull(function.apply(arg1));
                }

                return this._resolvedValue;
            }

            @Nullable
            private R _resolvedValue;
        };
    }

    public static <T1, T2, R> NonNullBiFunction<T1, T2, R> lazy(NonNullBiFunction<T1, T2, R> function) {
        return new NonNullBiFunction<T1, T2, R>() {

            @Override
            public @Nonnull R apply(@Nonnull T1 arg1, @Nonnull T2 arg2) {

                if (null == this._resolvedValue) {
                    this._resolvedValue = Preconditions.checkNotNull(function.apply(arg1, arg2));
                }

                return this._resolvedValue;
            }

            @Nullable
            private R _resolvedValue;
        };
    }

    @SafeVarargs
    public static <T> T[] resolveSuppliers(IntFunction<T[]> arrayFactory, java.util.function.Supplier<T>... suppliers) {

        Preconditions.checkNotNull(arrayFactory);
        Preconditions.checkNotNull(suppliers);

        final T[] array = Preconditions.checkNotNull(arrayFactory.apply(suppliers.length));

        Arrays.setAll(array, i -> suppliers[i].get());
        return array;
    }

    //endregion
    //region Optional helper functions to bridge the gap to Java 9+ and other utilities

    /**
     * If the provided {@link Optional} contains a value, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param opt the {@link Optional} used by this operation
     * @param action      the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is present
     * @param <T> the type of {@link Optional} value
     * @throws NullPointerException if the provided {@link Optional} contains a value and the given action
     * is {@code null}, or no value is present and the given empty-based action is {@code null}.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> void optionalIfPresentOrElse(Optional<T> opt, Consumer<? super T> action, Runnable emptyAction) {

        if (opt.isPresent()) {
            action.accept(opt.get());
        } else {
            emptyAction.run();
        }
    }

    /**
     * If the provided {@link Optional} contains a value, performs the given action with the value,
     * otherwise throws NoSuchElementException
     *
     * @param opt the {@link Optional} used by this operation
     * @param action      the action to be performed, if a value is present
     * @param <T> the type of {@link Optional} value
     * @throws NoSuchElementException if the provided {@link Optional} do not contains a value
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> void optionalIfPresentOrThrow(Optional<T> opt, Consumer<? super T> action) {

        if (opt.isPresent()) {
            action.accept(opt.get());
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * If the provided {@link Optional} contains a value, returns an {@link Optional} describing the value,
     * otherwise returns an {@link Optional} produced by the supplying function.
     *
     * @param opt the {@link Optional} used by this operation
     * @param supplier the supplying function that produces an {@link Optional} to be returned
     * @param <T> the type of {@link Optional} value
     * @return returns an {@link Optional} describing the value of the provieded {@link Optional}, if a value is present,
     * otherwise an {@link Optional} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or produces a {@code null} result
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Optional<T> optionalOr(Optional<T> opt, NonNullSupplier<? extends Optional<? extends T>> supplier) {

        Objects.requireNonNull(supplier);

        if (opt.isPresent()) {

            return opt;

        } else {

            @SuppressWarnings("unchecked")
            Optional<T> r = (Optional<T>) supplier.get();

            return Objects.requireNonNull(r);
        }
    }

    /**
     * If the first provided {@link Optional} contains a value, return it. Otherwise, return the value of the the second {@link Optional}
     *
     * @param opt1 the first {@link Optional}
     * @param opt2 the second {@link Optional}
     * @param <T> the type of {@link Optional} value
     * @return the value contained in the first {@link Optional} or the value contained in the second {@link Optional}
     * @throws NoSuchElementException if both {@link Optional}s are empty
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalGetWithoutIsPresent"})
    public static <T> T optionalGetOr(Optional<T> opt1, Optional<T> opt2) {
        return opt1.orElse(opt2.get());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U> void optionalIfPresent(Optional<T> opt1, Optional<U> opt2, BiConsumer<? super T, ? super U> consumer) {

        Objects.requireNonNull(consumer);

        if (opt1.isPresent() && opt2.isPresent()) {
            consumer.accept(opt1.get(), opt2.get());
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, R> Optional<R> optionalMap(Optional<T> opt1, Optional<U> opt2, BiFunction<? super T, ? super U, ? extends R> mapper) {

        Objects.requireNonNull(mapper);

        if (opt1.isPresent() && opt2.isPresent()) {
            return Optional.ofNullable(mapper.apply(opt1.get(), opt2.get()));
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, R> Optional<R> optionalFlatMap(Optional<T> opt1, Optional<U> opt2, BiFunction<? super T, ? super U, Optional<R>> mapper) {

        Objects.requireNonNull(mapper);

        if (opt1.isPresent() && opt2.isPresent()) {
            return Objects.requireNonNull(mapper.apply(opt1.get(), opt2.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * If the provided {@link Optional} contains a value, returns a sequential {@link Stream} containing only
     * that value, otherwise returns an empty {@link Stream}.
     *
     * This method can be used to transform a {@link Stream} of optional elements to a {@link Stream} of present
     * value elements:
     *
     * <pre>{@code
     *     Stream<Optional<T>> os = ..
     *     Stream<T> s = os.flatMap(Optional::stream)
     * }</pre>
     *
     * @param opt the {@link Optional} used by this operation
     * @param <T> the type of {@link Optional} value
     * @return the optional value as a {@link Stream}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Stream<T> optionalStream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * Return true if the provided {@link Optional} is empty
     *
     * @param opt the {@link Optional} used by this operation
     * @param <T> the type of {@link Optional} value
     * @return true if the provided {@link Optional} is empty, false otherwise
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> boolean optionalIsEmpty(Optional<T> opt) {
        return !opt.isPresent();
    }

    /**
     * This method hides an unchecked cast to the inferred type. Only use this if
     * you are sure the type should match
     *
     * @param opt the {@link Optional} used by this operation
     * @return the provided {@link Optional}, cast to the inferred generic type
     */
    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    public static <X, Y> Optional<Y> optionalCast(Optional<X> opt) {
        return (Optional<Y>)opt;
    }

    //endregion
    //region misc

    /**
     * i18n support and helper functions
     */

    @OnlyIn(Dist.CLIENT)
    public static Component i18nFormatComponent(final String translateKey, Object... parameters) {
        return Component.literal(I18n.get(translateKey, parameters));
    }

    /**
     * MC-Version independent wrapper around PlayerEntity::addChatMessage()
     */
    public static void sendChatMessage(final Player sender, final Component component) {
        sender.sendSystemMessage(component);
    }

    /**
     * MC-Version independent wrapper around PlayerEntity::sendStatusMessage() [backported to MC 1.10.2]
     */
    public static void sendStatusMessage(final Player player, final Component message) {
        ZeroCore.getProxy().sendPlayerStatusMessage(player, message);
    }

    //endregion
    //region Math helper functions

    public static int positiveModulo(int numerator, int denominator) {
        return (numerator % denominator + denominator) % denominator;
    }

    /**
     * Math helper function - Linear interpolate between two numbers
     */
    public static float mathLerp(float from, float to, float modifier) {

        modifier = Math.min(1.0f, Math.max(0.0f, modifier));
        return from + modifier * (to - from);
    }

    /**
     * Math helper function - Calculate the volume of the cube defined by two coordinates
     * @param minimum Minimum coordinate
     * @param maximum Maximum coordinate
     * @return the cube's volume, in blocks
     */
    public static int mathVolume(final BlockPos minimum, final BlockPos maximum) {
        return CodeHelper.mathVolume(minimum.getX(), minimum.getY(), minimum.getZ(), maximum.getX(), maximum.getY(), maximum.getZ());
    }

    /**
     * Math helper function - Calculate the volume of the cube defined by two coordinates.
     * @param x1 minimum X coordinate
     * @param y1 minimum Y coordinate
     * @param z1 minimum Z coordinate
     * @param x2 maximum X coordinate
     * @param y2 maximum Y coordinate
     * @param z2 maximum Z coordinate
     * @return the cube's volume, in blocks
     */
    public static int mathVolume(int x1, int y1, int z1, int x2, int y2, int z2) {

        final int cx = Math.abs(x2 - x1) + 1;
        final int cy = Math.abs(y2 - y1) + 1;
        final int cz = Math.abs(z2 - z1) + 1;

        return cx * cy * cz;
    }

    /**
     * Math helper function - Returns the int nearest in value to value.
     * @return  the same value cast to int if it is in the range of the int type, Integer.MAX_VALUE if it is too large,
     *          or Integer.MIN_VALUE if it is too small
     */
    public static int mathTruncateToInt(final long value) {
        return Ints.saturatedCast(value);
    }

    public static int mathClampUnsignedToInt(final long value) {
        return (value < 0 || value > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)value;
    }

    public static long mathClampUnsignedToLong(final long value) {
        return value < 0 ? Long.MAX_VALUE : value;
    }

    public static float mathUnsignedLongToFloat(final long value) {

        float fValue = (float) (value & UNSIGNED_MASK);

        if (value < 0) {
            fValue += 0x1.0p63F;
        }

        return fValue;
    }

    public static double mathUnsignedLongToDouble(final long value) {

        double dValue = (double) (value & UNSIGNED_MASK);

        if (value < 0) {
            dValue += 0x1.0p63;
        }
        return dValue;
    }

    public static boolean mathUnsignedLongMultiplicationWillOverFlow(final long a, final long b) {
        return (a != 0 && b != 0 && Long.compareUnsigned(b, Long.divideUnsigned(-1, a)) > 0);
    }

    public static long mathClamp(long value, long min, long max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }

    public static int commonVertices(final Vec3i a, final Vec3i b) {
        return (a.getX() == b.getX() ? 1 : 0) + (a.getY() == b.getY() ? 1 : 0) + (a.getZ() == b.getZ() ? 1 : 0);
    }

    public static Matrix4f translationMatrix(float x, float y, float z) {
        return new Matrix4f()
                .m03(x)
                .m13(y)
                .m23(z);
    }

    //endregion
    //region format string

    public static String formatAsHumanReadableNumber(WideAmount value, final String unit) {
        return formatAsHumanReadableNumber(value.doubleValue(), unit);
    }

    public static String formatAsHumanReadableNumber(double value, final String unit) {

        int order = 0;

        if (0.0 != value) {

            while (value > 1000.0) {

                value /= 1000.0;
                order += 3;
            }

            while (value < 1.0) {

                value *= 1000.0;
                order -= 3;
            }
        }

        int decimals = 2;
        final String format = String.format("%%.%1$df %%2$s%%3$s", decimals);

        return String.format(format, value, getSiPrefix(order), unit);
    }

    public static String formatAsHumanReadableNumber(long value, final String unit) {

        int order = 0;

        if (0 != value) {

            while (value > 1000) {

                value /= 1000;
                order += 3;
            }

            while (value < 1) {

                value *= 1000;
                order -= 3;
            }
        }

        return value + " " + getSiPrefix(order) + unit;
    }

    public static String getSiPrefix(int order) {

        order = Math.min(MAX_SI_PREFIX_ORDER, Math.max(MIN_SI_PREFIX_ORDER, order));
        return s_siPrefixes.get(order);
    }

    public static String formatAsMillibuckets(float value) {

        if (value <= 0.00001f) {
            return "0.000 mB";
        }

        final int power = (int) Math.floor(Math.log10(value));
        String format;

        if (power < 1) {
            format = "%.3f mB";
        } else if (power < 2) {
            format = "%.2f mB";
        } else if (power < 3) {
            format = "%.1f mB";
        } else if (power < 4) {
            format = "%.0f mB";
        } else {

            value /= 1000f; // convert into buckets

            if (power < 5) {
                format = "%.2f B";
            } else if (power < 6) {
                format = "%.1f B";
            } else {
                format = "%.0f B";
            }
        }

        return String.format(format, value);
    }

    public static String zeroFilled(final int count) {

        final char[] zeros = new char[count];

        Arrays.fill(zeros, '0');
        return new String(zeros);
    }

    public static String toString(final Vec3i value) {
        return String.format("(%d, %d, %d)", value.getX(), value.getY(), value.getZ());
    }

    //endregion
    //region internals

    private static final long UNSIGNED_MASK = 0x7FFFFFFFFFFFFFFFL;
    private static final int MAX_SI_PREFIX_ORDER = 30;
    private static final int MIN_SI_PREFIX_ORDER = -30;

    private static final Int2ObjectMap<String> s_siPrefixes;
    private static final RandomSource s_fakeRandom;
    private static final Map<Direction, List<Direction>> s_perpendicularDirections;

    static {

        s_fakeRandom = RandomSource.create();

        final Int2ObjectMap<String> prefixes = new Int2ObjectArrayMap<>(21);

        prefixes.put(30, "Q"); // quetta, 10^30
        prefixes.put(27, "R"); // ronna, 10^27
        prefixes.put(24, "Y"); // yotta, 10^24
        prefixes.put(21, "Z"); // zetta, 10^21
        prefixes.put(18, "E"); // exa, 10^18
        prefixes.put(15, "P"); // peta, 10^15
        prefixes.put(12, "T"); // tera, 10^12
        prefixes.put(9, "G"); // giga, 10^9
        prefixes.put(6, "M"); // mega, 10^6
        prefixes.put(3, "k"); // kilo, 10^3
        prefixes.put(0, ""); // one
        prefixes.put(-3, "m"); // milli, 10^-3
        prefixes.put(-6, "μ"); // micro, 10^-6
        prefixes.put(-9, "n"); // nano, 10^-9
        prefixes.put(-12, "p"); // pico, 10^-12
        prefixes.put(-15, "f"); // femto, 10^-15
        prefixes.put(-18, "a"); // atto, 10^-18
        prefixes.put(-21, "z"); // zepto, 10^-21
        prefixes.put(-24, "y"); // yocto, 10^-24
        prefixes.put(-27, "r"); // ronto, 10^-27
        prefixes.put(-30, "q"); // quecto, 10^-24

        s_siPrefixes = Int2ObjectMaps.unmodifiable(prefixes);

        // perpendicular directions

        final List<Direction> xPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.UP });
        final List<Direction> yPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST });
        final List<Direction> zPerpendicularsDirections = new ObjectArrayList<>(new Direction[] { Direction.EAST, Direction.DOWN, Direction.WEST, Direction.UP });

        s_perpendicularDirections = new Object2ObjectArrayMap<>(DIRECTIONS.length);
        s_perpendicularDirections.put(Direction.UP, yPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.DOWN, yPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.NORTH, zPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.SOUTH, zPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.EAST, xPerpendicularsDirections);
        s_perpendicularDirections.put(Direction.WEST, xPerpendicularsDirections);

        // positive & negative directions

        POSITIVE_DIRECTIONS = Arrays.stream(DIRECTIONS).filter(d -> d.getAxisDirection() == Direction.AxisDirection.POSITIVE).toArray(Direction[]::new);
        NEGATIVE_DIRECTIONS = Arrays.stream(DIRECTIONS).filter(d -> d.getAxisDirection() == Direction.AxisDirection.NEGATIVE).toArray(Direction[]::new);
    }

    //endregion
}

package it.zerono.mods.zerocore.lib.functional;

import net.minecraftforge.common.util.NonNullFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface NonNullBiFunction<T1, T2, R> {

    @NotNull
    R apply(@NotNull T1 t1, @NotNull T2 t2);

    @NotNull
    default <V> NonNullBiFunction<T1, T2, V> andThen(@NotNull NonNullFunction<? super R, ? extends V> after) {

        Objects.requireNonNull(after);
        return (T1 t1, T2 t2) -> after.apply(apply(t1, t2));
    }
}

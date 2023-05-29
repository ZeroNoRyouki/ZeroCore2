package it.zerono.mods.zerocore.lib.functional;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface NonNullBiConsumer<T1, T2> {

    void accept(@NotNull T1 t1, @NotNull T2 t2);

    @NotNull
    default NonNullBiConsumer<T1, T2> andThen(@NotNull NonNullBiConsumer<? super T1, ? super T2> after) {

        Objects.requireNonNull(after);

        return (t1, t2) -> {

            this.accept(t1, t2);
            after.accept(t1, t2);
        };
    }
}

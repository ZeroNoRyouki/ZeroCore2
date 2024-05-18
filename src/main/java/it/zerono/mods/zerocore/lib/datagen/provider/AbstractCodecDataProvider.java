package it.zerono.mods.zerocore.lib.datagen.provider;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class AbstractCodecDataProvider<T>
        extends AbstractDataProvider {

    protected AbstractCodecDataProvider(String name, PackOutput output, PackOutput.Target target, String kind,
                                        CompletableFuture<HolderLookup.Provider> registryLookup,
                                        ResourceLocationBuilder modLocationRoot, Codec<T> codec) {

        super(name, output, registryLookup, modLocationRoot);

        Preconditions.checkNotNull(target, "Target must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(kind), "Kind must not be null or empty");
        Preconditions.checkNotNull(codec, "Codec must not be null");

        this._pathProvider = output.createPathProvider(target, kind);
        this._codec = codec;
    }

    protected abstract void processData(BiConsumer<@NotNull ResourceLocation, @NotNull T> consumer);

    //region AbstractDataProvider

    @Override
    public CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup) {

        final List<CompletableFuture<?>> futures = new ObjectArrayList<>(64);

        this.processData((id, value) -> {

            final var path = this._pathProvider.json(id);
            final var json = this._codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();

            futures.add(DataProvider.saveStable(cache, json, path));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    //endregion
    //region internals

    private final PackOutput.PathProvider _pathProvider;
    private final Codec<T> _codec;

    //endregion
}

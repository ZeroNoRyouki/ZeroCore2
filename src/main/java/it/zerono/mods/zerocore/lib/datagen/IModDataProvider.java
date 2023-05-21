package it.zerono.mods.zerocore.lib.datagen;

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.ProviderSettings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface IModDataProvider {

    @FunctionalInterface
    interface ModFactory<P extends IModDataProvider> {

        P create(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                 ResourceLocationBuilder modLocationRoot);
    }

    void provideData();

    CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup);

    ProviderSettings getSettings();

    default PackOutput output() {
        return this.getSettings().output();
    }

    default ResourceLocationBuilder root() {
        return this.getSettings().root();
    }

    default CompletableFuture<HolderLookup.Provider> lookup() {
        return this.getSettings().registryLookup();
    }

    default <V> CompletableFuture<V> lookup(Function<HolderLookup.Provider, CompletableFuture<V>> future) {
        return this.lookup().thenCompose(future);
    }

    default PackOutput.PathProvider createPathProvider(PackOutput.Target target, String kind) {
        return this.output().createPathProvider(target, kind);
    }
}

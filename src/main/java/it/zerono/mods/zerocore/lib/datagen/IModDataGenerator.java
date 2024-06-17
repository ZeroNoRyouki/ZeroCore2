package it.zerono.mods.zerocore.lib.datagen;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.loot.SubProviderBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ITagDataProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IModDataGenerator {

    CompletableFuture<HolderLookup.Provider> getRegistryLookup();

    ResourceLocationBuilder getModRoot();

    <P extends DataProvider> void addProvider(DataProvider.Factory<P> factory);

    default <P extends IModDataProvider> void addProvider(IModDataProvider.ModFactory<P> factory) {
        this.addProvider(output -> new DataProvider() {

            @Override
            public CompletableFuture<?> run(CachedOutput cache) {

                this._provider.provideData();
                return this._provider.lookup(lookup -> this._provider.processData(cache, lookup));
            }

            @Override
            public String getName() {
                return this._provider.getSettings().name();
            }

            //region internals

            private final IModDataProvider _provider = factory.create(output, IModDataGenerator.this.getRegistryLookup(),
                    IModDataGenerator.this.getModRoot());

            //endregion
        });
    }

    <T> void addTagsProvider(ResourceKey<? extends Registry<T>> registryKey, ITagDataProvider<T> provider);

    <T> void addTagsProvider(ResourceKey<? extends Registry<T>> registryKey,
                             Function<@NotNull T, @NotNull ResourceKey<T>> elementKeyProvider, IIntrinsicTagDataProvider<T> provider);

    default void addBlockTagsProvider(IIntrinsicTagDataProvider<Block> provider) {
        //noinspection deprecation
        this.addTagsProvider(Registries.BLOCK, $ -> $.builtInRegistryHolder().key(), provider);
    }

    default void addItemTagsProvider(IIntrinsicTagDataProvider<Item> provider) {
        //noinspection deprecation
        this.addTagsProvider(Registries.ITEM, $ -> $.builtInRegistryHolder().key(), provider);
    }

    default void addEntityTypeTagsProvider(IIntrinsicTagDataProvider<EntityType<?>> provider) {
        //noinspection deprecation
        this.addTagsProvider(Registries.ENTITY_TYPE, $ -> $.builtInRegistryHolder().key(), provider);
    }

    default void addFluidTagsProvider(IIntrinsicTagDataProvider<Fluid> provider) {
        //noinspection deprecation
        this.addTagsProvider(Registries.FLUID, $ -> $.builtInRegistryHolder().key(), provider);
    }

    default void addLootProvider(Set<ResourceKey<LootTable>> requiredTables,
                                 Consumer<@NotNull SubProviderBuilder> subProvidersBuilder) {

        Preconditions.checkNotNull(requiredTables, "Required tables must not be null");
        Preconditions.checkNotNull(subProvidersBuilder, "Sub providers builder must not be null");

        final var builder = Util.make(new SubProviderBuilder(), subProvidersBuilder);

        this.addProvider(output -> new LootTableProvider(output, requiredTables, builder.getEntries(),
                this.getRegistryLookup()));
    }

    default void addLootProvider(Consumer<@NotNull SubProviderBuilder> subProvidersBuilder) {
        this.addLootProvider(Set.of(), subProvidersBuilder);
    }
}

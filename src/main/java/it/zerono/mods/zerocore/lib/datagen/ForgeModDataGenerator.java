package it.zerono.mods.zerocore.lib.datagen;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ITagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModIntrinsicTagAppender;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModTagAppender;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForgeModDataGenerator
        implements IModDataGenerator {

    public ForgeModDataGenerator(GatherDataEvent gatherDataEvent, ResourceLocationBuilder modRootLocation,
                                 Consumer<@NotNull IModDataGenerator> providersConfigurator) {

        Preconditions.checkNotNull(gatherDataEvent);
        Preconditions.checkNotNull(modRootLocation);
        Preconditions.checkNotNull(providersConfigurator);

        this._modRootLocation = modRootLocation;
        this._packGenerator = gatherDataEvent.getGenerator().getVanillaPack(true); // we always run
        this._lookup = gatherDataEvent.getLookupProvider();
        this._existingFileHelper = gatherDataEvent.getExistingFileHelper();

        providersConfigurator.accept(this);
    }

    //region IModDataGenerator


    @Override
    public CompletableFuture<HolderLookup.Provider> getRegistryLookup() {
        return this._lookup;
    }

    @Override
    public ResourceLocationBuilder getModRoot() {
        return this._modRootLocation;
    }

    @Override
    public <P extends DataProvider> void addProvider(DataProvider.Factory<P> factory) {
        this._packGenerator.addProvider(factory);
    }

    @Override
    public <T> void addTagsProvider(ResourceKey<? extends Registry<T>> registryKey, ITagDataProvider<T> provider) {

        Preconditions.checkNotNull(registryKey, "Registry key must not be null");
        Preconditions.checkNotNull(provider, "Provider must not be null");

        final var namespace = this._modRootLocation.namespace();
        final var lookup = this._lookup;
        final var helper = this._existingFileHelper;

        this.addProvider(output -> new TagsProvider<T>(output, registryKey, lookup, namespace, helper) {

            @Override
            protected void addTags(HolderLookup.Provider lookup) {
                provider.build(lookup, tagKey -> new ModTagAppender<>(this.getOrCreateRawBuilder(tagKey)));
            }

            @Override
            public String getName() {
                return provider.getName();
            }
        });
    }

    @Override
    public <T> void addTagsProvider(ResourceKey<? extends Registry<T>> registryKey,
                                    Function<@NotNull T, @NotNull ResourceKey<T>> elementKeyProvider,
                                    IIntrinsicTagDataProvider<T> provider) {

        Preconditions.checkNotNull(registryKey, "Registry key must not be null");
        Preconditions.checkNotNull(elementKeyProvider, "Element key provider must not be null");
        Preconditions.checkNotNull(provider, "Provider must not be null");

        final var namespace = this._modRootLocation.namespace();
        final var lookup = this._lookup;
        final var helper = this._existingFileHelper;

        this.addProvider(output -> new TagsProvider<T>(output, registryKey, lookup, namespace, helper) {

            @Override
            protected void addTags(HolderLookup.Provider lookup) {
                provider.build(lookup, tagKey -> new ModIntrinsicTagAppender<>(this.getOrCreateRawBuilder(tagKey), elementKeyProvider));
            }

            @Override
            public String getName() {
                return provider.getName();
            }
        });
    }

    //endregion
    //region internals

    private final ResourceLocationBuilder _modRootLocation;
    private final DataGenerator.PackGenerator _packGenerator;
    private final CompletableFuture<HolderLookup.Provider> _lookup;
    private final ExistingFileHelper _existingFileHelper;

    //endregion
}

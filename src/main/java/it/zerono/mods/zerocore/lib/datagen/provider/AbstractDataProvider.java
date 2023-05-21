package it.zerono.mods.zerocore.lib.datagen.provider;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.IModDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractDataProvider
        implements IModDataProvider {

    protected AbstractDataProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                   ResourceLocationBuilder modLocationRoot) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");
        Preconditions.checkNotNull(output, "Output must not be null");
        Preconditions.checkNotNull(registryLookup, "Registry lookup must not be null");
        Preconditions.checkNotNull(modLocationRoot, "Mod location root must not be null");

        this._settings = new ProviderSettings(name, output, registryLookup, modLocationRoot);
    }

    //region IModDataProvider

    @Override
    public ProviderSettings getSettings() {
        return this._settings;
    }

    //endregion
    //region internals

    private final ProviderSettings _settings;

    //endregion
}

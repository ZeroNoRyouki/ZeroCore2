package it.zerono.mods.zerocore.lib.datagen.provider;

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public record ProviderSettings(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                               ResourceLocationBuilder root) {
}

package it.zerono.mods.zerocore.lib.datagen.provider.loot;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

public class SubProviderBuilder {

    public SubProviderBuilder() {

        this._entries = new ObjectArrayList<>(16);
        this._immutableEntries = ObjectLists.unmodifiable(this._entries);
    }

    @ApiStatus.Internal
    public List<LootTableProvider.SubProviderEntry> getEntries() {
        return this._immutableEntries;
    }

    public SubProviderBuilder addSubProvider(Supplier<LootTableSubProvider> provider, LootContextParamSet paramSet) {

        Preconditions.checkNotNull(provider, "Provider must not be null");
        Preconditions.checkNotNull(paramSet, "Param set must not be null");

        this._entries.add(new LootTableProvider.SubProviderEntry(provider, paramSet));
        return this;
    }

    public SubProviderBuilder addSubProvider(Supplier<LootTableSubProvider> provider) {
        return this.addSubProvider(provider, LootContextParamSets.ALL_PARAMS);
    }

    public <P extends BlockLootSubProvider> SubProviderBuilder addBlockProvider(Supplier<P> blockProvider) {

        Preconditions.checkNotNull(blockProvider, "Block provider must not be null");

        return this.addSubProvider(blockProvider::get, LootContextParamSets.BLOCK);
    }

    public <P extends EntityLootSubProvider> SubProviderBuilder addEntityProvider(Supplier<P> entityProvider) {

        Preconditions.checkNotNull(entityProvider, "Entity provider must not be null");

        return this.addSubProvider(entityProvider::get, LootContextParamSets.ENTITY);
    }

    //region internals

    private final ObjectList<LootTableProvider.SubProviderEntry> _entries;
    private final List<LootTableProvider.SubProviderEntry> _immutableEntries;

    //endregion
}

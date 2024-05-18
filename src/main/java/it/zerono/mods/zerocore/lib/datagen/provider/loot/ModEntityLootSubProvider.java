package it.zerono.mods.zerocore.lib.datagen.provider.loot;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class ModEntityLootSubProvider
        extends EntityLootSubProvider {

    protected ModEntityLootSubProvider(FeatureFlagSet enabledFeatures) {

        super(enabledFeatures);
        this._registeredTypes = new ObjectOpenHashSet<>(128);
    }

    @Override
    protected void add(EntityType<?> entityType, ResourceKey<LootTable> defaultTable, LootTable.Builder builder) {

        Preconditions.checkState(this._registeredTypes.add(entityType),
                "Entity type %s was already added to this sub provider", entityType);

        super.add(entityType, defaultTable, builder);
    }

    protected void add(Supplier<EntityType<?>> entityType, LootTable.Builder builder) {
        this.add(entityType.get(), builder);
    }

    protected void add(Supplier<EntityType<?>> entityType, ResourceKey<LootTable> defaultTable, LootTable.Builder builder) {
        this.add(entityType.get(), defaultTable, builder);
    }

    //region EntityLootSubProvider

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return this._registeredTypes.stream();
    }

    //endregion
    //region internals

    private final Set<EntityType<?>> _registeredTypes;

    //endregion
}

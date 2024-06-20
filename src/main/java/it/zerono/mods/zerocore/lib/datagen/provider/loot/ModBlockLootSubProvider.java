package it.zerono.mods.zerocore.lib.datagen.provider.loot;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ModBlockLootSubProvider
        extends BlockLootSubProvider {

    protected ModBlockLootSubProvider(Set<Item> explosionResistantItems, FeatureFlagSet enabledFeatures,
                                      HolderLookup.Provider provider) {

        super(explosionResistantItems, enabledFeatures, provider);
        this._registeredBlocks = new ObjectOpenHashSet<>(128);
    }

    @Override
    protected void add(Block block, LootTable.Builder builder) {

        Preconditions.checkState(this._registeredBlocks.add(block),
                "Block %s was already added to this sub provider", block);

        super.add(block, builder);
    }

    protected void add(Supplier<? extends Block> block, LootTable.Builder builder) {
        this.add(block.get(), builder);
    }

    protected void add(Supplier<? extends Block> block, Function<Block, LootTable.Builder> mapper) {
        this.add(block.get(), mapper);
    }

    protected void otherWhenSilkTouch(Supplier<? extends Block> block, Supplier<? extends Block> drop) {
        this.otherWhenSilkTouch(block.get(), drop.get());
    }

    protected void dropWhenSilkTouch(Supplier<? extends Block> block) {
        this.dropWhenSilkTouch(block.get());
    }

    @SafeVarargs
    protected final void dropWhenSilkTouch(Supplier<? extends Block> block, Supplier<? extends Block>... others) {

        this.dropWhenSilkTouch(block);

        for (final var other : others) {
            this.dropWhenSilkTouch(other);
        }
    }

    protected void dropOther(Supplier<? extends Block> block, Supplier<? extends ItemLike> drop) {
        this.dropOther(block.get(), drop.get());
    }

    protected void dropSelf(Supplier<? extends Block> block) {
        this.dropOther(block, block);
    }

    @SafeVarargs
    protected final void dropSelf(Supplier<? extends Block> block, Supplier<? extends Block>... others) {

        this.dropSelf(block);

        for (final var other : others) {
            this.dropSelf(other);
        }
    }

    protected void dropOre(Supplier<? extends Block> ore, Supplier<? extends Item> drop) {
        this.add(ore.get(), (block) -> this.createOreDrop(block, drop.get()));
    }

    protected void dropWithComponents(Block block, DataComponentType<?> component,
                                      DataComponentType<?>... otherComponents) {

        final CopyComponentsFunction.Builder componentsBuilder =
                CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY);

        componentsBuilder.include(component);

        for (final var other : otherComponents) {
            componentsBuilder.include(other);
        }

        final LootTable.Builder tableBuilder = LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block).apply(componentsBuilder)));

        this.add(block, tableBuilder);
    }

    protected void dropWithComponents(Supplier<? extends Block> block, DataComponentType<?> component,
                                      DataComponentType<?>... otherComponents) {
        this.dropWithComponents(block.get(), component, otherComponents);
    }

    //region BlockLootSubProvider

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return this._registeredBlocks;
    }

    //endregion
    //region internals

    private final Set<Block> _registeredBlocks;

    //endregion
}

/*
 *
 * BaseBlockLootTableProvider.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.lib.datagen.provider;

import it.zerono.mods.zerocore.lib.datagen.LootTableType;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.loot.*;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public class BaseBlockLootTableProvider
        extends BaseLootTableProvider {

    protected BaseBlockLootTableProvider(final DataGenerator dataGenerator) {
        super(LootTableType.Block, dataGenerator);
    }

    protected void addDrop(final Supplier<? extends Block> block) {
        this.addDrop(block, block, 1);
    }

    @SafeVarargs
    protected final void addDrop(final Supplier<? extends Block>... blocks) {

        for (final Supplier<? extends Block> block : blocks) {
            this.addDrop(block, block, 1);
        }
    }

    protected void addDrop(final Supplier<? extends Block> block, final Supplier<? extends ItemLike> drop,
                           final int count) {
        this.addDrop(block.get(), LootItem.lootTableItem(drop.get())
                .when(ExplosionCondition.survivesExplosion())
                .apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(count))));
    }

    protected void addSilkDrop(final Supplier<? extends Block> block, final Supplier<? extends ItemLike> standardDrop,
                               final int count, final Supplier<? extends ItemLike> silkDrop) {
        this.addDrop(block.get(), AlternativesEntry.alternatives(
                // with silk touch ...
                LootItem.lootTableItem(silkDrop.get())
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))),
                // without ...
                LootItem.lootTableItem(standardDrop.get())
                        .when(ExplosionCondition.survivesExplosion())
                        .apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(count)))));
    }

    protected void addDrop(final Supplier<? extends Block> block, final Supplier<? extends ItemLike> drop,
                           final int min, final int max) {
        this.addDrop(block.get(), LootItem.lootTableItem(drop.get())
                .when(ExplosionCondition.survivesExplosion())
                .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                .apply(SetItemCountFunction.setCount(RandomValueBounds.between(min, max))));
    }

    protected void addSilkDrop(final Supplier<? extends Block> block, final Supplier<? extends ItemLike> standardDrop,
                               final int min, final int max, final Supplier<? extends ItemLike> silkDrop) {

        this.addDrop(block.get(), AlternativesEntry.alternatives(
                // with silk touch ...
                LootItem.lootTableItem(silkDrop.get())
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))),
                // without ...
                LootItem.lootTableItem(standardDrop.get())
                        .when(ExplosionCondition.survivesExplosion())
                        .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                        .apply(SetItemCountFunction.setCount(new RandomValueBounds(min, max)))));
    }

    //region internals

    private void addDrop(final Block block, final LootPoolEntryContainer.Builder<?> entry) {

        final ResourceLocation id = Objects.requireNonNull(block.getRegistryName());

        this.add(id, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name(id.getPath())
                        .setRolls(ConstantIntValue.exactly(1))
                        .add(entry)
                ));
    }

    //endregion
}

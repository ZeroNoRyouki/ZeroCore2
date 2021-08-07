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
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

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

    protected void addDrop(final Supplier<? extends Block> block, final Supplier<? extends IItemProvider> drop,
                           final int count) {
        this.addDrop(block.get(), ItemLootEntry.lootTableItem(drop.get())
                .when(SurvivesExplosion.survivesExplosion())
                .apply(SetCount.setCount(ConstantRange.exactly(count))));
    }

    protected void addSilkDrop(final Supplier<? extends Block> block, final Supplier<? extends IItemProvider> standardDrop,
                               final int count, final Supplier<? extends IItemProvider> silkDrop) {
        this.addDrop(block.get(), AlternativesLootEntry.alternatives(
                // with silk touch ...
                ItemLootEntry.lootTableItem(silkDrop.get())
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))),
                // without ...
                ItemLootEntry.lootTableItem(standardDrop.get())
                        .when(SurvivesExplosion.survivesExplosion())
                        .apply(SetCount.setCount(ConstantRange.exactly(count)))));
    }

    protected void addDrop(final Supplier<? extends Block> block, final Supplier<? extends IItemProvider> drop,
                           final int min, final int max) {
        this.addDrop(block.get(), ItemLootEntry.lootTableItem(drop.get())
                .when(SurvivesExplosion.survivesExplosion())
                .apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                .apply(SetCount.setCount(RandomValueRange.between(min, max))));
    }

    protected void addSilkDrop(final Supplier<? extends Block> block, final Supplier<? extends IItemProvider> standardDrop,
                               final int min, final int max, final Supplier<? extends IItemProvider> silkDrop) {

        this.addDrop(block.get(), AlternativesLootEntry.alternatives(
                // with silk touch ...
                ItemLootEntry.lootTableItem(silkDrop.get())
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))),
                // without ...
                ItemLootEntry.lootTableItem(standardDrop.get())
                        .when(SurvivesExplosion.survivesExplosion())
                        .apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                        .apply(SetCount.setCount(new RandomValueRange(min, max)))));
    }

    //region internals

    private void addDrop(final Block block, final LootEntry.Builder<?> entry) {

        final ResourceLocation id = Objects.requireNonNull(block.getRegistryName());

        this.add(id, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name(id.getPath())
                        .setRolls(ConstantRange.exactly(1))
                        .add(entry)
                ));
    }

    //endregion
}

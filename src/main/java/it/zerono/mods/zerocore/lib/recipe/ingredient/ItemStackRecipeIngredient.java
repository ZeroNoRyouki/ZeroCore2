/*
 *
 * ItemStackIngredient.java
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

package it.zerono.mods.zerocore.lib.recipe.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.NBTIngredient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemStackRecipeIngredient
    implements IRecipeIngredient<ItemStack> {

    public static final Codec<ItemStackRecipeIngredient> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf(Lib.NAME_INGREDIENT).forGetter(i -> i._ingredient),
                    Codec.INT.fieldOf(Lib.NAME_COUNT).forGetter(i -> i._amount)
            ).apply(instance, ItemStackRecipeIngredient::new)
    );

    public static ItemStackRecipeIngredient from(final Ingredient ingredient) {
        return from(ingredient, 1);
    }

    public static ItemStackRecipeIngredient from(final Ingredient ingredient, final int amount) {
        return new ItemStackRecipeIngredient(ingredient, amount);
    }

    public static ItemStackRecipeIngredient from(final FriendlyByteBuf buffer) {
        return new ItemStackRecipeIngredient(Ingredient.fromNetwork(buffer), buffer.readVarInt());
    }

    public static ItemStackRecipeIngredient from(final ItemStack stack) {
        return from(stack, stack.getCount());
    }

    public static ItemStackRecipeIngredient from(final ItemStack stack, final int amount) {
        return from(stack.hasTag() ? NBTIngredient.of(true, stack) : Ingredient.of(stack), amount);
    }

    public static ItemStackRecipeIngredient from(final ItemLike item) {
        return from(item, 1);
    }

    public static ItemStackRecipeIngredient from(final ItemLike item, final int amount) {
        return from(new ItemStack(item), amount);
    }

    public static ItemStackRecipeIngredient from(Supplier<? extends ItemLike> item) {
        return from(item.get().asItem(), 1);
    }

    public static ItemStackRecipeIngredient from(Supplier<? extends ItemLike> item, int amount) {
        return from(item.get().asItem(), amount);
    }

    public static ItemStackRecipeIngredient from(final TagKey<Item> tag) {
        return from(tag, 1);
    }

    public static ItemStackRecipeIngredient from(final TagKey<Item> tag, final int amount) {
        return from(Ingredient.of(tag), amount);
    }

    @Override
    public boolean isCompatible(final ItemStack stack) {
        return this._ingredient.test(stack);
    }

    @Override
    public boolean isCompatible(final ItemStack... ingredients) {

        for (final ItemStack stack : ingredients) {
            if (this._ingredient.test(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack getMatchFrom(final ItemStack stack) {
        return this.test(stack) ? ItemHelper.stackFrom(stack, this._amount) : ItemStack.EMPTY;
    }

    @Override
    public long getAmount(final ItemStack stack) {
        return this.isCompatible(stack) ? this._amount : 0;
    }

    @Override
    public List<ItemStack> getMatchingElements() {

        if (null == this._cachedMatchingElements) {

            this._cachedMatchingElements = Arrays.stream(this._ingredient.getItems())
                    .filter(stack -> !stack.isEmpty())
                    .map(stack -> stack.getCount() == this._amount ? stack : ItemHelper.stackFrom(stack, this._amount))
                    .collect(ImmutableList.toImmutableList());
        }

        return this._cachedMatchingElements;
    }

    @Override
    public boolean isEmpty() {
        return this._ingredient.isEmpty();
    }

    @Override
    public List<Ingredient> asVanillaIngredients() {
        return ObjectLists.singleton(this._ingredient);
    }

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {

        this._ingredient.toNetwork(buffer);
        buffer.writeVarInt(this._amount);
    }

    @Override
    public boolean test(final ItemStack stack) {
        return this.isCompatible(stack) && this._amount <= stack.getCount();
    }

    @Override
    public boolean testIgnoreAmount(final ItemStack stack) {
        return this.isCompatible(stack);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this._amount + " " + Arrays.stream(this._ingredient.getItems())
                .map(stack -> stack.getItem().toString())
                .collect(Collectors.joining(","));
    }

    //endregion
    //region internals

    protected ItemStackRecipeIngredient(final Ingredient ingredient, final int amount) {

        this._ingredient = ingredient;
        this._amount = amount;
    }

    private final Ingredient _ingredient;
    private final int _amount;
    private List<ItemStack> _cachedMatchingElements;

    //endregion
}

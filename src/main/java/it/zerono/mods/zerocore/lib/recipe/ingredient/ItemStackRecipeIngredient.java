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
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemStackRecipeIngredient
    implements IRecipeIngredient<ItemStack> {

    public static ItemStackRecipeIngredient from(final Ingredient ingredient) {
        return from(ingredient, 1);
    }

    public static ItemStackRecipeIngredient from(final Ingredient ingredient, final int amount) {
        return new Impl(ingredient, amount);
    }

    public static ItemStackRecipeIngredient from(final ItemStackRecipeIngredient... ingredients) {
        return new CompositeImpl(ingredients);
    }

    public static ItemStackRecipeIngredient from(final FriendlyByteBuf buffer) {

        final int ingredientsCount = buffer.readVarInt();

        if (1 == ingredientsCount) {

            return new Impl(Ingredient.fromNetwork(buffer), buffer.readVarInt());

        } else if (ingredientsCount > 1) {

            final ItemStackRecipeIngredient[] ingredients = new ItemStackRecipeIngredient[ingredientsCount];

            for (int idx = 0; idx < ingredients.length; ++idx) {
                ingredients[idx] = new Impl(Ingredient.fromNetwork(buffer), buffer.readVarInt());
            }

            return new CompositeImpl(ingredients);
        }

        throw new IllegalArgumentException("Invalid item ingredient data from then network");
    }

    public static ItemStackRecipeIngredient from(final JsonElement jsonElement) {

        if (jsonElement.isJsonArray()) {

            final JsonArray json = jsonElement.getAsJsonArray();
            final int size = json.size();

            switch (size) {

                case 0:
                    throw new JsonSyntaxException("No ingredients found, at least one is required");

                case 1:
                    return from(json.get(0));

                default:
                    //noinspection UnstableApiUsage
                    return from(Streams.stream(json)
                            .map(ItemStackRecipeIngredient::from)
                            .toArray(ItemStackRecipeIngredient[]::new));
            }
        } else if (jsonElement.isJsonObject()) {

            final JsonObject json = jsonElement.getAsJsonObject();
            final Ingredient ingredient = JSONHelper.jsonGetIngredient(json, Lib.NAME_INGREDIENT);
            final int amount = JSONHelper.jsonGetInt(json, Lib.NAME_COUNT, 1);

            if (amount < 1) {
                throw new JsonSyntaxException("Ingredient amount must be larger than or equal to one");
            }

            return new Impl(ingredient, amount);
        }

        throw new JsonSyntaxException("Expected item ingredient entry to be object or array of objects");
    }

    public static ItemStackRecipeIngredient from(final ItemStack stack) {
        return from(stack, stack.getCount());
    }

    public static ItemStackRecipeIngredient from(final ItemStack stack, final int amount) {
        return from(stack.hasTag() ? new StrictNBTIngredient(stack) {} : Ingredient.of(stack), amount);
    }

    public static ItemStackRecipeIngredient from(final ItemLike item) {
        return from(item, 1);
    }

    public static ItemStackRecipeIngredient from(final ItemLike item, final int amount) {
        return from(new ItemStack(item), amount);
    }

    public static ItemStackRecipeIngredient from(final TagKey<Item> tag) {
        return from(tag, 1);
    }

    public static ItemStackRecipeIngredient from(final TagKey<Item> tag, final int amount) {
        return from(Ingredient.of(tag), amount);
    }

    //region implementations

    static class Impl
            extends ItemStackRecipeIngredient {

        //region ItemStackRecipeIngredient

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

                //noinspection UnstableApiUsage
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

            buffer.writeVarInt(1);
            this._ingredient.toNetwork(buffer);
            buffer.writeVarInt(this._amount);
        }

        @Override
        public JsonElement serializeTo() {

            final JsonObject json = new JsonObject();

            JSONHelper.jsonSetIngredient(json, Lib.NAME_INGREDIENT, this._ingredient);
            JSONHelper.jsonSetInt(json, Lib.NAME_COUNT, this._amount);
            return json;
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

        protected Impl(final Ingredient ingredient, final int amount) {

            this._ingredient = ingredient;
            this._amount = amount;
        }

        private final Ingredient _ingredient;
        private final int _amount;
        private List<ItemStack> _cachedMatchingElements;

        //endregion
    }

    static class CompositeImpl
            extends ItemStackRecipeIngredient {

        //region ItemStackRecipeIngredient

        @Override
        public boolean isCompatible(final ItemStack stack) {
            return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(stack));
        }

        @Override
        public boolean isCompatible(final ItemStack... ingredients) {
            return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(ingredients));
        }

        @Override
        public ItemStack getMatchFrom(final ItemStack stack) {
            return this._ingredients.stream()
                    .map(ingredient -> ingredient.getMatchFrom(stack))
                    .filter(match -> !match.isEmpty())
                    .findAny()
                    .orElse(ItemStack.EMPTY);
        }

        @Override
        public long getAmount(final ItemStack stack) {
            return this._ingredients.stream()
                    .mapToLong(ingredient -> ingredient.getAmount(stack))
                    .filter(amount -> amount > 0)
                    .findAny()
                    .orElse(0);
        }

        @Override
        public List<ItemStack> getMatchingElements() {

            if (null == this._cachedMatchingElements) {

                //noinspection UnstableApiUsage
                this._cachedMatchingElements = this._ingredients.stream()
                        .flatMap(ingredient -> ingredient.getMatchingElements().stream())
                        .collect(ImmutableList.toImmutableList());
            }

            return this._cachedMatchingElements;
        }

        @Override
        public boolean isEmpty() {
            return this._ingredients.stream().anyMatch(IRecipeIngredient::isEmpty);
        }

        @Override
        public List<Ingredient> asVanillaIngredients() {
            return this.getMatchingElements().stream()
                    .map(Ingredient::of)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean test(final ItemStack stack) {
            return this._ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
        }

        @Override
        public boolean testIgnoreAmount(final ItemStack stack) {
            return this._ingredients.stream().anyMatch(ingredient -> ingredient.testIgnoreAmount(stack));
        }

        @Override
        public void serializeTo(final FriendlyByteBuf buffer) {

            buffer.writeVarInt(this._ingredients.size());
            this._ingredients.forEach(ingredient -> ingredient.serializeTo(buffer));
        }

        @Override
        public JsonElement serializeTo() {

            final JsonArray json = new JsonArray();

            this._ingredients.forEach(ingredient -> json.add(ingredient.serializeTo()));
            return json;
        }

        //endregion
        //region Object

        @Override
        public String toString() {
            return this._ingredients.stream()
                    .map(Object::toString)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
        }

        //endregion
        //region internals

        protected CompositeImpl(final ItemStackRecipeIngredient[] ingredients) {
            this._ingredients = Lists.newArrayList(ingredients);
        }

        private final List<ItemStackRecipeIngredient> _ingredients;
        private List<ItemStack> _cachedMatchingElements;

        //endregion
    }

    //endregion
}

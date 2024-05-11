package it.zerono.mods.zerocore.lib.recipe.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.stream.Collectors;

public class ItemStackRecipeIngredientsList
        implements IRecipeIngredient<ItemStack> {

    public static final Codec<ItemStackRecipeIngredientsList> CODEC =
            ItemStackRecipeIngredient.CODEC.listOf().xmap(ItemStackRecipeIngredientsList::from, l -> l._ingredients);

    public static ItemStackRecipeIngredientsList from(ItemStackRecipeIngredient... ingredients) {
        return new ItemStackRecipeIngredientsList(List.of(ingredients));
    }

    public static ItemStackRecipeIngredientsList from(List<ItemStackRecipeIngredient> ingredients) {
        return new ItemStackRecipeIngredientsList(ingredients);
    }

    public static ItemStackRecipeIngredientsList from(FriendlyByteBuf buffer) {

        final int count = buffer.readVarInt();
        final List<ItemStackRecipeIngredient> ingredients = new ObjectArrayList<>(count);

        for (int idx = 0; idx < count; ++idx) {
            ingredients.add(ItemStackRecipeIngredient.from(buffer));
        }

        return new ItemStackRecipeIngredientsList(ingredients);
    }

    @Override
    public boolean isCompatible(ItemStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(stack));
    }

    @Override
    public boolean isCompatible(ItemStack... ingredients) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(ingredients));
    }

    @Override
    public ItemStack getMatchFrom(ItemStack stack) {
        return this._ingredients.stream()
                .map(ingredient -> ingredient.getMatchFrom(stack))
                .filter(match -> !match.isEmpty())
                .findAny()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public long getAmount(ItemStack stack) {
        return this._ingredients.stream()
                .mapToLong(ingredient -> ingredient.getAmount(stack))
                .filter(amount -> amount > 0)
                .findAny()
                .orElse(0);
    }

    @Override
    public List<ItemStack> getMatchingElements() {

        if (null == this._cachedMatchingElements) {
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
    public boolean test(ItemStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    @Override
    public boolean testIgnoreAmount(ItemStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.testIgnoreAmount(stack));
    }

    @Override
    public void serializeTo(FriendlyByteBuf buffer) {

        buffer.writeVarInt(this._ingredients.size());
        this._ingredients.forEach(ingredient -> ingredient.serializeTo(buffer));
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

    protected ItemStackRecipeIngredientsList(List<ItemStackRecipeIngredient> ingredients) {
        this._ingredients = ObjectLists.unmodifiable(new ObjectArrayList<>(ingredients));
    }

    private final List<ItemStackRecipeIngredient> _ingredients;
    private List<ItemStack> _cachedMatchingElements;

    //endregion
}

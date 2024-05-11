package it.zerono.mods.zerocore.lib.recipe.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidStackRecipeIngredientsList
        implements IRecipeIngredient<FluidStack> {

    public static final Codec<FluidStackRecipeIngredientsList> CODEC =
            FluidStackRecipeIngredient.CODEC.listOf().xmap(FluidStackRecipeIngredientsList::from, l -> l._ingredients);

    public static FluidStackRecipeIngredientsList from(FluidStackRecipeIngredient... ingredients) {
        return new FluidStackRecipeIngredientsList(List.of(ingredients));
    }

    public static FluidStackRecipeIngredientsList from(List<FluidStackRecipeIngredient> ingredients) {
        return new FluidStackRecipeIngredientsList(ingredients);
    }

    public static FluidStackRecipeIngredientsList from(final FriendlyByteBuf buffer) {

        final int count = buffer.readVarInt();
        final List<FluidStackRecipeIngredient> ingredients = new ObjectArrayList<>(count);

        for (int idx = 0; idx < count; ++idx) {
            ingredients.add(FluidStackRecipeIngredient.from(buffer));
        }

        return new FluidStackRecipeIngredientsList(ingredients);
    }

    @Override
    public boolean isCompatible(final FluidStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(stack));
    }

    @Override
    public boolean isCompatible(FluidStack... ingredients) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.isCompatible(ingredients));
    }

    @Override
    public FluidStack getMatchFrom(final FluidStack stack) {
        return this._ingredients.stream()
                .map(ingredient -> ingredient.getMatchFrom(stack))
                .filter(match -> !match.isEmpty())
                .findAny()
                .orElse(FluidStack.EMPTY);
    }

    @Override
    public long getAmount(final FluidStack stack) {
        return this._ingredients.stream()
                .mapToLong(ingredient -> ingredient.getAmount(stack))
                .filter(amount -> amount > 0)
                .findAny()
                .orElse(0);
    }

    @Override
    public List<FluidStack> getMatchingElements() {

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
    public boolean test(final FluidStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    @Override
    public boolean testIgnoreAmount(final FluidStack stack) {
        return this._ingredients.stream().anyMatch(ingredient -> ingredient.testIgnoreAmount(stack));
    }

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {

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

    protected FluidStackRecipeIngredientsList(List<FluidStackRecipeIngredient> ingredients) {
        this._ingredients = ObjectLists.unmodifiable(new ObjectArrayList<>(ingredients));
    }

    private final List<FluidStackRecipeIngredient> _ingredients;
    private List<FluidStack> _cachedMatchingElements;

    //endregion
}

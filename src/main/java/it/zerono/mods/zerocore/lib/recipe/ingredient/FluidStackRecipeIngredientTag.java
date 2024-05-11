package it.zerono.mods.zerocore.lib.recipe.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

public class FluidStackRecipeIngredientTag
        implements IRecipeIngredient<FluidStack> {

    public static final Codec<FluidStackRecipeIngredientTag> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TagKey.codec(Registries.FLUID).fieldOf(Lib.NAME_TAG).forGetter(i -> i._tag),
                    Codec.INT.fieldOf(Lib.NAME_COUNT).forGetter(i -> i._amount)
            ).apply(instance, FluidStackRecipeIngredientTag::new)
    );

    public static FluidStackRecipeIngredientTag from(TagKey<Fluid> tag, int amount) {
        return new FluidStackRecipeIngredientTag(tag, amount);
    }

    public static FluidStackRecipeIngredientTag from(FriendlyByteBuf buffer) {
        return new FluidStackRecipeIngredientTag(TagsHelper.FLUIDS.createKey(buffer.readResourceLocation()), buffer.readVarInt());
    }

    @Override
    public boolean isCompatible(FluidStack stack) {
        return Objects.requireNonNull(stack).getFluid().is(this._tag);
    }

    @Override
    public boolean isCompatible(FluidStack... ingredients) {

        for (final FluidStack stack : ingredients) {
            if (stack.getFluid().is(this._tag)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public FluidStack getMatchFrom(FluidStack stack) {
        return this.test(stack) ? new FluidStack(stack, this._amount) : FluidStack.EMPTY;
    }

    @Override
    public long getAmount(FluidStack stack) {
        return this.isCompatible(stack) ? this._amount : 0;
    }

    @Override
    public List<FluidStack> getMatchingElements() {

        if (null == this._cachedMatchingElements) {
            this._cachedMatchingElements = TagsHelper.FLUIDS.getObjects(this._tag).stream()
                    .map(fluid -> new FluidStack(fluid, this._amount))
                    .collect(ImmutableList.toImmutableList());
        }

        return this._cachedMatchingElements;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean test(FluidStack stack) {
        return this.isCompatible(stack) && stack.getAmount() >= this._amount;
    }

    @Override
    public boolean testIgnoreAmount(FluidStack stack) {
        return this.isCompatible(stack);
    }

    @Override
    public void serializeTo(FriendlyByteBuf buffer) {

        buffer.writeResourceLocation(this._tag.location());
        buffer.writeVarInt(this._amount);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this._amount + ' ' + this._tag.toString();
    }

    //endregion
    //region internals

    protected FluidStackRecipeIngredientTag(TagKey<Fluid> tag, int amount) {

        this._tag = tag;
        this._amount = amount;
    }

    private final TagKey<Fluid> _tag;
    private final int _amount;
    private List<FluidStack> _cachedMatchingElements;

    //endregion
}

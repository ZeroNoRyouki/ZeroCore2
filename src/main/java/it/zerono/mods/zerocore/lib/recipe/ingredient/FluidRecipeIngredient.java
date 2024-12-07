/*
 *
 * FluidRecipeIngredient.java
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

import com.google.common.base.Preconditions;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class FluidRecipeIngredient
        implements IRecipeIngredient<@NotNull FluidStack> {

    public static final ModCodecs<FluidRecipeIngredient, RegistryFriendlyByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            FluidIngredient.CODEC.fieldOf(Lib.NAME_INGREDIENT).forGetter($ -> $._ingredient),
                            ExtraCodecs.POSITIVE_INT.fieldOf(Lib.NAME_AMOUNT).forGetter($ -> $._amount),
                            DataComponentPredicate.CODEC.optionalFieldOf(Lib.NAME_COMPONENTS).forGetter($ -> $._componentPredicate)
                    ).apply(instance, FluidRecipeIngredient::new)
            ),
            StreamCodec.composite(
                    FluidIngredient.STREAM_CODEC, $ -> $._ingredient,
                    ByteBufCodecs.VAR_INT, $ -> $._amount,
                    ByteBufCodecs.optional(DataComponentPredicate.STREAM_CODEC), $ -> $._componentPredicate,
                    FluidRecipeIngredient::new
            )
    );

    public static FluidRecipeIngredient copyOf(FluidStack stack) {

        Preconditions.checkNotNull(stack, "Stack must not be null");

        return of(stack.getAmount(), DataComponentPredicate.allOf(stack.getComponents()), stack.getFluid());
    }

    public static FluidRecipeIngredient of(Fluid fluid) {
        return of(1000, fluid);
    }

    public static FluidRecipeIngredient of(Supplier<? extends @NotNull Fluid> fluid) {
        return of(1000, fluid.get());
    }

    public static FluidRecipeIngredient of(int amount, Fluid fluid) {

        Preconditions.checkNotNull(fluid, "Fluid must not be null");

        return new FluidRecipeIngredient(amount, Optional.empty(), fluid);
    }

    public static FluidRecipeIngredient of(int amount, Supplier<? extends @NotNull Fluid> fluid) {
        return of(amount, fluid.get());
    }

    public static FluidRecipeIngredient of(int amount, DataComponentPredicate componentPredicate, Fluid fluid) {

        Preconditions.checkNotNull(fluid, "Fluid must not be null");

        return new FluidRecipeIngredient(amount, Optional.of(componentPredicate), fluid);
    }

    public static FluidRecipeIngredient of(int amount, DataComponentPredicate componentPredicate,
                                           Supplier<? extends @NotNull Fluid> fluid) {
        return of(amount, componentPredicate, fluid.get());
    }

    public static FluidRecipeIngredient of(Fluid firstFluid, Fluid... otherFluids) {
        return of(1000, firstFluid, otherFluids);
    }

    @SafeVarargs
    public static FluidRecipeIngredient of(Supplier<? extends @NotNull Fluid> firstFluid,
                                           Supplier<? extends @NotNull Fluid>... otherFluids) {

        Preconditions.checkNotNull(firstFluid, "First fluid must not be null");

        if (otherFluids.length > 0) {
            return of(firstFluid.get(), CodeHelper.resolveSuppliers(Fluid[]::new, otherFluids));
        } else {
            return of(firstFluid.get());
        }
    }

    public static FluidRecipeIngredient of(int amount, Fluid firstFluid, Fluid... otherFluids) {

        Preconditions.checkNotNull(firstFluid, "First fluid must not be null");

        return new FluidRecipeIngredient(amount, Optional.empty(), firstFluid, otherFluids);
    }

    @SafeVarargs
    public static FluidRecipeIngredient of(int amount, Supplier<? extends @NotNull Fluid> firstFluid,
                                           Supplier<? extends @NotNull Fluid>... otherFluids) {

        Preconditions.checkNotNull(firstFluid, "First fluid must not be null");

        if (otherFluids.length > 0) {
            return of(amount, firstFluid.get(), CodeHelper.resolveSuppliers(Fluid[]::new, otherFluids));
        } else {
            return of(amount, firstFluid.get());
        }
    }

    public static FluidRecipeIngredient of(int amount, DataComponentPredicate componentPredicate, Fluid firstFluid,
                                           Fluid... otherFluids) {

        Preconditions.checkNotNull(firstFluid, "First fluid must not be null");

        return new FluidRecipeIngredient(amount, Optional.of(componentPredicate), firstFluid, otherFluids);
    }

    @SafeVarargs
    public static FluidRecipeIngredient of(int amount, DataComponentPredicate componentPredicate,
                                           Supplier<? extends @NotNull Fluid> firstFluid,
                                           Supplier<? extends @NotNull Fluid>... otherFluids) {

        Preconditions.checkNotNull(firstFluid, "First fluid must not be null");

        if (otherFluids.length > 0) {
            return of(amount, componentPredicate, firstFluid.get(), CodeHelper.resolveSuppliers(Fluid[]::new, otherFluids));
        } else {
            return of(amount, componentPredicate, firstFluid.get());
        }
    }

    public static FluidRecipeIngredient of(TagKey<Fluid> tag, HolderGetter<Fluid> holderGetter) {
        return of(1000, tag, holderGetter);
    }

    public static FluidRecipeIngredient of(int amount, TagKey<Fluid> tag, HolderGetter<Fluid> holderGetter) {

        Preconditions.checkNotNull(tag, "Tag must not be null");
        Preconditions.checkNotNull(holderGetter, "Holder getter must not be null");

        return new FluidRecipeIngredient(amount, Optional.empty(), tag, holderGetter);
    }

    public static FluidRecipeIngredient of(int amount, DataComponentPredicate componentPredicate,
                                           TagKey<Fluid> tag, HolderGetter<Fluid> holderGetter) {

        Preconditions.checkNotNull(tag, "Tag must not be null");
        Preconditions.checkNotNull(holderGetter, "Holder getter must not be null");

        return new FluidRecipeIngredient(amount, Optional.of(componentPredicate), tag, holderGetter);
    }

    //region IRecipeIngredient<ItemStack>

    @Override
    public FluidStack getMatchFrom(FluidStack ingredient) {
        return this.test(ingredient) ? ingredient.copyWithAmount(this._amount) : FluidStack.EMPTY;
    }

    @Override
    public List<FluidStack> getMatchingElements() {

        if (null == this._cachedMatchingElements) {

            final var fluids = this._ingredient.fluids();
            final var components = this._componentPredicate
                    .map(DataComponentPredicate::asPatch)
                    .orElse(DataComponentPatch.EMPTY);

            final ObjectList<FluidStack> elements = new ObjectArrayList<>(fluids.size());

            for (final var fluid : fluids) {
                elements.add(new FluidStack(fluid, this._amount, components));
            }

            this._cachedMatchingElements = ObjectLists.unmodifiable(elements);
        }

        return this._cachedMatchingElements;
    }

    @Override
    public boolean isEmpty() {
        return this._ingredient.fluids().isEmpty();
    }

    @Override
    public SlotDisplay asSlotDisplay() {

        final var elements = this.getMatchingElements();

        if (1 == elements.size()) {

            return new FluidStackSlotDisplay(elements.getFirst());

        } else {

            final List<SlotDisplay> displays = new ObjectArrayList<>(elements.size());

            for (final var element : elements) {
                displays.add(new FluidStackSlotDisplay(element));
            }

            return new SlotDisplay.Composite(displays);
        }
    }

    @Override
    public ModCodecs<FluidRecipeIngredient, RegistryFriendlyByteBuf> getCodecs() {
        return CODECS;
    }

    @Override
    public boolean test(FluidStack stack) {
        return this._ingredient.test(stack) &&
                this._amount <= stack.getAmount() &&
                this._componentPredicate
                        .map(predicate -> predicate.test(stack.getComponents()))
                        .orElse(true);
    }

    //endregion
    //region internals

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FluidRecipeIngredient(int amount, Optional<DataComponentPredicate> componentPredicate, Fluid fluid) {
        this(FluidIngredient.of(fluid), amount, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FluidRecipeIngredient(int amount, Optional<DataComponentPredicate> componentPredicate, Fluid firstFluid,
                                  Fluid... otherFluids) {
        this(FluidIngredient.of(CodeHelper.concatArrays(firstFluid, otherFluids)), amount, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FluidRecipeIngredient(int amount, Optional<DataComponentPredicate> componentPredicate,
                                  TagKey<Fluid> tag, HolderGetter<Fluid> holderGetter) {
        this(FluidIngredient.of(holderGetter.getOrThrow(tag)), amount, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FluidRecipeIngredient(FluidIngredient ingredient, int amount, Optional<DataComponentPredicate> componentPredicate) {

        Preconditions.checkNotNull(ingredient, "Ingredient must not be null");
        Preconditions.checkArgument(amount > 0, "Count must be greater than zero");

        this._ingredient = ingredient;
        this._amount = amount;
        this._componentPredicate = componentPredicate;
    }

    private final FluidIngredient _ingredient;
    private final int _amount;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<DataComponentPredicate> _componentPredicate;

    private List<FluidStack> _cachedMatchingElements;

    //endregion
}

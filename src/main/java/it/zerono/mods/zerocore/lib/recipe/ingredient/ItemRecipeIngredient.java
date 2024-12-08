/*
 *
 * ItemRecipeIngredient.java
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class ItemRecipeIngredient
        implements IRecipeIngredient<@NotNull ItemStack> {

    public static final ModCodecs<ItemRecipeIngredient, RegistryFriendlyByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Ingredient.CODEC.fieldOf(Lib.NAME_INGREDIENT).forGetter($ -> $._ingredient),
                            ExtraCodecs.POSITIVE_INT.fieldOf(Lib.NAME_COUNT).forGetter($ -> $._count),
                            DataComponentPredicate.CODEC.optionalFieldOf(Lib.NAME_COMPONENTS).forGetter($ -> $._componentPredicate)
                    ).apply(instance, ItemRecipeIngredient::new)
            ),
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, $ -> $._ingredient,
                    ByteBufCodecs.VAR_INT, $ -> $._count,
                    ByteBufCodecs.optional(DataComponentPredicate.STREAM_CODEC), $ -> $._componentPredicate,
                    ItemRecipeIngredient::new
            )
    );

    public static ItemRecipeIngredient copyOf(ItemStack stack) {

        Preconditions.checkNotNull(stack, "Stack must not be null");

        return of(stack.getCount(), DataComponentPredicate.allOf(stack.getComponents()), stack.getItem());
    }

    public static ItemRecipeIngredient of(ItemLike item) {
        return of(1, item);
    }

    public static ItemRecipeIngredient of(Supplier<? extends @NotNull ItemLike> item) {
        return of(1, item.get());
    }

    public static ItemRecipeIngredient of(int count, ItemLike item) {

        Preconditions.checkNotNull(item, "Item must not be null");

        return new ItemRecipeIngredient(count, Optional.empty(), item);
    }

    public static ItemRecipeIngredient of(int count, Supplier<? extends @NotNull ItemLike> item) {
        return of(count, item.get());
    }

    public static ItemRecipeIngredient of(int count, DataComponentPredicate componentPredicate, ItemLike item) {

        Preconditions.checkNotNull(item, "Item must not be null");

        return new ItemRecipeIngredient(count, Optional.of(componentPredicate), item);
    }

    public static ItemRecipeIngredient of(int count, DataComponentPredicate componentPredicate,
                                          Supplier<? extends @NotNull ItemLike> item) {
        return of(count, componentPredicate, item.get());
    }

    public static ItemRecipeIngredient of(ItemLike fistItem, ItemLike... otherItems) {
        return of(1, fistItem, otherItems);
    }

    @SafeVarargs
    public static ItemRecipeIngredient of(Supplier<? extends @NotNull ItemLike> fistItem,
                                          Supplier<? extends @NotNull ItemLike>... otherItems) {

        Preconditions.checkNotNull(fistItem, "First item must not be null");

        if (otherItems.length > 0) {
            return of(fistItem.get(), CodeHelper.resolveSuppliers(ItemLike[]::new, otherItems));
        } else {
            return of(fistItem.get());
        }
    }

    public static ItemRecipeIngredient of(int count, ItemLike fistItem, ItemLike... otherItems) {

        Preconditions.checkNotNull(fistItem, "First item must not be null");

        return new ItemRecipeIngredient(count, Optional.empty(), fistItem, otherItems);
    }

    @SafeVarargs
    public static ItemRecipeIngredient of(int count, Supplier<? extends @NotNull ItemLike> fistItem,
                                          Supplier<? extends @NotNull ItemLike>... otherItems) {

        Preconditions.checkNotNull(fistItem, "First item must not be null");

        if (otherItems.length > 0) {
            return of(count, fistItem.get(), CodeHelper.resolveSuppliers(ItemLike[]::new, otherItems));
        } else {
            return of(count, fistItem.get());
        }
    }

    public static ItemRecipeIngredient of(int count, DataComponentPredicate componentPredicate, ItemLike fistItem,
                                          ItemLike... otherItems) {

        Preconditions.checkNotNull(fistItem, "First item must not be null");

        return new ItemRecipeIngredient(count, Optional.of(componentPredicate), fistItem, otherItems);
    }

    @SafeVarargs
    public static ItemRecipeIngredient of(int count, DataComponentPredicate componentPredicate,
                                          Supplier<? extends @NotNull ItemLike> fistItem,
                                          Supplier<? extends @NotNull ItemLike>... otherItems) {

        Preconditions.checkNotNull(fistItem, "First item must not be null");

        if (otherItems.length > 0) {
            return of(count, componentPredicate, fistItem.get(), CodeHelper.resolveSuppliers(ItemLike[]::new, otherItems));
        } else {
            return of(count, componentPredicate, fistItem.get());
        }
    }

    public static ItemRecipeIngredient of(TagKey<Item> tag, HolderGetter<Item> holderGetter) {
        return of(1, tag, holderGetter);
    }

    public static ItemRecipeIngredient of(int count, TagKey<Item> tag, HolderGetter<Item> holderGetter) {

        Preconditions.checkNotNull(tag, "Tag must not be null");
        Preconditions.checkNotNull(holderGetter, "Holder getter must not be null");

        return new ItemRecipeIngredient(count, Optional.empty(), tag, holderGetter);
    }

    public static ItemRecipeIngredient of(int count, DataComponentPredicate componentPredicate,
                                          TagKey<Item> tag, HolderGetter<Item> holderGetter) {

        Preconditions.checkNotNull(tag, "Tag must not be null");
        Preconditions.checkNotNull(holderGetter, "Holder getter must not be null");

        return new ItemRecipeIngredient(count, Optional.of(componentPredicate), tag, holderGetter);
    }

    //region IRecipeIngredient<ItemStack>

    @Override
    public ItemStack getMatchFrom(ItemStack ingredient) {
        return this.test(ingredient) ? ingredient.copyWithCount(this._count) : ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> getMatchingElements() {

        if (null == this._cachedMatchingElements) {

            final var items = this._ingredient.items();
            final var components = this._componentPredicate
                    .map(DataComponentPredicate::asPatch)
                    .orElse(DataComponentPatch.EMPTY);

            final ObjectList<ItemStack> elements = new ObjectArrayList<>(items.size());

            for (final var item : items) {
                elements.add(new ItemStack(item, this._count, components));
            }

            this._cachedMatchingElements = ObjectLists.unmodifiable(elements);
        }

        return this._cachedMatchingElements;
    }

    @Override
    public boolean isEmpty() {
        return this._ingredient.items().isEmpty();
    }

    @Override
    public List<Ingredient> asVanillaIngredients() {
        return ObjectLists.singleton(this._ingredient);
    }

    @Override
    public SlotDisplay asSlotDisplay() {

        final var elements = this.getMatchingElements();

        if (1 == elements.size()) {

            return new SlotDisplay.ItemStackSlotDisplay(elements.getFirst());

        } else {

            final List<SlotDisplay> displays = new ObjectArrayList<>(elements.size());

            for (final var element : elements) {
                displays.add(new SlotDisplay.ItemStackSlotDisplay(element));
            }

            return new SlotDisplay.Composite(displays);
        }
    }

    @Override
    public ModCodecs<ItemRecipeIngredient, RegistryFriendlyByteBuf> getCodecs() {
        return CODECS;
    }

    @Override
    public boolean testIgnoreAmount(@NotNull ItemStack stack) {
        return this._ingredient.test(stack) &&
                this._componentPredicate
                        .map(predicate -> predicate.test(stack.getComponents()))
                        .orElse(true);
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.testIgnoreAmount(stack) && this._count <= stack.getCount();
    }

    //endregion
    //region internals

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ItemRecipeIngredient(int count, Optional<DataComponentPredicate> componentPredicate, ItemLike item) {
        this(Ingredient.of(item), count, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ItemRecipeIngredient(int count, Optional<DataComponentPredicate> componentPredicate,
                                 ItemLike fistItem, ItemLike... otherItems) {
        this(Ingredient.of(CodeHelper.concatArrays(fistItem, otherItems)), count, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ItemRecipeIngredient(int count, Optional<DataComponentPredicate> componentPredicate,
                                 TagKey<Item> tag, HolderGetter<Item> holderGetter) {
        this(Ingredient.of(holderGetter.getOrThrow(tag)), count, componentPredicate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ItemRecipeIngredient(Ingredient ingredient, int count, Optional<DataComponentPredicate> componentPredicate) {

        Preconditions.checkNotNull(ingredient, "Ingredient must not be null");
        Preconditions.checkArgument(count > 0, "Count must be greater than zero");

        this._ingredient = ingredient;
        this._count = count;
        this._componentPredicate = componentPredicate;
    }

    private final Ingredient _ingredient;
    private final int _count;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<DataComponentPredicate> _componentPredicate;

    private List<ItemStack> _cachedMatchingElements;

    //endregion
}

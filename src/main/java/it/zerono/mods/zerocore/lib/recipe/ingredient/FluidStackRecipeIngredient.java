/*
 *
 * FluidStackRecipeIngredient.java
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
import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

public abstract class FluidStackRecipeIngredient
        implements IRecipeIngredient<FluidStack> {

    public static FluidStackRecipeIngredient from(final Fluid fluid, final int amount) {
        return from(new FluidStack(fluid, amount));
    }

    public static FluidStackRecipeIngredient from(final FluidStack stack) {
        return new FluidStackRecipeIngredient.Impl(stack);
    }

    public static FluidStackRecipeIngredient from(final Tag<Fluid> tag, final int amount) {
        return new FluidStackRecipeIngredient.TaggedImpl(tag, amount);
    }

    public static FluidStackRecipeIngredient from(final FluidStackRecipeIngredient... ingredients) {
        return new FluidStackRecipeIngredient.CompositeImpl(ingredients);
    }

    public static FluidStackRecipeIngredient from(final FriendlyByteBuf buffer) {

        final byte type = buffer.readByte();

        switch (type) {

            case 1:
                return new Impl(FluidStack.readFromPacket(buffer));

            case 2:

                final FluidStackRecipeIngredient[] ingredients = new FluidStackRecipeIngredient[buffer.readVarInt()];

                for (int idx = 0; idx < ingredients.length; ++idx) {
                    ingredients[idx] = new Impl(FluidStack.readFromPacket(buffer));
                }

                return new CompositeImpl(ingredients);

            case 3:
                return new TaggedImpl(FluidTags.bind(buffer.readResourceLocation().toString()), buffer.readVarInt());
        }

        throw new IllegalArgumentException("Invalid fluid ingredient data from then network");
    }

    public static FluidStackRecipeIngredient from(final JsonElement jsonElement) {

        if (jsonElement.isJsonNull()) {
            throw new JsonSyntaxException("A fluid ingredient entry cannot be null");
        }

        if (jsonElement.isJsonArray()) {

            final JsonArray json = jsonElement.getAsJsonArray();
            final int size = json.size();

            return switch (size) {
                case 0 -> throw new JsonSyntaxException("No ingredients found, at least one is required");
                case 1 -> from(json.get(0));
                default ->
                        //noinspection UnstableApiUsage
                        from(Streams.stream(json)
                                .map(FluidStackRecipeIngredient::from)
                                .toArray(FluidStackRecipeIngredient[]::new));
            };
        } else if (jsonElement.isJsonObject()) {

            final JsonObject json = jsonElement.getAsJsonObject();

            if (json.has(Lib.NAME_FLUID) && json.has(Lib.NAME_TAG)) {

                throw new JsonParseException("A fluid ingredient entry must be either a Tag or a Fluid, but cannot be both");

            } else if (json.has(Lib.NAME_FLUID)) {

                return from(FluidHelper.stackFrom(json.get(Lib.NAME_FLUID)));

            } else if (json.has(Lib.NAME_TAG)) {

                final int amount = JSONHelper.jsonGetInt(json, Lib.NAME_COUNT, 0);

                if (amount < 1) {
                    throw new JsonSyntaxException("The amount entry of a fluid ingredient must be a number greater than zero");
                }

                final ResourceLocation tagId = JSONHelper.jsonGetResourceLocation(json, Lib.NAME_TAG);
                final Tag<Fluid> tag = getFluidTagsCollection().getTag(tagId);

                if (null == tag) {
                    throw new JsonSyntaxException("Unknown fluid ingredient Tag: " + tagId);
                }

                return from(tag, amount);
            }
        }

        throw new JsonSyntaxException("Expected fluid ingredient entry to be object or array of objects");
    }

    //region implementations

    static class Impl
            extends FluidStackRecipeIngredient {

        //region FluidStackRecipeIngredient

        @Override
        public boolean isCompatible(final FluidStack stack) {
            return Objects.requireNonNull(stack).isFluidEqual(this._ingredient);
        }

        @Override
        public boolean isCompatible(final FluidStack... ingredients) {

            for (final FluidStack stack : ingredients) {
                if (stack.isFluidEqual(this._ingredient)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public FluidStack getMatchFrom(final FluidStack stack) {
            return this.test(stack) ? this._ingredient : FluidStack.EMPTY;
        }

        @Override
        public long getAmount(final FluidStack stack) {
            return this.isCompatible(stack) ? this._ingredient.getAmount() : 0;
        }

        @Override
        public List<FluidStack> getMatchingElements() {

            if (null == this._cachedMatchingElements) {
                this._cachedMatchingElements = ObjectLists.singleton(this._ingredient);
            }

            return this._cachedMatchingElements;
        }

        @Override
        public boolean isEmpty() {
            return this._ingredient.isEmpty();
        }

        @Override
        public void serializeTo(final FriendlyByteBuf buffer) {

            buffer.writeByte(1);
            this._ingredient.writeToPacket(buffer);
        }

        @Override
        public JsonElement serializeTo() {

            final JsonObject json = new JsonObject();

            json.add(Lib.NAME_FLUID, FluidHelper.stackToJSON(this._ingredient));
            return json;
        }

        @Override
        public boolean test(final FluidStack stack) {
            return this.isCompatible(stack) && stack.getAmount() >= this._ingredient.getAmount();
        }

        //endregion
        //region Object

        @Override
        public String toString() {
            return FluidHelper.toStringHelper(this._ingredient);
        }

        //endregion
        //region internals

        protected Impl(final FluidStack ingredient) {
            this._ingredient = ingredient;
        }

        private final FluidStack _ingredient;
        private List<FluidStack> _cachedMatchingElements;

        //endregion
    }

    static class CompositeImpl
            extends FluidStackRecipeIngredient {

        //region FluidStackRecipeIngredient

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
        public boolean test(final FluidStack stack) {
            return this._ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
        }

        @Override
        public void serializeTo(final FriendlyByteBuf buffer) {

            buffer.writeByte(2);
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

        protected CompositeImpl(final FluidStackRecipeIngredient[] ingredients) {
            this._ingredients = Lists.newArrayList(ingredients);
        }

        private final List<FluidStackRecipeIngredient> _ingredients;
        private List<FluidStack> _cachedMatchingElements;

        //endregion
    }

    static class TaggedImpl
            extends FluidStackRecipeIngredient {

        //region FluidStackRecipeIngredient

        @Override
        public boolean isCompatible(final FluidStack stack) {
            return Objects.requireNonNull(stack).getFluid().is(this._tag);
        }

        @Override
        public boolean isCompatible(final FluidStack... ingredients) {

            for (final FluidStack stack : ingredients) {
                if (stack.getFluid().is(this._tag)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public FluidStack getMatchFrom(final FluidStack stack) {
            return this.test(stack) ? new FluidStack(stack, this._amount) : FluidStack.EMPTY;
        }

        @Override
        public long getAmount(final FluidStack stack) {
            return this.isCompatible(stack) ? this._amount : 0;
        }

        @Override
        public List<FluidStack> getMatchingElements() {

            if (null == this._cachedMatchingElements) {

                //noinspection UnstableApiUsage
                this._cachedMatchingElements = TagsHelper.FLUIDS.getMatchingElements(this._tag).stream()
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
        public boolean test(final FluidStack stack) {
            return this.isCompatible(stack) && stack.getAmount() >= this._amount;
        }

        @Override
        public void serializeTo(final FriendlyByteBuf buffer) {

            buffer.writeByte(3);
            buffer.writeResourceLocation(Objects.requireNonNull(getFluidTagsCollection().getId(this._tag)));
            buffer.writeVarInt(this._amount);
        }

        @Override
        public JsonElement serializeTo() {

            final JsonObject json = new JsonObject();

            JSONHelper.jsonSetResourceLocation(json, Lib.NAME_TAG, Objects.requireNonNull(getFluidTagsCollection().getId(this._tag)));
            JSONHelper.jsonSetInt(json, Lib.NAME_COUNT, this._amount);
            return json;
        }

        //endregion
        //region Object

        @Override
        public String toString() {
            return this._amount + ' ' + this._tag.toString();
        }

        //endregion
        //region internals

        protected TaggedImpl(final Tag<Fluid> tag, final int amount) {

            this._tag = tag;
            this._amount = amount;
        }

        private final Tag<Fluid> _tag;
        private final int _amount;
        private List<FluidStack> _cachedMatchingElements;

        //endregion
    }

    //endregion
    //region internals

    private static TagCollection<Fluid> getFluidTagsCollection() {
        return SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY);
    }

    //endregion
}

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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidStackRecipeIngredient
        implements IRecipeIngredient<FluidStack> {

    public static final ModCodecs<FluidStackRecipeIngredient, RegistryFriendlyByteBuf> CODECS = new ModCodecs<>(
            FluidStack.CODEC.xmap(FluidStackRecipeIngredient::from, i -> i._ingredient),
            StreamCodec.composite(
                    FluidStack.STREAM_CODEC, i -> i._ingredient,
                    FluidStackRecipeIngredient::new
            )
    );

    public static FluidStackRecipeIngredient from(final Fluid fluid, final int amount) {
        return from(new FluidStack(fluid, amount));
    }

    public static FluidStackRecipeIngredient from(final FluidStack stack) {
        return new FluidStackRecipeIngredient(stack);
    }

    @Override
    public boolean isCompatible(final FluidStack stack) {

        Preconditions.checkNotNull(stack, "Stack must not be null");

        return FluidStack.isSameFluidSameComponents(this._ingredient, stack);
    }

    @Override
    public boolean isCompatible(final FluidStack... ingredients) {

        for (final FluidStack stack : ingredients) {
            if (FluidStack.isSameFluidSameComponents(this._ingredient, stack)) {
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
    public boolean test(final FluidStack stack) {
        return this.isCompatible(stack) && stack.getAmount() >= this._ingredient.getAmount();
    }

    @Override
    public boolean testIgnoreAmount(final FluidStack stack) {
        return this.isCompatible(stack);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return FluidHelper.toStringHelper(this._ingredient);
    }

    //endregion
    //region internals

    protected FluidStackRecipeIngredient(FluidStack ingredient) {
        this._ingredient = ingredient;
    }

    private final FluidStack _ingredient;
    private List<FluidStack> _cachedMatchingElements;

    //endregion
}

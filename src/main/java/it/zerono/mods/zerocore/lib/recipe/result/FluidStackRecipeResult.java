/*
 *
 * FluidStackRecipeResult.java
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

package it.zerono.mods.zerocore.lib.recipe.result;

import com.google.gson.JsonElement;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class FluidStackRecipeResult
        implements IRecipeResult<FluidStack> {

    public static FluidStackRecipeResult from(final FluidStack stack) {
        return new FluidStackRecipeResult(stack);
    }

    public static FluidStackRecipeResult from(final FriendlyByteBuf buffer) {
        return new FluidStackRecipeResult(FluidStack.readFromPacket(buffer));
    }

    public static FluidStackRecipeResult from(final JsonElement jsonElement) {
        return new FluidStackRecipeResult(FluidHelper.stackFrom(jsonElement));
    }

    //region IRecipeResult<FluidStack>

    /**
     * @return Return an unique identifier for this result
     */
    @Override
    public ResourceLocation getId() {
        return Objects.requireNonNull(this._result.getFluid().getRegistryName());
    }

    /**
     * @return Return a new instance of the recipe result
     */
    @Override
    public FluidStack getResult() {
        return this._result.copy();
    }

    /**
     * @return Amount produced by each crafting
     */
    @Override
    public long getAmount() {
        return this._result.isEmpty() ? 0 : this._result.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return this._result.isEmpty();
    }

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {
        this._result.writeToPacket(buffer);
    }

    @Override
    public JsonElement serializeTo() {
        return FluidHelper.stackToJSON(this._result);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return FluidHelper.toStringHelper(this._result);
    }

    //endregion
    //region internals

    private FluidStackRecipeResult(final FluidStack stack) {
        this._result = stack;
    }

    private final FluidStack _result;

    //endregion
}

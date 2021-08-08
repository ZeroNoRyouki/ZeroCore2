/*
 *
 * StackResult.java
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
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ItemStackRecipeResult
    implements IRecipeResult<ItemStack> {

    public static ItemStackRecipeResult from(final ItemStack stack) {
        return new ItemStackRecipeResult(stack);
    }

    public static ItemStackRecipeResult from(final FriendlyByteBuf buffer) {
        return new ItemStackRecipeResult(buffer.readItem());
    }

    public static ItemStackRecipeResult from(final JsonElement jsonElement) {
        return new ItemStackRecipeResult(ItemHelper.stackFrom(jsonElement));
    }

    public static ItemStackRecipeResult from(final ItemLike item) {
        return from(item, 1);
    }

    public static ItemStackRecipeResult from(final ItemLike item, final int amount) {
        return from(new ItemStack(item, amount));
    }

    //region IRecipeResult<ItemStack>

    /**
     * @return Return an unique identifier for this result
     */
    @Override
    public ResourceLocation getId() {
        return Objects.requireNonNull(this._result.getItem().getRegistryName());
    }

    /**
     * @return Return a new instance of the recipe result
     */
    @Override
    public ItemStack getResult() {
        return this._result.copy();
    }

    /**
     * @return Amount produced by each crafting
     */
    @Override
    public long getAmount() {
        return this._result.isEmpty() ? 0 : this._result.getCount();
    }

    @Override
    public boolean isEmpty() {
        return this._result.isEmpty();
    }

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {
        buffer.writeItem(this._result);
    }

    @Override
    public JsonElement serializeTo() {
        return ItemHelper.stackToJSON(this._result);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this._result.toString();
    }

    //endregion
    //region internals

    private ItemStackRecipeResult(final ItemStack stack) {
        this._result = stack;
    }

    private final ItemStack _result;

    //endregion
}

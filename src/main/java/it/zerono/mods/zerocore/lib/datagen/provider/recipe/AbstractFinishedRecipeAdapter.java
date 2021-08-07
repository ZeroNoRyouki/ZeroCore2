/*
 *
 * FinishedRecipeAdapter.java
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

package it.zerono.mods.zerocore.lib.datagen.provider.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public abstract class AbstractFinishedRecipeAdapter
        implements IFinishedRecipe {

    //region IFinishedRecipe

    @Override
    public void serializeRecipeData(final JsonObject json) {
        this._originalRecipe.serializeRecipeData(json);
    }

    /**
     * Gets the ID for the recipe.
     */
    @Override
    public ResourceLocation getId() {
        return this._originalRecipe.getId();
    }

    @Override
    public IRecipeSerializer<?> getType() {
        return this._serializer;
    }

    /**
     * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
     */
    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return this._originalRecipe.serializeAdvancement();
    }

    /**
     * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
     * non-null.
     */
    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return this._originalRecipe.getAdvancementId();
    }

    //endregion
    //region internals

    protected AbstractFinishedRecipeAdapter(final IFinishedRecipe originalRecipe, final IRecipeSerializer<?> serializer) {

        this._originalRecipe = originalRecipe;
        this._serializer = serializer;
    }

    protected final IFinishedRecipe _originalRecipe;
    protected final IRecipeSerializer<?> _serializer;

    //endregion
}

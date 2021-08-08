/*
 *
 * NbtResultFinishedRecipeAdapter.java
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
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;

import java.util.function.Consumer;

public class NbtResultFinishedRecipeAdapter
        extends AbstractFinishedRecipeAdapter {

    public static Consumer<FinishedRecipe> from(final Consumer<FinishedRecipe> originalRecipe, final RecipeSerializer<?> serializer,
                                                 final CompoundTag data)  {
        return fr -> originalRecipe.accept(new NbtResultFinishedRecipeAdapter(fr, serializer, data));
    }

    public static Consumer<FinishedRecipe> from(final Consumer<FinishedRecipe> originalRecipe, final RecipeSerializer<?> serializer,
                                                 final Consumer<CompoundTag> data) {

        final CompoundTag nbt = new CompoundTag();

        data.accept(nbt);
        return from(originalRecipe, serializer, nbt);
    }

    //region IFinishedRecipe

    @Override
    public void serializeRecipeData(final JsonObject json) {

        super.serializeRecipeData(json);

        if (null != this._data) {
            GsonHelper.getAsJsonObject(json, "result").addProperty("nbt", this._data.toString());
        }
    }

    //endregion
    //region internals

    private NbtResultFinishedRecipeAdapter(final FinishedRecipe originalRecipe, final RecipeSerializer<?> serializer,
                                           final CompoundTag resultData) {

        super(originalRecipe, serializer);
        this._data = resultData;
    }

    private final CompoundTag _data;

    //endregion
}

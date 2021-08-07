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
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;

import java.util.function.Consumer;

public class NbtResultFinishedRecipeAdapter
        extends AbstractFinishedRecipeAdapter {

    public static Consumer<IFinishedRecipe> from(final Consumer<IFinishedRecipe> originalRecipe, final IRecipeSerializer<?> serializer,
                                                 final CompoundNBT data)  {
        return fr -> originalRecipe.accept(new NbtResultFinishedRecipeAdapter(fr, serializer, data));
    }

    public static Consumer<IFinishedRecipe> from(final Consumer<IFinishedRecipe> originalRecipe, final IRecipeSerializer<?> serializer,
                                                 final Consumer<CompoundNBT> data) {

        final CompoundNBT nbt = new CompoundNBT();

        data.accept(nbt);
        return from(originalRecipe, serializer, nbt);
    }

    //region IFinishedRecipe

    @Override
    public void serializeRecipeData(final JsonObject json) {

        super.serializeRecipeData(json);

        if (null != this._data) {
            JSONUtils.getAsJsonObject(json, "result").addProperty("nbt", this._data.toString());
        }
    }

    //endregion
    //region internals

    private NbtResultFinishedRecipeAdapter(final IFinishedRecipe originalRecipe, final IRecipeSerializer<?> serializer,
                                           final CompoundNBT resultData) {

        super(originalRecipe, serializer);
        this._data = resultData;
    }

    private final CompoundNBT _data;

    //endregion
}

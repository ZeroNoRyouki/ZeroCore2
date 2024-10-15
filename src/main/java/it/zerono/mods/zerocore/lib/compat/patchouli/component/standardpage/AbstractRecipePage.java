/*
 *
 * AbstractRecipePage.java
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

package it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage;

import com.google.gson.annotations.SerializedName;
import it.zerono.mods.zerocore.internal.Log;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;

import java.lang.reflect.Field;
import java.util.function.UnaryOperator;

public abstract class AbstractRecipePage<T, PageType extends PageDoubleRecipe<T>>
    extends AbstractTextPage<PageType> {

    @SerializedName("recipe") String recipeId;
    @SerializedName("recipe2") String recipe2Id;
    String title;

    protected AbstractRecipePage(final PageType page) {
        super(page);
    }

    //region AbstractStandardPageComponent

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {

        super.onVariablesAvailable(lookup, registries);

        try {

            recipe1Field.set(this.getPage(), ResourceLocation.tryParse(lookup.apply(IVariable.wrap(recipeId, registries)).asString()));
            recipe2Field.set(this.getPage(), ResourceLocation.tryParse(lookup.apply(IVariable.wrap(recipe2Id, registries)).asString()));
            titleField.set(this.getPage(), lookup.apply(IVariable.wrap(title, registries)).asString());

        } catch (IllegalAccessException e) {

            Log.LOGGER.warn(Log.CORE, "patchouli AbstractRecipePage wrapper : Unable to set inner page fields");
        }
    }

    //endregion
    //region internals

    private static final Field recipe1Field;
    private static final Field recipe2Field;
    private static final Field titleField;

    static {

        recipe1Field = getField(PageDoubleRecipe.class, "recipeId");
        recipe2Field = getField(PageDoubleRecipe.class, "recipe2Id");
        titleField = getField(PageDoubleRecipe.class, "title");
    }

    //endregion
}

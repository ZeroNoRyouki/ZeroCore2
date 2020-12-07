/*
 *
 * Spotlight.java
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
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.page.PageSpotlight;

import java.lang.reflect.Field;
import java.util.function.UnaryOperator;

public class Spotlight
        extends AbstractTextPage<PageSpotlight> {

    IVariable item;
    String title;
    @SerializedName("link_recipe")
    boolean linkRecipe;

    protected Spotlight() {
        super(new PageSpotlight());
    }

    //region AbstractStandardPageComponent

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {

        super.onVariablesAvailable(lookup);

        try {

            itemField.set(this.getPage(), lookup.apply(item));
            titleField.set(this.getPage(), lookup.apply(IVariable.wrap(title)).asString());
            linkRecipeField.set(this.getPage(), lookup.apply(IVariable.wrap(linkRecipe)).asBoolean());

        } catch (IllegalAccessException e) {
            Log.LOGGER.warn(Log.CORE, "patchouli Spotlight wrapper : Unable to set inner page fields");
        }
    }

    //endregion
    //region internals

    private static final Field itemField;
    private static final Field titleField;
    private static final Field linkRecipeField;

    static {

        itemField = getField(PageSpotlight.class, "item");
        titleField = getField(PageSpotlight.class, "title");
        linkRecipeField = getField(PageSpotlight.class, "linkRecipe");
    }

    //endregion
}

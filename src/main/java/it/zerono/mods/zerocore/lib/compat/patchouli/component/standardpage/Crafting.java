/*
 *
 * Crafting.java
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.crafting.Recipe;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.page.PageCrafting;

import java.util.function.UnaryOperator;

public class Crafting
        extends AbstractRecipePage<Recipe<?>, PageCrafting> {

    @SerializedName("title1") String title1;
    @SerializedName("title2") String title2;

    protected Crafting() {
        super(new PageCraftingTooltipFix());
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {

        super.onVariablesAvailable(lookup);

        final String title1 = lookup.apply(IVariable.wrap(this.title1)).asString();
        final String title2 = lookup.apply(IVariable.wrap(this.title2)).asString();

        ((PageCraftingTooltipFix)this.getPage()).setTitles(title1, title2);
    }

    //region internals
    //region Tooltips fix

    private static class PageCraftingTooltipFix
            extends PageCrafting {

        void setTitles(String title1, String title2) {

            this._overrideTitle1 = new TextComponent(title1);
            this._overrideTitle2 = new TextComponent(title2);
        }

        //region PageCrafting

        @Override
        protected Component getTitle(boolean second) {
            return second ? this._overrideTitle2 : this._overrideTitle1;
        }

        //endregion
        //region internals

        protected Component _overrideTitle1;
        protected Component _overrideTitle2;

        //endregion
    }

    //endregion
    //endregion
}

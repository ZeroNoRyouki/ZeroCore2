/*
 *
 * ModRecipeCategory.java
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

package it.zerono.mods.zerocore.lib.compat.jei;

import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractModRecipeCategory<T extends ModRecipe>
        implements IRecipeCategory<T> {

    protected AbstractModRecipeCategory(final ResourceLocation id, final Component title, final ItemStack icon,
                                        final IGuiHelper guiHelper, final IDrawable background) {

        this._id = id;
        this._title = title;
        this._icon = guiHelper.createDrawableIngredient(icon);
        this._background = background;
    }

    //region IRecipeCategory

    @Override
    public ResourceLocation getUid() {
        return this._id;
    }

    @Override
    public IDrawable getBackground() {
        return this._background;
    }

    @Override
    public IDrawable getIcon() {
        return this._icon;
    }

    @Override
    public Component getTitle() {
        return this._title;
    }

    //endregion
    //region internals

    private final ResourceLocation _id;
    private final Component _title;
    private final IDrawable _icon;
    private final IDrawable _background;

    //endregion
}

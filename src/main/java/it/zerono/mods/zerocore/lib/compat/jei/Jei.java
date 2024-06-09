package it.zerono.mods.zerocore.lib.compat.jei;

/*
 * Jei
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
 * Do not remove or edit this header
 *
 */

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.internal.compat.jei.ZeroCoreJeiPlugin;
import mezz.jei.api.recipe.RecipeType;

import java.util.Collections;
import java.util.List;

public final class Jei {

    public static void displayRecipeType(RecipeType<?> type, RecipeType<?>... others) {

        final List<RecipeType<?>> types = new ObjectArrayList<>(1 + others.length);

        types.add(type);

        if (others.length > 0) {
            Collections.addAll(types, others);
        }

        ZeroCoreJeiPlugin.getInstance().getRecipesGui().showTypes(types);
    }

    //region internals

    private Jei() {
    }

    //endregion
}

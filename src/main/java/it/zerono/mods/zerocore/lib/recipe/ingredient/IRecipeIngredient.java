/*
 *
 * IRecipeIngredient.java
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

package it.zerono.mods.zerocore.lib.recipe.ingredient;

import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.function.Predicate;

public interface IRecipeIngredient<T>
    extends Predicate<T> {

    T getMatchFrom(T ingredient);

    List<T> getMatchingElements();

    boolean isEmpty();

    default List<Ingredient> asVanillaIngredients() {
        return ObjectLists.emptyList();
    }

    default boolean testIgnoreAmount(T ingredient) {
        return this.test(ingredient);
    }

    SlotDisplay asSlotDisplay();

    ModCodecs<? extends IRecipeIngredient<T>, RegistryFriendlyByteBuf> getCodecs();
}

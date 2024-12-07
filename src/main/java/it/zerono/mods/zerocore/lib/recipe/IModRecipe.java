/*
 *
 * IModRecipe.java
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

package it.zerono.mods.zerocore.lib.recipe;

import it.zerono.mods.zerocore.lib.item.inventory.EmptyVanillaInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface IModRecipe
        extends Recipe<EmptyVanillaInventory> {

    ResourceKey<Recipe<?>> getRegistrationKey();

    Supplier<? extends @NotNull Item> getRecipeIcon();

    default SlotDisplay getCraftingStationSlotDisplay() {
        return new SlotDisplay.ItemSlotDisplay(this.getRecipeIcon().get());
    }

    //region Recipe

    @Override
    default boolean matches(EmptyVanillaInventory inv, Level world) {
        return true;
    }

    @Override
    default ItemStack assemble(EmptyVanillaInventory inv, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    //endregion
}

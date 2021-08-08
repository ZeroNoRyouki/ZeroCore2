/*
 *
 * DataGenHelper.java
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

package it.zerono.mods.zerocore.lib.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;

public final class DataGenHelper {

    public static ItemModelBuilder simpleItem(ItemModelProvider provider, final String name,
                                                 final ResourceLocation parent, final ResourceLocation texture) {
        return provider.withExistingParent(name, parent)
                .texture("layer0", texture);
    }

    public static ItemModelBuilder simpleItem(ItemModelProvider provider, final Item item,
                                              final ResourceLocation parent, final ResourceLocation texture) {
        return simpleItem(provider, itemName(item), parent, texture);
    }

    public static ItemModelBuilder generatedItem(ItemModelProvider provider, final String name,
                                                 final ResourceLocation texture) {
        return provider.withExistingParent(name, ITEM_PARENT_GENERATED)
                .texture("layer0", texture);
    }

    public static ItemModelBuilder generatedItem(ItemModelProvider provider, final Item item,
                                                 final ResourceLocation texture) {
        return generatedItem(provider, itemName(item), texture);
    }

    public static ItemModelBuilder generatedItem(ItemModelProvider provider, final Item item) {
        return generatedItem(provider, itemName(item), defaultItemTexture(provider, item));
    }

    public static ItemModelBuilder handHeldItem(ItemModelProvider provider, final String name,
                                                final ResourceLocation texture) {
        return provider.withExistingParent(name, ITEM_PARENT_HANDHELD)
                .texture("layer0", texture);
    }

    public static ItemModelBuilder handHeldItem(ItemModelProvider provider, final Item item,
                                                final ResourceLocation texture) {
        return handHeldItem(provider, itemName(item), texture);
    }

    public static ItemModelBuilder handHeldItem(ItemModelProvider provider, final Item item) {
        return handHeldItem(provider, itemName(item), defaultItemTexture(provider, item));
    }

    //region internals

    private static String itemName(Item item) {
        return item.getRegistryName().getPath();
    }

    private static ResourceLocation defaultItemTexture(ItemModelProvider provider, final Item item) {
        return provider.modLoc(ItemModelProvider.ITEM_FOLDER + "/" + itemName(item));
    }

    private static final ResourceLocation ITEM_PARENT_GENERATED = new ResourceLocation("item/generated");
    private static final ResourceLocation ITEM_PARENT_HANDHELD = new ResourceLocation("item/handheld");

    private DataGenHelper() {
    }

    //endregion
}

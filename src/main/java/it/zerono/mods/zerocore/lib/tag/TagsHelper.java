/*
 *
 * TagsHelper.java
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

package it.zerono.mods.zerocore.lib.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class TagsHelper<T> {

    @SuppressWarnings("deprecation")
    public static final TagSource<Block> BLOCKS = new TagSource<>(() -> BuiltInRegistries.BLOCK);
    @SuppressWarnings("deprecation")
    public static final TagSource<Item> ITEMS = new TagSource<>(() -> BuiltInRegistries.ITEM);
    @SuppressWarnings("deprecation")
    public static final TagSource<Fluid> FLUIDS = new TagSource<>(() -> BuiltInRegistries.FLUID);

    public static final TagKey<Item> TAG_WRENCH = ITEMS.createKey("forge:tools/wrench");

    //region internals

    private TagsHelper() {
    }

    //endregion
}

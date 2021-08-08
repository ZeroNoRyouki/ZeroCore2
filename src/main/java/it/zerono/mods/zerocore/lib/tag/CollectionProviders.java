/*
 *
 * CollectionProviders.java
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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.util.NonNullSupplier;

@SuppressWarnings({"WeakerAccess"})
public final class CollectionProviders {

    public static final NonNullSupplier<TagCollection<Block>> BLOCKS_PROVIDER = BlockTags::getAllTags;
    public static final NonNullSupplier<TagCollection<Item>> ITEMS_PROVIDER = ItemTags::getAllTags;
    public static final NonNullSupplier<TagCollection<Fluid>> FLUIDS_PROVIDER = FluidTags::getAllTags;
}

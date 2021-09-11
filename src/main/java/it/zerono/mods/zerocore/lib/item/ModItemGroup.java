/*
 *
 * ModItemGroup.java
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

package it.zerono.mods.zerocore.lib.item;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

public class ModItemGroup extends ItemGroup {

    public ModItemGroup(final String label, final Supplier<ItemStack> iconFactory, final Supplier<List<ItemStack>> contentFactory) {

        super(label);
        this._iconFactory = iconFactory;
        this._contentFactory = contentFactory;
    }

    //region ItemGroup

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return this._iconFactory.get();
    }

    /**
     * Fills {@code items} with all items that are in this group.
     *
     * @param items the list of items
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {

        if (null == this._content) {
            this._content = Lists.newArrayList(this._contentFactory.get());
        }

        items.addAll(this._content);
    }

    //region internals

    private final Supplier<ItemStack> _iconFactory;
    private final Supplier<List<ItemStack>> _contentFactory;
    private List<ItemStack> _content;
}

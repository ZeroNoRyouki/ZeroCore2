/*
 *
 * ItemStackFilterCondition.java
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

package it.zerono.mods.zerocore.lib.item.inventory.filter;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public class ItemStackFilterCondition implements IFilterCondition {

    public ItemStackFilterCondition(final ItemStack stack) {

        Preconditions.checkNotNull(stack, "The stack must not be null");
        this.setFilterStack(stack);
    }

    @Nullable
    public ItemStack getFilterStack() {
        return this._filterStack;
    }

    public void setFilterStack(final ItemStack filterStack) {
        this._filterStack = ItemHelper.stackFrom(filterStack);
    }

    public void setFilterStack(final ItemStack filterStack, int amount) {
        this._filterStack = ItemHelper.stackFrom(filterStack, amount);
    }

    public boolean isValid() {
        return null != this.getFilterStack();
    }

    @Override
    public ResourceLocation getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    public boolean match(ItemStack stack, EnumSet<ItemHelper.MatchOption> matchOptions) {

        Preconditions.checkNotNull(matchOptions, "The match options must not be null");
        return ItemHelper.stackMatch(this._filterStack, stack, matchOptions);
    }

    @Override
    public CompoundNBT serializeNBT() {

        final CompoundNBT nbt = new CompoundNBT();

        if (null != this._filterStack) {
            nbt.put(NBT_KEY, this._filterStack.write(new CompoundNBT()));
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this._filterStack = null == nbt || !nbt.contains(NBT_KEY) ? null :
                ItemHelper.stackFrom(nbt.getCompound(NBT_KEY));
    }

    private ItemStackFilterCondition() {
        this._filterStack = null;
    }

    private ItemStack _filterStack;

    private static final String NBT_KEY = "CndStack";
    private static final ResourceLocation COMPONENT_ID;

    static {

        COMPONENT_ID = ZeroCore.newID("inventory.filter.ItemStackFilterCondition");

        final FilterManager<ItemStackFilterCondition> fm = FilterManager.getInstance();

        fm.registerFactory(COMPONENT_ID, new IFilterComponentFactory<ItemStackFilterCondition>() {

            @Override
            public Optional<ItemStackFilterCondition> createComponent(ResourceLocation componentId) {
                return Optional.of(new ItemStackFilterCondition());
            }

            @Override
            public Optional<ItemStackFilterCondition> createComponent(ResourceLocation componentId, CompoundNBT nbt) {

                return this.createComponent(componentId)
                        .map(condition -> {

                            condition.deserializeNBT(nbt);
                            return condition.isValid() ? condition : null;
                        });
            }
        });
    }
}

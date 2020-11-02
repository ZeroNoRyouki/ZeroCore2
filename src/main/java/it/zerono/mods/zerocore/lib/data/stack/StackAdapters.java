/*
 *
 * StackAdapters.java
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

package it.zerono.mods.zerocore.lib.data.stack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"WeakerAccess"})
public final class StackAdapters {

    public static final IStackAdapter<ItemStack, Item> ITEMSTACK;

    public static final IStackAdapter<FluidStack, Fluid> FLUIDSTACK;

    //region internals

    private StackAdapters() {
    }

    private static <StackType, ContentType> void validateNotEmpty(IStackAdapter<StackType, ContentType> adapter, StackType stack) {

        if (adapter.getEmptyStack() == stack) {
            throw new IllegalArgumentException("Attempt to modify an empty stack blocked");
        }
    }

    static {

        ITEMSTACK = new IStackAdapter<ItemStack, Item>() {

            @Override
            public Optional<Item> getContent(ItemStack stack) {
                return !stack.isEmpty() ? Optional.of(stack.getItem()) : Optional.empty();
            }

            @Override
            public int getAmount(ItemStack stack) {
                return stack.getCount();
            }

            @Override
            public ItemStack setAmount(ItemStack stack, int amount) {

                validateNotEmpty(this, stack);
                stack.setCount(amount);
                return stack;
            }

            @Override
            public ItemStack modifyAmount(ItemStack stack, int delta) {

                validateNotEmpty(this, stack);
                stack.grow(delta);
                return stack;
            }

            @Override
            public ItemStack getEmptyStack() {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean isEmpty(ItemStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isStackContentEqual(ItemStack stack1, ItemStack stack2) {
                return stack1.isItemEqual(stack2);
            }

            @Override
            public boolean isContentEqual(Item content1, Item content2) {
                return content1 == content2;
            }

            @Override
            public boolean areIdentical(ItemStack stack1, ItemStack stack2) {
                return ItemStack.areItemStacksEqual(stack1, stack2);
            }

            @Override
            public ItemStack create(Item content, int amount) {
                return ItemHelper.stackFrom(content, amount);
            }

            @Override
            public ItemStack create(ItemStack stack) {
                return ItemHelper.stackFrom(stack);
            }

            @Override
            public ItemStack[] createArray(int length) {
                return new ItemStack[length];
            }

            @Override
            public List<ItemStack> createList() {
                return Lists.newArrayList();
            }

            @Override
            public Set<ItemStack> createSet() {
                return Sets.newHashSet();
            }

            @Override
            public ItemStack readFrom(CompoundNBT data) {
                return ItemHelper.stackFrom(data);
            }

            @Override
            public CompoundNBT writeTo(ItemStack stack, CompoundNBT data) {
                return stack.write(data);
            }

            @Override
            public String toString(ItemStack stack) {
                return stack.toString();
            }
        };

        FLUIDSTACK = new IStackAdapter<FluidStack, Fluid>() {

            @Override
            public Optional<Fluid> getContent(FluidStack stack) {
                return !stack.isEmpty() ? Optional.ofNullable(stack.getFluid()) : Optional.empty();
            }

            @Override
            public int getAmount(FluidStack stack) {
                return stack.getAmount();
            }

            @Override
            public FluidStack setAmount(FluidStack stack, int amount) {

                validateNotEmpty(this, stack);
                stack.setAmount(amount);
                return stack;
            }

            @Override
            public FluidStack modifyAmount(FluidStack stack, int delta) {

                validateNotEmpty(this, stack);
                stack.grow(delta);
                return stack;
            }

            @Override
            public FluidStack getEmptyStack() {
                return FluidStack.EMPTY;
            }

            @Override
            public boolean isEmpty(FluidStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isStackContentEqual(FluidStack stack1, FluidStack stack2) {
                return stack1.isFluidEqual(stack2);
            }

            @Override
            public boolean isContentEqual(Fluid content1, Fluid content2) {
                return content1.isEquivalentTo(content2);
            }

            @Override
            public boolean areIdentical(FluidStack stack1, FluidStack stack2) {
                return stack1.isFluidStackIdentical(stack2);
            }

            @Override
            public FluidStack create(Fluid content, int amount) {
                return new FluidStack(content, amount);
            }

            @Override
            public FluidStack create(FluidStack stack) {
                return stack.copy();
            }

            @Override
            public FluidStack[] createArray(int length) {
                return new FluidStack[length];
            }

            @Override
            public List<FluidStack> createList() {
                return Lists.newArrayList();
            }

            @Override
            public Set<FluidStack> createSet() {
                return Sets.newHashSet();
            }

            @Override
            public FluidStack readFrom(CompoundNBT data) {
                return FluidStack.loadFluidStackFromNBT(data);
            }

            @Override
            public CompoundNBT writeTo(FluidStack stack, CompoundNBT data) {
                return stack.writeToNBT(data);
            }

            @Override
            public String toString(FluidStack stack) {
                return stack.getAmount() + " " + stack.getFluid();
            }
        };
    }
}

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
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergyStack;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.WideEnergyStack;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;

@SuppressWarnings({"WeakerAccess"})
public final class StackAdapters {

    public static final IStackAdapter<ItemStack, Item> ITEMSTACK;

    public static final IStackAdapter<FluidStack, Fluid> FLUIDSTACK;

    /**
     * @implNote currently the amount of energy stored in that stack is converted, and capped to, an int
     */
    @Deprecated // use WIDEENERGYSTACK
    public static final IStackAdapter<EnergyStack, EnergySystem> ENERGYSTACK;

    /**
     * @implNote currently the amount of energy stored in that stack is converted, and capped to, an int
     */
    public static final IStackAdapter<WideEnergyStack, EnergySystem> WIDEENERGYSTACK;

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
                return stack1.sameItem(stack2);
            }

            @Override
            public boolean isContentEqual(Item content1, Item content2) {
                return content1 == content2;
            }

            @Override
            public boolean areIdentical(ItemStack stack1, ItemStack stack2) {
                return ItemStack.matches(stack1, stack2);
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
                return stack.save(data);
            }

            @Override
            public String toString(ItemStack stack) {
                return stack.toString();
            }

            @Override
            public <T> T map(ItemStack stack, Function<Item, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getItem());
            }

            @Override
            public <T> T map(ItemStack stack, IntFunction<T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getCount());
            }

            @Override
            public <T> T map(ItemStack stack, BiFunction<Item, Integer, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getItem(), stack.getCount());
            }

            @Override
            public void accept(ItemStack stack, Consumer<Item> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getItem());
                }
            }

            @Override
            public void accept(ItemStack stack, IntConsumer consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getCount());
                }
            }

            @Override
            public void accept(ItemStack stack, BiConsumer<Item, Integer> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getItem(), stack.getCount());
                }
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
                return content1.isSame(content2);
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

            @Override
            public <T> T map(FluidStack stack, Function<Fluid, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getFluid());
            }

            @Override
            public <T> T map(FluidStack stack, IntFunction<T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getAmount());
            }

            @Override
            public <T> T map(FluidStack stack, BiFunction<Fluid, Integer, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getFluid(), stack.getAmount());
            }

            @Override
            public void accept(FluidStack stack, Consumer<Fluid> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getFluid());
                }
            }

            @Override
            public void accept(FluidStack stack, IntConsumer consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getAmount());
                }
            }

            @Override
            public void accept(FluidStack stack, BiConsumer<Fluid, Integer> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getFluid(), stack.getAmount());
                }
            }
        };

        ENERGYSTACK = new IStackAdapter<EnergyStack, EnergySystem>() {

            @Override
            public Optional<EnergySystem> getContent(EnergyStack stack) {
                return !stack.isEmpty() ? Optional.of(stack.getEnergySystem()) : Optional.empty();
            }

            @Override
            public int getAmount(EnergyStack stack) {
                return (int)stack.getAmount();
            }

            @Override
            public EnergyStack setAmount(EnergyStack stack, int amount) {

                validateNotEmpty(this, stack);
                stack.setAmount(amount);
                return stack;
            }

            @Override
            public EnergyStack modifyAmount(EnergyStack stack, int delta) {

                validateNotEmpty(this, stack);
                stack.grow(delta);
                return stack;
            }

            @Override
            public EnergyStack getEmptyStack() {
                return EnergyStack.EMPTY;
            }

            @Override
            public boolean isEmpty(EnergyStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isStackContentEqual(EnergyStack stack1, EnergyStack stack2) {
                return stack1.isEnergySystemEqual(stack2);
            }

            @Override
            public boolean isContentEqual(EnergySystem content1, EnergySystem content2) {
                return content1 == content2;
            }

            @Override
            public boolean areIdentical(EnergyStack stack1, EnergyStack stack2) {
                return EnergyStack.areItemStacksEqual(stack1, stack2);
            }

            @Override
            public EnergyStack create(EnergySystem content, int amount) {
                return new EnergyStack(content, amount);
            }

            @Override
            public EnergyStack create(EnergyStack stack) {
                return stack.copy();
            }

            @Override
            public EnergyStack[] createArray(int length) {
                return new EnergyStack[length];
            }

            @Override
            public List<EnergyStack> createList() {
                return Lists.newArrayList();
            }

            @Override
            public Set<EnergyStack> createSet() {
                return Sets.newHashSet();
            }

            @Override
            public EnergyStack readFrom(CompoundNBT data) {
                return EnergyStack.from(data);
            }

            @Override
            public CompoundNBT writeTo(EnergyStack stack, CompoundNBT data) {
                return stack.serializeTo(data);
            }

            @Override
            public String toString(EnergyStack stack) {
                return stack.toString();
            }

            @Override
            public <T> T map(EnergyStack stack, Function<EnergySystem, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getEnergySystem());
            }

            @Override
            public <T> T map(EnergyStack stack, IntFunction<T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply((int)stack.getAmount());
            }

            @Override
            public <T> T map(EnergyStack stack, BiFunction<EnergySystem, Integer, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getEnergySystem(), (int)stack.getAmount());
            }

            @Override
            public void accept(EnergyStack stack, Consumer<EnergySystem> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getEnergySystem());
                }
            }

            @Override
            public void accept(EnergyStack stack, IntConsumer consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept((int)stack.getAmount());
                }
            }

            @Override
            public void accept(EnergyStack stack, BiConsumer<EnergySystem, Integer> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getEnergySystem(), (int)stack.getAmount());
                }
            }
        };

        WIDEENERGYSTACK = new IStackAdapter<WideEnergyStack, EnergySystem>() {

            @Override
            public Optional<EnergySystem> getContent(WideEnergyStack stack) {
                return !stack.isEmpty() ? Optional.of(stack.getEnergySystem()) : Optional.empty();
            }

            @Override
            public int getAmount(WideEnergyStack stack) {
                return stack.getAmount().intValue();
            }

            @Override
            public WideEnergyStack setAmount(WideEnergyStack stack, int amount) {

                validateNotEmpty(this, stack);
                stack.setAmount(WideAmount.from(amount));
                return stack;
            }

            @Override
            public WideEnergyStack modifyAmount(WideEnergyStack stack, int delta) {

                validateNotEmpty(this, stack);
                stack.grow(WideAmount.from(delta));
                return stack;
            }

            @Override
            public WideEnergyStack getEmptyStack() {
                return WideEnergyStack.EMPTY;
            }

            @Override
            public boolean isEmpty(WideEnergyStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isStackContentEqual(WideEnergyStack stack1, WideEnergyStack stack2) {
                return stack1.isEnergySystemEqual(stack2);
            }

            @Override
            public boolean isContentEqual(EnergySystem content1, EnergySystem content2) {
                return content1 == content2;
            }

            @Override
            public boolean areIdentical(WideEnergyStack stack1, WideEnergyStack stack2) {
                return WideEnergyStack.areItemStacksEqual(stack1, stack2);
            }

            @Override
            public WideEnergyStack create(EnergySystem content, int amount) {
                return new WideEnergyStack(content, WideAmount.from(amount));
            }

            @Override
            public WideEnergyStack create(WideEnergyStack stack) {
                return stack.copy();
            }

            @Override
            public WideEnergyStack[] createArray(int length) {
                return new WideEnergyStack[length];
            }

            @Override
            public List<WideEnergyStack> createList() {
                return Lists.newArrayList();
            }

            @Override
            public Set<WideEnergyStack> createSet() {
                return Sets.newHashSet();
            }

            @Override
            public WideEnergyStack readFrom(CompoundNBT data) {
                return WideEnergyStack.from(data);
            }

            @Override
            public CompoundNBT writeTo(WideEnergyStack stack, CompoundNBT data) {
                return stack.serializeTo(data);
            }

            @Override
            public String toString(WideEnergyStack stack) {
                return stack.toString();
            }

            @Override
            public <T> T map(WideEnergyStack stack, Function<EnergySystem, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getEnergySystem());
            }

            @Override
            public <T> T map(WideEnergyStack stack, IntFunction<T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getAmount().intValue());
            }

            @Override
            public <T> T map(WideEnergyStack stack, BiFunction<EnergySystem, Integer, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack.getEnergySystem(), stack.getAmount().intValue());
            }

            @Override
            public void accept(WideEnergyStack stack, Consumer<EnergySystem> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getEnergySystem());
                }
            }

            @Override
            public void accept(WideEnergyStack stack, IntConsumer consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getAmount().intValue());
                }
            }

            @Override
            public void accept(WideEnergyStack stack, BiConsumer<EnergySystem, Integer> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack.getEnergySystem(), stack.getAmount().intValue());
                }
            }
        };
    }
}

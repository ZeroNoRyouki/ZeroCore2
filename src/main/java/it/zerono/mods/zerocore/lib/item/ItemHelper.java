/*
 *
 * ItemHelper.java
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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SuppressWarnings({"WeakerAccess"})
public final class ItemHelper {

    public static final String INVENTORY = "inventory";

    public enum MatchOption {

        Item,
        Size,
        Damage,
        NBT,
        Capabilities,
        Tags;

        public static final EnumSet<MatchOption> MATCH_ALWAYS = EnumSet.noneOf(MatchOption.class);
        public static final EnumSet<MatchOption> MATCH_ALL = EnumSet.of(Item, Size, Damage, NBT, Capabilities, Tags);
        public static final EnumSet<MatchOption> MATCH_ITEM = EnumSet.of(Item);
        public static final EnumSet<MatchOption> MATCH_ITEM_DAMAGE = EnumSet.of(Item, Damage);
        public static final EnumSet<MatchOption> MATCH_ITEM_DAMAGE_NBT = EnumSet.of(Item, Damage, NBT);
        public static final EnumSet<MatchOption> MATCH_EXISTING_STACK = EnumSet.of(Item, Damage, NBT, Capabilities, Tags);
    }

    /**
     * Compare the provided ItemStacks using the specified method(s)
     *
     * @param stackA    the first ItemStack
     * @param stackB    the second ItemStack
     * @param options   specify how the match will be performed
     * @return true if the ItemStacks match each other, false otherwise
     */
    public static boolean stackMatch(final ItemStack stackA, final ItemStack stackB, EnumSet<MatchOption> options) {

        if (stackA.isEmpty() || stackB.isEmpty()) {
            return false;
        }

        if (options.isEmpty()) { // MATCH_ALWAYS
            return true;
        }

        boolean result = true;

        if (options.contains(MatchOption.Item)) {
            result = stackA.getItem() == stackB.getItem();
        }

        if (result && options.contains(MatchOption.Size)) {
            result = stackA.getCount() == stackB.getCount();
        }

        if (result && options.contains(MatchOption.Damage)) {
            result = stackA.getDamage() == stackB.getDamage();
        }

        if (result && options.contains(MatchOption.NBT)) {

            final CompoundNBT nbtA = stackA.getTag();
            final CompoundNBT nbtB = stackB.getTag();

            result = (nbtA == nbtB) || (null != nbtA && null != nbtB && NBTUtil.areNBTEquals(nbtA, nbtB, true));
        }

        if (result && options.contains(MatchOption.Capabilities)) {
            result = stackA.areCapsCompatible(stackB);
        }

        if (result && options.contains(MatchOption.Tags)) {
            result = ItemStack.areItemStackTagsEqual(stackA, stackB);
        }

        return result;
    }

    /**
     * Create a stack of size 1 for the given IItemProvider
     *
     * @param provider the item provider
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final IItemProvider provider) {
        return ItemHelper.stackFrom(provider, 1, null);
    }

    /**
     * Create a stack of size 1 from the given IItemProvider supplier
     *
     * @param supplier a supplier of an item provider
     * @return the newly create stack or an empty stack if the supplier return null
     */
    public static <T extends IItemProvider> ItemStack stackFrom(final Supplier<T> supplier) {
        return ItemHelper.stackFrom(supplier, 1, null);
    }

    /**
     * Create a stack for the given IItemProvider
     *
     * @param provider the item provider
     * @param amount the number of items to put into the stack
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final IItemProvider provider, final int amount) {
        return ItemHelper.stackFrom(provider, amount, null);
    }

    /**
     * Create a stack from the given IItemProvider supplier
     *
     * @param supplier a supplier of an item provider
     * @param amount the number of items to put into the stack
     * @return the newly create stack or an empty stack if the supplier return null
     */
    public static <T extends IItemProvider> ItemStack stackFrom(final Supplier<T> supplier, final int amount) {
        return ItemHelper.stackFrom(supplier, amount, null);
    }

    /**
     * Create a stack for the given item
     *
     * @param provider the item provider
     * @param amount the number of items to put into the stack
     * @param damage the stack damage value
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final IItemProvider provider, final int amount, final int damage) {

        final ItemStack stack = ItemHelper.stackFrom(provider, amount);

        stack.setDamage(damage);
        return stack;
    }

    /**
     * Create a stack from the given IItemProvider supplier
     *
     * @param supplier a supplier of an item provider
     * @param amount the number of items to put into the stack
     * @param damage the stack damage value
     * @return the newly create stack or an empty stack if the supplier return null
     */
    public static <T extends IItemProvider> ItemStack stackFrom(final Supplier<T> supplier, final int amount, final int damage) {

        final ItemStack stack = ItemHelper.stackFrom(supplier, amount);

        if (!stack.isEmpty()) {
            stack.setDamage(damage);
        }

        return stack;
    }

    /**
     * Create a stack for the given item
     *
     * @param provider the item provider
     * @param amount the number of items to put into the stack
     * @param nbt the capabilities data to be associated with the stack
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final IItemProvider provider, final int amount, @Nullable final CompoundNBT nbt) {
        return new ItemStack(provider, amount, nbt);
    }

    /**
     * Create a stack for the given item
     *
     * @param supplier a supplier of an item provider
     * @param amount the number of items to put into the stack
     * @param nbt the capabilities data to be associated with the stack
     * @return the newly create stack
     */
    public static <T extends IItemProvider> ItemStack stackFrom(final Supplier<T> supplier, final int amount, @Nullable final CompoundNBT nbt) {

        final IItemProvider provider = supplier.get();

        return null != provider ? new ItemStack(provider, amount, nbt) : ItemHelper.stackEmpty();
    }

    /**
     * Create a stack for the item associated with the give block state
     *
     * @param state the source block state
     * @param amount the number of items to put into the stack
     * @return a newly create stack containing the specified amount of items
     */
    public static ItemStack stackFrom(final BlockState state, final int amount) {
        return ItemHelper.stackFrom(state.getBlock(), amount, null);
    }

    /**
     * Create a stack from the given NBT data
     *
     * @param nbt an NBT Tag Compound containing the data of the stack to create
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final CompoundNBT nbt) {
        return ItemStack.read(nbt);
    }

    /**
     * Create a copy of the given stack
     *
     * @param stack the stack to duplicate
     * @return a new stack with the same properties as the one passed in
     */
    public static ItemStack stackFrom(final ItemStack stack) {
        return stack.copy();
    }

    /**
     * Create a copy of the given stack and modify it's size
     *
     * @param stack the stack to duplicate
     * @param amount the new size of the stack
     * @return a new stack with the same properties as the one passed in
     */
    public static ItemStack stackFrom(final ItemStack stack, final int amount) {

        final ItemStack newStack = ItemHelper.stackFrom(stack);

        if (newStack.isEmpty()) {
            return ItemHelper.stackEmpty();
        }

        ItemHelper.stackSetSize(newStack, amount);
        return newStack;
    }

//    /**
//     * Check if the given stack is empty
//     *
//     * @param stack the stack to query
//     * @return true if the stack is empty, false otherwise
//     */
//    @Deprecated
//    public static boolean stackIsEmpty(final ItemStack stack) {
//        return stack.isEmpty();
//    }

//    /**
//     * Check if the given stack is NOT empty
//     *
//     * @param stack the stack to query
//     * @return true if the stack is NOT empty, false otherwise
//     */
//    public static boolean stackIsNotEmpty(final ItemStack stack) {
//        return !stack.isEmpty();
//    }

//    /**
//     * Get the number of items inside a stack
//     *
//     * @param stack the stack to query
//     * @return the number of items inside the stack
//     */
//    public static int stackGetSize(final ItemStack stack) {
//        return stack.getCount();
//    }

    /**
     * Set the number of items inside a stack
     *
     * @param stack the stack to query
     * @return the modified stack or an empty stack
     */
    @SuppressWarnings("UnusedReturnValue")
    public static ItemStack stackSetSize(final ItemStack stack, final int amount) {

        stack.setCount(Math.max(amount, 0));
        return stack;
    }

//    /**
//     * Modify the number of items inside a stack by the given amount
//     *
//     * @param stack the stack to modify
//     * @param amount the number of items to add or subtract from the stack
//     * @return the modified stack or an empty stack
//     */
//    @SuppressWarnings("UnusedReturnValue")
//    public static ItemStack stackAdd(final ItemStack stack, final int amount) {
//
//        stack.grow(amount);
//        return stack;
//    }

    /**
     * Set a stack as empty, removing all items from it
     *
     * @param stack the stack to empty
     * @return the modified empty stack
     */
    public static ItemStack stackEmpty(final ItemStack stack) {

        stack.setCount(0);
        return stack;
    }

    /**
     * Return an empty stack
     * @return an empty stack
     */
    public static ItemStack stackEmpty() {
        return ItemStack.EMPTY;
    }

//    /**
//     * Return the damage value of the item
//     * @param stack the stack to query
//     * @return      the damage value
//     */
//    public static int stackGetDamage(final ItemStack stack)  {
//        return stack.getDamage();
//    }

//    /**
//     * Set the damage value of the item
//     * @param stack the stack to modify
//     * @param damage the new damage value
//     */
//    public static void stackSetDamage(final ItemStack stack, final int damage)  {
//        stack.setDamage(damage);
//    }

//    /**
//     * Return if the item is damaged
//     * @param stack the stack to query
//     */
//    public static boolean stackIsDamaged(final ItemStack stack)  {
//        return stack.isDamaged();
//    }


    //TODO docs

    public static Optional<CompoundNBT> stackGetTag(final ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }

    public static boolean stackHasData(final ItemStack stack, final String key) {

        Preconditions.checkArgument(!key.isEmpty(), "'key' must not be empty");

        return ItemHelper.stackGetTag(stack).map(tag -> tag.contains(key)).orElse(false);
    }

    public static Optional<INBT> stackGetData(ItemStack stack, String key) {
        return ItemHelper.stackGetTag(stack).map(tag -> tag.get(key));
    }

    public static void stackSetData(ItemStack stack, String key, INBT value) {

        Preconditions.checkArgument(!key.isEmpty(), "'key' must not be empty");

        CodeHelper.optionalIfPresentOrElse(ItemHelper.stackGetTag(stack),
                tag -> tag.put(key, value),
                () -> {

                    final CompoundNBT newTag = new CompoundNBT();

                    newTag.put(key, value);
                    stack.setTag(newTag);
                });
    }

    public static ItemStack removeStackFromSlot(final IItemHandlerModifiable inventory, final int slot) {

        final ItemStack stack = inventory.getStackInSlot(slot);

        inventory.setStackInSlot(slot, ItemHelper.stackEmpty());
        return stack;
    }

    public static void inventoryDropItems(final IItemHandlerModifiable inventory, final World world,
                                          final BlockPos position/*, final boolean withMomentum*/) {

        final double x = position.getX(), y = position.getY(), z = position.getZ();
//        final int slotsCount = inventory.getSlots();
//
//        for (int slot = 0; slot < slotsCount; ++slot) {
//
//            final ItemStack stack = inventory.getStackInSlot(slot);
//
//            if (!stack.isEmpty()) {
//
//                inventory.setStackInSlot(slot, ItemHelper.stackEmpty());
//                WorldHelper.spawnItemStack(stack, world, x, y, z, withMomentum);
//            }
//        }

        IntStream.range(0, inventory.getSlots())
                .mapToObj(slot -> ItemHelper.removeStackFromSlot(inventory, slot))
                .filter(stack -> !stack.isEmpty())
                //.forEach(stack -> WorldHelper.spawnItemStack(stack, world, x, y, z, withMomentum));
                .forEach(stack -> InventoryHelper.spawnItemStack(world, x, y, z, stack));
    }

    //region internals

    private ItemHelper() {
    }

    //endregion
}

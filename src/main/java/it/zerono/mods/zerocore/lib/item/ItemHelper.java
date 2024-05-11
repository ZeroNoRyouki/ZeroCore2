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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyHandler;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SuppressWarnings({"WeakerAccess"})
public final class ItemHelper {

    public static final String INVENTORY = "inventory";
    public static final IItemHandlerModifiable EMPTY_ITEM_HANDLER = (IItemHandlerModifiable)EmptyHandler.INSTANCE;

    public static ResourceLocation getItemId(final ItemLike item) {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.asItem()));
    }

    public static ResourceLocation getItemId(final ItemStack stack) {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static MutableComponent getItemName(final Item item) {
        return Component.translatable(item.getDescriptionId());
    }

    public static MutableComponent getItemName(final ItemStack stack) {
        return Component.translatable(stack.getDescriptionId());
    }

    @Nullable
    public static Item getItemFrom(final String id) {
        return getItemFrom(new ResourceLocation(id));
    }

    public static Item getItemFromOrAir(final String id) {
        return getItemFromOrAir(new ResourceLocation(id));
    }

    @Nullable
    public static Item getItemFrom(final ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }

    public static Item getItemFromOrAir(final ResourceLocation id) {
        return BuiltInRegistries.ITEM.containsKey(id) ? Objects.requireNonNull(BuiltInRegistries.ITEM.get(id)) : Items.AIR;
    }

    public enum MatchOption {

        Item,
        Size,
        Damage,
        NBT,
        ;

        public static final EnumSet<MatchOption> MATCH_ALWAYS = EnumSet.noneOf(MatchOption.class);
        public static final EnumSet<MatchOption> MATCH_ALL = EnumSet.of(Item, Size, Damage, NBT);
        public static final EnumSet<MatchOption> MATCH_ITEM = EnumSet.of(Item);
        public static final EnumSet<MatchOption> MATCH_ITEM_SIZE = EnumSet.of(Item, Size);
        public static final EnumSet<MatchOption> MATCH_ITEM_NBT = EnumSet.of(Item, NBT);
        public static final EnumSet<MatchOption> MATCH_ITEM_DAMAGE = EnumSet.of(Item, Damage);
        public static final EnumSet<MatchOption> MATCH_EXISTING_STACK = EnumSet.of(Item, Damage, NBT);
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
            result = stackA.getDamageValue() == stackB.getDamageValue();
        }

        if (result && options.contains(MatchOption.NBT)) {
            result = Objects.equals(stackA.getTag(), stackB.getTag());
        }

        return result;
    }

    /**
     * Create a stack of size 1 for the given IItemProvider
     *
     * @param provider the item provider
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final ItemLike provider) {
        return ItemHelper.stackFrom(provider, 1, null);
    }

    /**
     * Create a stack of size 1 from the given IItemProvider supplier
     *
     * @param supplier a supplier of an item provider
     * @return the newly create stack or an empty stack if the supplier return null
     */
    public static <T extends ItemLike> ItemStack stackFrom(final Supplier<T> supplier) {
        return ItemHelper.stackFrom(supplier, 1, null);
    }

    /**
     * Create a stack for the given IItemProvider
     *
     * @param provider the item provider
     * @param amount the number of items to put into the stack
     * @return the newly create stack
     */
    public static ItemStack stackFrom(final ItemLike provider, final int amount) {
        return ItemHelper.stackFrom(provider, amount, null);
    }

    /**
     * Create a stack from the given IItemProvider supplier
     *
     * @param supplier a supplier of an item provider
     * @param amount the number of items to put into the stack
     * @return the newly create stack or an empty stack if the supplier return null
     */
    public static <T extends ItemLike> ItemStack stackFrom(final Supplier<T> supplier, final int amount) {
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
    public static ItemStack stackFrom(final ItemLike provider, final int amount, final int damage) {

        final ItemStack stack = ItemHelper.stackFrom(provider, amount);

        stack.setDamageValue(damage);
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
    public static <T extends ItemLike> ItemStack stackFrom(final Supplier<T> supplier, final int amount, final int damage) {

        final ItemStack stack = ItemHelper.stackFrom(supplier, amount);

        if (!stack.isEmpty()) {
            stack.setDamageValue(damage);
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
    public static ItemStack stackFrom(final ItemLike provider, final int amount, @Nullable final CompoundTag nbt) {
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
    public static <T extends ItemLike> ItemStack stackFrom(final Supplier<T> supplier, final int amount, @Nullable final CompoundTag nbt) {

        final ItemLike provider = supplier.get();

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
    public static ItemStack stackFrom(final CompoundTag nbt) {
        return ItemStack.of(nbt);
    }

    /**
     * Serialize a stack to NBT
     *
     * @param stack the stack to serialize
     * @return the serialized NBT data
     */
    public static CompoundTag stackToNBT(final ItemStack stack) {
        return stack.save(new CompoundTag());
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
    @Deprecated
    public static ItemStack stackEmpty() {
        return ItemStack.EMPTY;
    }

    public static Optional<CompoundTag> stackGetTag(final ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }

    public static boolean stackHasData(final ItemStack stack, final String key) {

        Preconditions.checkArgument(!key.isEmpty(), "'key' must not be empty");

        return ItemHelper.stackGetTag(stack).map(tag -> tag.contains(key)).orElse(false);
    }

    public static Optional<Tag> stackGetData(ItemStack stack, String key) {
        return ItemHelper.stackGetTag(stack).map(tag -> tag.get(key));
    }

    public static void stackSetData(ItemStack stack, String key, Tag value) {

        Preconditions.checkArgument(!key.isEmpty(), "'key' must not be empty");

        CodeHelper.optionalIfPresentOrElse(ItemHelper.stackGetTag(stack),
                tag -> tag.put(key, value),
                () -> {

                    final CompoundTag newTag = new CompoundTag();

                    newTag.put(key, value);
                    stack.setTag(newTag);
                });
    }

    public static ItemStack removeStackFromSlot(final IItemHandlerModifiable inventory, final int slot) {

        final ItemStack stack = inventory.getStackInSlot(slot);

        inventory.setStackInSlot(slot, ItemHelper.stackEmpty());
        return stack;
    }

    public static void inventoryDropItems(final IItemHandlerModifiable inventory, final Level world,
                                          final BlockPos position/*, final boolean withMomentum*/) {

        final double x = position.getX(), y = position.getY(), z = position.getZ();

        IntStream.range(0, inventory.getSlots())
                .mapToObj(slot -> ItemHelper.removeStackFromSlot(inventory, slot))
                .filter(stack -> !stack.isEmpty())
                .forEach(stack -> Containers.dropItemStack(world, x, y, z, stack));
    }

    //region internals

    private ItemHelper() {
    }

    //endregion
}

/*
 *
 * IndexedStackContainer.java
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

import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.data.nbt.IMergeableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.LogicalSide;

import java.util.List;
import java.util.Optional;
import java.util.function.*;

@SuppressWarnings({"WeakerAccess"})
public class IndexedStackContainer<Index extends Enum<Index>, Content, Stack>
        implements ISyncableEntity, IMergeableEntity, IDebuggable {

    @SafeVarargs
    public IndexedStackContainer(int capacity, IStackAdapter<Stack, Content> stackAdapter, Index firstValidIndex,
                                 Index secondValidIndex, Index... otherValidIndices) {
        this(capacity, false, 60, stackAdapter, firstValidIndex, secondValidIndex, otherValidIndices);
    }

    @SafeVarargs
    public IndexedStackContainer(int capacity, boolean capacityIsShared, int minimumTicksBetweenUpdates,
                                 IStackAdapter<Stack, Content> stackAdapter, Index firstValidIndex,
                                 Index secondValidIndex, Index... otherValidIndices) {

        this._capacity = capacity;
        this._sharedCapacity = capacityIsShared;
        this._minimumTicksBetweenUpdates = minimumTicksBetweenUpdates;
        this._adapter = stackAdapter;
        this._stacks = new EnumIndexedArray<>(this.getStackAdapter()::createArray, firstValidIndex, secondValidIndex,
                otherValidIndices);
        this._lastSeenLevels = new EnumIndexedArray<>(Integer[]::new, firstValidIndex, secondValidIndex,
                otherValidIndices);

        final Stack empty = this.getStackAdapter().getEmptyStack();

        this.getValidIndexes().forEach(type -> {

            this.setStack(type, empty);
            this.setLastSeenLevel(type, FORCE_UPDATE);
        });
    }

    /**
     * Get the capacity of the container
     *
     * @return the capacity of the container
     */
    public int getCapacity() {
        return this._capacity;
    }

    /**
     * Set the capacity of the container.
     * If the content amount exceed the new capacity, the excess will be voided
     *
     * @param capacity the new capacity of the container
     */
    public void setCapacity(int capacity) {

        this._capacity = capacity;
        this.clampContentsToCapacity();
    }

    /**
     * Get if the content capacity is shared between each stacks
     *
     * @return true if the capacity is shared, false otherwise
     */
    public boolean isCapacityShared() {
        return this._sharedCapacity;
    }

    /**
     * Get the total amount of content across all the contained stacks
     *
     * @return the total amount of content
     */
    public int getTotalAmount() {
        return this.getValidIndexes().stream()
                .mapToInt(type -> this.getStackAdapter().getAmount(this.getStack(type)))
                .sum();
    }

    /**
     * Compute the free space available for the given index.
     * If the container capacity is shared between all the indexes, the content amount of all the indexes will be used in the computation.
     * If the container capacity is not shared, only the content amount of the specified index will be used instead
     *
     * @param index the index used by the operation
     * @return the amount of free space in the container
     */
    public int getFreeSpace(Index index) {
        return this.getCapacity() - (this.isCapacityShared() ? this.getTotalAmount() : this.getContentAmount(index));
    }

    /**
     * Get a COPY of the stack at the specified index
     *
     * @param index the index used by the operation
     * @return a COPY of the stack at the specified index or an empty stack if there is no stack there
     */
    public Stack getStackCopy(Index index) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Stack currentStack = this.getStack(index);

        return adapter.isEmpty(currentStack) ? adapter.getEmptyStack() : adapter.create(currentStack);
    }

    /**
     * Get the content of the requested stack
     *
     * @param index the index used by the operation
     * @return the the content of the stack or null if the stack is empty
     */
    public Optional<Content> getContent(Index index) {
        return this.getStackAdapter().getContent(this.getStack(index));
    }

    /**
     * Get the amount of content in the requested stack
     *
     * @param index the index used by the operation
     * @return the the amount of content in the stack or null if the stack is empty
     */
    public int getContentAmount(Index index) {
        return this.getStackAdapter().getAmount(this.getStack(index));
    }

    public <T> T map(final Index type, final Function<Content, T> mapper, final T defaultValue) {
        return this._stacks.map(type, stack -> this.getStackAdapter().map(stack, mapper, defaultValue), defaultValue);
    }

    public <T> T map(final Index type, final IntFunction<T> mapper, final T defaultValue) {
        return this._stacks.map(type, stack -> this.getStackAdapter().map(stack, mapper, defaultValue), defaultValue);
    }

    public <T> T map(final Index type, final BiFunction<Content, Integer, T> mapper, final T defaultValue) {
        return this._stacks.map(type, stack -> this.getStackAdapter().map(stack, mapper, defaultValue), defaultValue);
    }

    public void accept(final Index type, final Consumer<Content> consumer) {
        this._stacks.accept(type, stack -> this.getStackAdapter().accept(stack, consumer));
    }

    public void accept(final Index type, final IntConsumer consumer) {
        this._stacks.accept(type, stack -> this.getStackAdapter().accept(stack, consumer));
    }

    public void accept(final Index type, final BiConsumer<Content, Integer> consumer) {
        this._stacks.accept(type, stack -> this.getStackAdapter().accept(stack, consumer));
    }

    /**
     * Evalutate if stacks were changed enough that an update should be sent to the client
     *
     * @return true if an update if needed, false otherwise
     */
    public boolean shouldUpdate() {

        ++this._ticksSinceLastUpdate;

        if (this._minimumTicksBetweenUpdates < this._ticksSinceLastUpdate) {

            final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

            int deviance = 0;
            boolean shouldUpdate = false;

            for (Index index : this.getValidIndexes()) {

                final Stack stack = this.getStack(index);
                final int lastLevel = this.getLastSeenLevel(index);

                if (adapter.isEmpty(stack) && lastLevel > 0) {

                    shouldUpdate = true;

                } else if (!adapter.isEmpty(stack)) {

                    if (FORCE_UPDATE == lastLevel) {
                        shouldUpdate = true;
                    } else {
                        deviance += Math.abs(adapter.getAmount(stack) - lastLevel);
                    }
                }
                // else, both levels are zero, no-op

                shouldUpdate = this.evaluateUpdate(deviance, shouldUpdate);

                if (shouldUpdate) {
                    break;
                }
            }

            if (shouldUpdate) {
                this.updateLastSeenLevels();
            }

            this._ticksSinceLastUpdate = 0;
            return shouldUpdate;
        }

        return false;
    }

    /**
     * Override this to get the last word on update evaluation
     *
     * @param currentDeviance the deviance between the current content amount and the amount at the last check
     * @param currentUpdateValuation the proposed evaluation
     * @return true if an update if needed, false otherwise
     */
    protected boolean evaluateUpdate(int currentDeviance, boolean currentUpdateValuation) {
        return currentUpdateValuation;
    }

    /**
     * Override this to get notified when an insert operation is completed on the given index
     *
     * @param index the index target of the insert operation
     * @param wasEmpty true if the index was empty before the insert operation
     */
    protected void onInsert(Index index, boolean wasEmpty) {
    }

    /**
     * Override this to get notified when an extract operation is completed on the given index
     *
     * @param index the index target of the extract operation
     * @param isEmptyNow true if the index has become empty after the extract operation
     */
    protected void onExtract(Index index, boolean isEmptyNow) {
    }

    /**
     * Check if insertions can be performed on the given index
     *
     * @param index the index used by the operation
     * @return true if insertions are allowed, false otherwise
     */
    public boolean canInsert(Index index) {
        return true;
    }

    /**
     * Check if extractions can be performed on the given index
     *
     * @param index the index used by the operation
     * @return true if extractions are allowed, false otherwise
     */
    public boolean canExtract(Index index) {
        return true;
    }

    /**
     * Check if the provided content can be added to (or subtracted from) the stack in the specified index.
     * If the index is empty, the result of {@code isContentValidForIndex} will be returned.
     *
     * @param index the index used by the operation
     * @param content the content to evaluate
     * @return true if the content is assignable, false otherwise
     */
    public boolean isContentCompatible(Index index, Content content) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Optional<Content> currentContent = adapter.getContent(this.getStack(index));

        return currentContent.map(c -> adapter.isContentEqual(c, content))
                .orElseGet(() -> this.isContentValidForIndex(index, content));
    }

    /**
     * Check if the provided stack can be added to (or subtracted from) the stack in the specified index.
     * If the index is empty, the result of {@code isStackValidForIndex} will be returned.
     *
     * @param index the index used by the operation
     * @param stack the stack to evaluate
     * @return true if the stack is assignable, false otherwise
     */
    public boolean isStackCompatible(Index index, Stack stack) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Stack currentStack = this.getStack(index);

        if (!adapter.isEmpty(currentStack)) {
            return adapter.isStackContentEqual(currentStack, stack);
        } else {
            return this.isStackValidForIndex(index, stack);
        }
    }

    /**
     * Check if the provided stack can be stored in the specified index
     *
     * @param index the index used by the operation
     * @param stack the stack to evaluate
     * @return true if the stack can be stored in the index, false otherwise
     */
    public boolean isStackValidForIndex(Index index, Stack stack) {
        return true;
    }

    /**
     * Check if the provided content can be stored (in a stack) in the specified index
     *
     * @param index the index used by the operation
     * @param content the content to evaluate
     * @return true if the content can be stored in the index, false otherwise
     */
    public boolean isContentValidForIndex(Index index, Content content) {
        return true;
    }

    /**
     * Add the provided stack to the stack in the specified index.
     * If the index is empty, a copy of the stack will be stored in the index.
     * If the index is not empty, the content of the provided stack will be added to the stack in the index.
     * If the operation cannot be performed, zero will be returned
     *
     * @param index the index used by the operation
     * @param stack the content to add to the index
     * @param mode how this operation should be handled
     * @return the amount of content added to the index
     */
    public int insert(Index index, Stack stack, OperationMode mode) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        if (this.canInsert(index) && !adapter.isEmpty(stack) && this.isStackCompatible(index, stack)) {

            final int amountToAdd = Math.min(adapter.getAmount(stack), this.getFreeSpace(index));

            if (amountToAdd > 0) {
                return this.insert(index, mode, adapter.create(stack), amountToAdd);
            }
        }

        return 0;
    }

    /**
     * Add the provided content in the given amount to the stack in the specified index.
     * If the index is empty, a new stack will be stored in the index.
     * If the index is not empty, the content will be added to the stack in the index.
     * If the operation cannot be performed, zero will be returned
     *
     * @param index the index used by the operation
     * @param content the content to add to the index
     * @param amount the amount of content to be added to the index
     * @param mode how this operation should be handled
     * @return the amount of content added to the index
     */
    public int insert(Index index, Content content, int amount, OperationMode mode) {

        if (this.canInsert(index) && amount > 0 && this.isContentCompatible(index, content)) {

            final int amountToAdd = Math.min(amount, this.getFreeSpace(index));

            if (amountToAdd > 0) {
                return this.insert(index, mode, this.getStackAdapter().create(content, amountToAdd), amountToAdd);
            }
        }

        return 0;
    }

    /**
     * Add the provided amount of the current content to the stack in the specified index.
     * If the index is empty, a new stack will be stored in the index.
     * If the index is not empty, the content will be added to the stack in the index.
     * If the operation cannot be performed, zero will be returned
     *
     * @param index the index used by the operation
     * @param amount the amount of content to be added to the index
     * @param mode how this operation should be handled
     * @return the amount of content added to the index
     */
    public int insert(Index index, int amount, OperationMode mode) {
        return this.getContent(index).map(content -> this.insert(index, content, amount, mode)).orElse(0);
    }

    /**
     * Remove the provided stack from the stack in the specified index.
     * If the index is empty, no operation is performed and an empty stack is returned.
     * If the index is not empty, the content of the provided stack will be removed from the stack in the index and returned as a new stack.
     * If the operation cannot be performed, an empty stack will be returned
     *
     * @param index the index used by the operation
     * @param stack the content to remove from the index
     * @param mode how this operation should be handled
     * @return a stack containing the removed content or an empty stack if the index is empty
     */
    public Stack extract(Index index, Stack stack, OperationMode mode) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        if (this.canExtract(index) && !adapter.isEmpty(stack) && this.isStackCompatible(index, stack)) {
            return this.extract(index, mode, adapter.create(stack));
        }

        return adapter.getEmptyStack();
    }

    /**
     * Remove the provided content from the stack in the specified index.
     * If the index is empty, no operation is performed and an empty stack is returned.
     * If the index is not empty, the content will be removed from the stack in the index and returned as a new stack.
     * If the operation cannot be performed, an empty stack will be returned
     *
     * @param index the index used by the operation
     * @param content the content to remove from the index
     * @param amount the amount of content to be removed from the index
     * @param mode how this operation should be handled
     * @return a stack containing the removed content or an empty stack if the index is empty
     */
    public Stack extract(Index index, Content content, int amount, OperationMode mode) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        if (this.canExtract(index) && amount > 0 && this.isContentCompatible(index, content)) {
            return this.extract(index, mode, adapter.create(content, amount));
        }

        return adapter.getEmptyStack();
    }

    /**
     * Remove the provided amount of the current content from the stack in the specified index.
     * If the index is empty, no operation is performed and an empty stack is returned.
     * If the index is not empty, the content will be removed from the stack in the index and returned as a new stack.
     * If the operation cannot be performed, an empty stack will be returned
     *
     * @param index the index used by the operation
     * @param amount the amount of content to be removed from the index
     * @param mode how this operation should be handled
     * @return a stack containing the removed content or an empty stack if the index is empty
     */
    public Stack extract(Index index, int amount, OperationMode mode) {
        return this.getContent(index).map(content -> this.extract(index, content, amount, mode))
                .orElse(this.getStackAdapter().getEmptyStack());
    }

    /**
     * Remove the stack in the given index and return it.
     * If the index is empty, an empty stack will be returned
     *
     * @param index the index used by the operation
     * @return the stack in the requested index, or an empty stack if the index is empty
     */
    public Stack clear(Index index) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Stack currentStack = this.getStack(index);

        if (this.canExtract(index) && !adapter.isEmpty(currentStack)) {

            this.setStack(index, adapter.getEmptyStack());
            this.onExtract(index, true);
            return currentStack;
        }

        return adapter.getEmptyStack();
    }

    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        for (Index index : this.getValidIndexes()) {

            final Stack stack;

            if (data.contains(index.name())) {

                stack = adapter.readFrom(data.getCompound(index.name()));

            } else {

                stack = adapter.getEmptyStack();
                Log.LOGGER.debug(Log.CORE, "{} data not found while loading a stack container from NBT data", index);
            }

            this.setStack(index, stack);
            this.setLastSeenLevel(index, FORCE_UPDATE);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        this.getValidIndexes().forEach(index -> data.put(index.name(), adapter.writeTo(this.getStack(index), new CompoundTag())));
        return data;
    }

    //endregion
    //region IMergeableEntity

    /**
     * Sync the entity data from another IMergeableEntity
     *
     * @param other the IMergeableEntity to sync from
     */
    @Override
    @SuppressWarnings("unchecked")
    public void syncDataFrom(IMergeableEntity other) {

        if (other instanceof IndexedStackContainer) {

            final IndexedStackContainer<Index, Content, Stack> otherContainer = (IndexedStackContainer<Index, Content, Stack>)other;

            if (otherContainer.getCapacity() > this.getCapacity()) {

                this.setCapacity(otherContainer.getCapacity());
                this.getValidIndexes().forEach(index -> this.setStack(index, otherContainer.getStack(index)));
            }
        }
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        messages.addUnlocalized("Container capacity: %1$s",
                CodeHelper.formatAsHumanReadableNumber(this.getCapacity() / 1000.0, "B"));

        this.getValidIndexes().forEach(index -> {

            final Stack stack = this.getStack(index);

            messages.addUnlocalized("[%1$s] %2$s: %3$d", index,
                    adapter.isEmpty(stack) ? "<EMPTY>" :
                            adapter.getContent(stack)
                                    .map(Object::toString)
                                    .orElse("<EMPTY STACK>"),
                    adapter.getAmount(stack));
        });
    }

    //endregion
    //region Object

    @Override
    public String toString() {

        return "Capacity: " + this._capacity + ", Shared: " + this._sharedCapacity + "\n" +
                "Stacks: " + this._stacks;
    }

    //endregion
    //region internals

    /**
     * Get the {@link IStackAdapter} for the stack in use
     * @return the {@link IStackAdapter} in use
     */
    protected IStackAdapter<Stack, Content> getStackAdapter() {
        return this._adapter;
    }

    /**
     * Get a {@link List} containing the valid indexes for the tracked stacks
     * @return the valid indexes
     */
    protected List<Index> getValidIndexes() {
        return this._stacks.getValidIndices();
    }

    /**
     * Get the stack at the specified index
     *
     * @param index the index used by the operation
     * @return the stack at the specified index or an EMPTY stack if there is no stack there
     */
    protected Stack getStack(Index index) {
        return this._stacks.getElement(index).orElse(this.getStackAdapter().getEmptyStack());
    }

    /**
     * Store the provided stack at the specified index
     *
     * @param index the index used by the operation
     * @param stack the stack to store
     */
    protected void setStack(Index index, Stack stack) {
        this._stacks.setElement(index, stack);
    }

    /**
     * Return the amount of content in the specified index at the time of the last update
     *
     * @param index the index used by the operation
     * @return the last amount level detected
     */
    protected int getLastSeenLevel(Index index) {
        return this._lastSeenLevels.getElement(index).orElse(0);
    }

    /**
     * Set the last seen level for the specified index
     *
     * @param index the index used by the operation
     * @param level the last seen level for the index
     */
    protected void setLastSeenLevel(Index index, int level) {
        this._lastSeenLevels.setElement(index, level);
    }

    /**
     * Update the last seen levels for the stacks
     */
    protected void updateLastSeenLevels() {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        this.getValidIndexes().forEach(type -> {

            final Stack stack = this.getStack(type);

            this.setLastSeenLevel(type, adapter.isEmpty(stack) ? 0 : adapter.getAmount(stack));
        });
    }

    /**
     * Remove any excess from the stacks, usually after a capacity change
     */
    protected void clampContentsToCapacity() {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final int capacity = this.getCapacity();

        if (this.isCapacityShared()) {

            if (this.getTotalAmount() > capacity) {

                int excess = this.getTotalAmount() - capacity;

                for (Index index : this.getValidIndexes()) {

                    excess = this.reduceStackExcess(index, excess);

                    if (excess <= 0) {
                        break;
                    }
                }
            }

        } else {

            this.getValidIndexes().forEach(index -> {

                final Stack stack = this.getStack(index);

                if (!adapter.isEmpty(stack)) {
                    adapter.setAmount(stack, Math.min(capacity, adapter.getAmount(stack)));
                }
            });
        }
    }

    /**
     * Reduce the excess in the stack at the specified index
     *
     * @param index the index used by the operation
     * @param excess the amount of content to be removed
     * @return the amount removed
     */
    private int reduceStackExcess(Index index, int excess) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Stack stack = this.getStack(index);

        if (!adapter.isEmpty(stack)) {

            final int amount = adapter.getAmount(stack);

            if (excess > amount) {

                excess -= amount;
                this.setStack(index, adapter.getEmptyStack());

            } else {

                adapter.modifyAmount(stack, - excess);
                excess = 0;
            }
        }

        return excess;
    }

    /**
     * Internal insert handler
     *
     * THIS CODE ASSUME THAT "stackCopy" IS A COPY OF THE ORIGINAL STACK
     *
     * @param index the index used by the operation
     * @param mode how this operation should be handled
     * @param stackCopy a copy of the original stack to insert
     * @param amountToAdd amount of content to insert
     * @return the amount of content inserted
     */
    protected int insert(Index index, OperationMode mode, Stack stackCopy, int amountToAdd) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();

        if (mode.execute()) {

            final Stack currentStack = this.getStack(index);

            if (adapter.isEmpty(currentStack)) {

                adapter.setAmount(stackCopy, amountToAdd);
                this.setStack(index, stackCopy);
                this.onInsert(index, true);

            } else {

                adapter.modifyAmount(currentStack, amountToAdd);
                this.onInsert(index, false);
            }
        }

        return amountToAdd;
    }

    /**
     * Internal extract handler
     *
     * THIS CODE ASSUME THAT "stackCopy" IS A COPY OF THE ORIGINAL STACK
     *
     * @param index the index used by the operation
     * @param mode how this operation should be handled
     * @param stackCopy a copy of the original stack to extract
     * @return a stack containing the removed content or an empty stack if the index is empty
     */
    protected Stack extract(Index index, OperationMode mode, Stack stackCopy) {

        final IStackAdapter<Stack, Content> adapter = this.getStackAdapter();
        final Stack currentStack = this.getStack(index);

        if (!adapter.isEmpty(currentStack)) {

            final int currentAmount = adapter.getAmount(currentStack);
            final int amountToRemove = Math.min(currentAmount, adapter.getAmount(stackCopy));

            if (mode.execute()) {

                if (currentAmount == amountToRemove) {

                    this.setStack(index, adapter.getEmptyStack());
                    this.onExtract(index, true);

                } else {

                    adapter.modifyAmount(currentStack, -amountToRemove);
                    this.onExtract(index, false);
                }
            }

            return adapter.setAmount(stackCopy, amountToRemove);
        }

        return adapter.getEmptyStack();
    }

    private static final int FORCE_UPDATE = -1000;

    private final IStackAdapter<Stack, Content> _adapter;
    private final EnumIndexedArray<Index, Stack> _stacks;
    private final EnumIndexedArray<Index, Integer> _lastSeenLevels;

    private int _capacity;
    private final boolean _sharedCapacity;
    private final int _minimumTicksBetweenUpdates;
    private int _ticksSinceLastUpdate;

    //endregion
}

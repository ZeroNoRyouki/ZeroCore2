/*
 *
 * IStackAdapter.java
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

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;

/**
 * A generic interface to manipulate stacks
 *
 * @param <StackType> the type of the stack
 * @param <ContentType> the type of the content of the stack
 */
public interface IStackAdapter<StackType, ContentType> {

    /**
     * Get the content of the provided stack, if any is available.
     * If the stack is empty, no content is returned
     *
     * @param stack the stack used by the operation
     * @return the {@link Optional} stack content
     */
    Optional<ContentType> getContent(StackType stack);

    /**
     * The amount of content in the provided stack
     *
     * @param stack the stack used by the operation
     * @return the amount of content in the provided stack or zero if the stack is empty
     */
    int getAmount(StackType stack);

    /**
     * Set the amount of content in the provided stack
     *
     * @param stack the stack used by the operation
     * @param amount the new content amount
     * @return the stack
     */
    StackType setAmount(StackType stack, int amount);

    /**
     * Grow or shrink the amount of content in the provided stack if the stack is not empty
     *
     * @param stack the stack used by the operation
     * @param delta this value will be added to the current amount of content
     * @return the stack
     */
    StackType modifyAmount(StackType stack, int delta);

    /**
     * Get an empty stack
     *
     * @return an empty stack
     */
    StackType getEmptyStack();

    /**
     * Check if the provided stack is empty, ie with no content defined and no amount of it
     *
     * @param stack the stack used by the operation
     * @return true if the stack is empty, false otherwise
     */
    boolean isEmpty(StackType stack);

    /**
     * Check if the provided stacks contains the same content (regardless of the amount)
     *
     * @param stack1 the first stack
     * @param stack2 the second stack
     * @return true if both stacks contains the same content (ie: two ItemStack that contains the same Item, even in different amounts)
     */
    boolean isStackContentEqual(StackType stack1, StackType stack2);

    /**
     * Check if the provided content are equals
     *
     * @param content1 the first content
     * @param content2 the second content
     * @return true if the contents are the same, false otherwise
     */
    boolean isContentEqual(ContentType content1, ContentType content2);

    /**
     * Check if the provided stacks are identical in every aspects
     *
     * @param stack1 the first stack
     * @param stack2 the second stack
     * @return true if the two stacks are identical in every aspect
     */
    boolean areIdentical(StackType stack1, StackType stack2);

    /**
     * Create a new stack with the given content and content amount
     *
     * @param content the content of the new stack
     * @param amount the amount of the content
     * @return the new stack
     */
    StackType create(ContentType content, int amount);

    /**
     * Create a duplicate of the given stack
     *
     * @param stack the stack used by the operation
     * @return the duplicate
     */
    StackType create(StackType stack);

    /**
     * Create an array of stacks
     *
     * @param length the length of the array
     * @return the new array
     */
    StackType[] createArray(int length);

    /**
     * Create a {@link List} of stacks
     *
     * @return the new {@link List}
     */
    List<StackType> createList();

    /**
     * Create a {@link Set} of stacks
     *
     * @return the new {@link Set}
     */
    Set<StackType> createSet();

    /**
     * Deserialize a stack from the provided NBT tag
     *
     * @param registries the registry data lookup
     * @param input the {@link Tag} that contains the data of the stack
     * @return the deserialized stack
     */
    StackType deserialize(HolderLookup.Provider registries, Tag input);

    /**
     * Serialize a stack in the provided {@link Tag}
     *
     * @param registries the registry data lookup
     * @param stack the stack used by the operation
     * @param output the {@link Tag} to save to stack data into
     * @return the serialized data
     */
    Tag serialize(HolderLookup.Provider registries, StackType stack, Tag output);

    /**
     * Get a textual representation of the provided stack
     *
     * @param stack the stack used by the operation
     * @return a String containing the textual representation of the stack
     */
    String toString(StackType stack);

    <T> T map(StackType stack, Function<ContentType, T> mapper, T defaultValue);

    <T> T map(StackType stack, IntFunction<T> mapper, T defaultValue);

    <T> T map(StackType stack, BiFunction<ContentType, Integer, T> mapper, T defaultValue);

    void accept(StackType stack, Consumer<ContentType> consumer);

    void accept(StackType stack, IntConsumer consumer);

    void accept(StackType stack, BiConsumer<ContentType, Integer> consumer);
}

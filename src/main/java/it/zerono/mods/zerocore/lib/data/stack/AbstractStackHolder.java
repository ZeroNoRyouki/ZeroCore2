/*
 *
 * AbstractStackHolder.java
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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.ObjIntConsumer;

public abstract class AbstractStackHolder<Holder extends AbstractStackHolder<Holder, Stack>, Stack>
        implements IStackHolder<Holder, Stack> {

    protected AbstractStackHolder() {
        this(AbstractStackHolder::defaultValidator);
    }

    protected AbstractStackHolder(final BiPredicate<Integer, Stack> stackValidator) {

        Preconditions.checkNotNull(stackValidator, "Stack validator must not be null");

        this._stackValidator = stackValidator;
        this._onChangeListener = (change, index) -> {};
        this._onLoadListener = CodeHelper.VOID_RUNNABLE;
        this._maxCapacityProvider = $ -> 0;
     }

    protected void onLoad() {
    this._onLoadListener.run();
    }

    protected void onChange(final ChangeType change, final int index) {
    this._onChangeListener.accept(change, index);
    }

    protected <StackType, ContentType> void syncFrom(CompoundTag data, HolderLookup.Provider registries,
                                                     IStackAdapter<StackType, ContentType> adapter,
                                                     Int2ObjectFunction<List<StackType>> itemsListSupplier) {

        final List<StackType> stacks = itemsListSupplier.get(data.getInt("Size"));
        final ListTag tagList = data.getList("Items", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {

            final CompoundTag itemTags = tagList.getCompound(i);
            final int slot = itemTags.getInt("Slot");
            final var stackTag = itemTags.get("Stack");

            if (null == stackTag) {
                continue;
            }

            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, adapter.deserialize(registries, stackTag));
            }
        }

        this.onLoad();
    }

    protected <StackType, ContentType> CompoundTag syncTo(CompoundTag data, HolderLookup.Provider registries,
                                                          List<StackType> items, IStackAdapter<StackType, ContentType> adapter) {

        final ListTag nbtTagList = new ListTag();

        for (int i = 0; i < items.size(); ++i) {

            if (!adapter.isEmpty(items.get(i))) {

                final CompoundTag itemTag = new CompoundTag();
                final var stackTag = adapter.serialize(registries, items.get(i));

                itemTag.putInt("Slot", i);
                itemTag.put("Stack", stackTag);
                nbtTagList.add(itemTag);
            }
        }

        data.put("Items", nbtTagList);
        data.putInt("Size", items.size());
        return data;
    }

    //region IStackHolder

    @Override
    public boolean isStackValid(final int index, final Stack stack) {
        return this._stackValidator.test(index, stack);
    }

    @Override
    public Holder setOnContentsChangedListener(final ObjIntConsumer<ChangeType> listener) {

        this._onChangeListener = Objects.requireNonNull(listener);
        //noinspection unchecked
        return (Holder)this;
    }

    @Override
    public Holder setOnLoadListener(Runnable listener) {

        this._onLoadListener = Objects.requireNonNull(listener);
        //noinspection unchecked
        return (Holder)this;
    }

    @Override
    public void setMaxCapacity(final Int2IntFunction maxCapacity) {
        this._maxCapacityProvider = Objects.requireNonNull(maxCapacity);
    }

    @Override
    public int getMaxCapacity(final int index) {
        return this._maxCapacityProvider.applyAsInt(index);
    }

    //endregion
    //region internals

    protected static <Stack> boolean defaultValidator(final Integer index, final Stack stack) {
        return true;
    }

    private final BiPredicate<Integer, Stack> _stackValidator;
    private ObjIntConsumer<ChangeType> _onChangeListener;
    private Runnable _onLoadListener;
    private Int2IntFunction _maxCapacityProvider;

    //endregion
}

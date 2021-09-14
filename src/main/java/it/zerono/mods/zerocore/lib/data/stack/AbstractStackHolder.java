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

import it.zerono.mods.zerocore.lib.CodeHelper;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.ObjIntConsumer;

public abstract class AbstractStackHolder<Holder extends AbstractStackHolder<Holder, Stack>, Stack>
        implements IStackHolder<Holder, Stack> {

    protected AbstractStackHolder() {
        this(AbstractStackHolder::defaultValidator);
    }

    protected AbstractStackHolder(final BiPredicate<Integer, Stack> stackValidator) {

        this._stackValidator = Objects.requireNonNull(stackValidator);
        this._onChangeListener = (change, index) -> {};
        this._onLoadListener = CodeHelper.VOID_RUNNABLE;
     }

     protected void onLoad() {
        this._onLoadListener.run();
     }

     protected void onChange(final ChangeType change, final int index) {
        this._onChangeListener.accept(change, index);
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

    //endregion
    //region internals

    protected static <Stack> boolean defaultValidator(final Integer index, final Stack stack) {
        return true;
    }

    private final BiPredicate<Integer, Stack> _stackValidator;
    private ObjIntConsumer<ChangeType> _onChangeListener;
    private Runnable _onLoadListener;

    //endregion
}

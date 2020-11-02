/*
 *
 * BindableTextComponent.java
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

package it.zerono.mods.zerocore.lib.client.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Consumer;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class BindableTextComponent<Value>
        implements NonNullSupplier<ITextComponent>, Consumer<Value> {

    public BindableTextComponent(final NonNullFunction<Value, ITextComponent> builder) {

        this._builder = builder;
        this._cachedText = null;
        this._cachedValue = null;
    }

    //region NonNullSupplier

    @Override
    public ITextComponent get() {

        if (null == this._cachedText) {
            this._cachedText = null != this._cachedValue ? this._builder.apply(this._cachedValue) : TEXT_EMPTY_LINE;
        }

        return this._cachedText;
    }

    //endregion
    //region Consumer

    @Override
    public void accept(Value value) {

        this._cachedValue = value;
        this._cachedText = null;
    }

    //endregion
    //region internals

    private final NonNullFunction<Value, ITextComponent> _builder;
    private ITextComponent _cachedText;
    private Value _cachedValue;

    //endregion
}

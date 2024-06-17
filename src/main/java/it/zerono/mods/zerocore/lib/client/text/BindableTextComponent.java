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

import it.zerono.mods.zerocore.lib.functional.ComponentSupplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class BindableTextComponent<Value>
        implements ComponentSupplier, Consumer<Value> {

    public BindableTextComponent(final Function<@NotNull Value, @NotNull Component> builder) {

        this._builder = builder;
        this._cachedText = null;
        this._cachedValue = null;
    }

    //region NonNullSupplier

    @Override
    public Component get() {

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

    private final Function<@NotNull Value, @NotNull Component> _builder;
    private Component _cachedText;
    private Value _cachedValue;

    //endregion
}

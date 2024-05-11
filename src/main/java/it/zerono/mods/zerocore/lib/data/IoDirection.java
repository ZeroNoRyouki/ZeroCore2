/*
 *
 * IoDirection.java
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

package it.zerono.mods.zerocore.lib.data;

import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.LogicalSide;

public enum IoDirection
        implements IDebuggable {

    Input,
    Output;

    public boolean isInput() {
        return this == Input;
    }

    public boolean isOutput() {
        return this == Output;
    }

    public IoDirection opposite() {
        return this.isInput() ? Output : Input;
    }

    public static IoDirection from(boolean isInput) {
        return isInput ? Input : Output;
    }

    //TODO check "data" owner; for write too
    public static IoDirection read(final CompoundTag data, final String key, final IoDirection defaultValue) {

        if (data.contains(key)) {

            final String value = data.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return IoDirection.valueOf(value);
            }
        }

        return defaultValue;
    }

    public static void write(final CompoundTag data, final String key, final IoDirection value) {
        data.putString(key, value.name());
    }

    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        messages.addUnlocalized("Direction: %1$s", this);
    }

    //endregion
}

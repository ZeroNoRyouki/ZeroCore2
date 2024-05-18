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
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.LogicalSide;

import java.util.function.IntFunction;

public enum IoDirection
        implements IDebuggable, StringRepresentable {

    Input,
    Output;

    public static final IntFunction<IoDirection> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final ModCodecs<IoDirection, ByteBuf> CODECS = new ModCodecs<>(
            StringRepresentable.fromEnum(IoDirection::values),
            ByteBufCodecs.idMapper(BY_ID, Enum::ordinal)
    );

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
    //region StringRepresentable

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
}

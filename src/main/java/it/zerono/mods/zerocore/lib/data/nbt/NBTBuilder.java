/*
 *
 * NBTBuilder.java
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

package it.zerono.mods.zerocore.lib.data.nbt;

import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NBTBuilder {

    public NBTBuilder() {
        this._root = new CompoundNBT();
    }

    public CompoundNBT build() {
        return this._root;
    }

    public NBTBuilder merge(final NBTBuilder builder) {

        this._root.merge(builder.build());
        return this;
    }

    public NBTBuilder merge(final CompoundNBT compound) {

        this._root.merge(compound);
        return this;
    }

    public NBTBuilder addEntity(final String name, final ISyncableEntity entity, final ISyncableEntity.SyncReason syncReason) {

        validateName(name);
        this._root.put(name, entity.syncDataTo(new CompoundNBT(), syncReason));
        return this;
    }

    public NBTBuilder addCompound(final String name, final Consumer<NBTBuilder> builder) {

        validateName(name);

        final NBTBuilder b = new NBTBuilder();

        builder.accept(b);
        this._root.put(name, b.build());
        return this;
    }

    public NBTBuilder addByte(final String name, final byte value) {

        validateName(name);
        this._root.putByte(name, value);
        return this;
    }

    public NBTBuilder addShort(final String name, final short value) {

        validateName(name);
        this._root.putShort(name, value);
        return this;
    }

    public NBTBuilder addInteger(final String name, final int value) {

        validateName(name);
        this._root.putInt(name, value);
        return this;
    }

    public NBTBuilder addLong(final String name, final long value) {

        validateName(name);
        this._root.putLong(name, value);
        return this;
    }

    public NBTBuilder addFloat(final String name, final float value) {

        validateName(name);
        this._root.putFloat(name, value);
        return this;
    }

    public NBTBuilder addDouble(final String name, final double value) {

        validateName(name);
        this._root.putDouble(name, value);
        return this;
    }

    public NBTBuilder addBoolean(final String name, final boolean value) {

        validateName(name);
        this._root.putBoolean(name, value);
        return this;
    }

    public NBTBuilder addString(final String name, final String value) {

        validateName(name);
        this._root.putString(name, value);
        return this;
    }

    public NBTBuilder addByteArray(final String name, final byte[] value) {

        validateName(name);
        this._root.putByteArray(name, value);
        return this;
    }

    public NBTBuilder addIntArray(final String name, final int[] value) {

        validateName(name);
        this._root.putIntArray(name, value);
        return this;
    }

    public NBTBuilder addIntArray(final String name, final List<Integer> value) {

        validateName(name);
        this._root.putIntArray(name, value);
        return this;
    }

    public NBTBuilder addLongArray(final String name, final long[] value) {

        validateName(name);
        this._root.putLongArray(name, value);
        return this;
    }

    public NBTBuilder addLongArray(final String name, final List<Long> value) {

        validateName(name);
        this._root.putLongArray(name, value);
        return this;
    }

    public NBTBuilder addUniqueId(final String name, final UUID value) {

        validateName(name);
        this._root.putUUID(name, value);
        return this;
    }

    //region internals

    private static void validateName(final String name) {

        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Invalid tag name");
        }
    }

    private final CompoundNBT _root;

    //endregion
}

/*
 *
 * NBTHelper.java
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
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class NBTHelper {

    public static final CompoundNBT EMPTY_COMPOUND = new CompoundNBT();

    /**
     * Load an CompoundNBT from the given file
     *
     * @param file the file to read from
     * @return the CompoundNBT read from the file or null if, for whatever reason, the operation fails
     */
    public static Optional<CompoundNBT> nbtFrom(final File file) {

        if (file.exists()) {

            try (final FileInputStream stream = new FileInputStream(file)) {
                return Optional.of(CompressedStreamTools.readCompressed(stream));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return Optional.empty();
    }

    /**
     * Save an CompoundNBT to the given file
     *
     * @param file the file to write the data to
     * @param data the data to store in the file
     * @return true if the operation succeeded, false otherwise
     */
    public static boolean nbtTo(final File file, final CompoundNBT data) {

        try (final FileOutputStream stream = new FileOutputStream(file)) {

            CompressedStreamTools.writeCompressed(data, stream);
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Set an Enum value in the provided NBT tag with the given key
     *
     * @param nbt   the NBT tag to store the data into
     * @param key   the key to be associated with the data
     * @param value the value to store
     * @return nbt
     */
    public static <E extends Enum<E>> CompoundNBT nbtSetEnum(CompoundNBT nbt, String key, E value) {

        nbt.putString(key, value.name());
        return nbt;
    }

    /**
     * Get an Enum value from the provided NBT tag
     *
     * Please note that this method assume that the requested value is in the tag (i.e., that nbt.hasKey(key) is true)
     *
     * @param nbt       the NBT tag to load the data from
     * @param key       the key associated with the data
     * @param enumClass the Class of the Enum
     * @return The Enum value contained in the NBT tag
     * @throws IllegalArgumentException if the value contained in the NBT tag is not a valid value for the provided Enum class
     */
    public static <E extends Enum<E>> E nbtGetEnum(CompoundNBT nbt, String key, Class<E> enumClass) throws IllegalArgumentException {
        return E.valueOf(enumClass, nbt.getString(key));
    }

    /**
     * Check if the provided NBT tag contains an Enum value saved by nbtSetEnum() with the given key
     *
     * @param nbt   the NBT tag to store the data into
     * @param key   the key to be associated with the data
     * @return true if an enum is present, false otherwise
     */
    public static boolean nbtContainsEnum(final CompoundNBT nbt, final String key) {
        return nbt.contains(key, Constants.NBT.TAG_STRING);
    }

    /**
     * Get an Enum value from the provided NBT tag
     *
     * Please note that this method assume that the requested value is in the tag (i.e., that nbt.hasKey(key) is true)
     *
     * @param nbt       the NBT tag to load the data from
     * @param key       the key associated with the data
     * @param enumFactory a factory function that convert the enum value name to the actual enum value (usually Enum::valueOf)
     * @return The Enum value contained in the NBT tag
     * @throws IllegalArgumentException if the value contained in the NBT tag is not a valid value for the provided Enum class
     */
    public static <E extends Enum<E>> E nbtGetEnum(CompoundNBT nbt, String key, Function<String, E> enumFactory, E defaultValue) {

        if (nbt.contains(key)) {

            final String value = nbt.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return enumFactory.apply(nbt.getString(key));
            }
        }

        return defaultValue;
    }

    /**
     * Store an EnumSet in the provided NBT tag with the given key
     *
     * @param nbt   the NBT tag to store the data into
     * @param key   the key to be associated with the data
     * @param value the value to store
     * @return nbt
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <E extends Enum<E>> CompoundNBT nbtSetEnumSet(CompoundNBT nbt, String key, EnumSet<E> value) {

        final ListNBT tagList = new ListNBT();

        for (final E enumValue : value) {
            tagList.add(NBTHelper.nbtSetEnum(new CompoundNBT(), "enum", enumValue));
        }

        nbt.put(key, tagList);
        return nbt;
    }

    /**
     * Check if the provided NBT tag contains an Enum value saved by nbtSetEnum() with the given key
     *
     * @param nbt   the NBT tag to store the data into
     * @param key   the key to be associated with the data
     * @return true if an enum is present, false otherwise
     */
    public static boolean nbtContainsEnumSet(final CompoundNBT nbt, final String key) {
        return nbt.contains(key, Constants.NBT.TAG_LIST);
    }

    /**
     * Load an EnumSet from the provided NBT tag
     *
     * Please note that this method assume that the requested data is in the tag (i.e., that nbt.hasKey(key) is true)
     *
     * @param nbt       the NBT tag to load the data from
     * @param key       the key associated with the data
     * @param enumClass the Class of the elements of the EnumSet
     * @return The EnumSet contained in the NBT tag
     * @throws IllegalArgumentException if one the Enum values contained in the NBT tag is not a valid value for the Enum class
     */
    public static <E extends Enum<E>> EnumSet<E> nbtGetEnumSet(CompoundNBT nbt, String key, Class<E> enumClass) throws IllegalArgumentException {

        final ListNBT tagList = nbt.getList(key, Constants.NBT.TAG_STRING);
        final List<E> valueList = Lists.newArrayList();

        for (int i = 0; i < tagList.size(); ++i) {

            final E enumValue = nbtGetEnum(tagList.getCompound(i), "enum", enumClass);

            valueList.add(enumValue);
        }

        return EnumSet.copyOf(valueList);
    }

    //region internals

    private NBTHelper() {
    }

    //endregion
}

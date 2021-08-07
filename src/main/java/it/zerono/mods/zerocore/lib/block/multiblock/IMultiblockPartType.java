/*
 *
 * IMultiblockPartType.java
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

package it.zerono.mods.zerocore.lib.block.multiblock;

import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public interface IMultiblockPartType
        extends IStringSerializable {

    @Nullable
    TileEntity createTileEntity(BlockState state, IBlockReader world);

    String getTranslationKey();

    /**
     * Return a single byte hash representing this part type
     *
     * The default implementation check if IMultiblockPartType is implemented as an Enum and, if so, return the Enum ordinal as a result.
     * If you are not using an Enum for your implementation you must override this method
     *
     * @return the byte-sized hash code for this part type
     */
    default byte getByteHashCode() {

        if (this instanceof Enum) {
            //noinspection rawtypes
            return (byte)((Enum)this).ordinal();
        }

        return 0;
    }

    default String getNameForId() {
        return CodeHelper.neutralLowercase(this.getSerializedName());
    }
}

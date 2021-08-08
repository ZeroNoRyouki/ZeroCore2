/*
 *
 * IMultiblockVariantProvider.java
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

import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public interface IMultiblockVariantProvider<V extends IMultiblockVariant> {

    Block getBlockType();

    default Optional<V> getMultiblockVariant() {

        final Block block = this.getBlockType();

        if (block instanceof MultiblockPartBlock) {

            @SuppressWarnings("unchecked")
            final Optional<V> variant = ((MultiblockPartBlock)block).getMultiblockVariant();

            return variant;
        }

        return Optional.empty();
    }
}

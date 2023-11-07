/*
 *
 * IMultiblockPartTypeProvider.java
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

import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public interface IMultiblockPartTypeProvider<Controller extends IMultiblockController<Controller>,
        PartType extends IMultiblockPartType> {

    Block getBlockType();

    @SuppressWarnings("unchecked")
    default Optional<PartType> getPartType() {

        final Block block = this.getBlockType();

        if (block instanceof MultiblockPartBlock) {
            return Optional.of(((MultiblockPartBlock<Controller, PartType>) block).getPartType());
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default PartType getPartTypeOrDefault(final PartType defaultValue) {

        final Block block = this.getBlockType();

        if (block instanceof MultiblockPartBlock) {
            return ((MultiblockPartBlock<Controller, PartType>) block).getPartType();
        }

        return defaultValue;
    }

    default boolean isTypeOfPart(final PartType type) {
        return this.getPartType().map(t -> t == type).orElse(false);
    }
}

/*
 *
 * BlocksMatcher.java
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

package it.zerono.mods.zerocore.lib.block.pattern;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlocksMatcher implements Predicate<BlockState> {

    public static Predicate<BlockState> forBlock(final Block block) {
        return new BlockPredicate(block);
    }

    public static Predicate<BlockState> forBlock(final Block block, final Block... others) {
        return new BlocksMatcher(block, others);
    }

    public boolean test(@Nullable final BlockState blockState) {
        return null != blockState && this._blocks.contains(blockState.getBlock());
    }

    //region internals

    private BlocksMatcher(final Block block, final Block... others) {

        this._blocks = Arrays.stream(others).collect(Collectors.toSet());
        this._blocks.add(block);
    }

    private final Set<Block> _blocks;

    //endregion
}

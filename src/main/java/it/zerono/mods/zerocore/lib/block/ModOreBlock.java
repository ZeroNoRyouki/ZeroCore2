/*
 *
 * ModOreBlock.java
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

package it.zerono.mods.zerocore.lib.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ModOreBlock
    extends ModBlock {

    public ModOreBlock(final Properties properties, final int minDroppedXP, final int maxDroppedXP) {

        super(properties);
        this._minXP = minDroppedXP;
        this._maxXP = maxDroppedXP;
    }

    //region Block

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.getExperience(RANDOM) : 0;
    }

    //endregion
    //region internals

    protected int getExperience(Random rand) {
        return Mth.nextInt(rand, this._minXP, this._maxXP);
    }

    private final int _minXP;
    private final int _maxXP;

    //endregion
}

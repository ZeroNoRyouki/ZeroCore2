/*
 *
 * OrientationProperty.java
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

package it.zerono.mods.zerocore.lib.block.property;

import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.core.Direction;

public final class OrientationProperty {

    /**
     * Common block-state properties for block orientation
     */
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final DirectionProperty HFACING = DirectionProperty.create("hfacing", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty VFACING = DirectionProperty.create("vfacing", Direction.Plane.VERTICAL);

    /*
    / **
     * Return the suggested facing for a block indirectly placed in the world (by World.setBlockState for example)
     *
     * @param world the current world
     * @param position position of the block
     * @param currentFacing the current facing
     * @return the new facing for the block based on the surrounding blocks
     * /
    public static Direction suggestDefaultHorizontalFacing(IBlockReader world, BlockPos position, Direction currentFacing) {

        final Direction oppositeFacing = currentFacing.getOpposite();
        final BlockState facingState = world.getBlockState(position.offset(currentFacing));
        final BlockState oppositeState = world.getBlockState(position.offset(oppositeFacing));

        return facingState.isFullCube() && !oppositeState.isFullCube() ? oppositeFacing : currentFacing;
    }
    */
}

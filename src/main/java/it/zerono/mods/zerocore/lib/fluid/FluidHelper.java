/*
 *
 * FluidHelper.java
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

package it.zerono.mods.zerocore.lib.fluid;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public final class FluidHelper {

    /**
     * Fill a destination fluid handler from a source fluid handler with a max amount.
     * The destination fluid handler is loaded from the provided world at the specified position.
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for maxAmount.
     *
     * @param source        The fluid handler to be drained
     * @param world         the world to witch the destination position belong
     * @param fillDirection the direction, relative to the destination handler, from witch the operation is performed
     * @param maxAmount     The largest amount of fluid that should be transferred
     * @param action        if the action should be executed or only simulated
     * @return the fluidStack that was transferred from the source to the destination
     */
    public static FluidStack tryFluidTransfer(IFluidHandler source, World world, BlockPos destinationPosition,
                                              Direction fillDirection, int maxAmount, IFluidHandler.FluidAction action) {
        return FluidUtil.getFluidHandler(world, destinationPosition, fillDirection)
                .map(destination -> FluidUtil.tryFluidTransfer(destination, source, maxAmount, action.execute()))
                .orElse(FluidStack.EMPTY);
    }

    //region internals

    private FluidHelper() {
    }

    //endregion
}

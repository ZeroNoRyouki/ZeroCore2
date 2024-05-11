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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.Objects;

public final class FluidHelper {

    public static final IFluidHandler EMPTY_FLUID_HANDLER = EmptyFluidHandler.INSTANCE;
    public static final int BUCKET_VOLUME = FluidType.BUCKET_VOLUME;

    public static ResourceLocation getFluidId(final Fluid fluid) {
        return Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(fluid));
    }

    public static ResourceLocation getFluidId(final FluidStack stack) {
        return Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(stack.getFluid()));
    }

    public static MutableComponent getFluidName(final Fluid fluid) {
        return Component.translatable(fluid.getFluidType().getDescriptionId());
    }

    public static MutableComponent getFluidName(final FluidStack stack) {
        return Component.translatable(stack.getFluid().getFluidType().getDescriptionId(stack));
    }

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
    public static FluidStack tryFluidTransfer(IFluidHandler source, Level world, BlockPos destinationPosition,
                                              Direction fillDirection, int maxAmount, IFluidHandler.FluidAction action) {
        return FluidUtil.getFluidHandler(world, destinationPosition, fillDirection)
                .map(destination -> FluidUtil.tryFluidTransfer(destination, source, maxAmount, action.execute()))
                .orElse(FluidStack.EMPTY);
    }

    public static FluidStack stackFrom(final FluidStack stack, final int amount) {
        return new FluidStack(stack.getFluid(), amount);
    }

    /**
     * Create a stack from the given NBT data
     *
     * @param nbt an NBT Tag Compound containing the data of the stack to create
     * @return the newly create stack
     */
    public static FluidStack stackFrom(final CompoundTag nbt) {
        return FluidStack.loadFluidStackFromNBT(nbt);
    }

    /**
     * Serialize a stack to NBT
     *
     * @param stack the stack to serialize
     * @return the serialized NBT data
     */
    public static CompoundTag stackToNBT(final FluidStack stack) {
        return stack.writeToNBT(new CompoundTag());
    }

    public static String toStringHelper(final FluidStack stack) {
        return "FluidStack: " + stack.getAmount() + ' ' + getFluidId(stack.getFluid());
    }

    //region internals

    private FluidHelper() {
    }

    //endregion
}

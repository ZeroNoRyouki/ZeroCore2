/*
 *
 * FluidHandlerPolicyWrapper.java
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

package it.zerono.mods.zerocore.lib.fluid.handler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.function.Predicate;

public final class FluidHandlerPolicyWrapper {

    public static IFluidHandler twoWay(final IFluidHandler original,
                                       final Predicate<FluidStack> inputValidator) {
        return new InputWrapper(original, inputValidator);
    }

    public static IFluidHandler inputOnly(final IFluidHandler original,
                                          final Predicate<FluidStack> inputValidator) {
        return new InputWrapper(original, inputValidator) {

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                return FluidStack.EMPTY;
            }

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return FluidStack.EMPTY;
            }
        };
    }

    public static IFluidHandler outputOnly(final IFluidHandler original) {
        return new FluidHandlerForwarder(original) {

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return false;
            }
        };
    }

    //region internals

    private FluidHandlerPolicyWrapper() {
    }

    private static class InputWrapper
            extends FluidHandlerForwarder {

        public InputWrapper(final IFluidHandler handler,
                            final Predicate<FluidStack> inputValidator) {

            super(handler);
            this._inputValidator = inputValidator;
        }

        @Override
        public int fill(FluidStack stack, FluidAction action) {
            return this._inputValidator.test(stack) ? super.fill(stack, action) : 0;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return this._inputValidator.test(stack);
        }

        private final Predicate<FluidStack> _inputValidator;
    }

    //endregion
}

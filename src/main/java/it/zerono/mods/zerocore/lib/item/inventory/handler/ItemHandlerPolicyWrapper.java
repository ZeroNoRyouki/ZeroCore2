/*
 *
 * ItemHandlerPolicyWrapper.java
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

package it.zerono.mods.zerocore.lib.item.inventory.handler;


import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.function.BiPredicate;

public class ItemHandlerPolicyWrapper {

    public static IItemHandlerModifiable twoWay(final IItemHandlerModifiable original,
                                                final BiPredicate<Integer, ItemStack> inputValidator) {
        return new InputWrapper(original, inputValidator);
    }

    public static IItemHandlerModifiable inputOnly(final IItemHandlerModifiable original,
                                                   final BiPredicate<Integer, ItemStack> inputValidator) {
        return new InputWrapper(original, inputValidator) {

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    public static IItemHandlerModifiable outputOnly(final IItemHandlerModifiable original) {
        return new ItemHandlerModifiableForwarder(original) {

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    //region internals

    private ItemHandlerPolicyWrapper() {
    }

    private static class InputWrapper
            extends ItemHandlerModifiableForwarder {

        public InputWrapper(final IItemHandlerModifiable handler,
                            final BiPredicate<Integer, ItemStack> inputValidator) {

            super(handler);
            this._inputValidator = inputValidator;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return this._inputValidator.test(slot, stack);
        }

        private final BiPredicate<Integer, ItemStack> _inputValidator;
    }

    //endregion
}

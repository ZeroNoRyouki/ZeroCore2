/*
 *
 * OperationMode.java
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

package it.zerono.mods.zerocore.lib.data.stack;

import net.minecraftforge.fluids.capability.IFluidHandler;

public enum OperationMode {

    /**
     * Perform the operation
     */
    Execute,

    /**
     * Only simulate the result of the operation
     */
    Simulate;

    public boolean execute() {
        return Execute == this;
    }

    public boolean simulate() {
        return Simulate == this;
    }

    public IFluidHandler.FluidAction toFluidAction() {
        return Execute == this ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE;
    }

    public static OperationMode from(IFluidHandler.FluidAction action) {
        return action.execute() ? Execute : Simulate;
    }
}

/*
 * BaseHelper
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
 * Do not remove or edit this header
 *
 */
package it.zerono.mods.zerocore.base;

import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public final class BaseHelper {

    public static ITextComponent getFluidNameOrEmpty(final FluidStack stack) {
        return stack.isEmpty() ? CommonConstants.EMPTY_VALUE : FluidHelper.getFluidName(stack);
    }

    //region internals

    private BaseHelper() {
    }

    //endregion
}

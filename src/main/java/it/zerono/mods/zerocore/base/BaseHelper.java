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
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;

public final class BaseHelper {

    public static final String EMPTY_TRANSLATION_KEY = "gui.zerocore.base.generic.empty";

    public static MutableComponent emptyValue() {
        return TextHelper.translatable(EMPTY_TRANSLATION_KEY);
    }

    public static MutableComponent getFluidNameOrEmpty(final FluidStack stack) {
        return stack.isEmpty() ? emptyValue() : FluidHelper.getFluidName(stack);
    }

    //region internals

    private BaseHelper() {
    }

    //endregion
}

/*
 *
 * RegistrySafeReferent.java
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

package it.zerono.mods.zerocore.internal;

import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockRegistry;
import it.zerono.mods.zerocore.lib.multiblock.registry.MultiblockClientRegistry;
import it.zerono.mods.zerocore.lib.multiblock.registry.MultiblockRegistry;

public final class MultiblockRegistrySafeReferent {

    public static <Controller extends IMultiblockController<Controller>> IMultiblockRegistry<Controller> server() {
        return new MultiblockRegistry<>();
    }

    public static <Controller extends IMultiblockController<Controller>> IMultiblockRegistry<Controller> client() {
        return new MultiblockClientRegistry<>();
    }
}

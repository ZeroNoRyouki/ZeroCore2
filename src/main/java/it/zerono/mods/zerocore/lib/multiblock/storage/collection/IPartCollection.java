package it.zerono.mods.zerocore.lib.multiblock.storage.collection;
/*
 * IPartCollection
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

import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraftforge.common.util.NonNullPredicate;

import java.util.stream.Stream;

public interface IPartCollection<Controller extends IMultiblockController<Controller>,
                                    Part extends IMultiblockPart<Controller>>
        extends Iterable<Part>, NonNullPredicate<IMultiblockPart<Controller>> {

    int size();

    boolean isEmpty();

    /**
     * Add a part to this collection. The caller must test that the part is compatible with this collection
     */
    boolean add(IMultiblockPart<Controller> part);

    /**
     * Remove a part to this collection. The caller must test that the part is compatible with this collection
     */
    boolean remove(IMultiblockPart<Controller> part);

    void clear();

    Stream<Part> stream();

    Stream<Part> parallelStream();
}

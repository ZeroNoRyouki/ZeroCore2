package it.zerono.mods.zerocore.lib.multiblock.storage.collection;
/*
 * PartsList
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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraftforge.common.util.NonNullPredicate;

import java.util.Iterator;
import java.util.stream.Stream;

public class PartCollection<Controller extends IMultiblockController<Controller>,
                                Part extends IMultiblockPart<Controller>>
        implements IPartCollection<Controller, Part> {

    public PartCollection(final int initialSize, final NonNullPredicate<IMultiblockPart<Controller>> validator) {

        this._initialSize = initialSize;
        this._validator = validator;
        this._list = ObjectLists.emptyList();
    }

    //region IPartCollection

    @Override
    public int size() {
        return this._list.size();
    }

    @Override
    public boolean isEmpty() {
        return this._list.isEmpty();
    }

    @Override
    public boolean add(final IMultiblockPart<Controller> part) {

        if (ObjectLists.<Part>emptyList() == this._list) {
            this._list = new ObjectArrayList<>(this._initialSize);
        }

        //noinspection unchecked
        return this._list.add((Part) part);
    }

    @Override
    public boolean remove(final IMultiblockPart<Controller> part) {

        boolean result = false;

        if (ObjectLists.<Part>emptyList() != this._list) {
            //noinspection unchecked
            result = this._list.remove((Part)part);
        }

        if (this._list.isEmpty()) {
            this._list = ObjectLists.emptyList();
        }

        return result;
    }

    @Override
    public void clear() {
        this._list = ObjectLists.emptyList();
    }

    @Override
    public Stream<Part> stream() {
        return this._list.stream();
    }

    @Override
    public Stream<Part> parallelStream() {
        return this._list.parallelStream();
    }

    @Override
    public Iterator<Part> iterator() {
        return this._list.iterator();
    }

    @Override
    public boolean test(final IMultiblockPart<Controller> part) {
        return this._validator.test(part);
    }

    //endregion
    //region internals

    private final int _initialSize;
    private final NonNullPredicate<IMultiblockPart<Controller>> _validator;
    private ObjectList<Part> _list;

    //endregion
}

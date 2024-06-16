/*
 *
 * WindowsManagerSingleWindow.java
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

package it.zerono.mods.zerocore.lib.client.gui;

import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

class WindowsManagerSingleWindow<C extends ModContainer> extends AbstractWindowsManager<C> {

    //region AbstractWindowsManager

    @Override
    protected void addWindow(Window<C> wnd, boolean isModal) {

//        if (null != this._window) {
//            throw new IllegalStateException("There is already a Window associated to this single-window WindowsManager");
//        }

        this._window = wnd;
    }

    @Override
    protected void showWindow(IWindow window, boolean show) {
        //NOP - hiding the only window of this windows manager is not a good idea...
    }

    @Override
    protected void forEachWindow(Consumer<Window<C>> action) {

        if (null != this._window) {
            action.accept(this._window);
        }
    }

    @Override
    protected void forEachInteractiveWindow(Consumer<Window<C>> action) {
        this.forEachWindow(action);
    }

    @Nullable
    protected <R> R forEachInteractiveWindow(final Function<Window<C>, R> transformation, @Nullable final R invalidResult) {
        return null != this._window ? transformation.apply(this._window) : invalidResult;
    }

    protected Optional<IControl> findControl(final int x, final int y) {
        return null != this._window ? this._window.findControl(x, y) : Optional.empty();
    }

    @Override
    protected void resetState() {

        this._window = null;
        super.resetState();
    }

    //region WindowsManagerSingleWindow

    WindowsManagerSingleWindow(final ModContainerScreen<C> guiContainer) {

        super(guiContainer);
        this._window = null;
    }

    //region internals

    private Window<C> _window;
}

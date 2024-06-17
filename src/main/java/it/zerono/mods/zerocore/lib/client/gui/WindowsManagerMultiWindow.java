/*
 *
 * WindowsManagerMultiWindow.java
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

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

class WindowsManagerMultiWindow<C extends ModContainer> extends AbstractWindowsManager<C> {

    //region AbstractWindowsManager

    @Override
    protected void addWindow(Window<C> wnd, boolean isModal) {

        if (isModal) {

            this._modalWindows.add(wnd);
            this._interactiveModalWindow = wnd;

        } else {

            this._modelessWindows.add(wnd);
        }
    }

    @Override
    protected void showWindow(IWindow window, boolean show) {
        //TODO imp!
    }

    @Override
    protected void forEachWindow(Consumer<Window<C>> action) {

        this._modalWindows.forEach(action);
        this._modelessWindows.forEach(action);
    }

    @Override
    protected void forEachInteractiveWindow(Consumer<Window<C>> action) {

        if (null != this._interactiveModalWindow) {
            action.accept(this._interactiveModalWindow);
        } else {
            this._modelessWindows.forEach(action);
        }
    }

    @Nullable
    protected <R> R forEachInteractiveWindow(final Function<Window<C>, R> transformation, @Nullable final R invalidResult) {

        R result;

        if (null != this._interactiveModalWindow) {

            result = transformation.apply(this._interactiveModalWindow);

            if (invalidResult != result) {
                return result;
            }

        } else {

            for (final Window<C> window : this._modelessWindows) {

                result = transformation.apply(window);

                if (invalidResult != result) {
                    return result;
                }
            }
        }

        return invalidResult;
    }

    protected Optional<IControl> findControl(final int x, final int y) {

        if (null != this._interactiveModalWindow) {

            return this._interactiveModalWindow.findControl(x, y);

        } else {

            for (final Window<C> window : this._modelessWindows) {

                final Optional<IControl> control = window.findControl(x, y);

                if (control.isPresent()) {
                    return control;
                }
            }

            return Optional.empty();
        }
    }

    @Override
    protected void resetState() {

        this._modelessWindows.clear();
        this._modalWindows.clear();
        super.resetState();
    }

    //region WindowsManagerMultiWindow

    WindowsManagerMultiWindow(final ModContainerScreen<C> guiContainer) {

        super(guiContainer);
        this._modelessWindows = Lists.newArrayListWithExpectedSize(2);
        this._modalWindows = Lists.newArrayList();
        this._interactiveModalWindow = null;
    }

    //region internals

    private final List<Window<C>> _modelessWindows;
    private final List<Window<C>> _modalWindows;
    private Window<C> _interactiveModalWindow;
}

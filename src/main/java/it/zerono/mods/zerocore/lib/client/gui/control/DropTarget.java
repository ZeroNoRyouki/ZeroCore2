/*
 *
 * DropTarget.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.IDragSource;
import it.zerono.mods.zerocore.lib.client.gui.IDropTarget;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

@SuppressWarnings("unused")
public abstract class DropTarget
        extends AbstractDragSource
        implements IDropTarget, IDragSource {

    public DropTarget(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this.setActAsSource(true);
    }

    public boolean getActAsSource() {
        return this._actAsSource;
    }

    public void setActAsSource(final boolean actAsSource) {
        this._actAsSource = actAsSource;
    }

    //region AbstractDragSource

    @Override
    public boolean onMouseClicked(final IWindow wnd, final int mouseX, final int mouseY, final int clickedButton) {

        boolean result = super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);

        switch (clickedButton) {

            case CodeHelper.MOUSE_BUTTON_LEFT: {

                if (this.getActAsSource()) {
                    result |= this.startDragging(wnd);
                }

                break;
            }

            case CodeHelper.MOUSE_BUTTON_RIGHT: {

                // clear the draggable (if any)
                this.setDraggable(null);
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" asSource")
                .append(this._actAsSource);
    }

    //endregion
    //region internals

    private boolean _actAsSource;

    //endregion
}

/*
 *
 * DragSource.java
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
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

@SuppressWarnings("unused")
public class DragSource
        extends AbstractDragSource
        implements IDragSource {

    public DragSource(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this.setBackground(this.getTheme().FLAT_BACKGROUND_COLOR);
    }

    //region AbstractDragSource

    @Override
    public boolean onMouseClicked(final IWindow wnd, final int mouseX, final int mouseY, final int clickedButton) {
        return ((CodeHelper.MOUSE_BUTTON_LEFT == clickedButton) && this.startDragging(wnd)) ||
                super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);
    }

    //endregion
}

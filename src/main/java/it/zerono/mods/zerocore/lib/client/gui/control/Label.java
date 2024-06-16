/*
 *
 * Label.java
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

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public class Label
        extends AbstractTextualControl {

    public Label(ModContainerScreen<? extends ModContainer> gui, final String name, final Component text) {
        this(gui, name, text.getString());
    }

    public Label(ModContainerScreen<? extends ModContainer> gui, final String name, final String text) {

        super(gui, name, text);
        this.setAutoSize(true);
    }

    public boolean getAutoSize() {
        return this._autoSize;
    }

    public void setAutoSize(final boolean autoSize) {
        this._autoSize = autoSize;
    }

    //region AbstractTextualControl

    @Override
    public int getDesiredDimension(final DesiredDimension dimension) {

        if (this.getAutoSize()) {

            switch (dimension) {

                case Width:
                    return this.getTextWidth() + this.getPadding().getLeft() + this.getPadding().getRight();

                case Height:
                    return this.getTextHeight() + this.getPadding().getTop() + this.getPadding().getBottom();
            }
        }

        return super.getDesiredDimension(dimension);
    }

    //endregion
    //region internals

    private boolean _autoSize;

    //endregion
}

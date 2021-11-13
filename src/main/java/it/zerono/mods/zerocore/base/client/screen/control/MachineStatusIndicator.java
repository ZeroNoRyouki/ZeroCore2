/*
 *
 * MachineStatus.java
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

package it.zerono.mods.zerocore.base.client.screen.control;

import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.function.Consumer;

public class MachineStatusIndicator
        extends AbstractCompositeControl
        implements Consumer<Boolean> {

    public MachineStatusIndicator(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this._statusOn = new Picture(gui, "on", BaseIcons.MachineStatusOn);
        this._statusOff = new Picture(gui, "off", BaseIcons.MachineStatusOff);

        this._statusOn.setVisible(false);
        this._statusOff.setVisible(false);

        this.addChildControl(this._statusOn, this._statusOff);
    }

    public void updateStatus(final boolean newStatus) {

        this._statusOn.setVisible(newStatus);
        this._statusOff.setVisible(!newStatus);
    }

    public void setTooltips(final boolean status, final List<ITextComponent> lines) {

        if (status) {
            this._statusOn.setTooltips(lines);
        } else {
            this._statusOff.setTooltips(lines);
        }
    }

    public void setTooltips(final boolean status, final String singleLineKey) {
        this.setTooltips(status, ObjectLists.singleton(new TranslationTextComponent(singleLineKey)));
    }

    public void setTooltips(final boolean status, final List<ITextComponent> lines, final List<Object> objects) {

        if (status) {
            this._statusOn.setTooltips(lines, objects);
        } else {
            this._statusOff.setTooltips(lines, objects);
        }
    }

    //region Consumer<Boolean>

    @Override
    public void accept(final Boolean active) {
        this.updateStatus(active);
    }

    //endregion
    //region AbstractCompoundControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        final Rectangle statusRect = bounds.offset(-bounds.getX1(), -bounds.getY1());

        this._statusOn.setBounds(statusRect);
        this._statusOff.setBounds(statusRect);
    }

    //endregion
    //region internals

    private final Picture _statusOn;
    private final Picture _statusOff;

    //endregion
}

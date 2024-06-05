/*
 *
 * SlotsGroup.java
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGeneric;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class SlotsGroup
        extends AbstractControl {

    public SlotsGroup(final ModContainerScreen<? extends ModContainer> gui, final String name,
                      final String inventorySlotsGroupName, final int width, final int height,
                      final ISprite slotsBackground) {

        super(gui, name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(inventorySlotsGroupName));
        this._inventorySlotsGroupName = inventorySlotsGroupName;

        this.setDesiredDimension(DesiredDimension.Width, width);
        this.setDesiredDimension(DesiredDimension.Height, height);
        this.setBackground(slotsBackground);
    }

    //region AbstractControl

    @Override
    public boolean onMouseClicked(IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        // allow the ContainerScreen to handle this message and interact with the slots
        super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);
        return false;
    }

    @Override
    public boolean onMouseReleased(IWindow wnd, int mouseX, int mouseY, int releasedButton) {

        // allow the ContainerScreen to handle this message and interact with the slots
        super.onMouseReleased(wnd, mouseX, mouseY, releasedButton);
        return false;
    }

    @Override
    public boolean onMouseDragged(IWindow wnd, int mouseX, int mouseY, int clickedButton, long timeSinceLastClick) {

        // allow the ContainerScreen to handle this message and interact with the slots
        super.onMouseDragged(wnd, mouseX, mouseY, clickedButton, timeSinceLastClick);
        return false;
    }

    @Override
    public boolean onMouseWheel(IWindow wnd, int mouseX, int mouseY, double movement) {

        // allow the ContainerScreen to handle this message and interact with the slots
        super.onMouseWheel(wnd, mouseX, mouseY, movement);
        return false;
    }

    @Override
    public boolean onMouseMoved(IWindow wnd, int mouseX, int mouseY) {

        //this.getGui().renderHoveredSlotToolTip(0, 0);
        return super.onMouseMoved(wnd, mouseX, mouseY);
    }

    @Override
    public void onMoved() {

        final List<Slot> slots = this.getGui().getMenu().getInventorySlotsGroup(this._inventorySlotsGroupName);

        slots.stream()
                .filter(slot -> slot instanceof SlotGeneric)
                .map(slot -> (SlotGeneric)slot)
                .forEach(slot -> slot.translate(this::translateSlot));
    }

    @Override
    public void onPaint(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
        this.getGui().renderHoveredSlotToolTip(gfx);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" group:")
                .append(this._inventorySlotsGroupName);
    }

    //endregion
    //region internals

    private Point translateSlot(final int x, final int y) {

        final Point p = this.controlToScreen(x + this.getPadding().getLeft(), y + this.getPadding().getTop());

        return p.offset(-this.getGui().getGuiLeft(), -this.getGui().getGuiTop());
    }

    private final String _inventorySlotsGroupName;

    //endregion
}

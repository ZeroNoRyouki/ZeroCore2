/*
 *
 * AbstractDragSource.java
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

import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.*;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractDragSource
        extends AbstractControl
        implements IDragSource {

    public final IEvent<BiConsumer<IControl, IDraggable>> DraggableAdded;
    public final IEvent<BiConsumer<IControl, IDraggable>> DraggableRemoved;

    protected AbstractDragSource(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this._draggable = null;
        this._paintXY = Point.ZERO;

        this.DraggableAdded = new Event<>();
        this.DraggableRemoved = new Event<>();

        this.updatePaintCoordinates();
    }

    //region AbstractControl

    @Override
    public void onWindowClosed() {

        super.onWindowClosed();
        this.DraggableAdded.unsubscribeAll();
        this.DraggableRemoved.unsubscribeAll();
    }

    @Override
    public void onPaintBackground(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        super.onPaintBackground(matrix, partialTicks, mouseX, mouseY);
        this.paintHollowRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, this.getTheme().DARK_OUTLINE_COLOR);
    }

    @Override
    public void onPaint(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.getDraggable().ifPresent(draggable -> draggable.onPaint(matrix, this.getPaintX(), this.getPaintY(),
                this.getGuiZLevel(), this.getMouseOver() ? IDraggable.PaintState.Highlighted : IDraggable.PaintState.Default));
    }

    @Override
    public void onMoved() {
        this.updatePaintCoordinates();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" paintAt:")
                .append(this._paintXY);
    }

    //endregion
    //region IDragSource

    @Override
    public Optional<IDraggable> getDraggable() {
        return Optional.ofNullable(this._draggable);
    }

    @Override
    public void setDraggable(@Nullable final IDraggable draggable) {

        if (null != draggable) {

            this._draggable = draggable;
            this.DraggableAdded.raise(c -> c.accept(this, draggable));

        } else {

            this.getDraggable().ifPresent(currentDraggable -> {

                this._draggable = null;
                this.DraggableRemoved.raise(c -> c.accept(this, currentDraggable));
            });
        }

        this.updatePaintCoordinates();
    }

    //endregion
    //region internals

    protected boolean startDragging(final IWindow wnd) {

        final Optional<IDraggable> draggable = this.getDraggable();

        if (draggable.isPresent()) {

            this.setDraggable(null);
            wnd.startDragging(draggable.get(), this);
            return true;
        }

        return false;
    }

    protected int getPaintX() {
        return this._paintXY.X;
    }

    protected int getPaintY() {
        return this._paintXY.Y;
    }

    private void updatePaintCoordinates() {
        CodeHelper.optionalIfPresentOrElse(this.getDraggable(),
                draggable -> this._paintXY = this.controlToScreen((this.getBounds().Width - draggable.getWidth()) / 2,
                        (this.getBounds().Height - draggable.getHeight()) / 2),
                () -> this._paintXY = Point.ZERO);
    }

    private IDraggable _draggable;
    private Point _paintXY;

    //endregion
}

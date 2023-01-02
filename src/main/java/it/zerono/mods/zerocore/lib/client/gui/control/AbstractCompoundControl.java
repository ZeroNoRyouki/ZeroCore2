/*
 *
 * AbstractControlContainer.java
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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Theme;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractCompoundControl
        extends AbstractControl
        implements Iterable<IControl> {

    //region AbstractControl

    @Override
    public Optional<IControl> findControl(final int x, final int y) {

        final int childX = this.parentToChildX(x);
        final int childY = this.parentToChildY(y);

        for (IControl child : this) {
            if (child.hitTest(childX, childY) && child.getVisible()) {
                return child.findControl(childX, childY);
            }
        }

        return Optional.of(this);
    }

    @Override
    public Optional<IControl> findControl(String name) {

        Optional<IControl> result = super.findControl(name);

        if (result.isPresent()) {
            return result;
        }

        for (final IControl child : this) {

            result = child.findControl(name);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean containsControl(IControl control) {

        if (super.containsControl(control)) {
            return true;
        }

        for (final IControl child : this) {
            if (child.containsControl(control)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);
        this.updateChildrenControlOrigin();
    }

    @Override
    public void translate(final int xOffset, final int yOffset) {

        super.translate(xOffset, yOffset);
        this.updateChildrenControlOrigin();
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        this.forEach(c -> c.setEnabled(enabled));
    }

    @Override
    public boolean onMouseMoved(final IWindow wnd, int mouseX, int mouseY) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible() && child.hitTest(childX, childY)) {
                return child.onMouseMoved(wnd, childX, childY);
            }
        }

        return false;
    }

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param clickedButton the mouse button clicked
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible() && child.hitTest(childX, childY)) {
                return child.getEnabled() && child.onMouseClicked(wnd, childX, childY, clickedButton);
            }
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final IWindow wnd, int mouseX, int mouseY, int releasedButton) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible() && child.hitTest(childX, childY)) {
                return child.getEnabled() && child.onMouseReleased(wnd, childX, childY, releasedButton);
            }
        }

        return false;
    }

    @Override
    public boolean onMouseWheel(final IWindow wnd, int mouseX, int mouseY, double movement) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible() && child.hitTest(childX, childY)) {
                return child.getEnabled() && child.onMouseWheel(wnd, childX, childY, movement);
            }
        }

        return false;
    }

    @Override
    public void onPaintBackground(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        super.onPaintBackground(matrix, partialTicks, mouseX, mouseY);

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible()) {
                child.onPaintBackground(matrix, partialTicks, childX, childY);
            }
        }
    }

    @Override
    public void onPaint(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible()) {
                child.onPaint(matrix, partialTicks, childX, childY);
            }
        }
    }

    @Override
    public void onPaintOverlay(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        final int childX = this.parentToChildX(mouseX);
        final int childY = this.parentToChildY(mouseY);

        for (final IControl child : this) {
            if (child.getVisible()) {
                child.onPaintOverlay(matrix, partialTicks, childX, childY);
            }
        }
    }

    @Override
    public void onWindowClosed() {
        this.forEach(IControl::onWindowClosed);
    }

    @Override
    public void onThemeChanged(Theme newTheme) {

        super.onThemeChanged(newTheme);
        this.forEach(c -> c.onThemeChanged(newTheme));
    }

    @Override
    public Iterator<IControl> iterator() {
        return this._children.iterator();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" children:")
                .append(this._children.size());
    }

    @Override
    protected void setControlOrigin(final Point origin) {

        super.setControlOrigin(origin);

        // the Control Origin of this container changed. Change the children Control Origin too
        this.updateChildrenControlOrigin();
    }

    //endregion
    //region internals

    protected AbstractCompoundControl(ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this._children = Lists.newArrayList();
    }

    protected void addChildControl(@Nonnull IControl control) {

        this._children.add(control);
        control.setParent(this);
    }

    protected void addChildControl(@Nonnull IControl... controls) {

        for (final IControl control: controls) {

            this._children.add(control);
            control.setParent(this);
        }
    }

    protected void removeChildControl(@Nonnull IControl control) {

        this._children.remove(control);
        control.setParent(null);
    }

    protected void removeChildrenControls() {

        this._children.forEach(child -> child.setParent(null));
        this._children.clear();
    }

    protected int getChildrenControlsCount() {
        return this._children.size();
    }

    protected int parentToChildX(int parentX) {
        return parentX - this.getBounds().Origin.X;
    }

    protected int parentToChildY(int parentY) {
        return parentY - this.getBounds().Origin.Y;
    }

    @SuppressWarnings("unused")
    protected int childToParentX(int childX) {
        return childX + this.getBounds().Origin.X;
    }

    @SuppressWarnings("unused")
    protected int childToParentY(int childY) {
        return childY + this.getBounds().Origin.Y;
    }

    private void updateChildrenControlOrigin() {

        final Point newOrigin = this.getOrigin().offset(this.getBounds().Origin);

        for (final IControl child : this) {
            if (child instanceof AbstractControl) {
                ((AbstractControl)child).setControlOrigin(newOrigin);
            }
        }
    }

    /*
    private void rebuildTabOrder() {

        // build the tab order

        final Map<Integer, IControl> focusableControls = Maps.newTreeMap();

        for (IControl child : this) {

            if (child.canAcceptFocus() && -1 != child.getTabOrder()) {
                focusableControls.put(child.getTabOrder(), child);
            }
        }

        if (focusableControls.isEmpty()) {

            this._tabOrder = EMPTY_CONTROLS;
            this._tabOrderNextIndex = -1;

        } else {

            final Collection<IControl> values = focusableControls.values();

            this._tabOrder = values.toArray(new IControl[values.size()]);
            this._tabOrderNextIndex = 0;
        }
    }*/

    protected Stream<IControl> stream() {
        return this._children.stream();
    }

    private final List<IControl> _children;

    //endregion
}

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

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.validator.IControlValidator;
import it.zerono.mods.zerocore.lib.data.Flags;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractControlContainer
        extends AbstractCompoundControl
        implements IControlContainer {

    //region IControlContainer

    @Override
    public void addControl(@Nonnull IControl control) {

        this.addChildControl(control);
        this.requestLayoutRun();
    }

    @Override
    public void addControl(@Nonnull IControl... controls) {

        this.addChildControl(controls);
        this.requestLayoutRun();
    }

    @Override
    public void removeControl(@Nonnull IControl control) {

        this.removeChildControl(control);
        this.requestLayoutRun();
    }

    @Override
    public void removeControls() {

        this.removeChildrenControls();
        this.requestLayoutRun();
    }

    @Override
    public int getControlsCount() {
        return this.getChildrenControlsCount();
    }

    @Override
    public ILayoutEngine getLayoutEngine() {
        return this._layoutEngine;
    }

    @Override
    public void setLayoutEngine(ILayoutEngine engine) {

        this._layoutEngine = engine;
        this.requestLayoutRun();
    }

    @Override
    public void setValidator(final IControlValidator validator) {
        this._validator = validator;
    }

    @Override
    public void validate(final Consumer<ITextComponent> errorReport) {

        this._validator.validate(this, errorReport);
        this.forEach(control -> {

            if (control instanceof IControlContainer) {
                ((IControlContainer)control).validate(errorReport);
            }
        });
    }

    //endregion
    //region AbstractCompoundControl

    @Override
    public Optional<IControl> findControl(final int x, final int y) {

        if (this.isLayoutRunRequested()) {
            this.runLayoutEngine();
        }

        return super.findControl(x, y);
    }

    @Override
    public void setDesiredDimension(DesiredDimension dimension, int value) {

        super.setDesiredDimension(dimension, value);
        this.setBoundsFromDesiredDimension();
    }

    @Override
    public void setDesiredDimension(int width, int height) {

        super.setDesiredDimension(width, height);
        this.setBoundsFromDesiredDimension();
    }

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);
        this.requestLayoutRun();
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        if (this.isLayoutRunRequested()) {
            this.runLayoutEngine();
        }

        super.onPaintBackground(matrix, partialTicks, mouseX, mouseY);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" contFlags:")
                .append(this._flags);
    }

    //endregion
    //region internals

    protected AbstractControlContainer(ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name);
        this._flags = new Flags<>(ContainerFlags.class);
        this._layoutEngine = DEFAULT_LAYOUT_ENGINE;
        this._validator = (c, e) -> {};
    }

    protected boolean isLayoutRunRequested() {
        return this._flags.contains(ContainerFlags.RunLayoutEngine);
    }

    protected void requestLayoutRun() {
        this._flags.add(ContainerFlags.RunLayoutEngine);
    }

    protected void runLayoutEngine() {

        this._layoutEngine.layout(this);
        this._flags.remove(ContainerFlags.RunLayoutEngine);
    }

    private void setBoundsFromDesiredDimension() {

        final Rectangle bounds = new Rectangle(this.getBounds().Origin, this.getDesiredDimension(DesiredDimension.Width),
                this.getDesiredDimension(DesiredDimension.Height));

        this.setBounds(bounds);
    }

    private enum ContainerFlags {
        RunLayoutEngine,
    }

    private static final ILayoutEngine DEFAULT_LAYOUT_ENGINE = new FixedLayoutEngine().setZeroMargins();

    private final Flags<ContainerFlags> _flags;
    private ILayoutEngine _layoutEngine;
    private IControlValidator _validator;

    //endregion
}

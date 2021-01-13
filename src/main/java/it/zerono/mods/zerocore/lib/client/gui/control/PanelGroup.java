/*
 *
 * PanelGroup.java
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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PanelGroup<Index extends Enum<Index>>
    extends AbstractControlContainer {

    /**
     * Raised before the currently active panel, if there is one, is changed.
     * The parameters are the currently active Index and the current Panel itself.
     */
    public IEvent<BiConsumer<Index, Panel>> PrePanelChange;

    /**
     * Raised after the currently active panel is changed.
     * The parameters are the new active Index and the new Panel itself.
     */
    public IEvent<BiConsumer<Index, Panel>> PostPanelChange;

    @SafeVarargs
    public PanelGroup(final ModContainerScreen<? extends ModContainer> gui, final String name, final Index firstValidIndex,
                      final Index secondValidIndex, final Index... otherValidIndices) {
        this(gui, name, new EnumIndexedArray<>(Panel[]::new, firstValidIndex, secondValidIndex, otherValidIndices));
    }

    public PanelGroup(final ModContainerScreen<? extends ModContainer> gui, final String name, final Index[] validIndices) {
        this(gui, name, new EnumIndexedArray<>(Panel[]::new, validIndices));
    }

    public PanelGroup(final ModContainerScreen<? extends ModContainer> gui, final String name, final Iterable<Index> validIndices) {
        this(gui, name, new EnumIndexedArray<>(Panel[]::new, validIndices));
    }

    public Optional<Index> getActivePanelIndex() {
        return Optional.ofNullable(this._activePanel);
    }

    public Optional<Panel> getActivePanel() {
        return this.getActivePanelIndex().flatMap(this._panels::getElement);
    }

    public void setActivePanel(final Index newIndex) {

        CodeHelper.optionalIfPresent(this.getActivePanelIndex(), this.getActivePanel(),
                (index, panel) -> this.PrePanelChange.raise(c -> c.accept(index, panel)));

        this._activePanel = newIndex;

        CodeHelper.optionalIfPresent(this.getActivePanelIndex(), this.getActivePanel(),
                (index, panel) -> this.PostPanelChange.raise(c -> c.accept(index, panel)));
    }

    public void clearActivePanel() {

        CodeHelper.optionalIfPresent(this.getActivePanelIndex(), this.getActivePanel(),
                (index, panel) -> this.PrePanelChange.raise(c -> c.accept(index, panel)));

        this._activePanel = null;
    }

    public void setPanel(final Index index, final Panel panel) {
        this._panels.setElement(index, panel);
    }

    public Optional<Panel> removePanel(final Index index) {

        Optional<Panel> p = this._panels.getElement(index);

        if (p.isPresent()) {
            this._panels.setElement(index, null);
        }

        return p;
    }

    public void removeAllPanels() {
        this._panels.setAll(null);
    }

    public void removeAllPanels(final Consumer<Panel> callback) {

        this._panels.stream()
                .filter(Objects::nonNull)
                .forEach(callback);

        this._panels.setAll(null);
    }

    //region Iterable<IControl>

    @Override
    public Iterator<IControl> iterator() {
        return this.getActivePanel().map(AbstractCompoundControl::iterator).orElse(Collections.emptyIterator());
    }

    //endregion
    //region AbstractControlContainer

    @Override
    public Optional<IControl> findControl(final int x, final int y) {
        return this.getActivePanel().flatMap(p -> p.findControl(x, y));
    }

    @Override
    public Optional<IControl> findControl(String name) {
        return this.getActivePanel().flatMap(p -> p.findControl(name));
    }

    @Override
    public boolean containsControl(IControl control) {
        return this.getActivePanel().map(p -> p.containsControl(control)).orElse(false);
    }

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);
        this._panels.stream().forEach(p -> p.setBounds(bounds));
    }

    @Override
    public void translate(final int xOffset, final int yOffset) {
        this._panels.stream().forEach(p -> p.translate(xOffset, yOffset));
    }

    @Override
    public boolean onMouseMoved(final IWindow wnd, int mouseX, int mouseY) {
        return this.getActivePanel().map(p -> p.onMouseMoved(wnd, mouseX, mouseY)).orElse(false);
    }

    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {
        return this.getActivePanel().map(p -> p.onMouseClicked(wnd, mouseX, mouseY, clickedButton)).orElse(false);
    }

    @Override
    public boolean onMouseReleased(final IWindow wnd, int mouseX, int mouseY, int releasedButton) {
        return this.getActivePanel().map(p -> p.onMouseClicked(wnd, mouseX, mouseY, releasedButton)).orElse(false);
    }

    @Override
    public boolean onMouseWheel(final IWindow wnd, int mouseX, int mouseY, double movement) {
        return this.getActivePanel().map(p -> p.onMouseWheel(wnd, mouseX, mouseY, movement)).orElse(false);
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.getActivePanel().ifPresent(p -> p.onPaintBackground(matrix, partialTicks, mouseX, mouseY));
    }

    @Override
    public void onPaint(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.getActivePanel().ifPresent(p -> p.onPaint(matrix, partialTicks, mouseX, mouseY));
    }

    @Override
    public void onPaintOverlay(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.getActivePanel().ifPresent(p -> p.onPaintOverlay(matrix, partialTicks, mouseX, mouseY));
    }

    @Override
    public void addControl(@Nonnull IControl control) {
        this.getActivePanel().ifPresent(p -> p.addControl(control));
    }

    @Override
    public void addControl(@Nonnull IControl... controls) {
        this.getActivePanel().ifPresent(p -> p.addControl(controls));
    }

    @Override
    public void removeControl(@Nonnull IControl control) {
        this.getActivePanel().ifPresent(p -> p.removeControl(control));
    }

    @Override
    public void removeControls() {
        this.getActivePanel().ifPresent(AbstractControlContainer::removeControls);
    }

    @Override
    public int getControlsCount() {
        return this.getActivePanel().map(AbstractControlContainer::getControlsCount).orElse(0);
    }

    @Override
    public ILayoutEngine getLayoutEngine() {
        return this.getActivePanel().map(AbstractControlContainer::getLayoutEngine).orElse((cc) -> {});
    }

    @Override
    public void setLayoutEngine(ILayoutEngine engine) {
        this.getActivePanel().ifPresent(p -> p.setLayoutEngine(engine));
    }

    @Override
    public void validate(final Consumer<ITextComponent> errorReport) {
        this.getActivePanel().ifPresent(p -> p.validate(errorReport));
    }

    //endregion
    //region AbstractCompoundControl

    @Override
    protected void setControlOrigin(final Point origin) {
        this._panels.stream().forEach(p -> p.setControlOrigin(origin));
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" indices:")
                .append(this._panels.getValidIndices())
                .append(" active:")
                .append(this.getActivePanel().map(Objects::toString).orElse(""));
    }

    //endregion
    //region internals

    private PanelGroup(final ModContainerScreen<? extends ModContainer> gui, final String name,
                       final EnumIndexedArray<Index, Panel> panels) {

        super(gui, name);
        this._panels = panels;
        this._activePanel = null;

        this.PrePanelChange = new Event<>();
        this.PostPanelChange = new Event<>();
    }

    private final EnumIndexedArray<Index, Panel> _panels;

    private Index _activePanel;

    //endregion
}

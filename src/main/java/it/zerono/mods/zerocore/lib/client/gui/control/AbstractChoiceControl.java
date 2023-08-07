/*
 *
 * ChoiceText.java
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

import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.data.EnumIndexerSelection;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import net.minecraft.util.Direction;

import java.util.List;
import java.util.Optional;

public abstract class AbstractChoiceControl<Index extends Enum<Index>, Value, Control extends IControl>
        extends AbstractCompositeControl {

    protected AbstractChoiceControl(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                    final Control valueControl, final EnumIndexedArray<Index, Value> indexedArray) {

        super(gui, name);
        this._values = indexedArray;
        this._selected = new EnumIndexerSelection<>(indexedArray);

        this._updown = new UpDown(gui, "ud");
        this._updown.Clicked.subscribe(this::changeSelection);
        this._valueControl = valueControl;
        this.addChildControl(this._valueControl, this._updown);
    }

    public Index getSelectedIndex() {
        return this._selected.getSelection();
    }

    public void setSelectedIndex(final Index index) {

        this._selected.setSelection(index);
        this.onSelectionChanged(index, this._valueControl);
    }

    public void bindSelectedIndex(final IBindableData<Index> bindableIndex) {
        bindableIndex.bind(this::setSelectedIndex);
    }

    public Optional<Value> getSelectedValue() {
        return this._values.getElement(this.getSelectedIndex());
    }

    public List<Index> getValidIndices() {
        return this._values.getValidIndices();
    }

    protected abstract void onSelectionChanged(Index newSelection, Control valueControl);

    protected Control getValueControl() {
        return this._valueControl;
    }

    protected void setValue(final Index index, final Value value) {
        this._values.setElement(index, value);
    }

    //region AbstractCompoundControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        final Padding padding = this.getPadding();
        final int h = bounds.Height - padding.getBottom() - padding.getTop();
        final int valueWidth = bounds.Width - h - padding.getRight() - padding.getLeft();

        this._valueControl.setBounds(new Rectangle(padding.getLeft(), padding.getTop(), valueWidth, h));
        this._updown.setBounds(new Rectangle(valueWidth + padding.getLeft(), padding.getTop(), h, h));
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" indices:[")
                .append(this._values.getValidIndices())
                .append("]; sel:")
                .append(this._selected.getSelection());
    }

    //endregion
    //region internals

    private void changeSelection(final Direction.AxisDirection changeDirection, Integer buttonClicked) {

        switch (changeDirection) {

            case POSITIVE:
                this.onSelectionChanged(this._selected.selectNext(), this._valueControl);
                break;

            case NEGATIVE:
                this.onSelectionChanged(this._selected.selectPrevious(), this._valueControl);
                break;
        }
    }

    private final EnumIndexedArray<Index, Value> _values;
    private final EnumIndexerSelection<Index> _selected;
    private final UpDown _updown;
    private final Control _valueControl;

    //endregion
}

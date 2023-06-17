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

import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class ChoiceText<Index extends Enum<Index>>
    extends AbstractChoiceControl<Index, String, Label> {

    public final IEvent<BiConsumer<ChoiceText<Index>, Index>> Changed;

    public ChoiceText(final ModContainerScreen<? extends ModContainer> gui, final String name, final Index validIndex) {
        this(gui, name, new EnumIndexedArray<>(String[]::new, validIndex));
    }

    @SafeVarargs
    public ChoiceText(final ModContainerScreen<? extends ModContainer> gui, final String name,
                      final Index firstValidIndex, final Index secondValidIndex, final Index... otherValidIndices) {
        this(gui, name, new EnumIndexedArray<>(String[]::new, firstValidIndex, secondValidIndex, otherValidIndices));
    }

    public ChoiceText(final ModContainerScreen<? extends ModContainer> gui, final String name, final Iterable<Index> validIndices) {
        this(gui, name, new EnumIndexedArray<>(String[]::new, validIndices));
    }

    public void addText(final Index index, final String text) {
        this.setValue(index, text);
    }

    public void addText(final Index index, final Component text) {
        this.setValue(index, text./*getFormattedText*/getString());
    }

    //region AbstractChoiceControl

    @Override
    protected void onSelectionChanged(final Index newSelection, final Label valueControl) {

        this.getSelectedValue().ifPresent(valueControl::setText);
        this.Changed.raise(c -> c.accept(this, newSelection));
    }

    @Override
    public void onPaintBackground(final GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {

        super.onPaintBackground(gfx, partialTicks, mouseX, mouseY);
        this.paintHollowRect(gfx, 0, 0, this.getBounds().Width, this.getBounds().Height, this.getTheme().DARK_OUTLINE_COLOR);
    }

    //endregion
    //region internals

    private ChoiceText(final ModContainerScreen<? extends ModContainer> gui, final String name,
                       final EnumIndexedArray<Index, String> indexedArray) {

        super(gui, name, new Label(gui, "value", ""), indexedArray);

        this.setPadding(3, 2, 2, 2);
        this.setBackground(this.getTheme().FLAT_BACKGROUND_COLOR);
        this.Changed = new Event<>();
    }

    //endregion
}

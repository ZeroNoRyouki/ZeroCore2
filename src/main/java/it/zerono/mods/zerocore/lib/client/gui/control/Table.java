/*
 *
 * Table.java
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
import it.zerono.mods.zerocore.lib.client.gui.layout.TabularLayoutEngine;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.Util;
import net.minecraftforge.common.util.NonNullConsumer;

public class Table
        extends AbstractControlContainer {

    public Table(final ModContainerScreen<? extends ModContainer> gui, final String name,
                 final NonNullConsumer<ITableLayoutBuilder> builder) {

        super(gui, name);
        this.setLayoutEngine(Util.make(TabularLayoutEngine.builder(), builder::accept).build());
    }

    public void addCellContent(final IControl content) {
        this.addChildControl(content);
    }

    public void addCellContent(final IControl content, final NonNullConsumer<ITableCellLayoutBuilder> cellBuilder) {

        content.setLayoutEngineHint(Util.make(TabularLayoutEngine.hintBuilder(), cellBuilder::accept).build());
        this.addChildControl(content);
    }

    public void addEmptyCell() {
        this.addChildControl(new Static(this.getGui(), 0, 0));
    }

    public void addEmptyCell(final NonNullConsumer<ITableCellLayoutBuilder> cellBuilder) {

        final Static empty = new Static(this.getGui(), 0, 0);

        empty.setLayoutEngineHint(Util.make(TabularLayoutEngine.hintBuilder(), cellBuilder::accept).build());
        this.addChildControl(empty);
    }
}

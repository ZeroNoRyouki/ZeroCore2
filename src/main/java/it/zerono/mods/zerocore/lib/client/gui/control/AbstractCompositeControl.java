/*
 *
 * AbstractCompositeControl.java
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
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

public class AbstractCompositeControl
    extends AbstractCompoundControl {

    //region AbstractCompoundControl

    @Override
    public void setTooltips(final List<ITextComponent> lines) {
        this.setTooltips(lines, Collections.emptyList());
    }

    @Override
    public void setTooltips(final List<ITextComponent> lines, final List<Object> objects) {
        this.forEach(c -> c.setTooltips(lines, objects));
    }

    //endregion
    //region internals

    protected AbstractCompositeControl(final ModContainerScreen<? extends ModContainer> gui, final String name) {
        super(gui, name);
    }

    //endregion
}

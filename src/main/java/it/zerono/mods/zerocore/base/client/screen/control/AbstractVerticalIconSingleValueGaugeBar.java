/*
 *
 * AbstractVerticalIconSingleValueGaugeBar.java
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

import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.GaugeBar;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import net.minecraftforge.common.util.NonNullSupplier;

public class AbstractVerticalIconSingleValueGaugeBar
        extends AbstractVerticalIconGaugeBar<GaugeBar> {

    protected AbstractVerticalIconSingleValueGaugeBar(ModContainerScreen<? extends ModContainer> gui, String name,
                                                      double maxValue, IBindableData<Double> bindableValue,
                                                      NonNullSupplier<ISprite> barSprite, NonNullSupplier<ISprite> iconSprite) {
        super(gui, name, new GaugeBar(gui, "bar", maxValue, bindableValue, barSprite.get()), iconSprite);
    }

    protected AbstractVerticalIconSingleValueGaugeBar(ModContainerScreen<? extends ModContainer> gui, String name,
                                                      float maxValue, IBindableData<Float> bindableValue,
                                                      NonNullSupplier<ISprite> barSprite, NonNullSupplier<ISprite> iconSprite) {
        super(gui, name, new GaugeBar(gui, "bar", maxValue, bindableValue, barSprite.get()), iconSprite);
    }

    protected AbstractVerticalIconSingleValueGaugeBar(ModContainerScreen<? extends ModContainer> gui, String name,
                                                      int maxValue, IBindableData<Integer> bindableValue,
                                                      NonNullSupplier<ISprite> barSprite, NonNullSupplier<ISprite> iconSprite) {
        super(gui, name, new GaugeBar(gui, "bar", maxValue, bindableValue, barSprite.get()), iconSprite);
    }
}

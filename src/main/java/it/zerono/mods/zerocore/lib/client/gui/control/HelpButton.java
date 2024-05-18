/*
 *
 * HelpButton.java
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

import com.google.common.collect.ImmutableList;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HelpButton
    extends Button {

    public static HelpButton patchouli(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                       final ResourceLocation bookId, final ResourceLocation entryId, final int pageNum) {

        final HelpButton button = new HelpButton(gui, name);

        if (IPatchouliService.SERVICE.isModLoaded()) {

            button.Clicked.subscribe((control, mb) -> control.enqueueTask(() ->
                    IPatchouliService.SERVICE.get().openBookEntry(bookId, entryId, pageNum)));
            button.setTooltips(ImmutableList.of(Component.translatable("zerocore:gui.manual.open")));

        } else {

            button.setTooltips(ImmutableList.of(Component.translatable("zerocore:gui.patchouli.missing")));
            button.setEnabled(false);
        }

        return button;
    }

    //region internals

    protected HelpButton(final ModContainerScreen<? extends ModContainer> gui, final String name) {

        super(gui, name, "?");
        this.setDesiredDimension(14, 14);
    }

    //endregion
}

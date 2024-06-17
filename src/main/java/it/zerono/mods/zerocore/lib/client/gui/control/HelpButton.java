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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HelpButton
    extends Button {

    public static HelpButton patchouli(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                       final ResourceLocation bookId, final ResourceLocation entryId, final int pageNum) {

        final HelpButton button = new HelpButton(gui, name, "?",
                () -> IPatchouliService.SERVICE.get().openBookEntry(bookId, entryId, pageNum));

        if (IPatchouliService.SERVICE.isModLoaded()) {

            button.Clicked.subscribe(button::onClick);
            button.setTooltips(ImmutableList.of(Component.translatable("zerocore:gui.manual.open")));

        } else {

            button.setTooltips(ImmutableList.of(Component.translatable("zerocore:gui.patchouli.missing")));
            button.setEnabled(false);
        }

        return button;
    }

    public static HelpButton jeiRecipes(ModContainerScreen<? extends ModContainer> gui, String name,
                                        String tooltipTranslationKey, Runnable onClick) {

        final HelpButton button = new HelpButton(gui, name, "#", onClick);

        if (CodeHelper.isModLoaded("jei")) {

            button.Clicked.subscribe(button::onClick);
            button.setTooltips(ImmutableList.of(Component.translatable(tooltipTranslationKey)));

        } else {

            button.setTooltips(ImmutableList.of(Component.translatable("zerocore:gui.jei.missing")));
            button.setEnabled(false);
        }

        return button;
    }

    //region internals

    protected HelpButton(ModContainerScreen<? extends ModContainer> gui, String name, String label, Runnable onClick) {

        super(gui, name, label);

        Preconditions.checkNotNull(onClick, "On Click must not be null");
        this._onClick = onClick;

        this.setDesiredDimension(14, 14);
    }

    private void onClick(Button button, int mouseButton) {

        button.enqueueTask(() -> {

            this._onClick.run();
            this.setMouseOver(false, 0, 0);

        });
    }

    protected final Runnable _onClick;

    //endregion
}

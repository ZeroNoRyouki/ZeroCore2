/*
 *
 * BaseClientHelper.java
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

package it.zerono.mods.zerocore.base.client.screen;

import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractButtonControl;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ClientBaseHelper {

    public static final int LABEL_HEIGHT = 10;
    public static final int PUSH_BUTTON_HEIGHT = 16;
    public static final int SQUARE_BUTTON_DIMENSION = 18;
    public static final int INVENTORY_SLOTS_ROW_WIDTH = 162;

    public static MutableComponent formatAsTitle(MutableComponent text) {
        return text.setStyle(CommonConstants.STYLE_TOOLTIP_TITLE);
    }

    public static MutableComponent formatAsValue(MutableComponent text) {
        return text.setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    }

    public static MutableComponent formatAsInfo(MutableComponent text) {
        return text.setStyle(CommonConstants.STYLE_TOOLTIP_INFO);
    }

    public static void setButtonSpritesAndOverlayForState(AbstractButtonControl button, ButtonState standardState,
                                                          Supplier<@NotNull ISprite> standardSprite) {
        setButtonSpritesAndOverlayForState(button, standardState,standardSprite.get());
    }

    public static void setButtonSpritesAndOverlayForState(AbstractButtonControl button, ButtonState standardState,
                                                          ISprite standardSprite) {

        button.setIconForState(standardSprite, standardState);

        ISprite withOverlay;

        withOverlay = standardSprite.copyWith(BaseIcons.Button16x16HightlightOverlay.get());
        button.setIconForState(withOverlay, standardState.getHighlighted());

        withOverlay = standardSprite.copyWith(BaseIcons.Button16x16DisabledOverlay.get());
        button.setIconForState(withOverlay, standardState.getDisabled());
    }

    //region internals

    private ClientBaseHelper() {
    }

    //endregion
}

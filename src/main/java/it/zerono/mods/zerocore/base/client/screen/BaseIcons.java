/*
 *
 * BaseIcons.java
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

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteBuilder;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;

public enum BaseIcons
        implements NonNullSupplier<ISprite> {

    Button16x16HightlightOverlay(builder().from(0, 0).build()),
    Button16x16DisabledOverlay(builder().from(16, 0).build()),
    Button16x16Background(builder().from(32, 0).build()),
    Button16x16BackgroundActive(builder().from(48, 0).build()),

    ButtonInputDirection(builder().from(64, 0).build()),
    ButtonInputDirectionActive(builder().from(80, 0).build()),
    ButtonOutputDirection(builder().from(96, 0).build()),
    ButtonOutputDirectionActive(builder().from(112, 0).build()),

    ImageButtonBorder(builder().from(174, 0).ofSize(18, 18).build()),
    ImageButtonBackground(builder().from(174, 18).ofSize(18, 18).build()),

    MachineStatusOff(builder().from(0, 16).ofSize(10, 10).build()),
    MachineStatusOn(builder().from(16, 16).ofSize(10, 10).build()),

    PowerBattery(builder().from(128, 96).build()),
    LabelEdit(builder().from(32, 16).build()),

    BarBackground(builder().from(174, 126).ofSize(18, 66).build()),
    PowerBar(builder().from(0, 112).ofSize(16, 64).build()),

    ;

    BaseIcons(final ISprite sprite) {
        this._sprite = sprite;
    }

    //region NonNullSupplier<ISprite>

    @Override
    @Nonnull
    public ISprite get() {
        return this._sprite;
    }

    //endregion
    //region internals

    private static ISpriteBuilder builder() {
        return getMap().sprite();
    }

    private static ISpriteTextureMap getMap() {

        if (null == s_map) {
            s_map = new SpriteTextureMap(ZeroCore.newID("textures/gui/base_icons.png"), 192, 192);
        }

        return s_map;
    }

    private static ISpriteTextureMap s_map;
    private final ISprite _sprite;

    //endregion
}

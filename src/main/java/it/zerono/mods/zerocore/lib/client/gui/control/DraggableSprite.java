/*
 *
 * DraggableSprite.java
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

import it.zerono.mods.zerocore.lib.client.gui.IDraggable;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class DraggableSprite
        extends Sprite
        implements IDraggable {

    public DraggableSprite(final ISprite sprite) {
        super(sprite);
    }

    public DraggableSprite(final ISpriteTextureMap textureMap) {
        super(16, 16, textureMap);
    }

    public DraggableSprite(final int width, final int height, final ISpriteTextureMap textureMap) {
        super(width, height, textureMap);
    }

    public DraggableSprite(final int width, final int height, final ISpriteTextureMap textureMap,
                           @Nullable final ISprite overlay) {
        super(width, height, textureMap, overlay);
    }

    public DraggableSprite(final int width, final int height, final ISpriteTextureMap textureMap, final int u, final int v) {
        super(width, height, textureMap, u, v, null);
    }

    public DraggableSprite(final int width, final int height, final ISpriteTextureMap textureMap,
                           final int u, final int v, @Nullable final ISprite overlay) {
        super(width, height,textureMap, u, v, overlay);
    }

    //region IDraggable

    @Override
    public void onPaint(final GuiGraphics gfx, final int x, final int y, final float zLevel, final PaintState paintState) {
        ModRenderHelper.paintSprite(gfx, this, new Point(x, y), (int)zLevel, this.getWidth(), this.getHeight()); //TODO new Point arg!!
    }

    //endregion
}

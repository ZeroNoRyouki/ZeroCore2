/*
 *
 * ISprite.java
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

package it.zerono.mods.zerocore.lib.client.gui.sprite;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Optional;

public interface ISprite {

    int getWidth();

    int getHeight();

    int getU();

    int getV();

    float getMinU();

    float getMaxU();

    float getMinV();

    float getMaxV();

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    default float getInterpolatedU(double u) {
        return this.getMinU() + (this.getMaxU() - this.getMinU()) * (float)u / 16.0F;
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    default float getInterpolatedV(double v) {
        return this.getMinV() + (this.getMaxV() - this.getMinV()) * (float)v / 16.0F;
    }

    /**
     * Get the TextureAtlasSprite wrapped by this ISprite if one is available
     *
     * @return the wrapped TextureAtlasSprite or an empty Optional
     */
    default Optional<TextureAtlasSprite> getAtlasSprite() {
        return Optional.empty();
    }

    ISpriteTextureMap getTextureMap();

//    int getTextureMapOffsetX();
//
//    int getTextureMapOffsetY();

    Optional<ISprite> getSpriteOverlay();

    void applyOverlay(NonNullConsumer<ISprite> overlayConsumer);

    ISprite copy();

    ISprite copyWith(ISprite overlay);
}

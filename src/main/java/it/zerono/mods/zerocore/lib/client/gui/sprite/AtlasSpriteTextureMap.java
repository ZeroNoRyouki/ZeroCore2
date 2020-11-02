/*
 *
 * AtlasSpriteTextureMap.java
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Optional;

public class AtlasSpriteTextureMap implements ISpriteTextureMap {

    public static final AtlasSpriteTextureMap BLOCKS = new AtlasSpriteTextureMap(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

    public AtlasSpriteTextureMap(final ResourceLocation atlasName) {
        this._atlasName = atlasName;
    }

    public ISprite sprite(final ResourceLocation spriteName) {
        return new AtlasSprite(this, Minecraft.getInstance().getAtlasSpriteGetter(this._atlasName).apply(spriteName));
    }

    //region ISpriteTextureMap

    @Override
    public ResourceLocation getTextureLocation() {
        return this._atlasName;
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public int getHeight() {
        return -1;
    }

    @Override
    public ISpriteBuilder sprite() {
        throw new UnsupportedOperationException();
    }

    //endregion
    //region internals
    //region AtlasSprite

    static class AtlasSprite implements ISprite {

        public AtlasSprite(final ISpriteTextureMap textureMap, final TextureAtlasSprite sprite) {

            this._map = textureMap;
            this._atlasSprite = sprite;
            this._overlay = null;
        }

        //region ISprite

        @Override
        public int getWidth() {
            return this._atlasSprite.getWidth();
        }

        @Override
        public int getHeight() {
            return this._atlasSprite.getHeight();
        }

        @Override
        public int getU() {
            return -1;
        }

        @Override
        public int getV() {
            return -1;
        }

        @Override
        public float getMinU() {
            return this._atlasSprite.getMinU();
        }

        @Override
        public float getMaxU() {
            return this._atlasSprite.getMaxU();
        }

        @Override
        public float getMinV() {
            return this._atlasSprite.getMinV();
        }

        @Override
        public float getMaxV() {
            return this._atlasSprite.getMaxV();
        }

        /**
         * Get the TextureAtlasSprite wrapped by this ISprite if one is available
         *
         * @return the wrapped TextureAtlasSprite or an empty Optional
         */
        @Override
        public Optional<TextureAtlasSprite> getAtlasSprite() {
            return Optional.of(this._atlasSprite);
        }

        @Override
        public ISpriteTextureMap getTextureMap() {
            return this._map;
        }

        @Override
        public Optional<ISprite> getSpriteOverlay() {
            return Optional.ofNullable(this._overlay);
        }

        @Override
        public void applyOverlay(final NonNullConsumer<ISprite> overlayConsumer) {

            if (null != this._overlay) {
                overlayConsumer.accept(this._overlay);
            }
        }

        @Override
        public ISprite copy() {
            return new AtlasSprite(this);
        }

        @Override
        public ISprite copyWith(ISprite overlay) {
            return new AtlasSprite(this, overlay);
        }

        //endregion
        //region internals

        protected AtlasSprite(final AtlasSprite other) {

            this._map = other._map;
            this._atlasSprite = other._atlasSprite;
            this._overlay = other._overlay;
        }

        protected AtlasSprite(final AtlasSprite other, final ISprite overlay) {

            this._map = other._map;
            this._atlasSprite = other._atlasSprite;
            this._overlay = overlay;
        }

        private final ISpriteTextureMap _map;
        private final TextureAtlasSprite _atlasSprite;
        private final ISprite _overlay;

        //endregion
    }

    //endregion

    private final ResourceLocation _atlasName;

    //endregion
}

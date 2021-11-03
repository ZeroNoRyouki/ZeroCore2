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

import it.zerono.mods.zerocore.internal.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;

public class AtlasSpriteTextureMap
        implements ISpriteTextureMap {

    public static final AtlasSpriteTextureMap BLOCKS = new AtlasSpriteTextureMap(PlayerContainer.BLOCK_ATLAS);

    public static AtlasSpriteTextureMap from(final TextureAtlasSprite sprite) {

        final ResourceLocation id = sprite.atlas().location();

        if (BLOCKS.getTextureLocation().equals(id)) {
            return BLOCKS;
        } else {
            return new AtlasSpriteTextureMap(id);
        }
    }

    public AtlasSpriteTextureMap(final ResourceLocation atlasName) {

        this._atlasName = atlasName;
        this._atlasWidth = this._atlasHeight = -1;
    }

    public ISprite sprite(final ResourceLocation spriteName) {
        return this.sprite(Minecraft.getInstance().getTextureAtlas(this._atlasName).apply(spriteName));
    }

    public ISprite sprite(final TextureAtlasSprite sprite, final ISprite overlay) {
        return this.makeSprite(sprite, overlay);
    }

    public ISprite sprite(final TextureAtlasSprite sprite) {
        return this.makeSprite(sprite, null);
    }

    //region ISpriteTextureMap

    @Override
    public ResourceLocation getTextureLocation() {
        return this._atlasName;
    }

    @Override
    public int getWidth() {
        return this._atlasWidth;
    }

    @Override
    public int getHeight() {
        return this._atlasHeight;
    }

    @Override
    public ISpriteBuilder sprite() {
        throw new UnsupportedOperationException();
    }

    //endregion
    //region internals
    //region AtlasSprite

    static class AtlasSprite
            implements ISprite {

        public AtlasSprite(final ISpriteTextureMap textureMap, final TextureAtlasSprite sprite,
                           final int u, final int v, @Nullable final ISprite overlay) {

            this._map = textureMap;
            this._atlasSprite = sprite;
            this._overlay = overlay;
            this._u = u;
            this._v = v;
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
            return this._u;
        }

        @Override
        public int getV() {
            return this._v;
        }

        @Override
        public float getMinU() {
            return this._atlasSprite.getU0();
        }

        @Override
        public float getMaxU() {
            return this._atlasSprite.getU1();
        }

        @Override
        public float getMinV() {
            return this._atlasSprite.getV0();
        }

        @Override
        public float getMaxV() {
            return this._atlasSprite.getV1();
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
            this(other, null);
        }

        protected AtlasSprite(final AtlasSprite other, @Nullable final ISprite overlay) {

            this._map = other._map;
            this._atlasSprite = other._atlasSprite;
            this._overlay = overlay;
            this._u = other._u;
            this._v = other._v;
        }

        private final ISpriteTextureMap _map;
        private final TextureAtlasSprite _atlasSprite;
        private final ISprite _overlay;
        private final int _u, _v;

        //endregion
    }

    //endregion

    private ISprite makeSprite(final TextureAtlasSprite sprite, @Nullable final ISprite overlay) {

        int spriteX, spriteY;

        try {

            spriteX = s_xField.getInt(sprite);
            spriteY = s_yField.getInt(sprite);

        } catch (IllegalAccessException e) {

            Log.LOGGER.warn(Log.CORE, "Unable to get the value of field x or y for a TextureAtlasSprite");
            spriteX = spriteY = 0;
        }

        if (this._atlasHeight < 0 || this._atlasWidth < 0) {

            this._atlasWidth = (int)(sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
            this._atlasHeight = (int)(sprite.getHeight() / (sprite.getV1() - sprite.getV0()));
        }

        return new AtlasSprite(this, sprite, spriteX, spriteY, overlay);
    }

    private final ResourceLocation _atlasName;
    private int _atlasWidth;
    private int _atlasHeight;

    private static final Field s_xField;
    private static final Field s_yField;

    static {

        s_xField = getField("field_110975_c"); // x
        s_yField = getField("field_110974_d"); // y
    }

    @Nullable
    private static Field getField(final String name) {

        try {

            return ObfuscationReflectionHelper.findField(TextureAtlasSprite.class, name);

        } catch (ObfuscationReflectionHelper.UnableToFindFieldException ex) {

            Log.LOGGER.error(Log.CORE, "AtlasSpriteTextureMap - Unable to get field {} : {}", name, ex);
            return null;
        }
    }

    //endregion
}

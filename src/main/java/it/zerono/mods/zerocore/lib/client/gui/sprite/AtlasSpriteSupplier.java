/*
 *
 * AtlasSpriteSupplier.java
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AtlasSpriteSupplier implements ResourceManagerReloadListener {

    public static final AtlasSpriteSupplier INSTANCE = new AtlasSpriteSupplier();

    public static Supplier<ISprite> create(final ResourceLocation spriteName, final AtlasSpriteTextureMap map) {
        return create(spriteName, map, false);
    }

    public static Supplier<ISprite> create(final ResourceLocation spriteName, final AtlasSpriteTextureMap map,
                                           final boolean stitch) {

        if (stitch) {
            INSTANCE._toBeStitched.computeIfAbsent(map.getTextureLocation(), loc -> Lists.newArrayList())
                    .add(spriteName);
        }

        return new AtlasSpriteSupplier.SpriteSupplier(spriteName, map);
    }

    //region ResourceManagerReloadListener

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        ++this._generation;
    }

    //endregion
    //region event handlers

    @SubscribeEvent
    public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
        
        final ResourceLocation atlasName = evt.getAtlas().location();

        if (this._toBeStitched.containsKey(atlasName)) {
            this._toBeStitched.get(atlasName).forEach(evt::addSprite);
        }
    }

    //endregion
    //region internals
    //region SpriteSupplier

    private static class SpriteSupplier implements Supplier<ISprite> {

        protected SpriteSupplier(ResourceLocation name, AtlasSpriteTextureMap map) {

            this._name = name;
            this._map = map;
            this._lastKnownGeneration = -1;
            this._cachedSprite = Sprite.EMPTY;
        }

        //region Supplier

        @Override
        public ISprite get() {

            if (this._lastKnownGeneration != INSTANCE._generation) {

                this._cachedSprite = this._map.sprite(this._name);
                this._lastKnownGeneration = INSTANCE._generation;
            }

            return this._cachedSprite;
        }

        //endregion
        //region internals

        private final AtlasSpriteTextureMap _map;
        private final ResourceLocation _name;
        private int _lastKnownGeneration;
        private ISprite _cachedSprite;

        //endregion
    }

    //endregion

    private AtlasSpriteSupplier() {

        this._toBeStitched = Maps.newHashMap();
        this._generation = 0;
    }

    private final Map<ResourceLocation, List<ResourceLocation>> _toBeStitched;
    private int _generation;

    //endregion
}

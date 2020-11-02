/*
 *
 * VertexBuilder.java
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

package it.zerono.mods.zerocore.lib.client.render.builder;

import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.Vertex;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "WeakerAccess"})
public class VertexBuilder implements IPrimitiveBuilder<Vertex> {

    public static VertexBuilder getDefaultBuilder() {

        if (null == s_defaultBuilder) {
            s_defaultBuilder = new VertexBuilder(false);
        }

        return s_defaultBuilder;
    }

    public VertexBuilder(final boolean autoReset) {
        this._autoReset = autoReset;
    }

    //region IPrimitiveBuilder<Vertex>

    @Override
    public Vertex build() {

        final Vertex vertex = new Vertex(this._position, this._normal, this._uv, this._colour,
                this._lightMap, this._overlayMap);

        vertex.IDX = this._idx;

        if (this._autoReset) {
            this.reset();
        }

        return vertex;
    }

    public void reset() {

        this._idx = 0;
        this._position = null;
        this._normal = null;
        this._uv = null;
        this._colour = null;
        this._lightMap = this._overlayMap = null;
    }

    //endregion

    public VertexBuilder setIdx(final int idx) {

        this._idx = idx;
        return this;
    }

    // Texture

    public VertexBuilder setTexture(final UV uv) {

        this._uv = uv;
        return this;
    }

    public VertexBuilder setTexture(final float u, final float v) {
        return this.setTexture(new UV(u, v));
    }

    public VertexBuilder setTexture(final TextureAtlasSprite sprite) {
        return this.setTexture(new UV(sprite.getMinU(), sprite.getMinV()));
    }

    public VertexBuilder setTexture(final ISprite sprite) {
        return this.setTexture(new UV(sprite.getMinU(), sprite.getMinV()));
    }

    public VertexBuilder setTexture(final Supplier<ISprite> sprite) {
        return this.setTexture(sprite.get());
    }

    // Light-map

    public VertexBuilder setLightMap(final LightMap map) {

        this._lightMap = map;
        return this;
    }

    public VertexBuilder setLightMap(final int sky, final int block) {
        return this.setLightMap(new LightMap(sky, block));
    }

    public VertexBuilder setLightMap(final int combined) {
        return this.setLightMap(new LightMap(combined));
    }

    // Overlay-map

    public VertexBuilder setOverlayMap(final LightMap map) {

        this._overlayMap = map;
        return this;
    }

    public VertexBuilder setOverlayMap(final int sky, final int block) {
        return this.setOverlayMap(new LightMap(sky, block));
    }

    public VertexBuilder setOverlayMap(final int combined) {
        return this.setOverlayMap(new LightMap(combined));
    }

    // Color

    public VertexBuilder setColour(final Colour colour) {

        this._colour = colour;
        return this;
    }

    public VertexBuilder setColour(final int red, final int green, final int blue, final int alpha) {
        return this.setColour(new Colour(red, green, blue, alpha));
    }

    public VertexBuilder setColour(final double red, final double green, final double blue, final double alpha) {
        return this.setColour(new Colour(red, green, blue, alpha));
    }

    // Position

    public VertexBuilder setPosition(final Vector3d position) {

        this._position = position;
        return this;
    }

    public VertexBuilder setPosition(final double x, final double y, final double z) {
        return this.setPosition(new Vector3d(x, y, z));
    }

    // Normal

    public VertexBuilder setNormal(final Vector3f normal) {

        this._normal = normal;
        return this;
    }

    public VertexBuilder setNormal(final int x, final int y, final int z) {
        return this.setNormal(new Vector3f(x, y, z));
    }

    //region internals

    private final boolean _autoReset;
    private int _idx;
    private Vector3d _position;
    private Vector3f _normal;
    private UV _uv;
    private Colour _colour;
    private LightMap _lightMap;
    private LightMap _overlayMap;

    private static VertexBuilder s_defaultBuilder = null;

    //endregion
}

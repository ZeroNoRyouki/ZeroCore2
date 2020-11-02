/*
 *
 * AbstractShapeBuilder.java
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
import it.zerono.mods.zerocore.lib.client.render.Shape;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractShapeBuilder implements IPrimitiveBuilder<Shape> {

    protected AbstractShapeBuilder(final boolean autoReset) {
        this._autoReset = autoReset;
    }

    protected boolean autoReset() {
        return this._autoReset;
    }

    private final boolean _autoReset;

    protected static class PolygonalFaceData {

        public final int VERTICES_COUNT;

        public final Vector3d[] NORMALS;
        public final UV[] UV_MAP;
        public final Colour[] COLOURS;
        public final LightMap[] LIGHT_MAPS;
        public final LightMap[] OVERLAY_MAPS;

        public PolygonalFaceData(final int vertexCount) {

            this.VERTICES_COUNT = vertexCount;
            this.NORMALS = new Vector3d[vertexCount];
            this.UV_MAP = new UV[vertexCount];
            this.COLOURS = new Colour[vertexCount];
            this.LIGHT_MAPS = new LightMap[vertexCount];
            this.OVERLAY_MAPS = new LightMap[vertexCount];

            this._filledElements = EnumSet.noneOf(VertexElementType.class);
        }

        public void reset() {

            this._filledElements.clear();

            for (int idx = 0; idx < this.VERTICES_COUNT; ++idx) {

                this.NORMALS[idx] = null;
                this.UV_MAP[idx] = null;
                this.COLOURS[idx] = null;
                this.LIGHT_MAPS[idx] = null;
                this.OVERLAY_MAPS[idx] = null;
            }
        }

        public Optional<Vector3d> getNormalAt(final int vertexIndex) {
            return this.checkElement(VertexElementType.Normal) ? Optional.ofNullable(this.NORMALS[vertexIndex]) : Optional.empty();
        }

        public Optional<UV> getUvAt(final int vertexIndex) {
            return this.checkElement(VertexElementType.Texture) ? Optional.ofNullable(this.UV_MAP[vertexIndex]) : Optional.empty();
        }

        public Optional<Colour> getColourlAt(final int vertexIndex) {
            return this.checkElement(VertexElementType.Colour) ? Optional.ofNullable(this.COLOURS[vertexIndex]) : Optional.empty();
        }

        public Optional<LightMap> getLightMapAt(final int vertexIndex) {
            return this.checkElement(VertexElementType.LightMap) ? Optional.ofNullable(this.LIGHT_MAPS[vertexIndex]) : Optional.empty();
        }

        public Optional<LightMap> getOverlayMapAt(final int vertexIndex) {
            return this.checkElement(VertexElementType.OverlayMap) ? Optional.ofNullable(this.OVERLAY_MAPS[vertexIndex]) : Optional.empty();
        }

        public PolygonalFaceData setNormal(final Vector3d normal) {

            Arrays.fill(this.NORMALS, normal);
            this.addElement(VertexElementType.Normal);
            return this;
        }

        public PolygonalFaceData setNormal(final int vertexIndex, final Vector3d normal) {

            this.NORMALS[vertexIndex] = normal;
            this.addElement(VertexElementType.Normal);
            return this;
        }

        public PolygonalFaceData setTexture(final UV texture) {

            Arrays.fill(this.UV_MAP, texture);
            this.addElement(VertexElementType.Texture);
            return this;
        }

        public PolygonalFaceData setTexture(final float u, final float v) {
            return this.setTexture(new UV(u, v));
        }

        public PolygonalFaceData setTexture(final int vertexIndex, final UV texture) {

            this.UV_MAP[vertexIndex] = texture;
            this.addElement(VertexElementType.Texture);
            return this;
        }

        public PolygonalFaceData setTexture(final int vertexIndex, final float u, final float v) {
            return this.setTexture(vertexIndex, new UV(u, v));
        }

        public PolygonalFaceData setTexture(final TextureAtlasSprite sprite) {

            if (4 != this.VERTICES_COUNT) {
                throw new IllegalArgumentException("This polygonal face does not have 4 vertices");
            }

            this.setTexture(0, sprite.getMinU(), sprite.getMinV());
            this.setTexture(1, sprite.getMinU(), sprite.getMaxV());
            this.setTexture(2, sprite.getMaxU(), sprite.getMaxV());
            this.setTexture(3, sprite.getMaxU(), sprite.getMinV());
            return this;
        }

        public PolygonalFaceData setTexture(final ISprite sprite) {

            if (4 != this.VERTICES_COUNT) {
                throw new IllegalArgumentException("This polygonal face does not have 4 vertices");
            }

            this.setTexture(0, sprite.getMinU(), sprite.getMinV());
            this.setTexture(1, sprite.getMinU(), sprite.getMaxV());
            this.setTexture(2, sprite.getMaxU(), sprite.getMaxV());
            this.setTexture(3, sprite.getMaxU(), sprite.getMinV());
            return this;
        }

        public PolygonalFaceData setTexture(final Supplier<ISprite> sprite) {
            return this.setTexture(sprite.get());
        }

        public PolygonalFaceData setColour(final Colour colour) {

            Arrays.fill(this.COLOURS, colour);
            this.addElement(VertexElementType.Colour);
            return this;
        }

        public PolygonalFaceData setColour(final int vertexIndex, final Colour colour) {

            this.COLOURS[vertexIndex] = colour;
            this.addElement(VertexElementType.Colour);
            return this;
        }

        public PolygonalFaceData setLightMap(final LightMap map) {

            Arrays.fill(this.LIGHT_MAPS, map);
            this.addElement(VertexElementType.LightMap);
            return this;
        }

        public PolygonalFaceData setLightMapCombined(final int combined) {
            return this.setLightMap(new LightMap(combined));
        }

        public PolygonalFaceData setLightMap(final int vertexIndex, final LightMap map) {

            this.LIGHT_MAPS[vertexIndex] = map;
            this.addElement(VertexElementType.LightMap);
            return this;
        }

        public PolygonalFaceData setLightMapCombined(final int vertexIndex, final int combined) {
            return this.setLightMap(vertexIndex, new LightMap(combined));
        }

        public PolygonalFaceData setOverlayMap(final LightMap map) {

            Arrays.fill(this.OVERLAY_MAPS, map);
            this.addElement(VertexElementType.OverlayMap);
            return this;
        }

        public PolygonalFaceData setOverlayMapCombined(final int combined) {
            return this.setOverlayMap(new LightMap(combined));
        }

        public PolygonalFaceData setOverlayMap(final int vertexIndex, final LightMap map) {

            this.OVERLAY_MAPS[vertexIndex] = map;
            this.addElement(VertexElementType.OverlayMap);
            return this;
        }

        public PolygonalFaceData setOverlayMapCombined(final int vertexIndex, final int combined) {
            return this.setOverlayMap(vertexIndex, new LightMap(combined));
        }

        //region internals

        protected boolean checkElement(final VertexElementType element) {
            return this._filledElements.contains(element);
        }

        protected void addElement(final VertexElementType element) {
            this._filledElements.add(element);
        }

        private final EnumSet<VertexElementType> _filledElements;

        //endregion
    }
}

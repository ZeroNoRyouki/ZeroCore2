/*
 *
 * QuadBuilder.java
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
import it.zerono.mods.zerocore.lib.client.render.Vertex;
import it.zerono.mods.zerocore.lib.data.geometry.Cuboid;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "WeakerAccess"})
public class QuadBuilder extends AbstractShapeBuilder {

    public final static int VERTICES_COUNT = 4;

    public static QuadBuilder getDefaultBuilder() {

        if (null == s_defaultBuilder) {
            s_defaultBuilder = new QuadBuilder(false);
        }

        return s_defaultBuilder;
    }

    public QuadBuilder(final boolean autoReset) {

        super(autoReset);
        this._faceData = new PolygonalFaceData(VERTICES_COUNT);
    }

    //region IShapeBuilder

    @Override
    public Shape build() {

        if (null == this._face) {
            throw new IllegalStateException("No face was provided!");
        }

        final Shape shape = new Shape(VERTICES_COUNT);
        final VertexBuilder vertexBuilder = new VertexBuilder(true);

        for (int vertexIndex = VERTICES_COUNT - 1; vertexIndex >= 0; --vertexIndex) {
//        for (int vertexIndex = 0; vertexIndex < VERTICES_COUNT; ++vertexIndex) {
            shape.addVertex(buildSingleVertex(vertexIndex, vertexIndex, vertexBuilder, this._face, this._faceData));
        }

        if (this.autoReset()) {
            this.reset();
        }

        return shape;
    }

    @Override
    public void reset() {

        this._face = null;
        this._faceData.reset();
    }

    //endregion
    //region Face

    public QuadBuilder setFace(final Cuboid.Face face) {

        this._face = face;
        return this;
    }

    public QuadBuilder setFace(final Cuboid cuboid, final Direction facing) {
        return this.setFace(cuboid.getFace(facing));
    }

    //endregion
    //region Color

    public QuadBuilder setColour(final Colour colour) {

        this._faceData.setColour(colour);
        return this;
    }

    public QuadBuilder setColour(final int vertexIndex, final Colour colour) {

        this._faceData.setColour(vertexIndex, colour);
        return this;
    }

    //endregion
    //region Texture

    public QuadBuilder setTexture(final UV a, final UV b, final UV c, final UV d) {

        this._faceData.setTexture(0, a);
        this._faceData.setTexture(1, b);
        this._faceData.setTexture(2, c);
        this._faceData.setTexture(3, d);
        return this;
    }

    public QuadBuilder setTexture(final int vertexIndex, final UV uv) {

        this._faceData.setTexture(vertexIndex, uv);
        return this;
    }

    public QuadBuilder setTexture(final int vertexIndex, final float u, float v) {
        return this.setTexture(vertexIndex, new UV(u, v));
    }

    public QuadBuilder setTexture(final TextureAtlasSprite sprite) {

        this._faceData.setTexture(sprite);
        return this;
    }

    public QuadBuilder setTexture(final ISprite sprite) {

        this._faceData.setTexture(sprite);
        return this;
    }

    public QuadBuilder setTexture(final Supplier<ISprite> sprite) {

        this._faceData.setTexture(sprite);
        return this;
    }

    //endregion
    //region Light-map

    public QuadBuilder setLightMap(final LightMap map) {

        this._faceData.setLightMap(map);
        return this;
    }

    public QuadBuilder setLightMap(final int vertexIndex, final LightMap map) {

        this._faceData.setLightMap(vertexIndex, map);
        return this;
    }

    public QuadBuilder setLightMap(final int sky, final int block) {
        return this.setLightMap(new LightMap(sky, block));
    }

    public QuadBuilder setLightMap(final int vertexIndex, final int sky, final int block) {
        return this.setLightMap(vertexIndex, new LightMap(sky, block));
    }

    public QuadBuilder setLightMapCombined(final int combined) {
        return this.setLightMap(new LightMap(combined));
    }

    public QuadBuilder setLightMapCombined(final int vertexIndex, final int combined) {
        return this.setLightMap(vertexIndex, new LightMap(combined));
    }

    //endregion
    //region Overlay-map

    public QuadBuilder setOverlayMap(final LightMap map) {

        this._faceData.setOverlayMap(map);
        return this;
    }

    public QuadBuilder setOverlayMap(final int vertexIndex, final LightMap map) {

        this._faceData.setOverlayMap(vertexIndex, map);
        return this;
    }

    public QuadBuilder setOverlayMap(final int sky, final int block) {
        return this.setOverlayMap(new LightMap(sky, block));
    }

    public QuadBuilder setOverlayMapCombined(final int combined) {
        return this.setOverlayMap(new LightMap(combined));
    }

    public QuadBuilder setOverlayMap(final int vertexIndex, final int sky, final int block) {
        return this.setOverlayMap(vertexIndex, new LightMap(sky, block));
    }

    public QuadBuilder setOverlayMapCombined(final int vertexIndex, final int combined) {
        return this.setOverlayMap(vertexIndex, new LightMap(combined));
    }

    //endregion
    //region internals

    static Vertex buildSingleVertex(final int vidx,
                                    final int vertexIndex, final VertexBuilder vertexBuilder,
                                    final Cuboid.Face face, final PolygonalFaceData faceData) {

        vertexBuilder.setIdx(vidx);
        vertexBuilder.setPosition(face.getVertexByIndex(vertexIndex));

        faceData.getUvAt(vertexIndex).ifPresent(vertexBuilder::setTexture);
        faceData.getColourlAt(vertexIndex).ifPresent(vertexBuilder::setColour);
        faceData.getLightMapAt(vertexIndex).ifPresent(vertexBuilder::setLightMap);
        faceData.getOverlayMapAt(vertexIndex).ifPresent(vertexBuilder::setOverlayMap);
        //faceData.getNormalAt(vertexIndex).ifPresent(vertexBuilder::setNormal);
        vertexBuilder.setNormal(face.getNormal());

        return vertexBuilder.build();
    }

    private final PolygonalFaceData _faceData;
    private Cuboid.Face _face;

    private static QuadBuilder s_defaultBuilder = null;

    //endregion
}

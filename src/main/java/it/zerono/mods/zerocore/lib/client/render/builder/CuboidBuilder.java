/*
 *
 * CuboidBuilder.java
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

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.Shape;
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
public class CuboidBuilder extends AbstractShapeBuilder {

    public final static int VERTICES_COUNT = 8;
    public final static int FACES_COUNT = CodeHelper.DIRECTIONS.length;

    public static CuboidBuilder getDefaultBuilder() {

        if (null == s_defaultBuilder) {
            s_defaultBuilder = new CuboidBuilder(false);
        }

        return s_defaultBuilder;
    }

    public CuboidBuilder(final boolean autoReset) {

        super(autoReset);
        this._cuboidData = new PolygonalFaceData[FACES_COUNT];
        this._facesToBeRendered = BlockFacings.ALL;

        for (int faceIndex = 0; faceIndex < this._cuboidData.length; ++faceIndex) {
            this._cuboidData[faceIndex] = new PolygonalFaceData(QuadBuilder.VERTICES_COUNT);
        }
    }

    //region IPrimitiveBuilder<Shape>

    @Override
    public Shape build() {

        if (null == this._cuboid) {
            throw new IllegalStateException("No cuboid was provided!");
        }

        final Shape shape = new Shape(VERTICES_COUNT);
        final VertexBuilder vertexBuilder = new VertexBuilder(true);
        int vidx = 0;

        for (final Direction facing : CodeHelper.DIRECTIONS) {

            if (this._facesToBeRendered.isSet(facing)) {

                final PolygonalFaceData data = this._cuboidData[facing.get3DDataValue()];

                for (int vertexIndex = data.VERTICES_COUNT - 1; vertexIndex >= 0; --vertexIndex) {
                    shape.addVertex(QuadBuilder.buildSingleVertex(vidx++, vertexIndex, vertexBuilder, this._cuboid.getFace(facing), data));
                }
            }
        }

        if (this.autoReset()) {
            this.reset();
        }

        return shape;
    }

    @Override
    public void reset() {

        this._cuboid = null;
        this._facesToBeRendered = BlockFacings.ALL;

        for (final PolygonalFaceData data : this._cuboidData) {
            data.reset();
        }
    }

    //endregion

    // Cuboid to be rendered

    public CuboidBuilder setCuboid(final Cuboid cuboid) {

        this._cuboid = cuboid;
        return this;
    }

    // Faces to be rendered

    public CuboidBuilder setVisibleFaces(final BlockFacings visibleFaces) {

        this._facesToBeRendered = visibleFaces;
        return this;
    }

    public CuboidBuilder setFaceVisibility(final Direction face, final boolean visible) {

        this._facesToBeRendered = this._facesToBeRendered.set(face, visible);
        return this;
    }

    // Color

    /***
     * Set the colour for ALL the faces of the cuboid to be rendered
     * See {@link CuboidBuilder#setVisibleFaces} or {@link CuboidBuilder#setFaceVisibility}
     * @param colour    the color
     * @return the builder
     */
    public CuboidBuilder setColour(final Colour colour) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setColour(colour);
        }

        return this;
    }

    /***
     * Set the colour for the given face of the cuboid to be rendered
     * @param facing    the face
     * @param colour    the color
     * @return the builder
     */
    public CuboidBuilder setColour(final Direction facing, final Colour colour) {

        this._cuboidData[facing.get3DDataValue()].setColour(colour);
        return this;
    }

    /***
     * Set the light map for a vertex of the given face of the cuboid to be rendered
     * @param facing        the face
     * @param vertexIndex   the vertex to change
     * @param colour        the color
     * @return the builder
     */
    public CuboidBuilder setColour(final Direction facing, final int vertexIndex, final Colour colour) {

        this._cuboidData[facing.get3DDataValue()].setColour(vertexIndex, colour);
        return this;
    }

    // Texture

    /***
     * Set the texture for ALL the faces of the cuboid to be rendered
     * See {@link CuboidBuilder#setVisibleFaces} or {@link CuboidBuilder#setFaceVisibility}
     * @param a the texture map for the first vertex of a face
     * @param b the texture map for the second vertex of a face
     * @param c the texture map for the third vertex of a face
     * @param d the texture map for the forth vertex of a face
     * @return the builder
     */
    public CuboidBuilder setTexture(final UV a, final UV b, final UV c, final UV d) {

        for (final PolygonalFaceData data : this._cuboidData) {

            data.setTexture(0, a);
            data.setTexture(1, b);
            data.setTexture(2, c);
            data.setTexture(3, d);
        }

        return this;
    }

    /***
     * Set the texture for the given face of the cuboid to be rendered.
     * See {@link CuboidBuilder#setVisibleFaces} or {@link CuboidBuilder#setFaceVisibility}
     * @param facing    the face
     * @param a         the texture map for the first vertex of a face
     * @param b         the texture map for the second vertex of a face
     * @param c         the texture map for the third vertex of a face
     * @param d         the texture map for the forth vertex of a face
     * @return the builder
     */
    public CuboidBuilder setTexture(final Direction facing, final UV a, final UV b,
                                    final UV c, final UV d) {

        final PolygonalFaceData data = this._cuboidData[facing.get3DDataValue()];

        data.setTexture(0, a);
        data.setTexture(1, b);
        data.setTexture(2, c);
        data.setTexture(3, d);
        return this;
    }

    public CuboidBuilder setTexture(final Direction facing, final int vertexIndex, final UV uv) {

        final PolygonalFaceData data = this._cuboidData[facing.get3DDataValue()];

        data.setTexture(vertexIndex, uv);
        return this;
    }

    public CuboidBuilder setTexture(final TextureAtlasSprite sprite) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setTexture(sprite);
        }

        return this;
    }

    public CuboidBuilder setTexture(final ISprite sprite) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setTexture(sprite);
        }

        return this;
    }

    public CuboidBuilder setTexture(final Supplier<ISprite> sprite) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setTexture(sprite);
        }

        return this;
    }

    public CuboidBuilder setTexture(final Direction facing, final TextureAtlasSprite sprite) {

        this._cuboidData[facing.get3DDataValue()].setTexture(sprite);
        return this;
    }

    public CuboidBuilder setTexture(final Direction facing, final ISprite sprite) {

        this._cuboidData[facing.get3DDataValue()].setTexture(sprite);
        return this;
    }

    public CuboidBuilder setTexture(final Direction facing, final Supplier<ISprite> sprite) {

        this._cuboidData[facing.get3DDataValue()].setTexture(sprite);
        return this;
    }

    // Light-map

    /***
     * Set the light map for all the face of the cuboid
     * @param lightMap  the light map
     * @return the builder
     */
    public CuboidBuilder setLightMap(final LightMap lightMap) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setLightMap(lightMap);
        }

        return this;
    }

    public CuboidBuilder setLightMapCombined(final int combined) {
        return this.setLightMap(new LightMap(combined));
    }

    /***
     * Set the light map for the given face of the cuboid
     * @param facing    the face to change
     * @param lightMap  the light map
     * @return the builder
     */
    public CuboidBuilder setLightMap(final Direction facing, final LightMap lightMap) {

        this._cuboidData[facing.get3DDataValue()].setLightMap(lightMap);
        return this;
    }

    public CuboidBuilder setLightMapCombined(final Direction facing, final int combined) {
        return this.setLightMap(facing, new LightMap(combined));
    }

    /***
     * Set the light map for a vertex of the given face of the cuboid
     * @param facing        the face to change
     * @param vertexIndex   the vertex to change
     * @param lightMap      the light map
     * @return the builder
     */
    public CuboidBuilder setLightMap(final Direction facing, final int vertexIndex, final LightMap lightMap) {

        this._cuboidData[facing.get3DDataValue()].setLightMap(vertexIndex, lightMap);
        return this;
    }

    public CuboidBuilder setLightMapCombined(final Direction facing, final int vertexIndex, final int combined) {
        return this.setLightMap(facing, vertexIndex, new LightMap(combined));
    }

    // Overlay-map

    /***
     * Set the overlay map for all the face of the cuboid
     * @param map  the light map
     * @return the builder
     */
    public CuboidBuilder setOverlayMap(final LightMap map) {

        for (final PolygonalFaceData data : this._cuboidData) {
            data.setOverlayMap(map);
        }

        return this;
    }

    public CuboidBuilder setOverlayMapCombined(final int combined) {
        return this.setOverlayMap(new LightMap(combined));
    }

    /***
     * Set the overlay map for the given face of the cuboid
     * @param facing    the face to change
     * @param map  the light map
     * @return the builder
     */
    public CuboidBuilder setOverlayMap(final Direction facing, final LightMap map) {

        this._cuboidData[facing.get3DDataValue()].setOverlayMap(map);
        return this;
    }

    public CuboidBuilder setOverlayMapCombined(final Direction facing, final int combined) {
        return this.setOverlayMap(facing, new LightMap(combined));
    }

    /***
     * Set the overlay map for a vertex of the given face of the cuboid
     * @param facing        the face to change
     * @param vertexIndex   the vertex to change
     * @param map      the light map
     * @return the builder
     */
    public CuboidBuilder setOverlayMap(final Direction facing, final int vertexIndex, final LightMap map) {

        this._cuboidData[facing.get3DDataValue()].setOverlayMap(vertexIndex, map);
        return this;
    }

    public CuboidBuilder setOverlayMapCombined(final Direction facing, final int vertexIndex, final int combined) {
        return this.setOverlayMap(facing, vertexIndex, new LightMap(combined));
    }

    //region internals

    private final PolygonalFaceData[] _cuboidData;
    private Cuboid _cuboid;
    private BlockFacings _facesToBeRendered;

    private static CuboidBuilder s_defaultBuilder = null;

    //endregion
}


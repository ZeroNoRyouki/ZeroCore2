/*
 *
 * Vertex.java
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

package it.zerono.mods.zerocore.lib.client.render;

import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "WeakerAccess"})
public class Vertex
        implements IVertexSource {

    public final Vector3d POSITION;
    public final Vector3f NORMAL;
    public final UV UV;
    public final Colour COLOUR;
    public final LightMap LIGHT_MAP;
    public final LightMap OVERLAY_MAP;
    public int IDX;

    public Vertex(final Vector3d position, final Colour colour) {

        this.POSITION = position;
        this.NORMAL = null;
        this.UV = null;
        this.COLOUR = colour;
        this.LIGHT_MAP = null;
        this.OVERLAY_MAP = null;
    }

    public Vertex(final Vector3d position, final UV uv) {

        this.POSITION = position;
        this.NORMAL = null;
        this.UV = uv;
        this.COLOUR = null;
        this.LIGHT_MAP = null;
        this.OVERLAY_MAP = null;
    }

    public Vertex(final Vector3d position, @Nullable final Vector3f normal,
                  @Nullable final UV uv, @Nullable final Colour colour,
                  @Nullable final LightMap lightMap, @Nullable final LightMap overlayMap) {

        this.POSITION = position;
        this.NORMAL = normal;
        this.UV = uv;
        this.COLOUR = colour;
        this.LIGHT_MAP = lightMap;
        this.OVERLAY_MAP = overlayMap;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%d] p:(%f, %f, %f)", this.IDX, this.POSITION.X, this.POSITION.Y, this.POSITION.Z));

        if (null != this.COLOUR) {
            sb.append(String.format(" c:(%d, %d, %d, %d)", this.COLOUR.R, this.COLOUR.G, this.COLOUR.B, this.COLOUR.A));
        }

        if (null != this.UV) {
            sb.append(String.format(" t:(%f, %f)", this.UV.U, this.UV.V));
        }

        if (null != this.OVERLAY_MAP) {
            sb.append(String.format(" o:(%d, %d)", this.OVERLAY_MAP.U, this.OVERLAY_MAP.V));
        }

        if (null != this.LIGHT_MAP) {
            sb.append(String.format(" l:(%d, %d)", this.LIGHT_MAP.U, this.LIGHT_MAP.V));
        }

        if (null != this.NORMAL) {
            sb.append(String.format(" n:(%f, %f, %f)", this.NORMAL.x(), this.NORMAL.y(), this.NORMAL.z()));
        }

        return sb.toString();
    }

    //region IVertexSource

    public Vector3d getPos() {
        return this.POSITION;
    }

    @Nullable
    public Vector3f getNormal() {
        return this.NORMAL;
    }

    @Nullable
    public UV getUV() {
        return this.UV;
    }

    @Nullable
    public Colour getColour() {
        return this.COLOUR;
    }

    @Nullable
    public LightMap getLightMap() {
        return this.LIGHT_MAP;
    }

    @Nullable
    public LightMap getOverlayMap() {
        return this.OVERLAY_MAP;
    }

//    @Override
//    public void uploadVertexData(final IVertexBuilder builder) {
//
//        builder.pos(this.POSITION.X, this.POSITION.Y, this.POSITION.Z);
//
//        if (null != this.COLOUR) {
//            builder.color(this.COLOUR.R, this.COLOUR.G, this.COLOUR.B, this.COLOUR.A);
//        }
//
//        if (null != this.UV) {
//            builder.tex(this.UV.U, this.UV.V);
//        }
//
//        if (null != this.OVERLAY_MAP) {
//            builder.overlay(this.OVERLAY_MAP.SKY_LIGHT, this.OVERLAY_MAP.BLOCK_LIGHT);
//        }
//
//        if (null != this.LIGHT_MAP) {
//            builder.lightmap(this.LIGHT_MAP.SKY_LIGHT, this.LIGHT_MAP.BLOCK_LIGHT);
//        }
//
//        if (null != this.NORMAL) {
//            builder.normal(this.NORMAL.getX(), this.NORMAL.getY(), this.NORMAL.getZ());
//        }
//
//        builder.endVertex();
//    }
//
//    @Override
//    public void uploadVertexData(final IVertexBuilder builder, final LightMap lightMapOverride,
//                                 final LightMap overlayMapOverride) {
//
//        builder.pos(this.POSITION.X, this.POSITION.Y, this.POSITION.Z);
//
//        if (null != this.COLOUR) {
//            builder.color(this.COLOUR.R, this.COLOUR.G, this.COLOUR.B, this.COLOUR.A);
//        }
//
//        if (null != this.UV) {
//            builder.tex(this.UV.U, this.UV.V);
//        }
//
//        builder.overlay(overlayMapOverride.SKY_LIGHT, overlayMapOverride.BLOCK_LIGHT);
//        builder.lightmap(lightMapOverride.SKY_LIGHT, lightMapOverride.BLOCK_LIGHT);
//
//        if (null != this.NORMAL) {
//            builder.normal(this.NORMAL.getX(), this.NORMAL.getY(), this.NORMAL.getZ());
//        }
//
//        builder.endVertex();
//    }
//
//    @Override
//    public void uploadVertexData(IVertexBuilder builder, Colour colourOverride) {
//
//        builder.pos(this.POSITION.X, this.POSITION.Y, this.POSITION.Z);
//        builder.color(colourOverride.R, colourOverride.G, colourOverride.B, colourOverride.A);
//
//        if (null != this.UV) {
//            builder.tex(this.UV.U, this.UV.V);
//        }
//
//        if (null != this.OVERLAY_MAP) {
//            builder.overlay(this.OVERLAY_MAP.SKY_LIGHT, this.OVERLAY_MAP.BLOCK_LIGHT);
//        }
//
//        if (null != this.LIGHT_MAP) {
//            builder.lightmap(this.LIGHT_MAP.SKY_LIGHT, this.LIGHT_MAP.BLOCK_LIGHT);
//        }
//
//        if (null != this.NORMAL) {
//            builder.normal(this.NORMAL.getX(), this.NORMAL.getY(), this.NORMAL.getZ());
//        }
//
//        builder.endVertex();
//    }

    //endregion
}

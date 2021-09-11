/*
 *
 * VertexUploader.java
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

package it.zerono.mods.zerocore.lib.client.render.vertexuploader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.client.render.IVertexSequence;
import it.zerono.mods.zerocore.lib.client.render.IVertexSource;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;
import java.util.Map;

public class VertexUploader {

    public static final VertexUploader INSTANCE = new VertexUploader();

    public void upload(IVertexBuilder builder, IVertexSource source) {
        this.upload(builder, source, DEFAULT_ADAPTER);
    }

    public void upload(IVertexBuilder builder, IVertexSequence sequence) {
        this.upload(builder, sequence, DEFAULT_ADAPTER);
    }

    public void upload(IVertexBuilder builder, List<IVertexSource> sources) {
        this.upload(builder, sources, DEFAULT_ADAPTER);
    }

    public void upload(IVertexBuilder builder, IVertexSource source, ISourceAdapter adapter) {
        this.getUploaderFor(builder).upload(builder, source, adapter);
    }

    public void upload(IVertexBuilder builder, IVertexSequence sequence, ISourceAdapter adapter) {
        this.upload(builder, sequence.getVertices(), adapter);
    }

    public void upload(IVertexBuilder builder, List<IVertexSource> sources, ISourceAdapter adapter) {

        final IUploader uploader = this.getUploaderFor(builder);

        sources.forEach(source -> uploader.upload(builder, source, adapter));
//
//        for (int i = 0; i<4; i++)
//            uploader.upload(builder, sources.get(i), adapter);
//

    }

    //region internals

    private VertexUploader() {

        this._uploaders = Maps.newHashMapWithExpectedSize(2);
        this._uploaders.put(DefaultVertexFormats.BLOCK, VertexUploader::blockUploader);
        this._uploaders.put(DefaultVertexFormats.NEW_ENTITY, VertexUploader::entityUploader);
    }

    private IUploader getUploaderFor(IVertexBuilder builder) {

        if (builder instanceof BufferBuilder) {
            return this._uploaders.getOrDefault(((BufferBuilder)builder).getVertexFormat(), VertexUploader::fallBackUploader);
        }

        return VertexUploader::fallBackUploader;
    }

    private static void fallBackUploader(IVertexBuilder builder, IVertexSource source, ISourceAdapter adapter) {

        final Vector3d pos = adapter.getPos(source);
        final Vector3f normal = adapter.getNormal(source);
        final UV uv = adapter.getUV(source);
        final Colour colour = adapter.getColour(source);
        final LightMap overlay = adapter.getOverlayMap(source);
        final LightMap light = adapter.getLightMap(source);

        builder.vertex(pos.X, pos.Y, pos.Z);

        if (null != colour) {
            builder.color(colour.R, colour.G, colour.B, colour.A);
        }

        if (null != uv) {
            builder.uv(uv.U, uv.V);
        }

        if (null != overlay) {
            builder.overlayCoords(overlay.U, overlay.V);
        }

        if (null != light) {
            builder.uv2(light.U, light.V);
        }

        if (null != normal) {
            builder.normal(normal.x(), normal.y(), normal.z());
        }

        builder.endVertex();
    }

    @SuppressWarnings("ConstantConditions") // if an element is missing (null) we would crash anyway in endVertex()
    private static void blockUploader(IVertexBuilder builder, IVertexSource source, ISourceAdapter adapter) {

        final Vector3d pos = adapter.getPos(source);
        final Vector3f normal = adapter.getNormal(source);
        final UV uv = adapter.getUV(source);
        final Colour colour = adapter.getColour(source);
        final LightMap light = adapter.getLightMap(source);

        builder.vertex(pos.X, pos.Y, pos.Z)
                .color(colour.R, colour.G, colour.B, colour.A)
                .uv(uv.U, uv.V)
                .uv2(light.U, light.V)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
    }

    @SuppressWarnings("ConstantConditions") // if an element is missing (null) we would crash anyway in endVertex()
    private static void entityUploader(IVertexBuilder builder, IVertexSource source, ISourceAdapter adapter) {

        final Vector3d pos = adapter.getPos(source);
        final Vector3f normal = adapter.getNormal(source);
        final UV uv = adapter.getUV(source);
        final Colour colour = adapter.getColour(source);
        final LightMap overlay = adapter.getOverlayMap(source);
        final LightMap light = adapter.getLightMap(source);

//        Log.LOGGER.info("UPV p:{} c:{} t:{} o:{} l:{} n:{}", pos, colour, uv, overlay, light, normal);
        Log.LOGGER.info("UPV {}", source);

        builder.vertex(pos.X, pos.Y, pos.Z)
                .color(colour.R, colour.G, colour.B, colour.A)
                .uv(uv.U, uv.V)
                .overlayCoords(overlay.U, overlay.V)
                .uv2(light.U, light.V)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
    }

    private static final ISourceAdapter DEFAULT_ADAPTER = new ISourceAdapter() {
    };

    private final Map<VertexFormat, IUploader> _uploaders;

    //endregion
}

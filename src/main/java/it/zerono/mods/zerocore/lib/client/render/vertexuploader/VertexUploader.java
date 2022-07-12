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
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import it.zerono.mods.zerocore.lib.client.render.IVertexSequence;
import it.zerono.mods.zerocore.lib.client.render.IVertexSource;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;

import java.util.List;
import java.util.Map;

public class VertexUploader {

    public static final VertexUploader INSTANCE = new VertexUploader();

    public void upload(VertexFormat format, VertexConsumer builder, IVertexSource source) {
        this.upload(format, builder, source, DEFAULT_ADAPTER);
    }

    public void upload(VertexFormat format, VertexConsumer builder, IVertexSequence sequence) {
        this.upload(format, builder, sequence, DEFAULT_ADAPTER);
    }

    public void upload(VertexFormat format, VertexConsumer builder, List<IVertexSource> sources) {
        this.upload(format, builder, sources, DEFAULT_ADAPTER);
    }

    public void upload(VertexFormat format, VertexConsumer builder, IVertexSource source, ISourceAdapter adapter) {
        this.getUploaderFor(format).upload(builder, source, adapter);
    }

    public void upload(VertexFormat format, VertexConsumer builder, IVertexSequence sequence, ISourceAdapter adapter) {
        this.upload(format, builder, sequence.getVertices(), adapter);
    }

    public void upload(VertexFormat format, VertexConsumer builder, List<IVertexSource> sources, ISourceAdapter adapter) {

        final IUploader uploader = this.getUploaderFor(format);

        sources.forEach(source -> uploader.upload(builder, source, adapter));
    }

    //region internals

    private VertexUploader() {

        this._uploaders = Maps.newHashMapWithExpectedSize(2);
        this._uploaders.put(DefaultVertexFormat.BLOCK, VertexUploader::blockUploader);
        this._uploaders.put(DefaultVertexFormat.NEW_ENTITY, VertexUploader::entityUploader);
    }

    private IUploader getUploaderFor(final VertexFormat format) {
        return this._uploaders.getOrDefault(format, VertexUploader::fallBackUploader);
    }

    private static void fallBackUploader(VertexConsumer builder, IVertexSource source, ISourceAdapter adapter) {

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
    private static void blockUploader(VertexConsumer builder, IVertexSource source, ISourceAdapter adapter) {

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
    private static void entityUploader(VertexConsumer builder, IVertexSource source, ISourceAdapter adapter) {

        final Vector3d pos = adapter.getPos(source);
        final Vector3f normal = adapter.getNormal(source);
        final UV uv = adapter.getUV(source);
        final Colour colour = adapter.getColour(source);
        final LightMap overlay = adapter.getOverlayMap(source);
        final LightMap light = adapter.getLightMap(source);

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

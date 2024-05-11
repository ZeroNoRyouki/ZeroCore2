/*
 *
 * AbstractDynamicBakedModel.java
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

package it.zerono.mods.zerocore.lib.client.model;

import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.Flags;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractDynamicBakedModel
        implements IDynamicBakedModel {

    protected AbstractDynamicBakedModel(final boolean ambientOcclusion, final boolean guid3D) {
        this(ambientOcclusion, guid3D, false);
    }

    protected AbstractDynamicBakedModel(final boolean ambientOcclusion, final boolean guid3D, final boolean builtInRenderer) {

        this._flags = new Flags<>(SupportFlags.class);
        this._flags.addIf(SupportFlags.AmbientOcclusion, ambientOcclusion);
        this._flags.addIf(SupportFlags.Gui3D, guid3D);
        this._flags.addIf(SupportFlags.BuiltInRenderer, builtInRenderer);
    }

    protected BakedQuad createQuad(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4, final Vector3d normal,
                                   final ISprite sprite, final Colour colour, final int tintIndex,
                                   final short lightMapU, final short lightMapV) {

        final BakedQuad[] result = new BakedQuad[1];
        final QuadBakingVertexConsumer builder = builder(quad -> result[0] = quad, sprite, normal, tintIndex);

        this.createVertex(builder, v1.X, v1.Y, v1.Z, normal, 16, 16, sprite, colour, lightMapU, lightMapV);
        this.createVertex(builder, v2.X, v2.Y, v2.Z, normal, 0, 16, sprite, colour, lightMapU, lightMapV);
        this.createVertex(builder, v3.X, v3.Y, v3.Z, normal, 0, 0, sprite, colour, lightMapU, lightMapV);
        this.createVertex(builder, v4.X, v4.Y, v4.Z, normal, 16, 0, sprite, colour, lightMapU, lightMapV);

        return result[0];
    }

    protected BakedQuad createQuad(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4,
                                   final ISprite sprite, final Colour colour) {
        return createQuad(v1, v2, v3, v4, normal(v1, v2, v3, v4), sprite, colour, 0, (short)0, (short)0);
    }

    protected BakedQuad createQuad(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4, final ISprite sprite) {
        return createQuad(v1, v2, v3, v4, sprite, Colour.WHITE);
    }

    protected BakedQuad createQuadReversed(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4,
                                           final ISprite sprite, final Colour colour, final int tintIndex,
                                           final short lightMapU, final short lightMapV) {
        return createQuad(v1, v2, v3, v4, reversedNormal(v1, v2, v3, v4), sprite, colour, tintIndex, lightMapU, lightMapV);
    }

    protected BakedQuad createQuadReversed(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4, final ISprite sprite) {
        return createQuad(v1, v2, v3, v4, reversedNormal(v1, v2, v3, v4), sprite, Colour.WHITE, 0, (short)0, (short)0);
    }

    protected BakedQuad createFace(final Direction face, final float width, final float height, final float depth,
                                   final ISprite sprite) {
        return this.createFace(face, width, height, depth, sprite, Colour.WHITE, 0, (short)0, (short)0, Vector3d.ZERO);
    }

    protected BakedQuad createFace(final Direction face, final float width, final float height, final float depth,
                                   final ISprite sprite, final int tintIndex, final Vector3d offset) {
        return this.createFace(face, width, height, depth, sprite, Colour.WHITE, tintIndex, (short)0, (short)0, offset);
    }

    protected BakedQuad createFace(final Direction face, final float width, final float height, final float depth,
                                   final ISprite sprite, final Colour colour, final int tintIndex,
                                   final short lightMapU, final short lightMapV, final Vector3d offset) {

        final Vector3d[] vertices = ModRenderHelper.getQuadVerticesFor(face, width, height, depth, offset);

        return this.createQuad(vertices[0], vertices[1], vertices[2], vertices[3], normal(face), sprite, colour,
                tintIndex, lightMapU, lightMapV);
    }

    protected void createVertex(QuadBakingVertexConsumer builder, double x, double y, double z, Vector3d normal,
                                float u, float v, ISprite sprite, Colour colour, short lightMapU, short lightMapV) {

        builder.vertex(x, y, z);
        builder.color(colour.R, colour.G, colour.B, colour.A);
        builder.uv(sprite.getInterpolatedU(u), sprite.getInterpolatedV(v));

        builder.uv2(lightMapU, lightMapV);
        builder.normal((float)normal.X, (float)normal.Y, (float)normal.Z);
        builder.endVertex();
    }

    //region IDynamicBakedModel

    @Override
    public boolean useAmbientOcclusion() {
        return this._flags.contains(SupportFlags.AmbientOcclusion);
    }

    @Override
    public boolean isGui3d() {
        return this._flags.contains(SupportFlags.Gui3D);
    }

    @Override
    public boolean isCustomRenderer() {
        return this._flags.contains(SupportFlags.BuiltInRenderer);
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return ModRenderHelper.getMissingModel().getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    //endregion
    //region internals

    protected static QuadBakingVertexConsumer builder(final Consumer<BakedQuad> quadConsumer, final ISprite sprite) {

        final var builder = new QuadBakingVertexConsumer(quadConsumer);

        builder.setSprite(sprite.getAtlasSprite().orElse(ModRenderHelper.getMissingTexture()));
        return builder;
    }

    protected static QuadBakingVertexConsumer builder(final Consumer<BakedQuad> quadConsumer, final ISprite sprite,
                                                      final Vector3d normal, final int tintIndex) {

        final var builder = builder(quadConsumer, sprite);

        builder.setDirection(Direction.getNearest(normal.X, normal.Y, normal.Z));
        builder.setTintIndex(tintIndex);
        return builder;
    }

    private static Vector3d normal(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4) {
        return v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();
    }

    private static Vector3d reversedNormal(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4) {
        return v3.subtract(v1).crossProduct(v2.subtract(v1)).normalize();
    }

    private static Vector3d normal(final Direction direction) {
        return Vector3d.from(direction.getNormal());
    }

    private enum SupportFlags {

        AmbientOcclusion,
        Gui3D,
        BuiltInRenderer
    }

    private final Flags<SupportFlags> _flags;

    //endregion
}

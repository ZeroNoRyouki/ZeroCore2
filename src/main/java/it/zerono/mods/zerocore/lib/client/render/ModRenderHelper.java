/*
 *
 * ModRenderHelper.java
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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class ModRenderHelper {

    public static final float ONE_PIXEL = 1.0f / 16.0f;

    public static final NonNullSupplier<FontRenderer> DEFAULT_FONT_RENDERER = () -> Minecraft.getInstance().fontRenderer;

    public static long getLastRenderTime() {
        return ZeroCore.getProxy().getLastRenderTime();
    }

    public static ModelManager getModelManager() {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
    }

    @SuppressWarnings("ConstantConditions")
    public static IUnbakedModel getModel(final ResourceLocation location) {
        return ModelLoader.instance().getModelOrMissing(location);
    }

    public static IBakedModel getModel(final BlockState state) {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
    }

    public static IBakedModel getModel(final ModelResourceLocation modelLocation) {
        return getModelManager().getModel(modelLocation);
    }

    public static IBakedModel getMissingModel() {
        return getModelManager().getMissingModel();
    }

    public static void bindTexture(final ResourceLocation textureLocation) {
        Minecraft.getInstance().getTextureManager().bindTexture(textureLocation);
    }

    public static void bindTexture(final ISprite sprite) {
        Minecraft.getInstance().getTextureManager().bindTexture(sprite.getTextureMap().getTextureLocation());
    }

    public static void bindBlocksTexture() {
        ModRenderHelper.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    }

    public static TextureAtlasSprite getTextureSprite(final ResourceLocation location) {
        return getTextureSprite(PlayerContainer.LOCATION_BLOCKS_TEXTURE, location);
    }

    public static TextureAtlasSprite getTextureSprite(final ResourceLocation atlasName, final ResourceLocation spriteName) {
        return Minecraft.getInstance().getAtlasSpriteGetter(atlasName).apply(spriteName);
    }

    public static TextureAtlasSprite getFluidStillSprite(final Fluid fluid) {
        return ModRenderHelper.getTextureSprite(fluid.getAttributes().getStillTexture());
    }

    public static TextureAtlasSprite getFluidStillSprite(final FluidStack fluid) {
        return ModRenderHelper.getTextureSprite(fluid.getFluid().getAttributes().getStillTexture(fluid));
    }

    public static TextureAtlasSprite getFluidFlowingSprite(final Fluid fluid) {
        return ModRenderHelper.getTextureSprite(fluid.getAttributes().getFlowingTexture());
    }

    public static TextureAtlasSprite getFluidFlowingSprite(final FluidStack fluid) {
        return ModRenderHelper.getTextureSprite(fluid.getFluid().getAttributes().getFlowingTexture(fluid));
    }

    public static TextureAtlasSprite getMissingTexture(final ResourceLocation atlasName) {
        return getTextureSprite(atlasName, MissingTextureSprite.getLocation());
    }

    public static TextureAtlasSprite getMissingTexture() {
        return getTextureSprite(MissingTextureSprite.getLocation());
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final Fluid fluid) {

        final ResourceLocation rl = fluid.getAttributes().getOverlayTexture();

        return null != rl ? ModRenderHelper.getTextureSprite(rl) : null;
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final FluidStack fluid) {

        final ResourceLocation rl = fluid.getFluid().getAttributes().getOverlayTexture();

        return null != rl ? ModRenderHelper.getTextureSprite(rl) : null;
    }

    public static ISprite getStillFluidSprite(final Fluid fluid) {
        return buildSprite(getFluidStillSprite(fluid), null);
    }

    public static ISprite getStillFluidSpriteWithOverlay(final Fluid fluid) {
        return buildSprite(getFluidStillSprite(fluid), getFluidOverlaySprite(fluid));
    }

    public static ISprite getFlowingFluidSprite(final Fluid fluid) {
        return buildSprite(getFluidFlowingSprite(fluid), null);
    }

    public static ISprite getFlowingFluidSpriteWithOverlay(final Fluid fluid) {
        return buildSprite(getFluidFlowingSprite(fluid), getFluidOverlaySprite(fluid));
    }

    private static ISprite buildSprite(final TextureAtlasSprite main, @Nullable final TextureAtlasSprite overlay) {

        final ISprite s = AtlasSpriteTextureMap.from(main).sprite(main);

        if (null != overlay) {
            return s.copyWith(AtlasSpriteTextureMap.from(overlay).sprite(overlay));
        } else {
            return s;
        }
    }

    public static List<String> wrapLines(final String text, final int maxLineWidth, final FontRenderer font) {

        final List<String> lines = Lists.newLinkedList();
        final int spaceWidth = font.getStringWidth(" ");

        final String[] tokens = text.split("\\s+");
        final Integer[] tokenWidths = Arrays.stream(tokens).map(font::getStringWidth).toArray(Integer[]::new);

        StringBuilder wrappedLine = new StringBuilder(text.length());
        int lineWidth = 0;

        for (int i = 0; i < tokens.length; ++i) {

            final String token = tokens[i];
            final int tokenWidth = tokenWidths[i];

            if (lineWidth + tokenWidth + spaceWidth > maxLineWidth) {

                lines.add(wrappedLine.toString());
                wrappedLine = new StringBuilder(text.length());
                lineWidth = 0;
            }

            if (i < tokens.length - 1 && (lineWidth + tokenWidth + spaceWidth + tokenWidths[i + 1] <= maxLineWidth)) {

                wrappedLine.append(token);
                wrappedLine.append(" ");
                lineWidth += tokenWidth + spaceWidth;

            } else {

                wrappedLine.append(token);
                lineWidth += tokenWidth;
            }
        }

        if (wrappedLine.length() > 0) {
            lines.add(wrappedLine.toString());
        }

        return lines;
    }

    //region render BakedQuad(s)

    /**
     * Render a list of BakedQuads into the provided builder
     *
     * @param matrix the render system matrix to use
     * @param builder the vertex builder to add the quads to
     * @param quads the quads to render
     * @param combinedLight
     * @param combinedOverlay
     */
    public static void renderQuads(final MatrixStack matrix, final IVertexBuilder builder, final List<BakedQuad> quads,
                                   final int combinedLight, final int combinedOverlay) {

        final MatrixStack.Entry entry = matrix.getLast();

        for (final BakedQuad quad : quads) {
            builder.addVertexData(entry, quad, 1, 1, 1, combinedLight, combinedOverlay, true);
        }
    }

    /**
     * Render a list of BakedQuads into the provided builder with a color tint (if the quad support it)
     *
     * @param matrix the render system matrix to use
     * @param builder the vertex builder to add the quads to
     * @param quads the quads to render
     * @param combinedLight
     * @param combinedOverlay
     * @param quadTintGetter get a Colour to use as a tint for the quad tint index
     */
    public static void renderQuads(final MatrixStack matrix, final IVertexBuilder builder, final List<BakedQuad> quads,
                                   final int combinedLight, final int combinedOverlay,
                                   final Function<Integer, Colour> quadTintGetter) {

        final MatrixStack.Entry entry = matrix.getLast();

        for (final BakedQuad quad : quads) {

            float red, green, blue;

            if (quad.hasTintIndex()) {

                final Colour tint = quadTintGetter.apply(quad.getTintIndex());

                red = tint.R;
                green = tint.G;
                blue = tint.B;

            } else {

                red = green = blue = 1;
            }

            builder.addVertexData(entry, quad, red, green, blue, combinedLight, combinedOverlay, true);
        }
    }

    //endregion
    //region render IBackedModel

    /**
     * Render a IBackedModel into the provided builder
     *
     * @param model the backed model to render
     * @param data addition data for the model
     * @param matrix the render system matrix to use
     * @param builder the vertex builder to add the quads to
     * @param combinedLight
     * @param combinedOverlay
     */
    public static void renderModel(final IBakedModel model, final IModelData data, final MatrixStack matrix,
                                   final IVertexBuilder builder, final int combinedLight, final int combinedOverlay) {

        for (final Direction direction : Direction.values()) {
            renderQuads(matrix, builder, model.getQuads(null, direction, CodeHelper.fakeRandom(), data),
                    combinedLight, combinedOverlay);
        }

        renderQuads(matrix, builder, model.getQuads(null, null, CodeHelper.fakeRandom(), data),
                combinedLight, combinedOverlay);
    }

    /**
     * Render a IBackedModel into the provided builder
     *
     * @param model the backed model to render
     * @param data addition data for the model
     * @param matrix the render system matrix to use
     * @param builder the vertex builder to add the quads to
     * @param combinedLight
     * @param combinedOverlay
     * @param quadTintGetter get a Colour to use as a tint for the quad tint index
     */
    public static void renderModel(final IBakedModel model, final IModelData data, final MatrixStack matrix,
                                   final IVertexBuilder builder, final int combinedLight, final int combinedOverlay,
                                   final Function<Integer, Colour> quadTintGetter) {

        for (final Direction direction : Direction.values()) {
            renderQuads(matrix, builder, model.getQuads(null, direction, CodeHelper.fakeRandom(), data),
                    combinedLight, combinedOverlay, quadTintGetter);
        }

        renderQuads(matrix, builder, model.getQuads(null, null, CodeHelper.fakeRandom(), data),
                combinedLight, combinedOverlay, quadTintGetter);
    }

    //endregion
    //region vertex helpers

    public static Vector3d[] getQuadVerticesFor(final Direction face, final float width, final float height, final float depth) {
        return getQuadVerticesFor(face, width, height, depth, Vector3d.ZERO);
    }

    public static Vector3d[] getQuadVerticesFor(final Direction face, float width, float height, float depth,
                                                final Vector3d offset) {

        Vector3d leftToRight, bottomToTop, nearToFar;

        switch (face) {

            case NORTH:
                // bottom left is east
                leftToRight = Vector3d.XN;
                bottomToTop = Vector3d.YP;
                nearToFar = Vector3d.ZN;
                break;

            case SOUTH:
                // bottom left is west
                leftToRight = Vector3d.XP;
                bottomToTop = Vector3d.YP;
                nearToFar = Vector3d.ZP;
                break;

            case EAST:
                // bottom left is south
                leftToRight = Vector3d.ZN;
                bottomToTop = Vector3d.YP;
                nearToFar = Vector3d.XP;
                break;

            default:
            case WEST:
                // bottom left is north
                leftToRight = Vector3d.ZP;
                bottomToTop = Vector3d.YP;
                nearToFar = Vector3d.XN;
                break;

            case UP:
                // bottom left is southwest by minecraft block convention
                leftToRight = Vector3d.XN;
                bottomToTop = Vector3d.ZP;
                nearToFar = Vector3d.YP;
                break;

            case DOWN:
                // bottom left is northwest by minecraft block convention
                leftToRight = Vector3d.XP;
                bottomToTop = Vector3d.ZP;
                nearToFar = Vector3d.YN;
                break;
        }

        // convert to half

        leftToRight = leftToRight.multiply(width * 0.5);
        bottomToTop = bottomToTop.multiply(height * 0.5);
        nearToFar = nearToFar.multiply(depth * 0.5);

        // calculate the four vertices based on the centre of the face

        final Vector3d[] vertices = new Vector3d[4];

        // bottom left
        vertices[0] = Vector3d.HALF
                .subtract(leftToRight)
                .subtract(bottomToTop)
                .add(nearToFar)
                .add(offset);

        // bottom right
        vertices[1] = Vector3d.HALF
                .add(leftToRight)
                .subtract(bottomToTop)
                .add(nearToFar)
                .add(offset);

        // top right
        vertices[2] = Vector3d.HALF
                .add(leftToRight)
                .add(bottomToTop)
                .add(nearToFar)
                .add(offset);

        // top left
        vertices[3] = Vector3d.HALF
                .subtract(leftToRight)
                .add(bottomToTop)
                .add(nearToFar)
                .add(offset);

        return vertices;
    }

    //endregion
    //region voxel shapes helpers

    public static void paintVoxelShape(final MatrixStack matrix, final VoxelShape shape,  final IVertexBuilder vertexBuilder,
                                       final double originX, final double originY, final double originZ, final Colour colour) {

        final Matrix4f m = matrix.getLast().getMatrix();
        final float red = colour.glRed();
        final float green = colour.glGreen();
        final float blue = colour.glBlue();
        final float alpha = colour.glAlpha();

        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {

            vertexBuilder.pos(m, (float)(x1 + originX), (float)(y1 + originY), (float)(z1 + originZ)).color(red, green, blue, alpha).endVertex();
            vertexBuilder.pos(m, (float)(x2 + originX), (float)(y2 + originY), (float)(z2 + originZ)).color(red, green, blue, alpha).endVertex();
        });
    }

    //endregion
    //region 2D/GUI paint helpers (with matrix)
    //region sprites

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY, final int zLevel,
                                   final int width, final int height) {
        paintSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel, width, height);
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final MatrixStack matrix, final ISprite sprite, final int x, final int y,
                                   final int zLevel, final int width, final int height) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ModRenderHelper.bindTexture(sprite);
        blitSprite(matrix, x, x + width, y, y + height, zLevel,
                sprite.getWidth(), sprite.getHeight(), sprite.getU(), sprite.getV(),
                sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight());

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(matrix, o, x, y, zLevel, width, height));
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param skip number of pixels to skip at the bottom of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static int paintVerticalProgressSprite(final MatrixStack matrix, final ISprite sprite, final Colour tint,
                                                  final Point screenXY, final int zLevel, final Rectangle area,
                                                  final int skip, final double progress) {
        return paintVerticalProgressSprite(matrix, sprite, tint, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, skip, progress);
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param skip number of pixels to skip at the bottom of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static int paintVerticalProgressSprite(final MatrixStack matrix, final ISprite sprite, final Colour tint,
                                                  final int x, final int y, final int zLevel,
                                                  final int areaWidth, final int areaHeight, final int skip,
                                                  final double progress) {

        if (progress < 0.01) {
            return 0;
        }

        final int filledHeight = (int)(areaHeight * progress);
        final int y2 = y + areaHeight - skip;
        final int y1 = y2 - filledHeight;

        paintProgressSprite(matrix, sprite, tint, x, y1, x + areaWidth, y2, zLevel);
        return filledHeight;
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static void paintVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY,
                                                      final int zLevel, final Rectangle area, final double progress) {
        paintVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     * @return the height of the painted sprite
     */
    public static void paintVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY,
                                                      final int zLevel, final Rectangle area, final double progress,
                                                      final Colour tint) {
        paintVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, tint);
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static void paintVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite,
                                                      final int x, final int y, final int zLevel,
                                                      final int areaWidth, final int areaHeight,
                                                      final double progress) {
        paintVerticalProgressBarSprite(matrix, sprite, x, y, zLevel, areaWidth, areaHeight, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     * @return the height of the painted sprite
     */
    public static void paintVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite,
                                                      final int x, final int y, final int zLevel,
                                                      final int areaWidth, final int areaHeight,
                                                      final double progress, final Colour tint) {

        if (progress < 0.01) {
            return;
        }

        final int filledHeight = (int)(areaHeight * progress);
        final int y1 = y + (areaHeight - filledHeight);
        final int y2 = y + areaHeight;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bindTexture(sprite);

        if (sprite.getHeight() == areaHeight) {

            blitSprite(matrix, x, x + areaWidth, y1, y2, zLevel, sprite.getWidth(), filledHeight,
                    sprite.getU(), sprite.getV() + (sprite.getHeight() - filledHeight),
                    sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);

        } else {

            final float verticalSlices = filledHeight / 16.0f;
            int verticalSliceIdx = 0;

            for (; verticalSliceIdx <= verticalSlices - 1.0f; ++verticalSliceIdx) {

                final int sliceY2 = y2 - (verticalSliceIdx * 16);
                final int sliceY1 = sliceY2 - 16;

                blitSprite(matrix, x, x + areaWidth, sliceY1, sliceY2, zLevel, sprite.getWidth(), sprite.getHeight(),
                        sprite.getU(), sprite.getV(), sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);
            }

            final float missing = verticalSlices - verticalSliceIdx;

            if (missing > 0.0f) {

                final int sliceY2 = y2 - (verticalSliceIdx * 16);
                final int sliceY1 = sliceY2 - (int)Math.ceil(16 * missing);

                blitSprite(matrix, x, x + areaWidth, sliceY1, sliceY2, zLevel, sprite.getWidth(), sprite.getHeight(),
                        sprite.getU(), sprite.getV(), sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);
            }
        }

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(matrix, o, x, y, zLevel, areaWidth, areaHeight));
    }

    /**
     * Paint a vertical rectangle filled, from the top down, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static void paintFlippedVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY,
                                                             final int zLevel, final Rectangle area, final double progress) {
        paintFlippedVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the top down, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     * @return the height of the painted sprite
     */
    public static void paintFlippedVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY,
                                                             final int zLevel, final Rectangle area, final double progress,
                                                             final Colour tint) {
        paintFlippedVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, tint);
    }

    /**
     * Paint a vertical rectangle filled, from the top down, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static void paintFlippedVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite,
                                                             final int x, final int y, final int zLevel,
                                                             final int areaWidth, final int areaHeight,
                                                             final double progress) {
        paintFlippedVerticalProgressBarSprite(matrix, sprite, x, y, zLevel, areaWidth, areaHeight, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the top down, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     * @return the height of the painted sprite
     */
    public static void paintFlippedVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite,
                                                             final int x, final int y, final int zLevel,
                                                             final int areaWidth, final int areaHeight,
                                                             final double progress, final Colour tint) {

        if (progress < 0.01) {
            return;
        }

        final int spriteHeight = sprite.getHeight();
        final int filledHeight = (int)(areaHeight * progress);
        final int y1 = y;
        final int y2 = y + filledHeight;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bindTexture(sprite);

        if (spriteHeight == areaHeight) {

            blitSprite(matrix, x, x + areaWidth, y1, y2, zLevel, sprite.getWidth(), filledHeight,
                    sprite.getU(), sprite.getV() + (sprite.getHeight() - filledHeight),
                    sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);

        } else {

            final float verticalSlices = filledHeight / 16.0f;
            int verticalSliceIdx = 0;

            for (; verticalSliceIdx <= verticalSlices - 1.0f; ++verticalSliceIdx) {

                final int sliceY1 = y1 + (verticalSliceIdx * 16);
                final int sliceY2 = sliceY1 + 16;

                blitSprite(matrix, x, x + areaWidth, sliceY1, sliceY2, zLevel, sprite.getWidth(), sprite.getHeight(),
                        sprite.getU(), sprite.getV(), sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);
            }

            final float missing = verticalSlices - verticalSliceIdx;

            if (missing > 0.0f) {

                final int h = (int)Math.ceil(16 * missing);
                final int sliceY1 = y1 + (verticalSliceIdx * 16);
                final int sliceY2 = sliceY1 + h;

                blitSprite(matrix, x, x + areaWidth, sliceY1, sliceY2, zLevel, sprite.getWidth(), h,
                        sprite.getU(), sprite.getV(), sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);
            }
        }

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(matrix, o, x, y, zLevel, areaWidth, areaHeight));
    }

    /**
     * Paint a horizontal rectangle filled, from the left to the right, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param skip number of pixels to skip at the left of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static int paintHorizontalProgressSprite(final MatrixStack matrix, final ISprite sprite, final Colour tint,
                                                    final Point screenXY, final int zLevel, final Rectangle area,
                                                    final int skip, final double progress) {
        return paintHorizontalProgressSprite(matrix, sprite, tint, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, skip, progress);
    }

    /**
     * Paint a horizontal rectangle filled, from the left to the right, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param skip number of pixels to skip at the left of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the width of the painted sprite
     */
    public static int paintHorizontalProgressSprite(final MatrixStack matrix, final ISprite sprite, final Colour tint,
                                                    final int x, final int y, final int zLevel,
                                                    final int areaWidth, final int areaHeight, final int skip,
                                                    final double progress) {

        if (progress < 0.01) {
            return 0;
        }

        final int filledWidth = (int)(areaWidth * progress);
        final int x1 = x + skip;
        final int x2 = x1 + filledWidth;

        paintProgressSprite(matrix, sprite, tint, x1, y, x2, y + areaHeight, zLevel);
        return filledWidth;
    }

    private static void paintProgressSprite(final MatrixStack matrix, final ISprite sprite, final Colour tint,
                                            final int x1, final int y1, final int x2, final int y2, final int zLevel) {

        bindTexture(sprite);
        blitSprite(matrix.getLast().getMatrix(), x1, x2, y1, y2, zLevel, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), tint);

        sprite.applyOverlay(o -> paintProgressSprite(matrix, o, tint, x1, y1, x2, y2, zLevel));
    }

    //endregion
    //region rectangles

    /**
     * Paint a solid color rectangle with the specified coordinates and colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param screenXY1 starting painting coordinates relative to the top-left corner of the screen
     * @param screenXY2 ending painting coordinates relative to the top-left corner of the screen (not included in the rectangle)
     * @param zLevel the position on the Z axis for the rectangle
     * @param colour    the colour to be used to fill the rectangle
     */
    public static void paintSolidRect(final MatrixStack matrix, final Point screenXY1, final Point screenXY2,
                                      final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, zLevel, colour.toARGB());
    }

    /**
     * Paint a solid color rectangle with the specified coordinates and colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1 starting point on the X axis
     * @param y1 starting point on the Y axis
     * @param x2 ending point on the X axis (not included in the rectangle)
     * @param y2 ending point on the Y axis (not included in the rectangle)
     * @param zLevel the position on the Z axis for the rectangle
     * @param colour the colour to be used to fill the rectangle
     */
    public static void paintSolidRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2,
                                      final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), x1, y1, x2, y2, zLevel, colour.toARGB());
    }

    /**
     * Paint the perimeter of a rectangle with the specified coordinates and colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param screenXY starting painting coordinates relative to the top-left corner of the screen
     * @param width the length of the rectangle
     * @param height the height of the rectangle
     * @param zLevel the position on the Z axis for all the rectangles
     * @param colour the colour to be used to paint the perimeter
     */
    public static void paintHollowRect(final MatrixStack matrix, final Point screenXY, final int width, final int height,
                                       final int zLevel, final Colour colour) {
        paintHollowRect(matrix, screenXY.X, screenXY.Y, width, height, zLevel, colour);
    }

    /**
     * Paint the perimeter of a rectangle with the specified coordinates and colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1 starting point on the X axis
     * @param y1 starting point on the Y axis
     * @param width the length of the rectangle
     * @param height the height of the rectangle
     * @param zLevel the position on the Z axis for all the rectangles
     * @param colour the colour to be used to paint the perimeter
     */
    public static void paintHollowRect(final MatrixStack matrix, final int x1, final int y1,
                                       final int width, final int height, final int zLevel, final Colour colour) {

        paintHorizontalLine(matrix, x1, y1, width, zLevel, colour);
        paintVerticalLine(matrix, x1 + width - 1, y1 + 1, height - 2, zLevel, colour);
        paintHorizontalLine(matrix, x1, y1 + height - 1, width, zLevel, colour);
        paintVerticalLine(matrix, x1, y1 + 1, height - 2, zLevel, colour);
    }


    /**
     * Paint a rectangle filled with a 3D gradient from a light colour to a dark colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param zLevel        the position on the Z axis for the rectangle
     * @param lightColour   the light colour to be used for the gradient
     * @param darkColour    the dark colour to be used for the gradient
     */
    public static void paintTriangularGradientRect(final MatrixStack matrix, final int x, final int y,
                                                   final int width, final int height, final int zLevel,
                                                   final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();
        final int x2 = x + width - 1;
        final int y2 = y + height - 1;

        builder.pos(x2,  y, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos( x,  y, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos( x, y2, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x2, y2, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    //endregion
    //region lines

    /**
     * Paint a 1 pixel wide horizontal line in the provided colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param screenXY starting point
     * @param length the length of the line
     * @param zLevel the position on the Z axis for the line
     * @param colour the colour to be used to paint the line
     */
    public static void paintHorizontalLine(final MatrixStack matrix, final Point screenXY, final int length,
                                           final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), screenXY.X, screenXY.Y, screenXY.X + length + 1, screenXY.Y + 1,
                zLevel, colour.toARGB());
    }

    /**
     * Paint a 1 pixel wide horizontal line in the provided colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     * @param length the length of the line
     * @param zLevel the position on the Z axis for the line
     * @param colour the colour to be used to paint the line
     */
    public static void paintHorizontalLine(final MatrixStack matrix, final int x, final int y, final int length,
                                           final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), x, y, x + length, y + 1, zLevel, colour.toARGB());
    }

    /**
     * Paint a 1 pixel wide vertical line in the provided colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param screenXY starting point
     * @param length the length of the line
     * @param zLevel the position on the Z axis for the line
     * @param colour the colour to be used to paint the line
     */

    public static void paintVerticalLine(final MatrixStack matrix, final Point screenXY, final int length,
                                         final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), screenXY.X, screenXY.Y, screenXY.X + 1, screenXY.Y + length + 1,
                zLevel, colour.toARGB());
    }

    /**
     * Paint a 1 pixel wide vertical line in the provided colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param length    the length of the line
     * @param zLevel    the position on the Z axis for the line
     * @param colour    the colour to be used to paint the line
     */

    public static void paintVerticalLine(final MatrixStack matrix, final int x, final int y, final int length,
                                         final int zLevel, final Colour colour) {
        fill(matrix.getLast().getMatrix(), x, y, x + 1, y + length, zLevel, colour.toARGB());
    }

    //endregion
    //region buttons

    public static void paintButton3D(final MatrixStack matrix, final Point screenXY, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour gradientLightColour,
                                     final Colour gradientDarkColour, final Colour borderLightColour,
                                     final Colour borderDarkColour) {
        paintButton3D(matrix, screenXY.X, screenXY.Y, width, height, zLevel, darkOutlineColour, gradientLightColour,
                gradientDarkColour, borderLightColour, borderDarkColour);
    }

    public static void paintButton3D(final MatrixStack matrix, final int x, final int y, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour gradientLightColour,
                                     final Colour gradientDarkColour, final Colour borderLightColour,
                                     final Colour borderDarkColour) {

        paintHollowRect(matrix, x, y, width, height, zLevel, darkOutlineColour);
        paintTriangularGradientRect(matrix, x + 2, y + 2, width - 3, height - 3, zLevel, gradientLightColour, gradientDarkColour);

        paintHorizontalLine(matrix, x + 1, y + 1, width - 3+1, zLevel, borderLightColour);
        paintVerticalLine(matrix, x + 1, y + 1, height - 3, zLevel, borderLightColour);
        paintHorizontalLine(matrix, x + 1, y + height - 2, width - 2, zLevel, borderDarkColour);
        paintVerticalLine(matrix, x + width - 2, y + 1+1, height - 3, zLevel, borderDarkColour);
    }

    //endregion
    //region message box

    public static void paintMessage(final MatrixStack matrix, final IRichText message, final int x, final int y,
                                    final int zLevel, final int margin, final Colour background,
                                    final Colour highlight1, final Colour highlight2) {

        final Rectangle boxBounds = message.bounds()
                .expand(margin * 2, margin * 2)
                .offset(x, y);

        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX1(), boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, background);
        ModRenderHelper.paintSolidRect(matrix, boxBounds.getX1() + 1, boxBounds.getY1(), boxBounds.getX2(), boxBounds.getY2() + 1, zLevel, background);
        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX2(), boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, background);

        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX1() + 1, boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY1() + 1, boxBounds.Width - 4, zLevel, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY2() - 1, boxBounds.Width - 4, zLevel, highlight1, highlight2);
        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX2() - 1, boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, highlight1, highlight2);

        message.paint(matrix, boxBounds.getX1() + margin, boxBounds.getY1() + margin, zLevel + 1);
    }

    //endregion
    //region internal helpers

    // copied from AbstractGui::innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
    private static void blitSprite(final Matrix4f matrix, final int x1, final int x2, final int y1, final int y2,
                                   final int blitOffset, final float minU, final float maxU, final float minV, final float maxV) {

        final BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    private static void blitSprite(final Matrix4f matrix, final int x1, final int x2, final int y1, final int y2,
                                   final int blitOffset, final float minU, final float maxU, final float minV, final float maxV,
                                   final Colour tint) {

        final BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    // copied from AbstractGui::innerBlit(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
    private static void blitSprite(final MatrixStack matrix, final int x1, final int x2, final int y1, final int y2, final int blitOffset,
                                   final int spriteWidth, final int spriteHeight, final float u, final float v,
                                   final int textureWidth, final int textureHeight) {
        blitSprite(matrix.getLast().getMatrix(), x1, x2, y1, y2, blitOffset,
                (u + 0.0F) / (float)textureWidth, (u + (float)spriteWidth) / (float)textureWidth,
                (v + 0.0F) / (float)textureHeight, (v + (float)spriteHeight) / (float)textureHeight);
    }

    private static void blitSprite(final MatrixStack matrix, final int x1, final int x2, final int y1, final int y2, final int blitOffset,
                                   final int spriteWidth, final int spriteHeight, final float u, final float v,
                                   final int textureWidth, final int textureHeight, final Colour tint) {
        blitSprite(matrix.getLast().getMatrix(), x1, x2, y1, y2, blitOffset,
                (u + 0.0F) / (float)textureWidth, (u + (float)spriteWidth) / (float)textureWidth,
                (v + 0.0F) / (float)textureHeight, (v + (float)spriteHeight) / (float)textureHeight, tint);
    }

    // modified from AbstractGui::fill(Matrix4f matrix, int minX, int minY, int maxX, int maxY, int color)
    private static void fill(final Matrix4f matrix, int minX, int minY, int maxX, int maxY, final int zLevel, final int color) {

        if (minX < maxX) {

            int i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {

            int j = minY;
            minY = maxY;
            maxY = j;
        }

        final float a = (float)(color >> 24 & 255) / 255.0F;
        final float r = (float)(color >> 16 & 255) / 255.0F;
        final float g = (float)(color >> 8 & 255) / 255.0F;
        final float b = (float)(color & 255) / 255.0F;
        final BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(matrix, (float)minX, (float)maxY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, (float)maxX, (float)maxY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, (float)maxX, (float)minY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, (float)minX, (float)minY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    //endregion
    //endregion
    //region common paint tasks

    public static boolean paintItemStack(final MatrixStack matrix, final ItemStack stack, final int x, final int y,
                                         final String text, final boolean highlight) {

        if (stack.isEmpty()) {
            return false;
        }

        final Minecraft mc = Minecraft.getInstance();
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        float saveZ = itemRenderer.zLevel;

        if (highlight) {
            fill(matrix.getLast().getMatrix(), x, y, x + 16, y + 16, 300, -2130706433);
        }

        itemRenderer.zLevel = 300.0F;
        RenderSystem.enableDepthTest();
        itemRenderer.renderItemAndEffectIntoGUI(mc.player, stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x + 4, y, text);
        itemRenderer.zLevel = saveZ;

        return true;
    }

    public static boolean paintItemStackWithCount(final MatrixStack matrix, final ItemStack stack,
                                                  final int x, final int y, final boolean highlight) {
        return !stack.isEmpty() &&
                paintItemStack(matrix, stack, x, y, CodeHelper.formatAsHumanReadableNumber(stack.getCount(), ""), highlight);
    }

    /**
     * Paint a series of lines in a solid colour.
     * <p>
     * The vertices parameter is interpreted as a series of 2 vertex per line (x, y).
     * Each vertex is relative to the screen upper/left corner. The lines don't need to be connected to each others
     * <p>
     * If the wrong number of vertices are passed in (not multiple of 2) an ArrayIndexOutOfBoundsException will be raised
     *
     * @param colour    the colour to be used to fill the rectangle
     * @param thickness the thickness of the lines
     * @param zLevel    the position on the Z axis for all the lines
     * @param vertices  the vertices of the lines
     *
     */
    public static void paintSolidLines(final MatrixStack matrix, final Colour colour, final double thickness, final double zLevel, final double... vertices) {

        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
                GlStateManager.DestFactor.ZERO.param);
        GlStateManager.lineWidth((float)thickness);

        final int verticesCount = vertices.length;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < verticesCount; i += 2) {
            builder.pos(vertices[i], vertices[i + 1], zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    /**
     * Paint a series of solid colour rectangles with the specified coordinates and colour.
     * <p>
     * The vertices parameter is interpreted as a series of 4 vertex per rectangle (x1, y1, x2, y2).
     * Each vertex is relative to the screen upper/left corner.
     * <p>
     * If the wrong number of vertices are passed in (not multiple of 4) an ArrayIndexOutOfBoundsException will be raised
     *
     * @param colour    the colour to be used to fill the rectangle
     * @param zLevel    the position on the Z axis for all the rectangles
     * @param vertices  the vertices of the rectangles
     */
    public static void paintSolidRects(final MatrixStack matrix, final Colour colour, final double zLevel, final int... vertices) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param,
                GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        ModRenderHelper.glSetColour(colour);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 4) {

            final double x1 = vertices[i];
            final double y1 = vertices[i + 1];
            final double x2 = vertices[i + 2];
            final double y2 = vertices[i + 3];

            builder.pos(x1, y2, zLevel).endVertex();
            builder.pos(x2, y2, zLevel).endVertex();
            builder.pos(x2, y1, zLevel).endVertex();
            builder.pos(x1, y1, zLevel).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    /**
     * Paint a series of solid colour rectangles with the specified coordinates and colour.
     * <p>
     * The vertices parameter is interpreted as a series of 4 vertex per rectangle (x1, y1, x2, y2).
     * Each vertex is relative to the screen upper/left corner.
     * <p>
     * If the wrong number of vertices are passed in (not multiple of 4) an ArrayIndexOutOfBoundsException will be raised
     *
     * @param colour    the colour to be used to fill the rectangle
     * @param zLevel    the position on the Z axis for all the rectangles
     * @param vertices  the vertices of the rectangles
     */
    public static void paintSolidTriangles(final MatrixStack matrix, final Colour colour, final double zLevel, final int... vertices) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param,
                GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        ModRenderHelper.glSetColour(colour);

        builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 6) {

            final double x1 = vertices[i];
            final double y1 = vertices[i + 1];
            final double x2 = vertices[i + 2];
            final double y2 = vertices[i + 3];
            final double x3 = vertices[i + 4];
            final double y3 = vertices[i + 5];

            builder.pos(x1, y1, zLevel).endVertex();
            builder.pos(x2, y2, zLevel).endVertex();
            builder.pos(x3, y3, zLevel).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    /**
     * Paint a 1 pixel wide horizontal line filled with a horizontal gradient from one colour to another.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param length    the length of the line
     * @param zLevel    the position on the Z axis for the line
     * @param startColour   the starting colour to be used for the gradient
     * @param endColour     the ending colour to be used for the gradient
     */
    public static void paintHorizontalGradientLine(final MatrixStack matrix, final int x, final int y, final int length, final double zLevel,
                                                   final Colour startColour, final Colour endColour) {
        ModRenderHelper.paintHorizontalGradientRect(matrix, x, y, x + length, y + 1, zLevel, startColour, endColour);
    }

    /**
     * Paint a 1 pixel wide vertical line filled with a vertical gradient from one colour to another.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     * <p>
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param length    the length of the line
     * @param zLevel    the position on the Z axis for the line
     * @param startColour   the starting colour to be used for the gradient
     * @param endColour     the ending colour to be used for the gradient
     */
    public static void paintVerticalGradientLine(final MatrixStack matrix, final int x, final int y, final int length, final double zLevel,
                                                 final Colour startColour, final Colour endColour) {
        ModRenderHelper.paintVerticalGradientRect(matrix, x, y, x + 1, y + length, zLevel, startColour, endColour);
    }

    /**
     * Paint a rectangle filled with a vertical gradient from one colour to another.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            starting point on the X axis
     * @param y1            starting point on the Y axis
     * @param x2            ending point on the X axis (not included in the rectangle)
     * @param y2            ending point on the Y axis (not included in the rectangle)
     * @param zLevel        the position on the Z axis for the rectangle
     * @param startColour   the starting colour to be used for the gradient
     * @param endColour     the ending colour to be used for the gradient
     */
    public static void paintVerticalGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                                 final Colour startColour, final Colour endColour) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        final float startAlpha = startColour.glAlpha();
        final float startRed = startColour.glRed();
        final float startGreen = startColour.glGreen();
        final float startBlue = startColour.glBlue();
        final float endAlpha = endColour.glAlpha();
        final float endRed = endColour.glRed();
        final float endGreen = endColour.glGreen();
        final float endBlue = endColour.glBlue();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param,
                GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        builder.pos(x2, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x1, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x1, y2, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        builder.pos(x2, y2, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a rectangle filled with a horizontal gradient from one colour to another.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            starting point on the X axis
     * @param y1            starting point on the Y axis
     * @param x2            ending point on the X axis (not included in the rectangle)
     * @param y2            ending point on the Y axis (not included in the rectangle)
     * @param zLevel        the position on the Z axis for the rectangle
     * @param startColour   the starting colour to be used for the gradient
     * @param endColour     the ending colour to be used for the gradient
     */
    public static void paintHorizontalGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                           final Colour startColour, final Colour endColour) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        final float startAlpha = startColour.glAlpha();
        final float startRed = startColour.glRed();
        final float startGreen = startColour.glGreen();
        final float startBlue = startColour.glBlue();
        final float endAlpha = endColour.glAlpha();
        final float endRed = endColour.glRed();
        final float endGreen = endColour.glGreen();
        final float endBlue = endColour.glBlue();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param,
                GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        builder.pos(x1, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x1, y2, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x2, y2, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        builder.pos(x2, y1, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a rectangle filled with a 3D gradient from a light colour to a dark colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            starting point on the X axis
     * @param y1            starting point on the Y axis
     * @param x2            ending point on the X axis (not included in the rectangle)
     * @param y2            ending point on the Y axis (not included in the rectangle)
     * @param zLevel        the position on the Z axis for the rectangle
     * @param lightColour   the light colour to be used for the gradient
     * @param darkColour    the dark colour to be used for the gradient
     */
    public static void paint3DGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                           final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
                GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        builder.pos(x2, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x1, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x1, y2, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x2, y2, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a triangle filled with a 3D gradient from a light colour to a dark colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            starting point on the X axis
     * @param y1            starting point on the Y axis
     * @param x2            ending point on the X axis (not included in the rectangle)
     * @param y2            ending point on the Y axis (not included in the rectangle)
     * @param zLevel        the position on the Z axis for the rectangle
     * @param lightColour   the light colour to be used for the gradient
     * @param darkColour    the dark colour to be used for the gradient
     */
    public static void paint3DGradientTriangle(final MatrixStack matrix, final double x1, final double y1, final double x2, final double y2,
                                               final double x3, final double y3, final double zLevel,
                                               final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
                GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        builder.pos(x1, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.pos(x2, y2, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();
        builder.pos(x3, y3, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a textured rectangle with the specified coordinates and the texture currently bound to the TextureManager.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param zLevel    the position on the Z axis for the rectangle
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param minU      the starting U coordinates of the texture
     * @param minV      the starting V coordinates of the texture
     */
    public static void paintTexturedRect(final MatrixStack matrix, final int x, final int y, final double zLevel, final int width, final int height,
                                         final int minU, final int minV) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        final float textureScale = 1.0f / (16 * 16);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        builder.pos(x        , y + height, zLevel).tex(textureScale * minU          , textureScale * (minV + height)).endVertex();
        builder.pos(x + width, y + height, zLevel).tex(textureScale * (minU + width), textureScale * (minV + height)).endVertex();
        builder.pos(x + width, y         , zLevel).tex(textureScale * (minU + width), textureScale * minV).endVertex();
        builder.pos(x        , y         , zLevel).tex(textureScale * minU          , textureScale * minV).endVertex();

        tessellator.draw();
    }

    public static void paint3DSunkenBox(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                        final Colour gradientLightColour, final Colour gradientDarkColour,
                                        final Colour borderLightColour, final Colour borderDarkColour) {

        ModRenderHelper.paint3DGradientRect(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, zLevel, gradientLightColour, gradientDarkColour);

        ModRenderHelper.paintSolidRects(matrix, borderDarkColour, zLevel,
                x1, y1, x2, y1 + 1,
                x1, y1, x1 + 1, y2);

        ModRenderHelper.paintSolidRects(matrix, borderLightColour, zLevel,
                x1, y2 - 1, x2, y2,
                x2 - 1, y1, x2, y2);
    }

    //endregion
    //region GL helpers

    public static void glSetColour(final Colour colour) {
        RenderSystem.color4f(colour.glRed(), colour.glGreen(), colour.glBlue(), colour.glAlpha());
    }

    public static void glSetViewport(final int x, final int y, final int width, final int height) {
        RenderSystem.viewport(x, y, width, height);
    }

    public static void glSetViewport(final double x, final double y, final double width, final double height) {
        RenderSystem.viewport(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(width), MathHelper.floor(height));
    }

    public static void glSetDefaultViewport() {
        RenderSystem.viewport(0, 0, Minecraft.getInstance().getMainWindow().getFramebufferWidth(),
                Minecraft.getInstance().getMainWindow().getFramebufferHeight());
    }

    public static Matrix4f glPerspectiveMatrix(final float fov, final float aspect, final float zNear, final float zFar) {
        return Matrix4f.perspective(fov, aspect, zNear, zFar);
    }

    public static void glPerspective(final float fov, final float aspect, final float zNear, final float zFar) {
        RenderSystem.multMatrix(glPerspectiveMatrix(fov, aspect, zNear, zFar));
    }

    //region internals

    private ModRenderHelper(){
    }

    //endregion
}

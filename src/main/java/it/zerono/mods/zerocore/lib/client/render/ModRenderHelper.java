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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class ModRenderHelper {

    public static final float ONE_PIXEL = 1.0f / 16.0f;

    public static final int GUI_TOPMOST_Z = 900;
    public static final int GUI_ITEM_Z = 600;

    public static final NonNullSupplier<Font> DEFAULT_FONT_RENDERER = () -> Minecraft.getInstance().font;

    public static long getLastRenderTime() {
        return ZeroCore.getProxy().getLastRenderTime();
    }

    public static ModelManager getModelManager() {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager();
    }

    @SuppressWarnings("ConstantConditions")
    public static UnbakedModel getModel(final ResourceLocation location) {
        return ForgeModelBakery.instance().getModelOrMissing(location);
    }

    public static BakedModel getModel(final BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state);
    }

    public static BakedModel getModel(final ModelResourceLocation modelLocation) {
        return getModelManager().getModel(modelLocation);
    }

    public static BakedModel getMissingModel() {
        return getModelManager().getMissingModel();
    }

    @Nullable
    public static BakedModel getMissingModel(Map<ResourceLocation, BakedModel> modelRegistry) {
        return modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
    }

    public static void bindTexture(final ResourceLocation textureLocation) {
        RenderSystem.setShaderTexture(0, textureLocation);
    }

    public static void bindTexture(final ISprite sprite) {
        RenderSystem.setShaderTexture(0, sprite.getTextureMap().getTextureLocation());
    }

    public static void bindBlocksTexture() {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    }

    public static TextureAtlasSprite getTextureSprite(final ResourceLocation location) {
        return getTextureSprite(InventoryMenu.BLOCK_ATLAS, location);
    }

    public static TextureAtlasSprite getTextureSprite(final ResourceLocation atlasName, final ResourceLocation spriteName) {
        return Minecraft.getInstance().getTextureAtlas(atlasName).apply(spriteName);
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
        return getTextureSprite(atlasName, MissingTextureAtlasSprite.getLocation());
    }

    public static TextureAtlasSprite getMissingTexture() {
        return getTextureSprite(MissingTextureAtlasSprite.getLocation());
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final Fluid fluid) {

        final ResourceLocation rl = fluid.getAttributes().getOverlayTexture();

        return null != rl ? ModRenderHelper.getTextureSprite(rl) : null;
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final FluidStack stack) {
        return getFluidOverlaySprite(stack.getFluid());
    }

    public static int getFluidTint(final Fluid fluid) {
        return fluid.getAttributes().getColor();
    }

    public static Colour getFluidTintColour(final Fluid fluid) {
        return Colour.fromARGB(getFluidTint(fluid));
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

    public static List<FormattedText> splitLines(final Font font, final String line, final int maxLineWidth) {
        return splitLines(font, line, maxLineWidth, Style.EMPTY);
    }

    public static List<FormattedText> splitLines(final Font font, final String line, final int maxLineWidth,
                                                   final Style lineStyle) {
        return font.getSplitter().splitLines(line, maxLineWidth, lineStyle);
    }

    public static List<FormattedText> splitLines(final Font font, final FormattedText line, final int maxLineWidth) {
        return splitLines(font, line, maxLineWidth, Style.EMPTY);
    }

    public static List<FormattedText> splitLines(final Font font, final FormattedText line, final int maxLineWidth,
                                                   final Style lineStyle) {
        return font.getSplitter().splitLines(line, maxLineWidth, lineStyle);
    }

    @Deprecated // use splitLines(Font, FormattedText, int)
    public static List<FormattedText> wrapLines(final FormattedText line, final int maxLineWidth, final Font font) {
        return wrapLines(line, Style.EMPTY, maxLineWidth, font);
    }

    @Deprecated // use splitLines(Font, FormattedText, int, Style)
    public static List<FormattedText> wrapLines(final FormattedText line, final Style lineStyle,
                                                  final int maxLineWidth, final Font font) {
        return font.getSplitter().splitLines(line, maxLineWidth, lineStyle);
    }

    @Deprecated // use splitLines(Font, String, int, Style)
    public static List<String> wrapLines(final String text, final int maxLineWidth, final Font font) {

        final List<String> lines = Lists.newLinkedList();
        final int spaceWidth = font.width(" ");

        final String[] tokens = text.split("\\s+");
        final Integer[] tokenWidths = Arrays.stream(tokens).map(font::width).toArray(Integer[]::new);

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
    public static void renderQuads(final PoseStack matrix, final VertexConsumer builder, final List<BakedQuad> quads,
                                   final int combinedLight, final int combinedOverlay) {

        final PoseStack.Pose entry = matrix.last();

        for (final BakedQuad quad : quads) {
            builder.putBulkData(entry, quad, 1, 1, 1, combinedLight, combinedOverlay, true);
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
    public static void renderQuads(final PoseStack matrix, final VertexConsumer builder, final List<BakedQuad> quads,
                                   final int combinedLight, final int combinedOverlay,
                                   final IntFunction<Colour> quadTintGetter) {

        final PoseStack.Pose entry = matrix.last();

        for (final BakedQuad quad : quads) {

            float red, green, blue;

            if (quad.isTinted()) {

                final Colour tint = quadTintGetter.apply(quad.getTintIndex());

                red = tint.R;
                green = tint.G;
                blue = tint.B;

            } else {

                red = green = blue = 1;
            }

            builder.putBulkData(entry, quad, red, green, blue, combinedLight, combinedOverlay, true);
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
    public static void renderModel(final BakedModel model, final IModelData data, final PoseStack matrix,
                                   final VertexConsumer builder, final int combinedLight, final int combinedOverlay) {

        for (final Direction direction : CodeHelper.DIRECTIONS) {
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
    public static void renderModel(final BakedModel model, final IModelData data, final PoseStack matrix,
                                   final VertexConsumer builder, final int combinedLight, final int combinedOverlay,
                                   final IntFunction<Colour> quadTintGetter) {

        for (final Direction direction : CodeHelper.DIRECTIONS) {
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

    public static void paintVoxelShape(final VoxelShape shape, final PoseStack matrix, final MultiBufferSource bufferSource,
                                       final RenderType renderType, final double originX, final double originY,
                                       final double originZ, final Colour colour) {

        final VertexConsumer buffer = bufferSource.getBuffer(renderType);
        final Matrix4f m = matrix.last().pose();
        final Matrix3f normal = matrix.last().normal();
        final float red = colour.glRed();
        final float green = colour.glGreen();
        final float blue = colour.glBlue();
        final float alpha = colour.glAlpha();

        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {

            float deltaX = (float) (x2 - x1);
            float deltaY = (float) (y2 - y1);
            float deltaZ = (float) (z2 - z1);
            float len = Mth.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            deltaX = deltaX / len;
            deltaY = deltaY / len;
            deltaZ = deltaZ / len;

            buffer.vertex(m, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
                    .color(red, green, blue, alpha)
                    .normal(normal, deltaX, deltaY, deltaZ)
                    .endVertex();
            buffer.vertex(m, (float) (x2 + originX), (float) (y2 + originY), (float) (z2 + originZ))
                    .color(red, green, blue, alpha)
                    .normal(normal, deltaX, deltaY, deltaZ)
                    .endVertex();
        });
    }

    //endregion
    //region 2D/GUI paint helpers (with matrix)
    //region sprites

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final PoseStack matrix, final ISprite sprite, final Point screenXY, final int zLevel,
                                   final int width, final int height) {
        paintSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel, width, height);
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final PoseStack matrix, final ISprite sprite, final int x, final int y,
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
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * Draw only part of the sprite, by masking off parts of it. For compatibly with JEI IDrawableStatic interface
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param xOffset painting coordinates relative to the top-left corner of the screen
     * @param yOffset painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param padding padding
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     * @param maskTop mask offset form the top of the sprite
     * @param maskBottom mask offset form the bottom of the sprite
     * @param maskLeft mask offset form the left of the sprite
     * @param maskRight mask offset form the right of the sprite
     */
    public static void paintSprite(final PoseStack matrix, final ISprite sprite, final int xOffset, final int yOffset,
                                   final int zLevel, final Padding padding, final int width, final int height,
                                   final int maskTop, final int maskBottom, final int maskLeft, final int maskRight) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ModRenderHelper.bindTexture(sprite);

        final int x = xOffset + padding.getLeft() + maskLeft;
        final int y = yOffset + padding.getTop() + maskTop;
        final int u = sprite.getU() + maskLeft;
        final int v = sprite.getV() + maskTop;
        final int paintWidth = width - maskRight - maskLeft;
        final int paintHeight = height - maskBottom - maskTop;
        final float widthRatio = 1.0F / sprite.getTextureMap().getWidth();
        final float heightRatio = 1.0F / sprite.getTextureMap().getHeight();

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pose, x, y + paintHeight, zLevel).uv(u * widthRatio, (v + (float) paintHeight) * heightRatio).endVertex();
        bufferbuilder.vertex(pose, x + paintWidth, y + paintHeight, zLevel).uv((u + (float) paintWidth) * widthRatio, (v + (float) paintHeight) * heightRatio).endVertex();
        bufferbuilder.vertex(pose, x + paintWidth, y, zLevel).uv((u + (float) paintWidth) * widthRatio, v * heightRatio).endVertex();
        bufferbuilder.vertex(pose, x, y, zLevel).uv(u * widthRatio, v * heightRatio).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(matrix, o, xOffset, yOffset, zLevel, padding, width, height, maskTop, maskBottom, maskLeft, maskRight));
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param skip number of pixels to skip at the bottom of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static int paintVerticalProgressSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
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
     * @param matrix the PoseStack for the current paint operation
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
    public static int paintVerticalProgressSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite, final Point screenXY,
                                                      final int zLevel, final Rectangle area, final double progress) {
        paintVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite, final Point screenXY,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite,
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

            blitSprite(matrix, x, x + sprite.getWidth(), y, y+sprite.getHeight(), zLevel, sprite.getWidth(), sprite.getHeight(),
                    sprite.getU(), sprite.getV(),
                    sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);

        } else {

            final int spriteHeight = sprite.getHeight();
            final float verticalSlices = filledHeight / (float)spriteHeight/*16.0f*/;
            int verticalSliceIdx = 0;

            for (; verticalSliceIdx <= verticalSlices - 1.0f; ++verticalSliceIdx) {

                final int sliceY2 = y2 - (verticalSliceIdx * spriteHeight/*16*/);
                final int sliceY1 = sliceY2 - spriteHeight/*16*/;

                blitSprite(matrix, x, x + areaWidth+16, sliceY1, sliceY2, zLevel, sprite.getWidth(), sprite.getHeight(),
                        sprite.getU(), sprite.getV(), sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight(), tint);

                verticalSliceIdx  = 5;
            }

            final float missing = verticalSlices - verticalSliceIdx;

            if (missing > 0.0f) {

                final int sliceY2 = y2 - (verticalSliceIdx * spriteHeight/*16*/);
                final int sliceY1 = sliceY2 - (int)Math.ceil(spriteHeight/*16*/ * missing);

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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintFlippedVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite, final Point screenXY,
                                                             final int zLevel, final Rectangle area, final double progress) {
        paintFlippedVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress, Colour.WHITE);
    }

    /**
     * Paint a vertical rectangle filled, from the top down, with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintFlippedVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite, final Point screenXY,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintFlippedVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static void paintFlippedVerticalProgressBarSprite(final PoseStack matrix, final ISprite sprite,
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
     * @param matrix the PoseStack for the current paint operation
     * @param sprite the sprite to paint
     * @param tint the colour to tint the sprite with
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param skip number of pixels to skip at the left of the area
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    @Deprecated // use paintOrientedProgressBarSprite()
    public static int paintHorizontalProgressSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
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
     * @param matrix the PoseStack for the current paint operation
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
    @Deprecated // use paintOrientedProgressBarSprite()
    public static int paintHorizontalProgressSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
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

    @Deprecated
    private static void paintProgressSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
                                            final int x1, final int y1, final int x2, final int y2, final int zLevel) {

        bindTexture(sprite);
        blitSprite(matrix.last().pose(), x1, x2, y1, y2, zLevel, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), tint);

        sprite.applyOverlay(o -> paintProgressSprite(matrix, o, tint, x1, y1, x2, y2, zLevel));
    }

    /**
     * Paint a progress bar with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the PoseStack for the current paint operation
     * @param orientation the {@link Orientation} of the progress bar
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    public static void paintOrientedProgressBarSprite(final PoseStack matrix, final Orientation orientation,
                                                      final ISprite sprite, final Point screenXY, final int zLevel,
                                                      final Rectangle area, final double progress, final Colour tint) {

        switch (orientation) {

            case BottomToTop:
                paintBottomToTopTiledSprite(matrix, sprite, tint, screenXY.X, screenXY.Y + area.Height, zLevel, area.Width, (int)(area.Height * progress));
                break;

            case TopToBottom:
                paintTopToBottomTiledSprite(matrix, sprite, tint, screenXY.X, screenXY.Y, zLevel, area.Width, (int)(area.Height * progress));
                break;

            case LeftToRight:
                paintLeftToRightTiledSprite(matrix, sprite, tint, screenXY.X, screenXY.Y, zLevel, (int)(area.Width * progress), area.Height);
                break;

            case RightToLeft:
                paintRightToLeftTiledSprite(matrix, sprite, tint, screenXY.X + area.Width, screenXY.Y, zLevel, (int)(area.Width * progress), area.Height);
                break;
        }
    }

    /**
     * Paint a progress bar with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param matrix the PoseStack for the current paint operation
     * @param orientation the {@link Orientation} of the progress bar
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param areaWidth the width of the maximum area to be filled
     * @param areaHeight the height of the maximum area to be filled
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    public static void paintOrientedProgressBarSprite(final PoseStack matrix, final Orientation orientation,
                                                      final ISprite sprite, final int x, final int y, final int zLevel,
                                                      final int areaWidth, final int areaHeight, final double progress,
                                                      final Colour tint) {

        switch (orientation) {

            case BottomToTop:
                paintBottomToTopTiledSprite(matrix, sprite, tint, x, y + areaHeight, zLevel, areaWidth, (int)(areaHeight * progress));
                break;

            case TopToBottom:
                paintTopToBottomTiledSprite(matrix, sprite, tint, x, y, zLevel, areaWidth, (int)(areaHeight * progress));
                break;

            case LeftToRight:
                paintLeftToRightTiledSprite(matrix, sprite, tint, x, y, zLevel, (int)(areaWidth * progress), areaHeight);
                break;

            case RightToLeft:
                paintRightToLeftTiledSprite(matrix, sprite, tint, x + areaWidth, y, zLevel, (int)(areaWidth * progress), areaHeight);
                break;
        }
    }

    public static void paintTopToBottomTiledSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float spriteMinU = sprite.getMinU();
        final float spriteMaxU = sprite.getMaxU();
        final float spriteMinV = sprite.getMinV();
        final float spriteMaxV = sprite.getMaxV();
        final float deltaU = spriteMaxU - spriteMinU;
        final float deltaV = spriteMaxV - spriteMinV;

        final int spriteWidth = sprite.getWidth();
        final int spriteHeight = sprite.getHeight();
        final int horizontalTiles = paintWidth / spriteWidth;
        final int verticalTiles = paintHeight / spriteHeight;
        final int leftoverWidth = paintWidth - (horizontalTiles * spriteWidth);
        final int leftoverHeight = paintHeight - (verticalTiles * spriteHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bindTexture(sprite);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        for (int horizontalTile = 0; horizontalTile <= horizontalTiles; ++horizontalTile) {

            final int width = (horizontalTile == horizontalTiles) ? leftoverWidth : spriteWidth;

            if (0 == width) {
                break;
            }

            final int skippedWidth = spriteWidth - width;
            final float tileMaxU = spriteMaxU - (deltaU * skippedWidth / spriteWidth);
            final int tileX1 = x + (horizontalTile * spriteWidth);
            final int tileX2 = tileX1 + spriteWidth - skippedWidth;

            for (int verticalTile = 0; verticalTile <= verticalTiles; ++verticalTile) {

                final int height = (verticalTile == verticalTiles) ? leftoverHeight : spriteHeight;

                if (0 == height) {
                    break;
                }

                final int skippedHeight = spriteHeight - height;
                final float tileMaxV = spriteMaxV - (deltaV * skippedHeight / spriteHeight);
                final int tileY1 = y + (verticalTile * spriteHeight);
                final int tileY2 = tileY1 + height;

                bufferBuilder.vertex(pose, tileX1, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, spriteMinV).endVertex();
                bufferBuilder.vertex(pose, tileX1, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, spriteMinV).endVertex();
            }
        }

        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
    }

    public static void paintBottomToTopTiledSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float spriteMinU = sprite.getMinU();
        final float spriteMaxU = sprite.getMaxU();
        final float spriteMinV = sprite.getMinV();
        final float spriteMaxV = sprite.getMaxV();
        final float deltaU = spriteMaxU - spriteMinU;
        final float deltaV = spriteMaxV - spriteMinV;

        final int spriteWidth = sprite.getWidth();
        final int spriteHeight = sprite.getHeight();
        final int horizontalTiles = paintWidth / spriteWidth;
        final int verticalTiles = paintHeight / spriteHeight;
        final int leftoverWidth = paintWidth - (horizontalTiles * spriteWidth);
        final int leftoverHeight = paintHeight - (verticalTiles * spriteHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bindTexture(sprite);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        for (int horizontalTile = 0; horizontalTile <= horizontalTiles; ++horizontalTile) {

            final int width = (horizontalTile == horizontalTiles) ? leftoverWidth : spriteWidth;

            if (0 == width) {
                break;
            }

            final int skippedWidth = spriteWidth - width;
            final float tileMaxU = spriteMaxU - (deltaU * skippedWidth / spriteWidth);
            final int tileX1 = x + (horizontalTile * spriteWidth);
            final int tileX2 = tileX1 + spriteWidth - skippedWidth;

            for (int verticalTile = 0; verticalTile <= verticalTiles; ++verticalTile) {

                final int height = (verticalTile == verticalTiles) ? leftoverHeight : spriteHeight;

                if (0 == height) {
                    break;
                }

                final int skippedHeight = spriteHeight - height;
                final float tileMinV = spriteMaxV - (deltaV * height / spriteHeight);
                final int baseY = y - ((verticalTile + 1) * spriteHeight);
                final int tileY1 = baseY + skippedHeight;
                final int tileY2 = baseY + spriteHeight;

                bufferBuilder.vertex(pose, tileX1, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, spriteMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, spriteMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, tileMinV).endVertex();
                bufferBuilder.vertex(pose, tileX1, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, tileMinV).endVertex();
            }
        }

        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
    }

    public static void paintLeftToRightTiledSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float spriteMinU = sprite.getMinU();
        final float spriteMaxU = sprite.getMaxU();
        final float spriteMinV = sprite.getMinV();
        final float spriteMaxV = sprite.getMaxV();
        final float deltaU = spriteMaxU - spriteMinU;
        final float deltaV = spriteMaxV - spriteMinV;

        final int spriteWidth = sprite.getWidth();
        final int spriteHeight = sprite.getHeight();
        final int horizontalTiles = paintWidth / spriteWidth;
        final int verticalTiles = paintHeight / spriteHeight;
        final int leftoverWidth = paintWidth - (horizontalTiles * spriteWidth);
        final int leftoverHeight = paintHeight - (verticalTiles * spriteHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bindTexture(sprite);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        for (int horizontalTile = 0; horizontalTile <= horizontalTiles; ++horizontalTile) {

            final int width = (horizontalTile == horizontalTiles) ? leftoverWidth : spriteWidth;

            if (0 == width) {
                break;
            }

            final int skippedWidth = spriteWidth - width;
            final float tileMaxU = spriteMaxU - (deltaU * skippedWidth / spriteWidth);
            final int tileX1 = x + (horizontalTile * spriteWidth);
            final int tileX2 = tileX1 + spriteWidth - skippedWidth;

            for (int verticalTile = 0; verticalTile <= verticalTiles; ++verticalTile) {

                final int height = (verticalTile == verticalTiles) ? leftoverHeight : spriteHeight;

                if (0 == height) {
                    break;
                }

                final int skippedHeight = spriteHeight - height;
                final float tileMaxV = spriteMaxV - (deltaV * skippedHeight / spriteHeight);
                final int baseY = y + (verticalTile * spriteHeight);
                final int tileY1 = baseY + skippedHeight;
                final int tileY2 = baseY + spriteHeight;

                bufferBuilder.vertex(pose, tileX1, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMaxU, spriteMinV).endVertex();
                bufferBuilder.vertex(pose, tileX1, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMinU, spriteMinV).endVertex();
            }
        }

        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
    }

    public static void paintRightToLeftTiledSprite(final PoseStack matrix, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float spriteMinU = sprite.getMinU();
        final float spriteMaxU = sprite.getMaxU();
        final float spriteMinV = sprite.getMinV();
        final float spriteMaxV = sprite.getMaxV();
        final float deltaU = spriteMaxU - spriteMinU;
        final float deltaV = spriteMaxV - spriteMinV;

        final int spriteWidth = sprite.getWidth();
        final int spriteHeight = sprite.getHeight();
        final int horizontalTiles = paintWidth / spriteWidth;
        final int verticalTiles = paintHeight / spriteHeight;
        final int leftoverWidth = paintWidth - (horizontalTiles * spriteWidth);
        final int leftoverHeight = paintHeight - (verticalTiles * spriteHeight);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bindTexture(sprite);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        for (int horizontalTile = 0; horizontalTile <= horizontalTiles; ++horizontalTile) {

            final int width = (horizontalTile == horizontalTiles) ? leftoverWidth : spriteWidth;

            if (0 == width) {
                break;
            }

            final int skippedWidth = spriteWidth - width;
            final float tileMinU = spriteMaxU - (deltaU * width / spriteWidth);
            final int tileX2 = x - (horizontalTile * spriteWidth);
            final int tileX1 = tileX2 - spriteWidth + skippedWidth;

            for (int verticalTile = 0; verticalTile <= verticalTiles; ++verticalTile) {

                final int height = (verticalTile == verticalTiles) ? leftoverHeight : spriteHeight;

                if (0 == height) {
                    break;
                }

                final int skippedHeight = spriteHeight - height;
                final float tileMaxV = spriteMaxV - (deltaV * skippedHeight / spriteHeight);
                final int baseY = y + (verticalTile * spriteHeight);
                final int tileY1 = baseY + skippedHeight;
                final int tileY2 = baseY + spriteHeight;

                bufferBuilder.vertex(pose, tileX1, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMinU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY2, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMaxU, tileMaxV).endVertex();
                bufferBuilder.vertex(pose, tileX2, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(spriteMaxU, spriteMinV).endVertex();
                bufferBuilder.vertex(pose, tileX1, tileY1, zLevel).color(tint.R, tint.G, tint.B, tint.A).uv(tileMinU, spriteMinV).endVertex();
            }
        }

        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
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
    public static void paintSolidRect(final PoseStack matrix, final Point screenXY1, final Point screenXY2,
                                      final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, zLevel, colour.toARGB());
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
    public static void paintSolidRect(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2,
                                      final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), x1, y1, x2, y2, zLevel, colour.toARGB());
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
    public static void paintHollowRect(final PoseStack matrix, final Point screenXY, final int width, final int height,
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
    public static void paintHollowRect(final PoseStack matrix, final int x1, final int y1,
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
    public static void paintTriangularGradientRect(final PoseStack matrix, final int x, final int y,
                                                   final int width, final int height, final int zLevel,
                                                   final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        final Matrix4f pose = matrix.last().pose();
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

        builder.vertex(pose, x2,  y, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose,  x,  y, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose,  x, y2, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x2, y2, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
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
    public static void paintHorizontalLine(final PoseStack matrix, final Point screenXY, final int length,
                                           final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), screenXY.X, screenXY.Y, screenXY.X + length + 1, screenXY.Y + 1,
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
    public static void paintHorizontalLine(final PoseStack matrix, final int x, final int y, final int length,
                                           final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), x, y, x + length, y + 1, zLevel, colour.toARGB());
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

    public static void paintVerticalLine(final PoseStack matrix, final Point screenXY, final int length,
                                         final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), screenXY.X, screenXY.Y, screenXY.X + 1, screenXY.Y + length + 1,
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

    public static void paintVerticalLine(final PoseStack matrix, final int x, final int y, final int length,
                                         final int zLevel, final Colour colour) {
        fill(matrix.last().pose(), x, y, x + 1, y + length, zLevel, colour.toARGB());
    }

    //endregion
    //region buttons

    public static void paintButton3D(final PoseStack matrix, final Point screenXY, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour gradientLightColour,
                                     final Colour gradientDarkColour, final Colour borderLightColour,
                                     final Colour borderDarkColour) {
        paintButton3D(matrix, screenXY.X, screenXY.Y, width, height, zLevel, darkOutlineColour, gradientLightColour,
                gradientDarkColour, borderLightColour, borderDarkColour);
    }

    public static void paintButton3D(final PoseStack matrix, final int x, final int y, final int width, final int height,
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

    public static void paintButton3D(final PoseStack matrix, final Point screenXY, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour flatBackgroundColour,
                                     final Colour borderLightColour, final Colour borderDarkColour) {
        paintButton3D(matrix, screenXY.X, screenXY.Y, width, height, zLevel, darkOutlineColour, flatBackgroundColour,
                borderLightColour, borderDarkColour);
    }

    public static void paintButton3D(final PoseStack matrix, final int x, final int y, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour flatBackgroundColour,
                                     final Colour borderLightColour, final Colour borderDarkColour) {

        paintHollowRect(matrix, x, y, width, height, zLevel, darkOutlineColour);
        paintSolidRect(matrix, x + 2, y + 2, x + 2 + width - 3, y + 2 + height - 3, zLevel, flatBackgroundColour);

        paintHorizontalLine(matrix, x + 1, y + 1, width - 3+1, zLevel, borderLightColour);
        paintVerticalLine(matrix, x + 1, y + 1, height - 3, zLevel, borderLightColour);
        paintHorizontalLine(matrix, x + 1, y + height - 2, width - 2, zLevel, borderDarkColour);
        paintVerticalLine(matrix, x + width - 2, y + 1+1, height - 3, zLevel, borderDarkColour);
    }

    //endregion
    //region message box

    public static void paintMessage(final PoseStack matrix, final IRichText message, final int x, final int y,
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

        final BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).uv(minU, minV).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

    private static void blitSprite(final Matrix4f matrix, final int x1, final int x2, final int y1, final int y2,
                                   final int blitOffset, final float minU, final float maxU, final float minV, final float maxV,
                                   final Colour tint) {

        final BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).color(tint.R, tint.G, tint.B, tint.A).uv(minU, minV).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

    // copied from AbstractGui::innerBlit(PoseStack matrixStack, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
    private static void blitSprite(final PoseStack matrix, final int x1, final int x2, final int y1, final int y2, final int blitOffset,
                                   final int spriteWidth, final int spriteHeight, final float u, final float v,
                                   final int textureWidth, final int textureHeight) {
        blitSprite(matrix.last().pose(), x1, x2, y1, y2, blitOffset,
                (u + 0.0F) / (float)textureWidth, (u + (float)spriteWidth) / (float)textureWidth,
                (v + 0.0F) / (float)textureHeight, (v + (float)spriteHeight) / (float)textureHeight);
    }

    private static void blitSprite(final PoseStack matrix, final int x1, final int x2, final int y1, final int y2, final int blitOffset,
                                   final int spriteWidth, final int spriteHeight, final float u, final float v,
                                   final int textureWidth, final int textureHeight, final Colour tint) {
        blitSprite(matrix.last().pose(), x1, x2, y1, y2, blitOffset,
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
        final BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float)minX, (float)maxY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)maxY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)minY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, (float)minX, (float)minY, zLevel).color(r, g, b, a).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    //endregion
    //endregion
    //region common paint tasks

    public static boolean paintItemStack(final PoseStack matrix, final ItemStack stack, final int x, final int y,
                                         final String text, final boolean highlight) {

        if (stack.isEmpty()) {
            return false;
        }

        if (highlight) {
            fill(matrix.last().pose(), x, y, x + 16, y + 16, GUI_ITEM_Z - 1, -2130706433);
        }

        final Minecraft mc = Minecraft.getInstance();
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        float saveZ = itemRenderer.blitOffset;
        final PoseStack viewModelMatrix = RenderSystem.getModelViewStack();

        viewModelMatrix.pushPose();
        viewModelMatrix.mulPoseMatrix(matrix.last().pose());
        RenderSystem.applyModelViewMatrix();

        itemRenderer.blitOffset = GUI_ITEM_Z;
        RenderSystem.enableDepthTest();
        itemRenderer.renderAndDecorateItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(mc.font, stack, x + 4, y, text);
        itemRenderer.blitOffset = saveZ;

        viewModelMatrix.popPose();
        RenderSystem.applyModelViewMatrix();

        return true;
    }

    public static boolean paintItemStackWithCount(final PoseStack matrix, final ItemStack stack,
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
    public static void paintSolidLines(final PoseStack matrix, final Colour colour, final double thickness, final double zLevel, final double... vertices) {

        final Matrix4f pose = matrix.last().pose();
        final BufferBuilder builder = Tesselator.getInstance().getBuilder();
        final float halfThickness = (float)(thickness / 2.0);
        final int verticesCount = vertices.length;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < verticesCount;) {
            
            float x1 = (float)vertices[i++];
            float y1 = (float)vertices[i++];
            float x2 = (float)vertices[i++];
            float y2 = (float)vertices[i++];
            
            if (x1 == x2) {
                
                if (y2 < y1) {

                    float swap = y1;

                    y1 = y2;
                    y2 = swap;
                }
                
                builder.vertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 - halfThickness, y2 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 + halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 + halfThickness, y2 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                
            } else if (y1 == y2) {

                if (x2 < x1) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;
                }

                builder.vertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 - halfThickness, y1 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 + halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 + halfThickness, y1 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();

            } else if ((x1 < x2 && y1 < y2) || (x2 < x1 && y2 < y1)) {

                if (x2 < x1) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;

                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }

                builder.vertex(pose, x1 + halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 - halfThickness, y1 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 + halfThickness, y2 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 - halfThickness, y2 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();

            } else {

                if (x1 < x2) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;

                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }

                builder.vertex(pose, x1 + halfThickness, y1 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 + halfThickness, y2 + halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
                builder.vertex(pose, x2 - halfThickness, y2 - halfThickness, (float)zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
            }
        }

        builder.end();
        BufferUploader.end(builder);
        RenderSystem.disableBlend();
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
    public static void paintSolidRects(final PoseStack matrix, final Colour colour, final double zLevel, final int... vertices) {

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();

        GlStateManager._enableBlend();
        GlStateManager._disableTexture();
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        ModRenderHelper.glSetColour(colour);

        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 4) {

            final int x1 = vertices[i];
            final int y1 = vertices[i + 1];
            final int x2 = vertices[i + 2];
            final int y2 = vertices[i + 3];

            builder.vertex(pose, x1, y2, (float)zLevel).endVertex();
            builder.vertex(pose, x2, y2, (float)zLevel).endVertex();
            builder.vertex(pose, x2, y1, (float)zLevel).endVertex();
            builder.vertex(pose, x1, y1, (float)zLevel).endVertex();
        }

        tessellator.end();

        GlStateManager._enableTexture();
        GlStateManager._disableBlend();
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
    public static void paintSolidTriangles(final PoseStack matrix, final Colour colour, final double zLevel, final int... vertices) {

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();

        GlStateManager._enableBlend();
        GlStateManager._disableTexture();
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        ModRenderHelper.glSetColour(colour);

        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 6) {

            final int x1 = vertices[i];
            final int y1 = vertices[i + 1];
            final int x2 = vertices[i + 2];
            final int y2 = vertices[i + 3];
            final int x3 = vertices[i + 4];
            final int y3 = vertices[i + 5];

            builder.vertex(pose, x1, y1, (float)zLevel).endVertex();
            builder.vertex(pose, x2, y2, (float)zLevel).endVertex();
            builder.vertex(pose, x3, y3, (float)zLevel).endVertex();
        }

        tessellator.end();

        GlStateManager._enableTexture();
        GlStateManager._disableBlend();
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
    public static void paintHorizontalGradientLine(final PoseStack matrix, final int x, final int y, final int length, final double zLevel,
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
    public static void paintVerticalGradientLine(final PoseStack matrix, final int x, final int y, final int length, final double zLevel,
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
    public static void paintVerticalGradientRect(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                                 final Colour startColour, final Colour endColour) {

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
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
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, x2, y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x1, y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x1, y2, (float)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        builder.vertex(pose, x2, y2, (float)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
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
    public static void paintHorizontalGradientRect(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                           final Colour startColour, final Colour endColour) {

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
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
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, x1, y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x1, y2, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x2, y2, (float)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        builder.vertex(pose, x2, y1, (float)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
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
    public static void paint3DGradientRect(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                           final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, x2, y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x1, y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x1, y2, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, x2, y2, (float)zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a triangle filled with a 3D gradient from a light colour to a dark colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            position of the first vertex on the X axis
     * @param y1            position of the first vertex on the Y axis
     * @param x2            position of the second vertex on the X axis
     * @param y2            position of the second vertex on the Y axis
     * @param x3            position of the third vertex on the X axis
     * @param y3            position of the third vertex on the Y axis
     * @param zLevel        the position on the Z axis for the rectangle
     * @param lightColour   the light colour to be used for the gradient
     * @param darkColour    the dark colour to be used for the gradient
     */
    public static void paint3DGradientTriangle(final PoseStack matrix, final double x1, final double y1, final double x2, final double y2,
                                               final double x3, final double y3, final double zLevel,
                                               final Colour lightColour, final Colour darkColour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, (float)x2, (float)y2, (float)zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();
        builder.vertex(pose, (float)x1, (float)y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, (float)x3, (float)y3, (float)zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a triangle filled with a 3D gradient.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            position of the first vertex on the X axis
     * @param y1            position of the first vertex on the Y axis
     * @param x2            position of the second vertex on the X axis
     * @param y2            position of the second vertex on the Y axis
     * @param x3            position of the third vertex on the X axis
     * @param y3            position of the third vertex on the Y axis
     * @param zLevel        the position on the Z axis for the triangle
     * @param colour1       the colour for the first vertex
     * @param colour2       the colour for the second vertex
     * @param colour3       the colour for the third vertex
     */
    public static void paint3DGradientTriangle(final PoseStack matrix, final double x1, final double y1, final double x2, final double y2,
                                               final double x3, final double y3, final double zLevel,
                                               final Colour colour1, final Colour colour2, final Colour colour3) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, (float)x2, (float)y2, (float)zLevel).color(colour2.glRed(), colour2.glGreen(), colour2.glBlue(), colour2.glAlpha()).endVertex();
        builder.vertex(pose, (float)x1, (float)y1, (float)zLevel).color(colour1.glRed(), colour1.glGreen(), colour1.glBlue(), colour1.glAlpha()).endVertex();
        builder.vertex(pose, (float)x3, (float)y3, (float)zLevel).color(colour3.glRed(), colour3.glGreen(), colour3.glBlue(), colour3.glAlpha()).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /**
     * Paint a triangle filled with a solid colour.
     * <p>
     * The x,y coordinates are relative to the screen upper/left corner
     *
     * @param x1            starting point on the X axis
     * @param y1            starting point on the Y axis
     * @param x2            ending point on the X axis (not included in the rectangle)
     * @param y2            ending point on the Y axis (not included in the rectangle)
     * @param zLevel        the position on the Z axis for the rectangle
     * @param colour        the colour to be used to fill the triangle
     */
    public static void paint3DSolidTriangle(final PoseStack matrix, final double x1, final double y1, final double x2, final double y2,
                                            final double x3, final double y3, final double zLevel, final Colour colour) {

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float startAlpha = colour.glAlpha();
        final float startRed = colour.glRed();
        final float startGreen = colour.glGreen();
        final float startBlue = colour.glBlue();
        final float endAlpha = colour.glAlpha();
        final float endRed = colour.glRed();
        final float endGreen = colour.glGreen();
        final float endBlue = colour.glBlue();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(pose, (float)x1, (float)y1, (float)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        builder.vertex(pose, (float)x3, (float)y3, (float)zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();
        builder.vertex(pose, (float)x2, (float)y2, (float)zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();

        tessellator.end();

        RenderSystem.disableBlend();
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
    public static void paintTexturedRect(final PoseStack matrix, final int x, final int y, final double zLevel, final int width, final int height,
                                         final int minU, final int minV) {

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder builder = tessellator.getBuilder();
        final Matrix4f pose = matrix.last().pose();
        final float textureScale = 1.0f / (16 * 16);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        builder.vertex(pose, x        , y + height, (float)zLevel).uv(textureScale * minU          , textureScale * (minV + height)).endVertex();
        builder.vertex(pose, x + width, y + height, (float)zLevel).uv(textureScale * (minU + width), textureScale * (minV + height)).endVertex();
        builder.vertex(pose, x + width, y         , (float)zLevel).uv(textureScale * (minU + width), textureScale * minV).endVertex();
        builder.vertex(pose, x        , y         , (float)zLevel).uv(textureScale * minU          , textureScale * minV).endVertex();

        tessellator.end();
    }

    public static void paint3DSunkenBox(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
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

    public static void paint3DSunkenBox(final PoseStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                        final Colour gradientLightColour, final Colour borderLightColour, final Colour borderDarkColour) {

        ModRenderHelper.paintSolidRect(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, (int)zLevel, gradientLightColour);

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
        RenderSystem.setShaderColor(colour.glRed(), colour.glGreen(), colour.glBlue(), colour.glAlpha());
    }

    public static void glSetViewport(final int x, final int y, final int width, final int height) {
        RenderSystem.viewport(x, y, width, height);
    }

    public static void glSetViewport(final double x, final double y, final double width, final double height) {
        RenderSystem.viewport(Mth.floor(x), Mth.floor(y), Mth.floor(width), Mth.floor(height));
    }

    public static void glSetDefaultViewport() {
        RenderSystem.viewport(0, 0, Minecraft.getInstance().getWindow().getWidth(),
                Minecraft.getInstance().getWindow().getHeight());
    }

    public static Matrix4f glPerspectiveMatrix(final float fov, final float aspect, final float zNear, final float zFar) {
        return Matrix4f.perspective(fov, aspect, zNear, zFar);
    }

    //region internals

    private ModRenderHelper(){
    }

    //endregion
}

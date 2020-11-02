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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class ModRenderHelper {

    public static final float ONE_PIXEL = 1.0f / 16.0f;

    public static final NonNullSupplier<FontRenderer> DEFAULT_FONT_RENDERER = () -> Minecraft.getInstance().fontRenderer;

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
     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
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
     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
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
     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @return the height of the painted sprite
     */
    public static void paintVerticalProgressBarSprite(final MatrixStack matrix, final ISprite sprite, final Point screenXY,
                                                      final int zLevel, final Rectangle area, final double progress) {
        paintVerticalProgressBarSprite(matrix, sprite, screenXY.X, screenXY.Y, zLevel,
                area.Width, area.Height, progress);
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
                                                      final int areaWidth, final int areaHeight, final double progress) {

        if (progress < 0.01) {
            return;
        }

        final int filledHeight = (int)(areaHeight * progress);
        final int y1 = y + (areaHeight - filledHeight);
        final int y2 = y + areaHeight;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bindTexture(sprite);
        blitSprite(matrix, x, x + areaWidth, y1, y2, zLevel, sprite.getWidth(), filledHeight,
                sprite.getU(), sprite.getV() + (sprite.getHeight() - filledHeight),
                sprite.getTextureMap().getWidth(), sprite.getTextureMap().getHeight());

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

//    //TODO fix before release
//    public static boolean renderObject(final MatrixStack matrix, Minecraft mc, int x, int y, Object o, boolean highlight) {
////        if (itm instanceof Entity) {
////            renderEntity((Entity) itm, x, y);
////            return true;
////        }
//        return renderObject(matrix, mc, Minecraft.getInstance().getItemRenderer(), x, y, o, highlight, 100);
//    }
//
//    public static boolean renderObject(final MatrixStack matrix, Minecraft mc, ItemRenderer itemRender,
//                                       int x, int y, Object o, boolean highlight, float lvl) {
//        itemRender.zLevel = lvl;
//
//        if (o == null) {
//            return renderItemStack(matrix, ItemStack.EMPTY, x, y, "", highlight);
//        }
//        if (o instanceof Item) {
//            return renderItemStack(matrix, new ItemStack((Item)o, 1), x, y, "", highlight);
//        }
//        if (o instanceof Block) {
//            return renderItemStack(matrix, new ItemStack((Block)o, 1), x, y, "", highlight);
//        }
//        if (o instanceof ItemStack) {
//            return renderItemStackWithCount(matrix, (ItemStack)o, x, y, highlight);
//        }
//        /*
//        if (itm instanceof FluidStack) {
//            return renderFluidStack(mc, (FluidStack) itm, x, y, highlight);
//        }
//        if (itm instanceof TextureAtlasSprite) {
//            return renderIcon(mc, itemRender, (TextureAtlasSprite) itm, x, y, highlight);
//        }
//        */
//        return renderItemStack(matrix, ItemStack.EMPTY, x, y, "", highlight);
//    }


    private static final Colour ITEMSTACK_HIGHLIGHT = Colour.fromARGB(0x80ffffff);

    public static boolean renderItemStack(final MatrixStack matrix, /*final IRenderTypeBuffer buffer,*/
                                          final ItemStack stack, int x, int y, final String text, boolean highlight) {

        boolean rc = false;

        if (highlight) {

//            RenderSystem.disableLighting();
            paintVerticalGradientRect(matrix, x, y, x + 16, y + 16, 0, ITEMSTACK_HIGHLIGHT, Colour.WHITE);
        }

        if (!stack.isEmpty()) {

            stack.getItem();
            rc = true;

            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableLighting();
            RenderHelper.enableStandardItemLighting();

            matrix.push();
            RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, (float) 240, (float) 240);

            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
            renderItemOverlayIntoGUI(matrix, Minecraft.getInstance().fontRenderer, stack, x, y, text, text.length() - 2);

            matrix.pop();

            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            RenderSystem.disableLighting();
            RenderSystem.disableRescaleNormal();
        }

        return rc;
    }

    public static boolean renderItemStackWithCount(final MatrixStack matrix, ItemStack stack, int x, int y, boolean highlight) {
        return renderItemStack(matrix, stack, x, y, CodeHelper.formatAsHumanReadableNumber(stack.getCount(), ""), highlight);
    }

    private static void renderItemOverlayIntoGUI(final MatrixStack matrix, /*final IRenderTypeBuffer buffer,*/
                                                 final FontRenderer fr, final ItemStack stack, int xPosition, int yPosition,
                                                 @Nullable String text, int scaled) {

        if (!stack.isEmpty()) {

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            if (stack.getCount() != 1 || text != null) {

                String s = text == null ? String.valueOf(stack.getCount()) : text;

                matrix.translate(0.0D, 0.0D, (itemRenderer.zLevel + 200.0F));

                IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

                if (scaled >= 2) {

                    matrix.push();
                    matrix.scale(0.5f, 0.5f, 0.5f);
                    fr.drawStringWithShadow(matrix, s, ((xPosition + 19 - 2) * 2 - 1 - fr.getStringWidth(s)), yPosition * 2 + 24, 16777215);
                    matrix.pop();

                } else if (scaled == 1) {

                    matrix.push();
                    matrix.scale(0.75f, 0.75f, 0.75f);
                    fr.drawStringWithShadow(matrix, s, ((xPosition - 2) * 1.34f + 24 - fr.getStringWidth(s)), yPosition * 1.34f + 14, 16777215);
                    matrix.pop();

                } else {
                    fr.drawStringWithShadow(matrix, s, (xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
                }

//                buffer.finish();
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = stack.getItem().getRGBDurabilityForDisplay(stack);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getInstance().getRenderPartialTicks());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
    }
    /**
     * Draw with the WorldRenderer
     */
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((x + 0), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + 0), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }


//
//    /**
//     * Paint a series of lines in a solid colour.
//     * <p>
//     * The vertices parameter is interpreted as a series of 2 vertex per line (x, y).
//     * Each vertex is relative to the screen upper/left corner. The lines don't need to be connected to each others
//     * <p>
//     * If the wrong number of vertices are passed in (not multiple of 2) an ArrayIndexOutOfBoundsException will be raised
//     *
//     * @param colour    the colour to be used to fill the rectangle
//     * @param thickness the thickness of the lines
//     * @param zLevel    the position on the Z axis for all the lines
//     * @param vertices  the vertices of the lines
//     *
//     */
//    public static void paintSolidLines(final MatrixStack matrix, final Colour colour, final int thickness, final double zLevel, final int... vertices) {
//
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture();
//        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
//                GlStateManager.DestFactor.ZERO.param);
//        GlStateManager.lineWidth(thickness);
//
//        final int verticesCount = vertices.length;
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
//
//        for (int i = 0; i < verticesCount; i += 2) {
//            builder.pos(vertices[i], vertices[i + 1], zLevel).color(colour.R, colour.G, colour.B, colour.A).endVertex();
//        }
//
//        tessellator.draw();
//
//        GlStateManager.enableTexture();
//        GlStateManager.disableBlend();
//    }

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

//    /**
//     * Paint a solid color rectangle with the specified coordinates and colour.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param rect      starting coordinates and size of the rectangle
//     * @param zLevel    the position on the Z axis for all the lines
//     * @param colour    the colour to be used to fill the rectangle
//     */
//    public static void paintSolidRect(final MatrixStack matrix, final Rectangle rect, final double zLevel, final Colour colour) {
//        paintSolidRect(matrix, rect.Origin.X, rect.Origin.Y, rect.Origin.X + rect.Width, rect.Origin.Y + rect.Height, zLevel, colour);
//    }

//    /**
//     * Paint a solid color rectangle with the specified coordinates and colour.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x1        starting point on the X axis
//     * @param y1        starting point on the Y axis
//     * @param x2        ending point on the X axis (not included in the rectangle)
//     * @param y2        ending point on the Y axis (not included in the rectangle)
//     * @param zLevel    the position on the Z axis for all the lines
//     * @param colour    the colour to be used to fill the rectangle
//     */
//    public static void paintSolidRect(final int x1, final int y1, final int x2, final int y2,
//                                      final double zLevel, final Colour colour) {
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture();
//        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
//                GlStateManager.DestFactor.ZERO.param);
//        ModRenderHelper.glSetColour(colour);
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//
//        builder.pos(x1, y2, zLevel).endVertex();
//        builder.pos(x2, y2, zLevel).endVertex();
//        builder.pos(x2, y1, zLevel).endVertex();
//        builder.pos(x1, y1, zLevel).endVertex();
//
//        tessellator.draw();
//
//
//        GlStateManager.enableTexture();
//        GlStateManager.disableBlend();
//    }

//    public static void paintSolidRect(MatrixStack matrix, final int x1, final int y1, final int x2, final int y2,
//                                      final double zLevel, final Colour colour) {
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture();
//        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
//                GlStateManager.DestFactor.ZERO.param);
//        ModRenderHelper.glSetColour(colour);
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//
//        Matrix4f m = matrix.getLast().getMatrix();
//
//        builder.pos(m, x1, y2, (float)zLevel).endVertex();
//        builder.pos(m, x2, y2, (float)zLevel).endVertex();
//        builder.pos(m, x2, y1, (float)zLevel).endVertex();
//        builder.pos(m, x1, y1, (float)zLevel).endVertex();
//
//        tessellator.draw();
//
//        GlStateManager.enableTexture();
//        GlStateManager.disableBlend();
//    }

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



//    /**
//     * Paint a 1 pixel wide horizontal line in the provided colour.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     * <p>
//     *
//     * @param x         starting point on the X axis
//     * @param y         starting point on the Y axis
//     * @param length    the length of the line
//     * @param zLevel    the position on the Z axis for the line
//     * @param colour    the colour to be used to paint the line
//     */
//    public static void paintHorizontalLine(final MatrixStack matrix, final int x, final int y, final int length, final double zLevel, final Colour colour) {
//        ModRenderHelper.paintSolidRect(matrix, x, y, x + length, y + 1, zLevel, colour);
//    }

//    /**
//     * Paint a 1 pixel wide vertical line in the provided colour.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     * <p>
//     *
//     * @param x         starting point on the X axis
//     * @param y         starting point on the Y axis
//     * @param length    the length of the line
//     * @param zLevel    the position on the Z axis for the line
//     * @param colour    the colour to be used to paint the line
//     */
//    public static void paintVerticalLine(final MatrixStack matrix, final int x, final int y, final int length, final double zLevel, final Colour colour) {
//        ModRenderHelper.paintSolidRect(matrix, x, y, x + 1, y + length, zLevel, colour);
//    }

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

//    /**
//     * Paint a triangle filled with a 3D gradient from a light colour to a dark colour.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param lightColour   the light colour to be used for the gradient
//     * @param darkColour    the dark colour to be used for the gradient
//     */
//    public static void paint3DGradientTriangle(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2,
//                                               final int x3, final int y3, final double zLevel,
//                                               final Colour lightColour, final Colour darkColour) {
//
//        RenderSystem.disableTexture();
//        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
//        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param,
//                GlStateManager.DestFactor.ZERO.param);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//        final float startAlpha = lightColour.glAlpha();
//        final float startRed = lightColour.glRed();
//        final float startGreen = lightColour.glGreen();
//        final float startBlue = lightColour.glBlue();
//        final float endAlpha = darkColour.glAlpha();
//        final float endRed = darkColour.glRed();
//        final float endGreen = darkColour.glGreen();
//        final float endBlue = darkColour.glBlue();
//
//        builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
//
//        builder.pos(x1, y1, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
//        builder.pos(x2, y2, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();
//        builder.pos(x3, y3, zLevel).color(endRed  , endGreen  , endBlue  , endAlpha).endVertex();
//
//        tessellator.draw();
//
//        RenderSystem.shadeModel(GL11.GL_FLAT);
//        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.enableTexture();
//    }

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

//    /**
//     * Paint a textured rectangle with the specified coordinates and the texture currently bound to the TextureManager and using the
//     * provided sprite for the texture coordinates
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param width         the width of the rectangle
//     * @param height        the height of the rectangle
//     * @param textureSprite the sprite associated with the texture
//     */
//    public static void paintTexturedRect(final MatrixStack matrix, final int x, final int y, final double zLevel, final int width, final int height,
//                                         final TextureAtlasSprite textureSprite) {
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//
//        builder.pos(x        , y + height, zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
//        builder.pos(x + width, y + height, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
//        builder.pos(x + width, y         , zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
//        builder.pos(x        , y         , zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
//
//        tessellator.draw();
//    }

//    /**
//     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param sprite        the sprite to paint
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param zLevel        the position on the Z axis for the sprite
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     */
//    public static void paintSprite(final ISprite sprite, final int x, final int y, final double zLevel,
//                                   final boolean blendSprite, final boolean bufferOnly) {
//        ModRenderHelper.paintSprite(sprite, Colour.WHITE, x, y, zLevel, sprite.getWidth(), sprite.getHeight(), blendSprite, bufferOnly);
//    }
//
//    /**
//     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param sprite        the sprite to paint
//     * @param colour        the colour tint
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param zLevel        the position on the Z axis for the sprite
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     */
//    public static void paintSprite(final ISprite sprite, final Colour colour, final int x, final int y, final double zLevel,
//                                   final boolean blendSprite, final boolean bufferOnly) {
//        ModRenderHelper.paintSprite(sprite, colour, x, y, zLevel, sprite.getWidth(), sprite.getHeight(), blendSprite, bufferOnly);
//    }
//
//    /**
//     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param sprite        the sprite to paint
//     * @param colour        the colour tint
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param zLevel        the position on the Z axis for the sprite
//     * @param width         the width of the sprite
//     * @param height        the height of the sprite
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     */
//    public static void paintSprite(final ISprite sprite, final Colour colour, final int x, final int y, final double zLevel,
//                                   final int width, final int height, final boolean blendSprite, final boolean bufferOnly) {
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        if (!bufferOnly) {
//            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
//        }
//
//        builder.pos(x,         y + height, zLevel).color(colour.R, colour.G, colour.B, colour.A).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
//        builder.pos(x + width, y + height, zLevel).color(colour.R, colour.G, colour.B, colour.A).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
//        builder.pos(x + width, y,          zLevel).color(colour.R, colour.G, colour.B, colour.A).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
//        builder.pos(x,         y,          zLevel).color(colour.R, colour.G, colour.B, colour.A).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
//
//        if (!bufferOnly) {
//
//            RenderHelper.disableStandardItemLighting();
//            RenderSystem.disableLighting();
//
//            if (blendSprite) {
//                RenderSystem.enableBlend();
//                RenderSystem.defaultBlendFunc();
//                RenderSystem.color4f(1,1,1,1);
//            }
//
//            ModRenderHelper.bindTexture(sprite);
//            tessellator.draw();
//
//            if (blendSprite) {
//                RenderSystem.disableBlend();
//            }
//
//            RenderSystem.enableLighting();
//            RenderHelper.enableStandardItemLighting();
//        }
//
//        sprite.getSpriteOverlay().ifPresent(o -> ModRenderHelper.paintSprite(o, colour, x, y, zLevel, width, height, true, false));
//    }

//    /**
//     * Paint a rectangle filled with an ISprite up to the indicated progress percentage.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the width or height of the painted rect
//     */
//    public static int paintStretchedProgressSprite(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
//                                                   final double progress, final ISprite sprite, final boolean blendSprite,
//                                                   final boolean bufferOnly) {
//        return paintStretchedProgressSprite(matrix, x1, y1, x2, y2, zLevel, progress, sprite, Colour.WHITE, blendSprite, bufferOnly);
//    }

//    /**
//     * Paint a rectangle filled with an ISprite stretched up to fill the indicated progress percentage.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param colour        the sprite tint
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the width or height of the painted rect
//     */
//    public static int paintStretchedProgressSprite(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
//                                                   final double progress, final ISprite sprite, final Colour colour,
//                                                   final boolean blendSprite, final boolean bufferOnly) {
//
//        if (progress <= 0 || progress > 1) {
//            return 0;
//        }
//
//        int width = x2 - x1;
//        int height = y2 - y1;
//        final int y, result;
//
//        if (width > height) {
//
//            width *= progress;
//            y = y1;
//            result = width;
//
//        } else {
//
//            height *= progress;
//            y = y2 - height;
//            result = height;
//        }
//
////        ModRenderHelper.paintSprite(sprite, colour, x1, y, zLevel, width, height, blendSprite, bufferOnly);
//        ModRenderHelper.paintSprite(matrix, sprite, new Point(x1, y), (int)zLevel, width, height);
//
//        return result;
//    }

//    /**
//     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param width         the width of the area to paint on
//     * @param height        the height of the area to paint on
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the height of the painted rect
//     */
//    public static int paintVerticalProgressSprite(final MatrixStack matrix, final int x, final int y, final double zLevel, final int width,
//                                                  final int height, final double progress, final ISprite sprite,
//                                                  final boolean blendSprite, final boolean bufferOnly) {
//        return paintVerticalProgressSprite(matrix, x, y, zLevel, width, height, progress, sprite, Colour.WHITE, blendSprite, bufferOnly);
//    }

//    /**
//     * Paint a vertical rectangle filled, from the bottom up, with an ISprite up to the indicated progress percentage.
//     * <p>
//     * The x,y coordinates are relative to the screen upper/left corner
//     *
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param width         the width of the area to paint on
//     * @param height        the height of the area to paint on
//     * @param zLevel        the position on the Z axis for the rectangle
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param tint          the sprite tint
//     * @param blendSprite   if true, blend the sprite with the current background
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the height of the painted rect
//     */
//    public static int paintVerticalProgressSprite(final MatrixStack matrix, final int x, final int y, final double zLevel, final int width,
//                                                  final int height, final double progress, final ISprite sprite,
//                                                  final Colour tint, final boolean blendSprite, final boolean bufferOnly) {
//
//        final int paintedHeight = (int)Math.max(0, height * progress);
//        final int y1 = y + height - paintedHeight;
//        final int y2 = y + height;
//        final float maxV = sprite.getMaxV();
//        final float minV = sprite.getInterpolatedV(16.0 * (1 - progress));
//        final Tessellator tessellator = Tessellator.getInstance();
//        final BufferBuilder builder = tessellator.getBuffer();
//
//        if (!bufferOnly) {
//            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
//        }
//
//        builder.pos(x        , y2, zLevel).color(tint.R, tint.G, tint.B, tint.A).tex(sprite.getMinU(), maxV).endVertex();
//        builder.pos(x + width, y2, zLevel).color(tint.R, tint.G, tint.B, tint.A).tex(sprite.getMaxU(), maxV).endVertex();
//        builder.pos(x + width, y1, zLevel).color(tint.R, tint.G, tint.B, tint.A).tex(sprite.getMaxU(), minV).endVertex();
//        builder.pos(x        , y1, zLevel).color(tint.R, tint.G, tint.B, tint.A).tex(sprite.getMinU(), minV).endVertex();
//
//        if (!bufferOnly) {
//
//            RenderHelper.disableStandardItemLighting();
//            RenderSystem.disableLighting();
//
//            if (blendSprite) {
//                RenderSystem.enableBlend();
//                RenderSystem.defaultBlendFunc();
//            }
//
//            ModRenderHelper.bindTexture(sprite);
//            tessellator.draw();
//
//            if (blendSprite) {
//                RenderSystem.disableBlend();
//            }
//
//            RenderSystem.enableLighting();
//            RenderHelper.enableStandardItemLighting();
//        }
//
////        sprite.getSpriteOverlay().ifPresent(o -> ModRenderHelper.paintVerticalProgressSprite(matrix, x, y, zLevel, width, height, progress, o, tint, blendSprite, false));
//        sprite.applyOverlay(o -> ModRenderHelper.paintVerticalProgressSprite(matrix, x, y, zLevel, width, height, progress, o, tint, blendSprite, false));
//
//        return paintedHeight;
//    }















//    public static void paint3DButton(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
//                                     final Colour darkOutlineColour, final Colour gradientLightColour,
//                                     final Colour gradientDarkColour, final Colour borderLightColour,
//                                     final Colour borderDarkColour) {
//
//        ModRenderHelper.glSetColour(Colour.WHITE);
//
//        ModRenderHelper.paintHollowRect(matrix, x1, y1, x2, y2, (int)zLevel, darkOutlineColour);
//
//        ModRenderHelper.paint3DGradientRect(matrix, x1 + 2, y1 + 2, x2 - 2, y2 - 2, zLevel, gradientLightColour, gradientDarkColour);
//
//        ModRenderHelper.paintSolidRects(matrix, borderLightColour, zLevel,
//                x1 + 1, y1 + 1, x2 - 1, y1 + 2,
//                x1 + 1, y1 + 1, x1 + 2, y2 - 1);
//
//        ModRenderHelper.paintSolidRects(matrix, borderDarkColour, zLevel,
//                x1 + 1, y2 - 2, x2 - 1, y2 - 1,
//                x2 - 2, y1 + 1, x2 - 1, y2 - 1);
//
//        ModRenderHelper.glSetColour(Colour.WHITE);
//    }

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

/*
    public static Matrix4f glFrustumMatrix(final double left, final double right, final double bottom, final double top,
                                           final double zNear, final double zFar) {
        return glFrustumMatrix((float)left, (float)right, (float)bottom, (float)top, (float)zNear, (float)zFar);
    }

    public static Matrix4f glFrustumMatrix(final float left, final float right, final float bottom, final float top,
                                           final float zNear, final float zFar) {

        final Matrix4f matrix = new Matrix4f();

        matrix.set(0, 0, 2.0f * zNear / (right - left));
        matrix.set(2, 0, (right + left) / (right - left));

        matrix.set(1, 1, 2.0f * zNear / (top - bottom));
        matrix.set(1, 2, (top + bottom) / (top - bottom));

        matrix.set(2, 2, -((zFar + zNear) / (zFar - zNear)));
        matrix.set(2, 3, -((2 * zFar * zNear) / (zFar - zNear)));

        matrix.set(3, 2, -1);

        return matrix;
    }
*/

    public static void glPerspective(final float fov, final float aspect, final float zNear, final float zFar) {
        RenderSystem.multMatrix(glPerspectiveMatrix(fov, aspect, zNear, zFar));
    }
/*
    public static void glFrustum(final float left, final float right, final float bottom, final float top,
                                 final float zNear, final float zFar) {
        GlStateManager.multMatrix(glFrustumMatrix(left, right, bottom, top, zNear, zFar));
    }
*/
    //endregion
/*
    @Deprecated
    public static void renderFluidCube(final Fluid fluid, final BlockFacings facesToDraw,
                                       final double offsetX, final double offsetY, final double offsetZ,
                                       final double x1, final double y1, final double z1,
                                       final double x2, final double y2, final double z2,
                                       final int color, final int brightness) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        bindBlocksTexture();

        final TextureAtlasSprite still = getFluidStillSprite(fluid);
        final TextureAtlasSprite flowing = getFluidFlowingSprite(fluid);

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translated(offsetX, offsetY, offsetZ);

        final Direction[] H = { Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
        final Direction[] V = { Direction.DOWN, Direction.UP};

        final TexturedQuadData quadData = new TexturedQuadData(buffer);
        final double width = x2 - x1;
        final double height = y2 - y1;
        final double depth = z2 - z1;
        Direction face;

        quadData.setColor(color);
        quadData.setBrightness(brightness);
        quadData.setCoordinates(x1, y1, z1, width, height, depth, true);

        for (int i = 0; i < 4; ++i) {

            face = H[i];

            if (facesToDraw.isSet(face)) {

                quadData.setFace(face, flowing, true);
                ModRenderHelper.createTexturedQuad2(quadData);
            }
        }

        quadData.setCoordinates(x1, y1, z1, width, height, depth, false);

        for (int i = 0; i < 2; ++i) {

            face = V[i];

            if (facesToDraw.isSet(face)) {

                quadData.setFace(face, still, false);
                ModRenderHelper.createTexturedQuad2(quadData);
            }
        }

        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Deprecated
    public static class TexturedQuadData {

        public final BufferBuilder vertexes;
        public int alpha;
        public int red;
        public int green;
        public int blue;

        public TexturedQuadData(final BufferBuilder buffer) {

            this.vertexes = buffer;
            this.alpha = this.red = this.green = this.blue = 0xFF;
        }

        public void setBrightness(final int brightness) {

            this.light1 = brightness >> 0x10 & 0xFFFF;
            this.light2 = brightness & 0xFFFF;
        }

        public void setColor(final int color) {

            this.alpha = color >> 24 & 0xFF;
            this.red   = color >> 16 & 0xFF;
            this.green = color >> 8 & 0xFF;
            this.blue  = color & 0xFF;
        }

        public void setCoordinates(final double x, final double y, final double z,
                                   final double width, final double height, final double depth,
                                   final boolean renderAsFlowingFluid) {

            this.x1 = x;
            this.x2 = x + width;
            this.y1 = y;
            this.y2 = y + height;
            this.z1 = z;
            this.z2 = z + depth;

            this.xText1 = this.x1 % 1.0;
            this.xText2 = this.xText1 + width;
            this.yText1 = this.y1 % 1.0;
            this.yText2 = this.yText1 + height;
            this.zText1 = this.z1 % 1.0;
            this.zText2 = this.zText1 + depth;

            while (this.xText2 > 1.0)
                this.xText2 -= 1.0;

            while (this.yText2 > 1.0)
                this.yText2 -= 1.0;

            while (this.zText2 > 1.0)
                this.zText2 -= 1.0;

            // render a flowing texture from the bottom-up
            if (renderAsFlowingFluid) {

                double swap = 1.0 - this.yText1;

                this.yText1 = 1.0 - this.yText2;
                this.yText2 = swap;
            }
        }

        //Set the face to be rendered. Call this method for last
        public void setFace(final Direction face, final TextureAtlasSprite sprite, final boolean renderAsFlowingFluid) {

            final double textSize = renderAsFlowingFluid ? 16.0 : 8.0;

            switch (face) {

                case DOWN:
                case UP:
                    this.minU = sprite.getInterpolatedU(this.xText1 * textSize);
                    this.maxU = sprite.getInterpolatedU(this.xText2 * textSize);
                    this.minV = sprite.getInterpolatedV(this.zText1 * textSize);
                    this.maxV = sprite.getInterpolatedV(this.zText2 * textSize);
                    break;

                case NORTH:
                case SOUTH:
                    this.minU = sprite.getInterpolatedU(this.xText2 * textSize);
                    this.maxU = sprite.getInterpolatedU(this.xText1 * textSize);
                    this.minV = sprite.getInterpolatedV(this.yText1 * textSize);
                    this.maxV = sprite.getInterpolatedV(this.yText2 * textSize);
                    break;

                case WEST:
                case EAST:
                    this.minU = sprite.getInterpolatedU(this.zText2 * textSize);
                    this.maxU = sprite.getInterpolatedU(this.zText1 * textSize);
                    this.minV = sprite.getInterpolatedV(this.yText1 * textSize);
                    this.maxV = sprite.getInterpolatedV(this.yText2 * textSize);
                    break;

                default:
                    this.minU = sprite.getMinU();
                    this.maxU = sprite.getMaxU();
                    this.minV = sprite.getMinV();
                    this.maxV = sprite.getMaxV();
            }

            this.face = face;
        }

        protected double x1, y1, z1, x2, y2, z2;
        protected double xText1, yText1, zText1, xText2, yText2, zText2;
        protected double minU, maxU, minV, maxV;
        protected int light1, light2;
        protected Direction face;
    }

    @Deprecated
    public static void createTexturedQuad2(final TexturedQuadData data) {

        final BufferBuilder vertexes = data.vertexes;

        if (null == vertexes)
            return;

        final int alpha = data.alpha;
        final int red = data.red;
        final int green = data.green;
        final int blue = data.blue;
        final int light1 = data.light1;
        final int light2 = data.light2;

        switch (data.face) {

            case DOWN:
                vertexes.pos(data.x1, data.y1, data.z1).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y1, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y1, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y1, data.z2).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                break;

            case UP:
                vertexes.pos(data.x1, data.y2, data.z1).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y2, data.z2).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                break;

            case NORTH:
                vertexes.pos(data.x1, data.y1, data.z1).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y2, data.z1).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y1, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                break;

            case SOUTH:
                vertexes.pos(data.x1, data.y1, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y1, data.z2).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z2).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y2, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                break;

            case WEST:
                vertexes.pos(data.x1, data.y1, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y1, data.z2).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y2, data.z2).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x1, data.y2, data.z1).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                break;

            case EAST:
                vertexes.pos(data.x2, data.y1, data.z1).color(red, green, blue, alpha).tex(data.minU, data.maxV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z1).color(red, green, blue, alpha).tex(data.minU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y2, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.minV).lightmap(light1, light2).endVertex();
                vertexes.pos(data.x2, data.y1, data.z2).color(red, green, blue, alpha).tex(data.maxU, data.maxV).lightmap(light1, light2).endVertex();
                break;
        }
    }
*/
    //region internals

    private ModRenderHelper(){
    }

    //endregion
}

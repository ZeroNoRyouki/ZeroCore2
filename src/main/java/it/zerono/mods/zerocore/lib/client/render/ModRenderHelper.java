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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.internal.client.RenderTypes;
import it.zerono.mods.zerocore.internal.client.model.MissingModel;
import it.zerono.mods.zerocore.internal.mixin.client.GuiGraphicsAccessor;
import it.zerono.mods.zerocore.internal.mixin.client.ModelBakeryAccessor;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public final class ModRenderHelper {

    public static final float ONE_PIXEL = 1.0f / 16.0f;

    public static final int GUI_TOPMOST_Z = 900;
    public static final int GUI_ITEM_Z = 600;

    public static final Supplier<@NotNull Font> DEFAULT_FONT_RENDERER = () -> Minecraft.getInstance().font;

    public static long getLastRenderTime() {
        return ZeroCore.getProxy().getLastRenderTime();
    }

    public static ModelManager getModelManager() {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager();
    }

    public static UnbakedModel getModel(final ResourceLocation location) {
        return ((ModelBakeryAccessor) (Minecraft.getInstance().getModelManager().getModelBakery()))
                .zerocore_getModel(location);
    }

    public static BakedModel getModel(final BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state);
    }

    public static BakedModel getModel(final ModelResourceLocation modelLocation) {
        return getModelManager().getModel(modelLocation);
    }

    public static BakedModel getMissingModel() {
        return MissingModel.INSTANCE;
    }

    @Nullable
    public static BakedModel getMissingModel(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        return modelRegistry.get(ModelBakery.MISSING_MODEL_VARIANT);
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
        return ModRenderHelper.getTextureSprite(Objects.requireNonNull(IClientFluidTypeExtensions.of(fluid).getStillTexture()));
    }

    public static TextureAtlasSprite getFluidStillSprite(final FluidStack stack) {
        return ModRenderHelper.getTextureSprite(Objects.requireNonNull(IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack)));
    }

    public static TextureAtlasSprite getFluidFlowingSprite(final Fluid fluid) {
        return ModRenderHelper.getTextureSprite(Objects.requireNonNull(IClientFluidTypeExtensions.of(fluid).getFlowingTexture()));
    }

    public static TextureAtlasSprite getFluidFlowingSprite(final FluidStack stack) {
        return ModRenderHelper.getTextureSprite(Objects.requireNonNull(IClientFluidTypeExtensions.of(stack.getFluid()).getFlowingTexture(stack)));
    }

    public static TextureAtlasSprite getMissingTexture(final ResourceLocation atlasName) {
        return getTextureSprite(atlasName, MissingTextureAtlasSprite.getLocation());
    }

    public static TextureAtlasSprite getMissingTexture() {
        return getTextureSprite(MissingTextureAtlasSprite.getLocation());
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final Fluid fluid) {

        final ResourceLocation rl = IClientFluidTypeExtensions.of(fluid).getOverlayTexture();

        return null != rl ? ModRenderHelper.getTextureSprite(rl) : null;
    }

    @Nullable
    public static TextureAtlasSprite getFluidOverlaySprite(final FluidStack stack) {

        final ResourceLocation rl = IClientFluidTypeExtensions.of(stack.getFluid()).getOverlayTexture(stack);

        return null != rl ? ModRenderHelper.getTextureSprite(rl) : null;
    }

    public static int getFluidTint(final Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getTintColor();
    }

    public static int getFluidTint(final FluidStack stack) {
        return IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);
    }
    public static Colour getFluidTintColour(final Fluid fluid) {
        return Colour.fromARGB(getFluidTint(fluid));
    }

    public static Colour getFluidTintColour(final FluidStack stack) {
        return Colour.fromARGB(getFluidTint(stack));
    }

    public static ISprite getStillFluidSprite(final Fluid fluid) {
        return buildSprite(getFluidStillSprite(fluid), null);
    }

    public static ISprite getStillFluidSprite(final FluidStack stack) {
        return buildSprite(getFluidStillSprite(stack), null);
    }

    public static ISprite getStillFluidSpriteWithOverlay(final Fluid fluid) {
        return buildSprite(getFluidStillSprite(fluid), getFluidOverlaySprite(fluid));
    }

    public static ISprite getStillFluidSpriteWithOverlay(final FluidStack stack) {
        return buildSprite(getFluidStillSprite(stack), getFluidOverlaySprite(stack));
    }

    public static ISprite getFlowingFluidSprite(final Fluid fluid) {
        return buildSprite(getFluidFlowingSprite(fluid), null);
    }

    public static ISprite getFlowingFluidSprite(final FluidStack stack) {
        return buildSprite(getFluidFlowingSprite(stack), null);
    }

    public static ISprite getFlowingFluidSpriteWithOverlay(final Fluid fluid) {
        return buildSprite(getFluidFlowingSprite(fluid), getFluidOverlaySprite(fluid));
    }

    public static ISprite getFlowingFluidSpriteWithOverlay(final FluidStack stack) {
        return buildSprite(getFluidFlowingSprite(stack), getFluidOverlaySprite(stack));
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

    public static int addBlockLight(int combinedLight, int blockLight) {
        return (combinedLight & 0xFFFF0000) | Math.max(blockLight << 4, combinedLight & 0xFFFF);
    }

    public static void renderBlockFace(VertexConsumer renderer, Matrix4f matrix, Direction face,
                                       float x1, float y1, float z1, float x2, float y2, float z2,
                                       float minU, float maxU, float minV, float maxV, int color, int brightness) {

        final int alpha = color >> 24 & 0xFF;
        final int red = color >> 16 & 0xFF;
        final int green = color >> 8 & 0xFF;
        final int blue = color & 0xFF;
        final int light1 = brightness & 0xFFFF;
        final int light2 = brightness >> 0x10 & 0xFFFF;

        switch (face) {

            case DOWN: {

                renderer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }

            case UP: {

                renderer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }

            case NORTH: {

                renderer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }

            case SOUTH: {

                renderer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }

            case WEST: {

                renderer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }

            case EAST: {

                renderer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha).setUv(minU, maxV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha).setUv(minU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setUv(maxU, minV).setUv2(light1, light2);
                renderer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha).setUv(maxU, maxV).setUv2(light1, light2);
                break;
            }
        }
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
            builder.putBulkData(entry, quad, 1.0f, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay, true);
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

            float red, green, blue, alpha;

            if (quad.isTinted()) {

                final Colour tint = quadTintGetter.apply(quad.getTintIndex());

                red = tint.R;
                green = tint.G;
                blue = tint.B;
                alpha = tint.A;

            } else {

                red = green = blue = alpha = 1.0f;
            }

            builder.putBulkData(entry, quad, red, green, blue, alpha, combinedLight, combinedOverlay, true);
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
    public static void renderModel(final BakedModel model, final ModelData data, final PoseStack matrix,
                                   final VertexConsumer builder, final int combinedLight, final int combinedOverlay,
                                   @Nullable RenderType renderType) {

        for (final Direction direction : CodeHelper.DIRECTIONS) {
            renderQuads(matrix, builder, model.getQuads(null, direction, CodeHelper.fakeRandom(), data, renderType),
                    combinedLight, combinedOverlay);
        }

        renderQuads(matrix, builder, model.getQuads(null, null, CodeHelper.fakeRandom(), data, renderType),
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
    public static void renderModel(final BakedModel model, final ModelData data, final PoseStack matrix,
                                   final VertexConsumer builder, final int combinedLight, final int combinedOverlay,
                                   final IntFunction<Colour> quadTintGetter, @Nullable RenderType renderType) {

        for (final Direction direction : CodeHelper.DIRECTIONS) {
            renderQuads(matrix, builder, model.getQuads(null, direction, CodeHelper.fakeRandom(), data, renderType),
                    combinedLight, combinedOverlay, quadTintGetter);
        }

        renderQuads(matrix, builder, model.getQuads(null, null, CodeHelper.fakeRandom(), data, renderType),
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
        final PoseStack.Pose normal = matrix.last();
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

            buffer.addVertex(m, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
                    .setColor(red, green, blue, alpha)
                    .setNormal(normal, deltaX, deltaY, deltaZ);
            buffer.addVertex(m, (float) (x2 + originX), (float) (y2 + originY), (float) (z2 + originZ))
                    .setColor(red, green, blue, alpha)
                    .setNormal(normal, deltaX, deltaY, deltaZ);
        });
    }

    //endregion
    //region 2D/GUI paint helpers (with matrix)
    //region sprites

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param gfx the GuiGraphics for the current paint operation
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final GuiGraphics gfx, final ISprite sprite, final Point screenXY, final int zLevel,
                                   final int width, final int height) {
        paintSprite(gfx, sprite, screenXY.X, screenXY.Y, zLevel, width, height);
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * @param gfx the GuiGraphics for the current paint operation
     * @param sprite the sprite to paint
     * @param x painting coordinates relative to the top-left corner of the screen
     * @param y painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the sprite
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    public static void paintSprite(final GuiGraphics gfx, final ISprite sprite, final int x, final int y,
                                   final int zLevel, final int width, final int height) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        final GuiGraphicsAccessor gfxAccessor = (GuiGraphicsAccessor)gfx;

        gfxAccessor.zerocore_invokeInnerBlit(sprite.getTextureMap().getTextureLocation(), x, x + width, y, y + height,
                zLevel, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(gfx, o, x, y, zLevel, width, height));
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given screen coordinates
     *
     * Draw only part of the sprite, by masking off parts of it. For compatibly with JEI IDrawableStatic interface
     *
     * @param gfx the GuiGraphics for the current paint operation
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
    public static void paintSprite(final GuiGraphics gfx, final ISprite sprite, final int xOffset, final int yOffset,
                                   final int zLevel, final Padding padding, final int width, final int height,
                                   final int maskTop, final int maskBottom, final int maskLeft, final int maskRight) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        final int x = xOffset + padding.getLeft() + maskLeft;
        final int y = yOffset + padding.getTop() + maskTop;
        final int u = sprite.getU() + maskLeft;
        final int v = sprite.getV() + maskTop;
        final int paintWidth = width - maskRight - maskLeft;
        final int paintHeight = height - maskBottom - maskTop;
        final float widthRatio = 1.0F / sprite.getTextureMap().getWidth();
        final float heightRatio = 1.0F / sprite.getTextureMap().getHeight();

        final GuiGraphicsAccessor gfxAccessor = (GuiGraphicsAccessor)gfx;

        gfxAccessor.zerocore_invokeInnerBlit(sprite.getTextureMap().getTextureLocation(), x, x + paintWidth, y, y + paintHeight,
                zLevel, u * widthRatio, (u + (float) paintWidth) * widthRatio,
                v * heightRatio, (v + (float) paintHeight) * heightRatio);

        RenderSystem.disableBlend();

        sprite.applyOverlay(o -> paintSprite(gfx, o, xOffset, yOffset, zLevel, padding, width, height, maskTop, maskBottom, maskLeft, maskRight));
    }

    /**
     * Paint a progress bar with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param gfx the GuiGraphics for the current paint operation
     * @param orientation the {@link Orientation} of the progress bar
     * @param sprite the sprite to paint
     * @param screenXY painting coordinates relative to the top-left corner of the screen
     * @param zLevel the position on the Z axis for the rectangle
     * @param area the maximum area to be filled (the origin is ignored)
     * @param progress a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
     * @param tint the colour to tint the sprite with
     */
    public static void paintOrientedProgressBarSprite(final GuiGraphics gfx, final Orientation orientation,
                                                      final ISprite sprite, final Point screenXY, final int zLevel,
                                                      final Rectangle area, final double progress, final Colour tint) {

        switch (orientation) {

            case BottomToTop:
                paintBottomToTopTiledSprite(gfx, sprite, tint, screenXY.X, screenXY.Y + area.Height, zLevel, area.Width, (int)(area.Height * progress));
                break;

            case TopToBottom:
                paintTopToBottomTiledSprite(gfx, sprite, tint, screenXY.X, screenXY.Y, zLevel, area.Width, (int)(area.Height * progress));
                break;

            case LeftToRight:
                paintLeftToRightTiledSprite(gfx, sprite, tint, screenXY.X, screenXY.Y, zLevel, (int)(area.Width * progress), area.Height);
                break;

            case RightToLeft:
                paintRightToLeftTiledSprite(gfx, sprite, tint, screenXY.X + area.Width, screenXY.Y, zLevel, (int)(area.Width * progress), area.Height);
                break;
        }
    }

    /**
     * Paint a progress bar with an ISprite up to the indicated progress percentage.
     * <p>
     * All the coordinates are relative to the screen upper/left corner.
     *
     * @param gfx the GuiGraphics for the current paint operation
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
    public static void paintOrientedProgressBarSprite(final GuiGraphics gfx, final Orientation orientation,
                                                      final ISprite sprite, final int x, final int y, final int zLevel,
                                                      final int areaWidth, final int areaHeight, final double progress,
                                                      final Colour tint) {

        switch (orientation) {

            case BottomToTop:
                paintBottomToTopTiledSprite(gfx, sprite, tint, x, y + areaHeight, zLevel, areaWidth, (int)(areaHeight * progress));
                break;

            case TopToBottom:
                paintTopToBottomTiledSprite(gfx, sprite, tint, x, y, zLevel, areaWidth, (int)(areaHeight * progress));
                break;

            case LeftToRight:
                paintLeftToRightTiledSprite(gfx, sprite, tint, x, y, zLevel, (int)(areaWidth * progress), areaHeight);
                break;

            case RightToLeft:
                paintRightToLeftTiledSprite(gfx, sprite, tint, x + areaWidth, y, zLevel, (int)(areaWidth * progress), areaHeight);
                break;
        }
    }

    public static void paintTopToBottomTiledSprite(final GuiGraphics gfx, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final Matrix4f pose = gfx.pose().last().pose();
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bindTexture(sprite);

        final BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

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

                bufferBuilder.addVertex(pose, tileX1, tileY2, zLevel).setUv(spriteMinU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY2, zLevel).setUv(tileMaxU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY1, zLevel).setUv(tileMaxU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX1, tileY1, zLevel).setUv(spriteMinU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
            }
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void paintBottomToTopTiledSprite(final GuiGraphics gfx, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final Matrix4f pose = gfx.pose().last().pose();
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bindTexture(sprite);

        final BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

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

                bufferBuilder.addVertex(pose, tileX1, tileY2, zLevel).setUv(spriteMinU, spriteMaxV);
                bufferBuilder.addVertex(pose, tileX2, tileY2, zLevel).setUv(tileMaxU, spriteMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY1, zLevel).setUv(tileMaxU, tileMinV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX1, tileY1, zLevel).setUv(spriteMinU, tileMinV).setColor(tint.R, tint.G, tint.B, tint.A);
            }
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void paintLeftToRightTiledSprite(final GuiGraphics gfx, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final Matrix4f pose = gfx.pose().last().pose();
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bindTexture(sprite);

        final BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

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

                bufferBuilder.addVertex(pose, tileX1, tileY2, zLevel).setUv(spriteMinU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY2, zLevel).setUv(tileMaxU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY1, zLevel).setUv(tileMaxU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX1, tileY1, zLevel).setUv(spriteMinU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
            }
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void paintRightToLeftTiledSprite(final GuiGraphics gfx, final ISprite sprite, final Colour tint,
                                                   final int x, final int y, final int zLevel,
                                                   final int paintWidth, final int paintHeight) {

        final Matrix4f pose = gfx.pose().last().pose();
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bindTexture(sprite);

        final BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

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

                bufferBuilder.addVertex(pose, tileX1, tileY2, zLevel).setUv(tileMinU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY2, zLevel).setUv(spriteMaxU, tileMaxV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX2, tileY1, zLevel).setUv(spriteMaxU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
                bufferBuilder.addVertex(pose, tileX1, tileY1, zLevel).setUv(tileMinU, spriteMinV).setColor(tint.R, tint.G, tint.B, tint.A);
            }
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
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
    public static void paintSolidRect(final GuiGraphics gfx, final Point screenXY1, final Point screenXY2,
                                      final int zLevel, final Colour colour) {
        gfx.fill(RenderTypes.gui(), screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, zLevel, colour.toARGB());
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
    public static void paintSolidRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2,
                                      final int zLevel, final Colour colour) {
        gfx.fill(RenderType.gui(), x1, y1, x2, y2, zLevel, colour.toARGB());
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
    public static void paintHollowRect(final GuiGraphics gfx, final Point screenXY, final int width, final int height,
                                       final int zLevel, final Colour colour) {
        paintHollowRect(gfx, screenXY.X, screenXY.Y, width, height, zLevel, colour);
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
    public static void paintHollowRect(final GuiGraphics gfx, final int x1, final int y1,
                                       final int width, final int height, final int zLevel, final Colour colour) {

        paintHorizontalLine(gfx, x1, y1, width, zLevel, colour);
        paintVerticalLine(gfx, x1 + width - 1, y1 + 1, height - 2, zLevel, colour);
        paintHorizontalLine(gfx, x1, y1 + height - 1, width, zLevel, colour);
        paintVerticalLine(gfx, x1, y1 + 1, height - 2, zLevel, colour);
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
    public static void paintTriangularGradientRect(final GuiGraphics gfx, final int x, final int y,
                                                   final int width, final int height, final int zLevel,
                                                   final Colour lightColour, final Colour darkColour) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        final Matrix4f pose = gfx.pose().last().pose();
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

        builder.addVertex(pose, x2,  y, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose,  x,  y, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose,  x, y2, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x2, y2, zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.disableBlend();
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
    public static void paintHorizontalLine(final GuiGraphics gfx, final Point screenXY, final int length,
                                           final int zLevel, final Colour colour) {
        gfx.fill(RenderTypes.gui(), screenXY.X, screenXY.Y, screenXY.X + length + 1, screenXY.Y + 1,
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
    public static void paintHorizontalLine(final GuiGraphics gfx, final int x, final int y, final int length,
                                           final int zLevel, final Colour colour) {
        gfx.fill(RenderTypes.gui(), x, y, x + length, y + 1, zLevel, colour.toARGB());
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

    public static void paintVerticalLine(final GuiGraphics gfx, final Point screenXY, final int length,
                                         final int zLevel, final Colour colour) {
        gfx.fill(RenderTypes.gui(), screenXY.X, screenXY.Y, screenXY.X + 1, screenXY.Y + length + 1,
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

    public static void paintVerticalLine(final GuiGraphics gfx, final int x, final int y, final int length,
                                         final int zLevel, final Colour colour) {
        gfx.fill(RenderTypes.gui(), x, y, x + 1, y + length, zLevel, colour.toARGB());
    }

    //endregion
    //region buttons

    public static void paintButton3D(final GuiGraphics gfx, final Point screenXY, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour gradientLightColour,
                                     final Colour gradientDarkColour, final Colour borderLightColour,
                                     final Colour borderDarkColour) {
        paintButton3D(gfx, screenXY.X, screenXY.Y, width, height, zLevel, darkOutlineColour, gradientLightColour,
                gradientDarkColour, borderLightColour, borderDarkColour);
    }

    public static void paintButton3D(final GuiGraphics gfx, final int x, final int y, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour gradientLightColour,
                                     final Colour gradientDarkColour, final Colour borderLightColour,
                                     final Colour borderDarkColour) {

        paintHollowRect(gfx, x, y, width, height, zLevel, darkOutlineColour);
        paintTriangularGradientRect(gfx, x + 2, y + 2, width - 3, height - 3, zLevel, gradientLightColour, gradientDarkColour);

        paintHorizontalLine(gfx, x + 1, y + 1, width - 2, zLevel, borderLightColour);
        paintVerticalLine(gfx, x + 1, y + 1, height - 3, zLevel, borderLightColour);
        paintHorizontalLine(gfx, x + 1, y + height - 2, width - 2, zLevel, borderDarkColour);
        paintVerticalLine(gfx, x + width - 2, y + 2, height - 3, zLevel, borderDarkColour);
    }

    public static void paintButton3D(final GuiGraphics gfx, final Point screenXY, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour flatBackgroundColour,
                                     final Colour borderLightColour, final Colour borderDarkColour) {
        paintButton3D(gfx, screenXY.X, screenXY.Y, width, height, zLevel, darkOutlineColour, flatBackgroundColour,
                borderLightColour, borderDarkColour);
    }

    public static void paintButton3D(final GuiGraphics gfx, final int x, final int y, final int width, final int height,
                                     final int zLevel, final Colour darkOutlineColour, final Colour flatBackgroundColour,
                                     final Colour borderLightColour, final Colour borderDarkColour) {

        paintHollowRect(gfx, x, y, width, height, zLevel, darkOutlineColour);
        paintSolidRect(gfx, x + 2, y + 2, x + 2 + width - 3, y + 2 + height - 3, zLevel, flatBackgroundColour);

        paintHorizontalLine(gfx, x + 1, y + 1, width - 2, zLevel, borderLightColour);
        paintVerticalLine(gfx, x + 1, y + 1, height - 3, zLevel, borderLightColour);
        paintHorizontalLine(gfx, x + 1, y + height - 2, width - 2, zLevel, borderDarkColour);
        paintVerticalLine(gfx, x + width - 2, y + 2, height - 3, zLevel, borderDarkColour);
    }

    //endregion
    //region message box

    public static void paintMessage(final GuiGraphics gfx, final IRichText message, final int x, final int y,
                                    final int zLevel, final int margin, final Colour background,
                                    final Colour highlight1, final Colour highlight2) {

        final Rectangle boxBounds = message.bounds()
                .expand(margin * 2, margin * 2)
                .offset(x, y);

        paintVerticalLine(gfx, boxBounds.getX1(), boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, background);
        paintSolidRect(gfx, boxBounds.getX1() + 1, boxBounds.getY1(), boxBounds.getX2(), boxBounds.getY2() + 1, zLevel, background);
        paintVerticalLine(gfx, boxBounds.getX2(), boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, background);

        paintVerticalGradientLine(gfx, boxBounds.getX1() + 1, boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, highlight1, highlight2);
        paintHorizontalGradientLine(gfx, boxBounds.getX1() + 2, boxBounds.getY1() + 1, boxBounds.Width - 4, zLevel, highlight1, highlight2);
        paintHorizontalGradientLine(gfx, boxBounds.getX1() + 2, boxBounds.getY2() - 1, boxBounds.Width - 4, zLevel, highlight1, highlight2);
        paintVerticalGradientLine(gfx, boxBounds.getX2() - 1, boxBounds.getY1() + 1, boxBounds.Height - 2, zLevel, highlight1, highlight2);

        message.paint(gfx, boxBounds.getX1() + margin, boxBounds.getY1() + margin, zLevel + 1);
    }

    //endregion
    //endregion
    //region common paint tasks

    public static boolean paintItemStack(final GuiGraphics gfx, final ItemStack stack, final int x, final int y,
                                         final String text, final boolean highlight) {

        if (stack.isEmpty()) {
            return false;
        }

        if (highlight) {
            paintSolidRect(gfx, x, y, x + 16, y + 16, GUI_ITEM_Z - 1, Colour.fromARGB(-2130706433));
        }

        gfx.renderItem(stack, x, y);
        gfx.renderItemDecorations(Minecraft.getInstance().font, stack, x + 4, y, text);
        return true;
    }

    public static boolean paintItemStackWithCount(final GuiGraphics gfx, final ItemStack stack,
                                                  final int x, final int y, final boolean highlight) {
        return !stack.isEmpty() &&
                paintItemStack(gfx, stack, x, y, CodeHelper.formatAsHumanReadableNumber(stack.getCount(), ""), highlight);
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
    public static void paintSolidLines(final GuiGraphics gfx, final Colour colour, final double thickness,
                                       final double zLevel, final double... vertices) {

        final Matrix4f pose = gfx.pose().last().pose();
        final float halfThickness = (float)(thickness / 2.0);
        final int verticesCount = vertices.length;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

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
                
                builder.addVertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 - halfThickness, y2 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 + halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 + halfThickness, y2 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                
            } else if (y1 == y2) {

                if (x2 < x1) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;
                }

                builder.addVertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 - halfThickness, y1 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 + halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 + halfThickness, y1 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);

            } else if ((x1 < x2 && y1 < y2) || (x2 < x1 && y2 < y1)) {

                if (x2 < x1) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;

                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }

                builder.addVertex(pose, x1 + halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 - halfThickness, y1 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 + halfThickness, y2 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 - halfThickness, y2 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);

            } else {

                if (x1 < x2) {

                    float swap = x1;

                    x1 = x2;
                    x2 = swap;

                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }

                builder.addVertex(pose, x1 + halfThickness, y1 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x1 - halfThickness, y1 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 + halfThickness, y2 + halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
                builder.addVertex(pose, x2 - halfThickness, y2 - halfThickness, (float)zLevel).setColor(colour.R, colour.G, colour.B, colour.A);
            }
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());
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
    public static void paintSolidRects(final GuiGraphics gfx, final Colour colour, final double zLevel, final int... vertices) {

        final Tesselator tessellator = Tesselator.getInstance();
        final Matrix4f pose = gfx.pose().last().pose();

        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        ModRenderHelper.glSetColour(colour);

        RenderSystem.setShader(GameRenderer::getPositionShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 4) {

            final int x1 = vertices[i];
            final int y1 = vertices[i + 1];
            final int x2 = vertices[i + 2];
            final int y2 = vertices[i + 3];

            builder.addVertex(pose, x1, y2, (float)zLevel);
            builder.addVertex(pose, x2, y2, (float)zLevel);
            builder.addVertex(pose, x2, y1, (float)zLevel);
            builder.addVertex(pose, x1, y1, (float)zLevel);
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());
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
    public static void paintSolidTriangles(final GuiGraphics gfx, final Colour colour, final double zLevel, final int... vertices) {

        final Matrix4f pose = gfx.pose().last().pose();

        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        ModRenderHelper.glSetColour(colour);

        RenderSystem.setShader(GameRenderer::getPositionShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        final int verticesCount = vertices.length;

        for (int i = 0; i < verticesCount; i += 6) {

            final int x1 = vertices[i];
            final int y1 = vertices[i + 1];
            final int x2 = vertices[i + 2];
            final int y2 = vertices[i + 3];
            final int x3 = vertices[i + 4];
            final int y3 = vertices[i + 5];

            builder.addVertex(pose, x1, y1, (float)zLevel);
            builder.addVertex(pose, x2, y2, (float)zLevel);
            builder.addVertex(pose, x3, y3, (float)zLevel);
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());
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
    public static void paintHorizontalGradientLine(final GuiGraphics gfx, final int x, final int y, final int length, final double zLevel,
                                                   final Colour startColour, final Colour endColour) {
        ModRenderHelper.paintHorizontalGradientRect(gfx, x, y, x + length, y + 1, zLevel, startColour, endColour);
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
    public static void paintVerticalGradientLine(final GuiGraphics gfx, final int x, final int y, final int length, final double zLevel,
                                                 final Colour startColour, final Colour endColour) {
        ModRenderHelper.paintVerticalGradientRect(gfx, x, y, x + 1, y + length, zLevel, startColour, endColour);
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
    public static void paintVerticalGradientRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                                 final Colour startColour, final Colour endColour) {

        final Matrix4f pose = gfx.pose().last().pose();
        final float startAlpha = startColour.glAlpha();
        final float startRed = startColour.glRed();
        final float startGreen = startColour.glGreen();
        final float startBlue = startColour.glBlue();
        final float endAlpha = endColour.glAlpha();
        final float endRed = endColour.glRed();
        final float endGreen = endColour.glGreen();
        final float endBlue = endColour.glBlue();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, x2, y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x1, y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x1, y2, (float)zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        builder.addVertex(pose, x2, y2, (float)zLevel).setColor(endRed, endGreen, endBlue, endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
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
    public static void paintHorizontalGradientRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2,
                                                   final double zLevel, final Colour startColour, final Colour endColour) {

        final Matrix4f pose = gfx.pose().last().pose();
        final float startAlpha = startColour.glAlpha();
        final float startRed = startColour.glRed();
        final float startGreen = startColour.glGreen();
        final float startBlue = startColour.glBlue();
        final float endAlpha = endColour.glAlpha();
        final float endRed = endColour.glRed();
        final float endGreen = endColour.glGreen();
        final float endBlue = endColour.glBlue();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, x1, y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x1, y2, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x2, y2, (float)zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        builder.addVertex(pose, x2, y1, (float)zLevel).setColor(endRed, endGreen, endBlue, endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
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
    public static void paint3DGradientRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                           final Colour lightColour, final Colour darkColour) {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Matrix4f pose = gfx.pose().last().pose();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, x2, y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x1, y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x1, y2, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, x2, y2, (float)zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
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
    public static void paint3DGradientTriangle(final GuiGraphics gfx, final double x1, final double y1, final double x2, final double y2,
                                               final double x3, final double y3, final double zLevel,
                                               final Colour lightColour, final Colour darkColour) {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Matrix4f pose = gfx.pose().last().pose();
        final float startAlpha = lightColour.glAlpha();
        final float startRed = lightColour.glRed();
        final float startGreen = lightColour.glGreen();
        final float startBlue = lightColour.glBlue();
        final float endAlpha = darkColour.glAlpha();
        final float endRed = darkColour.glRed();
        final float endGreen = darkColour.glGreen();
        final float endBlue = darkColour.glBlue();

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, (float)x2, (float)y2, (float)zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);
        builder.addVertex(pose, (float)x1, (float)y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, (float)x3, (float)y3, (float)zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
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
    public static void paint3DGradientTriangle(final GuiGraphics gfx, final double x1, final double y1, final double x2, final double y2,
                                               final double x3, final double y3, final double zLevel,
                                               final Colour colour1, final Colour colour2, final Colour colour3) {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Matrix4f pose = gfx.pose().last().pose();

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, (float)x2, (float)y2, (float)zLevel).setColor(colour2.glRed(), colour2.glGreen(), colour2.glBlue(), colour2.glAlpha());
        builder.addVertex(pose, (float)x1, (float)y1, (float)zLevel).setColor(colour1.glRed(), colour1.glGreen(), colour1.glBlue(), colour1.glAlpha());
        builder.addVertex(pose, (float)x3, (float)y3, (float)zLevel).setColor(colour3.glRed(), colour3.glGreen(), colour3.glBlue(), colour3.glAlpha());

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
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
    public static void paint3DSolidTriangle(final GuiGraphics gfx, final double x1, final double y1, final double x2, final double y2,
                                            final double x3, final double y3, final double zLevel, final Colour colour) {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                GlStateManager.DestFactor.ZERO.value);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        final Matrix4f pose = gfx.pose().last().pose();
        final float startAlpha = colour.glAlpha();
        final float startRed = colour.glRed();
        final float startGreen = colour.glGreen();
        final float startBlue = colour.glBlue();
        final float endAlpha = colour.glAlpha();
        final float endRed = colour.glRed();
        final float endGreen = colour.glGreen();
        final float endBlue = colour.glBlue();

        final BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        builder.addVertex(pose, (float)x1, (float)y1, (float)zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        builder.addVertex(pose, (float)x3, (float)y3, (float)zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);
        builder.addVertex(pose, (float)x2, (float)y2, (float)zLevel).setColor(endRed  , endGreen  , endBlue  , endAlpha);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void paint3DSunkenBox(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                        final Colour gradientLightColour, final Colour gradientDarkColour,
                                        final Colour borderLightColour, final Colour borderDarkColour) {

        ModRenderHelper.paint3DGradientRect(gfx, x1 + 1, y1 + 1, x2 - 1, y2 - 1, zLevel, gradientLightColour, gradientDarkColour);

        ModRenderHelper.paintSolidRects(gfx, borderDarkColour, zLevel,
                x1, y1, x2, y1 + 1,
                x1, y1, x1 + 1, y2);

        ModRenderHelper.paintSolidRects(gfx, borderLightColour, zLevel,
                x1, y2 - 1, x2, y2,
                x2 - 1, y1, x2, y2);
    }

    public static void paint3DSunkenBox(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final double zLevel,
                                        final Colour gradientLightColour, final Colour borderLightColour, final Colour borderDarkColour) {

        ModRenderHelper.paintSolidRect(gfx, x1 + 1, y1 + 1, x2 - 1, y2 - 1, (int)zLevel, gradientLightColour);

        ModRenderHelper.paintSolidRects(gfx, borderDarkColour, zLevel,
                x1, y1, x2, y1 + 1,
                x1, y1, x1 + 1, y2);

        ModRenderHelper.paintSolidRects(gfx, borderLightColour, zLevel,
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
        return new Matrix4f().perspective(fov, aspect, zNear, zFar);
    }

    //region Texture atlases helpers

    public static IntIntPair getAtlasDimensions(TextureAtlasSprite sprite) {

        Preconditions.checkNotNull(sprite, "Sprite must not be null");

        final var contents = sprite.contents();
        final int atlasWidth, atlasHeight;

        atlasWidth = (int)(contents.width() / (sprite.getU1() - sprite.getU0()));
        atlasHeight = (int)(contents.height() / (sprite.getV1() - sprite.getV0()));

        return IntIntPair.of(atlasWidth, atlasHeight);
    }

    public static void dumpAtlas(ResourceLocation id) {

        Preconditions.checkNotNull(id, "Id must not be null");

        final var atlas = Preconditions.checkNotNull(Minecraft.getInstance().getModelManager().getAtlas(id),
                "Atlas with ID %s was not found", id);

        dumpAtlas(atlas);
    }

    public static void dumpAtlas(TextureAtlas atlas) {

        Preconditions.checkNotNull(atlas, "Atlas must not be null");

        final var sprites = atlas.getTextures().keySet().stream()
                .map(atlas::getSprite)
                .sorted((s1, s2) -> s1.getX() == s2.getX() ? s1.getY() - s2.getY() : s1.getX() - s2.getX())
                .toList();

        if (sprites.isEmpty()) {

            Log.LOGGER.warn(Log.CLIENT, "Atlas {} is empty: nothing to dump", atlas.location());
            return;
        }

        final var filename = atlas.location().toDebugFileName();
        var path = TextureUtil.getDebugTexturePath();

        try {

            final var dimensions = getAtlasDimensions(sprites.getFirst());

            Files.createDirectories(path);
            TextureUtil.writeAsPNG(path, filename, atlas.getId(), 0, dimensions.firstInt(), dimensions.secondInt());

            // dump sprites data

            path = path.resolve(filename + "_sprites.txt");

            try (final var writer = new PrintWriter(Files.newBufferedWriter(path))) {

                writer.println("#\tID\tX\tY\tWidth\tHeight");

                int count = 0;

                for (final var sprite : sprites) {

                    final var contents = sprite.contents();

                    writer.println(String.format("%d\t%s\t%d\t%d\t%d\t%d", ++count, contents.name(),
                            sprite.getX(), sprite.getY(), contents.width(), contents.height()));
                }

            } catch (IOException ex) {
                Log.LOGGER.warn(Log.CLIENT, "Failed to write sprites data to {} : {}", path, ex);
            }

        } catch (IOException ex) {
            Log.LOGGER.warn(Log.CLIENT, "Failed to dump atlas to {} : {}", path, ex);
        }
    }

    //endregion
    //region debug helpers

    public static Map<ModelResourceLocation, BakedModel> debugFilterModelRegistryByModId(Map<ModelResourceLocation, BakedModel> modelRegistry,
                                                                                         String modId) {
        return debugFilterModelRegistryByModId(modelRegistry, modId, null);
    }

    public static Map<ModelResourceLocation, BakedModel> debugFilterModelRegistryByModId(Map<ModelResourceLocation, BakedModel> modelRegistry,
                                                                                         String modId,
                                                                                         @Nullable String optionalPathFilter) {

        Preconditions.checkNotNull(modelRegistry, "Model registry must not be null");
        Preconditions.checkArgument(!modelRegistry.isEmpty(), "Model registry must not be empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(modId), "Mod ID must not be null or empty");

        var stream = modelRegistry.entrySet().stream()
                .filter(entry -> entry.getKey().id().getNamespace().equals(modId));

        if (!Strings.isNullOrEmpty(optionalPathFilter)) {
            stream = stream.filter(entry -> entry.getKey().id().getPath().contains(optionalPathFilter));
        }
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (model1, model2) -> {
                    throw new IllegalStateException("Duplicated model resource locations found!");
                },
                () -> new Object2ObjectAVLTreeMap<>(Comparator.comparing(ModelResourceLocation::id)
                        .thenComparing(ModelResourceLocation::getVariant, String::compareTo))));
    }

    //endregion
    //region internals

    private ModRenderHelper(){
    }

    //endregion
}

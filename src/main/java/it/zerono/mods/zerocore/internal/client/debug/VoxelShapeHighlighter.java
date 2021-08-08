/*
 *
 * VoxelShapeHighlighter.java
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

package it.zerono.mods.zerocore.internal.client.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@OnlyIn(Dist.CLIENT)
public class VoxelShapeHighlighter {

    @SubscribeEvent
    public static void onHighlightBlock(final DrawHighlightEvent.HighlightBlock event) {

        final BlockRayTraceResult result = event.getTarget();
        final World world;

        if (RayTraceResult.Type.BLOCK != result.getType()) {
            return;
        }

        try {

            world = getWorld(event);

        } catch (IllegalAccessException e) {

            Log.LOGGER.error(Log.CORE, "Voxel highlighter: failed to get world!");
            return;
        }

        final BlockPos position = result.getBlockPos();
        final DebugHelper.VoxelShapeType voxelType = DebugHelper.getBlockVoxelShapeHighlight(world, position);

        if (DebugHelper.VoxelShapeType.None == voxelType) {
            return;
        }

        final BlockState blockstate = world.getBlockState(position);

        if (blockstate.isAir(world, position) || !world.getWorldBorder().isWithinBounds(position)) {
            return;
        }

        final ActiveRenderInfo renderInfo = event.getInfo();
        final ISelectionContext selection = ISelectionContext.of(renderInfo.getEntity());
        final IVertexBuilder builder = event.getBuffers().getBuffer(RenderType.lines());
        final MatrixStack matrixStack = event.getMatrix();
        final double x = position.getX() - renderInfo.getPosition().x();
        final double y = position.getY() - renderInfo.getPosition().y();
        final double z = position.getZ() - renderInfo.getPosition().z();

        switch (voxelType) {

            case General:
                paint(matrixStack, builder, x, y, z, COLOUR_SHAPE, blockstate.getShape(world, position, selection));
                break;

            case Render:
                paint(matrixStack, builder, x, y, z, COLOUR_RENDERSHAPE, blockstate.getBlockSupportShape(world, position));
                break;

            case Collision:
                paint(matrixStack, builder, x, y, z, COLOUR_COLLISIONSHAPE, blockstate.getCollisionShape(world, position, selection));
                break;

            case RayTrace:
                paint(matrixStack, builder, x, y, z, COLOUR_RAYTRACESHAPE, blockstate.getVisualShape(world, position, ISelectionContext.empty()));
                break;
        }

        event.setCanceled(true);
    }

    //region internals

    private static void paint(final MatrixStack matrixStack, final IVertexBuilder vertexBuilder,
                              final double originX, final double originY, final double originZ,
                              final Colour colour, final VoxelShape voxelShape) {

        final Matrix4f matrix = matrixStack.last().pose();
        final float red = colour.glRed();
        final float green = colour.glGreen();
        final float blue = colour.glBlue();

        voxelShape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {

            vertexBuilder.vertex(matrix, (float)(x1 + originX), (float)(y1 + originY), (float)(z1 + originZ)).color(red, green, blue, 0.5f).endVertex();
            vertexBuilder.vertex(matrix, (float)(x2 + originX), (float)(y2 + originY), (float)(z2 + originZ)).color(red, green, blue, 0.5f).endVertex();
        });
    }

    private static World getWorld(final DrawHighlightEvent.HighlightBlock event) throws IllegalAccessException {

        if (null == s_worldField) {
            s_worldField = ObfuscationReflectionHelper.findField(WorldRenderer.class, "level");
        }

        return (World)s_worldField.get(event.getContext());
    }

    private static final Colour COLOUR_SHAPE = Colour.from(DyeColor.YELLOW);
    private static final Colour COLOUR_RENDERSHAPE = Colour.from(DyeColor.RED);
    private static final Colour COLOUR_COLLISIONSHAPE = Colour.from(DyeColor.BLUE);
    private static final Colour COLOUR_RAYTRACESHAPE = Colour.from(DyeColor.PURPLE);

    private static Field s_worldField;

    //endregion
}

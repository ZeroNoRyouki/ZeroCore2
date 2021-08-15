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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@OnlyIn(Dist.CLIENT)
public class VoxelShapeHighlighter {

    @SubscribeEvent
    public static void onHighlightBlock(final DrawSelectionEvent.HighlightBlock event) {

        final BlockHitResult result = event.getTarget();
        final Level world;

        if (HitResult.Type.BLOCK != result.getType()) {
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

        if (blockstate.isAir() || !world.getWorldBorder().isWithinBounds(position)) {
            return;
        }

        final Camera renderInfo = event.getInfo();
        final CollisionContext selection = CollisionContext.of(renderInfo.getEntity());
        final VertexConsumer builder = event.getBuffers().getBuffer(RenderType.lines());
        final PoseStack matrixStack = event.getMatrix();
        final double x = position.getX() - renderInfo.getPosition().x();
        final double y = position.getY() - renderInfo.getPosition().y();
        final double z = position.getZ() - renderInfo.getPosition().z();

        switch (voxelType) {
            case General -> paint(matrixStack, builder, x, y, z, COLOUR_SHAPE, blockstate.getShape(world, position, selection));
            case Render -> paint(matrixStack, builder, x, y, z, COLOUR_RENDERSHAPE, blockstate.getBlockSupportShape(world, position));
            case Collision -> paint(matrixStack, builder, x, y, z, COLOUR_COLLISIONSHAPE, blockstate.getCollisionShape(world, position, selection));
            case RayTrace -> paint(matrixStack, builder, x, y, z, COLOUR_RAYTRACESHAPE, blockstate.getVisualShape(world, position, CollisionContext.empty()));
        }

        event.setCanceled(true);
    }

    //region internals

    private static void paint(final PoseStack matrixStack, final VertexConsumer vertexBuilder,
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

    private static Level getWorld(final DrawSelectionEvent.HighlightBlock event) throws IllegalAccessException {

        if (null == s_worldField) {
            s_worldField = ObfuscationReflectionHelper.findField(LevelRenderer.class, "field_145850_b"); // level
        }

        return (Level)s_worldField.get(event.getContext());
    }

    private static final Colour COLOUR_SHAPE = Colour.from(DyeColor.YELLOW);
    private static final Colour COLOUR_RENDERSHAPE = Colour.from(DyeColor.RED);
    private static final Colour COLOUR_COLLISIONSHAPE = Colour.from(DyeColor.BLUE);
    private static final Colour COLOUR_RAYTRACESHAPE = Colour.from(DyeColor.PURPLE);

    private static Field s_worldField;

    //endregion
}

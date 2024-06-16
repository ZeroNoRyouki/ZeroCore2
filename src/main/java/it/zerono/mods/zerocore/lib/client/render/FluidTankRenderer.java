package it.zerono.mods.zerocore.lib.client.render;

/*
 * FluidTankRenderer
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
 * Do not remove or edit this header
 *
 */

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.zerono.mods.zerocore.internal.client.RenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

public abstract class FluidTankRenderer {

    //region Single-fluid

    public static class Single
            extends FluidTankRenderer {

        public Single(int capacity, float x1, float y1, float z1, float x2, float y2, float z2) {

            super(capacity);

            this._xSteps = (int) (x2 - x1) + 1;
            this._xQuadCoords = computeQuadCoordinates(this._xSteps);
            this._ySteps = (int) (y2 - y1) + 1;
            this._yQuadCoords = computeQuadCoordinates(this._ySteps);
            this._zSteps = (int) (z2 - z1) + 1;
            this._zQuadCoords = computeQuadCoordinates(this._zSteps);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, FluidStack fluidStack) {

            if (fluidStack.isEmpty()) {
                return;
            }

            final float fillPercentage = Math.min(1.0f, fluidStack.getAmount() / (float) this._capacity);
            final float yFilledSteps = this._ySteps * fillPercentage;
            final int yFullSteps = (int) yFilledSteps;
            final float yIncompleteStepPercentage = yFilledSteps - yFullSteps;

            final Matrix4f matrix = poseStack.last().pose();
            final VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.FLUID_COLUMN);

            final Fluid fluid = fluidStack.getFluid();
            final FluidType fluidType = fluid.getFluidType();
            final TextureAtlasSprite stillSprite = ModRenderHelper.getFluidStillSprite(fluid);
            final TextureAtlasSprite flowingSprite = ModRenderHelper.getFluidFlowingSprite(fluid);
            final int fluidColour = ModRenderHelper.getFluidTint(fluid);

            final float stillSpriteMinU, stillSpriteMaxU, stillSpriteMinV, stillSpriteMaxV,
                    flowingSpriteMinU, flowingSpriteMaxU, flowingSpriteMinV, flowingSpriteMaxV,
                    incompleteSpriteMinV, incompleteSpriteMaxV;

            stillSpriteMinU = stillSprite.getU0();
            stillSpriteMaxU = stillSprite.getU1();
            flowingSpriteMinU = flowingSprite.getU0();
            flowingSpriteMaxU = flowingSprite.getU1();

            if (fluidType.isLighterThanAir()) {

                // upside down

                stillSpriteMinV = stillSprite.getV1();
                stillSpriteMaxV = stillSprite.getV0();

                flowingSpriteMinV = flowingSprite.getV1();
                flowingSpriteMaxV = flowingSprite.getV0();

                if (yIncompleteStepPercentage > 0.0f) {

                    incompleteSpriteMinV = flowingSprite.getV(16.0f * yIncompleteStepPercentage);
                    incompleteSpriteMaxV = flowingSprite.getV0();

                } else {

                    incompleteSpriteMinV = incompleteSpriteMaxV = 0.0f;
                }

            } else {

                // normal orientation

                stillSpriteMinV = stillSprite.getV0();
                stillSpriteMaxV = stillSprite.getV1();

                flowingSpriteMinV = flowingSprite.getV0();
                flowingSpriteMaxV = flowingSprite.getV1();

                if (yIncompleteStepPercentage > 0.0f) {

                    incompleteSpriteMinV = flowingSprite.getV(16.0f * (1.0f - yIncompleteStepPercentage));
                    incompleteSpriteMaxV = flowingSprite.getV1();

                } else {

                    incompleteSpriteMinV = incompleteSpriteMaxV = 0.0f;
                }
            }

            packedLight = ModRenderHelper.addBlockLight(packedLight, fluidType.getLightLevel(fluidStack));

            float y1, y2;

            y1 = y2 = this._yQuadCoords[0] + MARGIN;

            // bottom face

            for (int z = 0; z < this._zSteps; ++z) {
                for (int x = 0; x < this._xSteps; ++x) {

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.DOWN,
                            this._xQuadCoords[x], y1, this._zQuadCoords[z],
                            this._xQuadCoords[x + 1], y1, this._zQuadCoords[z + 1],
                            stillSpriteMinU, stillSpriteMaxU, stillSpriteMinV, stillSpriteMaxV,
                            fluidColour, packedLight);
                }
            }

            // sides

            for (int yStep = 0; yStep < yFullSteps; ++yStep) {

                y1 = this._yQuadCoords[yStep];
                y2 = this._yQuadCoords[yStep + 1];

                // - first full layer

                for (int x = 0; x < this._xSteps; ++x) {

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.NORTH,
                            this._xQuadCoords[x], y1, this._zQuadCoords[0],
                            this._xQuadCoords[x + 1], y2, this._zQuadCoords[1],
                            flowingSpriteMinU, flowingSpriteMaxU, flowingSpriteMinV, flowingSpriteMaxV,
                            fluidColour, packedLight);

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.SOUTH,
                            this._xQuadCoords[x], y1, this._zQuadCoords[this._zSteps - 1],
                            this._xQuadCoords[x + 1], y2, this._zQuadCoords[this._zSteps],
                            flowingSpriteMinU, flowingSpriteMaxU, flowingSpriteMinV, flowingSpriteMaxV,
                            fluidColour, packedLight);
                }

                for (int z = 0; z < this._zSteps; ++z) {

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.WEST,
                            this._xQuadCoords[0], y1, this._zQuadCoords[z],
                            this._xQuadCoords[1], y2, this._zQuadCoords[z + 1],
                            flowingSpriteMinU, flowingSpriteMaxU, flowingSpriteMinV, flowingSpriteMaxV,
                            fluidColour, packedLight);

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.EAST,
                            this._xQuadCoords[this._xSteps - 1], y1, this._zQuadCoords[z],
                            this._xQuadCoords[this._xSteps],  y2, this._zQuadCoords[z + 1],
                            flowingSpriteMinU, flowingSpriteMaxU, flowingSpriteMinV, flowingSpriteMaxV,
                            fluidColour, packedLight);
                }
            }

            if (yIncompleteStepPercentage > 0.0f) {

                y1 = y2;
                y2 = y1 + yIncompleteStepPercentage;

                for (int x = 0; x < this._xSteps; ++x) {

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.NORTH,
                            this._xQuadCoords[x], y1, this._zQuadCoords[0],
                            this._xQuadCoords[x + 1],  y2, this._zQuadCoords[1],
                            flowingSpriteMinU, flowingSpriteMaxU, incompleteSpriteMinV, incompleteSpriteMaxV,
                            fluidColour, packedLight);

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.SOUTH,
                            this._xQuadCoords[x], y1, this._zQuadCoords[this._zSteps - 1],
                            this._xQuadCoords[x + 1], y2, this._zQuadCoords[this._zSteps],
                            flowingSpriteMinU, flowingSpriteMaxU, incompleteSpriteMinV, incompleteSpriteMaxV,
                            fluidColour, packedLight);
                }

                for (int z = 0; z < this._zSteps; ++z) {

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.WEST,
                            this._xQuadCoords[0], y1, this._zQuadCoords[z],
                            this._xQuadCoords[1], y2, this._zQuadCoords[z + 1],
                            flowingSpriteMinU, flowingSpriteMaxU, incompleteSpriteMinV, incompleteSpriteMaxV,
                            fluidColour, packedLight);

                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.EAST,
                            this._xQuadCoords[this._xSteps - 1], y1, this._zQuadCoords[z],
                            this._xQuadCoords[this._xSteps], y2, this._zQuadCoords[z + 1],
                            flowingSpriteMinU, flowingSpriteMaxU, incompleteSpriteMinV, incompleteSpriteMaxV,
                            fluidColour, packedLight);
                }
            }

            // top layer

            for (int z = 0; z < this._zSteps; ++z) {
                for (int x = 0; x < this._xSteps; ++x) {
                    ModRenderHelper.renderBlockFace(vertexConsumer, matrix, Direction.UP,
                            this._xQuadCoords[x], y1, this._zQuadCoords[z],
                            this._xQuadCoords[x + 1], y2, this._zQuadCoords[z + 1],
                            stillSpriteMinU, stillSpriteMaxU, stillSpriteMinV, stillSpriteMaxV,
                            fluidColour, packedLight);
                }
            }
        }

        private final int _xSteps;
        private final float[] _xQuadCoords;
        private final int _ySteps;
        private final float[] _yQuadCoords;
        private final int _zSteps;
        private final float[] _zQuadCoords;

        //endregion
    }

    //endregion
    //region internals

    protected FluidTankRenderer(int capacity) {

        Preconditions.checkArgument(capacity > 0, "Capacity must be greater than zero");
        this._capacity = capacity;
    }

    private static float[] computeQuadCoordinates(int steps) {
        return computeQuadCoordinates(steps, 1.0f, MARGIN, steps - MARGIN);
    }

    private static float[] computeQuadCoordinates(int steps, float stepSize, float start, float end) {

        Preconditions.checkArgument(steps > 0, "Steps must be greater than zero");

        final float[] coords = new float[1 + steps];
        final int offset = (int) start;

        // the first coordinate is always "start"
        coords[0] = start;

        for (int i = 1; i <= steps - 1; ++i) {
            coords[i] = i * stepSize + offset;
        }

        // the last coordinate is always "end"
        coords[coords.length - 1] = end;

        return coords;
    }

    private static final float MARGIN = 0.005f;

    protected final int _capacity;

    //endregion
}

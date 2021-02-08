/*
 *
 * TintingRenderTypeBuffer.java
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

package it.zerono.mods.zerocore.lib.client.render.buffer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class TintingRenderTypeBufferWrapper
        implements IRenderTypeBuffer {

    public TintingRenderTypeBufferWrapper(final IRenderTypeBuffer originalBuffer, final int alpha,
                                          final int redTint, final int greenTint, final int blueTint) {

        this._buffer = Objects.requireNonNull(originalBuffer);
        this._alpha = MathHelper.clamp(alpha, 0, 255);
        this._red = MathHelper.clamp(redTint, 0, 255);
        this._green = MathHelper.clamp(greenTint, 0, 255);
        this._blue = MathHelper.clamp(blueTint, 0, 255);
    }

    public TintingRenderTypeBufferWrapper(final IRenderTypeBuffer originalBuffer, final float alpha,
                                          final float redTint, final float greenTint, final float blueTint) {
        this(originalBuffer, (int)(alpha * 255.0F), (int)(redTint * 255.0F), (int)(greenTint * 255.0F), (int)(blueTint * 255.0F));
    }

    //region IRenderTypeBuffer

    @Override
    public IVertexBuilder getBuffer(final RenderType type) {
        return new TintingVertexBuilder(this._buffer.getBuffer(type), this._alpha, this._red, this._green, this._blue);
    }

    //endregion
    //region internals

    private final IRenderTypeBuffer _buffer;
    private final int _alpha, _red, _green, _blue;

    private static class TintingVertexBuilder
            extends VertexBuilderWrapper {

        TintingVertexBuilder(final IVertexBuilder originalBuilder, final int alpha,
                             final int red, final int green, final int blue) {

            super(originalBuilder);
            this._alpha = alpha;
            this._red = red;
            this._green = green;
            this._blue = blue;
        }

        //region VertexBuilderWrapper

        @Override
        public IVertexBuilder color(final int red, final int green, final int blue, final int alpha) {
            return this._builder.color((red * this._red) / 0xFF, (green * this._green) / 0xFF,
                    (blue * this._blue) / 0xFF, (alpha * this._alpha) / 0xFF);
        }

        //endregion
        //region internals

        private final int _alpha, _red, _green, _blue;

        //endregion
    }

    //endregion
}

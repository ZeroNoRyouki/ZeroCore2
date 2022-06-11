/*
 *
 * ISpriteAwareVertexBuilder.java
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

package it.zerono.mods.zerocore.lib.client.gui.sprite;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

public class ISpriteAwareVertexBuilder implements VertexConsumer {

    public ISpriteAwareVertexBuilder(final VertexFormat format, final VertexConsumer buffer, final ISprite sprite) {

        this._format = format;
        this._builder = buffer;
        this._sprite = sprite;
    }

    public VertexConsumer vertex(double x, double y, double z) {
        return this._builder.vertex(x, y, z);
    }

    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this._builder.color(red, green, blue, alpha);
    }

    public VertexConsumer uv(float u, float v) {
        return this._builder.uv(this._sprite.getInterpolatedU(u * 16.0F), this._sprite.getInterpolatedV(v * 16.0F));
    }

    public VertexConsumer overlayCoords(int u, int v) {
        return this._builder.overlayCoords(u, v);
    }

    public VertexConsumer uv2(int u, int v) {
        return this._builder.uv2(u, v);
    }

    public VertexConsumer normal(float x, float y, float z) {
        return this._builder.normal(x, y, z);
    }

    public void endVertex() {
        this._builder.endVertex();
    }

    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha,
                          float texU, float texV, int overlayUV, int lightmapUV,
                          float normalX, float normalY, float normalZ) {
        this._builder.vertex(x, y, z, red, green, blue, alpha, this._sprite.getInterpolatedU(texU * 16.0F),
                this._sprite.getInterpolatedV(texV * 16.0F), overlayUV, lightmapUV, normalX, normalY, normalZ);
    }

    @Override
    public void defaultColor(int p_166901_, int p_166902_, int p_166903_, int p_166904_) {
        this._builder.defaultColor(p_166901_, p_166902_, p_166903_, p_166904_);
    }

    @Override
    public void unsetDefaultColor() {
        this._builder.unsetDefaultColor();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this._format;
    }

    //region internals

    private final VertexFormat _format;
    private final VertexConsumer _builder;
    private final ISprite _sprite;

    //endregion
}

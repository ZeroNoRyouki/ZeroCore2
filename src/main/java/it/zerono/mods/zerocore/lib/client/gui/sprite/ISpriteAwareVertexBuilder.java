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
import net.minecraft.util.FastColor;

public class ISpriteAwareVertexBuilder implements VertexConsumer {

    public ISpriteAwareVertexBuilder(final VertexConsumer buffer, final ISprite sprite) {

        this._builder = buffer;
        this._sprite = sprite;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return this._builder.addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this._builder.setColor(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return this._builder.setUv(this._sprite.getInterpolatedU(u * 16.0F), this._sprite.getInterpolatedV(v * 16.0F));
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this._builder.setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this._builder.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return this._builder.setNormal(x, y, z);
    }

    public void addVertex(float x, float y, float z, float red, float green, float blue, float alpha,
                          float texU, float texV, int overlayUV, int lightmapUV,
                          float normalX, float normalY, float normalZ) {
        this.addVertex(x, y, z, FastColor.ARGB32.colorFromFloat(alpha, red, green, blue), texU, texV,
                overlayUV, lightmapUV, normalX, normalY, normalZ);
    }

    @Override
    public void addVertex(float x, float y, float z, int colour, float texU, float texV, int overlay, int light,
                          float normalX, float normalY, float normalZ) {
        this._builder.addVertex(x, y, z, colour,
                this._sprite.getInterpolatedU(texU * 16.0F), this._sprite.getInterpolatedV(texV * 16.0F),
                overlay, light, normalX, normalY, normalZ);
    }

    //region internals

    private final VertexConsumer _builder;
    private final ISprite _sprite;

    //endregion
}

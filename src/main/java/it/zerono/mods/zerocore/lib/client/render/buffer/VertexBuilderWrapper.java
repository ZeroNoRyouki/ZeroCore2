/*
 *
 * VertexBuilderWrapper.java
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

import com.mojang.blaze3d.vertex.VertexConsumer;

public class VertexBuilderWrapper
    implements VertexConsumer {

    protected VertexBuilderWrapper(final VertexConsumer originalBuilder) {
        this._builder = originalBuilder;
    }

    //region IVertexBuilder

    @Override
    public VertexConsumer vertex(final double x, final double y, final double z) {
        return this._builder.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(final int red, final int green, final int blue, final int alpha) {
        return this._builder.color(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer uv(final float u, final float v) {
        return this._builder.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(final int u, final int v) {
        return this._builder.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(final int u, final int v) {
        return this._builder.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(final float x, final float y, final float z) {
        return this._builder.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        this._builder.endVertex();
    }

    //endregion
    //region internals

    protected final VertexConsumer _builder;

    //endregion
}

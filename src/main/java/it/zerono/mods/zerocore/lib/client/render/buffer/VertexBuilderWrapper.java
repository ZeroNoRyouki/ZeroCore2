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
    public VertexConsumer addVertex(final float x, final float y, final float z) {
        return this._builder.addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(final int red, final int green, final int blue, final int alpha) {
        return this._builder.setColor(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer setUv(final float u, final float v) {
        return this._builder.setUv(u, v);
    }

    @Override
    public VertexConsumer setUv1(final int u, final int v) {
        return this._builder.setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(final int u, final int v) {
        return this._builder.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(final float x, final float y, final float z) {
        return this._builder.setNormal(x, y, z);
    }

    //endregion
    //region internals

    protected final VertexConsumer _builder;

    //endregion
}

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

import com.mojang.blaze3d.vertex.IVertexBuilder;

public class VertexBuilderWrapper
    implements IVertexBuilder {

    protected VertexBuilderWrapper(final IVertexBuilder originalBuilder) {
        this._builder = originalBuilder;
    }

    //region IVertexBuilder

    @Override
    public IVertexBuilder pos(final double x, final double y, final double z) {
        return this._builder.pos(x, y, z);
    }

    @Override
    public IVertexBuilder color(final int red, final int green, final int blue, final int alpha) {
        return this._builder.color(red, green, blue, alpha);
    }

    @Override
    public IVertexBuilder tex(final float u, final float v) {
        return this._builder.tex(u, v);
    }

    @Override
    public IVertexBuilder overlay(final int u, final int v) {
        return this._builder.overlay(u, v);
    }

    @Override
    public IVertexBuilder lightmap(final int u, final int v) {
        return this._builder.lightmap(u, v);
    }

    @Override
    public IVertexBuilder normal(final float x, final float y, final float z) {
        return this._builder.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        this._builder.endVertex();
    }

    //endregion
    //region internals

    protected final IVertexBuilder _builder;

    //endregion
}

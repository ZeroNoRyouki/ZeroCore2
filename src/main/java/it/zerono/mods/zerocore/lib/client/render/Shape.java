/*
 *
 * Shape.java
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

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "WeakerAccess"})
public class Shape implements /*IVertexSource*/ IVertexSequence {

    public Shape() {
        this(4);
    }

    public Shape(final int initialSize) {
        this._vertices = Lists.newArrayListWithCapacity(initialSize);
    }

    public void addVertex(final Vertex vertex) {
        this._vertices.add(vertex);
    }

//    public ImmutableList<Vertex> getVertices() {
//        return ImmutableList.copyOf(this._vertices);
//    }

    public int getVerticesCount() {
        return this._vertices.size();
    }

    //region IVertexSequence

    @Override
    public Stream<IVertexSource> getVerticesStream() {
        return this._vertices.stream().map(vertex -> vertex);
    }

    //endregion

//    //region IVertexSource
//
//    @Override
//    public void uploadVertexData(final IVertexBuilder buffer) {
//        this._vertices.forEach(vertex -> vertex.uploadVertexData(buffer));
//    }
//
//    @Override
//    public void uploadVertexData(IVertexBuilder buffer, LightMap lightMapOverride, LightMap overlayMapOverride) {
//        this._vertices.forEach(vertex -> vertex.uploadVertexData(buffer, lightMapOverride, overlayMapOverride));
//    }
//
//    @Override
//    public void uploadVertexData(IVertexBuilder buffer, Colour colourOverride) {
//        this._vertices.forEach(vertex -> vertex.uploadVertexData(buffer, colourOverride));
//    }
//
//    //endregion
    //region internals

    private final List<Vertex> _vertices;

    //endregion
}

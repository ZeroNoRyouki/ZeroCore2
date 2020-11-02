/*
 *
 * CombinedShape.java
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
import it.zerono.mods.zerocore.lib.client.render.builder.IPrimitiveBuilder;

import java.util.List;
import java.util.stream.Stream;

public class CombinedShape implements /*IVertexSource*/ IVertexSequence {

    public CombinedShape() {
        this(4);
    }

    public CombinedShape(final int initialSize) {
        this._shapes = Lists.newArrayListWithCapacity(initialSize);
    }

    public CombinedShape addShape(final Shape shape) {

        this._shapes.add(shape);
        return this;
    }

    public CombinedShape addShape(final IPrimitiveBuilder<Shape> builder) {
        return this.addShape(builder.build());
    }

    //region IVertexSequence

    @Override
    public Stream<IVertexSource> getVerticesStream() {
        return this._shapes.stream().flatMap(IVertexSequence::getVerticesStream);
    }

    //endregion

//    //region IVertexSource
//
//    @Override
//    public void uploadVertexData(final IVertexBuilder buffer) {
//        this._shapes.forEach(shape -> shape.uploadVertexData(buffer));
//    }
//
//    @Override
//    public void uploadVertexData(IVertexBuilder buffer, LightMap lightMapOverride, LightMap overlayMapOverride) {
//        this._shapes.forEach(vertex -> vertex.uploadVertexData(buffer, lightMapOverride, overlayMapOverride));
//    }
//
//    @Override
//    public void uploadVertexData(IVertexBuilder buffer, Colour colourOverride) {
//        this._shapes.forEach(vertex -> vertex.uploadVertexData(buffer, colourOverride));
//    }
//
//    //endregion
    //region internals

    private final List<Shape> _shapes;

    //endregion
}

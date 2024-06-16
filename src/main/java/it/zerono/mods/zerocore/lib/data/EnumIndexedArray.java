/*
 *
 * EnumIndexedArray.java
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

package it.zerono.mods.zerocore.lib.data;

import it.zerono.mods.zerocore.lib.CodeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumIndexedArray<Index extends Enum<Index>, Element>
        extends EnumIndexer<Index> {

    @SafeVarargs
    public EnumIndexedArray(final Function<Integer, Element[]> arrayFactory, final Index firstValidIndex,
                            final Index secondValidIndex, final Index... otherValidIndices) {

        super(firstValidIndex, secondValidIndex, otherValidIndices);
        this._elements = arrayFactory.apply(this.validIndicesCount());
    }

    public EnumIndexedArray(final Function<Integer, Element[]> arrayFactory, final Index validIndex) {

        super(validIndex);
        this._elements = arrayFactory.apply(this.validIndicesCount());
    }

    public EnumIndexedArray(final Function<Integer, Element[]> arrayFactory, final Index[] validIndices) {

        super(validIndices);
        this._elements = arrayFactory.apply(this.validIndicesCount());
    }

    public EnumIndexedArray(final Function<Integer, Element[]> arrayFactory, final Iterable<Index> validIndices) {

        super(validIndices);
        this._elements = arrayFactory.apply(this.validIndicesCount());
    }

    public Optional<Element> getElement(final Index type) {
        return Optional.ofNullable(this._elements[this.getOrdinal(type)]);
    }

    public Element getElement(final Index type, final Element defaultValue) {
        return this.getElement(type).orElse(defaultValue);
    }

    public <T> T map(final Index type, final Function<Element, T> mapper, final T defaultValue) {

        final Element e = this._elements[this.getOrdinal(type)];

        return null != e ? mapper.apply(e) : defaultValue;
    }

    public void accept(final Index type, final Consumer<Element> consumer) {

        final Element e = this._elements[this.getOrdinal(type)];

        if (null != e) {
            consumer.accept(e);
        }
    }

    public void setElement(final Index type, @Nullable final Element element) {
        this._elements[this.getOrdinal(type)] = element;
    }

    public void setAll(@Nullable final Element element) {
        Arrays.fill(this._elements, element);
    }

    public boolean isEmpty(final Index type) {
        return null == this._elements[this.getOrdinal(type)];
    }

    public Stream<Element> stream() {
        return this.getValidIndices().stream()
                .flatMap(index -> CodeHelper.optionalStream(this.getElement(index)));
    }

    public Stream<Element> stream(final Predicate<Index> test) {
        return this.getValidIndices().stream()
                .filter(test)
                .flatMap(index -> CodeHelper.optionalStream(this.getElement(index)));
    }

    //region Object

    @Override
    public String toString() {
        return this.getValidIndices().stream()
                .map(index -> String.format("%s:%s", index.toString(),
                        this.getElement(index)
                            .map(Object::toString)
                            .orElse("<EMPTY>")))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    //endregion
    //region internals

    private final Element[] _elements;

    //endregion
}

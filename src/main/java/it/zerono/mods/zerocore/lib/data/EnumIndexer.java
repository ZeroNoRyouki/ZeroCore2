/*
 *
 * EnumIndexer.java
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess"})
public class EnumIndexer<Index extends Enum<Index>> {

    @SafeVarargs
    public EnumIndexer(final Index firstValidIndex, final Index secondValidIndex, final Index... otherValidIndices) {
        this(join(firstValidIndex, secondValidIndex, otherValidIndices));
    }

    public EnumIndexer(final Index validIndex) {
        this._validIndices = ImmutableList.of(validIndex);
    }

    public EnumIndexer(final Index[] validIndices) {
        this._validIndices = ImmutableList.copyOf(validIndices);
    }

    public EnumIndexer(final Iterable<Index> validIndices) {
        this._validIndices = ImmutableList.copyOf(validIndices);
    }

    public int getOrdinal(final Index index) {

        final int ordinal = this._validIndices.indexOf(index);

        if (-1 == ordinal) {
            throw new IllegalArgumentException("Invalid index: " + index.toString());
        }

        return ordinal;
    }

    public Optional<Index> getIndex(final int ordinal) {

        if (ordinal < 0 || ordinal > this._validIndices.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this._validIndices.get(ordinal));
    }

    public int validIndicesCount() {
        return this._validIndices.size();
    }

    public List<Index> getValidIndices() {
        return this._validIndices;
    }

    public boolean isIndexValid(final Index index) {
        return this.getValidIndices().contains(index);
    }

    //region Object

    @Override
    public String toString() {
        return this.getValidIndices().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    //endregion
    //region internals

    @SafeVarargs
    private static <Index extends Enum<Index>> List<Index> join(final Index firstValidIndex, final Index secondValidIndex,
                                                                final Index... otherValidIndices) {

        final List<Index> indices = Lists.newArrayListWithCapacity(2 + otherValidIndices.length);

        indices.add(firstValidIndex);
        indices.add(secondValidIndex);

        if (otherValidIndices.length > 0) {
            indices.addAll(Arrays.asList(otherValidIndices));
        }

        return indices;
    }

    private final List<Index> _validIndices;

    //endregion
}

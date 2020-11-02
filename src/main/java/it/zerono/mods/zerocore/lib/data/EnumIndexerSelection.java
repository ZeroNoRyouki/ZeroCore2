/*
 *
 * EnumIndexerSelection.java
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

public class EnumIndexerSelection<Index extends Enum<Index>> {

    public EnumIndexerSelection(final EnumIndexer<Index> indexer) {

        this._indexer = indexer;
        this._validIndicesCount = this._indexer.validIndicesCount();
        this._selected = 0;
    }

    public Index selectNext() {

        this._selected = CodeHelper.positiveModulo(this._selected + 1, this._validIndicesCount);
        return this.getSelection();
    }

    public Index selectPrevious() {

        this._selected = CodeHelper.positiveModulo(this._selected - 1, this._validIndicesCount);
        return this.getSelection();
    }

    public Index getSelection() {
        return this._indexer.getIndex(this._selected).orElseThrow(ArrayIndexOutOfBoundsException::new);
    }

    public void setSelection(final Index selection) {

        if (this._indexer.isIndexValid(selection)) {
            this._selected = this._indexer.getOrdinal(selection);
        } else {
            throw new IllegalArgumentException("The new selected value is not valid");
        }
    }

    //region internals

    private final EnumIndexer<Index> _indexer;
    private final int _validIndicesCount;
    private int _selected;

    //endregion
}

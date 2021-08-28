/*
 *
 * TabularLayoutEngine.java
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

package it.zerono.mods.zerocore.lib.client.gui.layout;

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class TabularLayoutEngine
        extends AbstractLayoutEngine<TabularLayoutEngine> {

    public static final TabularLayoutHint DEFAULT_HINT = new TabularLayoutHint();

    public static Builder builder() {
        return new Builder();
    }

    public static TabularLayoutHint.Builder hintBuilder() {
        return new TabularLayoutHint.Builder();
    }

    @Override
    public void layout(final IControlContainer controlsContainer) {
        new CellLayout(this._columnDefinition, this._rowDefinition, controlsContainer.getBounds()
                    .expand(-this.getHorizontalMargin() * 2, -this.getVerticalMargin() * 2)
                    .offset(this.getHorizontalMargin(), this.getVerticalMargin()))
                .layout(controlsContainer);
    }

    //region Builder

    public static class Builder {

        Builder() {
            this._columnsCount = this._rowsCount = -1;
        }

        public TabularLayoutEngine build() {
            return new TabularLayoutEngine(this._columnDefinition, this._rowDefinition);
        }

        public Builder columns(final int count, int... sizesInPixels) {

            this.validateColumns(count);
            this.validateSizes(count, sizesInPixels);
            this._columnsCount = count;
            this._columnDefinition = buildDefinition(sizesInPixels);
            return this;
        }

        public Builder columns(final int count, double... sizesInPercentage) {

            this.validateColumns(count);
            this.validatePercentages(count, sizesInPercentage);
            this._columnsCount = count;
            this._columnDefinition = buildDefinition(sizesInPercentage);
            return this;
        }

        public Builder columns(final int count) {

            this.validateColumns(count);

            final double[] percentages = new double[count];

            Arrays.fill(percentages, 1.0 / count);

            this._columnsCount = count;
            this._columnDefinition = buildDefinition(percentages);
            return this;
        }

        public Builder rows(final int count, int... sizesInPixels) {

            this.validateRows(count);
            this.validateSizes(count, sizesInPixels);
            this._rowsCount = count;
            this._rowDefinition = buildDefinition(sizesInPixels);
            return this;
        }

        public Builder rows(final int count) {

            this.validateRows(count);

            final double[] percentages = new double[count];

            Arrays.fill(percentages, 1.0 / count);

            this._rowsCount = count;
            this._rowDefinition = buildDefinition(percentages);
            return this;
        }

        public Builder rows(final int count, double... sizesInPercentage) {

            this.validateRows(count);
            this.validatePercentages(count, sizesInPercentage);
            this._rowsCount = count;
            this._rowDefinition = buildDefinition(sizesInPercentage);
            return this;
        }

        //region internals

        private void validateColumns(final int count) {

            if (this._columnsCount > 0) {
                throw new IllegalStateException("Columns were already defined");
            }

            if (count <= 0) {
                throw new IllegalArgumentException("The number of columns must be greater than zero");
            }
        }

        private void validateRows(final int count) {

            if (this._rowsCount > 0) {
                throw new IllegalStateException("Rows were already defined");
            }

            if (count <= 0) {
                throw new IllegalArgumentException("The number of rows must be greater than zero");
            }
        }

        private void validateSizes(final int count, final int[] pixels) {

            if (count != pixels.length) {
                throw new IllegalArgumentException("Invalid number of sizes passed in");
            }

            for (int pixel : pixels) {
                if (pixel <= 0) {
                    throw new IllegalArgumentException("Every size must be greater than zero");
                }
            }
        }

        private void validatePercentages(final int count, final double[] percentages) {

            if (count != percentages.length) {
                throw new IllegalArgumentException("Invalid number of percentages passed in");
            }

            for (double percentage : percentages) {
                if (percentage <= 0.0) {
                    throw new IllegalArgumentException("Every percentage must be greater than zero");
                }
            }
        }

        private static List<IntUnaryOperator> buildDefinition(final int[] pixels) {
            return Arrays.stream(pixels)
                    .mapToObj(TabularLayoutEngine::sizeDefinition)
                    .collect(Collectors.toList());
        }

        private static List<IntUnaryOperator> buildDefinition(final double[] percentages) {
            return Arrays.stream(percentages)
                    .mapToObj(TabularLayoutEngine::sizeDefinition)
                    .collect(Collectors.toList());
        }

        int _columnsCount;
        int _rowsCount;
        List<IntUnaryOperator> _columnDefinition;
        List<IntUnaryOperator> _rowDefinition;

        //endregion
    }

    //endregion
    //region TabularLayoutHint

    private static class TabularLayoutHint
            implements ILayoutEngineHint {

        private TabularLayoutHint() {
            this(1, 1, HorizontalAlignment.Center, VerticalAlignment.Center, Padding.ZERO);
        }

        TabularLayoutHint(final int colSpan, final int rowSpan, final HorizontalAlignment hAlign,
                          final VerticalAlignment vAlign, final Padding padding) {

            this.CellColumnsSpan = colSpan;
            this.CellRowsSpan = rowSpan;
            this.CellHorizontalAlignment = hAlign;
            this.CellVerticalAlignment = vAlign;
            this.CellPadding = padding;
        }

        //region Builder

        public static class Builder {

            Builder() {

                this._columnsSpan = this._rowsSpan = 1;
                this._horizontalAlignment = HorizontalAlignment.Center;
                this._verticalAlignment = VerticalAlignment.Center;
                this._padding = Padding.ZERO;
            }

            public TabularLayoutHint build() {
                return new TabularLayoutHint(this._columnsSpan, this._rowsSpan, this._horizontalAlignment,
                        this._verticalAlignment, this._padding);
            }

            public Builder setColumnsSpan(final int span) {

                this._columnsSpan = span;
                return this;
            }

            public Builder setRowsSpan(final int span) {

                this._rowsSpan = span;
                return this;
            }

            public Builder setHorizontalAlignment(final HorizontalAlignment alignment) {

                this._horizontalAlignment = alignment;
                return this;
            }

            public Builder setVerticalAlignment(final VerticalAlignment alignment) {

                this._verticalAlignment = alignment;
                return this;
            }

            public Builder setPadding(final Padding padding) {

                this._padding = padding;
                return this;
            }

            //region internals

            private int _columnsSpan;
            private int _rowsSpan;
            private HorizontalAlignment _horizontalAlignment;
            private VerticalAlignment _verticalAlignment;
            private Padding _padding;

            //endregion
        }

        //endregion
        //region internals

        private final int CellColumnsSpan;
        private final int CellRowsSpan;
        private final HorizontalAlignment CellHorizontalAlignment;
        private final VerticalAlignment CellVerticalAlignment;
        private final Padding CellPadding;

        //endregion
    }

    //endregion
    //region internals
    //region CellLayout

    private class CellLayout {

        public CellLayout(final List<IntUnaryOperator> columns, final List<IntUnaryOperator> rows,
                          final Rectangle tableBounds) {

            this._columns = columns;
            this._maxColumns = columns.size();
            this._rows = rows;
            this._maxRows = rows.size();
            this._filledCells = new boolean[this._maxRows][this._maxColumns];
            this._tableBounds = tableBounds;
            this._currentColumn = this._currentRow = 0;
            this._cellX = this._tableBounds.getX1();
            this._cellY = this._tableBounds.getY1();
            this._columnSpanApplied = this._rowSpanApplied = 1;
        }

        public void layout(final IControlContainer controlsContainer) {
            for (final IControl control : controlsContainer) {
                if (!this.placeControl(control)) {
                    return;
                }
            }
        }

        //region internals

        /**
         * Place the provided {@link IControl} in the current cell
         * @param control the control
         * @return true if the control was placed, false otherwise
         */
        private boolean placeControl(final IControl control) {

            final TabularLayoutHint hint = control.getLayoutEngineHint()
                    .filter(h -> h instanceof TabularLayoutHint)
                    .map(h -> (TabularLayoutHint)h)
                    .orElse(DEFAULT_HINT);

            int columnSpan = (this._currentColumn + hint.CellColumnsSpan <= this._maxColumns) ? hint.CellColumnsSpan : Math.min(0, this._maxColumns - this._currentColumn);
            int rowSpan = (this._currentRow + hint.CellRowsSpan <= this._maxRows) ? hint.CellRowsSpan : Math.min(0, this._maxRows - this._currentRow);

            while (columnSpan > 1 && this._filledCells[this._currentRow][this._currentColumn + columnSpan - 1]) {
                --columnSpan;
            }

            while (rowSpan > 1 && this._filledCells[this._currentRow + rowSpan - 1][this._currentColumn]) {
                --rowSpan;
            }

            this._columnSpanApplied = columnSpan;
            this._rowSpanApplied = rowSpan;

            final int cellWidth = cellSize(this._currentColumn, columnSpan, this._tableBounds.Width, this._columns);
            final int cellHeight = cellSize(this._currentRow, rowSpan, this._tableBounds.Height, this._rows);
            final int controlMaxWidth = cellWidth - (hint.CellPadding.getLeft() + hint.CellPadding.getRight());
            final int controlMaxHeight = cellHeight - (hint.CellPadding.getTop() + hint.CellPadding.getBottom());
            final int controlWidth = Math.min(controlMaxWidth, getControlDesiredDimension(control, DesiredDimension.Width, controlMaxWidth));
            final int controlHeight = Math.min(controlMaxHeight, getControlDesiredDimension(control, DesiredDimension.Height, controlMaxHeight));

            control.setBounds(new Rectangle(
                    hint.CellHorizontalAlignment.align(this._cellX + hint.CellPadding.getLeft(), controlWidth, controlMaxWidth),
                    hint.CellVerticalAlignment.align(this._cellY + hint.CellPadding.getTop(), controlHeight, controlMaxHeight),
                    controlWidth, controlHeight));

            this.fill(this._columnSpanApplied, this._rowSpanApplied);

            return this.next();
        }

        /**
         * Move to the next empty cell
         * @return true if a cell was selected, false if there is
         */
        private boolean next() {

            do {

                // move to the next cell in the current row

                for (int i = 0; i < this._columnSpanApplied; ++i) {

                    this._cellX += this._columns.get(this._currentColumn).applyAsInt(this._tableBounds.Width);
                    ++this._currentColumn;
                }

                // out of columns?

                if (this._currentColumn >= this._maxColumns) {

                    this._currentColumn = 0;
                    this._cellX = this._tableBounds.getX1();
                    this._cellY += this._rows.get(this._currentRow).applyAsInt(this._tableBounds.Height);
                    ++this._currentRow;

                    if (this._currentRow >= this._maxRows) {

                        // out of rows too!

                        this._currentRow = this._currentColumn = -1;
                        return false;
                    }
                }

            } while (this._filledCells[this._currentRow][this._currentColumn]);

            return true;
        }

        private void fill(final int colSpan, final int rowSpan) {

            this._filledCells[this._currentRow][this._currentColumn] = true;

            if (colSpan > 1) {
                for (int i = 1; i < colSpan; ++i) {
                    this._filledCells[this._currentRow][this._currentColumn + i] = true;
                }
            }

            if (rowSpan > 1) {
                for (int i = 1; i < rowSpan; ++i) {
                    this._filledCells[this._currentRow + i][this._currentColumn] = true;
                }
            }
        }

        private final boolean[][] _filledCells;
        private final int _maxColumns;
        private final int _maxRows;
        private final Rectangle _tableBounds;
        private final List<IntUnaryOperator> _columns;
        private final List<IntUnaryOperator> _rows;
        private int _currentColumn;
        private int _currentRow;
        private int _cellX, _cellY;
        private int _columnSpanApplied;
        private int _rowSpanApplied;

        //endregion
    }

    //endregion

    private static IntUnaryOperator sizeDefinition(final int pixel) {
        return (int $) -> pixel;
    }

    private static IntUnaryOperator sizeDefinition(final double percentage) {
        return (int $) -> (int)($ * percentage);
    }

    private TabularLayoutEngine(final List<IntUnaryOperator> columnDefinition,
                                final List<IntUnaryOperator> rowDefinition) {

        this._columnDefinition = columnDefinition;
        this._rowDefinition = rowDefinition;
    }

    private static int cellSize(final int index, final int span, final int tableSize,
                                final List<IntUnaryOperator> definition) {

        if (1 == span) {
            return definition.get(index).applyAsInt(tableSize);
        }

        int sum = 0;

        for (int i = 0; i < span; ++i) {
            sum += definition.get(index + i).applyAsInt(tableSize);
        }

        return sum;
    }

    final List<IntUnaryOperator> _columnDefinition;
    final List<IntUnaryOperator> _rowDefinition;

    //endregion
}

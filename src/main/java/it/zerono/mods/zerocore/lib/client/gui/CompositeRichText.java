/*
 *
 * CompositeRichText.java
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

package it.zerono.mods.zerocore.lib.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

import java.util.List;
import java.util.function.Supplier;

public class CompositeRichText
    implements IRichText {

    public static Builder builder() {
        return new Builder();
    }

    //region IRichText

    @Override
    public void paint(final PoseStack matrix, int x, int y, final int zLevel) {

        x += this._margin;
        y += this._margin;

        for (int i = 0; i < this._count; ++i) {

            final IRichText text = this._components[i];

            text.paint(matrix, x, y, zLevel);
            y += text.bounds().Height + this._interline;
        }
    }

    @Override
    public Rectangle bounds() {
        return this._bounds;
    }

    @Override
    public boolean isEmpty() {
        return 0 == this._count;
    }

    @Override
    public boolean isNotEmpty() {
        return this._count > 0;
    }

    //endregion
    //region Builder

    public static class Builder {

        public CompositeRichText build() {
            return new CompositeRichText(this._components, this._interline, this._margin);
        }

        public Builder add(final IRichText richText) {

            if (richText.isNotEmpty()) {
                this._components.add(richText);
            }

            return this;
        }

        public Builder add(final Supplier<IRichText> richTextSupplier) {
            return this.add(richTextSupplier.get());
        }

        public Builder interline(final int interline) {

            this._interline = interline;
            return this;
        }

        public Builder margin(final int margin) {

            this._margin = margin;
            return this;
        }

        //region internals

        protected Builder() {

            this._components = Lists.newArrayList();
            this._interline = this._margin = 0;
        }

        private final List<IRichText> _components;
        private int _interline;
        private int _margin;

        //endregion
    }

    //endregion
    //region internals

    private CompositeRichText(final List<IRichText> components, final int interline, final int margin) {

        if (!components.isEmpty()) {

            int width = 0, height = margin * 2 + ((components.size() - 1) * interline);

            for (final IRichText text : components) {

                final Rectangle bounds = text.bounds();

                if (bounds.Width > width) {
                    width = bounds.Width;
                }

                height += bounds.Height;
            }

            width += margin * 2;

            this._components = components.toArray(new IRichText[0]);
            this._count = this._components.length;
            this._bounds = new Rectangle(0, 0, width, height);

        } else {

            this._components = new IRichText[0];
            this._count = 0;
            this._bounds = Rectangle.ZERO;
        }

        this._interline = interline;
        this._margin = margin;
    }

    private final IRichText[] _components;
    private final int _count;
    private final Rectangle _bounds;
    private final int _interline;
    private final int _margin;

    //endregion
}

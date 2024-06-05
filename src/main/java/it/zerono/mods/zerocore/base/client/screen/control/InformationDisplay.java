/*
 *
 * InformationDisplay.java
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

package it.zerono.mods.zerocore.base.client.screen.control;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class InformationDisplay
        extends Table {

    public InformationDisplay(final ModContainerScreen<? extends ModContainer> gui, final String name,
                              final NonNullConsumer<ITableLayoutBuilder> builder) {
        super(gui, name, builder);
    }

    public void addInformationCell(final NonNullConsumer<InformationCellBuilder> builder) {

        final InformationCellBuilder cellBuilder = Util.make(new InformationCellBuilder(), builder::accept);

        this.addCellContent(new InformationCell(this.getGui(), cellBuilder), cellBuilder._layoutBuilder);
    }

    //region InformationCellBuilder

    public static class InformationCellBuilder {

        protected InformationCellBuilder() {

            this._name = "cell";
            this._iconSprite = Sprite.EMPTY_SUPPLIER;
            this._labelTextConfig = $ -> {};
            this._tooltipsLines = ObjectLists.emptyList();
            this._tooltipsObjects = ObjectLists.emptyList();
            this._tooltipsSource = null;
            this._layoutBuilder = $ -> {};
        }

        public InformationCellBuilder name(String name) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty.");

            this._name = name;
            return this;
        }

        public InformationCellBuilder icon(NonNullSupplier<ISprite> sprite) {

            this._iconSprite = Preconditions.checkNotNull(sprite, "Sprite must not be null.");
            return this;
        }

        public InformationCellBuilder tooltips(List<Component> lines) {

            this._tooltipsLines = Preconditions.checkNotNull(lines, "Lines must not be null");
            this._tooltipsObjects = ObjectLists.emptyList();
            this._tooltipsSource = null;
            return this;
        }

        public InformationCellBuilder tooltips(List<Component> lines, List<Object> objects) {

            this._tooltipsLines = Preconditions.checkNotNull(lines, "Lines must not be null");
            this._tooltipsObjects = Preconditions.checkNotNull(objects, "Objects must not be null");
            this._tooltipsSource = null;
            return this;
        }

        public InformationCellBuilder tooltips(BaseScreenToolTipsBuilder builder) {

            Preconditions.checkNotNull(builder, "Builder must not be null.");

            final ImmutablePair<List<Component>, List<Object>> values = builder.build();

            return this.tooltips(values.getLeft(), values.getRight());
        }

        public InformationCellBuilder tooltips(NonNullConsumer<BaseScreenToolTipsBuilder> builder) {

            Preconditions.checkNotNull(builder, "Builder must not be null.");

            return this.tooltips(Util.make(new BaseScreenToolTipsBuilder(), builder::accept));
        }

        public InformationCellBuilder useTooltipsFrom(IControl source) {

            this._tooltipsSource = Preconditions.checkNotNull(source, "Source must not be null");;
            this._tooltipsLines = ObjectLists.emptyList();
            this._tooltipsObjects = ObjectLists.emptyList();
            return this;
        }

        public InformationCellBuilder text(String text) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(text), "Text must not be null or empty.");

            this._labelTextConfig = label -> label.setText(text);
            return this;
        }

        public <V> InformationCellBuilder bindText(IBindableData<V> bindableValue, Function<V, String> textFactory) {

            Preconditions.checkNotNull(bindableValue, "Bindable value must not be null.");
            Preconditions.checkNotNull(textFactory, "Text factory must not be null.");

            this._labelTextConfig = label -> {

                label.setText(textFactory.apply(bindableValue.defaultValue()));
                label.bindText(bindableValue, textFactory);
            };
            return this;
        }

        public InformationCellBuilder layout(final NonNullConsumer<ITableCellLayoutBuilder> layoutBuilder) {

            Preconditions.checkNotNull(layoutBuilder, "Layout builder must not be null.");

            this._layoutBuilder = layoutBuilder;
            return this;
        }

        //region internals

        protected String _name;
        protected NonNullSupplier<ISprite> _iconSprite;
        protected NonNullConsumer<Label> _labelTextConfig;
        protected List<Component> _tooltipsLines;
        protected List<Object> _tooltipsObjects;
        @Nullable
        protected IControl _tooltipsSource;
        protected NonNullConsumer<ITableCellLayoutBuilder> _layoutBuilder;

        //endregion
    }

    //endregion
    //region InformationCell

    private static class InformationCell
            extends AbstractCompositeControl {

        protected InformationCell(final ModContainerScreen<? extends ModContainer> gui,
                                  final InformationCellBuilder builder) {

            super(gui, builder._name);
            this.setPadding(0);

            this._label = new Label(gui, "lbl", "");
            this._label.useTooltipsFrom(this);
            builder._labelTextConfig.accept(this._label);

            this._icon = CommonPanels.icon(gui, builder._iconSprite);
            this._icon.useTooltipsFrom(this);

            this.addChildControl(this._icon, this._label);

            if (null != builder._tooltipsSource) {
                this.useTooltipsFrom(builder._tooltipsSource);
            } else {
                this.setTooltips(builder._tooltipsLines, builder._tooltipsObjects);
            }
        }

        //region AbstractCompositeControl

        @Override
        public void setBounds(final Rectangle bounds) {

            super.setBounds(bounds);
            this._icon.setBounds(new Rectangle(0, 0, 16, 16));
            this._label.setBounds(new Rectangle(16 + 3, 0, bounds.Width - (16 + 3), bounds.Height));
        }

        //endregion
        //region internals

        private final Picture _icon;
        private final Label _label;

        //endregion
    }

    //endregion
}

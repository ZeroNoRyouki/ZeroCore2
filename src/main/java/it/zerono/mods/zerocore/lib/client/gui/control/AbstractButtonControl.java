/*
 *
 * AbstractButtonControl.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteSet;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractButtonControl
        extends AbstractTextualControl {

    public AbstractButtonControl(final ModContainerScreen<? extends ModContainer> gui, final String name, final String text) {

        super(gui, name, text);
        this._iconSet = null;
        this._autoSize = false;
    }

    public boolean getAutoSize() {
        return this._autoSize;
    }

    public void setAutoSize(final boolean autoSize) {
        this._autoSize = autoSize;
    }

    public ISprite getIconForState(final ButtonState state) {

        if (null == this._iconSet) {
            return Sprite.EMPTY;
        }

        return this._iconSet.getOrDefault(state);
    }

    public void setIconForState(final ISprite icon, final ButtonState state) {

        if (null == this._iconSet) {
            this._iconSet = new SpriteSet<>(ButtonState.values());
        }

        this._iconSet.set(state, icon);
    }

    public void setIconForState(final ISprite icon, final ButtonState firstState, final ButtonState secondState,
                                @Nullable final ButtonState... otherStates) {

        this.setIconForState(icon, firstState);
        this.setIconForState(icon, secondState);

        if (null != otherStates && otherStates.length > 0) {

            for (final ButtonState state : otherStates) {
                this.setIconForState(icon, state);
            }
        }
    }

    //region AbstractTextualControl

    @Override
    public int getDesiredDimension(final DesiredDimension dimension) {

        if (this.getAutoSize()) {

            switch (dimension) {

                case Width:
                    return this.getTextWidth() + this.getPadding().getLeft() + this.getPadding().getRight();

//                case Height:
//                    return this.getTextHeight() + this.getPadding().getTop() + this.getPadding().getBottom();
            }
        }

        return super.getDesiredDimension(dimension);
    }

    @Override
    public void onPaint(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        this.getIconFromState(this.getButtonState()).ifPresent(sprite -> this.paintButtonSprite(matrix, sprite));
        super.onPaint(matrix, partialTicks, mouseX, mouseY);
    }

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param clickedButton the mouse button clicked
     * @return the control itself if it accept the keyboard focus, null otherwise
     */
    @Override
    public boolean onMouseClicked(IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        final boolean handled = super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);

        if (handled) {
            this.playClickSound();
        }

        return handled;
    }

    //endregion
    //region derived controls

    /**
     * Retrieve the icon associated with the requested button state
     * <p>
     * If no icon is associated with the state, the default icon will be returned if one is present.
     *
     * @param state the state of the button
     * @return the icon associated with the state if present
     */
    protected Optional<ISprite> getIconFromState(final ButtonState state) {
        return null == this._iconSet ? Optional.empty() : CodeHelper.optionalOr(this._iconSet.get(state),
                () -> this._iconSet.get(ButtonState.Default));
    }

    protected int getIconWidth() {
        return this.getIconFromState(this.getButtonState()).map(ISprite::getWidth).orElse(0);
    }

    protected int getIconHeight() {
        return this.getIconFromState(this.getButtonState()).map(ISprite::getHeight).orElse(0);
    }

    @Override
    protected int getTextOffsetX() {
        return this.getIconWidth();
    }

    protected abstract ButtonState getButtonState();

    protected void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    protected void paintButton3D(final MatrixStack matrix, final ButtonState state, final int x, final int y,
                                 final int width, final int height) {

        final Theme theme = this.getTheme();

        switch (state) {

            case DefaultDisabled:
                this.paintButton3D(matrix, x, y, width, height,
                        theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_DISABLED_3D_GRADIENT_DARK,
                        theme.BUTTON_DISABLED_3D_BORDER_LIGHT,
                        theme.BUTTON_DISABLED_3D_BORDER_DARK);
                break;

            case Active:
            case ActiveHighlighted:
                this.paintButton3D(matrix, x, y, width, height,
                        theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_DARK,
                        theme.BUTTON_ACTIVE_3D_BORDER_LIGHT,
                        theme.BUTTON_ACTIVE_3D_BORDER_DARK);
                break;

            case DefaultHighlighted:
                this.paintButton3D(matrix, x, y, width, height,
                        theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);
                break;

            case Default:
                this.paintButton3D(matrix, x, y, width, height,
                        theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT,
                        theme.BUTTON_NORMAL_3D_GRADIENT_DARK,
                        theme.BUTTON_NORMAL_3D_BORDER_LIGHT,
                        theme.BUTTON_NORMAL_3D_BORDER_DARK);
                break;
        }
    }

    //endregion
    //region internals

    private void paintButtonSprite(final MatrixStack matrix, final ISprite sprite) {

        final Rectangle paddedBounds = this.getPaddingRect();

        this.paintSprite(matrix, sprite, paddedBounds.getX1(), paddedBounds.getY1(),
                Math.min(sprite.getWidth(), paddedBounds.Width),
                Math.min(sprite.getHeight(), paddedBounds.Height));
    }

    private SpriteSet<ButtonState> _iconSet;
    private boolean _autoSize;

    //endregion
}

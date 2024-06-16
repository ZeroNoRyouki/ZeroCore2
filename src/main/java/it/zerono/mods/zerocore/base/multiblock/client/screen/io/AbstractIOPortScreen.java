/*
 *
 * AbstractIOPortScreen.java
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

package it.zerono.mods.zerocore.base.multiblock.client.screen.io;

import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.multiblock.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.control.Label;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.TabularLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.common.util.NonNullSupplier;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractIOPortScreen<Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IIoEntity & MenuProvider,
            C extends ModTileContainer<T>>
        extends AbstractMultiblockScreen<Controller, T, C> {

    protected AbstractIOPortScreen(final C container, final Inventory inventory,
                                   final PlayerInventoryUsage inventoryUsage, final Component title,
                                   final NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {
        this(container, inventory, inventoryUsage, title, DEFAULT_GUI_WIDTH, DEFAULT_GUI_HEIGHT / 2, mainTextureSupplier.get());
    }

    protected AbstractIOPortScreen(final C container, final Inventory inventory,
                                   final PlayerInventoryUsage inventoryUsage, final Component title,
                                   final int guiWidth, final int guiHeight,
                                   final NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {
        this(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTextureSupplier.get());
    }

    protected AbstractIOPortScreen(final C container, final Inventory inventory,
                                   final PlayerInventoryUsage inventoryUsage, final Component title,
                                   final int guiWidth, final int guiHeight, final SpriteTextureMap mainTexture) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTexture);

        this._bindings = new BindingGroup();
        this._subContentPanel = new Panel(this, "subContent");
        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
    }

    protected abstract void setupContent(IControlContainer container);

    protected abstract void setupDirectionButtons(SwitchPictureButton input, SwitchPictureButton output);

    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        // - split the content panel in half and use the top portion for the direction buttons

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setHorizontalMargin(10));

        final Panel directionsPanel = new Panel(this, "directions");

        this.addControl(directionsPanel);
        this.addControl(this._subContentPanel);
        this._subContentPanel.setDesiredDimension(DesiredDimension.Width, this.getGuiWidth());

        // MC calls the init() method (witch rise onScreenCreated()) also when the main windows is resized: clear
        // the controls in the content panel to avoid duplications
        this._subContentPanel.removeControls();

        // - direction buttons

        directionsPanel.setDesiredDimension(DesiredDimension.Width, this.getGuiWidth());
        directionsPanel.setDesiredDimension(DesiredDimension.Height, 18 * 3);
        directionsPanel.setLayoutEngine(TabularLayoutEngine.builder()
                .columns(2)
                .rows(2, 0.3, 0.7)
                .build());

        // -- label

        final Label directionButtonsLabel = new Label(this, "directionButtonsLabel", Component.translatable("gui.zerocore.base.screen.ioport.mode"));

        directionButtonsLabel.setLayoutEngineHint(TabularLayoutEngine.hintBuilder()
                .setColumnsSpan(2)
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Center)
                .setPadding(Padding.get(5, 5, 5, 5))
                .build());
        directionsPanel.addControl(directionButtonsLabel);

        // -- input direction button
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, BaseIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, BaseIcons.ButtonInputDirectionActive);
        this._btnInputDirection.setDesiredDimension(18, 18);
        this._btnInputDirection.setLayoutEngineHint(TabularLayoutEngine.hintBuilder()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setVerticalAlignment(VerticalAlignment.Center)
                .build());
        this._btnInputDirection.setBackground(BaseIcons.ImageButtonBackground.get());
        this._btnInputDirection.setPadding(1);
        this._btnInputDirection.Activated.subscribe(this::onInputActivated);

        // -- output direction button
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, BaseIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, BaseIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.setDesiredDimension(18, 18);
        this._btnOutputDirection.setLayoutEngineHint(TabularLayoutEngine.hintBuilder()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setVerticalAlignment(VerticalAlignment.Center)
                .build());
        this._btnOutputDirection.setBackground(BaseIcons.ImageButtonBackground.get());
        this._btnOutputDirection.setPadding(1);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);

        //noinspection Convert2MethodRef
        this.addBinding(t -> t.getIoDirection(), value -> {

            this._btnInputDirection.setActive(value.isInput());
            this._btnOutputDirection.setActive(value.isOutput());
        });

        directionsPanel.addControl(this._btnInputDirection);
        directionsPanel.addControl(this._btnOutputDirection);

        this.setupDirectionButtons(this._btnInputDirection, this._btnOutputDirection);
        this.setupContent(this._subContentPanel);
    }

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();
        this._bindings.update();;
    }

    //endregion
    //region internals

    private <Value> void addBinding(final Function<T, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getTileEntity(), supplier, consumer));
    }

    private void onInputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_INPUT);
    }

    private void onOutputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_OUTPUT);
    }

    private final BindingGroup _bindings;

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;
    private final Panel _subContentPanel;

    //endregion
}

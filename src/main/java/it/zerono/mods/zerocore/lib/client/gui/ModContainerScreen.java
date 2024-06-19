/*
 *
 * ModContainerScreen.java
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
import com.mojang.blaze3d.platform.InputConstants;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.control.HelpButton;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.IBinding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.event.ConcurrentEvent;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"WeakerAccess", "unused"})
@OnlyIn(Dist.CLIENT)
public class ModContainerScreen<C extends ModContainer>
        extends AbstractContainerScreen<C> {

    public final IEvent<Runnable> Create;
    public final IEvent<Runnable> Close;
    public final IEvent<Runnable> Created;
    public final IEvent<Runnable> DataUpdated;

    protected ModContainerScreen(final C container, final Inventory inventory, final Component title,
                                 final int guiWidth, final int guiHeight) {
        this(container, inventory, title, guiWidth, guiHeight, true);
    }

    protected ModContainerScreen(final C container, final Inventory inventory, final Component title,
                                 final int guiWidth, final int guiHeight, boolean singleWindow) {

        super(container, inventory, title);
        this.imageWidth = guiWidth;
        this.imageHeight = guiHeight;
        this._tickHandlers = Lists.newArrayListWithCapacity(2);
        this._deferred = Lists.newLinkedList();
        this._originalMouseX = this._originalMouseY = 0;
        this._ignoreCloseOnInventoryKey = false;
        this._nextBogusId = 0;
        this._theme = Theme.DEFAULT;
        this._keepScreenAlive = false;

        this.Create = new Event<>();
        this.Close = new ConcurrentEvent<>();
        this.Created = new Event<>();
        this.DataUpdated = new Event<>();

        this._raiseContainerDataUpdatedHandler = container.subscribeContainerDataUpdate(this::raiseDataUpdated);

        // always create the windows manger after the events are in place so it can subscribe them
        this._windowsManager = singleWindow ? new WindowsManagerSingleWindow<>(this) : new WindowsManagerMultiWindow<>(this);
    }

    protected void close() {
        if (null != Minecraft.getInstance().player) {
            Minecraft.getInstance().player.closeContainer();
        }
    }

    protected boolean isDataUpdateInProgress() {
        return this._dataUpdateInProgress;
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    protected void onScreenCreate() {
    }

    /**
     * Called when this screen has being created.
     * Override to handle this event
     */
    protected void onScreenCreated() {
        // force an update when the screen is fully created
        this.raiseDataUpdated();
    }

    /**
     * Called when this screen was closed.
     * Override to handle this event
     */
    protected void onScreenClose() {
        this.getMenu().unsubscribeContainerDataUpdate(this._raiseContainerDataUpdatedHandler);
    }

    /**
     * Called when this screen need to be updated after a data update.
     * Override to handle this event
     */
    protected void onDataUpdated() {
    }

    /**
     * Run the control validators
     *
     * @return true if the validation process is successful, false otherwise
     */
    public boolean isValid() {

        final List<Component> errors = Lists.newArrayList();

        this._windowsManager.validate(errors::add);

        if (!errors.isEmpty()) {

            ZeroCore.getProxy().displayErrorToPlayer(null, errors);
            return false;
        }

        return true;
    }

    public void requestTickUpdates(final Runnable handler) {
        this._tickHandlers.add(handler);
    }

    public int getGuiX() {
        return this.leftPos;
    }

    public int getGuiY() {
        return this.topPos;
    }

    public int getGuiWidth() {
        return this.imageWidth;
    }

    public int getGuiHeight() {
        return this.imageHeight;
    }

    public int getMinecraftWindowWidth() {
        return this.getMinecraft().getWindow().getWidth();
    }

    public int getMinecraftWindowHeight() {
        return this.getMinecraft().getWindow().getHeight();
    }

    public float getZLevel() {
        return 0;
    }

    public double getGuiScaleFactor() {
        return this.getMinecraft().getWindow().getGuiScale();
    }

    public int getClippedMouseX() {
        return Mth.ceil(GuiHelper.getMouse().xpos() - this.getGuiX());
    }

    public int getClippedMouseY() {
        return Mth.ceil(GuiHelper.getMouse().ypos() - this.getGuiY());
    }

    public void renderHoveredSlotToolTip(final GuiGraphics gfx) {
        this.renderTooltip(gfx, this.getOriginalMouseX(), this.getOriginalMouseY());
    }

    public int getTooltipsPopupMaxWidth() {
        return RichText.NO_MAX_WIDTH;
    }

    public static int parseTooltipsPopupMaxWidthFromLang(final String langKey, final int defaultValue) {

        final Component text = Component.translatable(langKey);

        try {

            final int width = Integer.parseInt(text.getString());

            return width > 0 ? width : defaultValue;

        } catch (NumberFormatException ex) {
            Log.LOGGER.error(Log.GUI, "Invalid integer value from lang file: {}", langKey);
        }

        return defaultValue;
    }

    public int getOriginalMouseX() {
        return this._originalMouseX;
    }

    public int getOriginalMouseY() {
        return this._originalMouseY;
    }

    public Rectangle getScreenRect() {
        return new Rectangle(0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight());
    }

    public Rectangle getScreenRectPadded() {
        return this.getScreenRect().inset(5, 5);
    }

    public int nextGenericId() {
        return this._nextBogusId++;
    }

    public String nextGenericName() {
        return Integer.toString(this.nextGenericId());
    }

    public void enqueueTask(final Runnable runnable) {
        this._deferred.add(runnable);
    }

    protected IWindow createWindow(final IControlContainer rootContainer, boolean modalWindow) {
        return this._windowsManager.createWindow(rootContainer, modalWindow, this.getGuiX(), this.getGuiY(),
                this.getGuiWidth(), this.getGuiHeight());
    }

    protected IWindow createWindow(final IControlContainer rootContainer, boolean modalWindow,
                                final int x, final int y, final int width, final int height) {
        return this._windowsManager.createWindow(rootContainer, modalWindow, x, y, width, height);
    }

    protected void ignoreCloseOnInventoryKey(boolean ignore) {
        this._ignoreCloseOnInventoryKey = ignore;
    }

    protected boolean ignoreCloseOnInventoryKey() {
        return this._ignoreCloseOnInventoryKey;
    }

    protected IControl createPatchouliHelpButton(final ResourceLocation bookId, final ResourceLocation entryId, final int pageNum) {
        return HelpButton.patchouli(this, "helpPatchouli", bookId, entryId, pageNum);
    }

    protected IControl createRecipesButton(Runnable onClick, String tooltipTranslationKey) {
        return HelpButton.jeiRecipes(this, "recipes", tooltipTranslationKey, () -> {

            this._keepScreenAlive = true;
            onClick.run();
        });
    }

    public Theme getTheme() {
        return this._theme;
    }

    public void setTheme(final Theme theme) {

        this._theme = Objects.requireNonNull(theme);
        this._windowsManager.onThemeChanged(theme);
    }

    public Font getFont() {
        return this.font;
    }

    //region slot groups

    protected SlotsGroup createMonoSlotGroupControl(final String controlName, final String inventorySlotsGroupName,
                                                    final ISprite backgroundSprite, final int borderSize) {

        final SlotsGroup control = new SlotsGroup(this, controlName, inventorySlotsGroupName,
                backgroundSprite.getWidth(), backgroundSprite.getHeight(), backgroundSprite);

        control.setPadding(borderSize);
        return control;
    }

    protected SlotsGroup createPlayerHotBarSlotsGroupControl(final ISprite backgroundSprite, final int borderSize) {

        final SlotsGroup control = new SlotsGroup(this, "hotbar", ModContainer.SLOTGROUPNAME_PLAYER_HOTBAR,
                backgroundSprite.getWidth(), backgroundSprite.getHeight(), backgroundSprite);

        control.setPadding(borderSize);
        return control;
    }

    protected SlotsGroup createPlayerInventorySlotsGroupControl(final ISprite backgroundSprite, final int borderSize) {

        final SlotsGroup control = new SlotsGroup(this, "playerinv", ModContainer.SLOTGROUPNAME_PLAYER_INVENTORY,
                backgroundSprite.getWidth(), backgroundSprite.getHeight(), backgroundSprite);

        control.setPadding(borderSize);
        return control;
    }

    //endregion
    //region bindings

    public <Value> void addDataBinding(final Supplier<Value> supplier, final Consumer<Value> consumer) {
        this.addDataBinding(IBinding.from(supplier, consumer));
    }

    @SafeVarargs
    public final <Value> void addDataBinding(final Supplier<Value> supplier, final Consumer<Value>... consumers) {
        this.addDataBinding(IBinding.from(supplier, consumers));
    }

    private void addDataBinding(final IBinding binding) {

        if (null == this._bindings) {
            this._bindings = new BindingGroup();
        }

        this._bindings.addBinding(binding);
    }

    //endregion
    //region ContainerScreen
    
    @Override
    public final void init() {

        super.init();
        this.raiseScreenCreate();
        this.raiseScreenCreated();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void added() {

        super.added();
        Minecraft.getInstance().tell(() -> this.getMenu().onScreenOpened());
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public final void removed() {

        if (this._keepScreenAlive) {

            this._keepScreenAlive = false;
            // do not run cleanup if this screen was replaced by a temporary one (like JEI recipes GUI)
            return;
        }

        super.removed();
        this.raiseScreenClose();
        this.Create.unsubscribeAll();
        this.Created.unsubscribeAll();
        this.Close.unsubscribeAll();
        CodeHelper.clearErrorReport();
    }

    @Override
    public final void containerTick() {

        super.containerTick();
        this._windowsManager.onGuiContainerTick();
        this._tickHandlers.forEach(Runnable::run);

        if (!this._deferred.isEmpty()) {

            this._deferred.forEach(Runnable::run);
            this._deferred.clear();
        }
    }

    @Override
    public final void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {

        super.renderBackground(gfx, mouseX, mouseY, partialTicks);

        this._originalMouseX = mouseX;
        this._originalMouseY = mouseY;
        this._windowsManager.onGuiContainerPaintBackground(gfx, partialTicks);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected final void renderLabels(final GuiGraphics gfx, final int mouseX, final int mouseY) {

        this._originalMouseX = mouseX;
        this._originalMouseY = mouseY;
        this._windowsManager.onGuiContainerPaintForeground(gfx);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    public final boolean mouseClicked(final double mouseX, final double mouseY, int mouseButton) {

        this._originalMouseX = (int)mouseX;
        this._originalMouseY = (int)mouseY;

        return this._windowsManager.onGuiContainerMouseClicked(mouseX, mouseY, mouseButton) ||
                super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    @Override
    public final boolean mouseReleased(final double mouseX, final double mouseY, int mouseButton) {

        this._originalMouseX = (int)mouseX;
        this._originalMouseY = (int)mouseY;

        return this._windowsManager.onGuiContainerMouseReleased(mouseX, mouseY, mouseButton) ||
                super.mouseReleased(mouseX, mouseY, mouseButton);
    }
/*
    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }
    */

    /**
     * Called when the mouse cursor is moved.
     */
    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {

        super.mouseMoved(mouseX, mouseY);
        this._originalMouseX = (int)mouseX;
        this._originalMouseY = (int)mouseY;
        this._windowsManager.onGuiContainerMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {

        this._originalMouseX = (int)mouseX;
        this._originalMouseY = (int)mouseY;
        return this._windowsManager.onGuiContainerMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY) ||
                super.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    @Override
    public final boolean charTyped(final char typedChar, final int keyCode) {
        return this._windowsManager.onGuiContainerCharTyped(typedChar, keyCode) ||
                super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return this._windowsManager.onGuiContainerKeyPressed(keyCode, scanCode, modifiers) ||
                this.checkIgnoreCloseOnInventoryKey(keyCode, scanCode) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean checkIgnoreCloseOnInventoryKey(final int keyCode, final int scanCode) {
        return this.ignoreCloseOnInventoryKey() &&
                this.getMinecraft().options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return this._windowsManager.onGuiContainerKeyReleased(keyCode, scanCode, modifiers) ||
                super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean isHovering(final int regionX, final int regionY, final int regionWidth, final int regionHeight,
                                      double pointX, double pointY) {

        pointX -= this.getGuiLeft();
        pointY -= this.getGuiTop();

        return pointX >= (double) (regionX - 1) && pointX < (double) (regionX + regionWidth + 1) &&
                pointY >= (double) (regionY - 1) && pointY < (double) (regionY + regionHeight + 1);
    }

    //endregion
    //region internals

    private void raiseScreenCreate() {

        this.Create.raise(Runnable::run);
        this.onScreenCreate();
    }

    private void raiseScreenCreated() {

        this.onScreenCreated();
        this.Created.raise(Runnable::run);
    }

    private void raiseScreenClose() {

        this.Close.raise(Runnable::run);
        this.onScreenClose();

        if (null != this._bindings) {
            this._bindings.close();
        }
    }

    protected void raiseDataUpdated() {

        this._dataUpdateInProgress = true;

        if (null != this._bindings) {
            this._bindings.update();
        }

        this.onDataUpdated();
        this.DataUpdated.raise(Runnable::run);
        this._dataUpdateInProgress = false;
    }

    private final AbstractWindowsManager<C> _windowsManager;
    private final List<Runnable> _tickHandlers;
    private final Queue<Runnable> _deferred;
    private final Runnable _raiseContainerDataUpdatedHandler;
    private boolean _dataUpdateInProgress;
    private int _originalMouseX;
    private int _originalMouseY;
    private boolean _ignoreCloseOnInventoryKey;
    private int _nextBogusId;
    private BindingGroup _bindings;
    private Theme _theme;
    private boolean _keepScreenAlive;

    //endregion
}

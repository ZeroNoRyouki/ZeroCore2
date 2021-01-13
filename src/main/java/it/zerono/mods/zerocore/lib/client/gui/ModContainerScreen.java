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
import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.control.HelpButton;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.event.ConcurrentEvent;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;
import java.util.Queue;

@SuppressWarnings({"WeakerAccess", "unused"})
@OnlyIn(Dist.CLIENT)
public class ModContainerScreen<C extends ModContainer>
        extends ContainerScreen<C> {

    public final IEvent<Runnable> Create;
    public final IEvent<Runnable> Close;
    public final IEvent<Runnable> Created;

    protected ModContainerScreen(final C container, final PlayerInventory inventory, final ITextComponent title,
                                 final int guiWidth, final int guiHeight) {
        this(container, inventory, title, guiWidth, guiHeight, true);
    }

    protected ModContainerScreen(final C container, final PlayerInventory inventory, final ITextComponent title,
                                 final int guiWidth, final int guiHeight, boolean singleWindow) {

        super(container, inventory, title);
        this.xSize = guiWidth;
        this.ySize = guiHeight;
        this._tickHandlers = Lists.newArrayListWithCapacity(2);
        this._deferred = Lists.newLinkedList();
        this._originalMouseX = this._originalMouseY = 0;
        this._ignoreCloseOnInventoryKey = false;
        this._nextBogusId = 0;

        this.Create = new Event<>();
        this.Close = new ConcurrentEvent<>();
        this.Created = new Event<>();

        // always create the windows manger after the events are in place so it can subscribe them
        this._windowsManager = singleWindow ? new WindowsManagerSingleWindow<>(this) : new WindowsManagerMultiWindow<>(this);
    }

    protected void close() {
        if (null != Minecraft.getInstance().player) {
            Minecraft.getInstance().player.closeScreen();
        }
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
    }

    /**
     * Called when this screen was closed.
     * Override to handle this event
     */
    protected void onScreenClose() {
    }

    /**
     * Run the control validators
     *
     * @return true if the validation process is successful, false otherwise
     */
    public boolean isValid() {

        final List<ITextComponent> errors = Lists.newArrayList();

        this._windowsManager.validate(errors::add);

        if (!errors.isEmpty()) {

            CodeHelper.reportErrorToPlayer(Objects.requireNonNull(this.getMinecraft().player), null, errors);
            return false;
        }

        return true;
    }

    public void requestTickUpdates(final Runnable handler) {
        this._tickHandlers.add(handler);
    }

    public int getGuiX() {
        return this.guiLeft;
    }

    public int getGuiY() {
        return this.guiTop;
    }

    public int getGuiWidth() {
        return this.xSize;
    }

    public int getGuiHeight() {
        return this.ySize;
    }

    public int getMinecraftWindowWidth() {
        return this.getMinecraft().getMainWindow().getFramebufferWidth();
    }

    public int getMinecraftWindowHeight() {
        return this.getMinecraft().getMainWindow().getFramebufferHeight();
    }

    public float getZLevel() {
        return 0;
    }

    public double getGuiScaleFactor() {
        return this.getMinecraft().getMainWindow().getGuiScaleFactor();
    }

    public int getClippedMouseX() {
        return MathHelper.ceil(GuiHelper.getMouse().getMouseX() - this.getGuiX());
    }

    public int getClippedMouseY() {
        return MathHelper.ceil(GuiHelper.getMouse().getMouseY() - this.getGuiY());
    }

    public void renderHoveredSlotToolTip(final MatrixStack matrix) {
        this.renderHoveredTooltip(matrix, this.getOriginalMouseX(), this.getOriginalMouseY());
    }

    public int getOriginalMouseX() {
        return this._originalMouseX;
    }

    public int getOriginalMouseY() {
        return this._originalMouseY;
    }

    public Rectangle getScreenRect() {
        return new Rectangle(0, 0, Minecraft.getInstance().getMainWindow().getScaledWidth(),
                Minecraft.getInstance().getMainWindow().getScaledHeight());
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

        final IControl help = HelpButton.patchouli(this, "helpPatchouli", bookId, entryId, pageNum);

        help.setLayoutEngineHint(FixedLayoutEngine.hint(this.getGuiWidth() - 30, this.getGuiHeight() - 41));
        return help;
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

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public final void onClose() {

        super.onClose();
        GuiHelper.getKeyboard().enableRepeatEvents(false);
        this.raiseScreenClose();
        this.Create.unsubscribeAll();
        this.Created.unsubscribeAll();
        this.Close.unsubscribeAll();
        CodeHelper.clearErrorReport();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public final void tick() {

        super.tick();
        this._windowsManager.onGuiContainerTick();
        this._tickHandlers.forEach(Runnable::run);

        if (!this._deferred.isEmpty()) {

            this._deferred.forEach(Runnable::run);
            this._deferred.clear();
        }
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected final void drawGuiContainerBackgroundLayer(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        this.renderBackground(matrix);

        this._originalMouseX = mouseX;
        this._originalMouseY = mouseY;
        this._windowsManager.onGuiContainerPaintBackground(matrix, partialTicks);
    }

    @Override
    protected final void drawGuiContainerForegroundLayer(final MatrixStack matrix, final int mouseX, final int mouseY) {

        this._originalMouseX = mouseX;
        this._originalMouseY = mouseY;
        this._windowsManager.onGuiContainerPaintForeground(matrix);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {

        this._originalMouseX = (int)mouseX;
        this._originalMouseY = (int)mouseY;
        return this._windowsManager.onGuiContainerMouseScrolled(mouseX, mouseY, scrollDelta) ||
                super.mouseScrolled(mouseX, mouseY, scrollDelta);
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
                this.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(InputMappings.getInputByCode(keyCode, scanCode));
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return this._windowsManager.onGuiContainerKeyReleased(keyCode, scanCode, modifiers) ||
                super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean isPointInRegion(final int regionX, final int regionY, final int regionWidth, final int regionHeight,
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
    }

    private final AbstractWindowsManager<C> _windowsManager;
    private final List<Runnable> _tickHandlers;
    private final Queue<Runnable> _deferred;
    private int _originalMouseX;
    private int _originalMouseY;
    private boolean _ignoreCloseOnInventoryKey;
    private int _nextBogusId;

    //endregion
}

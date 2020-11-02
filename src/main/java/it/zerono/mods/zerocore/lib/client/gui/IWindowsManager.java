/*
 *
 * IWindowsManager.java
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

import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface IWindowsManager<C extends ModContainer> {

    ModContainerScreen<C> getGuiScreen();

    IWindow createWindow(IControlContainer rootContainer, boolean modalWindow,
                         int x, int y, int width, int height);

    double getMouseWheelMovement();

    void setFocus(@Nullable IWindow wnd, @Nullable IControl newFocus);

    void captureMouse(IWindow wnd, IControl target);

    void releaseMouse();

    boolean isMouseCaptured();

    void startDragging(IDraggable draggable, IDragSource source);

    boolean isDragging();

    void hideWindow(IWindow wnd);

    void showWindow(IWindow wnd);

    boolean isWindowVisible(IWindow wnd);

    float getPaintPartialTicks();
}

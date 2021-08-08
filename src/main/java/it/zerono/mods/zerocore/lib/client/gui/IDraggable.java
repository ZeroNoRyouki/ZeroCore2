/*
 *
 * IDraggable.java
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

import com.mojang.blaze3d.vertex.PoseStack;

public interface IDraggable {

    int getWidth();

    int getHeight();

    enum PaintState {

        Default,
        Highlighted,
        Dragging
    }

    /**
     * Paint the draggable
     *
     * @param x the x coordinate, in screen coordinates
     * @param y the y coordinate, in screen coordinates
     * @param zLevel
     * @param paintState
     */
    void onPaint(PoseStack matrix, int x, int y, float zLevel, PaintState paintState);
}

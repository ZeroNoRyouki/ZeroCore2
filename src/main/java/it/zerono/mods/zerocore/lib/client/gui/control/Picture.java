/*
 *
 * Picture.java
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

import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Picture
        extends AbstractControl {

    public Picture(final ModContainerScreen<? extends ModContainer> gui, final String name, final Supplier<@NotNull ISprite> picture) {
        this(gui, name, picture.get());
    }

    public Picture(final ModContainerScreen<? extends ModContainer> gui, final String name,
                   final Supplier<@NotNull ISprite> picture, final int width, final int height) {
        this(gui, name, picture.get(), width, height);
    }

    public Picture(final ModContainerScreen<? extends ModContainer> gui, final String name, final ISprite picture) {
        this(gui, name, picture, picture.getWidth(), picture.getHeight());
    }

    public Picture(final ModContainerScreen<? extends ModContainer> gui, final String name, final ISprite picture,
                   final int width, final int height) {

        super(gui, name);
        this.setBackground(picture);
        this.setDesiredDimension(width, height);
    }
}

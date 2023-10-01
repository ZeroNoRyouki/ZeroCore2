/*
 *
 * IControlContainer.java
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

import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.validator.IControlValidator;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A control that contains other controls
 */
@SuppressWarnings("unused")
public interface IControlContainer
        extends IControl, Iterable<IControl> {

    void addControl(IControl control);

    void addControl(IControl... controls);

    void removeControl(IControl control);

    void removeControls();

    int getControlsCount();

    ILayoutEngine getLayoutEngine();

    void setLayoutEngine(ILayoutEngine engine);

    void setValidator(IControlValidator validator);

    void validate(Consumer<ITextComponent> errorReport);

    default Stream<IControl> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}

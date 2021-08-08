/*
 *
 * IDebugMessages.java
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

package it.zerono.mods.zerocore.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.BiConsumer;

public interface IDebugMessages {

    /**
     * Add an unlocalized text message to the messages list
     *
     * @param text the unlocalized text to add
     */
    default void addUnlocalized(String text) {
        this.add(new TextComponent(text));
    }

    /**
     * Add an unlocalized text message to the messages list
     *
     * @param formatString the format string to be used to compose the unlocalized text
     * @param parameters   the parameters to be used to compose the unlocalized text
     */
    default void addUnlocalized(String formatString, Object... parameters) {

        if (parameters.length > 0) {
            this.addUnlocalized(String.format(formatString, parameters));
        } else {
            this.addUnlocalized(formatString);
        }
    }

    /**
     * Add a message to the messages list
     *
     * @param message the language resource key of the message to add
     */
    void add(Component message);

    /**
     * Add a message to the messages list using a localized format string
     *
     * @param messageFormatStringResourceKey the language resource key of a format string to use to create the message
     * @param messageParameters              the values to insert in the message
     */
    void add(String messageFormatStringResourceKey, Object... messageParameters);

    /**
     * Add messages from another IDebuggable to this messages list
     * <p>
     * If the provided IDebuggable provide only one message, the message will be added at the same level of the other
     * messages in this message list. If it provide more than one message, they will be added as nested messages
     *
     * @param side the LogicalSide of the caller
     * @param debuggable the other IDebuggable to query for messages
     * @param label      the unlocalized label for the other IDebuggable messages
     */
    default void add(LogicalSide side, IDebuggable debuggable, String label) {
        this.add(side, debuggable, new TextComponent(label));
    }

    /**
     * Add messages from another IDebuggable to this messages list
     * <p>
     * If the provided IDebuggable provide only one message, the message will be added at the same level of the other
     * messages in this message list. If it provide more than one message, they will be added as nested messages
     *
     * @param side the LogicalSide of the caller
     * @param debuggable the other IDebuggable to query for messages
     * @param label      the language resource key of the message to add as a label for the other IDebuggable messages
     */
    void add(LogicalSide side, IDebuggable debuggable, Component label);

    /**
     * Add messages from another IDebuggable to this messages list
     * <p>
     * If the provided IDebuggable provide only one message, the message will be added at the same level of the other
     * messages in this message list. If it provide more than one message, they will be added as nested messages
     *
     * @param side the LogicalSide of the caller
     * @param debuggable                   the other IDebuggable to query for messages
     * @param labelFormatStringResourceKey the language resource key of a format string to use to create the label
     *                                     for the other IDebuggable messages
     * @param labelParameters              the values to insert in the label
     */
    void add(LogicalSide side, IDebuggable debuggable, String labelFormatStringResourceKey, Object... labelParameters);

    default <T> void add(T debuggee, BiConsumer<IDebugMessages, T> consumer, String label) {
        this.add(debuggee, consumer, new TextComponent(label));
    }

    <T> void add(T debuggee, BiConsumer<IDebugMessages, T> consumer, Component label);

    <T> void add(T debuggee, BiConsumer<IDebugMessages, T> consumer, String labelFormatStringResourceKey, Object... labelParameters);
}

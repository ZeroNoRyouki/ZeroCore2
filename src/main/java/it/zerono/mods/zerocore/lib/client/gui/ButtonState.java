/*
 *
 * ButtonState.java
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

public enum ButtonState {

    Default,            // the default state of the button (not clicked / selected / disabled / etc
    Active,             // the button is clicked / selected / pressed
    DefaultHighlighted, // the mouse is over the a button that is not active
    ActiveHighlighted,  // the mouse is over the an active button
    DefaultDisabled,    // the button is disabled and can't receive input
    ActiveDisabled,     // the button is active but disabled
    ;

    public ButtonState getStandard() {

        switch (this) {

            case Default:
            case DefaultHighlighted:
            case DefaultDisabled:
                return Default;

            case Active:
            case ActiveHighlighted:
            case ActiveDisabled:
                return Active;
        }

        throw new IllegalArgumentException();
    }

    public ButtonState getHighlighted() {

        switch (this) {

            case Default:
            case DefaultHighlighted:
            case DefaultDisabled:
                return DefaultHighlighted;

            case Active:
            case ActiveHighlighted:
            case ActiveDisabled:
                return ActiveHighlighted;
        }

        throw new IllegalArgumentException();
    }

    public ButtonState getDisabled() {

        switch (this) {

            case Default:
            case DefaultHighlighted:
            case DefaultDisabled:
                return DefaultDisabled;

            case Active:
            case ActiveHighlighted:
            case ActiveDisabled:
                return ActiveDisabled;
        }

        throw new IllegalArgumentException();
    }
}

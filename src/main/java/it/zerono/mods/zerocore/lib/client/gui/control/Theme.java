/*
 *
 * Theme.java
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

import it.zerono.mods.zerocore.lib.data.gfx.Colour;

final class Theme {

    static final Colour FLAT_BACKGROUND_COLOR = new Colour(0x86, 0x86, 0x86, 0xFF); /*ok*/

    static final Colour TEXT_ENABLED_COLOR = new Colour(0x30, 0x30, 0x30, 0xFF);
    static final Colour TEXT_DISABLED_COLOR = new Colour(0xA0, 0xA0, 0xA0, 0xFF);

    static final Colour DARK_OUTLINE_COLOR = new Colour(0.0, 0.0, 0.0, 0.7);

    static final Colour STANDARD_3D_GRADIENT_LIGHT = new Colour(0x90, 0x90, 0x90, 0xFF); /*ok*/
    static final Colour STANDARD_3D_GRADIENT_DARK = new Colour(0x20, 0x20, 0x20, 0xFF); /*ok*/

    static final Colour BUTTON_NORMAL_3D_GRADIENT_LIGHT = Theme.STANDARD_3D_GRADIENT_LIGHT;
    static final Colour BUTTON_NORMAL_3D_GRADIENT_DARK = Theme.STANDARD_3D_GRADIENT_DARK;
    static final Colour BUTTON_NORMAL_3D_BORDER_LIGHT = new Colour(0xDD, 0xDD, 0xDD, 0xFF); /*ok*/
    static final Colour BUTTON_NORMAL_3D_BORDER_DARK = new Colour(0x77, 0x77, 0x77, 0xEE); /*ok*/

    static final Colour BUTTON_DISABLED_3D_GRADIENT_LIGHT = Theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT;
    static final Colour BUTTON_DISABLED_3D_GRADIENT_DARK = Theme.BUTTON_NORMAL_3D_GRADIENT_DARK;
    static final Colour BUTTON_DISABLED_3D_BORDER_LIGHT = Theme.BUTTON_NORMAL_3D_BORDER_LIGHT;
    static final Colour BUTTON_DISABLED_3D_BORDER_DARK = Theme.BUTTON_NORMAL_3D_BORDER_DARK;

    static final Colour BUTTON_ACTIVE_3D_GRADIENT_LIGHT = new Colour(0xA7, 0x99, 0xAE, 0xFF); /*ok*/
    static final Colour BUTTON_ACTIVE_3D_GRADIENT_DARK = new Colour(0x29, 0x24, 0x2E, 0xFF); /*ok*/
    static final Colour BUTTON_ACTIVE_3D_BORDER_LIGHT = new Colour(0x77, 0x77, 0x77, 0xEE); /*ok*/
    static final Colour BUTTON_ACTIVE_3D_BORDER_DARK = new Colour(0xDD, 0xDD, 0xDD, 0xFF); /*ok*/

    static final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT = new Colour(0xA7, 0x99, 0xAE, 0xFF); /*ok*/
    static final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK = new Colour(0x29, 0x24, 0x2E, 0xFF); /*ok*/
    static final Colour BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT = new Colour(0x77, 0x77, 0x77, 0xEE); /*ok*/
    static final Colour BUTTON_HIGHLIGHTED_3D_BORDER_DARK = new Colour(0xDD, 0xDD, 0xDD, 0xFF); /*ok*/

    static final Colour TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT = Theme.STANDARD_3D_GRADIENT_LIGHT;
    static final Colour TEXTFIELD_NORMAL_3D_GRADIENT_DARK = Theme.STANDARD_3D_GRADIENT_DARK;
    static final Colour TEXTFIELD_NORMAL_3D_BORDER_LIGHT = Colour.WHITE;
    static final Colour TEXTFIELD_NORMAL_3D_BORDER_DARK =  new Colour(0x2B, 0x2B, 0x2B, 0xFF);
    static final Colour TEXTFIELD_CARET = new Colour(0x00, 0x00, 0x00, 0x6F);

    private Theme(){
    }
}

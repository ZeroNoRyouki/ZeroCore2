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

package it.zerono.mods.zerocore.lib.client.gui;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import net.minecraft.resources.IResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Theme {

    /**
     * The default theme.
     */
    public static final Theme DEFAULT = new Theme();

    /**
     * Background of a control.
     */
    public final Colour FLAT_BACKGROUND_COLOR;

    /**
     * Text displayed in an enabled control.
     */
    public final Colour TEXT_ENABLED_COLOR;

    /**
     * Text displayed in a disabled control.
     */
    public final Colour TEXT_DISABLED_COLOR;

    /**
     * Dark border / outline of a control.
     */
    public final Colour DARK_OUTLINE_COLOR;

    /**
     * Background of an enabled button, starting colour of a gradient.
     * If equal to BUTTON_NORMAL_3D_GRADIENT_DARK, only BUTTON_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_NORMAL_3D_GRADIENT_LIGHT;

    /**
     * Background of an enabled button, ending colour of a gradient.
     * If equal to BUTTON_NORMAL_3D_GRADIENT_DARK, only BUTTON_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_NORMAL_3D_GRADIENT_DARK;

    /**
     * 3D border of an enabled button, light sections.
     */
    public final Colour BUTTON_NORMAL_3D_BORDER_LIGHT;

    /**
     * 3D border of an enabled button, dark sections.
     */
    public final Colour BUTTON_NORMAL_3D_BORDER_DARK;

    /**
     * Background of a disabled button, starting colour of a gradient.
     * If equal to BUTTON_DISABLED_3D_GRADIENT_DARK, only BUTTON_DISABLED_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_DISABLED_3D_GRADIENT_LIGHT;

    /**
     * Background of a disabled button, ending colour of a gradient.
     * If equal to BUTTON_DISABLED_3D_GRADIENT_DARK, only BUTTON_DISABLED_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_DISABLED_3D_GRADIENT_DARK;

    /**
     * 3D border of a disabled button, light sections.
     */
    public final Colour BUTTON_DISABLED_3D_BORDER_LIGHT;

    /**
     * 3D border of a disabled button, dark sections.
     */
    public final Colour BUTTON_DISABLED_3D_BORDER_DARK;

    /**
     * Background of an active button, starting colour of a gradient.
     * If equal to BUTTON_ACTIVE_3D_GRADIENT_DARK, only BUTTON_ACTIVE_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_ACTIVE_3D_GRADIENT_LIGHT;

    /**
     * Background of an active button, ending colour of a gradient.
     * If equal to BUTTON_ACTIVE_3D_GRADIENT_DARK, only BUTTON_ACTIVE_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_ACTIVE_3D_GRADIENT_DARK;

    /**
     * 3D border of an active button, light sections.
     */
    public final Colour BUTTON_ACTIVE_3D_BORDER_LIGHT;

    /**
     * 3D border of an active button, dark sections.
     */
    public final Colour BUTTON_ACTIVE_3D_BORDER_DARK;

    /**
     * Background of a highlighted button, starting colour of a gradient.
     * If equal to BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK, only BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT;

    /**
     * Background of a highlighted button, ending colour of a gradient.
     * If equal to BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK, only BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK;

    /**
     * 3D border of a highlighted button, light sections.
     */
    public final Colour BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT;

    /**
     * 3D border of a highlighted button, dark sections.
     */
    public final Colour BUTTON_HIGHLIGHTED_3D_BORDER_DARK;

    /**
     * Background of a text field, starting colour of a gradient.
     * If equal to TEXTFIELD_NORMAL_3D_GRADIENT_DARK, only TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT;

    /**
     * Background of a text field, ending colour of a gradient.
     * If equal to TEXTFIELD_NORMAL_3D_GRADIENT_DARK, only TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.
     */
    public final Colour TEXTFIELD_NORMAL_3D_GRADIENT_DARK;

    /**
     * 3D border of a text field, light sections.
     */
    public final Colour TEXTFIELD_NORMAL_3D_BORDER_LIGHT;

    /**
     * 3D border of a text field, dark sections.
     */
    public final Colour TEXTFIELD_NORMAL_3D_BORDER_DARK;

    /**
     * Colour of the caret in a text field.
     */
    public final Colour TEXTFIELD_CARET;

    /**
     * Colour of the title of a GUI.
     */
    public final Colour GUI_TITLE;

    /**
     * Create a {@code Theme} from the specified json file or return the {@code DEFAULT} theme if the file could not
     * be parsed.
     *
     * @param jsonFilePath The {@link Path} of the json file to parse.
     * @return A new {@code Theme} initialized from the json file or the {@code DEFAULT} theme.
     */
    public static Theme read(Path jsonFilePath) {

        if (Files.exists(jsonFilePath)) {

            try (Reader reader = (new FileReader(jsonFilePath.toFile()))) {
                return read(reader);
            } catch (IOException e) {
                Log.LOGGER.error(e);
            }
        }

        return DEFAULT;
    }

    /**
     * Create a {@code Theme} from the specified JSON {@link IResource} or return the {@code DEFAULT} theme if the theme
     * can't be loaded from the resource.
     *
     * @param resource The {@link IResource} of the json file to parse.
     * @return A new {@code Theme} initialized from the json file or the {@code DEFAULT} theme.
     */
    public static Theme read(IResource resource) {

        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            return read(reader);
        } catch (IOException e) {
            Log.LOGGER.error(e);
        }

        return DEFAULT;
    }

    public static void write(Path jsonFilePath, Theme theme) {

        final JsonObject json = new JsonObject();

        writeColour(json, "FLAT_BACKGROUND_COLOR", theme.FLAT_BACKGROUND_COLOR);
        writeColour(json, "TEXT_ENABLED_COLOR", theme.TEXT_ENABLED_COLOR);
        writeColour(json, "TEXT_DISABLED_COLOR", theme.TEXT_DISABLED_COLOR);
        writeColour(json, "DARK_OUTLINE_COLOR", theme.DARK_OUTLINE_COLOR);
        writeColour(json, "BUTTON_NORMAL_3D_GRADIENT_LIGHT", theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT);
        writeColour(json, "BUTTON_NORMAL_3D_GRADIENT_DARK", theme.BUTTON_NORMAL_3D_GRADIENT_DARK);
        writeColour(json, "BUTTON_NORMAL_3D_BORDER_LIGHT", theme.BUTTON_NORMAL_3D_BORDER_LIGHT);
        writeColour(json, "BUTTON_NORMAL_3D_BORDER_DARK", theme.BUTTON_NORMAL_3D_BORDER_DARK);
        writeColour(json, "BUTTON_DISABLED_3D_GRADIENT_LIGHT", theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT);
        writeColour(json, "BUTTON_DISABLED_3D_GRADIENT_DARK", theme.BUTTON_DISABLED_3D_GRADIENT_DARK);
        writeColour(json, "BUTTON_DISABLED_3D_BORDER_LIGHT", theme.BUTTON_DISABLED_3D_BORDER_LIGHT);
        writeColour(json, "BUTTON_DISABLED_3D_BORDER_DARK", theme.BUTTON_DISABLED_3D_BORDER_DARK);
        writeColour(json, "BUTTON_ACTIVE_3D_GRADIENT_LIGHT", theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT);
        writeColour(json, "BUTTON_ACTIVE_3D_GRADIENT_DARK", theme.BUTTON_ACTIVE_3D_GRADIENT_DARK);
        writeColour(json, "BUTTON_ACTIVE_3D_BORDER_LIGHT", theme.BUTTON_ACTIVE_3D_BORDER_LIGHT);
        writeColour(json, "BUTTON_ACTIVE_3D_BORDER_DARK", theme.BUTTON_ACTIVE_3D_BORDER_DARK);
        writeColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT", theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT);
        writeColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK", theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK);
        writeColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT", theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT);
        writeColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_DARK", theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);
        writeColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT", theme.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT);
        writeColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_DARK", theme.TEXTFIELD_NORMAL_3D_GRADIENT_DARK);
        writeColour(json, "TEXTFIELD_NORMAL_3D_BORDER_LIGHT", theme.TEXTFIELD_NORMAL_3D_BORDER_LIGHT);
        writeColour(json, "TEXTFIELD_NORMAL_3D_BORDER_DARK", theme.TEXTFIELD_NORMAL_3D_BORDER_DARK);
        writeColour(json, "TEXTFIELD_CARET", theme.TEXTFIELD_CARET);
        writeColour(json, "GUI_TITLE", theme.GUI_TITLE);

        try (FileWriter writer = new FileWriter(jsonFilePath.toFile())) {

            new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //region internals

    private Theme() {

        this.FLAT_BACKGROUND_COLOR = new Colour(0x86, 0x86, 0x86, 0xFF);
        this.TEXT_ENABLED_COLOR = new Colour(0x27, 0x27, 0x27, 0xFF);
        this.TEXT_DISABLED_COLOR = new Colour(0xA0, 0xA0, 0xA0, 0xFF);
        this.DARK_OUTLINE_COLOR = new Colour(0.0, 0.0, 0.0, 0.7);
        this.BUTTON_NORMAL_3D_GRADIENT_LIGHT = new Colour(0x90, 0x90, 0x90, 0xFF);
        this.BUTTON_NORMAL_3D_GRADIENT_DARK = new Colour(0x20, 0x20, 0x20, 0xFF);
        this.BUTTON_NORMAL_3D_BORDER_LIGHT = new Colour(0xDD, 0xDD, 0xDD, 0xFF);
        this.BUTTON_NORMAL_3D_BORDER_DARK = new Colour(0x77, 0x77, 0x77, 0xEE);
        this.BUTTON_DISABLED_3D_GRADIENT_LIGHT = this.BUTTON_NORMAL_3D_GRADIENT_LIGHT;
        this.BUTTON_DISABLED_3D_GRADIENT_DARK = this.BUTTON_NORMAL_3D_GRADIENT_DARK;
        this.BUTTON_DISABLED_3D_BORDER_LIGHT = this.BUTTON_NORMAL_3D_BORDER_LIGHT;
        this.BUTTON_DISABLED_3D_BORDER_DARK = this.BUTTON_NORMAL_3D_BORDER_DARK;
        this.BUTTON_ACTIVE_3D_GRADIENT_LIGHT = new Colour(0xA7, 0x99, 0xAE, 0xFF);
        this.BUTTON_ACTIVE_3D_GRADIENT_DARK = new Colour(0x29, 0x24, 0x2E, 0xFF);
        this.BUTTON_ACTIVE_3D_BORDER_LIGHT = new Colour(0x77, 0x77, 0x77, 0xEE);
        this.BUTTON_ACTIVE_3D_BORDER_DARK = new Colour(0xDD, 0xDD, 0xDD, 0xFF);
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT = new Colour(0xA7, 0x99, 0xAE, 0xFF);
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK = new Colour(0x29, 0x24, 0x2E, 0xFF);
        this.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT = new Colour(0x77, 0x77, 0x77, 0xEE);
        this.BUTTON_HIGHLIGHTED_3D_BORDER_DARK = new Colour(0xDD, 0xDD, 0xDD, 0xFF);
        this.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT = this.BUTTON_NORMAL_3D_GRADIENT_LIGHT;
        this.TEXTFIELD_NORMAL_3D_GRADIENT_DARK = this.BUTTON_NORMAL_3D_GRADIENT_DARK;
        this.TEXTFIELD_NORMAL_3D_BORDER_LIGHT = Colour.WHITE;
        this.TEXTFIELD_NORMAL_3D_BORDER_DARK =  new Colour(0x2B, 0x2B, 0x2B, 0xFF);
        this.TEXTFIELD_CARET = new Colour(0x00, 0x00, 0x00, 0x6F);
        this.GUI_TITLE = Colour.BLACK;
    }

    private Theme(JsonObject json) {

        this.FLAT_BACKGROUND_COLOR = readColour(json, "FLAT_BACKGROUND_COLOR");
        this.TEXT_ENABLED_COLOR = readColour(json, "TEXT_ENABLED_COLOR");
        this.TEXT_DISABLED_COLOR = readColour(json, "TEXT_DISABLED_COLOR");
        this.DARK_OUTLINE_COLOR = readColour(json, "DARK_OUTLINE_COLOR");
        this.BUTTON_NORMAL_3D_GRADIENT_LIGHT = readColour(json, "BUTTON_NORMAL_3D_GRADIENT_LIGHT");
        this.BUTTON_NORMAL_3D_GRADIENT_DARK = readColour(json, "BUTTON_NORMAL_3D_GRADIENT_DARK");
        this.BUTTON_NORMAL_3D_BORDER_LIGHT = readColour(json, "BUTTON_NORMAL_3D_BORDER_LIGHT");
        this.BUTTON_NORMAL_3D_BORDER_DARK = readColour(json, "BUTTON_NORMAL_3D_BORDER_DARK");
        this.BUTTON_DISABLED_3D_GRADIENT_LIGHT = readColour(json, "BUTTON_DISABLED_3D_GRADIENT_LIGHT");
        this.BUTTON_DISABLED_3D_GRADIENT_DARK = readColour(json, "BUTTON_DISABLED_3D_GRADIENT_DARK");
        this.BUTTON_DISABLED_3D_BORDER_LIGHT = readColour(json, "BUTTON_DISABLED_3D_BORDER_LIGHT");
        this.BUTTON_DISABLED_3D_BORDER_DARK = readColour(json, "BUTTON_DISABLED_3D_BORDER_DARK");
        this.BUTTON_ACTIVE_3D_GRADIENT_LIGHT = readColour(json, "BUTTON_ACTIVE_3D_GRADIENT_LIGHT");
        this.BUTTON_ACTIVE_3D_GRADIENT_DARK = readColour(json, "BUTTON_ACTIVE_3D_GRADIENT_DARK");
        this.BUTTON_ACTIVE_3D_BORDER_LIGHT = readColour(json, "BUTTON_ACTIVE_3D_BORDER_LIGHT");
        this.BUTTON_ACTIVE_3D_BORDER_DARK = readColour(json, "BUTTON_ACTIVE_3D_BORDER_DARK");
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT = readColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT");
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK = readColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK");
        this.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT = readColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT");
        this.BUTTON_HIGHLIGHTED_3D_BORDER_DARK = readColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_DARK");
        this.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT = readColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT");
        this.TEXTFIELD_NORMAL_3D_GRADIENT_DARK = readColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_DARK");
        this.TEXTFIELD_NORMAL_3D_BORDER_LIGHT = readColour(json, "TEXTFIELD_NORMAL_3D_BORDER_LIGHT");
        this.TEXTFIELD_NORMAL_3D_BORDER_DARK = readColour(json, "TEXTFIELD_NORMAL_3D_BORDER_DARK");
        this.TEXTFIELD_CARET = readColour(json, "TEXTFIELD_CARET");
        this.GUI_TITLE = readColour(json, "GUI_TITLE");
    }

    private static Theme read(Reader reader) {

        try (JsonReader jsonReader = new JsonReader(reader)) {

            final JsonParser parser = new JsonParser();
            final JsonElement root = parser.parse(jsonReader);

            if (root.isJsonObject()) {
                return new Theme(root.getAsJsonObject());
            }

        } catch (IOException | JsonParseException e) {
            Log.LOGGER.error(e);
        }

        return DEFAULT;
    }

    private static void writeColour(final JsonObject json, final String name, final Colour colour) {
        JSONHelper.jsonSetString(json, name, colour.toHexRGBA());
    }

    private static Colour readColour(final JsonObject json, final String name) {
        return Colour.fromHexRGBA(JSONHelper.jsonGetString(json, name));
    }

    //endregion
}

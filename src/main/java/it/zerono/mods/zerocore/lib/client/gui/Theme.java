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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Theme {

    public static final Theme DEFAULT = new Theme();

    public final Colour FLAT_BACKGROUND_COLOR;

    public final Colour TEXT_ENABLED_COLOR;
    public final Colour TEXT_DISABLED_COLOR;

    public final Colour DARK_OUTLINE_COLOR;

    public final Colour STANDARD_3D_GRADIENT_LIGHT;
    public final Colour STANDARD_3D_GRADIENT_DARK;

    public final Colour BUTTON_NORMAL_3D_GRADIENT_LIGHT;
    public final Colour BUTTON_NORMAL_3D_GRADIENT_DARK;
    public final Colour BUTTON_NORMAL_3D_BORDER_LIGHT;
    public final Colour BUTTON_NORMAL_3D_BORDER_DARK;

    public final Colour BUTTON_DISABLED_3D_GRADIENT_LIGHT;
    public final Colour BUTTON_DISABLED_3D_GRADIENT_DARK;
    public final Colour BUTTON_DISABLED_3D_BORDER_LIGHT;
    public final Colour BUTTON_DISABLED_3D_BORDER_DARK;

    public final Colour BUTTON_ACTIVE_3D_GRADIENT_LIGHT;
    public final Colour BUTTON_ACTIVE_3D_GRADIENT_DARK;
    public final Colour BUTTON_ACTIVE_3D_BORDER_LIGHT;
    public final Colour BUTTON_ACTIVE_3D_BORDER_DARK;

    public final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT;
    public final Colour BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK;
    public final Colour BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT;
    public final Colour BUTTON_HIGHLIGHTED_3D_BORDER_DARK;

    public final Colour TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT;
    public final Colour TEXTFIELD_NORMAL_3D_GRADIENT_DARK;
    public final Colour TEXTFIELD_NORMAL_3D_BORDER_LIGHT;
    public final Colour TEXTFIELD_NORMAL_3D_BORDER_DARK;
    public final Colour TEXTFIELD_CARET;

    /**
     * Create a {@code Theme} from the specified json file or return the {@code DEFAULT} theme if the file could not
     * be parsed.
     *
     * @param jsonFilePath The {@link Path} of the json file to parse.
     * @return A new {@code Theme} initialized from the json file or the {@code DEFAULT} theme.
     */
    public static Theme read(Path jsonFilePath) {

        if (Files.exists(jsonFilePath)) {

            try (JsonReader reader = new JsonReader(new FileReader(jsonFilePath.toFile()))) {

                final JsonParser parser = new JsonParser();
                final JsonElement root = parser.parse(reader);

                if (root.isJsonObject()) {
                    return new Theme(root.getAsJsonObject());
                }

            } catch (IOException | JsonParseException e) {
                Log.LOGGER.error(e);
            }
        }

        return DEFAULT;
    }

    public static void write(Path jsonFilePath, Theme theme) {

        final JsonObject json = new JsonObject();

        JSONHelper.jsonSetColour(json, "FLAT_BACKGROUND_COLOR", theme.FLAT_BACKGROUND_COLOR);
        JSONHelper.jsonSetColour(json, "TEXT_ENABLED_COLOR", theme.TEXT_ENABLED_COLOR);
        JSONHelper.jsonSetColour(json, "TEXT_DISABLED_COLOR", theme.TEXT_DISABLED_COLOR);
        JSONHelper.jsonSetColour(json, "DARK_OUTLINE_COLOR", theme.DARK_OUTLINE_COLOR);
        JSONHelper.jsonSetColour(json, "STANDARD_3D_GRADIENT_LIGHT", theme.STANDARD_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "STANDARD_3D_GRADIENT_DARK", theme.STANDARD_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_NORMAL_3D_GRADIENT_LIGHT", theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_NORMAL_3D_GRADIENT_DARK", theme.BUTTON_NORMAL_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_NORMAL_3D_BORDER_LIGHT", theme.BUTTON_NORMAL_3D_BORDER_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_NORMAL_3D_BORDER_DARK", theme.BUTTON_NORMAL_3D_BORDER_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_DISABLED_3D_GRADIENT_LIGHT", theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_DISABLED_3D_GRADIENT_DARK", theme.BUTTON_DISABLED_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_DISABLED_3D_BORDER_LIGHT", theme.BUTTON_DISABLED_3D_BORDER_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_DISABLED_3D_BORDER_DARK", theme.BUTTON_DISABLED_3D_BORDER_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_ACTIVE_3D_GRADIENT_LIGHT", theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_ACTIVE_3D_GRADIENT_DARK", theme.BUTTON_ACTIVE_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_ACTIVE_3D_BORDER_LIGHT", theme.BUTTON_ACTIVE_3D_BORDER_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_ACTIVE_3D_BORDER_DARK", theme.BUTTON_ACTIVE_3D_BORDER_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT", theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK", theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT", theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT);
        JSONHelper.jsonSetColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_DARK", theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);
        JSONHelper.jsonSetColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT", theme.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT);
        JSONHelper.jsonSetColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_DARK", theme.TEXTFIELD_NORMAL_3D_GRADIENT_DARK);
        JSONHelper.jsonSetColour(json, "TEXTFIELD_NORMAL_3D_BORDER_LIGHT", theme.TEXTFIELD_NORMAL_3D_BORDER_LIGHT);
        JSONHelper.jsonSetColour(json, "TEXTFIELD_NORMAL_3D_BORDER_DARK", theme.TEXTFIELD_NORMAL_3D_BORDER_DARK);
        JSONHelper.jsonSetColour(json, "TEXTFIELD_CARET", theme.TEXTFIELD_CARET);

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
        this.STANDARD_3D_GRADIENT_LIGHT = new Colour(0x90, 0x90, 0x90, 0xFF);
        this.STANDARD_3D_GRADIENT_DARK = new Colour(0x20, 0x20, 0x20, 0xFF);
        this.BUTTON_NORMAL_3D_GRADIENT_LIGHT = this.STANDARD_3D_GRADIENT_LIGHT;
        this.BUTTON_NORMAL_3D_GRADIENT_DARK = this.STANDARD_3D_GRADIENT_DARK;
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
        this.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT = this.STANDARD_3D_GRADIENT_LIGHT;
        this.TEXTFIELD_NORMAL_3D_GRADIENT_DARK = this.STANDARD_3D_GRADIENT_DARK;
        this.TEXTFIELD_NORMAL_3D_BORDER_LIGHT = Colour.WHITE;
        this.TEXTFIELD_NORMAL_3D_BORDER_DARK =  new Colour(0x2B, 0x2B, 0x2B, 0xFF);
        this.TEXTFIELD_CARET = new Colour(0x00, 0x00, 0x00, 0x6F);
    }

    private Theme(JsonObject json) {

        this.FLAT_BACKGROUND_COLOR = JSONHelper.jsonGetColour(json, "FLAT_BACKGROUND_COLOR", DEFAULT.FLAT_BACKGROUND_COLOR);
        this.TEXT_ENABLED_COLOR = JSONHelper.jsonGetColour(json, "TEXT_ENABLED_COLOR", DEFAULT.TEXT_ENABLED_COLOR);
        this.TEXT_DISABLED_COLOR = JSONHelper.jsonGetColour(json, "TEXT_DISABLED_COLOR", DEFAULT.TEXT_DISABLED_COLOR);
        this.DARK_OUTLINE_COLOR = JSONHelper.jsonGetColour(json, "DARK_OUTLINE_COLOR", DEFAULT.DARK_OUTLINE_COLOR);
        this.STANDARD_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "STANDARD_3D_GRADIENT_LIGHT", DEFAULT.STANDARD_3D_GRADIENT_LIGHT);
        this.STANDARD_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "STANDARD_3D_GRADIENT_DARK", DEFAULT.STANDARD_3D_GRADIENT_DARK);
        this.BUTTON_NORMAL_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_NORMAL_3D_GRADIENT_LIGHT", DEFAULT.BUTTON_NORMAL_3D_GRADIENT_LIGHT);
        this.BUTTON_NORMAL_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "BUTTON_NORMAL_3D_GRADIENT_DARK", DEFAULT.BUTTON_NORMAL_3D_GRADIENT_DARK);
        this.BUTTON_NORMAL_3D_BORDER_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_NORMAL_3D_BORDER_LIGHT", DEFAULT.BUTTON_NORMAL_3D_BORDER_LIGHT);
        this.BUTTON_NORMAL_3D_BORDER_DARK = JSONHelper.jsonGetColour(json, "BUTTON_NORMAL_3D_BORDER_DARK", DEFAULT.BUTTON_NORMAL_3D_BORDER_DARK);
        this.BUTTON_DISABLED_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_DISABLED_3D_GRADIENT_LIGHT", DEFAULT.BUTTON_DISABLED_3D_GRADIENT_LIGHT);
        this.BUTTON_DISABLED_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "BUTTON_DISABLED_3D_GRADIENT_DARK", DEFAULT.BUTTON_DISABLED_3D_GRADIENT_DARK);
        this.BUTTON_DISABLED_3D_BORDER_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_DISABLED_3D_BORDER_LIGHT", DEFAULT.BUTTON_DISABLED_3D_BORDER_LIGHT);
        this.BUTTON_DISABLED_3D_BORDER_DARK = JSONHelper.jsonGetColour(json, "BUTTON_DISABLED_3D_BORDER_DARK", DEFAULT.BUTTON_DISABLED_3D_BORDER_DARK);
        this.BUTTON_ACTIVE_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_ACTIVE_3D_GRADIENT_LIGHT", DEFAULT.BUTTON_ACTIVE_3D_GRADIENT_LIGHT);
        this.BUTTON_ACTIVE_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "BUTTON_ACTIVE_3D_GRADIENT_DARK", DEFAULT.BUTTON_ACTIVE_3D_GRADIENT_DARK);
        this.BUTTON_ACTIVE_3D_BORDER_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_ACTIVE_3D_BORDER_LIGHT", DEFAULT.BUTTON_ACTIVE_3D_BORDER_LIGHT);
        this.BUTTON_ACTIVE_3D_BORDER_DARK = JSONHelper.jsonGetColour(json, "BUTTON_ACTIVE_3D_BORDER_DARK", DEFAULT.BUTTON_ACTIVE_3D_BORDER_DARK);
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT", DEFAULT.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT);
        this.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK", DEFAULT.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK);
        this.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT = JSONHelper.jsonGetColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT", DEFAULT.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT);
        this.BUTTON_HIGHLIGHTED_3D_BORDER_DARK = JSONHelper.jsonGetColour(json, "BUTTON_HIGHLIGHTED_3D_BORDER_DARK", DEFAULT.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);
        this.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT = JSONHelper.jsonGetColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT", DEFAULT.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT);
        this.TEXTFIELD_NORMAL_3D_GRADIENT_DARK = JSONHelper.jsonGetColour(json, "TEXTFIELD_NORMAL_3D_GRADIENT_DARK", DEFAULT.TEXTFIELD_NORMAL_3D_GRADIENT_DARK);
        this.TEXTFIELD_NORMAL_3D_BORDER_LIGHT = JSONHelper.jsonGetColour(json, "TEXTFIELD_NORMAL_3D_BORDER_LIGHT", DEFAULT.TEXTFIELD_NORMAL_3D_BORDER_LIGHT);
        this.TEXTFIELD_NORMAL_3D_BORDER_DARK = JSONHelper.jsonGetColour(json, "TEXTFIELD_NORMAL_3D_BORDER_DARK", DEFAULT.TEXTFIELD_NORMAL_3D_BORDER_DARK);
        this.TEXTFIELD_CARET = JSONHelper.jsonGetColour(json, "TEXTFIELD_CARET", DEFAULT.TEXTFIELD_CARET);
    }

    //endregion
}

/*
 *
 * JSONHelper.java
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

package it.zerono.mods.zerocore.lib.data.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class JSONHelper {

    /**
     * Get a int value from a JSON element
     *
     * @param json the JSON element
     * @param elementName the name of the element
     * @return the int value of the element
     */
    public static int jsonGetInt(final JsonElement json, final String elementName) {

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsInt();
        } else {
            throw new JsonSyntaxException("JSON element is not a integer: " + elementName);
        }
    }

    /**
     * Get a mandatory int from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the int value of the element
     */
    public static int jsonGetInt(final JsonObject json, final String elementName) {
        return jsonGetInt(jsonGetMandatoryElement(json, elementName), elementName);
    }

    /**
     * Get an optional int from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static int jsonGetInt(final JsonObject json, final String elementName, final int defaultValue) {
        return json.has(elementName) ? jsonGetInt(json.get(elementName), elementName) : defaultValue;
    }

    /**
     * Set a int value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetInt(final JsonObject json, final String elementName, final int value) {
        json.addProperty(elementName, value);
    }

    /**
     * Get a long value from a JSON element
     *
     * @param json the JSON element
     * @param elementName the name of the element
     * @return the long value of the element
     */
    public static long jsonGetLong(final JsonElement json, final String elementName) {

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsLong();
        } else {
            throw new JsonSyntaxException("JSON element is not a integer: " + elementName);
        }
    }

    /**
     * Get a mandatory long from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the long value of the element
     */
    public static long jsonGetLong(final JsonObject json, final String elementName) {
        return jsonGetLong(jsonGetMandatoryElement(json, elementName), elementName);
    }

    /**
     * Get an optional long from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static long jsonGetLong(final JsonObject json, final String elementName, final long defaultValue) {
        return json.has(elementName) ? jsonGetLong(json.get(elementName), elementName) : defaultValue;
    }

    /**
     * Set a long value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetLong(final JsonObject json, final String elementName, final long value) {
        json.addProperty(elementName, value);
    }

    /**
     * Get a short value from a JSON element
     *
     * @param json the JSON element
     * @param elementName the name of the element
     * @return the short value of the element
     */
    public static short jsonGetShort(final JsonElement json, final String elementName) {

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsShort();
        } else {
            throw new JsonSyntaxException("JSON element is not a integer: " + elementName);
        }
    }

    /**
     * Get a mandatory short from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the short value of the element
     */
    public static short jsonGetShort(final JsonObject json, final String elementName) {
        return jsonGetShort(jsonGetMandatoryElement(json, elementName), elementName);
    }

    /**
     * Get an optional short from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static short jsonGetShort(final JsonObject json, final String elementName, final short defaultValue) {
        return json.has(elementName) ? jsonGetShort(json.get(elementName), elementName) : defaultValue;
    }

    /**
     * Set a short value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetShort(final JsonObject json, final String elementName, final short value) {
        json.addProperty(elementName, value);
    }

    /**
     * Get a double value from a JSON element
     *
     * @param json the JSON element
     * @param elementName the name of the element
     * @return the double value of the element
     */
    public static double jsonGetDouble(final JsonElement json, final String elementName) {

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsDouble();
        } else {
            throw new JsonSyntaxException("JSON element is not a double: " + elementName);
        }
    }

    /**
     * Get a mandatory double from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the double value of the element
     */
    public static double jsonGetDouble(final JsonObject json, final String elementName) {
        return jsonGetDouble(jsonGetMandatoryElement(json, elementName), elementName);
    }

    /**
     * Get an optional double from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static double jsonGetDouble(final JsonObject json, final String elementName, final double defaultValue) {
        return json.has(elementName) ? jsonGetDouble(json.get(elementName), elementName) : defaultValue;
    }

    /**
     * Set a double value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetDouble(final JsonObject json, final String elementName, final double value) {
        json.addProperty(elementName, value);
    }

    /**
     * Get a string value from an element
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the string value of the element
     */
    public static String jsonGetString(final JsonElement json, final String elementName) {

        if (json.isJsonPrimitive()) {
            return json.getAsString();
        } else {
            throw new JsonSyntaxException("JSON element is not a string: " + elementName);
        }
    }

    /**
     * Get a mandatory string from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the string value of the element
     */
    public static String jsonGetString(final JsonObject json, final String elementName) {
        return jsonGetString(jsonGetMandatoryElement(json, elementName), elementName);
    }

    /**
     * Get an optional string from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static String jsonGetString(final JsonObject json, final String elementName, final String defaultValue) {
        return json.has(elementName) ? jsonGetString(json.get(elementName), elementName) : defaultValue;
    }

    /**
     * Set a string value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetString(final JsonObject json, final String elementName, final String value) {
        json.addProperty(elementName, value);
    }

    /**
     * Get a mandatory Enum from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param enumClass the Class of the Enum
     * @return the Enum value of the element
     */
    public static <T extends Enum<T>> T jsonGetEnum(final JsonObject json, final String elementName, final Class<T> enumClass) {
        return (enumClass.getEnumConstants())[jsonGetInt(json, elementName)];
    }

    /**
     * Set a Enum value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetEnum(final JsonObject json, final String elementName, final Enum<?> value) {
        jsonSetInt(json, elementName, value.ordinal());
    }

    /**
     * Get a mandatory ResourceLocation from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the ResourceLocation value of the element
     */
    public static ResourceLocation jsonGetResourceLocation(final JsonObject json, final String elementName) {
        return new ResourceLocation(jsonGetString(jsonGetMandatoryElement(json, elementName), elementName));
    }

    /**
     * Set a ResourceLocation value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetResourceLocation(final JsonObject json, final String elementName, final ResourceLocation value) {
        jsonSetString(json, elementName, value.toString());
    }

    /**
     * Get a mandatory CompoundTag from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the CompoundTag value of the element
     */
    public static CompoundTag jsonGetNBT(final JsonObject json, final String elementName) {
        try {
            return TagParser.parseTag(jsonGetString(json, elementName));
        } catch (CommandSyntaxException ex) {
            throw new JsonSyntaxException("JSON element is not a valid CompoundTag tag: " + elementName);
        }
    }

    /**
     * Get an optional CompoundTag from a JSON object.
     * If the element if not present, return {@code defaultValue}
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param defaultValue the default value to use if the element is not found
     * @return the element value or {@code defaultValue}
     */
    public static CompoundTag jsonGetNBT(final JsonObject json, final String elementName, final CompoundTag defaultValue) {
        try {
            return json.has(elementName) ? TagParser.parseTag(jsonGetString(json.get(elementName), elementName)) : defaultValue;
        } catch (CommandSyntaxException ex) {
            throw new JsonSyntaxException("JSON element is not a valid CompoundTag tag: " + elementName);
        }
    }

    /**
     * Set a CompoundTag value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetNBT(final JsonObject json, final String elementName, final CompoundTag value) {
        jsonSetString(json, elementName, value.toString());
    }

    /**
     * Get a mandatory Item from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the Item
     */
    public static Item jsonGetItem(final JsonObject json, final String elementName) {

        final ResourceLocation id = jsonGetResourceLocation(json, elementName);

        if (ForgeRegistries.ITEMS.containsKey(id)) {
            return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id));
        } else {
            throw new JsonSyntaxException("JSON element is not a valid Item: " + elementName);
        }
    }

    /**
     * Set an Item value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetItem(final JsonObject json, final String elementName, final ItemLike value) {
        jsonSetResourceLocation(json, elementName, ItemHelper.getItemId(value));
    }

    /**
     * Get a mandatory Fluid from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the Fluid
     */
    public static Fluid jsonGetFluid(final JsonObject json, final String elementName) {

        final ResourceLocation id = jsonGetResourceLocation(json, elementName);

        if (ForgeRegistries.FLUIDS.containsKey(id)) {
            return Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(id));
        } else {
            throw new JsonSyntaxException("JSON element is not a valid Fluid: " + elementName);
        }
    }

    /**
     * Set an Fluid value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetFluid(final JsonObject json, final String elementName, final Fluid value) {
        jsonSetResourceLocation(json, elementName, FluidHelper.getFluidId(value));
    }

    /**
     * Get a mandatory Ingredient from a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @return the Ingredient
     */
    public static Ingredient jsonGetIngredient(final JsonObject json, final String elementName) {
        return Ingredient.fromJson(jsonGetMandatoryElement(json, elementName));
    }

    /**
     * Set an Ingredient value in a JSON object
     *
     * @param json the JSON object
     * @param elementName the name of the element
     * @param value the value
     */
    public static void jsonSetIngredient(final JsonObject json, final String elementName, final Ingredient value) {
        json.add(elementName, value.toJson());
    }

    public static JsonElement jsonGetMandatoryElement(final JsonObject json, final String elementName) {

        if (json.has(elementName)) {
            return json.get(elementName);
        } else {
            throw new JsonSyntaxException("Missing required element in JSON object " + elementName);
        }
    }

//    protected static void jsonValidateObjectKey(final JsonObject json, final String key) {
//
//        if (!json.has(key)) {
//            throw new JsonSyntaxException("Missing object key in JSON object: " + key);
//        }
//
//        if (!json.get(key).isJsonObject()) {
//            throw new JsonSyntaxException("Element is not a JSON object: " + key);
//        }
//    }

    //region internals

    private JSONHelper() {
    }

    //endregion
}

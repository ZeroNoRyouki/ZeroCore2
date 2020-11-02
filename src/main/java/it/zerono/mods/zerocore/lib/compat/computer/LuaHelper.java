/*
 *
 * LuaHelper.java
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

package it.zerono.mods.zerocore.lib.compat.computer;

//import li.cil.oc.api.machine.Arguments;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class LuaHelper {

    public static void validateArgsCount(Object[] arguments, int count) {

        if (arguments.length < count) {
            raiseInvalidArgsCount(count);
        }
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static void validateArgsCount(@Nonnull Arguments arguments, int count) throws Exception {

        if (arguments.count() < count)
            raiseInvalidArgsCount(count);
    }*/

    public static double getDoubleFromArgs(Object[] arguments, int index) {

        if (null == arguments[index] || !(arguments[index] instanceof Double)) {
            raiseIllegalArgumentType(index, "Number");
        }

        return (Double)arguments[index];
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static double getDoubleFromArgs(@Nonnull Arguments arguments, int index) throws Exception {

        if (!arguments.isDouble(index))
            raiseIllegalArgumentType(index, "Number");

        return arguments.checkDouble(index);
    }*/

    public static double getDoubleFromArgs(Object[] arguments, int index, double minValue, double maxValue) {

        double value = getDoubleFromArgs(arguments, index);

        if (value < minValue || value > maxValue) {
            raiseIllegalArgumentRange(index, minValue, maxValue);
        }

        return value;
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static double getDoubleFromArgs(@Nonnull Arguments arguments, int index, double minValue, double maxValue) throws Exception {

        double value = getDoubleFromArgs(arguments, index);

        if (value < minValue || value > maxValue)
            raiseIllegalArgumentRange(index, minValue, maxValue);

        return value;
    }*/

    public static int getIntFromArgs(Object[] arguments, int index) {
        return (int)Math.round(getDoubleFromArgs(arguments, index));
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static int getIntFromArgs(@Nonnull Arguments arguments, int index) throws Exception {
        return (int)Math.round(getDoubleFromArgs(arguments, index)); // keep a consistent implementation with getIntFromArgs(object[], int)
    }*/

    public static int getIntFromArgs(Object[] arguments, int index, int minValue, int maxValue) {

        int value = getIntFromArgs(arguments, index);

        if (value < minValue || value > maxValue) {
            raiseIllegalArgumentRange(index, minValue, maxValue);
        }

        return value;
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static int getIntFromArgs(@Nonnull Arguments arguments, int index, int minValue, int maxValue) throws Exception {

        int value = getIntFromArgs(arguments, index);

        if (value < minValue || value > maxValue)
            raiseIllegalArgumentRange(index, minValue, maxValue);

        return value;
    }*/

    public static boolean getBooleanFromArgs(Object[] arguments, int index) {

        if (null == arguments[index] || !(arguments[index] instanceof Boolean)) {
            raiseIllegalArgumentType(index, "Boolean");
        }

        return (Boolean)arguments[index];
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static boolean getBooleanFromArgs(@Nonnull Arguments arguments, int index) throws Exception {

        if (!arguments.isBoolean(index))
            raiseIllegalArgumentType(index, "Boolean");

        return arguments.checkBoolean(index);
    }*/

    public static String getStringFromArgs(Object[] arguments, int index) {

        if (null == arguments[index] || !(arguments[index] instanceof String)) {
            raiseIllegalArgumentType(index, "String");
        }

        return (String)arguments[index];
    }
    /*
    @Optional.Method(modid = MODID_OPENCOMPUTERS)
    public static String getStringFromArgs(@Nonnull Arguments arguments, int index) throws Exception {

        if (!arguments.isString(index))
            raiseIllegalArgumentType(index, "String");

        return arguments.checkString(index);
    }*/

    public static void raiseInvalidArgsCount(int expectedCount) {
        throw new IllegalArgumentException(String.format("Insufficient number of arguments, expected %d", expectedCount));
    }

    public static void raiseIllegalArgumentType(int index, String expectedType) {
        throw new IllegalArgumentException(String.format("Invalid argument %d, expected %s", index, expectedType));
    }

    public static void raiseIllegalArgumentRange(int index, double minValue, double maxValue) {
        throw new IllegalArgumentException(String.format("Invalid argument %d, valid range is %f : %f", index, minValue, maxValue));
    }

    public static void raiseIllegalArgumentRange(int index, int minValue, int maxValue) {
        throw new IllegalArgumentException(String.format("Invalid argument %d, valid range is %d : %d", index, minValue, maxValue));
    }

    //region internals

    private LuaHelper() {
    }

    //endregion
}

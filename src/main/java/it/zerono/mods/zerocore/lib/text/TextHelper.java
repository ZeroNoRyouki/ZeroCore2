/*
 *
 * TextHelper.java
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

package it.zerono.mods.zerocore.lib.text;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullFunction;

public final class TextHelper {

    public static final NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> IDENTITY_TEXT_POST_PROCESSOR = $ -> $;

    public static IFormattableTextComponent literal(String text) {
        return Strings.isNullOrEmpty(text) ? CodeHelper.TEXT_EMPTY_LINE.copy() : new StringTextComponent(text);
    }

    public static IFormattableTextComponent literal(String text,
                                                    NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return Strings.isNullOrEmpty(text) ? CodeHelper.TEXT_EMPTY_LINE.copy() :
                textPostProcessor.apply(new StringTextComponent(text));
    }

    public static IFormattableTextComponent literal(String format, Object... arguments) {
        return literal(String.format(format, arguments));
    }

    public static IFormattableTextComponent literal(String format,
                                                    NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor,
                                                    Object... arguments) {
        return literal(String.format(format, arguments), textPostProcessor);
    }

    public static IFormattableTextComponent translatable(String langKey) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(langKey), "Lang key must not be null or empty.");

        return new TranslationTextComponent(langKey);
    }

    public static IFormattableTextComponent translatable(String langKey,
                                                         NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return textPostProcessor.apply(translatable(langKey));
    }

    public static IFormattableTextComponent translatable(String langKey, Object... arguments) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(langKey), "Lang key must not be null or empty.");
        Preconditions.checkArgument(arguments.length > 0, "Arguments must not be empty.");
        
        return new TranslationTextComponent(langKey, arguments);
    }

    public static IFormattableTextComponent translatable(String langKey,
                                                         NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor,
                                                         Object... arguments) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return textPostProcessor.apply(translatable(langKey, arguments));
    }

    //region TextHelper

    private TextHelper() {
    }

    //endregion
}

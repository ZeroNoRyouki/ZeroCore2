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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.util.NonNullFunction;

public final class TextHelper {

    public static final NonNullFunction<MutableComponent, MutableComponent> IDENTITY_TEXT_POST_PROCESSOR = $ -> $;

    public static MutableComponent literal(String text) {
        return Strings.isNullOrEmpty(text) ? Component.empty() : Component.literal(text);
    }

    public static MutableComponent literal(String text,
                                           NonNullFunction<MutableComponent, MutableComponent> textPostProcessor) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return textPostProcessor.apply(literal(text));
    }

    public static MutableComponent literal(String format, Object... arguments) {
        return literal(String.format(format, arguments));
    }

    public static MutableComponent literal(String format,
                                           NonNullFunction<MutableComponent, MutableComponent> textPostProcessor,
                                           Object... arguments) {
        return literal(String.format(format, arguments), textPostProcessor);
    }

    public static MutableComponent translatable(String langKey) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(langKey), "Lang key must not be null or empty.");

        return Component.translatable(langKey);
    }

    public static MutableComponent translatable(String langKey,
                                                NonNullFunction<MutableComponent, MutableComponent> textPostProcessor) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return textPostProcessor.apply(translatable(langKey));
    }

    public static MutableComponent translatable(String langKey, Object... arguments) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(langKey), "Lang key must not be null or empty.");
        Preconditions.checkArgument(arguments.length > 0, "Arguments must not be empty.");

        return Component.translatable(langKey, arguments);
    }

    public static MutableComponent translatable(String langKey,
                                                NonNullFunction<MutableComponent, MutableComponent> textPostProcessor,
                                                Object... arguments) {

        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null or empty.");

        return textPostProcessor.apply(translatable(langKey, arguments));
    }

    //region TextHelper

    private TextHelper() {
    }

    //endregion
}

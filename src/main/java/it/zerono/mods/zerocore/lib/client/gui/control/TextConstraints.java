/*
 *
 * TextConstraints.java
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

import net.minecraft.util.Mth;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class TextConstraints {

    public final static Predicate<Character> FILTER_NUMBERS = Character::isDigit;

    public final static Function<String, Optional<String>> CONSTRAINT_POSITIVE_INTEGER_NUMBER = text -> {

        if (text.isEmpty()) {

            return Optional.of("0");

        } else {

            final long originalValue = Long.parseLong(text);
            long value = Mth.clamp(originalValue, 0, Integer.MAX_VALUE);

            if (originalValue != value || '0' == text.charAt(0)) {
                return Optional.of(Long.toString(value));
            }
        }

        return Optional.empty();
    };

    public final static Function<String, Optional<String>> CONSTRAINT_PERCENTAGE = text -> {

        if (text.isEmpty()) {

            return Optional.of("0");

        } else {

            final int originalValue = Integer.parseInt(text);
            int percentage = Mth.clamp(originalValue, 0, 100);

            if (originalValue != percentage || '0' == text.charAt(0)) {
                return Optional.of(Integer.toString(percentage));
            }
        }

        return Optional.empty();
    };
}

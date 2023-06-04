/*
 *
 * NonNullBiFunction.java
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

package it.zerono.mods.zerocore.lib.functional;

import net.minecraftforge.common.util.NonNullFunction;

import javax.annotation.Nonnull;
import java.util.Objects;

@FunctionalInterface
public interface NonNullBiFunction<T1, T2, R> {

    @Nonnull
    R apply(@Nonnull T1 t1, @Nonnull T2 t2);

    @Nonnull
    default <V> NonNullBiFunction<T1, T2, V> andThen(@Nonnull NonNullFunction<? super R, ? extends V> after) {

        Objects.requireNonNull(after);
        return (T1 t1, T2 t2) -> after.apply(apply(t1, t2));
    }
}

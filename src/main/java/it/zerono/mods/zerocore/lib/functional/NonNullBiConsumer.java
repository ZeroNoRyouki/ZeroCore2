/*
 *
 * NonNullBiConsumer.java
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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface NonNullBiConsumer<T1, T2> {

    void accept(@NotNull T1 t1, @NotNull T2 t2);

    @NotNull
    default NonNullBiConsumer<T1, T2> andThen(@NotNull NonNullBiConsumer<? super T1, ? super T2> after) {

        Objects.requireNonNull(after);

        return (t1, t2) -> {

            this.accept(t1, t2);
            after.accept(t1, t2);
        };
    }
}

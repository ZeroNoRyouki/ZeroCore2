/*
 *
 * TestResult.java
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

package it.zerono.mods.zerocore.lib;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public enum TestResult
    implements BooleanSupplier {

    /**
     * The test was successful
     */
    SUCCESS,

    /**
     * The test was NOT successful
     */
    FAIL,

    /**
     * The test was not performed because it was impossible to do so
     */
    SKIPPED
    ;

    public boolean wasSkipped() {
        return this == SKIPPED;
    }

    public static TestResult from(final boolean result) {
        return result ? SUCCESS : FAIL;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static TestResult from(final Optional<Boolean> test) {
        return test.map(TestResult::from).orElse(SKIPPED);
    }

    public static <T> TestResult from(final Predicate<T> test, final T argument) {
        return from(test.test(argument));
    }

    //region BooleanSupplier

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public boolean getAsBoolean() {

        if (this.wasSkipped()) {
            throw new IllegalArgumentException("Returning a result from a skipped test");
        }

        return this == SUCCESS;
    }

    //endregion
}

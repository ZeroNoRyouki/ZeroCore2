/*
 *
 * SensorBehavior.java
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

package it.zerono.mods.zerocore.base.redstone.sensor;

public enum SensorBehavior {

    /**
     * The sensor is disabled.
     * Direction: N/A
     */
    Disabled,
    /**
     * Change a state depending on the presence or absence of a redstone signal.
     * Direction: input
     */
    SetFromSignal,
    /**
     * Change a state depending on the level of a redstone signal.
     * Direction: input
     */
    SetFromSignalLevel,
    /**
     * Change a state on a redstone signal pulse.
     * Direction: input
     */
    SetOnPulse,
    /**
     * Toggle a state on a redstone signal pulse.
     * Direction: input
     */
    ToggleOnPulse,
    /**
     * Augment a state on a redstone signal pulse.
     * Direction: input
     */
    AugmentOnPulse,
    /**
     * Reduce a state on a redstone signal pulse.
     * Direction: input
     */
    ReduceOnPulse,
    /**
     * Do something on a redstone signal pulse.
     * Direction: input
     */
    PerformOnPulse,
    /**
     * Emit a redstone signal when a state is greater than a specified value.
     * Direction: output
     */
    ActiveWhileAbove,
    /**
     * Emit a redstone signal when a state is lower than a specified value.
     * Direction: output
     */
    ActiveWhileBelow,
    /**
     * Emit a redstone signal when a state is in a specified range of values.
     * Direction: output
     */
    ActiveWhileBetween
    ;

    public boolean onPulse() {

        switch (this) {

            case SetOnPulse:
            case ToggleOnPulse:
            case AugmentOnPulse:
            case ReduceOnPulse:
            case PerformOnPulse:
                return true;

            default:
                return false;
        }
    }

    public boolean outputTest(final int current, final int v1, final int v2) {

        switch (this) {

            case ActiveWhileAbove:
                return current > v1;

            case ActiveWhileBelow:
                return current < v1;

            case ActiveWhileBetween:
                return current >= v1 && current <= v2;

            default:
                return false;
        }
    }
}

/*
 *
 * IActivableMachine.java
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

public interface IActivableMachine {

    /**
     * @return true if the machine is active, false otherwise
     */
    boolean isMachineActive();

    /**
     * Change the state of the machine
     *
     * @param active if true, activate the machine, if false deactivate it
     */
    void setMachineActive(boolean active);

    /**
     * Activate the machine if it is not active and vice versa
     */
    default void toggleMachineActive() {
        this.setMachineActive(!this.isMachineActive());
    }
}

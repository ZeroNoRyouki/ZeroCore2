/*
 *
 * IModInitializationHandler.java
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

package it.zerono.mods.zerocore.lib.init;

import net.minecraftforge.fml.event.lifecycle.*;

public interface IModInitializationHandler {

    /**
     * Called on both the physical client and the physical server to perform common initialization tasks
     * @param event the event
     */
    default void onCommonInit(FMLCommonSetupEvent event) {
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     * @param event the event
     */
    default void onClientInit(FMLClientSetupEvent event) {
    }

    /**
     * Called on the physical server to perform server-specific initialization tasks
     * @param event the event
     */
    default void onServerInit(FMLDedicatedServerSetupEvent event) {
    }

    /**
     * Enqueue messages to other mods
     *
     * See {@link net.minecraftforge.fml.InterModComms}
     *
     * @param event the event
     */
    default void onInterModEnqueue(InterModEnqueueEvent event) {
    }

    /**
     * Retrieve and process inter-mods messages and process them
     *
     * See {@link net.minecraftforge.fml.InterModComms}
     *
     * @param event the event
     */
    default void onInterModProcess(InterModProcessEvent event) {
    }
}

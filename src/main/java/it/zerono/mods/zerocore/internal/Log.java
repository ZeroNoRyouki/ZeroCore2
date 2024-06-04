/*
 *
 * Log.java
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

package it.zerono.mods.zerocore.internal;

import it.zerono.mods.zerocore.ZeroCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Log {

    public static final Logger LOGGER = LogManager.getLogger(ZeroCore.MOD_ID);

    public static final Marker CORE = MarkerManager.getMarker("core");
    public static final Marker SERVICE_LOADER = MarkerManager.getMarker("service_loader");
    public static final Marker MULTIBLOCK = MarkerManager.getMarker("multiblock");
    public static final Marker NETWORK = MarkerManager.getMarker("network");
    public static final Marker COMPAT_COMPUTERS = MarkerManager.getMarker("compat.computers");
    public static final Marker CLIENT = MarkerManager.getMarker("client");
    public static final Marker GUI = MarkerManager.getMarker("gui").addParents(CLIENT);
}

/*
 *
 * ISensorSettingFactory.java
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

import it.zerono.mods.zerocore.lib.IMachineReader;

public interface ISensorSettingFactory<Reader extends IMachineReader, Writer,
        SensorType extends Enum<SensorType> & ISensorType<Reader>,
        SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType>> {

    SensorSetting create(SensorType sensor, SensorBehavior behavior, int v1, int v2);
}

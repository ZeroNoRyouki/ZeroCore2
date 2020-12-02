/*
 *
 * ModOreReGenFeature.java
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

package it.zerono.mods.zerocore.lib.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.Heightmap;

public class ModOreReGenFeature
    extends ModOreFeature {

    public ModOreReGenFeature(Codec<ModOreFeatureConfig> p_i231976_1_) {
        super(p_i231976_1_);
    }

    @Override
    protected Heightmap.Type getHeightmapType() {
        return Heightmap.Type.OCEAN_FLOOR;
    }
}

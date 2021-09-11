/*
 *
 * ModOreFeatureConfig.java
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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

// Copy of OreFeatureConfig
public class ModOreFeatureConfig
        implements IFeatureConfig {

    public static final Codec<ModOreFeatureConfig> CODEC = RecordCodecBuilder.create(
            (p) -> p.group(RuleTest.CODEC.fieldOf("target").forGetter((config) -> config.target), BlockState.CODEC.fieldOf("state").forGetter((config) -> config.state), Codec.intRange(0, 64).fieldOf("size").forGetter((config) -> config.size))
                    .apply(p, ModOreFeatureConfig::new));

    public final RuleTest target;
    public final int size;
    public final BlockState state;

    public ModOreFeatureConfig(RuleTest target, BlockState state, int size) {

        this.size = size;
        this.state = state;
        this.target = target;
    }
}


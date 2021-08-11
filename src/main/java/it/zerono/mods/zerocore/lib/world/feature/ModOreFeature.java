/*
 *
 * ModOreFeature.java
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
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.Random;

public class ModOreFeature
        extends OreFeature {

    public ModOreFeature(Codec<OreConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreConfiguration> p_160177_) {
        Random random = p_160177_.random();
        BlockPos blockpos = p_160177_.origin();
        WorldGenLevel worldgenlevel = p_160177_.level();
        OreConfiguration oreconfiguration = p_160177_.config();
        float f = random.nextFloat() * (float)Math.PI;
        float f1 = (float)oreconfiguration.size / 8.0F;
        int i = Mth.ceil(((float)oreconfiguration.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d0 = (double)blockpos.getX() + Math.sin((double)f) * (double)f1;
        double d1 = (double)blockpos.getX() - Math.sin((double)f) * (double)f1;
        double d2 = (double)blockpos.getZ() + Math.cos((double)f) * (double)f1;
        double d3 = (double)blockpos.getZ() - Math.cos((double)f) * (double)f1;
        int j = 2;
        double d4 = (double)(blockpos.getY() + random.nextInt(3) - 2);
        double d5 = (double)(blockpos.getY() + random.nextInt(3) - 2);
        int k = blockpos.getX() - Mth.ceil(f1) - i;
        int l = blockpos.getY() - 2 - i;
        int i1 = blockpos.getZ() - Mth.ceil(f1) - i;
        int j1 = 2 * (Mth.ceil(f1) + i);
        int k1 = 2 * (2 + i);

        final Heightmap.Types heightmap = this.getHeightmapType();

        for(int l1 = k; l1 <= k + j1; ++l1) {
            for(int i2 = i1; i2 <= i1 + j1; ++i2) {
                if (l <= worldgenlevel.getHeight(heightmap, l1, i2)) {
                    return this.doPlace(worldgenlevel, random, oreconfiguration, d0, d1, d2, d3, d4, d5, k, l, i1, j1, k1);
                }
            }
        }

        return false;
    }

    protected Heightmap.Types getHeightmapType() {
        return Heightmap.Types.OCEAN_FLOOR_WG;
    }
}

/*
 *
 * SpiralParticle.java
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

package it.zerono.mods.zerocore.lib.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpiralParticle
        extends Particle {

    protected SpiralParticle(ClientLevel world, double centerX, double centerY, double centerZ, double radius, int lifeInTicks) {

        super(world, centerX + radius * /*MathHelper.cos(0)*/1, centerY, centerZ + radius * /*MathHelper.sin(0)*/0);
        this._angle = 0;
        this._centerX = centerX;
        this._centerZ = centerZ;
        this._radius = radius;
        this.setLifetime(lifeInTicks);
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (++this.age >= this.getLifetime()) {
            this.remove();
        }

        if ((this._angle += 10) >= 360) {
            this._angle = 0;
        }

        final float radiants = (float)(this._angle * Math.PI / 180.0);
        final double newX = this._centerX + this._radius * Mth.cos(radiants);
        final double newZ = this._centerZ + this._radius * Mth.sin(radiants);

        this.xd = newX - this.x;
        this.zd = newZ - this.z;
        this.yd = 0.01;

        this.move(this.xd, this.yd, this.zd);
    }

    protected float _angle;
    protected final double _centerX;
    protected final double _centerZ;
    protected final double _radius;
}

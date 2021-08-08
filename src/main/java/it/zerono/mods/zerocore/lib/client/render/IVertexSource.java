/*
 *
 * IVertexSource.java
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

package it.zerono.mods.zerocore.lib.client.render;

import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public interface IVertexSource {

    /*
    IVertexSource EMPTY = new IVertexSource() {
        @Override
        public void uploadVertexData(IVertexBuilder builder) {
        }

        @Override
        public void uploadVertexData(IVertexBuilder builder, LightMap lightMapOverride, LightMap overlayMapOverride) {
        }

        @Override
        public void uploadVertexData(IVertexBuilder builder, Colour colourOverride) {
        }
    };
    */

    Vector3d getPos();

    @Nullable
    Vector3f getNormal();

    @Nullable
    UV getUV();

    @Nullable
    Colour getColour();

    @Nullable
    LightMap getLightMap();

    @Nullable
    LightMap getOverlayMap();

    /*
    void uploadVertexData(IVertexBuilder builder);

    void uploadVertexData(IVertexBuilder builder, LightMap lightMapOverride, LightMap overlayMapOverride);

    default void uploadVertexData(IVertexBuilder builder, int combinedLightMapOverride, int combinedOverlayMapOverride) {
        this.uploadVertexData(builder, new LightMap(combinedLightMapOverride), new LightMap(combinedOverlayMapOverride));
    }

    void uploadVertexData(IVertexBuilder builder, Colour colourOverride);
    */
}

/*
 *
 * ISourceAdapter.java
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

package it.zerono.mods.zerocore.lib.client.render.vertexuploader;

import it.zerono.mods.zerocore.lib.client.render.IVertexSource;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import it.zerono.mods.zerocore.lib.data.gfx.UV;
import com.mojang.math.Vector3f;

import javax.annotation.Nullable;

public interface ISourceAdapter {

    default Vector3d getPos(IVertexSource source) {
        return source.getPos();
    }

    @Nullable
    default Vector3f getNormal(IVertexSource source) {
        return source.getNormal();
    }

    @Nullable
    default UV getUV(IVertexSource source) {
        return source.getUV();
    }

    @Nullable
    default Colour getColour(IVertexSource source) {
        return source.getColour();
    }

    @Nullable
    default LightMap getLightMap(IVertexSource source) {
        return source.getLightMap();
    }

    @Nullable
    default LightMap getOverlayMap(IVertexSource source) {
        return source.getOverlayMap();
    }
}

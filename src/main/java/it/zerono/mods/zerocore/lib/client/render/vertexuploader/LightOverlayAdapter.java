/*
 *
 * LightOverlayAdapter.java
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
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;

import javax.annotation.Nullable;

public class LightOverlayAdapter implements ISourceAdapter {

    public LightOverlayAdapter(LightMap light, LightMap overlay) {

        this._light = light;
        this._overlay = overlay;
    }

    public LightOverlayAdapter(int light, int overlay) {
        this(new LightMap(light), new LightMap(overlay));
    }

    public LightOverlayAdapter setLight(LightMap light) {

        this._light = light;
        return this;
    }

    public LightOverlayAdapter setLight(int light) {

        this._light = new LightMap(light);
        return this;
    }

    public LightOverlayAdapter setOverlay(LightMap overlay) {

        this._light = overlay;
        return this;
    }

    public LightOverlayAdapter setOverlay(int overlay) {

        this._light = new LightMap(overlay);
        return this;
    }

    //region ISourceAdapter

    @Nullable
    @Override
    public LightMap getLightMap(IVertexSource source) {
        return this._light;
    }

    @Nullable
    @Override
    public LightMap getOverlayMap(IVertexSource source) {
        return this._overlay;
    }

    //endregion
    //region internals

    private LightMap _light;
    private LightMap _overlay;

    //endregion
}

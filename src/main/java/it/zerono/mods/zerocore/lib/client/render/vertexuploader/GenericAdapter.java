/*
 *
 * GenericAdapter.java
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

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.render.IVertexSource;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.gfx.LightMap;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import javax.annotation.Nullable;

public class GenericAdapter implements ISourceAdapter {

    public GenericAdapter() {

        this._matrix = null;
        this._colour = null;
        this._light = this._overlay = null;
    }

    public GenericAdapter setMatrix(@Nullable MatrixStack matrix) {

        this._matrix = matrix;
        return this;
    }

    public GenericAdapter setColour(@Nullable Colour colour) {

        this._colour = colour;
        return this;
    }

    public GenericAdapter setLight(@Nullable LightMap light) {

        this._light = light;
        return this;
    }

    public GenericAdapter setLight(int combined) {
        return this.setLight(new LightMap(combined));
    }

    public GenericAdapter setOverlay(@Nullable LightMap overlay) {

        this._overlay = overlay;
        return this;
    }

    public GenericAdapter setOverlay(int combined) {
        return this.setOverlay(new LightMap(combined));
    }

    //region ISourceAdapter

    @Override
    public Vector3d getPos(IVertexSource source) {

        if (null != this._matrix) {

            final Vector3d original = source.getPos();
            final Vector4f v = new Vector4f((float)original.X, (float)original.Y, (float)original.Y, 1.0f);

            v.transform(this._matrix.last().pose());
            return Vector3d.from(v);

        } else {

            return source.getPos();
        }
    }

    @Nullable
    @Override
    public Vector3f getNormal(IVertexSource source) {

        final Vector3f original = source.getNormal();

        if (null != this._matrix && null != original) {

            Vector3f v = original.copy();

            v.transform(this._matrix.last().normal());
            return v;

        } else {

            return original;
        }
    }

    @Nullable
    @Override
    public Colour getColour(IVertexSource source) {
        return null != this._colour ? this._colour : source.getColour();
    }

    @Nullable
    @Override
    public LightMap getLightMap(IVertexSource source) {
        return null != this._light ? this._light : source.getLightMap();
    }

    @Nullable
    @Override
    public LightMap getOverlayMap(IVertexSource source) {
        return null != this._overlay ? this._overlay : source.getOverlayMap();
    }

    //endregion
    //region internals

    private MatrixStack _matrix;
    private Colour _colour;
    private LightMap _light;
    private LightMap _overlay;

    //endregion
}

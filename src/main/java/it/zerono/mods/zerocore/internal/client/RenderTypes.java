/*
 *
 * RenderTypes.java
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

package it.zerono.mods.zerocore.internal.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public final class RenderTypes
    extends RenderType {

    public static final RenderType ERROR_BLOCK_HIGHLIGHT = create("zc_mb_error_block",
            DefaultVertexFormat.POSITION_COLOR, GL11.GL_LINES, 256,
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(8)))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setTextureState(NO_TEXTURE)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .setCullState(CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setFogState(NO_FOG)
                    .createCompositeState(false));

    //region internals

    protected RenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    //endregion
}

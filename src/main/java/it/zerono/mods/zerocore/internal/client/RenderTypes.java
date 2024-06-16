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

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.zerono.mods.zerocore.ZeroCore;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.OptionalDouble;

public final class RenderTypes
    extends RenderType {

    public static final RenderType ERROR_BLOCK_HIGHLIGHT = create(ZeroCore.MOD_ID + ":zc_mb_error_block",
            DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(8)))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));

    public static final RenderType FLUID_COLUMN = create(ZeroCore.MOD_ID + ":fluid_column",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS, 256, false, true,
            CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));

    //region internals

    private RenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn,
                        boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    //endregion
}

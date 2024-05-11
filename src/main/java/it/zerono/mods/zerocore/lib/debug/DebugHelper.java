/*
 *
 * DebugHelper.java
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

package it.zerono.mods.zerocore.lib.debug;

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.client.debug.VoxelShapeHighlighter;
import it.zerono.mods.zerocore.lib.data.Flags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collections;
import java.util.Map;

public class DebugHelper {

    //region Voxel shape highlighter

    public enum VoxelShapeType {

        General,
        Render,
        Collision,
        RayTrace,
        None
    }

    @OnlyIn(Dist.CLIENT)
    public static void initVoxelShapeHighlighter() {

        if (!isVoxelShapeHighlighterEnabled()) {

            NeoForge.EVENT_BUS.register(VoxelShapeHighlighter.class);
            setFlag(DebugFlags.HighlighterInitialized);
        }
    }

    public static boolean isVoxelShapeHighlighterEnabled() {
        return isFlagSet(DebugFlags.HighlighterInitialized);
    }

    public static void addVoxelShapeHighlight(final Level world, final BlockPos position, final VoxelShapeType type) {

        if (!isVoxelShapeHighlighterEnabled()) {
            throw new IllegalStateException("ZeroCore Voxel shape highlighting debug facility must be initialized before using it");
        }

        if (null == s_blockToHighlight) {
            s_blockToHighlight = Maps.newHashMap();
        }

        s_blockToHighlight.computeIfAbsent(world, k -> Maps.newHashMap()).put(position, type);
    }

    public static void removeVoxelShapeHighlight(final Level world, final BlockPos position) {

        if (null != s_blockToHighlight) {
            s_blockToHighlight.getOrDefault(world, Collections.emptyMap()).remove(position);
        }
    }

    public static VoxelShapeType getBlockVoxelShapeHighlight(final Level world, final BlockPos position) {

        if (!isVoxelShapeHighlighterEnabled()) {
            throw new IllegalStateException("ZeroCore Voxel shape highlighting debug facility must be initialized before using it");
        }

        if (null == s_blockToHighlight) {
            return VoxelShapeType.None;
        }

        return s_blockToHighlight.getOrDefault(world, Collections.emptyMap())
                .getOrDefault(position, VoxelShapeType.None);
    }

    public static void ungrabMouse() {
        ZeroCore.getProxy().debugUngrabMouse();
    }

    //endregion
    //region internals

    private enum DebugFlags {

        HighlighterInitialized,
    };

    private static boolean isFlagSet(final DebugFlags flag) {
        return null != s_flags && s_flags.contains(flag);
    }

    private static void setFlag(final DebugFlags flag) {

        if (null == s_flags) {
            s_flags = new Flags<>(flag);
        } else {
            s_flags.add(flag);
        }
    }

    private static Flags<DebugFlags> s_flags;
    private static Map<Level, Map<BlockPos, VoxelShapeType>> s_blockToHighlight;

    //endregion
}

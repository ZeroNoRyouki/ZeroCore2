/*
 *
 * Patchouli.java
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

package it.zerono.mods.zerocore.lib.compat.patchouli;

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Crafting;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Multiblock;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Smelting;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Spotlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.template.BookTemplate;

import java.util.Map;
import java.util.function.Function;

public class Patchouli {

    public static void registerMultiblock(final ResourceLocation id, final IMultiblock multiblock,
                                          final Function<BlockState, BlockState> renderBlockStateMappers,
                                          final Function<BlockState, IModelData> modelDataMapper) {

        PatchouliAPI.get().registerMultiblock(id, multiblock);
        s_renderBlockStateMappers.put(multiblock, renderBlockStateMappers);
        s_modelDataMappers.put(multiblock, modelDataMapper);
    }

    public static BlockState getRenderBlockStateFor(final IMultiblock multiblock, final BlockState blockState) {
        return s_renderBlockStateMappers.getOrDefault(multiblock, bs -> bs).apply(blockState);
    }

    public static IModelData getModelDataFor(final IMultiblock multiblock, final BlockState blockState) {
        return s_modelDataMappers.getOrDefault(multiblock, b -> EmptyModelData.INSTANCE).apply(blockState);
    }

    public static void initialize() {

        Mods.PATCHOULI.ifPresent(() -> () -> {

            if (s_init) {
                return;
            }

            Log.LOGGER.info("Initializing Patchouli custom templates...");

            BookTemplate.registerComponent(ZeroCore.newID("zcspt_multiblock"), Multiblock.class);
            BookTemplate.registerComponent(ZeroCore.newID("zcspt_spotlight"), Spotlight.class);
            BookTemplate.registerComponent(ZeroCore.newID("zcspt_crafting"), Crafting.class);
            BookTemplate.registerComponent(ZeroCore.newID("zcspt_smelting"), Smelting.class);

            s_init = true;
        });
    }

    //region internals

    private static boolean s_init = false;
    private static final Map<IMultiblock, Function<BlockState, BlockState>> s_renderBlockStateMappers = Maps.newHashMap();
    private static final Map<IMultiblock, Function<BlockState, IModelData>> s_modelDataMappers = Maps.newHashMap();

    //endregion
}

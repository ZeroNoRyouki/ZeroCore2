/*
 *
 * PatchouliSetup.java
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
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Crafting;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Multiblock;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Smelting;
import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Spotlight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.template.BookTemplate;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Patchouli {

    public static void registerMultiblock(final ResourceLocation id, final IMultiblock multiblock,
                                          final Function<Block, BlockState> renderBlockStateMappers,
                                          final Function<Block, IModelData> modelDataMapper) {

        PatchouliAPI.get().registerMultiblock(id, multiblock);
        s_renderBlockStateMappers.put(multiblock, renderBlockStateMappers);
        s_modelDataMappers.put(multiblock, modelDataMapper);
    }

    public static BlockState getRenderBlockStateFor(final IMultiblock multiblock, final Block block) {
        return s_renderBlockStateMappers.getOrDefault(multiblock, Block::getDefaultState).apply(block);
    }

    public static IModelData getModelDataFor(final IMultiblock multiblock, final Block block) {
        return s_modelDataMappers.getOrDefault(multiblock, b -> EmptyModelData.INSTANCE).apply(block);
    }

    @SubscribeEvent
    public static void onClientInit(final FMLClientSetupEvent event) {

        Mods.PATCHOULI.ifPresent(() -> () -> {

            Log.LOGGER.info("Initializing Patchouli custom templates...");

            BookTemplate.registerComponent("zcsptMultiblock", Multiblock.class);
            BookTemplate.registerComponent("zcsptSpotlight", Spotlight.class);
            BookTemplate.registerComponent("zcsptCrafting", Crafting.class);
            BookTemplate.registerComponent("zcsptSmelting", Smelting.class);
        });
    }

    //region internals

    private static final Map<IMultiblock, Function<Block, BlockState>> s_renderBlockStateMappers = Maps.newHashMap();
    private static final Map<IMultiblock, Function<Block, IModelData>> s_modelDataMappers = Maps.newHashMap();

    //endregion
}

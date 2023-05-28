/*
 *
 * Content.java
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

package it.zerono.mods.zerocore.internal.gamecontent;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import it.zerono.mods.zerocore.lib.event.CommonEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Content {

    public static void initialize() {

        final IEventBus modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        ITEMS.register(modBus);
        FEATURES.register(modBus);
        CommonEvents.onAddCreativeTabContent(CreativeModeTabs.TOOLS_AND_UTILITIES,
                (tab, enabledFeatureSet, showOpOnlyItems, output) -> output.accept(Content.DEBUG_TOOL));
    }

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ZeroCore.MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ZeroCore.MOD_ID);

    public static final RegistryObject<DebugToolItem> DEBUG_TOOL = ITEMS.register("debugtool", DebugToolItem::new);
}

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
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.component.FluidStackListComponent;
import it.zerono.mods.zerocore.lib.data.component.FluidTankComponent;
import it.zerono.mods.zerocore.lib.data.component.ItemStackListComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Content {

    public static void initialize(IEventBus modBus) {

        ITEMS.register(modBus);
        FEATURES.register(modBus);
        DATA_COMPONENTS.register(modBus);

        modBus.addListener(Content::modifyCreativeTabs);
    }

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ZeroCore.MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, ZeroCore.MOD_ID);
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ZeroCore.MOD_ID);

    public static final Supplier<@NotNull DebugToolItem> DEBUG_TOOL = ITEMS.register("debugtool", DebugToolItem::new);

    public static final Supplier<@NotNull DataComponentType<ItemStackListComponent>> ITEMSTACK_COMPONENT_TYPE =
            registerComponent("itemstacks", ItemStackListComponent.CODECS);

    public static final Supplier<@NotNull DataComponentType<FluidStackListComponent>> FLUIDSTACK_COMPONENT_TYPE =
            registerComponent("fluidstacks", FluidStackListComponent.CODECS);

    public static final Supplier<@NotNull DataComponentType<FluidTankComponent>> FLUIDTANK_COMPONENT_TYPE =
            registerComponent("fluid_tank", FluidTankComponent.CODECS);

    //region internals

    private static <Type> Supplier<@NotNull DataComponentType<Type>>
    registerComponent(String name, ModCodecs<Type, ? super RegistryFriendlyByteBuf> codecs) {
        return DATA_COMPONENTS.register(name, () -> DataComponentType.<Type>builder()
                .persistent(codecs.codec())
                .networkSynchronized(codecs.streamCodec())
                .build());
    }

    private static void modifyCreativeTabs(BuildCreativeModeTabContentsEvent event) {

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Content.DEBUG_TOOL.get());
        }
    }

    //endregion
}

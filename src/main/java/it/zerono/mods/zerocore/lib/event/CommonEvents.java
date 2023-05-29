package it.zerono.mods.zerocore.lib.event;

import it.zerono.mods.zerocore.lib.item.CreativeTabContentBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.common.Mod;

// temp class until V3
public class CommonEvents {

    //region Creative Tabs events

    /**
     * Creates a new {@link CreativeModeTab} and its content.
     *
     * @param name A unique name for the {@link CreativeModeTab}.
     * @param tabBuilder A builder for the {@link CreativeModeTab} itself.
     * @param contentBuilder A builder for the content of the {@link CreativeModeTab}. This builder will replace any
     *                       items generator already added to the {@link CreativeModeTab} builder.
     */
    public static void onCreateCreativeTab(ResourceLocation name, NonNullConsumer<CreativeModeTab.Builder> tabBuilder,
                                           CreativeTabContentBuilder contentBuilder) {

        Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener((CreativeModeTabEvent.Register event) ->
                event.registerCreativeModeTab(name, builder -> {

                    tabBuilder.accept(builder);
                    builder.displayItems((enabledFeatures, output, showOpOnlyItems) ->
                            contentBuilder.build(null, enabledFeatures, showOpOnlyItems, output::accept));
                }));
    }

    public static void onAddCreativeTabsContent(CreativeTabContentBuilder contentBuilder) {
        Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener((CreativeModeTabEvent.BuildContents event) ->
                contentBuilder.build(event.getTab(), event.getFlags(), event.hasPermissions(), event::accept));
    }

    public static void onAddCreativeTabContent(CreativeModeTab tab, CreativeTabContentBuilder contentBuilder) {
        Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener((CreativeModeTabEvent.BuildContents event) -> {

            if (event.getTab() == tab) {
                contentBuilder.build(tab, event.getFlags(), event.hasPermissions(), event::accept);
            }
        });
    }

    //endregion
}

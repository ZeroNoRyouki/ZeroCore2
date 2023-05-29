package it.zerono.mods.zerotest.datagen;

import it.zerono.mods.zerocore.lib.datagen.ForgeModDataGenerator;
import it.zerono.mods.zerotest.ZeroTest;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeDataGenerationHandler {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        new ForgeModDataGenerator(event, ZeroTest.ROOT_LOCATION, new DataGenerationHandler());
    }
}

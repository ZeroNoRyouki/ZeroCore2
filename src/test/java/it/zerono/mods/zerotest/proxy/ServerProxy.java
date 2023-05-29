package it.zerono.mods.zerotest.proxy;

import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import it.zerono.mods.zerotest.Log;
import it.zerono.mods.zerotest.test.DebugTest;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public class ServerProxy
        extends CommonProxy {

    @SubscribeEvent
    @Override
    public void onServerInit(FMLDedicatedServerSetupEvent event) {

        Log.LOGGER.info(Log.TEST, "ZeroTest server init...");

        DebugToolItem.setTestCallback(new DebugTest());
    }
}

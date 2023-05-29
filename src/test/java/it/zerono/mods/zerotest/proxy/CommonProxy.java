package it.zerono.mods.zerotest.proxy;

import it.zerono.mods.zerotest.Log;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public abstract class CommonProxy
        implements ITestProxy {

    //region IModInitializationHandler

    @SubscribeEvent
    @Override
    public void onCommonInit(FMLCommonSetupEvent event) {
        Log.LOGGER.info(Log.TEST, "ZeroTest common init...");
    }

    //endregion
}

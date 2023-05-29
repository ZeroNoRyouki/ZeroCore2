package it.zerono.mods.zerotest;

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.init.IModInitializationHandler;
import it.zerono.mods.zerotest.proxy.ClientProxy;
import it.zerono.mods.zerotest.proxy.ITestProxy;
import it.zerono.mods.zerotest.proxy.ServerProxy;
import it.zerono.mods.zerotest.test.content.TestContent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(value = ZeroTest.MOD_ID)
public class ZeroTest
        implements IModInitializationHandler {

    public static final String MOD_ID = "zerotest";
    public static final String MOD_NAME = "Zero TEST";
    public static ResourceLocationBuilder ROOT_LOCATION = ResourceLocationBuilder.of(MOD_ID);

    public ZeroTest() {

        Log.LOGGER.info(Log.TEST, "ZeroTest starting...");

        s_proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(s_proxy);

        TestContent.initialize();
    }

    public static ITestProxy getProxy() {

        if (null == s_proxy) {
            throw new IllegalStateException("ZeroTest::getProxy called before mod instantiation!");
        }

        return s_proxy;
    }

    //region internals

    private static ITestProxy s_proxy;

    //endregion
}

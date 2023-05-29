package it.zerono.mods.zerotest.proxy;

import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import it.zerono.mods.zerotest.Log;
import it.zerono.mods.zerotest.test.client.ClientDebugTest;
import it.zerono.mods.zerotest.test.content.TestContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy
        extends CommonProxy {

    @SubscribeEvent
    @Override
    public void onClientInit(FMLClientSetupEvent event) {

        Log.LOGGER.info(Log.TEST, "ZeroTest client init...");

        DebugToolItem.setTestCallback(new ClientDebugTest());

        ItemBlockRenderTypes.setRenderLayer(TestContent.Blocks.TEST_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(TestContent.Blocks.TEST_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(TestContent.Blocks.TEST_GLASS_PANE.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public void onColorHandlerEvent(final RegisterColorHandlersEvent.Block event) {
        event.register((state, tintGetter, position, index) -> 0xFF0000FF, TestContent.Blocks.TEST_LEAVES.get());
    }
}

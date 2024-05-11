package it.zerono.mods.zerocore.internal.proxy;

import net.neoforged.bus.api.IEventBus;

public interface IForgeProxy
        extends IProxy {

    void initialize(IEventBus modEventBus);
}

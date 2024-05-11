package it.zerono.mods.zerocore.lib.compat.patchouli;

import it.zerono.mods.zerocore.lib.compat.ModDependencyServiceLoader;

public interface IPatchouliClientService {

    ModDependencyServiceLoader<IPatchouliClientService> SERVICE = new ModDependencyServiceLoader<>("patchouli",
            IPatchouliClientService.class, () -> () -> {});

    void initialize();
}

//package it.zerono.mods.zerocore.internal.compat.patchouli;
//
//import it.zerono.mods.zerocore.ZeroCore;
//import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliClientService;
//import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Crafting;
//import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Multiblock;
//import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Smelting;
//import it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage.Spotlight;
//import vazkii.patchouli.client.book.template.BookTemplate;
//
//public class PatchouliClientService
//        implements IPatchouliClientService {
//
//    public PatchouliClientService() {
//
//        BookTemplate.registerComponent(ZeroCore.ROOT_LOCATION.buildWithSuffix("zcspt_multiblock"), Multiblock.class);
//        BookTemplate.registerComponent(ZeroCore.ROOT_LOCATION.buildWithSuffix("zcspt_spotlight"), Spotlight.class);
//        BookTemplate.registerComponent(ZeroCore.ROOT_LOCATION.buildWithSuffix("zcspt_crafting"), Crafting.class);
//        BookTemplate.registerComponent(ZeroCore.ROOT_LOCATION.buildWithSuffix("zcspt_smelting"), Smelting.class);
//    }
//
//    @Override
//    public void initialize() {
//    }
//}

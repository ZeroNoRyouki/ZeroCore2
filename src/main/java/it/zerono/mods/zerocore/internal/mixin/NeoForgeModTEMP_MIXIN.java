package it.zerono.mods.zerocore.internal.mixin;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.neoforged.neoforge.common.NeoForgeMod")
public class NeoForgeModTEMP_MIXIN {

    @Shadow
    @Final
    private static DeferredRegister<FluidIngredientType<?>> FLUID_INGREDIENT_TYPES;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void register_ingredients(IEventBus modEventBus, Dist dist, ModContainer container, CallbackInfo ci) {
        FLUID_INGREDIENT_TYPES.register(modEventBus);
    }
}

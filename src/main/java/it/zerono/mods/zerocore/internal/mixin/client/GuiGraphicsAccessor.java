package it.zerono.mods.zerocore.internal.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

    @Invoker("innerBlit")
    void zerocore_invokeInnerBlit(Function<ResourceLocation, RenderType> renderTypeGetter,
                                  ResourceLocation textureMap, int x1, int x2, int y1, int y2,
                                  float minU, float maxU, float minV, float maxV, int colourARGB);
}

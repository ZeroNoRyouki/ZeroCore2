package it.zerono.mods.zerocore.lib.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidTankComponent(int capacity, FluidStack content) {

    public static final ModCodecs<FluidTankComponent, RegistryFriendlyByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("capacity").forGetter(FluidTankComponent::capacity),
                            FluidStack.CODEC.fieldOf("content").forGetter(FluidTankComponent::content)
                    ).apply(instance, FluidTankComponent::new)
            ),
            StreamCodec.composite(
                    ByteBufCodecs.INT, FluidTankComponent::capacity,
                    FluidStack.OPTIONAL_STREAM_CODEC, FluidTankComponent::content,
                    FluidTankComponent::new
            )
    );

    public static DataComponentType<FluidTankComponent> getComponentType() {
        return Content.FLUIDTANK_COMPONENT_TYPE.get();
    }

    public Fluid getFluid() {
        return this.content.getFluid();
    }

    public int getFluidAmount() {
        return this.content.getAmount();
    }
}

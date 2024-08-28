package it.zerono.mods.zerocore.lib.data.component;

import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidStackListComponent
        extends AbstractStackListComponent<FluidStack, Fluid> {

    public static final ModCodecs<FluidStackListComponent, RegistryFriendlyByteBuf> CODECS = createCodecs(
            FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC, FluidStackListComponent::new);

    public FluidStackListComponent(NonNullList<FluidStack> stacks) {
        super(StackAdapters.FLUIDSTACK, stacks);
    }

    public static DataComponentType<FluidStackListComponent> getComponentType() {
        return Content.FLUIDSTACK_COMPONENT_TYPE.get();
    }
}

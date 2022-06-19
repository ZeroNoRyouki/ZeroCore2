package it.zerono.mods.zerocore.lib.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.IFluidTypeRenderProperties;

import javax.annotation.Nullable;
import java.util.Objects;

public class SimpleFluidTypeRenderProperties
        implements IFluidTypeRenderProperties {

    public SimpleFluidTypeRenderProperties(final int tint, final ResourceLocation stillTexture, final ResourceLocation flowingTexture,
                                           final @Nullable ResourceLocation overlayTexture) {

        this._tint = tint;
        this._stillTexture = Objects.requireNonNull(stillTexture);
        this._flowingTexture = Objects.requireNonNull(flowingTexture);
        this._overlayTexture = overlayTexture;
    }

    //region IFluidTypeRenderProperties

    @Override
    public ResourceLocation getStillTexture() {
        return this._stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return this._flowingTexture;
    }

    @Nullable
    @Override
    public ResourceLocation getOverlayTexture() {
        return this._overlayTexture;
    }

    @Override
    public int getColorTint() {
        return this._tint;
    }

    //endregion
    //region internals

    private final int _tint;
    private final ResourceLocation _stillTexture;
    private final ResourceLocation _flowingTexture;
    private final ResourceLocation _overlayTexture;

    //endregion
}

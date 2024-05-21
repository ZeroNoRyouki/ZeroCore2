package it.zerono.mods.zerocore.lib.item;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class TintedBucketItem
        extends BucketItem {

    public TintedBucketItem(Fluid fluid, Properties properties) {
        this(fluid, properties,
                (stack, tintIndex) -> 1 == tintIndex ? 0xFF000000 | IClientFluidTypeExtensions.of(fluid).getTintColor() : 0xFFFFFFFF);
    }

    public TintedBucketItem(Fluid fluid, Properties properties,
                            BiFunction<@NotNull ItemStack, Integer, Integer> colorProvider) {

        super(fluid, properties);

        Preconditions.checkNotNull(colorProvider, "Color provider must not be null");
        this._colorProvider = colorProvider;
    }

    public static int getTintColour(ItemStack stack, int tintIndex) {

        if (stack.getItem() instanceof TintedBucketItem bucket) {
            return bucket._colorProvider.apply(stack, tintIndex);
        }

        return 0xFFFFFFFF;
    }

    //region internals

    private final BiFunction<@NotNull ItemStack, Integer, Integer> _colorProvider;

    //endregion
}

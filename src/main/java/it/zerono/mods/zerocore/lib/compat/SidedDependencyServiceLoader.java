package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Load a service if the current distribution is a physical client or the provided fallback service if the current distribution is a physical server
 *
 * @param <T> The service type
 */
public class SidedDependencyServiceLoader<T>
        extends DependencyServiceLoader<T> {

    /**
     * Initializes a newly created {@code SidedDependencyServiceLoader} object
     *
     * @param service The {@link Class} of the service
     * @param physicalSideFallbackFactory A {@link Supplier} for the physical side service implementation
     */
    public SidedDependencyServiceLoader(Class<T> service, Supplier<@NotNull T> physicalSideFallbackFactory) {

        super(supplier(service, physicalSideFallbackFactory, FMLEnvironment.dist));
        this._distribution = FMLEnvironment.dist;
    }

    public Dist getSide() {
        return this._distribution;
    }

    //region internals

    private static <T> com.google.common.base.Supplier<@NotNull T> supplier(Class<T> service,
                                                                            Supplier<@NotNull T> physicalSideFallbackFactory,
                                                                            @SuppressWarnings("SameParameterValue") Dist distribution) {

        Preconditions.checkNotNull(physicalSideFallbackFactory, "Physical side fallback factory must not be null");

        if (distribution.isClient()) {
            return loadOrFail(service);
        } else {
            return physicalSideFallbackFactory::get;
        }
    }

    private final Dist _distribution;

    //endregion
}

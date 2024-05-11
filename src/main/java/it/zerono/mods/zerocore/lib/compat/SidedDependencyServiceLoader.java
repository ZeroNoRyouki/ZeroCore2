package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.ServiceLoader;
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
    public SidedDependencyServiceLoader(Class<T> service, Supplier<T> physicalSideFallbackFactory) {

        super(buildSupplier(service, physicalSideFallbackFactory));
        this._distribution = FMLEnvironment.dist;
    }

    public Dist getSide() {
        return this._distribution;
    }

    //region internals

    private static <T> com.google.common.base.Supplier<T> buildSupplier(Class<T> service, Supplier<T> fallbackFactory) {

        Preconditions.checkNotNull(service, "Service must not be null");
        Preconditions.checkNotNull(fallbackFactory, "Fallback factory must not be null");

        final com.google.common.base.Supplier<T> supplier;

        if (FMLEnvironment.dist.isClient()) {
            supplier = () -> ServiceLoader.load(service)
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("Failed to load mod service for " + service.getName()));
        } else {
            supplier = fallbackFactory::get;
        }

        return supplier;
    }

    private final Dist _distribution;

    //endregion
}

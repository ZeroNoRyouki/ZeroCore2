package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.CodeHelper;

import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Load a service if the specified mod is loaded or the provided fallback service if the mod is not loaded
 *
 * @param <T> The service type
 */
public class ModDependencyServiceLoader<T>
    extends DependencyServiceLoader<T> {

    /**
     * Initializes a newly created {@code ModDependencyServiceLoader} object
     *
     * @param modId The ID of the mod
     * @param service The {@link Class} of the service
     * @param fallbackFactory A {@link Supplier} for the fallback service implementation
     */
    public ModDependencyServiceLoader(String modId, Class<T> service, Supplier<T> fallbackFactory) {

        super(buildSupplier(modId, service, fallbackFactory));
        this._modId = modId;
    }

    public String getId() {
        return this._modId;
    }

    public boolean isModLoaded() {
        return CodeHelper.isModLoaded(this.getId());
    }

    //region internals

    private static <T> com.google.common.base.Supplier<T> buildSupplier(String modId, Class<T> service,
                                                                        Supplier<T> fallbackFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(modId), "Mod ID must not be null or empty");
        Preconditions.checkNotNull(service, "Service must not be null");
        Preconditions.checkNotNull(fallbackFactory, "Fallback factory must not be null");

        final com.google.common.base.Supplier<T> supplier;

        if (CodeHelper.isModLoaded(modId)) {
            supplier = () -> ServiceLoader.load(service)
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("Failed to load mod service for " + service.getName()));
        } else {
            supplier = fallbackFactory::get;
        }

        return supplier;
    }

    private final String _modId;

    //endregion
}

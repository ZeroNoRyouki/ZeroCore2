package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import it.zerono.mods.zerocore.internal.Log;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Load a service using {@link ServiceLoader} to avoid class loading issues
 *
 * @param <T> The service type
 */
public class DependencyServiceLoader<T>
        implements Supplier<@NotNull T>, Consumer<Consumer<@NotNull T>> {

    /**
     * Initializes a newly created {@code DependencyServiceLoader} object
     *
     * @param service The {@link Class} of the service
     */
    public DependencyServiceLoader(Class<T> service) {
        this(loadOrFail(service));
    }

    @ApiStatus.Internal
    protected DependencyServiceLoader(com.google.common.base.Supplier<@NotNull T> supplier) {

        Preconditions.checkNotNull(supplier, "Supplier must not be null");
        this._service = Suppliers.memoize(supplier);
    }

    @Override
    public void accept(Consumer<@NotNull T> consumer) {
        consumer.accept(this.get());
    }

    @Override
    public T get() {
        return this._service.get();
    }

    //region internals

    protected static <P> com.google.common.base.Supplier<@NotNull P> loadOrFallback(Class<P> service,
                                                                                    Supplier<@NotNull P> fallbackFactory) {

        Preconditions.checkNotNull(service, "Service must not be null");
        Preconditions.checkNotNull(fallbackFactory, "Fallback factory must not be null");

        return () -> {

            try {

                return ServiceLoader.load(service)
                        .findFirst()
                        .orElseGet(fallbackFactory);

            } catch (ServiceConfigurationError ex) {

                Log.LOGGER.warn(Log.SERVICE_LOADER, "Invalid service definition for {}. Using fallback implementation.",
                        service.getName());
                return fallbackFactory.get();
            }
        };
    }

    protected static <P> com.google.common.base.Supplier<@NotNull P> loadOrFail(Class<P> service) {
        return loadOrFallback(service, () -> {
            throw new IllegalStateException(String.format("Unable to load required service %s", service.getName()));
        });
    }

    private final Supplier<T> _service;

    //endregion
}

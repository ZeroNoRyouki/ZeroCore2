package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Load a service using {@link ServiceLoader} to avoid class loading issues
 *
 * @param <T> The service type
 */
public class DependencyServiceLoader<T>
        implements Supplier<T>, Consumer<Consumer<T>> {

    /**
     * Initializes a newly created {@code DependencyServiceLoader} object
     *
     * @param service The {@link Class} of the service
     */
    public DependencyServiceLoader(Class<T> service) {
        this(buildSupplier(service));
    }

    @ApiStatus.Internal
    DependencyServiceLoader(com.google.common.base.Supplier<T> supplier) {

        Preconditions.checkNotNull(supplier, "Supplier must not be null");
        this._service = Suppliers.memoize(supplier);
    }

    @Override
    public void accept(Consumer<T> consumer) {
        consumer.accept(this.get());
    }

    @Override
    public T get() {
        return this._service.get();
    }

    //region internals

    private static <T> com.google.common.base.Supplier<T> buildSupplier(Class<T> service) {

        Preconditions.checkNotNull(service, "Service must not be null");

        return () -> ServiceLoader.load(service)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load mod service for " + service.getName()));
    }

    private final Supplier<T> _service;

    //endregion
}

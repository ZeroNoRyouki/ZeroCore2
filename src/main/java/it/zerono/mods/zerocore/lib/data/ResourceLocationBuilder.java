package it.zerono.mods.zerocore.lib.data;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ResourceLocationBuilder {

    /**
     * Create a new {@link ResourceLocationBuilder} for the provided namespace with no base path.
     *
     * @param namespace The namespace to be used.
     * @return A new {@link ResourceLocationBuilder}.
     */
    public static ResourceLocationBuilder of(String namespace) {
        return new ResourceLocationBuilder(namespace, null, null);
    }

    /**
     * Create a new {@link ResourceLocationBuilder} based on the provided {@link ResourceLocation}.
     *
     * @param location The {@link ResourceLocation} whose components will be used to create the new builder.
     * @return A new {@link ResourceLocationBuilder}.
     */
    public static ResourceLocationBuilder of(ResourceLocation location) {

        Preconditions.checkNotNull(location, "Location must not be null");

        String name;
        String path = location.getPath();

        if (path.contains(CodeHelper.PATH_SEPARATOR)) {

            var components = path.split(CodeHelper.PATH_SEPARATOR);

            name = components[components.length - 1];
            components = Arrays.copyOf(components, components.length - 1);
            path = rebuildPath(null, components);

        } else {

            name = path;
            path = null;
        }

        return new ResourceLocationBuilder(location.getNamespace(), path, name);
    }

    /**
     * Create a new {@link ResourceLocationBuilder} for the minecraft namespace with no base path.
     *
     * @return A new {@link ResourceLocationBuilder}.
     */
    public static ResourceLocationBuilder vanilla() {
        return of("minecraft");
    }

    public String namespace() {
        return this._namespace;
    }

    public ResourceLocationBuilder appendPath(String... pathComponents) {

        Preconditions.checkArgument(pathComponents.length > 0, "At least one path component must be provided");

        return new ResourceLocationBuilder(this._namespace, rebuildPath(this._path, pathComponents), this._name);
    }

    /**
     * Create a new {@link ResourceLocationBuilder} using the same namespace and path of this builder and by appending
     * the given suffix to the current name if one is set. If no name is set, the suffix will be used as the new name.
     *
     * <p>The suffix must comply with the namespace validation rules of a {@link ResourceLocation}.</p>
     *
     * @param nameSuffix A non-empty string that will be added after the current name of this builder to
     *                   create the new builder.
     * @return A new {@link ResourceLocationBuilder}.
     */
    public ResourceLocationBuilder append(String nameSuffix) {

        validateName(nameSuffix);

        return new ResourceLocationBuilder(this._namespace, this._path, null != this._name ? this._name + nameSuffix : nameSuffix);
    }

    /**
     * Create a new {@link ResourceLocationBuilder} using the same namespace and path of this builder and by prepending
     * the given prefix to the current name if one is set. If no name is set, the prefix will be used as the new name.
     *
     * <p>The prefix must comply with the namespace validation rules of a {@link ResourceLocation}.</p>
     *
     * @param namePrefix A non-empty string that will be added before the current name of this builder to
     *                   create the new builder.
     * @return A new {@link ResourceLocationBuilder}.
     */
    public ResourceLocationBuilder prepend(String namePrefix) {

        validateName(namePrefix);

        return new ResourceLocationBuilder(this._namespace, this._path, null != this._name ? namePrefix + this._name : namePrefix);
    }

    public ResourceLocation build() {
        return this.build(null, null);
    }

    public ResourceLocation buildWithPrefix(String prefix) {
        return this.build(prefix, null);
    }

    public ResourceLocation buildWithSuffix(String suffix) {
        return this.build(null, suffix);
    }

    //region internals

    private ResourceLocationBuilder(String namespace, @Nullable String path, @Nullable String name) {

        validateNamespace(namespace);

        this._namespace = namespace;
        this._path = path;
        this._name = name;
    }

    private static void validateNamespace(String namespace) {

        if (Strings.isNullOrEmpty(namespace) || !ResourceLocation.isValidNamespace(namespace)) {
            throw new ResourceLocationException("Invalid ResourceLocation namespace: " + namespace);
        }
    }

    private static void validateName(String name) {

        // this should be validated like a namespace

        if (Strings.isNullOrEmpty(name) || !ResourceLocation.isValidNamespace(name)) {
            throw new ResourceLocationException("Invalid ResourceLocation name: " + name);
        }
    }

    /**
     * Build a full path by adding the provided components, if any, to the current path, it there is one.
     *
     * <p>A path separator is added between each components and after the last one.</p>
     *
     * <p>If the current path is null or empty and no path components are provided the result is {@code null}.</p>
     *
     * @param current The current path. Can be null or an empty string.
     * @param pathComponents The path components to add. Can be empty. Any component must be not null or empty and
     *                       be valid for use in a {@link ResourceLocation}.
     * @return The new path or {@code null}.
     */
    @Nullable
    private static String rebuildPath(@Nullable String current, String... pathComponents) {

        final var builder = new StringBuilder(256);

        if (!Strings.isNullOrEmpty(current)) {

            builder.append(current);

            if (!current.endsWith(CodeHelper.PATH_SEPARATOR)) {
                builder.append(CodeHelper.PATH_SEPARATOR);
            }
        }

        for (final var component : pathComponents) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(component),
                    "Every path components must not be null or empty");
            Preconditions.checkArgument(ResourceLocation.isValidPath(component),
                    "Invalid path component for a ResourceLocation: %s", component);

            builder.append(component);

            if (!component.endsWith(CodeHelper.PATH_SEPARATOR)) {
                builder.append(CodeHelper.PATH_SEPARATOR);
            }
        }

        return builder.length() > 0 ? builder.toString() : null;
    }

    private ResourceLocation build(@Nullable String prefix, @Nullable String suffix) {

        String fullName = "";

        if (!Strings.isNullOrEmpty(prefix)) {

            validateName(prefix);
            fullName = prefix;
        }

        if (null != this._name) {
            fullName += this._name;
        }

        if (!Strings.isNullOrEmpty(suffix)) {

            validateName(suffix);
            fullName += suffix;
        }

        Preconditions.checkState(!fullName.isEmpty() || !Strings.isNullOrEmpty(this._path),
                "A path or a name must be set before building a ResourceLocation");

        return ResourceLocation.fromNamespaceAndPath(this._namespace, null != this._path ? this._path + fullName : fullName);
    }

    private final String _namespace;
    @Nullable
    private final String _path;
    @Nullable
    private final String _name;

    //endregion
}

package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParentModel
        implements Supplier<JsonElement> {

    /**
     * Create a parent model with the specified ID.
     *
     * @param id The {@link ResourceLocation ID} of the parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(ResourceLocation id) {

        Preconditions.checkNotNull(id, "ID must not be null");

        return new ParentModel(id, ImmutableSet.of());
    }

    /**
     * Create a parent model with the specified ID.
     *
     * @param id The ID of the parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(String id) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "ID must not be null or empty");

        return of(ResourceLocation.parse(id));
    }

    /**
     * Create a parent model with the specified ID and required texture variables.
     *
     * @param id The {@link ResourceLocation ID} of the parent model.
     * @param textures The texture variables exposed by this parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(ResourceLocation id, String... textures) {

        Preconditions.checkNotNull(id, "ID must not be null");
        Preconditions.checkArgument(textures.length > 0, "At least one texture variable must be specified");

        return new ParentModel(id, ImmutableSet.copyOf(textures));
    }

    /**
     * Create a parent model with the specified ID and required texture variables.
     *
     * @param id The ID of the parent model.
     * @param textures The texture variables exposed by this parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(String id, String... textures) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "ID must not be null or empty");

        return of(ResourceLocation.parse(id), textures);
    }

    /**
     * Create a parent model with the specified ID and required texture variables.
     *
     * @param id The {@link ResourceLocation ID} of the parent model.
     * @param textures The texture variables exposed by this parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(ResourceLocation id, TextureSlot... textures) {

        Preconditions.checkNotNull(id, "ID must not be null");
        Preconditions.checkArgument(textures.length > 0, "At least one texture variable must be specified");

        final var set = Arrays.stream(textures)
                .map(TextureSlot::getId)
                .collect(Collectors.toUnmodifiableSet());

        return new ParentModel(id, set);
    }

    /**
     * Create a parent model with the specified ID and required texture variables.
     *
     * @param id The ID of the parent model.
     * @param textures The texture variables exposed by this parent model.
     * @return A new {@link ParentModel}.
     */
    public static ParentModel of(String id, TextureSlot... textures) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "ID must not be null or empty");

        return of(ResourceLocation.parse(id), textures);
    }

    /**
     * @return The texture variables exposed by this parent model.
     */
    public Set<String> textures() {
        return this._textures;
    }

    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {
        return Util.make(new JsonObject(), $ -> $.addProperty("parent", this._id.toString()));
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this._id.toString();
    }

    //endregion
    //region internals

    private ParentModel(ResourceLocation id, Set<String> textures) {

        Preconditions.checkNotNull(id);
        if (textures.stream().anyMatch(Strings::isNullOrEmpty)) {
            throw new IllegalArgumentException("All textures must not be empty");
        }

        this._id = id;
        this._textures = textures;
    }

    private final ResourceLocation _id;
    private final Set<String> _textures;

    //endregion
}

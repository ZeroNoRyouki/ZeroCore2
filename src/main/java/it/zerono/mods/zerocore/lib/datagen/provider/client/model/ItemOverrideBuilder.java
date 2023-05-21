package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class ItemOverrideBuilder
        implements Supplier<JsonElement> {

    ItemOverrideBuilder() {
        this._predicates = new Object2FloatArrayMap<>(8);
    }

    /**
     * Add am item predicate.
     *
     * @param itemPredicate The item predicate.
     * @param value The value used by the predicate.
     * @return This builder.
     */
    public ItemOverrideBuilder predicate(ResourceLocation itemPredicate, float value) {

        Preconditions.checkNotNull(itemPredicate, "The item predicate must not be null");
        Preconditions.checkState(!this._predicates.containsKey(itemPredicate), "A value for the provided predicate is already defined");

        this._predicates.put(itemPredicate, value);
        return this;
    }

    /**
     * Sets the model used when the predicates are matched.
     *
     * @param model The model.
     * @return This builder.
     */
    public ItemOverrideBuilder model(ResourceLocation model) {

        Preconditions.checkNotNull(model, "The model must not be null");
        Preconditions.checkState(null == this._model, "A model for this override is already defined");

        this._model = model;
        return this;
    }

    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {

        Preconditions.checkState(null != this._model, "A model must be provided for this override");
        Preconditions.checkState(!this._predicates.isEmpty(), "At least one predicate must be provided for this override");

        final var json = new JsonObject();
        final var predicates = new JsonObject();

        this._predicates.forEach((predicate, value) -> predicates.addProperty(predicate.toString(), value));
        json.add("predicate", predicates);
        json.addProperty("model", this._model.toString());

        return json;
    }

    //endregion
    //region internals

    private final Object2FloatMap<ResourceLocation> _predicates;
    private ResourceLocation _model;

    //endregion
}

package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public record UV(float U1, float V1, float U2, float V2)
        implements Supplier<JsonElement> {

    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {

        final var array = new JsonArray();

        array.add(this.U1);
        array.add(this.V1);
        array.add(this.U2);
        array.add(this.V2);

        return array;
    }

    //endregion
}

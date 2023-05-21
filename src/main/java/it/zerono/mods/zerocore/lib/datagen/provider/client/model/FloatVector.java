package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.joml.Vector3f;

import java.util.function.Supplier;

public record FloatVector(float X, float Y, float Z)
        implements Supplier<JsonElement> {

    public static FloatVector zero() {
        return new FloatVector(0, 0, 0);
    }

    public static FloatVector of(float x, float y, float z) {
        return new FloatVector(x, y, z);
    }

    public static FloatVector of(Vector3f value) {
        return new FloatVector(value.x, value.y, value.z);
    }

    //region Supplier<JsonElement>

    @Override
    public JsonElement get() {

        final var array = new JsonArray();

        array.add(this.X);
        array.add(this.Y);
        array.add(this.Z);

        return array;
    }

    //endregion
}

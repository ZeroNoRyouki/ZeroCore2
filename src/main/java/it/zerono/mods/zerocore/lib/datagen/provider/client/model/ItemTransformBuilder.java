package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class ItemTransformBuilder
        implements Supplier<JsonElement> {

    ItemTransformBuilder() {

        this._rotation = FloatVector.of(ItemTransform.Deserializer.DEFAULT_ROTATION);
        this._translation = FloatVector.of(ItemTransform.Deserializer.DEFAULT_TRANSLATION);
        this._scale = FloatVector.of(ItemTransform.Deserializer.DEFAULT_SCALE);
    }

    /**
     * Specifies the rotation of the model.
     *
     * @param x Rotation on the X axis.
     * @param y Rotation on the Y axis.
     * @param z Rotation on the Z axis.
     * @return This builder.
     */
    public ItemTransformBuilder rotation(float x, float y, float z) {

        this._rotation = FloatVector.of(x, y, z);
        return this;
    }

    /**
     * Specifies the translated position of the model.
     *
     * @param x Translation on the X axis. Must be between -80 and 80.
     * @param y Translation on the Y axis. Must be between -80 and 80.
     * @param z Translation on the Z axis. Must be between -80 and 80.
     * @return This builder.
     */
    public ItemTransformBuilder translation(float x, float y, float z) {

        this._translation = FloatVector.of(x, y, z);
        return this;
    }

    /**
     * Specifies how to rescale the model.
     *
     * @param x Scale on the X axis. Values greater than 4 will be clamped to 4.
     * @param y Scale on the Y axis. Values greater than 4 will be clamped to 4.
     * @param z Scale on the Z axis. Values greater than 4 will be clamped to 4.
     * @return This builder.
     */
    public ItemTransformBuilder scale(float x, float y, float z) {

        this._scale = FloatVector.of(x, y, z);
        return this;
    }

    /**
     * Specifies how to rescale the model.
     *
     * @param scale The scale. Values greater than 4 will be clamped to 4.
     * @return This builder.
     */
    public ItemTransformBuilder scale(float scale) {
        return this.scale(scale, scale, scale);
    }

    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {

        final var transform = new JsonObject();

        transform.add("translation", this._translation.get());
        transform.add("rotation", this._rotation.get());
        transform.add("scale", this._scale.get());
        return transform;
    }

    //endregion
    //region internals

    private FloatVector _rotation;
    private FloatVector _translation;
    private FloatVector _scale;

    //endregion
}

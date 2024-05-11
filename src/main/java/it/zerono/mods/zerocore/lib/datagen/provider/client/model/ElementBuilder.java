package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.functional.NonNullBiConsumer;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ElementBuilder
        implements Supplier<JsonElement> {

    public ElementBuilder() {

        this._faces = new Object2ObjectArrayMap<>(CodeHelper.DIRECTIONS.length);
        this._shade = true;
    }

    /**
     * Sets the starting point of the cuboid.
     *
     * @param x The X coordinate. Must be between -16 and 32.
     * @param y The Y coordinate. Must be between -16 and 32.
     * @param z The Z coordinate. Must be between -16 and 32.
     * @return This builder.
     */
    public ElementBuilder from(float x, float y, float z) {

        Preconditions.checkState(null == this._from, "The starting point of the cuboid is already defined for this element");

        this._from = cuboidVertex(x, y, z);
        return this;
    }

    /**
     * Sets the ending point of the cuboid.
     *
     * @param x The X coordinate. Must be between -16 and 32.
     * @param y The Y coordinate. Must be between -16 and 32.
     * @param z The Z coordinate. Must be between -16 and 32.
     * @return This builder.
     */
    public ElementBuilder to(float x, float y, float z) {

        Preconditions.checkState(null == this._to, "The ending point of the cuboid is already defined for this element");

        this._to = cuboidVertex(x, y, z);
        return this;
    }

    /**
     * Sets the rotation of the element being built.
     *
     * @param builder A builder used to define the rotation.
     * @return This builder.
     */
    public ElementBuilder rotation(NonNullConsumer<RotationBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder cannot be null");
        Preconditions.checkState(null == this._rotation, "A rotation is already defined for this element");

        this._rotation = new RotationBuilder();
        builder.accept(this._rotation);
        return this;
    }

    /**
     * Sets a face of the element being built.
     * If a face is left out, it will not be rendered.
     *
     * @param face The face being defined.
     * @param builder A builder used to define the face.
     * @return This builder.
     */
    public ElementBuilder face(Direction face, NonNullConsumer<FaceBuilder> builder) {

        Preconditions.checkNotNull(face, "Face cannot be null");
        Preconditions.checkNotNull(builder, "Builder cannot be null");
        Preconditions.checkState(!this._faces.containsKey(face), "The specified face is already defined for this element");

        this._faces.put(face, Util.make(new FaceBuilder(), builder::accept));
        return this;
    }

    /**
     * Sets all faces of the element being built.
     *
     * @param builder A builder used to define the single faces, one at the time.
     * @return This builder.
     */
    public ElementBuilder faces(NonNullBiConsumer<Direction, FaceBuilder> builder) {
        return this.faces(builder, new ObjectArrayList<>(CodeHelper.DIRECTIONS));
    }

    /**
     * Sets all faces of the element being built except the specified ones.
     * If a face is left out, it will not be rendered.
     *
     * @param builder A builder used to define the single faces, one at the time.
     * @param ignored The faces that should not be defined.
     * @return This builder.
     */
    public ElementBuilder faces(NonNullBiConsumer<Direction, FaceBuilder> builder, Direction... ignored) {

        final var directions = new ObjectArrayList<>(CodeHelper.DIRECTIONS);

        if (ignored.length > 0) {
            directions.removeAll(new ObjectArrayList<>(ignored));
        }

        return this.faces(builder, directions);
    }

    /**
     * Defines if shadows are rendered for the element being built.
     * Default value is true.
     *
     * @param shade If true shadows are rendered.
     * @return This builder.
     */
    public ElementBuilder shade(boolean shade) {

        this._shade = shade;
        return this;
    }

    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {

        final var json = new JsonObject();

        json.add("from", null != this._from ? this._from.get() : FloatVector.zero().get());
        json.add("to", null != this._to ? this._to.get() : FloatVector.of(16, 16, 16).get());

        if (null != this._rotation) {
            json.add("rotation", this._rotation.get());
        }

        if (!this._shade) {
            json.addProperty("shade", false);
        }

        if (!this._faces.isEmpty()) {

            final var faces = new JsonObject();

            this._faces.forEach(((direction, face) -> faces.add(direction.getSerializedName(), face.get())));
            json.add("faces", faces);
        }

        return json;
    }

    //endregion
    //region Rotation builder

    public static class RotationBuilder
            implements Supplier<JsonElement> {

        RotationBuilder() {

            this._origin = FloatVector.zero();
            this._axis = Direction.Axis.X;
        }

        /**
         * Sets the center of the rotation.
         *
         * @param x The X coordinate.
         * @param y The Y coordinate.
         * @param z The Z coordinate.
         * @return This builder.
         */
        public RotationBuilder origin(float x, float y, float z) {

            this._origin = FloatVector.of(x, y, z);
            return this;
        }

        /**
         * Specifies the direction of rotation.
         *
         * @param axis The direction.
         * @return This builder.
         */
        public RotationBuilder axis(Direction.Axis axis) {

            Preconditions.checkNotNull(axis, "Rotation axis cannot be null");

            this._axis = axis;
            return this;
        }

        /**
         * Specifies the angle of rotation.
         *
         * @param angle The angle. Must be 45 through -45 degrees in 22.5 degree increments.
         * @return This builder.
         */
        public RotationBuilder angle(float angle) {

            Preconditions.checkArgument(0.0f == angle || 22.5f == Mth.abs(angle) || 45.0f == Mth.abs(angle),
                    "Invalid rotation angle %f found. Allowed values are -45 / -22.5 / 0 / 22.5 / 45", angle);

            this._angle = angle;
            return this;
        }

        /**
         * Specifies whether to scale the faces across the whole block.
         * Default is false.
         *
         * @param rescale If true, the faces will be scaled.
         * @return This builder.
         */
        public RotationBuilder rescale(boolean rescale) {

            this._rescale = rescale;
            return this;
        }

        //region Supplier<JsonElement>

        @ApiStatus.Internal
        @Override
        public JsonElement get() {

            final var json = new JsonObject();

            json.add("origin", this._origin.get());
            json.addProperty("axis", this._axis.getName());

            if (0 != this._angle) {
                json.addProperty("angle", this._angle);
            }

            if (this._rescale) {
                json.addProperty("rescale", true);
            }

            return json;
        }

        //endregion
        //region internals

        private FloatVector _origin;
        private Direction.Axis _axis;
        private float _angle;
        private boolean _rescale;

        //endregion
    }

    //endergion
    //region Face builder

    public static class FaceBuilder
            implements Supplier<JsonElement> {

        FaceBuilder() {
            this._tintIndex = -1;
        }

        /**
         * Defines the area of the texture to use for the face being built.
         * If the numbers of u1 and u2 are swapped the texture flips.
         *
         * @param u1 Starting coordinate of the texture on the X axis.
         * @param v1 Starting coordinate of the texture on the Y axis.
         * @param u2 Ending coordinate of the texture on the X axis.
         * @param v2 Ending coordinate of the texture on the Y axis.
         * @return This builder.
         */
        public FaceBuilder uv(float u1, float v1, float u2, float v2) {

            Preconditions.checkState(null == this._uv, "UV is already defined for this face");

            this._uv = new UV(u1, v1, u2, v2);
            return this;
        }

        /**
         * Sets the texture to be used for this face.
         *
         * @param texture A texture variable or a {@link net.minecraft.resources.ResourceLocation} in text form.
         *
         * @return This builder.
         */
        public FaceBuilder texture(String texture) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(texture), "Texture cannot be null or an empty string");
            Preconditions.checkState(null == this._texture, "Texture is already defined for this face");

            this._texture = texture;
            return this;
        }

        /**
         * Specifies whether a face does not need to be rendered when there is a block touching it in the
         * specified position.
         *
         * @param cullface The position.
         * @return This builder.
         */
        public FaceBuilder cullface(Direction cullface) {

            Preconditions.checkNotNull(cullface, "Cullface cannot be null");
            Preconditions.checkState(null == this._cullface, "The face to be culled is already defined for this face");

            this._cullface = cullface;
            return this;
        }

        /**
         * Rotates the texture by the specified number of degrees.
         * Default is 0.
         *
         * @param rotation Number of degrees to rotate the texture for. Must be 0, 90, 180, or 270.
         * @return This builder.
         */
        public FaceBuilder rotation(int rotation) {

            Preconditions.checkArgument(0 == rotation || 90 == rotation || 180 == rotation || 270 == rotation,
                    "Rotation can only be 0, 90, 180 or 270");
            this._rotation = rotation;
            return this;
        }

        /**
         * Determines whether to tint the texture using the specified tint index. See {@link BlockColors}.
         * Default is -1 (no tinting).
         *
         * @param index The tint index.
         * @return This builder.
         */
        public FaceBuilder tintIndex(int index) {

            this._tintIndex = index;
            return this;
        }

        //region Supplier<JsonElement>

        @ApiStatus.Internal
        @Override
        public JsonElement get() {

            Preconditions.checkState(null != this._texture, "A texture was not defined for this face");

            final var json = new JsonObject();

            if (null != this._uv) {
                json.add("uv", this._uv.get());
            }

            json.addProperty("texture", JSONHelper.serializeTextureName(this._texture));

            if (null != this._cullface) {
                json.addProperty("cullface", this._cullface.getSerializedName());
            }

            if (0 != this._rotation) {
                json.addProperty("rotation", this._rotation);
            }

            if (-1 != this._tintIndex) {
                json.addProperty("tintindex", this._tintIndex);
            }

            return json;
        }

        //endregion
        //region internals

        @Nullable
        private UV _uv;
        private String _texture;
        private Direction _cullface;
        private int _rotation;
        private int _tintIndex;

        //endregion
    }

    //endregion
    //region internals

    private static FloatVector cuboidVertex(float x, float y, float z) {

        if (x < -16 || x > 32) {
            throw new IllegalArgumentException("X must be between -16 and 32");
        }

        if (y < -16 || y > 32) {
            throw new IllegalArgumentException("Y must be between -16 and 32");
        }

        if (z < -16 || z > 32) {
            throw new IllegalArgumentException("Z must be between -16 and 32");
        }

        return FloatVector.of(x, y, z);
    }

    private ElementBuilder faces(NonNullBiConsumer<Direction, FaceBuilder> builder, List<Direction> directions) {

        Preconditions.checkNotNull(builder, "Builder cannot be null");
        Preconditions.checkState(this._faces.isEmpty(), "One or more faces are already defined for this element");

        for (final var face : directions) {
            this._faces.put(face, Util.make(new FaceBuilder(), $ -> builder.accept(face, $)));
        }

        return this;
    }

    private final Map<Direction, FaceBuilder> _faces;

    private FloatVector _from;
    private FloatVector _to;
    @Nullable
    private RotationBuilder _rotation;
    private boolean _shade;

    //endregion
}

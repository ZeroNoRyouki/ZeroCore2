package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModelFileBuilder
        implements Supplier<JsonElement> {

    public ModelFileBuilder(ResourceLocation id, BiConsumer<@NotNull ResourceLocation, @NotNull Supplier<JsonElement>> sink) {

        this._id = Preconditions.checkNotNull(id);
        this._sink = Preconditions.checkNotNull(sink);
        this._targetIsItem = this._id.getPath().startsWith("item");
        this._elements = new LinkedList<>();
        this._textures = new Object2ObjectArrayMap<>(16);
        this._itemOverrides = new LinkedList<>();
        this._itemTransformations = new Object2ObjectArrayMap<>(ItemDisplayContext.values().length);
        this._ambientOcclusion = true;
        this._guiLight = BlockModel.GuiLight.SIDE;
    }

    public ResourceLocation build() {

        this._sink.accept(this._id, this);

        if (null != this._itemToBeDelegated) {
            this._sink.accept(CodeHelper.getObjectId(this._itemToBeDelegated).withPrefix(ModelBuilder.ITEM_PREFIX + "/"),
                    new DelegatedModel(this._id));;
        }

        return this._id;
    }

    /**
     * When the current model is built, a delegated model for the provided {@link ItemLike} will be build too.
     *
     * @param item The {@link ItemLike} to build a delegated model for.
     * @return This builder.
     */
    public <I extends ItemLike> ModelFileBuilder delegateFor(Supplier<I> item) {

        Preconditions.checkNotNull(item, "Item must not be null");

        this.ensureBlockOnly();
        this._itemToBeDelegated = item.get().asItem();
        return this;
    }

    /**
     * Sets the parent model for the model being built.
     *
     * @param parent The parent model.
     * @return This builder.
     */
    public ModelFileBuilder parent(ParentModel parent) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkState(null == this._parent, "A parent model is already defined: %s", this._parent);

        this._parent = parent;
        parent.textures().forEach(variable -> this._textures.put(variable, null));
        return this;
    }

    public ModelFileBuilder parent(ResourceLocation model) {
        return this.parent(ParentModel.of(model));
    }

    /**
     * Sets the texture for the specified texture variable.
     *
     * @param variable The texture variable.
     * @param texture The texture.
     * @return This builder.
     */
    public ModelFileBuilder texture(String variable, String texture) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(variable), "Variable must be not null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(texture), "Texture must be not null or empty");
        Preconditions.checkState(null == this._textures.get(variable), "A texture is already defined for %s", variable);

        this._textures.put(variable, texture);
        return this;
    }

    /**
     * Sets the texture for the specified texture variable.
     *
     * @param variable The texture variable.
     * @param texture The texture.
     * @return This builder.
     */
    public ModelFileBuilder texture(String variable, ResourceLocation texture) {

        Preconditions.checkNotNull(texture, "Texture must not be null");

        return this.texture(variable, texture.toString());
    }

    /**
     * Sets the texture for the specified texture variable.
     *
     * @param variable The texture variable.
     * @param texture The texture.
     * @return This builder.
     */
    public ModelFileBuilder texture(TextureSlot variable, String texture) {

        Preconditions.checkNotNull(variable, "Variable must not be null");

        return this.texture(variable.getId(), texture);
    }

    /**
     * Sets the texture for the specified texture variable.
     *
     * @param variable The texture variable.
     * @param texture The texture.
     * @return This builder.
     */
    public ModelFileBuilder texture(TextureSlot variable, ResourceLocation texture) {

        Preconditions.checkNotNull(variable, "Variable must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");

        return this.texture(variable.getId(), texture.toString());
    }

    /**
     * Add an element to the model being built.
     *
     * @param builder A builder used to define the new element.
     * @return This builder.
     */
    public ModelFileBuilder element(Consumer<@NotNull ElementBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        this._elements.add(Util.make(new ElementBuilder(), builder::accept));
        return this;
    }

    /**
     * Add a new item transformation to the model being built.
     *
     * @param type The type of the new item transformation.
     * @param builder A builder used to define the new item transformation.
     * @return This builder.
     */
    public ModelFileBuilder transformation(ItemDisplayContext type, Consumer<@NotNull ItemTransformBuilder> builder) {

        Preconditions.checkNotNull(type, "Transformation type must not be null");
        Preconditions.checkNotNull(builder, "Builder must not be null");
        Preconditions.checkState(!this._itemTransformations.containsKey(type), "Item transformation %s is already defined", type);

        this._itemTransformations.put(type, Util.make(new ItemTransformBuilder(), builder::accept));
        return this;
    }

    /**
     * Add a new item model override to the model being built. Available only for item models.
     *
     * @param builder A builder used to define the new override.
     * @return This builder.
     */
    public ModelFileBuilder override(Consumer<@NotNull ItemOverrideBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        this.ensureItemOnly();
        this._itemOverrides.add(Util.make(new ItemOverrideBuilder(), builder::accept));
        return this;
    }

    /**
     * Sets if ambient occlusion should be used or not. This only works for parent models defined for a Block.
     * Default is true.
     *
     * @param useAmbientOcclusion If true, ambient occlusion will be used.
     * @return This builder.
     */
    public ModelFileBuilder ambientOcclusion(boolean useAmbientOcclusion) {

        this.ensureBlockOnly();
        this._ambientOcclusion = useAmbientOcclusion;
        return this;
    }

    /**
     * If set to {@code SIDE}, the default, the model is rendered like a block. If set to {@code FRONT}, the model is
     * shaded like a flat item.
     *
     * @param light How to shade the model.
     * @return This builder.
     */
    public ModelFileBuilder guiLight(BlockModel.GuiLight light) {

        Preconditions.checkNotNull(light, "Light must not be null");

        this.ensureItemOnly();
        this._guiLight = light;
        return this;
    }

    //region Generic models helpers

    /**
     * Creates a model with a parent model and a single texture.
     *
     * @param parent The parent model.
     * @param textureVariable The texture variable.
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation withSingleTextureParent(ParentModel parent, String textureVariable, ResourceLocation texture) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(textureVariable), "Texture variable must not be null or empty");
        Preconditions.checkNotNull(texture, "Texture must not be null");

        return this
                .parent(parent)
                .texture(textureVariable, texture)
                .build();
    }

    /**
     * Creates a model with a parent model and a single texture.
     *
     * @param parent The parent model.
     * @param textureVariable The texture variable.
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation withSingleTextureParent(ParentModel parent, TextureSlot textureVariable, ResourceLocation texture) {

        Preconditions.checkNotNull(textureVariable, "Texture variable must not be null");

        return this.withSingleTextureParent(parent, textureVariable.getId(), texture);
    }

    /**
     * Creates a model with a parent model and two different textures.
     *
     * @param parent The parent model.
     * @param textureVariable1 The first texture variable.
     * @param texture1 The first texture.
     * @param textureVariable2 The second texture variable.
     * @param texture2 The second texture.
     * @return The ID of the new model.
     */
    public ResourceLocation withDoubleTextureParent(ParentModel parent, String textureVariable1, ResourceLocation texture1,
                                                    String textureVariable2, ResourceLocation texture2) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(textureVariable1), "Texture variable 1 must not be null or empty");
        Preconditions.checkNotNull(texture1, "Texture 1 must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(textureVariable2), "Texture variable 2 must not be null or empty");
        Preconditions.checkNotNull(texture2, "Texture 2 must not be null");

        return this
                .parent(parent)
                .texture(textureVariable1, texture1)
                .texture(textureVariable2, texture2)
                .build();
    }

    /**
     * Creates a model with a parent model and two different textures.
     *
     * @param parent The parent model.
     * @param textureVariable1 The first texture variable.
     * @param texture1 The first texture.
     * @param textureVariable2 The second texture variable.
     * @param texture2 The second texture.
     * @return The ID of the new model.
     */
    public ResourceLocation withDoubleTextureParent(ParentModel parent, TextureSlot textureVariable1, ResourceLocation texture1,
                                                    TextureSlot textureVariable2, ResourceLocation texture2) {

        Preconditions.checkNotNull(textureVariable1, "Texture variable 1 must not be null");
        Preconditions.checkNotNull(textureVariable2, "Texture variable 2 must not be null");

        return this.withDoubleTextureParent(parent, textureVariable1.getId(), texture1, textureVariable2.getId(), texture2);
    }

    /**
     * Creates a model with a parent model and bottom, top and side textures.
     *
     * @param parent The parent model.
     * @param bottom The bottom texture.
     * @param top The top texture.
     * @param side The side texture.
     * @return The ID of the new model.
     */
    public ResourceLocation withBottomTopSideTextures(ParentModel parent, ResourceLocation bottom, ResourceLocation top,
                                                      ResourceLocation side) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkNotNull(bottom, "Bottom must not be null");
        Preconditions.checkNotNull(bottom, "Top must not be null");
        Preconditions.checkNotNull(bottom, "Side must not be null");

        return this
                .parent(parent)
                .texture(TextureSlot.BOTTOM, bottom)
                .texture(TextureSlot.TOP, top)
                .texture(TextureSlot.SIDE, side)
                .build();
    }

    //endregion
    //region Generic vanilla models

    /**
     * Creates a model for a standard item.
     *
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation flatItem(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.ITEM_GENERATED, TextureSlot.LAYER0, texture);
    }

    /**
     * Creates a model for a hand held item.
     *
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation flatItemHandheld(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.ITEM_HANDHELD, TextureSlot.LAYER0, texture);
    }

    /**
     * Creates a model for a standard cube with a single texture on all sides.
     *
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cube(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.CUBE_ALL, TextureSlot.ALL, texture);
    }

    /**
     * Creates a model for a standard cube with the specified textures.
     *
     * @param particle The particle texture.
     * @param north The north texture.
     * @param south The south texture.
     * @param east The east texture.
     * @param west The west texture.
     * @param up The up texture.
     * @param down The down texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cube(ResourceLocation particle, ResourceLocation north, ResourceLocation south,
                                 ResourceLocation east, ResourceLocation west, ResourceLocation up, ResourceLocation down) {
        return this
                .parent(ParentModels.CUBE)
                .texture(TextureSlot.PARTICLE, particle)
                .texture(TextureSlot.NORTH, north)
                .texture(TextureSlot.SOUTH, south)
                .texture(TextureSlot.EAST, east)
                .texture(TextureSlot.WEST, west)
                .texture(TextureSlot.UP, up)
                .texture(TextureSlot.DOWN, down)
                .build();
    }

    /**
     * Creates a model for a mirrored cube with a single texture on all sides.
     *
     * @param texture The texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeMirrored(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.CUBE_MIRRORED_ALL, TextureSlot.ALL, texture);
    }

    /**
     * Creates a model for a mirrored cube with the specified textures.
     *
     * @param particle The particle texture.
     * @param north The north texture.
     * @param south The south texture.
     * @param east The east texture.
     * @param west The west texture.
     * @param up The up texture.
     * @param down The down texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeMirrored(ResourceLocation particle, ResourceLocation north, ResourceLocation south,
                                         ResourceLocation east, ResourceLocation west, ResourceLocation up,
                                         ResourceLocation down) {
        return this
                .parent(ParentModels.CUBE_MIRRORED)
                .texture(TextureSlot.PARTICLE, particle)
                .texture(TextureSlot.NORTH, north)
                .texture(TextureSlot.SOUTH, south)
                .texture(TextureSlot.EAST, east)
                .texture(TextureSlot.WEST, west)
                .texture(TextureSlot.UP, up)
                .texture(TextureSlot.DOWN, down)
                .build();
    }

    /**
     * Creates a model for a standard cube orientated horizontally with a texture on the sides and one on both ends.
     *
     * @param side The side texture.
     * @param ends The ends texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeHorizontalColumn(ResourceLocation side, ResourceLocation ends) {
        return this
                .parent(ParentModels.CUBE_COLUMN_HORIZONTAL)
                .texture(TextureSlot.SIDE, side)
                .texture(TextureSlot.END, ends)
                .build();
    }

    /**
     * Creates a model for a standard cube orientated vertically with a texture on the sides and one on both ends.
     *
     * @param side The side texture.
     * @param ends The ends texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeVerticalColumn(ResourceLocation side, ResourceLocation ends) {
        return this
                .parent(ParentModels.CUBE_COLUMN_VERTICAL)
                .texture(TextureSlot.SIDE, side)
                .texture(TextureSlot.END, ends)
                .build();
    }

    /**
     * Creates a model for a standard cube orientated vertically with a texture on the sides, one on top and one on
     * bottom.
     *
     * @param side The side texture.
     * @param top The top texture.
     * @param bottom The bottom texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeVerticalColumn(ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        return this.withBottomTopSideTextures(ParentModels.CUBE_BOTTOM_TOP, bottom, top, side);
    }

    /**
     * Creates a model for an orientable cube with a texture on front, one on the other sides, one on top and one on
     * bottom.
     *
     * @param front The front texture.
     * @param side The other sides texture.
     * @param top The top texture.
     * @param bottom The bottom texture.
     * @return The ID of the new model.
     */
    public ResourceLocation cubeOrientable(ResourceLocation front, ResourceLocation side, ResourceLocation top,
                                           ResourceLocation bottom) {
        return this
                .parent(ParentModels.CUBE_ORIENTABLE)
                .texture(TextureSlot.FRONT, front)
                .texture(TextureSlot.SIDE, side)
                .texture(TextureSlot.TOP, top)
                .texture(TextureSlot.BOTTOM, bottom)
                .build();
    }

    /**
     * Creates a model for an orientable cube with a texture on front, one on the other sides and one at the ends.
     *
     * @param front The front texture.
     * @param side The other sides texture.
     * @param ends The ends texture.
     */
    public ResourceLocation cubeOrientableCapped(ResourceLocation front, ResourceLocation side, ResourceLocation ends) {
        return this
                .parent(ParentModels.CUBE_ORIENTABLE_CAPPED)
                .texture(TextureSlot.FRONT, front)
                .texture(TextureSlot.SIDE, side)
                .texture(TextureSlot.TOP, ends)
                .build();
    }

    //endregion
    //region Vanilla blocks

    public ResourceLocation wood(ResourceLocation wood) {
        return this.cubeVerticalColumn(wood, wood);
    }

    public ResourceLocation woodHorizontalLog(ResourceLocation side, ResourceLocation ends) {
        return this.cubeHorizontalColumn(side, ends);
    }

    public ResourceLocation woodVerticalLog(ResourceLocation side, ResourceLocation ends) {
        return this.cubeVerticalColumn(side, ends);
    }

    public ResourceLocation woodPLanks(ResourceLocation texture) {
        return this.cube(texture);
    }

    public ResourceLocation button(boolean isPressed, ResourceLocation texture) {
        return this.withSingleTextureParent(isPressed ? ParentModels.BUTTON_PRESSED : ParentModels.BUTTON,
                TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation buttonItem(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.BUTTON_ITEM, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation doorClosedBottomLeft(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_CLOSED_BOTTOM_LEFT, TextureSlot.BOTTOM, texture);
    }

    public ResourceLocation doorClosedBottomRight(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_CLOSED_BOTTOM_RIGHT, TextureSlot.BOTTOM, texture);
    }

    public ResourceLocation doorClosedTopLeft(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_CLOSED_TOP_LEFT, TextureSlot.TOP, texture);
    }

    public ResourceLocation doorClosedTopRight(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_CLOSED_TOP_RIGHT, TextureSlot.TOP, texture);
    }

    public ResourceLocation doorOpenBottomLeft(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_OPEN_BOTTOM_LEFT, TextureSlot.BOTTOM, texture);
    }

    public ResourceLocation doorOpenBottomRight(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_OPEN_BOTTOM_RIGHT, TextureSlot.BOTTOM, texture);
    }

    public ResourceLocation doorOpenTopLeft(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_OPEN_TOP_LEFT, TextureSlot.TOP, texture);
    }

    public ResourceLocation doorOpenTopRight(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.DOOR_OPEN_TOP_RIGHT, TextureSlot.TOP, texture);
    }

    public ResourceLocation doorItem(ResourceLocation texture) {
        return this.flatItem(texture);
    }

    public ResourceLocation fencePost(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_POST, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceSide(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_SIDE, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceItem(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_ITEM, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceGateClosed(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_GATE_CLOSED, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceGateOpen(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_GATE_OPEN, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceGateWallClosed(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_GATE_WALL_CLOSED, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation fenceGateWallOpen(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.FENCE_GATE_WALL_OPEN, TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation wallPost(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.WALL_POST, TextureSlot.WALL, texture);
    }

    public ResourceLocation wallLowSide(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.WALL_LOW_SIDE, TextureSlot.WALL, texture);
    }

    public ResourceLocation wallTallSide(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.WALL_TALL_SIDE, TextureSlot.WALL, texture);
    }

    public ResourceLocation wallItem(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.WALL_ITEM, TextureSlot.WALL, texture);
    }

    public ResourceLocation pressurePlate(boolean isPressed, ResourceLocation texture) {
        return this.withSingleTextureParent(isPressed ? ParentModels.PRESSURE_PLATE_PRESSED : ParentModels.PRESSURE_PLATE,
                TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation slab(boolean isTop, ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
        return this.withBottomTopSideTextures(isTop ? ParentModels.SLAB_TOP : ParentModels.SLAB, bottom, top, side);
    }

    public ResourceLocation leaves(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.LEAVES, TextureSlot.ALL, texture);
    }

    public ResourceLocation stairsStraight(ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
        return this.withBottomTopSideTextures(ParentModels.STAIRS_STRAIGHT, bottom, top, side);
    }

    public ResourceLocation stairsInner(ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
        return this.withBottomTopSideTextures(ParentModels.STAIRS_INNER, bottom, top, side);
    }

    public ResourceLocation stairsOuter(ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
        return this.withBottomTopSideTextures(ParentModels.STAIRS_OUTER, bottom, top, side);
    }

    public ResourceLocation trapdoorTop(boolean orientable, ResourceLocation texture) {
        return this.withSingleTextureParent(orientable ? ParentModels.TRAPDOOR_TOP_ORIENTABLE : ParentModels.TRAPDOOR_TOP,
                TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation trapdoorBottom(boolean orientable, ResourceLocation texture) {
        return this.withSingleTextureParent(orientable ? ParentModels.TRAPDOOR_BOTTOM_ORIENTABLE : ParentModels.TRAPDOOR_BOTTOM,
                TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation trapdoorOpen(boolean orientable, ResourceLocation texture) {
        return this.withSingleTextureParent(orientable ? ParentModels.TRAPDOOR_OPEN_ORIENTABLE : ParentModels.TRAPDOOR_OPEN,
                TextureSlot.TEXTURE, texture);
    }

    public ResourceLocation railFlat(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.RAIL_FLAT, TextureSlot.RAIL, texture);
    }

    public ResourceLocation railCorner(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.RAIL_CORNER, TextureSlot.RAIL, texture);
    }

    public ResourceLocation railRaisedNE(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.RAIL_RAISED_NE, TextureSlot.RAIL, texture);
    }

    public ResourceLocation railRaisedSW(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.RAIL_RAISED_SW, TextureSlot.RAIL, texture);
    }

    public ResourceLocation railItem(ResourceLocation texture) {
        return this.flatItem(texture);
    }

    public ResourceLocation glassPaneNoSide(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.STAINED_GLASS_PANE_NOSIDE, TextureSlot.PANE, texture);
    }

    public ResourceLocation glassPaneNoSideAlt(ResourceLocation texture) {
        return this.withSingleTextureParent(ParentModels.STAINED_GLASS_PANE_NOSIDE_ALT, TextureSlot.PANE, texture);
    }

    public ResourceLocation glassPanePost(ResourceLocation pane, ResourceLocation edge) {
        return this.withDoubleTextureParent(ParentModels.STAINED_GLASS_PANE_POST, TextureSlot.PANE, pane,
                TextureSlot.EDGE, edge);
    }

    public ResourceLocation glassPaneSide(ResourceLocation pane, ResourceLocation edge) {
        return this.withDoubleTextureParent(ParentModels.STAINED_GLASS_PANE_SIDE, TextureSlot.PANE, pane,
                TextureSlot.EDGE, edge);
    }

    public ResourceLocation glassPaneSideAlt(ResourceLocation pane, ResourceLocation edge) {
        return this.withDoubleTextureParent(ParentModels.STAINED_GLASS_PANE_SIDE_ALT, TextureSlot.PANE, pane,
                TextureSlot.EDGE, edge);
    }

    public ResourceLocation glassPaneItem(ResourceLocation texture) {
        return this.flatItem(texture);
    }

    //endregion
    //region Supplier<JsonElement>

    @ApiStatus.Internal
    @Override
    public JsonElement get() {

        final var json = null != this._parent ? this._parent.get().getAsJsonObject() : new JsonObject();

        if (!this._ambientOcclusion) {
            json.addProperty("ambientocclusion", false);
        }

        if (!this._itemTransformations.isEmpty()) {

            final var display = new JsonObject();

            this._itemTransformations.entrySet().stream()
                    .filter(e -> e.getKey() != ItemDisplayContext.NONE)
                    .forEach(e -> display.add(e.getKey().getSerializedName(), e.getValue().get()));

            if (!display.isEmpty()) {
                json.add("display", display);
            }
        }

        if (!this._textures.isEmpty()) {

            final var textures = new JsonObject();

            for (final var entry : this._textures.entrySet()) {

                Preconditions.checkState(null != entry.getValue(), "Missing texture for variable %s", entry.getKey());
                textures.addProperty(entry.getKey(), JSONHelper.serializeTextureName(entry.getValue()));
            }

            json.add("textures", textures);
        }

        if (BlockModel.GuiLight.SIDE != this._guiLight) {
            json.addProperty("gui_light", this._guiLight.getSerializedName());
        }

        if (!this._elements.isEmpty()) {
            json.add("elements", JSONHelper.toArray(this._elements));
        }

        if (!this._itemOverrides.isEmpty()) {
            json.add("overrides", JSONHelper.toArray(this._itemOverrides));
        }

        return json;
    }

    //endregion
    //region internals

    protected void ensureBlockOnly() {

        if (this._targetIsItem) {
            throw new UnsupportedOperationException("Operation permitted only on block models");
        }
    }

    protected void ensureItemOnly() {

        if (!this._targetIsItem) {
            throw new UnsupportedOperationException("Operation permitted only on item models");
        }
    }

    private final ResourceLocation _id;
    private final boolean _targetIsItem;
    private final BiConsumer<@NotNull ResourceLocation, @NotNull Supplier<JsonElement>> _sink;
    private final List<Supplier<JsonElement>> _elements;
    private final Map<ItemDisplayContext, ItemTransformBuilder> _itemTransformations;
    private final List<Supplier<JsonElement>> _itemOverrides;
    private final Object2ObjectMap<String, String> _textures;

    @Nullable
    private ParentModel _parent;
    @Nullable
    private Item _itemToBeDelegated;
    private boolean _ambientOcclusion;
    private BlockModel.GuiLight _guiLight;

    //endregion
}

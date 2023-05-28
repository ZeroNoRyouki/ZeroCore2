package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.AbstractDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ModelBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class BlockStateDataProvider
        extends AbstractDataProvider {

    public BlockStateDataProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  ResourceLocationBuilder modLocationRoot) {

        super(name, output, lookupProvider, modLocationRoot);

        this._pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this._generators = new Object2ObjectArrayMap<>(128);
        this._models = new ModelBuilder(output);
        this._blocksRoot = modLocationRoot.appendPath(ModelBuilder.BLOCK_PREFIX);
        this._itemsRoot = modLocationRoot.appendPath(ModelBuilder.ITEM_PREFIX);
    }

    public ResourceLocationBuilder blocksRoot() {
        return this._blocksRoot;
    }

    public ResourceLocationBuilder itemsRoot() {
        return this._itemsRoot;
    }

    /**
     * Get the {@link ModelBuilder} used to build models for this builder.
     * @return The {@link ModelBuilder}.
     */
    public final ModelBuilder models() {
        return this._models;
    }

    /**
     * Return a builder for a block state with a single, default, variant ("").
     *
     * @param block The block whose block state will be generated.
     * @return The {@link SingleVariantBuilder} builder.
     */
    public <B extends Block> SingleVariantBuilder singleVariant(B block) {
        return this.add(new SingleVariantBuilder(this._models, block));
    }

    /**
     * Return a builder for a block state with a single, default, variant ("").
     *
     * @param block The block whose block state will be generated.
     * @return The {@link SingleVariantBuilder} builder.
     */
    public <B extends Block> SingleVariantBuilder singleVariant(Supplier<B> block) {

        Preconditions.checkNotNull(block, "Block must not be null");

        return this.singleVariant(block.get());
    }

    /**
     * Return a builder for a block state with multiple variants.
     *
     * @param block The block whose block state will be generated.
     * @return The {@link MultiVariantBuilder} builder.
     */
    public <B extends Block> MultiVariantBuilder multiVariant(B block) {
        return this.add(new MultiVariantBuilder(this._models, block));
    }

    /**
     * Return a builder for a block state with multiple variants.
     *
     * @param block The block whose block state will be generated.
     * @param ignoredProperties Block state properties that will be ignored while building the selectors.
     * @return The {@link MultiVariantBuilder} builder.
     */
    public <B extends Block> MultiVariantBuilder multiVariant(B block, Property<?>... ignoredProperties) {
        return this.add(new MultiVariantBuilder(this._models, block, ImmutableSet.copyOf(ignoredProperties)));
    }

    /**
     * Return a builder for a block state with multiple variants.
     *
     * @param block The block whose block state will be generated.
     * @return The {@link MultiVariantBuilder} builder.
     */
    public <B extends Block> MultiVariantBuilder multiVariant(Supplier<B> block) {

        Preconditions.checkNotNull(block, "Block must not be null");

        return this.multiVariant(block.get());
    }

    /**
     * Return a builder for a block state with multiple variants.
     *
     * @param block The block whose block state will be generated.
     * @param ignoredProperties Block state properties that will be ignored while building the selectors.
     * @return The {@link MultiVariantBuilder} builder.
     */
    public <B extends Block> MultiVariantBuilder multiVariant(Supplier<B> block, Property<?>... ignoredProperties) {

        Preconditions.checkNotNull(block, "Block must not be null");

        return this.multiVariant(block.get(), ignoredProperties);
    }

    /**
     * Return a builder for a block state with multiple parts.
     *
     * @param block The block whose block state will be generated.
     * @return The {@link MultiPartBuilder} builder.
     */
    public <B extends Block> MultiPartBuilder multiPart(B block) {
        return this.add(new MultiPartBuilder(this._models, block));
    }

    /**
     * Return a builder for a block state with multiple parts.
     *
     * @param block The block whose block state will be generated.
     * @return The {@link MultiPartBuilder} builder.
     */
    public <B extends Block> MultiPartBuilder multiPart(Supplier<B> block) {

        Preconditions.checkNotNull(block, "Block must not be null");

        return this.multiPart(block.get());
    }

    //region Vanilla helpers

    /**
     * Create a texture ID from the provided {@link Block}.
     *
     * @param block The {@link Block}.
     * @return The texture ID.
     */
    public <B extends Block> ResourceLocation defaultBlockTexture(Supplier<B> block) {
        return this.models().defaultBlockTexture(block);
    }

    /**
     * Create a texture ID from the provided {@link ItemLike}.
     *
     * @param item The {@link ItemLike}.
     * @return The texture ID.
     */
    public <I extends ItemLike> ResourceLocation defaultItemTexture(Supplier<I> item) {
        return this.models().defaultItemTexture(item);
    }

    public <B extends Block> void simpleBlock(Supplier<B> block) {
        this.cube(block, this.defaultBlockTexture(block));
    }

    public <I extends ItemLike> void simpleItem(Supplier<I> item) {
        this.models().item(item).flatItem(this.defaultItemTexture(item));
    }

    public <B extends Block> void axisAligned(Supplier<B> block,
                                              NonNullFunction<Direction.Axis, ResourceLocation> modelProvider) {

        Preconditions.checkNotNull(block, "Block must not be null");
        Preconditions.checkNotNull(modelProvider, "Model provider must not be null");

        this.multiVariant(block)
                .selector(BlockStateProperties.AXIS, Direction.Axis.X, variant -> variant
                        .model(modelProvider.apply(Direction.Axis.X))
                        .xRotation(90)
                        .yRotation(90))
                .selector(BlockStateProperties.AXIS, Direction.Axis.Y, variant -> variant
                        .model(modelProvider.apply(Direction.Axis.Y)))
                .selector(BlockStateProperties.AXIS, Direction.Axis.Z, variant -> variant
                        .model(modelProvider.apply(Direction.Axis.Z))
                        .xRotation(90));
    }

    public <B extends Block> void axisAligned(Supplier<B> block, ResourceLocation model) {
        this.axisAligned(block, $ -> model);
    }

    public <B extends Block> void cube(Supplier<B> block, ResourceLocation texture) {

        final var model = this.models()
                .block(block)
                .delegateFor(block)
                .cube(texture);

        this.singleVariant(block).model(model);
    }

    public <B extends Block> void wood(Supplier<B> block, ResourceLocation texture) {
        this.axisAligned(block, this.models()
                .block(block)
                .delegateFor(block)
                .wood(texture));
    }

    public <B extends Block> void woodLog(Supplier<B> block, ResourceLocation side, ResourceLocation ends) {

        final var vertical = this.models()
                .block(block)
                .delegateFor(block)
                .woodVerticalLog(side, ends);
        final var horizontal = this.models()
                .block(block, "_horizontal")
                .woodHorizontalLog(side, ends);

        this.axisAligned(block, axis -> axis.isVertical() ? vertical : horizontal);
    }

    public <B extends Block> void woodPlanks(Supplier<B> block, ResourceLocation texture) {
        this.singleVariant(block)
                .model(this.models()
                        .block(block)
                        .delegateFor(block)
                        .woodPLanks(texture));
    }

    public <B extends DoorBlock> void door(Supplier<B> block, ResourceLocation bottomTexture, ResourceLocation topTexture,
                                           ResourceLocation itemTexture) {

        final var models = new ResourceLocation[]{
                // 000
                this.models()
                        .block(block, "_bottom_left")
                        .doorClosedBottomLeft(bottomTexture),
                // 001
                this.models()
                        .block(block, "_bottom_right")
                        .doorClosedBottomRight(bottomTexture),
                // 002
                this.models()
                        .block(block, "_top_left")
                        .doorClosedTopLeft(topTexture),
                // 003
                this.models()
                        .block(block, "_top_right")
                        .doorClosedTopRight(topTexture),
                // 004
                this.models()
                        .block(block, "_bottom_left_open")
                        .doorOpenBottomLeft(bottomTexture),
                // 005
                this.models()
                        .block(block, "_bottom_right_open")
                        .doorOpenBottomRight(bottomTexture),
                // 006
                this.models()
                        .block(block, "_top_left_open")
                        .doorOpenTopLeft(topTexture),
                // 007
                this.models()
                        .block(block, "_top_right_open")
                        .doorOpenTopRight(topTexture),
        };

        this.multiVariant(block, DoorBlock.POWERED)
                .all(((state, builder) -> {

                    final DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
                    final boolean isOpen = state.getValue(DoorBlock.OPEN);

                    int modelIndex = 0;
                    int yRotation = 90 + ((int) state.getValue(DoorBlock.FACING).toYRot());

                    if (DoorHingeSide.RIGHT == hinge) {
                        modelIndex |= 0x1;
                    }

                    if (DoubleBlockHalf.UPPER == state.getValue(DoorBlock.HALF)) {
                        modelIndex |= 0x2;
                    }

                    if (isOpen) {

                        modelIndex |= 0x4;
                        yRotation += 90;

                        if (DoorHingeSide.RIGHT == hinge) {
                            yRotation += 180;
                        }
                    }

                    yRotation %= 360;

                    builder
                            .model(models[modelIndex])
                            .yRotation(yRotation);
                }));

        this.models()
                .item(block)
                .doorItem(itemTexture);
    }

    public <B extends Block> void button(Supplier<B> block, ResourceLocation texture) {

        final var nonPressed = this.models()
                .block(block)
                .button(false, texture);
        final var pressed = this.models()
                .block(block, "_pressed")
                .button(true, texture);

        this.multiVariant(block)
                .all((state, builder) -> {

                    final var model = state.getValue(BlockStateProperties.POWERED) ? pressed : nonPressed;
                    VariantProperties.Rotation xRotation = VariantProperties.Rotation.R0;
                    VariantProperties.Rotation yRotation = VariantProperties.Rotation.R0;
                    boolean uvLock = false;

                    switch (state.getValue(BlockStateProperties.ATTACH_FACE)) {

                        case CEILING -> {

                            xRotation = VariantProperties.Rotation.R180;
                            yRotation = switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {

                                case EAST -> VariantProperties.Rotation.R270;
                                case WEST -> VariantProperties.Rotation.R90;
                                case SOUTH -> VariantProperties.Rotation.R0;
                                case NORTH -> VariantProperties.Rotation.R180;
                                default -> throw new IllegalArgumentException("Invalid facing");
                            };
                        }

                        case WALL -> {

                            uvLock = true;
                            xRotation = VariantProperties.Rotation.R90;
                            yRotation = switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {

                                case EAST -> VariantProperties.Rotation.R90;
                                case WEST -> VariantProperties.Rotation.R270;
                                case SOUTH -> VariantProperties.Rotation.R180;
                                case NORTH -> VariantProperties.Rotation.R0;
                                default -> throw new IllegalArgumentException("Invalid facing");
                            };
                        }

                        case FLOOR -> yRotation = switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {

                            case EAST -> VariantProperties.Rotation.R90;
                            case WEST -> VariantProperties.Rotation.R270;
                            case SOUTH -> VariantProperties.Rotation.R180;
                            case NORTH -> VariantProperties.Rotation.R0;
                            default -> throw new IllegalArgumentException("Invalid facing");
                        };
                    }

                    builder
                            .model(model)
                            .xRotation(xRotation)
                            .yRotation(yRotation)
                            .uvLock(uvLock);
                });

        this.models()
                .item(block)
                .buttonItem(texture);
    }

    public <B extends Block> void fence(Supplier<B> block, ResourceLocation texture) {

        final var post = this.models()
                .block(block, "_post")
                .fencePost(texture);
        final var side = this.models()
                .block(block, "_side")
                .fenceSide(texture);

        this.multiPart(block)
                .part(variant -> variant.model(post))
                .part(BlockStateProperties.NORTH, true, variant -> variant
                        .model(side)
                        .uvLock())
                .part(BlockStateProperties.EAST, true, variant -> variant
                        .model(side)
                        .uvLock()
                        .yRotation(VariantProperties.Rotation.R90))
                .part(BlockStateProperties.SOUTH, true, variant -> variant
                        .model(side)
                        .uvLock()
                        .yRotation(VariantProperties.Rotation.R180))
                .part(BlockStateProperties.WEST, true, variant -> variant
                        .model(side)
                        .uvLock()
                        .yRotation(VariantProperties.Rotation.R270));

        this.models()
                .item(block)
                .fenceItem(texture);
    }

    public <B extends Block> void fenceGate(Supplier<B> block, ResourceLocation texture) {

        final var models = new ResourceLocation[]{
                this.models()
                        .block(block)
                        .delegateFor(block)
                        .fenceGateClosed(texture),
                this.models()
                        .block(block, "_open")
                        .fenceGateOpen(texture),
                this.models()
                        .block(block, "_wall")
                        .fenceGateWallClosed(texture),
                this.models()
                        .block(block, "_wall_open")
                        .fenceGateWallOpen(texture)
        };

        this.multiVariant(block)
                .all((state, builder) -> builder
                        .model(models[(state.getValue(BlockStateProperties.IN_WALL) ? 2 : 0) +
                                (state.getValue(BlockStateProperties.OPEN) ? 1 : 0)])
                        .uvLock()
                        .yRotation(switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                            case EAST -> VariantProperties.Rotation.R270;
                            case NORTH -> VariantProperties.Rotation.R180;
                            case WEST -> VariantProperties.Rotation.R90;
                            case SOUTH -> VariantProperties.Rotation.R0;
                            default -> throw new IllegalStateException("Invalid facing value");
                        }));
    }

    public <B extends Block> void wall(Supplier<B> block, ResourceLocation texture) {

        final var post = this.models()
                .block(block, "_post")
                .wallPost(texture);
        final var lowSide = this.models()
                .block(block, "_side")
                .wallLowSide(texture);
        final var tallSide = this.models()
                .block(block, "_side_tall")
                .wallTallSide(texture);

        this.multiPart(block)
                .part(BlockStateProperties.UP, true, variant -> variant.model(post))
                .part(BlockStateProperties.NORTH_WALL, WallSide.LOW, variant -> variant
                        .model(lowSide)
                        .uvLock())
                .part(BlockStateProperties.EAST_WALL, WallSide.LOW, variant -> variant
                        .model(lowSide)
                        .uvLock()
                        .yRotation(90))
                .part(BlockStateProperties.SOUTH_WALL, WallSide.LOW, variant -> variant
                        .model(lowSide)
                        .uvLock()
                        .yRotation(180))
                .part(BlockStateProperties.WEST_WALL, WallSide.LOW, variant -> variant
                        .model(lowSide)
                        .uvLock()
                        .yRotation(270))
                .part(BlockStateProperties.NORTH_WALL, WallSide.TALL, variant -> variant
                        .model(tallSide)
                        .uvLock())
                .part(BlockStateProperties.EAST_WALL, WallSide.TALL, variant -> variant
                        .model(tallSide)
                        .uvLock()
                        .yRotation(90))
                .part(BlockStateProperties.SOUTH_WALL, WallSide.TALL, variant -> variant
                        .model(tallSide)
                        .uvLock()
                        .yRotation(180))
                .part(BlockStateProperties.WEST_WALL, WallSide.TALL, variant -> variant
                        .model(tallSide)
                        .uvLock()
                        .yRotation(270));

        this.models()
                .item(block)
                .wallItem(texture);
    }

    public <B extends Block> void pressurePlate(Supplier<B> block, ResourceLocation texture) {
        this.multiVariant(block)
                .selector(BlockStateProperties.POWERED, false, variant -> variant
                                .model(model -> model
                                        .block(block)
                                        .delegateFor(block)
                                        .pressurePlate(false, texture)))
                .selector(BlockStateProperties.POWERED, true, variant -> variant
                                .model(model -> model
                                        .block(block, "_down")
                                        .pressurePlate(true, texture)));
    }

    public void slab(Supplier<SlabBlock> block, ResourceLocation doubleSlabsModel, ResourceLocation texture) {
        this.slab(block, doubleSlabsModel, texture, texture, texture);
    }

    public void slab(Supplier<SlabBlock> block, ResourceLocation doubleSlabsModel, ResourceLocation bottom,
                     ResourceLocation top, ResourceLocation side) {
        this.multiVariant(block, BlockStateProperties.WATERLOGGED)
                .selector(SlabBlock.TYPE, SlabType.BOTTOM, variant -> variant
                                .model(model -> model
                                        .block(block)
                                        .delegateFor(block)
                                        .slab(false, bottom, top, side)))
                .selector(SlabBlock.TYPE, SlabType.TOP, variant -> variant
                                .model(model -> model
                                        .block(block, "_top")
                                        .slab(true, bottom, top, side)))
                .selector(SlabBlock.TYPE, SlabType.DOUBLE, variant -> variant.model(doubleSlabsModel));
    }

    public <B extends Block> void leaves(Supplier<B> block, ResourceLocation texture) {
        this.singleVariant(block)
                .variant(variant -> variant
                        .model(model -> model
                                .block(block)
                                .delegateFor(block)
                                .leaves(texture)));
    }

    public void stairs(Supplier<StairBlock> block, ResourceLocation texture) {
        this.stairs(block, texture, texture, texture);
    }

    public void stairs(Supplier<StairBlock> block, ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {

        final var straight = this.models()
                .block(block)
                .delegateFor(block)
                .stairsStraight(bottom, top, side);
        final var inner = this.models()
                .block(block, "_inner")
                .stairsInner(bottom, top, side);
        final var outer = this.models()
                .block(block, "_outer")
                .stairsOuter(bottom, top, side);

        this.multiVariant(block, StairBlock.WATERLOGGED)
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant.model(inner)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant.model(outer)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant.model(straight)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.EAST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .xRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.NORTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant.model(inner)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant.model(outer)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.SOUTH)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(90)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.BOTTOM)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_LEFT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.INNER_RIGHT)
                        .variant(variant -> variant
                                .model(inner)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_LEFT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(180)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
                        .variant(variant -> variant
                                .model(outer)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(270)))
                .selector(selector -> selector
                        .state(StairBlock.FACING, Direction.WEST)
                        .state(StairBlock.HALF, Half.TOP)
                        .state(StairBlock.SHAPE, StairsShape.STRAIGHT)
                        .variant(variant -> variant
                                .model(straight)
                                .uvLock()
                                .xRotation(180)
                                .yRotation(180)));
    }

    public void trapdoor(Supplier<TrapDoorBlock> block, boolean orientable, ResourceLocation texture) {

        final var bottom = this.models()
                .block(block, "_bottom")
                .delegateFor(block)
                .trapdoorBottom(orientable, texture);
        final var top = this.models()
                .block(block, "_top")
                .trapdoorTop(orientable, texture);
        final var open = this.models()
                .block(block, "_open")
                .trapdoorOpen(orientable, texture);

        this.multiVariant(block, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED)
                .all((state, builder) -> {

                    boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
                    boolean isTop = Half.TOP == state.getValue(TrapDoorBlock.HALF);
                    int xRotation = 0;
                    int yRotation = 180 + (int)state.getValue(TrapDoorBlock.FACING).toYRot();

                    if (orientable && isOpen) {

                        if (isTop) {

                            xRotation += 180;
                            yRotation += 180;
                        }

                    } else {

                        yRotation = 0;
                    }

                    yRotation %= 360;

                    builder
                            .model(isOpen ? open : isTop ? top : bottom)
                            .xRotation(xRotation)
                            .yRotation(yRotation);
                });
    }

    public <B extends Block> void passiveRail(Supplier<B> block, ResourceLocation texture, ResourceLocation cornerTexture) {

        final var flat = this.models()
                .block(block)
                .railFlat(texture);
        final var corner = this.models()
                .block(block, "_corner")
                .railCorner(cornerTexture);
        final var raisedNE = this.models()
                .block(block, "_raised_ne")
                .railRaisedNE(texture);
        final var raisedSW = this.models()
                .block(block, "_raised_sw")
                .railRaisedSW(texture);

        this.multiVariant(block)
                .all((state, builder) -> {

                    ResourceLocation model = corner;
                    int yRotation = 0;

                    switch (state.getValue(BlockStateProperties.RAIL_SHAPE)) {

                        case ASCENDING_EAST:
                            yRotation = 90;
                        case ASCENDING_NORTH:
                            model = raisedNE;
                            break;

                        case ASCENDING_WEST:
                            yRotation = 90;
                        case ASCENDING_SOUTH:
                            model = raisedSW;
                            break;

                        case EAST_WEST:
                            yRotation = 90;
                        case NORTH_SOUTH:
                            model = flat;
                            break;

                        case SOUTH_EAST:
                            break;

                        case SOUTH_WEST:
                            yRotation = 90;
                            break;

                        case NORTH_WEST:
                            yRotation = 180;
                            break;

                        case NORTH_EAST:
                            yRotation = 270;
                            break;
                    }

                    builder
                            .model(model)
                            .yRotation(yRotation);
                });

        this.models()
                .item(block)
                .railItem(texture);
    }

    public <B extends Block> void activeRail(Supplier<B> block, ResourceLocation texture) {

        final var flat = this.models()
                .block(block)
                .railFlat(texture);
        final var raisedNE = this.models()
                .block(block, "_raised_ne")
                .railRaisedNE(texture);
        final var raisedSW = this.models()
                .block(block, "_raised_sw")
                .railRaisedSW(texture);
        final var onFlat = this.models()
                .block(block, "_on")
                .railFlat(texture);
        final var onRaisedNE = this.models()
                .block(block, "_on_raised_ne")
                .railRaisedNE(texture);
        final var onRaisedSW = this.models()
                .block(block, "_on_raised_sw")
                .railRaisedSW(texture);

        this.multiVariant(block)
                .all((state, builder) -> {

                    final boolean powered = state.getValue(BlockStateProperties.POWERED);
                    ResourceLocation model;
                    int yRotation = 0;

                    switch (state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {

                        case ASCENDING_EAST:
                            yRotation = 90;
                        case ASCENDING_NORTH:
                            model = powered ? onRaisedNE : raisedNE;
                            break;

                        case ASCENDING_WEST:
                            yRotation = 90;
                        case ASCENDING_SOUTH:
                            model = powered ? onRaisedSW : raisedSW;
                            break;

                        case EAST_WEST:
                            yRotation = 90;
                        case NORTH_SOUTH:
                            model = powered ? onFlat : flat;
                            break;

                        default:
                            throw new IllegalStateException("Invalid rail shape");
                    }

                    builder
                            .model(model)
                            .yRotation(yRotation);
                });

        this.models()
                .item(block)
                .railItem(texture);
    }

    public <B extends Block> void glass(Supplier<B> block, ResourceLocation texture) {
        this.cube(block, texture);
    }

    public <B extends Block> void glassPane(Supplier<B> block, ResourceLocation pane, ResourceLocation edge) {

        final var post = this.models()
                .block(block, "_post")
                .glassPanePost(pane, edge);
        final var side = this.models()
                .block(block, "_side")
                .glassPaneSide(pane, edge);
        final var sideAlt = this.models()
                .block(block, "_side_alt")
                .glassPaneSideAlt(pane, edge);
        final var noSide = this.models()
                .block(block, "_noside")
                .glassPaneNoSide(pane);
        final var noSideAlt = this.models()
                .block(block, "_noside_alt")
                .glassPaneNoSideAlt(pane);

        this.multiPart(block)
                .part(variant -> variant.model(post))
                .part(BlockStateProperties.NORTH, true, variant -> variant.model(side))
                .part(BlockStateProperties.NORTH, false, variant -> variant.model(noSide))
                .part(BlockStateProperties.SOUTH, true, variant -> variant.model(sideAlt))
                .part(BlockStateProperties.SOUTH, false, variant -> variant
                        .model(noSideAlt)
                        .yRotation(90))
                .part(BlockStateProperties.WEST, true, variant -> variant
                        .model(sideAlt)
                        .yRotation(90))
                .part(BlockStateProperties.WEST, false, variant -> variant
                        .model(noSide)
                        .yRotation(270))
                .part(BlockStateProperties.EAST, true, variant -> variant
                        .model(side)
                        .yRotation(90))
                .part(BlockStateProperties.EAST, false, variant -> variant.model(noSideAlt));

        this.models()
                .item(block)
                .glassPaneItem(pane);
    }

    //endregion
    //region IModDataProvider

    @Override
    public CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup) {

        final List<CompletableFuture<?>> futures = new ObjectArrayList<>(1 + this._generators.size());

        futures.add(this._models.apply(cache));

        this._generators.entrySet().stream()
                .map(entry -> DataProvider.saveStable(cache, entry.getValue().get(),
                        this._pathProvider.json(CodeHelper.getObjectId(entry.getKey()))))
                .forEachOrdered(futures::add);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }

    //endregion
    //region internals

    private <G extends BlockStateGenerator> G add(G generator) {

        final var block = generator.getBlock();

        if (this._generators.containsKey(block)) {
            throw new IllegalStateException("A block state generator is already defined for block " + block);
        }

        this._generators.put(block, generator);
        return generator;
    }

    private final Map<Block, BlockStateGenerator> _generators;
    private final ModelBuilder _models;
    private final PackOutput.PathProvider _pathProvider;
    private final ResourceLocationBuilder _blocksRoot;
    private final ResourceLocationBuilder _itemsRoot;

    //endregion
}


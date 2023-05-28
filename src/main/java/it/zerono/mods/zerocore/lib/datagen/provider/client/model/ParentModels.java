package it.zerono.mods.zerocore.lib.datagen.provider.client.model;

import net.minecraft.data.models.model.TextureSlot;

public final class ParentModels {

    public static ParentModel BUILTIN_ENTITY = ParentModel.of("builtin/entity");
    public static ParentModel BUILTIN_GENERATED = ParentModel.of("builtin/generated");
    public static ParentModel ITEM_GENERATED = ParentModel.of("item/generated");
    public static ParentModel ITEM_HANDHELD = ParentModel.of("item/handheld");

    public static ParentModel CUBE = ParentModel.of("block/cube", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
    public static ParentModel CUBE_ALL = ParentModel.of("block/cube_all", TextureSlot.ALL);
    public static ParentModel CUBE_MIRRORED = ParentModel.of("block/cube_mirrored", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
    public static ParentModel CUBE_MIRRORED_ALL = ParentModel.of("block/cube_mirrored_all", TextureSlot.ALL);
    public static final ParentModel CUBE_COLUMN_VERTICAL = ParentModel.of("block/cube_column", TextureSlot.END, TextureSlot.SIDE);
    public static final ParentModel CUBE_COLUMN_HORIZONTAL = ParentModel.of("block/cube_column_horizontal", TextureSlot.END, TextureSlot.SIDE);
    public static final ParentModel CUBE_BOTTOM_TOP = ParentModel.of("block/cube_bottom_top", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ParentModel CUBE_ORIENTABLE = ParentModel.of("block/orientable_with_bottom", TextureSlot.FRONT, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ParentModel CUBE_ORIENTABLE_CAPPED = ParentModel.of("block/orientable", TextureSlot.FRONT, TextureSlot.TOP, TextureSlot.SIDE);

    public static final ParentModel BUTTON = ParentModel.of("block/button", TextureSlot.TEXTURE);
    public static final ParentModel BUTTON_PRESSED = ParentModel.of("block/button_pressed", TextureSlot.TEXTURE);
    public static final ParentModel BUTTON_ITEM = ParentModel.of("block/button_inventory", TextureSlot.TEXTURE);

    public static final ParentModel DOOR_CLOSED_BOTTOM_LEFT = ParentModel.of("block/door_bottom_left", TextureSlot.BOTTOM);
    public static final ParentModel DOOR_CLOSED_BOTTOM_RIGHT = ParentModel.of("block/door_bottom_right", TextureSlot.BOTTOM);
    public static final ParentModel DOOR_CLOSED_TOP_LEFT = ParentModel.of("block/door_top_left", TextureSlot.TOP);
    public static final ParentModel DOOR_CLOSED_TOP_RIGHT = ParentModel.of("block/door_top_right", TextureSlot.TOP);
    public static final ParentModel DOOR_OPEN_BOTTOM_LEFT = ParentModel.of("block/door_bottom_left_open", TextureSlot.BOTTOM);
    public static final ParentModel DOOR_OPEN_BOTTOM_RIGHT = ParentModel.of("block/door_bottom_right_open", TextureSlot.BOTTOM);
    public static final ParentModel DOOR_OPEN_TOP_LEFT = ParentModel.of("block/door_top_left_open", TextureSlot.TOP);
    public static final ParentModel DOOR_OPEN_TOP_RIGHT = ParentModel.of("block/door_top_right_open", TextureSlot.TOP);

    public static final ParentModel FENCE_POST = ParentModel.of("block/fence_post", TextureSlot.TEXTURE);
    public static final ParentModel FENCE_SIDE = ParentModel.of("block/fence_side", TextureSlot.TEXTURE);
    public static final ParentModel FENCE_ITEM = ParentModel.of("block/fence_inventory", TextureSlot.TEXTURE);

    public static final ParentModel FENCE_GATE_CLOSED = ParentModel.of("block/template_fence_gate", TextureSlot.TEXTURE);
    public static final ParentModel FENCE_GATE_OPEN = ParentModel.of("block/template_fence_gate_open", TextureSlot.TEXTURE);
    public static final ParentModel FENCE_GATE_WALL_CLOSED = ParentModel.of("block/template_fence_gate_wall", TextureSlot.TEXTURE);
    public static final ParentModel FENCE_GATE_WALL_OPEN = ParentModel.of("block/template_fence_gate_wall_open", TextureSlot.TEXTURE);

    public static final ParentModel WALL_POST = ParentModel.of("block/template_wall_post", TextureSlot.WALL);
    public static final ParentModel WALL_LOW_SIDE = ParentModel.of("block/template_wall_side", TextureSlot.WALL);
    public static final ParentModel WALL_TALL_SIDE = ParentModel.of("block/template_wall_side_tall", TextureSlot.WALL);
    public static final ParentModel WALL_ITEM = ParentModel.of("block/wall_inventory", TextureSlot.WALL);

    public static final ParentModel PRESSURE_PLATE = ParentModel.of("block/pressure_plate_up", TextureSlot.TEXTURE);
    public static final ParentModel PRESSURE_PLATE_PRESSED = ParentModel.of("block/pressure_plate_down", TextureSlot.TEXTURE);

    public static final ParentModel SLAB = ParentModel.of("block/slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ParentModel SLAB_TOP = ParentModel.of("block/slab_top", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);

    public static ParentModel LEAVES = ParentModel.of("block/leaves", TextureSlot.ALL);

    public static final ParentModel STAIRS_STRAIGHT = ParentModel.of("block/stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ParentModel STAIRS_INNER = ParentModel.of("block/inner_stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ParentModel STAIRS_OUTER = ParentModel.of("block/outer_stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);

    public static final ParentModel TRAPDOOR_TOP = ParentModel.of("block/template_trapdoor_top", TextureSlot.TEXTURE);
    public static final ParentModel TRAPDOOR_BOTTOM = ParentModel.of("block/template_trapdoor_bottom", TextureSlot.TEXTURE);
    public static final ParentModel TRAPDOOR_OPEN = ParentModel.of("block/template_trapdoor_open", TextureSlot.TEXTURE);
    public static final ParentModel TRAPDOOR_TOP_ORIENTABLE = ParentModel.of("block/template_orientable_trapdoor_top", TextureSlot.TEXTURE);
    public static final ParentModel TRAPDOOR_BOTTOM_ORIENTABLE = ParentModel.of("block/template_orientable_trapdoor_bottom", TextureSlot.TEXTURE);
    public static final ParentModel TRAPDOOR_OPEN_ORIENTABLE = ParentModel.of("block/template_orientable_trapdoor_open", TextureSlot.TEXTURE);

    public static final ParentModel RAIL_FLAT = ParentModel.of("block/rail_flat", TextureSlot.RAIL);
    public static final ParentModel RAIL_CORNER = ParentModel.of("block/rail_curved", TextureSlot.RAIL);
    public static final ParentModel RAIL_RAISED_NE = ParentModel.of("block/template_rail_raised_ne", TextureSlot.RAIL);
    public static final ParentModel RAIL_RAISED_SW = ParentModel.of("block/template_rail_raised_sw", TextureSlot.RAIL);

    public static final ParentModel STAINED_GLASS_PANE_NOSIDE = ParentModel.of("block/template_glass_pane_noside", TextureSlot.PANE);
    public static final ParentModel STAINED_GLASS_PANE_NOSIDE_ALT = ParentModel.of("block/template_glass_pane_noside_alt", TextureSlot.PANE);
    public static final ParentModel STAINED_GLASS_PANE_POST = ParentModel.of("block/template_glass_pane_post", TextureSlot.PANE, TextureSlot.EDGE);
    public static final ParentModel STAINED_GLASS_PANE_SIDE = ParentModel.of("block/template_glass_pane_side", TextureSlot.PANE, TextureSlot.EDGE);
    public static final ParentModel STAINED_GLASS_PANE_SIDE_ALT = ParentModel.of("block/template_glass_pane_side_alt", TextureSlot.PANE, TextureSlot.EDGE);

    //region internals

    private ParentModels() {
    }

    //endregion
}

package it.zerono.mods.zerotest.test.content;

/*
 * TestContent
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerotest.ZeroTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.IRON_BLOCK;
import static net.minecraft.world.level.block.Blocks.OAK_DOOR;

public final class TestContent {

    public static void initialize() {

        final var bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        Blocks.initialize(bus);
        Items.initialize(bus);
        CreativeTabs.initialize(bus);
    }

    public final static class Blocks {

        private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ZeroTest.MOD_ID);

        static void initialize(IEventBus bus) {
            BLOCKS.register(bus);
        }

        public static Collection<RegistryObject<Block>> getAll() {
            return BLOCKS.getEntries();
        }

        public static final RegistryObject<Block> TEST_BLOCK = registerTestBlock("block",
                () -> new Block(testBlockProperties()));
        public static final RegistryObject<Block> TEST_BLOCK2 = registerTestBlock("block2",
                () -> new Block(testBlockProperties()));
        public static final RegistryObject<RotatedPillarBlock> TEST_WOOD = registerTestBlock("wood",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .ignitedByLava()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(2.0F)
                        .sound(SoundType.WOOD)));
        public static final RegistryObject<RotatedPillarBlock> TEST_WOOD_LOG = registerTestBlock("woodlog",
                () -> new RotatedPillarBlock(testBlockProperties()));
        public static final RegistryObject<Block> TEST_WOOD_PLANK = registerTestBlock("woodplank",
                () -> new Block(testBlockProperties()));
        public static final RegistryObject<LeavesBlock> TEST_LEAVES = registerTestBlock("leaves",
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
                        .strength(0.2F)
                        .randomTicks()
                        .sound(SoundType.GRASS)
                        .noOcclusion()
                        .isValidSpawn((state, level, position, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT)
                        .isSuffocating((state, level, position) -> false)
                        .isViewBlocking((state, level, position) -> false)));
        public static final RegistryObject<ButtonBlock> TEST_BUTTON = registerTestBlock("button",
                () -> new ButtonBlock(testBlockProperties(), BlockSetType.STONE, 10, true));
        public static final RegistryObject<DoorBlock> TEST_DOOR = registerTestBlock("door",
                () -> new DoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR)
                        .lightLevel($ -> 7)
                        .mapColor(MapColor.COLOR_BLUE), BlockSetType.OAK));
        public static final RegistryObject<FenceBlock> TEST_FENCE = registerTestBlock("fence",
                () -> new FenceBlock(testBlockProperties()));
        public static final RegistryObject<FenceGateBlock> TEST_FENCEGATE = registerTestBlock("fencegate",
                () -> new FenceGateBlock(testBlockProperties(), SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN));
        public static final RegistryObject<WallBlock> TEST_WALL = registerTestBlock("wall",
                () -> new WallBlock(testBlockProperties()));
        public static final RegistryObject<PressurePlateBlock> TEST_PRESSURE_PLATE = registerTestBlock("pressureplate",
                () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, testBlockProperties(), BlockSetType.IRON));
        public static final RegistryObject<SlabBlock> TEST_SLAB = registerTestBlock("slab",
                () -> new SlabBlock(testBlockProperties()));
        public static final RegistryObject<StairBlock> TEST_STAIRS = registerTestBlock("stairs",
                () -> new StairBlock(() -> TEST_BLOCK.get().defaultBlockState(), testBlockProperties()));
        public static final RegistryObject<TrapDoorBlock> TEST_TRAPDOOR = registerTestBlock("trapdoor",
                () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR)
                        .lightLevel($ -> 7)
                        .mapColor(MapColor.COLOR_BLUE), BlockSetType.OAK));
        public static final RegistryObject<TrapDoorBlock> TEST_TRAPDOOR_ORIENTABLE = registerTestBlock("trapdoor_orientable",
                () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR)
                        .lightLevel($ -> 7)
                        .mapColor(MapColor.COLOR_BLUE), BlockSetType.OAK));

        public static final RegistryObject<StainedGlassBlock> TEST_GLASS = registerTestBlock("glass",
                () -> new StainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.of()
                        .strength(0.3F)
                        .sound(SoundType.GLASS)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.HAT)
                        .isValidSpawn((state, level, position, entityType) -> false)
                        .isRedstoneConductor((state, level, position) -> false)
                        .isSuffocating((state, level, position) -> false)
                        .isViewBlocking((state, level, position) -> false)));

        public static final RegistryObject<StainedGlassPaneBlock> TEST_GLASS_PANE = registerTestBlock("glasspane",
                () -> new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.of()
                        .mapColor(MapColor.GRASS)
                        .strength(0.3F)
                        .sound(SoundType.GLASS)
                        .noOcclusion()));

        //region internals

        private static <T extends Block> RegistryObject<T> registerTestBlock(String name, NonNullSupplier<T> supplier) {
            return BLOCKS.register("test_" + name, supplier::get);
        }

        private static BlockBehaviour.Properties testBlockProperties() {
            return BlockBehaviour.Properties.copy(IRON_BLOCK)
                    .lightLevel($ -> 7)
                    .mapColor(MapColor.COLOR_BLUE);
        }

        //endregion
    }

    public static final class Items {

        private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ZeroTest.MOD_ID);

        static void initialize(IEventBus bus) {
            ITEMS.register(bus);
        }

        public static final RegistryObject<BlockItem> TEST_BLOCK = registerTestItemBlock("block",
                () -> Blocks.TEST_BLOCK);
        public static final RegistryObject<BlockItem> TEST_BLOCK2 = registerTestItemBlock("block2",
                () -> Blocks.TEST_BLOCK2);
        public static final RegistryObject<BlockItem> TEST_WOOD = registerTestItemBlock("wood",
                () -> Blocks.TEST_WOOD);
        public static final RegistryObject<BlockItem> TEST_WOOD_LOG = registerTestItemBlock("woodlog",
                () -> Blocks.TEST_WOOD_LOG);
        public static final RegistryObject<BlockItem> TEST_WOOD_PLANK = registerTestItemBlock("woodplank",
                () -> Blocks.TEST_WOOD_PLANK);
        public static final RegistryObject<BlockItem> TEST_LEAVES = registerTestItemBlock("leaves",
                () -> Blocks.TEST_LEAVES);
        public static final RegistryObject<BlockItem> TEST_BUTTON = registerTestItemBlock("button",
                () -> Blocks.TEST_BUTTON);
        public static final RegistryObject<BlockItem> TEST_DOOR = registerTestItemBlock("door",
                () -> Blocks.TEST_DOOR);
        public static final RegistryObject<BlockItem> TEST_FENCE = registerTestItemBlock("fence",
                () -> Blocks.TEST_FENCE);
        public static final RegistryObject<BlockItem> TEST_FENCEGATE = registerTestItemBlock("fencegate",
                () -> Blocks.TEST_FENCEGATE);
        public static final RegistryObject<BlockItem> TEST_WALL = registerTestItemBlock("wall",
                () -> Blocks.TEST_WALL);
        public static final RegistryObject<BlockItem> TEST_PRESSURE_PLATE = registerTestItemBlock("pressureplate",
                () -> Blocks.TEST_PRESSURE_PLATE);
        public static final RegistryObject<BlockItem> TEST_SLAB = registerTestItemBlock("slab",
                () -> Blocks.TEST_SLAB);
        public static final RegistryObject<BlockItem> TEST_STAIRS = registerTestItemBlock("stairs",
                () -> Blocks.TEST_STAIRS);
        public static final RegistryObject<BlockItem> TEST_TRAPDOOR = registerTestItemBlock("trapdoor",
                () -> Blocks.TEST_TRAPDOOR);
        public static final RegistryObject<BlockItem> TEST_TRAPDOOR_ORIENTABLE = registerTestItemBlock("trapdoor_orientable",
                () -> Blocks.TEST_TRAPDOOR_ORIENTABLE);
        public static final RegistryObject<BlockItem> TEST_GLASS = registerTestItemBlock("glass",
                () -> Blocks.TEST_GLASS);
        public static final RegistryObject<BlockItem> TEST_GLASS_PANE = registerTestItemBlock("glasspane",
                () -> Blocks.TEST_GLASS_PANE);

        //region internals

        private static <T extends Block> RegistryObject<BlockItem> registerTestItemBlock(String name,
                                                                                         Supplier<Supplier<T>> block) {
            return registerItemBlock("test_" + name, block);
        }

        private static <T extends Block> RegistryObject<BlockItem> registerItemBlock(String name,
                                                                                     Supplier<Supplier<T>> block) {
            return ITEMS.register(name, () -> new BlockItem(block.get().get(),
                    new Item.Properties().stacksTo(64)));
        }

        //endregion
    }

    public static final class CreativeTabs {

        private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ZeroTest.MOD_ID);

        public static final RegistryObject<CreativeModeTab> TEST_TAB = TABS.register("test_tab", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.zerotest.tab"))
                        .icon(() -> new ItemStack(Items.TEST_BLOCK.get()))
                        .noScrollBar()
                        .withLabelColor(0xff0000)
                        .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                        .withTabsAfter(ZeroTest.ROOT_LOCATION.buildWithSuffix("test_tab_vanilla"))
                        .displayItems((parameters, output) -> {

                            output.accept(Items.TEST_BLOCK.get());
                            output.accept(Items.TEST_BLOCK2.get());
                        })
                        .build()
        );

        public static final RegistryObject<CreativeModeTab> TEST_TAB_VANILLA = TABS.register("test_tab_vanilla", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.zerotest.tab_vanilla"))
                        .icon(() -> new ItemStack(Items.TEST_LEAVES.get()))
                        .noScrollBar()
                        .withLabelColor(0xff0000)
                        .withTabsBefore(ZeroTest.ROOT_LOCATION.buildWithSuffix("test_tab"))
                        .displayItems((parameters, output) -> {

                            output.accept(Items.TEST_WOOD.get());
                            output.accept(Items.TEST_WOOD_LOG.get());
                            output.accept(Items.TEST_WOOD_PLANK.get());
                            output.accept(Items.TEST_LEAVES.get());
                            output.accept(Items.TEST_BUTTON.get());
                            output.accept(Items.TEST_DOOR.get());
                            output.accept(Items.TEST_FENCE.get());
                            output.accept(Items.TEST_FENCEGATE.get());
                            output.accept(Items.TEST_WALL.get());
                            output.accept(Items.TEST_PRESSURE_PLATE.get());
                            output.accept(Items.TEST_SLAB.get());
                            output.accept(Items.TEST_STAIRS.get());
                            output.accept(Items.TEST_TRAPDOOR.get());
                            output.accept(Items.TEST_TRAPDOOR_ORIENTABLE.get());
                            output.accept(Items.TEST_GLASS.get());
                            output.accept(Items.TEST_GLASS_PANE.get());
                        })
                        .build()
        );

        static void initialize(IEventBus bus) {
            TABS.register(bus);
        }
    }

    //region internals

    private TestContent() {
    }

    //endregion
}

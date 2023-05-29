package it.zerono.mods.zerotest.datagen.client;

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.atlas.AtlasSpriteSourcesDataProvider;
import it.zerono.mods.zerotest.ZeroTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AtlasTestProvider
        extends AtlasSpriteSourcesDataProvider {

    public AtlasTestProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                             ResourceLocationBuilder modLocationRoot) {
        super("Atlas config test", output, registryLookup, modLocationRoot);
    }

    @Override
    public void provideData() {

        final var vanilla = ResourceLocationBuilder.vanilla();
        AtlasSpriteSources atlas;

        // blocks
        atlas = this.atlas(this.root().buildWithSuffix("test_blocks"));
        atlas.addDirectory("block", "block/");
        atlas.addDirectory("item", "item/");
        atlas.addDirectory("entity/conduit", "entity/conduit/");
        atlas.addFile("bell_body", vanilla, "entity", "bell");
        atlas.addFile("enchanting_table_book", vanilla, "entity");

        // banner_patterns
        atlas = this.atlas(this.root().buildWithSuffix("test_banner_patterns"));
        atlas.addFile("banner_base", vanilla, "entity");
        atlas.addDirectory("entity/banner", "entity/banner/");

        // beds
        atlas = this.atlas(this.root().buildWithSuffix("test_beds"));
        atlas.addDirectory("entity/bed", "entity/bed/");

        // chests
        atlas = this.atlas(this.root().buildWithSuffix("test_chests"));
        atlas.addDirectory("entity/chest", "entity/chest/");

        // mob_effects
        atlas = this.atlas(this.root().buildWithSuffix("test_mob_effects"));
        atlas.addDirectory("mob_effect");

        // paintings
        atlas = this.atlas(this.root().buildWithSuffix("test_paintings"));
        atlas.addDirectory("painting");

        // particles
        atlas = this.atlas(this.root().buildWithSuffix("test_particles"));
        atlas.addDirectory("particle");

        // shield_patterns
        atlas = this.atlas(this.root().buildWithSuffix("test_shield_patterns"));
        atlas.addFile("shield_base", vanilla, "entity");
        atlas.addFile("shield_base_nopattern", vanilla, "entity");
        atlas.addDirectory("entity/shield", "entity/shield/");

        // shulker_boxes
        atlas = this.atlas(this.root().buildWithSuffix("test_shulker_boxes"));
        atlas.addDirectory("entity/shulker", "entity/shulker/");

        // signs
        atlas = this.atlas(this.root().buildWithSuffix("test_signs"));
        atlas.addDirectory("entity/signs", "entity/signs/");
        // ... add a test sprite in there too
        atlas.addFile("test_sprite", ZeroTest.ROOT_LOCATION, "test");

        // test atlas
        atlas = this.atlas(this.root().buildWithSuffix("test_atlas"));
        atlas.addDirectory("test", "test");
    }
}

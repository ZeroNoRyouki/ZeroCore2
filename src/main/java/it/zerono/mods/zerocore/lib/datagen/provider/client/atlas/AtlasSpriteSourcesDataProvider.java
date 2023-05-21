package it.zerono.mods.zerocore.lib.datagen.provider.client.atlas;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.AbstractCodecDataProvider;
import it.zerono.mods.zerocore.lib.functional.NonNullBiConsumer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AtlasSpriteSourcesDataProvider
        extends AbstractCodecDataProvider<List<SpriteSource>> {

    protected static final ResourceLocation ATLAS_BANNER_PATTERNS = new ResourceLocation("banner_patterns");
    protected static final ResourceLocation ATLAS_BEDS = new ResourceLocation("beds");
    protected static final ResourceLocation ATLAS_BLOCKS = new ResourceLocation("blocks");
    protected static final ResourceLocation ATLAS_CHESTS = new ResourceLocation("chests");
    protected static final ResourceLocation ATLAS_MOB_EFFECTS = new ResourceLocation("mob_effects");
    protected static final ResourceLocation ATLAS_SHIELD_PATTERNS = new ResourceLocation("shield_patterns");
    protected static final ResourceLocation ATLAS_SHULKER_BOXES = new ResourceLocation("shulker_boxes");
    protected static final ResourceLocation ATLAS_SIGNS = new ResourceLocation("signs");
    protected static final ResourceLocation ATLAS_PAINTINGS = new ResourceLocation("paintings");
    protected static final ResourceLocation ATLAS_PARTICLES = new ResourceLocation("particles");

    protected AtlasSpriteSourcesDataProvider(String name, PackOutput output,
                                             CompletableFuture<HolderLookup.Provider> registryLookup,
                                             ResourceLocationBuilder modLocationRoot) {

        super(name, output, PackOutput.Target.RESOURCE_PACK, "atlases", registryLookup, modLocationRoot,
                SpriteSources.FILE_CODEC);
        this._atlases = new Object2ObjectArrayMap<>(16);
    }

    protected final AtlasSpriteSources atlas(ResourceLocation atlas) {

        Preconditions.checkNotNull(atlas, "Atlas must not be null");

        return this._atlases.computeIfAbsent(atlas, $ -> new AtlasSpriteSources());
    }

    //region AtlasSpriteSources

    protected static final class AtlasSpriteSources {

        private AtlasSpriteSources() {
            this._sources = new ObjectArrayList<>(16);
        }

        public AtlasSpriteSources addFile(ResourceLocation file) {

            Preconditions.checkNotNull(file, "File must not be null");

            this._sources.add(new SingleFile(file, Optional.empty()));
            return this;
        }

        public AtlasSpriteSources addFile(ResourceLocation file, ResourceLocation spriteId) {

            Preconditions.checkNotNull(file, "File must not be null");
            Preconditions.checkNotNull(spriteId, "Sprite ID must not be null");

            this._sources.add(new SingleFile(file, Optional.of(spriteId)));
            return this;
        }

        public AtlasSpriteSources addFile(String nameSuffix, ResourceLocationBuilder idBuilder, String... path) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(nameSuffix), "Name suffix must not be null or empty");
            Preconditions.checkNotNull(idBuilder, "ID builder must not be null");

            return this.addFile(idBuilder.appendPath(path).buildWithSuffix(nameSuffix));
        }

        public AtlasSpriteSources addDirectory(String sourcePath) {
            return this.addDirectory(sourcePath, "");
        }

        public AtlasSpriteSources addDirectory(String sourcePath, String prefix) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(sourcePath), "Source path must not be null or empty");
            Preconditions.checkNotNull(prefix, "Prefix I must not be null");

            this._sources.add(new DirectoryLister(sourcePath, prefix));
            return this;
        }

        //region internals

        private final List<SpriteSource> _sources;

        //endregion
    }

    //endregion
    //region AbstractCodecDataProvider

    @Override
    protected void processData(NonNullBiConsumer<ResourceLocation, List<SpriteSource>> consumer) {
        this._atlases.forEach((id, sources) -> consumer.accept(id, sources._sources));
    }

    //endregion
    //region internals

    private final Map<ResourceLocation, AtlasSpriteSources> _atlases;

    //endregion
}

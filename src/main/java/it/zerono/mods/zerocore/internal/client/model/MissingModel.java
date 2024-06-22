package it.zerono.mods.zerocore.internal.client.model;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MissingModel
        implements BakedModel, ResourceManagerReloadListener {

    public static final MissingModel INSTANCE = new MissingModel();

    //region BakedModel

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return this._supplier.get().getQuads(state, direction, random);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random,
                                    ModelData data, @Nullable RenderType renderType) {
        return this._supplier.get().getQuads(state, side, random, data, renderType);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this._supplier.get().useAmbientOcclusion();
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        return this._supplier.get().useAmbientOcclusion(state, data, renderType);
    }

    @Override
    public boolean isGui3d() {
        return this._supplier.get().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this._supplier.get().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this._supplier.get().isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this._supplier.get().getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return this._supplier.get().getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return this._supplier.get().getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return this._supplier.get().applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return this._supplier.get().getModelData(level, pos, state, modelData);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        return this._supplier.get().getRenderTypes(state, random, data);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return this._supplier.get().getRenderTypes(itemStack, fabulous);
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return this._supplier.get().getRenderPasses(itemStack, fabulous);
    }

    //endregion
    //region ResourceManagerReloadListener

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        this.reset();
    }

    //endregion
    //region internals

    private MissingModel() {
        this.reset();
    }

    private void reset() {
        this._supplier = Suppliers.memoize(() -> Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModelShaper()
                .getModelManager()
                .getMissingModel());
    }

    private Supplier<BakedModel> _supplier;

    //endregion
}

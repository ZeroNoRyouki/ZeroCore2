package it.zerono.mods.zerocore.internal.compat.patchouli;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PatchouliService
        implements IPatchouliService {

    public PatchouliService() {

        this._patchouli = PatchouliAPI.get();
        this._renderBlockStateMappers = new HashMap<>();
        this._modelDataMappers = new HashMap<>();
    }

    public BlockState getRenderBlockStateFor(IMultiblock multiblock, BlockState blockState) {
        return this._renderBlockStateMappers.getOrDefault(multiblock, bs -> bs).apply(blockState);
    }

    public ModelData getModelDataFor(IMultiblock multiblock, BlockState blockState) {
        return this._modelDataMappers.getOrDefault(multiblock, b -> ModelData.EMPTY).apply(blockState);
    }

    //region IPatchouliService

    @Override
    public void registerMultiblock(ResourceLocation id, Function<BlockState, BlockState> renderBlockStateMappers,
                                   Function<BlockState, ModelData> modelDataMapper, String[][] pattern,
                                   Object... targets) {

        final var multiblock = this._patchouli.makeMultiblock(pattern, targets);

        this._patchouli.registerMultiblock(id, multiblock);
        this._renderBlockStateMappers.put(multiblock, renderBlockStateMappers);
        this._modelDataMappers.put(multiblock, modelDataMapper);
    }

    @Override
    public void openBookEntry(ResourceLocation bookId, ResourceLocation entryId, int pageNum) {

        Preconditions.checkNotNull(bookId, "Book ID must not be null");
        Preconditions.checkNotNull(entryId, "Entry ID must not be null");
        Preconditions.checkArgument(pageNum >= 0, "Page number must be positive");

        try {
            this._patchouli.openBookEntry(bookId, entryId, pageNum);
        } catch (Exception ex) {
            Log.LOGGER.error(Log.CORE, "Failed to open a Patchouli book.", ex);
        }
    }

    @Override
    public void consumeBookStack(ResourceLocation bookId, Consumer<ItemStack> consumer) {

        final ItemStack stack = this._patchouli.getBookStack(bookId);

        if (!stack.isEmpty()) {
            consumer.accept(stack);
        }
    }

    //endregion
    //region internals

    private final PatchouliAPI.IPatchouliAPI _patchouli;
    private final Map<IMultiblock, Function<BlockState, BlockState>> _renderBlockStateMappers;
    private final Map<IMultiblock, Function<BlockState, ModelData>> _modelDataMappers;

    //endregion
}

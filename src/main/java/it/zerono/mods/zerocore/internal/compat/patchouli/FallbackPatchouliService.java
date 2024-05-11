package it.zerono.mods.zerocore.internal.compat.patchouli;

import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.function.Consumer;
import java.util.function.Function;

public final class FallbackPatchouliService
    implements IPatchouliService {

    //region IPatchouliService

    @Override
    public void registerMultiblock(ResourceLocation id, Function<BlockState, BlockState> renderBlockStateMappers,
                                   Function<BlockState, ModelData> modelDataMapper, String[][] pattern, Object... targets) {
    }

    @Override
    public void openBookEntry(ResourceLocation bookId, ResourceLocation entryId, int pageNum) {
    }

    @Override
    public void consumeBookStack(ResourceLocation bookId, Consumer<ItemStack> consumer) {
    }

    //endregion
}

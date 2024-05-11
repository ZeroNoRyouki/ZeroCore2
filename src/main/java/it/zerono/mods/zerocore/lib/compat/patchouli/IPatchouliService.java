package it.zerono.mods.zerocore.lib.compat.patchouli;

import it.zerono.mods.zerocore.internal.compat.patchouli.FallbackPatchouliService;
import it.zerono.mods.zerocore.lib.compat.ModDependencyServiceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IPatchouliService {

    ModDependencyServiceLoader<IPatchouliService> SERVICE = new ModDependencyServiceLoader<>("patchouli",
            IPatchouliService.class, FallbackPatchouliService::new);

    void registerMultiblock(ResourceLocation id,
                            Function<BlockState, BlockState> renderBlockStateMappers,
                            Function<BlockState, ModelData> modelDataMapper,
                            String[][] pattern, Object... targets);

    void openBookEntry(ResourceLocation bookId, ResourceLocation entryId, int pageNum);

    void consumeBookStack(ResourceLocation bookId, Consumer<ItemStack> consumer);
}

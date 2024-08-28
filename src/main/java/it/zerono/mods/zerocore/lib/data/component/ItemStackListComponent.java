package it.zerono.mods.zerocore.lib.data.component;

import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemStackListComponent
        extends AbstractStackListComponent<ItemStack, Item> {

    public static final ModCodecs<ItemStackListComponent, RegistryFriendlyByteBuf> CODECS = createCodecs(
            ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC, ItemStackListComponent::new);

    public ItemStackListComponent(NonNullList<ItemStack> stacks) {
        super(StackAdapters.ITEMSTACK, stacks);
    }

    public static DataComponentType<ItemStackListComponent> getComponentType() {
        return Content.ITEMSTACK_COMPONENT_TYPE.get();
    }
}

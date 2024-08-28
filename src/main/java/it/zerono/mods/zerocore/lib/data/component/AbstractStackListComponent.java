package it.zerono.mods.zerocore.lib.data.component;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.stack.IStackAdapter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractStackListComponent<StackType, ContentType> {

    protected AbstractStackListComponent(IStackAdapter<StackType, ContentType> adapter,
                                         NonNullList<StackType> stacks) {

        Preconditions.checkNotNull(adapter, "Adapter must not be null");
        Preconditions.checkNotNull(stacks, "Stacks must not be null");

        this._adapter = adapter;
        this._stacks = CodeHelper.asNonNullList(stacks);
    }

    protected static <StackType, ContentType, Component extends AbstractStackListComponent<StackType, ContentType>>
    ModCodecs<Component, RegistryFriendlyByteBuf> createCodecs(Codec<StackType> elementCodec,
                                                               StreamCodec<RegistryFriendlyByteBuf, StackType> streamCodec,
                                                               Function<@NotNull NonNullList<StackType>, @NotNull Component> componentFactory) {
        return new ModCodecs<>(
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                ModCodecs.nonNullListCodec(elementCodec).fieldOf("stacks").forGetter(component -> component._stacks)
                        ).apply(instance, componentFactory)
                ),
                StreamCodec.composite(
                        ModCodecs.nonNullListStreamCodec(streamCodec), component -> component._stacks,
                        componentFactory
                )
        );
    }

    public boolean isEmpty(int index) {
        return this._adapter.isEmpty(this._stacks.get(index));
    }

    public StackType getStack(int index) {
        return this._stacks.get(index);
    }

    public int getAmount(int index) {
        return this._adapter.getAmount(this.getStack(index));
    }

    public void copyInto(NonNullList<StackType> target) {
        CodeHelper.replaceNonNullListElements(target, this._stacks);
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        return this._stacks.equals(((AbstractStackListComponent<?, ?>) other)._stacks);
    }

    @Override
    public int hashCode() {
        return this._stacks.hashCode();
    }

    //endregion
    //region internals

    private final IStackAdapter<StackType, ContentType> _adapter;
    protected final NonNullList<StackType> _stacks;

    //endregion
}

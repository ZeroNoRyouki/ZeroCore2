package it.zerono.mods.zerocore.lib.data.capability;

import com.google.common.base.Preconditions;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerCapabilitySource<Capability, Context> {

    public ItemHandlerCapabilitySource(IItemHandler handler, int slot, ItemCapability<Capability, Context> capability) {

        Preconditions.checkNotNull(capability, "Capability must not be null");
        Preconditions.checkNotNull(handler, "Handler must not be null");
        Preconditions.checkArgument(slot >= 0, "Slot must greater or equal to zero");

        this._capability = capability;
        this._handler = handler;
        this._slot = slot;
    }

    @Nullable
    public Capability getCapability(@Nullable Context context) {
        return this._handler.getStackInSlot(this._slot).getCapability(this._capability, context);
    }

    //region internals

    private final ItemCapability<Capability, Context> _capability;
    private final IItemHandler _handler;
    private final int _slot;

    //endregion
}

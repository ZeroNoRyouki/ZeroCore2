package it.zerono.mods.zerocore.lib.data.capability;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class BlockCapabilitySource<Capability, Context> {

    public static <Capability> BlockCapabilitySource<Capability, @Nullable Direction>
    sided(BlockCapability<Capability, @Nullable Direction> capability) {
        return new BlockCapabilitySource<>(capability);
    }

    public static <Capability> BlockCapabilitySource<Capability, @Nullable Direction>
    sided(BlockCapability<Capability, @Nullable Direction> capability,
          BooleanSupplier isCacheValidSupplier, Runnable capabilityInvalidationListener) {
        return new BlockCapabilitySource<>(capability, isCacheValidSupplier, capabilityInvalidationListener);
    }

    public BlockCapabilitySource(BlockCapability<Capability, Context> capability) {

        Preconditions.checkNotNull(capability, "Capability must not be null");

        this._capability = capability;
        this._isCacheValidSupplier = null;
        this._capabilityInvalidationListener = null;
    }

    public BlockCapabilitySource(BlockCapability<Capability, Context> capability,
                                 BooleanSupplier isCacheValidSupplier, Runnable capabilityInvalidationListener) {

        Preconditions.checkNotNull(capability, "Capability must not be null");
        Preconditions.checkNotNull(isCacheValidSupplier, "Cache validity supplier must not be null");
        Preconditions.checkNotNull(capabilityInvalidationListener, "Capability invalidation listener must not be null");

        this._capability = capability;
        this._isCacheValidSupplier = isCacheValidSupplier;
        this._capabilityInvalidationListener = capabilityInvalidationListener;
    }

    public void setSource(ServerLevel level, BlockPos position, @Nullable Context context) {

        Preconditions.checkNotNull(level, "Level must not be null");
        Preconditions.checkNotNull(position, "Position must not be null");

        if (null != this._cache && this._cache.level() == level && this._cache.pos().equals(position)) {
            return;
        }

        if (null == this._isCacheValidSupplier && null == this._capabilityInvalidationListener) {
            this._cache = BlockCapabilityCache.create(this._capability, level, position, context);
        } else {
            //noinspection DataFlowIssue
            this._cache = BlockCapabilityCache.create(this._capability, level, position, context,
                    this._isCacheValidSupplier, this._capabilityInvalidationListener);
        }
    }

    @Nullable
    public Capability getCapability() {
        return null == this._cache ? null : this._cache.getCapability();
    }

    //region internals

    private final BlockCapability<Capability, Context> _capability;
    @Nullable
    private final BooleanSupplier _isCacheValidSupplier;
    @Nullable
    private final Runnable _capabilityInvalidationListener;

    @Nullable
    private BlockCapabilityCache<Capability, Context> _cache;

    //endregion
}

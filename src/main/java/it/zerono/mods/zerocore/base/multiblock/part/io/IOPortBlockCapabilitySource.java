package it.zerono.mods.zerocore.base.multiblock.part.io;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.capability.BlockCapabilitySource;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class IOPortBlockCapabilitySource<Controller extends AbstractCuboidMultiblockController<Controller>,
        Port extends AbstractMultiblockEntity<Controller> & IIoEntity, Capability>
    extends BlockCapabilitySource<Capability, @Nullable Direction> {

    public IOPortBlockCapabilitySource(Port ioPort, BlockCapability<Capability, @Nullable Direction> capability) {

        super(capability);

        Preconditions.checkNotNull(ioPort, "IO entity must not be null");
        this._ioPort = ioPort;
    }

    public IOPortBlockCapabilitySource(Port ioPort, BlockCapability<Capability, @Nullable Direction> capability,
                                       BooleanSupplier isCacheValidSupplier, Runnable capabilityInvalidationListener) {

        super(capability, isCacheValidSupplier, capabilityInvalidationListener);

        Preconditions.checkNotNull(ioPort, "IO entity must not be null");
        this._ioPort = ioPort;
    }

    public void onPortChanged() {
        this._ioPort.getOutwardDirection().ifPresent(this::updateRemoteCapabilitySource);
    }

    //region internals

    private void updateRemoteCapabilitySource(Direction approachDirection) {

        if (this._ioPort.getCurrentWorld() instanceof ServerLevel level) {
            this.setSource(level, this._ioPort.getWorldPosition().relative(approachDirection),
                    approachDirection.getOpposite());
        }
    }

    private final Port _ioPort;

    //endregion
}

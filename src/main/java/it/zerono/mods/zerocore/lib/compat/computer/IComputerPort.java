package it.zerono.mods.zerocore.lib.compat.computer;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IComputerPort {

    @Nullable
    Connector<? extends ComputerPeripheral<?>> getConnector(Direction direction);
}

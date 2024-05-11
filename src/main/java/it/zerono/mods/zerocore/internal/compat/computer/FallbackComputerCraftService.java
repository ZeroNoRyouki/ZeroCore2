package it.zerono.mods.zerocore.internal.compat.computer;

import it.zerono.mods.zerocore.lib.compat.computer.ComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.computer.Connector;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerCraftService;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerPort;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class FallbackComputerCraftService
        implements IComputerCraftService {

    @Override
    public @Nullable <P extends ComputerPeripheral<P>> Connector<? extends ComputerPeripheral<?>>
    createConnector(String connectionName, P peripheral) {
        return null;
    }

    @Override
    public <BE extends BlockEntity & IComputerPort> void registerCapabilityProvider(RegisterCapabilitiesEvent event,
                                                                                    BlockEntityType<BE> blockEntityType) {
    }
}

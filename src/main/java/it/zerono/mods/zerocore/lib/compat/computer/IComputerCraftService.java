package it.zerono.mods.zerocore.lib.compat.computer;

import it.zerono.mods.zerocore.internal.compat.computer.FallbackComputerCraftService;
import it.zerono.mods.zerocore.lib.compat.ModDependencyServiceLoader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public interface IComputerCraftService {

    ModDependencyServiceLoader<IComputerCraftService> SERVICE = new ModDependencyServiceLoader<>("computercraft",
            IComputerCraftService.class, FallbackComputerCraftService::new);

    @Nullable
    <P extends ComputerPeripheral<P>> Connector<? extends ComputerPeripheral<?>> createConnector(String connectionName,
                                                                                                 P peripheral);

    <BE extends BlockEntity & IComputerPort> void registerCapabilityProvider(RegisterCapabilitiesEvent event,
                                                                             BlockEntityType<BE> blockEntityType);
}

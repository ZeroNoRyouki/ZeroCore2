//package it.zerono.mods.zerocore.internal.compat.computer;
//
//import dan200.computercraft.api.peripheral.IPeripheral;
//import dan200.computercraft.api.peripheral.PeripheralCapability;
//import it.zerono.mods.zerocore.lib.compat.computer.*;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//import org.jetbrains.annotations.Nullable;
//
//public class ComputerCraftService
//        implements IComputerCraftService {
//
//    @Override
//    public @Nullable <P extends ComputerPeripheral<P>> Connector<? extends ComputerPeripheral<?>>
//    createConnector(String connectionName, P peripheral) {
//        return new ConnectorComputerCraft<>(connectionName, peripheral);
//    }
//
//    @Override
//    public <BE extends BlockEntity & IComputerPort> void registerCapabilityProvider(RegisterCapabilitiesEvent event,
//                                                                                    BlockEntityType<BE> blockEntityType) {
//        event.registerBlockEntity(PeripheralCapability.get(), blockEntityType,
//                (be, context) -> be.getConnector(context) instanceof IPeripheral peripheral ? peripheral : null);
//    }
//}

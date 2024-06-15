package it.zerono.mods.zerocore.lib.energy;

import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import net.minecraftforge.fml.LogicalSide;

public class MeteredWideEnergyBuffer
        extends WideEnergyBuffer {

    public MeteredWideEnergyBuffer(EnergySystem system, WideAmount capacity) {
        this(system, capacity, capacity, capacity);
    }

    public MeteredWideEnergyBuffer(EnergySystem system, WideAmount capacity, WideAmount maxTransfer) {
        this(system, capacity, maxTransfer, maxTransfer);
    }

    public MeteredWideEnergyBuffer(EnergySystem system, WideAmount capacity,
                                   WideAmount maxInsert, WideAmount maxExtract) {

        super(system, capacity, maxInsert, maxExtract);
        this._currentInsert = this._currentExtract = this._lastInsert = this._lastExtract = WideAmount.ZERO;
    }

    public void tick() {

        this._lastInsert = this._currentInsert;
        this._lastExtract = this._currentExtract;
        this._currentInsert = this._currentExtract = WideAmount.ZERO;
    }

    public WideAmount getInsertedLastTick(EnergySystem system) {
        return this.convertIf(system, this._lastInsert);
    }

    public WideAmount getExtractedLastTick(EnergySystem system) {
        return this.convertIf(system, this._lastExtract);
    }

    public WideAmount getIoRateLastTick(EnergySystem system) {
        return this.convertIf(system, this._lastInsert.toImmutable().subtract(this._lastExtract));
    }

    //region WideEnergyStorageForwarder2

    @Override
    public WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {

        final var inserted = super.insertEnergy(system, maxAmount, mode);

        if (mode.execute()) {
            this.addInserted(inserted);
        }

        return inserted;
    }

    @Override
    public WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {

        final var extracted = super.extractEnergy(system, maxAmount, mode);

        if (mode.execute()) {
            this.addExtracted((extracted));
        }

        return extracted;
    }

    @Override
    public WideEnergyBuffer grow(final WideAmount amount) {

        this.addInserted(amount);
        return super.grow(amount);
    }

    @Override
    public WideEnergyBuffer grow(final double amount) {

        this.addInserted(WideAmount.asImmutable(amount));
        return super.grow(amount);
    }

    @Override
    public WideEnergyBuffer shrink(final WideAmount amount) {

        this.addExtracted(amount);
        return super.shrink(amount);
    }

    @Override
    public WideEnergyBuffer shrink(final double amount) {

        this.addExtracted(WideAmount.asImmutable(amount));
        return super.shrink(amount);
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(LogicalSide side, IDebugMessages messages) {

        super.getDebugMessages(side, messages);

        final EnergySystem sys = this.getEnergySystem();

        messages.add("Current I/O: %1$s / %2$s",
                sys.asHumanReadableNumber(this._currentInsert.doubleValue()),
                sys.asHumanReadableNumber(this._currentExtract.doubleValue()));
        messages.add("Last tick I/O: %1$s / %2$s",
                sys.asHumanReadableNumber(this._lastInsert.doubleValue()),
                sys.asHumanReadableNumber(this._lastExtract.doubleValue()));
    }

    //endregion
    //region internals

    private void addInserted(WideAmount amount) {
        this._currentInsert = this._currentInsert.add(amount);
    }

    private void addExtracted(WideAmount amount) {
        this._currentExtract = this._currentExtract.add(amount);
    }

    private WideAmount _currentInsert, _currentExtract;
    private WideAmount _lastInsert, _lastExtract;

    //endregion
}

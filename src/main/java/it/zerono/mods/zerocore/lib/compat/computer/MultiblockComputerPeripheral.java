/*
 *
 * MultiblockComputerPeripheral.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.lib.compat.computer;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess"})
public abstract class MultiblockComputerPeripheral<Controller extends IMultiblockController<Controller>,
            Part extends AbstractMultiblockPart<Controller>>
        extends ComputerPeripheral<MultiblockComputerPeripheral<Controller, Part>> {

    public MultiblockComputerPeripheral(final Part part) {
        super(part);
    }

    @SuppressWarnings("unchecked")
    public Part getMultiblockPart() {
        return (Part) this.getTileEntity();
    }

//    protected Optional<Controller> getMultiblockController() {
//        return this.getMultiblockPart().getMultiblockController();
//    }

    protected void executeOnController(final Consumer<Controller> code) {
        this.getMultiblockPart().executeOnController(code);
    }

    protected Object evalOnControllerValue(final Function<Controller, Object> code) {
        return this.getMultiblockPart().evalOnController(code, ComputerMethod.EMPTY_RESULT);
    }

    protected Object[] evalOnControllerArray(final Function<Controller, Object[]> code) {
        return this.getMultiblockPart().evalOnController(code, ComputerMethod.EMPTY_RESULT);
    }

    //region ComputerPeripheral

    /**
     * Collect the methods provided by this ComputerPeripheral
     *
     * @param methodConsumer pass your methods to this Consumer
     */
    @Override
    public void populateMethods(final NonNullConsumer<ComputerMethod<MultiblockComputerPeripheral<Controller, Part>>> methodConsumer) {

        methodConsumer.accept(new ComputerMethod<>("mbIsConnected", this.wrapPartValue(AbstractMultiblockPart::isConnected)));

        methodConsumer.accept(new ComputerMethod<>("mbIsAssembled", this.wrapPartValue(AbstractMultiblockPart::isMachineAssembled)));

        methodConsumer.accept(new ComputerMethod<>("mbIsDisassembled", this.wrapPartValue(AbstractMultiblockPart::isMachineDisassembled)));

        methodConsumer.accept(new ComputerMethod<>("mbIsPaused", this.wrapPartValue(AbstractMultiblockPart::isMachinePaused)));

        methodConsumer.accept(new ComputerMethod<>("mbGetMultiblockControllerTypeName", this.wrapPartValue(p -> p.getControllerType().getName())));

        methodConsumer.accept(new ComputerMethod<>("mbGetMinimumCoordinate", this.wrapControllerValue(c ->
                c.getMinimumCoord().map(coords -> new Object[]{coords.getX(), coords.getY(), coords.getZ()}).orElse(null))));

        methodConsumer.accept(new ComputerMethod<>("mbGetMaximumCoordinate", this.wrapControllerValue(c ->
                c.getMaximumCoord().map(coords -> new Object[]{coords.getX(), coords.getY(), coords.getZ()}).orElse(null))));
    }

    //endregion
    //region Object

    @Override
    public boolean equals(Object other) {
        //noinspection unchecked
        return super.equals(other) && (other instanceof MultiblockComputerPeripheral) &&
                CodeHelper.optionalMap(this.getMultiblockPart().getMultiblockController(),
                        ((MultiblockComputerPeripheral<Controller, Part>)other).getMultiblockPart().getMultiblockController(),
                        IMultiblockController::isSameController)
                .orElse(false);
    }

    //endregion
    //region method wrappers and helpers

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapPartValue(final Function<Part, Object> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(code.apply(peripheral.getMultiblockPart()));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapPartValue(final BiFunction<Part, Object[], Object> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(code.apply(peripheral.getMultiblockPart(), arguments));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapPartArray(final Function<Part, Object[]> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaArrayResult(code.apply(peripheral.getMultiblockPart()));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapPartArray(final BiFunction<Part, Object[], Object[]> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaArrayResult(code.apply(peripheral.getMultiblockPart(), arguments));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerValue(final Function<Controller, Object> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(peripheral.evalOnControllerValue(code));
//                luaValueResult(peripheral.getMultiblockController().map(code).orElse(null));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerValue(final BiFunction<Controller, Object[], Object> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(peripheral.evalOnControllerValue(c -> code.apply(c, arguments)));
//                luaValueResult(peripheral.getMultiblockController().map(c -> code.apply(c, arguments)).orElse(null));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerArray(final Function<Controller, Object[]> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(peripheral.evalOnControllerArray(code));
//                luaArrayResult(peripheral.getMultiblockController().map(code).orElse(null));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerArray(final BiFunction<Controller, Object[], Object[]> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) ->
                luaValueResult(peripheral.evalOnControllerArray(c -> code.apply(c, arguments)));
//                luaArrayResult(peripheral.getMultiblockController().map(c -> code.apply(c, arguments)).orElse(null));
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerAction(final Consumer<Controller> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) -> {

            peripheral.executeOnController(code);
//            peripheral.getMultiblockController().ifPresent(c -> code.accept(c, arguments));
            return luaValueResult(null);
        };
    }

    protected IComputerMethodHandler<MultiblockComputerPeripheral<Controller, Part>> wrapControllerAction(final BiConsumer<Controller, Object[]> code) {
        return (MultiblockComputerPeripheral<Controller, Part> peripheral, Object[] arguments) -> {

            peripheral.executeOnController(c -> code.accept(c, arguments));
//            peripheral.getMultiblockController().ifPresent(c -> code.accept(c, arguments));
            return luaValueResult(null);
        };
    }

    //endregion
}

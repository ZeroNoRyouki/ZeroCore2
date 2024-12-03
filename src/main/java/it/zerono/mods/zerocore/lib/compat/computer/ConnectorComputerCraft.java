///*
// *
// * ConnectorComputerCraft.java
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.zerocore.lib.compat.computer;
//
//import dan200.computercraft.api.lua.IArguments;
//import dan200.computercraft.api.lua.ILuaContext;
//import dan200.computercraft.api.lua.LuaException;
//import dan200.computercraft.api.lua.MethodResult;
//import dan200.computercraft.api.peripheral.IComputerAccess;
//import dan200.computercraft.api.peripheral.IDynamicPeripheral;
//import dan200.computercraft.api.peripheral.IPeripheral;
//import org.jetbrains.annotations.Nullable;
//
//public class ConnectorComputerCraft<P extends ComputerPeripheral<P>>
//        extends Connector<P>
//        implements IDynamicPeripheral {
//
//    public ConnectorComputerCraft(final String connectionName, P peripheral) {
//        super(connectionName, peripheral);
//    }
//
//    //region IDynamicPeripheral
//
//    @Override
//    public String[] getMethodNames() {
//        return this.getPeripheral().getMethodsNames();
//    }
//
//    @Override
//    public MethodResult callMethod(final IComputerAccess computer, final ILuaContext luaContext,
//                                   final int methodIdx, final IArguments arguments) throws LuaException {
//
//        try {
//            return MethodResult.of(this.invoke(methodIdx, arguments.getAll()));
//        } catch (Exception ex) {
//            throw new LuaException(ex.getMessage());
//        }
//    }
//
//    //endregion
//    //region IPeripheral
//
//    @Override
//    public String getType() {
//        return this.getConnectionName();
//    }
//
//    @Override
//    public boolean equals(@Nullable final IPeripheral other) {
//        //noinspection rawtypes
//        return (other instanceof ConnectorComputerCraft) &&
//                this.getPeripheral().equals(((ConnectorComputerCraft)other).getPeripheral());
//    }
//
//    //endregion
//}

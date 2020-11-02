/*
 *
 * ComputerMethod.java
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

import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraftforge.fml.LogicalSide;

@SuppressWarnings("WeakerAccess")
public class ComputerMethod<P extends ComputerPeripheral<P>> {

    public static final Object[] EMPTY_RESULT = new Object[0];

    public ComputerMethod(final String name, final IComputerMethodHandler<P> handler) {
        this(name, handler, 0, false);
    }

    @SuppressWarnings("unused")
    public ComputerMethod(final String name, final IComputerMethodHandler<P> handler,
                          final int minArgumentsCount) {
        this(name, handler, minArgumentsCount, false);
    }

    public ComputerMethod(final String name, final IComputerMethodHandler<P> handler,
                          final int minArgumentsCount, final boolean runOnServerThread) {

        this._name = name;
        this._handler = handler;
        this._minArgumentsCount = minArgumentsCount;
        this._runOnServerThread = runOnServerThread;
    }

    public String getName() {
        return this._name;
    }

    public int getMinArgumentsCount() {
        return this._minArgumentsCount;
    }

    @SuppressWarnings("unused")
    public Object[] invoke(final P peripheral, final Object[] arguments) {

        LuaHelper.validateArgsCount(arguments, this._minArgumentsCount);

        if (this._runOnServerThread) {

            CodeHelper.enqueueTask(LogicalSide.SERVER, () -> {

                try {
                    this._handler.execute(peripheral, arguments);
                } catch (Exception ex) {
                    Log.LOGGER.error(Log.COMPAT_COMPUTERS, "Exception raised while running computer method {} on server thread : {}",
                            this.getName(),  ex.getMessage());
                }
            });

            return EMPTY_RESULT;

        } else {

            final Object[] result = this._handler.execute(peripheral, arguments);

            // always check for a null result, just in case...
            return null != result ? result : EMPTY_RESULT;
        }
    }

    public static <P extends ComputerPeripheral<P>> ComputerMethod<P> getEmptyMethod() {
        //noinspection unchecked
        return (ComputerMethod<P>)EMPTY_METHOD;
    }

    //region internals

    @SuppressWarnings("rawtypes")
    private static final ComputerMethod EMPTY_METHOD = new ComputerMethod<>("42emptyMethod", (p, args) -> EMPTY_RESULT, 0, false);

    private final String _name;
    private final IComputerMethodHandler<P> _handler;
    private final int _minArgumentsCount;
    private final boolean _runOnServerThread;

    //endregion
}

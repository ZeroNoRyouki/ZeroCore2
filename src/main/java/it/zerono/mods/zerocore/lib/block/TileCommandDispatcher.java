/*
 *
 * TileCommandDispatcher.java
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

package it.zerono.mods.zerocore.lib.block;

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.internal.Log;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.LogicalSide;

import java.util.Map;

public class TileCommandDispatcher<T extends AbstractModBlockEntity>
    implements ITileCommandDispatcher {

    public static <T extends AbstractModBlockEntity> Builder<T> builder() {
        return new Builder<>();
    }

    //region builder

    public static class Builder<T extends AbstractModBlockEntity>
            implements ITileCommandDispatcher.Builder<T> {

        public ITileCommandDispatcher.Builder<T> addHandler(String name, ITileCommandHandler<T> handler) {

            this._handlers.put(name, handler);
            return this;
        }

        public ITileCommandDispatcher build(final T tile) {
            return new TileCommandDispatcher<>(tile, this._handlers);
        }

        //region internals

        private Builder() {
            this._handlers = Maps.newHashMap();
        }

        private final Map<String, ITileCommandHandler<T>> _handlers;

        //endregion
    }

    //endregion
    //region ITileCommandDispatcher

    @Override
    public void dispatch(LogicalSide source, String name, CompoundTag parameters) {
        this._handlers.getOrDefault(name, (t, s, p) -> Log.LOGGER.error("No handler for Tile Command {}", name))
                .handle(this._tile, source, parameters);
    }

    //endregion
    //region internals

    private TileCommandDispatcher(T tile, Map<String, ITileCommandHandler<T>> map) {

        this._tile = tile;
        this._handlers = map;
    }

    private final T _tile;
    private final Map<String, ITileCommandHandler<T>> _handlers;

    //endregion
}

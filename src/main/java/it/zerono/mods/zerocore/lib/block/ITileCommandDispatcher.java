/*
 *
 * ITileCommandDispatcher.java
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

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ITileCommandDispatcher {

    void dispatch(LogicalSide source, String name, CompoundNBT parameters);

    interface Builder<T extends AbstractModBlockEntity> {

        Builder<T> addHandler(String name, ITileCommandHandler<T> handler);

        default ITileCommandDispatcher.Builder<T> addHandler(String name, BiConsumer<T, LogicalSide> handler) {
            return this.addHandler(name, (tile, source, parameters) -> handler.accept(tile, source));
        }

        default ITileCommandDispatcher.Builder<T> addServerHandler(String name, BiConsumer<T, CompoundNBT> handler) {
            return this.addHandler(name, (tile, source, parameters) -> {
                if (source.isClient()) {
                    handler.accept(tile, parameters);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addServerHandler(String name, Consumer<T> handler) {
            return this.addHandler(name, (tile, source, parameters) -> {
                if (source.isClient()) {
                    handler.accept(tile);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addClientHandler(String name, BiConsumer<T, CompoundNBT> handler) {
            return this.addHandler(name, (tile, source, parameters) -> {
                if (source.isServer()) {
                    handler.accept(tile, parameters);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addClientHandler(String name, Consumer<T> handler) {
            return this.addHandler(name, (tile, source, parameters) -> {
                if (source.isServer()) {
                    handler.accept(tile);
                }
            });
        }

        ITileCommandDispatcher build(T tile);
    }
}

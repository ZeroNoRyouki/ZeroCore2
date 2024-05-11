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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketFlow;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ITileCommandDispatcher {

    void dispatch(PacketFlow flow, String name, CompoundTag parameters);

    interface Builder<T extends AbstractModBlockEntity> {

        Builder<T> addHandler(String name, ITileCommandHandler<T> handler);

        default ITileCommandDispatcher.Builder<T> addHandler(String name, BiConsumer<T, PacketFlow> handler) {
            return this.addHandler(name, (tile, source, parameters) -> handler.accept(tile, source));
        }

        default ITileCommandDispatcher.Builder<T> addServerHandler(String name, BiConsumer<T, CompoundTag> handler) {
            return this.addHandler(name, (tile, flow, parameters) -> {
                if (PacketFlow.SERVERBOUND == flow) {
                    handler.accept(tile, parameters);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addServerHandler(String name, Consumer<T> handler) {
            return this.addHandler(name, (tile, flow, parameters) -> {
                if (PacketFlow.SERVERBOUND == flow) {
                    handler.accept(tile);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addClientHandler(String name, BiConsumer<T, CompoundTag> handler) {
            return this.addHandler(name, (tile, flow, parameters) -> {
                if (PacketFlow.CLIENTBOUND == flow) {
                    handler.accept(tile, parameters);
                }
            });
        }

        default ITileCommandDispatcher.Builder<T> addClientHandler(String name, Consumer<T> handler) {
            return this.addHandler(name, (tile, flow, parameters) -> {
                if (PacketFlow.CLIENTBOUND == flow) {
                    handler.accept(tile);
                }
            });
        }

        ITileCommandDispatcher build(T tile);
    }
}

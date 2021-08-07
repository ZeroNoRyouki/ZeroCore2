/*
 *
 * ModTileContainer.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container;

import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class ModTileContainer<T extends AbstractModBlockEntity>
        extends ModContainer {

    /**
     * Construct a container linked to the given TileEntity
     *
     * @param factory
     * @param type      the registered {@link ContainerType} for this container
     * @param windowId
     * @param tile      the TileEntity to link with
     */
    public ModTileContainer(final ContainerFactory factory, final ContainerType<? extends ModTileContainer<T>> type,
                            final int windowId, final T tile) {

        super(factory, type, windowId);
        this._tile = tile;
    }

    /**
     * Construct a container linked to the given TileEntity.
     * If the TileEntity implement INetworkTileEntitySyncProvider, subscribe the given Player to it
     *
     * @param factory
     * @param type      the registered {@link ContainerType} for this container
     * @param windowId
     * @param tile      the TileEntity to link with
     * @param player    the player
     */
    public ModTileContainer(final ContainerFactory factory, final ContainerType<? extends ModTileContainer<T>> type,
                            final int windowId, final T tile, final ServerPlayerEntity player) {

        this(factory, type, windowId, tile);

        if (this._tile instanceof INetworkTileEntitySyncProvider) {
            ((INetworkTileEntitySyncProvider)this._tile).enlistForUpdates(player, true);
        }
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final ContainerType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final T tile) {
        return new ModTileContainer<T>(ContainerFactory.EMPTY, type, windowId, tile) {
            @Override
            public void setItem(int slotID, ItemStack stack) {
            }
        };
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final ContainerType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final T tile,
                                                                                  final ServerPlayerEntity player) {
        return new ModTileContainer<T>(ContainerFactory.EMPTY, type, windowId, tile, player) {
            @Override
            public void setItem(int slotID, ItemStack stack) {
            }
        };
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final ContainerType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final PacketBuffer data) {
        return empty(type, windowId, AbstractModBlockEntity.getGuiClientBlockEntity(data));
    }

    public T getTileEntity() {
        return this._tile;
    }

    //region Container

    /**
     * Determines whether supplied player can use this container (when the container is already open)
     *
     * @param player the player
     */
    @Override
    public boolean stillValid(final PlayerEntity player) {

        return stillValid(IWorldPosCallable.create(Objects.requireNonNull(this._tile.getLevel()),
                this._tile.getBlockPos()), player, this._tile.getBlockType());
    }

    /**
     * Called when the container is closed.
     *
     * @param player
     */
    @Override
    public void removed(PlayerEntity player) {

        super.removed(player);

        if (this._tile instanceof INetworkTileEntitySyncProvider && player instanceof ServerPlayerEntity) {
            ((INetworkTileEntitySyncProvider)this._tile).delistFromUpdates((ServerPlayerEntity)player);
        }
    }

    //endregion
    //region internals

    private final T _tile;

    //endregion
}

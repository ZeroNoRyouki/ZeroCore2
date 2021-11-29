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
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ModTileContainer<T extends AbstractModBlockEntity>
        extends ModContainer {

    /**
     * Construct a container linked to the given TileEntity
     *
     * @param factory
     * @param type      the registered {@link MenuType} for this container
     * @param windowId
     * @param tile      the TileEntity to link with
     */
    public ModTileContainer(final ContainerFactory factory, final MenuType<? extends ModTileContainer<T>> type,
                            final int windowId, final T tile) {

        super(factory, type, windowId);
        this._tile = tile;

        if (tile instanceof IConditionallySyncableEntity) {
            this.syncFrom((IConditionallySyncableEntity)tile);
        }
    }

    /**
     * Construct a container linked to the given TileEntity.
     * If the TileEntity implement INetworkTileEntitySyncProvider, subscribe the given Player to it
     *
     * @param factory
     * @param type      the registered {@link MenuType} for this container
     * @param windowId
     * @param tile      the TileEntity to link with
     * @param player    the player
     */
    public ModTileContainer(final ContainerFactory factory, final MenuType<? extends ModTileContainer<T>> type,
                            final int windowId, final T tile, final ServerPlayer player) {

        this(factory, type, windowId, tile);

        if (this._tile instanceof INetworkTileEntitySyncProvider) {
            ((INetworkTileEntitySyncProvider)this._tile).enlistForUpdates(player, true);
        }
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final MenuType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final T tile) {
        return new ModTileContainer<T>(ContainerFactory.EMPTY, type, windowId, tile) {
            @Override
            public void setItem(int slotID, int stateId, ItemStack stack) {
            }
        };
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final MenuType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final T tile,
                                                                                  final ServerPlayer player) {
        return new ModTileContainer<T>(ContainerFactory.EMPTY, type, windowId, tile, player) {
            @Override
            public void setItem(int slotID, int stateId, ItemStack stack) {
            }
        };
    }

    public static <T extends AbstractModBlockEntity> ModTileContainer<T> empty(final MenuType<? extends ModTileContainer<T>> type,
                                                                                  final int windowId, final FriendlyByteBuf data) {
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
    public boolean stillValid(final Player player) {

        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this._tile.getLevel()),
                this._tile.getBlockPos()), player, this._tile.getBlockType());
    }

    /**
     * Called when the container is closed.
     *
     * @param player
     */
    @Override
    public void removed(Player player) {

        super.removed(player);

        if (this._tile instanceof INetworkTileEntitySyncProvider && player instanceof ServerPlayer) {
            ((INetworkTileEntitySyncProvider)this._tile).delistFromUpdates((ServerPlayer)player);
        }
    }

    //endregion
    //region internals

    private final T _tile;

    //endregion
}

/*
 *
 * ServerProxy.java
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

package it.zerono.mods.zerocore.internal.proxy;

import it.zerono.mods.zerocore.internal.InternalCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

import java.util.Optional;

public class ServerProxy
        implements IForgeProxy {

    public ServerProxy() {
    }

    //region IForgeProxy

    @Override
    public void initialize(IEventBus modEventBus) {
    }

    @Override
    public Optional<Level> getClientWorld() {
        return Optional.empty();
    }

    @Override
    public Optional<Player> getClientPlayer() {
        return Optional.empty();
    }

    @Override
    public void markBlockRangeForRenderUpdate(Level level, BlockPos min, BlockPos max) {
        // do nothing
    }

    @Override
    public void clearErrorReport() {
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void handleInternalCommand(final InternalCommand command, final CompoundTag data, final PacketFlow flow) {

        //noinspection EnhancedSwitchMigration
        switch (command) {

            default:
                IForgeProxy.super.handleInternalCommand(command, data, flow);
                break;
        }
    }

    @Override
    public void debugUngrabMouse() {
    }

    //endregion
}

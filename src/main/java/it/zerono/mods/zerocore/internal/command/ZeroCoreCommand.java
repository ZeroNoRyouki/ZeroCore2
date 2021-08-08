/*
 *
 * ZeroCoreCommand.java
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

package it.zerono.mods.zerocore.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.network.Network;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class ZeroCoreCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {

        final LiteralCommandNode<CommandSourceStack> mainCmd = dispatcher.register(Commands.literal(ZeroCore.MOD_ID)
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("debug")
                        .then(Commands.literal("gui")
                                .then(Commands.literal("hoverFrame")
                                        .then(Commands.literal("enable").executes(ZeroCoreCommand::debugEnableGuiFrame))
                                        .then(Commands.literal("disable").executes(ZeroCoreCommand::debugDisableGuiFrame))
                                )
                        )
                )
                .then(Commands.literal("recipe")
                        .then(Commands.literal("clearCache").executes(ZeroCoreCommand::recipeClearCache))
                )
        );

        dispatcher.register(Commands.literal("zc").redirect(mainCmd));
    }

    //region internals

    private ZeroCoreCommand() {
    }

    private static int debugEnableGuiFrame(CommandContext<CommandSourceStack> context) {

        Network.sendDebugGuiFrameCommand(true);
        return 0;
    }

    private static int debugDisableGuiFrame(CommandContext<CommandSourceStack> context) {

        Network.sendDebugGuiFrameCommand(false);
        return 0;
    }

    private static int recipeClearCache(CommandContext<CommandSourceStack> context) {

        Network.sendClearRecipeCommand();
        return 0;
    }

    //endregion
}

/*
 *
 * DebugToolItem.java
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

package it.zerono.mods.zerocore.internal.gamecontent.debugtool;

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class DebugToolItem
        extends ModItem {

    @FunctionalInterface
    public interface ITestCallback {

        void runTest(int test, @Nullable PlayerEntity player, World world, BlockPos clickedPos);
    }

    public DebugToolItem() {
        super(new Properties().maxStackSize(64).group(ItemGroup.TOOLS));
    }

    public static void setTestCallback(@Nullable ITestCallback callback) {
        s_testCallback = callback;
    }

    //region Item

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {

        tooltip.add(new TranslationTextComponent("zerocore:debugTool.block.tooltip1"));
        tooltip.add(new TranslationTextComponent("zerocore:debugTool.block.tooltip2", TextFormatting.ITALIC.toString()));
        tooltip.add(new TranslationTextComponent("zerocore:debugTool.block.tooltip3", TextFormatting.GREEN,
                TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString()));
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {

        final PlayerEntity player = context.getPlayer();
        final World world = context.getWorld();
        final BlockPos pos = context.getPos();
        final LogicalSide side = CodeHelper.getWorldLogicalSide(world);

        if (CodeHelper.isDevEnv() && null != s_testCallback && !stack.isEmpty() && stack.getCount() > 1) {

            if (context.getHand() == Hand.MAIN_HAND) {

                s_testCallback.runTest(stack.getCount(), player, world, pos);
                return ActionResultType.SUCCESS;
            }
        }

        if (null == player ||
                /*player.isSneaking() != WorldHelper.calledByLogicalClient(world)*/
                player.isSneaking() != side.isClient() ||
                world.isAirBlock(pos)) {
            return ActionResultType.PASS;
        }

        if (WorldHelper.getTile(world, pos)
                .filter(te -> te instanceof IDebuggable)
                .map(te -> (IDebuggable)te)
                .map(debuggee -> MessagesPool.build(debuggee, side))
                .map(pool -> this.sendMessages(player,
                        new StringTextComponent(String.format("%1$s side debug analysis report of Tile Entity at %2$d, %3$d, %4$d",
                                CodeHelper.getWorldSideName(world), pos.getX(), pos.getY(), pos.getZ())), pool))
                .filter(result -> result)
                .isPresent()) {
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    //endregion
    //region internals

    private boolean sendMessages(final PlayerEntity player, final ITextComponent header, final MessagesPool pool) {

        if (pool.isNotEmpty()) {

            CodeHelper.sendChatMessage(player, new StringTextComponent("--------------------------------------------------"));
            CodeHelper.sendChatMessage(player, header);
            pool.forEach(message -> CodeHelper.sendChatMessage(player, message));
            return true;
        }

        return false;
    }

    private static final class MessagesPool implements IDebugMessages, Iterable<ITextComponent> {

        public static MessagesPool build(IDebuggable debuggee, LogicalSide side) {

            final MessagesPool pool = new MessagesPool(0);

            debuggee.getDebugMessages(side, pool);
            return pool;
        }

        public boolean isNotEmpty() {
            return !this._messages.isEmpty();
        }

        //region IDebugMessages

        /**
         * Add a message to the messages list
         *
         * @param message the language resource key of the message to add
         */
        @Override
        public void add(final ITextComponent message) {
            this._messages.add(message);
        }

        /**
         * Add a message to the messages list using a localized format string
         *
         * @param messageFormatStringResourceKey the language resource key of a format string to use to create the message
         * @param messageParameters              the values to insert in the message
         */
        @Override
        public void add(final String messageFormatStringResourceKey, final Object... messageParameters) {
            this.add(this.getFormattedTextComponent(messageFormatStringResourceKey, messageParameters));
        }

        /**
         * Add messages from another IDebuggable to this messages list
         * <p>
         * If the provided IDebuggable provide only one message, the message will be added at the same level of the other
         * messages in this message list. If it provide more than one message, they will be added as nested messages
         *
         * @param debuggable the other IDebuggable to query for messages
         * @param label      the language resource key of the message to add as a label for the other IDebuggable messages
         */
        @Override
        public void add(final LogicalSide side, final IDebuggable debuggable, final ITextComponent label) {

            final MessagesPool other = new MessagesPool(this._depth + 1);

            debuggable.getDebugMessages(side, other);
            this.merge(other, label);
        }

        /**
         * Add messages from another IDebuggable to this messages list
         * <p>
         * If the provided IDebuggable provide only one message, the message will be added at the same level of the other
         * messages in this message list. If it provide more than one message, they will be added as nested messages
         *
         * @param side the LogicalSide of the caller
         * @param debuggable                   the other IDebuggable to query for messages
         * @param labelFormatStringResourceKey the language resource key of a format string to use to create the label
         *                                     for the other IDebuggable messages
         * @param labelParameters              the values to insert in the label
         */
        @Override
        public void add(final LogicalSide side, final IDebuggable debuggable, final String labelFormatStringResourceKey,
                        final Object... labelParameters) {
            this.add(side, debuggable, this.getFormattedTextComponent(labelFormatStringResourceKey, labelParameters));
        }

        @Override
        public <T> void add(final T debuggee, final BiConsumer<IDebugMessages, T> consumer, final ITextComponent label) {

            final MessagesPool other = new MessagesPool(this._depth + 1);

            consumer.accept(other, debuggee);
            this.merge(other, label);
        }

        @Override
        public <T> void add(final T debuggee, final BiConsumer<IDebugMessages, T> consumer, final String labelFormatStringResourceKey,
                        final Object... labelParameters) {
            this.add(debuggee, consumer, this.getFormattedTextComponent(labelFormatStringResourceKey, labelParameters));
        }

        //endregion
        //region Iterable<ITextComponent>

        /**
         * Returns an iterator over elements of type {@code T}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<ITextComponent> iterator() {
            return this._messages.iterator();
        }

        //endregion
        //region internals

        private MessagesPool(final int depth) {

            this._messages = Lists.newArrayListWithCapacity(2);
            this._depth = depth;
        }

        private void merge(final MessagesPool other, final ITextComponent label) {

            if (1 == other._messages.size()) {

                this.add(new StringTextComponent("").append(label).appendString(" ").append(other._messages.get(0)));

            } else {

                this.add(label);
                other._messages.forEach(message -> this.add(this.createPadding(other._depth).append(message)));
            }
        }

        private IFormattableTextComponent createPadding(final int depth) {
            return new StringTextComponent("                    ".substring(0, Math.min(20, depth)));
        }

        private ITextComponent getFormattedTextComponent(final String format, final Object... parameters) {

            if (parameters.length > 0) {
                return new TranslationTextComponent(format, parameters);
            } else {
                return new StringTextComponent(format);
            }
        }

        private final List<ITextComponent> _messages;
        private final int _depth;

        //endregion
    }

    private static ITestCallback s_testCallback = null;

    //endregion
}

/*
 *
 * MultiblockErrorData.java
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

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.client.gui.CompositeRichText;
import it.zerono.mods.zerocore.lib.client.gui.IRichText;
import it.zerono.mods.zerocore.lib.client.gui.RichText;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.IntFunction;
import java.util.function.Predicate;

class GuiErrorData
        implements Predicate<BlockPos>, IntFunction<@NotNull IRichText> {

    public GuiErrorData() {

        this._lock = new StampedLock();
        this._timeout = new AtomicInteger(0);
    }

    public void tick() {

        if (this._timeout.get() > 0) {
            if (0 == this._timeout.decrementAndGet()) {
                this.resetErrors();
            }
        }
    }

    public void addErrors(final @Nullable BlockPos position, final Component... messages) {
        this.addErrors(position, Lists.newArrayList(messages));
    }

    public void addErrors(final @Nullable BlockPos position, final List<Component> messages) {

        final long stamp = this._lock.writeLock();

        this._timeout.set(60 * 100);
        this._errorMessages = messages;
        this._errorPosition = position;
        this._errorText = null;

        this._lock.unlockWrite(stamp);
    }

    public void addErrors(final Component... messages) {
        this.addErrors(null, messages);
    }

    public void resetErrors() {

        final long stamp = this._lock.writeLock();

        this._timeout.set(0);
        this._errorMessages = null;
        this._errorPosition = null;
        this._errorText = null;

        this._lock.unlockWrite(stamp);
    }

    //region NonNullIntFunction<IRichText>

    @Nonnull
    @Override
    public IRichText apply(final int maxTextWidth) {

        long lockStamp = this._lock.readLock();
        final List<Component> errorMessages = this._errorMessages;

        if (null == errorMessages || errorMessages.isEmpty() || this._timeout.get() <= 0) {

            this._lock.unlockRead(lockStamp);
            return RichText.EMPTY;
        }

        IRichText texts = this._errorText;

        if (null == texts) {

            do {

                final long writeLockStamp = this._lock.tryConvertToWriteLock(lockStamp);

                if (0L != writeLockStamp) {

                    final BlockPos errorPosition = this._errorPosition;

                    if (null == errorPosition && 1 == errorMessages.size()) {

                        texts = this._errorText = textFrom(errorMessages.get(0), maxTextWidth);

                    } else {

                        final CompositeRichText.Builder builder = CompositeRichText.builder().interline(5);

                        if (null != errorPosition) {
                            builder.add(RichText.builder(maxTextWidth)
                                    .textLines(Lists.newArrayList(Component.literal(String.format("@0 %d, %d, %d",
                                            errorPosition.getX(), errorPosition.getY(), errorPosition.getZ()))))
                                    .objects(Lists.newArrayList(Items.COMPASS))
                                    .defaultColour(Colour.WHITE)
                                    .build());
                        }

                        errorMessages.forEach(message -> builder.add(textFrom(message, maxTextWidth)));
                        texts = this._errorText = builder.build();
                    }

                    lockStamp = writeLockStamp;
                    break;

                } else {

                    this._lock.unlockRead(lockStamp);
                    lockStamp = this._lock.writeLock();
                }

            } while (true);
        }

        this._lock.unlock(lockStamp);
        return texts;
    }

    //endregion
    //region Predicate<BlockPos>

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param pos the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final BlockPos pos) {

        long stamp = this._lock.tryOptimisticRead();
        BlockPos errorPosition = this._errorPosition;

        if (!this._lock.validate(stamp)) {

            stamp = this._lock.readLock();
            errorPosition = this._errorPosition;
            this._lock.unlockRead(stamp);
        }

        return null != errorPosition && errorPosition.equals(pos);
    }

    //endregion
    //region internals

    private static IRichText textFrom(final Component errorMessage, final int maxTextWidth) {
        return RichText.builder(maxTextWidth)
                .textLines(errorMessage)
                .defaultColour(Colour.WHITE)
                .build();
    }

    private final StampedLock _lock;
    private final AtomicInteger _timeout;

    private List<Component> _errorMessages;
    private BlockPos _errorPosition;
    private IRichText _errorText;

    //endregion
}

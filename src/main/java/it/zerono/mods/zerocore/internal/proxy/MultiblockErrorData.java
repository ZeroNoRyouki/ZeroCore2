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
import it.zerono.mods.zerocore.lib.client.gui.RichText;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;

class MultiblockErrorData
        implements Predicate<BlockPos> {

    public MultiblockErrorData() {

        this._lock = new StampedLock();
        this._timeout = new AtomicInteger(0);
    }

    public void tick() {

        if (this._timeout.get() > 0) {
            if (0 == this._timeout.decrementAndGet()) {
                this.resetError();
            }
        }
    }

    public void setError(final ITextComponent message, final @Nullable BlockPos position) {

        final long stamp = this._lock.writeLock();

        this._timeout.set(60 * 100);
        this._errorMessage = message;
        this._errorPosition = position;
        this._errorTexts = null;

        this._lock.unlockWrite(stamp);
    }

    public void resetError() {

        final long stamp = this._lock.writeLock();

        this._timeout.set(0);
        this._errorMessage = null;
        this._errorPosition = null;
        this._errorTexts = null;

        this._lock.unlockWrite(stamp);
    }

    public List<RichText> getErrorTexts(final int maxTextWidth) {

        long lockStamp = this._lock.readLock();

        final ITextComponent errorMessage = this._errorMessage;

        if (null == errorMessage || this._timeout.get() <= 0) {

            this._lock.unlockRead(lockStamp);
            return Collections.emptyList();
        }

        List<RichText> errorTexts = this._errorTexts;

        if (null == errorTexts) {

            errorTexts = Lists.newArrayList();

            do {

                final long writeLockStamp = this._lock.tryConvertToWriteLock(lockStamp);

                if (0L != writeLockStamp) {

                    final BlockPos errorPosition = this._errorPosition;

                    if (null != errorPosition) {
                        errorTexts.add(RichText.builder(maxTextWidth)
                                .textLines(Lists.newArrayList(new StringTextComponent(String.format("@0 %d, %d, %d",
                                        errorPosition.getX(), errorPosition.getY(), errorPosition.getZ()))))
                                .objects(Lists.newArrayList(Items.COMPASS))
                                .defaultColour(Colour.WHITE)
                                .build());
                    }

                    errorTexts.add(RichText.builder(maxTextWidth)
                            .textLines(Lists.newArrayList(errorMessage))
                            .defaultColour(Colour.WHITE)
                            .build());

                    this._errorTexts = errorTexts;

                    lockStamp = writeLockStamp;
                    break;

                } else {

                    this._lock.unlockRead(lockStamp);
                    lockStamp = this._lock.writeLock();
                }

            } while (true);
        }

        this._lock.unlock(lockStamp);
        return errorTexts;
    }

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

    private final StampedLock _lock;
    private final AtomicInteger _timeout;

    private ITextComponent _errorMessage;
    private BlockPos _errorPosition;

    private List<RichText> _errorTexts;

    //endregion
}

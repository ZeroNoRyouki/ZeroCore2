/*
 *
 * IStackHolder.java
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

package it.zerono.mods.zerocore.lib.data.stack;

import java.util.function.ObjIntConsumer;

public interface IStackHolder<Holder extends IStackHolder<Holder, Stack>, Stack> {

    enum ChangeType {

        /**
         * An empty stack in the holder was replaced with a non empty stack
         */
        Added,

        /**
         * A non empty stack in the holder grown in size
         */
        Grown,

        /**
         * A non empty stack in the holder was replaced with a different non empty stack
         */
        Replaced,

        /**
         * A non empty stack in the holder was replaced with an empty stack
         */
        Removed,

        /**
         * A non empty stack in the holder shrank in size
         */
        Shrunk;

        public boolean fullChange() {
            return this == Added || this == Replaced || this == Removed;
        }

        public boolean amountChange() {
            return this == Grown || this == Shrunk;
        }
    }

    boolean isStackValid(int index, Stack stack);

    boolean isEmpty(int index);

    Holder setOnContentsChangedListener(ObjIntConsumer<ChangeType> listener);

    Holder setOnLoadListener(Runnable listener);
}

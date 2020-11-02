/*
 *
 * SlotIndexSet.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.slot;

import com.google.common.base.Preconditions;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

import java.util.Set;

public class SlotIndexSet {

    public SlotIndexSet(final SlotTemplate template) {

        Preconditions.checkNotNull(template, "Invalid template");

        this._template = template;
        //noinspection UnstableApiUsage
        this._set = TreeRangeSet.create();
    }

    public void addIndex(int index) {
        this._set.add(Range.singleton(index).canonical(DiscreteDomain.integers()));
    }

    public Set<Range<Integer>> asRanges() {
        return this._set.asRanges();
    }

    public SlotTemplate getTemplate() {
        return this._template;
    }

    private final SlotTemplate _template;
    private final TreeRangeSet<Integer> _set;
}

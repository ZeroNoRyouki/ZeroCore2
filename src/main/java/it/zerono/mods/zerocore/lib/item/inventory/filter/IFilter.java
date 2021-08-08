/*
 *
 * IFilter.java
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

package it.zerono.mods.zerocore.lib.item.inventory.filter;

import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Optional;

public interface IFilter extends IFilterComponent {

    boolean canAccept(ItemStack stack);

    Optional<IFilterCondition> getCondition(String name);

    void addCondition(String name, IFilterCondition condition);

    void removeCondition(String name);

    FilterBehavior getBehavior();

    void setBehavior(FilterBehavior behavior);

    EnumSet<ItemHelper.MatchOption> getMatchOptions();

    void setMatchOptions(EnumSet<ItemHelper.MatchOption> options);
}

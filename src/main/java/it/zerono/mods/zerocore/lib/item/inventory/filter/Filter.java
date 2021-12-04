/*
 *
 * Filter.java
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

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public abstract class Filter implements IFilter {

    public Filter() {

        this._conditions = Maps.newHashMap();
        this._behavior = FilterBehavior.BlackList;
        this._matchOptions = ItemHelper.MatchOption.MATCH_ALWAYS;
    }

    //region IFilter

    @Override
    public boolean canAccept(ItemStack stack) {

        if (stack.isEmpty()) {
            return false;
        }

        for (final IFilterCondition condition : this._conditions.values()) {
            if (condition.match(stack, this.getMatchOptions())) {

                // one of the conditions matches the ItemStack:
                // the ItemStack can be accepted only if we are in WhiteList mode
                return FilterBehavior.WhiteList == this.getBehavior();
            }
        }

        // no condition matches the ItemStack:
        // the ItemStack can be accepted only if we are in BlackList mode
        return FilterBehavior.BlackList == this.getBehavior();
    }

    @Override
    public Optional<IFilterCondition> getCondition(String name) {
        return Optional.ofNullable(this._conditions.get(name));
    }

    @Override
    public void addCondition(String name, IFilterCondition condition) {
        this._conditions.put(name, condition);
    }

    @Override
    public void removeCondition(String name) {
        this._conditions.remove(name);
    }

    @Override
    public FilterBehavior getBehavior() {
        return this._behavior;
    }

    @Override
    public void setBehavior(FilterBehavior behavior) {
        this._behavior = behavior;
    }

    @Override
    public EnumSet<ItemHelper.MatchOption> getMatchOptions() {
        return this._matchOptions;
    }

    @Override
    public void setMatchOptions(EnumSet<ItemHelper.MatchOption> options) {
        this._matchOptions = options;
    }

    //region INBTSerializable

    @Override
    public CompoundTag serializeNBT() {

        final CompoundTag nbt = new CompoundTag();
        final ListTag conditionsList = new ListTag();

        this._conditions.forEach((name, condition) -> {

            final CompoundTag nameTag = new CompoundTag();

            nameTag.putString("name", name);
            conditionsList.add(nameTag);
            conditionsList.add(FilterManager.getInstance().storeComponentToNBT(condition));
        });

        nbt.put(NBT_CONDITIONS_KEY, conditionsList);
        NBTHelper.nbtSetEnum(nbt, NBT_BEHAVIOR_KEY, this._behavior);
        NBTHelper.nbtSetEnumSet(nbt, NBT_OPTIONS_KEY, this._matchOptions);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        if (nbt.contains(NBT_CONDITIONS_KEY)) {

            final ListTag conditionsList = nbt.getList(NBT_CONDITIONS_KEY, Tag.TAG_COMPOUND);

            this._conditions.clear();
            for (int i = 0; i < conditionsList.size(); i = i + 2) {

                final CompoundTag nameTag = conditionsList.getCompound(i);

                if (nameTag.contains("name") && (i + 1) < conditionsList.size()) {

                    final Optional<IFilterCondition> condition = FilterManager.<IFilterCondition>getInstance()
                            .loadComponentFromNBT(conditionsList.getCompound(i + 1));

                    condition.ifPresent(c -> this._conditions.put(nameTag.getString("name"), c));
                }
            }
        }

        if (nbt.contains(NBT_BEHAVIOR_KEY)) {
            this._behavior = NBTHelper.nbtGetEnum(nbt, NBT_BEHAVIOR_KEY, FilterBehavior.class);
        }

        if (nbt.contains(NBT_OPTIONS_KEY)) {
            this._matchOptions = NBTHelper.nbtGetEnumSet(nbt, NBT_OPTIONS_KEY, ItemHelper.MatchOption.class);
        }
    }

    private final Map<String, IFilterCondition> _conditions;
    private FilterBehavior _behavior;
    private EnumSet<ItemHelper.MatchOption> _matchOptions;

    private static final String NBT_BEHAVIOR_KEY = "Behavior";
    private static final String NBT_OPTIONS_KEY = "Options";
    private static final String NBT_CONDITIONS_KEY = "Conditions";
}

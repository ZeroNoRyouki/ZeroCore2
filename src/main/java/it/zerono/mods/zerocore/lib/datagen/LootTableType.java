/*
 *
 * LootTableType.java
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

package it.zerono.mods.zerocore.lib.datagen;

import com.google.common.base.Preconditions;
import joptsimple.internal.Strings;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public enum LootTableType {

    AdvancementReward(LootContextParamSets.ADVANCEMENT_REWARD, "advancement_reward"),
    Block(LootContextParamSets.BLOCK, "blocks"),
    Chest(LootContextParamSets.CHEST, "chests"),
    Entity(LootContextParamSets.ENTITY, "entities"),
    Fishing(LootContextParamSets.FISHING, "gameplay/fishing"),
    Generic(LootContextParamSets.ALL_PARAMS, "gameplay"),
    Gift(LootContextParamSets.GIFT, "gameplay/gift");

    LootTableType(final LootContextParamSet parameterSet, final String subFolderName) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(subFolderName));
        this._parameters = parameterSet;
        this._subFolder = subFolderName;
    }

    public String getSubFolderName() {
        return this._subFolder;
    }

    public LootContextParamSet getParameters() {
        return this._parameters;
    }

    //region internals

    private final LootContextParamSet _parameters;
    private final String _subFolder;

    //endregion
}

/*
 *
 * BaseLootTableProvider.java
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

package it.zerono.mods.zerocore.lib.datagen.provider;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.datagen.LootTableType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("unsued")
public class BaseLootTableProvider
        implements DataProvider {

    protected BaseLootTableProvider(final LootTableType type, final DataGenerator dataGenerator) {

        this._type = type;
        this._generator = dataGenerator;
        this._tables = Maps.newHashMap();
    }

    public LootTableType getType() {
        return this._type;
    }

    protected void generateTables() {
    }

    protected void add(final Block block, final LootTable.Builder builder) {
        this.add(CodeHelper.getObjectId(block), builder);
    }

    protected void add(final ResourceLocation id, final LootTable.Builder builder) {

        if (null != this._tables.put(id, builder)) {
            Log.LOGGER.error(Log.CORE, "Loot table provider - {} table {} already exist!", this.getType(), id);
        }
    }

    protected void addEmpty(final ResourceLocation id) {
        this.add(id, LootTable.lootTable());
    }

    //region IDataProvider

    /**
     * Performs this provider's action.
     */
    @Override
    public void run(CachedOutput cache) {

        this.generateTables();
        this._tables.forEach((id, builder) -> this.writeTable(cache, id,
                builder.setParamSet(this.getType().getParameters()).build()));
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "";
    }

    //endregion
    //region internals

    private void writeTable(final CachedOutput cache, final ResourceLocation id, final LootTable table) {

        final Path path = this._generator.getOutputFolder().resolve("data/" + id.getNamespace() +
                "/loot_tables/" + this.getType().getSubFolderName() + "/" + id.getPath() + ".json");

        try {
            DataProvider.saveStable(cache, LootTables.serialize(table), path);
        } catch (IOException ex) {
            Log.LOGGER.error(Log.CORE, "Loot table provider - couldn't write tables at {}", path, ex);
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final LootTableType _type;
    private final DataGenerator _generator;
    private final Map<ResourceLocation, LootTable.Builder> _tables;

    //endregion
}

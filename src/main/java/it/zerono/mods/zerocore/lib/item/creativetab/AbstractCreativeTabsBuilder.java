package it.zerono.mods.zerocore.lib.item.creativetab;

/*
 * AbstractCreativeTabsBuilder
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
 * Do not remove or edit this header
 *
 */

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractCreativeTabsBuilder
        implements ICreativeTabsBuilder {

    //region ICreativeTabsBuilder

    @Override
    public ICreativeTabsBuilder add(ResourceLocation id, NonNullConsumer<CreativeModeTab.Builder> tabBuilder,
                                    CreativeTabContentBuilder contentBuilder) {

        Preconditions.checkNotNull(id, "Id must not be null");
        Preconditions.checkNotNull(tabBuilder, "Tab builder must not be null");
        Preconditions.checkNotNull(contentBuilder, "Content builder must not be null");

        if (null == this._added) {
            this._added = new LinkedList<>();
        }

        this._added.add(new AddedGroup(id, tabBuilder, contentBuilder, new CompletableFuture<>()));
        return this;
    }

    @Override
    public ICreativeTabsBuilder modify(CreativeModeTab tab, CreativeTabContentBuilder contentBuilder) {

        Preconditions.checkNotNull(tab, "Tab must not be null");
        Preconditions.checkNotNull(contentBuilder, "Content builder must not be null");

        if (null == this._modified) {
            this._modified = new LinkedList<>();
        }

        this._modified.add(new ModifiedGroup(tab, contentBuilder));
        return this;
    }

    @Override
    public ICreativeTabsBuilder modifyAny(CreativeTabContentBuilder contentBuilder) {

        Preconditions.checkNotNull(contentBuilder, "Content builder must not be null");

        if (null == this._modified) {
            this._modified = new LinkedList<>();
        }

        this._modified.add(new ModifiedGroup(null, contentBuilder));
        return this;
    }

    @Override
    public Map<ResourceLocation, CompletableFuture<CreativeModeTab>> build() {
        return StreamSupport.stream(this.added().spliterator(), false)
                .collect(Collectors.toMap(AddedGroup::id, AddedGroup::group));
    }

    //endregion
    //region internals

    protected record AddedGroup(ResourceLocation id, NonNullConsumer<CreativeModeTab.Builder> tabBuilder,
                                CreativeTabContentBuilder contentBuilder, CompletableFuture<CreativeModeTab> group) {
    }

    protected record ModifiedGroup(@Nullable CreativeModeTab tab, CreativeTabContentBuilder contentBuilder) {
    }

    protected boolean hasAddedGroups() {
        return null != this._added && !this._added.isEmpty();
    }

    protected boolean hasModifiedGroups() {
        return null != this._modified && !this._modified.isEmpty();
    }

    protected final Iterable<AddedGroup> added() {
        return null != this._added ? this._added : Collections.emptyList();
    }

    protected final Iterable<ModifiedGroup> modified() {
        return null != this._modified ? this._modified : Collections.emptyList();
    }

    @Nullable
    private List<AddedGroup> _added;
    @Nullable
    private List<ModifiedGroup> _modified;

    //endregion
}

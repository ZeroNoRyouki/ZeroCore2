/*
 *
 * TagsHelper.java
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

package it.zerono.mods.zerocore.lib.tag;

import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class TagsHelper<T>
        extends TagSource<T> {

    public static final TagsHelper<Block> BLOCKS = new TagsHelper<>(CollectionProviders.BLOCKS_PROVIDER,
            id -> ForgeTagHandler.makeWrapperTag(ForgeRegistries.BLOCKS, id),
            rl -> ForgeTagHandler.createOptionalTag(ForgeRegistries.BLOCKS, rl));

    public static final TagsHelper<Item> ITEMS = new TagsHelper<>(CollectionProviders.ITEMS_PROVIDER,
            id -> ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, id),
            rl -> ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS, rl));

    public static final TagsHelper<Fluid> FLUIDS = new TagsHelper<>(CollectionProviders.FLUIDS_PROVIDER,
            id -> ForgeTagHandler.makeWrapperTag(ForgeRegistries.FLUIDS, id),
            rl -> ForgeTagHandler.createOptionalTag(ForgeRegistries.FLUIDS, rl));

    public static final ITag.INamedTag<Item> TAG_WRENCH = ITEMS.createForgeOptionalTag("tools/wrench");

    public static <T> T getTagFirstElement(final ITag<T> tag) {
        return Iterables.get(tag.getValues(), 0);
    }

    public ITag.INamedTag<T> createTag(final ResourceLocation tagId) {
        return this._factory.apply(tagId);
    }

    public Tags.IOptionalNamedTag<T> createOptionalTag(final ResourceLocation tagId) {
        return this._optionalFactory.apply(tagId);
    }

    public ITag.INamedTag<T> createTag(final String tagId) {
        return this.createTag(new ResourceLocation(tagId));
    }

    public Tags.IOptionalNamedTag<T> createOptionalTag(final String tagId) {
        return this.createOptionalTag(new ResourceLocation(tagId));
    }

    public ITag.INamedTag<T> createModTag(final String modId, final String tagName) {
        return this.createTag(new ResourceLocation(modId, tagName));
    }

    public Tags.IOptionalNamedTag<T> createModOptionalTag(final String modId, final String tagName) {
        return this.createOptionalTag(new ResourceLocation(modId, tagName));
    }

    public ITag.INamedTag<T> createForgeTag(final String tagName) {
        return this.createTag(new ResourceLocation("forge", tagName));
    }

    public Tags.IOptionalNamedTag<T> createForgeOptionalTag(final String tagName) {
        return this.createOptionalTag(new ResourceLocation("forge", tagName));
    }

    public boolean tagExist(final ResourceLocation tagId) {
        return this.getTag(tagId).isPresent();
    }

    public boolean tagExistWithContent(final ResourceLocation tagId) {
        return this.getTag(tagId).filter(tag -> tag.getValues().size() > 0).isPresent();
    }

    public Optional<T> getFirstElement(final ITag<T> tag) {
        return tag.getValues().isEmpty() ? Optional.empty() : Optional.of(getTagFirstElement(tag));
    }

    public Optional<T> getFirstElement(final ResourceLocation tagId) {
        return this.getTag(tagId)
                .filter(tag -> !tag.getValues().isEmpty())
                .map(TagsHelper::getTagFirstElement);
    }

    public List<T> getMatchingElements(final ITag<T> tag) {

        try {
            return tag.getValues();
        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }

    //region internals

    private TagsHelper(final NonNullSupplier<ITagCollection<T>> provider,
                       final NonNullFunction<ResourceLocation, ITag.INamedTag<T>> factory,
                       final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> optionalFactory) {

        super(provider);
        this._factory = factory;
        this._optionalFactory = optionalFactory;
    }

    private final NonNullFunction<ResourceLocation, ITag.INamedTag<T>> _factory;
    private final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> _optionalFactory;

    //endregion
}

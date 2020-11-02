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
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Optional;

public final class TagsHelper<T>
        extends TagSource<T> {

    public static final TagsHelper<Block> BLOCKS = new TagsHelper<>(CollectionProviders.BLOCKS_PROVIDER, BlockTags::makeWrapperTag, BlockTags::createOptional);
    public static final TagsHelper<Item> ITEMS = new TagsHelper<>(CollectionProviders.ITEMS_PROVIDER, ItemTags::makeWrapperTag, ItemTags::createOptional);

    public static <T> T getTagFirstElement(final ITag<T> tag) {
        return Iterables.get(tag.getAllElements(), 0);
    }

    public ITag.INamedTag<T> createTag(final String tagId) {
        return this._factory.apply(tagId);
    }

    public Tags.IOptionalNamedTag<T> createOptionalTag(final String tagId) {
        return this._optionalFactory.apply(new ResourceLocation(tagId));
    }

    public ITag.INamedTag<T> createModTag(final String modId, final String tagName) {
        return this.createTag(modId + ":" + tagName);
    }

    public Tags.IOptionalNamedTag<T> createOptionalModTag(final String modId, final String tagName) {
        return this.createOptionalTag(modId + ":" + tagName);
    }

    public ITag.INamedTag<T> createMinecraftTag(final String tagName) {
        return this.createTag("minecraft" + ":" + tagName);
    }

    public Tags.IOptionalNamedTag<T> createOptionalMinecraftTag(final String tagName) {
        return this.createOptionalTag("minecraft" + ":" + tagName);
    }

    public ITag.INamedTag<T> createForgeTag(final String tagName) {
        return this.createTag("forge" + ":" + tagName);
    }

    public Tags.IOptionalNamedTag<T> createOptionalForgeTag(final String tagName) {
        return this.createOptionalTag("forge" + ":" + tagName);
    }

    public boolean tagExist(final ResourceLocation tagId) {
        return this.getTag(tagId).isPresent();
    }

    public boolean tagExistWithContent(final ResourceLocation tagId) {
        return this.getTag(tagId).filter(tag -> tag.getAllElements().size() > 0).isPresent();
    }

    public Optional<T> getFirstElement(final ITag<T> tag) {
        return tag.getAllElements().isEmpty() ? Optional.empty() : Optional.of(getTagFirstElement(tag));
    }

    public Optional<T> getFirstElement(final ResourceLocation tagId) {
        return this.getTag(tagId)
                .filter(tag -> !tag.getAllElements().isEmpty())
                .map(TagsHelper::getTagFirstElement);
    }

    //region internals

    private TagsHelper(final NonNullSupplier<ITagCollection<T>> provider,
                       final NonNullFunction<String, ITag.INamedTag<T>> factory,
                       final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> optionalFactory) {

        super(provider);
        this._factory = factory;
        this._optionalFactory = optionalFactory;
    }

    private final NonNullFunction<String, ITag.INamedTag<T>> _factory;
    private final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> _optionalFactory;

    //endregion
}

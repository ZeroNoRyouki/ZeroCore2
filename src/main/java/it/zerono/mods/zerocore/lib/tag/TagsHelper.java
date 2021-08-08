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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.resources.ResourceLocation;
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

    public static <T> T getTagFirstElement(final Tag<T> tag) {
        return Iterables.get(tag.getValues(), 0);
    }

    public Tag.Named<T> createTag(final ResourceLocation tagId) {
        return this._factory.apply(tagId);
    }

    public Tags.IOptionalNamedTag<T> createOptionalTag(final ResourceLocation tagId) {
        return this._optionalFactory.apply(tagId);
    }

    public Tag.Named<T> createTag(final String tagId) {
        return this.createTag(new ResourceLocation(tagId));
    }

    public Tags.IOptionalNamedTag<T> createOptionalTag(final String tagId) {
        return this.createOptionalTag(new ResourceLocation(tagId));
    }

    public Tag.Named<T> createModTag(final String modId, final String tagName) {
        return this.createTag(new ResourceLocation(modId, tagName));
    }

    public Tags.IOptionalNamedTag<T> createModOptionalTag(final String modId, final String tagName) {
        return this.createOptionalTag(new ResourceLocation(modId, tagName));
    }

    public Tag.Named<T> createForgeTag(final String tagName) {
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

    public Optional<T> getFirstElement(final Tag<T> tag) {
        return tag.getValues().isEmpty() ? Optional.empty() : Optional.of(getTagFirstElement(tag));
    }

    public Optional<T> getFirstElement(final ResourceLocation tagId) {
        return this.getTag(tagId)
                .filter(tag -> !tag.getValues().isEmpty())
                .map(TagsHelper::getTagFirstElement);
    }

    public List<T> getMatchingElements(final Tag<T> tag) {

        try {
            return tag.getValues();
        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }

    //region internals

    private TagsHelper(final NonNullSupplier<TagCollection<T>> provider,
                       final NonNullFunction<ResourceLocation, Tag.Named<T>> factory,
                       final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> optionalFactory) {

        super(provider);
        this._factory = factory;
        this._optionalFactory = optionalFactory;
    }

    private final NonNullFunction<ResourceLocation, Tag.Named<T>> _factory;
    private final NonNullFunction<ResourceLocation, Tags.IOptionalNamedTag<T>> _optionalFactory;

    //endregion
}

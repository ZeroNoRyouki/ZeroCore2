/*
 *
 * TagList.java
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TagList<T>
        extends TagSource<T> {

    public TagList(final NonNullSupplier<TagCollection<T>> provider) {

        super(provider);
        this._ids = Lists.newArrayList();
        this._tags = Maps.newHashMap();
    }

    /**
     * Get a Tag from this list
     *
     * @param id the id of the Tag
     * @return the requested Tag if present, null otherwise
     */
    @Override
    public Optional<Tag<T>> getTag(final ResourceLocation id) {
        return Optional.ofNullable(this._tags.get(id));
    }

    public <R> R mapTag(final ResourceLocation id, final NonNullFunction<Tag<T>, R> mapper, final R defaultValue) {

        final Tag<T> tag = this._tags.get(id);

        return null != tag ? mapper.apply(tag) : defaultValue;
    }

    public void forTag(final ResourceLocation id, final NonNullConsumer<Tag<T>> consumer) {

        final Tag<T> tag = this._tags.get(id);

        if (null != tag) {
            consumer.accept(tag);
        }
    }

//    /**
//     * Add a Tag to the list by first looking it up in the associated collection
//     *
//     * @param id the id of the Tag to add
//     * @return true if the Tag was found and added, false otherwise
//     */
//    public boolean addTag(final ResourceLocation id) {
//
//        final ITag<T> tag = this.getCollection().get(id);
//
//        if (null != tag) {
//
//            this.addTag(tag);
//            return true;
//        }
//
//        return false;
//    }

    /**
     * Add a Tag to the list
     *
     * @param namedTag the Tag to add
     */
    public void addTag(final Tag.Named<T> namedTag) {

        this._ids.add(namedTag.getName());
        this._tags.put(namedTag.getName(), namedTag);
    }

    /**
     * Remove a Tag from the list
     *
     * @param id the id of the Tag to remove
     */
    public void removeTag(final ResourceLocation id) {

        this._ids.remove(id);
        this._tags.remove(id);
    }

    /**
     * Remove a Tag from the list
     *
     * @param tag the Tag to remove
     */
    public void removeTag(final Tag.Named<T> tag) {
        this.removeTag(tag.getName());
    }

    /**
     * Remove all Tags from the list
     */
    public void clear() {

        this._ids.clear();
        this._tags.clear();
    }

    /**
     * Reload all Tags from the associated collection
     */
    public void reloadTags() {

        final TagCollection<T> collection = this.getCollection();

        this._tags.clear();

        for (final ResourceLocation id : this._ids) {

            final Tag<T> tag = collection.getTag(id);

            if (null != tag) {
                this._tags.put(id, tag);
            }
        }
    }

    /**
     * Check if a Tag is in the list
     *
     * @param tag the Tag to check
     * @return true if the Tag is found, false otherwise
     */
    public boolean contains(final Tag.Named<T> tag) {
        return this.contains(tag.getName());
    }

    /**
     * Check if a Tag is in the list
     *
     * @param id the Id of the Tag to check
     * @return true if the Tag is found, false otherwise
     */
    public boolean contains(final ResourceLocation id) {
        return this._ids.contains(id);
    }

    /**
     * Find the fist Tag that match the given predicate
     *
     * @param predicate the predicate to check
     * @return the fist Tag in the list that matches the predicate or null if no such Tag is found
     */
    public Optional<Tag<T>> find(final Predicate<Tag<T>> predicate) {
        return this._tags.values().stream().filter(predicate).findFirst();
    }

    public Stream<ResourceLocation> idStream() {
        return this._ids.stream();
    }

    public Stream<Tag<T>> tagStream() {
        return this._tags.values().stream();
    }

    //region internals

    private final List<ResourceLocation> _ids;
    private final Map<ResourceLocation, Tag<T>> _tags;

    //endregion
}

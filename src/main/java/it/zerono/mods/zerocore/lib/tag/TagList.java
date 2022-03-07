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

import it.unimi.dsi.fastutil.objects.*;
import it.zerono.mods.zerocore.lib.data.WeakReferenceGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TagList<T> {

    public static <T> TagList<T> create(final TagSource<T> source) {

        final TagList<T> list = new TagList<>(source);

        s_lists.add(list);
        return list;
    }

    public static <T> TagList<T> create(final NonNullSupplier<Registry<T>> registrySupplier) {
        return create(new TagSource<>(registrySupplier));
    }

    public static TagList<Block> blocks() {
        return create(TagsHelper.BLOCKS);
    }

    public static TagList<Item> items() {
        return create(TagsHelper.ITEMS);
    }

    public static TagList<Fluid> fluids() {
        return create(TagsHelper.FLUIDS);
    }

    public void addTag(final TagKey<T> tagKey) {
        this._ids.add(tagKey);
    }

    public void addTag(final ResourceLocation id) {
        this.addTag(this._source.createKey(id));
    }

    public void removeTag(final TagKey<T> tagKey) {

        this._ids.remove(tagKey);
        this._objects.remove(tagKey);
    }

    public void removeTag(final ResourceLocation id) {
        this.removeTag(this._source.createKey(id));
    }

    public void clear() {

        this._objects.clear();
        this._ids.clear();
    }

    public boolean contains(final TagKey<T> tagKey) {
        return this._ids.contains(tagKey);
    }

    public boolean contains(final ResourceLocation id) {
        return this.contains(this._source.createKey(id));
    }

    public Stream<TagKey<T>> stream() {
        return this._ids.stream();
    }

    public List<T> objects(final TagKey<T> tagKey) {
        return this._objects.getOrDefault(tagKey, ObjectLists.emptyList());
    }

    public Optional<T> first(final TagKey<T> tagKey) {

        final List<T> objects = this.objects(tagKey);

        return objects.isEmpty() ? Optional.empty() : Optional.ofNullable(objects.get(0));
    }

    public boolean match(final Predicate<TagKey<T>> test) {
        return this._ids.stream().anyMatch(test);
    }

    public Optional<TagKey<T>> findFirst(final Predicate<TagKey<T>> test) {
        return this._ids.stream()
                .filter(test)
                .findFirst();
    }

    public void forEach(final Consumer<? super TagKey<T>> action) {
        this._ids.forEach(action);
    }

    public void forEach(final TagKey<T> tagKey, final Consumer<? super T> action) {
        this.objects(tagKey).forEach(action);
    }

    public static void initialize() {
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener(EventPriority.LOWEST,
                (TagsUpdatedEvent e) -> s_lists.forEach(TagList::updateTags));
    }

    //region internals

    private TagList(final TagSource<T> source) {

        this._source = source;
        this._ids = new ObjectArrayList<>(8);
        this._objects = new Object2ObjectArrayMap<>(8);
        this._objects.defaultReturnValue(ObjectLists.emptyList());
    }

    private void updateTags() {

        this._objects.clear();
        this._ids.forEach(key -> this._objects.put(key, this._source.getObjects(key)));
    }

    private static final WeakReferenceGroup<TagList<?>> s_lists = new WeakReferenceGroup<>();

    private final TagSource<T> _source;
    private final ObjectList<TagKey<T>> _ids;
    private final Object2ObjectMap<TagKey<T>, List<T>> _objects;

    //endregion
}

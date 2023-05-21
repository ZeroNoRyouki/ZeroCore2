package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.Set;

public class ModTagAppender<T> {

    public ModTagAppender(TagBuilder builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        this._builder = builder;
        this._addedElements = new ObjectArraySet<>(128);
        this._addedOptionalElements = new ObjectArraySet<>(128);
        this._addedTags = new ObjectArraySet<>(128);
        this._addedOptionalTags = new ObjectArraySet<>(128);
    }

    public ModTagAppender<T> add(ResourceKey<T> element) {

        Preconditions.checkNotNull(element, "Element must not be null");

        final var id = element.location();

        Preconditions.checkState(this._addedElements.add(id),
                "The element with ID %s was already added to this tag", id);

        this._builder.addElement(id);
        return this;
    }

    public ModTagAppender<T> addOptional(ResourceLocation optionalElement) {

        Preconditions.checkNotNull(optionalElement, "Optional element must not be null");
        Preconditions.checkState(this._addedOptionalElements.add(optionalElement),
                "The optional element with ID %s was already added to this tag", optionalElement);

        this._builder.addOptionalElement(optionalElement);
        return this;
    }

    public ModTagAppender<T> addTag(TagKey<T> tag) {

        Preconditions.checkNotNull(tag, "Tag must not be null");

        final var id = tag.location();

        Preconditions.checkState(this._addedTags.add(id),
                "The tag with ID %s was already added to this tag", id);

        this._builder.addTag(id);
        return this;
    }

    public ModTagAppender<T> addOptionalTag(ResourceLocation optionalTag) {

        Preconditions.checkNotNull(optionalTag, "Optional tag must not be null");
        Preconditions.checkState(this._addedOptionalTags.add(optionalTag),
                "The optional tag with ID %s was already added to this tag", optionalTag);

        this._builder.addOptionalTag(optionalTag);
        return this;
    }

    //region internals

    private final TagBuilder _builder;
    private final Set<ResourceLocation> _addedElements;
    private final Set<ResourceLocation> _addedOptionalElements;
    private final Set<ResourceLocation> _addedTags;
    private final Set<ResourceLocation> _addedOptionalTags;

    //endregion
}

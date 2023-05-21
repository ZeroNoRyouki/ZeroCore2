package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.functional.NonNullBiFunction;
import net.minecraft.data.models.blockstates.Variant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ModelVariantsList
        implements NonNullBiFunction<String, JsonObject, JsonObject> {

    protected ModelVariantsList(int initialCapacity) {

        Preconditions.checkArgument(initialCapacity > 0);
        this._modelVariants = new ObjectArrayList<>(initialCapacity);
    }

    void add(Variant... variants) {
        Collections.addAll(this._modelVariants, variants);
    }

    //region NonNullBiConsumer<String, JsonElement>

    @ApiStatus.Internal
    @Override
    public @NotNull JsonObject apply(@NotNull String name, @NotNull JsonObject parent) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkNotNull(name, "Name must not be null");
        parent.add(name, Variant.convertList(this._modelVariants));
        return parent;
    }

    //endregion
    //region internals

    private final List<Variant> _modelVariants;

    //endregion
}

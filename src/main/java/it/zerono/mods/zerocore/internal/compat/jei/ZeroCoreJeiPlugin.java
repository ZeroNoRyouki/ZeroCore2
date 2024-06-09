package it.zerono.mods.zerocore.internal.compat.jei;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.ZeroCore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@JeiPlugin
public class ZeroCoreJeiPlugin
        implements IModPlugin {

    public ZeroCoreJeiPlugin() {
        s_instance = this;
    }

    public static ZeroCoreJeiPlugin getInstance() {

        Preconditions.checkState(null != s_instance, "Trying to get an instance of ZeroCoreJeiPlugin before one is available");
        return s_instance;
    }

    public void displayRecipeType(RecipeType<?> type, RecipeType<?>... others) {

        if (null == this._jeiRuntime) {
            return;
        }

        final List<RecipeType<?>> types = new ObjectArrayList<>(1 + others.length);

        types.add(type);

        if (others.length > 0) {
            Collections.addAll(types, others);
        }

        this._jeiRuntime.getRecipesGui().showTypes(types);
    }

    //region IModPlugin

    @Override
    public ResourceLocation getPluginUid() {
        return s_id;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this._jeiRuntime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        this._jeiRuntime = null;
    }

    //endregion
    //region internals

    private static final ResourceLocation s_id = ZeroCore.ROOT_LOCATION.buildWithSuffix("jeiplugin");

    private static ZeroCoreJeiPlugin s_instance;

    @Nullable
    private IJeiRuntime _jeiRuntime;

    //endregion
}

package it.zerono.mods.zerocore.internal.compat.jei;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.ZeroCore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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

    public IJeiRuntime getJeiRuntime() {

        Preconditions.checkState(null != this._jeiRuntime, "JEI Runtime not available");
        return this._jeiRuntime;
    }

    public IRecipesGui getRecipesGui() {
        return getJeiRuntime().getRecipesGui();
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

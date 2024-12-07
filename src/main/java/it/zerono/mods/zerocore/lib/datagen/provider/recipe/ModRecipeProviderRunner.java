/*
 *
 * ModRecipeProviderRunner.java
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

package it.zerono.mods.zerocore.lib.datagen.provider.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.IModDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.ProviderSettings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProviderRunner<Provider extends ModRecipeProvider>
        extends RecipeProvider.Runner
        implements IModDataProvider {

    public ModRecipeProviderRunner(String providerName, CompletableFuture<HolderLookup.Provider> registryLookup,
                                   PackOutput output, ResourceLocationBuilder modLocationRoot,
                                   TriFunction<@NotNull ModRecipeProviderRunner<Provider>,
                                           HolderLookup.@NotNull Provider, @NotNull RecipeOutput,
                                           @NotNull Provider> providerFactory) {

        super(output, registryLookup);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(providerName), "Provider name must not be null or empty");
        Preconditions.checkNotNull(output, "Output must not be null");
        Preconditions.checkNotNull(registryLookup, "Registry lookup must not be null");
        Preconditions.checkNotNull(modLocationRoot, "Mod location root must not be null");
        Preconditions.checkNotNull(providerFactory, "Provider factory must not be null");

        this._settings = new ProviderSettings(providerName, output, registryLookup, modLocationRoot);
        this._providerFactory = providerFactory;
    }

    //region RecipeProvider.Runner

    @Override
    public String getName() {
        return this.getSettings().name();
    }

    @Override
    protected Provider createRecipeProvider(HolderLookup.Provider registryLookupProvider, RecipeOutput output) {
        return this._providerFactory.apply(this, registryLookupProvider, output);
    }

    //endregion
    //region IModDataProvider

    @Override
    public void provideData() {
    }

    @Override
    public CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup) {
        return super.run(cache);
    }

    @Override
    public ProviderSettings getSettings() {
        return this._settings;
    }

    //endregion
    //region internals

    private final ProviderSettings _settings;
    private final TriFunction<@NotNull ModRecipeProviderRunner<Provider>, HolderLookup.@NotNull Provider,
            @NotNull RecipeOutput, @NotNull Provider> _providerFactory;

    //endregion
}

/*
 *
 * BakedModelSupplier.java
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

package it.zerono.mods.zerocore.lib.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Deprecated // use ModBakedModelSupplier
public class BakedModelSupplier {

    public static final BakedModelSupplier INSTANCE = new BakedModelSupplier();

    public static Supplier<BakedModel> create(final ResourceLocation name) {
        return create(name, false);
    }

    public static Supplier<BakedModel> create(final ResourceLocation name, final boolean register) {

        if (register) {
            INSTANCE.addToLoadingList(name);
        }

        return INSTANCE.getOrCreate(name);
    }

    //region event handlers

    @SubscribeEvent
    public void onRegisterModels(final ModelRegistryEvent event) {
        this._toBeRegistered.forEach(ModelLoader::addSpecialModel);
    }

    @SubscribeEvent
    public void onModelBake(final ModelBakeEvent event) {

        final Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        final BakedModel missing = ModRenderHelper.getMissingModel();

        this._wrappers.forEach((key, value) -> value._cachedModel = modelRegistry.getOrDefault(key, missing));
    }

    //endregion
    //region internals
    //region SpriteSupplier

    private static class BakedModelWrapper implements Supplier<BakedModel> {

        protected BakedModelWrapper() {
            this._cachedModel = null;
        }

        //region Supplier

        @Override
        public BakedModel get() {
            return this._cachedModel;
        }

        //endregion
        //region internals

        private BakedModel _cachedModel;

        //endregion
    }

    private BakedModelSupplier() {

        this._toBeRegistered = Lists.newArrayList();
        this._wrappers = Maps.newHashMap();
    }

    private void addToLoadingList(final ResourceLocation name) {

        if (!this._toBeRegistered.contains(name)) {
            this._toBeRegistered.add(name);
        }
    }

    private Supplier<BakedModel> getOrCreate(final ResourceLocation name) {
        return INSTANCE._wrappers.computeIfAbsent(name, resourceLocation -> new BakedModelWrapper());
    }

    private final List<ResourceLocation> _toBeRegistered;
    private final Map<ResourceLocation, BakedModelWrapper> _wrappers;

    //endregion
}

/*
 *
 * ModBakedModelSupplier.java
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
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModBakedModelSupplier {

    public ModBakedModelSupplier() {

        this._toBeRegistered = Lists.newArrayList();
        this._wrappers = Maps.newHashMap();

        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(this);
    }

    public void addModel(final ResourceLocation name) {

        if (!this._toBeRegistered.contains(name)) {
            this._toBeRegistered.add(name);
        }
    }

    public Supplier<BakedModel> getOrCreate(final ResourceLocation name) {
        return this._wrappers.computeIfAbsent(name, resourceLocation -> new Wrapper());
    }

    //region event handlers

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        this._toBeRegistered.forEach(event::register);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onModelBake(final BakingCompleted event) {

        final Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        final BakedModel missing = ModRenderHelper.getMissingModel();

        this._wrappers.forEach((key, value) -> value._cachedModel = modelRegistry.getOrDefault(key, missing));
    }

    //endregion

    //region internals
    //region SpriteSupplier

    private static class Wrapper
            implements Supplier<BakedModel> {

        protected Wrapper() {
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

    private final List<ResourceLocation> _toBeRegistered;
    private final Map<ResourceLocation, Wrapper> _wrappers;

    //endregion
}

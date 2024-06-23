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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModBakedModelSupplier {

    public ModBakedModelSupplier(IEventBus modBus) {

        this._toBeRegistered = new ObjectArrayList<>(32);
        this._wrappers = new Object2ObjectOpenHashMap<>(32);

        modBus.register(this);
    }

    public void addModel(final ModelResourceLocation model) {

        if (!this._toBeRegistered.contains(model)) {
            this._toBeRegistered.add(model);
        }
    }

    public Supplier<BakedModel> getOrCreate(final ModelResourceLocation model) {
        return this._wrappers.computeIfAbsent(model, $ -> new Wrapper());
    }

    //region event handlers

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        this._toBeRegistered.forEach(event::register);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onModelBake(final BakingCompleted event) {

        final Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
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

    private final List<ModelResourceLocation> _toBeRegistered;
    private final Map<ModelResourceLocation, Wrapper> _wrappers;

    //endregion
}

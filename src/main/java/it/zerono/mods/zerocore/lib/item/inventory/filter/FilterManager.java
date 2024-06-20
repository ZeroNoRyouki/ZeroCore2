/*
 *
 * FilterManager.java
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

package it.zerono.mods.zerocore.lib.item.inventory.filter;

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.internal.Log;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public final class FilterManager<T extends IFilterComponent> {

    @SuppressWarnings("unchecked")
    public static <T extends IFilterComponent> FilterManager<T> getInstance() {
        return s_instance;
    }

    /**
     * Create a new instance of the requested component
     *
     * @param componentId the component id of the component to create
     * @return the new component instance initialized with it's default data or null if the component
     * could not be created (unknown component, etc)
     */
    public Optional<T> createComponent(final ResourceLocation componentId) {

        final Optional<IFilterComponentFactory<T>> factory = this.getFactory(componentId);

        if (!factory.isPresent()) {

            Log.LOGGER.warn("Unable to create filter component: no factory found for {}}", componentId);
            return Optional.empty();
        }

        return factory.flatMap(f -> f.createComponent(componentId));
    }

    /**
     * Load a component from an NBT tag
     * <p>
     * The component should have been saved using FilterManager.storeComponentToNBT()
     *
     * @param nbt the NBT tag to load the component from
     * @return a new component initialized with the data loaded from the NBT tag or null if the component
     * could not be created (unknown component, invalid data, etc)
     */
    public Optional<T> loadComponentFromNBT(HolderLookup.Provider registries, CompoundTag nbt) {

        if (!nbt.contains(KEY_COMPONENT_ID) || !nbt.contains(KEY_COMPONENT_DATA)) {
            return Optional.empty();
        }

        final ResourceLocation componentId = ResourceLocation.parse(nbt.getString(KEY_COMPONENT_ID));
        final Optional<IFilterComponentFactory<T>> factory = this.getFactory(componentId);

        if (!factory.isPresent()) {

            Log.LOGGER.warn("Unable to load filter component: no factory found for {}", componentId);
            return Optional.empty();
        }

        return factory.flatMap(f -> f.createComponent(componentId, registries, nbt.getCompound(KEY_COMPONENT_DATA)));
    }

    /**
     * Save a component data in an NBT tag
     * <p>
     * The component can be loaded back using FilterManager.loadComponentFromNBT()
     *
     * @param component the component to save to NBT
     * @return a new NBT tag containing the component data
     */
    public CompoundTag storeComponentToNBT(HolderLookup.Provider registries, T component) {

        final CompoundTag tag = new CompoundTag();

        tag.putString(KEY_COMPONENT_ID, component.getComponentId().toString());
        tag.put(KEY_COMPONENT_DATA, component.serializeNBT(registries));
        return tag;
    }

    public void registerFactory(ResourceLocation componentId, final IFilterComponentFactory<T> factory) {

        if (null == this._factories) {
            this._factories = Maps.newHashMap();
        }

        this._factories.put(componentId, factory);
    }

    //region internals

    private Optional<IFilterComponentFactory<T>> getFactory(ResourceLocation componentId) {
        return null != this._factories && this._factories.containsKey(componentId) ?
                Optional.of(this._factories.get(componentId)) : Optional.empty();
    }

    private FilterManager() {
    }

    private Map<ResourceLocation, IFilterComponentFactory<T>> _factories;

    private static final FilterManager s_instance = new FilterManager<>();
    private static final String KEY_COMPONENT_ID = "cid";
    private static final String KEY_COMPONENT_DATA = "cdata";
}

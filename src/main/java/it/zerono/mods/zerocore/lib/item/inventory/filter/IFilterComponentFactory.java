/*
 *
 * IFilterComponentFactory.java
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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

/**
 * Factory class for filter components
 *
 * Register your own with the FilterManager
 *
 * @param <T>   your component class
 */
public interface IFilterComponentFactory<T extends IFilterComponent> {

    /**
     * Create a new compoment with a default configuration
     *
     * @param componentId   the id of the component to create
     * @return  a new component or null if the component could not be created (unknown component id, etc)
     */
    Optional<T> createComponent(ResourceLocation componentId);

    /**
     * Create a new component by loading it's data from the provided NBT tag
     *
     * @param componentId   the id of the component to create
     * @param nbt           the NBT tag holding the data of the component
     * @return  a new component initialized with the provided data or null if the component could not be
     *          created (unknown component id, invalid data, etc)
     */
    Optional<T> createComponent(ResourceLocation componentId, CompoundNBT nbt);
}

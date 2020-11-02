/*
 *
 * AbstractModelDataMap.java
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

package it.zerono.mods.zerocore.lib.client.model.data;

import com.google.common.base.Preconditions;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AbstractModelDataMap implements IModelData {

    protected AbstractModelDataMap() {
        this._map = new IdentityHashMap<>(8);
    }

    protected void addProperty(ModelProperty<?> property) {
        this._map.put(property, null);
    }

    protected <T> void addProperty(ModelProperty<T> property, T data) {
        this._map.put(property, data);
    }

    //region IModelData

    @Override
    public boolean hasProperty(final ModelProperty<?> property) {
        return this._map.containsKey(property);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData(final ModelProperty<T> property) {
        return (T) this._map.get(property);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T setData(final ModelProperty<T> property, final T data) {

        Preconditions.checkArgument(property.test(data), "Invalid data for this property");
        return (T) this._map.put(property, data);
    }

    //endregion
    //region Object

    @Override
    public int hashCode() {
        return this._map.hashCode();
    }

    //endregion
    //region internals

    private final Map<ModelProperty<?>, Object> _map;

    //endregion
}

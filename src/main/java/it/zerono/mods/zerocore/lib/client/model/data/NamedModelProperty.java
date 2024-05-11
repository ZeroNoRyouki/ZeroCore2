/*
 *
 * NamedModelProperty.java
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
import com.google.common.base.Strings;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.function.Predicate;

public class NamedModelProperty<T> extends ModelProperty<T> {

    public NamedModelProperty(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this._name = name;
    }

    public NamedModelProperty(final String name, Predicate<T> pred) {

        super(pred);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this._name = name;
    }

    //region Object

    @Override
    public String toString() {
        return this._name;
    }

    //endregion
    //region internals

    private final String _name;

    //endregion
}

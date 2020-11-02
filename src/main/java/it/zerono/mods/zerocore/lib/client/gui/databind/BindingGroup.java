/*
 *
 * BindingGroup.java
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

package it.zerono.mods.zerocore.lib.client.gui.databind;

import com.google.common.collect.Lists;

import java.util.List;

public class BindingGroup {

    public BindingGroup() {
        this._bindings = Lists.newArrayList();
    }

    public void addBinding(final IBinding binding) {
        this._bindings.add(binding);
    }

    public void update() {
        this._bindings.forEach(IBinding::update);
    }

    public void close() {
        this._bindings.forEach(IBinding::close);
    }

    //region internals

    final List<IBinding> _bindings;

    //endregion
}

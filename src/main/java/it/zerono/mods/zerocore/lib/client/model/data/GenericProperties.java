/*
 *
 * GenericProperties.java
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

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Objects;

public final class GenericProperties {

    /**
     * The generic Id of a Block
     */
    public static final ModelProperty<Integer> ID = new NamedModelProperty<>("ID", Objects::nonNull);

    /**
     * The generic index of a specific variant of a Block model
     */
    public static final ModelProperty<Integer> VARIANT_INDEX = new NamedModelProperty<>("VARIANT_INDEX", Objects::nonNull);

    @SuppressWarnings("ConstantConditions")
    public static int getId(final IModelData data) {
        return data.getData(ID);
    }

    @SuppressWarnings("ConstantConditions")
    public static int getVariantIndex(final IModelData data) {
        return data.getData(VARIANT_INDEX);
    }

    private GenericProperties() {
    }
}

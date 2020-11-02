/*
 *
 * PartProperties.java
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

package it.zerono.mods.zerocore.lib.client.model.data.multiblock;

import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.client.model.data.NamedModelProperty;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class PartProperties {

    //region Generic parts

    /**
     * The hash of the model data
     */
    public static final ModelProperty<Integer> STATE_HASH = new NamedModelProperty<>("STATE_HASH", Objects::nonNull);

    /**
     * Indicates if the part multiblock is assembled or not
     */
    public static final ModelProperty<Boolean> ASSEMBLED = new NamedModelProperty<>("ASSEMBLED", Objects::nonNull);

    /**
     * Indicates the type of the multiblock part
     */
    public static final ModelProperty<IMultiblockPartType> TYPE = new NamedModelProperty<>("TYPE", Objects::nonNull);

    //endregion
    //region Cuboid parts

    /**
     * Indicates the outward facings of the cuboid multiblock part
     */
    public static final ModelProperty<BlockFacings> OUTWARD_FACING = new NamedModelProperty<>("OUTWARD_FACING", Objects::nonNull);

    /**
     * Indicates the position of the cuboid part inside the multiblock
     */
    public static final ModelProperty<PartPosition> POSITION = new NamedModelProperty<>("POSITION", Objects::nonNull);

    /**
     * Indicates the model variant to use
     */
    public static final ModelProperty<Byte> MODEL_VARIANT = new NamedModelProperty<>("MODEL_VARIANT", Objects::nonNull);

    /**
     * The hash of the model data
     */
    public static final ModelProperty<Function<IModelData, Integer>> MODEL_VARIANT_MAPPER = new NamedModelProperty<>("MODEL_VARIANT_MAPPER", Objects::nonNull);

    //endregion
    //region helper functions

    /**
     * Retrieve the STATE_HASH value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the STATE_HASH property
     * @throws NullPointerException if the model data do not contains a value for this property or it's value is null
     */
    @SuppressWarnings("ConstantConditions")
    public static int getStateHash(final IModelData data) {
        return data.getData(STATE_HASH);
    }

    /**
     * Retrieve the ASSEMBLED value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the ASSEMBLED property
     * @throws NullPointerException if the model data do not contains a value for this property or it's value is null
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean getAssembled(final IModelData data) {
        return data.getData(ASSEMBLED);
    }

    /**
     * Retrieve the TYPE value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the TYPE property
     */
    @SuppressWarnings("ConstantConditions")
    public static IMultiblockPartType getPartType(final IModelData data) {
        return data.getData(TYPE);
    }

    /**
     * Retrieve the OUTWARD_FACING value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the OUTWARD_FACING property
     */
    @SuppressWarnings("ConstantConditions")
    public static BlockFacings getOutwardFacing(final IModelData data) {
        return data.getData(OUTWARD_FACING);
    }

    /**
     * Retrieve the POSITION value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the POSITION property
     */
    @SuppressWarnings("ConstantConditions")
    public static PartPosition getPosition(final IModelData data) {
        return data.getData(POSITION);
    }

    /**
     * Retrieve the MODEL_VARIANT value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the MODEL_VARIANT property
     */
    @SuppressWarnings("ConstantConditions")
    public static Byte getModelVariant(final IModelData data) {
        return data.getData(MODEL_VARIANT);
    }

    /**
     * Retrieve the MODEL_VARIANT_MAPPER value from the model data.
     * This method assume that the model data contains the requested property and a non null value for it
     *
     * @param data the model data
     * @return value of the MODEL_VARIANT_MAPPER property
     */
    @SuppressWarnings("ConstantConditions")
    public static Function<IModelData, Integer> getModelVariantMapper(final IModelData data) {
        return data.getData(MODEL_VARIANT_MAPPER);
    }

    //endregion

    private PartProperties() {
    }
}

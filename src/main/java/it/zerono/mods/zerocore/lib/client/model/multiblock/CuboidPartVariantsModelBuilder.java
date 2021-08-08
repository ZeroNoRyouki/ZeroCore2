/*
 *
 * CuboidPartVariantsModelBuilder.java
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

package it.zerono.mods.zerocore.lib.client.model.multiblock;

import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModel;
import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModelBuilder;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;

public class CuboidPartVariantsModelBuilder extends BlockVariantsModelBuilder {

    public CuboidPartVariantsModelBuilder(final ResourceLocation template, final boolean ambientOcclusion) {

        super(ambientOcclusion, true, false);
        this._templateId = template;
    }

    @Override
    protected BlockVariantsModel createReplacementModel(int blockCount, boolean ambientOcclusion, boolean guid3D, boolean builtInRenderer) {
        return new CuboidPartVariantsModel(this._templateModel, blockCount, ambientOcclusion);
    }

    //region ICustomModelBuilder

    @Override
    public void onRegisterModels() {

        ModelLoader.addSpecialModel(this._templateId);
        super.onRegisterModels();
    }

    @Override
    public void onBakeModels(ModelBakeEvent event) {

        this._templateModel = event.getModelRegistry().getOrDefault(this._templateId, ModRenderHelper.getMissingModel());
        super.onBakeModels(event);
    }

    //endregion
    //region internals

    private final ResourceLocation _templateId;
    private BakedModel _templateModel;

    //endregion
}

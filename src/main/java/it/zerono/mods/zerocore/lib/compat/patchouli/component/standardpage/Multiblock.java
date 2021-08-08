/*
 *
 * Multiblock.java
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

package it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.zerono.mods.zerocore.lib.compat.patchouli.Patchouli;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;
import com.mojang.math.Vector4f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageEmpty;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;

import java.util.Random;
import java.util.function.UnaryOperator;

public class Multiblock
        extends AbstractStandardPageComponent<PageEmpty> {

    String name = "";
    @SerializedName("multiblock_id") String multiblockId;

    protected Multiblock() {
        super(new PageEmpty());
    }

    //region AbstractStandardPageComponent

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {

        super.onVariablesAvailable(lookup);
        this.multiblockId = lookup.apply(IVariable.wrap(multiblockId)).asString();
    }

    @Override
    public void build(BookPage page, BookEntry entry, int pageNum) {

        this.book = page.book;

        if (multiblockId != null) {
            IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(new ResourceLocation(multiblockId));

            if (mb instanceof AbstractMultiblock) {
                multiblockObj = (AbstractMultiblock) mb;
            }
        }

        if (multiblockObj == null) {
            throw new IllegalArgumentException("No multiblock located for " + multiblockId);
        }
    }

    @Override
    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {

        this.parent = parent;
        this.mc = parent.getMinecraft();
    }

    @Override
    public boolean mouseScrolled(BookPage page, double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //endregion
    //region internals
    //region hacky hack needed until patchouli support IModelData natively

    private transient Book book;
    private transient GuiBookEntry parent;
    private transient AbstractMultiblock multiblockObj;
    public transient Minecraft mc;
    private static final Random RAND = new Random();

    @Override
    protected void renderPage(final PoseStack ms, final int mouseX, final int mouseY, final float partialTicks) {

        int x = GuiBook.PAGE_WIDTH / 2 - 53;
        int y = 7;
        RenderSystem.enableBlend();
        RenderSystem.color3f(1F, 1F, 1F);
        GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

        parent.drawCenteredStringNoShadow(ms, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

        if (multiblockObj != null) {
            renderMultiblock(ms);
        }
    }

    private void renderMultiblock(PoseStack ms) {

        multiblockObj.setWorld(mc.level);
        Vec3i size = multiblockObj.getSize();
        int sizeX = size.getX();
        int sizeY = size.getY();
        int sizeZ = size.getZ();
        float maxX = 90;
        float maxY = 90;
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
        float scaleX = maxX / diag;
        float scaleY = maxY / sizeY;
        float scale = -Math.min(scaleX, scaleY);

        int xPos = GuiBook.PAGE_WIDTH / 2;
        int yPos = 60;
        ms.pushPose();
        ms.translate(xPos, yPos, 100);
        ms.scale(scale, scale, scale);
        ms.translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);

        // Initial eye pos somewhere off in the distance in the -Z direction
        Vector4f eye = new Vector4f(0, 0, -100, 1);
        Matrix4f rotMat = new Matrix4f();
        rotMat.setIdentity();

        // For each GL rotation done, track the opposite to keep the eye pos accurate
        ms.mulPose(Vector3f.XP.rotationDegrees(-30F));
        rotMat.multiply(Vector3f.XP.rotationDegrees(30));

        float offX = (float) -sizeX / 2;
        float offZ = (float) -sizeZ / 2 + 1;

        float time = parent.ticksInBook * 0.5F;
        if (!Screen.hasShiftDown()) {
            time += ClientTicker.partialTicks;
        }
        ms.translate(-offX, 0, -offZ);
        ms.mulPose(Vector3f.YP.rotationDegrees(time));
        rotMat.multiply(Vector3f.YP.rotationDegrees(-time));
        ms.mulPose(Vector3f.YP.rotationDegrees(45));
        rotMat.multiply(Vector3f.YP.rotationDegrees(-45));
        ms.translate(offX, 0, offZ);

        // Finally apply the rotations
        eye.transform(rotMat);
        eye.normalize();
		/* TODO XXX This does not handle visualization of sparse multiblocks correctly.
			Dense multiblocks store everything in positive X/Z, so this works, but sparse multiblocks store everything from the JSON as-is.
			Potential solution: Rotate around the offset vars of the multiblock, and add AABB method for extent of the multiblock
		*/
        renderElements(ms, multiblockObj, BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

        ms.popPose();
    }

    private void renderElements(PoseStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
        ms.pushPose();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        ms.translate(0, 0, -1);

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        doWorldRenderPass(ms, mb, blocks, buffers, eye);
//        doTileEntityRenderPass(ms, mb, blocks, buffers, eye);

        // todo 1.15 transparency sorting
        buffers.endBatch();
        ms.popPose();
    }

    private void doWorldRenderPass(PoseStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks,
                                   final MultiBufferSource.BufferSource buffers, Vector4f eye) {

        for (BlockPos pos : blocks) {

            BlockState bs = mb.getBlockState(pos);
            final BlockState renderBlockState = Patchouli.getRenderBlockStateFor(mb, bs);
            final IModelData renderModelData = Patchouli.getModelDataFor(mb, bs);

            ms.pushPose();
            ms.translate(pos.getX(), pos.getY(), pos.getZ());
            for (RenderType layer : RenderType.chunkBufferLayers()) {
                if (ItemBlockRenderTypes.canRenderInLayer(renderBlockState, layer)) {
                    ForgeHooksClient.setRenderLayer(layer);
                    VertexConsumer buffer = buffers.getBuffer(layer);
                    Minecraft.getInstance().getBlockRenderer().renderModel(renderBlockState, pos, mb, ms, buffer, false, RAND, renderModelData);
                    ForgeHooksClient.setRenderLayer(null);
                }
            }
            ms.popPose();
        }
    }

    //endregion
    //endregion
}

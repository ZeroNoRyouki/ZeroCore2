///*
// *
// * AbstractStandardPageComponent.java
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.zerocore.lib.compat.patchouli.component.standardpage;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import it.zerono.mods.zerocore.internal.Log;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.neoforged.fml.util.ObfuscationReflectionHelper;
//import vazkii.patchouli.client.book.BookContentsBuilder;
//import vazkii.patchouli.client.book.BookEntry;
//import vazkii.patchouli.client.book.BookPage;
//import vazkii.patchouli.client.book.gui.GuiBookEntry;
//import vazkii.patchouli.client.book.template.TemplateComponent;
//
//import org.jetbrains.annotations.Nullable;
//import java.lang.reflect.Field;
//
//public abstract class AbstractStandardPageComponent<PageType extends BookPage>
//        extends TemplateComponent {
//
//    protected AbstractStandardPageComponent(final PageType page) {
//        this._page = page;
//    }
//
//    protected PageType getPage() {
//        return this._page;
//    }
//
//    //region TemplateComponent
//
//    @Override
//    public void build(final BookContentsBuilder builder, final BookPage page, final BookEntry entry, final int pageNum) {
//        this.getPage().build(Minecraft.getInstance().level, entry, builder, pageNum);
//    }
//
//    @Override
//    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
//        this.getPage().onDisplayed(parent, left, top);
//    }
//
////    @Override
////    public boolean mouseScrolled(BookPage page, double mouseX, double mouseY, int mouseButton) {
////        return this.getPage().mouseScrolled(mouseX, mouseY, mouseButton);
////    }
//
//    @Override
//    public void render(final GuiGraphics graphics, final BookPage page, final int mouseX, final int mouseY, final float partialTicks) {
//
//        if (this.x > 0 || this.y > 0) {
//
//            final PoseStack stack = graphics.pose();
//
//            stack.pushPose();
//            stack.translate(this.x, this.y, 0);
//            this.renderPage(graphics, mouseX, mouseY, partialTicks);
//            stack.popPose();
//
//        } else {
//
//            this.renderPage(graphics, mouseX, mouseY, partialTicks);
//        }
//    }
//
//    //endregion
//    //region internals
//
//    protected void renderPage(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
//        this.getPage().render(graphics, mouseX, mouseY, partialTicks);
//    }
//
//    @Nullable
//    protected static <T> Field getField(final Class<? super T> clazz, final String name) {
//
//        try {
//
//            return ObfuscationReflectionHelper.findField(clazz, name);
//
//        } catch (ObfuscationReflectionHelper.UnableToFindFieldException ex) {
//
//            Log.LOGGER.error(Log.CORE, "patchouli AbstractStandardPageComponent - Unable to get field {} : {}", name, ex);
//            return null;
//        }
//    }
//
//    private final PageType _page;
//
//    //endregion
//}

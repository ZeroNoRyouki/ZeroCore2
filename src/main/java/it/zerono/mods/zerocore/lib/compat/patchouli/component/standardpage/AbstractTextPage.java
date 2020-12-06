/*
 *
 * AbstractTextPage.java
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

import it.zerono.mods.zerocore.internal.Log;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

import java.lang.reflect.Field;
import java.util.function.UnaryOperator;

public abstract class AbstractTextPage<PageType extends BookPage>
        extends AbstractStandardPageComponent<PageType> {

    IVariable text;

    protected AbstractTextPage(final PageType page) {
        super(page);
    }

    //region AbstractStandardPageComponent

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {

        super.onVariablesAvailable(lookup);

        try {
            textField.set(this.getPage(), lookup.apply(text));
        } catch (IllegalAccessException e) {
            Log.LOGGER.warn(Log.CORE, "patchouli AbstractTextPage wrapper : Unable to set inner page fields");
        }
    }

    //endregion
    //region internals

    private static final Field textField;

    static {
        textField = getField(PageWithText.class, "text");
    }

    //endregion
}

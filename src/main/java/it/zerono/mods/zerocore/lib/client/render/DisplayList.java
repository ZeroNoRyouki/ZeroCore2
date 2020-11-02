/*
 *
 * DisplayList.java
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

package it.zerono.mods.zerocore.lib.client.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("WeakerAccess")
public final class DisplayList {

    public DisplayList() {
        //TODO replace?
        this._id = 0;//GlStateManager.genLists(1);
    }

    @Override
    public void finalize() {
        //TODO replace?
        //GlStateManager.deleteLists(this._id, 1);
    }

    public void beginList() {
        //TODO replace?
        //GlStateManager.newList(this._id, GL11.GL_COMPILE);
    }

    public void endList() {
        //TODO replace?
        //GlStateManager.endList();
    }

    public void play() {
        //TODO replace?
        //GlStateManager.callList(this._id);
    }

    //region internals

    private final int _id;
}

/*
 *
 * TagSource.java
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

package it.zerono.mods.zerocore.lib.tag;

import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public class TagSource<T> {

    public TagSource(final NonNullSupplier<ITagCollection<T>> provider) {
        this._provider = provider;
    }

    public ITagCollection<T> getCollection() {
        return this._provider.get();
    }

    public Optional<ITag<T>> getTag(final ResourceLocation tagId) {
        return Optional.ofNullable(this.getCollection().get(tagId));
    }

    //region internals

    private final NonNullSupplier<ITagCollection<T>> _provider;

    //endregion
}

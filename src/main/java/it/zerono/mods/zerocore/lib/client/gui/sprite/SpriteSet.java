/*
 *
 * SpriteSet.java
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

package it.zerono.mods.zerocore.lib.client.gui.sprite;

import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;

import java.util.Optional;

public class SpriteSet<SpriteId extends Enum<SpriteId>> {

    public SpriteSet(final SpriteId[] validIds) {
        this(Sprite.EMPTY, validIds);
    }

    public SpriteSet(final ISprite defaultSprite, final SpriteId[] validIds) {

        this._sprites = new EnumIndexedArray<>(ISprite[]::new, validIds);
        this._default = defaultSprite;
    }

    @SafeVarargs
    public SpriteSet(final SpriteId firstValidSpriteId, final SpriteId secondValidSpriteId, final SpriteId... otherValidIndices) {
        this(Sprite.EMPTY, firstValidSpriteId, secondValidSpriteId, otherValidIndices);
    }

    @SafeVarargs
    public SpriteSet(final ISprite defaultSprite, final SpriteId firstValidSpriteId, final SpriteId secondValidSpriteId,
                     final SpriteId... otherValidIndices) {

        this._sprites = new EnumIndexedArray<>(ISprite[]::new, firstValidSpriteId, secondValidSpriteId, otherValidIndices);
        this._default = defaultSprite;
    }

    public Optional<ISprite> get(final SpriteId id) {
        return this._sprites.getElement(id);
    }

    public ISprite getOrDefault(final SpriteId id) {
        return this._sprites.getElement(id, this._default);
    }

    public ISprite getOrDefault(final SpriteId id, final ISprite defaultSprite) {
        return this._sprites.getElement(id, defaultSprite);
    }

    public void set(final SpriteId id, final ISprite sprite) {
        this._sprites.setElement(id, sprite);
    }

    public ISprite getDefaultSprite() {
        return this._default;
    }

    //region internals

    private final EnumIndexedArray<SpriteId, ISprite> _sprites;
    private final ISprite _default;

    //endregion
}

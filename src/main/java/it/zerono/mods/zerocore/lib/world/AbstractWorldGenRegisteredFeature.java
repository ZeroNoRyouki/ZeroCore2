/*
 *
 * WorldReGenHandler.java
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

package it.zerono.mods.zerocore.lib.world;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractWorldGenRegisteredFeature<T extends AbstractWorldGenRegisteredFeature>
    implements Supplier<PlacedFeature> {

    protected AbstractWorldGenRegisteredFeature(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                final ConfiguredFeature<?, ?> configuredFeature) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
        this._idFactory = Objects.requireNonNull(idFactory);
        this._configured = Objects.requireNonNull(configuredFeature);
    }

    public void register() {

        ResourceLocation key;

        key = this._idFactory.apply(this.name + "_conf");
        if (!BuiltinRegistries.CONFIGURED_FEATURE.containsKey(key)) {
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key, this._configured);
        }

        key = this._idFactory.apply(this.name + "_placed");
        Registry.register(BuiltinRegistries.PLACED_FEATURE, key, Objects.requireNonNull(this._placed));
    }


    public T placement(final PlacedFeature feature) {

        this._placed = Objects.requireNonNull(feature);
        return this.self();
    }

    public T placement(final PlacementModifier... modifiers) {

        this.placement(this._configured.placed(modifiers));
        return this.self();
    }

    //region Supplier<PlacedFeature>

    @Override
    public PlacedFeature get() {
        return this._placed;
    }

    //endregion
    //region internals

    @SuppressWarnings("unchecked")
    private T self() {
        return (T)this;
    }

    private final String name;
    private final NonNullFunction<String, ResourceLocation> _idFactory;
    private final ConfiguredFeature<?, ?> _configured;
    private PlacedFeature _placed;

    //endregion
}

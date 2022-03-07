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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractWorldGenRegisteredFeature<T extends AbstractWorldGenRegisteredFeature<T, FC, F>,
        FC extends FeatureConfiguration, F extends Feature<FC>>
    implements Supplier<Holder<PlacedFeature>> {

    protected AbstractWorldGenRegisteredFeature(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                final Supplier<F> featureSupplier, final Supplier<FC> configurationSupplier) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
        this._idFactory = Objects.requireNonNull(idFactory);
        this._featureSupplier = Objects.requireNonNull(featureSupplier);
        this._configurationSupplier = Objects.requireNonNull(configurationSupplier);
        this._placementModifiers = ObjectLists.emptyList();
        this._placed = null;
    }

    public void register() {

        final Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE,
                this._idFactory.apply(this.name + "_conf"), new ConfiguredFeature<>(this._featureSupplier.get(), this._configurationSupplier.get()));

        this._placed = BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, this._idFactory.apply(this.name + "_placed"),
                new PlacedFeature(Holder.hackyErase(configuredFeatureHolder), this._placementModifiers));
    }

    public T placement(final PlacementModifier... modifiers) {

        this._placementModifiers = ObjectLists.unmodifiable(new ObjectArrayList<>(modifiers));
        return this.self();
    }

    //region Supplier<Holder<PlacedFeature>>

    @Override
    public Holder<PlacedFeature> get() {
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
    private final Supplier<F> _featureSupplier;
    private final Supplier<FC> _configurationSupplier;
    private List<PlacementModifier> _placementModifiers;
    private Holder<PlacedFeature> _placed;

    //endregion
}

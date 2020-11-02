/*
 *
 * Mods.java
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

package it.zerono.mods.zerocore.lib.compat;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * An {@link Optional} like class to interact with other, known, mods
 */
public enum Mods {

    MINECRAFT("minecraft"),
    ZEROCORE("zerocore"),
    EXTREMEREACTORS("bigreactors"),
    COMPUTERCRAFT("computercraft"),
    OPENCOMPUTERS("opencomputers"),
    MEKANISM("mekanism"),
    COFHCORE("cofhcore"),
    COFHWORLD("cofhworld"),
    ENDERIO("enderio"),
    FORESTRY("forestry"),
    JEI("jei"),
    REDSTONEFLUX("redstoneflux"),
    THERMALEXPANSION("thermalexpansion"),
    THERMALDYNAMICS("thermaldynamics"),
    THERMALFOUNDATION("thermalfoundation"),
    MINEFACTORYRELOADED("minefactoryreloaded"),
    APPLIEDENERGISTICS2("appliedenergistics2"),
    ;

    public static Optional<Mods> from(String id) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));

        for (Mods mod : Mods.values()) {
            if (mod.match(id)) {
                return Optional.of(mod);
            }
        }

        return Optional.empty();
    }

    public String id() {
        return this._id;
    }

    public boolean match(String id) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        return this.id().equals(id);
    }

    public boolean isPresent() {
        return this._present;
    }

    public void ifPresent(Runnable present) {

        if (this.isPresent()) {
            present.run();
        }
    }

    public void ifPresent(NonNullSupplier<Runnable> present) {

        if (this.isPresent()) {
            present.get().run();
        }
    }

    public void ifPresentOrElse(Runnable present, Runnable notPresent) {

        if (this.isPresent()) {
            present.run();
        } else {
            notPresent.run();
        }
    }

    public void ifPresentOrElse(NonNullSupplier<Runnable> present, NonNullSupplier<Runnable> notPresent) {

        if (this.isPresent()) {
            present.get().run();
        } else {
            notPresent.get().run();
        }
    }

    public <T> Optional<T> map(Supplier<T> supplier) {
        return this.isPresent() ? Optional.of(supplier.get()) : Optional.empty();
    }

    public <T> Optional<T> flatMap(Supplier<Optional<T>> supplier) {
        return this.isPresent() ? supplier.get() : Optional.empty();
    }

    public boolean owns(IForgeRegistryEntry<?> entry) {
        return this.isPresent() && null != entry.getRegistryName() && this.match(entry.getRegistryName().getNamespace());
    }

    public void imc(Mods sender, String method, Supplier<?> thing) {

        if (this.isPresent()) {
            InterModComms.sendTo(sender.id(), this.id(), method, thing);
        }
    }

    //region internals

    Mods(String id) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        this._id = id;
        this._present = ModList.get().isLoaded(id);
    }

    private final String _id;
    private final boolean _present;

    //endregion
}

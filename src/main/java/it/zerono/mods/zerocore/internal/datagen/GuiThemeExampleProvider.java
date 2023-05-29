package it.zerono.mods.zerocore.internal.datagen;

/*
 * GuiThemeExampleProvider
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
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerocore.lib.client.gui.Theme;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.AbstractDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class GuiThemeExampleProvider
        extends AbstractDataProvider {

    protected GuiThemeExampleProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                      ResourceLocationBuilder modLocationRoot) {
        super("GUI Theme example file", output, registryLookup, modLocationRoot);
    }

    //region AbstractDataProvider

    @Override
    public void provideData() {
    }

    @Override
    public CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup) {
        return DataProvider.saveStable(cache, Theme.DEFAULT.toJson(),
                this.output().getOutputFolder().resolve("gui_theme_example.json"));
    }

    //endregion
}

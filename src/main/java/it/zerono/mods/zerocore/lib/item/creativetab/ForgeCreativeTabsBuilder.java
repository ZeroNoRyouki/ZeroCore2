package it.zerono.mods.zerocore.lib.item.creativetab;

/*
 * ForgeCreativeTabsBuilder
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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ForgeCreativeTabsBuilder
        extends AbstractCreativeTabsBuilder {

    //region ICreativeTabsBuilder

    @Override
    public Map<ResourceLocation, CompletableFuture<CreativeModeTab>> build() {

        final var bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        if (this.hasAddedGroups()) {
            bus.addListener(this::addGroups);
        }

        if (this.hasModifiedGroups()) {
            bus.addListener(this::modifyGroups);
        }

        return super.build();
    }

    //endregion
    //region internals

    private void addGroups(CreativeModeTabEvent.Register event) {

        this.added().forEach(added -> added.group().complete(event.registerCreativeModeTab(added.id(), builder -> {

            added.tabBuilder().accept(builder);
            builder.displayItems((parameters, output) ->
                    added.contentBuilder().build(null, parameters.enabledFeatures(), parameters.hasPermissions(), output::accept));
        })));
    }

    private void modifyGroups(CreativeModeTabEvent.BuildContents event) {

        this.modified().forEach(modified -> {

            if (null != modified.tab()) {

                if (modified.tab() == event.getTab()) {
                    modified.contentBuilder().build(modified.tab(), event.getFlags(), event.hasPermissions(), event::accept);
                }

            } else {

                modified.contentBuilder().build(event.getTab(), event.getFlags(), event.hasPermissions(), event::accept);
            }
        });
    }

    //endregion
}

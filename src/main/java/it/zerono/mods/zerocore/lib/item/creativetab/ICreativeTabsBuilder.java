package it.zerono.mods.zerocore.lib.item.creativetab;

/*
 * ICreativeTabsBuilder
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
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ICreativeTabsBuilder {

    static ICreativeTabsBuilder create() {
        return new ForgeCreativeTabsBuilder();
    }

    /**
     * Add a new {@link CreativeModeTab}.
     *
     * @param id
     * @param tabBuilder
     * @param contentBuilder
     * @return This builder.
     */
    ICreativeTabsBuilder add(ResourceLocation id, NonNullConsumer<CreativeModeTab.Builder> tabBuilder,
                             CreativeTabContentBuilder contentBuilder);

    /**
     * Modify the content of an existing {@link CreativeModeTab}.
     *
     * @param tab
     * @param contentBuilder
     * @return This builder.
     */
    ICreativeTabsBuilder modify(CreativeModeTab tab, CreativeTabContentBuilder contentBuilder);

    /**
     * Modify the content of any existing {@link CreativeModeTab}.
     *
     * @param contentBuilder
     * @return This builder.
     */
    ICreativeTabsBuilder modifyAny(CreativeTabContentBuilder contentBuilder);

    /**
     *
     * @return
     */
    Map<ResourceLocation, CompletableFuture<CreativeModeTab>> build();
}

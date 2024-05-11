/*
 *
 * ZeroCore.java
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

package it.zerono.mods.zerocore;

import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.internal.proxy.IForgeProxy;
import it.zerono.mods.zerocore.internal.proxy.IProxy;
import it.zerono.mods.zerocore.internal.proxy.ServerProxy;
import it.zerono.mods.zerocore.lib.compat.SidedDependencyServiceLoader;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ZeroCore.MOD_ID)
public final class ZeroCore {

    public static final String MOD_ID = "zerocore";
    public static ResourceLocationBuilder ROOT_LOCATION = ResourceLocationBuilder.of(MOD_ID);

    public static ZeroCore getInstance() {
        return s_instance;
    }

    public ZeroCore(IEventBus modEventBus, ModContainer container, Dist distribution) {

        s_instance = this;

        s_proxy = new SidedDependencyServiceLoader<>(IProxy.class, ServerProxy::new);
        if (s_proxy.get() instanceof IForgeProxy forgeProxy) {
            forgeProxy.initialize(modEventBus);
        }

        Lib.initialize(modEventBus);
        Content.initialize(modEventBus);
    }

    public static IProxy getProxy() {
        return s_proxy.get();
    }

    //region internals

    private static ZeroCore s_instance;
    private static SidedDependencyServiceLoader<IProxy> s_proxy;

    //endregion
}

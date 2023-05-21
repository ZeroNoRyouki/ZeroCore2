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
import it.zerono.mods.zerocore.internal.command.ZeroCoreCommand;
import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.internal.proxy.ClientProxy;
import it.zerono.mods.zerocore.internal.proxy.IProxy;
import it.zerono.mods.zerocore.internal.proxy.ServerProxy;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.init.IModInitializationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = ZeroCore.MOD_ID)
public final class ZeroCore implements IModInitializationHandler {

    public static final String MOD_ID = "zerocore";
    public static ResourceLocationBuilder ROOT_LOCATION = ResourceLocationBuilder.of(MOD_ID);

    public static ZeroCore getInstance() {
        return s_instance;
    }

    public static IProxy getProxy() {
        return s_proxy;
    }

    public ZeroCore() {

        s_instance = this;
        s_proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonInit);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        Lib.initialize();
        Content.initialize();
    }

    //region IModInitializationHandler

    /**
     * Called on both the physical client and the physical server to perform common initialization tasks
     *
     * @param event the event
     */
    @Override
    public void onCommonInit(FMLCommonSetupEvent event) {
        Network.initialize();
    }

    //endregion
    //region internals

    private void onRegisterCommands(final RegisterCommandsEvent event) {
        ZeroCoreCommand.register(event.getDispatcher());
    }

    private static ZeroCore s_instance;
    private static IProxy s_proxy;

    //endregion
}

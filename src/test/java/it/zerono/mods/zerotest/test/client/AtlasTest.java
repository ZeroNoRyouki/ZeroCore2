package it.zerono.mods.zerotest.test.client;

/*
 * AtlasTest
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerotest.Log;
import it.zerono.mods.zerotest.ZeroTest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ZeroTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AtlasTest {

    public static final ResourceLocation TEST_BLOCK_ATLAS = ZeroTest.ROOT_LOCATION
            .appendPath("texture", "atlas")
            .buildWithSuffix("test_blocks.png");

    @SubscribeEvent
    public static void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        getAtlases().forEach(event::registerReloadListener);
    }

    public static void run() {

        Log.LOGGER.info(Log.TEST, "Running AtlasTest...");

        // vanillas... (from ModelManager.VANILLA_ATLASES)
        ModRenderHelper.dumpAtlas(Sheets.BANNER_SHEET);
        ModRenderHelper.dumpAtlas(Sheets.BED_SHEET);
        ModRenderHelper.dumpAtlas(Sheets.CHEST_SHEET);
        ModRenderHelper.dumpAtlas(Sheets.SHIELD_SHEET);
        ModRenderHelper.dumpAtlas(Sheets.SIGN_SHEET);
        ModRenderHelper.dumpAtlas(Sheets.SHULKER_SHEET);
        ModRenderHelper.dumpAtlas(InventoryMenu.BLOCK_ATLAS);

        // test ones...
        getAtlases().forEach(AtlasHolder::dump);

        Log.LOGGER.info(Log.TEST, "AtlasTest completed.");
    }

    //region internals

    private static List<AtlasHolder> getAtlases() {

        if (s_atlases.isEmpty()) {

            // add the test atlases...
            s_atlases.add(AtlasHolder.of("test_blocks"));
            s_atlases.add(AtlasHolder.of("test_banner_patterns"));
            s_atlases.add(AtlasHolder.of("test_beds"));
            s_atlases.add(AtlasHolder.of("test_chests"));
            s_atlases.add(AtlasHolder.of("test_mob_effects"));
            s_atlases.add(AtlasHolder.of("test_paintings"));
            s_atlases.add(AtlasHolder.of("test_particles"));
            s_atlases.add(AtlasHolder.of("test_shield_patterns"));
            s_atlases.add(AtlasHolder.of("test_shulker_boxes"));
            s_atlases.add(AtlasHolder.of("test_signs"));

            s_atlases.add(AtlasHolder.of("test_atlas"));
        }

        return Collections.unmodifiableList(s_atlases);
    }

    private static class AtlasHolder
            extends TextureAtlasHolder {

        public static AtlasHolder of(String name) {

            Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");

            final var builder = ZeroTest.ROOT_LOCATION;

            return new AtlasHolder(builder.appendPath("texture", "atlas").buildWithSuffix(name + ".png"),
                    builder.buildWithSuffix(name));
        }

        public AtlasHolder(ResourceLocation location, ResourceLocation infoLocation) {
            super(Minecraft.getInstance().getTextureManager(), location, infoLocation);
        }

        public void dump() {
            ModRenderHelper.dumpAtlas(this.textureAtlas);
        }
    }

    private static final List<AtlasHolder> s_atlases = new LinkedList<>();

    //endregion
}

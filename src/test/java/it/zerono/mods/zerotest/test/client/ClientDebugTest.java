package it.zerono.mods.zerotest.test.client;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerotest.test.DebugTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientDebugTest
        extends DebugTest {

    @Override
    public void runTest(int test, @Nullable Player player, Level level, BlockPos clickedPos) {

        switch (test) {

            case 33 -> CodeHelper.callOnLogicalClient(level, AtlasTest::run);

            default ->  super.runTest(test, player, level, clickedPos);
        }
    }
}

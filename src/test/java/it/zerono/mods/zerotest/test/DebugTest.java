package it.zerono.mods.zerotest.test;

import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DebugTest
        implements DebugToolItem.ITestCallback {

    //region DebugToolItem.ITestCallback

    @Override
    public void runTest(int test, @Nullable Player player, Level world, BlockPos clickedPos) {
    }

    //region
}

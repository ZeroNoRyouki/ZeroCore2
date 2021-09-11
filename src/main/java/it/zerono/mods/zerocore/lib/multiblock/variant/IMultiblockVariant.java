/*
 *
 * IMultiblockVariant.java
 *
 * A multiblock library for making irregularly-shaped multiblock machines
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * Original author: Erogenous Beef
 * https://github.com/erogenousbeef/BeefCore
 *
 * Minecraft 1.9+ port and further development: ZeroNoRyouki
 * https://github.com/ZeroNoRyouki/ZeroCore2
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 "ZeroNoRyouki"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.zerono.mods.zerocore.lib.multiblock.variant;

import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraft.block.Block;

public interface IMultiblockVariant {

    int getId();

    String getName();

    String getTranslationKey();

    Block.Properties getBlockProperties();

    default Block.Properties getDefaultBlockProperties() {
        return Block.Properties.of(Material.METAL, MaterialColor.METAL)
                .sound(SoundType.METAL)
                .strength(5.0F, 6.0F)
                .harvestLevel(ItemTier.IRON.getLevel())
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .lightLevel(blockState -> 0)
                .isValidSpawn((blockState, blockReader, pos, entity) -> false)
                .isRedstoneConductor((blockState, blockReader, pos) -> true)
                .isViewBlocking((blockState, blockReader, pos) -> true);
    }
}

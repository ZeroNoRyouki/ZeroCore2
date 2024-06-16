/*
 *
 * ValidationError.java
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

package it.zerono.mods.zerocore.lib.multiblock.validation;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public class ValidationError implements IDebuggable {

    public static final ValidationError VALIDATION_ERROR_TOO_FEW_PARTS = new ValidationError(null, "zerocore:api.multiblock.validation.too_few_parts");
    public static final ValidationError VALIDATION_ERROR_NOT_CONNECTED = new ValidationError(null, "zerocore:api.multiblock.validation.block_not_connected");

    public ValidationError(@Nullable final BlockPos position, final String messageFormatStringResourceKey,
                           final @Nullable Object... messageParameters) {

        this._resourceKey = messageFormatStringResourceKey;
        this._parameters = null != messageParameters ? messageParameters : CodeHelper.EMPTY_GENERIC_ARRAY;
        this._position = position;
    }

    public Component getChatMessage() {
        return Component.translatable(this._resourceKey, _parameters);
    }

    @Nullable
    public BlockPos getPosition() {
        return this._position;
    }

    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        messages.add(this.getChatMessage());
    }

    //endregion
    //region internals

    protected final String _resourceKey;
    protected final Object[] _parameters;
    protected final BlockPos _position;

    //endregion
}

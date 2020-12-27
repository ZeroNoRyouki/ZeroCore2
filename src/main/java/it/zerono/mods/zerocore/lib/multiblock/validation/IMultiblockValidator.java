/*
 *
 * IMultiblockValidator.java
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

import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface IMultiblockValidator {

    default boolean hasLastError() {
        return this.getLastError().isPresent();
    }

    /**
     * @return the last validation error encountered when trying to assemble the multiblock, or null if there is no error.
     */
    Optional<ValidationError> getLastError();

    /**
     * Set a validation error
     *
     * @param error the error
     */
    void setLastError(ValidationError error);

    /**
     * Set a validation error
     *
     * @param messageFormatStringResourceKey a translation key for a message or a message format string
     * @param messageParameters optional parameters for a message format string
     */
    void setLastError(String messageFormatStringResourceKey, Object... messageParameters);

    /**
     * Set a validation error
     *
     * @param position the in-world position of the error
     * @param messageFormatStringResourceKey a translation key for a message or a message format string
     * @param messageParameters optional parameters for a message format string
     */
    void setLastError(BlockPos position, String messageFormatStringResourceKey, Object... messageParameters);
}

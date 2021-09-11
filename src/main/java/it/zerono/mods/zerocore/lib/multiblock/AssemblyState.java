/*
 *
 * AssemblyState.java
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

package it.zerono.mods.zerocore.lib.multiblock;

/**
 * Machine state:
 *  - Disassembled -> Assembled;
 *  - Assembled -> Disassembled OR Paused;
 *  - Paused -> Assembled
 */
public class AssemblyState {

    public AssemblyState() {
        this.setDisassembled();
    }

    public boolean isDisassembled() {
        return InternalState.Disassembled == this._state;
    }

    public boolean isAssembled() {
        return InternalState.Assembled == this._state;
    }

    public boolean isPaused() {
        return InternalState.Paused == this._state;
    }

    public void setDisassembled() {
        this._state = InternalState.Disassembled;
    }

    public void setAssembled() {
        this._state = InternalState.Assembled;
    }

    public void setPaused() {
        this._state = InternalState.Paused;
    }

    //region Object

    @Override
    public String toString() {
        return this._state.toString();
    }

    //endregion
    //region internals

    private enum InternalState {

        Disassembled,
        Assembled,
        Paused
    }

    private InternalState _state;

    //endregion
}

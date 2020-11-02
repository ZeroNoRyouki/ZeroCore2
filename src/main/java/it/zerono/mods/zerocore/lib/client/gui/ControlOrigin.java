///*
// *
// * ControlOrigin.java
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.zerocore.lib.client.gui;
//
//import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
//
//public class ControlOrigin {
//
//    public static final ControlOrigin ZERO = new ControlOrigin(0, 0);
//
//    public ControlOrigin(final int x, final int y) {
//
//        this._x = Math.max(0, x);
//        this._y = Math.max(0, y);
//    }
//
//    public ControlOrigin(final ControlOrigin parentOrigin, final Rectangle parentBounds) {
//        this(parentOrigin.getX() + parentBounds.getX1(), parentOrigin.getY() + parentBounds.getY1());
//    }
//
//    public int getX() {
//        return this._x;
//    }
//
//    public int getY() {
//        return this._y;
//    }
//
//    //region Object
//
//    @Override
//    public boolean equals(final Object o) {
//        return super.equals(o) && this.getX() == ((ControlOrigin)o).getX() && this.getY() == ((ControlOrigin)o).getY();
//    }
//
//    @Override
//    public String toString() {
//        return String.format("{%d,%d}", this.getX(), this.getY());
//    }
//
//    //endregion
//    //region internals
//
//    private final int _x;
//    private final int _y;
//
//    //endregion
//}

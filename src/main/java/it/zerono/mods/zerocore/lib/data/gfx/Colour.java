/*
 *
 * Colour.java
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

package it.zerono.mods.zerocore.lib.data.gfx;

import net.minecraft.item.DyeColor;

@SuppressWarnings({"WeakerAccess"})
public class Colour {

    public static final Colour WHITE = new Colour(0xff, 0xff, 0xff, 0xff);
    public static final Colour BLACK = new Colour(0x00, 0x00, 0x00, 0xff);

    public final byte R;
    public final byte G;
    public final byte B;
    public final byte A;

    public Colour(final int r, final int g, final int b, final int a) {

        this.R = (byte)r;
        this.G = (byte)g;
        this.B = (byte)b;
        this.A = (byte)a;
    }

    public Colour(final int r, final int g, final int b) {
        this(r, g, b, 255);
    }

    public Colour(final double r, final double g, final double b, final double a) {
        this((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public Colour(final double r, final double g, final double b) {
        this(r, g, b, 255.0);
    }

    public Colour(final Colour other) {
        this(other.R, other.G, other.B, other.A);
    }

    public Colour(final DyeColor color, final int a) {
        this(rgbRed(color.getColorValue()), rgbGreen(color.getColorValue()), rgbBlue(color.getColorValue()), a);
    }

    public Colour(final DyeColor color) {
        this(color, 255);
    }

    public static Colour from(final Colour colour) {
        return new Colour(colour);
    }

    public static Colour from(final DyeColor colour) {
        return new Colour(colour, 255);
    }

    public static Colour from(final DyeColor colour, final int alpha) {
        return new Colour(colour, alpha);
    }

    public static Colour fromRGB(final int packedRGB) {
        return new Colour((packedRGB >> 16) & 0xFF, (packedRGB >> 8) & 0xFF, packedRGB & 0xFF, 0xFF);
    }

    public static Colour fromARGB(final int packedARGB) {
        return new Colour((packedARGB >> 16) & 0xFF, (packedARGB >> 8) & 0xFF, packedARGB & 0xFF, (packedARGB >> 24) & 0xFF);
    }

    public static Colour fromRGBA(final int packedRGBA) {
        return new Colour((packedRGBA >> 24) & 0xFF, (packedRGBA >> 16) & 0xFF, (packedRGBA >> 8) & 0xFF, packedRGBA & 0xFF);
    }

    public int toRGB() {
        return RGB(this.R, this.G, this.B);
    }

    public int toARGB() {
        return ARGB(this.R, this.G, this.B, this.A);
    }

    public int toRGBA() {
        return RGBA(this.R, this.G, this.B, this.A);
    }

    public static float toFloat(final byte component) {
        return (component & 0xFF) / 255.0f;
    }

    public static int RGB(final int r, final int g, final int b) {
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int RGB(final byte r, final byte g, final byte b) {
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int RGB(final double r, final double g, final double b) {
        return RGB((int)(r * 255.0), (int)(g * 255.0), (int)(b * 255.0));
    }

    public static int RGBA(final int r, final int g, final int b, final int a) {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public static int RGBA(final byte r, final byte g, final byte b, final byte a) {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public static int RGBA(final double r, final double g, final double b, final double a) {
        return RGBA((int)(r * 255.0), (int)(g * 255.0), (int)(b * 255.0), (int)(a * 255.0));
    }

    public static int ARGB(final int r, final int g, final int b, final int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int ARGB(final byte r, final byte g, final byte b, final byte a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int rgbRed(int rgb) {
        return (rgb & 0xFF0000) >> 16;
    }

    public static int rgbGreen(int rgb) {
        return (rgb & '\uff00') >> 8;
    }

    public static int rgbBlue(int rgb) {
        return rgb & 255;
    }

    public static int ARGB(double r, double g, double b, double a) {
        return ARGB((int)(a * 255.0), (int)(r * 255.0), (int)(g * 255.0), (int)(b * 255.0));
    }

    public float glRed() {
        return Colour.toFloat(this.R);
    }

    public float glGreen() {
        return Colour.toFloat(this.G);
    }

    public float glBlue() {
        return Colour.toFloat(this.B);
    }

    public float glAlpha() {
        return Colour.toFloat(this.A);
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof Colour) {

            Colour c = (Colour)other;

            return this.R == c.R && this.G == c.G && this.B == c.B && this.A == c.A;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("Colour R 0x%02x, G 0x%02x, B 0x%02x, A 0x%02x", this.R, this.G, this.B, this.A);
    }

    private Colour() {

        this.R = this.G = this.B = 0;
        this.A = (byte)255;
    }
}

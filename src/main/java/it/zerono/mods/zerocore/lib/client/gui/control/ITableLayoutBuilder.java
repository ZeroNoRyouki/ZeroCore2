/*
 *
 * ITableLayoutBuilder.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

public interface ITableLayoutBuilder {

    ITableLayoutBuilder columns(int count, int... sizesInPixels);

    ITableLayoutBuilder columns(int count, double... sizesInPercentage);

    ITableLayoutBuilder columns(int count);

    ITableLayoutBuilder rows(int count, int... sizesInPixels);

    ITableLayoutBuilder rows(int count);

    ITableLayoutBuilder rows(int count, double... sizesInPercentage);
}

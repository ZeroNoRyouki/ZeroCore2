/*
 *
 * AllowedHandlerAction.java
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

package it.zerono.mods.zerocore.lib.data.stack;

/**
 * The allowed operation(s) in a Item/Fluid/whatever handler
 */
public enum AllowedHandlerAction {

    /**
     * Allow only insertion into the handler
     */
    InsertOnly,

    /**
     * Allow only extractions from the handler
     */
    ExtractOnly,

    /**
     * Allow both insertion and extraction from the handler
     */
    InsertExtract,

    /**
     * Allow no insertion or extraction from the handler
     */
    None;

    public boolean canInsert() {
        return this == InsertOnly || this == InsertExtract;
    }

    public boolean canExtract() {
        return this == ExtractOnly || this == InsertExtract;
    }
}

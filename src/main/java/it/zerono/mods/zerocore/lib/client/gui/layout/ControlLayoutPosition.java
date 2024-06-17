package it.zerono.mods.zerocore.lib.client.gui.layout;

import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

public record ControlLayoutPosition(IControl control, int x, int y, int with, int height) {

    public void layout() {
        this.control.setBounds(new Rectangle(this.x, this.y, this.with, this.height));
    }

    public void layoutAtX(int x) {
        this.control.setBounds(new Rectangle(x, this.y, this.with, this.height));
    }

    public void layoutAtY(int y) {
        this.control.setBounds(new Rectangle(this.x, y, this.with, this.height));
    }
}

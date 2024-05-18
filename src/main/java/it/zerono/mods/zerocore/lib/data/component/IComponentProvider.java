package it.zerono.mods.zerocore.lib.data.component;

public interface IComponentProvider<Component> {

    Component createDataComponent();

    void mergeComponent(Component component);
}

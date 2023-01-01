**ZeroCore themes for GUI**

The GUI framework now supports themes that specify the colours of various GUI elements.

A mod can load a theme from a JSON file like the provided example_gui_theme.json. The theme file can also be overrided by texture / mod packs authors.

**_Theme elements_**

All values are required and they express RGBA colours in a hexadecimal string (ie: "868686FF")

* FLAT_BACKGROUND_COLOR<br>
Background of a control.<br><br>

* TEXT_ENABLED_COLOR<br>
Text displayed in an enabled control.<br><br>

* TEXT_DISABLED_COLOR<br>
Text displayed in a disabled control.<br><br>

* DARK_OUTLINE_COLOR<br>
Dark border / outline of a control.<br><br>

* BUTTON_NORMAL_3D_GRADIENT_LIGHT<br>
Background of an enabled button, starting colour of a gradient.<br> 
If equal to BUTTON_NORMAL_3D_GRADIENT_DARK, only BUTTON_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>
 
* BUTTON_NORMAL_3D_GRADIENT_DARK<br>
Background of an enabled button, ending colour of a gradient.<br>
If equal to BUTTON_NORMAL_3D_GRADIENT_DARK, only BUTTON_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_NORMAL_3D_BORDER_LIGHT<br>
3D border of an enabled button, light sections.<br><br>

* BUTTON_NORMAL_3D_BORDER_DARK<br>
3D border of an enabled button, dark sections.<br><br>

* BUTTON_DISABLED_3D_GRADIENT_LIGHT<br>
Background of a disabled button, starting colour of a gradient.<br>
If equal to BUTTON_DISABLED_3D_GRADIENT_DARK, only BUTTON_DISABLED_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_DISABLED_3D_GRADIENT_DARK<br>
Background of a disabled button, ending colour of a gradient.<br>
If equal to BUTTON_DISABLED_3D_GRADIENT_DARK, only BUTTON_DISABLED_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_DISABLED_3D_BORDER_LIGHT<br>
3D border of a disabled button, light sections.<br><br>

* BUTTON_DISABLED_3D_BORDER_DARK<br>
3D border of a disabled button, dark sections.<br><br>

* BUTTON_ACTIVE_3D_GRADIENT_LIGHT<br>
Background of an active button, starting colour of a gradient.<br>
If equal to BUTTON_ACTIVE_3D_GRADIENT_DARK, only BUTTON_ACTIVE_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_ACTIVE_3D_GRADIENT_DARK<br>
Background of an active button, ending colour of a gradient.<br>
If equal to BUTTON_ACTIVE_3D_GRADIENT_DARK, only BUTTON_ACTIVE_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_ACTIVE_3D_BORDER_LIGHT<br>
3D border of an active button, light sections.<br><br>

* BUTTON_ACTIVE_3D_BORDER_DARK<br>
3D border of an active button, dark sections.<br><br>

* BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT<br>
Background of a highlighted button, starting colour of a gradient.<br>
If equal to BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK, only BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK<br>
Background of a highlighted button, ending colour of a gradient.<br>
If equal to BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK, only BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT<br>
3D border of a highlighted button, light sections.<br><br>

* BUTTON_HIGHLIGHTED_3D_BORDER_DARK<br>
3D border of a highlighted button, dark sections.<br><br>

* TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT<br>
Background of a text field, starting colour of a gradient.<br>
If equal to TEXTFIELD_NORMAL_3D_GRADIENT_DARK, only TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* TEXTFIELD_NORMAL_3D_GRADIENT_DARK<br>
Background of a text field, ending colour of a gradient.<br>
If equal to TEXTFIELD_NORMAL_3D_GRADIENT_DARK, only TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT will be used to draw a flat background.<br><br>

* TEXTFIELD_NORMAL_3D_BORDER_LIGHT<br>
3D border of a text field, light sections.<br><br>

* TEXTFIELD_NORMAL_3D_BORDER_DARK<br>
3D border of a text field, dark sections.<br><br>

* TEXTFIELD_CARET<br>
Colour of the caret in a text field.<br><br>

* GUI_TITLE<br>
Colour of the title of a GUI.<br><br>

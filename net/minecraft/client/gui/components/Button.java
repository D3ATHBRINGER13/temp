package net.minecraft.client.gui.components;

public class Button extends AbstractButton {
    protected final OnPress onPress;
    
    public Button(final int integer1, final int integer2, final int integer3, final int integer4, final String string, final OnPress a) {
        super(integer1, integer2, integer3, integer4, string);
        this.onPress = a;
    }
    
    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }
    
    public interface OnPress {
        void onPress(final Button czi);
    }
}

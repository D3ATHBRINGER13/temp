package net.minecraft.client.gui.screens;

import net.minecraft.network.chat.Component;

public class GenericDirtMessageScreen extends Screen {
    public GenericDirtMessageScreen(final Component jo) {
        super(jo);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
        super.render(integer1, integer2, float3);
    }
}

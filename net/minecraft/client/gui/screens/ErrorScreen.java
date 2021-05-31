package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
    private final String message;
    
    public ErrorScreen(final Component jo, final String string) {
        super(jo);
        this.message = string;
    }
    
    @Override
    protected void init() {
        super.init();
        this.<Button>addButton(new Button(this.width / 2 - 100, 140, 200, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(null)));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

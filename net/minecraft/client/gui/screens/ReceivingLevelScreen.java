package net.minecraft.client.gui.screens;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.chat.NarratorChatListener;

public class ReceivingLevelScreen extends Screen {
    public ReceivingLevelScreen() {
        super(NarratorChatListener.NO_TITLE);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, I18n.get("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

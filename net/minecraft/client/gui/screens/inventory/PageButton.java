package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.Button;

public class PageButton extends Button {
    private final boolean isForward;
    private final boolean playTurnSound;
    
    public PageButton(final int integer1, final int integer2, final boolean boolean3, final OnPress a, final boolean boolean5) {
        super(integer1, integer2, 23, 13, "", a);
        this.isForward = boolean3;
        this.playTurnSound = boolean5;
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getInstance().getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
        int integer3 = 0;
        int integer4 = 192;
        if (this.isHovered()) {
            integer3 += 23;
        }
        if (!this.isForward) {
            integer4 += 13;
        }
        this.blit(this.x, this.y, integer3, integer4, 23, 13);
    }
    
    @Override
    public void playDownSound(final SoundManager eap) {
        if (this.playTurnSound) {
            eap.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f));
        }
    }
}

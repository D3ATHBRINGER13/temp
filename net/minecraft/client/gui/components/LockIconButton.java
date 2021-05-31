package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class LockIconButton extends Button {
    private boolean locked;
    
    public LockIconButton(final int integer1, final int integer2, final OnPress a) {
        super(integer1, integer2, 20, 20, I18n.get("narrator.button.difficulty_lock"), a);
    }
    
    @Override
    protected String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + (this.isLocked() ? I18n.get("narrator.button.difficulty_lock.locked") : I18n.get("narrator.button.difficulty_lock.unlocked"));
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean boolean1) {
        this.locked = boolean1;
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        Minecraft.getInstance().getTextureManager().bind(Button.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Icon a5;
        if (!this.active) {
            a5 = (this.locked ? Icon.LOCKED_DISABLED : Icon.UNLOCKED_DISABLED);
        }
        else if (this.isHovered()) {
            a5 = (this.locked ? Icon.LOCKED_HOVER : Icon.UNLOCKED_HOVER);
        }
        else {
            a5 = (this.locked ? Icon.LOCKED : Icon.UNLOCKED);
        }
        this.blit(this.x, this.y, a5.getX(), a5.getY(), this.width, this.height);
    }
    
    enum Icon {
        LOCKED(0, 146), 
        LOCKED_HOVER(0, 166), 
        LOCKED_DISABLED(0, 186), 
        UNLOCKED(20, 146), 
        UNLOCKED_HOVER(20, 166), 
        UNLOCKED_DISABLED(20, 186);
        
        private final int x;
        private final int y;
        
        private Icon(final int integer3, final int integer4) {
            this.x = integer3;
            this.y = integer4;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
    }
}

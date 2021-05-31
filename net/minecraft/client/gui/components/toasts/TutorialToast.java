package net.minecraft.client.gui.components.toasts;

import net.minecraft.util.Mth;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class TutorialToast implements Toast {
    private final Icons icon;
    private final String title;
    private final String message;
    private Visibility visibility;
    private long lastProgressTime;
    private float lastProgress;
    private float progress;
    private final boolean progressable;
    
    public TutorialToast(final Icons a, final Component jo2, @Nullable final Component jo3, final boolean boolean4) {
        this.visibility = Visibility.SHOW;
        this.icon = a;
        this.title = jo2.getColoredString();
        this.message = ((jo3 == null) ? null : jo3.getColoredString());
        this.progressable = boolean4;
    }
    
    public Visibility render(final ToastComponent dan, final long long2) {
        dan.getMinecraft().getTextureManager().bind(TutorialToast.TEXTURE);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        dan.blit(0, 0, 0, 96, 160, 32);
        this.icon.render(dan, 6, 6);
        if (this.message == null) {
            dan.getMinecraft().font.draw(this.title, 30.0f, 12.0f, -11534256);
        }
        else {
            dan.getMinecraft().font.draw(this.title, 30.0f, 7.0f, -11534256);
            dan.getMinecraft().font.draw(this.message, 30.0f, 18.0f, -16777216);
        }
        if (this.progressable) {
            GuiComponent.fill(3, 28, 157, 29, -1);
            final float float5 = (float)Mth.clampedLerp(this.lastProgress, this.progress, (long2 - this.lastProgressTime) / 100.0f);
            int integer6;
            if (this.progress >= this.lastProgress) {
                integer6 = -16755456;
            }
            else {
                integer6 = -11206656;
            }
            GuiComponent.fill(3, 28, (int)(3.0f + 154.0f * float5), 29, integer6);
            this.lastProgress = float5;
            this.lastProgressTime = long2;
        }
        return this.visibility;
    }
    
    public void hide() {
        this.visibility = Visibility.HIDE;
    }
    
    public void updateProgress(final float float1) {
        this.progress = float1;
    }
    
    public enum Icons {
        MOVEMENT_KEYS(0, 0), 
        MOUSE(1, 0), 
        TREE(2, 0), 
        RECIPE_BOOK(0, 1), 
        WOODEN_PLANKS(1, 1);
        
        private final int x;
        private final int y;
        
        private Icons(final int integer3, final int integer4) {
            this.x = integer3;
            this.y = integer4;
        }
        
        public void render(final GuiComponent cyw, final int integer2, final int integer3) {
            GlStateManager.enableBlend();
            cyw.blit(integer2, integer3, 176 + this.x * 20, this.y * 20, 20, 20);
            GlStateManager.enableBlend();
        }
    }
}

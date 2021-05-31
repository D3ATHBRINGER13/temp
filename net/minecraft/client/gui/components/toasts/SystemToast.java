package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class SystemToast implements Toast {
    private final SystemToastIds id;
    private String title;
    private String message;
    private long lastChanged;
    private boolean changed;
    
    public SystemToast(final SystemToastIds a, final Component jo2, @Nullable final Component jo3) {
        this.id = a;
        this.title = jo2.getString();
        this.message = ((jo3 == null) ? null : jo3.getString());
    }
    
    public Visibility render(final ToastComponent dan, final long long2) {
        if (this.changed) {
            this.lastChanged = long2;
            this.changed = false;
        }
        dan.getMinecraft().getTextureManager().bind(SystemToast.TEXTURE);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        dan.blit(0, 0, 0, 64, 160, 32);
        if (this.message == null) {
            dan.getMinecraft().font.draw(this.title, 18.0f, 12.0f, -256);
        }
        else {
            dan.getMinecraft().font.draw(this.title, 18.0f, 7.0f, -256);
            dan.getMinecraft().font.draw(this.message, 18.0f, 18.0f, -1);
        }
        return (long2 - this.lastChanged < 5000L) ? Visibility.SHOW : Visibility.HIDE;
    }
    
    public void reset(final Component jo1, @Nullable final Component jo2) {
        this.title = jo1.getString();
        this.message = ((jo2 == null) ? null : jo2.getString());
        this.changed = true;
    }
    
    public SystemToastIds getToken() {
        return this.id;
    }
    
    public static void addOrUpdate(final ToastComponent dan, final SystemToastIds a, final Component jo3, @Nullable final Component jo4) {
        final SystemToast dal5 = dan.<SystemToast>getToast((java.lang.Class<? extends SystemToast>)SystemToast.class, a);
        if (dal5 == null) {
            dan.addToast(new SystemToast(a, jo3, jo4));
        }
        else {
            dal5.reset(jo3, jo4);
        }
    }
    
    public enum SystemToastIds {
        TUTORIAL_HINT, 
        NARRATOR_TOGGLE, 
        WORLD_BACKUP;
    }
}

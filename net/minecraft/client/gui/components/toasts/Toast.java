package net.minecraft.client.gui.components.toasts;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public interface Toast {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");
    public static final Object NO_TOKEN = new Object();
    
    Visibility render(final ToastComponent dan, final long long2);
    
    default Object getToken() {
        return Toast.NO_TOKEN;
    }
    
    public enum Visibility {
        SHOW(SoundEvents.UI_TOAST_IN), 
        HIDE(SoundEvents.UI_TOAST_OUT);
        
        private final SoundEvent soundEvent;
        
        private Visibility(final SoundEvent yo) {
            this.soundEvent = yo;
        }
        
        public void playSound(final SoundManager eap) {
            eap.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0f, 1.0f));
        }
    }
}

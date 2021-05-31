package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.client.player.LocalPlayer;

public class UnderwaterAmbientSoundInstances {
    public static class SubSound extends AbstractTickableSoundInstance {
        private final LocalPlayer player;
        
        protected SubSound(final LocalPlayer dmp, final SoundEvent yo) {
            super(yo, SoundSource.AMBIENT);
            this.player = dmp;
            this.looping = false;
            this.delay = 0;
            this.volume = 1.0f;
            this.priority = true;
            this.relative = true;
        }
        
        @Override
        public void tick() {
            if (this.player.removed || !this.player.isUnderWater()) {
                this.stopped = true;
            }
        }
    }
    
    public static class UnderwaterAmbientSoundInstance extends AbstractTickableSoundInstance {
        private final LocalPlayer player;
        private int fade;
        
        public UnderwaterAmbientSoundInstance(final LocalPlayer dmp) {
            super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT);
            this.player = dmp;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0f;
            this.priority = true;
            this.relative = true;
        }
        
        @Override
        public void tick() {
            if (this.player.removed || this.fade < 0) {
                this.stopped = true;
                return;
            }
            if (this.player.isUnderWater()) {
                ++this.fade;
            }
            else {
                this.fade -= 2;
            }
            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0f, Math.min(this.fade / 40.0f, 1.0f));
        }
    }
}

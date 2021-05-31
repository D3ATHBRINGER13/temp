package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.player.LocalPlayer;

public class UnderwaterAmbientSoundHandler implements AmbientSoundHandler {
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private int tick_delay;
    
    public UnderwaterAmbientSoundHandler(final LocalPlayer dmp, final SoundManager eap) {
        this.tick_delay = 0;
        this.player = dmp;
        this.soundManager = eap;
    }
    
    public void tick() {
        --this.tick_delay;
        if (this.tick_delay <= 0 && this.player.isUnderWater()) {
            final float float2 = this.player.level.random.nextFloat();
            if (float2 < 1.0E-4f) {
                this.tick_delay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
            }
            else if (float2 < 0.001f) {
                this.tick_delay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
            }
            else if (float2 < 0.01f) {
                this.tick_delay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
            }
        }
    }
}

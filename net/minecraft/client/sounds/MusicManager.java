package net.minecraft.client.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.Minecraft;
import java.util.Random;

public class MusicManager {
    private final Random random;
    private final Minecraft minecraft;
    private SoundInstance currentMusic;
    private int nextSongDelay;
    
    public MusicManager(final Minecraft cyc) {
        this.random = new Random();
        this.nextSongDelay = 100;
        this.minecraft = cyc;
    }
    
    public void tick() {
        final Music a2 = this.minecraft.getSituationalMusic();
        if (this.currentMusic != null) {
            if (!a2.getEvent().getLocation().equals(this.currentMusic.getLocation())) {
                this.minecraft.getSoundManager().stop(this.currentMusic);
                this.nextSongDelay = Mth.nextInt(this.random, 0, a2.getMinDelay() / 2);
            }
            if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
                this.currentMusic = null;
                this.nextSongDelay = Math.min(Mth.nextInt(this.random, a2.getMinDelay(), a2.getMaxDelay()), this.nextSongDelay);
            }
        }
        this.nextSongDelay = Math.min(this.nextSongDelay, a2.getMaxDelay());
        if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
            this.startPlaying(a2);
        }
    }
    
    public void startPlaying(final Music a) {
        this.currentMusic = SimpleSoundInstance.forMusic(a.getEvent());
        this.minecraft.getSoundManager().play(this.currentMusic);
        this.nextSongDelay = Integer.MAX_VALUE;
    }
    
    public void stopPlaying() {
        if (this.currentMusic != null) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.currentMusic = null;
            this.nextSongDelay = 0;
        }
    }
    
    public boolean isPlayingMusic(final Music a) {
        return this.currentMusic != null && a.getEvent().getLocation().equals(this.currentMusic.getLocation());
    }
    
    public enum Music {
        MENU(SoundEvents.MUSIC_MENU, 20, 600), 
        GAME(SoundEvents.MUSIC_GAME, 12000, 24000), 
        CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600), 
        CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0), 
        NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600), 
        END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0), 
        END(SoundEvents.MUSIC_END, 6000, 24000), 
        UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);
        
        private final SoundEvent event;
        private final int minDelay;
        private final int maxDelay;
        
        private Music(final SoundEvent yo, final int integer4, final int integer5) {
            this.event = yo;
            this.minDelay = integer4;
            this.maxDelay = integer5;
        }
        
        public SoundEvent getEvent() {
            return this.event;
        }
        
        public int getMinDelay() {
            return this.minDelay;
        }
        
        public int getMaxDelay() {
            return this.maxDelay;
        }
    }
}

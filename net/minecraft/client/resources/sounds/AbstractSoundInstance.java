package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import javax.annotation.Nullable;
import net.minecraft.client.sounds.WeighedSoundEvents;

public abstract class AbstractSoundInstance implements SoundInstance {
    protected Sound sound;
    @Nullable
    private WeighedSoundEvents soundEvent;
    protected final SoundSource source;
    protected final ResourceLocation location;
    protected float volume;
    protected float pitch;
    protected float x;
    protected float y;
    protected float z;
    protected boolean looping;
    protected int delay;
    protected Attenuation attenuation;
    protected boolean priority;
    protected boolean relative;
    
    protected AbstractSoundInstance(final SoundEvent yo, final SoundSource yq) {
        this(yo.getLocation(), yq);
    }
    
    protected AbstractSoundInstance(final ResourceLocation qv, final SoundSource yq) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.attenuation = Attenuation.LINEAR;
        this.location = qv;
        this.source = yq;
    }
    
    public ResourceLocation getLocation() {
        return this.location;
    }
    
    public WeighedSoundEvents resolve(final SoundManager eap) {
        this.soundEvent = eap.getSoundEvent(this.location);
        if (this.soundEvent == null) {
            this.sound = SoundManager.EMPTY_SOUND;
        }
        else {
            this.sound = this.soundEvent.getSound();
        }
        return this.soundEvent;
    }
    
    public Sound getSound() {
        return this.sound;
    }
    
    public SoundSource getSource() {
        return this.source;
    }
    
    public boolean isLooping() {
        return this.looping;
    }
    
    public int getDelay() {
        return this.delay;
    }
    
    public float getVolume() {
        return this.volume * this.sound.getVolume();
    }
    
    public float getPitch() {
        return this.pitch * this.sound.getPitch();
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
    
    public Attenuation getAttenuation() {
        return this.attenuation;
    }
    
    public boolean isRelative() {
        return this.relative;
    }
}

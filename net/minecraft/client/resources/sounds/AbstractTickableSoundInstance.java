package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;

public abstract class AbstractTickableSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
    protected boolean stopped;
    
    protected AbstractTickableSoundInstance(final SoundEvent yo, final SoundSource yq) {
        super(yo, yq);
    }
    
    @Override
    public boolean isStopped() {
        return this.stopped;
    }
}

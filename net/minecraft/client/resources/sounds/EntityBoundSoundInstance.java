package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class EntityBoundSoundInstance extends AbstractTickableSoundInstance {
    private final Entity entity;
    
    public EntityBoundSoundInstance(final SoundEvent yo, final SoundSource yq, final Entity aio) {
        this(yo, yq, 1.0f, 1.0f, aio);
    }
    
    public EntityBoundSoundInstance(final SoundEvent yo, final SoundSource yq, final float float3, final float float4, final Entity aio) {
        super(yo, yq);
        this.volume = float3;
        this.pitch = float4;
        this.entity = aio;
        this.x = (float)this.entity.x;
        this.y = (float)this.entity.y;
        this.z = (float)this.entity.z;
    }
    
    @Override
    public void tick() {
        if (this.entity.removed) {
            this.stopped = true;
            return;
        }
        this.x = (float)this.entity.x;
        this.y = (float)this.entity.y;
        this.z = (float)this.entity.z;
    }
}

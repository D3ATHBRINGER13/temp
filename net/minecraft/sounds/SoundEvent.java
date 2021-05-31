package net.minecraft.sounds;

import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
    private final ResourceLocation location;
    
    public SoundEvent(final ResourceLocation qv) {
        this.location = qv;
    }
    
    public ResourceLocation getLocation() {
        return this.location;
    }
}

package net.minecraft.world.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;

public abstract class ShoulderRidingEntity extends TamableAnimal {
    private int rideCooldownCounter;
    
    protected ShoulderRidingEntity(final EntityType<? extends ShoulderRidingEntity> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public boolean setEntityOnShoulder(final ServerPlayer vl) {
        final CompoundTag id3 = new CompoundTag();
        id3.putString("id", this.getEncodeId());
        this.saveWithoutId(id3);
        if (vl.setEntityOnShoulder(id3)) {
            this.remove();
            return true;
        }
        return false;
    }
    
    @Override
    public void tick() {
        ++this.rideCooldownCounter;
        super.tick();
    }
    
    public boolean canSitOnShoulder() {
        return this.rideCooldownCounter > 100;
    }
}

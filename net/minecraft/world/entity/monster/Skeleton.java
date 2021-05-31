package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Skeleton extends AbstractSkeleton {
    public Skeleton(final EntityType<? extends Skeleton> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.SKELETON_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }
    
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        final Entity aio5 = ahx.getEntity();
        if (aio5 instanceof Creeper) {
            final Creeper aue6 = (Creeper)aio5;
            if (aue6.canDropMobsSkull()) {
                aue6.increaseDroppedSkulls();
                this.spawnAtLocation(Items.SKELETON_SKULL);
            }
        }
    }
}

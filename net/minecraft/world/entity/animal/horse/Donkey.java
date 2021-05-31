package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Donkey extends AbstractChestedHorse {
    public Donkey(final EntityType<? extends Donkey> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.DONKEY_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.DONKEY_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        super.getHurtSound(ahx);
        return SoundEvents.DONKEY_HURT;
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        return ara != this && (ara instanceof Donkey || ara instanceof Horse) && this.canParent() && ((AbstractHorse)ara).canParent();
    }
    
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        final EntityType<? extends AbstractHorse> ais3 = (aim instanceof Horse) ? EntityType.MULE : EntityType.DONKEY;
        final AbstractHorse asb4 = (AbstractHorse)ais3.create(this.level);
        this.setOffspringAttributes(aim, asb4);
        return asb4;
    }
}

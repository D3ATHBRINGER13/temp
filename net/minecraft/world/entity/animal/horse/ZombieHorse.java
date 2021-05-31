package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class ZombieHorse extends AbstractHorse {
    public ZombieHorse(final EntityType<? extends ZombieHorse> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
        this.getAttribute(ZombieHorse.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
    }
    
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ZOMBIE_HORSE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ZOMBIE_HORSE_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        super.getHurtSound(ahx);
        return SoundEvents.ZOMBIE_HORSE_HURT;
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return EntityType.ZOMBIE_HORSE.create(this.level);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.isTamed()) {
            return false;
        }
        if (this.isBaby()) {
            return super.mobInteract(awg, ahi);
        }
        if (awg.isSneaking()) {
            this.openInventory(awg);
            return true;
        }
        if (this.isVehicle()) {
            return super.mobInteract(awg, ahi);
        }
        if (!bcj4.isEmpty()) {
            if (!this.isSaddled() && bcj4.getItem() == Items.SADDLE) {
                this.openInventory(awg);
                return true;
            }
            if (bcj4.interactEnemy(awg, this, ahi)) {
                return true;
            }
        }
        this.doPlayerRide(awg);
        return true;
    }
    
    @Override
    protected void addBehaviourGoals() {
    }
}

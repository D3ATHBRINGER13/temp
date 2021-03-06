package net.minecraft.world.entity.animal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

public abstract class WaterAnimal extends PathfinderMob {
    protected WaterAnimal(final EntityType<? extends WaterAnimal> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return bhu.isUnobstructed(this);
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
    
    @Override
    protected int getExperienceReward(final Player awg) {
        return 1 + this.level.random.nextInt(3);
    }
    
    protected void handleAirSupply(final int integer) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(integer - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DROWN, 2.0f);
            }
        }
        else {
            this.setAirSupply(300);
        }
    }
    
    @Override
    public void baseTick() {
        final int integer2 = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(integer2);
    }
    
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    @Override
    public boolean canBeLeashed(final Player awg) {
        return false;
    }
}

package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class CaveSpider extends Spider {
    public CaveSpider(final EntityType<? extends CaveSpider> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        if (super.doHurtTarget(aio)) {
            if (aio instanceof LivingEntity) {
                int integer3 = 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    integer3 = 7;
                }
                else if (this.level.getDifficulty() == Difficulty.HARD) {
                    integer3 = 15;
                }
                if (integer3 > 0) {
                    ((LivingEntity)aio).addEffect(new MobEffectInstance(MobEffects.POISON, integer3 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        return ajj;
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.45f;
    }
}

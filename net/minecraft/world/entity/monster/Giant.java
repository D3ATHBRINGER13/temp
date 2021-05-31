package net.minecraft.world.entity.monster;

import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Giant extends Monster {
    public Giant(final EntityType<? extends Giant> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 10.440001f;
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0);
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        return bhu.getBrightness(ew) - 0.5f;
    }
}

package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import java.util.Random;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Silverfish extends Monster {
    private SilverfishWakeUpFriendsGoal friendsGoal;
    
    public Silverfish(final EntityType<? extends Silverfish> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        this.friendsGoal = new SilverfishWakeUpFriendsGoal(this);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, this.friendsGoal);
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new SilverfishMergeWithStoneGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public double getRidingHeight() {
        return 0.1;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.1f;
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.SILVERFISH_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15f, 1.0f);
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if ((ahx instanceof EntityDamageSource || ahx == DamageSource.MAGIC) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public void tick() {
        this.yBodyRot = this.yRot;
        super.tick();
    }
    
    public void setYBodyRot(final float float1) {
        super.setYBodyRot(this.yRot = float1);
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        if (InfestedBlock.isCompatibleHostBlock(bhu.getBlockState(ew.below()))) {
            return 10.0f;
        }
        return super.getWalkTargetValue(ew, bhu);
    }
    
    public static boolean checkSliverfishSpawnRules(final EntityType<Silverfish> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        if (Monster.checkAnyLightMonsterSpawnRules(ais, bhs, aja, ew, random)) {
            final Player awg6 = bhs.getNearestPlayer(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, 5.0, true);
            return awg6 == null;
        }
        return false;
    }
    
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
    
    static class SilverfishWakeUpFriendsGoal extends Goal {
        private final Silverfish silverfish;
        private int lookForFriends;
        
        public SilverfishWakeUpFriendsGoal(final Silverfish avc) {
            this.silverfish = avc;
        }
        
        public void notifyHurt() {
            if (this.lookForFriends == 0) {
                this.lookForFriends = 20;
            }
        }
        
        @Override
        public boolean canUse() {
            return this.lookForFriends > 0;
        }
        
        @Override
        public void tick() {
            --this.lookForFriends;
            Label_0237: {
                if (this.lookForFriends <= 0) {
                    final Level bhr2 = this.silverfish.level;
                    final Random random3 = this.silverfish.getRandom();
                    final BlockPos ew4 = new BlockPos(this.silverfish);
                    for (int integer5 = 0; integer5 <= 5 && integer5 >= -5; integer5 = ((integer5 <= 0) ? 1 : 0) - integer5) {
                        for (int integer6 = 0; integer6 <= 10 && integer6 >= -10; integer6 = ((integer6 <= 0) ? 1 : 0) - integer6) {
                            for (int integer7 = 0; integer7 <= 10 && integer7 >= -10; integer7 = ((integer7 <= 0) ? 1 : 0) - integer7) {
                                final BlockPos ew5 = ew4.offset(integer6, integer5, integer7);
                                final BlockState bvt9 = bhr2.getBlockState(ew5);
                                final Block bmv10 = bvt9.getBlock();
                                if (bmv10 instanceof InfestedBlock) {
                                    if (bhr2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                        bhr2.destroyBlock(ew5, true);
                                    }
                                    else {
                                        bhr2.setBlock(ew5, ((InfestedBlock)bmv10).getHostBlock().defaultBlockState(), 3);
                                    }
                                    if (random3.nextBoolean()) {
                                        break Label_0237;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static class SilverfishMergeWithStoneGoal extends RandomStrollGoal {
        private Direction selectedDirection;
        private boolean doMerge;
        
        public SilverfishMergeWithStoneGoal(final Silverfish avc) {
            super(avc, 1.0, 10);
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null) {
                return false;
            }
            if (!this.mob.getNavigation().isDone()) {
                return false;
            }
            final Random random2 = this.mob.getRandom();
            if (this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && random2.nextInt(10) == 0) {
                this.selectedDirection = Direction.getRandomFace(random2);
                final BlockPos ew3 = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).relative(this.selectedDirection);
                final BlockState bvt4 = this.mob.level.getBlockState(ew3);
                if (InfestedBlock.isCompatibleHostBlock(bvt4)) {
                    return this.doMerge = true;
                }
            }
            this.doMerge = false;
            return super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.doMerge && super.canContinueToUse();
        }
        
        @Override
        public void start() {
            if (!this.doMerge) {
                super.start();
                return;
            }
            final LevelAccessor bhs2 = this.mob.level;
            final BlockPos ew3 = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).relative(this.selectedDirection);
            final BlockState bvt4 = bhs2.getBlockState(ew3);
            if (InfestedBlock.isCompatibleHostBlock(bvt4)) {
                bhs2.setBlock(ew3, InfestedBlock.stateByHostBlock(bvt4.getBlock()), 3);
                this.mob.spawnAnim();
                this.mob.remove();
            }
        }
    }
}

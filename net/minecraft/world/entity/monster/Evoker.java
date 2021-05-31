package net.minecraft.world.entity.monster;

import net.minecraft.world.item.DyeColor;
import java.util.List;
import net.minecraft.world.level.GameRules;
import java.util.function.Predicate;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;

public class Evoker extends SpellcasterIllager {
    private Sheep wololoTarget;
    
    public Evoker(final EntityType<? extends Evoker> ais, final Level bhr) {
        super(ais, bhr);
        this.xpReward = 10;
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(4, new EvokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new EvokerWololoSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Raider.class }).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
    }
    
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    public boolean isAlliedTo(final Entity aio) {
        if (aio == null) {
            return false;
        }
        if (aio == this) {
            return true;
        }
        if (super.isAlliedTo(aio)) {
            return true;
        }
        if (aio instanceof Vex) {
            return this.isAlliedTo(((Vex)aio).getOwner());
        }
        return aio instanceof LivingEntity && ((LivingEntity)aio).getMobType() == MobType.ILLAGER && this.getTeam() == null && aio.getTeam() == null;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.EVOKER_HURT;
    }
    
    private void setWololoTarget(@Nullable final Sheep ars) {
        this.wololoTarget = ars;
    }
    
    @Nullable
    private Sheep getWololoTarget() {
        return this.wololoTarget;
    }
    
    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }
    
    @Override
    public void applyRaidBuffs(final int integer, final boolean boolean2) {
    }
    
    class EvokerCastingSpellGoal extends SpellcasterCastingSpellGoal {
        private EvokerCastingSpellGoal() {
        }
        
        @Override
        public void tick() {
            if (Evoker.this.getTarget() != null) {
                Evoker.this.getLookControl().setLookAt(Evoker.this.getTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
            }
            else if (Evoker.this.getWololoTarget() != null) {
                Evoker.this.getLookControl().setLookAt(Evoker.this.getWololoTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
            }
        }
    }
    
    class EvokerAttackSpellGoal extends SpellcasterUseSpellGoal {
        private EvokerAttackSpellGoal() {
        }
        
        @Override
        protected int getCastingTime() {
            return 40;
        }
        
        @Override
        protected int getCastingInterval() {
            return 100;
        }
        
        @Override
        protected void performSpellCasting() {
            final LivingEntity aix2 = Evoker.this.getTarget();
            final double double3 = Math.min(aix2.y, Evoker.this.y);
            final double double4 = Math.max(aix2.y, Evoker.this.y) + 1.0;
            final float float7 = (float)Mth.atan2(aix2.z - Evoker.this.z, aix2.x - Evoker.this.x);
            if (Evoker.this.distanceToSqr(aix2) < 9.0) {
                for (int integer8 = 0; integer8 < 5; ++integer8) {
                    final float float8 = float7 + integer8 * 3.1415927f * 0.4f;
                    this.createSpellEntity(Evoker.this.x + Mth.cos(float8) * 1.5, Evoker.this.z + Mth.sin(float8) * 1.5, double3, double4, float8, 0);
                }
                for (int integer8 = 0; integer8 < 8; ++integer8) {
                    final float float8 = float7 + integer8 * 3.1415927f * 2.0f / 8.0f + 1.2566371f;
                    this.createSpellEntity(Evoker.this.x + Mth.cos(float8) * 2.5, Evoker.this.z + Mth.sin(float8) * 2.5, double3, double4, float8, 3);
                }
            }
            else {
                for (int integer8 = 0; integer8 < 16; ++integer8) {
                    final double double5 = 1.25 * (integer8 + 1);
                    final int integer9 = 1 * integer8;
                    this.createSpellEntity(Evoker.this.x + Mth.cos(float7) * double5, Evoker.this.z + Mth.sin(float7) * double5, double3, double4, float7, integer9);
                }
            }
        }
        
        private void createSpellEntity(final double double1, final double double2, final double double3, final double double4, final float float5, final int integer) {
            BlockPos ew12 = new BlockPos(double1, double4, double2);
            boolean boolean13 = false;
            double double5 = 0.0;
            do {
                final BlockPos ew13 = ew12.below();
                final BlockState bvt17 = Evoker.this.level.getBlockState(ew13);
                if (bvt17.isFaceSturdy(Evoker.this.level, ew13, Direction.UP)) {
                    if (!Evoker.this.level.isEmptyBlock(ew12)) {
                        final BlockState bvt18 = Evoker.this.level.getBlockState(ew12);
                        final VoxelShape ctc19 = bvt18.getCollisionShape(Evoker.this.level, ew12);
                        if (!ctc19.isEmpty()) {
                            double5 = ctc19.max(Direction.Axis.Y);
                        }
                    }
                    boolean13 = true;
                    break;
                }
                ew12 = ew12.below();
            } while (ew12.getY() >= Mth.floor(double3) - 1);
            if (boolean13) {
                Evoker.this.level.addFreshEntity(new EvokerFangs(Evoker.this.level, double1, ew12.getY() + double5, double2, float5, integer, Evoker.this));
            }
        }
        
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }
        
        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }
    
    class EvokerSummonSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting;
        
        private EvokerSummonSpellGoal() {
            this.vexCountTargeting = new TargetingConditions().range(16.0).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();
        }
        
        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            final int integer2 = Evoker.this.level.<LivingEntity>getNearbyEntities((java.lang.Class<? extends LivingEntity>)Vex.class, this.vexCountTargeting, (LivingEntity)Evoker.this, Evoker.this.getBoundingBox().inflate(16.0)).size();
            return Evoker.this.random.nextInt(8) + 1 > integer2;
        }
        
        @Override
        protected int getCastingTime() {
            return 100;
        }
        
        @Override
        protected int getCastingInterval() {
            return 340;
        }
        
        @Override
        protected void performSpellCasting() {
            for (int integer2 = 0; integer2 < 3; ++integer2) {
                final BlockPos ew3 = new BlockPos(Evoker.this).offset(-2 + Evoker.this.random.nextInt(5), 1, -2 + Evoker.this.random.nextInt(5));
                final Vex avi4 = EntityType.VEX.create(Evoker.this.level);
                avi4.moveTo(ew3, 0.0f, 0.0f);
                avi4.finalizeSpawn(Evoker.this.level, Evoker.this.level.getCurrentDifficultyAt(ew3), MobSpawnType.MOB_SUMMONED, null, null);
                avi4.setOwner(Evoker.this);
                avi4.setBoundOrigin(ew3);
                avi4.setLimitedLife(20 * (30 + Evoker.this.random.nextInt(90)));
                Evoker.this.level.addFreshEntity(avi4);
            }
        }
        
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }
        
        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }
    
    public class EvokerWololoSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions wololoTargeting;
        
        public EvokerWololoSpellGoal() {
            this.wololoTargeting = new TargetingConditions().range(16.0).allowInvulnerable().selector((Predicate<LivingEntity>)(aix -> ((Sheep)aix).getColor() == DyeColor.BLUE));
        }
        
        @Override
        public boolean canUse() {
            if (Evoker.this.getTarget() != null) {
                return false;
            }
            if (Evoker.this.isCastingSpell()) {
                return false;
            }
            if (Evoker.this.tickCount < this.nextAttackTickCount) {
                return false;
            }
            if (!Evoker.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            }
            final List<Sheep> list2 = Evoker.this.level.<Sheep>getNearbyEntities((java.lang.Class<? extends Sheep>)Sheep.class, this.wololoTargeting, (LivingEntity)Evoker.this, Evoker.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
            if (list2.isEmpty()) {
                return false;
            }
            Evoker.this.setWololoTarget((Sheep)list2.get(Evoker.this.random.nextInt(list2.size())));
            return true;
        }
        
        @Override
        public boolean canContinueToUse() {
            return Evoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }
        
        @Override
        public void stop() {
            super.stop();
            Evoker.this.setWololoTarget(null);
        }
        
        @Override
        protected void performSpellCasting() {
            final Sheep ars2 = Evoker.this.getWololoTarget();
            if (ars2 != null && ars2.isAlive()) {
                ars2.setColor(DyeColor.RED);
            }
        }
        
        @Override
        protected int getCastWarmupTime() {
            return 40;
        }
        
        @Override
        protected int getCastingTime() {
            return 60;
        }
        
        @Override
        protected int getCastingInterval() {
            return 140;
        }
        
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }
        
        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.WOLOLO;
        }
    }
}

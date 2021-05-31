package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.crafting.Ingredient;

public class Ocelot extends Animal {
    private static final Ingredient TEMPT_INGREDIENT;
    private static final EntityDataAccessor<Boolean> DATA_TRUSTING;
    private OcelotAvoidEntityGoal<Player> ocelotAvoidPlayersGoal;
    private OcelotTemptGoal temptGoal;
    
    public Ocelot(final EntityType<? extends Ocelot> ais, final Level bhr) {
        super(ais, bhr);
        this.reassessTrustingGoals();
    }
    
    private boolean isTrusting() {
        return this.entityData.<Boolean>get(Ocelot.DATA_TRUSTING);
    }
    
    private void setTrusting(final boolean boolean1) {
        this.entityData.<Boolean>set(Ocelot.DATA_TRUSTING, boolean1);
        this.reassessTrustingGoals();
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Trusting", this.isTrusting());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setTrusting(id.getBoolean("Trusting"));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Ocelot.DATA_TRUSTING, false);
    }
    
    @Override
    protected void registerGoals() {
        this.temptGoal = new OcelotTemptGoal(this, 0.6, Ocelot.TEMPT_INGREDIENT, true);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(9, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.8, 1.0000001E-5f));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Chicken.class, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            final double double2 = this.getMoveControl().getSpeedModifier();
            if (double2 == 0.6) {
                this.setSneaking(true);
                this.setSprinting(false);
            }
            else if (double2 == 1.33) {
                this.setSneaking(false);
                this.setSprinting(true);
            }
            else {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        }
        else {
            this.setSneaking(false);
            this.setSprinting(false);
        }
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return !this.isTrusting() && this.tickCount > 2400;
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.OCELOT_AMBIENT;
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 900;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.OCELOT_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.OCELOT_DEATH;
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        return aio.hurt(DamageSource.mobAttack(this), 3.0f);
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        return !this.isInvulnerableTo(ahx) && super.hurt(ahx, float2);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if ((this.temptGoal == null || this.temptGoal.isRunning()) && !this.isTrusting() && this.isFood(bcj4) && awg.distanceToSqr(this) < 9.0) {
            this.usePlayerItem(awg, bcj4);
            if (!this.level.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.spawnTrustingParticles(true);
                    this.level.broadcastEntityEvent(this, (byte)41);
                }
                else {
                    this.spawnTrustingParticles(false);
                    this.level.broadcastEntityEvent(this, (byte)40);
                }
            }
            return true;
        }
        return super.mobInteract(awg, ahi);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 41) {
            this.spawnTrustingParticles(true);
        }
        else if (byte1 == 40) {
            this.spawnTrustingParticles(false);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    private void spawnTrustingParticles(final boolean boolean1) {
        ParticleOptions gf3 = ParticleTypes.HEART;
        if (!boolean1) {
            gf3 = ParticleTypes.SMOKE;
        }
        for (int integer4 = 0; integer4 < 7; ++integer4) {
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            final double double7 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(gf3, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double5, double6, double7);
        }
    }
    
    protected void reassessTrustingGoals() {
        if (this.ocelotAvoidPlayersGoal == null) {
            this.ocelotAvoidPlayersGoal = new OcelotAvoidEntityGoal<Player>(this, Player.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.ocelotAvoidPlayersGoal);
        if (!this.isTrusting()) {
            this.goalSelector.addGoal(4, this.ocelotAvoidPlayersGoal);
        }
    }
    
    @Override
    public Ocelot getBreedOffspring(final AgableMob aim) {
        return EntityType.OCELOT.create(this.level);
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return Ocelot.TEMPT_INGREDIENT.test(bcj);
    }
    
    public static boolean checkOcelotSpawnRules(final EntityType<Ocelot> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return random.nextInt(3) != 0;
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        if (bhu.isUnobstructed(this) && !bhu.containsAnyLiquid(this.getBoundingBox())) {
            final BlockPos ew3 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
            if (ew3.getY() < bhu.getSeaLevel()) {
                return false;
            }
            final BlockState bvt4 = bhu.getBlockState(ew3.below());
            final Block bmv5 = bvt4.getBlock();
            if (bmv5 == Blocks.GRASS_BLOCK || bvt4.is(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }
    
    protected void addKittensDuringSpawn() {
        for (int integer2 = 0; integer2 < 2; ++integer2) {
            final Ocelot ark3 = EntityType.OCELOT.create(this.level);
            ark3.moveTo(this.x, this.y, this.z, this.yRot, 0.0f);
            ark3.setAge(-24000);
            this.level.addFreshEntity(ark3);
        }
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        if (bhs.getRandom().nextInt(7) == 0) {
            this.addKittensDuringSpawn();
        }
        return ajj;
    }
    
    static {
        TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.SALMON);
        DATA_TRUSTING = SynchedEntityData.<Boolean>defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);
    }
    
    static class OcelotAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Ocelot ocelot;
        
        public OcelotAvoidEntityGoal(final Ocelot ark, final Class<T> class2, final float float3, final double double4, final double double5) {
            super(ark, class2, float3, double4, double5, (Predicate<LivingEntity>)EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.ocelot = ark;
        }
        
        @Override
        public boolean canUse() {
            return !this.ocelot.isTrusting() && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.ocelot.isTrusting() && super.canContinueToUse();
        }
    }
    
    static class OcelotTemptGoal extends TemptGoal {
        private final Ocelot ocelot;
        
        public OcelotTemptGoal(final Ocelot ark, final double double2, final Ingredient beo, final boolean boolean4) {
            super(ark, double2, beo, boolean4);
            this.ocelot = ark;
        }
        
        @Override
        protected boolean canScare() {
            return super.canScare() && !this.ocelot.isTrusting();
        }
    }
}

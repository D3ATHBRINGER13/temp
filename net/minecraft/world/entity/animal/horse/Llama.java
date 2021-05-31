package net.minecraft.world.entity.animal.horse;

import java.util.function.Predicate;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.SoundType;
import java.util.Iterator;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.Container;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class Llama extends AbstractChestedHorse implements RangedAttackMob {
    private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID;
    private static final EntityDataAccessor<Integer> DATA_SWAG_ID;
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID;
    private boolean didSpit;
    @Nullable
    private Llama caravanHead;
    @Nullable
    private Llama caravanTail;
    
    public Llama(final EntityType<? extends Llama> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public boolean isTraderLlama() {
        return false;
    }
    
    private void setStrength(final int integer) {
        this.entityData.<Integer>set(Llama.DATA_STRENGTH_ID, Math.max(1, Math.min(5, integer)));
    }
    
    private void setRandomStrength() {
        final int integer2 = (this.random.nextFloat() < 0.04f) ? 5 : 3;
        this.setStrength(1 + this.random.nextInt(integer2));
    }
    
    public int getStrength() {
        return this.entityData.<Integer>get(Llama.DATA_STRENGTH_ID);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Variant", this.getVariant());
        id.putInt("Strength", this.getStrength());
        if (!this.inventory.getItem(1).isEmpty()) {
            id.put("DecorItem", (Tag)this.inventory.getItem(1).save(new CompoundTag()));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        this.setStrength(id.getInt("Strength"));
        super.readAdditionalSaveData(id);
        this.setVariant(id.getInt("Variant"));
        if (id.contains("DecorItem", 10)) {
            this.inventory.setItem(1, ItemStack.of(id.getCompound("DecorItem")));
        }
        this.updateEquipment();
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.0999999046325684));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new LlamaHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new LlamaAttackWolfGoal(this));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Llama.DATA_STRENGTH_ID, 0);
        this.entityData.<Integer>define(Llama.DATA_SWAG_ID, -1);
        this.entityData.<Integer>define(Llama.DATA_VARIANT_ID, 0);
    }
    
    public int getVariant() {
        return Mth.clamp(this.entityData.<Integer>get(Llama.DATA_VARIANT_ID), 0, 3);
    }
    
    public void setVariant(final int integer) {
        this.entityData.<Integer>set(Llama.DATA_VARIANT_ID, integer);
    }
    
    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.getInventoryColumns();
        }
        return super.getInventorySize();
    }
    
    @Override
    public void positionRider(final Entity aio) {
        if (!this.hasPassenger(aio)) {
            return;
        }
        final float float3 = Mth.cos(this.yBodyRot * 0.017453292f);
        final float float4 = Mth.sin(this.yBodyRot * 0.017453292f);
        final float float5 = 0.3f;
        aio.setPos(this.x + 0.3f * float4, this.y + this.getRideHeight() + aio.getRidingHeight(), this.z - 0.3f * float3);
    }
    
    @Override
    public double getRideHeight() {
        return this.getBbHeight() * 0.67;
    }
    
    @Override
    public boolean canBeControlledByRider() {
        return false;
    }
    
    @Override
    protected boolean handleEating(final Player awg, final ItemStack bcj) {
        int integer4 = 0;
        int integer5 = 0;
        float float6 = 0.0f;
        boolean boolean7 = false;
        final Item bce8 = bcj.getItem();
        if (bce8 == Items.WHEAT) {
            integer4 = 10;
            integer5 = 3;
            float6 = 2.0f;
        }
        else if (bce8 == Blocks.HAY_BLOCK.asItem()) {
            integer4 = 90;
            integer5 = 6;
            float6 = 10.0f;
            if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
                boolean7 = true;
                this.setInLove(awg);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && float6 > 0.0f) {
            this.heal(float6);
            boolean7 = true;
        }
        if (this.isBaby() && integer4 > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp(integer4);
            }
            boolean7 = true;
        }
        if (integer5 > 0 && (boolean7 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            boolean7 = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(integer5);
            }
        }
        if (boolean7 && !this.isSilent()) {
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.LLAMA_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return boolean7;
    }
    
    @Override
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f || this.isEating();
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        this.setRandomStrength();
        int integer7;
        if (ajj instanceof LlamaGroupData) {
            integer7 = ((LlamaGroupData)ajj).variant;
        }
        else {
            integer7 = this.random.nextInt(4);
            ajj = new LlamaGroupData(integer7);
        }
        this.setVariant(integer7);
        return ajj;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.LLAMA_ANGRY;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.LLAMA_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.LLAMA_STEP, 0.15f, 1.0f);
    }
    
    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.LLAMA_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }
    
    @Override
    public void makeMad() {
        final SoundEvent yo2 = this.getAngrySound();
        if (yo2 != null) {
            this.playSound(yo2, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    @Override
    public int getInventoryColumns() {
        return this.getStrength();
    }
    
    @Override
    public boolean wearsArmor() {
        return true;
    }
    
    @Override
    public boolean isArmor(final ItemStack bcj) {
        final Item bce3 = bcj.getItem();
        return ItemTags.CARPETS.contains(bce3);
    }
    
    @Override
    public boolean canBeSaddled() {
        return false;
    }
    
    @Override
    public void containerChanged(final Container ahc) {
        final DyeColor bbg3 = this.getSwag();
        super.containerChanged(ahc);
        final DyeColor bbg4 = this.getSwag();
        if (this.tickCount > 20 && bbg4 != null && bbg4 != bbg3) {
            this.playSound(SoundEvents.LLAMA_SWAG, 0.5f, 1.0f);
        }
    }
    
    @Override
    protected void updateEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        super.updateEquipment();
        this.setSwag(getDyeColor(this.inventory.getItem(1)));
    }
    
    private void setSwag(@Nullable final DyeColor bbg) {
        this.entityData.<Integer>set(Llama.DATA_SWAG_ID, (bbg == null) ? -1 : bbg.getId());
    }
    
    @Nullable
    private static DyeColor getDyeColor(final ItemStack bcj) {
        final Block bmv2 = Block.byItem(bcj.getItem());
        if (bmv2 instanceof WoolCarpetBlock) {
            return ((WoolCarpetBlock)bmv2).getColor();
        }
        return null;
    }
    
    @Nullable
    public DyeColor getSwag() {
        final int integer2 = this.entityData.<Integer>get(Llama.DATA_SWAG_ID);
        return (integer2 == -1) ? null : DyeColor.byId(integer2);
    }
    
    @Override
    public int getMaxTemper() {
        return 30;
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        return ara != this && ara instanceof Llama && this.canParent() && ((Llama)ara).canParent();
    }
    
    @Override
    public Llama getBreedOffspring(final AgableMob aim) {
        final Llama ase3 = this.makeBabyLlama();
        this.setOffspringAttributes(aim, ase3);
        final Llama ase4 = (Llama)aim;
        int integer5 = this.random.nextInt(Math.max(this.getStrength(), ase4.getStrength())) + 1;
        if (this.random.nextFloat() < 0.03f) {
            ++integer5;
        }
        ase3.setStrength(integer5);
        ase3.setVariant(this.random.nextBoolean() ? this.getVariant() : ase4.getVariant());
        return ase3;
    }
    
    protected Llama makeBabyLlama() {
        return EntityType.LLAMA.create(this.level);
    }
    
    private void spit(final LivingEntity aix) {
        final LlamaSpit awu3 = new LlamaSpit(this.level, this);
        final double double4 = aix.x - this.x;
        final double double5 = aix.getBoundingBox().minY + aix.getBbHeight() / 3.0f - awu3.y;
        final double double6 = aix.z - this.z;
        final float float10 = Mth.sqrt(double4 * double4 + double6 * double6) * 0.2f;
        awu3.shoot(double4, double5 + float10, double6, 1.5f, 10.0f);
        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        this.level.addFreshEntity(awu3);
        this.didSpit = true;
    }
    
    private void setDidSpit(final boolean boolean1) {
        this.didSpit = boolean1;
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
        final int integer4 = Mth.ceil((float1 * 0.5f - 3.0f) * float2);
        if (integer4 <= 0) {
            return;
        }
        if (float1 >= 6.0f) {
            this.hurt(DamageSource.FALL, (float)integer4);
            if (this.isVehicle()) {
                for (final Entity aio6 : this.getIndirectPassengers()) {
                    aio6.hurt(DamageSource.FALL, (float)integer4);
                }
            }
        }
        final BlockState bvt5 = this.level.getBlockState(new BlockPos(this.x, this.y - 0.2 - this.yRotO, this.z));
        if (!bvt5.isAir() && !this.isSilent()) {
            final SoundType bry6 = bvt5.getSoundType();
            this.level.playSound(null, this.x, this.y, this.z, bry6.getStepSound(), this.getSoundSource(), bry6.getVolume() * 0.5f, bry6.getPitch() * 0.75f);
        }
    }
    
    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }
        this.caravanHead = null;
    }
    
    public void joinCaravan(final Llama ase) {
        this.caravanHead = ase;
        this.caravanHead.caravanTail = this;
    }
    
    public boolean hasCaravanTail() {
        return this.caravanTail != null;
    }
    
    public boolean inCaravan() {
        return this.caravanHead != null;
    }
    
    @Nullable
    public Llama getCaravanHead() {
        return this.caravanHead;
    }
    
    protected double followLeashSpeed() {
        return 2.0;
    }
    
    @Override
    protected void followMommy() {
        if (!this.inCaravan() && this.isBaby()) {
            super.followMommy();
        }
    }
    
    @Override
    public boolean canEatGrass() {
        return false;
    }
    
    @Override
    public void performRangedAttack(final LivingEntity aix, final float float2) {
        this.spit(aix);
    }
    
    static {
        DATA_STRENGTH_ID = SynchedEntityData.<Integer>defineId(Llama.class, EntityDataSerializers.INT);
        DATA_SWAG_ID = SynchedEntityData.<Integer>defineId(Llama.class, EntityDataSerializers.INT);
        DATA_VARIANT_ID = SynchedEntityData.<Integer>defineId(Llama.class, EntityDataSerializers.INT);
    }
    
    static class LlamaGroupData implements SpawnGroupData {
        public final int variant;
        
        private LlamaGroupData(final int integer) {
            this.variant = integer;
        }
    }
    
    static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
        public LlamaHurtByTargetGoal(final Llama ase) {
            super(ase, new Class[0]);
        }
        
        @Override
        public boolean canContinueToUse() {
            if (this.mob instanceof Llama) {
                final Llama ase2 = (Llama)this.mob;
                if (ase2.didSpit) {
                    ase2.setDidSpit(false);
                    return false;
                }
            }
            return super.canContinueToUse();
        }
    }
    
    static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal<Wolf> {
        public LlamaAttackWolfGoal(final Llama ase) {
            super(ase, Wolf.class, 16, false, true, (Predicate<LivingEntity>)(aix -> !((Wolf)aix).isTame()));
        }
        
        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.25;
        }
    }
}

package net.minecraft.world.entity.animal;

import net.minecraft.world.level.storage.loot.LootTable;
import java.util.Random;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.HashMap;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.TamableAnimal;

public class Cat extends TamableAnimal {
    private static final Ingredient TEMPT_INGREDIENT;
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
    private static final EntityDataAccessor<Boolean> IS_LYING;
    private static final EntityDataAccessor<Boolean> RELAX_STATE_ONE;
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE;
    private CatAvoidEntityGoal<Player> avoidPlayersGoal;
    private TemptGoal temptGoal;
    private float lieDownAmount;
    private float lieDownAmountO;
    private float lieDownAmountTail;
    private float lieDownAmountOTail;
    private float relaxStateOneAmount;
    private float relaxStateOneAmountO;
    
    public Cat(final EntityType<? extends Cat> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ResourceLocation getResourceLocation() {
        return (ResourceLocation)Cat.TEXTURE_BY_TYPE.get(this.getCatType());
    }
    
    @Override
    protected void registerGoals() {
        this.sitGoal = new SitGoal(this);
        this.temptGoal = new CatTemptGoal(this, 0.6, Cat.TEMPT_INGREDIENT, true);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CatRelaxOnOwnerGoal(this));
        this.goalSelector.addGoal(2, this.sitGoal);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f));
        this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8, 1.0000001E-5f));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Rabbit.class, false, null));
        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    
    public int getCatType() {
        return this.entityData.<Integer>get(Cat.DATA_TYPE_ID);
    }
    
    public void setCatType(int integer) {
        if (integer < 0 || integer >= 11) {
            integer = this.random.nextInt(10);
        }
        this.entityData.<Integer>set(Cat.DATA_TYPE_ID, integer);
    }
    
    public void setLying(final boolean boolean1) {
        this.entityData.<Boolean>set(Cat.IS_LYING, boolean1);
    }
    
    public boolean isLying() {
        return this.entityData.<Boolean>get(Cat.IS_LYING);
    }
    
    public void setRelaxStateOne(final boolean boolean1) {
        this.entityData.<Boolean>set(Cat.RELAX_STATE_ONE, boolean1);
    }
    
    public boolean isRelaxStateOne() {
        return this.entityData.<Boolean>get(Cat.RELAX_STATE_ONE);
    }
    
    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.<Integer>get(Cat.DATA_COLLAR_COLOR));
    }
    
    public void setCollarColor(final DyeColor bbg) {
        this.entityData.<Integer>set(Cat.DATA_COLLAR_COLOR, bbg.getId());
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Cat.DATA_TYPE_ID, 1);
        this.entityData.<Boolean>define(Cat.IS_LYING, false);
        this.entityData.<Boolean>define(Cat.RELAX_STATE_ONE, false);
        this.entityData.<Integer>define(Cat.DATA_COLLAR_COLOR, DyeColor.RED.getId());
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("CatType", this.getCatType());
        id.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setCatType(id.getInt("CatType"));
        if (id.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(id.getInt("CollarColor")));
        }
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
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isTame()) {
            return SoundEvents.CAT_STRAY_AMBIENT;
        }
        if (this.isInLove()) {
            return SoundEvents.CAT_PURR;
        }
        if (this.random.nextInt(4) == 0) {
            return SoundEvents.CAT_PURREOW;
        }
        return SoundEvents.CAT_AMBIENT;
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
    
    public void hiss() {
        this.playSound(SoundEvents.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.CAT_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CAT_DEATH;
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
    
    @Override
    protected void usePlayerItem(final Player awg, final ItemStack bcj) {
        if (this.isFood(bcj)) {
            this.playSound(SoundEvents.CAT_EAT, 1.0f, 1.0f);
        }
        super.usePlayerItem(awg, bcj);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        return aio.hurt(DamageSource.mobAttack(this), 3.0f);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
            this.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
        }
        this.handleLieDown();
    }
    
    private void handleLieDown() {
        if ((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
            this.playSound(SoundEvents.CAT_PURR, 0.6f + 0.4f * (this.random.nextFloat() - this.random.nextFloat()), 1.0f);
        }
        this.updateLieDownAmount();
        this.updateRelaxStateOneAmount();
    }
    
    private void updateLieDownAmount() {
        this.lieDownAmountO = this.lieDownAmount;
        this.lieDownAmountOTail = this.lieDownAmountTail;
        if (this.isLying()) {
            this.lieDownAmount = Math.min(1.0f, this.lieDownAmount + 0.15f);
            this.lieDownAmountTail = Math.min(1.0f, this.lieDownAmountTail + 0.08f);
        }
        else {
            this.lieDownAmount = Math.max(0.0f, this.lieDownAmount - 0.22f);
            this.lieDownAmountTail = Math.max(0.0f, this.lieDownAmountTail - 0.13f);
        }
    }
    
    private void updateRelaxStateOneAmount() {
        this.relaxStateOneAmountO = this.relaxStateOneAmount;
        if (this.isRelaxStateOne()) {
            this.relaxStateOneAmount = Math.min(1.0f, this.relaxStateOneAmount + 0.1f);
        }
        else {
            this.relaxStateOneAmount = Math.max(0.0f, this.relaxStateOneAmount - 0.13f);
        }
    }
    
    public float getLieDownAmount(final float float1) {
        return Mth.lerp(float1, this.lieDownAmountO, this.lieDownAmount);
    }
    
    public float getLieDownAmountTail(final float float1) {
        return Mth.lerp(float1, this.lieDownAmountOTail, this.lieDownAmountTail);
    }
    
    public float getRelaxStateOneAmount(final float float1) {
        return Mth.lerp(float1, this.relaxStateOneAmountO, this.relaxStateOneAmount);
    }
    
    @Override
    public Cat getBreedOffspring(final AgableMob aim) {
        final Cat arb3 = EntityType.CAT.create(this.level);
        if (aim instanceof Cat) {
            if (this.random.nextBoolean()) {
                arb3.setCatType(this.getCatType());
            }
            else {
                arb3.setCatType(((Cat)aim).getCatType());
            }
            if (this.isTame()) {
                arb3.setOwnerUUID(this.getOwnerUUID());
                arb3.setTame(true);
                if (this.random.nextBoolean()) {
                    arb3.setCollarColor(this.getCollarColor());
                }
                else {
                    arb3.setCollarColor(((Cat)aim).getCollarColor());
                }
            }
        }
        return arb3;
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        if (!this.isTame()) {
            return false;
        }
        if (!(ara instanceof Cat)) {
            return false;
        }
        final Cat arb3 = (Cat)ara;
        return arb3.isTame() && super.canMate(ara);
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        if (bhs.getMoonBrightness() > 0.9f) {
            this.setCatType(this.random.nextInt(11));
        }
        else {
            this.setCatType(this.random.nextInt(10));
        }
        if (Feature.SWAMP_HUT.isInsideFeature(bhs, new BlockPos(this))) {
            this.setCatType(10);
            this.setPersistenceRequired();
        }
        return ajj;
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final Item bce5 = bcj4.getItem();
        if (this.isTame()) {
            if (this.isOwnedBy(awg)) {
                if (bce5 instanceof DyeItem) {
                    final DyeColor bbg6 = ((DyeItem)bce5).getDyeColor();
                    if (bbg6 != this.getCollarColor()) {
                        this.setCollarColor(bbg6);
                        if (!awg.abilities.instabuild) {
                            bcj4.shrink(1);
                        }
                        this.setPersistenceRequired();
                        return true;
                    }
                }
                else if (this.isFood(bcj4)) {
                    if (this.getHealth() < this.getMaxHealth() && bce5.isEdible()) {
                        this.usePlayerItem(awg, bcj4);
                        this.heal((float)bce5.getFoodProperties().getNutrition());
                        return true;
                    }
                }
                else if (!this.level.isClientSide) {
                    this.sitGoal.wantToSit(!this.isSitting());
                }
            }
        }
        else if (this.isFood(bcj4)) {
            this.usePlayerItem(awg, bcj4);
            if (!this.level.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(awg);
                    this.spawnTamingParticles(true);
                    this.sitGoal.wantToSit(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                }
                else {
                    this.spawnTamingParticles(false);
                    this.level.broadcastEntityEvent(this, (byte)6);
                }
            }
            this.setPersistenceRequired();
            return true;
        }
        final boolean boolean6 = super.mobInteract(awg, ahi);
        if (boolean6) {
            this.setPersistenceRequired();
        }
        return boolean6;
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return Cat.TEMPT_INGREDIENT.test(bcj);
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.5f;
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return !this.isTame() && this.tickCount > 2400;
    }
    
    @Override
    protected void reassessTameGoals() {
        if (this.avoidPlayersGoal == null) {
            this.avoidPlayersGoal = new CatAvoidEntityGoal<Player>(this, Player.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.avoidPlayersGoal);
        if (!this.isTame()) {
            this.goalSelector.addGoal(4, this.avoidPlayersGoal);
        }
    }
    
    static {
        TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.SALMON);
        DATA_TYPE_ID = SynchedEntityData.<Integer>defineId(Cat.class, EntityDataSerializers.INT);
        IS_LYING = SynchedEntityData.<Boolean>defineId(Cat.class, EntityDataSerializers.BOOLEAN);
        RELAX_STATE_ONE = SynchedEntityData.<Boolean>defineId(Cat.class, EntityDataSerializers.BOOLEAN);
        DATA_COLLAR_COLOR = SynchedEntityData.<Integer>defineId(Cat.class, EntityDataSerializers.INT);
        TEXTURE_BY_TYPE = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
            hashMap.put(1, new ResourceLocation("textures/entity/cat/black.png"));
            hashMap.put(2, new ResourceLocation("textures/entity/cat/red.png"));
            hashMap.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
            hashMap.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
            hashMap.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
            hashMap.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
            hashMap.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
            hashMap.put(8, new ResourceLocation("textures/entity/cat/white.png"));
            hashMap.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
            hashMap.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
        }));
    }
    
    static class CatAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Cat cat;
        
        public CatAvoidEntityGoal(final Cat arb, final Class<T> class2, final float float3, final double double4, final double double5) {
            super(arb, class2, float3, double4, double5, (Predicate<LivingEntity>)EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.cat = arb;
        }
        
        @Override
        public boolean canUse() {
            return !this.cat.isTame() && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.cat.isTame() && super.canContinueToUse();
        }
    }
    
    static class CatTemptGoal extends TemptGoal {
        @Nullable
        private Player selectedPlayer;
        private final Cat cat;
        
        public CatTemptGoal(final Cat arb, final double double2, final Ingredient beo, final boolean boolean4) {
            super(arb, double2, beo, boolean4);
            this.cat = arb;
        }
        
        @Override
        public void tick() {
            super.tick();
            if (this.selectedPlayer == null && this.mob.getRandom().nextInt(600) == 0) {
                this.selectedPlayer = this.player;
            }
            else if (this.mob.getRandom().nextInt(500) == 0) {
                this.selectedPlayer = null;
            }
        }
        
        @Override
        protected boolean canScare() {
            return (this.selectedPlayer == null || !this.selectedPlayer.equals(this.player)) && super.canScare();
        }
        
        @Override
        public boolean canUse() {
            return super.canUse() && !this.cat.isTame();
        }
    }
    
    static class CatRelaxOnOwnerGoal extends Goal {
        private final Cat cat;
        private Player ownerPlayer;
        private BlockPos goalPos;
        private int onBedTicks;
        
        public CatRelaxOnOwnerGoal(final Cat arb) {
            this.cat = arb;
        }
        
        @Override
        public boolean canUse() {
            if (!this.cat.isTame()) {
                return false;
            }
            if (this.cat.isSitting()) {
                return false;
            }
            final LivingEntity aix2 = this.cat.getOwner();
            if (aix2 instanceof Player) {
                this.ownerPlayer = (Player)aix2;
                if (!aix2.isSleeping()) {
                    return false;
                }
                if (this.cat.distanceToSqr(this.ownerPlayer) > 100.0) {
                    return false;
                }
                final BlockPos ew3 = new BlockPos(this.ownerPlayer);
                final BlockState bvt4 = this.cat.level.getBlockState(ew3);
                if (bvt4.getBlock().is(BlockTags.BEDS)) {
                    final Direction fb5 = bvt4.<Direction>getValue((Property<Direction>)BedBlock.FACING);
                    this.goalPos = new BlockPos(ew3.getX() - fb5.getStepX(), ew3.getY(), ew3.getZ() - fb5.getStepZ());
                    return !this.spaceIsOccupied();
                }
            }
            return false;
        }
        
        private boolean spaceIsOccupied() {
            final List<Cat> list2 = this.cat.level.<Cat>getEntitiesOfClass((java.lang.Class<? extends Cat>)Cat.class, new AABB(this.goalPos).inflate(2.0));
            for (final Cat arb4 : list2) {
                if (arb4 != this.cat && (arb4.isLying() || arb4.isRelaxStateOne())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean canContinueToUse() {
            return this.cat.isTame() && !this.cat.isSitting() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
        }
        
        @Override
        public void start() {
            if (this.goalPos != null) {
                this.cat.getSitGoal().wantToSit(false);
                this.cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY(), this.goalPos.getZ(), 1.100000023841858);
            }
        }
        
        @Override
        public void stop() {
            this.cat.setLying(false);
            final float float2 = this.cat.level.getTimeOfDay(1.0f);
            if (this.ownerPlayer.getSleepTimer() >= 100 && float2 > 0.77 && float2 < 0.8 && this.cat.level.getRandom().nextFloat() < 0.7) {
                this.giveMorningGift();
            }
            this.onBedTicks = 0;
            this.cat.setRelaxStateOne(false);
            this.cat.getNavigation().stop();
        }
        
        private void giveMorningGift() {
            final Random random2 = this.cat.getRandom();
            final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos();
            a3.set(this.cat);
            this.cat.randomTeleport(a3.getX() + random2.nextInt(11) - 5, a3.getY() + random2.nextInt(5) - 2, a3.getZ() + random2.nextInt(11) - 5, false);
            a3.set(this.cat);
            final LootTable cpb4 = this.cat.level.getServer().getLootTables().get(BuiltInLootTables.CAT_MORNING_GIFT);
            final LootContext.Builder a4 = new LootContext.Builder((ServerLevel)this.cat.level).<BlockPos>withParameter(LootContextParams.BLOCK_POS, a3).<Entity>withParameter(LootContextParams.THIS_ENTITY, this.cat).withRandom(random2);
            final List<ItemStack> list6 = cpb4.getRandomItems(a4.create(LootContextParamSets.GIFT));
            for (final ItemStack bcj8 : list6) {
                this.cat.level.addFreshEntity(new ItemEntity(this.cat.level, a3.getX() - Mth.sin(this.cat.yBodyRot * 0.017453292f), a3.getY(), a3.getZ() + Mth.cos(this.cat.yBodyRot * 0.017453292f), bcj8));
            }
        }
        
        @Override
        public void tick() {
            if (this.ownerPlayer != null && this.goalPos != null) {
                this.cat.getSitGoal().wantToSit(false);
                this.cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY(), this.goalPos.getZ(), 1.100000023841858);
                if (this.cat.distanceToSqr(this.ownerPlayer) < 2.5) {
                    ++this.onBedTicks;
                    if (this.onBedTicks > 16) {
                        this.cat.setLying(true);
                        this.cat.setRelaxStateOne(false);
                    }
                    else {
                        this.cat.lookAt(this.ownerPlayer, 45.0f, 45.0f);
                        this.cat.setRelaxStateOne(true);
                    }
                }
                else {
                    this.cat.setLying(false);
                }
            }
        }
    }
}

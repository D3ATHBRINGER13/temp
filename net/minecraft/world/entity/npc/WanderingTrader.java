package net.minecraft.world.entity.npc;

import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.Item;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class WanderingTrader extends AbstractVillager {
    @Nullable
    private BlockPos wanderTarget;
    private int despawnDelay;
    
    public WanderingTrader(final EntityType<? extends WanderingTrader> ais, final Level bhr) {
        super(ais, bhr);
        this.forcedLoading = true;
    }
    
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new UseItemGoal<>(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEvents.WANDERING_TRADER_DISAPPEARED, (java.util.function.Predicate<?>)(avz -> !this.level.isDay() && !avz.isInvisible())));
        this.goalSelector.addGoal(0, new UseItemGoal<>(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.WANDERING_TRADER_REAPPEARED, (java.util.function.Predicate<?>)(avz -> this.level.isDay() && avz.isInvisible())));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zombie.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Evoker.class, 12.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vindicator.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vex.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Pillager.class, 15.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Illusioner.class, 12.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.5));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(2, new WanderToPositionGoal(this, 2.0, 0.35));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return null;
    }
    
    @Override
    public boolean showProgressBar() {
        return false;
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final boolean boolean5 = bcj4.getItem() == Items.NAME_TAG;
        if (boolean5) {
            bcj4.interactEnemy(awg, this, ahi);
            return true;
        }
        if (bcj4.getItem() == Items.VILLAGER_SPAWN_EGG || !this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(awg, ahi);
        }
        if (ahi == InteractionHand.MAIN_HAND) {
            awg.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (this.getOffers().isEmpty()) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.level.isClientSide) {
            this.setTradingPlayer(awg);
            this.openTradingScreen(awg, this.getDisplayName(), 1);
        }
        return true;
    }
    
    @Override
    protected void updateTrades() {
        final VillagerTrades.ItemListing[] arr2 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        final VillagerTrades.ItemListing[] arr3 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(2);
        if (arr2 == null || arr3 == null) {
            return;
        }
        final MerchantOffers bgv4 = this.getOffers();
        this.addOffersFromItemListings(bgv4, arr2, 5);
        final int integer5 = this.random.nextInt(arr3.length);
        final VillagerTrades.ItemListing f6 = arr3[integer5];
        final MerchantOffer bgu7 = f6.getOffer(this, this.random);
        if (bgu7 != null) {
            bgv4.add(bgu7);
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            id.put("WanderTarget", (Tag)NbtUtils.writeBlockPos(this.wanderTarget));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("DespawnDelay", 99)) {
            this.despawnDelay = id.getInt("DespawnDelay");
        }
        if (id.contains("WanderTarget")) {
            this.wanderTarget = NbtUtils.readBlockPos(id.getCompound("WanderTarget"));
        }
        this.setAge(Math.max(0, this.getAge()));
    }
    
    public boolean removeWhenFarAway(final double double1) {
        return false;
    }
    
    @Override
    protected void rewardTradeXp(final MerchantOffer bgu) {
        if (bgu.shouldRewardExp()) {
            final int integer3 = 3 + this.random.nextInt(4);
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y + 0.5, this.z, integer3));
        }
    }
    
    protected SoundEvent getAmbientSound() {
        if (this.isTrading()) {
            return SoundEvents.WANDERING_TRADER_TRADE;
        }
        return SoundEvents.WANDERING_TRADER_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.WANDERING_TRADER_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.WANDERING_TRADER_DEATH;
    }
    
    protected SoundEvent getDrinkingSound(final ItemStack bcj) {
        final Item bce3 = bcj.getItem();
        if (bce3 == Items.MILK_BUCKET) {
            return SoundEvents.WANDERING_TRADER_DRINK_MILK;
        }
        return SoundEvents.WANDERING_TRADER_DRINK_POTION;
    }
    
    @Override
    protected SoundEvent getTradeUpdatedSound(final boolean boolean1) {
        return boolean1 ? SoundEvents.WANDERING_TRADER_YES : SoundEvents.WANDERING_TRADER_NO;
    }
    
    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.WANDERING_TRADER_YES;
    }
    
    public void setDespawnDelay(final int integer) {
        this.despawnDelay = integer;
    }
    
    public int getDespawnDelay() {
        return this.despawnDelay;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.maybeDespawn();
        }
    }
    
    private void maybeDespawn() {
        if (this.despawnDelay > 0 && !this.isTrading() && --this.despawnDelay == 0) {
            this.remove();
        }
    }
    
    public void setWanderTarget(@Nullable final BlockPos ew) {
        this.wanderTarget = ew;
    }
    
    @Nullable
    private BlockPos getWanderTarget() {
        return this.wanderTarget;
    }
    
    class WanderToPositionGoal extends Goal {
        final WanderingTrader trader;
        final double stopDistance;
        final double speedModifier;
        
        WanderToPositionGoal(final WanderingTrader avz2, final double double3, final double double4) {
            this.trader = avz2;
            this.stopDistance = double3;
            this.speedModifier = double4;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public void stop() {
            this.trader.setWanderTarget(null);
            WanderingTrader.this.navigation.stop();
        }
        
        @Override
        public boolean canUse() {
            final BlockPos ew2 = this.trader.getWanderTarget();
            return ew2 != null && this.isTooFarAway(ew2, this.stopDistance);
        }
        
        @Override
        public void tick() {
            final BlockPos ew2 = this.trader.getWanderTarget();
            if (ew2 != null && WanderingTrader.this.navigation.isDone()) {
                if (this.isTooFarAway(ew2, 10.0)) {
                    final Vec3 csi3 = new Vec3(ew2.getX() - this.trader.x, ew2.getY() - this.trader.y, ew2.getZ() - this.trader.z).normalize();
                    final Vec3 csi4 = csi3.scale(10.0).add(this.trader.x, this.trader.y, this.trader.z);
                    WanderingTrader.this.navigation.moveTo(csi4.x, csi4.y, csi4.z, this.speedModifier);
                }
                else {
                    WanderingTrader.this.navigation.moveTo(ew2.getX(), ew2.getY(), ew2.getZ(), this.speedModifier);
                }
            }
        }
        
        private boolean isTooFarAway(final BlockPos ew, final double double2) {
            return !ew.closerThan(this.trader.position(), double2);
        }
    }
}

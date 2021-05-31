package net.minecraft.world.entity.npc;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.trading.MerchantOffers;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.AgableMob;

public abstract class AbstractVillager extends AgableMob implements Npc, Merchant {
    private static final EntityDataAccessor<Integer> DATA_UNHAPPY_COUNTER;
    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected MerchantOffers offers;
    private final SimpleContainer inventory;
    
    public AbstractVillager(final EntityType<? extends AbstractVillager> ais, final Level bhr) {
        super(ais, bhr);
        this.inventory = new SimpleContainer(8);
    }
    
    public int getUnhappyCounter() {
        return this.entityData.<Integer>get(AbstractVillager.DATA_UNHAPPY_COUNTER);
    }
    
    public void setUnhappyCounter(final int integer) {
        this.entityData.<Integer>set(AbstractVillager.DATA_UNHAPPY_COUNTER, integer);
    }
    
    @Override
    public int getVillagerXp() {
        return 0;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        if (this.isBaby()) {
            return 0.81f;
        }
        return 1.62f;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(AbstractVillager.DATA_UNHAPPY_COUNTER, 0);
    }
    
    @Override
    public void setTradingPlayer(@Nullable final Player awg) {
        this.tradingPlayer = awg;
    }
    
    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }
    
    public boolean isTrading() {
        return this.tradingPlayer != null;
    }
    
    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
        }
        return this.offers;
    }
    
    @Override
    public void overrideOffers(@Nullable final MerchantOffers bgv) {
    }
    
    @Override
    public void overrideXp(final int integer) {
    }
    
    @Override
    public void notifyTrade(final MerchantOffer bgu) {
        bgu.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(bgu);
        if (this.tradingPlayer instanceof ServerPlayer) {
            CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, bgu.getResult());
        }
    }
    
    protected abstract void rewardTradeXp(final MerchantOffer bgu);
    
    @Override
    public boolean showProgressBar() {
        return true;
    }
    
    @Override
    public void notifyTradeUpdated(final ItemStack bcj) {
        if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getTradeUpdatedSound(!bcj.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }
    
    protected SoundEvent getTradeUpdatedSound(final boolean boolean1) {
        return boolean1 ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }
    
    public void playCelebrateSound() {
        this.playSound(SoundEvents.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        final MerchantOffers bgv3 = this.getOffers();
        if (!bgv3.isEmpty()) {
            id.put("Offers", (Tag)bgv3.createTag());
        }
        final ListTag ik4 = new ListTag();
        for (int integer5 = 0; integer5 < this.inventory.getContainerSize(); ++integer5) {
            final ItemStack bcj6 = this.inventory.getItem(integer5);
            if (!bcj6.isEmpty()) {
                ik4.add(bcj6.save(new CompoundTag()));
            }
        }
        id.put("Inventory", (Tag)ik4);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Offers", 10)) {
            this.offers = new MerchantOffers(id.getCompound("Offers"));
        }
        final ListTag ik3 = id.getList("Inventory", 10);
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final ItemStack bcj5 = ItemStack.of(ik3.getCompound(integer4));
            if (!bcj5.isEmpty()) {
                this.inventory.addItem(bcj5);
            }
        }
    }
    
    @Nullable
    public Entity changeDimension(final DimensionType byn) {
        this.stopTrading();
        return super.changeDimension(byn);
    }
    
    protected void stopTrading() {
        this.setTradingPlayer(null);
    }
    
    public void die(final DamageSource ahx) {
        super.die(ahx);
        this.stopTrading();
    }
    
    protected void addParticlesAroundSelf(final ParticleOptions gf) {
        for (int integer3 = 0; integer3 < 5; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(gf, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 1.0 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double4, double5, double6);
        }
    }
    
    public boolean canBeLeashed(final Player awg) {
        return false;
    }
    
    public SimpleContainer getInventory() {
        return this.inventory;
    }
    
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (super.setSlot(integer, bcj)) {
            return true;
        }
        final int integer2 = integer - 300;
        if (integer2 >= 0 && integer2 < this.inventory.getContainerSize()) {
            this.inventory.setItem(integer2, bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public Level getLevel() {
        return this.level;
    }
    
    protected abstract void updateTrades();
    
    protected void addOffersFromItemListings(final MerchantOffers bgv, final VillagerTrades.ItemListing[] arr, final int integer) {
        final Set<Integer> set5 = (Set<Integer>)Sets.newHashSet();
        if (arr.length > integer) {
            while (set5.size() < integer) {
                set5.add(this.random.nextInt(arr.length));
            }
        }
        else {
            for (int integer2 = 0; integer2 < arr.length; ++integer2) {
                set5.add(integer2);
            }
        }
        for (final Integer integer3 : set5) {
            final VillagerTrades.ItemListing f8 = arr[integer3];
            final MerchantOffer bgu9 = f8.getOffer(this, this.random);
            if (bgu9 != null) {
                bgv.add(bgu9);
            }
        }
    }
    
    static {
        DATA_UNHAPPY_COUNTER = SynchedEntityData.<Integer>defineId(AbstractVillager.class, EntityDataSerializers.INT);
    }
}

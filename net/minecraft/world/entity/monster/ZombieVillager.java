package net.minecraft.world.entity.monster;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import java.util.UUID;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.npc.VillagerDataHolder;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID;
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
    private int villagerConversionTime;
    private UUID conversionStarter;
    private Tag gossips;
    private CompoundTag tradeOffers;
    private int villagerXp;
    
    public ZombieVillager(final EntityType<? extends ZombieVillager> ais, final Level bhr) {
        super(ais, bhr);
        this.setVillagerData(this.getVillagerData().setProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(ZombieVillager.DATA_CONVERTING_ID, false);
        this.entityData.<VillagerData>define(ZombieVillager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.put("VillagerData", (Tag)this.getVillagerData().<Tag>serialize((com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE));
        if (this.tradeOffers != null) {
            id.put("Offers", (Tag)this.tradeOffers);
        }
        if (this.gossips != null) {
            id.put("Gossips", this.gossips);
        }
        id.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            id.putUUID("ConversionPlayer", this.conversionStarter);
        }
        id.putInt("Xp", this.villagerXp);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic((DynamicOps)NbtOps.INSTANCE, id.get("VillagerData"))));
        }
        if (id.contains("Offers", 10)) {
            this.tradeOffers = id.getCompound("Offers");
        }
        if (id.contains("Gossips", 10)) {
            this.gossips = id.getList("Gossips", 10);
        }
        if (id.contains("ConversionTime", 99) && id.getInt("ConversionTime") > -1) {
            this.startConverting(id.hasUUID("ConversionPlayer") ? id.getUUID("ConversionPlayer") : null, id.getInt("ConversionTime"));
        }
        if (id.contains("Xp", 3)) {
            this.villagerXp = id.getInt("Xp");
        }
    }
    
    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            final int integer2 = this.getConversionProgress();
            this.villagerConversionTime -= integer2;
            if (this.villagerConversionTime <= 0) {
                this.finishConversion((ServerLevel)this.level);
            }
        }
        super.tick();
    }
    
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            if (!this.level.isClientSide) {
                this.startConverting(awg.getUUID(), this.random.nextInt(2401) + 3600);
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean convertsInWater() {
        return false;
    }
    
    public boolean removeWhenFarAway(final double double1) {
        return !this.isConverting();
    }
    
    public boolean isConverting() {
        return this.getEntityData().<Boolean>get(ZombieVillager.DATA_CONVERTING_ID);
    }
    
    private void startConverting(@Nullable final UUID uUID, final int integer) {
        this.conversionStarter = uUID;
        this.villagerConversionTime = integer;
        this.getEntityData().<Boolean>set(ZombieVillager.DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, integer, Math.min(this.level.getDifficulty().getId() - 1, 0)));
        this.level.broadcastEntityEvent(this, (byte)16);
    }
    
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            return;
        }
        super.handleEntityEvent(byte1);
    }
    
    private void finishConversion(final ServerLevel vk) {
        final Villager avt3 = EntityType.VILLAGER.create(vk);
        avt3.copyPosition(this);
        avt3.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            avt3.setGossips(this.gossips);
        }
        if (this.tradeOffers != null) {
            avt3.setOffers(new MerchantOffers(this.tradeOffers));
        }
        avt3.setVillagerXp(this.villagerXp);
        avt3.finalizeSpawn(vk, vk.getCurrentDifficultyAt(new BlockPos(avt3)), MobSpawnType.CONVERSION, null, null);
        if (this.isBaby()) {
            avt3.setAge(-24000);
        }
        this.remove();
        avt3.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            avt3.setCustomName(this.getCustomName());
            avt3.setCustomNameVisible(this.isCustomNameVisible());
        }
        vk.addFreshEntity(avt3);
        if (this.conversionStarter != null) {
            final Player awg4 = vk.getPlayerByUUID(this.conversionStarter);
            if (awg4 instanceof ServerPlayer) {
                CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)awg4, this, avt3);
                vk.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, awg4, avt3);
            }
        }
        avt3.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        vk.levelEvent(null, 1027, new BlockPos(this), 0);
    }
    
    private int getConversionProgress() {
        int integer2 = 1;
        if (this.random.nextFloat() < 0.01f) {
            int integer3 = 0;
            final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos();
            for (int integer4 = (int)this.x - 4; integer4 < (int)this.x + 4 && integer3 < 14; ++integer4) {
                for (int integer5 = (int)this.y - 4; integer5 < (int)this.y + 4 && integer3 < 14; ++integer5) {
                    for (int integer6 = (int)this.z - 4; integer6 < (int)this.z + 4 && integer3 < 14; ++integer6) {
                        final Block bmv8 = this.level.getBlockState(a4.set(integer4, integer5, integer6)).getBlock();
                        if (bmv8 == Blocks.IRON_BARS || bmv8 instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3f) {
                                ++integer2;
                            }
                            ++integer3;
                        }
                    }
                }
            }
        }
        return integer2;
    }
    
    protected float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 2.0f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }
    
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }
    
    public SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }
    
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }
    
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
    
    public void setTradeOffers(final CompoundTag id) {
        this.tradeOffers = id;
    }
    
    public void setGossips(final Tag iu) {
        this.gossips = iu;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(bhs.getBiome(new BlockPos(this)))));
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    public void setVillagerData(final VillagerData avu) {
        final VillagerData avu2 = this.getVillagerData();
        if (avu2.getProfession() != avu.getProfession()) {
            this.tradeOffers = null;
        }
        this.entityData.<VillagerData>set(ZombieVillager.DATA_VILLAGER_DATA, avu);
    }
    
    @Override
    public VillagerData getVillagerData() {
        return this.entityData.<VillagerData>get(ZombieVillager.DATA_VILLAGER_DATA);
    }
    
    public void setVillagerXp(final int integer) {
        this.villagerXp = integer;
    }
    
    static {
        DATA_CONVERTING_ID = SynchedEntityData.<Boolean>defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
        DATA_VILLAGER_DATA = SynchedEntityData.<VillagerData>defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
    }
}

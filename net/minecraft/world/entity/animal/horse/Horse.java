package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import java.util.UUID;

public class Horse extends AbstractHorse {
    private static final UUID ARMOR_MODIFIER_UUID;
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
    private static final String[] VARIANT_TEXTURES;
    private static final String[] VARIANT_HASHES;
    private static final String[] MARKING_TEXTURES;
    private static final String[] MARKING_HASHES;
    private String layerTextureHashName;
    private final String[] layerTextureLayers;
    
    public Horse(final EntityType<? extends Horse> ais, final Level bhr) {
        super(ais, bhr);
        this.layerTextureLayers = new String[2];
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Horse.DATA_ID_TYPE_VARIANT, 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Variant", this.getVariant());
        if (!this.inventory.getItem(1).isEmpty()) {
            id.put("ArmorItem", (Tag)this.inventory.getItem(1).save(new CompoundTag()));
        }
    }
    
    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }
    
    private void setArmor(final ItemStack bcj) {
        this.setItemSlot(EquipmentSlot.CHEST, bcj);
        this.setDropChance(EquipmentSlot.CHEST, 0.0f);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setVariant(id.getInt("Variant"));
        if (id.contains("ArmorItem", 10)) {
            final ItemStack bcj3 = ItemStack.of(id.getCompound("ArmorItem"));
            if (!bcj3.isEmpty() && this.isArmor(bcj3)) {
                this.inventory.setItem(1, bcj3);
            }
        }
        this.updateEquipment();
    }
    
    public void setVariant(final int integer) {
        this.entityData.<Integer>set(Horse.DATA_ID_TYPE_VARIANT, integer);
        this.clearLayeredTextureInfo();
    }
    
    public int getVariant() {
        return this.entityData.<Integer>get(Horse.DATA_ID_TYPE_VARIANT);
    }
    
    private void clearLayeredTextureInfo() {
        this.layerTextureHashName = null;
    }
    
    private void rebuildLayeredTextureInfo() {
        final int integer2 = this.getVariant();
        final int integer3 = (integer2 & 0xFF) % 7;
        final int integer4 = ((integer2 & 0xFF00) >> 8) % 5;
        this.layerTextureLayers[0] = Horse.VARIANT_TEXTURES[integer3];
        this.layerTextureLayers[1] = Horse.MARKING_TEXTURES[integer4];
        this.layerTextureHashName = "horse/" + Horse.VARIANT_HASHES[integer3] + Horse.MARKING_HASHES[integer4];
    }
    
    public String getLayeredTextureHashName() {
        if (this.layerTextureHashName == null) {
            this.rebuildLayeredTextureInfo();
        }
        return this.layerTextureHashName;
    }
    
    public String[] getLayeredTextureLayers() {
        if (this.layerTextureHashName == null) {
            this.rebuildLayeredTextureInfo();
        }
        return this.layerTextureLayers;
    }
    
    @Override
    protected void updateEquipment() {
        super.updateEquipment();
        this.setArmorEquipment(this.inventory.getItem(1));
    }
    
    private void setArmorEquipment(final ItemStack bcj) {
        this.setArmor(bcj);
        if (!this.level.isClientSide) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(Horse.ARMOR_MODIFIER_UUID);
            if (this.isArmor(bcj)) {
                final int integer3 = ((HorseArmorItem)bcj.getItem()).getProtection();
                if (integer3 != 0) {
                    this.getAttribute(SharedMonsterAttributes.ARMOR).addModifier(new AttributeModifier(Horse.ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)integer3, AttributeModifier.Operation.ADDITION).setSerialize(false));
                }
            }
        }
    }
    
    @Override
    public void containerChanged(final Container ahc) {
        final ItemStack bcj3 = this.getArmor();
        super.containerChanged(ahc);
        final ItemStack bcj4 = this.getArmor();
        if (this.tickCount > 20 && this.isArmor(bcj4) && bcj3 != bcj4) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5f, 1.0f);
        }
    }
    
    @Override
    protected void playGallopSound(final SoundType bry) {
        super.playGallopSound(bry);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, bry.getVolume() * 0.6f, bry.getPitch());
        }
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
        this.getAttribute(Horse.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && this.entityData.isDirty()) {
            this.entityData.clearDirty();
            this.clearLayeredTextureInfo();
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        super.getHurtSound(ahx);
        return SoundEvents.HORSE_HURT;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final boolean boolean5 = !bcj4.isEmpty();
        if (boolean5 && bcj4.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.isBaby()) {
            if (this.isTamed() && awg.isSneaking()) {
                this.openInventory(awg);
                return true;
            }
            if (this.isVehicle()) {
                return super.mobInteract(awg, ahi);
            }
        }
        if (boolean5) {
            if (this.handleEating(awg, bcj4)) {
                if (!awg.abilities.instabuild) {
                    bcj4.shrink(1);
                }
                return true;
            }
            if (bcj4.interactEnemy(awg, this, ahi)) {
                return true;
            }
            if (!this.isTamed()) {
                this.makeMad();
                return true;
            }
            final boolean boolean6 = !this.isBaby() && !this.isSaddled() && bcj4.getItem() == Items.SADDLE;
            if (this.isArmor(bcj4) || boolean6) {
                this.openInventory(awg);
                return true;
            }
        }
        if (this.isBaby()) {
            return super.mobInteract(awg, ahi);
        }
        this.doPlayerRide(awg);
        return true;
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        return ara != this && (ara instanceof Donkey || ara instanceof Horse) && this.canParent() && ((AbstractHorse)ara).canParent();
    }
    
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        AbstractHorse asb3;
        if (aim instanceof Donkey) {
            asb3 = EntityType.MULE.create(this.level);
        }
        else {
            final Horse asd4 = (Horse)aim;
            asb3 = EntityType.HORSE.create(this.level);
            final int integer6 = this.random.nextInt(9);
            int integer7;
            if (integer6 < 4) {
                integer7 = (this.getVariant() & 0xFF);
            }
            else if (integer6 < 8) {
                integer7 = (asd4.getVariant() & 0xFF);
            }
            else {
                integer7 = this.random.nextInt(7);
            }
            final int integer8 = this.random.nextInt(5);
            if (integer8 < 2) {
                integer7 |= (this.getVariant() & 0xFF00);
            }
            else if (integer8 < 4) {
                integer7 |= (asd4.getVariant() & 0xFF00);
            }
            else {
                integer7 |= (this.random.nextInt(5) << 8 & 0xFF00);
            }
            ((Horse)asb3).setVariant(integer7);
        }
        this.setOffspringAttributes(aim, asb3);
        return asb3;
    }
    
    @Override
    public boolean wearsArmor() {
        return true;
    }
    
    @Override
    public boolean isArmor(final ItemStack bcj) {
        return bcj.getItem() instanceof HorseArmorItem;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        int integer7;
        if (ajj instanceof HorseGroupData) {
            integer7 = ((HorseGroupData)ajj).variant;
        }
        else {
            integer7 = this.random.nextInt(7);
            ajj = new HorseGroupData(integer7);
        }
        this.setVariant(integer7 | this.random.nextInt(5) << 8);
        return ajj;
    }
    
    static {
        ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
        DATA_ID_TYPE_VARIANT = SynchedEntityData.<Integer>defineId(Horse.class, EntityDataSerializers.INT);
        VARIANT_TEXTURES = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png" };
        VARIANT_HASHES = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb" };
        MARKING_TEXTURES = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png" };
        MARKING_HASHES = new String[] { "", "wo_", "wmo", "wdo", "bdo" };
    }
    
    public static class HorseGroupData implements SpawnGroupData {
        public final int variant;
        
        public HorseGroupData(final int integer) {
            this.variant = integer;
        }
    }
}

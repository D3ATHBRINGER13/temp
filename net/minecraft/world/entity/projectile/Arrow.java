package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import com.google.common.collect.Sets;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.Set;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Arrow extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
    private Potion potion;
    private final Set<MobEffectInstance> effects;
    private boolean fixedColor;
    
    public Arrow(final EntityType<? extends Arrow> ais, final Level bhr) {
        super(ais, bhr);
        this.potion = Potions.EMPTY;
        this.effects = (Set<MobEffectInstance>)Sets.newHashSet();
    }
    
    public Arrow(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.ARROW, double2, double3, double4, bhr);
        this.potion = Potions.EMPTY;
        this.effects = (Set<MobEffectInstance>)Sets.newHashSet();
    }
    
    public Arrow(final Level bhr, final LivingEntity aix) {
        super(EntityType.ARROW, aix, bhr);
        this.potion = Potions.EMPTY;
        this.effects = (Set<MobEffectInstance>)Sets.newHashSet();
    }
    
    public void setEffectsFromItem(final ItemStack bcj) {
        if (bcj.getItem() == Items.TIPPED_ARROW) {
            this.potion = PotionUtils.getPotion(bcj);
            final Collection<MobEffectInstance> collection3 = (Collection<MobEffectInstance>)PotionUtils.getCustomEffects(bcj);
            if (!collection3.isEmpty()) {
                for (final MobEffectInstance aii5 : collection3) {
                    this.effects.add(new MobEffectInstance(aii5));
                }
            }
            final int integer4 = getCustomColor(bcj);
            if (integer4 == -1) {
                this.updateColor();
            }
            else {
                this.setFixedColor(integer4);
            }
        }
        else if (bcj.getItem() == Items.ARROW) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.<Integer>set(Arrow.ID_EFFECT_COLOR, -1);
        }
    }
    
    public static int getCustomColor(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        if (id2 != null && id2.contains("CustomPotionColor", 99)) {
            return id2.getInt("CustomPotionColor");
        }
        return -1;
    }
    
    private void updateColor() {
        this.fixedColor = false;
        this.entityData.<Integer>set(Arrow.ID_EFFECT_COLOR, PotionUtils.getColor((Collection<MobEffectInstance>)PotionUtils.getAllEffects(this.potion, (Collection<MobEffectInstance>)this.effects)));
    }
    
    public void addEffect(final MobEffectInstance aii) {
        this.effects.add(aii);
        this.getEntityData().<Integer>set(Arrow.ID_EFFECT_COLOR, PotionUtils.getColor((Collection<MobEffectInstance>)PotionUtils.getAllEffects(this.potion, (Collection<MobEffectInstance>)this.effects)));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Arrow.ID_EFFECT_COLOR, -1);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            }
            else {
                this.makeParticle(2);
            }
        }
        else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.level.broadcastEntityEvent(this, (byte)0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.<Integer>set(Arrow.ID_EFFECT_COLOR, -1);
        }
    }
    
    private void makeParticle(final int integer) {
        final int integer2 = this.getColor();
        if (integer2 == -1 || integer <= 0) {
            return;
        }
        final double double4 = (integer2 >> 16 & 0xFF) / 255.0;
        final double double5 = (integer2 >> 8 & 0xFF) / 255.0;
        final double double6 = (integer2 >> 0 & 0xFF) / 255.0;
        for (int integer3 = 0; integer3 < integer; ++integer3) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), double4, double5, double6);
        }
    }
    
    public int getColor() {
        return this.entityData.<Integer>get(Arrow.ID_EFFECT_COLOR);
    }
    
    private void setFixedColor(final int integer) {
        this.fixedColor = true;
        this.entityData.<Integer>set(Arrow.ID_EFFECT_COLOR, integer);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            id.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }
        if (this.fixedColor) {
            id.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            final ListTag ik3 = new ListTag();
            for (final MobEffectInstance aii5 : this.effects) {
                ik3.add(aii5.save(new CompoundTag()));
            }
            id.put("CustomPotionEffects", (Tag)ik3);
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion(id);
        }
        for (final MobEffectInstance aii4 : PotionUtils.getCustomEffects(id)) {
            this.addEffect(aii4);
        }
        if (id.contains("Color", 99)) {
            this.setFixedColor(id.getInt("Color"));
        }
        else {
            this.updateColor();
        }
    }
    
    @Override
    protected void doPostHurtEffects(final LivingEntity aix) {
        super.doPostHurtEffects(aix);
        for (final MobEffectInstance aii4 : this.potion.getEffects()) {
            aix.addEffect(new MobEffectInstance(aii4.getEffect(), Math.max(aii4.getDuration() / 8, 1), aii4.getAmplifier(), aii4.isAmbient(), aii4.isVisible()));
        }
        if (!this.effects.isEmpty()) {
            for (final MobEffectInstance aii4 : this.effects) {
                aix.addEffect(aii4);
            }
        }
    }
    
    @Override
    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        }
        final ItemStack bcj2 = new ItemStack(Items.TIPPED_ARROW);
        PotionUtils.setPotion(bcj2, this.potion);
        PotionUtils.setCustomEffects(bcj2, (Collection<MobEffectInstance>)this.effects);
        if (this.fixedColor) {
            bcj2.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
        }
        return bcj2;
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 0) {
            final int integer3 = this.getColor();
            if (integer3 != -1) {
                final double double4 = (integer3 >> 16 & 0xFF) / 255.0;
                final double double5 = (integer3 >> 8 & 0xFF) / 255.0;
                final double double6 = (integer3 >> 0 & 0xFF) / 255.0;
                for (int integer4 = 0; integer4 < 20; ++integer4) {
                    this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), double4, double5, double6);
                }
            }
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    static {
        ID_EFFECT_COLOR = SynchedEntityData.<Integer>defineId(Arrow.class, EntityDataSerializers.INT);
    }
}

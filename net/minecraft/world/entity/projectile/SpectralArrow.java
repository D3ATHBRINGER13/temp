package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class SpectralArrow extends AbstractArrow {
    private int duration;
    
    public SpectralArrow(final EntityType<? extends SpectralArrow> ais, final Level bhr) {
        super(ais, bhr);
        this.duration = 200;
    }
    
    public SpectralArrow(final Level bhr, final LivingEntity aix) {
        super(EntityType.SPECTRAL_ARROW, aix, bhr);
        this.duration = 200;
    }
    
    public SpectralArrow(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.SPECTRAL_ARROW, double2, double3, double4, bhr);
        this.duration = 200;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && !this.inGround) {
            this.level.addParticle(ParticleTypes.INSTANT_EFFECT, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }
    
    @Override
    protected void doPostHurtEffects(final LivingEntity aix) {
        super.doPostHurtEffects(aix);
        final MobEffectInstance aii3 = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
        aix.addEffect(aii3);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Duration")) {
            this.duration = id.getInt("Duration");
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("Duration", this.duration);
    }
}

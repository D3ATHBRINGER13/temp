package net.minecraft.world.entity.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class MinecartTNT extends AbstractMinecart {
    private int fuse;
    
    public MinecartTNT(final EntityType<? extends MinecartTNT> ais, final Level bhr) {
        super(ais, bhr);
        this.fuse = -1;
    }
    
    public MinecartTNT(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.TNT_MINECART, bhr, double2, double3, double4);
        this.fuse = -1;
    }
    
    @Override
    public Type getMinecartType() {
        return Type.TNT;
    }
    
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.TNT.defaultBlockState();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        }
        else if (this.fuse == 0) {
            this.explode(Entity.getHorizontalDistanceSqr(this.getDeltaMovement()));
        }
        if (this.horizontalCollision) {
            final double double2 = Entity.getHorizontalDistanceSqr(this.getDeltaMovement());
            if (double2 >= 0.009999999776482582) {
                this.explode(double2);
            }
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        final Entity aio4 = ahx.getDirectEntity();
        if (aio4 instanceof AbstractArrow) {
            final AbstractArrow awk5 = (AbstractArrow)aio4;
            if (awk5.isOnFire()) {
                this.explode(awk5.getDeltaMovement().lengthSqr());
            }
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public void destroy(final DamageSource ahx) {
        final double double3 = Entity.getHorizontalDistanceSqr(this.getDeltaMovement());
        if (ahx.isFire() || ahx.isExplosion() || double3 >= 0.009999999776482582) {
            if (this.fuse < 0) {
                this.primeFuse();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }
            return;
        }
        super.destroy(ahx);
        if (!ahx.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
        }
    }
    
    protected void explode(final double double1) {
        if (!this.level.isClientSide) {
            double double2 = Math.sqrt(double1);
            if (double2 > 5.0) {
                double2 = 5.0;
            }
            this.level.explode(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * double2), Explosion.BlockInteraction.BREAK);
            this.remove();
        }
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
        if (float1 >= 3.0f) {
            final float float3 = float1 / 10.0f;
            this.explode(float3 * float3);
        }
        super.causeFallDamage(float1, float2);
    }
    
    @Override
    public void activateMinecart(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        if (boolean4 && this.fuse < 0) {
            this.primeFuse();
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 10) {
            this.primeFuse();
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public void primeFuse() {
        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)10);
            if (!this.isSilent()) {
                this.level.playSound(null, this.x, this.y, this.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
    
    public int getFuse() {
        return this.fuse;
    }
    
    public boolean isPrimed() {
        return this.fuse > -1;
    }
    
    @Override
    public float getBlockExplosionResistance(final Explosion bhk, final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final FluidState clk, final float float6) {
        if (this.isPrimed() && (bvt.is(BlockTags.RAILS) || bhb.getBlockState(ew.above()).is(BlockTags.RAILS))) {
            return 0.0f;
        }
        return super.getBlockExplosionResistance(bhk, bhb, ew, bvt, clk, float6);
    }
    
    @Override
    public boolean shouldBlockExplode(final Explosion bhk, final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final float float5) {
        return (!this.isPrimed() || (!bvt.is(BlockTags.RAILS) && !bhb.getBlockState(ew.above()).is(BlockTags.RAILS))) && super.shouldBlockExplode(bhk, bhb, ew, bvt, float5);
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("TNTFuse", 99)) {
            this.fuse = id.getInt("TNTFuse");
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("TNTFuse", this.fuse);
    }
}

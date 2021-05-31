package net.minecraft.world.level;

import net.minecraft.world.entity.item.PrimedTnt;
import java.util.Iterator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.entity.LivingEntity;
import com.google.common.collect.Sets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.util.Mth;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import java.util.Random;

public class Explosion {
    private final boolean fire;
    private final BlockInteraction blockInteraction;
    private final Random random;
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    private final Entity source;
    private final float radius;
    private DamageSource damageSource;
    private final List<BlockPos> toBlow;
    private final Map<Player, Vec3> hitPlayers;
    
    public Explosion(final Level bhr, @Nullable final Entity aio, final double double3, final double double4, final double double5, final float float6, final List<BlockPos> list) {
        this(bhr, aio, double3, double4, double5, float6, false, BlockInteraction.DESTROY, list);
    }
    
    public Explosion(final Level bhr, @Nullable final Entity aio, final double double3, final double double4, final double double5, final float float6, final boolean boolean7, final BlockInteraction a, final List<BlockPos> list) {
        this(bhr, aio, double3, double4, double5, float6, boolean7, a);
        this.toBlow.addAll((Collection)list);
    }
    
    public Explosion(final Level bhr, @Nullable final Entity aio, final double double3, final double double4, final double double5, final float float6, final boolean boolean7, final BlockInteraction a) {
        this.random = new Random();
        this.toBlow = (List<BlockPos>)Lists.newArrayList();
        this.hitPlayers = (Map<Player, Vec3>)Maps.newHashMap();
        this.level = bhr;
        this.source = aio;
        this.radius = float6;
        this.x = double3;
        this.y = double4;
        this.z = double5;
        this.fire = boolean7;
        this.blockInteraction = a;
        this.damageSource = DamageSource.explosion(this);
    }
    
    public static float getSeenPercent(final Vec3 csi, final Entity aio) {
        final AABB csc3 = aio.getBoundingBox();
        final double double4 = 1.0 / ((csc3.maxX - csc3.minX) * 2.0 + 1.0);
        final double double5 = 1.0 / ((csc3.maxY - csc3.minY) * 2.0 + 1.0);
        final double double6 = 1.0 / ((csc3.maxZ - csc3.minZ) * 2.0 + 1.0);
        final double double7 = (1.0 - Math.floor(1.0 / double4) * double4) / 2.0;
        final double double8 = (1.0 - Math.floor(1.0 / double6) * double6) / 2.0;
        if (double4 < 0.0 || double5 < 0.0 || double6 < 0.0) {
            return 0.0f;
        }
        int integer14 = 0;
        int integer15 = 0;
        for (float float16 = 0.0f; float16 <= 1.0f; float16 += (float)double4) {
            for (float float17 = 0.0f; float17 <= 1.0f; float17 += (float)double5) {
                for (float float18 = 0.0f; float18 <= 1.0f; float18 += (float)double6) {
                    final double double9 = Mth.lerp(float16, csc3.minX, csc3.maxX);
                    final double double10 = Mth.lerp(float17, csc3.minY, csc3.maxY);
                    final double double11 = Mth.lerp(float18, csc3.minZ, csc3.maxZ);
                    final Vec3 csi2 = new Vec3(double9 + double7, double10, double11 + double8);
                    if (aio.level.clip(new ClipContext(csi2, csi, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, aio)).getType() == HitResult.Type.MISS) {
                        ++integer14;
                    }
                    ++integer15;
                }
            }
        }
        return integer14 / (float)integer15;
    }
    
    public void explode() {
        final Set<BlockPos> set2 = (Set<BlockPos>)Sets.newHashSet();
        final int integer3 = 16;
        for (int integer4 = 0; integer4 < 16; ++integer4) {
            for (int integer5 = 0; integer5 < 16; ++integer5) {
                for (int integer6 = 0; integer6 < 16; ++integer6) {
                    if (integer4 == 0 || integer4 == 15 || integer5 == 0 || integer5 == 15 || integer6 == 0 || integer6 == 15) {
                        double double7 = integer4 / 15.0f * 2.0f - 1.0f;
                        double double8 = integer5 / 15.0f * 2.0f - 1.0f;
                        double double9 = integer6 / 15.0f * 2.0f - 1.0f;
                        final double double10 = Math.sqrt(double7 * double7 + double8 * double8 + double9 * double9);
                        double7 /= double10;
                        double8 /= double10;
                        double9 /= double10;
                        float float15 = this.radius * (0.7f + this.level.random.nextFloat() * 0.6f);
                        double double11 = this.x;
                        double double12 = this.y;
                        double double13 = this.z;
                        final float float16 = 0.3f;
                        while (float15 > 0.0f) {
                            final BlockPos ew23 = new BlockPos(double11, double12, double13);
                            final BlockState bvt24 = this.level.getBlockState(ew23);
                            final FluidState clk25 = this.level.getFluidState(ew23);
                            if (!bvt24.isAir() || !clk25.isEmpty()) {
                                float float17 = Math.max(bvt24.getBlock().getExplosionResistance(), clk25.getExplosionResistance());
                                if (this.source != null) {
                                    float17 = this.source.getBlockExplosionResistance(this, this.level, ew23, bvt24, clk25, float17);
                                }
                                float15 -= (float17 + 0.3f) * 0.3f;
                            }
                            if (float15 > 0.0f && (this.source == null || this.source.shouldBlockExplode(this, this.level, ew23, bvt24, float15))) {
                                set2.add(ew23);
                            }
                            double11 += double7 * 0.30000001192092896;
                            double12 += double8 * 0.30000001192092896;
                            double13 += double9 * 0.30000001192092896;
                            float15 -= 0.22500001f;
                        }
                    }
                }
            }
        }
        this.toBlow.addAll((Collection)set2);
        final float float18 = this.radius * 2.0f;
        int integer5 = Mth.floor(this.x - float18 - 1.0);
        int integer6 = Mth.floor(this.x + float18 + 1.0);
        final int integer7 = Mth.floor(this.y - float18 - 1.0);
        final int integer8 = Mth.floor(this.y + float18 + 1.0);
        final int integer9 = Mth.floor(this.z - float18 - 1.0);
        final int integer10 = Mth.floor(this.z + float18 + 1.0);
        final List<Entity> list11 = this.level.getEntities(this.source, new AABB(integer5, integer7, integer9, integer6, integer8, integer10));
        final Vec3 csi12 = new Vec3(this.x, this.y, this.z);
        for (int integer11 = 0; integer11 < list11.size(); ++integer11) {
            final Entity aio14 = (Entity)list11.get(integer11);
            if (!aio14.ignoreExplosion()) {
                final double double14 = Mth.sqrt(aio14.distanceToSqr(new Vec3(this.x, this.y, this.z))) / float18;
                if (double14 <= 1.0) {
                    double double15 = aio14.x - this.x;
                    double double16 = aio14.y + aio14.getEyeHeight() - this.y;
                    double double17 = aio14.z - this.z;
                    final double double18 = Mth.sqrt(double15 * double15 + double16 * double16 + double17 * double17);
                    if (double18 != 0.0) {
                        double15 /= double18;
                        double16 /= double18;
                        double17 /= double18;
                        final double double19 = getSeenPercent(csi12, aio14);
                        final double double20 = (1.0 - double14) * double19;
                        aio14.hurt(this.getDamageSource(), (float)(int)((double20 * double20 + double20) / 2.0 * 7.0 * float18 + 1.0));
                        double double21 = double20;
                        if (aio14 instanceof LivingEntity) {
                            double21 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)aio14, double20);
                        }
                        aio14.setDeltaMovement(aio14.getDeltaMovement().add(double15 * double21, double16 * double21, double17 * double21));
                        if (aio14 instanceof Player) {
                            final Player awg31 = (Player)aio14;
                            if (!awg31.isSpectator() && (!awg31.isCreative() || !awg31.abilities.flying)) {
                                this.hitPlayers.put(awg31, new Vec3(double15 * double20, double16 * double20, double17 * double20));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void finalizeExplosion(final boolean boolean1) {
        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f);
        final boolean boolean2 = this.blockInteraction != BlockInteraction.NONE;
        if (this.radius < 2.0f || !boolean2) {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        else {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        if (boolean2) {
            for (final BlockPos ew5 : this.toBlow) {
                final BlockState bvt6 = this.level.getBlockState(ew5);
                final Block bmv7 = bvt6.getBlock();
                if (boolean1) {
                    final double double8 = ew5.getX() + this.level.random.nextFloat();
                    final double double9 = ew5.getY() + this.level.random.nextFloat();
                    final double double10 = ew5.getZ() + this.level.random.nextFloat();
                    double double11 = double8 - this.x;
                    double double12 = double9 - this.y;
                    double double13 = double10 - this.z;
                    final double double14 = Mth.sqrt(double11 * double11 + double12 * double12 + double13 * double13);
                    double11 /= double14;
                    double12 /= double14;
                    double13 /= double14;
                    double double15 = 0.5 / (double14 / this.radius + 0.1);
                    double15 *= this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3f;
                    double11 *= double15;
                    double12 *= double15;
                    double13 *= double15;
                    this.level.addParticle(ParticleTypes.POOF, (double8 + this.x) / 2.0, (double9 + this.y) / 2.0, (double10 + this.z) / 2.0, double11, double12, double13);
                    this.level.addParticle(ParticleTypes.SMOKE, double8, double9, double10, double11, double12, double13);
                }
                if (!bvt6.isAir()) {
                    if (bmv7.dropFromExplosion(this) && this.level instanceof ServerLevel) {
                        final BlockEntity btw8 = bmv7.isEntityBlock() ? this.level.getBlockEntity(ew5) : null;
                        final LootContext.Builder a9 = new LootContext.Builder((ServerLevel)this.level).withRandom(this.level.random).<BlockPos>withParameter(LootContextParams.BLOCK_POS, ew5).<ItemStack>withParameter(LootContextParams.TOOL, ItemStack.EMPTY).<BlockEntity>withOptionalParameter(LootContextParams.BLOCK_ENTITY, btw8);
                        if (this.blockInteraction == BlockInteraction.DESTROY) {
                            a9.<Float>withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
                        }
                        Block.dropResources(bvt6, a9);
                    }
                    this.level.setBlock(ew5, Blocks.AIR.defaultBlockState(), 3);
                    bmv7.wasExploded(this.level, ew5, this);
                }
            }
        }
        if (this.fire) {
            for (final BlockPos ew5 : this.toBlow) {
                if (this.level.getBlockState(ew5).isAir() && this.level.getBlockState(ew5.below()).isSolidRender(this.level, ew5.below()) && this.random.nextInt(3) == 0) {
                    this.level.setBlockAndUpdate(ew5, Blocks.FIRE.defaultBlockState());
                }
            }
        }
    }
    
    public DamageSource getDamageSource() {
        return this.damageSource;
    }
    
    public void setDamageSource(final DamageSource ahx) {
        this.damageSource = ahx;
    }
    
    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }
    
    @Nullable
    public LivingEntity getSourceMob() {
        if (this.source == null) {
            return null;
        }
        if (this.source instanceof PrimedTnt) {
            return ((PrimedTnt)this.source).getOwner();
        }
        if (this.source instanceof LivingEntity) {
            return (LivingEntity)this.source;
        }
        return null;
    }
    
    public void clearToBlow() {
        this.toBlow.clear();
    }
    
    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }
    
    public enum BlockInteraction {
        NONE, 
        BREAK, 
        DESTROY;
    }
}

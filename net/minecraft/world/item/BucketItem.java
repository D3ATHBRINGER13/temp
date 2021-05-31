package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FlowingFluid;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class BucketItem extends Item {
    private final Fluid content;
    
    public BucketItem(final Fluid clj, final Properties a) {
        super(a);
        this.content = clj;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final HitResult csf6 = Item.getPlayerPOVHitResult(bhr, awg, (this.content == Fluids.EMPTY) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (csf6.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        if (csf6.getType() != HitResult.Type.BLOCK) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        final BlockHitResult csd7 = (BlockHitResult)csf6;
        final BlockPos ew8 = csd7.getBlockPos();
        if (!bhr.mayInteract(awg, ew8) || !awg.mayUseItemAt(ew8, csd7.getDirection(), bcj5)) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        if (this.content == Fluids.EMPTY) {
            final BlockState bvt9 = bhr.getBlockState(ew8);
            if (bvt9.getBlock() instanceof BucketPickup) {
                final Fluid clj10 = ((BucketPickup)bvt9.getBlock()).takeLiquid(bhr, ew8, bvt9);
                if (clj10 != Fluids.EMPTY) {
                    awg.awardStat(Stats.ITEM_USED.get(this));
                    awg.playSound(clj10.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, 1.0f, 1.0f);
                    final ItemStack bcj6 = this.createResultItem(bcj5, awg, clj10.getBucket());
                    if (!bhr.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)awg, new ItemStack(clj10.getBucket()));
                    }
                    return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj6);
                }
            }
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        final BlockState bvt9 = bhr.getBlockState(ew8);
        final BlockPos ew9 = (bvt9.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER) ? ew8 : csd7.getBlockPos().relative(csd7.getDirection());
        if (this.emptyBucket(awg, bhr, ew9, csd7)) {
            this.checkExtraContent(bhr, bcj5, ew9);
            if (awg instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)awg, ew9, bcj5);
            }
            awg.awardStat(Stats.ITEM_USED.get(this));
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, this.getEmptySuccessItem(bcj5, awg));
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
    
    protected ItemStack getEmptySuccessItem(final ItemStack bcj, final Player awg) {
        if (!awg.abilities.instabuild) {
            return new ItemStack(Items.BUCKET);
        }
        return bcj;
    }
    
    public void checkExtraContent(final Level bhr, final ItemStack bcj, final BlockPos ew) {
    }
    
    private ItemStack createResultItem(final ItemStack bcj, final Player awg, final Item bce) {
        if (awg.abilities.instabuild) {
            return bcj;
        }
        bcj.shrink(1);
        if (bcj.isEmpty()) {
            return new ItemStack(bce);
        }
        if (!awg.inventory.add(new ItemStack(bce))) {
            awg.drop(new ItemStack(bce), false);
        }
        return bcj;
    }
    
    public boolean emptyBucket(@Nullable final Player awg, final Level bhr, final BlockPos ew, @Nullable final BlockHitResult csd) {
        if (!(this.content instanceof FlowingFluid)) {
            return false;
        }
        final BlockState bvt6 = bhr.getBlockState(ew);
        final Material clo7 = bvt6.getMaterial();
        final boolean boolean8 = !clo7.isSolid();
        final boolean boolean9 = clo7.isReplaceable();
        if (bhr.isEmptyBlock(ew) || boolean8 || boolean9 || (bvt6.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)bvt6.getBlock()).canPlaceLiquid(bhr, ew, bvt6, this.content))) {
            if (bhr.dimension.isUltraWarm() && this.content.is(FluidTags.WATER)) {
                final int integer10 = ew.getX();
                final int integer11 = ew.getY();
                final int integer12 = ew.getZ();
                bhr.playSound(awg, ew, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (bhr.random.nextFloat() - bhr.random.nextFloat()) * 0.8f);
                for (int integer13 = 0; integer13 < 8; ++integer13) {
                    bhr.addParticle(ParticleTypes.LARGE_SMOKE, integer10 + Math.random(), integer11 + Math.random(), integer12 + Math.random(), 0.0, 0.0, 0.0);
                }
            }
            else if (bvt6.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
                if (((LiquidBlockContainer)bvt6.getBlock()).placeLiquid(bhr, ew, bvt6, ((FlowingFluid)this.content).getSource(false))) {
                    this.playEmptySound(awg, bhr, ew);
                }
            }
            else {
                if (!bhr.isClientSide && (boolean8 || boolean9) && !clo7.isLiquid()) {
                    bhr.destroyBlock(ew, true);
                }
                this.playEmptySound(awg, bhr, ew);
                bhr.setBlock(ew, this.content.defaultFluidState().createLegacyBlock(), 11);
            }
            return true;
        }
        return csd != null && this.emptyBucket(awg, bhr, csd.getBlockPos().relative(csd.getDirection()), null);
    }
    
    protected void playEmptySound(@Nullable final Player awg, final LevelAccessor bhs, final BlockPos ew) {
        final SoundEvent yo5 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        bhs.playSound(awg, ew, yo5, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}

package net.minecraft.world.level.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlock extends Block {
    public FallingBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        bhs.getBlockTicks().scheduleTick(ew5, this, this.getTickDelay(bhs));
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bhr.isClientSide) {
            this.checkSlide(bhr, ew);
        }
    }
    
    private void checkSlide(final Level bhr, final BlockPos ew) {
        if (!isFree(bhr.getBlockState(ew.below())) || ew.getY() < 0) {
            return;
        }
        if (!bhr.isClientSide) {
            final FallingBlockEntity atw4 = new FallingBlockEntity(bhr, ew.getX() + 0.5, ew.getY(), ew.getZ() + 0.5, bhr.getBlockState(ew));
            this.falling(atw4);
            bhr.addFreshEntity(atw4);
        }
    }
    
    protected void falling(final FallingBlockEntity atw) {
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 2;
    }
    
    public static boolean isFree(final BlockState bvt) {
        final Block bmv2 = bvt.getBlock();
        final Material clo3 = bvt.getMaterial();
        return bvt.isAir() || bmv2 == Blocks.FIRE || clo3.isLiquid() || clo3.isReplaceable();
    }
    
    public void onLand(final Level bhr, final BlockPos ew, final BlockState bvt3, final BlockState bvt4) {
    }
    
    public void onBroken(final Level bhr, final BlockPos ew) {
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (random.nextInt(16) == 0) {
            final BlockPos ew2 = ew.below();
            if (isFree(bhr.getBlockState(ew2))) {
                final double double7 = ew.getX() + random.nextFloat();
                final double double8 = ew.getY() - 0.05;
                final double double9 = ew.getZ() + random.nextFloat();
                bhr.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, bvt), double7, double8, double9, 0.0, 0.0, 0.0);
            }
        }
    }
    
    public int getDustColor(final BlockState bvt) {
        return -16777216;
    }
}

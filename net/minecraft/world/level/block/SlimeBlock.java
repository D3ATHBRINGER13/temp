package net.minecraft.world.level.block;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockLayer;

public class SlimeBlock extends HalfTransparentBlock {
    public SlimeBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        if (aio.isSneaking()) {
            super.fallOn(bhr, ew, aio, float4);
        }
        else {
            aio.causeFallDamage(float4, 0.0f);
        }
    }
    
    @Override
    public void updateEntityAfterFallOn(final BlockGetter bhb, final Entity aio) {
        if (aio.isSneaking()) {
            super.updateEntityAfterFallOn(bhb, aio);
        }
        else {
            final Vec3 csi4 = aio.getDeltaMovement();
            if (csi4.y < 0.0) {
                final double double5 = (aio instanceof LivingEntity) ? 1.0 : 0.8;
                aio.setDeltaMovement(csi4.x, -csi4.y * double5, csi4.z);
            }
        }
    }
    
    @Override
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
        final double double5 = Math.abs(aio.getDeltaMovement().y);
        if (double5 < 0.1 && !aio.isSneaking()) {
            final double double6 = 0.4 + double5 * 0.2;
            aio.setDeltaMovement(aio.getDeltaMovement().multiply(double6, 1.0, double6));
        }
        super.stepOn(bhr, ew, aio);
    }
}

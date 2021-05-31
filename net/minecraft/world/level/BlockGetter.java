package net.minecraft.world.level;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import java.util.function.Function;
import java.util.function.BiFunction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public interface BlockGetter {
    @Nullable
    BlockEntity getBlockEntity(final BlockPos ew);
    
    BlockState getBlockState(final BlockPos ew);
    
    FluidState getFluidState(final BlockPos ew);
    
    default int getLightEmission(final BlockPos ew) {
        return this.getBlockState(ew).getLightEmission();
    }
    
    default int getMaxLightLevel() {
        return 15;
    }
    
    default int getMaxBuildHeight() {
        return 256;
    }
    
    default BlockHitResult clip(final ClipContext bhf) {
        return BlockGetter.<BlockHitResult>traverseBlocks(bhf, (java.util.function.BiFunction<ClipContext, BlockPos, BlockHitResult>)((bhf, ew) -> {
            final BlockState bvt4 = this.getBlockState(ew);
            final FluidState clk5 = this.getFluidState(ew);
            final Vec3 csi6 = bhf.getFrom();
            final Vec3 csi7 = bhf.getTo();
            final VoxelShape ctc8 = bhf.getBlockShape(bvt4, this, ew);
            final BlockHitResult csd9 = this.clipWithInteractionOverride(csi6, csi7, ew, ctc8, bvt4);
            final VoxelShape ctc9 = bhf.getFluidShape(clk5, this, ew);
            final BlockHitResult csd10 = ctc9.clip(csi6, csi7, ew);
            final double double12 = (csd9 == null) ? Double.MAX_VALUE : bhf.getFrom().distanceToSqr(csd9.getLocation());
            final double double13 = (csd10 == null) ? Double.MAX_VALUE : bhf.getFrom().distanceToSqr(csd10.getLocation());
            return (double12 <= double13) ? csd9 : csd10;
        }), (java.util.function.Function<ClipContext, BlockHitResult>)(bhf -> {
            final Vec3 csi2 = bhf.getFrom().subtract(bhf.getTo());
            return BlockHitResult.miss(bhf.getTo(), Direction.getNearest(csi2.x, csi2.y, csi2.z), new BlockPos(bhf.getTo()));
        }));
    }
    
    @Nullable
    default BlockHitResult clipWithInteractionOverride(final Vec3 csi1, final Vec3 csi2, final BlockPos ew, final VoxelShape ctc, final BlockState bvt) {
        final BlockHitResult csd7 = ctc.clip(csi1, csi2, ew);
        if (csd7 != null) {
            final BlockHitResult csd8 = bvt.getInteractionShape(this, ew).clip(csi1, csi2, ew);
            if (csd8 != null && csd8.getLocation().subtract(csi1).lengthSqr() < csd7.getLocation().subtract(csi1).lengthSqr()) {
                return csd7.withDirection(csd8.getDirection());
            }
        }
        return csd7;
    }
    
    default <T> T traverseBlocks(final ClipContext bhf, final BiFunction<ClipContext, BlockPos, T> biFunction, final Function<ClipContext, T> function) {
        final Vec3 csi4 = bhf.getFrom();
        final Vec3 csi5 = bhf.getTo();
        if (csi4.equals(csi5)) {
            return (T)function.apply(bhf);
        }
        final double double6 = Mth.lerp(-1.0E-7, csi5.x, csi4.x);
        final double double7 = Mth.lerp(-1.0E-7, csi5.y, csi4.y);
        final double double8 = Mth.lerp(-1.0E-7, csi5.z, csi4.z);
        final double double9 = Mth.lerp(-1.0E-7, csi4.x, csi5.x);
        final double double10 = Mth.lerp(-1.0E-7, csi4.y, csi5.y);
        final double double11 = Mth.lerp(-1.0E-7, csi4.z, csi5.z);
        int integer18 = Mth.floor(double9);
        int integer19 = Mth.floor(double10);
        int integer20 = Mth.floor(double11);
        final BlockPos.MutableBlockPos a21 = new BlockPos.MutableBlockPos(integer18, integer19, integer20);
        final T object22 = (T)biFunction.apply(bhf, a21);
        if (object22 != null) {
            return object22;
        }
        final double double12 = double6 - double9;
        final double double13 = double7 - double10;
        final double double14 = double8 - double11;
        final int integer21 = Mth.sign(double12);
        final int integer22 = Mth.sign(double13);
        final int integer23 = Mth.sign(double14);
        final double double15 = (integer21 == 0) ? Double.MAX_VALUE : (integer21 / double12);
        final double double16 = (integer22 == 0) ? Double.MAX_VALUE : (integer22 / double13);
        final double double17 = (integer23 == 0) ? Double.MAX_VALUE : (integer23 / double14);
        double double18 = double15 * ((integer21 > 0) ? (1.0 - Mth.frac(double9)) : Mth.frac(double9));
        double double19 = double16 * ((integer22 > 0) ? (1.0 - Mth.frac(double10)) : Mth.frac(double10));
        double double20 = double17 * ((integer23 > 0) ? (1.0 - Mth.frac(double11)) : Mth.frac(double11));
        while (double18 <= 1.0 || double19 <= 1.0 || double20 <= 1.0) {
            if (double18 < double19) {
                if (double18 < double20) {
                    integer18 += integer21;
                    double18 += double15;
                }
                else {
                    integer20 += integer23;
                    double20 += double17;
                }
            }
            else if (double19 < double20) {
                integer19 += integer22;
                double19 += double16;
            }
            else {
                integer20 += integer23;
                double20 += double17;
            }
            final T object23 = (T)biFunction.apply(bhf, a21.set(integer18, integer19, integer20));
            if (object23 != null) {
                return object23;
            }
        }
        return (T)function.apply(bhf);
    }
}

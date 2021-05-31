package net.minecraft.world.entity.ai.util;

import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Position;
import net.minecraft.core.BlockPos;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.PathfinderMob;

public class RandomPos {
    @Nullable
    public static Vec3 getPos(final PathfinderMob aje, final int integer2, final int integer3) {
        return generateRandomPos(aje, integer2, integer3, null);
    }
    
    @Nullable
    public static Vec3 getLandPos(final PathfinderMob aje, final int integer2, final int integer3) {
        return getLandPos(aje, integer2, integer3, (ToDoubleFunction<BlockPos>)aje::getWalkTargetValue);
    }
    
    @Nullable
    public static Vec3 getLandPos(final PathfinderMob aje, final int integer2, final int integer3, final ToDoubleFunction<BlockPos> toDoubleFunction) {
        return generateRandomPos(aje, integer2, integer3, null, false, 0.0, toDoubleFunction);
    }
    
    @Nullable
    public static Vec3 getPosTowards(final PathfinderMob aje, final int integer2, final int integer3, final Vec3 csi) {
        final Vec3 csi2 = csi.subtract(aje.x, aje.y, aje.z);
        return generateRandomPos(aje, integer2, integer3, csi2);
    }
    
    @Nullable
    public static Vec3 getPosTowards(final PathfinderMob aje, final int integer2, final int integer3, final Vec3 csi, final double double5) {
        final Vec3 csi2 = csi.subtract(aje.x, aje.y, aje.z);
        return generateRandomPos(aje, integer2, integer3, csi2, true, double5, (ToDoubleFunction<BlockPos>)aje::getWalkTargetValue);
    }
    
    @Nullable
    public static Vec3 getLandPosAvoid(final PathfinderMob aje, final int integer2, final int integer3, final Vec3 csi) {
        final Vec3 csi2 = new Vec3(aje.x, aje.y, aje.z).subtract(csi);
        return generateRandomPos(aje, integer2, integer3, csi2, false, 1.5707963705062866, (ToDoubleFunction<BlockPos>)aje::getWalkTargetValue);
    }
    
    @Nullable
    public static Vec3 getPosAvoid(final PathfinderMob aje, final int integer2, final int integer3, final Vec3 csi) {
        final Vec3 csi2 = new Vec3(aje.x, aje.y, aje.z).subtract(csi);
        return generateRandomPos(aje, integer2, integer3, csi2);
    }
    
    @Nullable
    private static Vec3 generateRandomPos(final PathfinderMob aje, final int integer2, final int integer3, @Nullable final Vec3 csi) {
        return generateRandomPos(aje, integer2, integer3, csi, true, 1.5707963705062866, (ToDoubleFunction<BlockPos>)aje::getWalkTargetValue);
    }
    
    @Nullable
    private static Vec3 generateRandomPos(final PathfinderMob aje, final int integer2, final int integer3, @Nullable final Vec3 csi, final boolean boolean5, final double double6, final ToDoubleFunction<BlockPos> toDoubleFunction) {
        final PathNavigation app9 = aje.getNavigation();
        final Random random10 = aje.getRandom();
        final boolean boolean6 = aje.hasRestriction() && aje.getRestrictCenter().closerThan(aje.position(), aje.getRestrictRadius() + integer2 + 1.0);
        boolean boolean7 = false;
        double double7 = Double.NEGATIVE_INFINITY;
        BlockPos ew15 = new BlockPos(aje);
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final BlockPos ew16 = getRandomDelta(random10, integer2, integer3, csi, double6);
            if (ew16 != null) {
                int integer5 = ew16.getX();
                final int integer6 = ew16.getY();
                int integer7 = ew16.getZ();
                if (aje.hasRestriction() && integer2 > 1) {
                    final BlockPos ew17 = aje.getRestrictCenter();
                    if (aje.x > ew17.getX()) {
                        integer5 -= random10.nextInt(integer2 / 2);
                    }
                    else {
                        integer5 += random10.nextInt(integer2 / 2);
                    }
                    if (aje.z > ew17.getZ()) {
                        integer7 -= random10.nextInt(integer2 / 2);
                    }
                    else {
                        integer7 += random10.nextInt(integer2 / 2);
                    }
                }
                BlockPos ew17 = new BlockPos(integer5 + aje.x, integer6 + aje.y, integer7 + aje.z);
                if (!boolean6 || aje.isWithinRestriction(ew17)) {
                    if (app9.isStableDestination(ew17)) {
                        if (!boolean5) {
                            ew17 = moveAboveSolid(ew17, aje);
                            if (isWaterDestination(ew17, aje)) {
                                continue;
                            }
                        }
                        final double double8 = toDoubleFunction.applyAsDouble(ew17);
                        if (double8 > double7) {
                            double7 = double8;
                            ew15 = ew17;
                            boolean7 = true;
                        }
                    }
                }
            }
        }
        if (boolean7) {
            return new Vec3(ew15);
        }
        return null;
    }
    
    @Nullable
    private static BlockPos getRandomDelta(final Random random, final int integer2, final int integer3, @Nullable final Vec3 csi, final double double5) {
        if (csi == null || double5 >= 3.141592653589793) {
            final int integer4 = random.nextInt(2 * integer2 + 1) - integer2;
            final int integer5 = random.nextInt(2 * integer3 + 1) - integer3;
            final int integer6 = random.nextInt(2 * integer2 + 1) - integer2;
            return new BlockPos(integer4, integer5, integer6);
        }
        final double double6 = Mth.atan2(csi.z, csi.x) - 1.5707963705062866;
        final double double7 = double6 + (2.0f * random.nextFloat() - 1.0f) * double5;
        final double double8 = Math.sqrt(random.nextDouble()) * Mth.SQRT_OF_TWO * integer2;
        final double double9 = -double8 * Math.sin(double7);
        final double double10 = double8 * Math.cos(double7);
        if (Math.abs(double9) > integer2 || Math.abs(double10) > integer2) {
            return null;
        }
        final int integer7 = random.nextInt(2 * integer3 + 1) - integer3;
        return new BlockPos(double9, integer7, double10);
    }
    
    private static BlockPos moveAboveSolid(final BlockPos ew, final PathfinderMob aje) {
        if (aje.level.getBlockState(ew).getMaterial().isSolid()) {
            BlockPos ew2;
            for (ew2 = ew.above(); ew2.getY() < aje.level.getMaxBuildHeight() && aje.level.getBlockState(ew2).getMaterial().isSolid(); ew2 = ew2.above()) {}
            return ew2;
        }
        return ew;
    }
    
    private static boolean isWaterDestination(final BlockPos ew, final PathfinderMob aje) {
        return aje.level.getFluidState(ew).is(FluidTags.WATER);
    }
}

package net.minecraft.world.entity.projectile;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.function.Predicate;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class ProjectileUtil {
    public static HitResult forwardsRaycast(final Entity aio1, final boolean boolean2, final boolean boolean3, @Nullable final Entity aio4, final ClipContext.Block a) {
        return forwardsRaycast(aio1, boolean2, boolean3, aio4, a, true, (Predicate<Entity>)(aio3 -> !aio3.isSpectator() && aio3.isPickable() && (boolean3 || !aio3.is(aio4)) && !aio3.noPhysics), aio1.getBoundingBox().expandTowards(aio1.getDeltaMovement()).inflate(1.0));
    }
    
    public static HitResult getHitResult(final Entity aio, final AABB csc, final Predicate<Entity> predicate, final ClipContext.Block a, final boolean boolean5) {
        return forwardsRaycast(aio, boolean5, false, null, a, false, predicate, csc);
    }
    
    @Nullable
    public static EntityHitResult getHitResult(final Level bhr, final Entity aio, final Vec3 csi3, final Vec3 csi4, final AABB csc, final Predicate<Entity> predicate) {
        return getHitResult(bhr, aio, csi3, csi4, csc, predicate, Double.MAX_VALUE);
    }
    
    private static HitResult forwardsRaycast(final Entity aio1, final boolean boolean2, final boolean boolean3, @Nullable final Entity aio4, final ClipContext.Block a, final boolean boolean6, final Predicate<Entity> predicate, final AABB csc) {
        final double double9 = aio1.x;
        final double double10 = aio1.y;
        final double double11 = aio1.z;
        final Vec3 csi15 = aio1.getDeltaMovement();
        final Level bhr16 = aio1.level;
        final Vec3 csi16 = new Vec3(double9, double10, double11);
        if (boolean6 && !bhr16.noCollision(aio1, aio1.getBoundingBox(), (Set<Entity>)((boolean3 || aio4 == null) ? ImmutableSet.of() : getIgnoredEntities(aio4)))) {
            return new BlockHitResult(csi16, Direction.getNearest(csi15.x, csi15.y, csi15.z), new BlockPos(aio1), false);
        }
        Vec3 csi17 = csi16.add(csi15);
        HitResult csf19 = bhr16.clip(new ClipContext(csi16, csi17, a, ClipContext.Fluid.NONE, aio1));
        if (boolean2) {
            if (csf19.getType() != HitResult.Type.MISS) {
                csi17 = csf19.getLocation();
            }
            final HitResult csf20 = getHitResult(bhr16, aio1, csi16, csi17, csc, predicate);
            if (csf20 != null) {
                csf19 = csf20;
            }
        }
        return csf19;
    }
    
    @Nullable
    public static EntityHitResult getEntityHitResult(final Entity aio, final Vec3 csi2, final Vec3 csi3, final AABB csc, final Predicate<Entity> predicate, final double double6) {
        final Level bhr8 = aio.level;
        double double7 = double6;
        Entity aio2 = null;
        Vec3 csi4 = null;
        for (final Entity aio3 : bhr8.getEntities(aio, csc, predicate)) {
            final AABB csc2 = aio3.getBoundingBox().inflate(aio3.getPickRadius());
            final Optional<Vec3> optional16 = csc2.clip(csi2, csi3);
            if (csc2.contains(csi2)) {
                if (double7 < 0.0) {
                    continue;
                }
                aio2 = aio3;
                csi4 = (Vec3)optional16.orElse(csi2);
                double7 = 0.0;
            }
            else {
                if (!optional16.isPresent()) {
                    continue;
                }
                final Vec3 csi5 = (Vec3)optional16.get();
                final double double8 = csi2.distanceToSqr(csi5);
                if (double8 >= double7 && double7 != 0.0) {
                    continue;
                }
                if (aio3.getRootVehicle() == aio.getRootVehicle()) {
                    if (double7 != 0.0) {
                        continue;
                    }
                    aio2 = aio3;
                    csi4 = csi5;
                }
                else {
                    aio2 = aio3;
                    csi4 = csi5;
                    double7 = double8;
                }
            }
        }
        if (aio2 == null) {
            return null;
        }
        return new EntityHitResult(aio2, csi4);
    }
    
    @Nullable
    public static EntityHitResult getHitResult(final Level bhr, final Entity aio, final Vec3 csi3, final Vec3 csi4, final AABB csc, final Predicate<Entity> predicate, final double double7) {
        double double8 = double7;
        Entity aio2 = null;
        for (final Entity aio3 : bhr.getEntities(aio, csc, predicate)) {
            final AABB csc2 = aio3.getBoundingBox().inflate(0.30000001192092896);
            final Optional<Vec3> optional15 = csc2.clip(csi3, csi4);
            if (optional15.isPresent()) {
                final double double9 = csi3.distanceToSqr((Vec3)optional15.get());
                if (double9 >= double8) {
                    continue;
                }
                aio2 = aio3;
                double8 = double9;
            }
        }
        if (aio2 == null) {
            return null;
        }
        return new EntityHitResult(aio2);
    }
    
    private static Set<Entity> getIgnoredEntities(final Entity aio) {
        final Entity aio2 = aio.getVehicle();
        return (Set<Entity>)((aio2 != null) ? ImmutableSet.of(aio, aio2) : ImmutableSet.of(aio));
    }
    
    public static final void rotateTowardsMovement(final Entity aio, final float float2) {
        final Vec3 csi3 = aio.getDeltaMovement();
        final float float3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi3));
        aio.yRot = (float)(Mth.atan2(csi3.z, csi3.x) * 57.2957763671875) + 90.0f;
        aio.xRot = (float)(Mth.atan2(float3, csi3.y) * 57.2957763671875) - 90.0f;
        while (aio.xRot - aio.xRotO < -180.0f) {
            aio.xRotO -= 360.0f;
        }
        while (aio.xRot - aio.xRotO >= 180.0f) {
            aio.xRotO += 360.0f;
        }
        while (aio.yRot - aio.yRotO < -180.0f) {
            aio.yRotO -= 360.0f;
        }
        while (aio.yRot - aio.yRotO >= 180.0f) {
            aio.yRotO += 360.0f;
        }
        aio.xRot = Mth.lerp(float2, aio.xRotO, aio.xRot);
        aio.yRot = Mth.lerp(float2, aio.yRotO, aio.yRot);
    }
    
    public static InteractionHand getWeaponHoldingHand(final LivingEntity aix, final Item bce) {
        return (aix.getMainHandItem().getItem() == bce) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }
    
    public static AbstractArrow getMobArrow(final LivingEntity aix, final ItemStack bcj, final float float3) {
        final ArrowItem bah4 = (ArrowItem)((bcj.getItem() instanceof ArrowItem) ? bcj.getItem() : Items.ARROW);
        final AbstractArrow awk5 = bah4.createArrow(aix.level, bcj, aix);
        awk5.setEnchantmentEffectsFromEntity(aix, float3);
        if (bcj.getItem() == Items.TIPPED_ARROW && awk5 instanceof Arrow) {
            ((Arrow)awk5).setEffectsFromItem(bcj);
        }
        return awk5;
    }
}

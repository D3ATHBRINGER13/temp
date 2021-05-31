package net.minecraft.world.item;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;

public class BoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE;
    private final Boat.Type type;
    
    public BoatItem(final Boat.Type b, final Properties a) {
        super(a);
        this.type = b;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final HitResult csf6 = Item.getPlayerPOVHitResult(bhr, awg, ClipContext.Fluid.ANY);
        if (csf6.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        final Vec3 csi7 = awg.getViewVector(1.0f);
        final double double8 = 5.0;
        final List<Entity> list10 = bhr.getEntities(awg, awg.getBoundingBox().expandTowards(csi7.scale(5.0)).inflate(1.0), BoatItem.ENTITY_PREDICATE);
        if (!list10.isEmpty()) {
            final Vec3 csi8 = awg.getEyePosition(1.0f);
            for (final Entity aio13 : list10) {
                final AABB csc14 = aio13.getBoundingBox().inflate(aio13.getPickRadius());
                if (csc14.contains(csi8)) {
                    return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
                }
            }
        }
        if (csf6.getType() != HitResult.Type.BLOCK) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        final Boat axw11 = new Boat(bhr, csf6.getLocation().x, csf6.getLocation().y, csf6.getLocation().z);
        axw11.setType(this.type);
        axw11.yRot = awg.yRot;
        if (!bhr.noCollision(axw11, axw11.getBoundingBox().inflate(-0.1))) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        if (!bhr.isClientSide) {
            bhr.addFreshEntity(axw11);
        }
        if (!awg.abilities.instabuild) {
            bcj5.shrink(1);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    static {
        ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    }
}

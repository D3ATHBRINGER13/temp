package net.minecraft.world.item;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.function.Predicate;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BottleItem extends Item {
    public BottleItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final List<AreaEffectCloud> list5 = bhr.<AreaEffectCloud>getEntitiesOfClass((java.lang.Class<? extends AreaEffectCloud>)AreaEffectCloud.class, awg.getBoundingBox().inflate(2.0), (java.util.function.Predicate<? super AreaEffectCloud>)(ain -> ain != null && ain.isAlive() && ain.getOwner() instanceof EnderDragon));
        final ItemStack bcj6 = awg.getItemInHand(ahi);
        if (!list5.isEmpty()) {
            final AreaEffectCloud ain7 = (AreaEffectCloud)list5.get(0);
            ain7.setRadius(ain7.getRadius() - 0.5f);
            bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0f, 1.0f);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, this.turnBottleIntoItem(bcj6, awg, new ItemStack(Items.DRAGON_BREATH)));
        }
        final HitResult csf7 = Item.getPlayerPOVHitResult(bhr, awg, ClipContext.Fluid.SOURCE_ONLY);
        if (csf7.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj6);
        }
        if (csf7.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew8 = ((BlockHitResult)csf7).getBlockPos();
            if (!bhr.mayInteract(awg, ew8)) {
                return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj6);
            }
            if (bhr.getFluidState(ew8).is(FluidTags.WATER)) {
                bhr.playSound(awg, awg.x, awg.y, awg.z, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0f, 1.0f);
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, this.turnBottleIntoItem(bcj6, awg, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj6);
    }
    
    protected ItemStack turnBottleIntoItem(final ItemStack bcj1, final Player awg, final ItemStack bcj3) {
        bcj1.shrink(1);
        awg.awardStat(Stats.ITEM_USED.get(this));
        if (bcj1.isEmpty()) {
            return bcj3;
        }
        if (!awg.inventory.add(bcj3)) {
            awg.drop(bcj3, false);
        }
        return bcj1;
    }
}

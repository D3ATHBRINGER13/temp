package net.minecraft.world.item;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.ItemLike;
import java.util.function.Consumer;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CarrotOnAStickItem extends Item {
    public CarrotOnAStickItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (bhr.isClientSide) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        if (awg.isPassenger() && awg.getVehicle() instanceof Pig) {
            final Pig arn6 = (Pig)awg.getVehicle();
            if (bcj5.getMaxDamage() - bcj5.getDamageValue() >= 7 && arn6.boost()) {
                bcj5.<Player>hurtAndBreak(7, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
                if (bcj5.isEmpty()) {
                    final ItemStack bcj6 = new ItemStack(Items.FISHING_ROD);
                    bcj6.setTag(bcj5.getTag());
                    return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj6);
                }
                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
            }
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
    }
}

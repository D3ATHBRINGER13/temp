package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends PotionItem {
    public LingeringPotionItem(final Properties a) {
        super(a);
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        PotionUtils.addPotionTooltip(bcj, list, 0.25f);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final ItemStack bcj6 = awg.abilities.instabuild ? bcj5.copy() : bcj5.split(1);
        bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (LingeringPotionItem.random.nextFloat() * 0.4f + 0.8f));
        if (!bhr.isClientSide) {
            final ThrownPotion axg7 = new ThrownPotion(bhr, awg);
            axg7.setItem(bcj6);
            axg7.shootFromRotation(awg, awg.xRot, awg.yRot, -20.0f, 0.5f, 1.0f);
            bhr.addFreshEntity(axg7);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}

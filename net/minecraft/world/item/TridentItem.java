package net.minecraft.world.item;

import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MoverType;
import net.minecraft.util.Mth;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import java.util.function.Consumer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;

public class TridentItem extends Item {
    public TridentItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("throwing"), (bcj, bhr, aix) -> (aix != null && aix.isUsingItem() && aix.getUseItem() == bcj) ? 1.0f : 0.0f);
    }
    
    @Override
    public boolean canAttackBlock(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        return !awg.isCreative();
    }
    
    @Override
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return UseAnim.SPEAR;
    }
    
    @Override
    public int getUseDuration(final ItemStack bcj) {
        return 72000;
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return false;
    }
    
    @Override
    public void releaseUsing(final ItemStack bcj, final Level bhr, final LivingEntity aix, final int integer) {
        if (!(aix instanceof Player)) {
            return;
        }
        final Player awg6 = (Player)aix;
        final int integer2 = this.getUseDuration(bcj) - integer;
        if (integer2 < 10) {
            return;
        }
        final int integer3 = EnchantmentHelper.getRiptide(bcj);
        if (integer3 > 0 && !awg6.isInWaterOrRain()) {
            return;
        }
        if (!bhr.isClientSide) {
            bcj.<Player>hurtAndBreak(1, awg6, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(aix.getUsedItemHand())));
            if (integer3 == 0) {
                final ThrownTrident axh9 = new ThrownTrident(bhr, awg6, bcj);
                axh9.shootFromRotation(awg6, awg6.xRot, awg6.yRot, 0.0f, 2.5f + integer3 * 0.5f, 1.0f);
                if (awg6.abilities.instabuild) {
                    axh9.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                bhr.addFreshEntity(axh9);
                bhr.playSound(null, axh9, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (!awg6.abilities.instabuild) {
                    awg6.inventory.removeItem(bcj);
                }
            }
        }
        awg6.awardStat(Stats.ITEM_USED.get(this));
        if (integer3 > 0) {
            final float float9 = awg6.yRot;
            final float float10 = awg6.xRot;
            float float11 = -Mth.sin(float9 * 0.017453292f) * Mth.cos(float10 * 0.017453292f);
            float float12 = -Mth.sin(float10 * 0.017453292f);
            float float13 = Mth.cos(float9 * 0.017453292f) * Mth.cos(float10 * 0.017453292f);
            final float float14 = Mth.sqrt(float11 * float11 + float12 * float12 + float13 * float13);
            final float float15 = 3.0f * ((1.0f + integer3) / 4.0f);
            float11 *= float15 / float14;
            float12 *= float15 / float14;
            float13 *= float15 / float14;
            awg6.push(float11, float12, float13);
            awg6.startAutoSpinAttack(20);
            if (awg6.onGround) {
                final float float16 = 1.1999999f;
                awg6.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
            }
            SoundEvent yo16;
            if (integer3 >= 3) {
                yo16 = SoundEvents.TRIDENT_RIPTIDE_3;
            }
            else if (integer3 == 2) {
                yo16 = SoundEvents.TRIDENT_RIPTIDE_2;
            }
            else {
                yo16 = SoundEvents.TRIDENT_RIPTIDE_1;
            }
            bhr.playSound(null, awg6, yo16, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (bcj5.getDamageValue() >= bcj5.getMaxDamage()) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        if (EnchantmentHelper.getRiptide(bcj5) > 0 && !awg.isInWaterOrRain()) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        awg.startUsingItem(ahi);
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    @Override
    public boolean hurtEnemy(final ItemStack bcj, final LivingEntity aix2, final LivingEntity aix3) {
        bcj.<LivingEntity>hurtAndBreak(1, aix3, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        return true;
    }
    
    @Override
    public boolean mineBlock(final ItemStack bcj, final Level bhr, final BlockState bvt, final BlockPos ew, final LivingEntity aix) {
        if (bvt.getDestroySpeed(bhr, ew) != 0.0) {
            bcj.<LivingEntity>hurtAndBreak(2, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        }
        return true;
    }
    
    @Override
    public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot ait) {
        final Multimap<String, AttributeModifier> multimap3 = super.getDefaultAttributeModifiers(ait);
        if (ait == EquipmentSlot.MAINHAND) {
            multimap3.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(TridentItem.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0, AttributeModifier.Operation.ADDITION));
            multimap3.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(TridentItem.BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9000000953674316, AttributeModifier.Operation.ADDITION));
        }
        return multimap3;
    }
    
    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

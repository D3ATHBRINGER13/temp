package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import java.util.EnumSet;

public class RangedCrossbowAttackGoal<T extends Monster> extends Goal {
    private final T mob;
    private CrossbowState crossbowState;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    
    public RangedCrossbowAttackGoal(final T aus, final double double2, final float float3) {
        this.crossbowState = CrossbowState.UNCHARGED;
        this.mob = aus;
        this.speedModifier = double2;
        this.attackRadiusSqr = float3 * float3;
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow();
    }
    
    private boolean isHoldingCrossbow() {
        return ((Mob)this.mob).isHolding(Items.CROSSBOW);
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !((Mob)this.mob).getNavigation().isDone()) && this.isHoldingCrossbow();
    }
    
    private boolean isValidTarget() {
        return ((Mob)this.mob).getTarget() != null && ((Mob)this.mob).getTarget().isAlive();
    }
    
    @Override
    public void stop() {
        super.stop();
        ((Mob)this.mob).setAggressive(false);
        ((Mob)this.mob).setTarget(null);
        this.seeTime = 0;
        if (((LivingEntity)this.mob).isUsingItem()) {
            ((LivingEntity)this.mob).stopUsingItem();
            ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
            CrossbowItem.setCharged(((LivingEntity)this.mob).getUseItem(), false);
        }
    }
    
    @Override
    public void tick() {
        final LivingEntity aix2 = ((Mob)this.mob).getTarget();
        if (aix2 == null) {
            return;
        }
        final boolean boolean3 = ((Mob)this.mob).getSensing().canSee(aix2);
        final boolean boolean4 = this.seeTime > 0;
        if (boolean3 != boolean4) {
            this.seeTime = 0;
        }
        if (boolean3) {
            ++this.seeTime;
        }
        else {
            --this.seeTime;
        }
        final double double5 = ((Entity)this.mob).distanceToSqr(aix2);
        final boolean boolean5 = (double5 > this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
        if (boolean5) {
            ((Mob)this.mob).getNavigation().moveTo(aix2, this.canRun() ? this.speedModifier : (this.speedModifier * 0.5));
        }
        else {
            ((Mob)this.mob).getNavigation().stop();
        }
        ((Mob)this.mob).getLookControl().setLookAt(aix2, 30.0f, 30.0f);
        if (this.crossbowState == CrossbowState.UNCHARGED) {
            if (!boolean5) {
                ((LivingEntity)this.mob).startUsingItem(ProjectileUtil.getWeaponHoldingHand((LivingEntity)this.mob, Items.CROSSBOW));
                this.crossbowState = CrossbowState.CHARGING;
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(true);
            }
        }
        else if (this.crossbowState == CrossbowState.CHARGING) {
            if (!((LivingEntity)this.mob).isUsingItem()) {
                this.crossbowState = CrossbowState.UNCHARGED;
            }
            final int integer8 = ((LivingEntity)this.mob).getTicksUsingItem();
            final ItemStack bcj9 = ((LivingEntity)this.mob).getUseItem();
            if (integer8 >= CrossbowItem.getChargeDuration(bcj9)) {
                ((LivingEntity)this.mob).releaseUsingItem();
                this.crossbowState = CrossbowState.CHARGED;
                this.attackDelay = 20 + ((LivingEntity)this.mob).getRandom().nextInt(20);
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
            }
        }
        else if (this.crossbowState == CrossbowState.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
                this.crossbowState = CrossbowState.READY_TO_ATTACK;
            }
        }
        else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && boolean3) {
            ((RangedAttackMob)this.mob).performRangedAttack(aix2, 1.0f);
            final ItemStack bcj10 = ((LivingEntity)this.mob).getItemInHand(ProjectileUtil.getWeaponHoldingHand((LivingEntity)this.mob, Items.CROSSBOW));
            CrossbowItem.setCharged(bcj10, false);
            this.crossbowState = CrossbowState.UNCHARGED;
        }
    }
    
    private boolean canRun() {
        return this.crossbowState == CrossbowState.UNCHARGED;
    }
    
    enum CrossbowState {
        UNCHARGED, 
        CHARGING, 
        CHARGED, 
        READY_TO_ATTACK;
    }
}

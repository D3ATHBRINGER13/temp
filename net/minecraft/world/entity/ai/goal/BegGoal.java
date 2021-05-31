package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Wolf;

public class BegGoal extends Goal {
    private final Wolf wolf;
    private Player player;
    private final Level level;
    private final float lookDistance;
    private int lookTime;
    private final TargetingConditions begTargeting;
    
    public BegGoal(final Wolf arz, final float float2) {
        this.wolf = arz;
        this.level = arz.level;
        this.lookDistance = float2;
        this.begTargeting = new TargetingConditions().range(float2).allowInvulnerable().allowSameTeam().allowNonAttackable();
        this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
        return this.player != null && this.playerHoldingInteresting(this.player);
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.player.isAlive() && this.wolf.distanceToSqr(this.player) <= this.lookDistance * this.lookDistance && this.lookTime > 0 && this.playerHoldingInteresting(this.player);
    }
    
    @Override
    public void start() {
        this.wolf.setIsInterested(true);
        this.lookTime = 40 + this.wolf.getRandom().nextInt(40);
    }
    
    @Override
    public void stop() {
        this.wolf.setIsInterested(false);
        this.player = null;
    }
    
    @Override
    public void tick() {
        this.wolf.getLookControl().setLookAt(this.player.x, this.player.y + this.player.getEyeHeight(), this.player.z, 10.0f, (float)this.wolf.getMaxHeadXRot());
        --this.lookTime;
    }
    
    private boolean playerHoldingInteresting(final Player awg) {
        for (final InteractionHand ahi6 : InteractionHand.values()) {
            final ItemStack bcj7 = awg.getItemInHand(ahi6);
            if (this.wolf.isTame() && bcj7.getItem() == Items.BONE) {
                return true;
            }
            if (this.wolf.isFood(bcj7)) {
                return true;
            }
        }
        return false;
    }
}

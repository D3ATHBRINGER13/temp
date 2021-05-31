package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;

public class CombatTracker {
    private final List<CombatEntry> entries;
    private final LivingEntity mob;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    private String nextLocation;
    
    public CombatTracker(final LivingEntity aix) {
        this.entries = (List<CombatEntry>)Lists.newArrayList();
        this.mob = aix;
    }
    
    public void prepareForDamage() {
        this.resetPreparedStatus();
        if (this.mob.onLadder()) {
            final Block bmv2 = this.mob.level.getBlockState(new BlockPos(this.mob.x, this.mob.getBoundingBox().minY, this.mob.z)).getBlock();
            if (bmv2 == Blocks.LADDER) {
                this.nextLocation = "ladder";
            }
            else if (bmv2 == Blocks.VINE) {
                this.nextLocation = "vines";
            }
        }
        else if (this.mob.isInWater()) {
            this.nextLocation = "water";
        }
    }
    
    public void recordDamage(final DamageSource ahx, final float float2, final float float3) {
        this.recheckStatus();
        this.prepareForDamage();
        final CombatEntry ahu5 = new CombatEntry(ahx, this.mob.tickCount, float2, float3, this.nextLocation, this.mob.fallDistance);
        this.entries.add(ahu5);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (ahu5.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
            this.inCombat = true;
            this.combatStartTime = this.mob.tickCount;
            this.combatEndTime = this.combatStartTime;
            this.mob.onEnterCombat();
        }
    }
    
    public Component getDeathMessage() {
        if (this.entries.isEmpty()) {
            return new TranslatableComponent("death.attack.generic", new Object[] { this.mob.getDisplayName() });
        }
        final CombatEntry ahu2 = this.getMostSignificantFall();
        final CombatEntry ahu3 = (CombatEntry)this.entries.get(this.entries.size() - 1);
        final Component jo5 = ahu3.getAttackerName();
        final Entity aio6 = ahu3.getSource().getEntity();
        Component jo7;
        if (ahu2 != null && ahu3.getSource() == DamageSource.FALL) {
            final Component jo6 = ahu2.getAttackerName();
            if (ahu2.getSource() == DamageSource.FALL || ahu2.getSource() == DamageSource.OUT_OF_WORLD) {
                jo7 = new TranslatableComponent("death.fell.accident." + this.getFallLocation(ahu2), new Object[] { this.mob.getDisplayName() });
            }
            else if (jo6 != null && (jo5 == null || !jo6.equals(jo5))) {
                final Entity aio7 = ahu2.getSource().getEntity();
                final ItemStack bcj9 = (aio7 instanceof LivingEntity) ? ((LivingEntity)aio7).getMainHandItem() : ItemStack.EMPTY;
                if (!bcj9.isEmpty() && bcj9.hasCustomHoverName()) {
                    jo7 = new TranslatableComponent("death.fell.assist.item", new Object[] { this.mob.getDisplayName(), jo6, bcj9.getDisplayName() });
                }
                else {
                    jo7 = new TranslatableComponent("death.fell.assist", new Object[] { this.mob.getDisplayName(), jo6 });
                }
            }
            else if (jo5 != null) {
                final ItemStack bcj10 = (aio6 instanceof LivingEntity) ? ((LivingEntity)aio6).getMainHandItem() : ItemStack.EMPTY;
                if (!bcj10.isEmpty() && bcj10.hasCustomHoverName()) {
                    jo7 = new TranslatableComponent("death.fell.finish.item", new Object[] { this.mob.getDisplayName(), jo5, bcj10.getDisplayName() });
                }
                else {
                    jo7 = new TranslatableComponent("death.fell.finish", new Object[] { this.mob.getDisplayName(), jo5 });
                }
            }
            else {
                jo7 = new TranslatableComponent("death.fell.killer", new Object[] { this.mob.getDisplayName() });
            }
        }
        else {
            jo7 = ahu3.getSource().getLocalizedDeathMessage(this.mob);
        }
        return jo7;
    }
    
    @Nullable
    public LivingEntity getKiller() {
        LivingEntity aix2 = null;
        Player awg3 = null;
        float float4 = 0.0f;
        float float5 = 0.0f;
        for (final CombatEntry ahu7 : this.entries) {
            if (ahu7.getSource().getEntity() instanceof Player && (awg3 == null || ahu7.getDamage() > float5)) {
                float5 = ahu7.getDamage();
                awg3 = (Player)ahu7.getSource().getEntity();
            }
            if (ahu7.getSource().getEntity() instanceof LivingEntity && (aix2 == null || ahu7.getDamage() > float4)) {
                float4 = ahu7.getDamage();
                aix2 = (LivingEntity)ahu7.getSource().getEntity();
            }
        }
        if (awg3 != null && float5 >= float4 / 3.0f) {
            return awg3;
        }
        return aix2;
    }
    
    @Nullable
    private CombatEntry getMostSignificantFall() {
        CombatEntry ahu2 = null;
        CombatEntry ahu3 = null;
        float float4 = 0.0f;
        float float5 = 0.0f;
        for (int integer6 = 0; integer6 < this.entries.size(); ++integer6) {
            final CombatEntry ahu4 = (CombatEntry)this.entries.get(integer6);
            final CombatEntry ahu5 = (integer6 > 0) ? ((CombatEntry)this.entries.get(integer6 - 1)) : null;
            if ((ahu4.getSource() == DamageSource.FALL || ahu4.getSource() == DamageSource.OUT_OF_WORLD) && ahu4.getFallDistance() > 0.0f && (ahu2 == null || ahu4.getFallDistance() > float5)) {
                if (integer6 > 0) {
                    ahu2 = ahu5;
                }
                else {
                    ahu2 = ahu4;
                }
                float5 = ahu4.getFallDistance();
            }
            if (ahu4.getLocation() != null && (ahu3 == null || ahu4.getDamage() > float4)) {
                ahu3 = ahu4;
                float4 = ahu4.getDamage();
            }
        }
        if (float5 > 5.0f && ahu2 != null) {
            return ahu2;
        }
        if (float4 > 5.0f && ahu3 != null) {
            return ahu3;
        }
        return null;
    }
    
    private String getFallLocation(final CombatEntry ahu) {
        return (ahu.getLocation() == null) ? "generic" : ahu.getLocation();
    }
    
    public int getCombatDuration() {
        if (this.inCombat) {
            return this.mob.tickCount - this.combatStartTime;
        }
        return this.combatEndTime - this.combatStartTime;
    }
    
    private void resetPreparedStatus() {
        this.nextLocation = null;
    }
    
    public void recheckStatus() {
        final int integer2 = this.inCombat ? 300 : 100;
        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > integer2)) {
            final boolean boolean3 = this.inCombat;
            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if (boolean3) {
                this.mob.onLeaveCombat();
            }
            this.entries.clear();
        }
    }
    
    public LivingEntity getMob() {
        return this.mob;
    }
}

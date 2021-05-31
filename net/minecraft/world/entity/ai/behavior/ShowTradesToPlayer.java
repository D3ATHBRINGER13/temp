package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.Lists;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.Villager;

public class ShowTradesToPlayer extends Behavior<Villager> {
    @Nullable
    private ItemStack playerItemStack;
    private final List<ItemStack> displayItems;
    private int cycleCounter;
    private int displayIndex;
    private int lookTime;
    
    public ShowTradesToPlayer(final int integer1, final int integer2) {
        super((Map)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), integer1, integer2);
        this.displayItems = (List<ItemStack>)Lists.newArrayList();
    }
    
    public boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        final Brain<?> ajm4 = avt.getBrain();
        if (!ajm4.<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        final LivingEntity aix5 = (LivingEntity)ajm4.<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return aix5.getType() == EntityType.PLAYER && avt.isAlive() && aix5.isAlive() && !avt.isBaby() && avt.distanceToSqr(aix5) <= 17.0;
    }
    
    public boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.checkExtraStartConditions(vk, avt) && this.lookTime > 0 && avt.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }
    
    public void start(final ServerLevel vk, final Villager avt, final long long3) {
        super.start(vk, avt, long3);
        this.lookAtTarget(avt);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }
    
    public void tick(final ServerLevel vk, final Villager avt, final long long3) {
        final LivingEntity aix6 = this.lookAtTarget(avt);
        this.findItemsToDisplay(aix6, avt);
        if (!this.displayItems.isEmpty()) {
            this.displayCyclingItems(avt);
        }
        else {
            avt.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.lookTime = Math.min(this.lookTime, 40);
        }
        --this.lookTime;
    }
    
    public void tick(final ServerLevel vk, final Villager avt, final long long3) {
        super.stop(vk, avt, long3);
        avt.getBrain().<LivingEntity>eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        avt.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.playerItemStack = null;
    }
    
    private void findItemsToDisplay(final LivingEntity aix, final Villager avt) {
        boolean boolean4 = false;
        final ItemStack bcj5 = aix.getMainHandItem();
        if (this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, bcj5)) {
            this.playerItemStack = bcj5;
            boolean4 = true;
            this.displayItems.clear();
        }
        if (boolean4 && !this.playerItemStack.isEmpty()) {
            this.updateDisplayItems(avt);
            if (!this.displayItems.isEmpty()) {
                this.lookTime = 900;
                this.displayFirstItem(avt);
            }
        }
    }
    
    private void displayFirstItem(final Villager avt) {
        avt.setItemSlot(EquipmentSlot.MAINHAND, (ItemStack)this.displayItems.get(0));
    }
    
    private void updateDisplayItems(final Villager avt) {
        for (final MerchantOffer bgu4 : avt.getOffers()) {
            if (!bgu4.isOutOfStock() && this.playerItemStackMatchesCostOfOffer(bgu4)) {
                this.displayItems.add(bgu4.getResult());
            }
        }
    }
    
    private boolean playerItemStackMatchesCostOfOffer(final MerchantOffer bgu) {
        return ItemStack.isSame(this.playerItemStack, bgu.getCostA()) || ItemStack.isSame(this.playerItemStack, bgu.getCostB());
    }
    
    private LivingEntity lookAtTarget(final Villager avt) {
        final Brain<?> ajm3 = avt.getBrain();
        final LivingEntity aix4 = (LivingEntity)ajm3.<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        ajm3.<EntityPosWrapper>setMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix4));
        return aix4;
    }
    
    private void displayCyclingItems(final Villager avt) {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
            ++this.displayIndex;
            this.cycleCounter = 0;
            if (this.displayIndex > this.displayItems.size() - 1) {
                this.displayIndex = 0;
            }
            avt.setItemSlot(EquipmentSlot.MAINHAND, (ItemStack)this.displayItems.get(this.displayIndex));
        }
    }
}

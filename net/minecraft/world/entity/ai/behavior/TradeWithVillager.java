package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import java.util.stream.Collectors;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Item;
import java.util.Set;
import net.minecraft.world.entity.npc.Villager;

public class TradeWithVillager extends Behavior<Villager> {
    private Set<Item> trades;
    
    public TradeWithVillager() {
        super((Map)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.trades = (Set<Item>)ImmutableSet.of();
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        return BehaviorUtils.targetIsValid(avt.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.checkExtraStartConditions(vk, avt);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final Villager avt2 = (Villager)avt.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        BehaviorUtils.lockGazeAndWalkToEachOther(avt, avt2);
        this.trades = figureOutWhatIAmWillingToTrade(avt, avt2);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        final Villager avt2 = (Villager)avt.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (avt.distanceToSqr(avt2) > 5.0) {
            return;
        }
        BehaviorUtils.lockGazeAndWalkToEachOther(avt, avt2);
        avt.gossip(avt2, long3);
        if (avt.hasExcessFood() && (avt.getVillagerData().getProfession() == VillagerProfession.FARMER || avt2.wantsMoreFood())) {
            throwHalfStack(avt, (Set<Item>)Villager.FOOD_POINTS.keySet(), avt2);
        }
        if (!this.trades.isEmpty() && avt.getInventory().hasAnyOf(this.trades)) {
            throwHalfStack(avt, this.trades, avt2);
        }
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        avt.getBrain().<LivingEntity>eraseMemory(MemoryModuleType.INTERACTION_TARGET);
    }
    
    private static Set<Item> figureOutWhatIAmWillingToTrade(final Villager avt1, final Villager avt2) {
        final ImmutableSet<Item> immutableSet3 = avt2.getVillagerData().getProfession().getRequestedItems();
        final ImmutableSet<Item> immutableSet4 = avt1.getVillagerData().getProfession().getRequestedItems();
        return (Set<Item>)immutableSet3.stream().filter(bce -> !immutableSet4.contains(bce)).collect(Collectors.toSet());
    }
    
    private static void throwHalfStack(final Villager avt, final Set<Item> set, final LivingEntity aix) {
        final SimpleContainer aho4 = avt.getInventory();
        ItemStack bcj5 = ItemStack.EMPTY;
        for (int integer6 = 0; integer6 < aho4.getContainerSize(); ++integer6) {
            final ItemStack bcj6 = aho4.getItem(integer6);
            if (!bcj6.isEmpty()) {
                final Item bce8 = bcj6.getItem();
                if (set.contains(bce8)) {
                    int integer7;
                    if (bcj6.getCount() > bcj6.getMaxStackSize() / 2) {
                        integer7 = bcj6.getCount() / 2;
                    }
                    else {
                        if (bcj6.getCount() <= 24) {
                            continue;
                        }
                        integer7 = bcj6.getCount() - 24;
                    }
                    bcj6.shrink(integer7);
                    bcj5 = new ItemStack(bce8, integer7);
                    break;
                }
            }
        }
        if (!bcj5.isEmpty()) {
            BehaviorUtils.throwItem(avt, bcj5, aix);
        }
    }
}

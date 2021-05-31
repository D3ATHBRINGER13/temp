package net.minecraft.world.entity.ai.behavior;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import java.util.HashMap;
import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffects;
import java.util.Optional;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import java.util.Map;
import net.minecraft.world.entity.npc.Villager;

public class GiveGiftToHero extends Behavior<Villager> {
    private static final Map<VillagerProfession, ResourceLocation> gifts;
    private int timeUntilNextGift;
    private boolean giftGivenDuringThisRun;
    private long timeSinceStart;
    
    public GiveGiftToHero(final int integer) {
        super((Map)ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), integer);
        this.timeUntilNextGift = 600;
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        if (!this.isHeroVisible(avt)) {
            return false;
        }
        if (this.timeUntilNextGift > 0) {
            --this.timeUntilNextGift;
            return false;
        }
        return true;
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        this.giftGivenDuringThisRun = false;
        this.timeSinceStart = long3;
        final Player awg6 = (Player)this.getNearestTargetableHero(avt).get();
        avt.getBrain().<LivingEntity>setMemory(MemoryModuleType.INTERACTION_TARGET, awg6);
        BehaviorUtils.lookAtEntity(avt, awg6);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.isHeroVisible(avt) && !this.giftGivenDuringThisRun;
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        final Player awg6 = (Player)this.getNearestTargetableHero(avt).get();
        BehaviorUtils.lookAtEntity(avt, awg6);
        if (this.isWithinThrowingDistance(avt, awg6)) {
            if (long3 - this.timeSinceStart > 20L) {
                this.throwGift(avt, awg6);
                this.giftGivenDuringThisRun = true;
            }
        }
        else {
            BehaviorUtils.walkToEntity(avt, awg6, 5);
        }
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        this.timeUntilNextGift = calculateTimeUntilNextGift(vk);
        avt.getBrain().<LivingEntity>eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        avt.getBrain().<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
        avt.getBrain().<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
    }
    
    private void throwGift(final Villager avt, final LivingEntity aix) {
        final List<ItemStack> list4 = this.getItemToThrow(avt);
        for (final ItemStack bcj6 : list4) {
            BehaviorUtils.throwItem(avt, bcj6, aix);
        }
    }
    
    private List<ItemStack> getItemToThrow(final Villager avt) {
        if (avt.isBaby()) {
            return (List<ItemStack>)ImmutableList.of(new ItemStack(Items.POPPY));
        }
        final VillagerProfession avw3 = avt.getVillagerData().getProfession();
        if (GiveGiftToHero.gifts.containsKey(avw3)) {
            final LootTable cpb4 = avt.level.getServer().getLootTables().get((ResourceLocation)GiveGiftToHero.gifts.get(avw3));
            final LootContext.Builder a5 = new LootContext.Builder((ServerLevel)avt.level).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(avt)).<Entity>withParameter(LootContextParams.THIS_ENTITY, avt).withRandom(avt.getRandom());
            return cpb4.getRandomItems(a5.create(LootContextParamSets.GIFT));
        }
        return (List<ItemStack>)ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
    }
    
    private boolean isHeroVisible(final Villager avt) {
        return this.getNearestTargetableHero(avt).isPresent();
    }
    
    private Optional<Player> getNearestTargetableHero(final Villager avt) {
        return (Optional<Player>)avt.getBrain().<Player>getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
    }
    
    private boolean isHero(final Player awg) {
        return awg.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
    }
    
    private boolean isWithinThrowingDistance(final Villager avt, final Player awg) {
        final BlockPos ew4 = new BlockPos(awg);
        final BlockPos ew5 = new BlockPos(avt);
        return ew5.closerThan(ew4, 5.0);
    }
    
    private static int calculateTimeUntilNextGift(final ServerLevel vk) {
        return 600 + vk.random.nextInt(6001);
    }
    
    static {
        gifts = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT);
            hashMap.put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT);
            hashMap.put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT);
            hashMap.put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT);
            hashMap.put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT);
            hashMap.put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT);
            hashMap.put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT);
            hashMap.put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT);
            hashMap.put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT);
            hashMap.put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT);
            hashMap.put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT);
            hashMap.put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT);
            hashMap.put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT);
        }));
    }
}

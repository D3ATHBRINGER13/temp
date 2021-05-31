package net.minecraft.world.entity.ai.behavior;

import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Comparator;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.PathfinderMob;

public class PlayTagWithOtherKids extends Behavior<PathfinderMob> {
    public PlayTagWithOtherKids() {
        super((Map)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final PathfinderMob aje) {
        return vk.getRandom().nextInt(10) == 0 && this.hasFriendsNearby(aje);
    }
    
    @Override
    protected void start(final ServerLevel vk, final PathfinderMob aje, final long long3) {
        final LivingEntity aix6 = this.seeIfSomeoneIsChasingMe(aje);
        if (aix6 != null) {
            this.fleeFromChaser(vk, aje, aix6);
            return;
        }
        final Optional<LivingEntity> optional7 = this.findSomeoneBeingChased(aje);
        if (optional7.isPresent()) {
            chaseKid(aje, (LivingEntity)optional7.get());
            return;
        }
        this.findSomeoneToChase(aje).ifPresent(aix -> chaseKid(aje, aix));
    }
    
    private void fleeFromChaser(final ServerLevel vk, final PathfinderMob aje, final LivingEntity aix) {
        for (int integer5 = 0; integer5 < 10; ++integer5) {
            final Vec3 csi6 = RandomPos.getLandPos(aje, 20, 8);
            if (csi6 != null && vk.isVillage(new BlockPos(csi6))) {
                aje.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(csi6, 0.6f, 0));
                return;
            }
        }
    }
    
    private static void chaseKid(final PathfinderMob aje, final LivingEntity aix) {
        final Brain<?> ajm3 = aje.getBrain();
        ajm3.<LivingEntity>setMemory(MemoryModuleType.INTERACTION_TARGET, aix);
        ajm3.<EntityPosWrapper>setMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(aix));
        ajm3.<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(aix), 0.6f, 1));
    }
    
    private Optional<LivingEntity> findSomeoneToChase(final PathfinderMob aje) {
        return (Optional<LivingEntity>)this.getFriendsNearby(aje).stream().findAny();
    }
    
    private Optional<LivingEntity> findSomeoneBeingChased(final PathfinderMob aje) {
        final Map<LivingEntity, Integer> map3 = this.checkHowManyChasersEachFriendHas(aje);
        return (Optional<LivingEntity>)map3.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (int)entry.getValue() > 0 && (int)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }
    
    private Map<LivingEntity, Integer> checkHowManyChasersEachFriendHas(final PathfinderMob aje) {
        final Map<LivingEntity, Integer> map3 = (Map<LivingEntity, Integer>)Maps.newHashMap();
        this.getFriendsNearby(aje).stream().filter(this::isChasingSomeone).forEach(aix -> {
            final Integer n = (Integer)map3.compute(this.whoAreYouChasing(aix), (aix, integer) -> (integer == null) ? 1 : (integer + 1));
        });
        return map3;
    }
    
    private List<LivingEntity> getFriendsNearby(final PathfinderMob aje) {
        return (List<LivingEntity>)aje.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
    }
    
    private LivingEntity whoAreYouChasing(final LivingEntity aix) {
        return (LivingEntity)aix.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }
    
    @Nullable
    private LivingEntity seeIfSomeoneIsChasingMe(final LivingEntity aix) {
        return (LivingEntity)((List)aix.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get()).stream().filter(aix2 -> this.isFriendChasingMe(aix, aix2)).findAny().orElse(null);
    }
    
    private boolean isChasingSomeone(final LivingEntity aix) {
        return aix.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }
    
    private boolean isFriendChasingMe(final LivingEntity aix1, final LivingEntity aix2) {
        return aix2.getBrain().<LivingEntity>getMemory(MemoryModuleType.INTERACTION_TARGET).filter(aix2 -> aix2 == aix1).isPresent();
    }
    
    private boolean hasFriendsNearby(final PathfinderMob aje) {
        return aje.getBrain().hasMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}

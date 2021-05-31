package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.level.pathfinder.Path;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.EntityType;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class MakeLove extends Behavior<Villager> {
    private long birthTimestamp;
    
    public MakeLove() {
        super((Map)ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        return this.isBreedingPossible(avt);
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return long3 <= this.birthTimestamp && this.isBreedingPossible(avt);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final Villager avt2 = this.getBreedingTarget(avt);
        BehaviorUtils.lockGazeAndWalkToEachOther(avt, avt2);
        vk.broadcastEntityEvent(avt2, (byte)18);
        vk.broadcastEntityEvent(avt, (byte)18);
        final int integer7 = 275 + avt.getRandom().nextInt(50);
        this.birthTimestamp = long3 + integer7;
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        final Villager avt2 = this.getBreedingTarget(avt);
        if (avt.distanceToSqr(avt2) > 5.0) {
            return;
        }
        BehaviorUtils.lockGazeAndWalkToEachOther(avt, avt2);
        if (long3 >= this.birthTimestamp) {
            avt.eatAndDigestFood();
            avt2.eatAndDigestFood();
            this.tryToGiveBirth(vk, avt, avt2);
        }
        else if (avt.getRandom().nextInt(35) == 0) {
            vk.broadcastEntityEvent(avt2, (byte)12);
            vk.broadcastEntityEvent(avt, (byte)12);
        }
    }
    
    private void tryToGiveBirth(final ServerLevel vk, final Villager avt2, final Villager avt3) {
        final Optional<BlockPos> optional5 = this.takeVacantBed(vk, avt2);
        if (!optional5.isPresent()) {
            vk.broadcastEntityEvent(avt3, (byte)13);
            vk.broadcastEntityEvent(avt2, (byte)13);
        }
        else {
            final Optional<Villager> optional6 = this.breed(avt2, avt3);
            if (optional6.isPresent()) {
                this.giveBedToChild(vk, (Villager)optional6.get(), (BlockPos)optional5.get());
            }
            else {
                vk.getPoiManager().release((BlockPos)optional5.get());
            }
        }
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        avt.getBrain().<Villager>eraseMemory(MemoryModuleType.BREED_TARGET);
    }
    
    private Villager getBreedingTarget(final Villager avt) {
        return (Villager)avt.getBrain().<Villager>getMemory(MemoryModuleType.BREED_TARGET).get();
    }
    
    private boolean isBreedingPossible(final Villager avt) {
        final Brain<Villager> ajm3 = avt.getBrain();
        if (!ajm3.<Villager>getMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
            return false;
        }
        final Villager avt2 = this.getBreedingTarget(avt);
        return BehaviorUtils.targetIsValid(ajm3, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && avt.canBreed() && avt2.canBreed();
    }
    
    private Optional<BlockPos> takeVacantBed(final ServerLevel vk, final Villager avt) {
        return vk.getPoiManager().take(PoiType.HOME.getPredicate(), (Predicate<BlockPos>)(ew -> this.canReach(avt, ew)), new BlockPos(avt), 48);
    }
    
    private boolean canReach(final Villager avt, final BlockPos ew) {
        final Path cnr4 = avt.getNavigation().createPath(ew, PoiType.HOME.getValidRange());
        return cnr4 != null && cnr4.canReach();
    }
    
    private Optional<Villager> breed(final Villager avt1, final Villager avt2) {
        final Villager avt3 = avt1.getBreedOffspring(avt2);
        if (avt3 == null) {
            return (Optional<Villager>)Optional.empty();
        }
        avt1.setAge(6000);
        avt2.setAge(6000);
        avt3.setAge(-24000);
        avt3.moveTo(avt1.x, avt1.y, avt1.z, 0.0f, 0.0f);
        avt1.level.addFreshEntity(avt3);
        avt1.level.broadcastEntityEvent(avt3, (byte)12);
        return (Optional<Villager>)Optional.of(avt3);
    }
    
    private void giveBedToChild(final ServerLevel vk, final Villager avt, final BlockPos ew) {
        final GlobalPos fd5 = GlobalPos.of(vk.getDimension().getType(), ew);
        avt.getBrain().<GlobalPos>setMemory(MemoryModuleType.HOME, fd5);
    }
}

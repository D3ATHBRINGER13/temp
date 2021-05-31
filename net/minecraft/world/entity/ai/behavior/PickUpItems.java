package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.ArrayList;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.List;
import net.minecraft.world.entity.npc.Villager;

public class PickUpItems extends Behavior<Villager> {
    private List<ItemEntity> items;
    
    public PickUpItems() {
        super((Map)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.items = (List<ItemEntity>)new ArrayList();
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        this.items = vk.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, avt.getBoundingBox().inflate(4.0, 2.0, 4.0));
        return !this.items.isEmpty();
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final ItemEntity atx6 = (ItemEntity)this.items.get(vk.random.nextInt(this.items.size()));
        if (avt.wantToPickUp(atx6.getItem().getItem())) {
            final Vec3 csi7 = atx6.position();
            avt.getBrain().<BlockPosWrapper>setMemory((MemoryModuleType<BlockPosWrapper>)MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(new BlockPos(csi7)));
            avt.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(csi7, 0.5f, 0));
        }
    }
}

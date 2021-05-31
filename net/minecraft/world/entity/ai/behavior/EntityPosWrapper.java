package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class EntityPosWrapper implements PositionWrapper {
    private final Entity entity;
    
    public EntityPosWrapper(final Entity aio) {
        this.entity = aio;
    }
    
    public BlockPos getPos() {
        return new BlockPos(this.entity);
    }
    
    public Vec3 getLookAtPos() {
        return new Vec3(this.entity.x, this.entity.y + this.entity.getEyeHeight(), this.entity.z);
    }
    
    public boolean isVisible(final LivingEntity aix) {
        final Optional<List<LivingEntity>> optional3 = aix.getBrain().<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
        return this.entity.isAlive() && optional3.isPresent() && ((List)optional3.get()).contains(this.entity);
    }
    
    public String toString() {
        return new StringBuilder().append("EntityPosWrapper for ").append(this.entity).toString();
    }
}

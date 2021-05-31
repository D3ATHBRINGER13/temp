package net.minecraft.world.phys.shapes;

import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface CollisionContext {
    default CollisionContext empty() {
        return EntityCollisionContext.EMPTY;
    }
    
    default CollisionContext of(final Entity aio) {
        return new EntityCollisionContext(aio);
    }
    
    boolean isSneaking();
    
    boolean isAbove(final VoxelShape ctc, final BlockPos ew, final boolean boolean3);
    
    boolean isHoldingItem(final Item bce);
}

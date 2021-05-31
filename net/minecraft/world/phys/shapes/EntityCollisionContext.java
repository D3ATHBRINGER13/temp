package net.minecraft.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;

public class EntityCollisionContext implements CollisionContext {
    protected static final CollisionContext EMPTY;
    private final boolean sneaking;
    private final double entityBottom;
    private final Item heldItem;
    
    protected EntityCollisionContext(final boolean boolean1, final double double2, final Item bce) {
        this.sneaking = boolean1;
        this.entityBottom = double2;
        this.heldItem = bce;
    }
    
    @Deprecated
    protected EntityCollisionContext(final Entity aio) {
        this(aio.isSneaking(), aio.getBoundingBox().minY, (aio instanceof LivingEntity) ? ((LivingEntity)aio).getMainHandItem().getItem() : Items.AIR);
    }
    
    public boolean isHoldingItem(final Item bce) {
        return this.heldItem == bce;
    }
    
    public boolean isSneaking() {
        return this.sneaking;
    }
    
    public boolean isAbove(final VoxelShape ctc, final BlockPos ew, final boolean boolean3) {
        return this.entityBottom > ew.getY() + ctc.max(Direction.Axis.Y) - 9.999999747378752E-6;
    }
    
    static {
        EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308, Items.AIR) {
            @Override
            public boolean isAbove(final VoxelShape ctc, final BlockPos ew, final boolean boolean3) {
                return boolean3;
            }
        };
    }
}

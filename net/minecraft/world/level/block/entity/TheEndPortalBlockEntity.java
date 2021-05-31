package net.minecraft.world.level.block.entity;

import net.minecraft.core.Direction;

public class TheEndPortalBlockEntity extends BlockEntity {
    public TheEndPortalBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
    }
    
    public TheEndPortalBlockEntity() {
        this(BlockEntityType.END_PORTAL);
    }
    
    public boolean shouldRenderFace(final Direction fb) {
        return fb == Direction.UP;
    }
}

package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DaylightDetectorBlock;

public class DaylightDetectorBlockEntity extends BlockEntity implements TickableBlockEntity {
    public DaylightDetectorBlockEntity() {
        super(BlockEntityType.DAYLIGHT_DETECTOR);
    }
    
    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 20L == 0L) {
            final BlockState bvt2 = this.getBlockState();
            final Block bmv3 = bvt2.getBlock();
            if (bmv3 instanceof DaylightDetectorBlock) {
                DaylightDetectorBlock.updateSignalStrength(bvt2, this.level, this.worldPosition);
            }
        }
    }
}

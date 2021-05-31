package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractGlassBlock extends HalfTransparentBlock {
    protected AbstractGlassBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public float getShadeBrightness(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return 1.0f;
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    @Override
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return false;
    }
}

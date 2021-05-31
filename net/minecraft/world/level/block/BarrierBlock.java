package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierBlock extends Block {
    protected BarrierBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return false;
    }
    
    @Override
    public float getShadeBrightness(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return 1.0f;
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return false;
    }
}

package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HayBlock extends RotatedPillarBlock {
    public HayBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Direction.Axis, Direction.Axis>setValue(HayBlock.AXIS, Direction.Axis.Y));
    }
    
    @Override
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        aio.causeFallDamage(float4, 0.2f);
    }
}

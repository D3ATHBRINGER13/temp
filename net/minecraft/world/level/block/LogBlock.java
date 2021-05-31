package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

public class LogBlock extends RotatedPillarBlock {
    private final MaterialColor woodMaterialColor;
    
    public LogBlock(final MaterialColor clp, final Properties c) {
        super(c);
        this.woodMaterialColor = clp;
    }
    
    @Override
    public MaterialColor getMapColor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return (bvt.<Direction.Axis>getValue(LogBlock.AXIS) == Direction.Axis.Y) ? this.woodMaterialColor : this.materialColor;
    }
}

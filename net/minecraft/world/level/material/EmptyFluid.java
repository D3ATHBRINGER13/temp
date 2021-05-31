package net.minecraft.world.level.material;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockLayer;

public class EmptyFluid extends Fluid {
    public BlockLayer getRenderLayer() {
        return BlockLayer.SOLID;
    }
    
    @Override
    public Item getBucket() {
        return Items.AIR;
    }
    
    public boolean canBeReplacedWith(final FluidState clk, final BlockGetter bhb, final BlockPos ew, final Fluid clj, final Direction fb) {
        return true;
    }
    
    public Vec3 getFlow(final BlockGetter bhb, final BlockPos ew, final FluidState clk) {
        return Vec3.ZERO;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 0;
    }
    
    @Override
    protected boolean isEmpty() {
        return true;
    }
    
    @Override
    protected float getExplosionResistance() {
        return 0.0f;
    }
    
    @Override
    public float getHeight(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        return 0.0f;
    }
    
    @Override
    public float getOwnHeight(final FluidState clk) {
        return 0.0f;
    }
    
    @Override
    protected BlockState createLegacyBlock(final FluidState clk) {
        return Blocks.AIR.defaultBlockState();
    }
    
    @Override
    public boolean isSource(final FluidState clk) {
        return false;
    }
    
    @Override
    public int getAmount(final FluidState clk) {
        return 0;
    }
    
    @Override
    public VoxelShape getShape(final FluidState clk, final BlockGetter bhb, final BlockPos ew) {
        return Shapes.empty();
    }
}

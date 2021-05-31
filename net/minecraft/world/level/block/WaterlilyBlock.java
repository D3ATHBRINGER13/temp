package net.minecraft.world.level.block;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock extends BushBlock {
    protected static final VoxelShape AABB;
    
    protected WaterlilyBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        super.entityInside(bvt, bhr, ew, aio);
        if (aio instanceof Boat) {
            bhr.destroyBlock(new BlockPos(ew), true);
        }
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return WaterlilyBlock.AABB;
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final FluidState clk5 = bhb.getFluidState(ew);
        return clk5.getType() == Fluids.WATER || bvt.getMaterial() == Material.ICE;
    }
    
    static {
        AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);
    }
}

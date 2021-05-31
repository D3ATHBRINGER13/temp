package net.minecraft.world.level.block;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.LevelReader;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class TallSeagrass extends ShearableDoublePlantBlock implements LiquidBlockContainer {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final VoxelShape SHAPE;
    
    public TallSeagrass(final Properties c) {
        super(c);
    }
    
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return TallSeagrass.SHAPE;
    }
    
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.isFaceSturdy(bhb, ew, Direction.UP) && bvt.getBlock() != Blocks.MAGMA_BLOCK;
    }
    
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(Blocks.SEAGRASS);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = super.getStateForPlacement(ban);
        if (bvt3 != null) {
            final FluidState clk4 = ban.getLevel().getFluidState(ban.getClickedPos().above());
            if (clk4.is(FluidTags.WATER) && clk4.getAmount() == 8) {
                return bvt3;
            }
        }
        return null;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        if (bvt.<DoubleBlockHalf>getValue(TallSeagrass.HALF) == DoubleBlockHalf.UPPER) {
            final BlockState bvt2 = bhu.getBlockState(ew.below());
            return bvt2.getBlock() == this && bvt2.<DoubleBlockHalf>getValue(TallSeagrass.HALF) == DoubleBlockHalf.LOWER;
        }
        final FluidState clk5 = bhu.getFluidState(ew);
        return super.canSurvive(bvt, bhu, ew) && clk5.is(FluidTags.WATER) && clk5.getAmount() == 8;
    }
    
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.WATER.getSource(false);
    }
    
    @Override
    public boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return false;
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return false;
    }
    
    static {
        HALF = ShearableDoublePlantBlock.HALF;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }
}

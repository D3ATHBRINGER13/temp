package net.minecraft.world.level.block;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoolCarpetBlock extends Block {
    protected static final VoxelShape SHAPE;
    private final DyeColor color;
    
    protected WoolCarpetBlock(final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
    }
    
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return WoolCarpetBlock.SHAPE;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return !bhu.isEmptyBlock(ew.below());
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }
}

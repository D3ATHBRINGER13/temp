package net.minecraft.world.level.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulsandBlock extends Block {
    protected static final VoxelShape SHAPE;
    
    public SoulsandBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SoulsandBlock.SHAPE;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        aio.setDeltaMovement(aio.getDeltaMovement().multiply(0.4, 1.0, 0.4));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        BubbleColumnBlock.growColumn(bhr, ew.above(), false);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        bhr.getBlockTicks().scheduleTick(ew3, this, this.getTickDelay(bhr));
    }
    
    @Override
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 20;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return true;
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    }
}

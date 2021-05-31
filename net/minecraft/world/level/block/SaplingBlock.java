package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SaplingBlock extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty STAGE;
    protected static final VoxelShape SHAPE;
    private final AbstractTreeGrower treeGrower;
    
    protected SaplingBlock(final AbstractTreeGrower bvd, final Properties c) {
        super(c);
        this.treeGrower = bvd;
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)SaplingBlock.STAGE, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SaplingBlock.SHAPE;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.tick(bvt, bhr, ew, random);
        if (bhr.getMaxLocalRawBrightness(ew.above()) >= 9 && random.nextInt(7) == 0) {
            this.advanceTree(bhr, ew, bvt, random);
        }
    }
    
    public void advanceTree(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Random random) {
        if (bvt.<Integer>getValue((Property<Integer>)SaplingBlock.STAGE) == 0) {
            bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)SaplingBlock.STAGE), 4);
        }
        else {
            this.treeGrower.growTree(bhs, ew, bvt, random);
        }
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return bhr.random.nextFloat() < 0.45;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        this.advanceTree(bhr, ew, bvt, random);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SaplingBlock.STAGE);
    }
    
    static {
        STAGE = BlockStateProperties.STAGE;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    }
}

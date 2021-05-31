package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class NetherWartBlock extends BushBlock {
    public static final IntegerProperty AGE;
    private static final VoxelShape[] SHAPE_BY_AGE;
    
    protected NetherWartBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)NetherWartBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return NetherWartBlock.SHAPE_BY_AGE[bvt.<Integer>getValue((Property<Integer>)NetherWartBlock.AGE)];
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getBlock() == Blocks.SOUL_SAND;
    }
    
    @Override
    public void tick(BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)NetherWartBlock.AGE);
        if (integer6 < 3 && random.nextInt(10) == 0) {
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)NetherWartBlock.AGE, integer6 + 1);
            bhr.setBlock(ew, bvt, 2);
        }
        super.tick(bvt, bhr, ew, random);
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(Items.NETHER_WART);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(NetherWartBlock.AGE);
    }
    
    static {
        AGE = BlockStateProperties.AGE_3;
        SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0) };
    }
}

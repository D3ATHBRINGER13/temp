package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SkullBlock extends AbstractSkullBlock {
    public static final IntegerProperty ROTATION;
    protected static final VoxelShape SHAPE;
    
    protected SkullBlock(final Type a, final Properties c) {
        super(a, c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)SkullBlock.ROTATION, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return SkullBlock.SHAPE;
    }
    
    @Override
    public VoxelShape getOcclusionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return Shapes.empty();
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SkullBlock.ROTATION, Mth.floor(ban.getRotation() * 16.0f / 360.0f + 0.5) & 0xF);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SkullBlock.ROTATION, brg.rotate(bvt.<Integer>getValue((Property<Integer>)SkullBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SkullBlock.ROTATION, bqg.mirror(bvt.<Integer>getValue((Property<Integer>)SkullBlock.ROTATION), 16));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SkullBlock.ROTATION);
    }
    
    static {
        ROTATION = BlockStateProperties.ROTATION_16;
        SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }
    
    public enum Types implements Type {
        SKELETON, 
        WITHER_SKELETON, 
        PLAYER, 
        ZOMBIE, 
        CREEPER, 
        DRAGON;
    }
    
    public interface Type {
    }
}

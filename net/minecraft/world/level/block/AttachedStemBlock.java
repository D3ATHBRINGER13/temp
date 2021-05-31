package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class AttachedStemBlock extends BushBlock {
    public static final DirectionProperty FACING;
    private final StemGrownBlock fruit;
    private static final Map<Direction, VoxelShape> AABBS;
    
    protected AttachedStemBlock(final StemGrownBlock bsh, final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)AttachedStemBlock.FACING, Direction.NORTH));
        this.fruit = bsh;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return (VoxelShape)AttachedStemBlock.AABBS.get(bvt.getValue((Property<Object>)AttachedStemBlock.FACING));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt3.getBlock() != this.fruit && fb == bvt1.<Comparable>getValue((Property<Comparable>)AttachedStemBlock.FACING)) {
            return ((AbstractStateHolder<O, BlockState>)this.fruit.getStem().defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)StemBlock.AGE, 7);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getBlock() == Blocks.FARMLAND;
    }
    
    protected Item getSeedItem() {
        if (this.fruit == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        if (this.fruit == Blocks.MELON) {
            return Items.MELON_SEEDS;
        }
        return Items.AIR;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(this.getSeedItem());
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)AttachedStemBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)AttachedStemBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)AttachedStemBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(AttachedStemBlock.FACING);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        AABBS = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.SOUTH, Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0), Direction.WEST, Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0), Direction.NORTH, Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0), Direction.EAST, Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)));
    }
}

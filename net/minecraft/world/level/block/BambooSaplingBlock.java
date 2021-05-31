package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooSaplingBlock extends Block implements BonemealableBlock {
    protected static final VoxelShape SAPLING_SHAPE;
    
    public BambooSaplingBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Vec3 csi6 = bvt.getOffset(bhb, ew);
        return BambooSaplingBlock.SAPLING_SHAPE.move(csi6.x, csi6.y, csi6.z);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (random.nextInt(3) == 0 && bhr.isEmptyBlock(ew.above()) && bhr.getRawBrightness(ew.above(), 0) >= 9) {
            this.growBamboo(bhr, ew);
        }
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return bhu.getBlockState(ew.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (fb == Direction.UP && bvt3.getBlock() == Blocks.BAMBOO) {
            bhs.setBlock(ew5, Blocks.BAMBOO.defaultBlockState(), 2);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(Items.BAMBOO);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return bhb.getBlockState(ew.above()).isAir();
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        this.growBamboo(bhr, ew);
    }
    
    @Override
    public float getDestroyProgress(final BlockState bvt, final Player awg, final BlockGetter bhb, final BlockPos ew) {
        if (awg.getMainHandItem().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.getDestroyProgress(bvt, awg, bhb, ew);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    protected void growBamboo(final Level bhr, final BlockPos ew) {
        bhr.setBlock(ew.above(), ((AbstractStateHolder<O, BlockState>)Blocks.BAMBOO.defaultBlockState()).<BambooLeaves, BambooLeaves>setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
    }
    
    static {
        SAPLING_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
    }
}

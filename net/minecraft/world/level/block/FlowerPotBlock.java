package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Map;

public class FlowerPotBlock extends Block {
    private static final Map<Block, Block> POTTED_BY_CONTENT;
    protected static final VoxelShape SHAPE;
    private final Block content;
    
    public FlowerPotBlock(final Block bmv, final Properties c) {
        super(c);
        this.content = bmv;
        FlowerPotBlock.POTTED_BY_CONTENT.put(bmv, this);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return FlowerPotBlock.SHAPE;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final ItemStack bcj8 = awg.getItemInHand(ahi);
        final Item bce9 = bcj8.getItem();
        final Block bmv10 = (Block)((bce9 instanceof BlockItem) ? FlowerPotBlock.POTTED_BY_CONTENT.getOrDefault(((BlockItem)bce9).getBlock(), Blocks.AIR) : Blocks.AIR);
        final boolean boolean11 = bmv10 == Blocks.AIR;
        final boolean boolean12 = this.content == Blocks.AIR;
        if (boolean11 != boolean12) {
            if (boolean12) {
                bhr.setBlock(ew, bmv10.defaultBlockState(), 3);
                awg.awardStat(Stats.POT_FLOWER);
                if (!awg.abilities.instabuild) {
                    bcj8.shrink(1);
                }
            }
            else {
                final ItemStack bcj9 = new ItemStack(this.content);
                if (bcj8.isEmpty()) {
                    awg.setItemInHand(ahi, bcj9);
                }
                else if (!awg.addItem(bcj9)) {
                    awg.drop(bcj9, false);
                }
                bhr.setBlock(ew, Blocks.FLOWER_POT.defaultBlockState(), 3);
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        if (this.content == Blocks.AIR) {
            return super.getCloneItemStack(bhb, ew, bvt);
        }
        return new ItemStack(this.content);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    public Block getContent() {
        return this.content;
    }
    
    static {
        POTTED_BY_CONTENT = (Map)Maps.newHashMap();
        SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    }
}

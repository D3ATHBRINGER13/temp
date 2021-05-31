package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CauldronBlock extends Block {
    public static final IntegerProperty LEVEL;
    private static final VoxelShape INSIDE;
    protected static final VoxelShape SHAPE;
    
    public CauldronBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)CauldronBlock.LEVEL, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return CauldronBlock.SHAPE;
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return false;
    }
    
    @Override
    public VoxelShape getInteractionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return CauldronBlock.INSIDE;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)CauldronBlock.LEVEL);
        final float float7 = ew.getY() + (6.0f + 3 * integer6) / 16.0f;
        if (!bhr.isClientSide && aio.isOnFire() && integer6 > 0 && aio.getBoundingBox().minY <= float7) {
            aio.clearFire();
            this.setWaterLevel(bhr, ew, bvt, integer6 - 1);
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final ItemStack bcj8 = awg.getItemInHand(ahi);
        if (bcj8.isEmpty()) {
            return true;
        }
        final int integer9 = bvt.<Integer>getValue((Property<Integer>)CauldronBlock.LEVEL);
        final Item bce10 = bcj8.getItem();
        if (bce10 == Items.WATER_BUCKET) {
            if (integer9 < 3 && !bhr.isClientSide) {
                if (!awg.abilities.instabuild) {
                    awg.setItemInHand(ahi, new ItemStack(Items.BUCKET));
                }
                awg.awardStat(Stats.FILL_CAULDRON);
                this.setWaterLevel(bhr, ew, bvt, 3);
                bhr.playSound(null, ew, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }
        if (bce10 == Items.BUCKET) {
            if (integer9 == 3 && !bhr.isClientSide) {
                if (!awg.abilities.instabuild) {
                    bcj8.shrink(1);
                    if (bcj8.isEmpty()) {
                        awg.setItemInHand(ahi, new ItemStack(Items.WATER_BUCKET));
                    }
                    else if (!awg.inventory.add(new ItemStack(Items.WATER_BUCKET))) {
                        awg.drop(new ItemStack(Items.WATER_BUCKET), false);
                    }
                }
                awg.awardStat(Stats.USE_CAULDRON);
                this.setWaterLevel(bhr, ew, bvt, 0);
                bhr.playSound(null, ew, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }
        if (bce10 == Items.GLASS_BOTTLE) {
            if (integer9 > 0 && !bhr.isClientSide) {
                if (!awg.abilities.instabuild) {
                    final ItemStack bcj9 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                    awg.awardStat(Stats.USE_CAULDRON);
                    bcj8.shrink(1);
                    if (bcj8.isEmpty()) {
                        awg.setItemInHand(ahi, bcj9);
                    }
                    else if (!awg.inventory.add(bcj9)) {
                        awg.drop(bcj9, false);
                    }
                    else if (awg instanceof ServerPlayer) {
                        ((ServerPlayer)awg).refreshContainer(awg.inventoryMenu);
                    }
                }
                bhr.playSound(null, ew, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(bhr, ew, bvt, integer9 - 1);
            }
            return true;
        }
        if (bce10 == Items.POTION && PotionUtils.getPotion(bcj8) == Potions.WATER) {
            if (integer9 < 3 && !bhr.isClientSide) {
                if (!awg.abilities.instabuild) {
                    final ItemStack bcj9 = new ItemStack(Items.GLASS_BOTTLE);
                    awg.awardStat(Stats.USE_CAULDRON);
                    awg.setItemInHand(ahi, bcj9);
                    if (awg instanceof ServerPlayer) {
                        ((ServerPlayer)awg).refreshContainer(awg.inventoryMenu);
                    }
                }
                bhr.playSound(null, ew, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(bhr, ew, bvt, integer9 + 1);
            }
            return true;
        }
        if (integer9 > 0 && bce10 instanceof DyeableLeatherItem) {
            final DyeableLeatherItem bbk11 = (DyeableLeatherItem)bce10;
            if (bbk11.hasCustomColor(bcj8) && !bhr.isClientSide) {
                bbk11.clearColor(bcj8);
                this.setWaterLevel(bhr, ew, bvt, integer9 - 1);
                awg.awardStat(Stats.CLEAN_ARMOR);
                return true;
            }
        }
        if (integer9 > 0 && bce10 instanceof BannerItem) {
            if (BannerBlockEntity.getPatternCount(bcj8) > 0 && !bhr.isClientSide) {
                final ItemStack bcj9 = bcj8.copy();
                bcj9.setCount(1);
                BannerBlockEntity.removeLastPattern(bcj9);
                awg.awardStat(Stats.CLEAN_BANNER);
                if (!awg.abilities.instabuild) {
                    bcj8.shrink(1);
                    this.setWaterLevel(bhr, ew, bvt, integer9 - 1);
                }
                if (bcj8.isEmpty()) {
                    awg.setItemInHand(ahi, bcj9);
                }
                else if (!awg.inventory.add(bcj9)) {
                    awg.drop(bcj9, false);
                }
                else if (awg instanceof ServerPlayer) {
                    ((ServerPlayer)awg).refreshContainer(awg.inventoryMenu);
                }
            }
            return true;
        }
        if (integer9 > 0 && bce10 instanceof BlockItem) {
            final Block bmv11 = ((BlockItem)bce10).getBlock();
            if (bmv11 instanceof ShulkerBoxBlock && !bhr.isClientSide()) {
                final ItemStack bcj10 = new ItemStack(Blocks.SHULKER_BOX, 1);
                if (bcj8.hasTag()) {
                    bcj10.setTag(bcj8.getTag().copy());
                }
                awg.setItemInHand(ahi, bcj10);
                this.setWaterLevel(bhr, ew, bvt, integer9 - 1);
                awg.awardStat(Stats.CLEAN_SHULKER_BOX);
            }
            return true;
        }
        return false;
    }
    
    public void setWaterLevel(final Level bhr, final BlockPos ew, final BlockState bvt, final int integer) {
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)CauldronBlock.LEVEL, Mth.clamp(integer, 0, 3)), 2);
        bhr.updateNeighbourForOutputSignal(ew, this);
    }
    
    @Override
    public void handleRain(final Level bhr, final BlockPos ew) {
        if (bhr.random.nextInt(20) != 1) {
            return;
        }
        final float float4 = bhr.getBiome(ew).getTemperature(ew);
        if (float4 < 0.15f) {
            return;
        }
        final BlockState bvt5 = bhr.getBlockState(ew);
        if (bvt5.<Integer>getValue((Property<Integer>)CauldronBlock.LEVEL) < 3) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt5).<Comparable>cycle((Property<Comparable>)CauldronBlock.LEVEL), 2);
        }
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return bvt.<Integer>getValue((Property<Integer>)CauldronBlock.LEVEL);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CauldronBlock.LEVEL);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        LEVEL = BlockStateProperties.LEVEL_CAULDRON;
        INSIDE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
        SHAPE = Shapes.join(Shapes.block(), Shapes.or(Block.box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), Block.box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), CauldronBlock.INSIDE), BooleanOp.ONLY_FIRST);
    }
}

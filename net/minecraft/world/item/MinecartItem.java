package net.minecraft.world.item;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.core.dispenser.DispenseItemBehavior;

public class MinecartItem extends Item {
    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR;
    private final AbstractMinecart.Type type;
    
    public MinecartItem(final AbstractMinecart.Type a, final Properties a) {
        super(a);
        this.type = a;
        DispenserBlock.registerBehavior(this, MinecartItem.DISPENSE_ITEM_BEHAVIOR);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (!bvt5.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        final ItemStack bcj6 = bdu.getItemInHand();
        if (!bhr3.isClientSide) {
            final RailShape bwx7 = (bvt5.getBlock() instanceof BaseRailBlock) ? bvt5.<RailShape>getValue(((BaseRailBlock)bvt5.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double double8 = 0.0;
            if (bwx7.isAscending()) {
                double8 = 0.5;
            }
            final AbstractMinecart axu10 = AbstractMinecart.createMinecart(bhr3, ew4.getX() + 0.5, ew4.getY() + 0.0625 + double8, ew4.getZ() + 0.5, this.type);
            if (bcj6.hasCustomHoverName()) {
                axu10.setCustomName(bcj6.getHoverName());
            }
            bhr3.addFreshEntity(axu10);
        }
        bcj6.shrink(1);
        return InteractionResult.SUCCESS;
    }
    
    static {
        DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
            
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
                final Level bhr5 = ex.getLevel();
                final double double6 = ex.x() + fb4.getStepX() * 1.125;
                final double double7 = Math.floor(ex.y()) + fb4.getStepY();
                final double double8 = ex.z() + fb4.getStepZ() * 1.125;
                final BlockPos ew12 = ex.getPos().relative(fb4);
                final BlockState bvt13 = bhr5.getBlockState(ew12);
                final RailShape bwx14 = (bvt13.getBlock() instanceof BaseRailBlock) ? bvt13.<RailShape>getValue(((BaseRailBlock)bvt13.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double double9;
                if (bvt13.is(BlockTags.RAILS)) {
                    if (bwx14.isAscending()) {
                        double9 = 0.6;
                    }
                    else {
                        double9 = 0.1;
                    }
                }
                else {
                    if (!bvt13.isAir() || !bhr5.getBlockState(ew12.below()).is(BlockTags.RAILS)) {
                        return this.defaultDispenseItemBehavior.dispense(ex, bcj);
                    }
                    final BlockState bvt14 = bhr5.getBlockState(ew12.below());
                    final RailShape bwx15 = (bvt14.getBlock() instanceof BaseRailBlock) ? bvt14.<RailShape>getValue(((BaseRailBlock)bvt14.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    if (fb4 == Direction.DOWN || !bwx15.isAscending()) {
                        double9 = -0.9;
                    }
                    else {
                        double9 = -0.4;
                    }
                }
                final AbstractMinecart axu17 = AbstractMinecart.createMinecart(bhr5, double6, double7 + double9, double8, ((MinecartItem)bcj.getItem()).type);
                if (bcj.hasCustomHoverName()) {
                    axu17.setCustomName(bcj.getHoverName());
                }
                bhr5.addFreshEntity(axu17);
                bcj.shrink(1);
                return bcj;
            }
            
            @Override
            protected void playSound(final BlockSource ex) {
                ex.getLevel().levelEvent(1000, ex.getPos(), 0);
            }
        };
    }
}

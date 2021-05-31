package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior;
    private final Boat.Type type;
    
    public BoatDispenseItemBehavior(final Boat.Type b) {
        this.defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
        this.type = b;
    }
    
    public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
        final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
        final Level bhr5 = ex.getLevel();
        final double double6 = ex.x() + fb4.getStepX() * 1.125f;
        final double double7 = ex.y() + fb4.getStepY() * 1.125f;
        final double double8 = ex.z() + fb4.getStepZ() * 1.125f;
        final BlockPos ew12 = ex.getPos().relative(fb4);
        double double9;
        if (bhr5.getFluidState(ew12).is(FluidTags.WATER)) {
            double9 = 1.0;
        }
        else {
            if (!bhr5.getBlockState(ew12).isAir() || !bhr5.getFluidState(ew12.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(ex, bcj);
            }
            double9 = 0.0;
        }
        final Boat axw15 = new Boat(bhr5, double6, double7 + double9, double8);
        axw15.setType(this.type);
        axw15.yRot = fb4.toYRot();
        bhr5.addFreshEntity(axw15);
        bcj.shrink(1);
        return bcj;
    }
    
    @Override
    protected void playSound(final BlockSource ex) {
        ex.getLevel().levelEvent(1000, ex.getPos(), 0);
    }
}

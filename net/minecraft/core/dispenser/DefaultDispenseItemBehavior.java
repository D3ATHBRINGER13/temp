package net.minecraft.core.dispenser;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;

public class DefaultDispenseItemBehavior implements DispenseItemBehavior {
    public final ItemStack dispense(final BlockSource ex, final ItemStack bcj) {
        final ItemStack bcj2 = this.execute(ex, bcj);
        this.playSound(ex);
        this.playAnimation(ex, ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
        return bcj2;
    }
    
    protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
        final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
        final Position fl5 = DispenserBlock.getDispensePosition(ex);
        final ItemStack bcj2 = bcj.split(1);
        spawnItem(ex.getLevel(), bcj2, 6, fb4, fl5);
        return bcj;
    }
    
    public static void spawnItem(final Level bhr, final ItemStack bcj, final int integer, final Direction fb, final Position fl) {
        final double double6 = fl.x();
        double double7 = fl.y();
        final double double8 = fl.z();
        if (fb.getAxis() == Direction.Axis.Y) {
            double7 -= 0.125;
        }
        else {
            double7 -= 0.15625;
        }
        final ItemEntity atx12 = new ItemEntity(bhr, double6, double7, double8, bcj);
        final double double9 = bhr.random.nextDouble() * 0.1 + 0.2;
        atx12.setDeltaMovement(bhr.random.nextGaussian() * 0.007499999832361937 * integer + fb.getStepX() * double9, bhr.random.nextGaussian() * 0.007499999832361937 * integer + 0.20000000298023224, bhr.random.nextGaussian() * 0.007499999832361937 * integer + fb.getStepZ() * double9);
        bhr.addFreshEntity(atx12);
    }
    
    protected void playSound(final BlockSource ex) {
        ex.getLevel().levelEvent(1000, ex.getPos(), 0);
    }
    
    protected void playAnimation(final BlockSource ex, final Direction fb) {
        ex.getLevel().levelEvent(2000, ex.getPos(), fb.get3DDataValue());
    }
}

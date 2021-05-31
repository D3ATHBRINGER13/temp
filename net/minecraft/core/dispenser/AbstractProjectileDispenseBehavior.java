package net.minecraft.core.dispenser;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
    public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
        final Level bhr4 = ex.getLevel();
        final Position fl5 = DispenserBlock.getDispensePosition(ex);
        final Direction fb6 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
        final Projectile awv7 = this.getProjectile(bhr4, fl5, bcj);
        awv7.shoot(fb6.getStepX(), fb6.getStepY() + 0.1f, fb6.getStepZ(), this.getPower(), this.getUncertainty());
        bhr4.addFreshEntity((Entity)awv7);
        bcj.shrink(1);
        return bcj;
    }
    
    @Override
    protected void playSound(final BlockSource ex) {
        ex.getLevel().levelEvent(1002, ex.getPos(), 0);
    }
    
    protected abstract Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj);
    
    protected float getUncertainty() {
        return 6.0f;
    }
    
    protected float getPower() {
        return 1.1f;
    }
}

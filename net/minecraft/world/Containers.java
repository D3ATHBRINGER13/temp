package net.minecraft.world;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.Random;

public class Containers {
    private static final Random RANDOM;
    
    public static void dropContents(final Level bhr, final BlockPos ew, final Container ahc) {
        dropContents(bhr, ew.getX(), ew.getY(), ew.getZ(), ahc);
    }
    
    public static void dropContents(final Level bhr, final Entity aio, final Container ahc) {
        dropContents(bhr, aio.x, aio.y, aio.z, ahc);
    }
    
    private static void dropContents(final Level bhr, final double double2, final double double3, final double double4, final Container ahc) {
        for (int integer9 = 0; integer9 < ahc.getContainerSize(); ++integer9) {
            dropItemStack(bhr, double2, double3, double4, ahc.getItem(integer9));
        }
    }
    
    public static void dropContents(final Level bhr, final BlockPos ew, final NonNullList<ItemStack> fk) {
        fk.forEach(bcj -> dropItemStack(bhr, ew.getX(), ew.getY(), ew.getZ(), bcj));
    }
    
    public static void dropItemStack(final Level bhr, final double double2, final double double3, final double double4, final ItemStack bcj) {
        final double double5 = EntityType.ITEM.getWidth();
        final double double6 = 1.0 - double5;
        final double double7 = double5 / 2.0;
        final double double8 = Math.floor(double2) + Containers.RANDOM.nextDouble() * double6 + double7;
        final double double9 = Math.floor(double3) + Containers.RANDOM.nextDouble() * double6;
        final double double10 = Math.floor(double4) + Containers.RANDOM.nextDouble() * double6 + double7;
        while (!bcj.isEmpty()) {
            final ItemEntity atx21 = new ItemEntity(bhr, double8, double9, double10, bcj.split(Containers.RANDOM.nextInt(21) + 10));
            final float float22 = 0.05f;
            atx21.setDeltaMovement(Containers.RANDOM.nextGaussian() * 0.05000000074505806, Containers.RANDOM.nextGaussian() * 0.05000000074505806 + 0.20000000298023224, Containers.RANDOM.nextGaussian() * 0.05000000074505806);
            bhr.addFreshEntity(atx21);
        }
    }
    
    static {
        RANDOM = new Random();
    }
}

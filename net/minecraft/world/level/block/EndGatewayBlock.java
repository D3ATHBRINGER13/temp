package net.minecraft.world.level.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class EndGatewayBlock extends BaseEntityBlock {
    protected EndGatewayBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new TheEndGatewayBlockEntity();
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        if (!(btw6 instanceof TheEndGatewayBlockEntity)) {
            return;
        }
        for (int integer7 = ((TheEndGatewayBlockEntity)btw6).getParticleAmount(), integer8 = 0; integer8 < integer7; ++integer8) {
            double double9 = ew.getX() + random.nextFloat();
            final double double10 = ew.getY() + random.nextFloat();
            double double11 = ew.getZ() + random.nextFloat();
            double double12 = (random.nextFloat() - 0.5) * 0.5;
            final double double13 = (random.nextFloat() - 0.5) * 0.5;
            double double14 = (random.nextFloat() - 0.5) * 0.5;
            final int integer9 = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
                double11 = ew.getZ() + 0.5 + 0.25 * integer9;
                double14 = random.nextFloat() * 2.0f * integer9;
            }
            else {
                double9 = ew.getX() + 0.5 + 0.25 * integer9;
                double12 = random.nextFloat() * 2.0f * integer9;
            }
            bhr.addParticle(ParticleTypes.PORTAL, double9, double10, double11, double12, double13, double14);
        }
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
}

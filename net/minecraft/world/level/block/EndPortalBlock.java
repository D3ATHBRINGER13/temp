package net.minecraft.world.level.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE;
    
    protected EndPortalBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new TheEndPortalBlockEntity();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return EndPortalBlock.SHAPE;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (!bhr.isClientSide && !aio.isPassenger() && !aio.isVehicle() && aio.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(aio.getBoundingBox().move(-ew.getX(), -ew.getY(), -ew.getZ())), bvt.getShape(bhr, ew), BooleanOp.AND)) {
            aio.changeDimension((bhr.dimension.getType() == DimensionType.THE_END) ? DimensionType.OVERWORLD : DimensionType.THE_END);
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final double double6 = ew.getX() + random.nextFloat();
        final double double7 = ew.getY() + 0.8f;
        final double double8 = ew.getZ() + random.nextFloat();
        final double double9 = 0.0;
        final double double10 = 0.0;
        final double double11 = 0.0;
        bhr.addParticle(ParticleTypes.SMOKE, double6, double7, double8, 0.0, 0.0, 0.0);
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    }
}

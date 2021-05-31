package net.minecraft.world.level.block;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock {
    protected static final VoxelShape SHAPE;
    private final MobEffect suspiciousStewEffect;
    private final int effectDuration;
    
    public FlowerBlock(final MobEffect aig, final int integer, final Properties c) {
        super(c);
        this.suspiciousStewEffect = aig;
        if (aig.isInstantenous()) {
            this.effectDuration = integer;
        }
        else {
            this.effectDuration = integer * 20;
        }
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Vec3 csi6 = bvt.getOffset(bhb, ew);
        return FlowerBlock.SHAPE.move(csi6.x, csi6.y, csi6.z);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    public MobEffect getSuspiciousStewEffect() {
        return this.suspiciousStewEffect;
    }
    
    public int getEffectDuration() {
        return this.effectDuration;
    }
    
    static {
        SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
    }
}

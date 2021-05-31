package net.minecraft.world.level.block;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock extends FallingBlock {
    private final BlockState concrete;
    
    public ConcretePowderBlock(final Block bmv, final Properties c) {
        super(c);
        this.concrete = bmv.defaultBlockState();
    }
    
    @Override
    public void onLand(final Level bhr, final BlockPos ew, final BlockState bvt3, final BlockState bvt4) {
        if (canSolidify(bvt4)) {
            bhr.setBlock(ew, this.concrete, 3);
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockGetter bhb3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        if (canSolidify(bhb3.getBlockState(ew4)) || touchesLiquid(bhb3, ew4)) {
            return this.concrete;
        }
        return super.getStateForPlacement(ban);
    }
    
    private static boolean touchesLiquid(final BlockGetter bhb, final BlockPos ew) {
        boolean boolean3 = false;
        final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos(ew);
        for (final Direction fb8 : Direction.values()) {
            BlockState bvt9 = bhb.getBlockState(a4);
            if (fb8 != Direction.DOWN || canSolidify(bvt9)) {
                a4.set(ew).move(fb8);
                bvt9 = bhb.getBlockState(a4);
                if (canSolidify(bvt9) && !bvt9.isFaceSturdy(bhb, ew, fb8.getOpposite())) {
                    boolean3 = true;
                    break;
                }
            }
        }
        return boolean3;
    }
    
    private static boolean canSolidify(final BlockState bvt) {
        return bvt.getFluidState().is(FluidTags.WATER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (touchesLiquid(bhs, ew5)) {
            return this.concrete;
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
}

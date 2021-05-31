package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class BlockPlaceContext extends UseOnContext {
    private final BlockPos relativePos;
    protected boolean replaceClicked;
    
    public BlockPlaceContext(final UseOnContext bdu) {
        this(bdu.getLevel(), bdu.getPlayer(), bdu.getHand(), bdu.getItemInHand(), bdu.hitResult);
    }
    
    protected BlockPlaceContext(final Level bhr, @Nullable final Player awg, final InteractionHand ahi, final ItemStack bcj, final BlockHitResult csd) {
        super(bhr, awg, ahi, bcj, csd);
        this.replaceClicked = true;
        this.relativePos = csd.getBlockPos().relative(csd.getDirection());
        this.replaceClicked = bhr.getBlockState(csd.getBlockPos()).canBeReplaced(this);
    }
    
    public static BlockPlaceContext at(final BlockPlaceContext ban, final BlockPos ew, final Direction fb) {
        return new BlockPlaceContext(ban.getLevel(), ban.getPlayer(), ban.getHand(), ban.getItemInHand(), new BlockHitResult(new Vec3(ew.getX() + 0.5 + fb.getStepX() * 0.5, ew.getY() + 0.5 + fb.getStepY() * 0.5, ew.getZ() + 0.5 + fb.getStepZ() * 0.5), fb, ew, false));
    }
    
    @Override
    public BlockPos getClickedPos() {
        return this.replaceClicked ? super.getClickedPos() : this.relativePos;
    }
    
    public boolean canPlace() {
        return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced(this);
    }
    
    public boolean replacingClickedOnBlock() {
        return this.replaceClicked;
    }
    
    public Direction getNearestLookingDirection() {
        return Direction.orderedByNearest(this.player)[0];
    }
    
    public Direction[] getNearestLookingDirections() {
        final Direction[] arr2 = Direction.orderedByNearest(this.player);
        if (this.replaceClicked) {
            return arr2;
        }
        Direction fb3;
        int integer4;
        for (fb3 = this.getClickedFace(), integer4 = 0; integer4 < arr2.length && arr2[integer4] != fb3.getOpposite(); ++integer4) {}
        if (integer4 > 0) {
            System.arraycopy(arr2, 0, arr2, 1, integer4);
            arr2[0] = fb3.getOpposite();
        }
        return arr2;
    }
}

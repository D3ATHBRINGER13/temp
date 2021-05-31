package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;

public class DirectionalPlaceContext extends BlockPlaceContext {
    private final Direction direction;
    
    public DirectionalPlaceContext(final Level bhr, final BlockPos ew, final Direction fb3, final ItemStack bcj, final Direction fb5) {
        super(bhr, null, InteractionHand.MAIN_HAND, bcj, new BlockHitResult(new Vec3(ew.getX() + 0.5, ew.getY(), ew.getZ() + 0.5), fb5, ew, false));
        this.direction = fb3;
    }
    
    @Override
    public BlockPos getClickedPos() {
        return this.hitResult.getBlockPos();
    }
    
    @Override
    public boolean canPlace() {
        return this.level.getBlockState(this.hitResult.getBlockPos()).canBeReplaced(this);
    }
    
    @Override
    public boolean replacingClickedOnBlock() {
        return this.canPlace();
    }
    
    @Override
    public Direction getNearestLookingDirection() {
        return Direction.DOWN;
    }
    
    @Override
    public Direction[] getNearestLookingDirections() {
        switch (this.direction) {
            default: {
                return new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP };
            }
            case UP: {
                return new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
            }
            case NORTH: {
                return new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH };
            }
            case SOUTH: {
                return new Direction[] { Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH };
            }
            case WEST: {
                return new Direction[] { Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST };
            }
            case EAST: {
                return new Direction[] { Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST };
            }
        }
    }
    
    @Override
    public Direction getHorizontalDirection() {
        return (this.direction.getAxis() == Direction.Axis.Y) ? Direction.NORTH : this.direction;
    }
    
    @Override
    public boolean isSneaking() {
        return false;
    }
    
    @Override
    public float getRotation() {
        return (float)(this.direction.get2DDataValue() * 90);
    }
}

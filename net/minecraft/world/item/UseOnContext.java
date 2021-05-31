package net.minecraft.world.item;

import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class UseOnContext {
    protected final Player player;
    protected final InteractionHand hand;
    protected final BlockHitResult hitResult;
    protected final Level level;
    protected final ItemStack itemStack;
    
    public UseOnContext(final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        this(awg.level, awg, ahi, awg.getItemInHand(ahi), csd);
    }
    
    protected UseOnContext(final Level bhr, @Nullable final Player awg, final InteractionHand ahi, final ItemStack bcj, final BlockHitResult csd) {
        this.player = awg;
        this.hand = ahi;
        this.hitResult = csd;
        this.itemStack = bcj;
        this.level = bhr;
    }
    
    public BlockPos getClickedPos() {
        return this.hitResult.getBlockPos();
    }
    
    public Direction getClickedFace() {
        return this.hitResult.getDirection();
    }
    
    public Vec3 getClickLocation() {
        return this.hitResult.getLocation();
    }
    
    public boolean isInside() {
        return this.hitResult.isInside();
    }
    
    public ItemStack getItemInHand() {
        return this.itemStack;
    }
    
    @Nullable
    public Player getPlayer() {
        return this.player;
    }
    
    public InteractionHand getHand() {
        return this.hand;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public Direction getHorizontalDirection() {
        return (this.player == null) ? Direction.NORTH : this.player.getDirection();
    }
    
    public boolean isSneaking() {
        return this.player != null && this.player.isSneaking();
    }
    
    public float getRotation() {
        return (this.player == null) ? 0.0f : this.player.yRot;
    }
}

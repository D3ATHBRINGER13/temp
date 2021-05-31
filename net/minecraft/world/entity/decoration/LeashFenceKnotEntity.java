package net.minecraft.world.entity.decoration;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.BlockTags;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class LeashFenceKnotEntity extends HangingEntity {
    public LeashFenceKnotEntity(final EntityType<? extends LeashFenceKnotEntity> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public LeashFenceKnotEntity(final Level bhr, final BlockPos ew) {
        super(EntityType.LEASH_KNOT, bhr, ew);
        this.setPos(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5);
        final float float4 = 0.125f;
        final float float5 = 0.1875f;
        final float float6 = 0.25f;
        this.setBoundingBox(new AABB(this.x - 0.1875, this.y - 0.25 + 0.125, this.z - 0.1875, this.x + 0.1875, this.y + 0.25 + 0.125, this.z + 0.1875));
        this.forcedLoading = true;
    }
    
    @Override
    public void setPos(final double double1, final double double2, final double double3) {
        super.setPos(Mth.floor(double1) + 0.5, Mth.floor(double2) + 0.5, Mth.floor(double3) + 0.5);
    }
    
    @Override
    protected void recalculateBoundingBox() {
        this.x = this.pos.getX() + 0.5;
        this.y = this.pos.getY() + 0.5;
        this.z = this.pos.getZ() + 0.5;
    }
    
    public void setDirection(final Direction fb) {
    }
    
    @Override
    public int getWidth() {
        return 9;
    }
    
    @Override
    public int getHeight() {
        return 9;
    }
    
    @Override
    protected float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return -0.0625f;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        return double1 < 1024.0;
    }
    
    @Override
    public void dropItem(@Nullable final Entity aio) {
        this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
    }
    
    @Override
    public boolean interact(final Player awg, final InteractionHand ahi) {
        if (this.level.isClientSide) {
            return true;
        }
        boolean boolean4 = false;
        final double double5 = 7.0;
        final List<Mob> list7 = this.level.<Mob>getEntitiesOfClass((java.lang.Class<? extends Mob>)Mob.class, new AABB(this.x - 7.0, this.y - 7.0, this.z - 7.0, this.x + 7.0, this.y + 7.0, this.z + 7.0));
        for (final Mob aiy9 : list7) {
            if (aiy9.getLeashHolder() == awg) {
                aiy9.setLeashedTo(this, true);
                boolean4 = true;
            }
        }
        if (!boolean4) {
            this.remove();
            if (awg.abilities.instabuild) {
                for (final Mob aiy9 : list7) {
                    if (aiy9.isLeashed() && aiy9.getLeashHolder() == this) {
                        aiy9.dropLeash(true, false);
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean survives() {
        return this.level.getBlockState(this.pos).getBlock().is(BlockTags.FENCES);
    }
    
    public static LeashFenceKnotEntity getOrCreateKnot(final Level bhr, final BlockPos ew) {
        final int integer3 = ew.getX();
        final int integer4 = ew.getY();
        final int integer5 = ew.getZ();
        final List<LeashFenceKnotEntity> list6 = bhr.<LeashFenceKnotEntity>getEntitiesOfClass((java.lang.Class<? extends LeashFenceKnotEntity>)LeashFenceKnotEntity.class, new AABB(integer3 - 1.0, integer4 - 1.0, integer5 - 1.0, integer3 + 1.0, integer4 + 1.0, integer5 + 1.0));
        for (final LeashFenceKnotEntity ato8 : list6) {
            if (ato8.getPos().equals(ew)) {
                return ato8;
            }
        }
        final LeashFenceKnotEntity ato9 = new LeashFenceKnotEntity(bhr, ew);
        bhr.addFreshEntity(ato9);
        ato9.playPlacementSound();
        return ato9;
    }
    
    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
    }
}

package net.minecraft.world.entity.boss.enderdragon;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import javax.annotation.Nullable;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class EndCrystal extends Entity {
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET;
    private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM;
    public int time;
    
    public EndCrystal(final EntityType<? extends EndCrystal> ais, final Level bhr) {
        super(ais, bhr);
        this.blocksBuilding = true;
        this.time = this.random.nextInt(100000);
    }
    
    public EndCrystal(final Level bhr, final double double2, final double double3, final double double4) {
        this(EntityType.END_CRYSTAL, bhr);
        this.setPos(double2, double3, double4);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<Optional<BlockPos>>define(EndCrystal.DATA_BEAM_TARGET, (Optional<BlockPos>)Optional.empty());
        this.getEntityData().<Boolean>define(EndCrystal.DATA_SHOW_BOTTOM, true);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        ++this.time;
        if (!this.level.isClientSide) {
            final BlockPos ew2 = new BlockPos(this);
            if (this.level.dimension instanceof TheEndDimension && this.level.getBlockState(ew2).isAir()) {
                this.level.setBlockAndUpdate(ew2, Blocks.FIRE.defaultBlockState());
            }
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        if (this.getBeamTarget() != null) {
            id.put("BeamTarget", (Tag)NbtUtils.writeBlockPos(this.getBeamTarget()));
        }
        id.putBoolean("ShowBottom", this.showsBottom());
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        if (id.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtUtils.readBlockPos(id.getCompound("BeamTarget")));
        }
        if (id.contains("ShowBottom", 1)) {
            this.setShowBottom(id.getBoolean("ShowBottom"));
        }
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (ahx.getEntity() instanceof EnderDragon) {
            return false;
        }
        if (!this.removed && !this.level.isClientSide) {
            this.remove();
            if (!ahx.isExplosion()) {
                this.level.explode(null, this.x, this.y, this.z, 6.0f, Explosion.BlockInteraction.DESTROY);
            }
            this.onDestroyedBy(ahx);
        }
        return true;
    }
    
    @Override
    public void kill() {
        this.onDestroyedBy(DamageSource.GENERIC);
        super.kill();
    }
    
    private void onDestroyedBy(final DamageSource ahx) {
        if (this.level.dimension instanceof TheEndDimension) {
            final TheEndDimension bys3 = (TheEndDimension)this.level.dimension;
            final EndDragonFight byr4 = bys3.getDragonFight();
            if (byr4 != null) {
                byr4.onCrystalDestroyed(this, ahx);
            }
        }
    }
    
    public void setBeamTarget(@Nullable final BlockPos ew) {
        this.getEntityData().<Optional<BlockPos>>set(EndCrystal.DATA_BEAM_TARGET, (Optional<BlockPos>)Optional.ofNullable(ew));
    }
    
    @Nullable
    public BlockPos getBeamTarget() {
        return (BlockPos)this.getEntityData().<Optional<BlockPos>>get(EndCrystal.DATA_BEAM_TARGET).orElse(null);
    }
    
    public void setShowBottom(final boolean boolean1) {
        this.getEntityData().<Boolean>set(EndCrystal.DATA_SHOW_BOTTOM, boolean1);
    }
    
    public boolean showsBottom() {
        return this.getEntityData().<Boolean>get(EndCrystal.DATA_SHOW_BOTTOM);
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        return super.shouldRenderAtSqrDistance(double1) || this.getBeamTarget() != null;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    static {
        DATA_BEAM_TARGET = SynchedEntityData.<Optional<BlockPos>>defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        DATA_SHOW_BOTTOM = SynchedEntityData.<Boolean>defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
    }
}

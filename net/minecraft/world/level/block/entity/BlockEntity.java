package net.minecraft.world.level.block.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
    private static final Logger LOGGER;
    private final BlockEntityType<?> type;
    @Nullable
    protected Level level;
    protected BlockPos worldPosition;
    protected boolean remove;
    @Nullable
    private BlockState blockState;
    private boolean hasLoggedInvalidStateBefore;
    
    public BlockEntity(final BlockEntityType<?> btx) {
        this.worldPosition = BlockPos.ZERO;
        this.type = btx;
    }
    
    @Nullable
    public Level getLevel() {
        return this.level;
    }
    
    public void setLevel(final Level bhr) {
        this.level = bhr;
    }
    
    public boolean hasLevel() {
        return this.level != null;
    }
    
    public void load(final CompoundTag id) {
        this.worldPosition = new BlockPos(id.getInt("x"), id.getInt("y"), id.getInt("z"));
    }
    
    public CompoundTag save(final CompoundTag id) {
        return this.saveMetadata(id);
    }
    
    private CompoundTag saveMetadata(final CompoundTag id) {
        final ResourceLocation qv3 = BlockEntityType.getKey(this.getType());
        if (qv3 == null) {
            throw new RuntimeException(new StringBuilder().append(this.getClass()).append(" is missing a mapping! This is a bug!").toString());
        }
        id.putString("id", qv3.toString());
        id.putInt("x", this.worldPosition.getX());
        id.putInt("y", this.worldPosition.getY());
        id.putInt("z", this.worldPosition.getZ());
        return id;
    }
    
    @Nullable
    public static BlockEntity loadStatic(final CompoundTag id) {
        final String string2 = id.getString("id");
        return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(string2)).map(btx -> {
            try {
                return btx.create();
            }
            catch (Throwable throwable3) {
                BlockEntity.LOGGER.error("Failed to create block entity {}", string2, throwable3);
                return null;
            }
        }).map(btw -> {
            try {
                btw.load(id);
                return btw;
            }
            catch (Throwable throwable4) {
                BlockEntity.LOGGER.error("Failed to load data for block entity {}", string2, throwable4);
                return null;
            }
        }).orElseGet(() -> {
            BlockEntity.LOGGER.warn("Skipping BlockEntity with id {}", string2);
            return null;
        });
    }
    
    public void setChanged() {
        if (this.level != null) {
            this.blockState = this.level.getBlockState(this.worldPosition);
            this.level.blockEntityChanged(this.worldPosition, this);
            if (!this.blockState.isAir()) {
                this.level.updateNeighbourForOutputSignal(this.worldPosition, this.blockState.getBlock());
            }
        }
    }
    
    public double distanceToSqr(final double double1, final double double2, final double double3) {
        final double double4 = this.worldPosition.getX() + 0.5 - double1;
        final double double5 = this.worldPosition.getY() + 0.5 - double2;
        final double double6 = this.worldPosition.getZ() + 0.5 - double3;
        return double4 * double4 + double5 * double5 + double6 * double6;
    }
    
    public double getViewDistance() {
        return 4096.0;
    }
    
    public BlockPos getBlockPos() {
        return this.worldPosition;
    }
    
    public BlockState getBlockState() {
        if (this.blockState == null) {
            this.blockState = this.level.getBlockState(this.worldPosition);
        }
        return this.blockState;
    }
    
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return null;
    }
    
    public CompoundTag getUpdateTag() {
        return this.saveMetadata(new CompoundTag());
    }
    
    public boolean isRemoved() {
        return this.remove;
    }
    
    public void setRemoved() {
        this.remove = true;
    }
    
    public void clearRemoved() {
        this.remove = false;
    }
    
    public boolean triggerEvent(final int integer1, final int integer2) {
        return false;
    }
    
    public void clearCache() {
        this.blockState = null;
    }
    
    public void fillCrashReportCategory(final CrashReportCategory e) {
        e.setDetail("Name", (CrashReportDetail<String>)(() -> new StringBuilder().append(Registry.BLOCK_ENTITY_TYPE.getKey(this.getType())).append(" // ").append(this.getClass().getCanonicalName()).toString()));
        if (this.level == null) {
            return;
        }
        CrashReportCategory.populateBlockDetails(e, this.worldPosition, this.getBlockState());
        CrashReportCategory.populateBlockDetails(e, this.worldPosition, this.level.getBlockState(this.worldPosition));
    }
    
    public void setPosition(final BlockPos ew) {
        this.worldPosition = ew.immutable();
    }
    
    public boolean onlyOpCanSetNbt() {
        return false;
    }
    
    public void rotate(final Rotation brg) {
    }
    
    public void mirror(final Mirror bqg) {
    }
    
    public BlockEntityType<?> getType() {
        return this.type;
    }
    
    public void logInvalidState() {
        if (this.hasLoggedInvalidStateBefore) {
            return;
        }
        this.hasLoggedInvalidStateBefore = true;
        BlockEntity.LOGGER.warn("Block entity invalid: {} @ {}", new Supplier[] { () -> Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()), this::getBlockPos });
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

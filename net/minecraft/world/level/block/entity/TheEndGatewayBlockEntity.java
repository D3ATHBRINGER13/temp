package net.minecraft.world.level.block.entity;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.Logger;

public class TheEndGatewayBlockEntity extends TheEndPortalBlockEntity implements TickableBlockEntity {
    private static final Logger LOGGER;
    private long age;
    private int teleportCooldown;
    private BlockPos exitPortal;
    private boolean exactTeleport;
    
    public TheEndGatewayBlockEntity() {
        super(BlockEntityType.END_GATEWAY);
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putLong("Age", this.age);
        if (this.exitPortal != null) {
            id.put("ExitPortal", (Tag)NbtUtils.writeBlockPos(this.exitPortal));
        }
        if (this.exactTeleport) {
            id.putBoolean("ExactTeleport", this.exactTeleport);
        }
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.age = id.getLong("Age");
        if (id.contains("ExitPortal", 10)) {
            this.exitPortal = NbtUtils.readBlockPos(id.getCompound("ExitPortal"));
        }
        this.exactTeleport = id.getBoolean("ExactTeleport");
    }
    
    @Override
    public double getViewDistance() {
        return 65536.0;
    }
    
    @Override
    public void tick() {
        final boolean boolean2 = this.isSpawning();
        final boolean boolean3 = this.isCoolingDown();
        ++this.age;
        if (boolean3) {
            --this.teleportCooldown;
        }
        else if (!this.level.isClientSide) {
            final List<Entity> list4 = this.level.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)Entity.class, new AABB(this.getBlockPos()));
            if (!list4.isEmpty()) {
                this.teleportEntity((Entity)list4.get(0));
            }
            if (this.age % 2400L == 0L) {
                this.triggerCooldown();
            }
        }
        if (boolean2 != this.isSpawning() || boolean3 != this.isCoolingDown()) {
            this.setChanged();
        }
    }
    
    public boolean isSpawning() {
        return this.age < 200L;
    }
    
    public boolean isCoolingDown() {
        return this.teleportCooldown > 0;
    }
    
    public float getSpawnPercent(final float float1) {
        return Mth.clamp((this.age + float1) / 200.0f, 0.0f, 1.0f);
    }
    
    public float getCooldownPercent(final float float1) {
        return 1.0f - Mth.clamp((this.teleportCooldown - float1) / 40.0f, 0.0f, 1.0f);
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 8, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    public void triggerCooldown() {
        if (!this.level.isClientSide) {
            this.teleportCooldown = 40;
            this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, 0);
            this.setChanged();
        }
    }
    
    @Override
    public boolean triggerEvent(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.teleportCooldown = 40;
            return true;
        }
        return super.triggerEvent(integer1, integer2);
    }
    
    public void teleportEntity(final Entity aio) {
        if (this.level.isClientSide || this.isCoolingDown()) {
            return;
        }
        this.teleportCooldown = 100;
        if (this.exitPortal == null && this.level.dimension instanceof TheEndDimension) {
            this.findExitPortal();
        }
        if (this.exitPortal != null) {
            final BlockPos ew3 = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            aio.teleportToWithTicket(ew3.getX() + 0.5, ew3.getY() + 0.5, ew3.getZ() + 0.5);
        }
        this.triggerCooldown();
    }
    
    private BlockPos findExitPosition() {
        final BlockPos ew2 = findTallestBlock(this.level, this.exitPortal, 5, false);
        TheEndGatewayBlockEntity.LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, ew2);
        return ew2.above();
    }
    
    private void findExitPortal() {
        final Vec3 csi2 = new Vec3(this.getBlockPos().getX(), 0.0, this.getBlockPos().getZ()).normalize();
        Vec3 csi3 = csi2.scale(1024.0);
        for (int integer4 = 16; getChunk(this.level, csi3).getHighestSectionPosition() > 0 && integer4-- > 0; csi3 = csi3.add(csi2.scale(-16.0))) {
            TheEndGatewayBlockEntity.LOGGER.debug("Skipping backwards past nonempty chunk at {}", csi3);
        }
        for (int integer4 = 16; getChunk(this.level, csi3).getHighestSectionPosition() == 0 && integer4-- > 0; csi3 = csi3.add(csi2.scale(16.0))) {
            TheEndGatewayBlockEntity.LOGGER.debug("Skipping forward past empty chunk at {}", csi3);
        }
        TheEndGatewayBlockEntity.LOGGER.debug("Found chunk at {}", csi3);
        final LevelChunk bxt5 = getChunk(this.level, csi3);
        this.exitPortal = findValidSpawnInChunk(bxt5);
        if (this.exitPortal == null) {
            this.exitPortal = new BlockPos(csi3.x + 0.5, 75.0, csi3.z + 0.5);
            TheEndGatewayBlockEntity.LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortal);
            Feature.END_ISLAND.place(this.level, this.level.getChunkSource().getGenerator(), new Random(this.exitPortal.asLong()), this.exitPortal, FeatureConfiguration.NONE);
        }
        else {
            TheEndGatewayBlockEntity.LOGGER.debug("Found block at {}", this.exitPortal);
        }
        this.exitPortal = findTallestBlock(this.level, this.exitPortal, 16, true);
        TheEndGatewayBlockEntity.LOGGER.debug("Creating portal at {}", this.exitPortal);
        this.createExitPortal(this.exitPortal = this.exitPortal.above(10));
        this.setChanged();
    }
    
    private static BlockPos findTallestBlock(final BlockGetter bhb, final BlockPos ew, final int integer, final boolean boolean4) {
        BlockPos ew2 = null;
        for (int integer2 = -integer; integer2 <= integer; ++integer2) {
            for (int integer3 = -integer; integer3 <= integer; ++integer3) {
                if (integer2 != 0 || integer3 != 0 || boolean4) {
                    for (int integer4 = 255; integer4 > ((ew2 == null) ? 0 : ew2.getY()); --integer4) {
                        final BlockPos ew3 = new BlockPos(ew.getX() + integer2, integer4, ew.getZ() + integer3);
                        final BlockState bvt10 = bhb.getBlockState(ew3);
                        if (bvt10.isCollisionShapeFullBlock(bhb, ew3) && (boolean4 || bvt10.getBlock() != Blocks.BEDROCK)) {
                            ew2 = ew3;
                            break;
                        }
                    }
                }
            }
        }
        return (ew2 == null) ? ew : ew2;
    }
    
    private static LevelChunk getChunk(final Level bhr, final Vec3 csi) {
        return bhr.getChunk(Mth.floor(csi.x / 16.0), Mth.floor(csi.z / 16.0));
    }
    
    @Nullable
    private static BlockPos findValidSpawnInChunk(final LevelChunk bxt) {
        final ChunkPos bhd2 = bxt.getPos();
        final BlockPos ew3 = new BlockPos(bhd2.getMinBlockX(), 30, bhd2.getMinBlockZ());
        final int integer4 = bxt.getHighestSectionPosition() + 16 - 1;
        final BlockPos ew4 = new BlockPos(bhd2.getMaxBlockX(), integer4, bhd2.getMaxBlockZ());
        BlockPos ew5 = null;
        double double7 = 0.0;
        for (final BlockPos ew6 : BlockPos.betweenClosed(ew3, ew4)) {
            final BlockState bvt11 = bxt.getBlockState(ew6);
            final BlockPos ew7 = ew6.above();
            final BlockPos ew8 = ew6.above(2);
            if (bvt11.getBlock() == Blocks.END_STONE && !bxt.getBlockState(ew7).isCollisionShapeFullBlock(bxt, ew7) && !bxt.getBlockState(ew8).isCollisionShapeFullBlock(bxt, ew8)) {
                final double double8 = ew6.distSqr(0.0, 0.0, 0.0, true);
                if (ew5 != null && double8 >= double7) {
                    continue;
                }
                ew5 = ew6;
                double7 = double8;
            }
        }
        return ew5;
    }
    
    private void createExitPortal(final BlockPos ew) {
        Feature.END_GATEWAY.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), ew, EndGatewayConfiguration.knownExit(this.getBlockPos(), false));
    }
    
    @Override
    public boolean shouldRenderFace(final Direction fb) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), fb);
    }
    
    public int getParticleAmount() {
        int integer2 = 0;
        for (final Direction fb6 : Direction.values()) {
            integer2 += (this.shouldRenderFace(fb6) ? 1 : 0);
        }
        return integer2;
    }
    
    public void setExitPosition(final BlockPos ew, final boolean boolean2) {
        this.exactTeleport = boolean2;
        this.exitPortal = ew;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

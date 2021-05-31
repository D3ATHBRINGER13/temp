package net.minecraft.world.level;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.server.level.TicketType;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.server.level.ColumnPos;
import java.util.Map;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.NetherPortalBlock;
import org.apache.logging.log4j.Logger;

public class PortalForcer {
    private static final Logger LOGGER;
    private static final NetherPortalBlock PORTAL_BLOCK;
    private final ServerLevel level;
    private final Random random;
    private final Map<ColumnPos, PortalPosition> cachedPortals;
    private final Object2LongMap<ColumnPos> negativeChecks;
    
    public PortalForcer(final ServerLevel vk) {
        this.cachedPortals = (Map<ColumnPos, PortalPosition>)Maps.newHashMapWithExpectedSize(4096);
        this.negativeChecks = (Object2LongMap<ColumnPos>)new Object2LongOpenHashMap();
        this.level = vk;
        this.random = new Random(vk.getSeed());
    }
    
    public boolean findAndMoveToPortal(final Entity aio, final float float2) {
        final Vec3 csi4 = aio.getPortalEntranceOffset();
        final Direction fb5 = aio.getPortalEntranceForwards();
        final BlockPattern.PortalInfo c6 = this.findPortal(new BlockPos(aio), aio.getDeltaMovement(), fb5, csi4.x, csi4.y, aio instanceof Player);
        if (c6 == null) {
            return false;
        }
        final Vec3 csi5 = c6.pos;
        final Vec3 csi6 = c6.speed;
        aio.setDeltaMovement(csi6);
        aio.yRot = float2 + c6.angle;
        if (aio instanceof ServerPlayer) {
            ((ServerPlayer)aio).connection.teleport(csi5.x, csi5.y, csi5.z, aio.yRot, aio.xRot);
            ((ServerPlayer)aio).connection.resetPosition();
        }
        else {
            aio.moveTo(csi5.x, csi5.y, csi5.z, aio.yRot, aio.xRot);
        }
        return true;
    }
    
    @Nullable
    public BlockPattern.PortalInfo findPortal(final BlockPos ew, final Vec3 csi, final Direction fb, final double double4, final double double5, final boolean boolean6) {
        final int integer10 = 128;
        boolean boolean7 = true;
        BlockPos ew2 = null;
        final ColumnPos va13 = new ColumnPos(ew);
        if (!boolean6 && this.negativeChecks.containsKey(va13)) {
            return null;
        }
        final PortalPosition a14 = (PortalPosition)this.cachedPortals.get(va13);
        if (a14 != null) {
            ew2 = a14.pos;
            a14.lastUsed = this.level.getGameTime();
            boolean7 = false;
        }
        else {
            double double6 = Double.MAX_VALUE;
            for (int integer11 = -128; integer11 <= 128; ++integer11) {
                for (int integer12 = -128; integer12 <= 128; ++integer12) {
                    BlockPos ew4;
                    for (BlockPos ew3 = ew.offset(integer11, this.level.getHeight() - 1 - ew.getY(), integer12); ew3.getY() >= 0; ew3 = ew4) {
                        ew4 = ew3.below();
                        if (this.level.getBlockState(ew3).getBlock() == PortalForcer.PORTAL_BLOCK) {
                            for (ew4 = ew3.below(); this.level.getBlockState(ew4).getBlock() == PortalForcer.PORTAL_BLOCK; ew4 = ew3.below()) {
                                ew3 = ew4;
                            }
                            final double double7 = ew3.distSqr(ew);
                            if (double6 < 0.0 || double7 < double6) {
                                double6 = double7;
                                ew2 = ew3;
                            }
                        }
                    }
                }
            }
        }
        if (ew2 == null) {
            final long long15 = this.level.getGameTime() + 300L;
            this.negativeChecks.put(va13, long15);
            return null;
        }
        if (boolean7) {
            this.cachedPortals.put(va13, new PortalPosition(ew2, this.level.getGameTime()));
            PortalForcer.LOGGER.debug("Adding nether portal ticket for {}:{}", new Supplier[] { this.level.getDimension()::getType, () -> va13 });
            this.level.getChunkSource().<ColumnPos>addRegionTicket(TicketType.PORTAL, new ChunkPos(ew2), 3, va13);
        }
        final BlockPattern.BlockPatternMatch b15 = PortalForcer.PORTAL_BLOCK.getPortalShape(this.level, ew2);
        return b15.getPortalOutput(fb, ew2, double5, csi, double4);
    }
    
    public boolean createPortal(final Entity aio) {
        final int integer3 = 16;
        double double4 = -1.0;
        final int integer4 = Mth.floor(aio.x);
        final int integer5 = Mth.floor(aio.y);
        final int integer6 = Mth.floor(aio.z);
        int integer7 = integer4;
        int integer8 = integer5;
        int integer9 = integer6;
        int integer10 = 0;
        final int integer11 = this.random.nextInt(4);
        final BlockPos.MutableBlockPos a14 = new BlockPos.MutableBlockPos();
        for (int integer12 = integer4 - 16; integer12 <= integer4 + 16; ++integer12) {
            final double double5 = integer12 + 0.5 - aio.x;
            for (int integer13 = integer6 - 16; integer13 <= integer6 + 16; ++integer13) {
                final double double6 = integer13 + 0.5 - aio.z;
            Label_0463:
                for (int integer14 = this.level.getHeight() - 1; integer14 >= 0; --integer14) {
                    if (this.level.isEmptyBlock(a14.set(integer12, integer14, integer13))) {
                        while (integer14 > 0 && this.level.isEmptyBlock(a14.set(integer12, integer14 - 1, integer13))) {
                            --integer14;
                        }
                        for (int integer15 = integer11; integer15 < integer11 + 4; ++integer15) {
                            int integer16 = integer15 % 2;
                            int integer17 = 1 - integer16;
                            if (integer15 % 4 >= 2) {
                                integer16 = -integer16;
                                integer17 = -integer17;
                            }
                            for (int integer18 = 0; integer18 < 3; ++integer18) {
                                for (int integer19 = 0; integer19 < 4; ++integer19) {
                                    for (int integer20 = -1; integer20 < 4; ++integer20) {
                                        final int integer21 = integer12 + (integer19 - 1) * integer16 + integer18 * integer17;
                                        final int integer22 = integer14 + integer20;
                                        final int integer23 = integer13 + (integer19 - 1) * integer17 - integer18 * integer16;
                                        a14.set(integer21, integer22, integer23);
                                        if (integer20 < 0 && !this.level.getBlockState(a14).getMaterial().isSolid()) {
                                            continue Label_0463;
                                        }
                                        if (integer20 >= 0 && !this.level.isEmptyBlock(a14)) {
                                            continue Label_0463;
                                        }
                                    }
                                }
                            }
                            final double double7 = integer14 + 0.5 - aio.y;
                            final double double8 = double5 * double5 + double7 * double7 + double6 * double6;
                            if (double4 < 0.0 || double8 < double4) {
                                double4 = double8;
                                integer7 = integer12;
                                integer8 = integer14;
                                integer9 = integer13;
                                integer10 = integer15 % 4;
                            }
                        }
                    }
                }
            }
        }
        if (double4 < 0.0) {
            for (int integer12 = integer4 - 16; integer12 <= integer4 + 16; ++integer12) {
                final double double5 = integer12 + 0.5 - aio.x;
                for (int integer13 = integer6 - 16; integer13 <= integer6 + 16; ++integer13) {
                    final double double6 = integer13 + 0.5 - aio.z;
                Label_0837:
                    for (int integer14 = this.level.getHeight() - 1; integer14 >= 0; --integer14) {
                        if (this.level.isEmptyBlock(a14.set(integer12, integer14, integer13))) {
                            while (integer14 > 0 && this.level.isEmptyBlock(a14.set(integer12, integer14 - 1, integer13))) {
                                --integer14;
                            }
                            for (int integer15 = integer11; integer15 < integer11 + 2; ++integer15) {
                                final int integer16 = integer15 % 2;
                                final int integer17 = 1 - integer16;
                                for (int integer18 = 0; integer18 < 4; ++integer18) {
                                    for (int integer19 = -1; integer19 < 4; ++integer19) {
                                        final int integer20 = integer12 + (integer18 - 1) * integer16;
                                        final int integer21 = integer14 + integer19;
                                        final int integer22 = integer13 + (integer18 - 1) * integer17;
                                        a14.set(integer20, integer21, integer22);
                                        if (integer19 < 0 && !this.level.getBlockState(a14).getMaterial().isSolid()) {
                                            continue Label_0837;
                                        }
                                        if (integer19 >= 0 && !this.level.isEmptyBlock(a14)) {
                                            continue Label_0837;
                                        }
                                    }
                                }
                                final double double7 = integer14 + 0.5 - aio.y;
                                final double double8 = double5 * double5 + double7 * double7 + double6 * double6;
                                if (double4 < 0.0 || double8 < double4) {
                                    double4 = double8;
                                    integer7 = integer12;
                                    integer8 = integer14;
                                    integer9 = integer13;
                                    integer10 = integer15 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        int integer12 = integer10;
        final int integer24 = integer7;
        int integer25 = integer8;
        int integer13 = integer9;
        int integer26 = integer12 % 2;
        int integer27 = 1 - integer26;
        if (integer12 % 4 >= 2) {
            integer26 = -integer26;
            integer27 = -integer27;
        }
        if (double4 < 0.0) {
            integer8 = (integer25 = Mth.clamp(integer8, 70, this.level.getHeight() - 10));
            for (int integer14 = -1; integer14 <= 1; ++integer14) {
                for (int integer15 = 1; integer15 < 3; ++integer15) {
                    for (int integer16 = -1; integer16 < 3; ++integer16) {
                        final int integer17 = integer24 + (integer15 - 1) * integer26 + integer14 * integer27;
                        final int integer18 = integer25 + integer16;
                        final int integer19 = integer13 + (integer15 - 1) * integer27 - integer14 * integer26;
                        final boolean boolean27 = integer16 < 0;
                        a14.set(integer17, integer18, integer19);
                        this.level.setBlockAndUpdate(a14, boolean27 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        for (int integer14 = -1; integer14 < 3; ++integer14) {
            for (int integer15 = -1; integer15 < 4; ++integer15) {
                if (integer14 == -1 || integer14 == 2 || integer15 == -1 || integer15 == 3) {
                    a14.set(integer24 + integer14 * integer26, integer25 + integer15, integer13 + integer14 * integer27);
                    this.level.setBlock(a14, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }
        final BlockState bvt21 = ((AbstractStateHolder<O, BlockState>)PortalForcer.PORTAL_BLOCK.defaultBlockState()).<Comparable, Direction.Axis>setValue((Property<Comparable>)NetherPortalBlock.AXIS, (integer26 == 0) ? Direction.Axis.Z : Direction.Axis.X);
        for (int integer15 = 0; integer15 < 2; ++integer15) {
            for (int integer16 = 0; integer16 < 3; ++integer16) {
                a14.set(integer24 + integer15 * integer26, integer25 + integer16, integer13 + integer15 * integer27);
                this.level.setBlock(a14, bvt21, 18);
            }
        }
        return true;
    }
    
    public void tick(final long long1) {
        if (long1 % 100L == 0L) {
            this.purgeNegativeChecks(long1);
            this.clearStaleCacheEntries(long1);
        }
    }
    
    private void purgeNegativeChecks(final long long1) {
        final LongIterator longIterator4 = this.negativeChecks.values().iterator();
        while (longIterator4.hasNext()) {
            final long long2 = longIterator4.nextLong();
            if (long2 <= long1) {
                longIterator4.remove();
            }
        }
    }
    
    private void clearStaleCacheEntries(final long long1) {
        final long long2 = long1 - 300L;
        final Iterator<Map.Entry<ColumnPos, PortalPosition>> iterator6 = (Iterator<Map.Entry<ColumnPos, PortalPosition>>)this.cachedPortals.entrySet().iterator();
        while (iterator6.hasNext()) {
            final Map.Entry<ColumnPos, PortalPosition> entry7 = (Map.Entry<ColumnPos, PortalPosition>)iterator6.next();
            final PortalPosition a8 = (PortalPosition)entry7.getValue();
            if (a8.lastUsed < long2) {
                final ColumnPos va9 = (ColumnPos)entry7.getKey();
                PortalForcer.LOGGER.debug("Removing nether portal ticket for {}:{}", new Supplier[] { this.level.getDimension()::getType, () -> va9 });
                this.level.getChunkSource().<ColumnPos>removeRegionTicket(TicketType.PORTAL, new ChunkPos(a8.pos), 3, va9);
                iterator6.remove();
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PORTAL_BLOCK = (NetherPortalBlock)Blocks.NETHER_PORTAL;
    }
    
    static class PortalPosition {
        public final BlockPos pos;
        public long lastUsed;
        
        public PortalPosition(final BlockPos ew, final long long2) {
            this.pos = ew;
            this.lastUsed = long2;
        }
    }
}

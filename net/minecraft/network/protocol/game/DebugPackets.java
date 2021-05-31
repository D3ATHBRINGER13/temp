package net.minecraft.network.protocol.game;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import java.util.Collection;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.Logger;

public class DebugPackets {
    private static final Logger LOGGER;
    
    public static void sendPoiPacketsForChunk(final ServerLevel vk, final ChunkPos bhd) {
    }
    
    public static void sendPoiAddedPacket(final ServerLevel vk, final BlockPos ew) {
    }
    
    public static void sendPoiRemovedPacket(final ServerLevel vk, final BlockPos ew) {
    }
    
    public static void sendPoiTicketCountPacket(final ServerLevel vk, final BlockPos ew) {
    }
    
    public static void sendPathFindingPacket(final Level bhr, final Mob aiy, @Nullable final Path cnr, final float float4) {
    }
    
    public static void sendNeighborsUpdatePacket(final Level bhr, final BlockPos ew) {
    }
    
    public static void sendStructurePacket(final LevelAccessor bhs, final StructureStart ciw) {
    }
    
    public static void sendGoalSelector(final Level bhr, final Mob aiy, final GoalSelector anf) {
    }
    
    public static void sendRaids(final ServerLevel vk, final Collection<Raid> collection) {
    }
    
    public static void sendEntityBrain(final LivingEntity aix) {
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.world.level.dimension.end;

import net.minecraft.world.entity.EntitySelector;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.ChunkStatus;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.TicketType;
import java.util.Iterator;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import java.util.Collections;
import java.util.Random;
import java.util.Collection;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.minecraft.nbt.NbtUtils;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.core.BlockPos;
import java.util.UUID;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;
import org.apache.logging.log4j.Logger;

public class EndDragonFight {
    private static final Logger LOGGER;
    private static final Predicate<Entity> VALID_PLAYER;
    private final ServerBossEvent dragonEvent;
    private final ServerLevel level;
    private final List<Integer> gateways;
    private final BlockPattern exitPortalPattern;
    private int ticksSinceDragonSeen;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    private UUID dragonUUID;
    private boolean needsStateScanning;
    private BlockPos portalLocation;
    private DragonRespawnAnimation respawnStage;
    private int respawnTime;
    private List<EndCrystal> respawnCrystals;
    
    public EndDragonFight(final ServerLevel vk, final CompoundTag id) {
        this.dragonEvent = (ServerBossEvent)new ServerBossEvent(new TranslatableComponent("entity.minecraft.ender_dragon", new Object[0]), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS).setPlayBossMusic(true).setCreateWorldFog(true);
        this.gateways = (List<Integer>)Lists.newArrayList();
        this.needsStateScanning = true;
        this.level = vk;
        if (id.contains("DragonKilled", 99)) {
            if (id.hasUUID("DragonUUID")) {
                this.dragonUUID = id.getUUID("DragonUUID");
            }
            this.dragonKilled = id.getBoolean("DragonKilled");
            this.previouslyKilled = id.getBoolean("PreviouslyKilled");
            if (id.getBoolean("IsRespawning")) {
                this.respawnStage = DragonRespawnAnimation.START;
            }
            if (id.contains("ExitPortalLocation", 10)) {
                this.portalLocation = NbtUtils.readBlockPos(id.getCompound("ExitPortalLocation"));
            }
        }
        else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if (id.contains("Gateways", 9)) {
            final ListTag ik4 = id.getList("Gateways", 3);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                this.gateways.add(ik4.getInt(integer5));
            }
        }
        else {
            this.gateways.addAll((Collection)ContiguousSet.create(Range.closedOpen((Comparable)0, (Comparable)20), DiscreteDomain.integers()));
            Collections.shuffle((List)this.gateways, new Random(vk.getSeed()));
        }
        this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockPredicate.forBlock(Blocks.BEDROCK))).build();
    }
    
    public CompoundTag saveData() {
        final CompoundTag id2 = new CompoundTag();
        if (this.dragonUUID != null) {
            id2.putUUID("DragonUUID", this.dragonUUID);
        }
        id2.putBoolean("DragonKilled", this.dragonKilled);
        id2.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.portalLocation != null) {
            id2.put("ExitPortalLocation", (Tag)NbtUtils.writeBlockPos(this.portalLocation));
        }
        final ListTag ik3 = new ListTag();
        for (final int integer5 : this.gateways) {
            ik3.add(new IntTag(integer5));
        }
        id2.put("Gateways", (Tag)ik3);
        return id2;
    }
    
    public void tick() {
        this.dragonEvent.setVisible(!this.dragonKilled);
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }
        if (!this.dragonEvent.getPlayers().isEmpty()) {
            this.level.getChunkSource().<Unit>addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            final boolean boolean2 = this.isArenaLoaded();
            if (this.needsStateScanning && boolean2) {
                this.scanState();
                this.needsStateScanning = false;
            }
            if (this.respawnStage != null) {
                if (this.respawnCrystals == null && boolean2) {
                    this.respawnStage = null;
                    this.tryRespawn();
                }
                this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && boolean2) {
                    this.findOrCreateDragon();
                    this.ticksSinceDragonSeen = 0;
                }
                if (++this.ticksSinceCrystalsScanned >= 100 && boolean2) {
                    this.updateCrystalCount();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        }
        else {
            this.level.getChunkSource().<Unit>removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }
    }
    
    private void scanState() {
        EndDragonFight.LOGGER.info("Scanning for legacy world dragon fight...");
        final boolean boolean2 = this.hasExitPortal();
        if (boolean2) {
            EndDragonFight.LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        }
        else {
            EndDragonFight.LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.spawnExitPortal(this.previouslyKilled = false);
        }
        final List<EnderDragon> list3 = this.level.getDragons();
        if (list3.isEmpty()) {
            this.dragonKilled = true;
        }
        else {
            final EnderDragon asp4 = (EnderDragon)list3.get(0);
            this.dragonUUID = asp4.getUUID();
            EndDragonFight.LOGGER.info("Found that there's a dragon still alive ({})", asp4);
            this.dragonKilled = false;
            if (!boolean2) {
                EndDragonFight.LOGGER.info("But we didn't have a portal, let's remove it.");
                asp4.remove();
                this.dragonUUID = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }
    
    private void findOrCreateDragon() {
        final List<EnderDragon> list2 = this.level.getDragons();
        if (list2.isEmpty()) {
            EndDragonFight.LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createNewDragon();
        }
        else {
            EndDragonFight.LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUUID = ((EnderDragon)list2.get(0)).getUUID();
        }
    }
    
    protected void setRespawnStage(final DragonRespawnAnimation byq) {
        if (this.respawnStage == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.respawnTime = 0;
        if (byq == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            final EnderDragon asp3 = this.createNewDragon();
            for (final ServerPlayer vl5 : this.dragonEvent.getPlayers()) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(vl5, asp3);
            }
        }
        else {
            this.respawnStage = byq;
        }
    }
    
    private boolean hasExitPortal() {
        for (int integer2 = -8; integer2 <= 8; ++integer2) {
            for (int integer3 = -8; integer3 <= 8; ++integer3) {
                final LevelChunk bxt4 = this.level.getChunk(integer2, integer3);
                for (final BlockEntity btw6 : bxt4.getBlockEntities().values()) {
                    if (btw6 instanceof TheEndPortalBlockEntity) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Nullable
    private BlockPattern.BlockPatternMatch findExitPortal() {
        for (int integer2 = -8; integer2 <= 8; ++integer2) {
            for (int integer3 = -8; integer3 <= 8; ++integer3) {
                final LevelChunk bxt4 = this.level.getChunk(integer2, integer3);
                for (final BlockEntity btw6 : bxt4.getBlockEntities().values()) {
                    if (btw6 instanceof TheEndPortalBlockEntity) {
                        final BlockPattern.BlockPatternMatch b7 = this.exitPortalPattern.find(this.level, btw6.getBlockPos());
                        if (b7 != null) {
                            final BlockPos ew8 = b7.getBlock(3, 3, 3).getPos();
                            if (this.portalLocation == null && ew8.getX() == 0 && ew8.getZ() == 0) {
                                this.portalLocation = ew8;
                            }
                            return b7;
                        }
                        continue;
                    }
                }
            }
        }
        int integer3;
        for (int integer2 = integer3 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY(); integer3 >= 0; --integer3) {
            final BlockPattern.BlockPatternMatch b8 = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), integer3, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
            if (b8 != null) {
                if (this.portalLocation == null) {
                    this.portalLocation = b8.getBlock(3, 3, 3).getPos();
                }
                return b8;
            }
        }
        return null;
    }
    
    private boolean isArenaLoaded() {
        for (int integer2 = -8; integer2 <= 8; ++integer2) {
            for (int integer3 = 8; integer3 <= 8; ++integer3) {
                final ChunkAccess bxh4 = this.level.getChunk(integer2, integer3, ChunkStatus.FULL, false);
                if (!(bxh4 instanceof LevelChunk)) {
                    return false;
                }
                final ChunkHolder.FullChunkStatus b5 = ((LevelChunk)bxh4).getFullStatus();
                if (!b5.isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void updatePlayers() {
        final Set<ServerPlayer> set2 = (Set<ServerPlayer>)Sets.newHashSet();
        for (final ServerPlayer vl4 : this.level.getPlayers(EndDragonFight.VALID_PLAYER)) {
            this.dragonEvent.addPlayer(vl4);
            set2.add(vl4);
        }
        final Set<ServerPlayer> set3 = (Set<ServerPlayer>)Sets.newHashSet((Iterable)this.dragonEvent.getPlayers());
        set3.removeAll((Collection)set2);
        for (final ServerPlayer vl5 : set3) {
            this.dragonEvent.removePlayer(vl5);
        }
    }
    
    private void updateCrystalCount() {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = 0;
        for (final SpikeFeature.EndSpike a3 : SpikeFeature.getSpikesForLevel(this.level)) {
            this.crystalsAlive += this.level.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)EndCrystal.class, a3.getTopBoundingBox()).size();
        }
        EndDragonFight.LOGGER.debug("Found {} end crystals still alive", this.crystalsAlive);
    }
    
    public void setDragonKilled(final EnderDragon asp) {
        if (asp.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setPercent(0.0f);
            this.dragonEvent.setVisible(false);
            this.spawnExitPortal(true);
            this.spawnNewGateway();
            if (!this.previouslyKilled) {
                this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }
    
    private void spawnNewGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        final int integer2 = (int)this.gateways.remove(this.gateways.size() - 1);
        final int integer3 = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * integer2)));
        final int integer4 = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * integer2)));
        this.spawnNewGateway(new BlockPos(integer3, 75, integer4));
    }
    
    private void spawnNewGateway(final BlockPos ew) {
        this.level.levelEvent(3000, ew, 0);
        Feature.END_GATEWAY.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), ew, EndGatewayConfiguration.delayedExitSearch());
    }
    
    private void spawnExitPortal(final boolean boolean1) {
        final EndPodiumFeature cbm3 = new EndPodiumFeature(boolean1);
        if (this.portalLocation == null) {
            this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below();
            while (this.level.getBlockState(this.portalLocation).getBlock() == Blocks.BEDROCK && this.portalLocation.getY() > this.level.getSeaLevel()) {
                this.portalLocation = this.portalLocation.below();
            }
        }
        cbm3.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), this.portalLocation, FeatureConfiguration.NONE);
    }
    
    private EnderDragon createNewDragon() {
        this.level.getChunkAt(new BlockPos(0, 128, 0));
        final EnderDragon asp2 = EntityType.ENDER_DRAGON.create(this.level);
        asp2.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        asp2.moveTo(0.0, 128.0, 0.0, this.level.random.nextFloat() * 360.0f, 0.0f);
        this.level.addFreshEntity(asp2);
        this.dragonUUID = asp2.getUUID();
        return asp2;
    }
    
    public void updateDragon(final EnderDragon asp) {
        if (asp.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setPercent(asp.getHealth() / asp.getMaxHealth());
            this.ticksSinceDragonSeen = 0;
            if (asp.hasCustomName()) {
                this.dragonEvent.setName(asp.getDisplayName());
            }
        }
    }
    
    public int getCrystalsAlive() {
        return this.crystalsAlive;
    }
    
    public void onCrystalDestroyed(final EndCrystal aso, final DamageSource ahx) {
        if (this.respawnStage != null && this.respawnCrystals.contains(aso)) {
            EndDragonFight.LOGGER.debug("Aborting respawn sequence");
            this.respawnStage = null;
            this.respawnTime = 0;
            this.resetSpikeCrystals();
            this.spawnExitPortal(true);
        }
        else {
            this.updateCrystalCount();
            final Entity aio4 = this.level.getEntity(this.dragonUUID);
            if (aio4 instanceof EnderDragon) {
                ((EnderDragon)aio4).onCrystalDestroyed(aso, new BlockPos(aso), ahx);
            }
        }
    }
    
    public boolean hasPreviouslyKilledDragon() {
        return this.previouslyKilled;
    }
    
    public void tryRespawn() {
        if (this.dragonKilled && this.respawnStage == null) {
            BlockPos ew2 = this.portalLocation;
            if (ew2 == null) {
                EndDragonFight.LOGGER.debug("Tried to respawn, but need to find the portal first.");
                final BlockPattern.BlockPatternMatch b3 = this.findExitPortal();
                if (b3 == null) {
                    EndDragonFight.LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.spawnExitPortal(true);
                }
                else {
                    EndDragonFight.LOGGER.debug("Found the exit portal & temporarily using it.");
                }
                ew2 = this.portalLocation;
            }
            final List<EndCrystal> list3 = (List<EndCrystal>)Lists.newArrayList();
            final BlockPos ew3 = ew2.above(1);
            for (final Direction fb6 : Direction.Plane.HORIZONTAL) {
                final List<EndCrystal> list4 = this.level.<EndCrystal>getEntitiesOfClass((java.lang.Class<? extends EndCrystal>)EndCrystal.class, new AABB(ew3.relative(fb6, 2)));
                if (list4.isEmpty()) {
                    return;
                }
                list3.addAll((Collection)list4);
            }
            EndDragonFight.LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon(list3);
        }
    }
    
    private void respawnDragon(final List<EndCrystal> list) {
        if (this.dragonKilled && this.respawnStage == null) {
            for (BlockPattern.BlockPatternMatch b3 = this.findExitPortal(); b3 != null; b3 = this.findExitPortal()) {
                for (int integer4 = 0; integer4 < this.exitPortalPattern.getWidth(); ++integer4) {
                    for (int integer5 = 0; integer5 < this.exitPortalPattern.getHeight(); ++integer5) {
                        for (int integer6 = 0; integer6 < this.exitPortalPattern.getDepth(); ++integer6) {
                            final BlockInWorld bvx7 = b3.getBlock(integer4, integer5, integer6);
                            if (bvx7.getState().getBlock() == Blocks.BEDROCK || bvx7.getState().getBlock() == Blocks.END_PORTAL) {
                                this.level.setBlockAndUpdate(bvx7.getPos(), Blocks.END_STONE.defaultBlockState());
                            }
                        }
                    }
                }
            }
            this.respawnStage = DragonRespawnAnimation.START;
            this.respawnTime = 0;
            this.spawnExitPortal(false);
            this.respawnCrystals = list;
        }
    }
    
    public void resetSpikeCrystals() {
        for (final SpikeFeature.EndSpike a3 : SpikeFeature.getSpikesForLevel(this.level)) {
            final List<EndCrystal> list4 = this.level.<EndCrystal>getEntitiesOfClass((java.lang.Class<? extends EndCrystal>)EndCrystal.class, a3.getTopBoundingBox());
            for (final EndCrystal aso6 : list4) {
                aso6.setInvulnerable(false);
                aso6.setBeamTarget(null);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and((Predicate)EntitySelector.withinDistance(0.0, 128.0, 0.0, 192.0));
    }
}

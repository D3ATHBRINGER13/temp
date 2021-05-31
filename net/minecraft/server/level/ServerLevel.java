package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.TickList;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.CsvOutput;
import java.io.IOException;
import java.io.Writer;
import net.minecraft.CrashReport;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import java.util.Optional;
import java.util.Objects;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.world.level.ForcedChunksSavedData;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.Unit;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import javax.annotation.Nonnull;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.entity.Mob;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.tags.BlockTags;
import java.util.Random;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.ServerScoreboard;
import java.util.Iterator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.chunk.LevelChunk;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import com.google.common.collect.Sets;
import net.minecraft.world.level.TickNextTickData;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import com.google.common.collect.Queues;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import com.google.common.collect.Lists;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import java.util.function.BiFunction;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.BlockEventData;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import java.util.Set;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.server.MinecraftServer;
import java.util.Queue;
import java.util.UUID;
import java.util.Map;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.Entity;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.Level;

public class ServerLevel extends Level {
    private static final Logger LOGGER;
    private final List<Entity> globalEntities;
    private final Int2ObjectMap<Entity> entitiesById;
    private final Map<UUID, Entity> entitiesByUuid;
    private final Queue<Entity> toAddAfterTick;
    private final List<ServerPlayer> players;
    boolean tickingEntities;
    private final MinecraftServer server;
    private final LevelStorage levelStorage;
    public boolean noSave;
    private boolean allPlayersSleeping;
    private int emptyTime;
    private final PortalForcer portalForcer;
    private final ServerTickList<Block> blockTicks;
    private final ServerTickList<Fluid> liquidTicks;
    private final Set<PathNavigation> navigations;
    protected final Raids raids;
    private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents;
    private boolean handlingTick;
    @Nullable
    private final WanderingTraderSpawner wanderingTraderSpawner;
    
    public ServerLevel(final MinecraftServer minecraftServer, final Executor executor, final LevelStorage coo, final LevelData com, final DimensionType byn, final ProfilerFiller agn, final ChunkProgressListener vt) {
        super(com, byn, (BiFunction<Level, Dimension, ChunkSource>)((bhr, bym) -> new ServerChunkCache((ServerLevel)bhr, coo.getFolder(), coo.getFixerUpper(), coo.getStructureManager(), executor, bym.createRandomLevelGenerator(), minecraftServer.getPlayerList().getViewDistance(), vt, (Supplier<DimensionDataStorage>)(() -> minecraftServer.getLevel(DimensionType.OVERWORLD).getDataStorage()))), agn, false);
        this.globalEntities = (List<Entity>)Lists.newArrayList();
        this.entitiesById = (Int2ObjectMap<Entity>)new Int2ObjectLinkedOpenHashMap();
        this.entitiesByUuid = (Map<UUID, Entity>)Maps.newHashMap();
        this.toAddAfterTick = (Queue<Entity>)Queues.newArrayDeque();
        this.players = (List<ServerPlayer>)Lists.newArrayList();
        this.blockTicks = new ServerTickList<Block>(this, (java.util.function.Predicate<Block>)(bmv -> bmv == null || bmv.defaultBlockState().isAir()), (java.util.function.Function<Block, ResourceLocation>)Registry.BLOCK::getKey, (java.util.function.Function<ResourceLocation, Block>)Registry.BLOCK::get, (java.util.function.Consumer<TickNextTickData<Block>>)this::tickBlock);
        this.liquidTicks = new ServerTickList<Fluid>(this, (java.util.function.Predicate<Fluid>)(clj -> clj == null || clj == Fluids.EMPTY), (java.util.function.Function<Fluid, ResourceLocation>)Registry.FLUID::getKey, (java.util.function.Function<ResourceLocation, Fluid>)Registry.FLUID::get, (java.util.function.Consumer<TickNextTickData<Fluid>>)this::tickLiquid);
        this.navigations = (Set<PathNavigation>)Sets.newHashSet();
        this.blockEvents = (ObjectLinkedOpenHashSet<BlockEventData>)new ObjectLinkedOpenHashSet();
        this.levelStorage = coo;
        this.server = minecraftServer;
        this.portalForcer = new PortalForcer(this);
        this.updateSkyBrightness();
        this.prepareWeather();
        this.getWorldBorder().setAbsoluteMaxSize(minecraftServer.getAbsoluteMaxWorldSize());
        this.raids = this.getDataStorage().<Raids>computeIfAbsent((java.util.function.Supplier<Raids>)(() -> new Raids(this)), Raids.getFileId(this.dimension));
        if (!minecraftServer.isSingleplayer()) {
            this.getLevelData().setGameType(minecraftServer.getDefaultGameType());
        }
        this.wanderingTraderSpawner = ((this.dimension.getType() == DimensionType.OVERWORLD) ? new WanderingTraderSpawner(this) : null);
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        final ProfilerFiller agn3 = this.getProfiler();
        this.handlingTick = true;
        agn3.push("world border");
        this.getWorldBorder().tick();
        agn3.popPush("weather");
        final boolean boolean4 = this.isRaining();
        if (this.dimension.isHasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                int integer5 = this.levelData.getClearWeatherTime();
                int integer6 = this.levelData.getThunderTime();
                int integer7 = this.levelData.getRainTime();
                boolean boolean5 = this.levelData.isThundering();
                boolean boolean6 = this.levelData.isRaining();
                if (integer5 > 0) {
                    --integer5;
                    integer6 = (boolean5 ? 0 : 1);
                    integer7 = (boolean6 ? 0 : 1);
                    boolean5 = false;
                    boolean6 = false;
                }
                else {
                    if (integer6 > 0) {
                        if (--integer6 == 0) {
                            boolean5 = !boolean5;
                        }
                    }
                    else if (boolean5) {
                        integer6 = this.random.nextInt(12000) + 3600;
                    }
                    else {
                        integer6 = this.random.nextInt(168000) + 12000;
                    }
                    if (integer7 > 0) {
                        if (--integer7 == 0) {
                            boolean6 = !boolean6;
                        }
                    }
                    else if (boolean6) {
                        integer7 = this.random.nextInt(12000) + 12000;
                    }
                    else {
                        integer7 = this.random.nextInt(168000) + 12000;
                    }
                }
                this.levelData.setThunderTime(integer6);
                this.levelData.setRainTime(integer7);
                this.levelData.setClearWeatherTime(integer5);
                this.levelData.setThundering(boolean5);
                this.levelData.setRaining(boolean6);
            }
            this.oThunderLevel = this.thunderLevel;
            if (this.levelData.isThundering()) {
                this.thunderLevel += (float)0.01;
            }
            else {
                this.thunderLevel -= (float)0.01;
            }
            this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0f, 1.0f);
            this.oRainLevel = this.rainLevel;
            if (this.levelData.isRaining()) {
                this.rainLevel += (float)0.01;
            }
            else {
                this.rainLevel -= (float)0.01;
            }
            this.rainLevel = Mth.clamp(this.rainLevel, 0.0f, 1.0f);
        }
        if (this.oRainLevel != this.rainLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel), this.dimension.getType());
        }
        if (this.oThunderLevel != this.thunderLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel), this.dimension.getType());
        }
        if (boolean4 != this.isRaining()) {
            if (boolean4) {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(2, 0.0f));
            }
            else {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(1, 0.0f));
            }
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel));
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel));
        }
        if (this.getLevelData().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
            this.getLevelData().setDifficulty(Difficulty.HARD);
        }
        if (this.allPlayersSleeping && this.players.stream().noneMatch(vl -> !vl.isSpectator() && !vl.isSleepingLongEnough())) {
            this.allPlayersSleeping = false;
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                final long long5 = this.levelData.getDayTime() + 24000L;
                this.setDayTime(long5 - long5 % 24000L);
            }
            this.players.stream().filter(LivingEntity::isSleeping).forEach(vl -> vl.stopSleepInBed(false, false, true));
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                this.stopWeather();
            }
        }
        this.updateSkyBrightness();
        this.tickTime();
        agn3.popPush("chunkSource");
        this.getChunkSource().tick(booleanSupplier);
        agn3.popPush("tickPending");
        if (this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
            this.blockTicks.tick();
            this.liquidTicks.tick();
        }
        agn3.popPush("portalForcer");
        this.portalForcer.tick(this.getGameTime());
        agn3.popPush("raid");
        this.raids.tick();
        if (this.wanderingTraderSpawner != null) {
            this.wanderingTraderSpawner.tick();
        }
        agn3.popPush("blockEvents");
        this.runBlockEvents();
        this.handlingTick = false;
        agn3.popPush("entities");
        final boolean boolean7 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if (boolean7) {
            this.resetEmptyTime();
        }
        if (boolean7 || this.emptyTime++ < 300) {
            this.dimension.tick();
            agn3.push("global");
            for (int integer6 = 0; integer6 < this.globalEntities.size(); ++integer6) {
                final Entity aio7 = (Entity)this.globalEntities.get(integer6);
                this.guardEntityTick((Consumer<Entity>)(aio -> {
                    ++aio.tickCount;
                    aio.tick();
                }), aio7);
                if (aio7.removed) {
                    this.globalEntities.remove(integer6--);
                }
            }
            agn3.popPush("regular");
            this.tickingEntities = true;
            final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator6 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.entitiesById.int2ObjectEntrySet().iterator();
            while (objectIterator6.hasNext()) {
                final Int2ObjectMap.Entry<Entity> entry7 = (Int2ObjectMap.Entry<Entity>)objectIterator6.next();
                final Entity aio8 = (Entity)entry7.getValue();
                final Entity aio9 = aio8.getVehicle();
                if (!this.server.isAnimals() && (aio8 instanceof Animal || aio8 instanceof WaterAnimal)) {
                    aio8.remove();
                }
                if (!this.server.isNpcsEnabled() && aio8 instanceof Npc) {
                    aio8.remove();
                }
                if (aio9 != null) {
                    if (!aio9.removed && aio9.hasPassenger(aio8)) {
                        continue;
                    }
                    aio8.stopRiding();
                }
                agn3.push("tick");
                if (!aio8.removed && !(aio8 instanceof EnderDragonPart)) {
                    this.guardEntityTick((Consumer<Entity>)this::tickNonPassenger, aio8);
                }
                agn3.pop();
                agn3.push("remove");
                if (aio8.removed) {
                    this.removeFromChunk(aio8);
                    objectIterator6.remove();
                    this.onEntityRemoved(aio8);
                }
                agn3.pop();
            }
            this.tickingEntities = false;
            Entity aio7;
            while ((aio7 = (Entity)this.toAddAfterTick.poll()) != null) {
                this.add(aio7);
            }
            agn3.pop();
            this.tickBlockEntities();
        }
        agn3.pop();
    }
    
    public void tickChunk(final LevelChunk bxt, final int integer) {
        final ChunkPos bhd4 = bxt.getPos();
        final boolean boolean5 = this.isRaining();
        final int integer2 = bhd4.getMinBlockX();
        final int integer3 = bhd4.getMinBlockZ();
        final ProfilerFiller agn8 = this.getProfiler();
        agn8.push("thunder");
        if (boolean5 && this.isThundering() && this.random.nextInt(100000) == 0) {
            final BlockPos ew9 = this.findLightingTargetAround(this.getBlockRandomPos(integer2, 0, integer3, 15));
            if (this.isRainingAt(ew9)) {
                final DifficultyInstance ahh10 = this.getCurrentDifficultyAt(ew9);
                final boolean boolean6 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < ahh10.getEffectiveDifficulty() * 0.01;
                if (boolean6) {
                    final SkeletonHorse asg12 = EntityType.SKELETON_HORSE.create(this);
                    asg12.setTrap(true);
                    asg12.setAge(0);
                    asg12.setPos(ew9.getX(), ew9.getY(), ew9.getZ());
                    this.addFreshEntity(asg12);
                }
                this.addGlobalEntity(new LightningBolt(this, ew9.getX() + 0.5, ew9.getY(), ew9.getZ() + 0.5, boolean6));
            }
        }
        agn8.popPush("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            final BlockPos ew9 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(integer2, 0, integer3, 15));
            final BlockPos ew10 = ew9.below();
            final Biome bio11 = this.getBiome(ew9);
            if (bio11.shouldFreeze(this, ew10)) {
                this.setBlockAndUpdate(ew10, Blocks.ICE.defaultBlockState());
            }
            if (boolean5 && bio11.shouldSnow(this, ew9)) {
                this.setBlockAndUpdate(ew9, Blocks.SNOW.defaultBlockState());
            }
            if (boolean5 && this.getBiome(ew10).getPrecipitation() == Biome.Precipitation.RAIN) {
                this.getBlockState(ew10).getBlock().handleRain(this, ew10);
            }
        }
        agn8.popPush("tickBlocks");
        if (integer > 0) {
            for (final LevelChunkSection bxu12 : bxt.getSections()) {
                if (bxu12 != LevelChunk.EMPTY_SECTION && bxu12.isRandomlyTicking()) {
                    final int integer4 = bxu12.bottomBlockY();
                    for (int integer5 = 0; integer5 < integer; ++integer5) {
                        final BlockPos ew11 = this.getBlockRandomPos(integer2, integer4, integer3, 15);
                        agn8.push("randomTick");
                        final BlockState bvt16 = bxu12.getBlockState(ew11.getX() - integer2, ew11.getY() - integer4, ew11.getZ() - integer3);
                        if (bvt16.isRandomlyTicking()) {
                            bvt16.randomTick(this, ew11, this.random);
                        }
                        final FluidState clk17 = bvt16.getFluidState();
                        if (clk17.isRandomlyTicking()) {
                            clk17.randomTick(this, ew11, this.random);
                        }
                        agn8.pop();
                    }
                }
            }
        }
        agn8.pop();
    }
    
    protected BlockPos findLightingTargetAround(final BlockPos ew) {
        BlockPos ew2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew);
        final AABB csc4 = new AABB(ew2, new BlockPos(ew2.getX(), this.getMaxBuildHeight(), ew2.getZ())).inflate(3.0);
        final List<LivingEntity> list5 = this.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, csc4, (java.util.function.Predicate<? super LivingEntity>)(aix -> aix != null && aix.isAlive() && this.canSeeSky(aix.getCommandSenderBlockPosition())));
        if (!list5.isEmpty()) {
            return ((LivingEntity)list5.get(this.random.nextInt(list5.size()))).getCommandSenderBlockPosition();
        }
        if (ew2.getY() == -1) {
            ew2 = ew2.above(2);
        }
        return ew2;
    }
    
    public boolean isHandlingTick() {
        return this.handlingTick;
    }
    
    public void updateSleepingPlayerList() {
        this.allPlayersSleeping = false;
        if (!this.players.isEmpty()) {
            int integer2 = 0;
            int integer3 = 0;
            for (final ServerPlayer vl5 : this.players) {
                if (vl5.isSpectator()) {
                    ++integer2;
                }
                else {
                    if (!vl5.isSleeping()) {
                        continue;
                    }
                    ++integer3;
                }
            }
            this.allPlayersSleeping = (integer3 > 0 && integer3 >= this.players.size() - integer2);
        }
    }
    
    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }
    
    private void stopWeather() {
        this.levelData.setRainTime(0);
        this.levelData.setRaining(false);
        this.levelData.setThunderTime(0);
        this.levelData.setThundering(false);
    }
    
    @Override
    public void validateSpawn() {
        if (this.levelData.getYSpawn() <= 0) {
            this.levelData.setYSpawn(this.getSeaLevel() + 1);
        }
        int integer2 = this.levelData.getXSpawn();
        int integer3 = this.levelData.getZSpawn();
        int integer4 = 0;
        while (this.getTopBlockState(new BlockPos(integer2, 0, integer3)).isAir()) {
            integer2 += this.random.nextInt(8) - this.random.nextInt(8);
            integer3 += this.random.nextInt(8) - this.random.nextInt(8);
            if (++integer4 == 10000) {
                break;
            }
        }
        this.levelData.setXSpawn(integer2);
        this.levelData.setZSpawn(integer3);
    }
    
    public void resetEmptyTime() {
        this.emptyTime = 0;
    }
    
    private void tickLiquid(final TickNextTickData<Fluid> bih) {
        final FluidState clk3 = this.getFluidState(bih.pos);
        if (clk3.getType() == bih.getType()) {
            clk3.tick(this, bih.pos);
        }
    }
    
    private void tickBlock(final TickNextTickData<Block> bih) {
        final BlockState bvt3 = this.getBlockState(bih.pos);
        if (bvt3.getBlock() == bih.getType()) {
            bvt3.tick(this, bih.pos, this.random);
        }
    }
    
    public void tickNonPassenger(final Entity aio) {
        if (!(aio instanceof Player) && !this.getChunkSource().isEntityTickingChunk(aio)) {
            return;
        }
        aio.xOld = aio.x;
        aio.yOld = aio.y;
        aio.zOld = aio.z;
        aio.yRotO = aio.yRot;
        aio.xRotO = aio.xRot;
        if (aio.inChunk) {
            ++aio.tickCount;
            this.getProfiler().push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getKey(aio.getType()).toString()));
            aio.tick();
            this.getProfiler().pop();
        }
        this.updateChunkPos(aio);
        if (aio.inChunk) {
            for (final Entity aio2 : aio.getPassengers()) {
                this.tickPassenger(aio, aio2);
            }
        }
    }
    
    public void tickPassenger(final Entity aio1, final Entity aio2) {
        if (aio2.removed || aio2.getVehicle() != aio1) {
            aio2.stopRiding();
            return;
        }
        if (!(aio2 instanceof Player) && !this.getChunkSource().isEntityTickingChunk(aio2)) {
            return;
        }
        aio2.xOld = aio2.x;
        aio2.yOld = aio2.y;
        aio2.zOld = aio2.z;
        aio2.yRotO = aio2.yRot;
        aio2.xRotO = aio2.xRot;
        if (aio2.inChunk) {
            ++aio2.tickCount;
            aio2.rideTick();
        }
        this.updateChunkPos(aio2);
        if (aio2.inChunk) {
            for (final Entity aio3 : aio2.getPassengers()) {
                this.tickPassenger(aio2, aio3);
            }
        }
    }
    
    public void updateChunkPos(final Entity aio) {
        this.getProfiler().push("chunkCheck");
        final int integer3 = Mth.floor(aio.x / 16.0);
        final int integer4 = Mth.floor(aio.y / 16.0);
        final int integer5 = Mth.floor(aio.z / 16.0);
        if (!aio.inChunk || aio.xChunk != integer3 || aio.yChunk != integer4 || aio.zChunk != integer5) {
            if (aio.inChunk && this.hasChunk(aio.xChunk, aio.zChunk)) {
                this.getChunk(aio.xChunk, aio.zChunk).removeEntity(aio, aio.yChunk);
            }
            if (aio.checkAndResetTeleportedFlag() || this.hasChunk(integer3, integer5)) {
                this.getChunk(integer3, integer5).addEntity(aio);
            }
            else {
                aio.inChunk = false;
            }
        }
        this.getProfiler().pop();
    }
    
    @Override
    public boolean mayInteract(final Player awg, final BlockPos ew) {
        return !this.server.isUnderSpawnProtection(this, ew, awg) && this.getWorldBorder().isWithinBounds(ew);
    }
    
    public void setInitialSpawn(final LevelSettings bhv) {
        if (!this.dimension.mayRespawn()) {
            this.levelData.setSpawn(BlockPos.ZERO.above(this.chunkSource.getGenerator().getSpawnHeight()));
            return;
        }
        if (this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
            this.levelData.setSpawn(BlockPos.ZERO.above());
            return;
        }
        final BiomeSource biq3 = this.chunkSource.getGenerator().getBiomeSource();
        final List<Biome> list4 = biq3.getPlayerSpawnBiomes();
        final Random random5 = new Random(this.getSeed());
        final BlockPos ew6 = biq3.findBiome(0, 0, 256, list4, random5);
        final ChunkPos bhd7 = (ew6 == null) ? new ChunkPos(0, 0) : new ChunkPos(ew6);
        if (ew6 == null) {
            ServerLevel.LOGGER.warn("Unable to find spawn biome");
        }
        boolean boolean8 = false;
        for (final Block bmv10 : BlockTags.VALID_SPAWN.getValues()) {
            if (biq3.getSurfaceBlocks().contains(bmv10.defaultBlockState())) {
                boolean8 = true;
                break;
            }
        }
        this.levelData.setSpawn(bhd7.getWorldPosition().offset(8, this.chunkSource.getGenerator().getSpawnHeight(), 8));
        int integer9 = 0;
        int integer10 = 0;
        int integer11 = 0;
        int integer12 = -1;
        final int integer13 = 32;
        for (int integer14 = 0; integer14 < 1024; ++integer14) {
            if (integer9 > -16 && integer9 <= 16 && integer10 > -16 && integer10 <= 16) {
                final BlockPos ew7 = this.dimension.getSpawnPosInChunk(new ChunkPos(bhd7.x + integer9, bhd7.z + integer10), boolean8);
                if (ew7 != null) {
                    this.levelData.setSpawn(ew7);
                    break;
                }
            }
            if (integer9 == integer10 || (integer9 < 0 && integer9 == -integer10) || (integer9 > 0 && integer9 == 1 - integer10)) {
                final int integer15 = integer11;
                integer11 = -integer12;
                integer12 = integer15;
            }
            integer9 += integer11;
            integer10 += integer12;
        }
        if (bhv.hasStartingBonusItems()) {
            this.generateBonusItemsNearSpawn();
        }
    }
    
    protected void generateBonusItemsNearSpawn() {
        final BonusChestFeature cad2 = Feature.BONUS_CHEST;
        for (int integer3 = 0; integer3 < 10; ++integer3) {
            final int integer4 = this.levelData.getXSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
            final int integer5 = this.levelData.getZSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
            final BlockPos ew6 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(integer4, 0, integer5)).above();
            if (cad2.place(this, this.chunkSource.getGenerator(), this.random, ew6, FeatureConfiguration.NONE)) {
                break;
            }
        }
    }
    
    @Nullable
    public BlockPos getDimensionSpecificSpawn() {
        return this.dimension.getDimensionSpecificSpawn();
    }
    
    public void save(@Nullable final ProgressListener zz, final boolean boolean2, final boolean boolean3) throws LevelConflictException {
        final ServerChunkCache vi5 = this.getChunkSource();
        if (boolean3) {
            return;
        }
        if (zz != null) {
            zz.progressStartNoAbort(new TranslatableComponent("menu.savingLevel", new Object[0]));
        }
        this.saveLevelData();
        if (zz != null) {
            zz.progressStage(new TranslatableComponent("menu.savingChunks", new Object[0]));
        }
        vi5.save(boolean2);
    }
    
    protected void saveLevelData() throws LevelConflictException {
        this.checkSession();
        this.dimension.saveData();
        this.getChunkSource().getDataStorage().save();
    }
    
    public List<Entity> getEntities(@Nullable final EntityType<?> ais, final Predicate<? super Entity> predicate) {
        final List<Entity> list4 = (List<Entity>)Lists.newArrayList();
        final ServerChunkCache vi5 = this.getChunkSource();
        for (final Entity aio7 : this.entitiesById.values()) {
            if ((ais == null || aio7.getType() == ais) && vi5.hasChunk(Mth.floor(aio7.x) >> 4, Mth.floor(aio7.z) >> 4) && predicate.test(aio7)) {
                list4.add(aio7);
            }
        }
        return list4;
    }
    
    public List<EnderDragon> getDragons() {
        final List<EnderDragon> list2 = (List<EnderDragon>)Lists.newArrayList();
        for (final Entity aio4 : this.entitiesById.values()) {
            if (aio4 instanceof EnderDragon && aio4.isAlive()) {
                list2.add(aio4);
            }
        }
        return list2;
    }
    
    public List<ServerPlayer> getPlayers(final Predicate<? super ServerPlayer> predicate) {
        final List<ServerPlayer> list3 = (List<ServerPlayer>)Lists.newArrayList();
        for (final ServerPlayer vl5 : this.players) {
            if (predicate.test(vl5)) {
                list3.add(vl5);
            }
        }
        return list3;
    }
    
    @Nullable
    public ServerPlayer getRandomPlayer() {
        final List<ServerPlayer> list2 = this.getPlayers(LivingEntity::isAlive);
        if (list2.isEmpty()) {
            return null;
        }
        return (ServerPlayer)list2.get(this.random.nextInt(list2.size()));
    }
    
    public Object2IntMap<MobCategory> getMobCategoryCounts() {
        final Object2IntMap<MobCategory> object2IntMap2 = (Object2IntMap<MobCategory>)new Object2IntOpenHashMap();
        for (final Entity aio4 : this.entitiesById.values()) {
            if (aio4 instanceof Mob) {
                final Mob aiy5 = (Mob)aio4;
                if (aiy5.isPersistenceRequired()) {
                    continue;
                }
                if (aiy5.requiresCustomPersistence()) {
                    continue;
                }
            }
            final MobCategory aiz5 = aio4.getType().getCategory();
            if (aiz5 == MobCategory.MISC) {
                continue;
            }
            if (!this.getChunkSource().isInAccessibleChunk(aio4)) {
                continue;
            }
            object2IntMap2.mergeInt(aiz5, 1, Integer::sum);
        }
        return object2IntMap2;
    }
    
    public boolean addFreshEntity(final Entity aio) {
        return this.addEntity(aio);
    }
    
    public boolean addWithUUID(final Entity aio) {
        return this.addEntity(aio);
    }
    
    public void addFromAnotherDimension(final Entity aio) {
        final boolean boolean3 = aio.forcedLoading;
        aio.forcedLoading = true;
        this.addWithUUID(aio);
        aio.forcedLoading = boolean3;
        this.updateChunkPos(aio);
    }
    
    public void addDuringCommandTeleport(final ServerPlayer vl) {
        this.addPlayer(vl);
        this.updateChunkPos(vl);
    }
    
    public void addDuringPortalTeleport(final ServerPlayer vl) {
        this.addPlayer(vl);
        this.updateChunkPos(vl);
    }
    
    public void addNewPlayer(final ServerPlayer vl) {
        this.addPlayer(vl);
    }
    
    public void addRespawnedPlayer(final ServerPlayer vl) {
        this.addPlayer(vl);
    }
    
    private void addPlayer(final ServerPlayer vl) {
        final Entity aio3 = (Entity)this.entitiesByUuid.get(vl.getUUID());
        if (aio3 != null) {
            ServerLevel.LOGGER.warn("Force-added player with duplicate UUID {}", vl.getUUID().toString());
            aio3.unRide();
            this.removePlayerImmediately((ServerPlayer)aio3);
        }
        this.players.add(vl);
        this.updateSleepingPlayerList();
        final ChunkAccess bxh4 = this.getChunk(Mth.floor(vl.x / 16.0), Mth.floor(vl.z / 16.0), ChunkStatus.FULL, true);
        if (bxh4 instanceof LevelChunk) {
            bxh4.addEntity(vl);
        }
        this.add(vl);
    }
    
    private boolean addEntity(final Entity aio) {
        if (aio.removed) {
            ServerLevel.LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey(aio.getType()));
            return false;
        }
        if (this.isUUIDUsed(aio)) {
            return false;
        }
        final ChunkAccess bxh3 = this.getChunk(Mth.floor(aio.x / 16.0), Mth.floor(aio.z / 16.0), ChunkStatus.FULL, aio.forcedLoading);
        if (!(bxh3 instanceof LevelChunk)) {
            return false;
        }
        bxh3.addEntity(aio);
        this.add(aio);
        return true;
    }
    
    public boolean loadFromChunk(final Entity aio) {
        if (this.isUUIDUsed(aio)) {
            return false;
        }
        this.add(aio);
        return true;
    }
    
    private boolean isUUIDUsed(final Entity aio) {
        final Entity aio2 = (Entity)this.entitiesByUuid.get(aio.getUUID());
        if (aio2 == null) {
            return false;
        }
        ServerLevel.LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getKey(aio2.getType()), aio.getUUID().toString());
        return true;
    }
    
    public void unload(final LevelChunk bxt) {
        this.blockEntitiesToUnload.addAll(bxt.getBlockEntities().values());
        for (final ClassInstanceMultiMap<Entity> zl6 : bxt.getEntitySections()) {
            for (final Entity aio8 : zl6) {
                if (aio8 instanceof ServerPlayer) {
                    continue;
                }
                if (this.tickingEntities) {
                    throw new IllegalStateException("Removing entity while ticking!");
                }
                this.entitiesById.remove(aio8.getId());
                this.onEntityRemoved(aio8);
            }
        }
    }
    
    public void onEntityRemoved(final Entity aio) {
        if (aio instanceof EnderDragon) {
            for (final EnderDragonPart asn6 : ((EnderDragon)aio).getSubEntities()) {
                asn6.remove();
            }
        }
        this.entitiesByUuid.remove(aio.getUUID());
        this.getChunkSource().removeEntity(aio);
        if (aio instanceof ServerPlayer) {
            final ServerPlayer vl3 = (ServerPlayer)aio;
            this.players.remove(vl3);
        }
        this.getScoreboard().entityRemoved(aio);
        if (aio instanceof Mob) {
            this.navigations.remove(((Mob)aio).getNavigation());
        }
    }
    
    private void add(final Entity aio) {
        if (this.tickingEntities) {
            this.toAddAfterTick.add(aio);
        }
        else {
            this.entitiesById.put(aio.getId(), aio);
            if (aio instanceof EnderDragon) {
                for (final EnderDragonPart asn6 : ((EnderDragon)aio).getSubEntities()) {
                    this.entitiesById.put(asn6.getId(), asn6);
                }
            }
            this.entitiesByUuid.put(aio.getUUID(), aio);
            this.getChunkSource().addEntity(aio);
            if (aio instanceof Mob) {
                this.navigations.add(((Mob)aio).getNavigation());
            }
        }
    }
    
    public void despawn(final Entity aio) {
        if (this.tickingEntities) {
            throw new IllegalStateException("Removing entity while ticking!");
        }
        this.removeFromChunk(aio);
        this.entitiesById.remove(aio.getId());
        this.onEntityRemoved(aio);
    }
    
    private void removeFromChunk(final Entity aio) {
        final ChunkAccess bxh3 = this.getChunk(aio.xChunk, aio.zChunk, ChunkStatus.FULL, false);
        if (bxh3 instanceof LevelChunk) {
            ((LevelChunk)bxh3).removeEntity(aio);
        }
    }
    
    public void removePlayerImmediately(final ServerPlayer vl) {
        vl.remove();
        this.despawn(vl);
        this.updateSleepingPlayerList();
    }
    
    public void addGlobalEntity(final LightningBolt atu) {
        this.globalEntities.add(atu);
        this.server.getPlayerList().broadcast(null, atu.x, atu.y, atu.z, 512.0, this.dimension.getType(), new ClientboundAddGlobalEntityPacket(atu));
    }
    
    @Override
    public void destroyBlockProgress(final int integer1, final BlockPos ew, final int integer3) {
        for (final ServerPlayer vl6 : this.server.getPlayerList().getPlayers()) {
            if (vl6 != null && vl6.level == this) {
                if (vl6.getId() == integer1) {
                    continue;
                }
                final double double7 = ew.getX() - vl6.x;
                final double double8 = ew.getY() - vl6.y;
                final double double9 = ew.getZ() - vl6.z;
                if (double7 * double7 + double8 * double8 + double9 * double9 >= 1024.0) {
                    continue;
                }
                vl6.connection.send(new ClientboundBlockDestructionPacket(integer1, ew, integer3));
            }
        }
    }
    
    @Override
    public void playSound(@Nullable final Player awg, final double double2, final double double3, final double double4, final SoundEvent yo, final SoundSource yq, final float float7, final float float8) {
        this.server.getPlayerList().broadcast(awg, double2, double3, double4, (float7 > 1.0f) ? ((double)(16.0f * float7)) : 16.0, this.dimension.getType(), new ClientboundSoundPacket(yo, yq, double2, double3, double4, float7, float8));
    }
    
    @Override
    public void playSound(@Nullable final Player awg, final Entity aio, final SoundEvent yo, final SoundSource yq, final float float5, final float float6) {
        this.server.getPlayerList().broadcast(awg, aio.x, aio.y, aio.z, (float5 > 1.0f) ? ((double)(16.0f * float5)) : 16.0, this.dimension.getType(), new ClientboundSoundEntityPacket(yo, yq, aio, float5, float6));
    }
    
    @Override
    public void globalLevelEvent(final int integer1, final BlockPos ew, final int integer3) {
        this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(integer1, ew, integer3, true));
    }
    
    public void levelEvent(@Nullable final Player awg, final int integer2, final BlockPos ew, final int integer4) {
        this.server.getPlayerList().broadcast(awg, ew.getX(), ew.getY(), ew.getZ(), 64.0, this.dimension.getType(), new ClientboundLevelEventPacket(integer2, ew, integer4, false));
    }
    
    @Override
    public void sendBlockUpdated(final BlockPos ew, final BlockState bvt2, final BlockState bvt3, final int integer) {
        this.getChunkSource().blockChanged(ew);
        final VoxelShape ctc6 = bvt2.getCollisionShape(this, ew);
        final VoxelShape ctc7 = bvt3.getCollisionShape(this, ew);
        if (!Shapes.joinIsNotEmpty(ctc6, ctc7, BooleanOp.NOT_SAME)) {
            return;
        }
        for (final PathNavigation app9 : this.navigations) {
            if (app9.hasDelayedRecomputation()) {
                continue;
            }
            app9.recomputePath(ew);
        }
    }
    
    @Override
    public void broadcastEntityEvent(final Entity aio, final byte byte2) {
        this.getChunkSource().broadcastAndSend(aio, new ClientboundEntityEventPacket(aio, byte2));
    }
    
    @Override
    public ServerChunkCache getChunkSource() {
        return (ServerChunkCache)super.getChunkSource();
    }
    
    @Override
    public Explosion explode(@Nullable final Entity aio, final DamageSource ahx, final double double3, final double double4, final double double5, final float float6, final boolean boolean7, final Explosion.BlockInteraction a) {
        final Explosion bhk13 = new Explosion(this, aio, double3, double4, double5, float6, boolean7, a);
        if (ahx != null) {
            bhk13.setDamageSource(ahx);
        }
        bhk13.explode();
        bhk13.finalizeExplosion(false);
        if (a == Explosion.BlockInteraction.NONE) {
            bhk13.clearToBlow();
        }
        for (final ServerPlayer vl15 : this.players) {
            if (vl15.distanceToSqr(double3, double4, double5) < 4096.0) {
                vl15.connection.send(new ClientboundExplodePacket(double3, double4, double5, float6, bhk13.getToBlow(), (Vec3)bhk13.getHitPlayers().get(vl15)));
            }
        }
        return bhk13;
    }
    
    @Override
    public void blockEvent(final BlockPos ew, final Block bmv, final int integer3, final int integer4) {
        this.blockEvents.add(new BlockEventData(ew, bmv, integer3, integer4));
    }
    
    private void runBlockEvents() {
        while (!this.blockEvents.isEmpty()) {
            final BlockEventData bha2 = (BlockEventData)this.blockEvents.removeFirst();
            if (this.doBlockEvent(bha2)) {
                this.server.getPlayerList().broadcast(null, bha2.getPos().getX(), bha2.getPos().getY(), bha2.getPos().getZ(), 64.0, this.dimension.getType(), new ClientboundBlockEventPacket(bha2.getPos(), bha2.getBlock(), bha2.getParamA(), bha2.getParamB()));
            }
        }
    }
    
    private boolean doBlockEvent(final BlockEventData bha) {
        final BlockState bvt3 = this.getBlockState(bha.getPos());
        return bvt3.getBlock() == bha.getBlock() && bvt3.triggerEvent(this, bha.getPos(), bha.getParamA(), bha.getParamB());
    }
    
    public ServerTickList<Block> getBlockTicks() {
        return this.blockTicks;
    }
    
    public ServerTickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }
    
    @Nonnull
    @Override
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }
    
    public StructureManager getStructureManager() {
        return this.levelStorage.getStructureManager();
    }
    
    public <T extends ParticleOptions> int sendParticles(final T gf, final double double2, final double double3, final double double4, final int integer, final double double6, final double double7, final double double8, final double double9) {
        final ClientboundLevelParticlesPacket lq18 = new ClientboundLevelParticlesPacket((T)gf, false, (float)double2, (float)double3, (float)double4, (float)double6, (float)double7, (float)double8, (float)double9, integer);
        int integer2 = 0;
        for (int integer3 = 0; integer3 < this.players.size(); ++integer3) {
            final ServerPlayer vl21 = (ServerPlayer)this.players.get(integer3);
            if (this.sendParticles(vl21, false, double2, double3, double4, lq18)) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    public <T extends ParticleOptions> boolean sendParticles(final ServerPlayer vl, final T gf, final boolean boolean3, final double double4, final double double5, final double double6, final int integer, final double double8, final double double9, final double double10, final double double11) {
        final Packet<?> kc20 = new ClientboundLevelParticlesPacket((T)gf, boolean3, (float)double4, (float)double5, (float)double6, (float)double8, (float)double9, (float)double10, (float)double11, integer);
        return this.sendParticles(vl, boolean3, double4, double5, double6, kc20);
    }
    
    private boolean sendParticles(final ServerPlayer vl, final boolean boolean2, final double double3, final double double4, final double double5, final Packet<?> kc) {
        if (vl.getLevel() != this) {
            return false;
        }
        final BlockPos ew11 = vl.getCommandSenderBlockPosition();
        if (ew11.closerThan(new Vec3(double3, double4, double5), boolean2 ? 512.0 : 32.0)) {
            vl.connection.send(kc);
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public Entity getEntity(final int integer) {
        return (Entity)this.entitiesById.get(integer);
    }
    
    @Nullable
    public Entity getEntity(final UUID uUID) {
        return (Entity)this.entitiesByUuid.get(uUID);
    }
    
    @Nullable
    @Override
    public BlockPos findNearestMapFeature(final String string, final BlockPos ew, final int integer, final boolean boolean4) {
        return this.getChunkSource().getGenerator().findNearestMapFeature(this, string, ew, integer, boolean4);
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return this.server.getRecipeManager();
    }
    
    @Override
    public TagManager getTagManager() {
        return this.server.getTags();
    }
    
    @Override
    public void setGameTime(final long long1) {
        super.setGameTime(long1);
        this.levelData.getScheduledEvents().tick(this.server, long1);
    }
    
    @Override
    public boolean noSave() {
        return this.noSave;
    }
    
    public void checkSession() throws LevelConflictException {
        this.levelStorage.checkSession();
    }
    
    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }
    
    public DimensionDataStorage getDataStorage() {
        return this.getChunkSource().getDataStorage();
    }
    
    @Nullable
    @Override
    public MapItemSavedData getMapData(final String string) {
        return this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().<MapItemSavedData>get((java.util.function.Supplier<MapItemSavedData>)(() -> new MapItemSavedData(string)), string);
    }
    
    @Override
    public void setMapData(final MapItemSavedData coh) {
        this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().set(coh);
    }
    
    @Override
    public int getFreeMapId() {
        return this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().<MapIndex>computeIfAbsent((java.util.function.Supplier<MapIndex>)MapIndex::new, "idcounts").getFreeAuxValueForMap();
    }
    
    @Override
    public void setSpawnPos(final BlockPos ew) {
        final ChunkPos bhd3 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
        super.setSpawnPos(ew);
        this.getChunkSource().<Unit>removeRegionTicket(TicketType.START, bhd3, 11, Unit.INSTANCE);
        this.getChunkSource().<Unit>addRegionTicket(TicketType.START, new ChunkPos(ew), 11, Unit.INSTANCE);
    }
    
    public LongSet getForcedChunks() {
        final ForcedChunksSavedData bhm2 = this.getDataStorage().<ForcedChunksSavedData>get((java.util.function.Supplier<ForcedChunksSavedData>)ForcedChunksSavedData::new, "chunks");
        return (LongSet)((bhm2 != null) ? LongSets.unmodifiable(bhm2.getChunks()) : LongSets.EMPTY_SET);
    }
    
    public boolean setChunkForced(final int integer1, final int integer2, final boolean boolean3) {
        final ForcedChunksSavedData bhm5 = this.getDataStorage().<ForcedChunksSavedData>computeIfAbsent((java.util.function.Supplier<ForcedChunksSavedData>)ForcedChunksSavedData::new, "chunks");
        final ChunkPos bhd6 = new ChunkPos(integer1, integer2);
        final long long7 = bhd6.toLong();
        boolean boolean4;
        if (boolean3) {
            boolean4 = bhm5.getChunks().add(long7);
            if (boolean4) {
                this.getChunk(integer1, integer2);
            }
        }
        else {
            boolean4 = bhm5.getChunks().remove(long7);
        }
        bhm5.setDirty(boolean4);
        if (boolean4) {
            this.getChunkSource().updateChunkForced(bhd6, boolean3);
        }
        return boolean4;
    }
    
    public List<ServerPlayer> players() {
        return this.players;
    }
    
    @Override
    public void onBlockStateChange(final BlockPos ew, final BlockState bvt2, final BlockState bvt3) {
        final Optional<PoiType> optional5 = PoiType.forState(bvt2);
        final Optional<PoiType> optional6 = PoiType.forState(bvt3);
        if (Objects.equals(optional5, optional6)) {
            return;
        }
        final BlockPos ew2 = ew.immutable();
        optional5.ifPresent(aqs -> this.getServer().execute(() -> {
            this.getPoiManager().remove(ew2);
            DebugPackets.sendPoiRemovedPacket(this, ew2);
        }));
        optional6.ifPresent(aqs -> this.getServer().execute(() -> {
            this.getPoiManager().add(ew2, aqs);
            DebugPackets.sendPoiAddedPacket(this, ew2);
        }));
    }
    
    public PoiManager getPoiManager() {
        return this.getChunkSource().getPoiManager();
    }
    
    public boolean isVillage(final BlockPos ew) {
        return this.closeToVillage(ew, 1);
    }
    
    public boolean isVillage(final SectionPos fp) {
        return this.isVillage(fp.center());
    }
    
    public boolean closeToVillage(final BlockPos ew, final int integer) {
        return integer <= 6 && this.sectionsToVillage(SectionPos.of(ew)) <= integer;
    }
    
    public int sectionsToVillage(final SectionPos fp) {
        return this.getPoiManager().sectionsToVillage(fp);
    }
    
    public Raids getRaids() {
        return this.raids;
    }
    
    @Nullable
    public Raid getRaidAt(final BlockPos ew) {
        return this.raids.getNearbyRaid(ew, 9216);
    }
    
    public boolean isRaided(final BlockPos ew) {
        return this.getRaidAt(ew) != null;
    }
    
    public void onReputationEvent(final ReputationEventType aqm, final Entity aio, final ReputationEventHandler aji) {
        aji.onReputationEventFrom(aqm, aio);
    }
    
    public void saveDebugReport(final Path path) throws IOException {
        final ChunkMap uw3 = this.getChunkSource().chunkMap;
        try (final Writer writer4 = (Writer)Files.newBufferedWriter(path.resolve("stats.txt"), new OpenOption[0])) {
            writer4.write(String.format("spawning_chunks: %d\n", new Object[] { uw3.getDistanceManager().getNaturalSpawnChunkCount() }));
            for (final Object2IntMap.Entry<MobCategory> entry7 : this.getMobCategoryCounts().object2IntEntrySet()) {
                writer4.write(String.format("spawn_count.%s: %d\n", new Object[] { ((MobCategory)entry7.getKey()).getName(), entry7.getIntValue() }));
            }
            writer4.write(String.format("entities: %d\n", new Object[] { this.entitiesById.size() }));
            writer4.write(String.format("block_entities: %d\n", new Object[] { this.blockEntityList.size() }));
            writer4.write(String.format("block_ticks: %d\n", new Object[] { this.getBlockTicks().size() }));
            writer4.write(String.format("fluid_ticks: %d\n", new Object[] { this.getLiquidTicks().size() }));
            writer4.write("distance_manager: " + uw3.getDistanceManager().getDebugStatus() + "\n");
            writer4.write(String.format("pending_tasks: %d\n", new Object[] { this.getChunkSource().getPendingTasksCount() }));
        }
        final CrashReport d4 = new CrashReport("Level dump", (Throwable)new Exception("dummy"));
        this.fillReportDetails(d4);
        try (final Writer writer5 = (Writer)Files.newBufferedWriter(path.resolve("example_crash.txt"), new OpenOption[0])) {
            writer5.write(d4.getFriendlyReport());
        }
        final Path path2 = path.resolve("chunks.csv");
        try (final Writer writer6 = (Writer)Files.newBufferedWriter(path2, new OpenOption[0])) {
            uw3.dumpChunks(writer6);
        }
        final Path path3 = path.resolve("entities.csv");
        try (final Writer writer7 = (Writer)Files.newBufferedWriter(path3, new OpenOption[0])) {
            dumpEntities(writer7, (Iterable<Entity>)this.entitiesById.values());
        }
        final Path path4 = path.resolve("global_entities.csv");
        try (final Writer writer8 = (Writer)Files.newBufferedWriter(path4, new OpenOption[0])) {
            dumpEntities(writer8, (Iterable<Entity>)this.globalEntities);
        }
        final Path path5 = path.resolve("block_entities.csv");
        try (final Writer writer9 = (Writer)Files.newBufferedWriter(path5, new OpenOption[0])) {
            this.dumpBlockEntities(writer9);
        }
    }
    
    private static void dumpEntities(final Writer writer, final Iterable<Entity> iterable) throws IOException {
        final CsvOutput zp3 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(writer);
        for (final Entity aio5 : iterable) {
            final Component jo6 = aio5.getCustomName();
            final Component jo7 = aio5.getDisplayName();
            zp3.writeRow(aio5.x, aio5.y, aio5.z, aio5.getUUID(), Registry.ENTITY_TYPE.getKey(aio5.getType()), aio5.isAlive(), jo7.getString(), (jo6 != null) ? jo6.getString() : null);
        }
    }
    
    private void dumpBlockEntities(final Writer writer) throws IOException {
        final CsvOutput zp3 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(writer);
        for (final BlockEntity btw5 : this.blockEntityList) {
            final BlockPos ew6 = btw5.getBlockPos();
            zp3.writeRow(ew6.getX(), ew6.getY(), ew6.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(btw5.getType()));
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.client.multiplayer;

import net.minecraft.core.Registry;
import net.minecraft.ReportedException;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.TickList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReport;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.util.Mth;
import java.util.Iterator;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import com.google.common.collect.Iterables;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.google.common.collect.Lists;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import java.util.function.BiFunction;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import java.util.Map;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.Entity;
import java.util.List;
import net.minecraft.world.level.Level;

public class MultiPlayerLevel extends Level {
    private final List<Entity> globalEntities;
    private final Int2ObjectMap<Entity> entitiesById;
    private final ClientPacketListener connection;
    private final LevelRenderer levelRenderer;
    private final Minecraft minecraft;
    private final List<AbstractClientPlayer> players;
    private int delayUntilNextMoodSound;
    private Scoreboard scoreboard;
    private final Map<String, MapItemSavedData> mapData;
    
    public MultiPlayerLevel(final ClientPacketListener dkc, final LevelSettings bhv, final DimensionType byn, final int integer, final ProfilerFiller agn, final LevelRenderer dng) {
        super(new LevelData(bhv, "MpServer"), byn, (BiFunction<Level, Dimension, ChunkSource>)((bhr, bym) -> new ClientChunkCache((MultiPlayerLevel)bhr, integer)), agn, true);
        this.globalEntities = (List<Entity>)Lists.newArrayList();
        this.entitiesById = (Int2ObjectMap<Entity>)new Int2ObjectOpenHashMap();
        this.minecraft = Minecraft.getInstance();
        this.players = (List<AbstractClientPlayer>)Lists.newArrayList();
        this.delayUntilNextMoodSound = this.random.nextInt(12000);
        this.scoreboard = new Scoreboard();
        this.mapData = (Map<String, MapItemSavedData>)Maps.newHashMap();
        this.connection = dkc;
        this.levelRenderer = dng;
        this.setSpawnPos(new BlockPos(8, 64, 8));
        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkSource.tick(booleanSupplier);
        this.playMoodSounds();
        this.getProfiler().pop();
    }
    
    public Iterable<Entity> entitiesForRendering() {
        return (Iterable<Entity>)Iterables.concat((Iterable)this.entitiesById.values(), (Iterable)this.globalEntities);
    }
    
    public void tickEntities() {
        final ProfilerFiller agn2 = this.getProfiler();
        agn2.push("entities");
        agn2.push("global");
        for (int integer3 = 0; integer3 < this.globalEntities.size(); ++integer3) {
            final Entity aio4 = (Entity)this.globalEntities.get(integer3);
            this.guardEntityTick((Consumer<Entity>)(aio -> {
                ++aio.tickCount;
                aio.tick();
            }), aio4);
            if (aio4.removed) {
                this.globalEntities.remove(integer3--);
            }
        }
        agn2.popPush("regular");
        final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator3 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.entitiesById.int2ObjectEntrySet().iterator();
        while (objectIterator3.hasNext()) {
            final Int2ObjectMap.Entry<Entity> entry4 = (Int2ObjectMap.Entry<Entity>)objectIterator3.next();
            final Entity aio5 = (Entity)entry4.getValue();
            if (aio5.isPassenger()) {
                continue;
            }
            agn2.push("tick");
            if (!aio5.removed) {
                this.guardEntityTick((Consumer<Entity>)this::tickNonPassenger, aio5);
            }
            agn2.pop();
            agn2.push("remove");
            if (aio5.removed) {
                objectIterator3.remove();
                this.onEntityRemoved(aio5);
            }
            agn2.pop();
        }
        agn2.pop();
        this.tickBlockEntities();
        agn2.pop();
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
        if (aio.inChunk || aio.isSpectator()) {
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
    
    public void unload(final LevelChunk bxt) {
        this.blockEntitiesToUnload.addAll(bxt.getBlockEntities().values());
        this.chunkSource.getLightEngine().enableLightSources(bxt.getPos(), false);
    }
    
    public boolean hasChunk(final int integer1, final int integer2) {
        return true;
    }
    
    private void playMoodSounds() {
        if (this.minecraft.player == null) {
            return;
        }
        if (this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
            return;
        }
        final BlockPos ew2 = new BlockPos(this.minecraft.player);
        final BlockPos ew3 = ew2.offset(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
        final double double4 = ew2.distSqr(ew3);
        if (double4 >= 4.0 && double4 <= 256.0) {
            final BlockState bvt6 = this.getBlockState(ew3);
            if (bvt6.isAir() && this.getRawBrightness(ew3, 0) <= this.random.nextInt(8) && this.getBrightness(LightLayer.SKY, ew3) <= 0) {
                this.playLocalSound(ew3.getX() + 0.5, ew3.getY() + 0.5, ew3.getZ() + 0.5, SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, 0.7f, 0.8f + this.random.nextFloat() * 0.2f, false);
                this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
            }
        }
    }
    
    public int getEntityCount() {
        return this.entitiesById.size();
    }
    
    public void addLightning(final LightningBolt atu) {
        this.globalEntities.add(atu);
    }
    
    public void addPlayer(final int integer, final AbstractClientPlayer dmm) {
        this.addEntity(integer, dmm);
        this.players.add(dmm);
    }
    
    public void putNonPlayerEntity(final int integer, final Entity aio) {
        this.addEntity(integer, aio);
    }
    
    private void addEntity(final int integer, final Entity aio) {
        this.removeEntity(integer);
        this.entitiesById.put(integer, aio);
        this.getChunkSource().getChunk(Mth.floor(aio.x / 16.0), Mth.floor(aio.z / 16.0), ChunkStatus.FULL, true).addEntity(aio);
    }
    
    public void removeEntity(final int integer) {
        final Entity aio3 = (Entity)this.entitiesById.remove(integer);
        if (aio3 != null) {
            aio3.remove();
            this.onEntityRemoved(aio3);
        }
    }
    
    private void onEntityRemoved(final Entity aio) {
        aio.unRide();
        if (aio.inChunk) {
            this.getChunk(aio.xChunk, aio.zChunk).removeEntity(aio);
        }
        this.players.remove(aio);
    }
    
    public void reAddEntitiesToChunk(final LevelChunk bxt) {
        for (final Int2ObjectMap.Entry<Entity> entry4 : this.entitiesById.int2ObjectEntrySet()) {
            final Entity aio5 = (Entity)entry4.getValue();
            final int integer6 = Mth.floor(aio5.x / 16.0);
            final int integer7 = Mth.floor(aio5.z / 16.0);
            if (integer6 == bxt.getPos().x && integer7 == bxt.getPos().z) {
                bxt.addEntity(aio5);
            }
        }
    }
    
    @Nullable
    @Override
    public Entity getEntity(final int integer) {
        return (Entity)this.entitiesById.get(integer);
    }
    
    public void setKnownState(final BlockPos ew, final BlockState bvt) {
        this.setBlock(ew, bvt, 19);
    }
    
    @Override
    public void disconnect() {
        this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting", new Object[0]));
    }
    
    public void animateTick(final int integer1, final int integer2, final int integer3) {
        final int integer4 = 32;
        final Random random6 = new Random();
        final ItemStack bcj7 = this.minecraft.player.getMainHandItem();
        final boolean boolean8 = this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && !bcj7.isEmpty() && bcj7.getItem() == Blocks.BARRIER.asItem();
        final BlockPos.MutableBlockPos a9 = new BlockPos.MutableBlockPos();
        for (int integer5 = 0; integer5 < 667; ++integer5) {
            this.doAnimateTick(integer1, integer2, integer3, 16, random6, boolean8, a9);
            this.doAnimateTick(integer1, integer2, integer3, 32, random6, boolean8, a9);
        }
    }
    
    public void doAnimateTick(final int integer1, final int integer2, final int integer3, final int integer4, final Random random, final boolean boolean6, final BlockPos.MutableBlockPos a) {
        final int integer5 = integer1 + this.random.nextInt(integer4) - this.random.nextInt(integer4);
        final int integer6 = integer2 + this.random.nextInt(integer4) - this.random.nextInt(integer4);
        final int integer7 = integer3 + this.random.nextInt(integer4) - this.random.nextInt(integer4);
        a.set(integer5, integer6, integer7);
        final BlockState bvt12 = this.getBlockState(a);
        bvt12.getBlock().animateTick(bvt12, this, a, random);
        final FluidState clk13 = this.getFluidState(a);
        if (!clk13.isEmpty()) {
            clk13.animateTick(this, a, random);
            final ParticleOptions gf14 = clk13.getDripParticle();
            if (gf14 != null && this.random.nextInt(10) == 0) {
                final boolean boolean7 = bvt12.isFaceSturdy(this, a, Direction.DOWN);
                final BlockPos ew16 = a.below();
                this.trySpawnDripParticles(ew16, this.getBlockState(ew16), gf14, boolean7);
            }
        }
        if (boolean6 && bvt12.getBlock() == Blocks.BARRIER) {
            this.addParticle(ParticleTypes.BARRIER, integer5 + 0.5f, integer6 + 0.5f, integer7 + 0.5f, 0.0, 0.0, 0.0);
        }
    }
    
    private void trySpawnDripParticles(final BlockPos ew, final BlockState bvt, final ParticleOptions gf, final boolean boolean4) {
        if (!bvt.getFluidState().isEmpty()) {
            return;
        }
        final VoxelShape ctc6 = bvt.getCollisionShape(this, ew);
        final double double7 = ctc6.max(Direction.Axis.Y);
        if (double7 < 1.0) {
            if (boolean4) {
                this.spawnFluidParticle(ew.getX(), ew.getX() + 1, ew.getZ(), ew.getZ() + 1, ew.getY() + 1 - 0.05, gf);
            }
        }
        else if (!bvt.is(BlockTags.IMPERMEABLE)) {
            final double double8 = ctc6.min(Direction.Axis.Y);
            if (double8 > 0.0) {
                this.spawnParticle(ew, gf, ctc6, ew.getY() + double8 - 0.05);
            }
            else {
                final BlockPos ew2 = ew.below();
                final BlockState bvt2 = this.getBlockState(ew2);
                final VoxelShape ctc7 = bvt2.getCollisionShape(this, ew2);
                final double double9 = ctc7.max(Direction.Axis.Y);
                if (double9 < 1.0 && bvt2.getFluidState().isEmpty()) {
                    this.spawnParticle(ew, gf, ctc6, ew.getY() - 0.05);
                }
            }
        }
    }
    
    private void spawnParticle(final BlockPos ew, final ParticleOptions gf, final VoxelShape ctc, final double double4) {
        this.spawnFluidParticle(ew.getX() + ctc.min(Direction.Axis.X), ew.getX() + ctc.max(Direction.Axis.X), ew.getZ() + ctc.min(Direction.Axis.Z), ew.getZ() + ctc.max(Direction.Axis.Z), double4, gf);
    }
    
    private void spawnFluidParticle(final double double1, final double double2, final double double3, final double double4, final double double5, final ParticleOptions gf) {
        this.addParticle(gf, Mth.lerp(this.random.nextDouble(), double1, double2), double5, Mth.lerp(this.random.nextDouble(), double3, double4), 0.0, 0.0, 0.0);
    }
    
    public void removeAllPendingEntityRemovals() {
        final ObjectIterator<Int2ObjectMap.Entry<Entity>> objectIterator2 = (ObjectIterator<Int2ObjectMap.Entry<Entity>>)this.entitiesById.int2ObjectEntrySet().iterator();
        while (objectIterator2.hasNext()) {
            final Int2ObjectMap.Entry<Entity> entry3 = (Int2ObjectMap.Entry<Entity>)objectIterator2.next();
            final Entity aio4 = (Entity)entry3.getValue();
            if (aio4.removed) {
                objectIterator2.remove();
                this.onEntityRemoved(aio4);
            }
        }
    }
    
    @Override
    public CrashReportCategory fillReportDetails(final CrashReport d) {
        final CrashReportCategory e3 = super.fillReportDetails(d);
        e3.setDetail("Server brand", (CrashReportDetail<String>)(() -> this.minecraft.player.getServerBrand()));
        e3.setDetail("Server type", (CrashReportDetail<String>)(() -> (this.minecraft.getSingleplayerServer() == null) ? "Non-integrated multiplayer server" : "Integrated singleplayer server"));
        return e3;
    }
    
    @Override
    public void playSound(@Nullable final Player awg, final double double2, final double double3, final double double4, final SoundEvent yo, final SoundSource yq, final float float7, final float float8) {
        if (awg == this.minecraft.player) {
            this.playLocalSound(double2, double3, double4, yo, yq, float7, float8, false);
        }
    }
    
    @Override
    public void playSound(@Nullable final Player awg, final Entity aio, final SoundEvent yo, final SoundSource yq, final float float5, final float float6) {
        if (awg == this.minecraft.player) {
            this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(yo, yq, aio));
        }
    }
    
    public void playLocalSound(final BlockPos ew, final SoundEvent yo, final SoundSource yq, final float float4, final float float5, final boolean boolean6) {
        this.playLocalSound(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, yo, yq, float4, float5, boolean6);
    }
    
    @Override
    public void playLocalSound(final double double1, final double double2, final double double3, final SoundEvent yo, final SoundSource yq, final float float6, final float float7, final boolean boolean8) {
        final double double4 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(double1, double2, double3);
        final SimpleSoundInstance dzl15 = new SimpleSoundInstance(yo, yq, float6, float7, (float)double1, (float)double2, (float)double3);
        if (boolean8 && double4 > 100.0) {
            final double double5 = Math.sqrt(double4) / 40.0;
            this.minecraft.getSoundManager().playDelayed(dzl15, (int)(double5 * 20.0));
        }
        else {
            this.minecraft.getSoundManager().play(dzl15);
        }
    }
    
    @Override
    public void createFireworks(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6, @Nullable final CompoundTag id) {
        this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, double1, double2, double3, double4, double5, double6, this.minecraft.particleEngine, id));
    }
    
    @Override
    public void sendPacketToServer(final Packet<?> kc) {
        this.connection.send(kc);
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        return this.connection.getRecipeManager();
    }
    
    public void setScoreboard(final Scoreboard cti) {
        this.scoreboard = cti;
    }
    
    @Override
    public void setDayTime(long long1) {
        if (long1 < 0L) {
            long1 = -long1;
            this.getGameRules().<GameRules.BooleanValue>getRule(GameRules.RULE_DAYLIGHT).set(false, null);
        }
        else {
            this.getGameRules().<GameRules.BooleanValue>getRule(GameRules.RULE_DAYLIGHT).set(true, null);
        }
        super.setDayTime(long1);
    }
    
    public TickList<Block> getBlockTicks() {
        return EmptyTickList.empty();
    }
    
    public TickList<Fluid> getLiquidTicks() {
        return EmptyTickList.empty();
    }
    
    @Override
    public ClientChunkCache getChunkSource() {
        return (ClientChunkCache)super.getChunkSource();
    }
    
    @Nullable
    @Override
    public MapItemSavedData getMapData(final String string) {
        return (MapItemSavedData)this.mapData.get(string);
    }
    
    @Override
    public void setMapData(final MapItemSavedData coh) {
        this.mapData.put(coh.getId(), coh);
    }
    
    @Override
    public int getFreeMapId() {
        return 0;
    }
    
    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    @Override
    public TagManager getTagManager() {
        return this.connection.getTags();
    }
    
    @Override
    public void sendBlockUpdated(final BlockPos ew, final BlockState bvt2, final BlockState bvt3, final int integer) {
        this.levelRenderer.blockChanged(this, ew, bvt2, bvt3, integer);
    }
    
    @Override
    public void setBlocksDirty(final BlockPos ew, final BlockState bvt2, final BlockState bvt3) {
        this.levelRenderer.setBlockDirty(ew, bvt2, bvt3);
    }
    
    public void setSectionDirtyWithNeighbors(final int integer1, final int integer2, final int integer3) {
        this.levelRenderer.setSectionDirtyWithNeighbors(integer1, integer2, integer3);
    }
    
    @Override
    public void destroyBlockProgress(final int integer1, final BlockPos ew, final int integer3) {
        this.levelRenderer.destroyBlockProgress(integer1, ew, integer3);
    }
    
    @Override
    public void globalLevelEvent(final int integer1, final BlockPos ew, final int integer3) {
        this.levelRenderer.globalLevelEvent(integer1, ew, integer3);
    }
    
    public void levelEvent(@Nullable final Player awg, final int integer2, final BlockPos ew, final int integer4) {
        try {
            this.levelRenderer.levelEvent(awg, integer2, ew, integer4);
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Playing level event");
            final CrashReportCategory e8 = d7.addCategory("Level event being played");
            e8.setDetail("Block coordinates", CrashReportCategory.formatLocation(ew));
            e8.setDetail("Event source", awg);
            e8.setDetail("Event type", integer2);
            e8.setDetail("Event data", integer4);
            throw new ReportedException(d7);
        }
    }
    
    @Override
    public void addParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this.levelRenderer.addParticle(gf, gf.getType().getOverrideLimiter(), double2, double3, double4, double5, double6, double7);
    }
    
    @Override
    public void addParticle(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
        this.levelRenderer.addParticle(gf, gf.getType().getOverrideLimiter() || boolean2, double3, double4, double5, double6, double7, double8);
    }
    
    @Override
    public void addAlwaysVisibleParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this.levelRenderer.addParticle(gf, false, true, double2, double3, double4, double5, double6, double7);
    }
    
    @Override
    public void addAlwaysVisibleParticle(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
        this.levelRenderer.addParticle(gf, gf.getType().getOverrideLimiter() || boolean2, true, double3, double4, double5, double6, double7, double8);
    }
    
    public List<AbstractClientPlayer> players() {
        return this.players;
    }
}

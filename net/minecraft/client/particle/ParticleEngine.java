package net.minecraft.client.particle;

import java.util.stream.Collectors;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import com.google.common.collect.EvictingQueue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.Camera;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import java.io.Reader;
import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import net.minecraft.util.GsonHelper;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.google.common.collect.Queues;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Random;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.Queue;
import java.util.Map;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class ParticleEngine implements PreparableReloadListener {
    private static final List<ParticleRenderType> RENDER_ORDER;
    protected Level level;
    private final Map<ParticleRenderType, Queue<Particle>> particles;
    private final Queue<TrackingEmitter> trackingEmitters;
    private final TextureManager textureManager;
    private final Random random;
    private final Int2ObjectMap<ParticleProvider<?>> providers;
    private final Queue<Particle> particlesToAdd;
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets;
    private final TextureAtlas textureAtlas;
    
    public ParticleEngine(final Level bhr, final TextureManager dxc) {
        this.particles = (Map<ParticleRenderType, Queue<Particle>>)Maps.newIdentityHashMap();
        this.trackingEmitters = (Queue<TrackingEmitter>)Queues.newArrayDeque();
        this.random = new Random();
        this.providers = (Int2ObjectMap<ParticleProvider<?>>)new Int2ObjectOpenHashMap();
        this.particlesToAdd = (Queue<Particle>)Queues.newArrayDeque();
        this.spriteSets = (Map<ResourceLocation, MutableSpriteSet>)Maps.newHashMap();
        this.textureAtlas = new TextureAtlas("textures/particle");
        dxc.register(TextureAtlas.LOCATION_PARTICLES, this.textureAtlas);
        this.level = bhr;
        this.textureManager = dxc;
        this.registerProviders();
    }
    
    private void registerProviders() {
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.BARRIER, (ParticleProvider<ParticleOptions>)new BarrierParticle.Provider());
        this.<BlockParticleOption>register(ParticleTypes.BLOCK, new TerrainParticle.Provider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.CRIT, CritParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
        this.<DustParticleOptions>register(ParticleTypes.DUST, DustParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.EFFECT, SpellParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ELDER_GUARDIAN, (ParticleProvider<ParticleOptions>)new MobAppearanceParticle.Provider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ENCHANT, EnchantmentTableParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.END_ROD, EndRodParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ENTITY_EFFECT, SpellParticle.MobProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.EXPLOSION_EMITTER, (ParticleProvider<ParticleOptions>)new HugeExplosionSeedParticle.Provider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
        this.<BlockParticleOption>register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FISHING, WakeParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FLAME, FlameParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.HEART, HeartParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
        this.<ItemParticleOption>register(ParticleTypes.ITEM, new BreakingItemParticle.Provider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ITEM_SLIME, (ParticleProvider<ParticleOptions>)new BreakingItemParticle.SlimeProvider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.ITEM_SNOWBALL, (ParticleProvider<ParticleOptions>)new BreakingItemParticle.SnowballProvider());
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.LAVA, LavaParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.NOTE, NoteParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.POOF, ExplodeParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.PORTAL, PortalParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.RAIN, WaterDropParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SMOKE, SmokeParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SPIT, SpitParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.UNDERWATER, SuspendedParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.SPLASH, SplashParticle.Provider::new);
        this.<ParticleOptions>register((ParticleType<ParticleOptions>)ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
    }
    
    private <T extends ParticleOptions> void register(final ParticleType<T> gg, final ParticleProvider<T> dlq) {
        this.providers.put(Registry.PARTICLE_TYPE.getId(gg), dlq);
    }
    
    private <T extends ParticleOptions> void register(final ParticleType<T> gg, final SpriteParticleRegistration<T> b) {
        final MutableSpriteSet a4 = new MutableSpriteSet();
        this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(gg), a4);
        this.providers.put(Registry.PARTICLE_TYPE.getId(gg), b.create(a4));
    }
    
    public CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6) {
        final Map<ResourceLocation, List<ResourceLocation>> map8 = (Map<ResourceLocation, List<ResourceLocation>>)Maps.newConcurrentMap();
        final CompletableFuture<?>[] arr9 = Registry.PARTICLE_TYPE.keySet().stream().map(qv -> CompletableFuture.runAsync(() -> this.loadParticleDescription(xi, qv, map8), executor5)).toArray(CompletableFuture[]::new);
        return (CompletableFuture<Void>)CompletableFuture.allOf((CompletableFuture[])arr9).thenApplyAsync(void4 -> {
            agn3.startTick();
            agn3.push("stitching");
            final Set<ResourceLocation> set6 = (Set<ResourceLocation>)map8.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            final TextureAtlas.Preparations a7 = this.textureAtlas.prepareToStitch(xi, (Iterable<ResourceLocation>)set6, agn3);
            agn3.pop();
            agn3.endTick();
            return a7;
        }, executor5).thenCompose(a::wait).thenAcceptAsync(a -> {
            agn4.startTick();
            agn4.push("upload");
            this.textureAtlas.reload(a);
            agn4.popPush("bindSpriteSets");
            final TextureAtlasSprite dxb5 = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
            map8.forEach((qv, list) -> {
                final ImmutableList<TextureAtlasSprite> immutableList5 = (ImmutableList<TextureAtlasSprite>)(list.isEmpty() ? ImmutableList.of(dxb5) : list.stream().map(this.textureAtlas::getSprite).collect(ImmutableList.toImmutableList()));
                ((MutableSpriteSet)this.spriteSets.get(qv)).rebind((List<TextureAtlasSprite>)immutableList5);
            });
            agn4.pop();
            agn4.endTick();
        }, executor6);
    }
    
    public void close() {
        this.textureAtlas.clearTextureData();
    }
    
    private void loadParticleDescription(final ResourceManager xi, final ResourceLocation qv, final Map<ResourceLocation, List<ResourceLocation>> map) {
        final ResourceLocation qv2 = new ResourceLocation(qv.getNamespace(), "particles/" + qv.getPath() + ".json");
        try (final Resource xh6 = xi.getResource(qv2);
             final Reader reader8 = (Reader)new InputStreamReader(xh6.getInputStream(), Charsets.UTF_8)) {
            final ParticleDescription dlo10 = ParticleDescription.fromJson(GsonHelper.parse(reader8));
            final List<ResourceLocation> list11 = dlo10.getTextures();
            final boolean boolean12 = this.spriteSets.containsKey(qv);
            if (list11 == null) {
                if (boolean12) {
                    throw new IllegalStateException(new StringBuilder().append("Missing texture list for particle ").append(qv).toString());
                }
            }
            else {
                if (!boolean12) {
                    throw new IllegalStateException(new StringBuilder().append("Redundant texture list for particle ").append(qv).toString());
                }
                map.put(qv, list11);
            }
        }
        catch (IOException iOException6) {
            throw new IllegalStateException(new StringBuilder().append("Failed to load description for particle ").append(qv).toString(), (Throwable)iOException6);
        }
    }
    
    public void createTrackingEmitter(final Entity aio, final ParticleOptions gf) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, aio, gf));
    }
    
    public void createTrackingEmitter(final Entity aio, final ParticleOptions gf, final int integer) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, aio, gf, integer));
    }
    
    @Nullable
    public Particle createParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        final Particle dln15 = this.<ParticleOptions>makeParticle(gf, double2, double3, double4, double5, double6, double7);
        if (dln15 != null) {
            this.add(dln15);
            return dln15;
        }
        return null;
    }
    
    @Nullable
    private <T extends ParticleOptions> Particle makeParticle(final T gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        final ParticleProvider<T> dlq15 = (ParticleProvider<T>)this.providers.get(Registry.PARTICLE_TYPE.getId(gf.getType()));
        if (dlq15 == null) {
            return null;
        }
        return dlq15.createParticle(gf, this.level, double2, double3, double4, double5, double6, double7);
    }
    
    public void add(final Particle dln) {
        this.particlesToAdd.add(dln);
    }
    
    public void tick() {
        this.particles.forEach((dlr, queue) -> {
            this.level.getProfiler().push(dlr.toString());
            this.tickParticleList((Collection<Particle>)queue);
            this.level.getProfiler().pop();
        });
        if (!this.trackingEmitters.isEmpty()) {
            final List<TrackingEmitter> list2 = (List<TrackingEmitter>)Lists.newArrayList();
            for (final TrackingEmitter dmh4 : this.trackingEmitters) {
                dmh4.tick();
                if (!dmh4.isAlive()) {
                    list2.add(dmh4);
                }
            }
            this.trackingEmitters.removeAll((Collection)list2);
        }
        if (!this.particlesToAdd.isEmpty()) {
            Particle dln2;
            while ((dln2 = (Particle)this.particlesToAdd.poll()) != null) {
                ((Queue)this.particles.computeIfAbsent(dln2.getRenderType(), dlr -> EvictingQueue.create(16384))).add(dln2);
            }
        }
    }
    
    private void tickParticleList(final Collection<Particle> collection) {
        if (!collection.isEmpty()) {
            final Iterator<Particle> iterator3 = (Iterator<Particle>)collection.iterator();
            while (iterator3.hasNext()) {
                final Particle dln4 = (Particle)iterator3.next();
                this.tickParticle(dln4);
                if (!dln4.isAlive()) {
                    iterator3.remove();
                }
            }
        }
    }
    
    private void tickParticle(final Particle dln) {
        try {
            dln.tick();
        }
        catch (Throwable throwable3) {
            final CrashReport d4 = CrashReport.forThrowable(throwable3, "Ticking Particle");
            final CrashReportCategory e5 = d4.addCategory("Particle being ticked");
            e5.setDetail("Particle", (CrashReportDetail<String>)dln::toString);
            e5.setDetail("Particle Type", (CrashReportDetail<String>)dln.getRenderType()::toString);
            throw new ReportedException(d4);
        }
    }
    
    public void render(final Camera cxq, final float float2) {
        final float float3 = Mth.cos(cxq.getYRot() * 0.017453292f);
        final float float4 = Mth.sin(cxq.getYRot() * 0.017453292f);
        final float float5 = -float4 * Mth.sin(cxq.getXRot() * 0.017453292f);
        final float float6 = float3 * Mth.sin(cxq.getXRot() * 0.017453292f);
        final float float7 = Mth.cos(cxq.getXRot() * 0.017453292f);
        Particle.xOff = cxq.getPosition().x;
        Particle.yOff = cxq.getPosition().y;
        Particle.zOff = cxq.getPosition().z;
        for (final ParticleRenderType dlr10 : ParticleEngine.RENDER_ORDER) {
            final Iterable<Particle> iterable11 = (Iterable<Particle>)this.particles.get(dlr10);
            if (iterable11 == null) {
                continue;
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final Tesselator cuz12 = Tesselator.getInstance();
            final BufferBuilder cuw13 = cuz12.getBuilder();
            dlr10.begin(cuw13, this.textureManager);
            for (final Particle dln15 : iterable11) {
                try {
                    dln15.render(cuw13, cxq, float2, float3, float7, float4, float5, float6);
                }
                catch (Throwable throwable16) {
                    final CrashReport d17 = CrashReport.forThrowable(throwable16, "Rendering Particle");
                    final CrashReportCategory e18 = d17.addCategory("Particle being rendered");
                    e18.setDetail("Particle", (CrashReportDetail<String>)dln15::toString);
                    e18.setDetail("Particle Type", (CrashReportDetail<String>)dlr10::toString);
                    throw new ReportedException(d17);
                }
            }
            dlr10.end(cuz12);
        }
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
    }
    
    public void setLevel(@Nullable final Level bhr) {
        this.level = bhr;
        this.particles.clear();
        this.trackingEmitters.clear();
    }
    
    public void destroy(final BlockPos ew, final BlockState bvt) {
        if (bvt.isAir()) {
            return;
        }
        final VoxelShape ctc4 = bvt.getShape(this.level, ew);
        final double double9 = 0.25;
        final double double10;
        final double double11;
        final double double12;
        final int integer22;
        final int integer23;
        final int integer24;
        int integer25;
        int integer26;
        int integer27;
        double double13;
        double double14;
        double double15;
        double double16;
        double double17;
        double double18;
        ctc4.forAllBoxes((double3, double4, double5, double6, double7, double8) -> {
            double10 = Math.min(1.0, double6 - double3);
            double11 = Math.min(1.0, double7 - double4);
            double12 = Math.min(1.0, double8 - double5);
            integer22 = Math.max(2, Mth.ceil(double10 / 0.25));
            integer23 = Math.max(2, Mth.ceil(double11 / 0.25));
            integer24 = Math.max(2, Mth.ceil(double12 / 0.25));
            for (integer25 = 0; integer25 < integer22; ++integer25) {
                for (integer26 = 0; integer26 < integer23; ++integer26) {
                    for (integer27 = 0; integer27 < integer24; ++integer27) {
                        double13 = (integer25 + 0.5) / integer22;
                        double14 = (integer26 + 0.5) / integer23;
                        double15 = (integer27 + 0.5) / integer24;
                        double16 = double13 * double10 + double3;
                        double17 = double14 * double11 + double4;
                        double18 = double15 * double12 + double5;
                        this.add(new TerrainParticle(this.level, ew.getX() + double16, ew.getY() + double17, ew.getZ() + double18, double13 - 0.5, double14 - 0.5, double15 - 0.5, bvt).init(ew));
                    }
                }
            }
        });
    }
    
    public void crack(final BlockPos ew, final Direction fb) {
        final BlockState bvt4 = this.level.getBlockState(ew);
        if (bvt4.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        final int integer5 = ew.getX();
        final int integer6 = ew.getY();
        final int integer7 = ew.getZ();
        final float float8 = 0.1f;
        final AABB csc9 = bvt4.getShape(this.level, ew).bounds();
        double double10 = integer5 + this.random.nextDouble() * (csc9.maxX - csc9.minX - 0.20000000298023224) + 0.10000000149011612 + csc9.minX;
        double double11 = integer6 + this.random.nextDouble() * (csc9.maxY - csc9.minY - 0.20000000298023224) + 0.10000000149011612 + csc9.minY;
        double double12 = integer7 + this.random.nextDouble() * (csc9.maxZ - csc9.minZ - 0.20000000298023224) + 0.10000000149011612 + csc9.minZ;
        if (fb == Direction.DOWN) {
            double11 = integer6 + csc9.minY - 0.10000000149011612;
        }
        if (fb == Direction.UP) {
            double11 = integer6 + csc9.maxY + 0.10000000149011612;
        }
        if (fb == Direction.NORTH) {
            double12 = integer7 + csc9.minZ - 0.10000000149011612;
        }
        if (fb == Direction.SOUTH) {
            double12 = integer7 + csc9.maxZ + 0.10000000149011612;
        }
        if (fb == Direction.WEST) {
            double10 = integer5 + csc9.minX - 0.10000000149011612;
        }
        if (fb == Direction.EAST) {
            double10 = integer5 + csc9.maxX + 0.10000000149011612;
        }
        this.add(new TerrainParticle(this.level, double10, double11, double12, 0.0, 0.0, 0.0, bvt4).init(ew).setPower(0.2f).scale(0.6f));
    }
    
    public String countParticles() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }
    
    static {
        RENDER_ORDER = (List)ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);
    }
    
    class MutableSpriteSet implements SpriteSet {
        private List<TextureAtlasSprite> sprites;
        
        private MutableSpriteSet() {
        }
        
        public TextureAtlasSprite get(final int integer1, final int integer2) {
            return (TextureAtlasSprite)this.sprites.get(integer1 * (this.sprites.size() - 1) / integer2);
        }
        
        public TextureAtlasSprite get(final Random random) {
            return (TextureAtlasSprite)this.sprites.get(random.nextInt(this.sprites.size()));
        }
        
        public void rebind(final List<TextureAtlasSprite> list) {
            this.sprites = (List<TextureAtlasSprite>)ImmutableList.copyOf((Collection)list);
        }
    }
    
    @FunctionalInterface
    interface SpriteParticleRegistration<T extends ParticleOptions> {
        ParticleProvider<T> create(final SpriteSet dma);
    }
}

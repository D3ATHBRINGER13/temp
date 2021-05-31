package net.minecraft.client.renderer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.Registry;
import net.minecraft.CrashReport;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.RecordItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.Util;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.function.Supplier;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import java.util.Queue;
import java.util.Collection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import com.google.common.collect.Queues;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.Random;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.Tesselator;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import com.mojang.blaze3d.shaders.ProgramManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import com.mojang.blaze3d.platform.GLX;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.chunk.RenderChunkFactory;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.CloudStatus;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import java.util.Map;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import java.util.Set;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class LevelRenderer implements AutoCloseable, ResourceManagerReloadListener {
    private static final Logger LOGGER;
    private static final ResourceLocation MOON_LOCATION;
    private static final ResourceLocation SUN_LOCATION;
    private static final ResourceLocation CLOUDS_LOCATION;
    private static final ResourceLocation END_SKY_LOCATION;
    private static final ResourceLocation FORCEFIELD_LOCATION;
    public static final Direction[] DIRECTIONS;
    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private MultiPlayerLevel level;
    private Set<RenderChunk> chunksToCompile;
    private List<RenderChunkInfo> renderChunks;
    private final Set<BlockEntity> globalBlockEntities;
    private ViewArea viewArea;
    private int starList;
    private int skyList;
    private int darkList;
    private final VertexFormat skyFormat;
    private VertexBuffer starBuffer;
    private VertexBuffer skyBuffer;
    private VertexBuffer darkBuffer;
    private final int CLOUD_VERTEX_SIZE = 28;
    private boolean generateClouds;
    private int cloudList;
    private VertexBuffer cloudBuffer;
    private int ticks;
    private final Map<Integer, BlockDestructionProgress> destroyingBlocks;
    private final Map<BlockPos, SoundInstance> playingRecords;
    private final TextureAtlasSprite[] breakingTextures;
    private RenderTarget entityTarget;
    private PostChain entityEffect;
    private double lastCameraX;
    private double lastCameraY;
    private double lastCameraZ;
    private int lastCameraChunkX;
    private int lastCameraChunkY;
    private int lastCameraChunkZ;
    private double prevCamX;
    private double prevCamY;
    private double prevCamZ;
    private double prevCamRotX;
    private double prevCamRotY;
    private int prevCloudX;
    private int prevCloudY;
    private int prevCloudZ;
    private Vec3 prevCloudColor;
    private CloudStatus prevCloudsType;
    private ChunkRenderDispatcher chunkRenderDispatcher;
    private ChunkRenderList renderList;
    private int lastViewDistance;
    private int noEntityRenderFrames;
    private int renderedEntities;
    private int culledEntities;
    private boolean captureFrustum;
    private FrustumData capturedFrustum;
    private final Vector4f[] frustumPoints;
    private final Vector3d frustumPos;
    private boolean usingVbo;
    private RenderChunkFactory renderChunkFactory;
    private double xTransparentOld;
    private double yTransparentOld;
    private double zTransparentOld;
    private boolean needsUpdate;
    private boolean hadRenderedEntityOutlines;
    
    public LevelRenderer(final Minecraft cyc) {
        this.chunksToCompile = (Set<RenderChunk>)Sets.newLinkedHashSet();
        this.renderChunks = (List<RenderChunkInfo>)Lists.newArrayListWithCapacity(69696);
        this.globalBlockEntities = (Set<BlockEntity>)Sets.newHashSet();
        this.starList = -1;
        this.skyList = -1;
        this.darkList = -1;
        this.generateClouds = true;
        this.cloudList = -1;
        this.destroyingBlocks = (Map<Integer, BlockDestructionProgress>)Maps.newHashMap();
        this.playingRecords = (Map<BlockPos, SoundInstance>)Maps.newHashMap();
        this.breakingTextures = new TextureAtlasSprite[10];
        this.lastCameraX = Double.MIN_VALUE;
        this.lastCameraY = Double.MIN_VALUE;
        this.lastCameraZ = Double.MIN_VALUE;
        this.lastCameraChunkX = Integer.MIN_VALUE;
        this.lastCameraChunkY = Integer.MIN_VALUE;
        this.lastCameraChunkZ = Integer.MIN_VALUE;
        this.prevCamX = Double.MIN_VALUE;
        this.prevCamY = Double.MIN_VALUE;
        this.prevCamZ = Double.MIN_VALUE;
        this.prevCamRotX = Double.MIN_VALUE;
        this.prevCamRotY = Double.MIN_VALUE;
        this.prevCloudX = Integer.MIN_VALUE;
        this.prevCloudY = Integer.MIN_VALUE;
        this.prevCloudZ = Integer.MIN_VALUE;
        this.prevCloudColor = Vec3.ZERO;
        this.lastViewDistance = -1;
        this.noEntityRenderFrames = 2;
        this.frustumPoints = new Vector4f[8];
        this.frustumPos = new Vector3d(0.0, 0.0, 0.0);
        this.needsUpdate = true;
        this.minecraft = cyc;
        this.entityRenderDispatcher = cyc.getEntityRenderDispatcher();
        this.textureManager = cyc.getTextureManager();
        this.usingVbo = GLX.useVbo();
        if (this.usingVbo) {
            this.renderList = new VboRenderList();
            this.renderChunkFactory = RenderChunk::new;
        }
        else {
            this.renderList = new OffsettedRenderList();
            this.renderChunkFactory = ListedRenderChunk::new;
        }
        (this.skyFormat = new VertexFormat()).addElement(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
        this.createStars();
        this.createLightSky();
        this.createDarkSky();
    }
    
    public void close() {
        if (this.entityEffect != null) {
            this.entityEffect.close();
        }
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        this.textureManager.bind(LevelRenderer.FORCEFIELD_LOCATION);
        GlStateManager.texParameter(3553, 10242, 10497);
        GlStateManager.texParameter(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.setupBreakingTextureSprites();
        this.initOutline();
    }
    
    private void setupBreakingTextureSprites() {
        final TextureAtlas dxa2 = this.minecraft.getTextureAtlas();
        this.breakingTextures[0] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_0);
        this.breakingTextures[1] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_1);
        this.breakingTextures[2] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_2);
        this.breakingTextures[3] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_3);
        this.breakingTextures[4] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_4);
        this.breakingTextures[5] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_5);
        this.breakingTextures[6] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_6);
        this.breakingTextures[7] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_7);
        this.breakingTextures[8] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_8);
        this.breakingTextures[9] = dxa2.getSprite(ModelBakery.DESTROY_STAGE_9);
    }
    
    public void initOutline() {
        if (GLX.usePostProcess) {
            if (ProgramManager.getInstance() == null) {
                ProgramManager.createInstance();
            }
            if (this.entityEffect != null) {
                this.entityEffect.close();
            }
            final ResourceLocation qv2 = new ResourceLocation("shaders/post/entity_outline.json");
            try {
                (this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), qv2)).resize(this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
                this.entityTarget = this.entityEffect.getTempTarget("final");
            }
            catch (IOException iOException3) {
                LevelRenderer.LOGGER.warn("Failed to load shader: {}", qv2, iOException3);
                this.entityEffect = null;
                this.entityTarget = null;
            }
            catch (JsonSyntaxException jsonSyntaxException3) {
                LevelRenderer.LOGGER.warn("Failed to load shader: {}", qv2, jsonSyntaxException3);
                this.entityEffect = null;
                this.entityTarget = null;
            }
        }
        else {
            this.entityEffect = null;
            this.entityTarget = null;
        }
    }
    
    public void doEntityOutline() {
        if (this.shouldShowEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityTarget.blitToScreen(this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), false);
            GlStateManager.disableBlend();
        }
    }
    
    protected boolean shouldShowEntityOutlines() {
        return this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
    }
    
    private void createDarkSky() {
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        if (this.darkBuffer != null) {
            this.darkBuffer.delete();
        }
        if (this.darkList >= 0) {
            MemoryTracker.releaseList(this.darkList);
            this.darkList = -1;
        }
        if (this.usingVbo) {
            this.darkBuffer = new VertexBuffer(this.skyFormat);
            this.drawSkyHemisphere(cuw3, -16.0f, true);
            cuw3.end();
            cuw3.clear();
            this.darkBuffer.upload(cuw3.getBuffer());
        }
        else {
            GlStateManager.newList(this.darkList = MemoryTracker.genLists(1), 4864);
            this.drawSkyHemisphere(cuw3, -16.0f, true);
            cuz2.end();
            GlStateManager.endList();
        }
    }
    
    private void createLightSky() {
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        if (this.skyBuffer != null) {
            this.skyBuffer.delete();
        }
        if (this.skyList >= 0) {
            MemoryTracker.releaseList(this.skyList);
            this.skyList = -1;
        }
        if (this.usingVbo) {
            this.skyBuffer = new VertexBuffer(this.skyFormat);
            this.drawSkyHemisphere(cuw3, 16.0f, false);
            cuw3.end();
            cuw3.clear();
            this.skyBuffer.upload(cuw3.getBuffer());
        }
        else {
            GlStateManager.newList(this.skyList = MemoryTracker.genLists(1), 4864);
            this.drawSkyHemisphere(cuw3, 16.0f, false);
            cuz2.end();
            GlStateManager.endList();
        }
    }
    
    private void drawSkyHemisphere(final BufferBuilder cuw, final float float2, final boolean boolean3) {
        final int integer5 = 64;
        final int integer6 = 6;
        cuw.begin(7, DefaultVertexFormat.POSITION);
        for (int integer7 = -384; integer7 <= 384; integer7 += 64) {
            for (int integer8 = -384; integer8 <= 384; integer8 += 64) {
                float float3 = (float)integer7;
                float float4 = (float)(integer7 + 64);
                if (boolean3) {
                    float4 = (float)integer7;
                    float3 = (float)(integer7 + 64);
                }
                cuw.vertex(float3, float2, integer8).endVertex();
                cuw.vertex(float4, float2, integer8).endVertex();
                cuw.vertex(float4, float2, integer8 + 64).endVertex();
                cuw.vertex(float3, float2, integer8 + 64).endVertex();
            }
        }
    }
    
    private void createStars() {
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        if (this.starBuffer != null) {
            this.starBuffer.delete();
        }
        if (this.starList >= 0) {
            MemoryTracker.releaseList(this.starList);
            this.starList = -1;
        }
        if (this.usingVbo) {
            this.starBuffer = new VertexBuffer(this.skyFormat);
            this.drawStars(cuw3);
            cuw3.end();
            cuw3.clear();
            this.starBuffer.upload(cuw3.getBuffer());
        }
        else {
            this.starList = MemoryTracker.genLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.newList(this.starList, 4864);
            this.drawStars(cuw3);
            cuz2.end();
            GlStateManager.endList();
            GlStateManager.popMatrix();
        }
    }
    
    private void drawStars(final BufferBuilder cuw) {
        final Random random3 = new Random(10842L);
        cuw.begin(7, DefaultVertexFormat.POSITION);
        for (int integer4 = 0; integer4 < 1500; ++integer4) {
            double double5 = random3.nextFloat() * 2.0f - 1.0f;
            double double6 = random3.nextFloat() * 2.0f - 1.0f;
            double double7 = random3.nextFloat() * 2.0f - 1.0f;
            final double double8 = 0.15f + random3.nextFloat() * 0.1f;
            double double9 = double5 * double5 + double6 * double6 + double7 * double7;
            if (double9 < 1.0 && double9 > 0.01) {
                double9 = 1.0 / Math.sqrt(double9);
                double5 *= double9;
                double6 *= double9;
                double7 *= double9;
                final double double10 = double5 * 100.0;
                final double double11 = double6 * 100.0;
                final double double12 = double7 * 100.0;
                final double double13 = Math.atan2(double5, double7);
                final double double14 = Math.sin(double13);
                final double double15 = Math.cos(double13);
                final double double16 = Math.atan2(Math.sqrt(double5 * double5 + double7 * double7), double6);
                final double double17 = Math.sin(double16);
                final double double18 = Math.cos(double16);
                final double double19 = random3.nextDouble() * 3.141592653589793 * 2.0;
                final double double20 = Math.sin(double19);
                final double double21 = Math.cos(double19);
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    final double double22 = 0.0;
                    final double double23 = ((integer5 & 0x2) - 1) * double8;
                    final double double24 = ((integer5 + 1 & 0x2) - 1) * double8;
                    final double double25 = 0.0;
                    final double double26 = double23 * double21 - double24 * double20;
                    final double double28;
                    final double double27 = double28 = double24 * double21 + double23 * double20;
                    final double double29 = double26 * double17 + 0.0 * double18;
                    final double double30 = 0.0 * double17 - double26 * double18;
                    final double double31 = double30 * double14 - double28 * double15;
                    final double double32 = double29;
                    final double double33 = double28 * double14 + double30 * double15;
                    cuw.vertex(double10 + double31, double11 + double32, double12 + double33).endVertex();
                }
            }
        }
    }
    
    public void setLevel(@Nullable final MultiPlayerLevel dkf) {
        this.lastCameraX = Double.MIN_VALUE;
        this.lastCameraY = Double.MIN_VALUE;
        this.lastCameraZ = Double.MIN_VALUE;
        this.lastCameraChunkX = Integer.MIN_VALUE;
        this.lastCameraChunkY = Integer.MIN_VALUE;
        this.lastCameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setLevel(dkf);
        this.level = dkf;
        if (dkf != null) {
            this.allChanged();
        }
        else {
            this.chunksToCompile.clear();
            this.renderChunks.clear();
            if (this.viewArea != null) {
                this.viewArea.releaseAllBuffers();
                this.viewArea = null;
            }
            if (this.chunkRenderDispatcher != null) {
                this.chunkRenderDispatcher.dispose();
            }
            this.chunkRenderDispatcher = null;
            this.globalBlockEntities.clear();
        }
    }
    
    public void allChanged() {
        if (this.level == null) {
            return;
        }
        if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.minecraft.is64Bit());
        }
        this.needsUpdate = true;
        this.generateClouds = true;
        LeavesBlock.setFancy(this.minecraft.options.fancyGraphics);
        this.lastViewDistance = this.minecraft.options.renderDistance;
        final boolean boolean2 = this.usingVbo;
        this.usingVbo = GLX.useVbo();
        if (boolean2 && !this.usingVbo) {
            this.renderList = new OffsettedRenderList();
            this.renderChunkFactory = ListedRenderChunk::new;
        }
        else if (!boolean2 && this.usingVbo) {
            this.renderList = new VboRenderList();
            this.renderChunkFactory = RenderChunk::new;
        }
        if (boolean2 != this.usingVbo) {
            this.createStars();
            this.createLightSky();
            this.createDarkSky();
        }
        if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
        }
        this.resetChunksToCompile();
        synchronized (this.globalBlockEntities) {
            this.globalBlockEntities.clear();
        }
        this.viewArea = new ViewArea(this.level, this.minecraft.options.renderDistance, this, this.renderChunkFactory);
        if (this.level != null) {
            final Entity aio3 = this.minecraft.getCameraEntity();
            if (aio3 != null) {
                this.viewArea.repositionCamera(aio3.x, aio3.z);
            }
        }
        this.noEntityRenderFrames = 2;
    }
    
    protected void resetChunksToCompile() {
        this.chunksToCompile.clear();
        this.chunkRenderDispatcher.blockUntilClear();
    }
    
    public void resize(final int integer1, final int integer2) {
        this.needsUpdate();
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.entityEffect != null) {
            this.entityEffect.resize(integer1, integer2);
        }
    }
    
    public void prepare(final Camera cxq) {
        BlockEntityRenderDispatcher.instance.prepare(this.level, this.minecraft.getTextureManager(), this.minecraft.font, cxq, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, this.minecraft.font, cxq, this.minecraft.crosshairPickEntity, this.minecraft.options);
    }
    
    public void renderEntities(final Camera cxq, final Culler dqe, final float float3) {
        if (this.noEntityRenderFrames > 0) {
            --this.noEntityRenderFrames;
            return;
        }
        final double double5 = cxq.getPosition().x;
        final double double6 = cxq.getPosition().y;
        final double double7 = cxq.getPosition().z;
        this.level.getProfiler().push("prepare");
        this.renderedEntities = 0;
        this.culledEntities = 0;
        final double double8 = cxq.getPosition().x;
        final double double9 = cxq.getPosition().y;
        final double double10 = cxq.getPosition().z;
        BlockEntityRenderDispatcher.xOff = double8;
        BlockEntityRenderDispatcher.yOff = double9;
        BlockEntityRenderDispatcher.zOff = double10;
        this.entityRenderDispatcher.setPosition(double8, double9, double10);
        this.minecraft.gameRenderer.turnOnLightLayer();
        this.level.getProfiler().popPush("entities");
        final List<Entity> list17 = (List<Entity>)Lists.newArrayList();
        final List<Entity> list18 = (List<Entity>)Lists.newArrayList();
        for (final Entity aio20 : this.level.entitiesForRendering()) {
            if (!this.entityRenderDispatcher.shouldRender(aio20, dqe, double5, double6, double7) && !aio20.hasIndirectPassenger(this.minecraft.player)) {
                continue;
            }
            if (aio20 == cxq.getEntity() && !cxq.isDetached()) {
                if (!(cxq.getEntity() instanceof LivingEntity)) {
                    continue;
                }
                if (!((LivingEntity)cxq.getEntity()).isSleeping()) {
                    continue;
                }
            }
            ++this.renderedEntities;
            this.entityRenderDispatcher.render(aio20, float3, false);
            if (aio20.isGlowing() || (aio20 instanceof Player && this.minecraft.player.isSpectator() && this.minecraft.options.keySpectatorOutlines.isDown())) {
                list17.add(aio20);
            }
            if (!this.entityRenderDispatcher.hasSecondPass(aio20)) {
                continue;
            }
            list18.add(aio20);
        }
        if (!list18.isEmpty()) {
            for (final Entity aio20 : list18) {
                this.entityRenderDispatcher.renderSecondPass(aio20, float3);
            }
        }
        if (this.shouldShowEntityOutlines() && (!list17.isEmpty() || this.hadRenderedEntityOutlines)) {
            this.level.getProfiler().popPush("entityOutlines");
            this.entityTarget.clear(Minecraft.ON_OSX);
            this.hadRenderedEntityOutlines = !list17.isEmpty();
            if (!list17.isEmpty()) {
                GlStateManager.depthFunc(519);
                GlStateManager.disableFog();
                this.entityTarget.bindWrite(false);
                Lighting.turnOff();
                this.entityRenderDispatcher.setSolidRendering(true);
                for (int integer19 = 0; integer19 < list17.size(); ++integer19) {
                    this.entityRenderDispatcher.render((Entity)list17.get(integer19), float3, false);
                }
                this.entityRenderDispatcher.setSolidRendering(false);
                Lighting.turnOn();
                GlStateManager.depthMask(false);
                this.entityEffect.process(float3);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(515);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
            }
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        this.level.getProfiler().popPush("blockentities");
        Lighting.turnOn();
        for (final RenderChunkInfo a20 : this.renderChunks) {
            final List<BlockEntity> list19 = a20.chunk.getCompiledChunk().getRenderableBlockEntities();
            if (list19.isEmpty()) {
                continue;
            }
            for (final BlockEntity btw23 : list19) {
                BlockEntityRenderDispatcher.instance.render(btw23, float3, -1);
            }
        }
        synchronized (this.globalBlockEntities) {
            for (final BlockEntity btw24 : this.globalBlockEntities) {
                BlockEntityRenderDispatcher.instance.render(btw24, float3, -1);
            }
        }
        this.setupDestroyState();
        for (final BlockDestructionProgress uu20 : this.destroyingBlocks.values()) {
            BlockPos ew21 = uu20.getPos();
            final BlockState bvt22 = this.level.getBlockState(ew21);
            if (!bvt22.getBlock().isEntityBlock()) {
                continue;
            }
            BlockEntity btw23 = this.level.getBlockEntity(ew21);
            if (btw23 instanceof ChestBlockEntity && bvt22.<ChestType>getValue(ChestBlock.TYPE) == ChestType.LEFT) {
                ew21 = ew21.relative(bvt22.<Direction>getValue((Property<Direction>)ChestBlock.FACING).getClockWise());
                btw23 = this.level.getBlockEntity(ew21);
            }
            if (btw23 == null || !bvt22.hasCustomBreakingProgress()) {
                continue;
            }
            BlockEntityRenderDispatcher.instance.render(btw23, float3, uu20.getProgress());
        }
        this.restoreDestroyState();
        this.minecraft.gameRenderer.turnOffLightLayer();
        this.minecraft.getProfiler().pop();
    }
    
    public String getChunkStatistics() {
        final int integer2 = this.viewArea.chunks.length;
        final int integer3 = this.countRenderedChunks();
        return String.format("C: %d/%d %sD: %d, %s", new Object[] { integer3, integer2, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, (this.chunkRenderDispatcher == null) ? "null" : this.chunkRenderDispatcher.getStats() });
    }
    
    protected int countRenderedChunks() {
        int integer2 = 0;
        for (final RenderChunkInfo a4 : this.renderChunks) {
            final CompiledChunk dpw5 = a4.chunk.compiled;
            if (dpw5 != CompiledChunk.UNCOMPILED && !dpw5.hasNoRenderableLayers()) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    public String getEntityStatistics() {
        return new StringBuilder().append("E: ").append(this.renderedEntities).append("/").append(this.level.getEntityCount()).append(", B: ").append(this.culledEntities).toString();
    }
    
    public void setupRender(final Camera cxq, Culler dqe, final int integer, final boolean boolean4) {
        if (this.minecraft.options.renderDistance != this.lastViewDistance) {
            this.allChanged();
        }
        this.level.getProfiler().push("camera");
        final double double6 = this.minecraft.player.x - this.lastCameraX;
        final double double7 = this.minecraft.player.y - this.lastCameraY;
        final double double8 = this.minecraft.player.z - this.lastCameraZ;
        if (this.lastCameraChunkX != this.minecraft.player.xChunk || this.lastCameraChunkY != this.minecraft.player.yChunk || this.lastCameraChunkZ != this.minecraft.player.zChunk || double6 * double6 + double7 * double7 + double8 * double8 > 16.0) {
            this.lastCameraX = this.minecraft.player.x;
            this.lastCameraY = this.minecraft.player.y;
            this.lastCameraZ = this.minecraft.player.z;
            this.lastCameraChunkX = this.minecraft.player.xChunk;
            this.lastCameraChunkY = this.minecraft.player.yChunk;
            this.lastCameraChunkZ = this.minecraft.player.zChunk;
            this.viewArea.repositionCamera(this.minecraft.player.x, this.minecraft.player.z);
        }
        this.level.getProfiler().popPush("renderlistcamera");
        this.renderList.setCameraLocation(cxq.getPosition().x, cxq.getPosition().y, cxq.getPosition().z);
        this.chunkRenderDispatcher.setCamera(cxq.getPosition());
        this.level.getProfiler().popPush("cull");
        if (this.capturedFrustum != null) {
            final FrustumCuller dqg12 = new FrustumCuller(this.capturedFrustum);
            dqg12.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
            dqe = dqg12;
        }
        this.minecraft.getProfiler().popPush("culling");
        final BlockPos ew12 = cxq.getBlockPosition();
        final RenderChunk dpy13 = this.viewArea.getRenderChunkAt(ew12);
        final BlockPos ew13 = new BlockPos(Mth.floor(cxq.getPosition().x / 16.0) * 16, Mth.floor(cxq.getPosition().y / 16.0) * 16, Mth.floor(cxq.getPosition().z / 16.0) * 16);
        final float float15 = cxq.getXRot();
        final float float16 = cxq.getYRot();
        this.needsUpdate = (this.needsUpdate || !this.chunksToCompile.isEmpty() || cxq.getPosition().x != this.prevCamX || cxq.getPosition().y != this.prevCamY || cxq.getPosition().z != this.prevCamZ || float15 != this.prevCamRotX || float16 != this.prevCamRotY);
        this.prevCamX = cxq.getPosition().x;
        this.prevCamY = cxq.getPosition().y;
        this.prevCamZ = cxq.getPosition().z;
        this.prevCamRotX = float15;
        this.prevCamRotY = float16;
        final boolean boolean5 = this.capturedFrustum != null;
        this.minecraft.getProfiler().popPush("update");
        if (!boolean5 && this.needsUpdate) {
            this.needsUpdate = false;
            this.renderChunks = (List<RenderChunkInfo>)Lists.newArrayList();
            final Queue<RenderChunkInfo> queue18 = (Queue<RenderChunkInfo>)Queues.newArrayDeque();
            Entity.setViewScale(Mth.clamp(this.minecraft.options.renderDistance / 8.0, 1.0, 2.5));
            boolean boolean6 = this.minecraft.smartCull;
            if (dpy13 == null) {
                final int integer2 = (ew12.getY() > 0) ? 248 : 8;
                for (int integer3 = -this.lastViewDistance; integer3 <= this.lastViewDistance; ++integer3) {
                    for (int integer4 = -this.lastViewDistance; integer4 <= this.lastViewDistance; ++integer4) {
                        final RenderChunk dpy14 = this.viewArea.getRenderChunkAt(new BlockPos((integer3 << 4) + 8, integer2, (integer4 << 4) + 8));
                        if (dpy14 != null && dqe.isVisible(dpy14.bb)) {
                            dpy14.setFrame(integer);
                            queue18.add(new RenderChunkInfo(dpy14, (Direction)null, 0));
                        }
                    }
                }
            }
            else {
                boolean boolean7 = false;
                final RenderChunkInfo a21 = new RenderChunkInfo(dpy13, (Direction)null, 0);
                final Set<Direction> set22 = this.getVisibleDirections(ew12);
                if (set22.size() == 1) {
                    final Vec3 csi23 = cxq.getLookVector();
                    final Direction fb24 = Direction.getNearest(csi23.x, csi23.y, csi23.z).getOpposite();
                    set22.remove(fb24);
                }
                if (set22.isEmpty()) {
                    boolean7 = true;
                }
                if (!boolean7 || boolean4) {
                    if (boolean4 && this.level.getBlockState(ew12).isSolidRender(this.level, ew12)) {
                        boolean6 = false;
                    }
                    dpy13.setFrame(integer);
                    queue18.add(a21);
                }
                else {
                    this.renderChunks.add(a21);
                }
            }
            this.minecraft.getProfiler().push("iteration");
            while (!queue18.isEmpty()) {
                final RenderChunkInfo a22 = (RenderChunkInfo)queue18.poll();
                final RenderChunk dpy15 = a22.chunk;
                final Direction fb25 = a22.sourceDirection;
                this.renderChunks.add(a22);
                for (final Direction fb26 : LevelRenderer.DIRECTIONS) {
                    final RenderChunk dpy16 = this.getRelativeFrom(ew13, dpy15, fb26);
                    if (!boolean6 || !a22.hasDirection(fb26.getOpposite())) {
                        if (!boolean6 || fb25 == null || dpy15.getCompiledChunk().facesCanSeeEachother(fb25.getOpposite(), fb26)) {
                            if (dpy16 != null) {
                                if (dpy16.hasAllNeighbors()) {
                                    if (dpy16.setFrame(integer)) {
                                        if (dqe.isVisible(dpy16.bb)) {
                                            final RenderChunkInfo a23 = new RenderChunkInfo(dpy16, fb26, a22.step + 1);
                                            a23.setDirections(a22.directions, fb26);
                                            queue18.add(a23);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.minecraft.getProfiler().pop();
        }
        this.minecraft.getProfiler().popPush("captureFrustum");
        if (this.captureFrustum) {
            this.captureFrustum(cxq.getPosition().x, cxq.getPosition().y, cxq.getPosition().z);
            this.captureFrustum = false;
        }
        this.minecraft.getProfiler().popPush("rebuildNear");
        final Set<RenderChunk> set23 = this.chunksToCompile;
        this.chunksToCompile = (Set<RenderChunk>)Sets.newLinkedHashSet();
        final Iterator iterator = this.renderChunks.iterator();
        while (iterator.hasNext()) {
            final RenderChunkInfo a22 = (RenderChunkInfo)iterator.next();
            final RenderChunk dpy15 = a22.chunk;
            if (dpy15.isDirty() || set23.contains(dpy15)) {
                this.needsUpdate = true;
                final BlockPos ew14 = dpy15.getOrigin().offset(8, 8, 8);
                final boolean boolean8 = ew14.distSqr(ew12) < 768.0;
                if (dpy15.isDirtyFromPlayer() || boolean8) {
                    this.minecraft.getProfiler().push("build near");
                    this.chunkRenderDispatcher.rebuildChunkSync(dpy15);
                    dpy15.setNotDirty();
                    this.minecraft.getProfiler().pop();
                }
                else {
                    this.chunksToCompile.add(dpy15);
                }
            }
        }
        this.chunksToCompile.addAll((Collection)set23);
        this.minecraft.getProfiler().pop();
    }
    
    private Set<Direction> getVisibleDirections(final BlockPos ew) {
        final VisGraph dqb3 = new VisGraph();
        final BlockPos ew2 = new BlockPos(ew.getX() >> 4 << 4, ew.getY() >> 4 << 4, ew.getZ() >> 4 << 4);
        final LevelChunk bxt5 = this.level.getChunkAt(ew2);
        for (final BlockPos ew3 : BlockPos.betweenClosed(ew2, ew2.offset(15, 15, 15))) {
            if (bxt5.getBlockState(ew3).isSolidRender(this.level, ew3)) {
                dqb3.setOpaque(ew3);
            }
        }
        return dqb3.floodFill(ew);
    }
    
    @Nullable
    private RenderChunk getRelativeFrom(final BlockPos ew, final RenderChunk dpy, final Direction fb) {
        final BlockPos ew2 = dpy.getRelativeOrigin(fb);
        if (Mth.abs(ew.getX() - ew2.getX()) > this.lastViewDistance * 16) {
            return null;
        }
        if (ew2.getY() < 0 || ew2.getY() >= 256) {
            return null;
        }
        if (Mth.abs(ew.getZ() - ew2.getZ()) > this.lastViewDistance * 16) {
            return null;
        }
        return this.viewArea.getRenderChunkAt(ew2);
    }
    
    private void captureFrustum(final double double1, final double double2, final double double3) {
    }
    
    public int render(final BlockLayer bhc, final Camera cxq) {
        Lighting.turnOff();
        if (bhc == BlockLayer.TRANSLUCENT) {
            this.minecraft.getProfiler().push("translucent_sort");
            final double double4 = cxq.getPosition().x - this.xTransparentOld;
            final double double5 = cxq.getPosition().y - this.yTransparentOld;
            final double double6 = cxq.getPosition().z - this.zTransparentOld;
            if (double4 * double4 + double5 * double5 + double6 * double6 > 1.0) {
                this.xTransparentOld = cxq.getPosition().x;
                this.yTransparentOld = cxq.getPosition().y;
                this.zTransparentOld = cxq.getPosition().z;
                int integer10 = 0;
                for (final RenderChunkInfo a12 : this.renderChunks) {
                    if (a12.chunk.compiled.hasLayer(bhc) && integer10++ < 15) {
                        this.chunkRenderDispatcher.resortChunkTransparencyAsync(a12.chunk);
                    }
                }
            }
            this.minecraft.getProfiler().pop();
        }
        this.minecraft.getProfiler().push("filterempty");
        int integer11 = 0;
        final boolean boolean5 = bhc == BlockLayer.TRANSLUCENT;
        final int integer12 = boolean5 ? (this.renderChunks.size() - 1) : 0;
        for (int integer13 = boolean5 ? -1 : this.renderChunks.size(), integer14 = boolean5 ? -1 : 1, integer15 = integer12; integer15 != integer13; integer15 += integer14) {
            final RenderChunk dpy10 = ((RenderChunkInfo)this.renderChunks.get(integer15)).chunk;
            if (!dpy10.getCompiledChunk().isEmpty(bhc)) {
                ++integer11;
                this.renderList.add(dpy10, bhc);
            }
        }
        this.minecraft.getProfiler().popPush((Supplier<String>)(() -> new StringBuilder().append("render_").append(bhc).toString()));
        this.renderSameAsLast(bhc);
        this.minecraft.getProfiler().pop();
        return integer11;
    }
    
    private void renderSameAsLast(final BlockLayer bhc) {
        this.minecraft.gameRenderer.turnOnLightLayer();
        if (GLX.useVbo()) {
            GlStateManager.enableClientState(32884);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
        }
        this.renderList.render(bhc);
        if (GLX.useVbo()) {
            final List<VertexFormatElement> list3 = DefaultVertexFormat.BLOCK.getElements();
            for (final VertexFormatElement cvd5 : list3) {
                final VertexFormatElement.Usage b6 = cvd5.getUsage();
                final int integer7 = cvd5.getIndex();
                switch (b6) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        continue;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer7);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        continue;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                        continue;
                    }
                }
            }
        }
        this.minecraft.gameRenderer.turnOffLightLayer();
    }
    
    private void updateBlockDestruction(final Iterator<BlockDestructionProgress> iterator) {
        while (iterator.hasNext()) {
            final BlockDestructionProgress uu3 = (BlockDestructionProgress)iterator.next();
            final int integer4 = uu3.getUpdatedRenderTick();
            if (this.ticks - integer4 > 400) {
                iterator.remove();
            }
        }
    }
    
    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 == 0) {
            this.updateBlockDestruction((Iterator<BlockDestructionProgress>)this.destroyingBlocks.values().iterator());
        }
    }
    
    private void renderEndSky() {
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Lighting.turnOff();
        GlStateManager.depthMask(false);
        this.textureManager.bind(LevelRenderer.END_SKY_LOCATION);
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        for (int integer4 = 0; integer4 < 6; ++integer4) {
            GlStateManager.pushMatrix();
            if (integer4 == 1) {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer4 == 2) {
                GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer4 == 3) {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer4 == 4) {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            }
            if (integer4 == 5) {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            }
            cuw3.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw3.vertex(-100.0, -100.0, -100.0).uv(0.0, 0.0).color(40, 40, 40, 255).endVertex();
            cuw3.vertex(-100.0, -100.0, 100.0).uv(0.0, 16.0).color(40, 40, 40, 255).endVertex();
            cuw3.vertex(100.0, -100.0, 100.0).uv(16.0, 16.0).color(40, 40, 40, 255).endVertex();
            cuw3.vertex(100.0, -100.0, -100.0).uv(16.0, 0.0).color(40, 40, 40, 255).endVertex();
            cuz2.end();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
    
    public void renderSky(final float float1) {
        if (this.minecraft.level.dimension.getType() == DimensionType.THE_END) {
            this.renderEndSky();
            return;
        }
        if (!this.minecraft.level.dimension.isNaturalDimension()) {
            return;
        }
        GlStateManager.disableTexture();
        final Vec3 csi3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getBlockPosition(), float1);
        final float float2 = (float)csi3.x;
        final float float3 = (float)csi3.y;
        final float float4 = (float)csi3.z;
        GlStateManager.color3f(float2, float3, float4);
        final Tesselator cuz7 = Tesselator.getInstance();
        final BufferBuilder cuw8 = cuz7.getBuilder();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color3f(float2, float3, float4);
        if (this.usingVbo) {
            this.skyBuffer.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.skyBuffer.draw(7);
            VertexBuffer.unbind();
            GlStateManager.disableClientState(32884);
        }
        else {
            GlStateManager.callList(this.skyList);
        }
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Lighting.turnOff();
        final float[] arr9 = this.level.dimension.getSunriseColor(this.level.getTimeOfDay(float1), float1);
        if (arr9 != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef((Mth.sin(this.level.getSunAngle(float1)) < 0.0f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            final float float5 = arr9[0];
            final float float6 = arr9[1];
            final float float7 = arr9[2];
            cuw8.begin(6, DefaultVertexFormat.POSITION_COLOR);
            cuw8.vertex(0.0, 100.0, 0.0).color(float5, float6, float7, arr9[3]).endVertex();
            final int integer13 = 16;
            for (int integer14 = 0; integer14 <= 16; ++integer14) {
                final float float8 = integer14 * 6.2831855f / 16.0f;
                final float float9 = Mth.sin(float8);
                final float float10 = Mth.cos(float8);
                cuw8.vertex(float9 * 120.0f, float10 * 120.0f, -float10 * 40.0f * arr9[3]).color(arr9[0], arr9[1], arr9[2], 0.0f).endVertex();
            }
            cuz7.end();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final float float5 = 1.0f - this.level.getRainLevel(float1);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float5);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(this.level.getTimeOfDay(float1) * 360.0f, 1.0f, 0.0f, 0.0f);
        float float6 = 30.0f;
        this.textureManager.bind(LevelRenderer.SUN_LOCATION);
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw8.vertex(-float6, 100.0, -float6).uv(0.0, 0.0).endVertex();
        cuw8.vertex(float6, 100.0, -float6).uv(1.0, 0.0).endVertex();
        cuw8.vertex(float6, 100.0, float6).uv(1.0, 1.0).endVertex();
        cuw8.vertex(-float6, 100.0, float6).uv(0.0, 1.0).endVertex();
        cuz7.end();
        float6 = 20.0f;
        this.textureManager.bind(LevelRenderer.MOON_LOCATION);
        final int integer15 = this.level.getMoonPhase();
        final int integer13 = integer15 % 4;
        int integer14 = integer15 / 4 % 2;
        final float float8 = (integer13 + 0) / 4.0f;
        final float float9 = (integer14 + 0) / 2.0f;
        final float float10 = (integer13 + 1) / 4.0f;
        final float float11 = (integer14 + 1) / 2.0f;
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw8.vertex(-float6, -100.0, float6).uv(float10, float11).endVertex();
        cuw8.vertex(float6, -100.0, float6).uv(float8, float11).endVertex();
        cuw8.vertex(float6, -100.0, -float6).uv(float8, float9).endVertex();
        cuw8.vertex(-float6, -100.0, -float6).uv(float10, float9).endVertex();
        cuz7.end();
        GlStateManager.disableTexture();
        final float float12 = this.level.getStarBrightness(float1) * float5;
        if (float12 > 0.0f) {
            GlStateManager.color4f(float12, float12, float12, float12);
            if (this.usingVbo) {
                this.starBuffer.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.starBuffer.draw(7);
                VertexBuffer.unbind();
                GlStateManager.disableClientState(32884);
            }
            else {
                GlStateManager.callList(this.starList);
            }
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture();
        GlStateManager.color3f(0.0f, 0.0f, 0.0f);
        final double double10 = this.minecraft.player.getEyePosition(float1).y - this.level.getHorizonHeight();
        if (double10 < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 12.0f, 0.0f);
            if (this.usingVbo) {
                this.darkBuffer.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.darkBuffer.draw(7);
                VertexBuffer.unbind();
                GlStateManager.disableClientState(32884);
            }
            else {
                GlStateManager.callList(this.darkList);
            }
            GlStateManager.popMatrix();
        }
        if (this.level.dimension.hasGround()) {
            GlStateManager.color3f(float2 * 0.2f + 0.04f, float3 * 0.2f + 0.04f, float4 * 0.6f + 0.1f);
        }
        else {
            GlStateManager.color3f(float2, float3, float4);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -(float)(double10 - 16.0), 0.0f);
        GlStateManager.callList(this.darkList);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }
    
    public void renderClouds(final float float1, final double double2, final double double3, final double double4) {
        if (!this.minecraft.level.dimension.isNaturalDimension()) {
            return;
        }
        final float float2 = 12.0f;
        final float float3 = 4.0f;
        final double double5 = 2.0E-4;
        final double double6 = (this.ticks + float1) * 0.03f;
        double double7 = (double2 + double6) / 12.0;
        final double double8 = this.level.dimension.getCloudHeight() - (float)double3 + 0.33f;
        double double9 = double4 / 12.0 + 0.33000001311302185;
        double7 -= Mth.floor(double7 / 2048.0) * 2048;
        double9 -= Mth.floor(double9 / 2048.0) * 2048;
        final float float4 = (float)(double7 - Mth.floor(double7));
        final float float5 = (float)(double8 / 4.0 - Mth.floor(double8 / 4.0)) * 4.0f;
        final float float6 = (float)(double9 - Mth.floor(double9));
        final Vec3 csi24 = this.level.getCloudColor(float1);
        final int integer25 = (int)Math.floor(double7);
        final int integer26 = (int)Math.floor(double8 / 4.0);
        final int integer27 = (int)Math.floor(double9);
        if (integer25 != this.prevCloudX || integer26 != this.prevCloudY || integer27 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(csi24) > 2.0E-4) {
            this.prevCloudX = integer25;
            this.prevCloudY = integer26;
            this.prevCloudZ = integer27;
            this.prevCloudColor = csi24;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
        }
        if (this.generateClouds) {
            this.generateClouds = false;
            final Tesselator cuz28 = Tesselator.getInstance();
            final BufferBuilder cuw29 = cuz28.getBuilder();
            if (this.cloudBuffer != null) {
                this.cloudBuffer.delete();
            }
            if (this.cloudList >= 0) {
                MemoryTracker.releaseList(this.cloudList);
                this.cloudList = -1;
            }
            if (this.usingVbo) {
                this.cloudBuffer = new VertexBuffer(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
                this.buildClouds(cuw29, double7, double8, double9, csi24);
                cuw29.end();
                cuw29.clear();
                this.cloudBuffer.upload(cuw29.getBuffer());
            }
            else {
                GlStateManager.newList(this.cloudList = MemoryTracker.genLists(1), 4864);
                this.buildClouds(cuw29, double7, double8, double9, csi24);
                cuz28.end();
                GlStateManager.endList();
            }
        }
        GlStateManager.disableCull();
        this.textureManager.bind(LevelRenderer.CLOUDS_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(12.0f, 1.0f, 12.0f);
        GlStateManager.translatef(-float4, float5, -float6);
        if (this.usingVbo && this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
            GlStateManager.enableClientState(32885);
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            int integer29;
            for (int integer28 = integer29 = ((this.prevCloudsType != CloudStatus.FANCY) ? 1 : 0); integer29 < 2; ++integer29) {
                if (integer29 == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                }
                else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                this.cloudBuffer.draw(7);
            }
            VertexBuffer.unbind();
            GlStateManager.disableClientState(32884);
            GlStateManager.disableClientState(32888);
            GlStateManager.disableClientState(32886);
            GlStateManager.disableClientState(32885);
        }
        else if (this.cloudList >= 0) {
            int integer29;
            for (int integer28 = integer29 = ((this.prevCloudsType != CloudStatus.FANCY) ? 1 : 0); integer29 < 2; ++integer29) {
                if (integer29 == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                }
                else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                GlStateManager.callList(this.cloudList);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.clearCurrentColor();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
    
    private void buildClouds(final BufferBuilder cuw, final double double2, final double double3, final double double4, final Vec3 csi) {
        final float float10 = 4.0f;
        final float float11 = 0.00390625f;
        final int integer12 = 8;
        final int integer13 = 4;
        final float float12 = 9.765625E-4f;
        final float float13 = Mth.floor(double2) * 0.00390625f;
        final float float14 = Mth.floor(double4) * 0.00390625f;
        final float float15 = (float)csi.x;
        final float float16 = (float)csi.y;
        final float float17 = (float)csi.z;
        final float float18 = float15 * 0.9f;
        final float float19 = float16 * 0.9f;
        final float float20 = float17 * 0.9f;
        final float float21 = float15 * 0.7f;
        final float float22 = float16 * 0.7f;
        final float float23 = float17 * 0.7f;
        final float float24 = float15 * 0.8f;
        final float float25 = float16 * 0.8f;
        final float float26 = float17 * 0.8f;
        cuw.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        final float float27 = (float)Math.floor(double3 / 4.0) * 4.0f;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            for (int integer14 = -3; integer14 <= 4; ++integer14) {
                for (int integer15 = -3; integer15 <= 4; ++integer15) {
                    final float float28 = (float)(integer14 * 8);
                    final float float29 = (float)(integer15 * 8);
                    if (float27 > -5.0f) {
                        cuw.vertex(float28 + 0.0f, float27 + 0.0f, float29 + 8.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float21, float22, float23, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 8.0f, float27 + 0.0f, float29 + 8.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float21, float22, float23, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 8.0f, float27 + 0.0f, float29 + 0.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float21, float22, float23, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 0.0f, float27 + 0.0f, float29 + 0.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float21, float22, float23, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    }
                    if (float27 <= 5.0f) {
                        cuw.vertex(float28 + 0.0f, float27 + 4.0f - 9.765625E-4f, float29 + 8.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 8.0f, float27 + 4.0f - 9.765625E-4f, float29 + 8.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 8.0f, float27 + 4.0f - 9.765625E-4f, float29 + 0.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        cuw.vertex(float28 + 0.0f, float27 + 4.0f - 9.765625E-4f, float29 + 0.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                    }
                    if (integer14 > -1) {
                        for (int integer16 = 0; integer16 < 8; ++integer16) {
                            cuw.vertex(float28 + integer16 + 0.0f, float27 + 0.0f, float29 + 8.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 0.0f, float27 + 4.0f, float29 + 8.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 0.0f, float27 + 4.0f, float29 + 0.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 0.0f, float27 + 0.0f, float29 + 0.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (integer14 <= 1) {
                        for (int integer16 = 0; integer16 < 8; ++integer16) {
                            cuw.vertex(float28 + integer16 + 1.0f - 9.765625E-4f, float27 + 0.0f, float29 + 8.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 1.0f - 9.765625E-4f, float27 + 4.0f, float29 + 8.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 8.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 1.0f - 9.765625E-4f, float27 + 4.0f, float29 + 0.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            cuw.vertex(float28 + integer16 + 1.0f - 9.765625E-4f, float27 + 0.0f, float29 + 0.0f).uv((float28 + integer16 + 0.5f) * 0.00390625f + float13, (float29 + 0.0f) * 0.00390625f + float14).color(float18, float19, float20, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (integer15 > -1) {
                        for (int integer16 = 0; integer16 < 8; ++integer16) {
                            cuw.vertex(float28 + 0.0f, float27 + 4.0f, float29 + integer16 + 0.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            cuw.vertex(float28 + 8.0f, float27 + 4.0f, float29 + integer16 + 0.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            cuw.vertex(float28 + 8.0f, float27 + 0.0f, float29 + integer16 + 0.0f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            cuw.vertex(float28 + 0.0f, float27 + 0.0f, float29 + integer16 + 0.0f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                        }
                    }
                    if (integer15 <= 1) {
                        for (int integer16 = 0; integer16 < 8; ++integer16) {
                            cuw.vertex(float28 + 0.0f, float27 + 4.0f, float29 + integer16 + 1.0f - 9.765625E-4f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            cuw.vertex(float28 + 8.0f, float27 + 4.0f, float29 + integer16 + 1.0f - 9.765625E-4f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            cuw.vertex(float28 + 8.0f, float27 + 0.0f, float29 + integer16 + 1.0f - 9.765625E-4f).uv((float28 + 8.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            cuw.vertex(float28 + 0.0f, float27 + 0.0f, float29 + integer16 + 1.0f - 9.765625E-4f).uv((float28 + 0.0f) * 0.00390625f + float13, (float29 + integer16 + 0.5f) * 0.00390625f + float14).color(float24, float25, float26, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        }
                    }
                }
            }
        }
        else {
            final int integer14 = 1;
            final int integer15 = 32;
            for (int integer17 = -32; integer17 < 32; integer17 += 32) {
                for (int integer18 = -32; integer18 < 32; integer18 += 32) {
                    cuw.vertex(integer17 + 0, float27, integer18 + 32).uv((integer17 + 0) * 0.00390625f + float13, (integer18 + 32) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    cuw.vertex(integer17 + 32, float27, integer18 + 32).uv((integer17 + 32) * 0.00390625f + float13, (integer18 + 32) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    cuw.vertex(integer17 + 32, float27, integer18 + 0).uv((integer17 + 32) * 0.00390625f + float13, (integer18 + 0) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    cuw.vertex(integer17 + 0, float27, integer18 + 0).uv((integer17 + 0) * 0.00390625f + float13, (integer18 + 0) * 0.00390625f + float14).color(float15, float16, float17, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                }
            }
        }
    }
    
    public void compileChunksUntil(final long long1) {
        this.needsUpdate |= this.chunkRenderDispatcher.uploadAllPendingUploadsUntil(long1);
        if (!this.chunksToCompile.isEmpty()) {
            final Iterator<RenderChunk> iterator4 = (Iterator<RenderChunk>)this.chunksToCompile.iterator();
            while (iterator4.hasNext()) {
                final RenderChunk dpy5 = (RenderChunk)iterator4.next();
                boolean boolean6;
                if (dpy5.isDirtyFromPlayer()) {
                    boolean6 = this.chunkRenderDispatcher.rebuildChunkSync(dpy5);
                }
                else {
                    boolean6 = this.chunkRenderDispatcher.rebuildChunkAsync(dpy5);
                }
                if (!boolean6) {
                    break;
                }
                dpy5.setNotDirty();
                iterator4.remove();
                final long long2 = long1 - Util.getNanos();
                if (long2 < 0L) {
                    break;
                }
            }
        }
    }
    
    public void renderWorldBounds(final Camera cxq, final float float2) {
        final Tesselator cuz4 = Tesselator.getInstance();
        final BufferBuilder cuw5 = cuz4.getBuilder();
        final WorldBorder bxf6 = this.level.getWorldBorder();
        final double double7 = this.minecraft.options.renderDistance * 16;
        if (cxq.getPosition().x < bxf6.getMaxX() - double7 && cxq.getPosition().x > bxf6.getMinX() + double7 && cxq.getPosition().z < bxf6.getMaxZ() - double7 && cxq.getPosition().z > bxf6.getMinZ() + double7) {
            return;
        }
        double double8 = 1.0 - bxf6.getDistanceToBorder(cxq.getPosition().x, cxq.getPosition().z) / double7;
        double8 = Math.pow(double8, 4.0);
        final double double9 = cxq.getPosition().x;
        final double double10 = cxq.getPosition().y;
        final double double11 = cxq.getPosition().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.textureManager.bind(LevelRenderer.FORCEFIELD_LOCATION);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        final int integer17 = bxf6.getStatus().getColor();
        final float float3 = (integer17 >> 16 & 0xFF) / 255.0f;
        final float float4 = (integer17 >> 8 & 0xFF) / 255.0f;
        final float float5 = (integer17 & 0xFF) / 255.0f;
        GlStateManager.color4f(float3, float4, float5, (float)double8);
        GlStateManager.polygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableCull();
        final float float6 = Util.getMillis() % 3000L / 3000.0f;
        final float float7 = 0.0f;
        final float float8 = 0.0f;
        final float float9 = 128.0f;
        cuw5.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw5.offset(-double9, -double10, -double11);
        double double12 = Math.max((double)Mth.floor(double11 - double7), bxf6.getMinZ());
        double double13 = Math.min((double)Mth.ceil(double11 + double7), bxf6.getMaxZ());
        if (double9 > bxf6.getMaxX() - double7) {
            float float10 = 0.0f;
            for (double double14 = double12; double14 < double13; ++double14, float10 += 0.5f) {
                final double double15 = Math.min(1.0, double13 - double14);
                final float float11 = (float)double15 * 0.5f;
                cuw5.vertex(bxf6.getMaxX(), 256.0, double14).uv(float6 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(bxf6.getMaxX(), 256.0, double14 + double15).uv(float6 + float11 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(bxf6.getMaxX(), 0.0, double14 + double15).uv(float6 + float11 + float10, float6 + 128.0f).endVertex();
                cuw5.vertex(bxf6.getMaxX(), 0.0, double14).uv(float6 + float10, float6 + 128.0f).endVertex();
            }
        }
        if (double9 < bxf6.getMinX() + double7) {
            float float10 = 0.0f;
            for (double double14 = double12; double14 < double13; ++double14, float10 += 0.5f) {
                final double double15 = Math.min(1.0, double13 - double14);
                final float float11 = (float)double15 * 0.5f;
                cuw5.vertex(bxf6.getMinX(), 256.0, double14).uv(float6 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(bxf6.getMinX(), 256.0, double14 + double15).uv(float6 + float11 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(bxf6.getMinX(), 0.0, double14 + double15).uv(float6 + float11 + float10, float6 + 128.0f).endVertex();
                cuw5.vertex(bxf6.getMinX(), 0.0, double14).uv(float6 + float10, float6 + 128.0f).endVertex();
            }
        }
        double12 = Math.max((double)Mth.floor(double9 - double7), bxf6.getMinX());
        double13 = Math.min((double)Mth.ceil(double9 + double7), bxf6.getMaxX());
        if (double11 > bxf6.getMaxZ() - double7) {
            float float10 = 0.0f;
            for (double double14 = double12; double14 < double13; ++double14, float10 += 0.5f) {
                final double double15 = Math.min(1.0, double13 - double14);
                final float float11 = (float)double15 * 0.5f;
                cuw5.vertex(double14, 256.0, bxf6.getMaxZ()).uv(float6 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(double14 + double15, 256.0, bxf6.getMaxZ()).uv(float6 + float11 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(double14 + double15, 0.0, bxf6.getMaxZ()).uv(float6 + float11 + float10, float6 + 128.0f).endVertex();
                cuw5.vertex(double14, 0.0, bxf6.getMaxZ()).uv(float6 + float10, float6 + 128.0f).endVertex();
            }
        }
        if (double11 < bxf6.getMinZ() + double7) {
            float float10 = 0.0f;
            for (double double14 = double12; double14 < double13; ++double14, float10 += 0.5f) {
                final double double15 = Math.min(1.0, double13 - double14);
                final float float11 = (float)double15 * 0.5f;
                cuw5.vertex(double14, 256.0, bxf6.getMinZ()).uv(float6 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(double14 + double15, 256.0, bxf6.getMinZ()).uv(float6 + float11 + float10, float6 + 0.0f).endVertex();
                cuw5.vertex(double14 + double15, 0.0, bxf6.getMinZ()).uv(float6 + float11 + float10, float6 + 128.0f).endVertex();
                cuw5.vertex(double14, 0.0, bxf6.getMinZ()).uv(float6 + float10, float6 + 128.0f).endVertex();
            }
        }
        cuz4.end();
        cuw5.offset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }
    
    private void setupDestroyState() {
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.polygonOffset(-1.0f, -10.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
    }
    
    private void restoreDestroyState() {
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
    
    public void renderDestroyAnimation(final Tesselator cuz, final BufferBuilder cuw, final Camera cxq) {
        final double double5 = cxq.getPosition().x;
        final double double6 = cxq.getPosition().y;
        final double double7 = cxq.getPosition().z;
        if (!this.destroyingBlocks.isEmpty()) {
            this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
            this.setupDestroyState();
            cuw.begin(7, DefaultVertexFormat.BLOCK);
            cuw.offset(-double5, -double6, -double7);
            cuw.noColor();
            final Iterator<BlockDestructionProgress> iterator11 = (Iterator<BlockDestructionProgress>)this.destroyingBlocks.values().iterator();
            while (iterator11.hasNext()) {
                final BlockDestructionProgress uu12 = (BlockDestructionProgress)iterator11.next();
                final BlockPos ew13 = uu12.getPos();
                final Block bmv14 = this.level.getBlockState(ew13).getBlock();
                if (!(bmv14 instanceof ChestBlock) && !(bmv14 instanceof EnderChestBlock) && !(bmv14 instanceof SignBlock)) {
                    if (bmv14 instanceof AbstractSkullBlock) {
                        continue;
                    }
                    final double double8 = ew13.getX() - double5;
                    final double double9 = ew13.getY() - double6;
                    final double double10 = ew13.getZ() - double7;
                    if (double8 * double8 + double9 * double9 + double10 * double10 > 1024.0) {
                        iterator11.remove();
                    }
                    else {
                        final BlockState bvt21 = this.level.getBlockState(ew13);
                        if (bvt21.isAir()) {
                            continue;
                        }
                        final int integer22 = uu12.getProgress();
                        final TextureAtlasSprite dxb23 = this.breakingTextures[integer22];
                        final BlockRenderDispatcher dnw24 = this.minecraft.getBlockRenderer();
                        dnw24.renderBreakingTexture(bvt21, ew13, dxb23, this.level);
                    }
                }
            }
            cuz.end();
            cuw.offset(0.0, 0.0, 0.0);
            this.restoreDestroyState();
        }
    }
    
    public void renderHitOutline(final Camera cxq, final HitResult csf, final int integer) {
        if (integer == 0 && csf.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew5 = ((BlockHitResult)csf).getBlockPos();
            final BlockState bvt6 = this.level.getBlockState(ew5);
            if (!bvt6.isAir() && this.level.getWorldBorder().isWithinBounds(ew5)) {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.lineWidth(Math.max(2.5f, this.minecraft.window.getWidth() / 1920.0f * 2.5f));
                GlStateManager.disableTexture();
                GlStateManager.depthMask(false);
                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(1.0f, 1.0f, 0.999f);
                final double double7 = cxq.getPosition().x;
                final double double8 = cxq.getPosition().y;
                final double double9 = cxq.getPosition().z;
                renderShape(bvt6.getShape(this.level, ew5, CollisionContext.of(cxq.getEntity())), ew5.getX() - double7, ew5.getY() - double8, ew5.getZ() - double9, 0.0f, 0.0f, 0.0f, 0.4f);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture();
                GlStateManager.disableBlend();
            }
        }
    }
    
    public static void renderVoxelShape(final VoxelShape ctc, final double double2, final double double3, final double double4, final float float5, final float float6, final float float7, final float float8) {
        final List<AABB> list12 = ctc.toAabbs();
        final int integer13 = Mth.ceil(list12.size() / 3.0);
        for (int integer14 = 0; integer14 < list12.size(); ++integer14) {
            final AABB csc15 = (AABB)list12.get(integer14);
            final float float9 = (integer14 % (float)integer13 + 1.0f) / integer13;
            final float float10 = (float)(integer14 / integer13);
            final float float11 = float9 * ((float10 == 0.0f) ? 1 : 0);
            final float float12 = float9 * ((float10 == 1.0f) ? 1 : 0);
            final float float13 = float9 * ((float10 == 2.0f) ? 1 : 0);
            renderShape(Shapes.create(csc15.move(0.0, 0.0, 0.0)), double2, double3, double4, float11, float12, float13, 1.0f);
        }
    }
    
    public static void renderShape(final VoxelShape ctc, final double double2, final double double3, final double double4, final float float5, final float float6, final float float7, final float float8) {
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        cuw13.begin(1, DefaultVertexFormat.POSITION_COLOR);
        final BufferBuilder bufferBuilder;
        ctc.forAllEdges((double9, double10, double11, double12, double13, double14) -> {
            bufferBuilder.vertex(double9 + double2, double10 + double3, double11 + double4).color(float5, float6, float7, float8).endVertex();
            bufferBuilder.vertex(double12 + double2, double13 + double3, double14 + double4).color(float5, float6, float7, float8).endVertex();
            return;
        });
        cuz12.end();
    }
    
    public static void renderLineBox(final AABB csc, final float float2, final float float3, final float float4, final float float5) {
        renderLineBox(csc.minX, csc.minY, csc.minZ, csc.maxX, csc.maxY, csc.maxZ, float2, float3, float4, float5);
    }
    
    public static void renderLineBox(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6, final float float7, final float float8, final float float9, final float float10) {
        final Tesselator cuz17 = Tesselator.getInstance();
        final BufferBuilder cuw18 = cuz17.getBuilder();
        cuw18.begin(3, DefaultVertexFormat.POSITION_COLOR);
        addChainedLineBoxVertices(cuw18, double1, double2, double3, double4, double5, double6, float7, float8, float9, float10);
        cuz17.end();
    }
    
    public static void addChainedLineBoxVertices(final BufferBuilder cuw, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final float float8, final float float9, final float float10, final float float11) {
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, 0.0f).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double7).color(float8, float9, float10, 0.0f).endVertex();
        cuw.vertex(double2, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, 0.0f).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double4).color(float8, float9, float10, 0.0f).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, 0.0f).endVertex();
    }
    
    public static void addChainedFilledBoxVertices(final BufferBuilder cuw, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final float float8, final float float9, final float float10, final float float11) {
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double3, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double2, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double4).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
        cuw.vertex(double5, double6, double7).color(float8, float9, float10, float11).endVertex();
    }
    
    public void blockChanged(final BlockGetter bhb, final BlockPos ew, final BlockState bvt3, final BlockState bvt4, final int integer) {
        this.setBlockDirty(ew, (integer & 0x8) != 0x0);
    }
    
    private void setBlockDirty(final BlockPos ew, final boolean boolean2) {
        for (int integer4 = ew.getZ() - 1; integer4 <= ew.getZ() + 1; ++integer4) {
            for (int integer5 = ew.getX() - 1; integer5 <= ew.getX() + 1; ++integer5) {
                for (int integer6 = ew.getY() - 1; integer6 <= ew.getY() + 1; ++integer6) {
                    this.setSectionDirty(integer5 >> 4, integer6 >> 4, integer4 >> 4, boolean2);
                }
            }
        }
    }
    
    public void setBlocksDirty(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        for (int integer7 = integer3 - 1; integer7 <= integer6 + 1; ++integer7) {
            for (int integer8 = integer1 - 1; integer8 <= integer4 + 1; ++integer8) {
                for (int integer9 = integer2 - 1; integer9 <= integer5 + 1; ++integer9) {
                    this.setSectionDirty(integer8 >> 4, integer9 >> 4, integer7 >> 4);
                }
            }
        }
    }
    
    public void setBlockDirty(final BlockPos ew, final BlockState bvt2, final BlockState bvt3) {
        if (this.minecraft.getModelManager().requiresRender(bvt2, bvt3)) {
            this.setBlocksDirty(ew.getX(), ew.getY(), ew.getZ(), ew.getX(), ew.getY(), ew.getZ());
        }
    }
    
    public void setSectionDirtyWithNeighbors(final int integer1, final int integer2, final int integer3) {
        for (int integer4 = integer3 - 1; integer4 <= integer3 + 1; ++integer4) {
            for (int integer5 = integer1 - 1; integer5 <= integer1 + 1; ++integer5) {
                for (int integer6 = integer2 - 1; integer6 <= integer2 + 1; ++integer6) {
                    this.setSectionDirty(integer5, integer6, integer4);
                }
            }
        }
    }
    
    public void setSectionDirty(final int integer1, final int integer2, final int integer3) {
        this.setSectionDirty(integer1, integer2, integer3, false);
    }
    
    private void setSectionDirty(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        this.viewArea.setDirty(integer1, integer2, integer3, boolean4);
    }
    
    public void playStreamingMusic(@Nullable final SoundEvent yo, final BlockPos ew) {
        SoundInstance dzp4 = (SoundInstance)this.playingRecords.get(ew);
        if (dzp4 != null) {
            this.minecraft.getSoundManager().stop(dzp4);
            this.playingRecords.remove(ew);
        }
        if (yo != null) {
            final RecordItem bcx5 = RecordItem.getBySound(yo);
            if (bcx5 != null) {
                this.minecraft.gui.setNowPlaying(bcx5.getDisplayName().getColoredString());
            }
            dzp4 = SimpleSoundInstance.forRecord(yo, (float)ew.getX(), (float)ew.getY(), (float)ew.getZ());
            this.playingRecords.put(ew, dzp4);
            this.minecraft.getSoundManager().play(dzp4);
        }
        this.notifyNearbyEntities(this.level, ew, yo != null);
    }
    
    private void notifyNearbyEntities(final Level bhr, final BlockPos ew, final boolean boolean3) {
        final List<LivingEntity> list5 = bhr.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, new AABB(ew).inflate(3.0));
        for (final LivingEntity aix7 : list5) {
            aix7.setRecordPlayingNearby(ew, boolean3);
        }
    }
    
    public void addParticle(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
        this.addParticle(gf, boolean2, false, double3, double4, double5, double6, double7, double8);
    }
    
    public void addParticle(final ParticleOptions gf, final boolean boolean2, final boolean boolean3, final double double4, final double double5, final double double6, final double double7, final double double8, final double double9) {
        try {
            this.addParticleInternal(gf, boolean2, boolean3, double4, double5, double6, double7, double8, double9);
        }
        catch (Throwable throwable17) {
            final CrashReport d18 = CrashReport.forThrowable(throwable17, "Exception while adding particle");
            final CrashReportCategory e19 = d18.addCategory("Particle being added");
            e19.setDetail("ID", Registry.PARTICLE_TYPE.getKey(gf.getType()));
            e19.setDetail("Parameters", gf.writeToString());
            e19.setDetail("Position", (CrashReportDetail<String>)(() -> CrashReportCategory.formatLocation(double4, double5, double6)));
            throw new ReportedException(d18);
        }
    }
    
    private <T extends ParticleOptions> void addParticle(final T gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this.addParticle(gf, gf.getType().getOverrideLimiter(), double2, double3, double4, double5, double6, double7);
    }
    
    @Nullable
    private Particle addParticleInternal(final ParticleOptions gf, final boolean boolean2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
        return this.addParticleInternal(gf, boolean2, false, double3, double4, double5, double6, double7, double8);
    }
    
    @Nullable
    private Particle addParticleInternal(final ParticleOptions gf, final boolean boolean2, final boolean boolean3, final double double4, final double double5, final double double6, final double double7, final double double8, final double double9) {
        final Camera cxq17 = this.minecraft.gameRenderer.getMainCamera();
        if (this.minecraft == null || !cxq17.isInitialized() || this.minecraft.particleEngine == null) {
            return null;
        }
        final ParticleStatus cyh18 = this.calculateParticleLevel(boolean3);
        if (boolean2) {
            return this.minecraft.particleEngine.createParticle(gf, double4, double5, double6, double7, double8, double9);
        }
        if (cxq17.getPosition().distanceToSqr(double4, double5, double6) > 1024.0) {
            return null;
        }
        if (cyh18 == ParticleStatus.MINIMAL) {
            return null;
        }
        return this.minecraft.particleEngine.createParticle(gf, double4, double5, double6, double7, double8, double9);
    }
    
    private ParticleStatus calculateParticleLevel(final boolean boolean1) {
        ParticleStatus cyh3 = this.minecraft.options.particles;
        if (boolean1 && cyh3 == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
            cyh3 = ParticleStatus.DECREASED;
        }
        if (cyh3 == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
            cyh3 = ParticleStatus.MINIMAL;
        }
        return cyh3;
    }
    
    public void clear() {
    }
    
    public void globalLevelEvent(final int integer1, final BlockPos ew, final int integer3) {
        switch (integer1) {
            case 1023:
            case 1028:
            case 1038: {
                final Camera cxq5 = this.minecraft.gameRenderer.getMainCamera();
                if (!cxq5.isInitialized()) {
                    break;
                }
                final double double6 = ew.getX() - cxq5.getPosition().x;
                final double double7 = ew.getY() - cxq5.getPosition().y;
                final double double8 = ew.getZ() - cxq5.getPosition().z;
                final double double9 = Math.sqrt(double6 * double6 + double7 * double7 + double8 * double8);
                double double10 = cxq5.getPosition().x;
                double double11 = cxq5.getPosition().y;
                double double12 = cxq5.getPosition().z;
                if (double9 > 0.0) {
                    double10 += double6 / double9 * 2.0;
                    double11 += double7 / double9 * 2.0;
                    double12 += double8 / double9 * 2.0;
                }
                if (integer1 == 1023) {
                    this.level.playLocalSound(double10, double11, double12, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (integer1 == 1038) {
                    this.level.playLocalSound(double10, double11, double12, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.level.playLocalSound(double10, double11, double12, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0f, 1.0f, false);
                break;
            }
        }
    }
    
    public void levelEvent(final Player awg, final int integer2, final BlockPos ew, final int integer4) {
        final Random random6 = this.level.random;
        switch (integer2) {
            case 1035: {
                this.level.playLocalSound(ew, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.level.playLocalSound(ew, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.level.playLocalSound(ew, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PORTAL_TRAVEL, random6.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1001: {
                this.level.playLocalSound(ew, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.level.playLocalSound(ew, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.level.playLocalSound(ew, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.level.playLocalSound(ew, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.level.playLocalSound(ew, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                final Direction fb7 = Direction.from3DDataValue(integer4);
                final int integer5 = fb7.getStepX();
                final int integer6 = fb7.getStepY();
                final int integer7 = fb7.getStepZ();
                final double double11 = ew.getX() + integer5 * 0.6 + 0.5;
                final double double12 = ew.getY() + integer6 * 0.6 + 0.5;
                final double double13 = ew.getZ() + integer7 * 0.6 + 0.5;
                for (int integer8 = 0; integer8 < 10; ++integer8) {
                    final double double14 = random6.nextDouble() * 0.2 + 0.01;
                    final double double15 = double11 + integer5 * 0.01 + (random6.nextDouble() - 0.5) * integer7 * 0.5;
                    final double double16 = double12 + integer6 * 0.01 + (random6.nextDouble() - 0.5) * integer6 * 0.5;
                    final double double17 = double13 + integer7 * 0.01 + (random6.nextDouble() - 0.5) * integer5 * 0.5;
                    final double double18 = integer5 * double14 + random6.nextGaussian() * 0.01;
                    final double double19 = integer6 * double14 + random6.nextGaussian() * 0.01;
                    final double double20 = integer7 * double14 + random6.nextGaussian() * 0.01;
                    this.<SimpleParticleType>addParticle(ParticleTypes.SMOKE, double15, double16, double17, double18, double19, double20);
                }
                break;
            }
            case 2003: {
                final double double21 = ew.getX() + 0.5;
                final double double22 = ew.getY();
                final double double11 = ew.getZ() + 0.5;
                for (int integer9 = 0; integer9 < 8; ++integer9) {
                    this.<ItemParticleOption>addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), double21, double22, double11, random6.nextGaussian() * 0.15, random6.nextDouble() * 0.2, random6.nextGaussian() * 0.15);
                }
                for (double double12 = 0.0; double12 < 6.283185307179586; double12 += 0.15707963267948966) {
                    this.<SimpleParticleType>addParticle(ParticleTypes.PORTAL, double21 + Math.cos(double12) * 5.0, double22 - 0.4, double11 + Math.sin(double12) * 5.0, Math.cos(double12) * -5.0, 0.0, Math.sin(double12) * -5.0);
                    this.<SimpleParticleType>addParticle(ParticleTypes.PORTAL, double21 + Math.cos(double12) * 5.0, double22 - 0.4, double11 + Math.sin(double12) * 5.0, Math.cos(double12) * -7.0, 0.0, Math.sin(double12) * -7.0);
                }
                break;
            }
            case 2002:
            case 2007: {
                final double double21 = ew.getX();
                final double double22 = ew.getY();
                final double double11 = ew.getZ();
                for (int integer9 = 0; integer9 < 8; ++integer9) {
                    this.<ItemParticleOption>addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), double21, double22, double11, random6.nextGaussian() * 0.15, random6.nextDouble() * 0.2, random6.nextGaussian() * 0.15);
                }
                final float float13 = (integer4 >> 16 & 0xFF) / 255.0f;
                final float float14 = (integer4 >> 8 & 0xFF) / 255.0f;
                final float float15 = (integer4 >> 0 & 0xFF) / 255.0f;
                final ParticleOptions gf16 = (integer2 == 2007) ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int integer8 = 0; integer8 < 100; ++integer8) {
                    final double double14 = random6.nextDouble() * 4.0;
                    final double double15 = random6.nextDouble() * 3.141592653589793 * 2.0;
                    final double double16 = Math.cos(double15) * double14;
                    final double double17 = 0.01 + random6.nextDouble() * 0.5;
                    final double double18 = Math.sin(double15) * double14;
                    final Particle dln28 = this.addParticleInternal(gf16, gf16.getType().getOverrideLimiter(), double21 + double16 * 0.1, double22 + 0.3, double11 + double18 * 0.1, double16, double17, double18);
                    if (dln28 != null) {
                        final float float16 = 0.75f + random6.nextFloat() * 0.25f;
                        dln28.setColor(float13 * float16, float14 * float16, float15 * float16);
                        dln28.setPower((float)double14);
                    }
                }
                this.level.playLocalSound(ew, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                final BlockState bvt7 = Block.stateById(integer4);
                if (!bvt7.isAir()) {
                    final SoundType bry8 = bvt7.getSoundType();
                    this.level.playLocalSound(ew, bry8.getBreakSound(), SoundSource.BLOCKS, (bry8.getVolume() + 1.0f) / 2.0f, bry8.getPitch() * 0.8f, false);
                }
                this.minecraft.particleEngine.destroy(ew, bvt7);
                break;
            }
            case 2004: {
                for (int integer5 = 0; integer5 < 20; ++integer5) {
                    final double double22 = ew.getX() + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                    final double double11 = ew.getY() + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                    final double double12 = ew.getZ() + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                    this.level.addParticle(ParticleTypes.SMOKE, double22, double11, double12, 0.0, 0.0, 0.0);
                    this.level.addParticle(ParticleTypes.FLAME, double22, double11, double12, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.addGrowthParticles(this.level, ew, integer4);
                break;
            }
            case 2008: {
                this.level.addParticle(ParticleTypes.EXPLOSION, ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.handleFill(this.level, ew, integer4 > 0);
                break;
            }
            case 1501: {
                this.level.playLocalSound(ew, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.8f, false);
                for (int integer5 = 0; integer5 < 8; ++integer5) {
                    this.level.addParticle(ParticleTypes.LARGE_SMOKE, ew.getX() + Math.random(), ew.getY() + 1.2, ew.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.level.playLocalSound(ew, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 2.6f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.8f, false);
                for (int integer5 = 0; integer5 < 5; ++integer5) {
                    final double double22 = ew.getX() + random6.nextDouble() * 0.6 + 0.2;
                    final double double11 = ew.getY() + random6.nextDouble() * 0.6 + 0.2;
                    final double double12 = ew.getZ() + random6.nextDouble() * 0.6 + 0.2;
                    this.level.addParticle(ParticleTypes.SMOKE, double22, double11, double12, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.level.playLocalSound(ew, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                for (int integer5 = 0; integer5 < 16; ++integer5) {
                    final double double22 = ew.getX() + (5.0f + random6.nextFloat() * 6.0f) / 16.0f;
                    final double double11 = ew.getY() + 0.8125f;
                    final double double12 = ew.getZ() + (5.0f + random6.nextFloat() * 6.0f) / 16.0f;
                    final double double13 = 0.0;
                    final double double23 = 0.0;
                    final double double24 = 0.0;
                    this.level.addParticle(ParticleTypes.SMOKE, double22, double11, double12, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int integer5 = 0; integer5 < 200; ++integer5) {
                    final float float17 = random6.nextFloat() * 4.0f;
                    final float float18 = random6.nextFloat() * 6.2831855f;
                    final double double11 = Mth.cos(float18) * float17;
                    final double double12 = 0.01 + random6.nextDouble() * 0.5;
                    final double double13 = Mth.sin(float18) * float17;
                    final Particle dln29 = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, ew.getX() + double11 * 0.1, ew.getY() + 0.3, ew.getZ() + double13 * 0.1, double11, double12, double13);
                    if (dln29 != null) {
                        dln29.setPower(float17);
                    }
                }
                this.level.playLocalSound(ew, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.level.playLocalSound(ew, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.level.playLocalSound(ew, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.level.playLocalSound(ew, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.level.playLocalSound(ew, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.level.playLocalSound(ew, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.level.playLocalSound(ew, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.level.playLocalSound(ew, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.level.playLocalSound(ew, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.level.playLocalSound(ew, SoundEvents.FENCE_GATE_OPEN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.level.playLocalSound(ew, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.level.playLocalSound(ew, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random6.nextFloat() - random6.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.level.playLocalSound(ew, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.level.playLocalSound(ew, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.level.playLocalSound(ew, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.level.playLocalSound(ew, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byId(integer4) instanceof RecordItem) {
                    this.playStreamingMusic(((RecordItem)Item.byId(integer4)).getSound(), ew);
                    break;
                }
                this.playStreamingMusic(null, ew);
                break;
            }
            case 1015: {
                this.level.playLocalSound(ew, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.level.playLocalSound(ew, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.level.playLocalSound(ew, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.level.playLocalSound(ew, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.level.playLocalSound(ew, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.level.playLocalSound(ew, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.level.playLocalSound(ew, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.NEUTRAL, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.level.playLocalSound(ew, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.NEUTRAL, 2.0f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.level.playLocalSound(ew, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05f, (random6.nextFloat() - random6.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.level.playLocalSound(ew, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.level.playLocalSound(ew, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.level.playLocalSound(ew, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.level.playLocalSound(ew, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0f, 0.8f + this.level.random.nextFloat() * 0.3f, false);
                break;
            }
        }
    }
    
    public void destroyBlockProgress(final int integer1, final BlockPos ew, final int integer3) {
        if (integer3 < 0 || integer3 >= 10) {
            this.destroyingBlocks.remove(integer1);
        }
        else {
            BlockDestructionProgress uu5 = (BlockDestructionProgress)this.destroyingBlocks.get(integer1);
            if (uu5 == null || uu5.getPos().getX() != ew.getX() || uu5.getPos().getY() != ew.getY() || uu5.getPos().getZ() != ew.getZ()) {
                uu5 = new BlockDestructionProgress(integer1, ew);
                this.destroyingBlocks.put(integer1, uu5);
            }
            uu5.setProgress(integer3);
            uu5.updateTick(this.ticks);
        }
    }
    
    public boolean hasRenderedAllChunks() {
        return this.chunksToCompile.isEmpty() && this.chunkRenderDispatcher.isQueueEmpty();
    }
    
    public void needsUpdate() {
        this.needsUpdate = true;
        this.generateClouds = true;
    }
    
    public void updateGlobalBlockEntities(final Collection<BlockEntity> collection1, final Collection<BlockEntity> collection2) {
        synchronized (this.globalBlockEntities) {
            this.globalBlockEntities.removeAll((Collection)collection1);
            this.globalBlockEntities.addAll((Collection)collection2);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
        SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
        CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
        END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
        FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
        DIRECTIONS = Direction.values();
    }
    
    class RenderChunkInfo {
        private final RenderChunk chunk;
        private final Direction sourceDirection;
        private byte directions;
        private final int step;
        
        private RenderChunkInfo(final RenderChunk dpy, @Nullable final Direction fb, final int integer) {
            this.chunk = dpy;
            this.sourceDirection = fb;
            this.step = integer;
        }
        
        public void setDirections(final byte byte1, final Direction fb) {
            this.directions |= (byte)(byte1 | 1 << fb.ordinal());
        }
        
        public boolean hasDirection(final Direction fb) {
            return (this.directions & 1 << fb.ordinal()) > 0;
        }
    }
}

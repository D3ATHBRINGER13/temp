package net.minecraft.client.renderer;

import org.apache.logging.log4j.LogManager;
import java.util.Locale;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.client.particle.ParticleEngine;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.SimpleResource;
import net.minecraft.client.Screenshot;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.level.GameType;
import net.minecraft.world.effect.MobEffects;
import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.function.Predicate;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Creeper;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.MapRenderer;
import java.util.Random;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class GameRenderer implements AutoCloseable, ResourceManagerReloadListener {
    private static final Logger LOGGER;
    private static final ResourceLocation RAIN_LOCATION;
    private static final ResourceLocation SNOW_LOCATION;
    private final Minecraft minecraft;
    private final ResourceManager resourceManager;
    private final Random random;
    private float renderDistance;
    public final ItemInHandRenderer itemInHandRenderer;
    private final MapRenderer mapRenderer;
    private int tick;
    private float fov;
    private float oldFov;
    private float darkenWorldAmount;
    private float darkenWorldAmountO;
    private boolean renderHand;
    private boolean renderBlockOutline;
    private long lastScreenshotAttempt;
    private long lastActiveTime;
    private final LightTexture lightTexture;
    private int rainSoundTime;
    private final float[] rainSizeX;
    private final float[] rainSizeZ;
    private final FogRenderer fog;
    private boolean panoramicMode;
    private double zoom;
    private double zoom_x;
    private double zoom_y;
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    private PostChain postEffect;
    private static final ResourceLocation[] EFFECTS;
    public static final int EFFECT_NONE;
    private int effectIndex;
    private boolean effectActive;
    private int frameId;
    private final Camera mainCamera;
    
    public GameRenderer(final Minecraft cyc, final ResourceManager xi) {
        this.random = new Random();
        this.renderHand = true;
        this.renderBlockOutline = true;
        this.lastActiveTime = Util.getMillis();
        this.rainSizeX = new float[1024];
        this.rainSizeZ = new float[1024];
        this.zoom = 1.0;
        this.effectIndex = GameRenderer.EFFECT_NONE;
        this.mainCamera = new Camera();
        this.minecraft = cyc;
        this.resourceManager = xi;
        this.itemInHandRenderer = cyc.getItemInHandRenderer();
        this.mapRenderer = new MapRenderer(cyc.getTextureManager());
        this.lightTexture = new LightTexture(this);
        this.fog = new FogRenderer(this);
        this.postEffect = null;
        for (int integer4 = 0; integer4 < 32; ++integer4) {
            for (int integer5 = 0; integer5 < 32; ++integer5) {
                final float float6 = (float)(integer5 - 16);
                final float float7 = (float)(integer4 - 16);
                final float float8 = Mth.sqrt(float6 * float6 + float7 * float7);
                this.rainSizeX[integer4 << 5 | integer5] = -float7 / float8;
                this.rainSizeZ[integer4 << 5 | integer5] = float6 / float8;
            }
        }
    }
    
    public void close() {
        this.lightTexture.close();
        this.mapRenderer.close();
        this.shutdownEffect();
    }
    
    public boolean postEffectActive() {
        return GLX.usePostProcess && this.postEffect != null;
    }
    
    public void shutdownEffect() {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        this.effectIndex = GameRenderer.EFFECT_NONE;
    }
    
    public void togglePostEffect() {
        this.effectActive = !this.effectActive;
    }
    
    public void checkEntityPostEffect(@Nullable final Entity aio) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        if (aio instanceof Creeper) {
            this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
        }
        else if (aio instanceof Spider) {
            this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
        }
        else if (aio instanceof EnderMan) {
            this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
        }
    }
    
    private void loadEffect(final ResourceLocation qv) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        try {
            (this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), qv)).resize(this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
            this.effectActive = true;
        }
        catch (IOException iOException3) {
            GameRenderer.LOGGER.warn("Failed to load shader: {}", qv, iOException3);
            this.effectIndex = GameRenderer.EFFECT_NONE;
            this.effectActive = false;
        }
        catch (JsonSyntaxException jsonSyntaxException3) {
            GameRenderer.LOGGER.warn("Failed to load shader: {}", qv, jsonSyntaxException3);
            this.effectIndex = GameRenderer.EFFECT_NONE;
            this.effectActive = false;
        }
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        if (this.effectIndex == GameRenderer.EFFECT_NONE) {
            this.checkEntityPostEffect(this.minecraft.getCameraEntity());
        }
        else {
            this.loadEffect(GameRenderer.EFFECTS[this.effectIndex]);
        }
    }
    
    public void tick() {
        if (GLX.usePostProcess && ProgramManager.getInstance() == null) {
            ProgramManager.createInstance();
        }
        this.tickFov();
        this.lightTexture.tick();
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.mainCamera.tick();
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.tickRain();
        this.darkenWorldAmountO = this.darkenWorldAmount;
        if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05f;
            if (this.darkenWorldAmount > 1.0f) {
                this.darkenWorldAmount = 1.0f;
            }
        }
        else if (this.darkenWorldAmount > 0.0f) {
            this.darkenWorldAmount -= 0.0125f;
        }
        if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
                this.itemActivationItem = null;
            }
        }
    }
    
    public PostChain currentEffect() {
        return this.postEffect;
    }
    
    public void resize(final int integer1, final int integer2) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.postEffect != null) {
            this.postEffect.resize(integer1, integer2);
        }
        this.minecraft.levelRenderer.resize(integer1, integer2);
    }
    
    public void pick(final float float1) {
        final Entity aio3 = this.minecraft.getCameraEntity();
        if (aio3 == null) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        this.minecraft.getProfiler().push("pick");
        this.minecraft.crosshairPickEntity = null;
        double double4 = this.minecraft.gameMode.getPickRange();
        this.minecraft.hitResult = aio3.pick(double4, float1, false);
        final Vec3 csi6 = aio3.getEyePosition(float1);
        boolean boolean7 = false;
        final int integer8 = 3;
        double double5 = double4;
        if (this.minecraft.gameMode.hasFarPickRange()) {
            double5 = (double4 = 6.0);
        }
        else {
            if (double5 > 3.0) {
                boolean7 = true;
            }
            double4 = double5;
        }
        double5 *= double5;
        if (this.minecraft.hitResult != null) {
            double5 = this.minecraft.hitResult.getLocation().distanceToSqr(csi6);
        }
        final Vec3 csi7 = aio3.getViewVector(1.0f);
        final Vec3 csi8 = csi6.add(csi7.x * double4, csi7.y * double4, csi7.z * double4);
        final float float2 = 1.0f;
        final AABB csc14 = aio3.getBoundingBox().expandTowards(csi7.scale(double4)).inflate(1.0, 1.0, 1.0);
        final EntityHitResult cse15 = ProjectileUtil.getEntityHitResult(aio3, csi6, csi8, csc14, (Predicate<Entity>)(aio -> !aio.isSpectator() && aio.isPickable()), double5);
        if (cse15 != null) {
            final Entity aio4 = cse15.getEntity();
            final Vec3 csi9 = cse15.getLocation();
            final double double6 = csi6.distanceToSqr(csi9);
            if (boolean7 && double6 > 9.0) {
                this.minecraft.hitResult = BlockHitResult.miss(csi9, Direction.getNearest(csi7.x, csi7.y, csi7.z), new BlockPos(csi9));
            }
            else if (double6 < double5 || this.minecraft.hitResult == null) {
                this.minecraft.hitResult = cse15;
                if (aio4 instanceof LivingEntity || aio4 instanceof ItemFrame) {
                    this.minecraft.crosshairPickEntity = aio4;
                }
            }
        }
        this.minecraft.getProfiler().pop();
    }
    
    private void tickFov() {
        float float2 = 1.0f;
        if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer) {
            final AbstractClientPlayer dmm3 = (AbstractClientPlayer)this.minecraft.getCameraEntity();
            float2 = dmm3.getFieldOfViewModifier();
        }
        this.oldFov = this.fov;
        this.fov += (float2 - this.fov) * 0.5f;
        if (this.fov > 1.5f) {
            this.fov = 1.5f;
        }
        if (this.fov < 0.1f) {
            this.fov = 0.1f;
        }
    }
    
    private double getFov(final Camera cxq, final float float2, final boolean boolean3) {
        if (this.panoramicMode) {
            return 90.0;
        }
        double double5 = 70.0;
        if (boolean3) {
            double5 = this.minecraft.options.fov;
            double5 *= Mth.lerp(float2, this.oldFov, this.fov);
        }
        if (cxq.getEntity() instanceof LivingEntity && ((LivingEntity)cxq.getEntity()).getHealth() <= 0.0f) {
            final float float3 = ((LivingEntity)cxq.getEntity()).deathTime + float2;
            double5 /= (1.0f - 500.0f / (float3 + 500.0f)) * 2.0f + 1.0f;
        }
        final FluidState clk7 = cxq.getFluidInCamera();
        if (!clk7.isEmpty()) {
            double5 = double5 * 60.0 / 70.0;
        }
        return double5;
    }
    
    private void bobHurt(final float float1) {
        if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
            final LivingEntity aix3 = (LivingEntity)this.minecraft.getCameraEntity();
            float float2 = aix3.hurtTime - float1;
            if (aix3.getHealth() <= 0.0f) {
                final float float3 = aix3.deathTime + float1;
                GlStateManager.rotatef(40.0f - 8000.0f / (float3 + 200.0f), 0.0f, 0.0f, 1.0f);
            }
            if (float2 < 0.0f) {
                return;
            }
            float2 /= aix3.hurtDuration;
            float2 = Mth.sin(float2 * float2 * float2 * float2 * 3.1415927f);
            final float float3 = aix3.hurtDir;
            GlStateManager.rotatef(-float3, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(-float2 * 14.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(float3, 0.0f, 1.0f, 0.0f);
        }
    }
    
    private void bobView(final float float1) {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return;
        }
        final Player awg3 = (Player)this.minecraft.getCameraEntity();
        final float float2 = awg3.walkDist - awg3.walkDistO;
        final float float3 = -(awg3.walkDist + float2 * float1);
        final float float4 = Mth.lerp(float1, awg3.oBob, awg3.bob);
        GlStateManager.translatef(Mth.sin(float3 * 3.1415927f) * float4 * 0.5f, -Math.abs(Mth.cos(float3 * 3.1415927f) * float4), 0.0f);
        GlStateManager.rotatef(Mth.sin(float3 * 3.1415927f) * float4 * 3.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(Math.abs(Mth.cos(float3 * 3.1415927f - 0.2f) * float4) * 5.0f, 1.0f, 0.0f, 0.0f);
    }
    
    private void setupCamera(final float float1) {
        this.renderDistance = (float)(this.minecraft.options.renderDistance * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        if (this.zoom != 1.0) {
            GlStateManager.translatef((float)this.zoom_x, (float)(-this.zoom_y), 0.0f);
            GlStateManager.scaled(this.zoom, this.zoom, 1.0);
        }
        GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(this.mainCamera, float1, true), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * Mth.SQRT_OF_TWO));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.bobHurt(float1);
        if (this.minecraft.options.bobView) {
            this.bobView(float1);
        }
        final float float2 = Mth.lerp(float1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
        if (float2 > 0.0f) {
            int integer4 = 20;
            if (this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
                integer4 = 7;
            }
            float float3 = 5.0f / (float2 * float2 + 5.0f) - float2 * 0.04f;
            float3 *= float3;
            GlStateManager.rotatef((this.tick + float1) * integer4, 0.0f, 1.0f, 1.0f);
            GlStateManager.scalef(1.0f / float3, 1.0f, 1.0f);
            GlStateManager.rotatef(-(this.tick + float1) * integer4, 0.0f, 1.0f, 1.0f);
        }
    }
    
    private void renderItemInHand(final Camera cxq, final float float2) {
        if (this.panoramicMode) {
            return;
        }
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(cxq, float2, false), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * 2.0f));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.pushMatrix();
        this.bobHurt(float2);
        if (this.minecraft.options.bobView) {
            this.bobView(float2);
        }
        final boolean boolean4 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
        if (this.minecraft.options.thirdPersonView == 0 && !boolean4 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.turnOnLightLayer();
            this.itemInHandRenderer.render(float2);
            this.turnOffLightLayer();
        }
        GlStateManager.popMatrix();
        if (this.minecraft.options.thirdPersonView == 0 && !boolean4) {
            this.itemInHandRenderer.renderScreenEffect(float2);
            this.bobHurt(float2);
        }
        if (this.minecraft.options.bobView) {
            this.bobView(float2);
        }
    }
    
    public void turnOffLightLayer() {
        this.lightTexture.turnOffLightLayer();
    }
    
    public void turnOnLightLayer() {
        this.lightTexture.turnOnLightLayer();
    }
    
    public float getNightVisionScale(final LivingEntity aix, final float float2) {
        final int integer4 = aix.getEffect(MobEffects.NIGHT_VISION).getDuration();
        if (integer4 > 200) {
            return 1.0f;
        }
        return 0.7f + Mth.sin((integer4 - float2) * 3.1415927f * 0.2f) * 0.3f;
    }
    
    public void render(final float float1, final long long2, final boolean boolean3) {
        if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || (this.minecraft.options.touchscreen && this.minecraft.mouseHandler.isRightPressed())) {
            this.lastActiveTime = Util.getMillis();
        }
        else if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
        }
        if (this.minecraft.noRender) {
            return;
        }
        final int integer6 = (int)(this.minecraft.mouseHandler.xpos() * this.minecraft.window.getGuiScaledWidth() / this.minecraft.window.getScreenWidth());
        final int integer7 = (int)(this.minecraft.mouseHandler.ypos() * this.minecraft.window.getGuiScaledHeight() / this.minecraft.window.getScreenHeight());
        final int integer8 = this.minecraft.options.framerateLimit;
        if (boolean3 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            int integer9 = Math.min(Minecraft.getAverageFps(), integer8);
            integer9 = Math.max(integer9, 60);
            final long long3 = Util.getNanos() - long2;
            final long long4 = Math.max(1000000000 / integer9 / 4 - long3, 0L);
            this.renderLevel(float1, Util.getNanos() + long4);
            if (this.minecraft.hasSingleplayerServer() && this.lastScreenshotAttempt < Util.getMillis() - 1000L) {
                this.lastScreenshotAttempt = Util.getMillis();
                if (!this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
                    this.takeAutoScreenshot();
                }
            }
            if (GLX.usePostProcess) {
                this.minecraft.levelRenderer.doEntityOutline();
                if (this.postEffect != null && this.effectActive) {
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    this.postEffect.process(float1);
                    GlStateManager.popMatrix();
                }
                this.minecraft.getMainRenderTarget().bindWrite(true);
            }
            this.minecraft.getProfiler().popPush("gui");
            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
                GlStateManager.alphaFunc(516, 0.1f);
                this.minecraft.window.setupGuiState(Minecraft.ON_OSX);
                this.renderItemActivationAnimation(this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight(), float1);
                this.minecraft.gui.render(float1);
            }
            this.minecraft.getProfiler().pop();
        }
        else {
            GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.minecraft.window.setupGuiState(Minecraft.ON_OSX);
        }
        if (this.minecraft.overlay != null) {
            GlStateManager.clear(256, Minecraft.ON_OSX);
            try {
                this.minecraft.overlay.render(integer6, integer7, this.minecraft.getDeltaFrameTime());
                return;
            }
            catch (Throwable throwable9) {
                final CrashReport d10 = CrashReport.forThrowable(throwable9, "Rendering overlay");
                final CrashReportCategory e11 = d10.addCategory("Overlay render details");
                e11.setDetail("Overlay name", (CrashReportDetail<String>)(() -> this.minecraft.overlay.getClass().getCanonicalName()));
                throw new ReportedException(d10);
            }
        }
        if (this.minecraft.screen != null) {
            GlStateManager.clear(256, Minecraft.ON_OSX);
            try {
                this.minecraft.screen.render(integer6, integer7, this.minecraft.getDeltaFrameTime());
            }
            catch (Throwable throwable9) {
                final CrashReport d10 = CrashReport.forThrowable(throwable9, "Rendering screen");
                final CrashReportCategory e11 = d10.addCategory("Screen render details");
                e11.setDetail("Screen name", (CrashReportDetail<String>)(() -> this.minecraft.screen.getClass().getCanonicalName()));
                e11.setDetail("Mouse location", (CrashReportDetail<String>)(() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", new Object[] { integer6, integer7, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos() })));
                e11.setDetail("Screen size", (CrashReportDetail<String>)(() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", new Object[] { this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight(), this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.window.getGuiScale() })));
                throw new ReportedException(d10);
            }
        }
    }
    
    private void takeAutoScreenshot() {
        if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks() && !this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
            final NativeImage cuj2 = Screenshot.takeScreenshot(this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.getMainRenderTarget());
            SimpleResource.IO_EXECUTOR.execute(() -> {
                int integer3 = cuj2.getWidth();
                int integer4 = cuj2.getHeight();
                int integer5 = 0;
                int integer6 = 0;
                if (integer3 > integer4) {
                    integer5 = (integer3 - integer4) / 2;
                    integer3 = integer4;
                }
                else {
                    integer6 = (integer4 - integer3) / 2;
                    integer4 = integer3;
                }
                try (final NativeImage cuj2 = new NativeImage(64, 64, false)) {
                    cuj2.resizeSubRectTo(integer5, integer6, integer3, integer4, cuj2);
                    cuj2.writeToFile(this.minecraft.getSingleplayerServer().getWorldScreenshotFile());
                }
                catch (IOException iOException7) {
                    GameRenderer.LOGGER.warn("Couldn't save auto screenshot", (Throwable)iOException7);
                }
                finally {
                    cuj2.close();
                }
            });
        }
    }
    
    private boolean shouldRenderBlockOutline() {
        if (!this.renderBlockOutline) {
            return false;
        }
        final Entity aio2 = this.minecraft.getCameraEntity();
        boolean boolean3 = aio2 instanceof Player && !this.minecraft.options.hideGui;
        if (boolean3 && !((Player)aio2).abilities.mayBuild) {
            final ItemStack bcj4 = ((LivingEntity)aio2).getMainHandItem();
            final HitResult csf5 = this.minecraft.hitResult;
            if (csf5 != null && csf5.getType() == HitResult.Type.BLOCK) {
                final BlockPos ew6 = ((BlockHitResult)csf5).getBlockPos();
                final BlockState bvt7 = this.minecraft.level.getBlockState(ew6);
                if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                    boolean3 = (bvt7.getMenuProvider(this.minecraft.level, ew6) != null);
                }
                else {
                    final BlockInWorld bvx8 = new BlockInWorld(this.minecraft.level, ew6, false);
                    boolean3 = (!bcj4.isEmpty() && (bcj4.hasAdventureModeBreakTagForBlock(this.minecraft.level.getTagManager(), bvx8) || bcj4.hasAdventureModePlaceTagForBlock(this.minecraft.level.getTagManager(), bvx8)));
                }
            }
        }
        return boolean3;
    }
    
    public void renderLevel(final float float1, final long long2) {
        this.lightTexture.updateLightTexture(float1);
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.pick(float1);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.5f);
        this.minecraft.getProfiler().push("center");
        this.render(float1, long2);
        this.minecraft.getProfiler().pop();
    }
    
    private void render(final float float1, final long long2) {
        final LevelRenderer dng5 = this.minecraft.levelRenderer;
        final ParticleEngine dlp6 = this.minecraft.particleEngine;
        final boolean boolean7 = this.shouldRenderBlockOutline();
        GlStateManager.enableCull();
        this.minecraft.getProfiler().popPush("camera");
        this.setupCamera(float1);
        final Camera cxq8 = this.mainCamera;
        cxq8.setup(this.minecraft.level, (this.minecraft.getCameraEntity() == null) ? this.minecraft.player : this.minecraft.getCameraEntity(), this.minecraft.options.thirdPersonView > 0, this.minecraft.options.thirdPersonView == 2, float1);
        final FrustumData dqh9 = Frustum.getFrustum();
        dng5.prepare(cxq8);
        this.minecraft.getProfiler().popPush("clear");
        GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
        this.fog.setupClearColor(cxq8, float1);
        GlStateManager.clear(16640, Minecraft.ON_OSX);
        this.minecraft.getProfiler().popPush("culling");
        final Culler dqe10 = new FrustumCuller(dqh9);
        final double double11 = cxq8.getPosition().x;
        final double double12 = cxq8.getPosition().y;
        final double double13 = cxq8.getPosition().z;
        dqe10.prepare(double11, double12, double13);
        if (this.minecraft.options.renderDistance >= 4) {
            this.fog.setupFog(cxq8, -1);
            this.minecraft.getProfiler().popPush("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(cxq8, float1, true), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * 2.0f));
            GlStateManager.matrixMode(5888);
            dng5.renderSky(float1);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(cxq8, float1, true), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * Mth.SQRT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
        this.fog.setupFog(cxq8, 0);
        GlStateManager.shadeModel(7425);
        if (cxq8.getPosition().y < 128.0) {
            this.prepareAndRenderClouds(cxq8, dng5, float1, double11, double12, double13);
        }
        this.minecraft.getProfiler().popPush("prepareterrain");
        this.fog.setupFog(cxq8, 0);
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        Lighting.turnOff();
        this.minecraft.getProfiler().popPush("terrain_setup");
        this.minecraft.level.getChunkSource().getLightEngine().runUpdates(Integer.MAX_VALUE, true, true);
        dng5.setupRender(cxq8, dqe10, this.frameId++, this.minecraft.player.isSpectator());
        this.minecraft.getProfiler().popPush("updatechunks");
        this.minecraft.levelRenderer.compileChunksUntil(long2);
        this.minecraft.getProfiler().popPush("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();
        dng5.render(BlockLayer.SOLID, cxq8);
        GlStateManager.enableAlphaTest();
        dng5.render(BlockLayer.CUTOUT_MIPPED, cxq8);
        this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
        dng5.render(BlockLayer.CUTOUT, cxq8);
        this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        Lighting.turnOn();
        this.minecraft.getProfiler().popPush("entities");
        dng5.renderEntities(cxq8, dqe10, float1);
        Lighting.turnOff();
        this.turnOffLightLayer();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        if (boolean7 && this.minecraft.hitResult != null) {
            GlStateManager.disableAlphaTest();
            this.minecraft.getProfiler().popPush("outline");
            dng5.renderHitOutline(cxq8, this.minecraft.hitResult, 0);
            GlStateManager.enableAlphaTest();
        }
        if (this.minecraft.debugRenderer.shouldRender()) {
            this.minecraft.debugRenderer.render(long2);
        }
        this.minecraft.getProfiler().popPush("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
        dng5.renderDestroyAnimation(Tesselator.getInstance(), Tesselator.getInstance().getBuilder(), cxq8);
        this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
        GlStateManager.disableBlend();
        this.turnOnLightLayer();
        this.fog.setupFog(cxq8, 0);
        this.minecraft.getProfiler().popPush("particles");
        dlp6.render(cxq8, float1);
        this.turnOffLightLayer();
        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.minecraft.getProfiler().popPush("weather");
        this.renderSnowAndRain(float1);
        GlStateManager.depthMask(true);
        dng5.renderWorldBounds(cxq8, float1);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        this.fog.setupFog(cxq8, 0);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        GlStateManager.shadeModel(7425);
        this.minecraft.getProfiler().popPush("translucent");
        dng5.render(BlockLayer.TRANSLUCENT, cxq8);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();
        if (cxq8.getPosition().y >= 128.0) {
            this.minecraft.getProfiler().popPush("aboveClouds");
            this.prepareAndRenderClouds(cxq8, dng5, float1, double11, double12, double13);
        }
        this.minecraft.getProfiler().popPush("hand");
        if (this.renderHand) {
            GlStateManager.clear(256, Minecraft.ON_OSX);
            this.renderItemInHand(cxq8, float1);
        }
    }
    
    private void prepareAndRenderClouds(final Camera cxq, final LevelRenderer dng, final float float3, final double double4, final double double5, final double double6) {
        if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
            this.minecraft.getProfiler().popPush("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(cxq, float3, true), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * 4.0f));
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.fog.setupFog(cxq, 0);
            dng.renderClouds(float3, double4, double5, double6);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(cxq, float3, true), this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05f, this.renderDistance * Mth.SQRT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void tickRain() {
        float float2 = this.minecraft.level.getRainLevel(1.0f);
        if (!this.minecraft.options.fancyGraphics) {
            float2 /= 2.0f;
        }
        if (float2 == 0.0f) {
            return;
        }
        this.random.setSeed(this.tick * 312987231L);
        final LevelReader bhu3 = this.minecraft.level;
        final BlockPos ew4 = new BlockPos(this.mainCamera.getPosition());
        final int integer5 = 10;
        double double6 = 0.0;
        double double7 = 0.0;
        double double8 = 0.0;
        int integer6 = 0;
        int integer7 = (int)(100.0f * float2 * float2);
        if (this.minecraft.options.particles == ParticleStatus.DECREASED) {
            integer7 >>= 1;
        }
        else if (this.minecraft.options.particles == ParticleStatus.MINIMAL) {
            integer7 = 0;
        }
        for (int integer8 = 0; integer8 < integer7; ++integer8) {
            final BlockPos ew5 = bhu3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew4.offset(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            final Biome bio16 = bhu3.getBiome(ew5);
            final BlockPos ew6 = ew5.below();
            if (ew5.getY() <= ew4.getY() + 10 && ew5.getY() >= ew4.getY() - 10 && bio16.getPrecipitation() == Biome.Precipitation.RAIN && bio16.getTemperature(ew5) >= 0.15f) {
                final double double9 = this.random.nextDouble();
                final double double10 = this.random.nextDouble();
                final BlockState bvt22 = bhu3.getBlockState(ew6);
                final FluidState clk23 = bhu3.getFluidState(ew5);
                final VoxelShape ctc24 = bvt22.getCollisionShape(bhu3, ew6);
                final double double11 = ctc24.max(Direction.Axis.Y, double9, double10);
                final double double12 = clk23.getHeight(bhu3, ew5);
                double double13;
                double double14;
                if (double11 >= double12) {
                    double13 = double11;
                    double14 = ctc24.min(Direction.Axis.Y, double9, double10);
                }
                else {
                    double13 = 0.0;
                    double14 = 0.0;
                }
                if (double13 > -1.7976931348623157E308) {
                    if (clk23.is(FluidTags.LAVA) || bvt22.getBlock() == Blocks.MAGMA_BLOCK || (bvt22.getBlock() == Blocks.CAMPFIRE && bvt22.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT))) {
                        this.minecraft.level.addParticle(ParticleTypes.SMOKE, ew5.getX() + double9, ew5.getY() + 0.1f - double14, ew5.getZ() + double10, 0.0, 0.0, 0.0);
                    }
                    else {
                        if (this.random.nextInt(++integer6) == 0) {
                            double6 = ew6.getX() + double9;
                            double7 = ew6.getY() + 0.1f + double13 - 1.0;
                            double8 = ew6.getZ() + double10;
                        }
                        this.minecraft.level.addParticle(ParticleTypes.RAIN, ew6.getX() + double9, ew6.getY() + 0.1f + double13, ew6.getZ() + double10, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
        if (integer6 > 0 && this.random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (double7 > ew4.getY() + 1 && bhu3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ew4).getY() > Mth.floor((float)ew4.getY())) {
                this.minecraft.level.playLocalSound(double6, double7, double8, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1f, 0.5f, false);
            }
            else {
                this.minecraft.level.playLocalSound(double6, double7, double8, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }
    
    protected void renderSnowAndRain(final float float1) {
        final float float2 = this.minecraft.level.getRainLevel(float1);
        if (float2 <= 0.0f) {
            return;
        }
        this.turnOnLightLayer();
        final Level bhr4 = this.minecraft.level;
        final int integer5 = Mth.floor(this.mainCamera.getPosition().x);
        final int integer6 = Mth.floor(this.mainCamera.getPosition().y);
        final int integer7 = Mth.floor(this.mainCamera.getPosition().z);
        final Tesselator cuz8 = Tesselator.getInstance();
        final BufferBuilder cuw9 = cuz8.getBuilder();
        GlStateManager.disableCull();
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        final double double10 = this.mainCamera.getPosition().x;
        final double double11 = this.mainCamera.getPosition().y;
        final double double12 = this.mainCamera.getPosition().z;
        final int integer8 = Mth.floor(double11);
        int integer9 = 5;
        if (this.minecraft.options.fancyGraphics) {
            integer9 = 10;
        }
        int integer10 = -1;
        final float float3 = this.tick + float1;
        cuw9.offset(-double10, -double11, -double12);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final BlockPos.MutableBlockPos a20 = new BlockPos.MutableBlockPos();
        for (int integer11 = integer7 - integer9; integer11 <= integer7 + integer9; ++integer11) {
            for (int integer12 = integer5 - integer9; integer12 <= integer5 + integer9; ++integer12) {
                final int integer13 = (integer11 - integer7 + 16) * 32 + integer12 - integer5 + 16;
                final double double13 = this.rainSizeX[integer13] * 0.5;
                final double double14 = this.rainSizeZ[integer13] * 0.5;
                a20.set(integer12, 0, integer11);
                final Biome bio28 = bhr4.getBiome(a20);
                if (bio28.getPrecipitation() != Biome.Precipitation.NONE) {
                    final int integer14 = bhr4.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, a20).getY();
                    int integer15 = integer6 - integer9;
                    int integer16 = integer6 + integer9;
                    if (integer15 < integer14) {
                        integer15 = integer14;
                    }
                    if (integer16 < integer14) {
                        integer16 = integer14;
                    }
                    int integer17 = integer14;
                    if (integer17 < integer8) {
                        integer17 = integer8;
                    }
                    if (integer15 != integer16) {
                        this.random.setSeed((long)(integer12 * integer12 * 3121 + integer12 * 45238971 ^ integer11 * integer11 * 418711 + integer11 * 13761));
                        a20.set(integer12, integer15, integer11);
                        final float float4 = bio28.getTemperature(a20);
                        if (float4 >= 0.15f) {
                            if (integer10 != 0) {
                                if (integer10 >= 0) {
                                    cuz8.end();
                                }
                                integer10 = 0;
                                this.minecraft.getTextureManager().bind(GameRenderer.RAIN_LOCATION);
                                cuw9.begin(7, DefaultVertexFormat.PARTICLE);
                            }
                            final double double15 = -((this.tick + integer12 * integer12 * 3121 + integer12 * 45238971 + integer11 * integer11 * 418711 + integer11 * 13761 & 0x1F) + (double)float1) / 32.0 * (3.0 + this.random.nextDouble());
                            final double double16 = integer12 + 0.5f - this.mainCamera.getPosition().x;
                            final double double17 = integer11 + 0.5f - this.mainCamera.getPosition().z;
                            final float float5 = Mth.sqrt(double16 * double16 + double17 * double17) / integer9;
                            final float float6 = ((1.0f - float5 * float5) * 0.5f + 0.5f) * float2;
                            a20.set(integer12, integer17, integer11);
                            final int integer18 = bhr4.getLightColor(a20, 0);
                            final int integer19 = integer18 >> 16 & 0xFFFF;
                            final int integer20 = integer18 & 0xFFFF;
                            cuw9.vertex(integer12 - double13 + 0.5, integer16, integer11 - double14 + 0.5).uv(0.0, integer15 * 0.25 + double15).color(1.0f, 1.0f, 1.0f, float6).uv2(integer19, integer20).endVertex();
                            cuw9.vertex(integer12 + double13 + 0.5, integer16, integer11 + double14 + 0.5).uv(1.0, integer15 * 0.25 + double15).color(1.0f, 1.0f, 1.0f, float6).uv2(integer19, integer20).endVertex();
                            cuw9.vertex(integer12 + double13 + 0.5, integer15, integer11 + double14 + 0.5).uv(1.0, integer16 * 0.25 + double15).color(1.0f, 1.0f, 1.0f, float6).uv2(integer19, integer20).endVertex();
                            cuw9.vertex(integer12 - double13 + 0.5, integer15, integer11 - double14 + 0.5).uv(0.0, integer16 * 0.25 + double15).color(1.0f, 1.0f, 1.0f, float6).uv2(integer19, integer20).endVertex();
                        }
                        else {
                            if (integer10 != 1) {
                                if (integer10 >= 0) {
                                    cuz8.end();
                                }
                                integer10 = 1;
                                this.minecraft.getTextureManager().bind(GameRenderer.SNOW_LOCATION);
                                cuw9.begin(7, DefaultVertexFormat.PARTICLE);
                            }
                            final double double15 = -((this.tick & 0x1FF) + float1) / 512.0f;
                            final double double16 = this.random.nextDouble() + float3 * 0.01 * (float)this.random.nextGaussian();
                            final double double17 = this.random.nextDouble() + float3 * (float)this.random.nextGaussian() * 0.001;
                            final double double18 = integer12 + 0.5f - this.mainCamera.getPosition().x;
                            final double double19 = integer11 + 0.5f - this.mainCamera.getPosition().z;
                            final float float7 = Mth.sqrt(double18 * double18 + double19 * double19) / integer9;
                            final float float8 = ((1.0f - float7 * float7) * 0.3f + 0.5f) * float2;
                            a20.set(integer12, integer17, integer11);
                            final int integer21 = (bhr4.getLightColor(a20, 0) * 3 + 15728880) / 4;
                            final int integer22 = integer21 >> 16 & 0xFFFF;
                            final int integer23 = integer21 & 0xFFFF;
                            cuw9.vertex(integer12 - double13 + 0.5, integer16, integer11 - double14 + 0.5).uv(0.0 + double16, integer15 * 0.25 + double15 + double17).color(1.0f, 1.0f, 1.0f, float8).uv2(integer22, integer23).endVertex();
                            cuw9.vertex(integer12 + double13 + 0.5, integer16, integer11 + double14 + 0.5).uv(1.0 + double16, integer15 * 0.25 + double15 + double17).color(1.0f, 1.0f, 1.0f, float8).uv2(integer22, integer23).endVertex();
                            cuw9.vertex(integer12 + double13 + 0.5, integer15, integer11 + double14 + 0.5).uv(1.0 + double16, integer16 * 0.25 + double15 + double17).color(1.0f, 1.0f, 1.0f, float8).uv2(integer22, integer23).endVertex();
                            cuw9.vertex(integer12 - double13 + 0.5, integer15, integer11 - double14 + 0.5).uv(0.0 + double16, integer16 * 0.25 + double15 + double17).color(1.0f, 1.0f, 1.0f, float8).uv2(integer22, integer23).endVertex();
                        }
                    }
                }
            }
        }
        if (integer10 >= 0) {
            cuz8.end();
        }
        cuw9.offset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        this.turnOffLightLayer();
    }
    
    public void resetFogColor(final boolean boolean1) {
        this.fog.resetFogColor(boolean1);
    }
    
    public void resetData() {
        this.itemActivationItem = null;
        this.mapRenderer.resetData();
        this.mainCamera.reset();
    }
    
    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }
    
    public static void renderNameTagInWorld(final Font cyu, final String string, final float float3, final float float4, final float float5, final int integer, final float float7, final float float8, final boolean boolean9) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(float3, float4, float5);
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-float7, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float8, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        if (!boolean9) {
            GlStateManager.disableDepthTest();
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final int integer2 = cyu.width(string) / 2;
        GlStateManager.disableTexture();
        final Tesselator cuz11 = Tesselator.getInstance();
        final BufferBuilder cuw12 = cuz11.getBuilder();
        cuw12.begin(7, DefaultVertexFormat.POSITION_COLOR);
        final float float9 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        cuw12.vertex(-integer2 - 1, -1 + integer, 0.0).color(0.0f, 0.0f, 0.0f, float9).endVertex();
        cuw12.vertex(-integer2 - 1, 8 + integer, 0.0).color(0.0f, 0.0f, 0.0f, float9).endVertex();
        cuw12.vertex(integer2 + 1, 8 + integer, 0.0).color(0.0f, 0.0f, 0.0f, float9).endVertex();
        cuw12.vertex(integer2 + 1, -1 + integer, 0.0).color(0.0f, 0.0f, 0.0f, float9).endVertex();
        cuz11.end();
        GlStateManager.enableTexture();
        if (!boolean9) {
            cyu.draw(string, (float)(-cyu.width(string) / 2), (float)integer, 553648127);
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        cyu.draw(string, (float)(-cyu.width(string) / 2), (float)integer, boolean9 ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    public void displayItemActivation(final ItemStack bcj) {
        this.itemActivationItem = bcj;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0f - 1.0f;
        this.itemActivationOffY = this.random.nextFloat() * 2.0f - 1.0f;
    }
    
    private void renderItemActivationAnimation(final int integer1, final int integer2, final float float3) {
        if (this.itemActivationItem == null || this.itemActivationTicks <= 0) {
            return;
        }
        final int integer3 = 40 - this.itemActivationTicks;
        final float float4 = (integer3 + float3) / 40.0f;
        final float float5 = float4 * float4;
        final float float6 = float4 * float5;
        final float float7 = 10.25f * float6 * float5 - 24.95f * float5 * float5 + 25.5f * float6 - 13.8f * float5 + 4.0f * float4;
        final float float8 = float7 * 3.1415927f;
        final float float9 = this.itemActivationOffX * (integer1 / 4);
        final float float10 = this.itemActivationOffY * (integer2 / 4);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
        GlStateManager.pushLightingAttributes();
        GlStateManager.enableDepthTest();
        GlStateManager.disableCull();
        Lighting.turnOn();
        GlStateManager.translatef(integer1 / 2 + float9 * Mth.abs(Mth.sin(float8 * 2.0f)), integer2 / 2 + float10 * Mth.abs(Mth.sin(float8 * 2.0f)), -50.0f);
        final float float11 = 50.0f + 175.0f * Mth.sin(float8);
        GlStateManager.scalef(float11, -float11, float11);
        GlStateManager.rotatef(900.0f * Mth.abs(Mth.sin(float8)), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(6.0f * Mth.cos(float4 * 8.0f), 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(6.0f * Mth.cos(float4 * 8.0f), 0.0f, 0.0f, 1.0f);
        this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemTransforms.TransformType.FIXED);
        GlStateManager.popAttributes();
        GlStateManager.popMatrix();
        Lighting.turnOff();
        GlStateManager.enableCull();
        GlStateManager.disableDepthTest();
    }
    
    public Minecraft getMinecraft() {
        return this.minecraft;
    }
    
    public float getDarkenWorldAmount(final float float1) {
        return Mth.lerp(float1, this.darkenWorldAmountO, this.darkenWorldAmount);
    }
    
    public float getRenderDistance() {
        return this.renderDistance;
    }
    
    public Camera getMainCamera() {
        return this.mainCamera;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
        SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
        EFFECTS = new ResourceLocation[] { new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json") };
        EFFECT_NONE = GameRenderer.EFFECTS.length;
    }
}

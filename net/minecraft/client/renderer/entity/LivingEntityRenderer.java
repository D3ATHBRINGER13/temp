package net.minecraft.client.renderer.entity;

import java.util.function.Consumer;
import net.minecraft.Util;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;
import net.minecraft.client.player.LocalPlayer;
import java.util.Iterator;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import java.util.List;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    private static final Logger LOGGER;
    private static final DynamicTexture WHITE_TEXTURE;
    protected M model;
    protected final FloatBuffer tintBuffer;
    protected final List<RenderLayer<T, M>> layers;
    protected boolean onlySolidLayers;
    
    public LivingEntityRenderer(final EntityRenderDispatcher dsa, final M dhh, final float float3) {
        super(dsa);
        this.tintBuffer = MemoryTracker.createFloatBuffer(4);
        this.layers = (List<RenderLayer<T, M>>)Lists.newArrayList();
        this.model = dhh;
        this.shadowRadius = float3;
    }
    
    protected final boolean addLayer(final RenderLayer<T, M> dvy) {
        return this.layers.add(dvy);
    }
    
    @Override
    public M getModel() {
        return this.model;
    }
    
    @Override
    public void render(final T aix, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.model.attackTime = this.getAttackAnim(aix, float6);
        this.model.riding = aix.isPassenger();
        this.model.young = aix.isBaby();
        try {
            float float7 = Mth.rotLerp(float6, aix.yBodyRotO, aix.yBodyRot);
            final float float8 = Mth.rotLerp(float6, aix.yHeadRotO, aix.yHeadRot);
            float float9 = float8 - float7;
            if (aix.isPassenger() && aix.getVehicle() instanceof LivingEntity) {
                final LivingEntity aix2 = (LivingEntity)aix.getVehicle();
                float7 = Mth.rotLerp(float6, aix2.yBodyRotO, aix2.yBodyRot);
                float9 = float8 - float7;
                float float10 = Mth.wrapDegrees(float9);
                if (float10 < -85.0f) {
                    float10 = -85.0f;
                }
                if (float10 >= 85.0f) {
                    float10 = 85.0f;
                }
                float7 = float8 - float10;
                if (float10 * float10 > 2500.0f) {
                    float7 += float10 * 0.2f;
                }
                float9 = float8 - float7;
            }
            final float float11 = Mth.lerp(float6, aix.xRotO, aix.xRot);
            this.renderName(aix, double2, double3, double4);
            float float10 = this.getBob(aix, float6);
            this.setupRotations(aix, float10, float7, float6);
            final float float12 = this.setupScale(aix, float6);
            float float13 = 0.0f;
            float float14 = 0.0f;
            if (!aix.isPassenger() && aix.isAlive()) {
                float13 = Mth.lerp(float6, aix.animationSpeedOld, aix.animationSpeed);
                float14 = aix.animationPosition - aix.animationSpeed * (1.0f - float6);
                if (aix.isBaby()) {
                    float14 *= 3.0f;
                }
                if (float13 > 1.0f) {
                    float13 = 1.0f;
                }
            }
            GlStateManager.enableAlphaTest();
            this.model.prepareMobModel(aix, float14, float13, float6);
            this.model.setupAnim(aix, float14, float13, float10, float9, float11, float12);
            if (this.solidRender) {
                final boolean boolean19 = this.setupSolidState(aix);
                GlStateManager.enableColorMaterial();
                GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(aix));
                if (!this.onlySolidLayers) {
                    this.renderModel(aix, float14, float13, float10, float9, float11, float12);
                }
                if (!aix.isSpectator()) {
                    this.renderLayers(aix, float14, float13, float6, float10, float9, float11, float12);
                }
                GlStateManager.tearDownSolidRenderingTextureCombine();
                GlStateManager.disableColorMaterial();
                if (boolean19) {
                    this.tearDownSolidState();
                }
            }
            else {
                final boolean boolean19 = this.setupOverlayColor(aix, float6);
                this.renderModel(aix, float14, float13, float10, float9, float11, float12);
                if (boolean19) {
                    this.teardownOverlayColor();
                }
                GlStateManager.depthMask(true);
                if (!aix.isSpectator()) {
                    this.renderLayers(aix, float14, float13, float6, float10, float9, float11, float12);
                }
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception11) {
            LivingEntityRenderer.LOGGER.error("Couldn't render entity", (Throwable)exception11);
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.render(aix, double2, double3, double4, float5, float6);
    }
    
    public float setupScale(final T aix, final float float2) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.scale(aix, float2);
        final float float3 = 0.0625f;
        GlStateManager.translatef(0.0f, -1.501f, 0.0f);
        return 0.0625f;
    }
    
    protected boolean setupSolidState(final T aix) {
        GlStateManager.disableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        return true;
    }
    
    protected void tearDownSolidState() {
        GlStateManager.enableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    protected void renderModel(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final boolean boolean9 = this.isVisible(aix);
        final boolean boolean10 = !boolean9 && !aix.isInvisibleTo(Minecraft.getInstance().player);
        if (boolean9 || boolean10) {
            if (!this.bindTexture(aix)) {
                return;
            }
            if (boolean10) {
                GlStateManager.setProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            this.model.render(aix, float2, float3, float4, float5, float6, float7);
            if (boolean10) {
                GlStateManager.unsetProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }
    
    protected boolean isVisible(final T aix) {
        return !aix.isInvisible() || this.solidRender;
    }
    
    protected boolean setupOverlayColor(final T aix, final float float2) {
        return this.setupOverlayColor(aix, float2, true);
    }
    
    protected boolean setupOverlayColor(final T aix, final float float2, final boolean boolean3) {
        final float float3 = aix.getBrightness();
        final int integer6 = this.getOverlayColor(aix, float3, float2);
        final boolean boolean4 = (integer6 >> 24 & 0xFF) > 0;
        final boolean boolean5 = aix.hurtTime > 0 || aix.deathTime > 0;
        if (!boolean4 && !boolean5) {
            return false;
        }
        if (!boolean4 && !boolean3) {
            return false;
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        this.tintBuffer.position(0);
        if (boolean5) {
            this.tintBuffer.put(1.0f);
            this.tintBuffer.put(0.0f);
            this.tintBuffer.put(0.0f);
            this.tintBuffer.put(0.3f);
        }
        else {
            final float float4 = (integer6 >> 24 & 0xFF) / 255.0f;
            final float float5 = (integer6 >> 16 & 0xFF) / 255.0f;
            final float float6 = (integer6 >> 8 & 0xFF) / 255.0f;
            final float float7 = (integer6 & 0xFF) / 255.0f;
            this.tintBuffer.put(float5);
            this.tintBuffer.put(float6);
            this.tintBuffer.put(float7);
            this.tintBuffer.put(1.0f - float4);
        }
        this.tintBuffer.flip();
        GlStateManager.texEnv(8960, 8705, this.tintBuffer);
        GlStateManager.activeTexture(GLX.GL_TEXTURE2);
        GlStateManager.enableTexture();
        GlStateManager.bindTexture(LivingEntityRenderer.WHITE_TEXTURE.getId());
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        return true;
    }
    
    protected void teardownOverlayColor() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.activeTexture(GLX.GL_TEXTURE2);
        GlStateManager.disableTexture();
        GlStateManager.bindTexture(0);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    @Override
    protected void renderName(final T aix, final double double2, final double double3, final double double4) {
        if (aix.getPose() == Pose.SLEEPING) {
            final Direction fb9 = aix.getBedOrientation();
            if (fb9 != null) {
                final float float10 = aix.getEyeHeight(Pose.STANDING) - 0.1f;
                GlStateManager.translatef((float)double2 - fb9.getStepX() * float10, (float)double3, (float)double4 - fb9.getStepZ() * float10);
                return;
            }
        }
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
    }
    
    private static float sleepDirectionToRotation(final Direction fb) {
        switch (fb) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    protected void setupRotations(final T aix, final float float2, final float float3, final float float4) {
        final Pose ajh6 = aix.getPose();
        if (ajh6 != Pose.SLEEPING) {
            GlStateManager.rotatef(180.0f - float3, 0.0f, 1.0f, 0.0f);
        }
        if (aix.deathTime > 0) {
            float float5 = (aix.deathTime + float4 - 1.0f) / 20.0f * 1.6f;
            float5 = Mth.sqrt(float5);
            if (float5 > 1.0f) {
                float5 = 1.0f;
            }
            GlStateManager.rotatef(float5 * this.getFlipDegrees(aix), 0.0f, 0.0f, 1.0f);
        }
        else if (aix.isAutoSpinAttack()) {
            GlStateManager.rotatef(-90.0f - aix.xRot, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef((aix.tickCount + float4) * -75.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (ajh6 == Pose.SLEEPING) {
            final Direction fb7 = aix.getBedOrientation();
            GlStateManager.rotatef((fb7 != null) ? sleepDirectionToRotation(fb7) : float3, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(this.getFlipDegrees(aix), 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(270.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (aix.hasCustomName() || aix instanceof Player) {
            final String string7 = ChatFormatting.stripFormatting(aix.getName().getString());
            if (string7 != null && ("Dinnerbone".equals(string7) || "Grumm".equals(string7)) && (!(aix instanceof Player) || ((Player)aix).isModelPartShown(PlayerModelPart.CAPE))) {
                GlStateManager.translatef(0.0f, aix.getBbHeight() + 0.1f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }
    
    protected float getAttackAnim(final T aix, final float float2) {
        return aix.getAttackAnim(float2);
    }
    
    protected float getBob(final T aix, final float float2) {
        return aix.tickCount + float2;
    }
    
    protected void renderLayers(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        for (final RenderLayer<T, M> dvy11 : this.layers) {
            final boolean boolean12 = this.setupOverlayColor(aix, float4, dvy11.colorsOnDamage());
            dvy11.render(aix, float2, float3, float4, float5, float6, float7, float8);
            if (boolean12) {
                this.teardownOverlayColor();
            }
        }
    }
    
    protected float getFlipDegrees(final T aix) {
        return 90.0f;
    }
    
    protected int getOverlayColor(final T aix, final float float2, final float float3) {
        return 0;
    }
    
    protected void scale(final T aix, final float float2) {
    }
    
    public void renderName(final T aix, final double double2, final double double3, final double double4) {
        if (!this.shouldShowName(aix)) {
            return;
        }
        final double double5 = aix.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
        final float float11 = aix.isVisuallySneaking() ? 32.0f : 64.0f;
        if (double5 >= float11 * float11) {
            return;
        }
        final String string12 = aix.getDisplayName().getColoredString();
        GlStateManager.alphaFunc(516, 0.1f);
        this.renderNameTags(aix, double2, double3, double4, string12, double5);
    }
    
    @Override
    protected boolean shouldShowName(final T aix) {
        final LocalPlayer dmp3 = Minecraft.getInstance().player;
        final boolean boolean4 = !aix.isInvisibleTo(dmp3);
        if (aix != dmp3) {
            final Team ctk5 = aix.getTeam();
            final Team ctk6 = dmp3.getTeam();
            if (ctk5 != null) {
                final Team.Visibility b7 = ctk5.getNameTagVisibility();
                switch (b7) {
                    case ALWAYS: {
                        return boolean4;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return (ctk6 == null) ? boolean4 : (ctk5.isAlliedTo(ctk6) && (ctk5.canSeeFriendlyInvisibles() || boolean4));
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return (ctk6 == null) ? boolean4 : (!ctk5.isAlliedTo(ctk6) && boolean4);
                    }
                    default: {
                        return true;
                    }
                }
            }
        }
        return Minecraft.renderNames() && aix != this.entityRenderDispatcher.camera.getEntity() && boolean4 && !aix.isVehicle();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        WHITE_TEXTURE = Util.<DynamicTexture>make(new DynamicTexture(16, 16, false), (java.util.function.Consumer<DynamicTexture>)(dwr -> {
            dwr.getPixels().untrack();
            for (int integer2 = 0; integer2 < 16; ++integer2) {
                for (int integer3 = 0; integer3 < 16; ++integer3) {
                    dwr.getPixels().setPixelRGBA(integer3, integer2, -1);
                }
            }
            dwr.upload();
        }));
    }
}

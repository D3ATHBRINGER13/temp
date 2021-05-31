package net.minecraft.client.gui;

import net.minecraft.world.level.border.WorldBorder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.food.FoodData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Score;
import com.google.common.collect.Iterables;
import java.util.stream.Collectors;
import net.minecraft.util.StringUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffect;
import java.util.Iterator;
import net.minecraft.client.resources.MobEffectTextureManager;
import java.util.Collection;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.effect.MobEffectInstance;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.AttackIndicatorStatus;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.level.GameType;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.chat.StandardChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.chat.ChatListener;
import java.util.List;
import net.minecraft.network.chat.ChatType;
import java.util.Map;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.Minecraft;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;

public class Gui extends GuiComponent {
    private static final ResourceLocation VIGNETTE_LOCATION;
    private static final ResourceLocation WIDGETS_LOCATION;
    private static final ResourceLocation PUMPKIN_BLUR_LOCATION;
    private final Random random;
    private final Minecraft minecraft;
    private final ItemRenderer itemRenderer;
    private final ChatComponent chat;
    private int tickCount;
    private String overlayMessageString;
    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    public float vignetteBrightness;
    private int toolHighlightTimer;
    private ItemStack lastToolHighlight;
    private final DebugScreenOverlay debugScreen;
    private final SubtitleOverlay subtitleOverlay;
    private final SpectatorGui spectatorGui;
    private final PlayerTabOverlay tabList;
    private final BossHealthOverlay bossOverlay;
    private int titleTime;
    private String title;
    private String subtitle;
    private int titleFadeInTime;
    private int titleStayTime;
    private int titleFadeOutTime;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private int screenWidth;
    private int screenHeight;
    private final Map<ChatType, List<ChatListener>> chatListeners;
    
    public Gui(final Minecraft cyc) {
        this.random = new Random();
        this.overlayMessageString = "";
        this.vignetteBrightness = 1.0f;
        this.lastToolHighlight = ItemStack.EMPTY;
        this.title = "";
        this.subtitle = "";
        this.chatListeners = (Map<ChatType, List<ChatListener>>)Maps.newHashMap();
        this.minecraft = cyc;
        this.itemRenderer = cyc.getItemRenderer();
        this.debugScreen = new DebugScreenOverlay(cyc);
        this.spectatorGui = new SpectatorGui(cyc);
        this.chat = new ChatComponent(cyc);
        this.tabList = new PlayerTabOverlay(cyc, this);
        this.bossOverlay = new BossHealthOverlay(cyc);
        this.subtitleOverlay = new SubtitleOverlay(cyc);
        for (final ChatType jm6 : ChatType.values()) {
            this.chatListeners.put(jm6, Lists.newArrayList());
        }
        final ChatListener cyy3 = NarratorChatListener.INSTANCE;
        ((List)this.chatListeners.get(ChatType.CHAT)).add(new StandardChatListener(cyc));
        ((List)this.chatListeners.get(ChatType.CHAT)).add(cyy3);
        ((List)this.chatListeners.get(ChatType.SYSTEM)).add(new StandardChatListener(cyc));
        ((List)this.chatListeners.get(ChatType.SYSTEM)).add(cyy3);
        ((List)this.chatListeners.get(ChatType.GAME_INFO)).add(new OverlayChatListener(cyc));
        this.resetTitleTimes();
    }
    
    public void resetTitleTimes() {
        this.titleFadeInTime = 10;
        this.titleStayTime = 70;
        this.titleFadeOutTime = 20;
    }
    
    public void render(final float float1) {
        this.screenWidth = this.minecraft.window.getGuiScaledWidth();
        this.screenHeight = this.minecraft.window.getGuiScaledHeight();
        final Font cyu3 = this.getFont();
        GlStateManager.enableBlend();
        if (Minecraft.useFancyGraphics()) {
            this.renderVignette(this.minecraft.getCameraEntity());
        }
        else {
            GlStateManager.enableDepthTest();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        final ItemStack bcj4 = this.minecraft.player.inventory.getArmor(3);
        if (this.minecraft.options.thirdPersonView == 0 && bcj4.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            this.renderPumpkin();
        }
        if (!this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
            final float float2 = Mth.lerp(float1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
            if (float2 > 0.0f) {
                this.renderPortalOverlay(float2);
            }
        }
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            this.spectatorGui.renderHotbar(float1);
        }
        else if (!this.minecraft.options.hideGui) {
            this.renderHotbar(float1);
        }
        if (!this.minecraft.options.hideGui) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(Gui.GUI_ICONS_LOCATION);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            this.renderCrosshair();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.minecraft.getProfiler().push("bossHealth");
            this.bossOverlay.render();
            this.minecraft.getProfiler().pop();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(Gui.GUI_ICONS_LOCATION);
            if (this.minecraft.gameMode.canHurtPlayer()) {
                this.renderPlayerHealth();
            }
            this.renderVehicleHealth();
            GlStateManager.disableBlend();
            final int integer5 = this.screenWidth / 2 - 91;
            if (this.minecraft.player.isRidingJumpable()) {
                this.renderJumpMeter(integer5);
            }
            else if (this.minecraft.gameMode.hasExperience()) {
                this.renderExperienceBar(integer5);
            }
            if (this.minecraft.options.heldItemTooltips && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                this.renderSelectedItemName();
            }
            else if (this.minecraft.player.isSpectator()) {
                this.spectatorGui.renderTooltip();
            }
        }
        if (this.minecraft.player.getSleepTimer() > 0) {
            this.minecraft.getProfiler().push("sleep");
            GlStateManager.disableDepthTest();
            GlStateManager.disableAlphaTest();
            final float float2 = (float)this.minecraft.player.getSleepTimer();
            float float3 = float2 / 100.0f;
            if (float3 > 1.0f) {
                float3 = 1.0f - (float2 - 100.0f) / 10.0f;
            }
            final int integer6 = (int)(220.0f * float3) << 24 | 0x101020;
            GuiComponent.fill(0, 0, this.screenWidth, this.screenHeight, integer6);
            GlStateManager.enableAlphaTest();
            GlStateManager.enableDepthTest();
            this.minecraft.getProfiler().pop();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.minecraft.isDemo()) {
            this.renderDemoOverlay();
        }
        this.renderEffects();
        if (this.minecraft.options.renderDebug) {
            this.debugScreen.render();
        }
        if (!this.minecraft.options.hideGui) {
            if (this.overlayMessageTime > 0) {
                this.minecraft.getProfiler().push("overlayMessage");
                final float float2 = this.overlayMessageTime - float1;
                int integer7 = (int)(float2 * 255.0f / 20.0f);
                if (integer7 > 255) {
                    integer7 = 255;
                }
                if (integer7 > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight - 68), 0.0f);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    int integer6 = 16777215;
                    if (this.animateOverlayMessageColor) {
                        integer6 = (Mth.hsvToRgb(float2 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF);
                    }
                    final int integer8 = integer7 << 24 & 0xFF000000;
                    this.drawBackdrop(cyu3, -4, cyu3.width(this.overlayMessageString));
                    cyu3.draw(this.overlayMessageString, (float)(-cyu3.width(this.overlayMessageString) / 2), -4.0f, integer6 | integer8);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
                this.minecraft.getProfiler().pop();
            }
            if (this.titleTime > 0) {
                this.minecraft.getProfiler().push("titleAndSubtitle");
                final float float2 = this.titleTime - float1;
                int integer7 = 255;
                if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
                    final float float4 = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime - float2;
                    integer7 = (int)(float4 * 255.0f / this.titleFadeInTime);
                }
                if (this.titleTime <= this.titleFadeOutTime) {
                    integer7 = (int)(float2 * 255.0f / this.titleFadeOutTime);
                }
                integer7 = Mth.clamp(integer7, 0, 255);
                if (integer7 > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), 0.0f);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(4.0f, 4.0f, 4.0f);
                    final int integer6 = integer7 << 24 & 0xFF000000;
                    final int integer8 = cyu3.width(this.title);
                    this.drawBackdrop(cyu3, -10, integer8);
                    cyu3.drawShadow(this.title, (float)(-integer8 / 2), -10.0f, 0xFFFFFF | integer6);
                    GlStateManager.popMatrix();
                    if (!this.subtitle.isEmpty()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
                        final int integer9 = cyu3.width(this.subtitle);
                        this.drawBackdrop(cyu3, 5, integer9);
                        cyu3.drawShadow(this.subtitle, (float)(-integer9 / 2), 5.0f, 0xFFFFFF | integer6);
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
                this.minecraft.getProfiler().pop();
            }
            this.subtitleOverlay.render();
            final Scoreboard cti5 = this.minecraft.level.getScoreboard();
            Objective ctf6 = null;
            final PlayerTeam ctg7 = cti5.getPlayersTeam(this.minecraft.player.getScoreboardName());
            if (ctg7 != null) {
                final int integer8 = ctg7.getColor().getId();
                if (integer8 >= 0) {
                    ctf6 = cti5.getDisplayObjective(3 + integer8);
                }
            }
            Objective ctf7 = (ctf6 != null) ? ctf6 : cti5.getDisplayObjective(1);
            if (ctf7 != null) {
                this.displayScoreboardSidebar(ctf7);
            }
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlphaTest();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, (float)(this.screenHeight - 48), 0.0f);
            this.minecraft.getProfiler().push("chat");
            this.chat.render(this.tickCount);
            this.minecraft.getProfiler().pop();
            GlStateManager.popMatrix();
            ctf7 = cti5.getDisplayObjective(0);
            if (this.minecraft.options.keyPlayerList.isDown() && (!this.minecraft.isLocalServer() || this.minecraft.player.connection.getOnlinePlayers().size() > 1 || ctf7 != null)) {
                this.tabList.setVisible(true);
                this.tabList.render(this.screenWidth, cti5, ctf7);
            }
            else {
                this.tabList.setVisible(false);
            }
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlphaTest();
    }
    
    private void drawBackdrop(final Font cyu, final int integer2, final int integer3) {
        final int integer4 = this.minecraft.options.getBackgroundColor(0.0f);
        if (integer4 != 0) {
            final int integer5 = -integer3 / 2;
            final int integer6 = integer5 - 2;
            final int integer7 = integer2 - 2;
            final int integer8 = integer5 + integer3 + 2;
            cyu.getClass();
            GuiComponent.fill(integer6, integer7, integer8, integer2 + 9 + 2, integer4);
        }
    }
    
    private void renderCrosshair() {
        final Options cyg2 = this.minecraft.options;
        if (cyg2.thirdPersonView != 0) {
            return;
        }
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            return;
        }
        if (cyg2.renderDebug && !cyg2.hideGui && !this.minecraft.player.isReducedDebugInfo() && !cyg2.reducedDebugInfo) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), (float)this.blitOffset);
            final Camera cxq3 = this.minecraft.gameRenderer.getMainCamera();
            GlStateManager.rotatef(cxq3.getXRot(), -1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(cxq3.getYRot(), 0.0f, 1.0f, 0.0f);
            GlStateManager.scalef(-1.0f, -1.0f, -1.0f);
            GLX.renderCrosshair(10);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final int integer3 = 15;
            this.blit((this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
            if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                final float float4 = this.minecraft.player.getAttackStrengthScale(0.0f);
                boolean boolean5 = false;
                if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && float4 >= 1.0f) {
                    boolean5 = (this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f);
                    boolean5 &= this.minecraft.crosshairPickEntity.isAlive();
                }
                final int integer4 = this.screenHeight / 2 - 7 + 16;
                final int integer5 = this.screenWidth / 2 - 8;
                if (boolean5) {
                    this.blit(integer5, integer4, 68, 94, 16, 16);
                }
                else if (float4 < 1.0f) {
                    final int integer6 = (int)(float4 * 17.0f);
                    this.blit(integer5, integer4, 36, 94, 16, 4);
                    this.blit(integer5, integer4, 52, 94, integer6, 4);
                }
            }
        }
    }
    
    private boolean canRenderCrosshairForSpectator(final HitResult csf) {
        if (csf == null) {
            return false;
        }
        if (csf.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)csf).getEntity() instanceof MenuProvider;
        }
        if (csf.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew3 = ((BlockHitResult)csf).getBlockPos();
            final Level bhr4 = this.minecraft.level;
            return bhr4.getBlockState(ew3).getMenuProvider(bhr4, ew3) != null;
        }
        return false;
    }
    
    protected void renderEffects() {
        final Collection<MobEffectInstance> collection2 = this.minecraft.player.getActiveEffects();
        if (collection2.isEmpty()) {
            return;
        }
        GlStateManager.enableBlend();
        int integer3 = 0;
        int integer4 = 0;
        final MobEffectTextureManager dxr5 = this.minecraft.getMobEffectTextures();
        final List<Runnable> list6 = (List<Runnable>)Lists.newArrayListWithExpectedSize(collection2.size());
        this.minecraft.getTextureManager().bind(AbstractContainerScreen.INVENTORY_LOCATION);
        for (final MobEffectInstance aii8 : Ordering.natural().reverse().sortedCopy((Iterable)collection2)) {
            final MobEffect aig9 = aii8.getEffect();
            if (aii8.showIcon()) {
                int integer5 = this.screenWidth;
                int integer6 = 1;
                if (this.minecraft.isDemo()) {
                    integer6 += 15;
                }
                if (aig9.isBeneficial()) {
                    ++integer3;
                    integer5 -= 25 * integer3;
                }
                else {
                    ++integer4;
                    integer5 -= 25 * integer4;
                    integer6 += 26;
                }
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float float12 = 1.0f;
                if (aii8.isAmbient()) {
                    this.blit(integer5, integer6, 165, 166, 24, 24);
                }
                else {
                    this.blit(integer5, integer6, 141, 166, 24, 24);
                    if (aii8.getDuration() <= 200) {
                        final int integer7 = 10 - aii8.getDuration() / 20;
                        float12 = Mth.clamp(aii8.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + Mth.cos(aii8.getDuration() * 3.1415927f / 5.0f) * Mth.clamp(integer7 / 10.0f * 0.25f, 0.0f, 0.25f);
                    }
                }
                final TextureAtlasSprite dxb13 = dxr5.get(aig9);
                final int integer8 = integer5;
                final int integer9 = integer6;
                final float float13 = float12;
                list6.add((() -> {
                    GlStateManager.color4f(1.0f, 1.0f, 1.0f, float13);
                    GuiComponent.blit(integer8 + 3, integer9 + 3, this.blitOffset, 18, 18, dxb13);
                }));
            }
        }
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_MOB_EFFECTS);
        list6.forEach(Runnable::run);
    }
    
    protected void renderHotbar(final float float1) {
        final Player awg3 = this.getCameraPlayer();
        if (awg3 == null) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(Gui.WIDGETS_LOCATION);
        final ItemStack bcj4 = awg3.getOffhandItem();
        final HumanoidArm aiw5 = awg3.getMainArm().getOpposite();
        final int integer6 = this.screenWidth / 2;
        final int integer7 = this.blitOffset;
        final int integer8 = 182;
        final int integer9 = 91;
        this.blitOffset = -90;
        this.blit(integer6 - 91, this.screenHeight - 22, 0, 0, 182, 22);
        this.blit(integer6 - 91 - 1 + awg3.inventory.selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);
        if (!bcj4.isEmpty()) {
            if (aiw5 == HumanoidArm.LEFT) {
                this.blit(integer6 - 91 - 29, this.screenHeight - 23, 24, 22, 29, 24);
            }
            else {
                this.blit(integer6 + 91, this.screenHeight - 23, 53, 22, 29, 24);
            }
        }
        this.blitOffset = integer7;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Lighting.turnOnGui();
        for (int integer10 = 0; integer10 < 9; ++integer10) {
            final int integer11 = integer6 - 90 + integer10 * 20 + 2;
            final int integer12 = this.screenHeight - 16 - 3;
            this.renderSlot(integer11, integer12, float1, awg3, awg3.inventory.items.get(integer10));
        }
        if (!bcj4.isEmpty()) {
            final int integer10 = this.screenHeight - 16 - 3;
            if (aiw5 == HumanoidArm.LEFT) {
                this.renderSlot(integer6 - 91 - 26, integer10, float1, awg3, bcj4);
            }
            else {
                this.renderSlot(integer6 + 91 + 10, integer10, float1, awg3, bcj4);
            }
        }
        if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
            final float float2 = this.minecraft.player.getAttackStrengthScale(0.0f);
            if (float2 < 1.0f) {
                final int integer11 = this.screenHeight - 20;
                int integer12 = integer6 + 91 + 6;
                if (aiw5 == HumanoidArm.RIGHT) {
                    integer12 = integer6 - 91 - 22;
                }
                this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
                final int integer13 = (int)(float2 * 19.0f);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                this.blit(integer12, integer11, 0, 94, 18, 18);
                this.blit(integer12, integer11 + 18 - integer13, 18, 112 - integer13, 18, integer13);
            }
        }
        Lighting.turnOff();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
    
    public void renderJumpMeter(final int integer) {
        this.minecraft.getProfiler().push("jumpBar");
        this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
        final float float3 = this.minecraft.player.getJumpRidingScale();
        final int integer2 = 182;
        final int integer3 = (int)(float3 * 183.0f);
        final int integer4 = this.screenHeight - 32 + 3;
        this.blit(integer, integer4, 0, 84, 182, 5);
        if (integer3 > 0) {
            this.blit(integer, integer4, 0, 89, integer3, 5);
        }
        this.minecraft.getProfiler().pop();
    }
    
    public void renderExperienceBar(final int integer) {
        this.minecraft.getProfiler().push("expBar");
        this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
        final int integer2 = this.minecraft.player.getXpNeededForNextLevel();
        if (integer2 > 0) {
            final int integer3 = 182;
            final int integer4 = (int)(this.minecraft.player.experienceProgress * 183.0f);
            final int integer5 = this.screenHeight - 32 + 3;
            this.blit(integer, integer5, 0, 64, 182, 5);
            if (integer4 > 0) {
                this.blit(integer, integer5, 0, 69, integer4, 5);
            }
        }
        this.minecraft.getProfiler().pop();
        if (this.minecraft.player.experienceLevel > 0) {
            this.minecraft.getProfiler().push("expLevel");
            final String string4 = new StringBuilder().append("").append(this.minecraft.player.experienceLevel).toString();
            final int integer4 = (this.screenWidth - this.getFont().width(string4)) / 2;
            final int integer5 = this.screenHeight - 31 - 4;
            this.getFont().draw(string4, (float)(integer4 + 1), (float)integer5, 0);
            this.getFont().draw(string4, (float)(integer4 - 1), (float)integer5, 0);
            this.getFont().draw(string4, (float)integer4, (float)(integer5 + 1), 0);
            this.getFont().draw(string4, (float)integer4, (float)(integer5 - 1), 0);
            this.getFont().draw(string4, (float)integer4, (float)integer5, 8453920);
            this.minecraft.getProfiler().pop();
        }
    }
    
    public void renderSelectedItemName() {
        this.minecraft.getProfiler().push("selectedItemName");
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            final Component jo2 = new TextComponent("").append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color);
            if (this.lastToolHighlight.hasCustomHoverName()) {
                jo2.withStyle(ChatFormatting.ITALIC);
            }
            final String string3 = jo2.getColoredString();
            final int integer4 = (this.screenWidth - this.getFont().width(string3)) / 2;
            int integer5 = this.screenHeight - 59;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                integer5 += 14;
            }
            int integer6 = (int)(this.toolHighlightTimer * 256.0f / 10.0f);
            if (integer6 > 255) {
                integer6 = 255;
            }
            if (integer6 > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                final int integer7 = integer4 - 2;
                final int integer8 = integer5 - 2;
                final int integer9 = integer4 + this.getFont().width(string3) + 2;
                final int n = integer5;
                this.getFont().getClass();
                GuiComponent.fill(integer7, integer8, integer9, n + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                this.getFont().drawShadow(string3, (float)integer4, (float)integer5, 16777215 + (integer6 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.minecraft.getProfiler().pop();
    }
    
    public void renderDemoOverlay() {
        this.minecraft.getProfiler().push("demo");
        String string2;
        if (this.minecraft.level.getGameTime() >= 120500L) {
            string2 = I18n.get("demo.demoExpired");
        }
        else {
            string2 = I18n.get("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
        }
        final int integer3 = this.getFont().width(string2);
        this.getFont().drawShadow(string2, (float)(this.screenWidth - integer3 - 10), 5.0f, 16777215);
        this.minecraft.getProfiler().pop();
    }
    
    private void displayScoreboardSidebar(final Objective ctf) {
        final Scoreboard cti3 = ctf.getScoreboard();
        Collection<Score> collection4 = cti3.getPlayerScores(ctf);
        final List<Score> list5 = (List<Score>)collection4.stream().filter(cth -> cth.getOwner() != null && !cth.getOwner().startsWith("#")).collect(Collectors.toList());
        if (list5.size() > 15) {
            collection4 = (Collection<Score>)Lists.newArrayList(Iterables.skip((Iterable)list5, collection4.size() - 15));
        }
        else {
            collection4 = (Collection<Score>)list5;
        }
        final String string6 = ctf.getDisplayName().getColoredString();
        int integer8;
        final int integer7 = integer8 = this.getFont().width(string6);
        for (final Score cth10 : collection4) {
            final PlayerTeam ctg11 = cti3.getPlayersTeam(cth10.getOwner());
            final String string7 = PlayerTeam.formatNameForTeam(ctg11, new TextComponent(cth10.getOwner())).getColoredString() + ": " + ChatFormatting.RED + cth10.getScore();
            integer8 = Math.max(integer8, this.getFont().width(string7));
        }
        final int size = collection4.size();
        this.getFont().getClass();
        final int integer9 = size * 9;
        final int integer10 = this.screenHeight / 2 + integer9 / 3;
        final int integer11 = 3;
        final int integer12 = this.screenWidth - integer8 - 3;
        int integer13 = 0;
        final int integer14 = this.minecraft.options.getBackgroundColor(0.3f);
        final int integer15 = this.minecraft.options.getBackgroundColor(0.4f);
        for (final Score cth11 : collection4) {
            ++integer13;
            final PlayerTeam ctg12 = cti3.getPlayersTeam(cth11.getOwner());
            final String string8 = PlayerTeam.formatNameForTeam(ctg12, new TextComponent(cth11.getOwner())).getColoredString();
            final String string9 = new StringBuilder().append(ChatFormatting.RED).append("").append(cth11.getScore()).toString();
            final int integer16 = integer12;
            final int n = integer10;
            final int n2 = integer13;
            this.getFont().getClass();
            final int integer17 = n - n2 * 9;
            final int integer18 = this.screenWidth - 3 + 2;
            final int integer19 = integer16 - 2;
            final int integer20 = integer17;
            final int integer21 = integer18;
            final int n3 = integer17;
            this.getFont().getClass();
            GuiComponent.fill(integer19, integer20, integer21, n3 + 9, integer14);
            this.getFont().draw(string8, (float)integer16, (float)integer17, 553648127);
            this.getFont().draw(string9, (float)(integer18 - this.getFont().width(string9)), (float)integer17, 553648127);
            if (integer13 == collection4.size()) {
                final int integer22 = integer16 - 2;
                final int n4 = integer17;
                this.getFont().getClass();
                GuiComponent.fill(integer22, n4 - 9 - 1, integer18, integer17 - 1, integer15);
                GuiComponent.fill(integer16 - 2, integer17 - 1, integer18, integer17, integer14);
                final Font font = this.getFont();
                final String string10 = string6;
                final float float2 = (float)(integer16 + integer8 / 2 - integer7 / 2);
                final int n5 = integer17;
                this.getFont().getClass();
                font.draw(string10, float2, (float)(n5 - 9), 553648127);
            }
        }
    }
    
    private Player getCameraPlayer() {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return null;
        }
        return (Player)this.minecraft.getCameraEntity();
    }
    
    private LivingEntity getPlayerVehicleWithHealth() {
        final Player awg2 = this.getCameraPlayer();
        if (awg2 != null) {
            final Entity aio3 = awg2.getVehicle();
            if (aio3 == null) {
                return null;
            }
            if (aio3 instanceof LivingEntity) {
                return (LivingEntity)aio3;
            }
        }
        return null;
    }
    
    private int getVehicleMaxHearts(final LivingEntity aix) {
        if (aix == null || !aix.showVehicleHealth()) {
            return 0;
        }
        final float float3 = aix.getMaxHealth();
        int integer4 = (int)(float3 + 0.5f) / 2;
        if (integer4 > 30) {
            integer4 = 30;
        }
        return integer4;
    }
    
    private int getVisibleVehicleHeartRows(final int integer) {
        return (int)Math.ceil(integer / 10.0);
    }
    
    private void renderPlayerHealth() {
        final Player awg2 = this.getCameraPlayer();
        if (awg2 == null) {
            return;
        }
        final int integer3 = Mth.ceil(awg2.getHealth());
        final boolean boolean4 = this.healthBlinkTime > this.tickCount && (this.healthBlinkTime - this.tickCount) / 3L % 2L == 1L;
        final long long5 = Util.getMillis();
        if (integer3 < this.lastHealth && awg2.invulnerableTime > 0) {
            this.lastHealthTime = long5;
            this.healthBlinkTime = this.tickCount + 20;
        }
        else if (integer3 > this.lastHealth && awg2.invulnerableTime > 0) {
            this.lastHealthTime = long5;
            this.healthBlinkTime = this.tickCount + 10;
        }
        if (long5 - this.lastHealthTime > 1000L) {
            this.lastHealth = integer3;
            this.displayHealth = integer3;
            this.lastHealthTime = long5;
        }
        this.lastHealth = integer3;
        final int integer4 = this.displayHealth;
        this.random.setSeed((long)(this.tickCount * 312871));
        final FoodData ayg8 = awg2.getFoodData();
        final int integer5 = ayg8.getFoodLevel();
        final AttributeInstance ajo10 = awg2.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        final int integer6 = this.screenWidth / 2 - 91;
        final int integer7 = this.screenWidth / 2 + 91;
        final int integer8 = this.screenHeight - 39;
        final float float14 = (float)ajo10.getValue();
        final int integer9 = Mth.ceil(awg2.getAbsorptionAmount());
        final int integer10 = Mth.ceil((float14 + integer9) / 2.0f / 10.0f);
        final int integer11 = Math.max(10 - (integer10 - 2), 3);
        final int integer12 = integer8 - (integer10 - 1) * integer11 - 10;
        int integer13 = integer8 - 10;
        int integer14 = integer9;
        final int integer15 = awg2.getArmorValue();
        int integer16 = -1;
        if (awg2.hasEffect(MobEffects.REGENERATION)) {
            integer16 = this.tickCount % Mth.ceil(float14 + 5.0f);
        }
        this.minecraft.getProfiler().push("armor");
        for (int integer17 = 0; integer17 < 10; ++integer17) {
            if (integer15 > 0) {
                final int integer18 = integer6 + integer17 * 8;
                if (integer17 * 2 + 1 < integer15) {
                    this.blit(integer18, integer12, 34, 9, 9, 9);
                }
                if (integer17 * 2 + 1 == integer15) {
                    this.blit(integer18, integer12, 25, 9, 9, 9);
                }
                if (integer17 * 2 + 1 > integer15) {
                    this.blit(integer18, integer12, 16, 9, 9, 9);
                }
            }
        }
        this.minecraft.getProfiler().popPush("health");
        for (int integer17 = Mth.ceil((float14 + integer9) / 2.0f) - 1; integer17 >= 0; --integer17) {
            int integer18 = 16;
            if (awg2.hasEffect(MobEffects.POISON)) {
                integer18 += 36;
            }
            else if (awg2.hasEffect(MobEffects.WITHER)) {
                integer18 += 72;
            }
            int integer19 = 0;
            if (boolean4) {
                integer19 = 1;
            }
            final int integer20 = Mth.ceil((integer17 + 1) / 10.0f) - 1;
            final int integer21 = integer6 + integer17 % 10 * 8;
            int integer22 = integer8 - integer20 * integer11;
            if (integer3 <= 4) {
                integer22 += this.random.nextInt(2);
            }
            if (integer14 <= 0 && integer17 == integer16) {
                integer22 -= 2;
            }
            int integer23 = 0;
            if (awg2.level.getLevelData().isHardcore()) {
                integer23 = 5;
            }
            this.blit(integer21, integer22, 16 + integer19 * 9, 9 * integer23, 9, 9);
            if (boolean4) {
                if (integer17 * 2 + 1 < integer4) {
                    this.blit(integer21, integer22, integer18 + 54, 9 * integer23, 9, 9);
                }
                if (integer17 * 2 + 1 == integer4) {
                    this.blit(integer21, integer22, integer18 + 63, 9 * integer23, 9, 9);
                }
            }
            if (integer14 > 0) {
                if (integer14 == integer9 && integer9 % 2 == 1) {
                    this.blit(integer21, integer22, integer18 + 153, 9 * integer23, 9, 9);
                    --integer14;
                }
                else {
                    this.blit(integer21, integer22, integer18 + 144, 9 * integer23, 9, 9);
                    integer14 -= 2;
                }
            }
            else {
                if (integer17 * 2 + 1 < integer3) {
                    this.blit(integer21, integer22, integer18 + 36, 9 * integer23, 9, 9);
                }
                if (integer17 * 2 + 1 == integer3) {
                    this.blit(integer21, integer22, integer18 + 45, 9 * integer23, 9, 9);
                }
            }
        }
        final LivingEntity aix23 = this.getPlayerVehicleWithHealth();
        int integer18 = this.getVehicleMaxHearts(aix23);
        if (integer18 == 0) {
            this.minecraft.getProfiler().popPush("food");
            for (int integer19 = 0; integer19 < 10; ++integer19) {
                int integer20 = integer8;
                int integer21 = 16;
                int integer22 = 0;
                if (awg2.hasEffect(MobEffects.HUNGER)) {
                    integer21 += 36;
                    integer22 = 13;
                }
                if (awg2.getFoodData().getSaturationLevel() <= 0.0f && this.tickCount % (integer5 * 3 + 1) == 0) {
                    integer20 += this.random.nextInt(3) - 1;
                }
                final int integer23 = integer7 - integer19 * 8 - 9;
                this.blit(integer23, integer20, 16 + integer22 * 9, 27, 9, 9);
                if (integer19 * 2 + 1 < integer5) {
                    this.blit(integer23, integer20, integer21 + 36, 27, 9, 9);
                }
                if (integer19 * 2 + 1 == integer5) {
                    this.blit(integer23, integer20, integer21 + 45, 27, 9, 9);
                }
            }
            integer13 -= 10;
        }
        this.minecraft.getProfiler().popPush("air");
        int integer19 = awg2.getAirSupply();
        int integer20 = awg2.getMaxAirSupply();
        if (awg2.isUnderLiquid(FluidTags.WATER) || integer19 < integer20) {
            final int integer21 = this.getVisibleVehicleHeartRows(integer18) - 1;
            integer13 -= integer21 * 10;
            for (int integer22 = Mth.ceil((integer19 - 2) * 10.0 / integer20), integer23 = Mth.ceil(integer19 * 10.0 / integer20) - integer22, integer24 = 0; integer24 < integer22 + integer23; ++integer24) {
                if (integer24 < integer22) {
                    this.blit(integer7 - integer24 * 8 - 9, integer13, 16, 18, 9, 9);
                }
                else {
                    this.blit(integer7 - integer24 * 8 - 9, integer13, 25, 18, 9, 9);
                }
            }
        }
        this.minecraft.getProfiler().pop();
    }
    
    private void renderVehicleHealth() {
        final LivingEntity aix2 = this.getPlayerVehicleWithHealth();
        if (aix2 == null) {
            return;
        }
        int integer3 = this.getVehicleMaxHearts(aix2);
        if (integer3 == 0) {
            return;
        }
        final int integer4 = (int)Math.ceil((double)aix2.getHealth());
        this.minecraft.getProfiler().popPush("mountHealth");
        final int integer5 = this.screenHeight - 39;
        final int integer6 = this.screenWidth / 2 + 91;
        int integer7 = integer5;
        int integer8 = 0;
        final boolean boolean9 = false;
        while (integer3 > 0) {
            final int integer9 = Math.min(integer3, 10);
            integer3 -= integer9;
            for (int integer10 = 0; integer10 < integer9; ++integer10) {
                final int integer11 = 52;
                final int integer12 = 0;
                final int integer13 = integer6 - integer10 * 8 - 9;
                this.blit(integer13, integer7, 52 + integer12 * 9, 9, 9, 9);
                if (integer10 * 2 + 1 + integer8 < integer4) {
                    this.blit(integer13, integer7, 88, 9, 9, 9);
                }
                if (integer10 * 2 + 1 + integer8 == integer4) {
                    this.blit(integer13, integer7, 97, 9, 9, 9);
                }
            }
            integer7 -= 10;
            integer8 += 20;
        }
    }
    
    private void renderPumpkin() {
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlphaTest();
        this.minecraft.getTextureManager().bind(Gui.PUMPKIN_BLUR_LOCATION);
        final Tesselator cuz2 = Tesselator.getInstance();
        final BufferBuilder cuw3 = cuz2.getBuilder();
        cuw3.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw3.vertex(0.0, this.screenHeight, -90.0).uv(0.0, 1.0).endVertex();
        cuw3.vertex(this.screenWidth, this.screenHeight, -90.0).uv(1.0, 1.0).endVertex();
        cuw3.vertex(this.screenWidth, 0.0, -90.0).uv(1.0, 0.0).endVertex();
        cuw3.vertex(0.0, 0.0, -90.0).uv(0.0, 0.0).endVertex();
        cuz2.end();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void updateVignetteBrightness(final Entity aio) {
        if (aio == null) {
            return;
        }
        final float float3 = Mth.clamp(1.0f - aio.getBrightness(), 0.0f, 1.0f);
        this.vignetteBrightness += (float)((float3 - this.vignetteBrightness) * 0.01);
    }
    
    private void renderVignette(final Entity aio) {
        final WorldBorder bxf3 = this.minecraft.level.getWorldBorder();
        float float4 = (float)bxf3.getDistanceToBorder(aio);
        final double double5 = Math.min(bxf3.getLerpSpeed() * bxf3.getWarningTime() * 1000.0, Math.abs(bxf3.getLerpTarget() - bxf3.getSize()));
        final double double6 = Math.max((double)bxf3.getWarningBlocks(), double5);
        if (float4 < double6) {
            float4 = 1.0f - (float)(float4 / double6);
        }
        else {
            float4 = 0.0f;
        }
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (float4 > 0.0f) {
            GlStateManager.color4f(0.0f, float4, float4, 1.0f);
        }
        else {
            GlStateManager.color4f(this.vignetteBrightness, this.vignetteBrightness, this.vignetteBrightness, 1.0f);
        }
        this.minecraft.getTextureManager().bind(Gui.VIGNETTE_LOCATION);
        final Tesselator cuz9 = Tesselator.getInstance();
        final BufferBuilder cuw10 = cuz9.getBuilder();
        cuw10.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw10.vertex(0.0, this.screenHeight, -90.0).uv(0.0, 1.0).endVertex();
        cuw10.vertex(this.screenWidth, this.screenHeight, -90.0).uv(1.0, 1.0).endVertex();
        cuw10.vertex(this.screenWidth, 0.0, -90.0).uv(1.0, 0.0).endVertex();
        cuw10.vertex(0.0, 0.0, -90.0).uv(0.0, 0.0).endVertex();
        cuz9.end();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }
    
    private void renderPortalOverlay(float float1) {
        if (float1 < 1.0f) {
            float1 *= float1;
            float1 *= float1;
            float1 = float1 * 0.8f + 0.2f;
        }
        GlStateManager.disableAlphaTest();
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float1);
        this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        final TextureAtlasSprite dxb3 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        final float float2 = dxb3.getU0();
        final float float3 = dxb3.getV0();
        final float float4 = dxb3.getU1();
        final float float5 = dxb3.getV1();
        final Tesselator cuz8 = Tesselator.getInstance();
        final BufferBuilder cuw9 = cuz8.getBuilder();
        cuw9.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw9.vertex(0.0, this.screenHeight, -90.0).uv(float2, float5).endVertex();
        cuw9.vertex(this.screenWidth, this.screenHeight, -90.0).uv(float4, float5).endVertex();
        cuw9.vertex(this.screenWidth, 0.0, -90.0).uv(float4, float3).endVertex();
        cuw9.vertex(0.0, 0.0, -90.0).uv(float2, float3).endVertex();
        cuz8.end();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderSlot(final int integer1, final int integer2, final float float3, final Player awg, final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return;
        }
        final float float4 = bcj.getPopTime() - float3;
        if (float4 > 0.0f) {
            GlStateManager.pushMatrix();
            final float float5 = 1.0f + float4 / 5.0f;
            GlStateManager.translatef((float)(integer1 + 8), (float)(integer2 + 12), 0.0f);
            GlStateManager.scalef(1.0f / float5, (float5 + 1.0f) / 2.0f, 1.0f);
            GlStateManager.translatef((float)(-(integer1 + 8)), (float)(-(integer2 + 12)), 0.0f);
        }
        this.itemRenderer.renderAndDecorateItem(awg, bcj, integer1, integer2);
        if (float4 > 0.0f) {
            GlStateManager.popMatrix();
        }
        this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, bcj, integer1, integer2);
    }
    
    public void tick() {
        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }
        if (this.titleTime > 0) {
            --this.titleTime;
            if (this.titleTime <= 0) {
                this.title = "";
                this.subtitle = "";
            }
        }
        ++this.tickCount;
        final Entity aio2 = this.minecraft.getCameraEntity();
        if (aio2 != null) {
            this.updateVignetteBrightness(aio2);
        }
        if (this.minecraft.player != null) {
            final ItemStack bcj3 = this.minecraft.player.inventory.getSelected();
            if (bcj3.isEmpty()) {
                this.toolHighlightTimer = 0;
            }
            else if (this.lastToolHighlight.isEmpty() || bcj3.getItem() != this.lastToolHighlight.getItem() || !bcj3.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
                this.toolHighlightTimer = 40;
            }
            else if (this.toolHighlightTimer > 0) {
                --this.toolHighlightTimer;
            }
            this.lastToolHighlight = bcj3;
        }
    }
    
    public void setNowPlaying(final String string) {
        this.setOverlayMessage(I18n.get("record.nowPlaying", string), true);
    }
    
    public void setOverlayMessage(final String string, final boolean boolean2) {
        this.overlayMessageString = string;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = boolean2;
    }
    
    public void setTitles(final String string1, final String string2, final int integer3, final int integer4, final int integer5) {
        if (string1 == null && string2 == null && integer3 < 0 && integer4 < 0 && integer5 < 0) {
            this.title = "";
            this.subtitle = "";
            this.titleTime = 0;
            return;
        }
        if (string1 != null) {
            this.title = string1;
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
            return;
        }
        if (string2 != null) {
            this.subtitle = string2;
            return;
        }
        if (integer3 >= 0) {
            this.titleFadeInTime = integer3;
        }
        if (integer4 >= 0) {
            this.titleStayTime = integer4;
        }
        if (integer5 >= 0) {
            this.titleFadeOutTime = integer5;
        }
        if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
        }
    }
    
    public void setOverlayMessage(final Component jo, final boolean boolean2) {
        this.setOverlayMessage(jo.getString(), boolean2);
    }
    
    public void handleChat(final ChatType jm, final Component jo) {
        for (final ChatListener cyy5 : (List)this.chatListeners.get(jm)) {
            cyy5.handle(jm, jo);
        }
    }
    
    public ChatComponent getChat() {
        return this.chat;
    }
    
    public int getGuiTicks() {
        return this.tickCount;
    }
    
    public Font getFont() {
        return this.minecraft.font;
    }
    
    public SpectatorGui getSpectatorGui() {
        return this.spectatorGui;
    }
    
    public PlayerTabOverlay getTabList() {
        return this.tabList;
    }
    
    public void onDisconnected() {
        this.tabList.reset();
        this.bossOverlay.reset();
        this.minecraft.getToasts().clear();
    }
    
    public BossHealthOverlay getBossOverlay() {
        return this.bossOverlay;
    }
    
    public void clearCache() {
        this.debugScreen.clearChunkCache();
    }
    
    static {
        VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
        PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
    }
}

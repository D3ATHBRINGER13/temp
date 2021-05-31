package net.minecraft.client.gui.screens;

import net.minecraft.util.StringUtil;
import com.google.common.util.concurrent.Runnables;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.SharedConstants;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.language.I18n;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.platform.GLX;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.gui.components.Button;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.CubeMap;

public class TitleScreen extends Screen {
    public static final CubeMap CUBE_MAP;
    private static final ResourceLocation PANORAMA_OVERLAY;
    private static final ResourceLocation ACCESSIBILITY_TEXTURE;
    private final boolean minceraftEasterEgg;
    @Nullable
    private String splash;
    private Button resetDemoButton;
    @Nullable
    private WarningMessageWidget warningMessage;
    private static final ResourceLocation MINECRAFT_LOGO;
    private static final ResourceLocation MINECRAFT_EDITION;
    private boolean realmsNotificationsInitialized;
    private Screen realmsNotificationsScreen;
    private int copyrightWidth;
    private int copyrightX;
    private final PanoramaRenderer panorama;
    private final boolean fading;
    private long fadeInStart;
    
    public TitleScreen() {
        this(false);
    }
    
    public TitleScreen(final boolean boolean1) {
        super(new TranslatableComponent("narrator.screen.title", new Object[0]));
        this.panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
        this.fading = boolean1;
        this.minceraftEasterEgg = (new Random().nextFloat() < 1.0E-4);
        if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
            this.warningMessage = new WarningMessageWidget(new TranslatableComponent("title.oldgl.eol.line1", new Object[0]).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), new TranslatableComponent("title.oldgl.eol.line2", new Object[0]).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), "https://help.mojang.com/customer/portal/articles/325948?ref=game");
        }
    }
    
    private boolean realmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen != null;
    }
    
    @Override
    public void tick() {
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.tick();
        }
    }
    
    public static CompletableFuture<Void> preloadResources(final TextureManager dxc, final Executor executor) {
        return (CompletableFuture<Void>)CompletableFuture.allOf(new CompletableFuture[] { dxc.preload(TitleScreen.MINECRAFT_LOGO, executor), dxc.preload(TitleScreen.MINECRAFT_EDITION, executor), dxc.preload(TitleScreen.PANORAMA_OVERLAY, executor), TitleScreen.CUBE_MAP.preload(dxc, executor) });
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        if (this.splash == null) {
            this.splash = this.minecraft.getSplashManager().getSplash();
        }
        this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
        this.copyrightX = this.width - this.copyrightWidth - 2;
        final int integer2 = 24;
        final int integer3 = this.height / 4 + 48;
        if (this.minecraft.isDemo()) {
            this.createDemoMenuOptions(integer3, 24);
        }
        else {
            this.createNormalMenuOptions(integer3, 24);
        }
        this.<ImageButton>addButton(new ImageButton(this.width / 2 - 124, integer3 + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, czi -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), I18n.get("narrator.button.language")));
        this.<Button>addButton(new Button(this.width / 2 - 100, integer3 + 72 + 12, 98, 20, I18n.get("menu.options"), czi -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));
        this.<Button>addButton(new Button(this.width / 2 + 2, integer3 + 72 + 12, 98, 20, I18n.get("menu.quit"), czi -> this.minecraft.stop()));
        this.<ImageButton>addButton(new ImageButton(this.width / 2 + 104, integer3 + 72 + 12, 20, 20, 0, 0, 20, TitleScreen.ACCESSIBILITY_TEXTURE, 32, 64, czi -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), I18n.get("narrator.button.accessibility")));
        if (this.warningMessage != null) {
            this.warningMessage.updatePosition(integer3);
        }
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            final RealmsBridge realmsBridge4 = new RealmsBridge();
            this.realmsNotificationsScreen = realmsBridge4.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }
    }
    
    private void createNormalMenuOptions(final int integer1, final int integer2) {
        this.<Button>addButton(new Button(this.width / 2 - 100, integer1, 200, 20, I18n.get("menu.singleplayer"), czi -> this.minecraft.setScreen(new SelectWorldScreen(this))));
        this.<Button>addButton(new Button(this.width / 2 - 100, integer1 + integer2 * 1, 200, 20, I18n.get("menu.multiplayer"), czi -> this.minecraft.setScreen(new JoinMultiplayerScreen(this))));
        this.<Button>addButton(new Button(this.width / 2 - 100, integer1 + integer2 * 2, 200, 20, I18n.get("menu.online"), czi -> this.realmsButtonClicked()));
    }
    
    private void createDemoMenuOptions(final int integer1, final int integer2) {
        this.<Button>addButton(new Button(this.width / 2 - 100, integer1, 200, 20, I18n.get("menu.playdemo"), czi -> this.minecraft.selectLevel("Demo_World", "Demo_World", MinecraftServer.DEMO_SETTINGS)));
        final LevelStorageSource coq3;
        final LevelData com4;
        Minecraft minecraft;
        TranslatableComponent jo2;
        final TranslatableComponent jo3;
        final Screen screen;
        final BooleanConsumer booleanConsumer;
        this.resetDemoButton = this.<Button>addButton(new Button(this.width / 2 - 100, integer1 + integer2 * 1, 200, 20, I18n.get("menu.resetdemo"), czi -> {
            coq3 = this.minecraft.getLevelSource();
            com4 = coq3.getDataTagFor("Demo_World");
            if (com4 != null) {
                minecraft = this.minecraft;
                // new(net.minecraft.client.gui.screens.ConfirmScreen.class)
                this::confirmDemo;
                jo2 = new TranslatableComponent("selectWorld.deleteQuestion", new Object[0]);
                new TranslatableComponent("selectWorld.deleteWarning", new Object[] { com4.getLevelName() });
                new ConfirmScreen(booleanConsumer, jo2, jo3, I18n.get("selectWorld.deleteButton"), I18n.get("gui.cancel"));
                minecraft.setScreen(screen);
            }
            return;
        }));
        final LevelStorageSource coq4 = this.minecraft.getLevelSource();
        final LevelData com5 = coq4.getDataTagFor("Demo_World");
        if (com5 == null) {
            this.resetDemoButton.active = false;
        }
    }
    
    private void realmsButtonClicked() {
        final RealmsBridge realmsBridge2 = new RealmsBridge();
        realmsBridge2.switchToRealms(this);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        final float float4 = this.fading ? ((Util.getMillis() - this.fadeInStart) / 1000.0f) : 1.0f;
        GuiComponent.fill(0, 0, this.width, this.height, -1);
        this.panorama.render(float3, Mth.clamp(float4, 0.0f, 1.0f));
        final int integer3 = 274;
        final int integer4 = this.width / 2 - 137;
        final int integer5 = 30;
        this.minecraft.getTextureManager().bind(TitleScreen.PANORAMA_OVERLAY);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.fading ? ((float)Mth.ceil(Mth.clamp(float4, 0.0f, 1.0f))) : 1.0f);
        GuiComponent.blit(0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        final float float5 = this.fading ? Mth.clamp(float4 - 1.0f, 0.0f, 1.0f) : 1.0f;
        final int integer6 = Mth.ceil(float5 * 255.0f) << 24;
        if ((integer6 & 0xFC000000) == 0x0) {
            return;
        }
        this.minecraft.getTextureManager().bind(TitleScreen.MINECRAFT_LOGO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float5);
        if (this.minceraftEasterEgg) {
            this.blit(integer4 + 0, 30, 0, 0, 99, 44);
            this.blit(integer4 + 99, 30, 129, 0, 27, 44);
            this.blit(integer4 + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(integer4 + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(integer4 + 155, 30, 0, 45, 155, 44);
        }
        else {
            this.blit(integer4 + 0, 30, 0, 0, 155, 44);
            this.blit(integer4 + 155, 30, 0, 45, 155, 44);
        }
        this.minecraft.getTextureManager().bind(TitleScreen.MINECRAFT_EDITION);
        GuiComponent.blit(integer4 + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.splash != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.width / 2 + 90), 70.0f, 0.0f);
            GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            float float6 = 1.8f - Mth.abs(Mth.sin(Util.getMillis() % 1000L / 1000.0f * 6.2831855f) * 0.1f);
            float6 = float6 * 100.0f / (this.font.width(this.splash) + 32);
            GlStateManager.scalef(float6, float6, float6);
            this.drawCenteredString(this.font, this.splash, 0, -8, 0xFFFF00 | integer6);
            GlStateManager.popMatrix();
        }
        String string11 = "Minecraft " + SharedConstants.getCurrentVersion().getName();
        if (this.minecraft.isDemo()) {
            string11 += " Demo";
        }
        else {
            string11 += ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : ("/" + this.minecraft.getVersionType()));
        }
        this.drawString(this.font, string11, 2, this.height - 10, 0xFFFFFF | integer6);
        this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX, this.height - 10, 0xFFFFFF | integer6);
        if (integer1 > this.copyrightX && integer1 < this.copyrightX + this.copyrightWidth && integer2 > this.height - 10 && integer2 < this.height) {
            GuiComponent.fill(this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height, 0xFFFFFF | integer6);
        }
        if (this.warningMessage != null) {
            this.warningMessage.render(integer6);
        }
        for (final AbstractWidget czg13 : this.buttons) {
            czg13.setAlpha(float5);
        }
        super.render(integer1, integer2, float3);
        if (this.realmsNotificationsEnabled() && float5 >= 1.0f) {
            this.realmsNotificationsScreen.render(integer1, integer2, float3);
        }
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (super.mouseClicked(double1, double2, integer)) {
            return true;
        }
        if (this.warningMessage != null && this.warningMessage.mouseClicked(double1, double2)) {
            return true;
        }
        if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(double1, double2, integer)) {
            return true;
        }
        if (double1 > this.copyrightX && double1 < this.copyrightX + this.copyrightWidth && double2 > this.height - 10 && double2 < this.height) {
            this.minecraft.setScreen(new WinScreen(false, Runnables.doNothing()));
        }
        return false;
    }
    
    @Override
    public void removed() {
        if (this.realmsNotificationsScreen != null) {
            this.realmsNotificationsScreen.removed();
        }
    }
    
    private void confirmDemo(final boolean boolean1) {
        if (boolean1) {
            final LevelStorageSource coq3 = this.minecraft.getLevelSource();
            coq3.deleteLevel("Demo_World");
        }
        this.minecraft.setScreen(this);
    }
    
    static {
        CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
        PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
        ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
        MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
        MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    }
    
    class WarningMessageWidget {
        private int warningClickWidth;
        private int warningx0;
        private int warningy0;
        private int warningx1;
        private int warningy1;
        private final Component warningMessageTop;
        private final Component warningMessageBottom;
        private final String warningMessageUrl;
        
        public WarningMessageWidget(final Component jo2, final Component jo3, final String string) {
            this.warningMessageTop = jo2;
            this.warningMessageBottom = jo3;
            this.warningMessageUrl = string;
        }
        
        public void updatePosition(final int integer) {
            final int integer2 = TitleScreen.this.font.width(this.warningMessageTop.getString());
            this.warningClickWidth = TitleScreen.this.font.width(this.warningMessageBottom.getString());
            final int integer3 = Math.max(integer2, this.warningClickWidth);
            this.warningx0 = (TitleScreen.this.width - integer3) / 2;
            this.warningy0 = integer - 24;
            this.warningx1 = this.warningx0 + integer3;
            this.warningy1 = this.warningy0 + 24;
        }
        
        public void render(final int integer) {
            GuiComponent.fill(this.warningx0 - 2, this.warningy0 - 2, this.warningx1 + 2, this.warningy1 - 1, 1428160512);
            TitleScreen.this.drawString(TitleScreen.this.font, this.warningMessageTop.getColoredString(), this.warningx0, this.warningy0, 0xFFFFFF | integer);
            TitleScreen.this.drawString(TitleScreen.this.font, this.warningMessageBottom.getColoredString(), (TitleScreen.this.width - this.warningClickWidth) / 2, this.warningy0 + 12, 0xFFFFFF | integer);
        }
        
        public boolean mouseClicked(final double double1, final double double2) {
            if (!StringUtil.isNullOrEmpty(this.warningMessageUrl) && double1 >= this.warningx0 && double1 <= this.warningx1 && double2 >= this.warningy0 && double2 <= this.warningy1) {
                TitleScreen.this.minecraft.setScreen(new ConfirmLinkScreen(boolean1 -> {
                    if (boolean1) {
                        Util.getPlatform().openUri(this.warningMessageUrl);
                    }
                    TitleScreen.this.minecraft.setScreen(TitleScreen.this);
                }, this.warningMessageUrl, true));
                return true;
            }
            return false;
        }
    }
}

package net.minecraft.client.gui.screens.worldselection;

import java.io.InputStream;
import net.minecraft.client.renderer.texture.TextureObject;
import org.apache.commons.lang3.Validate;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.FileInputStream;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.ChatFormatting;
import org.apache.commons.lang3.StringUtils;
import com.google.common.hash.Hashing;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.components.AbstractSelectionList;
import java.util.Optional;
import net.minecraft.client.resources.language.I18n;
import java.util.Date;
import net.minecraft.client.gui.chat.NarratorChatListener;
import java.util.Iterator;
import net.minecraft.world.level.storage.LevelStorageSource;
import java.util.Locale;
import java.util.Collections;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.LevelSummary;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import java.text.DateFormat;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class WorldSelectionList extends ObjectSelectionList<WorldListEntry> {
    private static final Logger LOGGER;
    private static final DateFormat DATE_FORMAT;
    private static final ResourceLocation ICON_MISSING;
    private static final ResourceLocation ICON_OVERLAY_LOCATION;
    private final SelectWorldScreen screen;
    @Nullable
    private List<LevelSummary> cachedList;
    
    public WorldSelectionList(final SelectWorldScreen dft, final Minecraft cyc, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Supplier<String> supplier, @Nullable final WorldSelectionList dfu) {
        super(cyc, integer3, integer4, integer5, integer6, integer7);
        this.screen = dft;
        if (dfu != null) {
            this.cachedList = dfu.cachedList;
        }
        this.refreshList(supplier, false);
    }
    
    public void refreshList(final Supplier<String> supplier, final boolean boolean2) {
        this.clearEntries();
        final LevelStorageSource coq4 = this.minecraft.getLevelSource();
        Label_0088: {
            if (this.cachedList != null) {
                if (!boolean2) {
                    break Label_0088;
                }
            }
            try {
                this.cachedList = coq4.getLevelList();
            }
            catch (LevelStorageException cop5) {
                WorldSelectionList.LOGGER.error("Couldn't load level list", (Throwable)cop5);
                this.minecraft.setScreen(new ErrorScreen(new TranslatableComponent("selectWorld.unable_to_load", new Object[0]), cop5.getMessage()));
                return;
            }
            Collections.sort((List)this.cachedList);
        }
        final String string5 = ((String)supplier.get()).toLowerCase(Locale.ROOT);
        for (final LevelSummary cor7 : this.cachedList) {
            if (cor7.getLevelName().toLowerCase(Locale.ROOT).contains((CharSequence)string5) || cor7.getLevelId().toLowerCase(Locale.ROOT).contains((CharSequence)string5)) {
                this.addEntry(new WorldListEntry(this, cor7, this.minecraft.getLevelSource()));
            }
        }
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }
    
    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }
    
    @Override
    public void setSelected(@Nullable final WorldListEntry a) {
        super.setSelected(a);
        if (a != null) {
            final LevelSummary cor3 = a.summary;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { new TranslatableComponent("narrator.select.world", new Object[] { cor3.getLevelName(), new Date(cor3.getLastPlayed()), cor3.isHardcore() ? I18n.get("gameMode.hardcore") : I18n.get("gameMode." + cor3.getGameMode().getName()), cor3.hasCheats() ? I18n.get("selectWorld.cheats") : "", cor3.getWorldVersionName() }) }).getString());
        }
    }
    
    @Override
    protected void moveSelection(final int integer) {
        super.moveSelection(integer);
        this.screen.updateButtonStatus(true);
    }
    
    public Optional<WorldListEntry> getSelectedOpt() {
        return (Optional<WorldListEntry>)Optional.ofNullable(((AbstractSelectionList<Object>)this).getSelected());
    }
    
    public SelectWorldScreen getScreen() {
        return this.screen;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DATE_FORMAT = (DateFormat)new SimpleDateFormat();
        ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
        ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    }
    
    public final class WorldListEntry extends Entry<WorldListEntry> implements AutoCloseable {
        private final Minecraft minecraft;
        private final SelectWorldScreen screen;
        private final LevelSummary summary;
        private final ResourceLocation iconLocation;
        private File iconFile;
        @Nullable
        private final DynamicTexture icon;
        private long lastClickTime;
        
        public WorldListEntry(final WorldSelectionList dfu2, final LevelSummary cor, final LevelStorageSource coq) {
            this.screen = dfu2.getScreen();
            this.summary = cor;
            this.minecraft = Minecraft.getInstance();
            this.iconLocation = new ResourceLocation(new StringBuilder().append("worlds/").append(Hashing.sha1().hashUnencodedChars((CharSequence)cor.getLevelId())).append("/icon").toString());
            this.iconFile = coq.getFile(cor.getLevelId(), "icon.png");
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.loadServerIcon();
        }
        
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            String string11 = this.summary.getLevelName();
            final String string12 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string11)) {
                string11 = I18n.get("selectWorld.world") + " " + (integer1 + 1);
            }
            String string13 = "";
            if (this.summary.isRequiresConversion()) {
                string13 = I18n.get("selectWorld.conversion") + " " + string13;
            }
            else {
                string13 = I18n.get("gameMode." + this.summary.getGameMode().getName());
                if (this.summary.isHardcore()) {
                    string13 = ChatFormatting.DARK_RED + I18n.get("gameMode.hardcore") + ChatFormatting.RESET;
                }
                if (this.summary.hasCheats()) {
                    string13 = string13 + ", " + I18n.get("selectWorld.cheats");
                }
                final String string14 = this.summary.getWorldVersionName().getColoredString();
                if (this.summary.markVersionInList()) {
                    if (this.summary.askToOpenWorld()) {
                        string13 = string13 + ", " + I18n.get("selectWorld.version") + " " + ChatFormatting.RED + string14 + ChatFormatting.RESET;
                    }
                    else {
                        string13 = string13 + ", " + I18n.get("selectWorld.version") + " " + ChatFormatting.ITALIC + string14 + ChatFormatting.RESET;
                    }
                }
                else {
                    string13 = string13 + ", " + I18n.get("selectWorld.version") + " " + string14;
                }
            }
            this.minecraft.font.draw(string11, (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            final Font font = this.minecraft.font;
            final String string15 = string12;
            final float float10 = (float)(integer3 + 32 + 3);
            this.minecraft.font.getClass();
            font.draw(string15, float10, (float)(integer2 + 9 + 3), 8421504);
            final Font font2 = this.minecraft.font;
            final String string16 = string13;
            final float float11 = (float)(integer3 + 32 + 3);
            this.minecraft.font.getClass();
            final int n = integer2 + 9;
            this.minecraft.font.getClass();
            font2.draw(string16, float11, (float)(n + 9 + 3), 8421504);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind((this.icon != null) ? this.iconLocation : WorldSelectionList.ICON_MISSING);
            GlStateManager.enableBlend();
            GuiComponent.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
            if (this.minecraft.options.touchscreen || boolean8) {
                this.minecraft.getTextureManager().bind(WorldSelectionList.ICON_OVERLAY_LOCATION);
                GuiComponent.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer8 = integer6 - integer3;
                final int integer9 = (integer8 < 32) ? 32 : 0;
                if (this.summary.markVersionInList()) {
                    GuiComponent.blit(integer3, integer2, 32.0f, (float)integer9, 32, 32, 256, 256);
                    if (this.summary.isOldCustomizedWorld()) {
                        GuiComponent.blit(integer3, integer2, 96.0f, (float)integer9, 32, 32, 256, 256);
                        if (integer8 < 32) {
                            final Component jo16 = new TranslatableComponent("selectWorld.tooltip.unsupported", new Object[] { this.summary.getWorldVersionName() }).withStyle(ChatFormatting.RED);
                            this.screen.setToolTip(this.minecraft.font.insertLineBreaks(jo16.getColoredString(), 175));
                        }
                    }
                    else if (this.summary.askToOpenWorld()) {
                        GuiComponent.blit(integer3, integer2, 96.0f, (float)integer9, 32, 32, 256, 256);
                        if (integer8 < 32) {
                            this.screen.setToolTip(ChatFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion1") + "\n" + ChatFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion2"));
                        }
                    }
                    else if (!SharedConstants.getCurrentVersion().isStable()) {
                        GuiComponent.blit(integer3, integer2, 64.0f, (float)integer9, 32, 32, 256, 256);
                        if (integer8 < 32) {
                            this.screen.setToolTip(ChatFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot1") + "\n" + ChatFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot2"));
                        }
                    }
                }
                else {
                    GuiComponent.blit(integer3, integer2, 0.0f, (float)integer9, 32, 32, 256, 256);
                }
            }
        }
        
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            WorldSelectionList.this.setSelected(this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if (double1 - AbstractSelectionList.this.getRowLeft() <= 32.0) {
                this.joinWorld();
                return true;
            }
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.joinWorld();
                return true;
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }
        
        public void joinWorld() {
            if (this.summary.shouldBackup() || this.summary.isOldCustomizedWorld()) {
                Component jo2 = new TranslatableComponent("selectWorld.backupQuestion", new Object[0]);
                Component jo3 = new TranslatableComponent("selectWorld.backupWarning", new Object[] { this.summary.getWorldVersionName().getColoredString(), SharedConstants.getCurrentVersion().getName() });
                if (this.summary.isOldCustomizedWorld()) {
                    jo2 = new TranslatableComponent("selectWorld.backupQuestion.customized", new Object[0]);
                    jo3 = new TranslatableComponent("selectWorld.backupWarning.customized", new Object[0]);
                }
                String string4;
                this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (boolean1, boolean2) -> {
                    if (boolean1) {
                        string4 = this.summary.getLevelId();
                        EditWorldScreen.makeBackupAndShowToast(this.minecraft.getLevelSource(), string4);
                    }
                    this.loadWorld();
                }, jo2, jo3, false));
            }
            else if (this.summary.askToOpenWorld()) {
                this.minecraft.setScreen(new ConfirmScreen(boolean1 -> {
                    if (boolean1) {
                        try {
                            this.loadWorld();
                        }
                        catch (Exception exception3) {
                            WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception3);
                            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), new TranslatableComponent("selectWorld.futureworld.error.title", new Object[0]), new TranslatableComponent("selectWorld.futureworld.error.text", new Object[0])));
                        }
                    }
                    else {
                        this.minecraft.setScreen(this.screen);
                    }
                }, new TranslatableComponent("selectWorld.versionQuestion", new Object[0]), new TranslatableComponent("selectWorld.versionWarning", new Object[] { this.summary.getWorldVersionName().getColoredString() }), I18n.get("selectWorld.versionJoinButton"), I18n.get("gui.cancel")));
            }
            else {
                this.loadWorld();
            }
        }
        
        public void deleteWorld() {
            this.minecraft.setScreen(new ConfirmScreen(boolean1 -> {
                if (boolean1) {
                    this.minecraft.setScreen(new ProgressScreen());
                    final LevelStorageSource coq3 = this.minecraft.getLevelSource();
                    coq3.deleteLevel(this.summary.getLevelId());
                    WorldSelectionList.this.refreshList((Supplier<String>)(() -> this.screen.searchBox.getValue()), true);
                }
                this.minecraft.setScreen(this.screen);
            }, new TranslatableComponent("selectWorld.deleteQuestion", new Object[0]), new TranslatableComponent("selectWorld.deleteWarning", new Object[] { this.summary.getLevelName() }), I18n.get("selectWorld.deleteButton"), I18n.get("gui.cancel")));
        }
        
        public void editWorld() {
            this.minecraft.setScreen(new EditWorldScreen(boolean1 -> {
                if (boolean1) {
                    WorldSelectionList.this.refreshList((Supplier<String>)(() -> this.screen.searchBox.getValue()), true);
                }
                this.minecraft.setScreen(this.screen);
            }, this.summary.getLevelId()));
        }
        
        public void recreateWorld() {
            try {
                this.minecraft.setScreen(new ProgressScreen());
                final CreateWorldScreen dfq2 = new CreateWorldScreen(this.screen);
                final LevelStorage coo3 = this.minecraft.getLevelSource().selectLevel(this.summary.getLevelId(), null);
                final LevelData com4 = coo3.prepareLevel();
                if (com4 != null) {
                    dfq2.copyFromWorld(com4);
                    if (this.summary.isOldCustomizedWorld()) {
                        this.minecraft.setScreen(new ConfirmScreen(boolean2 -> this.minecraft.setScreen(boolean2 ? dfq2 : this.screen), new TranslatableComponent("selectWorld.recreate.customized.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.customized.text", new Object[0]), I18n.get("gui.proceed"), I18n.get("gui.cancel")));
                    }
                    else {
                        this.minecraft.setScreen(dfq2);
                    }
                }
            }
            catch (Exception exception2) {
                WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception2);
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), new TranslatableComponent("selectWorld.recreate.error.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.error.text", new Object[0])));
            }
        }
        
        private void loadWorld() {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
                this.minecraft.selectLevel(this.summary.getLevelId(), this.summary.getLevelName(), null);
            }
        }
        
        @Nullable
        private DynamicTexture loadServerIcon() {
            final boolean boolean2 = this.iconFile != null && this.iconFile.isFile();
            if (boolean2) {
                try (final InputStream inputStream3 = (InputStream)new FileInputStream(this.iconFile)) {
                    final NativeImage cuj5 = NativeImage.read(inputStream3);
                    Validate.validState(cuj5.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(cuj5.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    final DynamicTexture dwr6 = new DynamicTexture(cuj5);
                    this.minecraft.getTextureManager().register(this.iconLocation, dwr6);
                    return dwr6;
                }
                catch (Throwable throwable3) {
                    WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), throwable3);
                    this.iconFile = null;
                    return null;
                }
            }
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
        }
        
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }
    }
}

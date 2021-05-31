package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.realms.Tezzelator;
import net.minecraft.realms.RealmListEntry;
import java.util.Arrays;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import java.util.Date;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.Realms;
import java.util.Iterator;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import java.util.Collections;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsLevelSummary;
import java.util.List;
import java.text.DateFormat;
import net.minecraft.realms.RealmsButton;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsSelectFileToUploadScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsResetWorldScreen lastScreen;
    private final long worldId;
    private final int slotId;
    private RealmsButton uploadButton;
    private final DateFormat DATE_FORMAT;
    private List<RealmsLevelSummary> levelList;
    private int selectedWorld;
    private WorldSelectionList worldSelectionList;
    private String worldLang;
    private String conversionLang;
    private final String[] gameModesLang;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private RealmsLabel noWorldsLabel;
    
    public RealmsSelectFileToUploadScreen(final long long1, final int integer, final RealmsResetWorldScreen cwu) {
        this.DATE_FORMAT = (DateFormat)new SimpleDateFormat();
        this.levelList = (List<RealmsLevelSummary>)new ArrayList();
        this.selectedWorld = -1;
        this.gameModesLang = new String[4];
        this.lastScreen = cwu;
        this.worldId = long1;
        this.slotId = integer;
    }
    
    private void loadLevelList() throws Exception {
        final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource2 = this.getLevelStorageSource();
        Collections.sort((List)(this.levelList = realmsAnvilLevelStorageSource2.getLevelList()));
        for (final RealmsLevelSummary realmsLevelSummary4 : this.levelList) {
            this.worldSelectionList.addEntry(realmsLevelSummary4);
        }
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        }
        catch (Exception exception2) {
            RealmsSelectFileToUploadScreen.LOGGER.error("Couldn't load level list", (Throwable)exception2);
            Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", exception2.getMessage(), (RealmsScreen)this.lastScreen));
            return;
        }
        this.worldLang = RealmsScreen.getLocalizedString("selectWorld.world");
        this.conversionLang = RealmsScreen.getLocalizedString("selectWorld.conversion");
        this.gameModesLang[Realms.survivalId()] = RealmsScreen.getLocalizedString("gameMode.survival");
        this.gameModesLang[Realms.creativeId()] = RealmsScreen.getLocalizedString("gameMode.creative");
        this.gameModesLang[Realms.adventureId()] = RealmsScreen.getLocalizedString("gameMode.adventure");
        this.gameModesLang[Realms.spectatorId()] = RealmsScreen.getLocalizedString("gameMode.spectator");
        this.addWidget(this.worldSelectionList);
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsSelectFileToUploadScreen.this.lastScreen);
            }
        });
        this.buttonsAdd(this.uploadButton = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, RealmsScreen.getLocalizedString("mco.upload.button.name")) {
            @Override
            public void onPress() {
                RealmsSelectFileToUploadScreen.this.upload();
            }
        });
        this.uploadButton.active(this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size());
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
        this.addWidget(this.subtitleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, RealmsConstants.row(-1), 10526880));
        if (this.levelList.isEmpty()) {
            this.addWidget(this.noWorldsLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.upload.select.world.none"), this.width() / 2, this.height() / 2 - 20, 16777215));
        }
        else {
            this.noWorldsLabel = null;
        }
        this.narrateLabels();
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    private void upload() {
        if (this.selectedWorld != -1 && !((RealmsLevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
            final RealmsLevelSummary realmsLevelSummary2 = (RealmsLevelSummary)this.levelList.get(this.selectedWorld);
            Realms.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, realmsLevelSummary2));
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.worldSelectionList.render(integer1, integer2, float3);
        this.titleLabel.render(this);
        this.subtitleLabel.render(this);
        if (this.noWorldsLabel != null) {
            this.noWorldsLabel.render(this);
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    private String gameModeName(final RealmsLevelSummary realmsLevelSummary) {
        return this.gameModesLang[realmsLevelSummary.getGameMode()];
    }
    
    private String formatLastPlayed(final RealmsLevelSummary realmsLevelSummary) {
        return this.DATE_FORMAT.format(new Date(realmsLevelSummary.getLastPlayed()));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class WorldSelectionList extends RealmsObjectSelectionList {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width(), RealmsSelectFileToUploadScreen.this.height(), RealmsConstants.row(0), RealmsSelectFileToUploadScreen.this.height() - 40, 36);
        }
        
        public void addEntry(final RealmsLevelSummary realmsLevelSummary) {
            this.addEntry(new WorldListEntry(realmsLevelSummary));
        }
        
        @Override
        public int getItemCount() {
            return RealmsSelectFileToUploadScreen.this.levelList.size();
        }
        
        @Override
        public int getMaxPosition() {
            return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
        }
        
        @Override
        public boolean isFocused() {
            return RealmsSelectFileToUploadScreen.this.isFocused(this);
        }
        
        @Override
        public void renderBackground() {
            RealmsSelectFileToUploadScreen.this.renderBackground();
        }
        
        @Override
        public void selectItem(final int integer) {
            this.setSelected(integer);
            if (integer != -1) {
                final RealmsLevelSummary realmsLevelSummary3 = (RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(integer);
                final String string4 = RealmsScreen.getLocalizedString("narrator.select.list.position", integer + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
                final String string5 = Realms.joinNarrations((Iterable<String>)Arrays.asList((Object[])new String[] { realmsLevelSummary3.getLevelName(), RealmsSelectFileToUploadScreen.this.formatLastPlayed(realmsLevelSummary3), RealmsSelectFileToUploadScreen.this.gameModeName(realmsLevelSummary3), string4 }));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string5));
            }
            RealmsSelectFileToUploadScreen.this.selectedWorld = integer;
            RealmsSelectFileToUploadScreen.this.uploadButton.active(RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore());
        }
    }
    
    class WorldListEntry extends RealmListEntry {
        final RealmsLevelSummary levelSummary;
        
        public WorldListEntry(final RealmsLevelSummary realmsLevelSummary) {
            this.levelSummary = realmsLevelSummary;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderItem(this.levelSummary, integer1, integer3, integer2, integer5, Tezzelator.instance, integer6, integer7);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
            return true;
        }
        
        protected void renderItem(final RealmsLevelSummary realmsLevelSummary, final int integer2, final int integer3, final int integer4, final int integer5, final Tezzelator tezzelator, final int integer7, final int integer8) {
            String string10 = realmsLevelSummary.getLevelName();
            if (string10 == null || string10.isEmpty()) {
                string10 = RealmsSelectFileToUploadScreen.this.worldLang + " " + (integer2 + 1);
            }
            String string11 = realmsLevelSummary.getLevelId();
            string11 = string11 + " (" + RealmsSelectFileToUploadScreen.this.formatLastPlayed(realmsLevelSummary);
            string11 += ")";
            String string12 = "";
            if (realmsLevelSummary.isRequiresConversion()) {
                string12 = RealmsSelectFileToUploadScreen.this.conversionLang + " " + string12;
            }
            else {
                string12 = RealmsSelectFileToUploadScreen.this.gameModeName(realmsLevelSummary);
                if (realmsLevelSummary.isHardcore()) {
                    string12 = ChatFormatting.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + ChatFormatting.RESET;
                }
                if (realmsLevelSummary.hasCheats()) {
                    string12 = string12 + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
                }
            }
            RealmsSelectFileToUploadScreen.this.drawString(string10, integer3 + 2, integer4 + 1, 16777215);
            RealmsSelectFileToUploadScreen.this.drawString(string11, integer3 + 2, integer4 + 12, 8421504);
            RealmsSelectFileToUploadScreen.this.drawString(string12, integer3 + 2, integer4 + 12 + 10, 8421504);
        }
    }
}

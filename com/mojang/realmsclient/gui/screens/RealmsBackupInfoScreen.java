package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.Tezzelator;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import java.util.Locale;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import com.mojang.realmsclient.dto.Backup;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupInfoScreen extends RealmsScreen {
    private final RealmsScreen lastScreen;
    private final int BUTTON_BACK_ID = 0;
    private final Backup backup;
    private final List<String> keys;
    private BackupInfoList backupInfoList;
    String[] difficulties;
    String[] gameModes;
    
    public RealmsBackupInfoScreen(final RealmsScreen realmsScreen, final Backup backup) {
        this.keys = (List<String>)new ArrayList();
        this.difficulties = new String[] { RealmsScreen.getLocalizedString("options.difficulty.peaceful"), RealmsScreen.getLocalizedString("options.difficulty.easy"), RealmsScreen.getLocalizedString("options.difficulty.normal"), RealmsScreen.getLocalizedString("options.difficulty.hard") };
        this.gameModes = new String[] { RealmsScreen.getLocalizedString("selectWorld.gameMode.survival"), RealmsScreen.getLocalizedString("selectWorld.gameMode.creative"), RealmsScreen.getLocalizedString("selectWorld.gameMode.adventure") };
        this.lastScreen = realmsScreen;
        this.backup = backup;
        if (backup.changeList != null) {
            for (final Map.Entry<String, String> entry5 : backup.changeList.entrySet()) {
                this.keys.add(entry5.getKey());
            }
        }
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsBackupInfoScreen.this.lastScreen);
            }
        });
        this.addWidget(this.backupInfoList = new BackupInfoList());
        this.focusOn(this.backupInfoList);
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
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
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
        this.backupInfoList.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    private String checkForSpecificMetadata(final String string1, final String string2) {
        final String string3 = string1.toLowerCase(Locale.ROOT);
        if (string3.contains("game") && string3.contains("mode")) {
            return this.gameModeMetadata(string2);
        }
        if (string3.contains("game") && string3.contains("difficulty")) {
            return this.gameDifficultyMetadata(string2);
        }
        return string2;
    }
    
    private String gameDifficultyMetadata(final String string) {
        try {
            return this.difficulties[Integer.parseInt(string)];
        }
        catch (Exception exception3) {
            return "UNKNOWN";
        }
    }
    
    private String gameModeMetadata(final String string) {
        try {
            return this.gameModes[Integer.parseInt(string)];
        }
        catch (Exception exception3) {
            return "UNKNOWN";
        }
    }
    
    class BackupInfoList extends RealmsSimpleScrolledSelectionList {
        public BackupInfoList() {
            super(RealmsBackupInfoScreen.this.width(), RealmsBackupInfoScreen.this.height(), 32, RealmsBackupInfoScreen.this.height() - 64, 36);
        }
        
        @Override
        public int getItemCount() {
            return RealmsBackupInfoScreen.this.backup.changeList.size();
        }
        
        @Override
        public boolean isSelectedItem(final int integer) {
            return false;
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }
        
        @Override
        public void renderBackground() {
        }
        
        public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final Tezzelator tezzelator, final int integer6, final int integer7) {
            final String string9 = (String)RealmsBackupInfoScreen.this.keys.get(integer1);
            RealmsBackupInfoScreen.this.drawString(string9, this.width() / 2 - 40, integer3, 10526880);
            final String string10 = (String)RealmsBackupInfoScreen.this.backup.changeList.get(string9);
            RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.this.checkForSpecificMetadata(string9, string10), this.width() / 2 - 40, integer3 + 12, 16777215);
        }
    }
}

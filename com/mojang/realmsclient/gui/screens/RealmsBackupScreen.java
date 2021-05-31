package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.Date;
import net.minecraft.realms.RealmsConfirmResultListener;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.util.Iterator;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.client.RealmsClient;
import java.util.Collections;
import net.minecraft.realms.RealmsLabel;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.Backup;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private static int lastScrollPosition;
    private final RealmsConfigureWorldScreen lastScreen;
    private List<Backup> backups;
    private String toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup;
    private final int slotId;
    private RealmsButton downloadButton;
    private RealmsButton restoreButton;
    private RealmsButton changesButton;
    private Boolean noBackups;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;
    
    public RealmsBackupScreen(final RealmsConfigureWorldScreen cwg, final RealmsServer realmsServer, final int integer) {
        this.backups = (List<Backup>)Collections.emptyList();
        this.selectedBackup = -1;
        this.noBackups = false;
        this.lastScreen = cwg;
        this.serverData = realmsServer;
        this.slotId = integer;
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (RealmsBackupScreen.lastScrollPosition != -1) {
            this.backupObjectSelectionList.scroll(RealmsBackupScreen.lastScrollPosition);
        }
        new Thread("Realms-fetch-backups") {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    final List<Backup> list3 = cvm2.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
                    Realms.execute(() -> {
                        RealmsBackupScreen.this.backups = list3;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        for (final Backup backup4 : RealmsBackupScreen.this.backups) {
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup4);
                        }
                        RealmsBackupScreen.this.generateChangeList();
                    });
                }
                catch (RealmsServiceException cvu3) {
                    RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)cvu3);
                }
            }
        }.start();
        this.postInit();
    }
    
    private void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int integer2 = 0; integer2 < this.backups.size() - 1; ++integer2) {
            final Backup backup3 = (Backup)this.backups.get(integer2);
            final Backup backup4 = (Backup)this.backups.get(integer2 + 1);
            if (!backup3.metadata.isEmpty()) {
                if (!backup4.metadata.isEmpty()) {
                    for (final String string6 : backup3.metadata.keySet()) {
                        if (!string6.contains("Uploaded") && backup4.metadata.containsKey(string6)) {
                            if (((String)backup3.metadata.get(string6)).equals(backup4.metadata.get(string6))) {
                                continue;
                            }
                            this.addToChangeList(backup3, string6);
                        }
                        else {
                            this.addToChangeList(backup3, string6);
                        }
                    }
                }
            }
        }
    }
    
    private void addToChangeList(final Backup backup, final String string) {
        if (string.contains("Uploaded")) {
            final String string2 = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(string, string2);
            backup.setUploadedVersion(true);
        }
        else {
            backup.changeList.put(string, backup.metadata.get(string));
        }
    }
    
    private void postInit() {
        this.buttonsAdd(this.downloadButton = new RealmsButton(2, this.width() - 135, RealmsConstants.row(1), 120, 20, RealmsScreen.getLocalizedString("mco.backup.button.download")) {
            @Override
            public void onPress() {
                RealmsBackupScreen.this.downloadClicked();
            }
        });
        this.buttonsAdd(this.restoreButton = new RealmsButton(3, this.width() - 135, RealmsConstants.row(3), 120, 20, RealmsScreen.getLocalizedString("mco.backup.button.restore")) {
            @Override
            public void onPress() {
                RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.selectedBackup);
            }
        });
        this.buttonsAdd(this.changesButton = new RealmsButton(4, this.width() - 135, RealmsConstants.row(5), 120, 20, RealmsScreen.getLocalizedString("mco.backup.changes.tooltip")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(RealmsBackupScreen.this.selectedBackup)));
                RealmsBackupScreen.this.selectedBackup = -1;
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsBackupScreen.this.lastScreen);
            }
        });
        this.addWidget(this.backupObjectSelectionList);
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 16777215));
        this.focusOn(this.backupObjectSelectionList);
        this.updateButtonStates();
        this.narrateLabels();
    }
    
    private void updateButtonStates() {
        this.restoreButton.setVisible(this.shouldRestoreButtonBeVisible());
        this.changesButton.setVisible(this.shouldChangesButtonBeVisible());
    }
    
    private boolean shouldChangesButtonBeVisible() {
        return this.selectedBackup != -1 && !((Backup)this.backups.get(this.selectedBackup)).changeList.isEmpty();
    }
    
    private boolean shouldRestoreButtonBeVisible() {
        return this.selectedBackup != -1 && !this.serverData.expired;
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void restoreClicked(final int integer) {
        if (integer >= 0 && integer < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = integer;
            final Date date3 = ((Backup)this.backups.get(integer)).lastModifiedDate;
            final String string4 = DateFormat.getDateTimeInstance(3, 3).format(date3);
            final String string5 = RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date3.getTime());
            final String string6 = RealmsScreen.getLocalizedString("mco.configure.world.restore.question.line1", string4, string5);
            final String string7 = RealmsScreen.getLocalizedString("mco.configure.world.restore.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, string6, string7, true, 1));
        }
    }
    
    private void downloadClicked() {
        final String string2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
        final String string3 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, string2, string3, true, 2));
    }
    
    private void downloadWorldData() {
        final RealmsTasks.DownloadTask b2 = new RealmsTasks.DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this);
        final RealmsLongRunningMcoTaskScreen cwo3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), b2);
        cwo3.start();
        Realms.setScreen(cwo3);
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (boolean1 && integer == 1) {
            this.restore();
        }
        else if (integer == 1) {
            this.selectedBackup = -1;
            Realms.setScreen(this);
        }
        else if (boolean1 && integer == 2) {
            this.downloadWorldData();
        }
        else {
            Realms.setScreen(this);
        }
    }
    
    private void restore() {
        final Backup backup2 = (Backup)this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        final RealmsTasks.RestoreTask g3 = new RealmsTasks.RestoreTask(backup2, this.serverData.id, this.lastScreen);
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), g3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.renderBackground();
        this.backupObjectSelectionList.render(integer1, integer2, float3);
        this.titleLabel.render(this);
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 10526880);
        if (this.noBackups) {
            this.drawString(RealmsScreen.getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 16777215);
        }
        this.downloadButton.active(!this.noBackups);
        super.render(integer1, integer2, float3);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
    }
    
    protected void renderMousehoverTooltip(final String string, final int integer2, final int integer3) {
        if (string == null) {
            return;
        }
        final int integer4 = integer2 + 12;
        final int integer5 = integer3 - 12;
        final int integer6 = this.fontWidth(string);
        this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, integer4, integer5, 16777215);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        RealmsBackupScreen.lastScrollPosition = -1;
    }
    
    class BackupObjectSelectionList extends RealmsObjectSelectionList {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
        }
        
        public void addEntry(final Backup backup) {
            this.addEntry(new BackupObjectSelectionListEntry(backup));
        }
        
        @Override
        public int getRowWidth() {
            return (int)(this.width() * 0.93);
        }
        
        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.isFocused(this);
        }
        
        @Override
        public int getItemCount() {
            return RealmsBackupScreen.this.backups.size();
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }
        
        @Override
        public void renderBackground() {
            RealmsBackupScreen.this.renderBackground();
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            if (integer != 0) {
                return false;
            }
            if (double1 < this.getScrollbarPosition() && double2 >= this.y0() && double2 <= this.y1()) {
                final int integer2 = this.width() / 2 - 92;
                final int integer3 = this.width();
                final int integer4 = (int)Math.floor(double2 - this.y0()) - this.headerHeight() + this.getScroll();
                final int integer5 = integer4 / this.itemHeight();
                if (double1 >= integer2 && double1 <= integer3 && integer5 >= 0 && integer4 >= 0 && integer5 < this.getItemCount()) {
                    this.selectItem(integer5);
                    this.itemClicked(integer4, integer5, double1, double2, this.width());
                }
                return true;
            }
            return false;
        }
        
        @Override
        public int getScrollbarPosition() {
            return this.width() - 5;
        }
        
        @Override
        public void itemClicked(final int integer1, final int integer2, final double double3, final double double4, final int integer5) {
            final int integer6 = this.width() - 35;
            final int integer7 = integer2 * this.itemHeight() + 36 - this.getScroll();
            final int integer8 = integer6 + 10;
            final int integer9 = integer7 - 3;
            if (double3 >= integer6 && double3 <= integer6 + 9 && double4 >= integer7 && double4 <= integer7 + 9) {
                if (!((Backup)RealmsBackupScreen.this.backups.get(integer2)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    RealmsBackupScreen.lastScrollPosition = this.getScroll();
                    Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(integer2)));
                }
            }
            else if (double3 >= integer8 && double3 < integer8 + 13 && double4 >= integer9 && double4 < integer9 + 15) {
                RealmsBackupScreen.lastScrollPosition = this.getScroll();
                RealmsBackupScreen.this.restoreClicked(integer2);
            }
        }
        
        @Override
        public void selectItem(final int integer) {
            this.setSelected(integer);
            if (integer != -1) {
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((Backup)RealmsBackupScreen.this.backups.get(integer)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(integer);
        }
        
        public void selectInviteListItem(final int integer) {
            RealmsBackupScreen.this.selectedBackup = integer;
            RealmsBackupScreen.this.updateButtonStates();
        }
    }
    
    class BackupObjectSelectionListEntry extends RealmListEntry {
        final Backup mBackup;
        
        public BackupObjectSelectionListEntry(final Backup backup) {
            this.mBackup = backup;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderBackupItem(this.mBackup, integer3 - 40, integer2, integer6, integer7);
        }
        
        private void renderBackupItem(final Backup backup, final int integer2, final int integer3, final int integer4, final int integer5) {
            final int integer6 = backup.isUploadedVersion() ? -8388737 : 16777215;
            RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - backup.lastModifiedDate.getTime()) + ")", integer2 + 40, integer3 + 1, integer6);
            RealmsBackupScreen.this.drawString(this.getMediumDatePresentation(backup.lastModifiedDate), integer2 + 40, integer3 + 12, 5000268);
            final int integer7 = RealmsBackupScreen.this.width() - 175;
            final int integer8 = -3;
            final int integer9 = integer7 - 10;
            final int integer10 = 0;
            if (!RealmsBackupScreen.this.serverData.expired) {
                this.drawRestore(integer7, integer3 - 3, integer4, integer5);
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(integer9, integer3 + 0, integer4, integer5);
            }
        }
        
        private String getMediumDatePresentation(final Date date) {
            return DateFormat.getDateTimeInstance(3, 3).format(date);
        }
        
        private void drawRestore(final int integer1, final int integer2, final int integer3, final int integer4) {
            final boolean boolean6 = integer3 >= integer1 && integer3 <= integer1 + 12 && integer4 >= integer2 && integer4 <= integer2 + 14 && integer4 < RealmsBackupScreen.this.height() - 15 && integer4 > 32;
            RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(integer1 * 2, integer2 * 2, 0.0f, boolean6 ? 28.0f : 0.0f, 23, 28, 23, 56);
            GlStateManager.popMatrix();
            if (boolean6) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
            }
        }
        
        private void drawInfo(final int integer1, final int integer2, final int integer3, final int integer4) {
            final boolean boolean6 = integer3 >= integer1 && integer3 <= integer1 + 8 && integer4 >= integer2 && integer4 <= integer2 + 8 && integer4 < RealmsBackupScreen.this.height() - 15 && integer4 > 32;
            RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(integer1 * 2, integer2 * 2, 0.0f, boolean6 ? 15.0f : 0.0f, 15, 15, 15, 30);
            GlStateManager.popMatrix();
            if (boolean6) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
            }
        }
    }
}

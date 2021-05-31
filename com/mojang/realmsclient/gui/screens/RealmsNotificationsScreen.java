package com.mojang.realmsclient.gui.screens;

import java.util.Arrays;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.Realms;
import java.util.List;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.realms.RealmsScreen;

public class RealmsNotificationsScreen extends RealmsScreen {
    private static final RealmsDataFetcher realmsDataFetcher;
    private volatile int numberOfPendingInvites;
    private static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    private static boolean validClient;
    private static boolean hasUnreadNews;
    private static final List<RealmsDataFetcher.Task> tasks;
    
    public RealmsNotificationsScreen(final RealmsScreen realmsScreen) {
    }
    
    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.setKeyboardHandlerSendRepeatsToGui(true);
    }
    
    @Override
    public void tick() {
        if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !RealmsNotificationsScreen.validClient) && !RealmsNotificationsScreen.realmsDataFetcher.isStopped()) {
            RealmsNotificationsScreen.realmsDataFetcher.stop();
            return;
        }
        if (RealmsNotificationsScreen.validClient && Realms.getRealmsNotificationsEnabled()) {
            RealmsNotificationsScreen.realmsDataFetcher.initWithSpecificTaskList(RealmsNotificationsScreen.tasks);
            if (RealmsNotificationsScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
                this.numberOfPendingInvites = RealmsNotificationsScreen.realmsDataFetcher.getPendingInvitesCount();
            }
            if (RealmsNotificationsScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
                RealmsNotificationsScreen.trialAvailable = RealmsNotificationsScreen.realmsDataFetcher.isTrialAvailable();
            }
            if (RealmsNotificationsScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
                RealmsNotificationsScreen.hasUnreadNews = RealmsNotificationsScreen.realmsDataFetcher.hasUnreadNews();
            }
            RealmsNotificationsScreen.realmsDataFetcher.markClean();
        }
    }
    
    private void checkIfMcoEnabled() {
        if (!RealmsNotificationsScreen.checkedMcoAvailability) {
            RealmsNotificationsScreen.checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1") {
                public void run() {
                    final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                    try {
                        final RealmsClient.CompatibleVersionResponse a3 = cvm2.clientCompatible();
                        if (!a3.equals(RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
                            return;
                        }
                    }
                    catch (RealmsServiceException cvu3) {
                        if (cvu3.httpResultCode != 401) {
                            RealmsNotificationsScreen.checkedMcoAvailability = false;
                        }
                        return;
                    }
                    catch (IOException iOException3) {
                        RealmsNotificationsScreen.checkedMcoAvailability = false;
                        return;
                    }
                    RealmsNotificationsScreen.validClient = true;
                }
            }.start();
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (RealmsNotificationsScreen.validClient) {
            this.drawIcons(integer1, integer2);
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return super.mouseClicked(double1, double2, integer);
    }
    
    private void drawIcons(final int integer1, final int integer2) {
        final int integer3 = this.numberOfPendingInvites;
        final int integer4 = 24;
        final int integer5 = this.height() / 4 + 48;
        final int integer6 = this.width() / 2 + 80;
        final int integer7 = integer5 + 48 + 2;
        int integer8 = 0;
        if (RealmsNotificationsScreen.hasUnreadNews) {
            RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.4f, 0.4f, 0.4f);
            RealmsScreen.blit((int)((integer6 + 2 - integer8) * 2.5), (int)(integer7 * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            GlStateManager.popMatrix();
            integer8 += 14;
        }
        if (integer3 != 0) {
            RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(integer6 - integer8, integer7 - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            GlStateManager.popMatrix();
            integer8 += 16;
        }
        if (RealmsNotificationsScreen.trialAvailable) {
            RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            int integer9 = 0;
            if ((System.currentTimeMillis() / 800L & 0x1L) == 0x1L) {
                integer9 = 8;
            }
            RealmsScreen.blit(integer6 + 4 - integer8, integer7 + 4, 0.0f, (float)integer9, 8, 8, 8, 16);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void removed() {
        RealmsNotificationsScreen.realmsDataFetcher.stop();
    }
    
    static {
        realmsDataFetcher = new RealmsDataFetcher();
        tasks = Arrays.asList((Object[])new RealmsDataFetcher.Task[] { RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS });
    }
}

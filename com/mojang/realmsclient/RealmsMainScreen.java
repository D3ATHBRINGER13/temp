package com.mojang.realmsclient;

import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.RealmsMth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.realms.RealmsConfirmResultListener;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import net.minecraft.client.Minecraft;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateTrialScreen;
import java.util.ArrayList;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import net.minecraft.realms.RealmsGuiEventListener;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.AbstractRealmsButton;
import java.util.Iterator;
import net.minecraft.realms.Realms;
import com.google.common.collect.Lists;
import java.util.concurrent.locks.ReentrantLock;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.List;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsMainScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private static boolean overrideConfigure;
    private final RateLimiter inviteNarrationLimiter;
    private boolean dontSetConnectedToRealms;
    private static final String[] IMAGES_LOCATION;
    private static final RealmsDataFetcher realmsDataFetcher;
    private static int lastScrollYPosition;
    private final RealmsScreen lastScreen;
    private volatile RealmSelectionList realmSelectionList;
    private long selectedServerId;
    private RealmsButton playButton;
    private RealmsButton backButton;
    private RealmsButton renewButton;
    private RealmsButton configureButton;
    private RealmsButton leaveButton;
    private String toolTip;
    private List<RealmsServer> realmsServers;
    private volatile int numberOfPendingInvites;
    private int animTick;
    private static volatile boolean hasParentalConsent;
    private static volatile boolean checkedParentalConsent;
    private static volatile boolean checkedClientCompatability;
    private boolean hasFetchedServers;
    private boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
    private volatile boolean createdTrial;
    private volatile boolean showingPopup;
    private volatile boolean hasUnreadNews;
    private volatile String newsLink;
    private int carouselIndex;
    private int carouselTick;
    private boolean hasSwitchedCarouselImage;
    private static RealmsScreen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private List<KeyCombo> keyCombos;
    private int clicks;
    private ReentrantLock connectLock;
    private boolean expiredHover;
    private ShowPopupButton showPopupButton;
    private PendingInvitesButton pendingInvitesButton;
    private NewsButton newsButton;
    private RealmsButton createTrialButton;
    private RealmsButton buyARealmButton;
    private RealmsButton closeButton;
    
    public RealmsMainScreen(final RealmsScreen realmsScreen) {
        this.selectedServerId = -1L;
        this.realmsServers = (List<RealmsServer>)Lists.newArrayList();
        this.connectLock = new ReentrantLock();
        this.lastScreen = realmsScreen;
        this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107);
    }
    
    public boolean shouldShowMessageInList() {
        if (!this.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialsAvailable && !this.createdTrial) {
            return true;
        }
        for (final RealmsServer realmsServer3 : this.realmsServers) {
            if (realmsServer3.ownerUUID.equals(Realms.getUUID())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean shouldShowPopup() {
        return this.hasParentalConsent() && this.hasFetchedServers && (this.popupOpenedByUser || (this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty()) || this.realmsServers.isEmpty());
    }
    
    @Override
    public void init() {
        this.keyCombos = (List<KeyCombo>)Lists.newArrayList((Object[])new KeyCombo[] { new KeyCombo(new char[] { '3', '2', '1', '4', '5', '6' }, () -> RealmsMainScreen.overrideConfigure = !RealmsMainScreen.overrideConfigure), new KeyCombo(new char[] { '9', '8', '7', '1', '2', '3' }, () -> {
                if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
                    this.switchToProd();
                }
                else {
                    this.switchToStage();
                }
            }), new KeyCombo(new char[] { '9', '8', '7', '4', '5', '6' }, () -> {
                if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
                    this.switchToProd();
                }
                else {
                    this.switchToLocal();
                }
            }) });
        if (RealmsMainScreen.realmsGenericErrorScreen != null) {
            Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (RealmsMainScreen.checkedClientCompatability && !this.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatability();
        this.checkUnreadNews();
        if (!this.dontSetConnectedToRealms) {
            Realms.setConnectedToRealms(false);
        }
        this.setKeyboardHandlerSendRepeatsToGui(true);
        if (this.hasParentalConsent()) {
            RealmsMainScreen.realmsDataFetcher.forceUpdate();
        }
        this.showingPopup = false;
        this.postInit();
    }
    
    private boolean hasParentalConsent() {
        return RealmsMainScreen.checkedParentalConsent && RealmsMainScreen.hasParentalConsent;
    }
    
    public void addButtons() {
        this.buttonsAdd(this.configureButton = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, RealmsScreen.getLocalizedString("mco.selectServer.configure")) {
            @Override
            public void onPress() {
                RealmsMainScreen.this.configureClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
            }
        });
        this.buttonsAdd(this.playButton = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, RealmsScreen.getLocalizedString("mco.selectServer.play")) {
            @Override
            public void onPress() {
                RealmsMainScreen.this.onPlay();
            }
        });
        this.buttonsAdd(this.backButton = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                if (!RealmsMainScreen.this.justClosedPopup) {
                    Realms.setScreen(RealmsMainScreen.this.lastScreen);
                }
            }
        });
        this.buttonsAdd(this.renewButton = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew")) {
            @Override
            public void onPress() {
                RealmsMainScreen.this.onRenew();
            }
        });
        this.buttonsAdd(this.leaveButton = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, RealmsScreen.getLocalizedString("mco.selectServer.leave")) {
            @Override
            public void onPress() {
                RealmsMainScreen.this.leaveClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
            }
        });
        this.buttonsAdd(this.pendingInvitesButton = new PendingInvitesButton());
        this.buttonsAdd(this.newsButton = new NewsButton());
        this.buttonsAdd(this.showPopupButton = new ShowPopupButton());
        this.buttonsAdd(this.closeButton = new CloseButton());
        this.buttonsAdd(this.createTrialButton = new RealmsButton(6, this.width() / 2 + 52, this.popupY0() + 137 - 20, 98, 20, RealmsScreen.getLocalizedString("mco.selectServer.trial")) {
            @Override
            public void onPress() {
                RealmsMainScreen.this.createTrial();
            }
        });
        this.buttonsAdd(this.buyARealmButton = new RealmsButton(5, this.width() / 2 + 52, this.popupY0() + 160 - 20, 98, 20, RealmsScreen.getLocalizedString("mco.selectServer.buy")) {
            @Override
            public void onPress() {
                RealmsUtil.browseTo("https://minecraft.net/realms");
            }
        });
        final RealmsServer realmsServer2 = this.findServer(this.selectedServerId);
        this.updateButtonStates(realmsServer2);
    }
    
    private void updateButtonStates(final RealmsServer realmsServer) {
        this.playButton.active(this.shouldPlayButtonBeActive(realmsServer) && !this.shouldShowPopup());
        this.renewButton.setVisible(this.shouldRenewButtonBeActive(realmsServer));
        this.configureButton.setVisible(this.shouldConfigureButtonBeVisible(realmsServer));
        this.leaveButton.setVisible(this.shouldLeaveButtonBeVisible(realmsServer));
        final boolean boolean3 = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
        this.createTrialButton.setVisible(boolean3);
        this.createTrialButton.active(boolean3);
        this.buyARealmButton.setVisible(this.shouldShowPopup());
        this.closeButton.setVisible(this.shouldShowPopup() && this.popupOpenedByUser);
        this.renewButton.active(!this.shouldShowPopup());
        this.configureButton.active(!this.shouldShowPopup());
        this.leaveButton.active(!this.shouldShowPopup());
        this.newsButton.active(true);
        this.pendingInvitesButton.active(true);
        this.backButton.active(true);
        this.showPopupButton.active(!this.shouldShowPopup());
    }
    
    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && this.hasParentalConsent() && this.hasFetchedServers;
    }
    
    private boolean shouldPlayButtonBeActive(final RealmsServer realmsServer) {
        return realmsServer != null && !realmsServer.expired && realmsServer.state == RealmsServer.State.OPEN;
    }
    
    private boolean shouldRenewButtonBeActive(final RealmsServer realmsServer) {
        return realmsServer != null && realmsServer.expired && this.isSelfOwnedServer(realmsServer);
    }
    
    private boolean shouldConfigureButtonBeVisible(final RealmsServer realmsServer) {
        return realmsServer != null && this.isSelfOwnedServer(realmsServer);
    }
    
    private boolean shouldLeaveButtonBeVisible(final RealmsServer realmsServer) {
        return realmsServer != null && !this.isSelfOwnedServer(realmsServer);
    }
    
    public void postInit() {
        if (this.hasParentalConsent() && this.hasFetchedServers) {
            this.addButtons();
        }
        this.realmSelectionList = new RealmSelectionList();
        if (RealmsMainScreen.lastScrollYPosition != -1) {
            this.realmSelectionList.scroll(RealmsMainScreen.lastScrollYPosition);
        }
        this.addWidget(this.realmSelectionList);
        this.focusOn(this.realmSelectionList);
    }
    
    @Override
    public void tick() {
        this.tickButtons();
        this.justClosedPopup = false;
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
        if (this.hasParentalConsent()) {
            RealmsMainScreen.realmsDataFetcher.init();
            if (RealmsMainScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
                final List<RealmsServer> list2 = RealmsMainScreen.realmsDataFetcher.getServers();
                this.realmSelectionList.clear();
                final boolean boolean3 = !this.hasFetchedServers;
                if (boolean3) {
                    this.hasFetchedServers = true;
                }
                if (list2 != null) {
                    boolean boolean4 = false;
                    for (final RealmsServer realmsServer6 : list2) {
                        if (this.isSelfOwnedNonExpiredServer(realmsServer6)) {
                            boolean4 = true;
                        }
                    }
                    this.realmsServers = list2;
                    if (this.shouldShowMessageInList()) {
                        this.realmSelectionList.addEntry(new RealmSelectionListTrialEntry());
                    }
                    for (final RealmsServer realmsServer6 : this.realmsServers) {
                        this.realmSelectionList.addEntry(new RealmSelectionListEntry(realmsServer6));
                    }
                    if (!RealmsMainScreen.regionsPinged && boolean4) {
                        RealmsMainScreen.regionsPinged = true;
                        this.pingRegions();
                    }
                }
                if (boolean3) {
                    this.addButtons();
                }
            }
            if (RealmsMainScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
                this.numberOfPendingInvites = RealmsMainScreen.realmsDataFetcher.getPendingInvitesCount();
                if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
                    Realms.narrateNow(RealmsScreen.getLocalizedString("mco.configure.world.invite.narration", this.numberOfPendingInvites));
                }
            }
            if (RealmsMainScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
                final boolean boolean5 = RealmsMainScreen.realmsDataFetcher.isTrialAvailable();
                if (boolean5 != this.trialsAvailable && this.shouldShowPopup()) {
                    this.trialsAvailable = boolean5;
                    this.showingPopup = false;
                }
                else {
                    this.trialsAvailable = boolean5;
                }
            }
            if (RealmsMainScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
                final RealmsServerPlayerLists realmsServerPlayerLists2 = RealmsMainScreen.realmsDataFetcher.getLivestats();
                for (final RealmsServerPlayerList realmsServerPlayerList4 : realmsServerPlayerLists2.servers) {
                    for (final RealmsServer realmsServer6 : this.realmsServers) {
                        if (realmsServer6.id == realmsServerPlayerList4.serverId) {
                            realmsServer6.updateServerPing(realmsServerPlayerList4);
                            break;
                        }
                    }
                }
            }
            if (RealmsMainScreen.realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
                this.hasUnreadNews = RealmsMainScreen.realmsDataFetcher.hasUnreadNews();
                this.newsLink = RealmsMainScreen.realmsDataFetcher.newsLink();
            }
            RealmsMainScreen.realmsDataFetcher.markClean();
            if (this.shouldShowPopup()) {
                ++this.carouselTick;
            }
            if (this.showPopupButton != null) {
                this.showPopupButton.setVisible(this.shouldShowPopupButton());
            }
        }
    }
    
    private void browseURL(final String string) {
        Realms.setClipboard(string);
        RealmsUtil.browseTo(string);
    }
    
    private void pingRegions() {
        new Thread() {
            public void run() {
                final List<RegionPingResult> list2 = Ping.pingAllRegions();
                final RealmsClient cvm3 = RealmsClient.createRealmsClient();
                final PingResult pingResult4 = new PingResult();
                pingResult4.pingResults = list2;
                pingResult4.worldIds = RealmsMainScreen.this.getOwnedNonExpiredWorldIds();
                try {
                    cvm3.sendPingResults(pingResult4);
                }
                catch (Throwable throwable5) {
                    RealmsMainScreen.LOGGER.warn("Could not send ping result to Realms: ", throwable5);
                }
            }
        }.start();
    }
    
    private List<Long> getOwnedNonExpiredWorldIds() {
        final List<Long> list2 = (List<Long>)new ArrayList();
        for (final RealmsServer realmsServer4 : this.realmsServers) {
            if (this.isSelfOwnedNonExpiredServer(realmsServer4)) {
                list2.add(realmsServer4.id);
            }
        }
        return list2;
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
        this.stopRealmsFetcher();
    }
    
    public void setCreatedTrial(final boolean boolean1) {
        this.createdTrial = boolean1;
    }
    
    private void onPlay() {
        final RealmsServer realmsServer2 = this.findServer(this.selectedServerId);
        if (realmsServer2 == null) {
            return;
        }
        this.play(realmsServer2, this);
    }
    
    private void onRenew() {
        final RealmsServer realmsServer2 = this.findServer(this.selectedServerId);
        if (realmsServer2 == null) {
            return;
        }
        final String string3 = "https://account.mojang.com/buy/realms?sid=" + realmsServer2.remoteSubscriptionId + "&pid=" + Realms.getUUID() + "&ref=" + (realmsServer2.expiredTrial ? "expiredTrial" : "expiredRealm");
        this.browseURL(string3);
    }
    
    private void createTrial() {
        if (!this.trialsAvailable || this.createdTrial) {
            return;
        }
        Realms.setScreen(new RealmsCreateTrialScreen(this));
    }
    
    private void checkClientCompatability() {
        if (!RealmsMainScreen.checkedClientCompatability) {
            RealmsMainScreen.checkedClientCompatability = true;
            new Thread("MCO Compatability Checker #1") {
                public void run() {
                    final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                    try {
                        final RealmsClient.CompatibleVersionResponse a3 = cvm2.clientCompatible();
                        if (a3.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                            RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                            Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                            return;
                        }
                        if (a3.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                            RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                            Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    }
                    catch (RealmsServiceException cvu3) {
                        RealmsMainScreen.checkedClientCompatability = false;
                        RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", cvu3.toString());
                        if (cvu3.httpResultCode == 401) {
                            RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.error.invalid.session.title"), RealmsScreen.getLocalizedString("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                            return;
                        }
                        Realms.setScreen(new RealmsGenericErrorScreen(cvu3, RealmsMainScreen.this.lastScreen));
                    }
                    catch (IOException iOException3) {
                        RealmsMainScreen.checkedClientCompatability = false;
                        RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", iOException3.getMessage());
                        Realms.setScreen(new RealmsGenericErrorScreen(iOException3.getMessage(), RealmsMainScreen.this.lastScreen));
                    }
                }
            }.start();
        }
    }
    
    private void checkUnreadNews() {
    }
    
    private void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1") {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    final Boolean boolean3 = cvm2.mcoEnabled();
                    if (boolean3) {
                        RealmsMainScreen.LOGGER.info("Realms is available for this user");
                        RealmsMainScreen.hasParentalConsent = true;
                    }
                    else {
                        RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                        RealmsMainScreen.hasParentalConsent = false;
                        Realms.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
                    }
                    RealmsMainScreen.checkedParentalConsent = true;
                }
                catch (RealmsServiceException cvu3) {
                    RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", cvu3.toString());
                    Realms.setScreen(new RealmsGenericErrorScreen(cvu3, RealmsMainScreen.this.lastScreen));
                }
                catch (IOException iOException3) {
                    RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", iOException3.getMessage());
                    Realms.setScreen(new RealmsGenericErrorScreen(iOException3.getMessage(), RealmsMainScreen.this.lastScreen));
                }
            }
        }.start();
    }
    
    private void switchToStage() {
        if (!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
            new Thread("MCO Stage Availability Checker #1") {
                public void run() {
                    final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                    try {
                        final Boolean boolean3 = cvm2.stageAvailable();
                        if (boolean3) {
                            RealmsClient.switchToStage();
                            RealmsMainScreen.LOGGER.info("Switched to stage");
                            RealmsMainScreen.realmsDataFetcher.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException cvu3) {
                        RealmsMainScreen.LOGGER.error(new StringBuilder().append("Couldn't connect to Realms: ").append(cvu3).toString());
                    }
                    catch (IOException iOException3) {
                        RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + iOException3.getMessage());
                    }
                }
            }.start();
        }
    }
    
    private void switchToLocal() {
        if (!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
            new Thread("MCO Local Availability Checker #1") {
                public void run() {
                    final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                    try {
                        final Boolean boolean3 = cvm2.stageAvailable();
                        if (boolean3) {
                            RealmsClient.switchToLocal();
                            RealmsMainScreen.LOGGER.info("Switched to local");
                            RealmsMainScreen.realmsDataFetcher.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException cvu3) {
                        RealmsMainScreen.LOGGER.error(new StringBuilder().append("Couldn't connect to Realms: ").append(cvu3).toString());
                    }
                    catch (IOException iOException3) {
                        RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + iOException3.getMessage());
                    }
                }
            }.start();
        }
    }
    
    private void switchToProd() {
        RealmsClient.switchToProd();
        RealmsMainScreen.realmsDataFetcher.forceUpdate();
    }
    
    private void stopRealmsFetcher() {
        RealmsMainScreen.realmsDataFetcher.stop();
    }
    
    private void configureClicked(final RealmsServer realmsServer) {
        if (Realms.getUUID().equals(realmsServer.ownerUUID) || RealmsMainScreen.overrideConfigure) {
            this.saveListScrollPosition();
            final Minecraft cyc3 = Minecraft.getInstance();
            cyc3.execute(() -> cyc3.setScreen(new RealmsConfigureWorldScreen(this, realmsServer.id).getProxy()));
        }
    }
    
    private void leaveClicked(final RealmsServer realmsServer) {
        if (!Realms.getUUID().equals(realmsServer.ownerUUID)) {
            this.saveListScrollPosition();
            final String string3 = RealmsScreen.getLocalizedString("mco.configure.world.leave.question.line1");
            final String string4 = RealmsScreen.getLocalizedString("mco.configure.world.leave.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, string3, string4, true, 4));
        }
    }
    
    private void saveListScrollPosition() {
        RealmsMainScreen.lastScrollYPosition = this.realmSelectionList.getScroll();
    }
    
    private RealmsServer findServer(final long long1) {
        for (final RealmsServer realmsServer5 : this.realmsServers) {
            if (realmsServer5.id == long1) {
                return realmsServer5;
            }
        }
        return null;
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (integer == 4) {
            if (boolean1) {
                new Thread("Realms-leave-server") {
                    public void run() {
                        try {
                            final RealmsServer realmsServer2 = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                            if (realmsServer2 != null) {
                                final RealmsClient cvm3 = RealmsClient.createRealmsClient();
                                cvm3.uninviteMyselfFrom(realmsServer2.id);
                                RealmsMainScreen.realmsDataFetcher.removeItem(realmsServer2);
                                RealmsMainScreen.this.realmsServers.remove(realmsServer2);
                                RealmsMainScreen.this.selectedServerId = -1L;
                                RealmsMainScreen.this.playButton.active(false);
                            }
                        }
                        catch (RealmsServiceException cvu2) {
                            RealmsMainScreen.LOGGER.error("Couldn't configure world");
                            Realms.setScreen(new RealmsGenericErrorScreen(cvu2, RealmsMainScreen.this));
                        }
                    }
                }.start();
            }
            Realms.setScreen(this);
        }
    }
    
    public void removeSelection() {
        this.selectedServerId = -1L;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 256: {
                this.keyCombos.forEach(KeyCombo::reset);
                this.onClosePopup();
                return true;
            }
            default: {
                return super.keyPressed(integer1, integer2, integer3);
            }
        }
    }
    
    private void onClosePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        }
        else {
            Realms.setScreen(this.lastScreen);
        }
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        this.keyCombos.forEach(cvh -> cvh.keyPressed(character));
        return true;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.expiredHover = false;
        this.toolTip = null;
        this.renderBackground();
        this.realmSelectionList.render(integer1, integer2, float3);
        this.drawRealmsLogo(this.width() / 2 - 50, 7);
        if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
            this.renderStage();
        }
        if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
            this.renderLocal();
        }
        if (this.shouldShowPopup()) {
            this.drawPopup(integer1, integer2);
        }
        else {
            if (this.showingPopup) {
                this.updateButtonStates(null);
                if (!this.hasWidget(this.realmSelectionList)) {
                    this.addWidget(this.realmSelectionList);
                }
                final RealmsServer realmsServer5 = this.findServer(this.selectedServerId);
                this.playButton.active(this.shouldPlayButtonBeActive(realmsServer5));
            }
            this.showingPopup = false;
        }
        super.render(integer1, integer2, float3);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            final int integer3 = 8;
            final int integer4 = 8;
            int integer5 = 0;
            if ((System.currentTimeMillis() / 800L & 0x1L) == 0x1L) {
                integer5 = 8;
            }
            RealmsScreen.blit(this.createTrialButton.x() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y() + this.createTrialButton.getHeight() / 2 - 4, 0.0f, (float)integer5, 8, 8, 8, 16);
            GlStateManager.popMatrix();
        }
    }
    
    private void drawRealmsLogo(final int integer1, final int integer2) {
        RealmsScreen.bind("realms:textures/gui/title/realms.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(integer1 * 2, integer2 * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.isOutsidePopup(double1, double2) && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
            return this.justClosedPopup = true;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    private boolean isOutsidePopup(final double double1, final double double2) {
        final int integer6 = this.popupX0();
        final int integer7 = this.popupY0();
        return double1 < integer6 - 5 || double1 > integer6 + 315 || double2 < integer7 - 5 || double2 > integer7 + 171;
    }
    
    private void drawPopup(final int integer1, final int integer2) {
        final int integer3 = this.popupX0();
        final int integer4 = this.popupY0();
        final String string6 = RealmsScreen.getLocalizedString("mco.selectServer.popup");
        final List<String> list7 = this.fontSplit(string6, 100);
        if (!this.showingPopup) {
            this.carouselIndex = 0;
            this.carouselTick = 0;
            this.hasSwitchedCarouselImage = true;
            this.updateButtonStates(null);
            if (this.hasWidget(this.realmSelectionList)) {
                this.removeWidget(this.realmSelectionList);
            }
            Realms.narrateNow(string6);
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.7f);
        GlStateManager.enableBlend();
        RealmsScreen.bind("realms:textures/gui/realms/darken.png");
        GlStateManager.pushMatrix();
        final int integer5 = 0;
        final int integer6 = 32;
        RealmsScreen.blit(0, 32, 0.0f, 0.0f, this.width(), this.height() - 40 - 32, 310, 166);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RealmsScreen.bind("realms:textures/gui/realms/popup.png");
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer3, integer4, 0.0f, 0.0f, 310, 166, 310, 166);
        GlStateManager.popMatrix();
        RealmsScreen.bind(RealmsMainScreen.IMAGES_LOCATION[this.carouselIndex]);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer3 + 7, integer4 + 7, 0.0f, 0.0f, 195, 152, 195, 152);
        GlStateManager.popMatrix();
        if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
                if (this.carouselIndex == RealmsMainScreen.IMAGES_LOCATION.length - 1) {
                    this.carouselIndex = 0;
                }
                else {
                    ++this.carouselIndex;
                }
                this.hasSwitchedCarouselImage = true;
            }
        }
        else {
            this.hasSwitchedCarouselImage = false;
        }
        int integer7 = 0;
        for (final String string7 : list7) {
            this.drawString(string7, this.width() / 2 + 52, integer4 + 10 * ++integer7 - 3, 5000268, false);
        }
    }
    
    private int popupX0() {
        return (this.width() - 310) / 2;
    }
    
    private int popupY0() {
        return this.height() / 2 - 80;
    }
    
    private void drawInvitationPendingIcon(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5, final boolean boolean6) {
        final int integer5 = this.numberOfPendingInvites;
        final boolean boolean7 = this.inPendingInvitationArea(integer1, integer2);
        final boolean boolean8 = boolean6 && boolean5;
        if (boolean8) {
            final float float11 = 0.25f + (1.0f + RealmsMth.sin(this.animTick * 0.5f)) * 0.25f;
            int integer6 = 0xFF000000 | (int)(float11 * 64.0f) << 16 | (int)(float11 * 64.0f) << 8 | (int)(float11 * 64.0f) << 0;
            this.fillGradient(integer3 - 2, integer4 - 2, integer3 + 18, integer4 + 18, integer6, integer6);
            integer6 = (0xFF000000 | (int)(float11 * 255.0f) << 16 | (int)(float11 * 255.0f) << 8 | (int)(float11 * 255.0f) << 0);
            this.fillGradient(integer3 - 2, integer4 - 2, integer3 + 18, integer4 - 1, integer6, integer6);
            this.fillGradient(integer3 - 2, integer4 - 2, integer3 - 1, integer4 + 18, integer6, integer6);
            this.fillGradient(integer3 + 17, integer4 - 2, integer3 + 18, integer4 + 18, integer6, integer6);
            this.fillGradient(integer3 - 2, integer4 + 17, integer3 + 18, integer4 + 18, integer6, integer6);
        }
        RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        final boolean boolean9 = boolean6 && boolean5;
        RealmsScreen.blit(integer3, integer4 - 6, boolean9 ? 16.0f : 0.0f, 0.0f, 15, 25, 31, 25);
        GlStateManager.popMatrix();
        final boolean boolean10 = boolean6 && integer5 != 0;
        if (boolean10) {
            final int integer7 = (Math.min(integer5, 6) - 1) * 8;
            final int integer8 = (int)(Math.max(0.0f, Math.max(RealmsMth.sin((10 + this.animTick) * 0.57f), RealmsMth.cos(this.animTick * 0.35f))) * -6.0f);
            RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(integer3 + 4, integer4 + 4 + integer8, (float)integer7, boolean7 ? 8.0f : 0.0f, 8, 8, 48, 16);
            GlStateManager.popMatrix();
        }
        final int integer7 = integer1 + 12;
        final int integer8 = integer2;
        final boolean boolean11 = boolean6 && boolean7;
        if (boolean11) {
            final String string16 = RealmsScreen.getLocalizedString((integer5 == 0) ? "mco.invites.nopending" : "mco.invites.pending");
            final int integer9 = this.fontWidth(string16);
            this.fillGradient(integer7 - 3, integer8 - 3, integer7 + integer9 + 3, integer8 + 8 + 3, -1073741824, -1073741824);
            this.fontDrawShadow(string16, integer7, integer8, -1);
        }
    }
    
    private boolean inPendingInvitationArea(final double double1, final double double2) {
        int integer6 = this.width() / 2 + 50;
        int integer7 = this.width() / 2 + 66;
        int integer8 = 11;
        int integer9 = 23;
        if (this.numberOfPendingInvites != 0) {
            integer6 -= 3;
            integer7 += 3;
            integer8 -= 5;
            integer9 += 5;
        }
        return integer6 <= double1 && double1 <= integer7 && integer8 <= double2 && double2 <= integer9;
    }
    
    public void play(final RealmsServer realmsServer, final RealmsScreen realmsScreen) {
        if (realmsServer != null) {
            try {
                if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                if (this.connectLock.getHoldCount() > 1) {
                    return;
                }
            }
            catch (InterruptedException interruptedException4) {
                return;
            }
            this.dontSetConnectedToRealms = true;
            this.connectToServer(realmsServer, realmsScreen);
        }
    }
    
    private void connectToServer(final RealmsServer realmsServer, final RealmsScreen realmsScreen) {
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(realmsScreen, new RealmsTasks.RealmsGetServerDetailsTask(this, realmsScreen, realmsServer, this.connectLock));
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    private boolean isSelfOwnedServer(final RealmsServer realmsServer) {
        return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID());
    }
    
    private boolean isSelfOwnedNonExpiredServer(final RealmsServer realmsServer) {
        return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID()) && !realmsServer.expired;
    }
    
    private void drawExpired(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expired");
        }
    }
    
    private void drawExpiring(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        if (this.animTick % 20 < 10) {
            RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 20, 28);
        }
        else {
            RealmsScreen.blit(integer1, integer2, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            if (integer5 <= 0) {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.soon");
            }
            else if (integer5 == 1) {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.day");
            }
            else {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.days", integer5);
            }
        }
    }
    
    private void drawOpen(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.open");
        }
    }
    
    private void drawClose(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.closed");
        }
    }
    
    private void drawLeave(final int integer1, final int integer2, final int integer3, final int integer4) {
        boolean boolean6 = false;
        if (integer3 >= integer1 && integer3 <= integer1 + 28 && integer4 >= integer2 && integer4 <= integer2 + 28 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            boolean6 = true;
        }
        RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, boolean6 ? 28.0f : 0.0f, 0.0f, 28, 28, 56, 28);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.leave");
        }
    }
    
    private void drawConfigure(final int integer1, final int integer2, final int integer3, final int integer4) {
        boolean boolean6 = false;
        if (integer3 >= integer1 && integer3 <= integer1 + 28 && integer4 >= integer2 && integer4 <= integer2 + 28 && integer4 < this.height() - 40 && integer4 > 32 && !this.shouldShowPopup()) {
            boolean6 = true;
        }
        RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, boolean6 ? 28.0f : 0.0f, 0.0f, 28, 28, 56, 28);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.configure");
        }
    }
    
    protected void renderMousehoverTooltip(final String string, final int integer2, final int integer3) {
        if (string == null) {
            return;
        }
        int integer4 = 0;
        int integer5 = 0;
        for (final String string2 : string.split("\n")) {
            final int integer6 = this.fontWidth(string2);
            if (integer6 > integer5) {
                integer5 = integer6;
            }
        }
        int integer7 = integer2 - integer5 - 5;
        final int integer8 = integer3;
        if (integer7 < 0) {
            integer7 = integer2 + 12;
        }
        for (final String string3 : string.split("\n")) {
            this.fillGradient(integer7 - 3, integer8 - ((integer4 == 0) ? 3 : 0) + integer4, integer7 + integer5 + 3, integer8 + 8 + 3 + integer4, -1073741824, -1073741824);
            this.fontDrawShadow(string3, integer7, integer8 + integer4, 16777215);
            integer4 += 10;
        }
    }
    
    private void renderMoreInfo(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        boolean boolean6 = false;
        if (integer1 >= integer3 && integer1 <= integer3 + 20 && integer2 >= integer4 && integer2 <= integer4 + 20) {
            boolean6 = true;
        }
        RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer3, integer4, boolean5 ? 20.0f : 0.0f, 0.0f, 20, 20, 40, 20);
        GlStateManager.popMatrix();
        if (boolean6) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.info");
        }
    }
    
    private void renderNews(final int integer1, final int integer2, final boolean boolean3, final int integer4, final int integer5, final boolean boolean6, final boolean boolean7) {
        boolean boolean8 = false;
        if (integer1 >= integer4 && integer1 <= integer4 + 20 && integer2 >= integer5 && integer2 <= integer5 + 20) {
            boolean8 = true;
        }
        RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
        if (boolean7) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        else {
            GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        }
        GlStateManager.pushMatrix();
        final boolean boolean9 = boolean7 && boolean6;
        RealmsScreen.blit(integer4, integer5, boolean9 ? 20.0f : 0.0f, 0.0f, 20, 20, 40, 20);
        GlStateManager.popMatrix();
        if (boolean8 && boolean7) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.news");
        }
        if (boolean3 && boolean7) {
            final int integer6 = boolean8 ? 0 : ((int)(Math.max(0.0f, Math.max(RealmsMth.sin((10 + this.animTick) * 0.57f), RealmsMth.cos(this.animTick * 0.35f))) * -6.0f));
            RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(integer4 + 10, integer5 + 2 + integer6, 40.0f, 0.0f, 8, 8, 48, 16);
            GlStateManager.popMatrix();
        }
    }
    
    private void renderLocal() {
        final String string2 = "LOCAL!";
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0f, 0.0f);
        GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scalef(1.5f, 1.5f, 1.5f);
        this.drawString("LOCAL!", 0, 0, 8388479);
        GlStateManager.popMatrix();
    }
    
    private void renderStage() {
        final String string2 = "STAGE!";
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0f, 0.0f);
        GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scalef(1.5f, 1.5f, 1.5f);
        this.drawString("STAGE!", 0, 0, -256);
        GlStateManager.popMatrix();
    }
    
    public RealmsMainScreen newScreen() {
        return new RealmsMainScreen(this.lastScreen);
    }
    
    public void closePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        IMAGES_LOCATION = new String[] { "realms:textures/gui/realms/images/sand_castle.png", "realms:textures/gui/realms/images/factory_floor.png", "realms:textures/gui/realms/images/escher_tunnel.png", "realms:textures/gui/realms/images/tree_houses.png", "realms:textures/gui/realms/images/balloon_trip.png", "realms:textures/gui/realms/images/halloween_woods.png", "realms:textures/gui/realms/images/flower_mountain.png", "realms:textures/gui/realms/images/dornenstein_estate.png", "realms:textures/gui/realms/images/desert.png", "realms:textures/gui/realms/images/gray.png", "realms:textures/gui/realms/images/imperium.png", "realms:textures/gui/realms/images/ludo.png", "realms:textures/gui/realms/images/makersspleef.png", "realms:textures/gui/realms/images/negentropy.png", "realms:textures/gui/realms/images/pumpkin_party.png", "realms:textures/gui/realms/images/sparrenhout.png", "realms:textures/gui/realms/images/spindlewood.png" };
        realmsDataFetcher = new RealmsDataFetcher();
        RealmsMainScreen.lastScrollYPosition = -1;
    }
    
    class RealmSelectionList extends RealmsObjectSelectionList {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 40, 36);
        }
        
        @Override
        public boolean isFocused() {
            return RealmsMainScreen.this.isFocused(this);
        }
        
        @Override
        public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
            if (integer1 != 257 && integer1 != 32 && integer1 != 335) {
                return false;
            }
            final RealmListEntry realmListEntry5 = this.getSelected();
            if (realmListEntry5 == null) {
                return super.keyPressed(integer1, integer2, integer3);
            }
            return realmListEntry5.mouseClicked(0.0, 0.0, 0);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            if (integer == 0 && double1 < this.getScrollbarPosition() && double2 >= this.y0() && double2 <= this.y1()) {
                final int integer2 = RealmsMainScreen.this.realmSelectionList.getRowLeft();
                final int integer3 = this.getScrollbarPosition();
                final int integer4 = (int)Math.floor(double2 - this.y0()) - this.headerHeight() + this.getScroll() - 4;
                final int integer5 = integer4 / this.itemHeight();
                if (double1 >= integer2 && double1 <= integer3 && integer5 >= 0 && integer4 >= 0 && integer5 < this.getItemCount()) {
                    this.itemClicked(integer4, integer5, double1, double2, this.width());
                    RealmsMainScreen.this.clicks += 7;
                    this.selectItem(integer5);
                }
                return true;
            }
            return super.mouseClicked(double1, double2, integer);
        }
        
        @Override
        public void selectItem(final int integer) {
            this.setSelected(integer);
            if (integer == -1) {
                return;
            }
            RealmsServer realmsServer3;
            if (RealmsMainScreen.this.shouldShowMessageInList()) {
                if (integer == 0) {
                    Realms.narrateNow(RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2"));
                    realmsServer3 = null;
                }
                else {
                    realmsServer3 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(integer - 1);
                }
            }
            else {
                realmsServer3 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(integer);
            }
            RealmsMainScreen.this.updateButtonStates(realmsServer3);
            if (realmsServer3 == null) {
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            if (realmsServer3.state == RealmsServer.State.UNINITIALIZED) {
                Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            RealmsMainScreen.this.selectedServerId = realmsServer3.id;
            if (RealmsMainScreen.this.clicks >= 10 && RealmsMainScreen.this.playButton.active()) {
                RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
            }
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", realmsServer3.name));
        }
        
        @Override
        public void itemClicked(final int integer1, int integer2, final double double3, final double double4, final int integer5) {
            if (RealmsMainScreen.this.shouldShowMessageInList()) {
                if (integer2 == 0) {
                    RealmsMainScreen.this.popupOpenedByUser = true;
                    return;
                }
                --integer2;
            }
            if (integer2 >= RealmsMainScreen.this.realmsServers.size()) {
                return;
            }
            final RealmsServer realmsServer9 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(integer2);
            if (realmsServer9 == null) {
                return;
            }
            if (realmsServer9.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                Realms.setScreen(new RealmsCreateRealmScreen(realmsServer9, RealmsMainScreen.this));
            }
            else {
                RealmsMainScreen.this.selectedServerId = realmsServer9.id;
            }
            if (RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
                RealmsMainScreen.this.selectedServerId = realmsServer9.id;
                RealmsMainScreen.this.configureClicked(realmsServer9);
            }
            else if (RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
                RealmsMainScreen.this.selectedServerId = realmsServer9.id;
                RealmsMainScreen.this.leaveClicked(realmsServer9);
            }
            else if (RealmsMainScreen.this.isSelfOwnedServer(realmsServer9) && realmsServer9.expired && RealmsMainScreen.this.expiredHover) {
                RealmsMainScreen.this.onRenew();
            }
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }
        
        @Override
        public int getRowWidth() {
            return 300;
        }
    }
    
    class RealmSelectionListTrialEntry extends RealmListEntry {
        public RealmSelectionListTrialEntry() {
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderTrialItem(integer1, integer3, integer2, integer6, integer7);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            RealmsMainScreen.this.popupOpenedByUser = true;
            return true;
        }
        
        private void renderTrialItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
            final int integer6 = integer3 + 8;
            int integer7 = 0;
            final String string9 = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
            boolean boolean10 = false;
            if (integer2 <= integer4 && integer4 <= RealmsMainScreen.this.realmSelectionList.getScroll() && integer3 <= integer5 && integer5 <= integer3 + 32) {
                boolean10 = true;
            }
            int integer8 = 8388479;
            if (boolean10 && !RealmsMainScreen.this.shouldShowPopup()) {
                integer8 = 6077788;
            }
            for (final String string10 : string9.split("\\\\n")) {
                RealmsMainScreen.this.drawCenteredString(string10, RealmsMainScreen.this.width() / 2, integer6 + integer7, integer8);
                integer7 += 10;
            }
        }
    }
    
    class RealmSelectionListEntry extends RealmListEntry {
        final RealmsServer mServerData;
        
        public RealmSelectionListEntry(final RealmsServer realmsServer) {
            this.mServerData = realmsServer;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderMcoServerItem(this.mServerData, integer3, integer2, integer6, integer7);
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            if (this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                Realms.setScreen(new RealmsCreateRealmScreen(this.mServerData, RealmsMainScreen.this));
            }
            else {
                RealmsMainScreen.this.selectedServerId = this.mServerData.id;
            }
            return true;
        }
        
        private void renderMcoServerItem(final RealmsServer realmsServer, final int integer2, final int integer3, final int integer4, final int integer5) {
            this.renderLegacy(realmsServer, integer2 + 36, integer3, integer4, integer5);
        }
        
        private void renderLegacy(final RealmsServer realmsServer, final int integer2, final int integer3, final int integer4, final int integer5) {
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlphaTest();
                GlStateManager.pushMatrix();
                RealmsScreen.blit(integer2 + 10, integer3 + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                GlStateManager.popMatrix();
                final float float7 = 0.5f + (1.0f + RealmsMth.sin(RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                final int integer6 = 0xFF000000 | (int)(127.0f * float7) << 16 | (int)(255.0f * float7) << 8 | (int)(127.0f * float7);
                RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), integer2 + 10 + 40 + 75, integer3 + 12, integer6);
                return;
            }
            final int integer7 = 225;
            final int integer6 = 2;
            if (realmsServer.expired) {
                RealmsMainScreen.this.drawExpired(integer2 + 225 - 14, integer3 + 2, integer4, integer5);
            }
            else if (realmsServer.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(integer2 + 225 - 14, integer3 + 2, integer4, integer5);
            }
            else if (RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.daysLeft < 7) {
                RealmsMainScreen.this.drawExpiring(integer2 + 225 - 14, integer3 + 2, integer4, integer5, realmsServer.daysLeft);
            }
            else if (realmsServer.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(integer2 + 225 - 14, integer3 + 2, integer4, integer5);
            }
            if (!RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && !RealmsMainScreen.overrideConfigure) {
                RealmsMainScreen.this.drawLeave(integer2 + 225, integer3 + 2, integer4, integer5);
            }
            else {
                RealmsMainScreen.this.drawConfigure(integer2 + 225, integer3 + 2, integer4, integer5);
            }
            if (!"0".equals(realmsServer.serverPing.nrOfPlayers)) {
                final String string9 = new StringBuilder().append(ChatFormatting.GRAY).append("").append(realmsServer.serverPing.nrOfPlayers).toString();
                RealmsMainScreen.this.drawString(string9, integer2 + 207 - RealmsMainScreen.this.fontWidth(string9), integer3 + 3, 8421504);
                if (integer4 >= integer2 + 207 - RealmsMainScreen.this.fontWidth(string9) && integer4 <= integer2 + 207 && integer5 >= integer3 + 1 && integer5 <= integer3 + 10 && integer5 < RealmsMainScreen.this.height() - 40 && integer5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.toolTip = realmsServer.serverPing.playerList;
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.expired) {
                boolean boolean9 = false;
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableBlend();
                RealmsScreen.bind("minecraft:textures/gui/widgets.png");
                GlStateManager.pushMatrix();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                String string10 = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
                String string11 = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
                if (realmsServer.expiredTrial) {
                    string10 = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
                    string11 = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
                }
                final int integer8 = RealmsMainScreen.this.fontWidth(string11) + 17;
                final int integer9 = 16;
                final int integer10 = integer2 + RealmsMainScreen.this.fontWidth(string10) + 8;
                final int integer11 = integer3 + 13;
                if (integer4 >= integer10 && integer4 < integer10 + integer8 && integer5 > integer11 && (integer5 <= integer11 + 16 & integer5 < RealmsMainScreen.this.height() - 40) && integer5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    boolean9 = true;
                    RealmsMainScreen.this.expiredHover = true;
                }
                final int integer12 = boolean9 ? 2 : 1;
                RealmsScreen.blit(integer10, integer11, 0.0f, (float)(46 + integer12 * 20), integer8 / 2, 8, 256, 256);
                RealmsScreen.blit(integer10 + integer8 / 2, integer11, (float)(200 - integer8 / 2), (float)(46 + integer12 * 20), integer8 / 2, 8, 256, 256);
                RealmsScreen.blit(integer10, integer11 + 8, 0.0f, (float)(46 + integer12 * 20 + 12), integer8 / 2, 8, 256, 256);
                RealmsScreen.blit(integer10 + integer8 / 2, integer11 + 8, (float)(200 - integer8 / 2), (float)(46 + integer12 * 20 + 12), integer8 / 2, 8, 256, 256);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                final int integer13 = integer3 + 11 + 5;
                final int integer14 = boolean9 ? 16777120 : 16777215;
                RealmsMainScreen.this.drawString(string10, integer2 + 2, integer13 + 1, 15553363);
                RealmsMainScreen.this.drawCenteredString(string11, integer10 + integer8 / 2, integer13 + 1, integer14);
            }
            else {
                if (realmsServer.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                    final int integer15 = 13413468;
                    final String string10 = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                    final int integer16 = RealmsMainScreen.this.fontWidth(string10);
                    RealmsMainScreen.this.drawString(string10, integer2 + 2, integer3 + 12, 13413468);
                    RealmsMainScreen.this.drawString(realmsServer.getMinigameName(), integer2 + 2 + integer16, integer3 + 12, 7105644);
                }
                else {
                    RealmsMainScreen.this.drawString(realmsServer.getDescription(), integer2 + 2, integer3 + 12, 7105644);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(realmsServer)) {
                    RealmsMainScreen.this.drawString(realmsServer.owner, integer2 + 2, integer3 + 12 + 11, 5000268);
                }
            }
            RealmsMainScreen.this.drawString(realmsServer.getName(), integer2 + 2, integer3 + 1, 16777215);
            RealmsTextureManager.withBoundFace(realmsServer.ownerUUID, () -> {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(integer2 - 36, integer3, 8.0f, 8.0f, 8, 8, 32, 32, 64, 64);
                RealmsScreen.blit(integer2 - 36, integer3, 40.0f, 8.0f, 8, 8, 32, 32, 64, 64);
            });
        }
    }
    
    class PendingInvitesButton extends RealmsButton {
        public PendingInvitesButton() {
            super(8, RealmsMainScreen.this.width() / 2 + 47, 6, 22, 22, "");
        }
        
        @Override
        public void tick() {
            this.setMessage(Realms.getLocalizedString((RealmsMainScreen.this.numberOfPendingInvites == 0) ? "mco.invites.nopending" : "mco.invites.pending"));
        }
        
        @Override
        public void render(final int integer1, final int integer2, final float float3) {
            super.render(integer1, integer2, float3);
        }
        
        @Override
        public void onPress() {
            final RealmsPendingInvitesScreen cwr2 = new RealmsPendingInvitesScreen(RealmsMainScreen.this.lastScreen);
            Realms.setScreen(cwr2);
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            RealmsMainScreen.this.drawInvitationPendingIcon(integer1, integer2, this.x(), this.y(), this.getProxy().isHovered(), this.active());
        }
    }
    
    class NewsButton extends RealmsButton {
        public NewsButton() {
            super(9, RealmsMainScreen.this.width() - 62, 6, 20, 20, "");
        }
        
        @Override
        public void tick() {
            this.setMessage(Realms.getLocalizedString("mco.news"));
        }
        
        @Override
        public void render(final int integer1, final int integer2, final float float3) {
            super.render(integer1, integer2, float3);
        }
        
        @Override
        public void onPress() {
            if (RealmsMainScreen.this.newsLink == null) {
                return;
            }
            RealmsUtil.browseTo(RealmsMainScreen.this.newsLink);
            if (RealmsMainScreen.this.hasUnreadNews) {
                final RealmsPersistence.RealmsPersistenceData a2 = RealmsPersistence.readFile();
                a2.hasUnreadNews = false;
                RealmsMainScreen.this.hasUnreadNews = false;
                RealmsPersistence.writeFile(a2);
            }
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            RealmsMainScreen.this.renderNews(integer1, integer2, RealmsMainScreen.this.hasUnreadNews, this.x(), this.y(), this.getProxy().isHovered(), this.active());
        }
    }
    
    class ShowPopupButton extends RealmsButton {
        public ShowPopupButton() {
            super(10, RealmsMainScreen.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
        }
        
        @Override
        public void tick() {
            super.tick();
        }
        
        @Override
        public void render(final int integer1, final int integer2, final float float3) {
            super.render(integer1, integer2, float3);
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            RealmsMainScreen.this.renderMoreInfo(integer1, integer2, this.x(), this.y(), this.getProxy().isHovered());
        }
        
        @Override
        public void onPress() {
            RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser;
        }
    }
    
    class CloseButton extends RealmsButton {
        public CloseButton() {
            super(11, RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
        }
        
        @Override
        public void tick() {
            super.tick();
        }
        
        @Override
        public void render(final int integer1, final int integer2, final float float3) {
            super.render(integer1, integer2, float3);
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(this.x(), this.y(), 0.0f, this.getProxy().isHovered() ? 12.0f : 0.0f, 12, 12, 12, 24);
            GlStateManager.popMatrix();
            if (this.getProxy().isMouseOver(integer1, integer2)) {
                RealmsMainScreen.this.toolTip = this.getProxy().getMessage();
            }
        }
        
        @Override
        public void onPress() {
            RealmsMainScreen.this.onClosePopup();
        }
    }
}

package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.Tezzelator;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.ArrayList;
import com.mojang.realmsclient.client.FileDownload;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.AbstractRealmsButton;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.RealmsButton;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.realmsclient.dto.WorldDownload;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final WorldDownload worldDownload;
    private final String downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private RealmsButton cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    private volatile String errorMessage;
    private volatile String status;
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots;
    private volatile boolean finished;
    private volatile boolean extracting;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS;
    private int dotIndex;
    private final int WARNING_ID = 100;
    private int confirmationId;
    private boolean checked;
    private static final ReentrantLock downloadLock;
    
    public RealmsDownloadLatestWorldScreen(final RealmsScreen realmsScreen, final WorldDownload worldDownload, final String string) {
        this.showDots = true;
        this.confirmationId = -1;
        this.lastScreen = realmsScreen;
        this.worldName = string;
        this.worldDownload = worldDownload;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = RealmsScreen.getLocalizedString("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
    }
    
    public void setConfirmationId(final int integer) {
        this.confirmationId = integer;
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                RealmsDownloadLatestWorldScreen.this.cancelled = true;
                RealmsDownloadLatestWorldScreen.this.backButtonClicked();
            }
        });
        this.checkDownloadSize();
    }
    
    private void checkDownloadSize() {
        if (this.finished) {
            return;
        }
        if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            final String string2 = RealmsScreen.getLocalizedString("mco.download.confirmation.line1", humanReadableSize(5368709120L));
            final String string3 = RealmsScreen.getLocalizedString("mco.download.confirmation.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, string2, string3, false, 100));
        }
        else {
            this.downloadSave();
        }
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        this.checked = true;
        Realms.setScreen(this);
        this.downloadSave();
    }
    
    private long getContentLength(final String string) {
        final FileDownload cvj3 = new FileDownload();
        return cvj3.contentLength(string);
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            final ArrayList<String> arrayList2 = (ArrayList<String>)new ArrayList();
            arrayList2.add(this.downloadTitle);
            arrayList2.add(this.status);
            if (this.progress != null) {
                arrayList2.add((this.progress + "%"));
                arrayList2.add(humanReadableSpeed(this.bytesPersSecond));
            }
            if (this.errorMessage != null) {
                arrayList2.add(this.errorMessage);
            }
            final String string3 = String.join((CharSequence)System.lineSeparator(), (Iterable)arrayList2);
            Realms.narrateNow(string3);
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void backButtonClicked() {
        if (this.finished && this.confirmationId != -1 && this.errorMessage == null) {
            this.lastScreen.confirmResult(true, this.confirmationId);
        }
        Realms.setScreen(this.lastScreen);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        if (this.extracting && !this.finished) {
            this.status = RealmsScreen.getLocalizedString("mco.download.extracting");
        }
        this.drawCenteredString(this.downloadTitle, this.width() / 2, 20, 16777215);
        this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
        if (this.showDots) {
            this.drawDots();
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar();
            this.drawDownloadSpeed();
        }
        if (this.errorMessage != null) {
            this.drawCenteredString(this.errorMessage, this.width() / 2, 110, 16711680);
        }
        super.render(integer1, integer2, float3);
    }
    
    private void drawDots() {
        final int integer2 = this.fontWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.drawString(RealmsDownloadLatestWorldScreen.DOTS[this.dotIndex % RealmsDownloadLatestWorldScreen.DOTS.length], this.width() / 2 + integer2 / 2 + 5, 50, 16777215);
    }
    
    private void drawProgressBar() {
        final double double2 = this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes * 100.0;
        this.progress = String.format(Locale.ROOT, "%.1f", new Object[] { double2 });
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableTexture();
        final Tezzelator tezzelator4 = Tezzelator.instance;
        tezzelator4.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        final double double3 = this.width() / 2 - 100;
        final double double4 = 0.5;
        tezzelator4.vertex(double3 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator4.vertex(double3 + 200.0 * double2 / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator4.vertex(double3 + 200.0 * double2 / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator4.vertex(double3 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator4.vertex(double3, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator4.vertex(double3 + 200.0 * double2 / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator4.vertex(double3 + 200.0 * double2 / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator4.vertex(double3, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator4.end();
        GlStateManager.enableTexture();
        this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
    }
    
    private void drawDownloadSpeed() {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long long2 = System.currentTimeMillis() - this.previousTimeSnapshot;
                if (long2 == 0L) {
                    long2 = 1L;
                }
                this.drawDownloadSpeed0(this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / long2);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = System.currentTimeMillis();
        }
        else {
            this.drawDownloadSpeed0(this.bytesPersSecond);
        }
    }
    
    private void drawDownloadSpeed0(final long long1) {
        if (long1 > 0L) {
            final int integer4 = this.fontWidth(this.progress);
            final String string5 = "(" + humanReadableSpeed(long1) + ")";
            this.drawString(string5, this.width() / 2 + integer4 / 2 + 15, 84, 16777215);
        }
    }
    
    public static String humanReadableSpeed(final long long1) {
        final int integer3 = 1024;
        if (long1 < 1024L) {
            return new StringBuilder().append(long1).append(" B/s").toString();
        }
        final int integer4 = (int)(Math.log((double)long1) / Math.log(1024.0));
        final String string5 = new StringBuilder().append("KMGTPE".charAt(integer4 - 1)).append("").toString();
        return String.format(Locale.ROOT, "%.1f %sB/s", new Object[] { long1 / Math.pow(1024.0, (double)integer4), string5 });
    }
    
    public static String humanReadableSize(final long long1) {
        final int integer3 = 1024;
        if (long1 < 1024L) {
            return new StringBuilder().append(long1).append(" B").toString();
        }
        final int integer4 = (int)(Math.log((double)long1) / Math.log(1024.0));
        final String string5 = new StringBuilder().append("KMGTPE".charAt(integer4 - 1)).append("").toString();
        return String.format(Locale.ROOT, "%.0f %sB", new Object[] { long1 / Math.pow(1024.0, (double)integer4), string5 });
    }
    
    private void downloadSave() {
        new Thread() {
            public void run() {
                try {
                    if (!RealmsDownloadLatestWorldScreen.downloadLock.tryLock(1L, TimeUnit.SECONDS)) {
                        return;
                    }
                    RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.preparing");
                    if (RealmsDownloadLatestWorldScreen.this.cancelled) {
                        RealmsDownloadLatestWorldScreen.this.downloadCancelled();
                        return;
                    }
                    RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.downloading", RealmsDownloadLatestWorldScreen.this.worldName);
                    final FileDownload cvj2 = new FileDownload();
                    cvj2.contentLength(RealmsDownloadLatestWorldScreen.this.worldDownload.downloadLink);
                    cvj2.download(RealmsDownloadLatestWorldScreen.this.worldDownload, RealmsDownloadLatestWorldScreen.this.worldName, RealmsDownloadLatestWorldScreen.this.downloadStatus, RealmsDownloadLatestWorldScreen.this.getLevelStorageSource());
                    while (!cvj2.isFinished()) {
                        if (cvj2.isError()) {
                            cvj2.cancel();
                            RealmsDownloadLatestWorldScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
                            RealmsDownloadLatestWorldScreen.this.cancelButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                            return;
                        }
                        if (cvj2.isExtracting()) {
                            RealmsDownloadLatestWorldScreen.this.extracting = true;
                        }
                        if (RealmsDownloadLatestWorldScreen.this.cancelled) {
                            cvj2.cancel();
                            RealmsDownloadLatestWorldScreen.this.downloadCancelled();
                            return;
                        }
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException interruptedException3) {
                            RealmsDownloadLatestWorldScreen.LOGGER.error("Failed to check Realms backup download status");
                        }
                    }
                    RealmsDownloadLatestWorldScreen.this.finished = true;
                    RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.done");
                    RealmsDownloadLatestWorldScreen.this.cancelButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                }
                catch (InterruptedException interruptedException4) {
                    RealmsDownloadLatestWorldScreen.LOGGER.error("Could not acquire upload lock");
                }
                catch (Exception exception2) {
                    RealmsDownloadLatestWorldScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
                    exception2.printStackTrace();
                }
                finally {
                    if (!RealmsDownloadLatestWorldScreen.downloadLock.isHeldByCurrentThread()) {
                        return;
                    }
                    RealmsDownloadLatestWorldScreen.downloadLock.unlock();
                    RealmsDownloadLatestWorldScreen.this.showDots = false;
                    RealmsDownloadLatestWorldScreen.this.finished = true;
                }
            }
        }.start();
    }
    
    private void downloadCancelled() {
        this.status = RealmsScreen.getLocalizedString("mco.download.cancelled");
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DOTS = new String[] { "", ".", ". .", ". . ." };
        downloadLock = new ReentrantLock();
    }
    
    public class DownloadStatus {
        public volatile Long bytesWritten;
        public volatile Long totalBytes;
        
        public DownloadStatus() {
            this.bytesWritten = 0L;
            this.totalBytes = 0L;
        }
    }
}

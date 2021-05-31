package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import java.io.InputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.FileInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.FileOutputStream;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.io.IOException;
import java.util.function.Consumer;
import com.mojang.realmsclient.client.FileUpload;
import java.io.File;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.util.concurrent.TimeUnit;
import com.mojang.realmsclient.client.RealmsClient;
import java.util.ArrayList;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.Tezzelator;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import net.minecraft.realms.Realms;
import net.minecraft.realms.AbstractRealmsButton;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.RealmsButton;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.realmsclient.client.UploadStatus;
import net.minecraft.realms.RealmsLevelSummary;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsUploadScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsResetWorldScreen lastScreen;
    private final RealmsLevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    private volatile String errorMessage;
    private volatile String status;
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots;
    private volatile boolean uploadStarted;
    private RealmsButton backButton;
    private RealmsButton cancelButton;
    private int animTick;
    private static final String[] DOTS;
    private int dotIndex;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private static final ReentrantLock uploadLock;
    
    public RealmsUploadScreen(final long long1, final int integer, final RealmsResetWorldScreen cwu, final RealmsLevelSummary realmsLevelSummary) {
        this.showDots = true;
        this.worldId = long1;
        this.slotId = integer;
        this.lastScreen = cwu;
        this.selectedLevel = realmsLevelSummary;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.backButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                RealmsUploadScreen.this.onBack();
            }
        };
        this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                RealmsUploadScreen.this.onCancel();
            }
        });
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.upload();
            }
            else {
                this.lastScreen.switchSlot(this);
            }
        }
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (boolean1 && !this.uploadStarted) {
            this.uploadStarted = true;
            Realms.setScreen(this);
            this.upload();
        }
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    private void onBack() {
        this.lastScreen.confirmResult(true, 0);
    }
    
    private void onCancel() {
        this.cancelled = true;
        Realms.setScreen(this.lastScreen);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            if (this.showDots) {
                this.onCancel();
            }
            else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == (long)this.uploadStatus.totalBytes) {
            this.status = RealmsScreen.getLocalizedString("mco.upload.verifying");
            this.cancelButton.active(false);
        }
        this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
        if (this.showDots) {
            this.drawDots();
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar();
            this.drawUploadSpeed();
        }
        if (this.errorMessage != null) {
            final String[] arr5 = this.errorMessage.split("\\\\n");
            for (int integer3 = 0; integer3 < arr5.length; ++integer3) {
                this.drawCenteredString(arr5[integer3], this.width() / 2, 110 + 12 * integer3, 16711680);
            }
        }
        super.render(integer1, integer2, float3);
    }
    
    private void drawDots() {
        final int integer2 = this.fontWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.drawString(RealmsUploadScreen.DOTS[this.dotIndex % RealmsUploadScreen.DOTS.length], this.width() / 2 + integer2 / 2 + 5, 50, 16777215);
    }
    
    private void drawProgressBar() {
        double double2 = this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes * 100.0;
        if (double2 > 100.0) {
            double2 = 100.0;
        }
        this.progress = String.format(Locale.ROOT, "%.1f", new Object[] { double2 });
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableTexture();
        final double double3 = this.width() / 2 - 100;
        final double double4 = 0.5;
        final Tezzelator tezzelator8 = Tezzelator.instance;
        tezzelator8.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        tezzelator8.vertex(double3 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator8.vertex(double3 + 200.0 * double2 / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator8.vertex(double3 + 200.0 * double2 / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator8.vertex(double3 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator8.vertex(double3, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator8.vertex(double3 + 200.0 * double2 / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator8.vertex(double3 + 200.0 * double2 / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator8.vertex(double3, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator8.end();
        GlStateManager.enableTexture();
        this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
    }
    
    private void drawUploadSpeed() {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long long2 = System.currentTimeMillis() - this.previousTimeSnapshot;
                if (long2 == 0L) {
                    long2 = 1L;
                }
                this.drawUploadSpeed0(this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / long2);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = System.currentTimeMillis();
        }
        else {
            this.drawUploadSpeed0(this.bytesPersSecond);
        }
    }
    
    private void drawUploadSpeed0(final long long1) {
        if (long1 > 0L) {
            final int integer4 = this.fontWidth(this.progress);
            final String string5 = "(" + humanReadableByteCount(long1) + ")";
            this.drawString(string5, this.width() / 2 + integer4 / 2 + 15, 84, 16777215);
        }
    }
    
    public static String humanReadableByteCount(final long long1) {
        final int integer3 = 1024;
        if (long1 < 1024L) {
            return new StringBuilder().append(long1).append(" B").toString();
        }
        final int integer4 = (int)(Math.log((double)long1) / Math.log(1024.0));
        final String string5 = new StringBuilder().append("KMGTPE".charAt(integer4 - 1)).append("").toString();
        return String.format(Locale.ROOT, "%.1f %sB/s", new Object[] { long1 / Math.pow(1024.0, (double)integer4), string5 });
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            final ArrayList<String> arrayList2 = (ArrayList<String>)new ArrayList();
            arrayList2.add(this.status);
            if (this.progress != null) {
                arrayList2.add((this.progress + "%"));
            }
            if (this.errorMessage != null) {
                arrayList2.add(this.errorMessage);
            }
            Realms.narrateNow(String.join((CharSequence)System.lineSeparator(), (Iterable)arrayList2));
        }
    }
    
    public static Unit getLargestUnit(final long long1) {
        if (long1 < 1024L) {
            return Unit.B;
        }
        final int integer3 = (int)(Math.log((double)long1) / Math.log(1024.0));
        final String string4 = new StringBuilder().append("KMGTPE".charAt(integer3 - 1)).append("").toString();
        try {
            return Unit.valueOf(string4 + "B");
        }
        catch (Exception exception5) {
            return Unit.GB;
        }
    }
    
    public static double convertToUnit(final long long1, final Unit a) {
        if (a.equals(Unit.B)) {
            return (double)long1;
        }
        return long1 / Math.pow(1024.0, (double)a.ordinal());
    }
    
    public static String humanReadableSize(final long long1, final Unit a) {
        return String.format(new StringBuilder().append("%.").append(a.equals(Unit.GB) ? "1" : "0").append("f %s").toString(), new Object[] { convertToUnit(long1, a), a.name() });
    }
    
    private void upload() {
        this.uploadStarted = true;
        new Thread() {
            public void run() {
                File file2 = null;
                final RealmsClient cvm3 = RealmsClient.createRealmsClient();
                final long long4 = RealmsUploadScreen.this.worldId;
                try {
                    if (!RealmsUploadScreen.uploadLock.tryLock(1L, TimeUnit.SECONDS)) {
                        return;
                    }
                    RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.preparing");
                    UploadInfo uploadInfo6 = null;
                    int integer7 = 0;
                    while (integer7 < 20) {
                        try {
                            if (RealmsUploadScreen.this.cancelled) {
                                RealmsUploadScreen.this.uploadCancelled();
                                return;
                            }
                            uploadInfo6 = cvm3.upload(long4, UploadTokenCache.get(long4));
                        }
                        catch (RetryCallException cvv8) {
                            Thread.sleep((long)(cvv8.delaySeconds * 1000));
                            ++integer7;
                            continue;
                        }
                        break;
                    }
                    if (uploadInfo6 == null) {
                        RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                        return;
                    }
                    UploadTokenCache.put(long4, uploadInfo6.getToken());
                    if (!uploadInfo6.isWorldClosed()) {
                        RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                        return;
                    }
                    if (RealmsUploadScreen.this.cancelled) {
                        RealmsUploadScreen.this.uploadCancelled();
                        return;
                    }
                    final File file3 = new File(Realms.getGameDirectoryPath(), "saves");
                    file2 = RealmsUploadScreen.this.tarGzipArchive(new File(file3, RealmsUploadScreen.this.selectedLevel.getLevelId()));
                    if (RealmsUploadScreen.this.cancelled) {
                        RealmsUploadScreen.this.uploadCancelled();
                        return;
                    }
                    if (RealmsUploadScreen.this.verify(file2)) {
                        RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.uploading", RealmsUploadScreen.this.selectedLevel.getLevelName());
                        final FileUpload cvk8 = new FileUpload(file2, RealmsUploadScreen.this.worldId, RealmsUploadScreen.this.slotId, uploadInfo6, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), RealmsUploadScreen.this.uploadStatus);
                        cvk8.upload((Consumer<UploadResult>)(cxe -> {
                            if (cxe.statusCode >= 200 && cxe.statusCode < 300) {
                                RealmsUploadScreen.this.uploadFinished = true;
                                RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.done");
                                RealmsUploadScreen.this.backButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                                UploadTokenCache.invalidate(long4);
                            }
                            else if (cxe.statusCode == 400 && cxe.errorMessage != null) {
                                RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", cxe.errorMessage);
                            }
                            else {
                                RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", cxe.statusCode);
                            }
                        }));
                        while (!cvk8.isFinished()) {
                            if (RealmsUploadScreen.this.cancelled) {
                                cvk8.cancel();
                                RealmsUploadScreen.this.uploadCancelled();
                                return;
                            }
                            try {
                                Thread.sleep(500L);
                            }
                            catch (InterruptedException interruptedException9) {
                                RealmsUploadScreen.LOGGER.error("Failed to check Realms file upload status");
                            }
                        }
                        return;
                    }
                    final long long5 = file2.length();
                    final Unit a10 = RealmsUploadScreen.getLargestUnit(long5);
                    final Unit a11 = RealmsUploadScreen.getLargestUnit(5368709120L);
                    if (RealmsUploadScreen.humanReadableSize(long5, a10).equals(RealmsUploadScreen.humanReadableSize(5368709120L, a11)) && a10 != Unit.B) {
                        final Unit a12 = Unit.values()[a10.ordinal() - 1];
                        RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", RealmsUploadScreen.this.selectedLevel.getLevelName()) + "\\n" + RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(long5, a12), RealmsUploadScreen.humanReadableSize(5368709120L, a12));
                        return;
                    }
                    RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", RealmsUploadScreen.this.selectedLevel.getLevelName()) + "\\n" + RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(long5, a10), RealmsUploadScreen.humanReadableSize(5368709120L, a11));
                }
                catch (IOException iOException6) {
                    RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", iOException6.getMessage());
                }
                catch (RealmsServiceException cvu6) {
                    RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", cvu6.toString());
                }
                catch (InterruptedException interruptedException10) {
                    RealmsUploadScreen.LOGGER.error("Could not acquire upload lock");
                }
                finally {
                    RealmsUploadScreen.this.uploadFinished = true;
                    if (!RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
                        return;
                    }
                    RealmsUploadScreen.uploadLock.unlock();
                    RealmsUploadScreen.this.showDots = false;
                    RealmsUploadScreen.this.childrenClear();
                    RealmsUploadScreen.this.buttonsAdd(RealmsUploadScreen.this.backButton);
                    if (file2 != null) {
                        RealmsUploadScreen.LOGGER.debug("Deleting file " + file2.getAbsolutePath());
                        file2.delete();
                    }
                }
            }
        }.start();
    }
    
    private void uploadCancelled() {
        this.status = RealmsScreen.getLocalizedString("mco.upload.cancelled");
        RealmsUploadScreen.LOGGER.debug("Upload was cancelled");
    }
    
    private boolean verify(final File file) {
        return file.length() < 5368709120L;
    }
    
    private File tarGzipArchive(final File file) throws IOException {
        TarArchiveOutputStream tarArchiveOutputStream3 = null;
        try {
            final File file2 = File.createTempFile("realms-upload-file", ".tar.gz");
            tarArchiveOutputStream3 = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream((OutputStream)new FileOutputStream(file2)));
            tarArchiveOutputStream3.setLongFileMode(3);
            this.addFileToTarGz(tarArchiveOutputStream3, file.getAbsolutePath(), "world", true);
            tarArchiveOutputStream3.finish();
            return file2;
        }
        finally {
            if (tarArchiveOutputStream3 != null) {
                tarArchiveOutputStream3.close();
            }
        }
    }
    
    private void addFileToTarGz(final TarArchiveOutputStream tarArchiveOutputStream, final String string2, final String string3, final boolean boolean4) throws IOException {
        if (this.cancelled) {
            return;
        }
        final File file6 = new File(string2);
        final String string4 = boolean4 ? string3 : (string3 + file6.getName());
        final TarArchiveEntry tarArchiveEntry8 = new TarArchiveEntry(file6, string4);
        tarArchiveOutputStream.putArchiveEntry((ArchiveEntry)tarArchiveEntry8);
        if (file6.isFile()) {
            IOUtils.copy((InputStream)new FileInputStream(file6), (OutputStream)tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }
        else {
            tarArchiveOutputStream.closeArchiveEntry();
            final File[] arr9 = file6.listFiles();
            if (arr9 != null) {
                for (final File file7 : arr9) {
                    this.addFileToTarGz(tarArchiveOutputStream, file7.getAbsolutePath(), string4 + "/", false);
                }
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DOTS = new String[] { "", ".", ". .", ". . ." };
        uploadLock = new ReentrantLock();
    }
    
    enum Unit {
        B, 
        KB, 
        MB, 
        GB;
    }
}

package com.mojang.realmsclient.client;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.FileUtils;
import com.google.common.io.Files;
import com.google.common.hash.Hashing;
import java.awt.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.io.BufferedOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import net.minecraft.realms.Realms;
import java.util.Locale;
import net.minecraft.realms.RealmsLevelSummary;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.realms.RealmsSharedConstants;
import java.util.regex.Pattern;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import org.apache.http.HttpResponse;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.dto.WorldDownload;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import java.io.IOException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class FileDownload {
    private static final Logger LOGGER;
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile File tempFile;
    private volatile File resourcePackPath;
    private volatile HttpGet request;
    private Thread currentThread;
    private final RequestConfig requestConfig;
    private static final String[] INVALID_FILE_NAMES;
    
    public FileDownload() {
        this.requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    }
    
    public long contentLength(final String string) {
        CloseableHttpClient closeableHttpClient3 = null;
        HttpGet httpGet4 = null;
        try {
            httpGet4 = new HttpGet(string);
            closeableHttpClient3 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            final CloseableHttpResponse closeableHttpResponse5 = closeableHttpClient3.execute((HttpUriRequest)httpGet4);
            return Long.parseLong(closeableHttpResponse5.getFirstHeader("Content-Length").getValue());
        }
        catch (Throwable throwable5) {
            FileDownload.LOGGER.error("Unable to get content length for download");
            return 0L;
        }
        finally {
            if (httpGet4 != null) {
                httpGet4.releaseConnection();
            }
            if (closeableHttpClient3 != null) {
                try {
                    closeableHttpClient3.close();
                }
                catch (IOException iOException10) {
                    FileDownload.LOGGER.error("Could not close http client", (Throwable)iOException10);
                }
            }
        }
    }
    
    public void download(final WorldDownload worldDownload, final String string, final RealmsDownloadLatestWorldScreen.DownloadStatus a, final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) {
        if (this.currentThread != null) {
            return;
        }
        (this.currentThread = new Thread() {
            public void run() {
                CloseableHttpClient closeableHttpClient2 = null;
                try {
                    FileDownload.this.tempFile = File.createTempFile("backup", ".tar.gz");
                    FileDownload.this.request = new HttpGet(worldDownload.downloadLink);
                    closeableHttpClient2 = HttpClientBuilder.create().setDefaultRequestConfig(FileDownload.this.requestConfig).build();
                    final HttpResponse httpResponse3 = (HttpResponse)closeableHttpClient2.execute((HttpUriRequest)FileDownload.this.request);
                    a.totalBytes = Long.parseLong(httpResponse3.getFirstHeader("Content-Length").getValue());
                    if (httpResponse3.getStatusLine().getStatusCode() != 200) {
                        FileDownload.this.error = true;
                        FileDownload.this.request.abort();
                        return;
                    }
                    final OutputStream outputStream4 = (OutputStream)new FileOutputStream(FileDownload.this.tempFile);
                    final ProgressListener b5 = new ProgressListener(string.trim(), FileDownload.this.tempFile, realmsAnvilLevelStorageSource, a, worldDownload);
                    final DownloadCountingOutputStream a6 = new DownloadCountingOutputStream(outputStream4);
                    a6.setListener((ActionListener)b5);
                    IOUtils.copy(httpResponse3.getEntity().getContent(), (OutputStream)a6);
                }
                catch (Exception exception3) {
                    FileDownload.LOGGER.error("Caught exception while downloading: " + exception3.getMessage());
                    FileDownload.this.error = true;
                    FileDownload.this.request.releaseConnection();
                    if (FileDownload.this.tempFile != null) {
                        FileDownload.this.tempFile.delete();
                    }
                    if (!FileDownload.this.error) {
                        if (!worldDownload.resourcePackUrl.isEmpty() && !worldDownload.resourcePackHash.isEmpty()) {
                            try {
                                FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                                FileDownload.this.request = new HttpGet(worldDownload.resourcePackUrl);
                                final HttpResponse httpResponse3 = (HttpResponse)closeableHttpClient2.execute((HttpUriRequest)FileDownload.this.request);
                                a.totalBytes = Long.parseLong(httpResponse3.getFirstHeader("Content-Length").getValue());
                                if (httpResponse3.getStatusLine().getStatusCode() != 200) {
                                    FileDownload.this.error = true;
                                    FileDownload.this.request.abort();
                                    return;
                                }
                                final OutputStream outputStream4 = (OutputStream)new FileOutputStream(FileDownload.this.tempFile);
                                final ResourcePackProgressListener c5 = new ResourcePackProgressListener(FileDownload.this.tempFile, a, worldDownload);
                                final DownloadCountingOutputStream a6 = new DownloadCountingOutputStream(outputStream4);
                                a6.setListener((ActionListener)c5);
                                IOUtils.copy(httpResponse3.getEntity().getContent(), (OutputStream)a6);
                            }
                            catch (Exception exception3) {
                                FileDownload.LOGGER.error("Caught exception while downloading: " + exception3.getMessage());
                                FileDownload.this.error = true;
                            }
                            finally {
                                FileDownload.this.request.releaseConnection();
                                if (FileDownload.this.tempFile != null) {
                                    FileDownload.this.tempFile.delete();
                                }
                            }
                        }
                        else {
                            FileDownload.this.finished = true;
                        }
                    }
                    if (closeableHttpClient2 != null) {
                        try {
                            closeableHttpClient2.close();
                        }
                        catch (IOException iOException3) {
                            FileDownload.LOGGER.error("Failed to close Realms download client");
                        }
                    }
                }
                finally {
                    FileDownload.this.request.releaseConnection();
                    if (FileDownload.this.tempFile != null) {
                        FileDownload.this.tempFile.delete();
                    }
                    if (!FileDownload.this.error) {
                        if (!worldDownload.resourcePackUrl.isEmpty() && !worldDownload.resourcePackHash.isEmpty()) {
                            try {
                                FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                                FileDownload.this.request = new HttpGet(worldDownload.resourcePackUrl);
                                final HttpResponse httpResponse4 = (HttpResponse)closeableHttpClient2.execute((HttpUriRequest)FileDownload.this.request);
                                a.totalBytes = Long.parseLong(httpResponse4.getFirstHeader("Content-Length").getValue());
                                if (httpResponse4.getStatusLine().getStatusCode() != 200) {
                                    FileDownload.this.error = true;
                                    FileDownload.this.request.abort();
                                    return;
                                }
                                final OutputStream outputStream5 = (OutputStream)new FileOutputStream(FileDownload.this.tempFile);
                                final ResourcePackProgressListener c6 = new ResourcePackProgressListener(FileDownload.this.tempFile, a, worldDownload);
                                final DownloadCountingOutputStream a7 = new DownloadCountingOutputStream(outputStream5);
                                a7.setListener((ActionListener)c6);
                                IOUtils.copy(httpResponse4.getEntity().getContent(), (OutputStream)a7);
                            }
                            catch (Exception exception4) {
                                FileDownload.LOGGER.error("Caught exception while downloading: " + exception4.getMessage());
                                FileDownload.this.error = true;
                                FileDownload.this.request.releaseConnection();
                                if (FileDownload.this.tempFile != null) {
                                    FileDownload.this.tempFile.delete();
                                }
                            }
                            finally {
                                FileDownload.this.request.releaseConnection();
                                if (FileDownload.this.tempFile != null) {
                                    FileDownload.this.tempFile.delete();
                                }
                            }
                        }
                        else {
                            FileDownload.this.finished = true;
                        }
                    }
                    if (closeableHttpClient2 != null) {
                        try {
                            closeableHttpClient2.close();
                        }
                        catch (IOException iOException4) {
                            FileDownload.LOGGER.error("Failed to close Realms download client");
                        }
                    }
                }
            }
        }).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new RealmsDefaultUncaughtExceptionHandler(FileDownload.LOGGER));
        this.currentThread.start();
    }
    
    public void cancel() {
        if (this.request != null) {
            this.request.abort();
        }
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
        this.cancelled = true;
    }
    
    public boolean isFinished() {
        return this.finished;
    }
    
    public boolean isError() {
        return this.error;
    }
    
    public boolean isExtracting() {
        return this.extracting;
    }
    
    public static String findAvailableFolderName(String string) {
        string = string.replaceAll("[\\./\"]", "_");
        for (final String string2 : FileDownload.INVALID_FILE_NAMES) {
            if (string.equalsIgnoreCase(string2)) {
                string = "_" + string + "_";
            }
        }
        return string;
    }
    
    private void untarGzipArchive(String string, final File file, final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) throws IOException {
        final Pattern pattern5 = Pattern.compile(".*-([0-9]+)$");
        int integer7 = 1;
        for (final char character11 : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
            string = string.replace(character11, '_');
        }
        if (StringUtils.isEmpty((CharSequence)string)) {
            string = "Realm";
        }
        string = findAvailableFolderName(string);
        try {
            for (final RealmsLevelSummary realmsLevelSummary9 : realmsAnvilLevelStorageSource.getLevelList()) {
                if (realmsLevelSummary9.getLevelId().toLowerCase(Locale.ROOT).startsWith(string.toLowerCase(Locale.ROOT))) {
                    final Matcher matcher10 = pattern5.matcher((CharSequence)realmsLevelSummary9.getLevelId());
                    if (matcher10.matches()) {
                        if (Integer.valueOf(matcher10.group(1)) <= integer7) {
                            continue;
                        }
                        integer7 = Integer.valueOf(matcher10.group(1));
                    }
                    else {
                        ++integer7;
                    }
                }
            }
        }
        catch (Exception exception8) {
            FileDownload.LOGGER.error("Error getting level list", (Throwable)exception8);
            this.error = true;
            return;
        }
        String string2;
        if (!realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string) || integer7 > 1) {
            string2 = string + ((integer7 == 1) ? "" : new StringBuilder().append("-").append(integer7).toString());
            if (!realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string2)) {
                for (boolean boolean8 = false; !boolean8; boolean8 = true) {
                    ++integer7;
                    string2 = string + ((integer7 == 1) ? "" : new StringBuilder().append("-").append(integer7).toString());
                    if (realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string2)) {}
                }
            }
        }
        else {
            string2 = string;
        }
        TarArchiveInputStream tarArchiveInputStream8 = null;
        final File file2 = new File(Realms.getGameDirectoryPath(), "saves");
        try {
            file2.mkdir();
            tarArchiveInputStream8 = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream((InputStream)new FileInputStream(file))));
            for (TarArchiveEntry tarArchiveEntry10 = tarArchiveInputStream8.getNextTarEntry(); tarArchiveEntry10 != null; tarArchiveEntry10 = tarArchiveInputStream8.getNextTarEntry()) {
                final File file3 = new File(file2, tarArchiveEntry10.getName().replace("world", (CharSequence)string2));
                if (tarArchiveEntry10.isDirectory()) {
                    file3.mkdirs();
                }
                else {
                    file3.createNewFile();
                    byte[] arr12 = new byte[1024];
                    final BufferedOutputStream bufferedOutputStream13 = new BufferedOutputStream((OutputStream)new FileOutputStream(file3));
                    int integer8 = 0;
                    while ((integer8 = tarArchiveInputStream8.read(arr12)) != -1) {
                        bufferedOutputStream13.write(arr12, 0, integer8);
                    }
                    bufferedOutputStream13.close();
                    arr12 = null;
                }
            }
        }
        catch (Exception exception9) {
            FileDownload.LOGGER.error("Error extracting world", (Throwable)exception9);
            this.error = true;
        }
        finally {
            if (tarArchiveInputStream8 != null) {
                tarArchiveInputStream8.close();
            }
            if (file != null) {
                file.delete();
            }
            final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource2 = realmsAnvilLevelStorageSource;
            realmsAnvilLevelStorageSource2.renameLevel(string2, string2.trim());
            final File file4 = new File(file2, string2 + File.separator + "level.dat");
            Realms.deletePlayerTag(file4);
            this.resourcePackPath = new File(file2, string2 + File.separator + "resources.zip");
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        INVALID_FILE_NAMES = new String[] { "CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9" };
    }
    
    class ProgressListener implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final RealmsAnvilLevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;
        
        private ProgressListener(final String string, final File file, final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource, final RealmsDownloadLatestWorldScreen.DownloadStatus a, final WorldDownload worldDownload) {
            this.worldName = string;
            this.tempFile = file;
            this.levelStorageSource = realmsAnvilLevelStorageSource;
            this.downloadStatus = a;
            this.worldDownload = worldDownload;
        }
        
        public void actionPerformed(final ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)actionEvent.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                }
                catch (IOException iOException3) {
                    FileDownload.LOGGER.error("Error extracting archive", (Throwable)iOException3);
                    FileDownload.this.error = true;
                }
            }
        }
    }
    
    class ResourcePackProgressListener implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;
        
        private ResourcePackProgressListener(final File file, final RealmsDownloadLatestWorldScreen.DownloadStatus a, final WorldDownload worldDownload) {
            this.tempFile = file;
            this.downloadStatus = a;
            this.worldDownload = worldDownload;
        }
        
        public void actionPerformed(final ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)actionEvent.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    final String string3 = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
                    if (string3.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    }
                    else {
                        FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + string3 + "). Deleting it.");
                        FileUtils.deleteQuietly(this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException iOException3) {
                    FileDownload.LOGGER.error("Error copying resourcepack file", iOException3.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }
    
    class DownloadCountingOutputStream extends CountingOutputStream {
        private ActionListener listener;
        
        public DownloadCountingOutputStream(final OutputStream outputStream) {
            super(outputStream);
        }
        
        public void setListener(final ActionListener actionListener) {
            this.listener = actionListener;
        }
        
        protected void afterWrite(final int integer) throws IOException {
            super.afterWrite(integer);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
            }
        }
    }
}

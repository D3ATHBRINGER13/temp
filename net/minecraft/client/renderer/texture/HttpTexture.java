package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.DefaultUncaughtExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import net.minecraft.client.Minecraft;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.HttpTextureProcessor;
import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;

public class HttpTexture extends SimpleTexture {
    private static final Logger LOGGER;
    private static final AtomicInteger UNIQUE_THREAD_ID;
    @Nullable
    private final File file;
    private final String urlString;
    @Nullable
    private final HttpTextureProcessor processor;
    @Nullable
    private Thread thread;
    private volatile boolean uploaded;
    
    public HttpTexture(@Nullable final File file, final String string, final ResourceLocation qv, @Nullable final HttpTextureProcessor dnd) {
        super(qv);
        this.file = file;
        this.urlString = string;
        this.processor = dnd;
    }
    
    private void uploadImage(final NativeImage cuj) {
        TextureUtil.prepareImage(this.getId(), cuj.getWidth(), cuj.getHeight());
        cuj.upload(0, 0, 0, false);
    }
    
    public void loadCallback(final NativeImage cuj) {
        if (this.processor != null) {
            this.processor.onTextureDownloaded();
        }
        synchronized (this) {
            this.uploadImage(cuj);
            this.uploaded = true;
        }
    }
    
    @Override
    public void load(final ResourceManager xi) throws IOException {
        if (!this.uploaded) {
            synchronized (this) {
                super.load(xi);
                this.uploaded = true;
            }
        }
        if (this.thread == null) {
            if (this.file != null && this.file.isFile()) {
                HttpTexture.LOGGER.debug("Loading http texture from local cache ({})", this.file);
                NativeImage cuj3 = null;
                try {
                    cuj3 = NativeImage.read((InputStream)new FileInputStream(this.file));
                    if (this.processor != null) {
                        cuj3 = this.processor.process(cuj3);
                    }
                    this.loadCallback(cuj3);
                }
                catch (IOException iOException4) {
                    HttpTexture.LOGGER.error("Couldn't load skin {}", this.file, iOException4);
                    this.startDownloadThread();
                }
                finally {
                    if (cuj3 != null) {
                        cuj3.close();
                    }
                }
            }
            else {
                this.startDownloadThread();
            }
        }
    }
    
    protected void startDownloadThread() {
        (this.thread = new Thread(new StringBuilder().append("Texture Downloader #").append(HttpTexture.UNIQUE_THREAD_ID.incrementAndGet()).toString()) {
            public void run() {
                HttpURLConnection httpURLConnection2 = null;
                HttpTexture.LOGGER.debug("Downloading http texture from {} to {}", HttpTexture.this.urlString, HttpTexture.this.file);
                try {
                    httpURLConnection2 = (HttpURLConnection)new URL(HttpTexture.this.urlString).openConnection(Minecraft.getInstance().getProxy());
                    httpURLConnection2.setDoInput(true);
                    httpURLConnection2.setDoOutput(false);
                    httpURLConnection2.connect();
                    if (httpURLConnection2.getResponseCode() / 100 != 2) {
                        return;
                    }
                    InputStream inputStream3;
                    if (HttpTexture.this.file != null) {
                        FileUtils.copyInputStreamToFile(httpURLConnection2.getInputStream(), HttpTexture.this.file);
                        inputStream3 = (InputStream)new FileInputStream(HttpTexture.this.file);
                    }
                    else {
                        inputStream3 = httpURLConnection2.getInputStream();
                    }
                    Minecraft.getInstance().execute(() -> {
                        NativeImage cuj3 = null;
                        try {
                            cuj3 = NativeImage.read(inputStream3);
                            if (HttpTexture.this.processor != null) {
                                cuj3 = HttpTexture.this.processor.process(cuj3);
                            }
                            HttpTexture.this.loadCallback(cuj3);
                        }
                        catch (IOException iOException4) {
                            HttpTexture.LOGGER.warn("Error while loading the skin texture", (Throwable)iOException4);
                        }
                        finally {
                            if (cuj3 != null) {
                                cuj3.close();
                            }
                            IOUtils.closeQuietly(inputStream3);
                        }
                    });
                }
                catch (Exception exception3) {
                    HttpTexture.LOGGER.error("Couldn't download http texture", (Throwable)exception3);
                }
                finally {
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                }
            }
        }).setDaemon(true);
        this.thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(HttpTexture.LOGGER));
        this.thread.start();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        UNIQUE_THREAD_ID = new AtomicInteger(0);
    }
}

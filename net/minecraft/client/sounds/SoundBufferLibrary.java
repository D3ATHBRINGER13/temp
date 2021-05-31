package net.minecraft.client.sounds;

import java.nio.ByteBuffer;
import java.io.InputStream;
import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import com.mojang.blaze3d.audio.OggAudioStream;
import net.minecraft.client.resources.sounds.Sound;
import java.util.Collection;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.server.packs.resources.ResourceManager;

public class SoundBufferLibrary {
    private final ResourceManager resourceManager;
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache;
    
    public SoundBufferLibrary(final ResourceManager xi) {
        this.cache = (Map<ResourceLocation, CompletableFuture<SoundBuffer>>)Maps.newHashMap();
        this.resourceManager = xi;
    }
    
    public CompletableFuture<SoundBuffer> getCompleteBuffer(final ResourceLocation qv) {
        return (CompletableFuture<SoundBuffer>)this.cache.computeIfAbsent(qv, qv -> CompletableFuture.supplyAsync(() -> {
            try (final Resource xh3 = this.resourceManager.getResource(qv);
                 final InputStream inputStream5 = xh3.getInputStream();
                 final AudioStream eai7 = new OggAudioStream(inputStream5)) {
                final ByteBuffer byteBuffer9 = eai7.readAll();
                return new SoundBuffer(byteBuffer9, eai7.getFormat());
            }
            catch (IOException iOException3) {
                throw new CompletionException((Throwable)iOException3);
            }
        }, Util.backgroundExecutor()));
    }
    
    public CompletableFuture<AudioStream> getStream(final ResourceLocation qv) {
        return (CompletableFuture<AudioStream>)CompletableFuture.supplyAsync(() -> {
            try {
                final Resource xh3 = this.resourceManager.getResource(qv);
                final InputStream inputStream4 = xh3.getInputStream();
                return new OggAudioStream(inputStream4);
            }
            catch (IOException iOException3) {
                throw new CompletionException((Throwable)iOException3);
            }
        }, Util.backgroundExecutor());
    }
    
    public void clear() {
        this.cache.values().forEach(completableFuture -> completableFuture.thenAccept(SoundBuffer::discardAlBuffer));
        this.cache.clear();
    }
    
    public CompletableFuture<?> preload(final Collection<Sound> collection) {
        return CompletableFuture.allOf((CompletableFuture[])collection.stream().map(dzm -> this.getCompleteBuffer(dzm.getPath())).toArray(CompletableFuture[]::new));
    }
}

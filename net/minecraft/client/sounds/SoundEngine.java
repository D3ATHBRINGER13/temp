package net.minecraft.client.sounds;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;
import com.mojang.blaze3d.audio.SoundBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import java.util.stream.Stream;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import java.util.function.Consumer;
import com.mojang.blaze3d.audio.Channel;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;
import com.google.common.collect.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.sounds.SoundInstance;
import java.util.Map;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.Library;
import net.minecraft.client.Options;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

public class SoundEngine {
    private static final Marker MARKER;
    private static final Logger LOGGER;
    private static final Set<ResourceLocation> ONLY_WARN_ONCE;
    private final SoundManager soundManager;
    private final Options options;
    private boolean loaded;
    private final Library library;
    private final Listener listener;
    private final SoundBufferLibrary soundBuffers;
    private final SoundEngineExecutor executor;
    private final ChannelAccess channelAccess;
    private int tickCount;
    private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;
    private final Multimap<SoundSource, SoundInstance> instanceBySource;
    private final List<TickableSoundInstance> tickingSounds;
    private final Map<SoundInstance, Integer> queuedSounds;
    private final Map<SoundInstance, Integer> soundDeleteTime;
    private final List<SoundEventListener> listeners;
    private final List<Sound> preloadQueue;
    
    public SoundEngine(final SoundManager eap, final Options cyg, final ResourceManager xi) {
        this.library = new Library();
        this.listener = this.library.getListener();
        this.executor = new SoundEngineExecutor();
        this.channelAccess = new ChannelAccess(this.library, (Executor)this.executor);
        this.instanceToChannel = (Map<SoundInstance, ChannelAccess.ChannelHandle>)Maps.newHashMap();
        this.instanceBySource = (Multimap<SoundSource, SoundInstance>)HashMultimap.create();
        this.tickingSounds = (List<TickableSoundInstance>)Lists.newArrayList();
        this.queuedSounds = (Map<SoundInstance, Integer>)Maps.newHashMap();
        this.soundDeleteTime = (Map<SoundInstance, Integer>)Maps.newHashMap();
        this.listeners = (List<SoundEventListener>)Lists.newArrayList();
        this.preloadQueue = (List<Sound>)Lists.newArrayList();
        this.soundManager = eap;
        this.options = cyg;
        this.soundBuffers = new SoundBufferLibrary(xi);
    }
    
    public void reload() {
        SoundEngine.ONLY_WARN_ONCE.clear();
        for (final SoundEvent yo3 : Registry.SOUND_EVENT) {
            final ResourceLocation qv4 = yo3.getLocation();
            if (this.soundManager.getSoundEvent(qv4) == null) {
                SoundEngine.LOGGER.warn("Missing sound for event: {}", Registry.SOUND_EVENT.getKey(yo3));
                SoundEngine.ONLY_WARN_ONCE.add(qv4);
            }
        }
        this.destroy();
        this.loadLibrary();
    }
    
    private synchronized void loadLibrary() {
        if (this.loaded) {
            return;
        }
        try {
            this.library.init();
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            this.soundBuffers.preload((Collection<Sound>)this.preloadQueue).thenRun(this.preloadQueue::clear);
            this.loaded = true;
            SoundEngine.LOGGER.info(SoundEngine.MARKER, "Sound engine started");
        }
        catch (RuntimeException runtimeException2) {
            SoundEngine.LOGGER.error(SoundEngine.MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException2);
        }
    }
    
    private float getVolume(final SoundSource yq) {
        if (yq == null || yq == SoundSource.MASTER) {
            return 1.0f;
        }
        return this.options.getSoundSourceVolume(yq);
    }
    
    public void updateCategoryVolume(final SoundSource yq, final float float2) {
        if (!this.loaded) {
            return;
        }
        if (yq == SoundSource.MASTER) {
            this.listener.setGain(float2);
            return;
        }
        this.instanceToChannel.forEach((dzp, a) -> {
            final float float4 = this.calculateVolume(dzp);
            a.execute((Consumer<Channel>)(ctp -> {
                if (float4 <= 0.0f) {
                    ctp.stop();
                }
                else {
                    ctp.setVolume(float4);
                }
            }));
        });
    }
    
    public void destroy() {
        if (this.loaded) {
            this.stopAll();
            this.soundBuffers.clear();
            this.library.cleanup();
            this.loaded = false;
        }
    }
    
    public void stop(final SoundInstance dzp) {
        if (this.loaded) {
            final ChannelAccess.ChannelHandle a3 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(dzp);
            if (a3 != null) {
                a3.execute((Consumer<Channel>)Channel::stop);
            }
        }
    }
    
    public void stopAll() {
        if (this.loaded) {
            this.executor.flush();
            this.instanceToChannel.values().forEach(a -> a.execute((Consumer<Channel>)Channel::stop));
            this.instanceToChannel.clear();
            this.channelAccess.clear();
            this.queuedSounds.clear();
            this.tickingSounds.clear();
            this.instanceBySource.clear();
            this.soundDeleteTime.clear();
        }
    }
    
    public void addEventListener(final SoundEventListener eao) {
        this.listeners.add(eao);
    }
    
    public void removeEventListener(final SoundEventListener eao) {
        this.listeners.remove(eao);
    }
    
    public void tick(final boolean boolean1) {
        if (!boolean1) {
            this.tickNonPaused();
        }
        this.channelAccess.scheduleTick();
    }
    
    private void tickNonPaused() {
        ++this.tickCount;
        for (final TickableSoundInstance dzq3 : this.tickingSounds) {
            dzq3.tick();
            if (dzq3.isStopped()) {
                this.stop(dzq3);
            }
            else {
                final float float4 = this.calculateVolume(dzq3);
                final float float5 = this.calculatePitch(dzq3);
                final Vec3 csi6 = new Vec3(dzq3.getX(), dzq3.getY(), dzq3.getZ());
                final ChannelAccess.ChannelHandle a7 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(dzq3);
                if (a7 == null) {
                    continue;
                }
                a7.execute((Consumer<Channel>)(ctp -> {
                    ctp.setVolume(float4);
                    ctp.setPitch(float5);
                    ctp.setSelfPosition(csi6);
                }));
            }
        }
        final Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> iterator2 = (Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>>)this.instanceToChannel.entrySet().iterator();
        while (iterator2.hasNext()) {
            final Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry3 = (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>)iterator2.next();
            final ChannelAccess.ChannelHandle a8 = (ChannelAccess.ChannelHandle)entry3.getValue();
            final SoundInstance dzp5 = (SoundInstance)entry3.getKey();
            final float float6 = this.options.getSoundSourceVolume(dzp5.getSource());
            if (float6 <= 0.0f) {
                a8.execute((Consumer<Channel>)Channel::stop);
                iterator2.remove();
            }
            else {
                if (!a8.isStopped()) {
                    continue;
                }
                final int integer7 = (int)this.soundDeleteTime.get(dzp5);
                if (integer7 > this.tickCount) {
                    continue;
                }
                final int integer8 = dzp5.getDelay();
                if (dzp5.isLooping() && integer8 > 0) {
                    this.queuedSounds.put(dzp5, (this.tickCount + integer8));
                }
                iterator2.remove();
                SoundEngine.LOGGER.debug(SoundEngine.MARKER, "Removed channel {} because it's not playing anymore", a8);
                this.soundDeleteTime.remove(dzp5);
                try {
                    this.instanceBySource.remove(dzp5.getSource(), dzp5);
                }
                catch (RuntimeException ex) {}
                if (!(dzp5 instanceof TickableSoundInstance)) {
                    continue;
                }
                this.tickingSounds.remove(dzp5);
            }
        }
        final Iterator<Map.Entry<SoundInstance, Integer>> iterator3 = (Iterator<Map.Entry<SoundInstance, Integer>>)this.queuedSounds.entrySet().iterator();
        while (iterator3.hasNext()) {
            final Map.Entry<SoundInstance, Integer> entry4 = (Map.Entry<SoundInstance, Integer>)iterator3.next();
            if (this.tickCount >= (int)entry4.getValue()) {
                final SoundInstance dzp5 = (SoundInstance)entry4.getKey();
                if (dzp5 instanceof TickableSoundInstance) {
                    ((TickableSoundInstance)dzp5).tick();
                }
                this.play(dzp5);
                iterator3.remove();
            }
        }
    }
    
    public boolean isActive(final SoundInstance dzp) {
        return this.loaded && ((this.soundDeleteTime.containsKey(dzp) && (int)this.soundDeleteTime.get(dzp) <= this.tickCount) || this.instanceToChannel.containsKey(dzp));
    }
    
    public void play(final SoundInstance dzp) {
        if (!this.loaded) {
            return;
        }
        final WeighedSoundEvents eaq3 = dzp.resolve(this.soundManager);
        final ResourceLocation qv4 = dzp.getLocation();
        if (eaq3 == null) {
            if (SoundEngine.ONLY_WARN_ONCE.add(qv4)) {
                SoundEngine.LOGGER.warn(SoundEngine.MARKER, "Unable to play unknown soundEvent: {}", qv4);
            }
            return;
        }
        if (!this.listeners.isEmpty()) {
            for (final SoundEventListener eao6 : this.listeners) {
                eao6.onPlaySound(dzp, eaq3);
            }
        }
        if (this.listener.getGain() <= 0.0f) {
            SoundEngine.LOGGER.debug(SoundEngine.MARKER, "Skipped playing soundEvent: {}, master volume was zero", qv4);
            return;
        }
        final Sound dzm5 = dzp.getSound();
        if (dzm5 == SoundManager.EMPTY_SOUND) {
            if (SoundEngine.ONLY_WARN_ONCE.add(qv4)) {
                SoundEngine.LOGGER.warn(SoundEngine.MARKER, "Unable to play empty soundEvent: {}", qv4);
            }
            return;
        }
        final float float6 = dzp.getVolume();
        final float float7 = Math.max(float6, 1.0f) * dzm5.getAttenuationDistance();
        final SoundSource yq8 = dzp.getSource();
        final float float8 = this.calculateVolume(dzp);
        final float float9 = this.calculatePitch(dzp);
        final SoundInstance.Attenuation a11 = dzp.getAttenuation();
        final boolean boolean12 = dzp.isRelative();
        if (float8 == 0.0f && !dzp.canStartSilent()) {
            SoundEngine.LOGGER.debug(SoundEngine.MARKER, "Skipped playing sound {}, volume was zero.", dzm5.getLocation());
            return;
        }
        final boolean boolean13 = dzp.isLooping() && dzp.getDelay() == 0;
        final Vec3 csi14 = new Vec3(dzp.getX(), dzp.getY(), dzp.getZ());
        final ChannelAccess.ChannelHandle a12 = this.channelAccess.createHandle(dzm5.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
        SoundEngine.LOGGER.debug(SoundEngine.MARKER, "Playing sound {} for event {}", dzm5.getLocation(), qv4);
        this.soundDeleteTime.put(dzp, (this.tickCount + 20));
        this.instanceToChannel.put(dzp, a12);
        this.instanceBySource.put(yq8, dzp);
        a12.execute((Consumer<Channel>)(ctp -> {
            ctp.setPitch(float9);
            ctp.setVolume(float8);
            if (a11 == SoundInstance.Attenuation.LINEAR) {
                ctp.linearAttenuation(float7);
            }
            else {
                ctp.disableAttenuation();
            }
            ctp.setLooping(boolean13);
            ctp.setSelfPosition(csi14);
            ctp.setRelative(boolean12);
        }));
        if (!dzm5.shouldStream()) {
            this.soundBuffers.getCompleteBuffer(dzm5.getPath()).thenAccept(ctu -> a12.execute((Consumer<Channel>)(ctp -> {
                ctp.attachStaticBuffer(ctu);
                ctp.play();
            })));
        }
        else {
            this.soundBuffers.getStream(dzm5.getPath()).thenAccept(eai -> a12.execute((Consumer<Channel>)(ctp -> {
                ctp.attachBufferStream(eai);
                ctp.play();
            })));
        }
        if (dzp instanceof TickableSoundInstance) {
            this.tickingSounds.add(dzp);
        }
    }
    
    public void requestPreload(final Sound dzm) {
        this.preloadQueue.add(dzm);
    }
    
    private float calculatePitch(final SoundInstance dzp) {
        return Mth.clamp(dzp.getPitch(), 0.5f, 2.0f);
    }
    
    private float calculateVolume(final SoundInstance dzp) {
        return Mth.clamp(dzp.getVolume() * this.getVolume(dzp.getSource()), 0.0f, 1.0f);
    }
    
    public void pause() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels((Consumer<Stream<Channel>>)(stream -> stream.forEach(Channel::pause)));
        }
    }
    
    public void resume() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels((Consumer<Stream<Channel>>)(stream -> stream.forEach(Channel::unpause)));
        }
    }
    
    public void playDelayed(final SoundInstance dzp, final int integer) {
        this.queuedSounds.put(dzp, (this.tickCount + integer));
    }
    
    public void updateSource(final Camera cxq) {
        if (!this.loaded || !cxq.isInitialized()) {
            return;
        }
        final Vec3 csi3 = cxq.getPosition();
        final Vec3 csi4 = cxq.getLookVector();
        final Vec3 csi5 = cxq.getUpVector();
        this.executor.execute(() -> {
            this.listener.setListenerPosition(csi3);
            this.listener.setListenerOrientation(csi4, csi5);
        });
    }
    
    public void stop(@Nullable final ResourceLocation qv, @Nullable final SoundSource yq) {
        if (yq != null) {
            for (final SoundInstance dzp5 : this.instanceBySource.get(yq)) {
                if (qv == null || dzp5.getLocation().equals(qv)) {
                    this.stop(dzp5);
                }
            }
        }
        else if (qv == null) {
            this.stopAll();
        }
        else {
            for (final SoundInstance dzp5 : this.instanceToChannel.keySet()) {
                if (dzp5.getLocation().equals(qv)) {
                    this.stop(dzp5);
                }
            }
        }
    }
    
    public String getDebugString() {
        return this.library.getDebugString();
    }
    
    static {
        MARKER = MarkerManager.getMarker("SOUNDS");
        LOGGER = LogManager.getLogger();
        ONLY_WARN_ONCE = (Set)Sets.newHashSet();
    }
}

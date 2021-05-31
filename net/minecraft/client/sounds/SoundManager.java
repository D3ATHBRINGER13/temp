package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.network.chat.Component;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.Camera;
import net.minecraft.client.resources.sounds.SoundInstance;
import java.util.Collection;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import java.lang.reflect.Type;
import java.io.Reader;
import net.minecraft.util.GsonHelper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import net.minecraft.core.Registry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.profiling.ProfilerFiller;
import com.google.common.collect.Maps;
import net.minecraft.client.Options;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public class SoundManager extends SimplePreparableReloadListener<Preparations> {
    public static final Sound EMPTY_SOUND;
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final ParameterizedType SOUND_EVENT_REGISTRATION_TYPE;
    private final Map<ResourceLocation, WeighedSoundEvents> registry;
    private final SoundEngine soundEngine;
    
    public SoundManager(final ResourceManager xi, final Options cyg) {
        this.registry = (Map<ResourceLocation, WeighedSoundEvents>)Maps.newHashMap();
        this.soundEngine = new SoundEngine(this, cyg, xi);
    }
    
    @Override
    protected Preparations prepare(final ResourceManager xi, final ProfilerFiller agn) {
        final Preparations a4 = new Preparations();
        agn.startTick();
        for (final String string6 : xi.getNamespaces()) {
            agn.push(string6);
            try {
                final List<Resource> list7 = xi.getResources(new ResourceLocation(string6, "sounds.json"));
                for (final Resource xh9 : list7) {
                    agn.push(xh9.getSourceName());
                    try {
                        agn.push("parse");
                        final Map<String, SoundEventRegistration> map10 = getEventFromJson(xh9.getInputStream());
                        agn.popPush("register");
                        for (final Map.Entry<String, SoundEventRegistration> entry12 : map10.entrySet()) {
                            a4.handleRegistration(new ResourceLocation(string6, (String)entry12.getKey()), (SoundEventRegistration)entry12.getValue(), xi);
                        }
                        agn.pop();
                    }
                    catch (RuntimeException runtimeException10) {
                        SoundManager.LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", xh9.getSourceName(), runtimeException10);
                    }
                    agn.pop();
                }
            }
            catch (IOException ex) {}
            agn.pop();
        }
        agn.endTick();
        return a4;
    }
    
    @Override
    protected void apply(final Preparations a, final ResourceManager xi, final ProfilerFiller agn) {
        a.apply(this.registry, this.soundEngine);
        for (final ResourceLocation qv6 : this.registry.keySet()) {
            final WeighedSoundEvents eaq7 = (WeighedSoundEvents)this.registry.get(qv6);
            if (eaq7.getSubtitle() instanceof TranslatableComponent) {
                final String string8 = ((TranslatableComponent)eaq7.getSubtitle()).getKey();
                if (I18n.exists(string8)) {
                    continue;
                }
                SoundManager.LOGGER.debug("Missing subtitle {} for event: {}", string8, qv6);
            }
        }
        if (SoundManager.LOGGER.isDebugEnabled()) {
            for (final ResourceLocation qv6 : this.registry.keySet()) {
                if (!Registry.SOUND_EVENT.containsKey(qv6)) {
                    SoundManager.LOGGER.debug("Not having sound event for: {}", qv6);
                }
            }
        }
        this.soundEngine.reload();
    }
    
    @Nullable
    protected static Map<String, SoundEventRegistration> getEventFromJson(final InputStream inputStream) {
        try {
            return GsonHelper.fromJson(SoundManager.GSON, (Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), (Type)SoundManager.SOUND_EVENT_REGISTRATION_TYPE);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private static boolean validateSoundResource(final Sound dzm, final ResourceLocation qv, final ResourceManager xi) {
        final ResourceLocation qv2 = dzm.getPath();
        if (!xi.hasResource(qv2)) {
            SoundManager.LOGGER.warn("File {} does not exist, cannot add it to event {}", qv2, qv);
            return false;
        }
        return true;
    }
    
    @Nullable
    public WeighedSoundEvents getSoundEvent(final ResourceLocation qv) {
        return (WeighedSoundEvents)this.registry.get(qv);
    }
    
    public Collection<ResourceLocation> getAvailableSounds() {
        return (Collection<ResourceLocation>)this.registry.keySet();
    }
    
    public void play(final SoundInstance dzp) {
        this.soundEngine.play(dzp);
    }
    
    public void playDelayed(final SoundInstance dzp, final int integer) {
        this.soundEngine.playDelayed(dzp, integer);
    }
    
    public void updateSource(final Camera cxq) {
        this.soundEngine.updateSource(cxq);
    }
    
    public void pause() {
        this.soundEngine.pause();
    }
    
    public void stop() {
        this.soundEngine.stopAll();
    }
    
    public void destroy() {
        this.soundEngine.destroy();
    }
    
    public void tick(final boolean boolean1) {
        this.soundEngine.tick(boolean1);
    }
    
    public void resume() {
        this.soundEngine.resume();
    }
    
    public void updateSourceVolume(final SoundSource yq, final float float2) {
        if (yq == SoundSource.MASTER && float2 <= 0.0f) {
            this.stop();
        }
        this.soundEngine.updateCategoryVolume(yq, float2);
    }
    
    public void stop(final SoundInstance dzp) {
        this.soundEngine.stop(dzp);
    }
    
    public boolean isActive(final SoundInstance dzp) {
        return this.soundEngine.isActive(dzp);
    }
    
    public void addListener(final SoundEventListener eao) {
        this.soundEngine.addEventListener(eao);
    }
    
    public void removeListener(final SoundEventListener eao) {
        this.soundEngine.removeEventListener(eao);
    }
    
    public void stop(@Nullable final ResourceLocation qv, @Nullable final SoundSource yq) {
        this.soundEngine.stop(qv, yq);
    }
    
    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }
    
    static {
        EMPTY_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.Type.FILE, false, false, 16);
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeHierarchyAdapter((Class)Component.class, new Component.Serializer()).registerTypeAdapter((Type)SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
        SOUND_EVENT_REGISTRATION_TYPE = (ParameterizedType)new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[] { (Type)String.class, (Type)SoundEventRegistration.class };
            }
            
            public Type getRawType() {
                return (Type)Map.class;
            }
            
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    public static class Preparations {
        private final Map<ResourceLocation, WeighedSoundEvents> registry;
        
        protected Preparations() {
            this.registry = (Map<ResourceLocation, WeighedSoundEvents>)Maps.newHashMap();
        }
        
        private void handleRegistration(final ResourceLocation qv, final SoundEventRegistration dzn, final ResourceManager xi) {
            WeighedSoundEvents eaq5 = (WeighedSoundEvents)this.registry.get(qv);
            final boolean boolean6 = eaq5 == null;
            if (boolean6 || dzn.isReplace()) {
                if (!boolean6) {
                    SoundManager.LOGGER.debug("Replaced sound event location {}", qv);
                }
                eaq5 = new WeighedSoundEvents(qv, dzn.getSubtitle());
                this.registry.put(qv, eaq5);
            }
            for (final Sound dzm8 : dzn.getSounds()) {
                final ResourceLocation qv2 = dzm8.getLocation();
                Weighted<Sound> ear10 = null;
                switch (dzm8.getType()) {
                    case FILE: {
                        if (!validateSoundResource(dzm8, qv, xi)) {
                            continue;
                        }
                        ear10 = dzm8;
                        break;
                    }
                    case SOUND_EVENT: {
                        ear10 = new Weighted<Sound>() {
                            public int getWeight() {
                                final WeighedSoundEvents eaq2 = (WeighedSoundEvents)Preparations.this.registry.get(qv2);
                                return (eaq2 == null) ? 0 : eaq2.getWeight();
                            }
                            
                            public Sound getSound() {
                                final WeighedSoundEvents eaq2 = (WeighedSoundEvents)Preparations.this.registry.get(qv2);
                                if (eaq2 == null) {
                                    return SoundManager.EMPTY_SOUND;
                                }
                                final Sound dzm3 = eaq2.getSound();
                                return new Sound(dzm3.getLocation().toString(), dzm3.getVolume() * dzm8.getVolume(), dzm3.getPitch() * dzm8.getPitch(), dzm8.getWeight(), Sound.Type.FILE, dzm3.shouldStream() || dzm8.shouldStream(), dzm3.shouldPreload(), dzm3.getAttenuationDistance());
                            }
                            
                            public void preloadIfRequired(final SoundEngine eam) {
                                final WeighedSoundEvents eaq3 = (WeighedSoundEvents)Preparations.this.registry.get(qv2);
                                if (eaq3 == null) {
                                    return;
                                }
                                eaq3.preloadIfRequired(eam);
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException(new StringBuilder().append("Unknown SoundEventRegistration type: ").append(dzm8.getType()).toString());
                    }
                }
                eaq5.addSound(ear10);
            }
        }
        
        public void apply(final Map<ResourceLocation, WeighedSoundEvents> map, final SoundEngine eam) {
            map.clear();
            for (final Map.Entry<ResourceLocation, WeighedSoundEvents> entry5 : this.registry.entrySet()) {
                map.put(entry5.getKey(), entry5.getValue());
                ((WeighedSoundEvents)entry5.getValue()).preloadIfRequired(eam);
            }
        }
    }
}

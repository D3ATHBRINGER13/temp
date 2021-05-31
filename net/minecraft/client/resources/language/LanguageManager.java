package net.minecraft.client.resources.language;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Sets;
import java.util.SortedSet;
import com.google.common.collect.Lists;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.Pack;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class LanguageManager implements ResourceManagerReloadListener {
    private static final Logger LOGGER;
    protected static final Locale LOCALE;
    private String currentCode;
    private final Map<String, Language> languages;
    
    public LanguageManager(final String string) {
        this.languages = (Map<String, Language>)Maps.newHashMap();
        this.currentCode = string;
        I18n.setLocale(LanguageManager.LOCALE);
    }
    
    public void reload(final List<Pack> list) {
        this.languages.clear();
        for (final Pack wl4 : list) {
            try {
                final LanguageMetadataSection dyi5 = wl4.<LanguageMetadataSection>getMetadataSection((MetadataSectionSerializer<LanguageMetadataSection>)LanguageMetadataSection.SERIALIZER);
                if (dyi5 == null) {
                    continue;
                }
                for (final Language dxy7 : dyi5.getLanguages()) {
                    if (!this.languages.containsKey(dxy7.getCode())) {
                        this.languages.put(dxy7.getCode(), dxy7);
                    }
                }
            }
            catch (RuntimeException | IOException ex2) {
                final Exception ex;
                final Exception exception5 = ex;
                LanguageManager.LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", wl4.getName(), exception5);
            }
        }
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        final List<String> list3 = (List<String>)Lists.newArrayList((Object[])new String[] { "en_us" });
        if (!"en_us".equals(this.currentCode)) {
            list3.add(this.currentCode);
        }
        LanguageManager.LOCALE.loadFrom(xi, list3);
        net.minecraft.locale.Language.forceData(LanguageManager.LOCALE.storage);
    }
    
    public boolean isBidirectional() {
        return this.getSelected() != null && this.getSelected().isBidirectional();
    }
    
    public void setSelected(final Language dxy) {
        this.currentCode = dxy.getCode();
    }
    
    public Language getSelected() {
        final String string2 = this.languages.containsKey(this.currentCode) ? this.currentCode : "en_us";
        return (Language)this.languages.get(string2);
    }
    
    public SortedSet<Language> getLanguages() {
        return (SortedSet<Language>)Sets.newTreeSet((Iterable)this.languages.values());
    }
    
    public Language getLanguage(final String string) {
        return (Language)this.languages.get(string);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        LOCALE = new Locale();
    }
}

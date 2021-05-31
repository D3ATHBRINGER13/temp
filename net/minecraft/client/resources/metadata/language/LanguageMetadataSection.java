package net.minecraft.client.resources.metadata.language;

import net.minecraft.client.resources.language.Language;
import java.util.Collection;

public class LanguageMetadataSection {
    public static final LanguageMetadataSectionSerializer SERIALIZER;
    private final Collection<Language> languages;
    
    public LanguageMetadataSection(final Collection<Language> collection) {
        this.languages = collection;
    }
    
    public Collection<Language> getLanguages() {
        return this.languages;
    }
    
    static {
        SERIALIZER = new LanguageMetadataSectionSerializer();
    }
}

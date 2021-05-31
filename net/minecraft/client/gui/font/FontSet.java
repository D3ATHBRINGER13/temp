package net.minecraft.client.gui.font;

import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import net.minecraft.client.renderer.texture.TextureObject;
import java.util.Set;
import java.util.Iterator;
import net.minecraft.util.Mth;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.RawGlyph;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import com.mojang.blaze3d.font.GlyphProvider;
import java.util.List;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.Random;
import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import org.apache.logging.log4j.Logger;

public class FontSet implements AutoCloseable {
    private static final Logger LOGGER;
    private static final EmptyGlyph SPACE_GLYPH;
    private static final GlyphInfo SPACE_INFO;
    private static final Random RANDOM;
    private final TextureManager textureManager;
    private final ResourceLocation name;
    private BakedGlyph missingGlyph;
    private final List<GlyphProvider> providers;
    private final Char2ObjectMap<BakedGlyph> glyphs;
    private final Char2ObjectMap<GlyphInfo> glyphInfos;
    private final Int2ObjectMap<CharList> glyphsByWidth;
    private final List<FontTexture> textures;
    
    public FontSet(final TextureManager dxc, final ResourceLocation qv) {
        this.providers = (List<GlyphProvider>)Lists.newArrayList();
        this.glyphs = (Char2ObjectMap<BakedGlyph>)new Char2ObjectOpenHashMap();
        this.glyphInfos = (Char2ObjectMap<GlyphInfo>)new Char2ObjectOpenHashMap();
        this.glyphsByWidth = (Int2ObjectMap<CharList>)new Int2ObjectOpenHashMap();
        this.textures = (List<FontTexture>)Lists.newArrayList();
        this.textureManager = dxc;
        this.name = qv;
    }
    
    public void reload(final List<GlyphProvider> list) {
        for (final GlyphProvider ctw4 : this.providers) {
            ctw4.close();
        }
        this.providers.clear();
        this.closeTextures();
        this.textures.clear();
        this.glyphs.clear();
        this.glyphInfos.clear();
        this.glyphsByWidth.clear();
        this.missingGlyph = this.stitch(MissingGlyph.INSTANCE);
        final Set<GlyphProvider> set3 = (Set<GlyphProvider>)Sets.newHashSet();
        for (char character4 = '\0'; character4 < '\uffff'; ++character4) {
            for (final GlyphProvider ctw5 : list) {
                final GlyphInfo ctv7 = (character4 == ' ') ? FontSet.SPACE_INFO : ctw5.getGlyph(character4);
                if (ctv7 != null) {
                    set3.add(ctw5);
                    if (ctv7 != MissingGlyph.INSTANCE) {
                        ((CharList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(ctv7.getAdvance(false)), integer -> new CharArrayList())).add(character4);
                        break;
                    }
                    break;
                }
            }
        }
        list.stream().filter(set3::contains).forEach(this.providers::add);
    }
    
    public void close() {
        this.closeTextures();
    }
    
    public void closeTextures() {
        for (final FontTexture dat3 : this.textures) {
            dat3.close();
        }
    }
    
    public GlyphInfo getGlyphInfo(final char character) {
        return (GlyphInfo)this.glyphInfos.computeIfAbsent(character, integer -> (integer == 32) ? FontSet.SPACE_INFO : this.getRaw((char)integer));
    }
    
    private RawGlyph getRaw(final char character) {
        for (final GlyphProvider ctw4 : this.providers) {
            final RawGlyph ctx5 = ctw4.getGlyph(character);
            if (ctx5 != null) {
                return ctx5;
            }
        }
        return MissingGlyph.INSTANCE;
    }
    
    public BakedGlyph getGlyph(final char character) {
        return (BakedGlyph)this.glyphs.computeIfAbsent(character, integer -> (integer == 32) ? FontSet.SPACE_GLYPH : this.stitch(this.getRaw((char)integer)));
    }
    
    private BakedGlyph stitch(final RawGlyph ctx) {
        for (final FontTexture dat4 : this.textures) {
            final BakedGlyph dav5 = dat4.add(ctx);
            if (dav5 != null) {
                return dav5;
            }
        }
        final FontTexture dat5 = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), ctx.isColored());
        this.textures.add(dat5);
        this.textureManager.register(dat5.getName(), dat5);
        final BakedGlyph dav6 = dat5.add(ctx);
        return (dav6 == null) ? this.missingGlyph : dav6;
    }
    
    public BakedGlyph getRandomGlyph(final GlyphInfo ctv) {
        final CharList charList3 = (CharList)this.glyphsByWidth.get(Mth.ceil(ctv.getAdvance(false)));
        if (charList3 != null && !charList3.isEmpty()) {
            return this.getGlyph(charList3.get(FontSet.RANDOM.nextInt(charList3.size())));
        }
        return this.missingGlyph;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SPACE_GLYPH = new EmptyGlyph();
        SPACE_INFO = (() -> 4.0f);
        RANDOM = new Random();
    }
}

package net.minecraft.client.gui.font;

import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.font.GlyphProvider;

public class AllMissingGlyphProvider implements GlyphProvider {
    @Nullable
    public RawGlyph getGlyph(final char character) {
        return MissingGlyph.INSTANCE;
    }
}

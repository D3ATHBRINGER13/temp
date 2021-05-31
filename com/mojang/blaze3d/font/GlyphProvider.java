package com.mojang.blaze3d.font;

import javax.annotation.Nullable;
import java.io.Closeable;

public interface GlyphProvider extends Closeable {
    default void close() {
    }
    
    @Nullable
    default RawGlyph getGlyph(final char character) {
        return null;
    }
}

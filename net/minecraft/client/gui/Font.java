package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.util.Mth;
import java.util.Iterator;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.ChatFormatting;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.ArabicShaping;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.font.GlyphProvider;
import java.util.List;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.Random;

public class Font implements AutoCloseable {
    public final int lineHeight = 9;
    public final Random random;
    private final TextureManager textureManager;
    private final FontSet fonts;
    private boolean bidirectional;
    
    public Font(final TextureManager dxc, final FontSet das) {
        this.random = new Random();
        this.textureManager = dxc;
        this.fonts = das;
    }
    
    public void reload(final List<GlyphProvider> list) {
        this.fonts.reload(list);
    }
    
    public void close() {
        this.fonts.close();
    }
    
    public int drawShadow(final String string, final float float2, final float float3, final int integer) {
        GlStateManager.enableAlphaTest();
        return this.drawInternal(string, float2, float3, integer, true);
    }
    
    public int draw(final String string, final float float2, final float float3, final int integer) {
        GlStateManager.enableAlphaTest();
        return this.drawInternal(string, float2, float3, integer, false);
    }
    
    public String bidirectionalShaping(final String string) {
        try {
            final Bidi bidi3 = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi3.setReorderingMode(0);
            return bidi3.writeReordered(2);
        }
        catch (ArabicShapingException ex) {
            return string;
        }
    }
    
    private int drawInternal(String string, float float2, final float float3, int integer, final boolean boolean5) {
        if (string == null) {
            return 0;
        }
        if (this.bidirectional) {
            string = this.bidirectionalShaping(string);
        }
        if ((integer & 0xFC000000) == 0x0) {
            integer |= 0xFF000000;
        }
        if (boolean5) {
            this.renderText(string, float2, float3, integer, true);
        }
        float2 = this.renderText(string, float2, float3, integer, false);
        return (int)float2 + (boolean5 ? 1 : 0);
    }
    
    private float renderText(final String string, float float2, final float float3, final int integer, final boolean boolean5) {
        final float float4 = boolean5 ? 0.25f : 1.0f;
        final float float5 = (integer >> 16 & 0xFF) / 255.0f * float4;
        final float float6 = (integer >> 8 & 0xFF) / 255.0f * float4;
        final float float7 = (integer & 0xFF) / 255.0f * float4;
        float float8 = float5;
        float float9 = float6;
        float float10 = float7;
        final float float11 = (integer >> 24 & 0xFF) / 255.0f;
        final Tesselator cuz15 = Tesselator.getInstance();
        final BufferBuilder cuw16 = cuz15.getBuilder();
        ResourceLocation qv17 = null;
        cuw16.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        boolean boolean6 = false;
        boolean boolean7 = false;
        boolean boolean8 = false;
        boolean boolean9 = false;
        boolean boolean10 = false;
        final List<Effect> list23 = (List<Effect>)Lists.newArrayList();
        for (int integer2 = 0; integer2 < string.length(); ++integer2) {
            final char character25 = string.charAt(integer2);
            if (character25 == '§' && integer2 + 1 < string.length()) {
                final ChatFormatting c26 = ChatFormatting.getByCode(string.charAt(integer2 + 1));
                if (c26 != null) {
                    if (c26.shouldReset()) {
                        boolean6 = false;
                        boolean7 = false;
                        boolean10 = false;
                        boolean9 = false;
                        boolean8 = false;
                        float8 = float5;
                        float9 = float6;
                        float10 = float7;
                    }
                    if (c26.getColor() != null) {
                        final int integer3 = c26.getColor();
                        float8 = (integer3 >> 16 & 0xFF) / 255.0f * float4;
                        float9 = (integer3 >> 8 & 0xFF) / 255.0f * float4;
                        float10 = (integer3 & 0xFF) / 255.0f * float4;
                    }
                    else if (c26 == ChatFormatting.OBFUSCATED) {
                        boolean6 = true;
                    }
                    else if (c26 == ChatFormatting.BOLD) {
                        boolean7 = true;
                    }
                    else if (c26 == ChatFormatting.STRIKETHROUGH) {
                        boolean10 = true;
                    }
                    else if (c26 == ChatFormatting.UNDERLINE) {
                        boolean9 = true;
                    }
                    else if (c26 == ChatFormatting.ITALIC) {
                        boolean8 = true;
                    }
                }
                ++integer2;
            }
            else {
                final GlyphInfo ctv26 = this.fonts.getGlyphInfo(character25);
                final BakedGlyph dav27 = (boolean6 && character25 != ' ') ? this.fonts.getRandomGlyph(ctv26) : this.fonts.getGlyph(character25);
                final ResourceLocation qv18 = dav27.getTexture();
                if (qv18 != null) {
                    if (qv17 != qv18) {
                        cuz15.end();
                        this.textureManager.bind(qv18);
                        cuw16.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                        qv17 = qv18;
                    }
                    final float float12 = boolean7 ? ctv26.getBoldOffset() : 0.0f;
                    final float float13 = boolean5 ? ctv26.getShadowOffset() : 0.0f;
                    this.renderChar(dav27, boolean7, boolean8, float12, float2 + float13, float3 + float13, cuw16, float8, float9, float10, float11);
                }
                final float float12 = ctv26.getAdvance(boolean7);
                final float float13 = boolean5 ? 1.0f : 0.0f;
                if (boolean10) {
                    list23.add(new Effect(float2 + float13 - 1.0f, float3 + float13 + 4.5f, float2 + float13 + float12, float3 + float13 + 4.5f - 1.0f, float8, float9, float10, float11));
                }
                if (boolean9) {
                    list23.add(new Effect(float2 + float13 - 1.0f, float3 + float13 + 9.0f, float2 + float13 + float12, float3 + float13 + 9.0f - 1.0f, float8, float9, float10, float11));
                }
                float2 += float12;
            }
        }
        cuz15.end();
        if (!list23.isEmpty()) {
            GlStateManager.disableTexture();
            cuw16.begin(7, DefaultVertexFormat.POSITION_COLOR);
            for (final Effect a25 : list23) {
                a25.render(cuw16);
            }
            cuz15.end();
            GlStateManager.enableTexture();
        }
        return float2;
    }
    
    private void renderChar(final BakedGlyph dav, final boolean boolean2, final boolean boolean3, final float float4, final float float5, final float float6, final BufferBuilder cuw, final float float8, final float float9, final float float10, final float float11) {
        dav.render(this.textureManager, boolean3, float5, float6, cuw, float8, float9, float10, float11);
        if (boolean2) {
            dav.render(this.textureManager, boolean3, float5 + float4, float6, cuw, float8, float9, float10, float11);
        }
    }
    
    public int width(final String string) {
        if (string == null) {
            return 0;
        }
        float float3 = 0.0f;
        boolean boolean4 = false;
        for (int integer5 = 0; integer5 < string.length(); ++integer5) {
            final char character6 = string.charAt(integer5);
            if (character6 == '§' && integer5 < string.length() - 1) {
                final ChatFormatting c7 = ChatFormatting.getByCode(string.charAt(++integer5));
                if (c7 == ChatFormatting.BOLD) {
                    boolean4 = true;
                }
                else if (c7 != null && c7.shouldReset()) {
                    boolean4 = false;
                }
            }
            else {
                float3 += this.fonts.getGlyphInfo(character6).getAdvance(boolean4);
            }
        }
        return Mth.ceil(float3);
    }
    
    public float charWidth(final char character) {
        if (character == '§') {
            return 0.0f;
        }
        return this.fonts.getGlyphInfo(character).getAdvance(false);
    }
    
    public String substrByWidth(final String string, final int integer) {
        return this.substrByWidth(string, integer, false);
    }
    
    public String substrByWidth(final String string, final int integer, final boolean boolean3) {
        final StringBuilder stringBuilder5 = new StringBuilder();
        float float6 = 0.0f;
        final int integer2 = boolean3 ? (string.length() - 1) : 0;
        final int integer3 = boolean3 ? -1 : 1;
        boolean boolean4 = false;
        boolean boolean5 = false;
        for (int integer4 = integer2; integer4 >= 0 && integer4 < string.length() && float6 < integer; integer4 += integer3) {
            final char character12 = string.charAt(integer4);
            if (boolean4) {
                boolean4 = false;
                final ChatFormatting c13 = ChatFormatting.getByCode(character12);
                if (c13 == ChatFormatting.BOLD) {
                    boolean5 = true;
                }
                else if (c13 != null && c13.shouldReset()) {
                    boolean5 = false;
                }
            }
            else if (character12 == '§') {
                boolean4 = true;
            }
            else {
                float6 += this.charWidth(character12);
                if (boolean5) {
                    ++float6;
                }
            }
            if (float6 > integer) {
                break;
            }
            if (boolean3) {
                stringBuilder5.insert(0, character12);
            }
            else {
                stringBuilder5.append(character12);
            }
        }
        return stringBuilder5.toString();
    }
    
    private String eraseTrailingNewLines(String string) {
        while (string != null && string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }
    
    public void drawWordWrap(String string, final int integer2, final int integer3, final int integer4, final int integer5) {
        string = this.eraseTrailingNewLines(string);
        this.drawWordWrapInternal(string, integer2, integer3, integer4, integer5);
    }
    
    private void drawWordWrapInternal(final String string, final int integer2, int integer3, final int integer4, final int integer5) {
        final List<String> list7 = this.split(string, integer4);
        for (final String string2 : list7) {
            float float10 = (float)integer2;
            if (this.bidirectional) {
                final int integer6 = this.width(this.bidirectionalShaping(string2));
                float10 += integer4 - integer6;
            }
            this.drawInternal(string2, float10, (float)integer3, integer5, false);
            integer3 += 9;
        }
    }
    
    public int wordWrapHeight(final String string, final int integer) {
        return 9 * this.split(string, integer).size();
    }
    
    public void setBidirectional(final boolean boolean1) {
        this.bidirectional = boolean1;
    }
    
    public List<String> split(final String string, final int integer) {
        return (List<String>)Arrays.asList((Object[])this.insertLineBreaks(string, integer).split("\n"));
    }
    
    public String insertLineBreaks(String string, final int integer) {
        String string2;
        int integer2;
        String string3;
        boolean boolean8;
        for (string2 = ""; !string.isEmpty(); string = ChatFormatting.getLastColors(string3) + string.substring(integer2 + (boolean8 ? 1 : 0)), string2 = string2 + string3 + "\n") {
            integer2 = this.indexAtWidth(string, integer);
            if (string.length() <= integer2) {
                return string2 + string;
            }
            string3 = string.substring(0, integer2);
            final char character7 = string.charAt(integer2);
            boolean8 = (character7 == ' ' || character7 == '\n');
        }
        return string2;
    }
    
    public int indexAtWidth(final String string, final int integer) {
        final int integer2 = Math.max(1, integer);
        final int integer3 = string.length();
        float float6 = 0.0f;
        int integer4 = 0;
        int integer5 = -1;
        boolean boolean9 = false;
        boolean boolean10 = true;
        while (integer4 < integer3) {
            final char character11 = string.charAt(integer4);
            Label_0178: {
                switch (character11) {
                    case '§': {
                        if (integer4 < integer3 - 1) {
                            final ChatFormatting c12 = ChatFormatting.getByCode(string.charAt(++integer4));
                            if (c12 == ChatFormatting.BOLD) {
                                boolean9 = true;
                            }
                            else if (c12 != null && c12.shouldReset()) {
                                boolean9 = false;
                            }
                        }
                        break Label_0178;
                    }
                    case '\n': {
                        --integer4;
                        break Label_0178;
                    }
                    case ' ': {
                        integer5 = integer4;
                        break;
                    }
                }
                if (float6 != 0.0f) {
                    boolean10 = false;
                }
                float6 += this.charWidth(character11);
                if (boolean9) {
                    ++float6;
                }
            }
            if (character11 == '\n') {
                integer5 = ++integer4;
                break;
            }
            if (float6 > integer2) {
                if (boolean10) {
                    ++integer4;
                    break;
                }
                break;
            }
            else {
                ++integer4;
            }
        }
        if (integer4 != integer3 && integer5 != -1 && integer5 < integer4) {
            return integer5;
        }
        return integer4;
    }
    
    public int getWordPosition(final String string, final int integer2, final int integer3, final boolean boolean4) {
        int integer4 = integer3;
        final boolean boolean5 = integer2 < 0;
        for (int integer5 = Math.abs(integer2), integer6 = 0; integer6 < integer5; ++integer6) {
            if (boolean5) {
                while (boolean4 && integer4 > 0 && (string.charAt(integer4 - 1) == ' ' || string.charAt(integer4 - 1) == '\n')) {
                    --integer4;
                }
                while (integer4 > 0 && string.charAt(integer4 - 1) != ' ' && string.charAt(integer4 - 1) != '\n') {
                    --integer4;
                }
            }
            else {
                final int integer7 = string.length();
                final int integer8 = string.indexOf(32, integer4);
                final int integer9 = string.indexOf(10, integer4);
                if (integer8 == -1 && integer9 == -1) {
                    integer4 = -1;
                }
                else if (integer8 != -1 && integer9 != -1) {
                    integer4 = Math.min(integer8, integer9);
                }
                else if (integer8 != -1) {
                    integer4 = integer8;
                }
                else {
                    integer4 = integer9;
                }
                if (integer4 == -1) {
                    integer4 = integer7;
                }
                else {
                    while (boolean4 && integer4 < integer7 && (string.charAt(integer4) == ' ' || string.charAt(integer4) == '\n')) {
                        ++integer4;
                    }
                }
            }
        }
        return integer4;
    }
    
    public boolean isBidirectional() {
        return this.bidirectional;
    }
    
    static class Effect {
        protected final float x0;
        protected final float y0;
        protected final float x1;
        protected final float y1;
        protected final float r;
        protected final float g;
        protected final float b;
        protected final float a;
        
        private Effect(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
            this.x0 = float1;
            this.y0 = float2;
            this.x1 = float3;
            this.y1 = float4;
            this.r = float5;
            this.g = float6;
            this.b = float7;
            this.a = float8;
        }
        
        public void render(final BufferBuilder cuw) {
            cuw.vertex(this.x0, this.y0, 0.0).color(this.r, this.g, this.b, this.a).endVertex();
            cuw.vertex(this.x1, this.y0, 0.0).color(this.r, this.g, this.b, this.a).endVertex();
            cuw.vertex(this.x1, this.y1, 0.0).color(this.r, this.g, this.b, this.a).endVertex();
            cuw.vertex(this.x0, this.y1, 0.0).color(this.r, this.g, this.b, this.a).endVertex();
        }
    }
}

package net.minecraft.client.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.GuiComponent;
import java.io.InputStream;
import net.minecraft.server.packs.resources.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.util.Collection;
import java.util.Random;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.ChatFormatting;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.chat.NarratorChatListener;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class WinScreen extends Screen {
    private static final Logger LOGGER;
    private static final ResourceLocation LOGO_LOCATION;
    private static final ResourceLocation EDITION_LOCATION;
    private static final ResourceLocation VIGNETTE_LOCATION;
    private final boolean poem;
    private final Runnable onFinished;
    private float time;
    private List<String> lines;
    private int totalScrollLength;
    private float scrollSpeed;
    
    public WinScreen(final boolean boolean1, final Runnable runnable) {
        super(NarratorChatListener.NO_TITLE);
        this.scrollSpeed = 0.5f;
        this.poem = boolean1;
        this.onFinished = runnable;
        if (!boolean1) {
            this.scrollSpeed = 0.75f;
        }
    }
    
    @Override
    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        final float float2 = (this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        if (this.time > float2) {
            this.respawn();
        }
    }
    
    @Override
    public void onClose() {
        this.respawn();
    }
    
    private void respawn() {
        this.onFinished.run();
        this.minecraft.setScreen(null);
    }
    
    @Override
    protected void init() {
        if (this.lines != null) {
            return;
        }
        this.lines = (List<String>)Lists.newArrayList();
        Resource xh2 = null;
        try {
            final String string3 = new StringBuilder().append("").append(ChatFormatting.WHITE).append(ChatFormatting.OBFUSCATED).append(ChatFormatting.GREEN).append(ChatFormatting.AQUA).toString();
            final int integer4 = 274;
            if (this.poem) {
                xh2 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
                final InputStream inputStream5 = xh2.getInputStream();
                final BufferedReader bufferedReader6 = new BufferedReader((Reader)new InputStreamReader(inputStream5, StandardCharsets.UTF_8));
                final Random random7 = new Random(8124371L);
                String string4;
                while ((string4 = bufferedReader6.readLine()) != null) {
                    String string5;
                    String string6;
                    for (string4 = string4.replaceAll("PLAYERNAME", this.minecraft.getUser().getName()); string4.contains((CharSequence)string3); string4 = string5 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random7.nextInt(4) + 3) + string6) {
                        final int integer5 = string4.indexOf(string3);
                        string5 = string4.substring(0, integer5);
                        string6 = string4.substring(integer5 + string3.length());
                    }
                    this.lines.addAll((Collection)this.minecraft.font.split(string4, 274));
                    this.lines.add("");
                }
                inputStream5.close();
                for (int integer5 = 0; integer5 < 8; ++integer5) {
                    this.lines.add("");
                }
            }
            final InputStream inputStream5 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            final BufferedReader bufferedReader6 = new BufferedReader((Reader)new InputStreamReader(inputStream5, StandardCharsets.UTF_8));
            String string7;
            while ((string7 = bufferedReader6.readLine()) != null) {
                string7 = string7.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
                string7 = string7.replaceAll("\t", "    ");
                this.lines.addAll((Collection)this.minecraft.font.split(string7, 274));
                this.lines.add("");
            }
            inputStream5.close();
            this.totalScrollLength = this.lines.size() * 12;
        }
        catch (Exception exception3) {
            WinScreen.LOGGER.error("Couldn't load credits", (Throwable)exception3);
        }
        finally {
            IOUtils.closeQuietly((Closeable)xh2);
        }
    }
    
    private void renderBg(final int integer1, final int integer2, final float float3) {
        this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
        final int integer3 = this.width;
        final float float4 = -this.time * 0.5f * this.scrollSpeed;
        final float float5 = this.height - this.time * 0.5f * this.scrollSpeed;
        final float float6 = 0.015625f;
        float float7 = this.time * 0.02f;
        final float float8 = (this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        final float float9 = (float8 - 20.0f - this.time) * 0.005f;
        if (float9 < float7) {
            float7 = float9;
        }
        if (float7 > 1.0f) {
            float7 = 1.0f;
        }
        float7 *= float7;
        float7 = float7 * 96.0f / 255.0f;
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        cuw13.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw13.vertex(0.0, this.height, this.blitOffset).uv(0.0, float4 * 0.015625f).color(float7, float7, float7, 1.0f).endVertex();
        cuw13.vertex(integer3, this.height, this.blitOffset).uv(integer3 * 0.015625f, float4 * 0.015625f).color(float7, float7, float7, 1.0f).endVertex();
        cuw13.vertex(integer3, 0.0, this.blitOffset).uv(integer3 * 0.015625f, float5 * 0.015625f).color(float7, float7, float7, 1.0f).endVertex();
        cuw13.vertex(0.0, 0.0, this.blitOffset).uv(0.0, float5 * 0.015625f).color(float7, float7, float7, 1.0f).endVertex();
        cuz12.end();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBg(integer1, integer2, float3);
        final int integer3 = 274;
        final int integer4 = this.width / 2 - 137;
        final int integer5 = this.height + 50;
        this.time += float3;
        final float float4 = -this.time * this.scrollSpeed;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, float4, 0.0f);
        this.minecraft.getTextureManager().bind(WinScreen.LOGO_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.blit(integer4, integer5, 0, 0, 155, 44);
        this.blit(integer4 + 155, integer5, 0, 45, 155, 44);
        this.minecraft.getTextureManager().bind(WinScreen.EDITION_LOCATION);
        GuiComponent.blit(integer4 + 88, integer5 + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        GlStateManager.disableAlphaTest();
        int integer6 = integer5 + 100;
        for (int integer7 = 0; integer7 < this.lines.size(); ++integer7) {
            if (integer7 == this.lines.size() - 1) {
                final float float5 = integer6 + float4 - (this.height / 2 - 6);
                if (float5 < 0.0f) {
                    GlStateManager.translatef(0.0f, -float5, 0.0f);
                }
            }
            if (integer6 + float4 + 12.0f + 8.0f > 0.0f && integer6 + float4 < this.height) {
                final String string11 = (String)this.lines.get(integer7);
                if (string11.startsWith("[C]")) {
                    this.font.drawShadow(string11.substring(3), (float)(integer4 + (274 - this.font.width(string11.substring(3))) / 2), (float)integer6, 16777215);
                }
                else {
                    this.font.random.setSeed((long)(integer7 * 4238972211L + this.time / 4.0f));
                    this.font.drawShadow(string11, (float)integer4, (float)integer6, 16777215);
                }
            }
            integer6 += 12;
        }
        GlStateManager.popMatrix();
        this.minecraft.getTextureManager().bind(WinScreen.VIGNETTE_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        int integer7 = this.width;
        final int integer8 = this.height;
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        cuw13.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw13.vertex(0.0, integer8, this.blitOffset).uv(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        cuw13.vertex(integer7, integer8, this.blitOffset).uv(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        cuw13.vertex(integer7, 0.0, this.blitOffset).uv(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        cuw13.vertex(0.0, 0.0, this.blitOffset).uv(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        cuz12.end();
        GlStateManager.disableBlend();
        super.render(integer1, integer2, float3);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        LOGO_LOCATION = new ResourceLocation("textures/gui/title/minecraft.png");
        EDITION_LOCATION = new ResourceLocation("textures/gui/title/edition.png");
        VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
    }
}

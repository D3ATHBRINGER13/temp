package net.minecraft.client.gui.screens;

import java.io.InputStream;
import net.minecraft.server.packs.VanillaPack;
import java.io.IOException;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class LoadingOverlay extends Overlay {
    private static final ResourceLocation MOJANG_LOGO_LOCATION;
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Runnable onFinish;
    private final boolean fadeIn;
    private float currentProgress;
    private long fadeOutStart;
    private long fadeInStart;
    
    public LoadingOverlay(final Minecraft cyc, final ReloadInstance xf, final Runnable runnable, final boolean boolean4) {
        this.fadeOutStart = -1L;
        this.fadeInStart = -1L;
        this.minecraft = cyc;
        this.reload = xf;
        this.onFinish = runnable;
        this.fadeIn = boolean4;
    }
    
    public static void registerTextures(final Minecraft cyc) {
        cyc.getTextureManager().register(LoadingOverlay.MOJANG_LOGO_LOCATION, new LogoTexture());
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        final int integer3 = this.minecraft.window.getGuiScaledWidth();
        final int integer4 = this.minecraft.window.getGuiScaledHeight();
        final long long7 = Util.getMillis();
        if (this.fadeIn && (this.reload.isApplying() || this.minecraft.screen != null) && this.fadeInStart == -1L) {
            this.fadeInStart = long7;
        }
        final float float4 = (this.fadeOutStart > -1L) ? ((long7 - this.fadeOutStart) / 1000.0f) : -1.0f;
        final float float5 = (this.fadeInStart > -1L) ? ((long7 - this.fadeInStart) / 500.0f) : -1.0f;
        float float6;
        if (float4 >= 1.0f) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render(0, 0, float3);
            }
            final int integer5 = Mth.ceil((1.0f - Mth.clamp(float4 - 1.0f, 0.0f, 1.0f)) * 255.0f);
            GuiComponent.fill(0, 0, integer3, integer4, 0xFFFFFF | integer5 << 24);
            float6 = 1.0f - Mth.clamp(float4 - 1.0f, 0.0f, 1.0f);
        }
        else if (this.fadeIn) {
            if (this.minecraft.screen != null && float5 < 1.0f) {
                this.minecraft.screen.render(integer1, integer2, float3);
            }
            final int integer5 = Mth.ceil(Mth.clamp(float5, 0.15, 1.0) * 255.0);
            GuiComponent.fill(0, 0, integer3, integer4, 0xFFFFFF | integer5 << 24);
            float6 = Mth.clamp(float5, 0.0f, 1.0f);
        }
        else {
            GuiComponent.fill(0, 0, integer3, integer4, -1);
            float6 = 1.0f;
        }
        final int integer5 = (this.minecraft.window.getGuiScaledWidth() - 256) / 2;
        final int integer6 = (this.minecraft.window.getGuiScaledHeight() - 256) / 2;
        this.minecraft.getTextureManager().bind(LoadingOverlay.MOJANG_LOGO_LOCATION);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float6);
        this.blit(integer5, integer6, 0, 0, 256, 256);
        final float float7 = this.reload.getActualProgress();
        this.currentProgress = this.currentProgress * 0.95f + float7 * 0.050000012f;
        if (float4 < 1.0f) {
            this.drawProgressBar(integer3 / 2 - 150, integer4 / 4 * 3, integer3 / 2 + 150, integer4 / 4 * 3 + 10, this.currentProgress, 1.0f - Mth.clamp(float4, 0.0f, 1.0f));
        }
        if (float4 >= 2.0f) {
            this.minecraft.setOverlay(null);
        }
        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || float5 >= 2.0f)) {
            this.reload.checkExceptions();
            this.fadeOutStart = Util.getMillis();
            this.onFinish.run();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight());
            }
        }
    }
    
    private void drawProgressBar(final int integer1, final int integer2, final int integer3, final int integer4, final float float5, final float float6) {
        final int integer5 = Mth.ceil((integer3 - integer1 - 2) * float5);
        GuiComponent.fill(integer1 - 1, integer2 - 1, integer3 + 1, integer4 + 1, 0xFF000000 | Math.round((1.0f - float6) * 255.0f) << 16 | Math.round((1.0f - float6) * 255.0f) << 8 | Math.round((1.0f - float6) * 255.0f));
        GuiComponent.fill(integer1, integer2, integer3, integer4, -1);
        GuiComponent.fill(integer1 + 1, integer2 + 1, integer1 + integer5, integer4 - 1, 0xFF000000 | (int)Mth.lerp(1.0f - float6, 226.0f, 255.0f) << 16 | (int)Mth.lerp(1.0f - float6, 40.0f, 255.0f) << 8 | (int)Mth.lerp(1.0f - float6, 55.0f, 255.0f));
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
    
    static {
        MOJANG_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojang.png");
    }
    
    static class LogoTexture extends SimpleTexture {
        public LogoTexture() {
            super(LoadingOverlay.MOJANG_LOGO_LOCATION);
        }
        
        @Override
        protected TextureImage getTextureImage(final ResourceManager xi) {
            final Minecraft cyc3 = Minecraft.getInstance();
            final VanillaPack wo4 = cyc3.getClientPackSource().getVanillaPack();
            try (final InputStream inputStream5 = wo4.getResource(PackType.CLIENT_RESOURCES, LoadingOverlay.MOJANG_LOGO_LOCATION)) {
                return new TextureImage(null, NativeImage.read(inputStream5));
            }
            catch (IOException iOException5) {
                return new TextureImage(iOException5);
            }
        }
    }
}

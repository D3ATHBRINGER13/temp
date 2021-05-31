package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;
    
    public ImageButton(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final ResourceLocation qv, final OnPress a) {
        this(integer1, integer2, integer3, integer4, integer5, integer6, integer7, qv, 256, 256, a);
    }
    
    public ImageButton(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final ResourceLocation qv, final int integer9, final int integer10, final OnPress a) {
        this(integer1, integer2, integer3, integer4, integer5, integer6, integer7, qv, integer9, integer10, a, "");
    }
    
    public ImageButton(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final ResourceLocation qv, final int integer9, final int integer10, final OnPress a, final String string) {
        super(integer1, integer2, integer3, integer4, string, a);
        this.textureWidth = integer9;
        this.textureHeight = integer10;
        this.xTexStart = integer5;
        this.yTexStart = integer6;
        this.yDiffTex = integer7;
        this.resourceLocation = qv;
    }
    
    public void setPosition(final int integer1, final int integer2) {
        this.x = integer1;
        this.y = integer2;
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        final Minecraft cyc5 = Minecraft.getInstance();
        cyc5.getTextureManager().bind(this.resourceLocation);
        GlStateManager.disableDepthTest();
        int integer3 = this.yTexStart;
        if (this.isHovered()) {
            integer3 += this.yDiffTex;
        }
        GuiComponent.blit(this.x, this.y, (float)this.xTexStart, (float)integer3, this.width, this.height, this.textureWidth, this.textureHeight);
        GlStateManager.enableDepthTest();
    }
}

package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton extends AbstractWidget {
    protected ResourceLocation resourceLocation;
    protected boolean isStateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;
    
    public StateSwitchingButton(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        super(integer1, integer2, integer3, integer4, "");
        this.isStateTriggered = boolean5;
    }
    
    public void initTextureValues(final int integer1, final int integer2, final int integer3, final int integer4, final ResourceLocation qv) {
        this.xTexStart = integer1;
        this.yTexStart = integer2;
        this.xDiffTex = integer3;
        this.yDiffTex = integer4;
        this.resourceLocation = qv;
    }
    
    public void setStateTriggered(final boolean boolean1) {
        this.isStateTriggered = boolean1;
    }
    
    public boolean isStateTriggered() {
        return this.isStateTriggered;
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
        int integer3 = this.xTexStart;
        int integer4 = this.yTexStart;
        if (this.isStateTriggered) {
            integer3 += this.xDiffTex;
        }
        if (this.isHovered()) {
            integer4 += this.yDiffTex;
        }
        this.blit(this.x, this.y, integer3, integer4, this.width, this.height);
        GlStateManager.enableDepthTest();
    }
}

package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class Checkbox extends AbstractButton {
    private static final ResourceLocation TEXTURE;
    boolean selected;
    
    public Checkbox(final int integer1, final int integer2, final int integer3, final int integer4, final String string, final boolean boolean6) {
        super(integer1, integer2, integer3, integer4, string);
        this.selected = boolean6;
    }
    
    @Override
    public void onPress() {
        this.selected = !this.selected;
    }
    
    public boolean selected() {
        return this.selected;
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        final Minecraft cyc5 = Minecraft.getInstance();
        cyc5.getTextureManager().bind(Checkbox.TEXTURE);
        GlStateManager.enableDepthTest();
        final Font cyu6 = cyc5.font;
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiComponent.blit(this.x, this.y, 0.0f, this.selected ? 20.0f : 0.0f, 20, this.height, 32, 64);
        this.renderBg(cyc5, integer1, integer2);
        final int integer3 = 14737632;
        this.drawString(cyu6, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xE0E0E0 | Mth.ceil(this.alpha * 255.0f) << 24);
    }
    
    static {
        TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    }
}

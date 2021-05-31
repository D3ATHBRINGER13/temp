package net.minecraft.client.gui.components.spectator;

import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.GuiComponent;

public class SpectatorGui extends GuiComponent implements SpectatorMenuListener {
    private static final ResourceLocation WIDGETS_LOCATION;
    public static final ResourceLocation SPECTATOR_LOCATION;
    private final Minecraft minecraft;
    private long lastSelectionTime;
    private SpectatorMenu menu;
    
    public SpectatorGui(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void onHotbarSelected(final int integer) {
        this.lastSelectionTime = Util.getMillis();
        if (this.menu != null) {
            this.menu.selectSlot(integer);
        }
        else {
            this.menu = new SpectatorMenu(this);
        }
    }
    
    private float getHotbarAlpha() {
        final long long2 = this.lastSelectionTime - Util.getMillis() + 5000L;
        return Mth.clamp(long2 / 2000.0f, 0.0f, 1.0f);
    }
    
    public void renderHotbar(final float float1) {
        if (this.menu == null) {
            return;
        }
        final float float2 = this.getHotbarAlpha();
        if (float2 <= 0.0f) {
            this.menu.exit();
            return;
        }
        final int integer4 = this.minecraft.window.getGuiScaledWidth() / 2;
        final int integer5 = this.blitOffset;
        this.blitOffset = -90;
        final int integer6 = Mth.floor(this.minecraft.window.getGuiScaledHeight() - 22.0f * float2);
        final SpectatorPage dgc7 = this.menu.getCurrentPage();
        this.renderPage(float2, integer4, integer6, dgc7);
        this.blitOffset = integer5;
    }
    
    protected void renderPage(final float float1, final int integer2, final int integer3, final SpectatorPage dgc) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float1);
        this.minecraft.getTextureManager().bind(SpectatorGui.WIDGETS_LOCATION);
        this.blit(integer2 - 91, integer3, 0, 0, 182, 22);
        if (dgc.getSelectedSlot() >= 0) {
            this.blit(integer2 - 91 - 1 + dgc.getSelectedSlot() * 20, integer3 - 1, 0, 22, 24, 22);
        }
        Lighting.turnOnGui();
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.renderSlot(integer4, this.minecraft.window.getGuiScaledWidth() / 2 - 90 + integer4 * 20 + 2, (float)(integer3 + 3), float1, dgc.getItem(integer4));
        }
        Lighting.turnOff();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
    
    private void renderSlot(final int integer1, final int integer2, final float float3, final float float4, final SpectatorMenuItem dga) {
        this.minecraft.getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
        if (dga != SpectatorMenu.EMPTY_SLOT) {
            final int integer3 = (int)(float4 * 255.0f);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)integer2, float3, 0.0f);
            final float float5 = dga.isEnabled() ? 1.0f : 0.25f;
            GlStateManager.color4f(float5, float5, float5, float4);
            dga.renderIcon(float5, integer3);
            GlStateManager.popMatrix();
            final String string9 = String.valueOf(this.minecraft.options.keyHotbarSlots[integer1].getTranslatedKeyMessage());
            if (integer3 > 3 && dga.isEnabled()) {
                this.minecraft.font.drawShadow(string9, (float)(integer2 + 19 - 2 - this.minecraft.font.width(string9)), float3 + 6.0f + 3.0f, 16777215 + (integer3 << 24));
            }
        }
    }
    
    public void renderTooltip() {
        final int integer2 = (int)(this.getHotbarAlpha() * 255.0f);
        if (integer2 > 3 && this.menu != null) {
            final SpectatorMenuItem dga3 = this.menu.getSelectedItem();
            final String string4 = (dga3 == SpectatorMenu.EMPTY_SLOT) ? this.menu.getSelectedCategory().getPrompt().getColoredString() : dga3.getName().getColoredString();
            if (string4 != null) {
                final int integer3 = (this.minecraft.window.getGuiScaledWidth() - this.minecraft.font.width(string4)) / 2;
                final int integer4 = this.minecraft.window.getGuiScaledHeight() - 35;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.minecraft.font.drawShadow(string4, (float)integer3, (float)integer4, 16777215 + (integer2 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }
    
    @Override
    public void onSpectatorMenuClosed(final SpectatorMenu dfy) {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }
    
    public boolean isMenuActive() {
        return this.menu != null;
    }
    
    public void onMouseScrolled(final double double1) {
        int integer4;
        for (integer4 = this.menu.getSelectedSlot() + (int)double1; integer4 >= 0 && integer4 <= 8 && (this.menu.getItem(integer4) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(integer4).isEnabled()); integer4 += (int)double1) {}
        if (integer4 >= 0 && integer4 <= 8) {
            this.menu.selectSlot(integer4);
            this.lastSelectionTime = Util.getMillis();
        }
    }
    
    public void onMouseMiddleClick() {
        this.lastSelectionTime = Util.getMillis();
        if (this.isMenuActive()) {
            final int integer2 = this.menu.getSelectedSlot();
            if (integer2 != -1) {
                this.menu.selectSlot(integer2);
            }
        }
        else {
            this.menu = new SpectatorMenu(this);
        }
    }
    
    static {
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
        SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
    }
}

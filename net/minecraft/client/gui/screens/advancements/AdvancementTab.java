package net.minecraft.client.gui.screens.advancements;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public class AdvancementTab extends GuiComponent {
    private final Minecraft minecraft;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final String title;
    private final AdvancementWidget root;
    private final Map<Advancement, AdvancementWidget> widgets;
    private double scrollX;
    private double scrollY;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private float fade;
    private boolean centered;
    
    public AdvancementTab(final Minecraft cyc, final AdvancementsScreen dcz, final AdvancementTabType dcw, final int integer, final Advancement q, final DisplayInfo z) {
        this.widgets = (Map<Advancement, AdvancementWidget>)Maps.newLinkedHashMap();
        this.minX = Integer.MAX_VALUE;
        this.minY = Integer.MAX_VALUE;
        this.maxX = Integer.MIN_VALUE;
        this.maxY = Integer.MIN_VALUE;
        this.minecraft = cyc;
        this.screen = dcz;
        this.type = dcw;
        this.index = integer;
        this.advancement = q;
        this.display = z;
        this.icon = z.getIcon();
        this.title = z.getTitle().getColoredString();
        this.addWidget(this.root = new AdvancementWidget(this, cyc, q, z), q);
    }
    
    public Advancement getAdvancement() {
        return this.advancement;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void drawTab(final int integer1, final int integer2, final boolean boolean3) {
        this.type.draw(this, integer1, integer2, boolean3, this.index);
    }
    
    public void drawIcon(final int integer1, final int integer2, final ItemRenderer dsv) {
        this.type.drawIcon(integer1, integer2, this.index, dsv, this.icon);
    }
    
    public void drawContents() {
        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2;
            this.scrollY = 56 - (this.maxY + this.minY) / 2;
            this.centered = true;
        }
        GlStateManager.depthFunc(518);
        GuiComponent.fill(0, 0, 234, 113, -16777216);
        GlStateManager.depthFunc(515);
        final ResourceLocation qv2 = this.display.getBackground();
        if (qv2 != null) {
            this.minecraft.getTextureManager().bind(qv2);
        }
        else {
            this.minecraft.getTextureManager().bind(TextureManager.INTENTIONAL_MISSING_TEXTURE);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer3 = Mth.floor(this.scrollX);
        final int integer4 = Mth.floor(this.scrollY);
        final int integer5 = integer3 % 16;
        final int integer6 = integer4 % 16;
        for (int integer7 = -1; integer7 <= 15; ++integer7) {
            for (int integer8 = -1; integer8 <= 8; ++integer8) {
                GuiComponent.blit(integer5 + 16 * integer7, integer6 + 16 * integer8, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.root.drawConnectivity(integer3, integer4, true);
        this.root.drawConnectivity(integer3, integer4, false);
        this.root.draw(integer3, integer4);
    }
    
    public void drawTooltips(final int integer1, final int integer2, final int integer3, final int integer4) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 200.0f);
        GuiComponent.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0f) << 24);
        boolean boolean6 = false;
        final int integer5 = Mth.floor(this.scrollX);
        final int integer6 = Mth.floor(this.scrollY);
        if (integer1 > 0 && integer1 < 234 && integer2 > 0 && integer2 < 113) {
            for (final AdvancementWidget dcx10 : this.widgets.values()) {
                if (dcx10.isMouseOver(integer5, integer6, integer1, integer2)) {
                    boolean6 = true;
                    dcx10.drawHover(integer5, integer6, this.fade, integer3, integer4);
                    break;
                }
            }
        }
        GlStateManager.popMatrix();
        if (boolean6) {
            this.fade = Mth.clamp(this.fade + 0.02f, 0.0f, 0.3f);
        }
        else {
            this.fade = Mth.clamp(this.fade - 0.04f, 0.0f, 1.0f);
        }
    }
    
    public boolean isMouseOver(final int integer1, final int integer2, final double double3, final double double4) {
        return this.type.isMouseOver(integer1, integer2, this.index, double3, double4);
    }
    
    @Nullable
    public static AdvancementTab create(final Minecraft cyc, final AdvancementsScreen dcz, int integer, final Advancement q) {
        if (q.getDisplay() == null) {
            return null;
        }
        for (final AdvancementTabType dcw8 : AdvancementTabType.values()) {
            if (integer < dcw8.getMax()) {
                return new AdvancementTab(cyc, dcz, dcw8, integer, q, q.getDisplay());
            }
            integer -= dcw8.getMax();
        }
        return null;
    }
    
    public void scroll(final double double1, final double double2) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + double1, -(this.maxX - 234), 0.0);
        }
        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + double2, -(this.maxY - 113), 0.0);
        }
    }
    
    public void addAdvancement(final Advancement q) {
        if (q.getDisplay() == null) {
            return;
        }
        final AdvancementWidget dcx3 = new AdvancementWidget(this, this.minecraft, q, q.getDisplay());
        this.addWidget(dcx3, q);
    }
    
    private void addWidget(final AdvancementWidget dcx, final Advancement q) {
        this.widgets.put(q, dcx);
        final int integer4 = dcx.getX();
        final int integer5 = integer4 + 28;
        final int integer6 = dcx.getY();
        final int integer7 = integer6 + 27;
        this.minX = Math.min(this.minX, integer4);
        this.maxX = Math.max(this.maxX, integer5);
        this.minY = Math.min(this.minY, integer6);
        this.maxY = Math.max(this.maxY, integer7);
        for (final AdvancementWidget dcx2 : this.widgets.values()) {
            dcx2.attachToParent();
        }
    }
    
    @Nullable
    public AdvancementWidget getWidget(final Advancement q) {
        return (AdvancementWidget)this.widgets.get(q);
    }
    
    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}

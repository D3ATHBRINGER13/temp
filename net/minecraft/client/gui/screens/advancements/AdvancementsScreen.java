package net.minecraft.client.gui.screens.advancements;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementProgress;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.GuiComponent;
import java.util.Iterator;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.advancements.Advancement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.gui.screens.Screen;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
    private static final ResourceLocation WINDOW_LOCATION;
    private static final ResourceLocation TABS_LOCATION;
    private final ClientAdvancements advancements;
    private final Map<Advancement, AdvancementTab> tabs;
    private AdvancementTab selectedTab;
    private boolean isScrolling;
    
    public AdvancementsScreen(final ClientAdvancements djz) {
        super(NarratorChatListener.NO_TITLE);
        this.tabs = (Map<Advancement, AdvancementTab>)Maps.newLinkedHashMap();
        this.advancements = djz;
    }
    
    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.advancements.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.advancements.setSelectedTab(((AdvancementTab)this.tabs.values().iterator().next()).getAdvancement(), true);
        }
        else {
            this.advancements.setSelectedTab((this.selectedTab == null) ? null : this.selectedTab.getAdvancement(), true);
        }
    }
    
    @Override
    public void removed() {
        this.advancements.setListener(null);
        final ClientPacketListener dkc2 = this.minecraft.getConnection();
        if (dkc2 != null) {
            dkc2.send(ServerboundSeenAdvancementsPacket.closedScreen());
        }
    }
    
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (integer == 0) {
            final int integer2 = (this.width - 252) / 2;
            final int integer3 = (this.height - 140) / 2;
            for (final AdvancementTab dcv10 : this.tabs.values()) {
                if (dcv10.isMouseOver(integer2, integer3, double1, double2)) {
                    this.advancements.setSelectedTab(dcv10.getAdvancement(), true);
                    break;
                }
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (this.minecraft.options.keyAdvancements.matches(integer1, integer2)) {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        final int integer3 = (this.width - 252) / 2;
        final int integer4 = (this.height - 140) / 2;
        this.renderBackground();
        this.renderInside(integer1, integer2, integer3, integer4);
        this.renderWindow(integer3, integer4);
        this.renderTooltips(integer1, integer2, integer3, integer4);
    }
    
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (integer != 0) {
            return this.isScrolling = false;
        }
        if (!this.isScrolling) {
            this.isScrolling = true;
        }
        else if (this.selectedTab != null) {
            this.selectedTab.scroll(double4, double5);
        }
        return true;
    }
    
    private void renderInside(final int integer1, final int integer2, final int integer3, final int integer4) {
        final AdvancementTab dcv6 = this.selectedTab;
        if (dcv6 == null) {
            GuiComponent.fill(integer3 + 9, integer4 + 18, integer3 + 9 + 234, integer4 + 18 + 113, -16777216);
            final String string7 = I18n.get("advancements.empty");
            final int integer5 = this.font.width(string7);
            final Font font = this.font;
            final String string8 = string7;
            final float float2 = (float)(integer3 + 9 + 117 - integer5 / 2);
            final int n = integer4 + 18 + 56;
            this.font.getClass();
            font.draw(string8, float2, (float)(n - 9 / 2), -1);
            final Font font2 = this.font;
            final String string9 = ":(";
            final float float3 = (float)(integer3 + 9 + 117 - this.font.width(":(") / 2);
            final int n2 = integer4 + 18 + 113;
            this.font.getClass();
            font2.draw(string9, float3, (float)(n2 - 9), -1);
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(integer3 + 9), (float)(integer4 + 18), -400.0f);
        GlStateManager.enableDepthTest();
        dcv6.drawContents();
        GlStateManager.popMatrix();
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepthTest();
    }
    
    public void renderWindow(final int integer1, final int integer2) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        Lighting.turnOff();
        this.minecraft.getTextureManager().bind(AdvancementsScreen.WINDOW_LOCATION);
        this.blit(integer1, integer2, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bind(AdvancementsScreen.TABS_LOCATION);
            for (final AdvancementTab dcv5 : this.tabs.values()) {
                dcv5.drawTab(integer1, integer2, dcv5 == this.selectedTab);
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Lighting.turnOnGui();
            for (final AdvancementTab dcv5 : this.tabs.values()) {
                dcv5.drawIcon(integer1, integer2, this.itemRenderer);
            }
            GlStateManager.disableBlend();
        }
        this.font.draw(I18n.get("gui.advancements"), (float)(integer1 + 8), (float)(integer2 + 6), 4210752);
    }
    
    private void renderTooltips(final int integer1, final int integer2, final int integer3, final int integer4) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translatef((float)(integer3 + 9), (float)(integer4 + 18), 400.0f);
            this.selectedTab.drawTooltips(integer1 - integer3 - 9, integer2 - integer4 - 18, integer3, integer4);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }
        if (this.tabs.size() > 1) {
            for (final AdvancementTab dcv7 : this.tabs.values()) {
                if (dcv7.isMouseOver(integer3, integer4, integer1, integer2)) {
                    this.renderTooltip(dcv7.getTitle(), integer1, integer2);
                }
            }
        }
    }
    
    public void onAddAdvancementRoot(final Advancement q) {
        final AdvancementTab dcv3 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), q);
        if (dcv3 == null) {
            return;
        }
        this.tabs.put(q, dcv3);
    }
    
    public void onRemoveAdvancementRoot(final Advancement q) {
    }
    
    public void onAddAdvancementTask(final Advancement q) {
        final AdvancementTab dcv3 = this.getTab(q);
        if (dcv3 != null) {
            dcv3.addAdvancement(q);
        }
    }
    
    public void onRemoveAdvancementTask(final Advancement q) {
    }
    
    @Override
    public void onUpdateAdvancementProgress(final Advancement q, final AdvancementProgress s) {
        final AdvancementWidget dcx4 = this.getAdvancementWidget(q);
        if (dcx4 != null) {
            dcx4.setProgress(s);
        }
    }
    
    @Override
    public void onSelectedTabChanged(@Nullable final Advancement q) {
        this.selectedTab = (AdvancementTab)this.tabs.get(q);
    }
    
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }
    
    @Nullable
    public AdvancementWidget getAdvancementWidget(final Advancement q) {
        final AdvancementTab dcv3 = this.getTab(q);
        return (dcv3 == null) ? null : dcv3.getWidget(q);
    }
    
    @Nullable
    private AdvancementTab getTab(Advancement q) {
        while (q.getParent() != null) {
            q = q.getParent();
        }
        return (AdvancementTab)this.tabs.get(q);
    }
    
    static {
        WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
        TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    }
}

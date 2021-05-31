package net.minecraft.client.gui.screens.resourcepacks.lists;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import java.util.List;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.server.packs.repository.PackCompatibility;
import java.util.function.Function;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.client.gui.screens.resourcepacks.ResourcePackSelectScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class ResourcePackList extends ObjectSelectionList<ResourcePackEntry> {
    private static final ResourceLocation ICON_OVERLAY_LOCATION;
    private static final Component INCOMPATIBLE_TITLE;
    private static final Component INCOMPATIBLE_CONFIRM_TITLE;
    protected final Minecraft minecraft;
    private final Component title;
    
    public ResourcePackList(final Minecraft cyc, final int integer2, final int integer3, final Component jo) {
        super(cyc, integer2, integer3, 32, integer3 - 55 + 4, 36);
        this.minecraft = cyc;
        this.centerListVertically = false;
        final boolean boolean1 = true;
        cyc.font.getClass();
        this.setRenderHeader(boolean1, (int)(9.0f * 1.5f));
        this.title = jo;
    }
    
    @Override
    protected void renderHeader(final int integer1, final int integer2, final Tesselator cuz) {
        final Component jo5 = new TextComponent("").append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        this.minecraft.font.draw(jo5.getColoredString(), (float)(integer1 + this.width / 2 - this.minecraft.font.width(jo5.getColoredString()) / 2), (float)Math.min(this.y0 + 3, integer2), 16777215);
    }
    
    @Override
    public int getRowWidth() {
        return this.width;
    }
    
    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }
    
    public void addResourcePackEntry(final ResourcePackEntry a) {
        this.addEntry(a);
        a.parent = this;
    }
    
    static {
        ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
        INCOMPATIBLE_TITLE = new TranslatableComponent("resourcePack.incompatible", new Object[0]);
        INCOMPATIBLE_CONFIRM_TITLE = new TranslatableComponent("resourcePack.incompatible.confirm.title", new Object[0]);
    }
    
    public static class ResourcePackEntry extends Entry<ResourcePackEntry> {
        private ResourcePackList parent;
        protected final Minecraft minecraft;
        protected final ResourcePackSelectScreen screen;
        private final UnopenedResourcePack resourcePack;
        
        public ResourcePackEntry(final ResourcePackList dfl, final ResourcePackSelectScreen dfi, final UnopenedResourcePack dxw) {
            this.screen = dfi;
            this.minecraft = Minecraft.getInstance();
            this.resourcePack = dxw;
            this.parent = dfl;
        }
        
        public void addToList(final SelectedResourcePackList dfm) {
            this.getResourcePack().getDefaultPosition().<ResourcePackEntry, UnopenedPack>insert(dfm.children(), this, (java.util.function.Function<ResourcePackEntry, UnopenedPack>)ResourcePackEntry::getResourcePack, true);
            this.parent = dfm;
        }
        
        protected void bindToIcon() {
            this.resourcePack.bindIcon(this.minecraft.getTextureManager());
        }
        
        protected PackCompatibility getCompatibility() {
            return this.resourcePack.getCompatibility();
        }
        
        protected String getDescription() {
            return this.resourcePack.getDescription().getColoredString();
        }
        
        protected String getName() {
            return this.resourcePack.getTitle().getColoredString();
        }
        
        public UnopenedResourcePack getResourcePack() {
            return this.resourcePack;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            final PackCompatibility ww11 = this.getCompatibility();
            if (!ww11.isCompatible()) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.fill(integer3 - 1, integer2 - 1, integer3 + integer4 - 9, integer2 + integer5 + 1, -8978432);
            }
            this.bindToIcon();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            String string12 = this.getName();
            String string13 = this.getDescription();
            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen || boolean8)) {
                this.minecraft.getTextureManager().bind(ResourcePackList.ICON_OVERLAY_LOCATION);
                GuiComponent.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer8 = integer6 - integer3;
                final int integer9 = integer7 - integer2;
                if (!ww11.isCompatible()) {
                    string12 = ResourcePackList.INCOMPATIBLE_TITLE.getColoredString();
                    string13 = ww11.getDescription().getColoredString();
                }
                if (this.canMoveRight()) {
                    if (integer8 < 32) {
                        GuiComponent.blit(integer3, integer2, 0.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        GuiComponent.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                else {
                    if (this.canMoveLeft()) {
                        if (integer8 < 16) {
                            GuiComponent.blit(integer3, integer2, 32.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            GuiComponent.blit(integer3, integer2, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveUp()) {
                        if (integer8 < 32 && integer8 > 16 && integer9 < 16) {
                            GuiComponent.blit(integer3, integer2, 96.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            GuiComponent.blit(integer3, integer2, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveDown()) {
                        if (integer8 < 32 && integer8 > 16 && integer9 > 16) {
                            GuiComponent.blit(integer3, integer2, 64.0f, 32.0f, 32, 32, 256, 256);
                        }
                        else {
                            GuiComponent.blit(integer3, integer2, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            final int integer8 = this.minecraft.font.width(string12);
            if (integer8 > 157) {
                string12 = this.minecraft.font.substrByWidth(string12, 157 - this.minecraft.font.width("...")) + "...";
            }
            this.minecraft.font.drawShadow(string12, (float)(integer3 + 32 + 2), (float)(integer2 + 1), 16777215);
            final List<String> list15 = this.minecraft.font.split(string13, 157);
            for (int integer10 = 0; integer10 < 2 && integer10 < list15.size(); ++integer10) {
                this.minecraft.font.drawShadow((String)list15.get(integer10), (float)(integer3 + 32 + 2), (float)(integer2 + 12 + 10 * integer10), 8421504);
            }
        }
        
        protected boolean showHoverOverlay() {
            return !this.resourcePack.isFixedPosition() || !this.resourcePack.isRequired();
        }
        
        protected boolean canMoveRight() {
            return !this.screen.isSelected(this);
        }
        
        protected boolean canMoveLeft() {
            return this.screen.isSelected(this) && !this.resourcePack.isRequired();
        }
        
        protected boolean canMoveUp() {
            final List<ResourcePackEntry> list2 = this.parent.children();
            final int integer3 = list2.indexOf(this);
            return integer3 > 0 && !((ResourcePackEntry)list2.get(integer3 - 1)).resourcePack.isFixedPosition();
        }
        
        protected boolean canMoveDown() {
            final List<ResourcePackEntry> list2 = this.parent.children();
            final int integer3 = list2.indexOf(this);
            return integer3 >= 0 && integer3 < list2.size() - 1 && !((ResourcePackEntry)list2.get(integer3 + 1)).resourcePack.isFixedPosition();
        }
        
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            final double double3 = double1 - this.parent.getRowLeft();
            final double double4 = double2 - this.parent.getRowTop(this.parent.children().indexOf(this));
            if (this.showHoverOverlay() && double3 <= 32.0) {
                if (this.canMoveRight()) {
                    this.getScreen().setChanged();
                    final PackCompatibility ww11 = this.getCompatibility();
                    if (ww11.isCompatible()) {
                        this.getScreen().select(this);
                    }
                    else {
                        final Component jo12 = ww11.getConfirmation();
                        this.minecraft.setScreen(new ConfirmScreen(boolean1 -> {
                            this.minecraft.setScreen(this.getScreen());
                            if (boolean1) {
                                this.getScreen().select(this);
                            }
                        }, ResourcePackList.INCOMPATIBLE_CONFIRM_TITLE, jo12));
                    }
                    return true;
                }
                if (double3 < 16.0 && this.canMoveLeft()) {
                    this.getScreen().deselect(this);
                    return true;
                }
                if (double3 > 16.0 && double4 < 16.0 && this.canMoveUp()) {
                    final List<ResourcePackEntry> list11 = this.parent.children();
                    final int integer2 = list11.indexOf(this);
                    list11.remove(this);
                    list11.add(integer2 - 1, this);
                    this.getScreen().setChanged();
                    return true;
                }
                if (double3 > 16.0 && double4 > 16.0 && this.canMoveDown()) {
                    final List<ResourcePackEntry> list11 = this.parent.children();
                    final int integer2 = list11.indexOf(this);
                    list11.remove(this);
                    list11.add(integer2 + 1, this);
                    this.getScreen().setChanged();
                    return true;
                }
            }
            return false;
        }
        
        public ResourcePackSelectScreen getScreen() {
            return this.screen;
        }
    }
}

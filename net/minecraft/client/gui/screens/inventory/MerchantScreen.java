package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import java.util.Iterator;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MerchantMenu;

public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
    private static final ResourceLocation VILLAGER_LOCATION;
    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons;
    private int scrollOff;
    private boolean isDragging;
    
    public MerchantScreen(final MerchantMenu azn, final Inventory awf, final Component jo) {
        super(azn, awf, jo);
        this.tradeOfferButtons = new TradeOfferButton[7];
        this.imageWidth = 276;
    }
    
    private void postButtonClick() {
        ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
        ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
        this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
    }
    
    @Override
    protected void init() {
        super.init();
        final int integer2 = (this.width - this.imageWidth) / 2;
        final int integer3 = (this.height - this.imageHeight) / 2;
        int integer4 = integer3 + 16 + 2;
        for (int integer5 = 0; integer5 < 7; ++integer5) {
            this.tradeOfferButtons[integer5] = this.<TradeOfferButton>addButton(new TradeOfferButton(integer2 + 5, integer4, integer5, czi -> {
                if (czi instanceof TradeOfferButton) {
                    this.shopItem = czi.getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
                return;
            }));
            integer4 += 20;
        }
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        final int integer3 = ((MerchantMenu)this.menu).getTraderLevel();
        final int integer4 = this.imageHeight - 94;
        if (integer3 > 0 && integer3 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
            final String string6 = this.title.getColoredString();
            final String string7 = "- " + I18n.get(new StringBuilder().append("merchant.level.").append(integer3).toString());
            final int integer5 = this.font.width(string6);
            final int integer6 = this.font.width(string7);
            final int integer7 = integer5 + integer6 + 3;
            final int integer8 = 49 + this.imageWidth / 2 - integer7 / 2;
            this.font.draw(string6, (float)integer8, 6.0f, 4210752);
            this.font.draw(this.inventory.getDisplayName().getColoredString(), 107.0f, (float)integer4, 4210752);
            this.font.draw(string7, (float)(integer8 + integer5 + 3), 6.0f, 4210752);
        }
        else {
            final String string6 = this.title.getColoredString();
            this.font.draw(string6, (float)(49 + this.imageWidth / 2 - this.font.width(string6) / 2), 6.0f, 4210752);
            this.font.draw(this.inventory.getDisplayName().getColoredString(), 107.0f, (float)integer4, 4210752);
        }
        final String string6 = I18n.get("merchant.trades");
        final int integer9 = this.font.width(string6);
        this.font.draw(string6, (float)(5 - integer9 / 2 + 48), 6.0f, 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        GuiComponent.blit(integer4, integer5, this.blitOffset, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 512);
        final MerchantOffers bgv7 = ((MerchantMenu)this.menu).getOffers();
        if (!bgv7.isEmpty()) {
            final int integer6 = this.shopItem;
            if (integer6 < 0 || integer6 >= bgv7.size()) {
                return;
            }
            final MerchantOffer bgu9 = (MerchantOffer)bgv7.get(integer6);
            if (bgu9.isOutOfStock()) {
                this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                GuiComponent.blit(this.leftPos + 83 + 99, this.topPos + 35, this.blitOffset, 311.0f, 0.0f, 28, 21, 256, 512);
            }
        }
    }
    
    private void renderProgressBar(final int integer1, final int integer2, final MerchantOffer bgu) {
        this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
        final int integer3 = ((MerchantMenu)this.menu).getTraderLevel();
        final int integer4 = ((MerchantMenu)this.menu).getTraderXp();
        if (integer3 >= 5) {
            return;
        }
        GuiComponent.blit(integer1 + 136, integer2 + 16, this.blitOffset, 0.0f, 186.0f, 102, 5, 256, 512);
        final int integer5 = VillagerData.getMinXpPerLevel(integer3);
        if (integer4 < integer5 || !VillagerData.canLevelUp(integer3)) {
            return;
        }
        final int integer6 = 100;
        final float float9 = (float)(100 / (VillagerData.getMaxXpPerLevel(integer3) - integer5));
        final int integer7 = Mth.floor(float9 * (integer4 - integer5));
        GuiComponent.blit(integer1 + 136, integer2 + 16, this.blitOffset, 0.0f, 191.0f, integer7 + 1, 5, 256, 512);
        final int integer8 = ((MerchantMenu)this.menu).getFutureTraderXp();
        if (integer8 > 0) {
            final int integer9 = Math.min(Mth.floor(integer8 * float9), 100 - integer7);
            GuiComponent.blit(integer1 + 136 + integer7 + 1, integer2 + 16 + 1, this.blitOffset, 2.0f, 182.0f, integer9, 3, 256, 512);
        }
    }
    
    private void renderScroller(final int integer1, final int integer2, final MerchantOffers bgv) {
        Lighting.turnOff();
        final int integer3 = bgv.size() + 1 - 7;
        if (integer3 > 1) {
            final int integer4 = 139 - (27 + (integer3 - 1) * 139 / integer3);
            final int integer5 = 1 + integer4 / integer3 + 139 / integer3;
            final int integer6 = 113;
            int integer7 = Math.min(113, this.scrollOff * integer5);
            if (this.scrollOff == integer3 - 1) {
                integer7 = 113;
            }
            GuiComponent.blit(integer1 + 94, integer2 + 18 + integer7, this.blitOffset, 0.0f, 199.0f, 6, 27, 256, 512);
        }
        else {
            GuiComponent.blit(integer1 + 94, integer2 + 18, this.blitOffset, 6.0f, 199.0f, 6, 27, 256, 512);
        }
        Lighting.turnOnGui();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        final MerchantOffers bgv5 = ((MerchantMenu)this.menu).getOffers();
        if (!bgv5.isEmpty()) {
            final int integer3 = (this.width - this.imageWidth) / 2;
            final int integer4 = (this.height - this.imageHeight) / 2;
            int integer5 = integer4 + 16 + 1;
            final int integer6 = integer3 + 5 + 5;
            GlStateManager.pushMatrix();
            Lighting.turnOnGui();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
            this.renderScroller(integer3, integer4, bgv5);
            int integer7 = 0;
            for (final MerchantOffer bgu12 : bgv5) {
                if (this.canScroll(bgv5.size()) && (integer7 < this.scrollOff || integer7 >= 7 + this.scrollOff)) {
                    ++integer7;
                }
                else {
                    final ItemStack bcj13 = bgu12.getBaseCostA();
                    final ItemStack bcj14 = bgu12.getCostA();
                    final ItemStack bcj15 = bgu12.getCostB();
                    final ItemStack bcj16 = bgu12.getResult();
                    this.itemRenderer.blitOffset = 100.0f;
                    final int integer8 = integer5 + 2;
                    this.renderAndDecorateCostA(bcj14, bcj13, integer6, integer8);
                    if (!bcj15.isEmpty()) {
                        this.itemRenderer.renderAndDecorateItem(bcj15, integer3 + 5 + 35, integer8);
                        this.itemRenderer.renderGuiItemDecorations(this.font, bcj15, integer3 + 5 + 35, integer8);
                    }
                    this.renderButtonArrows(bgu12, integer3, integer8);
                    this.itemRenderer.renderAndDecorateItem(bcj16, integer3 + 5 + 68, integer8);
                    this.itemRenderer.renderGuiItemDecorations(this.font, bcj16, integer3 + 5 + 68, integer8);
                    this.itemRenderer.blitOffset = 0.0f;
                    integer5 += 20;
                    ++integer7;
                }
            }
            final int integer9 = this.shopItem;
            MerchantOffer bgu12 = (MerchantOffer)bgv5.get(integer9);
            GlStateManager.disableLighting();
            if (((MerchantMenu)this.menu).showProgressBar()) {
                this.renderProgressBar(integer3, integer4, bgu12);
            }
            if (bgu12.isOutOfStock() && this.isHovering(186, 35, 22, 21, integer1, integer2) && ((MerchantMenu)this.menu).canRestock()) {
                this.renderTooltip(I18n.get("merchant.deprecated"), integer1, integer2);
            }
            for (final TradeOfferButton a16 : this.tradeOfferButtons) {
                if (a16.isHovered()) {
                    a16.renderToolTip(integer1, integer2);
                }
                a16.visible = (a16.index < ((MerchantMenu)this.menu).getOffers().size());
            }
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            Lighting.turnOn();
        }
        this.renderTooltip(integer1, integer2);
    }
    
    private void renderButtonArrows(final MerchantOffer bgu, final int integer2, final int integer3) {
        Lighting.turnOff();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
        if (bgu.isOutOfStock()) {
            GuiComponent.blit(integer2 + 5 + 35 + 20, integer3 + 3, this.blitOffset, 25.0f, 171.0f, 10, 9, 256, 512);
        }
        else {
            GuiComponent.blit(integer2 + 5 + 35 + 20, integer3 + 3, this.blitOffset, 15.0f, 171.0f, 10, 9, 256, 512);
        }
        Lighting.turnOnGui();
    }
    
    private void renderAndDecorateCostA(final ItemStack bcj1, final ItemStack bcj2, final int integer3, final int integer4) {
        this.itemRenderer.renderAndDecorateItem(bcj1, integer3, integer4);
        if (bcj2.getCount() == bcj1.getCount()) {
            this.itemRenderer.renderGuiItemDecorations(this.font, bcj1, integer3, integer4);
        }
        else {
            this.itemRenderer.renderGuiItemDecorations(this.font, bcj2, integer3, integer4, (bcj2.getCount() == 1) ? "1" : null);
            this.itemRenderer.renderGuiItemDecorations(this.font, bcj1, integer3 + 14, integer4, (bcj1.getCount() == 1) ? "1" : null);
            this.minecraft.getTextureManager().bind(MerchantScreen.VILLAGER_LOCATION);
            this.blitOffset += 300;
            Lighting.turnOff();
            GuiComponent.blit(integer3 + 7, integer4 + 12, this.blitOffset, 0.0f, 176.0f, 9, 2, 256, 512);
            Lighting.turnOnGui();
            this.blitOffset -= 300;
        }
    }
    
    private boolean canScroll(final int integer) {
        return integer > 7;
    }
    
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        final int integer8 = ((MerchantMenu)this.menu).getOffers().size();
        if (this.canScroll(integer8)) {
            final int integer9 = integer8 - 7;
            this.scrollOff -= (int)double3;
            this.scrollOff = Mth.clamp(this.scrollOff, 0, integer9);
        }
        return true;
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        final int integer2 = ((MerchantMenu)this.menu).getOffers().size();
        if (this.isDragging) {
            final int integer3 = this.topPos + 18;
            final int integer4 = integer3 + 139;
            final int integer5 = integer2 - 7;
            float float15 = ((float)double2 - integer3 - 13.5f) / (integer4 - integer3 - 27.0f);
            float15 = float15 * integer5 + 0.5f;
            this.scrollOff = Mth.clamp((int)float15, 0, integer5);
            return true;
        }
        return super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        this.isDragging = false;
        final int integer2 = (this.width - this.imageWidth) / 2;
        final int integer3 = (this.height - this.imageHeight) / 2;
        if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && double1 > integer2 + 94 && double1 < integer2 + 94 + 6 && double2 > integer3 + 18 && double2 <= integer3 + 18 + 139 + 1) {
            this.isDragging = true;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    static {
        VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    }
    
    class TradeOfferButton extends Button {
        final int index;
        
        public TradeOfferButton(final int integer2, final int integer3, final int integer4, final OnPress a) {
            super(integer2, integer3, 89, 20, "", a);
            this.index = integer4;
            this.visible = false;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        @Override
        public void renderToolTip(final int integer1, final int integer2) {
            if (this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
                if (integer1 < this.x + 20) {
                    final ItemStack bcj4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
                    Screen.this.renderTooltip(bcj4, integer1, integer2);
                }
                else if (integer1 < this.x + 50 && integer1 > this.x + 30) {
                    final ItemStack bcj4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
                    if (!bcj4.isEmpty()) {
                        Screen.this.renderTooltip(bcj4, integer1, integer2);
                    }
                }
                else if (integer1 > this.x + 65) {
                    final ItemStack bcj4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
                    Screen.this.renderTooltip(bcj4, integer1, integer2);
                }
            }
        }
    }
}

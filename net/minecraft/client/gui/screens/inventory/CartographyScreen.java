package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CartographyMenu;

public class CartographyScreen extends AbstractContainerScreen<CartographyMenu> {
    private static final ResourceLocation BG_LOCATION;
    
    public CartographyScreen(final CartographyMenu ayq, final Inventory awf, final Component jo) {
        super(ayq, awf, jo);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 4.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(CartographyScreen.BG_LOCATION);
        final int integer4 = this.leftPos;
        final int integer5 = this.topPos;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        final Item bce7 = ((CartographyMenu)this.menu).getSlot(1).getItem().getItem();
        final boolean boolean8 = bce7 == Items.MAP;
        final boolean boolean9 = bce7 == Items.PAPER;
        final boolean boolean10 = bce7 == Items.GLASS_PANE;
        final ItemStack bcj11 = ((CartographyMenu)this.menu).getSlot(0).getItem();
        boolean boolean11 = false;
        MapItemSavedData coh12;
        if (bcj11.getItem() == Items.FILLED_MAP) {
            coh12 = MapItem.getSavedData(bcj11, this.minecraft.level);
            if (coh12 != null) {
                if (coh12.locked) {
                    boolean11 = true;
                    if (boolean9 || boolean10) {
                        this.blit(integer4 + 35, integer5 + 31, this.imageWidth + 50, 132, 28, 21);
                    }
                }
                if (boolean9 && coh12.scale >= 4) {
                    boolean11 = true;
                    this.blit(integer4 + 35, integer5 + 31, this.imageWidth + 50, 132, 28, 21);
                }
            }
        }
        else {
            coh12 = null;
        }
        this.renderResultingMap(coh12, boolean8, boolean9, boolean10, boolean11);
    }
    
    private void renderResultingMap(@Nullable final MapItemSavedData coh, final boolean boolean2, final boolean boolean3, final boolean boolean4, final boolean boolean5) {
        final int integer7 = this.leftPos;
        final int integer8 = this.topPos;
        if (boolean3 && !boolean5) {
            this.blit(integer7 + 67, integer8 + 13, this.imageWidth, 66, 66, 66);
            this.renderMap(coh, integer7 + 85, integer8 + 31, 0.226f);
        }
        else if (boolean2) {
            this.blit(integer7 + 67 + 16, integer8 + 13, this.imageWidth, 132, 50, 66);
            this.renderMap(coh, integer7 + 86, integer8 + 16, 0.34f);
            this.minecraft.getTextureManager().bind(CartographyScreen.BG_LOCATION);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(integer7 + 67, integer8 + 13 + 16, this.imageWidth, 132, 50, 66);
            this.renderMap(coh, integer7 + 70, integer8 + 32, 0.34f);
            GlStateManager.popMatrix();
        }
        else if (boolean4) {
            this.blit(integer7 + 67, integer8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(coh, integer7 + 71, integer8 + 17, 0.45f);
            this.minecraft.getTextureManager().bind(CartographyScreen.BG_LOCATION);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(integer7 + 66, integer8 + 12, 0, this.imageHeight, 66, 66);
            GlStateManager.popMatrix();
        }
        else {
            this.blit(integer7 + 67, integer8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(coh, integer7 + 71, integer8 + 17, 0.45f);
        }
    }
    
    private void renderMap(@Nullable final MapItemSavedData coh, final int integer2, final int integer3, final float float4) {
        if (coh != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)integer2, (float)integer3, 1.0f);
            GlStateManager.scalef(float4, float4, 1.0f);
            this.minecraft.gameRenderer.getMapRenderer().render(coh, true);
            GlStateManager.popMatrix();
        }
    }
    
    static {
        BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");
    }
}

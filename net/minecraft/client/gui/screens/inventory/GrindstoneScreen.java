package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindstoneScreen extends AbstractContainerScreen<GrindstoneMenu> {
    private static final ResourceLocation GRINDSTONE_LOCATION;
    
    public GrindstoneScreen(final GrindstoneMenu aze, final Inventory awf, final Component jo) {
        super(aze, awf, jo);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.renderBg(float3, integer1, integer2);
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(GrindstoneScreen.GRINDSTONE_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        if ((((GrindstoneMenu)this.menu).getSlot(0).hasItem() || ((GrindstoneMenu)this.menu).getSlot(1).hasItem()) && !((GrindstoneMenu)this.menu).getSlot(2).hasItem()) {
            this.blit(integer4 + 92, integer5 + 31, this.imageWidth, 0, 28, 21);
        }
    }
    
    static {
        GRINDSTONE_LOCATION = new ResourceLocation("textures/gui/container/grindstone.png");
    }
}

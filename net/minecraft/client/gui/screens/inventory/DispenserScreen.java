package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.DispenserMenu;

public class DispenserScreen extends AbstractContainerScreen<DispenserMenu> {
    private static final ResourceLocation CONTAINER_LOCATION;
    
    public DispenserScreen(final DispenserMenu ayz, final Inventory awf, final Component jo) {
        super(ayz, awf, jo);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        final String string4 = this.title.getColoredString();
        this.font.draw(string4, (float)(this.imageWidth / 2 - this.font.width(string4) / 2), 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(DispenserScreen.CONTAINER_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    static {
        CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");
    }
}

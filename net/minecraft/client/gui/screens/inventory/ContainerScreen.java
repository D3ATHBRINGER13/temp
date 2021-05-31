package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ChestMenu;

public class ContainerScreen extends AbstractContainerScreen<ChestMenu> implements MenuAccess<ChestMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND;
    private final int containerRows;
    
    public ContainerScreen(final ChestMenu ayr, final Inventory awf, final Component jo) {
        super(ayr, awf, jo);
        this.passEvents = false;
        final int integer5 = 222;
        final int integer6 = 114;
        this.containerRows = ayr.getRowCount();
        this.imageHeight = 114 + this.containerRows * 18;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(ContainerScreen.CONTAINER_BACKGROUND);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        this.blit(integer4, integer5 + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
    
    static {
        CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    }
}

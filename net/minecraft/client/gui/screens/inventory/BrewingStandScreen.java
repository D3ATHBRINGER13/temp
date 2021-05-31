package net.minecraft.client.gui.screens.inventory;

import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;

public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION;
    private static final int[] BUBBLELENGTHS;
    
    public BrewingStandScreen(final BrewingStandMenu ayp, final Inventory awf, final Component jo) {
        super(ayp, awf, jo);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), (float)(this.imageWidth / 2 - this.font.width(this.title.getColoredString()) / 2), 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BrewingStandScreen.BREWING_STAND_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        final int integer6 = ((BrewingStandMenu)this.menu).getFuel();
        final int integer7 = Mth.clamp((18 * integer6 + 20 - 1) / 20, 0, 18);
        if (integer7 > 0) {
            this.blit(integer4 + 60, integer5 + 44, 176, 29, integer7, 4);
        }
        final int integer8 = ((BrewingStandMenu)this.menu).getBrewingTicks();
        if (integer8 > 0) {
            int integer9 = (int)(28.0f * (1.0f - integer8 / 400.0f));
            if (integer9 > 0) {
                this.blit(integer4 + 97, integer5 + 16, 176, 0, 9, integer9);
            }
            integer9 = BrewingStandScreen.BUBBLELENGTHS[integer8 / 2 % 7];
            if (integer9 > 0) {
                this.blit(integer4 + 63, integer5 + 14 + 29 - integer9, 185, 29 - integer9, 12, integer9);
            }
        }
    }
    
    static {
        BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
        BUBBLELENGTHS = new int[] { 29, 24, 20, 16, 11, 6, 0 };
    }
}

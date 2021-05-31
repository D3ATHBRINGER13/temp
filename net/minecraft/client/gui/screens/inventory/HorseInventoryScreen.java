package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen extends AbstractContainerScreen<HorseInventoryMenu> {
    private static final ResourceLocation HORSE_INVENTORY_LOCATION;
    private final AbstractHorse horse;
    private float xMouse;
    private float yMouse;
    
    public HorseInventoryScreen(final HorseInventoryMenu azg, final Inventory awf, final AbstractHorse asb) {
        super(azg, awf, asb.getDisplayName());
        this.horse = asb;
        this.passEvents = false;
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(HorseInventoryScreen.HORSE_INVENTORY_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        if (this.horse instanceof AbstractChestedHorse) {
            final AbstractChestedHorse asa7 = (AbstractChestedHorse)this.horse;
            if (asa7.hasChest()) {
                this.blit(integer4 + 79, integer5 + 17, 0, this.imageHeight, asa7.getInventoryColumns() * 18, 54);
            }
        }
        if (this.horse.canBeSaddled()) {
            this.blit(integer4 + 7, integer5 + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        }
        if (this.horse.wearsArmor()) {
            if (this.horse instanceof Llama) {
                this.blit(integer4 + 7, integer5 + 35, 36, this.imageHeight + 54, 18, 18);
            }
            else {
                this.blit(integer4 + 7, integer5 + 35, 0, this.imageHeight + 54, 18, 18);
            }
        }
        InventoryScreen.renderPlayerModel(integer4 + 51, integer5 + 60, 17, integer4 + 51 - this.xMouse, integer5 + 75 - 50 - this.yMouse, this.horse);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.xMouse = (float)integer1;
        this.yMouse = (float)integer2;
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    static {
        HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
    }
}

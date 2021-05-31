package net.minecraft.client.gui.screens.inventory;

import net.minecraft.util.Mth;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.StonecutterMenu;

public class StonecutterScreen extends AbstractContainerScreen<StonecutterMenu> {
    private static final ResourceLocation BG_LOCATION;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;
    
    public StonecutterScreen(final StonecutterMenu baa, final Inventory awf, final Component jo) {
        super(baa, awf, jo);
        baa.registerUpdateListener(this::containerChanged);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 4.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 94), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(StonecutterScreen.BG_LOCATION);
        final int integer4 = this.leftPos;
        final int integer5 = this.topPos;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        final int integer6 = (int)(41.0f * this.scrollOffs);
        this.blit(integer4 + 119, integer5 + 15 + integer6, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
        final int integer7 = this.leftPos + 52;
        final int integer8 = this.topPos + 14;
        final int integer9 = this.startIndex + 12;
        this.renderButtons(integer2, integer3, integer7, integer8, integer9);
        this.renderRecipes(integer7, integer8, integer9);
    }
    
    private void renderButtons(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = this.startIndex; integer6 < integer5 && integer6 < ((StonecutterMenu)this.menu).getNumRecipes(); ++integer6) {
            final int integer7 = integer6 - this.startIndex;
            final int integer8 = integer3 + integer7 % 4 * 16;
            final int integer9 = integer7 / 4;
            final int integer10 = integer4 + integer9 * 18 + 2;
            int integer11 = this.imageHeight;
            if (integer6 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
                integer11 += 18;
            }
            else if (integer1 >= integer8 && integer2 >= integer10 && integer1 < integer8 + 16 && integer2 < integer10 + 18) {
                integer11 += 36;
            }
            this.blit(integer8, integer10 - 1, 0, integer11, 16, 18);
        }
    }
    
    private void renderRecipes(final int integer1, final int integer2, final int integer3) {
        Lighting.turnOnGui();
        final List<StonecutterRecipe> list5 = ((StonecutterMenu)this.menu).getRecipes();
        for (int integer4 = this.startIndex; integer4 < integer3 && integer4 < ((StonecutterMenu)this.menu).getNumRecipes(); ++integer4) {
            final int integer5 = integer4 - this.startIndex;
            final int integer6 = integer1 + integer5 % 4 * 16;
            final int integer7 = integer5 / 4;
            final int integer8 = integer2 + integer7 * 18 + 2;
            this.minecraft.getItemRenderer().renderAndDecorateItem(((StonecutterRecipe)list5.get(integer4)).getResultItem(), integer6, integer8);
        }
        Lighting.turnOff();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int integer2 = this.leftPos + 52;
            int integer3 = this.topPos + 14;
            for (int integer4 = this.startIndex + 12, integer5 = this.startIndex; integer5 < integer4; ++integer5) {
                final int integer6 = integer5 - this.startIndex;
                final double double3 = double1 - (integer2 + integer6 % 4 * 16);
                final double double4 = double2 - (integer3 + integer6 / 4 * 18);
                if (double3 >= 0.0 && double4 >= 0.0 && double3 < 16.0 && double4 < 18.0 && ((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, integer5)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                    this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, integer5);
                    return true;
                }
            }
            integer2 = this.leftPos + 119;
            integer3 = this.topPos + 9;
            if (double1 >= integer2 && double1 < integer2 + 12 && double2 >= integer3 && double2 < integer3 + 54) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (this.scrolling && this.isScrollBarActive()) {
            final int integer2 = this.topPos + 14;
            final int integer3 = integer2 + 54;
            this.scrollOffs = ((float)double2 - integer2 - 7.5f) / (integer3 - integer2 - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)(this.scrollOffs * this.getOffscreenRows() + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        if (this.isScrollBarActive()) {
            final int integer8 = this.getOffscreenRows();
            this.scrollOffs -= (float)(double3 / integer8);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)(this.scrollOffs * integer8 + 0.5) * 4;
        }
        return true;
    }
    
    private boolean isScrollBarActive() {
        return this.displayRecipes && ((StonecutterMenu)this.menu).getNumRecipes() > 12;
    }
    
    protected int getOffscreenRows() {
        return (((StonecutterMenu)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
    }
    
    private void containerChanged() {
        if (!(this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem())) {
            this.scrollOffs = 0.0f;
            this.startIndex = 0;
        }
    }
    
    static {
        BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
    }
}

package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingScreen extends AbstractContainerScreen<CraftingMenu> implements RecipeUpdateListener {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION;
    private static final ResourceLocation RECIPE_BUTTON_LOCATION;
    private final RecipeBookComponent recipeBookComponent;
    private boolean widthTooNarrow;
    
    public CraftingScreen(final CraftingMenu ayx, final Inventory awf, final Component jo) {
        super(ayx, awf, jo);
        this.recipeBookComponent = new RecipeBookComponent();
    }
    
    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = (this.width < 379);
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.children.add(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.<ImageButton>addButton(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, CraftingScreen.RECIPE_BUTTON_LOCATION, czi -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            czi.setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
    }
    
    @Override
    public void tick() {
        super.tick();
        this.recipeBookComponent.tick();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(float3, integer1, integer2);
            this.recipeBookComponent.render(integer1, integer2, float3);
        }
        else {
            this.recipeBookComponent.render(integer1, integer2, float3);
            super.render(integer1, integer2, float3);
            this.recipeBookComponent.renderGhostRecipe(this.leftPos, this.topPos, true, float3);
        }
        this.renderTooltip(integer1, integer2);
        this.recipeBookComponent.renderTooltip(this.leftPos, this.topPos, integer1, integer2);
        this.magicalSpecialHackyFocus(this.recipeBookComponent);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 28.0f, 6.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(CraftingScreen.CRAFTING_TABLE_LOCATION);
        final int integer4 = this.leftPos;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    @Override
    protected boolean isHovering(final int integer1, final int integer2, final int integer3, final int integer4, final double double5, final double double6) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(integer1, integer2, integer3, integer4, double5, double6);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.recipeBookComponent.mouseClicked(double1, double2, integer) || (this.widthTooNarrow && this.recipeBookComponent.isVisible()) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    protected boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5) {
        final boolean boolean9 = double1 < integer3 || double2 < integer4 || double1 >= integer3 + this.imageWidth || double2 >= integer4 + this.imageHeight;
        return this.recipeBookComponent.hasClickedOutside(double1, double2, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, integer5) && boolean9;
    }
    
    @Override
    protected void slotClicked(final Slot azx, final int integer2, final int integer3, final ClickType ays) {
        super.slotClicked(azx, integer2, integer3, ays);
        this.recipeBookComponent.slotClicked(azx);
    }
    
    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }
    
    @Override
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }
    
    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
    
    static {
        CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
        RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    }
}

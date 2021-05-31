package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION;
    public final AbstractFurnaceRecipeBookComponent recipeBookComponent;
    private boolean widthTooNarrow;
    private final ResourceLocation texture;
    
    public AbstractFurnaceScreen(final T ayl, final AbstractFurnaceRecipeBookComponent deu, final Inventory awf, final Component jo, final ResourceLocation qv) {
        super(ayl, awf, jo);
        this.recipeBookComponent = deu;
        this.texture = qv;
    }
    
    public void init() {
        super.init();
        this.widthTooNarrow = (this.width < 379);
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.<ImageButton>addButton(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, AbstractFurnaceScreen.RECIPE_BUTTON_LOCATION, czi -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            czi.setPosition(this.leftPos + 20, this.height / 2 - 49);
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
        this.minecraft.getTextureManager().bind(this.texture);
        final int integer4 = this.leftPos;
        final int integer5 = this.topPos;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            final int integer6 = this.menu.getLitProgress();
            this.blit(integer4 + 56, integer5 + 36 + 12 - integer6, 176, 12 - integer6, 14, integer6 + 1);
        }
        final int integer6 = this.menu.getBurnProgress();
        this.blit(integer4 + 79, integer5 + 34, 176, 14, integer6 + 1, 16);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.recipeBookComponent.mouseClicked(double1, double2, integer) || (this.widthTooNarrow && this.recipeBookComponent.isVisible()) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    protected void slotClicked(final Slot azx, final int integer2, final int integer3, final ClickType ays) {
        super.slotClicked(azx, integer2, integer3, ays);
        this.recipeBookComponent.slotClicked(azx);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return !this.recipeBookComponent.keyPressed(integer1, integer2, integer3) && super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    protected boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5) {
        final boolean boolean9 = double1 < integer3 || double2 < integer4 || double1 >= integer3 + this.imageWidth || double2 >= integer4 + this.imageHeight;
        return this.recipeBookComponent.hasClickedOutside(double1, double2, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, integer5) && boolean9;
    }
    
    public boolean charTyped(final char character, final int integer) {
        return this.recipeBookComponent.charTyped(character, integer) || super.charTyped(character, integer);
    }
    
    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }
    
    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
    
    @Override
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }
    
    static {
        RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    }
}

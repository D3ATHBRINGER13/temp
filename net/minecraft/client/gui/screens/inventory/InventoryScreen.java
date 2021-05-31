package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.InventoryMenu;

public class InventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION;
    private float xMouse;
    private float yMouse;
    private final RecipeBookComponent recipeBookComponent;
    private boolean recipeBookComponentInitialized;
    private boolean widthTooNarrow;
    private boolean buttonClicked;
    
    public InventoryScreen(final Player awg) {
        super(awg.inventoryMenu, awg.inventory, new TranslatableComponent("container.crafting", new Object[0]));
        this.recipeBookComponent = new RecipeBookComponent();
        this.passEvents = true;
    }
    
    @Override
    public void tick() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
            return;
        }
        this.recipeBookComponent.tick();
    }
    
    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
            return;
        }
        super.init();
        this.widthTooNarrow = (this.width < 379);
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.recipeBookComponentInitialized = true;
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.children.add(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.<ImageButton>addButton(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, InventoryScreen.RECIPE_BUTTON_LOCATION, czi -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            czi.setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
        }));
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 97.0f, 8.0f, 4210752);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.doRenderEffects = !this.recipeBookComponent.isVisible();
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(float3, integer1, integer2);
            this.recipeBookComponent.render(integer1, integer2, float3);
        }
        else {
            this.recipeBookComponent.render(integer1, integer2, float3);
            super.render(integer1, integer2, float3);
            this.recipeBookComponent.renderGhostRecipe(this.leftPos, this.topPos, false, float3);
        }
        this.renderTooltip(integer1, integer2);
        this.recipeBookComponent.renderTooltip(this.leftPos, this.topPos, integer1, integer2);
        this.xMouse = (float)integer1;
        this.yMouse = (float)integer2;
        this.magicalSpecialHackyFocus(this.recipeBookComponent);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(InventoryScreen.INVENTORY_LOCATION);
        final int integer4 = this.leftPos;
        final int integer5 = this.topPos;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        renderPlayerModel(integer4 + 51, integer5 + 75, 30, integer4 + 51 - this.xMouse, integer5 + 75 - 50 - this.yMouse, this.minecraft.player);
    }
    
    public static void renderPlayerModel(final int integer1, final int integer2, final int integer3, final float float4, final float float5, final LivingEntity aix) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)integer1, (float)integer2, 50.0f);
        GlStateManager.scalef((float)(-integer3), (float)integer3, (float)integer3);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        final float float6 = aix.yBodyRot;
        final float float7 = aix.yRot;
        final float float8 = aix.xRot;
        final float float9 = aix.yHeadRotO;
        final float float10 = aix.yHeadRot;
        GlStateManager.rotatef(135.0f, 0.0f, 1.0f, 0.0f);
        Lighting.turnOn();
        GlStateManager.rotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-(float)Math.atan((double)(float5 / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        aix.yBodyRot = (float)Math.atan((double)(float4 / 40.0f)) * 20.0f;
        aix.yRot = (float)Math.atan((double)(float4 / 40.0f)) * 40.0f;
        aix.xRot = -(float)Math.atan((double)(float5 / 40.0f)) * 20.0f;
        aix.yHeadRot = aix.yRot;
        aix.yHeadRotO = aix.yRot;
        GlStateManager.translatef(0.0f, 0.0f, 0.0f);
        final EntityRenderDispatcher dsa12 = Minecraft.getInstance().getEntityRenderDispatcher();
        dsa12.setPlayerRotY(180.0f);
        dsa12.setRenderShadow(false);
        dsa12.render(aix, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        dsa12.setRenderShadow(true);
        aix.yBodyRot = float6;
        aix.yRot = float7;
        aix.xRot = float8;
        aix.yHeadRotO = float9;
        aix.yHeadRot = float10;
        GlStateManager.popMatrix();
        Lighting.turnOff();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    @Override
    protected boolean isHovering(final int integer1, final int integer2, final int integer3, final int integer4, final double double5, final double double6) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(integer1, integer2, integer3, integer4, double5, double6);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.recipeBookComponent.mouseClicked(double1, double2, integer) || ((!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.mouseClicked(double1, double2, integer));
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        }
        return super.mouseReleased(double1, double2, integer);
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
        if (this.recipeBookComponentInitialized) {
            this.recipeBookComponent.removed();
        }
        super.removed();
    }
    
    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
    
    static {
        RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    }
}

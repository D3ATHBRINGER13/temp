package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import java.util.Iterator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    public static final ResourceLocation INVENTORY_LOCATION;
    protected int imageWidth;
    protected int imageHeight;
    protected final T menu;
    protected final Inventory inventory;
    protected int leftPos;
    protected int topPos;
    protected Slot hoveredSlot;
    private Slot clickedSlot;
    private boolean isSplittingStack;
    private ItemStack draggingItem;
    private int snapbackStartX;
    private int snapbackStartY;
    private Slot snapbackEnd;
    private long snapbackTime;
    private ItemStack snapbackItem;
    private Slot quickdropSlot;
    private long quickdropTime;
    protected final Set<Slot> quickCraftSlots;
    protected boolean isQuickCrafting;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private Slot lastClickSlot;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved;
    
    public AbstractContainerScreen(final T ayk, final Inventory awf, final Component jo) {
        super(jo);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.draggingItem = ItemStack.EMPTY;
        this.snapbackItem = ItemStack.EMPTY;
        this.quickCraftSlots = (Set<Slot>)Sets.newHashSet();
        this.lastQuickMoved = ItemStack.EMPTY;
        this.menu = ayk;
        this.inventory = awf;
        this.skipNextRelease = true;
    }
    
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        final int integer3 = this.leftPos;
        final int integer4 = this.topPos;
        this.renderBg(float3, integer1, integer2);
        GlStateManager.disableRescaleNormal();
        Lighting.turnOff();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        super.render(integer1, integer2, float3);
        Lighting.turnOnGui();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)integer3, (float)integer4, 0.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableRescaleNormal();
        this.hoveredSlot = null;
        final int integer5 = 240;
        final int integer6 = 240;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int integer7 = 0; integer7 < this.menu.slots.size(); ++integer7) {
            final Slot azx10 = (Slot)this.menu.slots.get(integer7);
            if (azx10.isActive()) {
                this.renderSlot(azx10);
            }
            if (this.isHovering(azx10, integer1, integer2) && azx10.isActive()) {
                this.hoveredSlot = azx10;
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                final int integer8 = azx10.x;
                final int integer9 = azx10.y;
                GlStateManager.colorMask(true, true, true, false);
                this.fillGradient(integer8, integer9, integer8 + 16, integer9 + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
        Lighting.turnOff();
        this.renderLabels(integer1, integer2);
        Lighting.turnOnGui();
        final Inventory awf9 = this.minecraft.player.inventory;
        ItemStack bcj10 = this.draggingItem.isEmpty() ? awf9.getCarried() : this.draggingItem;
        if (!bcj10.isEmpty()) {
            final int integer8 = 8;
            final int integer9 = this.draggingItem.isEmpty() ? 8 : 16;
            String string13 = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                bcj10 = bcj10.copy();
                bcj10.setCount(Mth.ceil(bcj10.getCount() / 2.0f));
            }
            else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                bcj10 = bcj10.copy();
                bcj10.setCount(this.quickCraftingRemainder);
                if (bcj10.isEmpty()) {
                    string13 = new StringBuilder().append("").append(ChatFormatting.YELLOW).append("0").toString();
                }
            }
            this.renderFloatingItem(bcj10, integer1 - integer3 - 8, integer2 - integer4 - integer9, string13);
        }
        if (!this.snapbackItem.isEmpty()) {
            float float4 = (Util.getMillis() - this.snapbackTime) / 100.0f;
            if (float4 >= 1.0f) {
                float4 = 1.0f;
                this.snapbackItem = ItemStack.EMPTY;
            }
            final int integer9 = this.snapbackEnd.x - this.snapbackStartX;
            final int integer10 = this.snapbackEnd.y - this.snapbackStartY;
            final int integer11 = this.snapbackStartX + (int)(integer9 * float4);
            final int integer12 = this.snapbackStartY + (int)(integer10 * float4);
            this.renderFloatingItem(this.snapbackItem, integer11, integer12, null);
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        Lighting.turnOn();
    }
    
    protected void renderTooltip(final int integer1, final int integer2) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            this.renderTooltip(this.hoveredSlot.getItem(), integer1, integer2);
        }
    }
    
    private void renderFloatingItem(final ItemStack bcj, final int integer2, final int integer3, final String string) {
        GlStateManager.translatef(0.0f, 0.0f, 32.0f);
        this.blitOffset = 200;
        this.itemRenderer.blitOffset = 200.0f;
        this.itemRenderer.renderAndDecorateItem(bcj, integer2, integer3);
        this.itemRenderer.renderGuiItemDecorations(this.font, bcj, integer2, integer3 - (this.draggingItem.isEmpty() ? 0 : 8), string);
        this.blitOffset = 0;
        this.itemRenderer.blitOffset = 0.0f;
    }
    
    protected void renderLabels(final int integer1, final int integer2) {
    }
    
    protected abstract void renderBg(final float float1, final int integer2, final int integer3);
    
    private void renderSlot(final Slot azx) {
        final int integer3 = azx.x;
        final int integer4 = azx.y;
        ItemStack bcj5 = azx.getItem();
        boolean boolean6 = false;
        boolean boolean7 = azx == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        final ItemStack bcj6 = this.minecraft.player.inventory.getCarried();
        String string9 = null;
        if (azx == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !bcj5.isEmpty()) {
            bcj5 = bcj5.copy();
            bcj5.setCount(bcj5.getCount() / 2);
        }
        else if (this.isQuickCrafting && this.quickCraftSlots.contains(azx) && !bcj6.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }
            if (AbstractContainerMenu.canItemQuickReplace(azx, bcj6, true) && this.menu.canDragTo(azx)) {
                bcj5 = bcj6.copy();
                boolean6 = true;
                AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, bcj5, azx.getItem().isEmpty() ? 0 : azx.getItem().getCount());
                final int integer5 = Math.min(bcj5.getMaxStackSize(), azx.getMaxStackSize(bcj5));
                if (bcj5.getCount() > integer5) {
                    string9 = ChatFormatting.YELLOW.toString() + integer5;
                    bcj5.setCount(integer5);
                }
            }
            else {
                this.quickCraftSlots.remove(azx);
                this.recalculateQuickCraftRemaining();
            }
        }
        this.blitOffset = 100;
        this.itemRenderer.blitOffset = 100.0f;
        if (bcj5.isEmpty() && azx.isActive()) {
            final String string10 = azx.getNoItemIcon();
            if (string10 != null) {
                final TextureAtlasSprite dxb11 = this.minecraft.getTextureAtlas().getTexture(string10);
                GlStateManager.disableLighting();
                this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
                GuiComponent.blit(integer3, integer4, this.blitOffset, 16, 16, dxb11);
                GlStateManager.enableLighting();
                boolean7 = true;
            }
        }
        if (!boolean7) {
            if (boolean6) {
                GuiComponent.fill(integer3, integer4, integer3 + 16, integer4 + 16, -2130706433);
            }
            GlStateManager.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, bcj5, integer3, integer4);
            this.itemRenderer.renderGuiItemDecorations(this.font, bcj5, integer3, integer4, string9);
        }
        this.itemRenderer.blitOffset = 0.0f;
        this.blitOffset = 0;
    }
    
    private void recalculateQuickCraftRemaining() {
        final ItemStack bcj2 = this.minecraft.player.inventory.getCarried();
        if (bcj2.isEmpty() || !this.isQuickCrafting) {
            return;
        }
        if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = bcj2.getMaxStackSize();
            return;
        }
        this.quickCraftingRemainder = bcj2.getCount();
        for (final Slot azx4 : this.quickCraftSlots) {
            final ItemStack bcj3 = bcj2.copy();
            final ItemStack bcj4 = azx4.getItem();
            final int integer7 = bcj4.isEmpty() ? 0 : bcj4.getCount();
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, bcj3, integer7);
            final int integer8 = Math.min(bcj3.getMaxStackSize(), azx4.getMaxStackSize(bcj3));
            if (bcj3.getCount() > integer8) {
                bcj3.setCount(integer8);
            }
            this.quickCraftingRemainder -= bcj3.getCount() - integer7;
        }
    }
    
    private Slot findSlot(final double double1, final double double2) {
        for (int integer6 = 0; integer6 < this.menu.slots.size(); ++integer6) {
            final Slot azx7 = (Slot)this.menu.slots.get(integer6);
            if (this.isHovering(azx7, double1, double2) && azx7.isActive()) {
                return azx7;
            }
        }
        return null;
    }
    
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (super.mouseClicked(double1, double2, integer)) {
            return true;
        }
        final boolean boolean7 = this.minecraft.options.keyPickItem.matchesMouse(integer);
        final Slot azx8 = this.findSlot(double1, double2);
        final long long9 = Util.getMillis();
        this.doubleclick = (this.lastClickSlot == azx8 && long9 - this.lastClickTime < 250L && this.lastClickButton == integer);
        this.skipNextRelease = false;
        if (integer == 0 || integer == 1 || boolean7) {
            final int integer2 = this.leftPos;
            final int integer3 = this.topPos;
            final boolean boolean8 = this.hasClickedOutside(double1, double2, integer2, integer3, integer);
            int integer4 = -1;
            if (azx8 != null) {
                integer4 = azx8.index;
            }
            if (boolean8) {
                integer4 = -999;
            }
            if (this.minecraft.options.touchscreen && boolean8 && this.minecraft.player.inventory.getCarried().isEmpty()) {
                this.minecraft.setScreen(null);
                return true;
            }
            if (integer4 != -1) {
                if (this.minecraft.options.touchscreen) {
                    if (azx8 != null && azx8.hasItem()) {
                        this.clickedSlot = azx8;
                        this.draggingItem = ItemStack.EMPTY;
                        this.isSplittingStack = (integer == 1);
                    }
                    else {
                        this.clickedSlot = null;
                    }
                }
                else if (!this.isQuickCrafting) {
                    if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                        if (this.minecraft.options.keyPickItem.matchesMouse(integer)) {
                            this.slotClicked(azx8, integer4, integer, ClickType.CLONE);
                        }
                        else {
                            final boolean boolean9 = integer4 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
                            ClickType ays16 = ClickType.PICKUP;
                            if (boolean9) {
                                this.lastQuickMoved = ((azx8 != null && azx8.hasItem()) ? azx8.getItem().copy() : ItemStack.EMPTY);
                                ays16 = ClickType.QUICK_MOVE;
                            }
                            else if (integer4 == -999) {
                                ays16 = ClickType.THROW;
                            }
                            this.slotClicked(azx8, integer4, integer, ays16);
                        }
                        this.skipNextRelease = true;
                    }
                    else {
                        this.isQuickCrafting = true;
                        this.quickCraftingButton = integer;
                        this.quickCraftSlots.clear();
                        if (integer == 0) {
                            this.quickCraftingType = 0;
                        }
                        else if (integer == 1) {
                            this.quickCraftingType = 1;
                        }
                        else if (this.minecraft.options.keyPickItem.matchesMouse(integer)) {
                            this.quickCraftingType = 2;
                        }
                    }
                }
            }
        }
        this.lastClickSlot = azx8;
        this.lastClickTime = long9;
        this.lastClickButton = integer;
        return true;
    }
    
    protected boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5) {
        return double1 < integer3 || double2 < integer4 || double1 >= integer3 + this.imageWidth || double2 >= integer4 + this.imageHeight;
    }
    
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        final Slot azx11 = this.findSlot(double1, double2);
        final ItemStack bcj12 = this.minecraft.player.inventory.getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
            if (integer == 0 || integer == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (azx11 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                }
                else if (this.draggingItem.getCount() > 1 && azx11 != null && AbstractContainerMenu.canItemQuickReplace(azx11, this.draggingItem, false)) {
                    final long long13 = Util.getMillis();
                    if (this.quickdropSlot == azx11) {
                        if (long13 - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked(azx11, azx11.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = long13 + 750L;
                            this.draggingItem.shrink(1);
                        }
                    }
                    else {
                        this.quickdropSlot = azx11;
                        this.quickdropTime = long13;
                    }
                }
            }
        }
        else if (this.isQuickCrafting && azx11 != null && !bcj12.isEmpty() && (bcj12.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(azx11, bcj12, true) && azx11.mayPlace(bcj12) && this.menu.canDragTo(azx11)) {
            this.quickCraftSlots.add(azx11);
            this.recalculateQuickCraftRemaining();
        }
        return true;
    }
    
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        final Slot azx7 = this.findSlot(double1, double2);
        final int integer2 = this.leftPos;
        final int integer3 = this.topPos;
        final boolean boolean10 = this.hasClickedOutside(double1, double2, integer2, integer3, integer);
        int integer4 = -1;
        if (azx7 != null) {
            integer4 = azx7.index;
        }
        if (boolean10) {
            integer4 = -999;
        }
        if (this.doubleclick && azx7 != null && integer == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, azx7)) {
            if (hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (final Slot azx8 : this.menu.slots) {
                        if (azx8 != null && azx8.mayPickup(this.minecraft.player) && azx8.hasItem() && azx8.container == azx7.container && AbstractContainerMenu.canItemQuickReplace(azx8, this.lastQuickMoved, true)) {
                            this.slotClicked(azx8, azx8.index, integer, ClickType.QUICK_MOVE);
                        }
                    }
                }
            }
            else {
                this.slotClicked(azx7, integer4, integer, ClickType.PICKUP_ALL);
            }
            this.doubleclick = false;
            this.lastClickTime = 0L;
        }
        else {
            if (this.isQuickCrafting && this.quickCraftingButton != integer) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                return this.skipNextRelease = true;
            }
            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }
            if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
                if (integer == 0 || integer == 1) {
                    if (this.draggingItem.isEmpty() && azx7 != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }
                    final boolean boolean11 = AbstractContainerMenu.canItemQuickReplace(azx7, this.draggingItem, false);
                    if (integer4 != -1 && !this.draggingItem.isEmpty() && boolean11) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, integer, ClickType.PICKUP);
                        this.slotClicked(azx7, integer4, 0, ClickType.PICKUP);
                        if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        }
                        else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, integer, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor(double1 - integer2);
                            this.snapbackStartY = Mth.floor(double2 - integer3);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            this.snapbackTime = Util.getMillis();
                        }
                    }
                    else if (!this.draggingItem.isEmpty()) {
                        this.snapbackStartX = Mth.floor(double1 - integer2);
                        this.snapbackStartY = Mth.floor(double2 - integer3);
                        this.snapbackEnd = this.clickedSlot;
                        this.snapbackItem = this.draggingItem;
                        this.snapbackTime = Util.getMillis();
                    }
                    this.draggingItem = ItemStack.EMPTY;
                    this.clickedSlot = null;
                }
            }
            else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
                for (final Slot azx8 : this.quickCraftSlots) {
                    this.slotClicked(azx8, azx8.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }
            else if (!this.minecraft.player.inventory.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(integer)) {
                    this.slotClicked(azx7, integer4, integer, ClickType.CLONE);
                }
                else {
                    final boolean boolean11 = integer4 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
                    if (boolean11) {
                        this.lastQuickMoved = ((azx7 != null && azx7.hasItem()) ? azx7.getItem().copy() : ItemStack.EMPTY);
                    }
                    this.slotClicked(azx7, integer4, integer, boolean11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }
        if (this.minecraft.player.inventory.getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }
        this.isQuickCrafting = false;
        return true;
    }
    
    private boolean isHovering(final Slot azx, final double double2, final double double3) {
        return this.isHovering(azx.x, azx.y, 16, 16, double2, double3);
    }
    
    protected boolean isHovering(final int integer1, final int integer2, final int integer3, final int integer4, double double5, double double6) {
        final int integer5 = this.leftPos;
        final int integer6 = this.topPos;
        double5 -= integer5;
        double6 -= integer6;
        return double5 >= integer1 - 1 && double5 < integer1 + integer3 + 1 && double6 >= integer2 - 1 && double6 < integer2 + integer4 + 1;
    }
    
    protected void slotClicked(final Slot azx, int integer2, final int integer3, final ClickType ays) {
        if (azx != null) {
            integer2 = azx.index;
        }
        this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, integer2, integer3, ays, this.minecraft.player);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 256 || this.minecraft.options.keyInventory.matches(integer1, integer2)) {
            this.minecraft.player.closeContainer();
        }
        this.checkNumkeyPressed(integer1, integer2);
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches(integer1, integer2)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            }
            else if (this.minecraft.options.keyDrop.matches(integer1, integer2)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, Screen.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
        }
        return true;
    }
    
    protected boolean checkNumkeyPressed(final int integer1, final int integer2) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                if (this.minecraft.options.keyHotbarSlots[integer3].matches(integer1, integer2)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, integer3, ClickType.SWAP);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        this.menu.removed(this.minecraft.player);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeContainer();
        }
    }
    
    @Override
    public T getMenu() {
        return this.menu;
    }
    
    static {
        INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
    }
}

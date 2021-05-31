package net.minecraft.client.gui.screens.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.LoomMenu;

public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
    private static final ResourceLocation BG_LOCATION;
    private static final int TOTAL_PATTERN_ROWS;
    private static final DyeColor PATTERN_BASE_COLOR;
    private static final DyeColor PATTERN_OVERLAY_COLOR;
    private static final List<DyeColor> PATTERN_COLORS;
    private ResourceLocation resultBannerTexture;
    private ItemStack bannerStack;
    private ItemStack dyeStack;
    private ItemStack patternStack;
    private final ResourceLocation[] patternTextures;
    private boolean displayPatterns;
    private boolean displaySpecialPattern;
    private boolean hasMaxPatterns;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private int loadNextTextureIndex;
    
    public LoomScreen(final LoomMenu azj, final Inventory awf, final Component jo) {
        super(azj, awf, jo);
        this.bannerStack = ItemStack.EMPTY;
        this.dyeStack = ItemStack.EMPTY;
        this.patternStack = ItemStack.EMPTY;
        this.patternTextures = new ResourceLocation[BannerPattern.COUNT];
        this.startIndex = 1;
        this.loadNextTextureIndex = 1;
        azj.registerUpdateListener(this::containerChanged);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.loadNextTextureIndex < BannerPattern.COUNT) {
            final BannerPattern btp2 = BannerPattern.values()[this.loadNextTextureIndex];
            final String string3 = new StringBuilder().append("b").append(LoomScreen.PATTERN_BASE_COLOR.getId()).toString();
            final String string4 = btp2.getHashname() + LoomScreen.PATTERN_OVERLAY_COLOR.getId();
            this.patternTextures[this.loadNextTextureIndex] = BannerTextures.BANNER_CACHE.getTextureLocation(string3 + string4, (List<BannerPattern>)Lists.newArrayList((Object[])new BannerPattern[] { BannerPattern.BASE, btp2 }), LoomScreen.PATTERN_COLORS);
            ++this.loadNextTextureIndex;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 8.0f, 4.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(LoomScreen.BG_LOCATION);
        final int integer4 = this.leftPos;
        final int integer5 = this.topPos;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        final Slot azx7 = ((LoomMenu)this.menu).getBannerSlot();
        final Slot azx8 = ((LoomMenu)this.menu).getDyeSlot();
        final Slot azx9 = ((LoomMenu)this.menu).getPatternSlot();
        final Slot azx10 = ((LoomMenu)this.menu).getResultSlot();
        if (!azx7.hasItem()) {
            this.blit(integer4 + azx7.x, integer5 + azx7.y, this.imageWidth, 0, 16, 16);
        }
        if (!azx8.hasItem()) {
            this.blit(integer4 + azx8.x, integer5 + azx8.y, this.imageWidth + 16, 0, 16, 16);
        }
        if (!azx9.hasItem()) {
            this.blit(integer4 + azx9.x, integer5 + azx9.y, this.imageWidth + 32, 0, 16, 16);
        }
        final int integer6 = (int)(41.0f * this.scrollOffs);
        this.blit(integer4 + 119, integer5 + 13 + integer6, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
        if (this.resultBannerTexture != null && !this.hasMaxPatterns) {
            this.minecraft.getTextureManager().bind(this.resultBannerTexture);
            GuiComponent.blit(integer4 + 141, integer5 + 8, 20, 40, 1.0f, 1.0f, 20, 40, 64, 64);
        }
        else if (this.hasMaxPatterns) {
            this.blit(integer4 + azx10.x - 2, integer5 + azx10.y - 2, this.imageWidth, 17, 17, 16);
        }
        if (this.displayPatterns) {
            final int integer7 = integer4 + 60;
            final int integer8 = integer5 + 13;
            for (int integer9 = this.startIndex + 16, integer10 = this.startIndex; integer10 < integer9 && integer10 < this.patternTextures.length - 5; ++integer10) {
                final int integer11 = integer10 - this.startIndex;
                final int integer12 = integer7 + integer11 % 4 * 14;
                final int integer13 = integer8 + integer11 / 4 * 14;
                this.minecraft.getTextureManager().bind(LoomScreen.BG_LOCATION);
                int integer14 = this.imageHeight;
                if (integer10 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                    integer14 += 14;
                }
                else if (integer2 >= integer12 && integer3 >= integer13 && integer2 < integer12 + 14 && integer3 < integer13 + 14) {
                    integer14 += 28;
                }
                this.blit(integer12, integer13, 0, integer14, 14, 14);
                if (this.patternTextures[integer10] != null) {
                    this.minecraft.getTextureManager().bind(this.patternTextures[integer10]);
                    GuiComponent.blit(integer12 + 4, integer13 + 2, 5, 10, 1.0f, 1.0f, 20, 40, 64, 64);
                }
            }
        }
        else if (this.displaySpecialPattern) {
            final int integer7 = integer4 + 60;
            final int integer8 = integer5 + 13;
            this.minecraft.getTextureManager().bind(LoomScreen.BG_LOCATION);
            this.blit(integer7, integer8, 0, this.imageHeight, 14, 14);
            final int integer9 = ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
            if (this.patternTextures[integer9] != null) {
                this.minecraft.getTextureManager().bind(this.patternTextures[integer9]);
                GuiComponent.blit(integer7 + 4, integer8 + 2, 5, 10, 1.0f, 1.0f, 20, 40, 64, 64);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int integer2 = this.leftPos + 60;
            int integer3 = this.topPos + 13;
            for (int integer4 = this.startIndex + 16, integer5 = this.startIndex; integer5 < integer4; ++integer5) {
                final int integer6 = integer5 - this.startIndex;
                final double double3 = double1 - (integer2 + integer6 % 4 * 14);
                final double double4 = double2 - (integer3 + integer6 / 4 * 14);
                if (double3 >= 0.0 && double4 >= 0.0 && double3 < 14.0 && double4 < 14.0 && ((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, integer5)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                    this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, integer5);
                    return true;
                }
            }
            integer2 = this.leftPos + 119;
            integer3 = this.topPos + 9;
            if (double1 >= integer2 && double1 < integer2 + 12 && double2 >= integer3 && double2 < integer3 + 56) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (this.scrolling && this.displayPatterns) {
            final int integer2 = this.topPos + 13;
            final int integer3 = integer2 + 56;
            this.scrollOffs = ((float)double2 - integer2 - 7.5f) / (integer3 - integer2 - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            final int integer4 = LoomScreen.TOTAL_PATTERN_ROWS - 4;
            int integer5 = (int)(this.scrollOffs * integer4 + 0.5);
            if (integer5 < 0) {
                integer5 = 0;
            }
            this.startIndex = 1 + integer5 * 4;
            return true;
        }
        return super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        if (this.displayPatterns) {
            final int integer8 = LoomScreen.TOTAL_PATTERN_ROWS - 4;
            this.scrollOffs -= (float)(double3 / integer8);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = 1 + (int)(this.scrollOffs * integer8 + 0.5) * 4;
        }
        return true;
    }
    
    @Override
    protected boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5) {
        return double1 < integer3 || double2 < integer4 || double1 >= integer3 + this.imageWidth || double2 >= integer4 + this.imageHeight;
    }
    
    private void containerChanged() {
        final ItemStack bcj2 = ((LoomMenu)this.menu).getResultSlot().getItem();
        if (bcj2.isEmpty()) {
            this.resultBannerTexture = null;
        }
        else {
            final BannerBlockEntity bto3 = new BannerBlockEntity();
            bto3.fromItem(bcj2, ((BannerItem)bcj2.getItem()).getColor());
            this.resultBannerTexture = BannerTextures.BANNER_CACHE.getTextureLocation(bto3.getTextureHashName(), bto3.getPatterns(), bto3.getColors());
        }
        final ItemStack bcj3 = ((LoomMenu)this.menu).getBannerSlot().getItem();
        final ItemStack bcj4 = ((LoomMenu)this.menu).getDyeSlot().getItem();
        final ItemStack bcj5 = ((LoomMenu)this.menu).getPatternSlot().getItem();
        final CompoundTag id6 = bcj3.getOrCreateTagElement("BlockEntityTag");
        this.hasMaxPatterns = (id6.contains("Patterns", 9) && !bcj3.isEmpty() && id6.getList("Patterns", 10).size() >= 6);
        if (this.hasMaxPatterns) {
            this.resultBannerTexture = null;
        }
        if (!ItemStack.matches(bcj3, this.bannerStack) || !ItemStack.matches(bcj4, this.dyeStack) || !ItemStack.matches(bcj5, this.patternStack)) {
            this.displayPatterns = (!bcj3.isEmpty() && !bcj4.isEmpty() && bcj5.isEmpty() && !this.hasMaxPatterns);
            this.displaySpecialPattern = (!this.hasMaxPatterns && !bcj5.isEmpty() && !bcj3.isEmpty() && !bcj4.isEmpty());
        }
        this.bannerStack = bcj3.copy();
        this.dyeStack = bcj4.copy();
        this.patternStack = bcj5.copy();
    }
    
    static {
        BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
        TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
        PATTERN_BASE_COLOR = DyeColor.GRAY;
        PATTERN_OVERLAY_COLOR = DyeColor.WHITE;
        PATTERN_COLORS = (List)Lists.newArrayList((Object[])new DyeColor[] { LoomScreen.PATTERN_BASE_COLOR, LoomScreen.PATTERN_OVERLAY_COLOR });
    }
}

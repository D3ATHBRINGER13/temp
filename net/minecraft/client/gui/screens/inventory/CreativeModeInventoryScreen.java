package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.NonNullList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import com.google.common.collect.Lists;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.util.Mth;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.HotbarManager;
import net.minecraft.world.Container;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.tags.TagCollection;
import java.util.function.Predicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.client.searchtree.SearchTree;
import java.util.Iterator;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.Registry;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.inventory.ClickType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import java.util.Map;
import net.minecraft.world.inventory.Slot;
import java.util.List;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.SimpleContainer;
import net.minecraft.resources.ResourceLocation;

public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen<ItemPickerMenu> {
    private static final ResourceLocation CREATIVE_TABS_LOCATION;
    private static final SimpleContainer CONTAINER;
    private static int selectedTab;
    private float scrollOffs;
    private boolean scrolling;
    private EditBox searchBox;
    private List<Slot> originalSlots;
    private Slot destroyItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTextInput;
    private boolean hasClickedOutside;
    private final Map<ResourceLocation, Tag<Item>> visibleTags;
    
    public CreativeModeInventoryScreen(final Player awg) {
        super(new ItemPickerMenu(awg), awg.inventory, new TextComponent(""));
        this.visibleTags = (Map<ResourceLocation, Tag<Item>>)Maps.newTreeMap();
        awg.containerMenu = this.menu;
        this.passEvents = true;
        this.imageHeight = 136;
        this.imageWidth = 195;
    }
    
    @Override
    public void tick() {
        if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
        else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }
    
    @Override
    protected void slotClicked(@Nullable final Slot azx, final int integer2, final int integer3, ClickType ays) {
        if (this.isCreativeSlot(azx)) {
            this.searchBox.moveCursorToEnd();
            this.searchBox.setHighlightPos(0);
        }
        final boolean boolean6 = ays == ClickType.QUICK_MOVE;
        ays = ((integer2 == -999 && ays == ClickType.PICKUP) ? ClickType.THROW : ays);
        if (azx != null || CreativeModeInventoryScreen.selectedTab == CreativeModeTab.TAB_INVENTORY.getId() || ays == ClickType.QUICK_CRAFT) {
            if (azx != null && !azx.mayPickup(this.minecraft.player)) {
                return;
            }
            if (azx == this.destroyItemSlot && boolean6) {
                for (int integer4 = 0; integer4 < this.minecraft.player.inventoryMenu.getItems().size(); ++integer4) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, integer4);
                }
            }
            else if (CreativeModeInventoryScreen.selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
                if (azx == this.destroyItemSlot) {
                    this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
                }
                else if (ays == ClickType.THROW && azx != null && azx.hasItem()) {
                    final ItemStack bcj7 = azx.remove((integer3 == 0) ? 1 : azx.getItem().getMaxStackSize());
                    final ItemStack bcj8 = azx.getItem();
                    this.minecraft.player.drop(bcj7, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(bcj7);
                    this.minecraft.gameMode.handleCreativeModeItemAdd(bcj8, ((SlotWrapper)azx).target.index);
                }
                else if (ays == ClickType.THROW && !this.minecraft.player.inventory.getCarried().isEmpty()) {
                    this.minecraft.player.drop(this.minecraft.player.inventory.getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(this.minecraft.player.inventory.getCarried());
                    this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
                }
                else {
                    this.minecraft.player.inventoryMenu.clicked((azx == null) ? integer2 : ((SlotWrapper)azx).target.index, integer3, ays, this.minecraft.player);
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            }
            else if (ays != ClickType.QUICK_CRAFT && azx.container == CreativeModeInventoryScreen.CONTAINER) {
                final Inventory awf7 = this.minecraft.player.inventory;
                ItemStack bcj8 = awf7.getCarried();
                final ItemStack bcj9 = azx.getItem();
                if (ays == ClickType.SWAP) {
                    if (!bcj9.isEmpty() && integer3 >= 0 && integer3 < 9) {
                        final ItemStack bcj10 = bcj9.copy();
                        bcj10.setCount(bcj10.getMaxStackSize());
                        this.minecraft.player.inventory.setItem(integer3, bcj10);
                        this.minecraft.player.inventoryMenu.broadcastChanges();
                    }
                    return;
                }
                if (ays == ClickType.CLONE) {
                    if (awf7.getCarried().isEmpty() && azx.hasItem()) {
                        final ItemStack bcj10 = azx.getItem().copy();
                        bcj10.setCount(bcj10.getMaxStackSize());
                        awf7.setCarried(bcj10);
                    }
                    return;
                }
                if (ays == ClickType.THROW) {
                    if (!bcj9.isEmpty()) {
                        final ItemStack bcj10 = bcj9.copy();
                        bcj10.setCount((integer3 == 0) ? 1 : bcj10.getMaxStackSize());
                        this.minecraft.player.drop(bcj10, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(bcj10);
                    }
                    return;
                }
                if (!bcj8.isEmpty() && !bcj9.isEmpty() && bcj8.sameItem(bcj9) && ItemStack.tagMatches(bcj8, bcj9)) {
                    if (integer3 == 0) {
                        if (boolean6) {
                            bcj8.setCount(bcj8.getMaxStackSize());
                        }
                        else if (bcj8.getCount() < bcj8.getMaxStackSize()) {
                            bcj8.grow(1);
                        }
                    }
                    else {
                        bcj8.shrink(1);
                    }
                }
                else if (bcj9.isEmpty() || !bcj8.isEmpty()) {
                    if (integer3 == 0) {
                        awf7.setCarried(ItemStack.EMPTY);
                    }
                    else {
                        awf7.getCarried().shrink(1);
                    }
                }
                else {
                    awf7.setCarried(bcj9.copy());
                    bcj8 = awf7.getCarried();
                    if (boolean6) {
                        bcj8.setCount(bcj8.getMaxStackSize());
                    }
                }
            }
            else if (this.menu != null) {
                final ItemStack bcj7 = (azx == null) ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot(azx.index).getItem();
                ((ItemPickerMenu)this.menu).clicked((azx == null) ? integer2 : azx.index, integer3, ays, this.minecraft.player);
                if (AbstractContainerMenu.getQuickcraftHeader(integer3) == 2) {
                    for (int integer5 = 0; integer5 < 9; ++integer5) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(((ItemPickerMenu)this.menu).getSlot(45 + integer5).getItem(), 36 + integer5);
                    }
                }
                else if (azx != null) {
                    final ItemStack bcj8 = ((ItemPickerMenu)this.menu).getSlot(azx.index).getItem();
                    this.minecraft.gameMode.handleCreativeModeItemAdd(bcj8, azx.index - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    final int integer6 = 45 + integer3;
                    if (ays == ClickType.SWAP) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(bcj7, integer6 - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    }
                    else if (ays == ClickType.THROW && !bcj7.isEmpty()) {
                        final ItemStack bcj10 = bcj7.copy();
                        bcj10.setCount((integer3 == 0) ? 1 : bcj10.getMaxStackSize());
                        this.minecraft.player.drop(bcj10, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(bcj10);
                    }
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            }
        }
        else {
            final Inventory awf7 = this.minecraft.player.inventory;
            if (!awf7.getCarried().isEmpty() && this.hasClickedOutside) {
                if (integer3 == 0) {
                    this.minecraft.player.drop(awf7.getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(awf7.getCarried());
                    awf7.setCarried(ItemStack.EMPTY);
                }
                if (integer3 == 1) {
                    final ItemStack bcj8 = awf7.getCarried().split(1);
                    this.minecraft.player.drop(bcj8, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(bcj8);
                }
            }
        }
    }
    
    private boolean isCreativeSlot(@Nullable final Slot azx) {
        return azx != null && azx.container == CreativeModeInventoryScreen.CONTAINER;
    }
    
    @Override
    protected void checkEffectRendering() {
        final int integer2 = this.leftPos;
        super.checkEffectRendering();
        if (this.searchBox != null && this.leftPos != integer2) {
            this.searchBox.setX(this.leftPos + 82);
        }
    }
    
    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            super.init();
            this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
            final Font font = this.font;
            final int integer3 = this.leftPos + 82;
            final int integer4 = this.topPos + 6;
            final int integer5 = 80;
            this.font.getClass();
            (this.searchBox = new EditBox(font, integer3, integer4, integer5, 9, I18n.get("itemGroup.search"))).setMaxLength(50);
            this.searchBox.setBordered(false);
            this.searchBox.setVisible(false);
            this.searchBox.setTextColor(16777215);
            this.children.add(this.searchBox);
            final int integer2 = CreativeModeInventoryScreen.selectedTab;
            CreativeModeInventoryScreen.selectedTab = -1;
            this.selectTab(CreativeModeTab.TABS[integer2]);
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
            this.listener = new CreativeInventoryListener(this.minecraft);
            this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
        }
        else {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.searchBox.getValue();
        this.init(cyc, integer2, integer3);
        this.searchBox.setValue(string5);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
    }
    
    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        }
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    public boolean charTyped(final char character, final int integer) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (CreativeModeInventoryScreen.selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
            return false;
        }
        final String string4 = this.searchBox.getValue();
        if (this.searchBox.charTyped(character, integer)) {
            if (!Objects.equals(string4, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        this.ignoreTextInput = false;
        if (CreativeModeInventoryScreen.selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
            if (this.minecraft.options.keyChat.matches(integer1, integer2)) {
                this.ignoreTextInput = true;
                this.selectTab(CreativeModeTab.TAB_SEARCH);
                return true;
            }
            return super.keyPressed(integer1, integer2, integer3);
        }
        else {
            final boolean boolean5 = !this.isCreativeSlot(this.hoveredSlot) || (this.hoveredSlot != null && this.hoveredSlot.hasItem());
            if (boolean5 && this.checkNumkeyPressed(integer1, integer2)) {
                return this.ignoreTextInput = true;
            }
            final String string6 = this.searchBox.getValue();
            if (this.searchBox.keyPressed(integer1, integer2, integer3)) {
                if (!Objects.equals(string6, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }
                return true;
            }
            return (this.searchBox.isFocused() && this.searchBox.isVisible() && integer1 != 256) || super.keyPressed(integer1, integer2, integer3);
        }
    }
    
    public boolean keyReleased(final int integer1, final int integer2, final int integer3) {
        this.ignoreTextInput = false;
        return super.keyReleased(integer1, integer2, integer3);
    }
    
    private void refreshSearchResults() {
        ((ItemPickerMenu)this.menu).items.clear();
        this.visibleTags.clear();
        String string2 = this.searchBox.getValue();
        if (string2.isEmpty()) {
            for (final Item bce4 : Registry.ITEM) {
                bce4.fillItemCategory(CreativeModeTab.TAB_SEARCH, ((ItemPickerMenu)this.menu).items);
            }
        }
        else {
            SearchTree<ItemStack> dzy3;
            if (string2.startsWith("#")) {
                string2 = string2.substring(1);
                dzy3 = this.minecraft.<ItemStack>getSearchTree(SearchRegistry.CREATIVE_TAGS);
                this.updateVisibleTags(string2);
            }
            else {
                dzy3 = this.minecraft.<ItemStack>getSearchTree(SearchRegistry.CREATIVE_NAMES);
            }
            ((ItemPickerMenu)this.menu).items.addAll((Collection)dzy3.search(string2.toLowerCase(Locale.ROOT)));
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }
    
    private void updateVisibleTags(final String string) {
        final int integer3 = string.indexOf(58);
        Predicate<ResourceLocation> predicate4;
        if (integer3 == -1) {
            predicate4 = (Predicate<ResourceLocation>)(qv -> qv.getPath().contains((CharSequence)string));
        }
        else {
            final String string2 = string.substring(0, integer3).trim();
            final String string3 = string.substring(integer3 + 1).trim();
            predicate4 = (Predicate<ResourceLocation>)(qv -> qv.getNamespace().contains((CharSequence)string2) && qv.getPath().contains((CharSequence)string3));
        }
        final TagCollection<Item> zh5 = ItemTags.getAllTags();
        zh5.getAvailableTags().stream().filter((Predicate)predicate4).forEach(qv -> {
            final Tag tag = (Tag)this.visibleTags.put(qv, zh5.getTag(qv));
        });
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        final CreativeModeTab bba4 = CreativeModeTab.TABS[CreativeModeInventoryScreen.selectedTab];
        if (bba4.showTitle()) {
            GlStateManager.disableBlend();
            this.font.draw(I18n.get(bba4.getName()), 8.0f, 6.0f, 4210752);
        }
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (integer == 0) {
            final double double3 = double1 - this.leftPos;
            final double double4 = double2 - this.topPos;
            for (final CreativeModeTab bba14 : CreativeModeTab.TABS) {
                if (this.checkTabClicked(bba14, double3, double4)) {
                    return true;
                }
            }
            if (CreativeModeInventoryScreen.selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && this.insideScrollbar(double1, double2)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        if (integer == 0) {
            final double double3 = double1 - this.leftPos;
            final double double4 = double2 - this.topPos;
            this.scrolling = false;
            for (final CreativeModeTab bba14 : CreativeModeTab.TABS) {
                if (this.checkTabClicked(bba14, double3, double4)) {
                    this.selectTab(bba14);
                    return true;
                }
            }
        }
        return super.mouseReleased(double1, double2, integer);
    }
    
    private boolean canScroll() {
        return CreativeModeInventoryScreen.selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[CreativeModeInventoryScreen.selectedTab].canScroll() && ((ItemPickerMenu)this.menu).canScroll();
    }
    
    private void selectTab(final CreativeModeTab bba) {
        final int integer3 = CreativeModeInventoryScreen.selectedTab;
        CreativeModeInventoryScreen.selectedTab = bba.getId();
        this.quickCraftSlots.clear();
        ((ItemPickerMenu)this.menu).items.clear();
        if (bba == CreativeModeTab.TAB_HOTBAR) {
            final HotbarManager cxy4 = this.minecraft.getHotbarManager();
            for (int integer4 = 0; integer4 < 9; ++integer4) {
                final Hotbar dmr6 = cxy4.get(integer4);
                if (dmr6.isEmpty()) {
                    for (int integer5 = 0; integer5 < 9; ++integer5) {
                        if (integer5 == integer4) {
                            final ItemStack bcj8 = new ItemStack(Items.PAPER);
                            bcj8.getOrCreateTagElement("CustomCreativeLock");
                            final String string9 = this.minecraft.options.keyHotbarSlots[integer4].getTranslatedKeyMessage();
                            final String string10 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            bcj8.setHoverName(new TranslatableComponent("inventory.hotbarInfo", new Object[] { string10, string9 }));
                            ((ItemPickerMenu)this.menu).items.add(bcj8);
                        }
                        else {
                            ((ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                        }
                    }
                }
                else {
                    ((ItemPickerMenu)this.menu).items.addAll((Collection)dmr6);
                }
            }
        }
        else if (bba != CreativeModeTab.TAB_SEARCH) {
            bba.fillItemList(((ItemPickerMenu)this.menu).items);
        }
        if (bba == CreativeModeTab.TAB_INVENTORY) {
            final AbstractContainerMenu ayk4 = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = (List<Slot>)ImmutableList.copyOf((Collection)((ItemPickerMenu)this.menu).slots);
            }
            ((ItemPickerMenu)this.menu).slots.clear();
            for (int integer4 = 0; integer4 < ayk4.slots.size(); ++integer4) {
                final Slot azx6 = new SlotWrapper((Slot)ayk4.slots.get(integer4), integer4);
                ((ItemPickerMenu)this.menu).slots.add(azx6);
                if (integer4 >= 5 && integer4 < 9) {
                    final int integer5 = integer4 - 5;
                    final int integer6 = integer5 / 2;
                    final int integer7 = integer5 % 2;
                    azx6.x = 54 + integer6 * 54;
                    azx6.y = 6 + integer7 * 27;
                }
                else if (integer4 >= 0 && integer4 < 5) {
                    azx6.x = -2000;
                    azx6.y = -2000;
                }
                else if (integer4 == 45) {
                    azx6.x = 35;
                    azx6.y = 20;
                }
                else if (integer4 < ayk4.slots.size()) {
                    final int integer5 = integer4 - 9;
                    final int integer6 = integer5 % 9;
                    final int integer7 = integer5 / 9;
                    azx6.x = 9 + integer6 * 18;
                    if (integer4 >= 36) {
                        azx6.y = 112;
                    }
                    else {
                        azx6.y = 54 + integer7 * 18;
                    }
                }
            }
            this.destroyItemSlot = new Slot(CreativeModeInventoryScreen.CONTAINER, 0, 173, 112);
            ((ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
        }
        else if (integer3 == CreativeModeTab.TAB_INVENTORY.getId()) {
            ((ItemPickerMenu)this.menu).slots.clear();
            ((ItemPickerMenu)this.menu).slots.addAll((Collection)this.originalSlots);
            this.originalSlots = null;
        }
        if (this.searchBox != null) {
            if (bba == CreativeModeTab.TAB_SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setCanLoseFocus(false);
                this.searchBox.setFocus(true);
                if (integer3 != bba.getId()) {
                    this.searchBox.setValue("");
                }
                this.refreshSearchResults();
            }
            else {
                this.searchBox.setVisible(false);
                this.searchBox.setCanLoseFocus(true);
                this.searchBox.setFocus(false);
                this.searchBox.setValue("");
            }
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }
    
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        if (!this.canScroll()) {
            return false;
        }
        final int integer8 = (((ItemPickerMenu)this.menu).items.size() + 9 - 1) / 9 - 5;
        this.scrollOffs -= (float)(double3 / integer8);
        this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
        return true;
    }
    
    @Override
    protected boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5) {
        final boolean boolean9 = double1 < integer3 || double2 < integer4 || double1 >= integer3 + this.imageWidth || double2 >= integer4 + this.imageHeight;
        return this.hasClickedOutside = (boolean9 && !this.checkTabClicked(CreativeModeTab.TABS[CreativeModeInventoryScreen.selectedTab], double1, double2));
    }
    
    protected boolean insideScrollbar(final double double1, final double double2) {
        final int integer6 = this.leftPos;
        final int integer7 = this.topPos;
        final int integer8 = integer6 + 175;
        final int integer9 = integer7 + 18;
        final int integer10 = integer8 + 14;
        final int integer11 = integer9 + 112;
        return double1 >= integer8 && double2 >= integer9 && double1 < integer10 && double2 < integer11;
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (this.scrolling) {
            final int integer2 = this.topPos + 18;
            final int integer3 = integer2 + 112;
            this.scrollOffs = ((float)double2 - integer2 - 7.5f) / (integer3 - integer2 - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            return true;
        }
        return super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        for (final CreativeModeTab bba8 : CreativeModeTab.TABS) {
            if (this.checkTabHovering(bba8, integer1, integer2)) {
                break;
            }
        }
        if (this.destroyItemSlot != null && CreativeModeInventoryScreen.selectedTab == CreativeModeTab.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, integer1, integer2)) {
            this.renderTooltip(I18n.get("inventory.binSlot"), integer1, integer2);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        this.renderTooltip(integer1, integer2);
    }
    
    @Override
    protected void renderTooltip(final ItemStack bcj, final int integer2, final int integer3) {
        if (CreativeModeInventoryScreen.selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
            final List<Component> list5 = bcj.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            final List<String> list6 = (List<String>)Lists.newArrayListWithCapacity(list5.size());
            for (final Component jo8 : list5) {
                list6.add(jo8.getColoredString());
            }
            final Item bce7 = bcj.getItem();
            CreativeModeTab bba8 = bce7.getItemCategory();
            if (bba8 == null && bce7 == Items.ENCHANTED_BOOK) {
                final Map<Enchantment, Integer> map9 = EnchantmentHelper.getEnchantments(bcj);
                if (map9.size() == 1) {
                    final Enchantment bfs10 = (Enchantment)map9.keySet().iterator().next();
                    for (final CreativeModeTab bba9 : CreativeModeTab.TABS) {
                        if (bba9.hasEnchantmentCategory(bfs10.category)) {
                            bba8 = bba9;
                            break;
                        }
                    }
                }
            }
            this.visibleTags.forEach((qv, zg) -> {
                if (zg.contains(bce7)) {
                    list6.add(1, new StringBuilder().append("").append((Object)ChatFormatting.BOLD).append((Object)ChatFormatting.DARK_PURPLE).append("#").append((Object)qv).toString());
                }
            });
            if (bba8 != null) {
                list6.add(1, new StringBuilder().append("").append((Object)ChatFormatting.BOLD).append((Object)ChatFormatting.BLUE).append(I18n.get(bba8.getName())).toString());
            }
            for (int integer4 = 0; integer4 < list6.size(); ++integer4) {
                if (integer4 == 0) {
                    list6.set(integer4, ((Object)bcj.getRarity().color + (String)list6.get(integer4)));
                }
                else {
                    list6.set(integer4, ((Object)ChatFormatting.GRAY + (String)list6.get(integer4)));
                }
            }
            this.renderTooltip(list6, integer2, integer3);
        }
        else {
            super.renderTooltip(bcj, integer2, integer3);
        }
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Lighting.turnOnGui();
        final CreativeModeTab bba5 = CreativeModeTab.TABS[CreativeModeInventoryScreen.selectedTab];
        for (final CreativeModeTab bba6 : CreativeModeTab.TABS) {
            this.minecraft.getTextureManager().bind(CreativeModeInventoryScreen.CREATIVE_TABS_LOCATION);
            if (bba6.getId() != CreativeModeInventoryScreen.selectedTab) {
                this.renderTabButton(bba6);
            }
        }
        this.minecraft.getTextureManager().bind(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + bba5.getBackgroundSuffix()));
        this.blit(this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render(integer2, integer3, float1);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer4 = this.leftPos + 175;
        final int integer5 = this.topPos + 18;
        final int integer6 = integer5 + 112;
        this.minecraft.getTextureManager().bind(CreativeModeInventoryScreen.CREATIVE_TABS_LOCATION);
        if (bba5.canScroll()) {
            this.blit(integer4, integer5 + (int)((integer6 - integer5 - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabButton(bba5);
        if (bba5 == CreativeModeTab.TAB_INVENTORY) {
            InventoryScreen.renderPlayerModel(this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - integer2), (float)(this.topPos + 45 - 30 - integer3), this.minecraft.player);
        }
    }
    
    protected boolean checkTabClicked(final CreativeModeTab bba, final double double2, final double double3) {
        final int integer7 = bba.getColumn();
        int integer8 = 28 * integer7;
        int integer9 = 0;
        if (bba.isAlignedRight()) {
            integer8 = this.imageWidth - 28 * (6 - integer7) + 2;
        }
        else if (integer7 > 0) {
            integer8 += integer7;
        }
        if (bba.isTopRow()) {
            integer9 -= 32;
        }
        else {
            integer9 += this.imageHeight;
        }
        return double2 >= integer8 && double2 <= integer8 + 28 && double3 >= integer9 && double3 <= integer9 + 32;
    }
    
    protected boolean checkTabHovering(final CreativeModeTab bba, final int integer2, final int integer3) {
        final int integer4 = bba.getColumn();
        int integer5 = 28 * integer4;
        int integer6 = 0;
        if (bba.isAlignedRight()) {
            integer5 = this.imageWidth - 28 * (6 - integer4) + 2;
        }
        else if (integer4 > 0) {
            integer5 += integer4;
        }
        if (bba.isTopRow()) {
            integer6 -= 32;
        }
        else {
            integer6 += this.imageHeight;
        }
        if (this.isHovering(integer5 + 3, integer6 + 3, 23, 27, integer2, integer3)) {
            this.renderTooltip(I18n.get(bba.getName()), integer2, integer3);
            return true;
        }
        return false;
    }
    
    protected void renderTabButton(final CreativeModeTab bba) {
        final boolean boolean3 = bba.getId() == CreativeModeInventoryScreen.selectedTab;
        final boolean boolean4 = bba.isTopRow();
        final int integer5 = bba.getColumn();
        final int integer6 = integer5 * 28;
        int integer7 = 0;
        int integer8 = this.leftPos + 28 * integer5;
        int integer9 = this.topPos;
        final int integer10 = 32;
        if (boolean3) {
            integer7 += 32;
        }
        if (bba.isAlignedRight()) {
            integer8 = this.leftPos + this.imageWidth - 28 * (6 - integer5);
        }
        else if (integer5 > 0) {
            integer8 += integer5;
        }
        if (boolean4) {
            integer9 -= 28;
        }
        else {
            integer7 += 64;
            integer9 += this.imageHeight - 4;
        }
        GlStateManager.disableLighting();
        this.blit(integer8, integer9, integer6, integer7, 28, 32);
        this.blitOffset = 100;
        this.itemRenderer.blitOffset = 100.0f;
        integer8 += 6;
        integer9 += 8 + (boolean4 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        final ItemStack bcj11 = bba.getIconItem();
        this.itemRenderer.renderAndDecorateItem(bcj11, integer8, integer9);
        this.itemRenderer.renderGuiItemDecorations(this.font, bcj11, integer8, integer9);
        GlStateManager.disableLighting();
        this.itemRenderer.blitOffset = 0.0f;
        this.blitOffset = 0;
    }
    
    public int getSelectedTab() {
        return CreativeModeInventoryScreen.selectedTab;
    }
    
    public static void handleHotbarLoadOrSave(final Minecraft cyc, final int integer, final boolean boolean3, final boolean boolean4) {
        final LocalPlayer dmp5 = cyc.player;
        final HotbarManager cxy6 = cyc.getHotbarManager();
        final Hotbar dmr7 = cxy6.get(integer);
        if (boolean3) {
            for (int integer2 = 0; integer2 < Inventory.getSelectionSize(); ++integer2) {
                final ItemStack bcj9 = ((ItemStack)dmr7.get(integer2)).copy();
                dmp5.inventory.setItem(integer2, bcj9);
                cyc.gameMode.handleCreativeModeItemAdd(bcj9, 36 + integer2);
            }
            dmp5.inventoryMenu.broadcastChanges();
        }
        else if (boolean4) {
            for (int integer2 = 0; integer2 < Inventory.getSelectionSize(); ++integer2) {
                dmr7.set(integer2, dmp5.inventory.getItem(integer2).copy());
            }
            final String string8 = cyc.options.keyHotbarSlots[integer].getTranslatedKeyMessage();
            final String string9 = cyc.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
            cyc.gui.setOverlayMessage(new TranslatableComponent("inventory.hotbarSaved", new Object[] { string9, string8 }), false);
            cxy6.save();
        }
    }
    
    static {
        CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
        CONTAINER = new SimpleContainer(45);
        CreativeModeInventoryScreen.selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
    }
    
    public static class ItemPickerMenu extends AbstractContainerMenu {
        public final NonNullList<ItemStack> items;
        
        public ItemPickerMenu(final Player awg) {
            super(null, 0);
            this.items = NonNullList.<ItemStack>create();
            final Inventory awf3 = awg.inventory;
            for (int integer4 = 0; integer4 < 5; ++integer4) {
                for (int integer5 = 0; integer5 < 9; ++integer5) {
                    this.addSlot(new CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, integer4 * 9 + integer5, 9 + integer5 * 18, 18 + integer4 * 18));
                }
            }
            for (int integer4 = 0; integer4 < 9; ++integer4) {
                this.addSlot(new Slot(awf3, integer4, 9 + integer4 * 18, 112));
            }
            this.scrollTo(0.0f);
        }
        
        @Override
        public boolean stillValid(final Player awg) {
            return true;
        }
        
        public void scrollTo(final float float1) {
            final int integer3 = (this.items.size() + 9 - 1) / 9 - 5;
            int integer4 = (int)(float1 * integer3 + 0.5);
            if (integer4 < 0) {
                integer4 = 0;
            }
            for (int integer5 = 0; integer5 < 5; ++integer5) {
                for (int integer6 = 0; integer6 < 9; ++integer6) {
                    final int integer7 = integer6 + (integer5 + integer4) * 9;
                    if (integer7 >= 0 && integer7 < this.items.size()) {
                        CreativeModeInventoryScreen.CONTAINER.setItem(integer6 + integer5 * 9, this.items.get(integer7));
                    }
                    else {
                        CreativeModeInventoryScreen.CONTAINER.setItem(integer6 + integer5 * 9, ItemStack.EMPTY);
                    }
                }
            }
        }
        
        public boolean canScroll() {
            return this.items.size() > 45;
        }
        
        @Override
        public ItemStack quickMoveStack(final Player awg, final int integer) {
            if (integer >= this.slots.size() - 9 && integer < this.slots.size()) {
                final Slot azx4 = (Slot)this.slots.get(integer);
                if (azx4 != null && azx4.hasItem()) {
                    azx4.set(ItemStack.EMPTY);
                }
            }
            return ItemStack.EMPTY;
        }
        
        @Override
        public boolean canTakeItemForPickAll(final ItemStack bcj, final Slot azx) {
            return azx.container != CreativeModeInventoryScreen.CONTAINER;
        }
        
        @Override
        public boolean canDragTo(final Slot azx) {
            return azx.container != CreativeModeInventoryScreen.CONTAINER;
        }
    }
    
    class SlotWrapper extends Slot {
        private final Slot target;
        
        public SlotWrapper(final Slot azx, final int integer) {
            super(azx.container, integer, 0, 0);
            this.target = azx;
        }
        
        @Override
        public ItemStack onTake(final Player awg, final ItemStack bcj) {
            this.target.onTake(awg, bcj);
            return bcj;
        }
        
        @Override
        public boolean mayPlace(final ItemStack bcj) {
            return this.target.mayPlace(bcj);
        }
        
        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }
        
        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }
        
        @Override
        public void set(final ItemStack bcj) {
            this.target.set(bcj);
        }
        
        @Override
        public void setChanged() {
            this.target.setChanged();
        }
        
        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }
        
        @Override
        public int getMaxStackSize(final ItemStack bcj) {
            return this.target.getMaxStackSize(bcj);
        }
        
        @Nullable
        @Override
        public String getNoItemIcon() {
            return this.target.getNoItemIcon();
        }
        
        @Override
        public ItemStack remove(final int integer) {
            return this.target.remove(integer);
        }
        
        @Override
        public boolean isActive() {
            return this.target.isActive();
        }
        
        @Override
        public boolean mayPickup(final Player awg) {
            return this.target.mayPickup(awg);
        }
    }
    
    static class CustomCreativeSlot extends Slot {
        public CustomCreativeSlot(final Container ahc, final int integer2, final int integer3, final int integer4) {
            super(ahc, integer2, integer3, integer4);
        }
        
        @Override
        public boolean mayPickup(final Player awg) {
            if (super.mayPickup(awg) && this.hasItem()) {
                return this.getItem().getTagElement("CustomCreativeLock") == null;
            }
            return !this.hasItem();
        }
    }
}

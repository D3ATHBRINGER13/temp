package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.stats.RecipeBook;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.client.resources.language.Language;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Locale;
import net.minecraft.client.searchtree.SearchRegistry;
import javax.annotation.Nullable;
import net.minecraft.world.inventory.Slot;
import java.util.Iterator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.resources.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.client.gui.components.StateSwitchingButton;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.GuiComponent;

public class RecipeBookComponent extends GuiComponent implements Widget, GuiEventListener, RecipeShownListener, PlaceRecipe<Ingredient> {
    protected static final ResourceLocation RECIPE_BOOK_LOCATION;
    private int xOffset;
    private int width;
    private int height;
    protected final GhostRecipe ghostRecipe;
    private final List<RecipeBookTabButton> tabButtons;
    private RecipeBookTabButton selectedTab;
    protected StateSwitchingButton filterButton;
    protected RecipeBookMenu<?> menu;
    protected Minecraft minecraft;
    private EditBox searchBox;
    private String lastSearch;
    protected ClientRecipeBook book;
    protected final RecipeBookPage recipeBookPage;
    protected final StackedContents stackedContents;
    private int timesInventoryChanged;
    private boolean ignoreTextInput;
    
    public RecipeBookComponent() {
        this.ghostRecipe = new GhostRecipe();
        this.tabButtons = (List<RecipeBookTabButton>)Lists.newArrayList();
        this.lastSearch = "";
        this.recipeBookPage = new RecipeBookPage();
        this.stackedContents = new StackedContents();
    }
    
    public void init(final int integer1, final int integer2, final Minecraft cyc, final boolean boolean4, final RecipeBookMenu<?> azq) {
        this.minecraft = cyc;
        this.width = integer1;
        this.height = integer2;
        this.menu = azq;
        cyc.player.containerMenu = azq;
        this.book = cyc.player.getRecipeBook();
        this.timesInventoryChanged = cyc.player.inventory.getTimesChanged();
        if (this.isVisible()) {
            this.initVisuals(boolean4);
        }
        cyc.keyboardHandler.setSendRepeatsToGui(true);
    }
    
    public void initVisuals(final boolean boolean1) {
        this.xOffset = (boolean1 ? 0 : 86);
        final int integer3 = (this.width - 147) / 2 - this.xOffset;
        final int integer4 = (this.height - 166) / 2;
        this.stackedContents.clear();
        this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        final String string5 = (this.searchBox != null) ? this.searchBox.getValue() : "";
        final Font font = this.minecraft.font;
        final int integer5 = integer3 + 25;
        final int integer6 = integer4 + 14;
        final int integer7 = 80;
        this.minecraft.font.getClass();
        (this.searchBox = new EditBox(font, integer5, integer6, integer7, 9 + 5, I18n.get("itemGroup.search"))).setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string5);
        this.recipeBookPage.init(this.minecraft, integer3, integer4);
        this.recipeBookPage.addListener(this);
        this.filterButton = new StateSwitchingButton(integer3 + 110, integer4 + 12, 26, 16, this.book.isFilteringCraftable(this.menu));
        this.initFilterButtonTextures();
        this.tabButtons.clear();
        for (final RecipeBookCategories cyj7 : ClientRecipeBook.getCategories(this.menu)) {
            this.tabButtons.add(new RecipeBookTabButton(cyj7));
        }
        if (this.selectedTab != null) {
            this.selectedTab = (RecipeBookTabButton)this.tabButtons.stream().filter(dfa -> dfa.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
        }
        if (this.selectedTab == null) {
            this.selectedTab = (RecipeBookTabButton)this.tabButtons.get(0);
        }
        this.selectedTab.setStateTriggered(true);
        this.updateCollections(false);
        this.updateTabs();
    }
    
    @Override
    public boolean changeFocus(final boolean boolean1) {
        return false;
    }
    
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 41, 28, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }
    
    public void removed() {
        this.searchBox = null;
        this.selectedTab = null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    public int updateScreenPosition(final boolean boolean1, final int integer2, final int integer3) {
        int integer4;
        if (this.isVisible() && !boolean1) {
            integer4 = 177 + (integer2 - integer3 - 200) / 2;
        }
        else {
            integer4 = (integer2 - integer3) / 2;
        }
        return integer4;
    }
    
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }
    
    public boolean isVisible() {
        return this.book.isGuiOpen();
    }
    
    protected void setVisible(final boolean boolean1) {
        this.book.setGuiOpen(boolean1);
        if (!boolean1) {
            this.recipeBookPage.setInvisible();
        }
        this.sendUpdateSettings();
    }
    
    public void slotClicked(@Nullable final Slot azx) {
        if (azx != null && azx.index < this.menu.getSize()) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }
    }
    
    private void updateCollections(final boolean boolean1) {
        final List<RecipeCollection> list3 = this.book.getCollection(this.selectedTab.getCategory());
        list3.forEach(dfc -> dfc.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book));
        final List<RecipeCollection> list4 = (List<RecipeCollection>)Lists.newArrayList((Iterable)list3);
        list4.removeIf(dfc -> !dfc.hasKnownRecipes());
        list4.removeIf(dfc -> !dfc.hasFitting());
        final String string5 = this.searchBox.getValue();
        if (!string5.isEmpty()) {
            final ObjectSet<RecipeCollection> objectSet6 = (ObjectSet<RecipeCollection>)new ObjectLinkedOpenHashSet((Collection)this.minecraft.<RecipeCollection>getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(string5.toLowerCase(Locale.ROOT)));
            list4.removeIf(dfc -> !objectSet6.contains(dfc));
        }
        if (this.book.isFilteringCraftable(this.menu)) {
            list4.removeIf(dfc -> !dfc.hasCraftable());
        }
        this.recipeBookPage.updateCollections(list4, boolean1);
    }
    
    private void updateTabs() {
        final int integer2 = (this.width - 147) / 2 - this.xOffset - 30;
        final int integer3 = (this.height - 166) / 2 + 3;
        final int integer4 = 27;
        int integer5 = 0;
        for (final RecipeBookTabButton dfa7 : this.tabButtons) {
            final RecipeBookCategories cyj8 = dfa7.getCategory();
            if (cyj8 == RecipeBookCategories.SEARCH || cyj8 == RecipeBookCategories.FURNACE_SEARCH) {
                dfa7.visible = true;
                dfa7.setPosition(integer2, integer3 + 27 * integer5++);
            }
            else {
                if (!dfa7.updateVisibility(this.book)) {
                    continue;
                }
                dfa7.setPosition(integer2, integer3 + 27 * integer5++);
                dfa7.startAnimation(this.minecraft);
            }
        }
    }
    
    public void tick() {
        if (!this.isVisible()) {
            return;
        }
        if (this.timesInventoryChanged != this.minecraft.player.inventory.getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.inventory.getTimesChanged();
        }
    }
    
    private void updateStackedContents() {
        this.stackedContents.clear();
        this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        this.updateCollections(false);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (!this.isVisible()) {
            return;
        }
        Lighting.turnOnGui();
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 100.0f);
        this.minecraft.getTextureManager().bind(RecipeBookComponent.RECIPE_BOOK_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer3 = (this.width - 147) / 2 - this.xOffset;
        final int integer4 = (this.height - 166) / 2;
        this.blit(integer3, integer4, 1, 1, 147, 166);
        this.searchBox.render(integer1, integer2, float3);
        Lighting.turnOff();
        for (final RecipeBookTabButton dfa8 : this.tabButtons) {
            dfa8.render(integer1, integer2, float3);
        }
        this.filterButton.render(integer1, integer2, float3);
        this.recipeBookPage.render(integer3, integer4, integer1, integer2, float3);
        GlStateManager.popMatrix();
    }
    
    public void renderTooltip(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (!this.isVisible()) {
            return;
        }
        this.recipeBookPage.renderTooltip(integer3, integer4);
        if (this.filterButton.isHovered()) {
            final String string6 = this.getFilterButtonTooltip();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.renderTooltip(string6, integer3, integer4);
            }
        }
        this.renderGhostRecipeTooltip(integer1, integer2, integer3, integer4);
    }
    
    protected String getFilterButtonTooltip() {
        return I18n.get(this.filterButton.isStateTriggered() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
    }
    
    private void renderGhostRecipeTooltip(final int integer1, final int integer2, final int integer3, final int integer4) {
        ItemStack bcj6 = null;
        for (int integer5 = 0; integer5 < this.ghostRecipe.size(); ++integer5) {
            final GhostRecipe.GhostIngredient a8 = this.ghostRecipe.get(integer5);
            final int integer6 = a8.getX() + integer1;
            final int integer7 = a8.getY() + integer2;
            if (integer3 >= integer6 && integer4 >= integer7 && integer3 < integer6 + 16 && integer4 < integer7 + 16) {
                bcj6 = a8.getItem();
            }
        }
        if (bcj6 != null && this.minecraft.screen != null) {
            this.minecraft.screen.renderTooltip(this.minecraft.screen.getTooltipFromItem(bcj6), integer3, integer4);
        }
    }
    
    public void renderGhostRecipe(final int integer1, final int integer2, final boolean boolean3, final float float4) {
        this.ghostRecipe.render(this.minecraft, integer1, integer2, boolean3, float4);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.recipeBookPage.mouseClicked(double1, double2, integer, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            final Recipe<?> ber7 = this.recipeBookPage.getLastClickedRecipe();
            final RecipeCollection dfc8 = this.recipeBookPage.getLastClickedRecipeCollection();
            if (ber7 != null && dfc8 != null) {
                if (!dfc8.isCraftable(ber7) && this.ghostRecipe.getRecipe() == ber7) {
                    return false;
                }
                this.ghostRecipe.clear();
                this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, ber7, Screen.hasShiftDown());
                if (!this.isOffsetNextToMainGUI()) {
                    this.setVisible(false);
                }
            }
            return true;
        }
        if (this.searchBox.mouseClicked(double1, double2, integer)) {
            return true;
        }
        if (this.filterButton.mouseClicked(double1, double2, integer)) {
            final boolean boolean7 = this.updateFiltering();
            this.filterButton.setStateTriggered(boolean7);
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
        }
        for (final RecipeBookTabButton dfa8 : this.tabButtons) {
            if (dfa8.mouseClicked(double1, double2, integer)) {
                if (this.selectedTab != dfa8) {
                    this.selectedTab.setStateTriggered(false);
                    (this.selectedTab = dfa8).setStateTriggered(true);
                    this.updateCollections(true);
                }
                return true;
            }
        }
        return false;
    }
    
    protected boolean updateFiltering() {
        final boolean boolean2 = !this.book.isFilteringCraftable();
        this.book.setFilteringCraftable(boolean2);
        return boolean2;
    }
    
    public boolean hasClickedOutside(final double double1, final double double2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        if (!this.isVisible()) {
            return true;
        }
        final boolean boolean11 = double1 < integer3 || double2 < integer4 || double1 >= integer3 + integer5 || double2 >= integer4 + integer6;
        final boolean boolean12 = integer3 - 147 < double1 && double1 < integer3 && integer4 < double2 && double2 < integer4 + integer6;
        return boolean11 && !boolean12 && !this.selectedTab.isHovered();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (integer1 == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
        }
        if (this.searchBox.keyPressed(integer1, integer2, integer3)) {
            this.checkSearchStringUpdate();
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && integer1 != 256) {
            return true;
        }
        if (this.minecraft.options.keyChat.matches(integer1, integer2) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocus(true);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyReleased(final int integer1, final int integer2, final int integer3) {
        this.ignoreTextInput = false;
        return super.keyReleased(integer1, integer2, integer3);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.searchBox.charTyped(character, integer)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return super.charTyped(character, integer);
    }
    
    @Override
    public boolean isMouseOver(final double double1, final double double2) {
        return false;
    }
    
    private void checkSearchStringUpdate() {
        final String string2 = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople(string2);
        if (!string2.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = string2;
        }
    }
    
    private void pirateSpeechForThePeople(final String string) {
        if ("excitedze".equals(string)) {
            final LanguageManager dxz3 = this.minecraft.getLanguageManager();
            final Language dxy4 = dxz3.getLanguage("en_pt");
            if (dxz3.getSelected().compareTo(dxy4) == 0) {
                return;
            }
            dxz3.setSelected(dxy4);
            this.minecraft.options.languageCode = dxy4.getCode();
            this.minecraft.reloadResourcePacks();
            this.minecraft.font.setBidirectional(dxz3.isBidirectional());
            this.minecraft.options.save();
        }
    }
    
    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }
    
    public void recipesUpdated() {
        this.updateTabs();
        if (this.isVisible()) {
            this.updateCollections(false);
        }
    }
    
    @Override
    public void recipesShown(final List<Recipe<?>> list) {
        for (final Recipe<?> ber4 : list) {
            this.minecraft.player.removeRecipeHighlight(ber4);
        }
    }
    
    public void setupGhostRecipe(final Recipe<?> ber, final List<Slot> list) {
        final ItemStack bcj4 = ber.getResultItem();
        this.ghostRecipe.setRecipe(ber);
        this.ghostRecipe.addIngredient(Ingredient.of(bcj4), ((Slot)list.get(0)).x, ((Slot)list.get(0)).y);
        this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), ber, (java.util.Iterator<Ingredient>)ber.getIngredients().iterator(), 0);
    }
    
    @Override
    public void addItemToSlot(final Iterator<Ingredient> iterator, final int integer2, final int integer3, final int integer4, final int integer5) {
        final Ingredient beo7 = (Ingredient)iterator.next();
        if (!beo7.isEmpty()) {
            final Slot azx8 = (Slot)this.menu.slots.get(integer2);
            this.ghostRecipe.addIngredient(beo7, azx8.x, azx8.y);
        }
    }
    
    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
            this.minecraft.getConnection().send(new ServerboundRecipeBookUpdatePacket(this.book.isGuiOpen(), this.book.isFilteringCraftable(), this.book.isFurnaceGuiOpen(), this.book.isFurnaceFilteringCraftable(), this.book.isBlastingFurnaceGuiOpen(), this.book.isBlastingFurnaceFilteringCraftable()));
        }
    }
    
    static {
        RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    }
}

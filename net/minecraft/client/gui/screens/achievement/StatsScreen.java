package net.minecraft.client.gui.screens.achievement;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Set;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Comparator;
import net.minecraft.world.level.block.Block;
import net.minecraft.stats.StatType;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import java.util.Iterator;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.Font;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.gui.screens.Screen;

public class StatsScreen extends Screen implements StatsUpdateListener {
    protected final Screen lastScreen;
    private GeneralStatisticsList statsList;
    private ItemStatisticsList itemStatsList;
    private MobsStatisticsList mobsStatsList;
    private final StatsCounter stats;
    @Nullable
    private ObjectSelectionList<?> activeList;
    private boolean isLoading;
    
    public StatsScreen(final Screen dcl, final StatsCounter yz) {
        super(new TranslatableComponent("gui.stats", new Object[0]));
        this.isLoading = true;
        this.lastScreen = dcl;
        this.stats = yz;
    }
    
    @Override
    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }
    
    public void initLists() {
        this.statsList = new GeneralStatisticsList(this.minecraft);
        this.itemStatsList = new ItemStatisticsList(this.minecraft);
        this.mobsStatsList = new MobsStatisticsList(this.minecraft);
    }
    
    public void initButtons() {
        this.<Button>addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, I18n.get("stat.generalButton"), czi -> this.setActiveList(this.statsList)));
        final Button czi2 = this.<Button>addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, I18n.get("stat.itemsButton"), czi -> this.setActiveList(this.itemStatsList)));
        final Button czi3 = this.<Button>addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, I18n.get("stat.mobsButton"), czi -> this.setActiveList(this.mobsStatsList)));
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
        if (this.itemStatsList.children().isEmpty()) {
            czi2.active = false;
        }
        if (this.mobsStatsList.children().isEmpty()) {
            czi3.active = false;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (this.isLoading) {
            this.renderBackground();
            this.drawCenteredString(this.font, I18n.get("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
            final Font font = this.font;
            final String string = StatsScreen.LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % StatsScreen.LOADING_SYMBOLS.length)];
            final int integer3 = this.width / 2;
            final int n = this.height / 2;
            this.font.getClass();
            this.drawCenteredString(font, string, integer3, n + 9 * 2, 16777215);
        }
        else {
            this.getActiveList().render(integer1, integer2, float3);
            this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
            super.render(integer1, integer2, float3);
        }
    }
    
    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.initLists();
            this.initButtons();
            this.setActiveList(this.statsList);
            this.isLoading = false;
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return !this.isLoading;
    }
    
    @Nullable
    public ObjectSelectionList<?> getActiveList() {
        return this.activeList;
    }
    
    public void setActiveList(@Nullable final ObjectSelectionList<?> czs) {
        this.children.remove(this.statsList);
        this.children.remove(this.itemStatsList);
        this.children.remove(this.mobsStatsList);
        if (czs != null) {
            this.children.add(0, czs);
            this.activeList = czs;
        }
    }
    
    private int getColumnX(final int integer) {
        return 115 + 40 * integer;
    }
    
    private void blitSlot(final int integer1, final int integer2, final Item bce) {
        this.blitSlotIcon(integer1 + 1, integer2 + 1, 0, 0);
        GlStateManager.enableRescaleNormal();
        Lighting.turnOnGui();
        this.itemRenderer.renderGuiItem(bce.getDefaultInstance(), integer1 + 2, integer2 + 2);
        Lighting.turnOff();
        GlStateManager.disableRescaleNormal();
    }
    
    private void blitSlotIcon(final int integer1, final int integer2, final int integer3, final int integer4) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(StatsScreen.STATS_ICON_LOCATION);
        GuiComponent.blit(integer1, integer2, this.blitOffset, (float)integer3, (float)integer4, 18, 18, 128, 128);
    }
    
    class GeneralStatisticsList extends ObjectSelectionList<Entry> {
        public GeneralStatisticsList(final Minecraft cyc) {
            super(cyc, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            for (final Stat<ResourceLocation> yv5 : Stats.CUSTOM) {
                this.addEntry(new Entry((Stat)yv5));
            }
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Stat<ResourceLocation> stat;
            
            private Entry(final Stat<ResourceLocation> yv) {
                this.stat = yv;
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                final Component jo11 = new TranslatableComponent("stat." + this.stat.getValue().toString().replace(':', '.'), new Object[0]).withStyle(ChatFormatting.GRAY);
                GeneralStatisticsList.this.drawString(StatsScreen.this.font, jo11.getString(), integer3 + 2, integer2 + 1, (integer1 % 2 == 0) ? 16777215 : 9474192);
                final String string12 = this.stat.format(StatsScreen.this.stats.getValue(this.stat));
                GeneralStatisticsList.this.drawString(StatsScreen.this.font, string12, integer3 + 2 + 213 - StatsScreen.this.font.width(string12), integer2 + 1, (integer1 % 2 == 0) ? 16777215 : 9474192);
            }
        }
    }
    
    class ItemStatisticsList extends ObjectSelectionList<ItemRow> {
        protected final List<StatType<Block>> blockColumns;
        protected final List<StatType<Item>> itemColumns;
        private final int[] iconOffsets;
        protected int headerPressed;
        protected final List<Item> statItemList;
        protected final Comparator<Item> itemStatSorter;
        @Nullable
        protected StatType<?> sortColumn;
        protected int sortOrder;
        
        public ItemStatisticsList(final Minecraft cyc) {
            super(cyc, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.iconOffsets = new int[] { 3, 4, 1, 2, 5, 6 };
            this.headerPressed = -1;
            this.itemStatSorter = (Comparator<Item>)new ItemComparator();
            (this.blockColumns = (List<StatType<Block>>)Lists.newArrayList()).add(Stats.BLOCK_MINED);
            this.itemColumns = (List<StatType<Item>>)Lists.newArrayList((Object[])new StatType[] { Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED });
            this.setRenderHeader(true, 20);
            final Set<Item> set4 = (Set<Item>)Sets.newIdentityHashSet();
            for (final Item bce6 : Registry.ITEM) {
                boolean boolean7 = false;
                for (final StatType<Item> yx9 : this.itemColumns) {
                    if (yx9.contains(bce6) && StatsScreen.this.stats.getValue(yx9.get(bce6)) > 0) {
                        boolean7 = true;
                    }
                }
                if (boolean7) {
                    set4.add(bce6);
                }
            }
            for (final Block bmv6 : Registry.BLOCK) {
                boolean boolean7 = false;
                for (final StatType<Block> yx10 : this.blockColumns) {
                    if (yx10.contains(bmv6) && StatsScreen.this.stats.getValue(yx10.get(bmv6)) > 0) {
                        boolean7 = true;
                    }
                }
                if (boolean7) {
                    set4.add(bmv6.asItem());
                }
            }
            set4.remove(Items.AIR);
            this.statItemList = (List<Item>)Lists.newArrayList((Iterable)set4);
            for (int integer5 = 0; integer5 < this.statItemList.size(); ++integer5) {
                this.addEntry(new ItemRow());
            }
        }
        
        @Override
        protected void renderHeader(final int integer1, final int integer2, final Tesselator cuz) {
            if (!this.minecraft.mouseHandler.isLeftPressed()) {
                this.headerPressed = -1;
            }
            for (int integer3 = 0; integer3 < this.iconOffsets.length; ++integer3) {
                StatsScreen.this.blitSlotIcon(integer1 + StatsScreen.this.getColumnX(integer3) - 18, integer2 + 1, 0, (this.headerPressed == integer3) ? 0 : 18);
            }
            if (this.sortColumn != null) {
                final int integer3 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
                final int integer4 = (this.sortOrder == 1) ? 2 : 1;
                StatsScreen.this.blitSlotIcon(integer1 + integer3, integer2 + 1, 18 * integer4, 0);
            }
            for (int integer3 = 0; integer3 < this.iconOffsets.length; ++integer3) {
                final int integer4 = (this.headerPressed == integer3) ? 1 : 0;
                StatsScreen.this.blitSlotIcon(integer1 + StatsScreen.this.getColumnX(integer3) - 18 + integer4, integer2 + 1 + integer4, 18 * this.iconOffsets[integer3], 18);
            }
        }
        
        @Override
        public int getRowWidth() {
            return 375;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        @Override
        protected void clickedHeader(final int integer1, final int integer2) {
            this.headerPressed = -1;
            for (int integer3 = 0; integer3 < this.iconOffsets.length; ++integer3) {
                final int integer4 = integer1 - StatsScreen.this.getColumnX(integer3);
                if (integer4 >= -36 && integer4 <= 0) {
                    this.headerPressed = integer3;
                    break;
                }
            }
            if (this.headerPressed >= 0) {
                this.sortByColumn(this.getColumn(this.headerPressed));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }
        
        private StatType<?> getColumn(final int integer) {
            return ((integer < this.blockColumns.size()) ? this.blockColumns.get(integer) : ((StatType)this.itemColumns.get(integer - this.blockColumns.size())));
        }
        
        private int getColumnIndex(final StatType<?> yx) {
            final int integer3 = this.blockColumns.indexOf(yx);
            if (integer3 >= 0) {
                return integer3;
            }
            final int integer4 = this.itemColumns.indexOf(yx);
            if (integer4 >= 0) {
                return integer4 + this.blockColumns.size();
            }
            return -1;
        }
        
        @Override
        protected void renderDecorations(final int integer1, final int integer2) {
            if (integer2 < this.y0 || integer2 > this.y1) {
                return;
            }
            final ItemRow b4 = this.getEntryAtPosition(integer1, integer2);
            final int integer3 = (this.width - this.getRowWidth()) / 2;
            if (b4 != null) {
                if (integer1 < integer3 + 40 || integer1 > integer3 + 40 + 20) {
                    return;
                }
                final Item bce6 = (Item)this.statItemList.get(this.children().indexOf(b4));
                this.renderMousehoverTooltip(this.getString(bce6), integer1, integer2);
            }
            else {
                Component jo6 = null;
                final int integer4 = integer1 - integer3;
                for (int integer5 = 0; integer5 < this.iconOffsets.length; ++integer5) {
                    final int integer6 = StatsScreen.this.getColumnX(integer5);
                    if (integer4 >= integer6 - 18 && integer4 <= integer6) {
                        jo6 = new TranslatableComponent(this.getColumn(integer5).getTranslationKey(), new Object[0]);
                        break;
                    }
                }
                this.renderMousehoverTooltip(jo6, integer1, integer2);
            }
        }
        
        protected void renderMousehoverTooltip(@Nullable final Component jo, final int integer2, final int integer3) {
            if (jo == null) {
                return;
            }
            final String string5 = jo.getColoredString();
            final int integer4 = integer2 + 12;
            final int integer5 = integer3 - 12;
            final int integer6 = StatsScreen.this.font.width(string5);
            this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
            StatsScreen.this.font.drawShadow(string5, (float)integer4, (float)integer5, -1);
        }
        
        protected Component getString(final Item bce) {
            return bce.getDescription();
        }
        
        protected void sortByColumn(final StatType<?> yx) {
            if (yx != this.sortColumn) {
                this.sortColumn = yx;
                this.sortOrder = -1;
            }
            else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            }
            else {
                this.sortColumn = null;
                this.sortOrder = 0;
            }
            this.statItemList.sort((Comparator)this.itemStatSorter);
        }
        
        class ItemComparator implements Comparator<Item> {
            private ItemComparator() {
            }
            
            public int compare(final Item bce1, final Item bce2) {
                int integer4;
                int integer5;
                if (ItemStatisticsList.this.sortColumn == null) {
                    integer4 = 0;
                    integer5 = 0;
                }
                else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                    final StatType<Block> yx6 = (StatType<Block>)ItemStatisticsList.this.sortColumn;
                    integer4 = ((bce1 instanceof BlockItem) ? StatsScreen.this.stats.<Block>getValue(yx6, ((BlockItem)bce1).getBlock()) : -1);
                    integer5 = ((bce2 instanceof BlockItem) ? StatsScreen.this.stats.<Block>getValue(yx6, ((BlockItem)bce2).getBlock()) : -1);
                }
                else {
                    final StatType<Item> yx7 = (StatType<Item>)ItemStatisticsList.this.sortColumn;
                    integer4 = StatsScreen.this.stats.<Item>getValue(yx7, bce1);
                    integer5 = StatsScreen.this.stats.<Item>getValue(yx7, bce2);
                }
                if (integer4 == integer5) {
                    return ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(bce1), Item.getId(bce2));
                }
                return ItemStatisticsList.this.sortOrder * Integer.compare(integer4, integer5);
            }
        }
        
        class ItemRow extends Entry<ItemRow> {
            private ItemRow() {
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                final Item bce11 = (Item)StatsScreen.this.itemStatsList.statItemList.get(integer1);
                StatsScreen.this.blitSlot(integer3 + 40, integer2, bce11);
                for (int integer8 = 0; integer8 < StatsScreen.this.itemStatsList.blockColumns.size(); ++integer8) {
                    Stat<Block> yv13;
                    if (bce11 instanceof BlockItem) {
                        yv13 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get(integer8)).get(((BlockItem)bce11).getBlock());
                    }
                    else {
                        yv13 = null;
                    }
                    this.renderStat(yv13, integer3 + StatsScreen.this.getColumnX(integer8), integer2, integer1 % 2 == 0);
                }
                for (int integer8 = 0; integer8 < StatsScreen.this.itemStatsList.itemColumns.size(); ++integer8) {
                    this.renderStat(((StatType)StatsScreen.this.itemStatsList.itemColumns.get(integer8)).get(bce11), integer3 + StatsScreen.this.getColumnX(integer8 + StatsScreen.this.itemStatsList.blockColumns.size()), integer2, integer1 % 2 == 0);
                }
            }
            
            protected void renderStat(@Nullable final Stat<?> yv, final int integer2, final int integer3, final boolean boolean4) {
                final String string6 = (yv == null) ? "-" : yv.format(StatsScreen.this.stats.getValue(yv));
                ItemStatisticsList.this.drawString(StatsScreen.this.font, string6, integer2 - StatsScreen.this.font.width(string6), integer3 + 5, boolean4 ? 16777215 : 9474192);
            }
        }
    }
    
    class MobsStatisticsList extends ObjectSelectionList<MobRow> {
        public MobsStatisticsList(final Minecraft cyc) {
            final int width = StatsScreen.this.width;
            final int height = StatsScreen.this.height;
            final int integer4 = 32;
            final int integer5 = StatsScreen.this.height - 64;
            StatsScreen.this.font.getClass();
            super(cyc, width, height, integer4, integer5, 9 * 4);
            for (final EntityType<?> ais5 : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(ais5)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(ais5)) > 0) {
                    this.addEntry(new MobRow(ais5));
                }
            }
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        class MobRow extends Entry<MobRow> {
            private final EntityType<?> type;
            
            public MobRow(final EntityType<?> ais) {
                this.type = ais;
            }
            
            @Override
            public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
                final String string11 = I18n.get(Util.makeDescriptionId("entity", EntityType.getKey(this.type)));
                final int integer8 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(this.type));
                final int integer9 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(this.type));
                MobsStatisticsList.this.drawString(StatsScreen.this.font, string11, integer3 + 2, integer2 + 1, 16777215);
                final MobsStatisticsList this$1 = MobsStatisticsList.this;
                final Font access$1700 = StatsScreen.this.font;
                final String killsMessage = this.killsMessage(string11, integer8);
                final int integer10 = integer3 + 2 + 10;
                final int n = integer2 + 1;
                StatsScreen.this.font.getClass();
                this$1.drawString(access$1700, killsMessage, integer10, n + 9, (integer8 == 0) ? 6316128 : 9474192);
                final MobsStatisticsList this$2 = MobsStatisticsList.this;
                final Font access$1701 = StatsScreen.this.font;
                final String killedByMessage = this.killedByMessage(string11, integer9);
                final int integer11 = integer3 + 2 + 10;
                final int n2 = integer2 + 1;
                StatsScreen.this.font.getClass();
                this$2.drawString(access$1701, killedByMessage, integer11, n2 + 9 * 2, (integer9 == 0) ? 6316128 : 9474192);
            }
            
            private String killsMessage(final String string, final int integer) {
                final String string2 = Stats.ENTITY_KILLED.getTranslationKey();
                if (integer == 0) {
                    return I18n.get(string2 + ".none", string);
                }
                return I18n.get(string2, integer, string);
            }
            
            private String killedByMessage(final String string, final int integer) {
                final String string2 = Stats.ENTITY_KILLED_BY.getTranslationKey();
                if (integer == 0) {
                    return I18n.get(string2 + ".none", string);
                }
                return I18n.get(string2, string, integer);
            }
        }
    }
}

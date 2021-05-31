package net.minecraft.client.gui.screens.inventory;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import java.util.Collections;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;

public class BookViewScreen extends Screen {
    public static final BookAccess EMPTY_ACCESS;
    public static final ResourceLocation BOOK_LOCATION;
    private BookAccess bookAccess;
    private int currentPage;
    private List<Component> cachedPageComponents;
    private int cachedPage;
    private PageButton forwardButton;
    private PageButton backButton;
    private final boolean playTurnSound;
    
    public BookViewScreen(final BookAccess a) {
        this(a, true);
    }
    
    public BookViewScreen() {
        this(BookViewScreen.EMPTY_ACCESS, false);
    }
    
    private BookViewScreen(final BookAccess a, final boolean boolean2) {
        super(NarratorChatListener.NO_TITLE);
        this.cachedPageComponents = (List<Component>)Collections.emptyList();
        this.cachedPage = -1;
        this.bookAccess = a;
        this.playTurnSound = boolean2;
    }
    
    public void setBookAccess(final BookAccess a) {
        this.bookAccess = a;
        this.currentPage = Mth.clamp(this.currentPage, 0, a.getPageCount());
        this.updateButtonVisibility();
        this.cachedPage = -1;
    }
    
    public boolean setPage(final int integer) {
        final int integer2 = Mth.clamp(integer, 0, this.bookAccess.getPageCount() - 1);
        if (integer2 != this.currentPage) {
            this.currentPage = integer2;
            this.updateButtonVisibility();
            this.cachedPage = -1;
            return true;
        }
        return false;
    }
    
    protected boolean forcePage(final int integer) {
        return this.setPage(integer);
    }
    
    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }
    
    protected void createMenuControls() {
        this.<Button>addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(null)));
    }
    
    protected void createPageControlButtons() {
        final int integer2 = (this.width - 192) / 2;
        final int integer3 = 2;
        this.forwardButton = this.<PageButton>addButton(new PageButton(integer2 + 116, 159, true, czi -> this.pageForward(), this.playTurnSound));
        this.backButton = this.<PageButton>addButton(new PageButton(integer2 + 43, 159, false, czi -> this.pageBack(), this.playTurnSound));
        this.updateButtonVisibility();
    }
    
    private int getNumPages() {
        return this.bookAccess.getPageCount();
    }
    
    protected void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtonVisibility();
    }
    
    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        }
        this.updateButtonVisibility();
    }
    
    private void updateButtonVisibility() {
        this.forwardButton.visible = (this.currentPage < this.getNumPages() - 1);
        this.backButton.visible = (this.currentPage > 0);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        switch (integer1) {
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
        final int integer3 = (this.width - 192) / 2;
        final int integer4 = 2;
        this.blit(integer3, 2, 0, 0, 192, 192);
        final String string7 = I18n.get("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
        if (this.cachedPage != this.currentPage) {
            final Component jo8 = this.bookAccess.getPage(this.currentPage);
            this.cachedPageComponents = ComponentRenderUtils.wrapComponents(jo8, 114, this.font, true, true);
        }
        this.cachedPage = this.currentPage;
        final int integer5 = this.strWidth(string7);
        this.font.draw(string7, (float)(integer3 - integer5 + 192 - 44), 18.0f, 0);
        final int n = 128;
        this.font.getClass();
        for (int integer6 = Math.min(n / 9, this.cachedPageComponents.size()), integer7 = 0; integer7 < integer6; ++integer7) {
            final Component jo9 = (Component)this.cachedPageComponents.get(integer7);
            final Font font = this.font;
            final String coloredString = jo9.getColoredString();
            final float float4 = (float)(integer3 + 36);
            final int n2 = 32;
            final int n3 = integer7;
            this.font.getClass();
            font.draw(coloredString, float4, (float)(n2 + n3 * 9), 0);
        }
        final Component jo10 = this.getClickedComponentAt(integer1, integer2);
        if (jo10 != null) {
            this.renderComponentHoverEffect(jo10, integer1, integer2);
        }
        super.render(integer1, integer2, float3);
    }
    
    private int strWidth(final String string) {
        return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(string) : string);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (integer == 0) {
            final Component jo7 = this.getClickedComponentAt(double1, double2);
            if (jo7 != null && this.handleComponentClicked(jo7)) {
                return true;
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean handleComponentClicked(final Component jo) {
        final ClickEvent jn3 = jo.getStyle().getClickEvent();
        if (jn3 == null) {
            return false;
        }
        if (jn3.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            final String string4 = jn3.getValue();
            try {
                final int integer5 = Integer.parseInt(string4) - 1;
                return this.forcePage(integer5);
            }
            catch (Exception ex) {
                return false;
            }
        }
        final boolean boolean4 = super.handleComponentClicked(jo);
        if (boolean4 && jn3.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.setScreen(null);
        }
        return boolean4;
    }
    
    @Nullable
    public Component getClickedComponentAt(final double double1, final double double2) {
        if (this.cachedPageComponents == null) {
            return null;
        }
        final int integer6 = Mth.floor(double1 - (this.width - 192) / 2 - 36.0);
        final int integer7 = Mth.floor(double2 - 2.0 - 30.0);
        if (integer6 < 0 || integer7 < 0) {
            return null;
        }
        final int n = 128;
        this.font.getClass();
        final int integer8 = Math.min(n / 9, this.cachedPageComponents.size());
        if (integer6 <= 114) {
            final int n2 = integer7;
            this.minecraft.font.getClass();
            if (n2 < 9 * integer8 + integer8) {
                final int n3 = integer7;
                this.minecraft.font.getClass();
                final int integer9 = n3 / 9;
                if (integer9 >= 0 && integer9 < this.cachedPageComponents.size()) {
                    final Component jo10 = (Component)this.cachedPageComponents.get(integer9);
                    int integer10 = 0;
                    for (final Component jo11 : jo10) {
                        if (jo11 instanceof TextComponent) {
                            integer10 += this.minecraft.font.width(jo11.getColoredString());
                            if (integer10 > integer6) {
                                return jo11;
                            }
                            continue;
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    
    public static List<String> convertPages(final CompoundTag id) {
        final ListTag ik2 = id.getList("pages", 8).copy();
        final ImmutableList.Builder<String> builder3 = (ImmutableList.Builder<String>)ImmutableList.builder();
        for (int integer4 = 0; integer4 < ik2.size(); ++integer4) {
            builder3.add(ik2.getString(integer4));
        }
        return (List<String>)builder3.build();
    }
    
    static {
        EMPTY_ACCESS = new BookAccess() {
            public int getPageCount() {
                return 0;
            }
            
            public Component getPageRaw(final int integer) {
                return new TextComponent("");
            }
        };
        BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
    }
    
    public interface BookAccess {
        int getPageCount();
        
        Component getPageRaw(final int integer);
        
        default Component getPage(final int integer) {
            if (integer >= 0 && integer < this.getPageCount()) {
                return this.getPageRaw(integer);
            }
            return new TextComponent("");
        }
        
        default BookAccess fromItem(final ItemStack bcj) {
            final Item bce2 = bcj.getItem();
            if (bce2 == Items.WRITTEN_BOOK) {
                return new WrittenBookAccess(bcj);
            }
            if (bce2 == Items.WRITABLE_BOOK) {
                return new WritableBookAccess(bcj);
            }
            return BookViewScreen.EMPTY_ACCESS;
        }
    }
    
    public static class WrittenBookAccess implements BookAccess {
        private final List<String> pages;
        
        public WrittenBookAccess(final ItemStack bcj) {
            this.pages = readPages(bcj);
        }
        
        private static List<String> readPages(final ItemStack bcj) {
            final CompoundTag id2 = bcj.getTag();
            if (id2 != null && WrittenBookItem.makeSureTagIsValid(id2)) {
                return BookViewScreen.convertPages(id2);
            }
            return (List<String>)ImmutableList.of(new TranslatableComponent("book.invalid.tag", new Object[0]).withStyle(ChatFormatting.DARK_RED).getColoredString());
        }
        
        public int getPageCount() {
            return this.pages.size();
        }
        
        public Component getPageRaw(final int integer) {
            final String string3 = (String)this.pages.get(integer);
            try {
                final Component jo4 = Component.Serializer.fromJson(string3);
                if (jo4 != null) {
                    return jo4;
                }
            }
            catch (Exception ex) {}
            return new TextComponent(string3);
        }
    }
    
    public static class WritableBookAccess implements BookAccess {
        private final List<String> pages;
        
        public WritableBookAccess(final ItemStack bcj) {
            this.pages = readPages(bcj);
        }
        
        private static List<String> readPages(final ItemStack bcj) {
            final CompoundTag id2 = bcj.getTag();
            return (List<String>)((id2 != null) ? BookViewScreen.convertPages(id2) : ImmutableList.of());
        }
        
        public int getPageCount() {
            return this.pages.size();
        }
        
        public Component getPageRaw(final int integer) {
            return new TextComponent((String)this.pages.get(integer));
        }
    }
}

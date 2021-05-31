package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.RealmListEntry;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.realms.RealmsObjectSelectionList;
import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.Iterator;
import com.mojang.datafixers.util.Either;
import java.util.function.Supplier;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.Realms;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Collection;
import java.util.ArrayList;
import javax.annotation.Nullable;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.List;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.WorldTemplate;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreenWithCallback<WorldTemplate> lastScreen;
    private WorldTemplateObjectSelectionList worldTemplateObjectSelectionList;
    private int selectedTemplate;
    private String title;
    private RealmsButton selectButton;
    private RealmsButton trailerButton;
    private RealmsButton publisherButton;
    private String toolTip;
    private String currentLink;
    private final RealmsServer.WorldType worldType;
    private int clicks;
    private String warning;
    private String warningURL;
    private boolean displayWarning;
    private boolean hoverWarning;
    private List<TextRenderingUtils.Line> noTemplatesMessage;
    
    public RealmsSelectWorldTemplateScreen(final RealmsScreenWithCallback<WorldTemplate> cww, final RealmsServer.WorldType c) {
        this(cww, c, null);
    }
    
    public RealmsSelectWorldTemplateScreen(final RealmsScreenWithCallback<WorldTemplate> cww, final RealmsServer.WorldType c, @Nullable final WorldTemplatePaginatedList worldTemplatePaginatedList) {
        this.selectedTemplate = -1;
        this.lastScreen = cww;
        this.worldType = c;
        if (worldTemplatePaginatedList == null) {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList();
            this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
        }
        else {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList((Iterable<WorldTemplate>)new ArrayList((Collection)worldTemplatePaginatedList.templates));
            this.fetchTemplatesAsync(worldTemplatePaginatedList);
        }
        this.title = RealmsScreen.getLocalizedString("mco.template.title");
    }
    
    public void setTitle(final String string) {
        this.title = string;
    }
    
    public void setWarning(final String string) {
        this.warning = string;
        this.displayWarning = true;
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.hoverWarning && this.warningURL != null) {
            RealmsUtil.browseTo("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList((Iterable<WorldTemplate>)this.worldTemplateObjectSelectionList.getTemplates());
        this.buttonsAdd(this.trailerButton = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("mco.template.button.trailer")) {
            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.onTrailer();
            }
        });
        this.buttonsAdd(this.selectButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("mco.template.button.select")) {
            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.selectTemplate();
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 6, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString((this.worldType == RealmsServer.WorldType.MINIGAME) ? "gui.cancel" : "gui.back")) {
            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.backButtonClicked();
            }
        });
        this.buttonsAdd(this.publisherButton = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, RealmsScreen.getLocalizedString("mco.template.button.publisher")) {
            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.onPublish();
            }
        });
        this.selectButton.active(false);
        this.trailerButton.setVisible(false);
        this.publisherButton.setVisible(false);
        this.addWidget(this.worldTemplateObjectSelectionList);
        this.focusOn(this.worldTemplateObjectSelectionList);
        Realms.narrateNow((Iterable<String>)Stream.of((Object[])new String[] { this.title, this.warning }).filter(Objects::nonNull).collect(Collectors.toList()));
    }
    
    private void updateButtonStates() {
        this.publisherButton.setVisible(this.shouldPublisherBeVisible());
        this.trailerButton.setVisible(this.shouldTrailerBeVisible());
        this.selectButton.active(this.shouldSelectButtonBeActive());
    }
    
    private boolean shouldSelectButtonBeActive() {
        return this.selectedTemplate != -1;
    }
    
    private boolean shouldPublisherBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
    }
    
    private WorldTemplate getSelectedTemplate() {
        return this.worldTemplateObjectSelectionList.get(this.selectedTemplate);
    }
    
    private boolean shouldTrailerBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
    }
    
    @Override
    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 256: {
                this.backButtonClicked();
                return true;
            }
            default: {
                return super.keyPressed(integer1, integer2, integer3);
            }
        }
    }
    
    private void backButtonClicked() {
        this.lastScreen.callback(null);
        Realms.setScreen(this.lastScreen);
    }
    
    private void selectTemplate() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
            final WorldTemplate worldTemplate2 = this.getSelectedTemplate();
            this.lastScreen.callback(worldTemplate2);
        }
    }
    
    private void onTrailer() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
            final WorldTemplate worldTemplate2 = this.getSelectedTemplate();
            if (!"".equals(worldTemplate2.trailer)) {
                RealmsUtil.browseTo(worldTemplate2.trailer);
            }
        }
    }
    
    private void onPublish() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
            final WorldTemplate worldTemplate2 = this.getSelectedTemplate();
            if (!"".equals(worldTemplate2.link)) {
                RealmsUtil.browseTo(worldTemplate2.link);
            }
        }
    }
    
    private void fetchTemplatesAsync(final WorldTemplatePaginatedList worldTemplatePaginatedList) {
        new Thread("realms-template-fetcher") {
            public void run() {
                WorldTemplatePaginatedList worldTemplatePaginatedList2 = worldTemplatePaginatedList;
                final RealmsClient cvm3 = RealmsClient.createRealmsClient();
                while (worldTemplatePaginatedList2 != null) {
                    final Either<WorldTemplatePaginatedList, String> either4 = RealmsSelectWorldTemplateScreen.this.fetchTemplates(worldTemplatePaginatedList2, cvm3);
                    worldTemplatePaginatedList2 = (WorldTemplatePaginatedList)Realms.execute((java.util.function.Supplier<Object>)(() -> {
                        if (either4.right().isPresent()) {
                            RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", either4.right().get());
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(RealmsScreen.getLocalizedString("mco.template.select.failure"));
                            }
                            return null;
                        }
                        assert either4.left().isPresent();
                        final WorldTemplatePaginatedList worldTemplatePaginatedList3 = (WorldTemplatePaginatedList)either4.left().get();
                        for (final WorldTemplate worldTemplate5 : worldTemplatePaginatedList3.templates) {
                            RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry(worldTemplate5);
                        }
                        if (worldTemplatePaginatedList3.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                                final String string4 = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
                                final TextRenderingUtils.LineSegment b5 = TextRenderingUtils.LineSegment.link(RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/");
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string4, b5);
                            }
                            return null;
                        }
                        return worldTemplatePaginatedList3;
                    })).join();
                }
            }
        }.start();
    }
    
    private Either<WorldTemplatePaginatedList, String> fetchTemplates(final WorldTemplatePaginatedList worldTemplatePaginatedList, final RealmsClient cvm) {
        try {
            return (Either<WorldTemplatePaginatedList, String>)Either.left(cvm.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
        }
        catch (RealmsServiceException cvu4) {
            return (Either<WorldTemplatePaginatedList, String>)Either.right(cvu4.getMessage());
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.currentLink = null;
        this.hoverWarning = false;
        this.renderBackground();
        this.worldTemplateObjectSelectionList.render(integer1, integer2, float3);
        if (this.noTemplatesMessage != null) {
            this.renderMultilineMessage(integer1, integer2, this.noTemplatesMessage);
        }
        this.drawCenteredString(this.title, this.width() / 2, 13, 16777215);
        if (this.displayWarning) {
            final String[] arr5 = this.warning.split("\\\\n");
            for (int integer3 = 0; integer3 < arr5.length; ++integer3) {
                final int integer4 = this.fontWidth(arr5[integer3]);
                final int integer5 = this.width() / 2 - integer4 / 2;
                final int integer6 = RealmsConstants.row(-1 + integer3);
                if (integer1 >= integer5 && integer1 <= integer5 + integer4 && integer2 >= integer6 && integer2 <= integer6 + this.fontLineHeight()) {
                    this.hoverWarning = true;
                }
            }
            for (int integer3 = 0; integer3 < arr5.length; ++integer3) {
                String string7 = arr5[integer3];
                int integer5 = 10526880;
                if (this.warningURL != null) {
                    if (this.hoverWarning) {
                        integer5 = 7107012;
                        string7 = "§n" + string7;
                    }
                    else {
                        integer5 = 3368635;
                    }
                }
                this.drawCenteredString(string7, this.width() / 2, RealmsConstants.row(-1 + integer3), integer5);
            }
        }
        super.render(integer1, integer2, float3);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
    }
    
    private void renderMultilineMessage(final int integer1, final int integer2, final List<TextRenderingUtils.Line> list) {
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final TextRenderingUtils.Line a6 = (TextRenderingUtils.Line)list.get(integer3);
            final int integer4 = RealmsConstants.row(4 + integer3);
            final int integer5 = a6.segments.stream().mapToInt(b -> this.fontWidth(b.renderedText())).sum();
            int integer6 = this.width() / 2 - integer5 / 2;
            for (final TextRenderingUtils.LineSegment b11 : a6.segments) {
                final int integer7 = b11.isLink() ? 3368635 : 16777215;
                final int integer8 = this.draw(b11.renderedText(), integer6, integer4, integer7, true);
                if (b11.isLink() && integer1 > integer6 && integer1 < integer8 && integer2 > integer4 - 3 && integer2 < integer4 + 8) {
                    this.toolTip = b11.getLinkUrl();
                    this.currentLink = b11.getLinkUrl();
                }
                integer6 = integer8;
            }
        }
    }
    
    protected void renderMousehoverTooltip(final String string, final int integer2, final int integer3) {
        if (string == null) {
            return;
        }
        final int integer4 = integer2 + 12;
        final int integer5 = integer3 - 12;
        final int integer6 = this.fontWidth(string);
        this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, integer4, integer5, 16777215);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<WorldTemplateObjectSelectionListEntry> {
        public WorldTemplateObjectSelectionList(final RealmsSelectWorldTemplateScreen cwy) {
            this(cwy, (Iterable<WorldTemplate>)Collections.emptyList());
        }
        
        public WorldTemplateObjectSelectionList(final Iterable<WorldTemplate> iterable) {
            super(RealmsSelectWorldTemplateScreen.this.width(), RealmsSelectWorldTemplateScreen.this.height(), RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsConstants.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height() - 40, 46);
            iterable.forEach(this::addEntry);
        }
        
        public void addEntry(final WorldTemplate worldTemplate) {
            this.addEntry(new WorldTemplateObjectSelectionListEntry(worldTemplate));
        }
        
        @Override
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            if (integer == 0 && double2 >= this.y0() && double2 <= this.y1()) {
                final int integer2 = this.width() / 2 - 150;
                if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                    RealmsUtil.browseTo(RealmsSelectWorldTemplateScreen.this.currentLink);
                }
                final int integer3 = (int)Math.floor(double2 - this.y0()) - this.headerHeight() + this.getScroll() - 4;
                final int integer4 = integer3 / this.itemHeight();
                if (double1 >= integer2 && double1 < this.getScrollbarPosition() && integer4 >= 0 && integer3 >= 0 && integer4 < this.getItemCount()) {
                    this.selectItem(integer4);
                    this.itemClicked(integer3, integer4, double1, double2, this.width());
                    if (integer4 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                        return super.mouseClicked(double1, double2, integer);
                    }
                    RealmsSelectWorldTemplateScreen.this.selectedTemplate = integer4;
                    RealmsSelectWorldTemplateScreen.this.updateButtonStates();
                    RealmsSelectWorldTemplateScreen.this.clicks += 7;
                    if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                        RealmsSelectWorldTemplateScreen.this.selectTemplate();
                    }
                    return true;
                }
            }
            return super.mouseClicked(double1, double2, integer);
        }
        
        @Override
        public void selectItem(final int integer) {
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = integer;
            this.setSelected(integer);
            if (integer != -1) {
                final WorldTemplate worldTemplate3 = RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.get(integer);
                final String string4 = RealmsScreen.getLocalizedString("narrator.select.list.position", integer + 1, RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount());
                final String string5 = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", worldTemplate3.version);
                final String string6 = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", worldTemplate3.author);
                final String string7 = Realms.joinNarrations((Iterable<String>)Arrays.asList((Object[])new String[] { worldTemplate3.name, string6, worldTemplate3.recommendedPlayers, string5, string4 }));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string7));
            }
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }
        
        @Override
        public void itemClicked(final int integer1, final int integer2, final double double3, final double double4, final int integer5) {
            if (integer2 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                return;
            }
        }
        
        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }
        
        @Override
        public int getRowWidth() {
            return 300;
        }
        
        @Override
        public void renderBackground() {
            RealmsSelectWorldTemplateScreen.this.renderBackground();
        }
        
        @Override
        public boolean isFocused() {
            return RealmsSelectWorldTemplateScreen.this.isFocused(this);
        }
        
        public boolean isEmpty() {
            return this.getItemCount() == 0;
        }
        
        public WorldTemplate get(final int integer) {
            return ((WorldTemplateObjectSelectionListEntry)this.children().get(integer)).template;
        }
        
        public List<WorldTemplate> getTemplates() {
            return (List<WorldTemplate>)this.children().stream().map(b -> b.template).collect(Collectors.toList());
        }
    }
    
    class WorldTemplateObjectSelectionListEntry extends RealmListEntry {
        final WorldTemplate template;
        
        public WorldTemplateObjectSelectionListEntry(final WorldTemplate worldTemplate) {
            this.template = worldTemplate;
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.renderWorldTemplateItem(this.template, integer3, integer2, integer6, integer7);
        }
        
        private void renderWorldTemplateItem(final WorldTemplate worldTemplate, final int integer2, final int integer3, final int integer4, final int integer5) {
            final int integer6 = integer2 + 45 + 20;
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.name, integer6, integer3 + 2, 16777215);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.author, integer6, integer3 + 15, 7105644);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.version, integer6 + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(worldTemplate.version), integer3 + 1, 7105644);
            if (!"".equals(worldTemplate.link) || !"".equals(worldTemplate.trailer) || !"".equals(worldTemplate.recommendedPlayers)) {
                this.drawIcons(integer6 - 1, integer3 + 25, integer4, integer5, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
            }
            this.drawImage(integer2, integer3 + 1, integer4, integer5, worldTemplate);
        }
        
        private void drawImage(final int integer1, final int integer2, final int integer3, final int integer4, final WorldTemplate worldTemplate) {
            RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(integer1 + 1, integer2 + 1, 0.0f, 0.0f, 38, 38, 38, 38);
            RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 40, 40, 40, 40);
        }
        
        private void drawIcons(final int integer1, final int integer2, final int integer3, final int integer4, final String string5, final String string6, final String string7) {
            if (!"".equals(string7)) {
                RealmsSelectWorldTemplateScreen.this.drawString(string7, integer1, integer2 + 4, 5000268);
            }
            final int integer5 = "".equals(string7) ? 0 : (RealmsSelectWorldTemplateScreen.this.fontWidth(string7) + 2);
            boolean boolean10 = false;
            boolean boolean11 = false;
            if (integer3 >= integer1 + integer5 && integer3 <= integer1 + integer5 + 32 && integer4 >= integer2 && integer4 <= integer2 + 15 && integer4 < RealmsSelectWorldTemplateScreen.this.height() - 15 && integer4 > 32) {
                if (integer3 <= integer1 + 15 + integer5 && integer3 > integer5) {
                    if ("".equals(string5)) {
                        boolean11 = true;
                    }
                    else {
                        boolean10 = true;
                    }
                }
                else if (!"".equals(string5)) {
                    boolean11 = true;
                }
            }
            if (!"".equals(string5)) {
                RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(integer1 + integer5, integer2, boolean10 ? 15.0f : 0.0f, 0.0f, 15, 15, 30, 15);
                GlStateManager.popMatrix();
            }
            if (!"".equals(string6)) {
                RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(integer1 + integer5 + ("".equals(string5) ? 0 : 17), integer2, boolean11 ? 15.0f : 0.0f, 0.0f, 15, 15, 30, 15);
                GlStateManager.popMatrix();
            }
            if (boolean10 && !"".equals(string5)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = string5;
            }
            else if (boolean11 && !"".equals(string6)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = string6;
            }
        }
    }
}

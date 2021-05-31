package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.util.Mth;
import com.google.common.collect.Lists;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.Advancement;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiComponent;

public class AdvancementWidget extends GuiComponent {
    private static final ResourceLocation WIDGETS_LOCATION;
    private static final Pattern LAST_WORD;
    private final AdvancementTab tab;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final String title;
    private final int width;
    private final List<String> description;
    private final Minecraft minecraft;
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children;
    private AdvancementProgress progress;
    private final int x;
    private final int y;
    
    public AdvancementWidget(final AdvancementTab dcv, final Minecraft cyc, final Advancement q, final DisplayInfo z) {
        this.children = (List<AdvancementWidget>)Lists.newArrayList();
        this.tab = dcv;
        this.advancement = q;
        this.display = z;
        this.minecraft = cyc;
        this.title = cyc.font.substrByWidth(z.getTitle().getColoredString(), 163);
        this.x = Mth.floor(z.getX() * 28.0f);
        this.y = Mth.floor(z.getY() * 27.0f);
        final int integer6 = q.getMaxCriteraRequired();
        final int integer7 = String.valueOf(integer6).length();
        final int integer8 = (integer6 > 1) ? (cyc.font.width("  ") + cyc.font.width("0") * integer7 * 2 + cyc.font.width("/")) : 0;
        int integer9 = 29 + cyc.font.width(this.title) + integer8;
        final String string10 = z.getDescription().getColoredString();
        this.description = this.findOptimalLines(string10, integer9);
        for (final String string11 : this.description) {
            integer9 = Math.max(integer9, cyc.font.width(string11));
        }
        this.width = integer9 + 3 + 5;
    }
    
    private List<String> findOptimalLines(final String string, final int integer) {
        if (string.isEmpty()) {
            return (List<String>)Collections.emptyList();
        }
        final List<String> list4 = this.minecraft.font.split(string, integer);
        if (list4.size() < 2) {
            return list4;
        }
        final String string2 = (String)list4.get(0);
        final String string3 = (String)list4.get(1);
        final int integer2 = this.minecraft.font.width(string2 + ' ' + string3.split(" ")[0]);
        if (integer2 - integer <= 10) {
            return this.minecraft.font.split(string, integer2);
        }
        final Matcher matcher8 = AdvancementWidget.LAST_WORD.matcher((CharSequence)string2);
        if (matcher8.matches()) {
            final int integer3 = this.minecraft.font.width(matcher8.group(1));
            if (integer - integer3 <= 10) {
                return this.minecraft.font.split(string, integer3);
            }
        }
        return list4;
    }
    
    @Nullable
    private AdvancementWidget getFirstVisibleParent(Advancement q) {
        do {
            q = q.getParent();
        } while (q != null && q.getDisplay() == null);
        if (q == null || q.getDisplay() == null) {
            return null;
        }
        return this.tab.getWidget(q);
    }
    
    public void drawConnectivity(final int integer1, final int integer2, final boolean boolean3) {
        if (this.parent != null) {
            final int integer3 = integer1 + this.parent.x + 13;
            final int integer4 = integer1 + this.parent.x + 26 + 4;
            final int integer5 = integer2 + this.parent.y + 13;
            final int integer6 = integer1 + this.x + 13;
            final int integer7 = integer2 + this.y + 13;
            final int integer8 = boolean3 ? -16777216 : -1;
            if (boolean3) {
                this.hLine(integer4, integer3, integer5 - 1, integer8);
                this.hLine(integer4 + 1, integer3, integer5, integer8);
                this.hLine(integer4, integer3, integer5 + 1, integer8);
                this.hLine(integer6, integer4 - 1, integer7 - 1, integer8);
                this.hLine(integer6, integer4 - 1, integer7, integer8);
                this.hLine(integer6, integer4 - 1, integer7 + 1, integer8);
                this.vLine(integer4 - 1, integer7, integer5, integer8);
                this.vLine(integer4 + 1, integer7, integer5, integer8);
            }
            else {
                this.hLine(integer4, integer3, integer5, integer8);
                this.hLine(integer6, integer4, integer7, integer8);
                this.vLine(integer4, integer7, integer5, integer8);
            }
        }
        for (final AdvancementWidget dcx6 : this.children) {
            dcx6.drawConnectivity(integer1, integer2, boolean3);
        }
    }
    
    public void draw(final int integer1, final int integer2) {
        if (!this.display.isHidden() || (this.progress != null && this.progress.isDone())) {
            final float float4 = (this.progress == null) ? 0.0f : this.progress.getPercent();
            AdvancementWidgetType dcy5;
            if (float4 >= 1.0f) {
                dcy5 = AdvancementWidgetType.OBTAINED;
            }
            else {
                dcy5 = AdvancementWidgetType.UNOBTAINED;
            }
            this.minecraft.getTextureManager().bind(AdvancementWidget.WIDGETS_LOCATION);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            this.blit(integer1 + this.x + 3, integer2 + this.y, this.display.getFrame().getTexture(), 128 + dcy5.getIndex() * 26, 26, 26);
            Lighting.turnOnGui();
            this.minecraft.getItemRenderer().renderAndDecorateItem(null, this.display.getIcon(), integer1 + this.x + 8, integer2 + this.y + 5);
        }
        for (final AdvancementWidget dcx5 : this.children) {
            dcx5.draw(integer1, integer2);
        }
    }
    
    public void setProgress(final AdvancementProgress s) {
        this.progress = s;
    }
    
    public void addChild(final AdvancementWidget dcx) {
        this.children.add(dcx);
    }
    
    public void drawHover(final int integer1, final int integer2, final float float3, final int integer4, final int integer5) {
        final boolean boolean7 = integer4 + integer1 + this.x + this.width + 26 >= this.tab.getScreen().width;
        final String string8 = (this.progress == null) ? null : this.progress.getProgressText();
        final int integer6 = (string8 == null) ? 0 : this.minecraft.font.width(string8);
        final int n = 113 - integer2 - this.y - 26;
        final int n2 = 6;
        final int size = this.description.size();
        this.minecraft.font.getClass();
        final boolean boolean8 = n <= n2 + size * 9;
        final float float4 = (this.progress == null) ? 0.0f : this.progress.getPercent();
        int integer7 = Mth.floor(float4 * this.width);
        AdvancementWidgetType dcy12;
        AdvancementWidgetType dcy13;
        AdvancementWidgetType dcy14;
        if (float4 >= 1.0f) {
            integer7 = this.width / 2;
            dcy12 = AdvancementWidgetType.OBTAINED;
            dcy13 = AdvancementWidgetType.OBTAINED;
            dcy14 = AdvancementWidgetType.OBTAINED;
        }
        else if (integer7 < 2) {
            integer7 = this.width / 2;
            dcy12 = AdvancementWidgetType.UNOBTAINED;
            dcy13 = AdvancementWidgetType.UNOBTAINED;
            dcy14 = AdvancementWidgetType.UNOBTAINED;
        }
        else if (integer7 > this.width - 2) {
            integer7 = this.width / 2;
            dcy12 = AdvancementWidgetType.OBTAINED;
            dcy13 = AdvancementWidgetType.OBTAINED;
            dcy14 = AdvancementWidgetType.UNOBTAINED;
        }
        else {
            dcy12 = AdvancementWidgetType.OBTAINED;
            dcy13 = AdvancementWidgetType.UNOBTAINED;
            dcy14 = AdvancementWidgetType.UNOBTAINED;
        }
        final int integer8 = this.width - integer7;
        this.minecraft.getTextureManager().bind(AdvancementWidget.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        final int integer9 = integer2 + this.y;
        int integer10;
        if (boolean7) {
            integer10 = integer1 + this.x - this.width + 26 + 6;
        }
        else {
            integer10 = integer1 + this.x;
        }
        final int n3 = 32;
        final int size2 = this.description.size();
        this.minecraft.font.getClass();
        final int integer11 = n3 + size2 * 9;
        if (!this.description.isEmpty()) {
            if (boolean8) {
                this.render9Sprite(integer10, integer9 + 26 - integer11, this.width, integer11, 10, 200, 26, 0, 52);
            }
            else {
                this.render9Sprite(integer10, integer9, this.width, integer11, 10, 200, 26, 0, 52);
            }
        }
        this.blit(integer10, integer9, 0, dcy12.getIndex() * 26, integer7, 26);
        this.blit(integer10 + integer7, integer9, 200 - integer8, dcy13.getIndex() * 26, integer8, 26);
        this.blit(integer1 + this.x + 3, integer2 + this.y, this.display.getFrame().getTexture(), 128 + dcy14.getIndex() * 26, 26, 26);
        if (boolean7) {
            this.minecraft.font.drawShadow(this.title, (float)(integer10 + 5), (float)(integer2 + this.y + 9), -1);
            if (string8 != null) {
                this.minecraft.font.drawShadow(string8, (float)(integer1 + this.x - integer6), (float)(integer2 + this.y + 9), -1);
            }
        }
        else {
            this.minecraft.font.drawShadow(this.title, (float)(integer1 + this.x + 32), (float)(integer2 + this.y + 9), -1);
            if (string8 != null) {
                this.minecraft.font.drawShadow(string8, (float)(integer1 + this.x + this.width - integer6 - 5), (float)(integer2 + this.y + 9), -1);
            }
        }
        if (boolean8) {
            for (int integer12 = 0; integer12 < this.description.size(); ++integer12) {
                final Font font = this.minecraft.font;
                final String string9 = (String)this.description.get(integer12);
                final float float5 = (float)(integer10 + 5);
                final int n4 = integer9 + 26 - integer11 + 7;
                final int n5 = integer12;
                this.minecraft.font.getClass();
                font.draw(string9, float5, (float)(n4 + n5 * 9), -5592406);
            }
        }
        else {
            for (int integer12 = 0; integer12 < this.description.size(); ++integer12) {
                final Font font2 = this.minecraft.font;
                final String string10 = (String)this.description.get(integer12);
                final float float6 = (float)(integer10 + 5);
                final int n6 = integer2 + this.y + 9 + 17;
                final int n7 = integer12;
                this.minecraft.font.getClass();
                font2.draw(string10, float6, (float)(n6 + n7 * 9), -5592406);
            }
        }
        Lighting.turnOnGui();
        this.minecraft.getItemRenderer().renderAndDecorateItem(null, this.display.getIcon(), integer1 + this.x + 8, integer2 + this.y + 5);
    }
    
    protected void render9Sprite(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        this.blit(integer1, integer2, integer8, integer9, integer5, integer5);
        this.renderRepeating(integer1 + integer5, integer2, integer3 - integer5 - integer5, integer5, integer8 + integer5, integer9, integer6 - integer5 - integer5, integer7);
        this.blit(integer1 + integer3 - integer5, integer2, integer8 + integer6 - integer5, integer9, integer5, integer5);
        this.blit(integer1, integer2 + integer4 - integer5, integer8, integer9 + integer7 - integer5, integer5, integer5);
        this.renderRepeating(integer1 + integer5, integer2 + integer4 - integer5, integer3 - integer5 - integer5, integer5, integer8 + integer5, integer9 + integer7 - integer5, integer6 - integer5 - integer5, integer7);
        this.blit(integer1 + integer3 - integer5, integer2 + integer4 - integer5, integer8 + integer6 - integer5, integer9 + integer7 - integer5, integer5, integer5);
        this.renderRepeating(integer1, integer2 + integer5, integer5, integer4 - integer5 - integer5, integer8, integer9 + integer5, integer6, integer7 - integer5 - integer5);
        this.renderRepeating(integer1 + integer5, integer2 + integer5, integer3 - integer5 - integer5, integer4 - integer5 - integer5, integer8 + integer5, integer9 + integer5, integer6 - integer5 - integer5, integer7 - integer5 - integer5);
        this.renderRepeating(integer1 + integer3 - integer5, integer2 + integer5, integer5, integer4 - integer5 - integer5, integer8 + integer6 - integer5, integer9 + integer5, integer6, integer7 - integer5 - integer5);
    }
    
    protected void renderRepeating(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        for (int integer9 = 0; integer9 < integer3; integer9 += integer7) {
            final int integer10 = integer1 + integer9;
            final int integer11 = Math.min(integer7, integer3 - integer9);
            for (int integer12 = 0; integer12 < integer4; integer12 += integer8) {
                final int integer13 = integer2 + integer12;
                final int integer14 = Math.min(integer8, integer4 - integer12);
                this.blit(integer10, integer13, integer5, integer6, integer11, integer14);
            }
        }
    }
    
    public boolean isMouseOver(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        final int integer5 = integer1 + this.x;
        final int integer6 = integer5 + 26;
        final int integer7 = integer2 + this.y;
        final int integer8 = integer7 + 26;
        return integer3 >= integer5 && integer3 <= integer6 && integer4 >= integer7 && integer4 <= integer8;
    }
    
    public void attachToParent() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getFirstVisibleParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getX() {
        return this.x;
    }
    
    static {
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
        LAST_WORD = Pattern.compile("(.+) \\S+");
    }
}

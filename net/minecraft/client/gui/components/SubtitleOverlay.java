package net.minecraft.client.gui.components;

import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.resources.sounds.SoundInstance;
import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.gui.GuiComponent;

public class SubtitleOverlay extends GuiComponent implements SoundEventListener {
    private final Minecraft minecraft;
    private final List<Subtitle> subtitles;
    private boolean isListening;
    
    public SubtitleOverlay(final Minecraft cyc) {
        this.subtitles = (List<Subtitle>)Lists.newArrayList();
        this.minecraft = cyc;
    }
    
    public void render() {
        if (!this.isListening && this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().addListener(this);
            this.isListening = true;
        }
        else if (this.isListening && !this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().removeListener(this);
            this.isListening = false;
        }
        if (!this.isListening || this.subtitles.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final Vec3 csi2 = new Vec3(this.minecraft.player.x, this.minecraft.player.y + this.minecraft.player.getEyeHeight(), this.minecraft.player.z);
        final Vec3 csi3 = new Vec3(0.0, 0.0, -1.0).xRot(-this.minecraft.player.xRot * 0.017453292f).yRot(-this.minecraft.player.yRot * 0.017453292f);
        final Vec3 csi4 = new Vec3(0.0, 1.0, 0.0).xRot(-this.minecraft.player.xRot * 0.017453292f).yRot(-this.minecraft.player.yRot * 0.017453292f);
        final Vec3 csi5 = csi3.cross(csi4);
        int integer6 = 0;
        int integer7 = 0;
        final Iterator<Subtitle> iterator8 = (Iterator<Subtitle>)this.subtitles.iterator();
        while (iterator8.hasNext()) {
            final Subtitle a9 = (Subtitle)iterator8.next();
            if (a9.getTime() + 3000L <= Util.getMillis()) {
                iterator8.remove();
            }
            else {
                integer7 = Math.max(integer7, this.minecraft.font.width(a9.getText()));
            }
        }
        integer7 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
        final Iterator iterator9 = this.subtitles.iterator();
        while (iterator9.hasNext()) {
            final Subtitle a9 = (Subtitle)iterator9.next();
            final int integer8 = 255;
            final String string11 = a9.getText();
            final Vec3 csi6 = a9.getLocation().subtract(csi2).normalize();
            final double double13 = -csi5.dot(csi6);
            final double double14 = -csi3.dot(csi6);
            final boolean boolean17 = double14 > 0.5;
            final int integer9 = integer7 / 2;
            this.minecraft.font.getClass();
            final int integer10 = 9;
            final int integer11 = integer10 / 2;
            final float float21 = 1.0f;
            final int integer12 = this.minecraft.font.width(string11);
            final int integer13 = Mth.floor(Mth.clampedLerp(255.0, 75.0, (Util.getMillis() - a9.getTime()) / 3000.0f));
            final int integer14 = integer13 << 16 | integer13 << 8 | integer13;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.minecraft.window.getGuiScaledWidth() - integer9 * 1.0f - 2.0f, this.minecraft.window.getGuiScaledHeight() - 30 - integer6 * (integer10 + 1) * 1.0f, 0.0f);
            GlStateManager.scalef(1.0f, 1.0f, 1.0f);
            GuiComponent.fill(-integer9 - 1, -integer11 - 1, integer9 + 1, integer11 + 1, this.minecraft.options.getBackgroundColor(0.8f));
            GlStateManager.enableBlend();
            if (!boolean17) {
                if (double13 > 0.0) {
                    this.minecraft.font.draw(">", (float)(integer9 - this.minecraft.font.width(">")), (float)(-integer11), integer14 - 16777216);
                }
                else if (double13 < 0.0) {
                    this.minecraft.font.draw("<", (float)(-integer9), (float)(-integer11), integer14 - 16777216);
                }
            }
            this.minecraft.font.draw(string11, (float)(-integer12 / 2), (float)(-integer11), integer14 - 16777216);
            GlStateManager.popMatrix();
            ++integer6;
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    @Override
    public void onPlaySound(final SoundInstance dzp, final WeighedSoundEvents eaq) {
        if (eaq.getSubtitle() == null) {
            return;
        }
        final String string4 = eaq.getSubtitle().getColoredString();
        if (!this.subtitles.isEmpty()) {
            for (final Subtitle a6 : this.subtitles) {
                if (a6.getText().equals(string4)) {
                    a6.refresh(new Vec3(dzp.getX(), dzp.getY(), dzp.getZ()));
                    return;
                }
            }
        }
        this.subtitles.add(new Subtitle(string4, new Vec3(dzp.getX(), dzp.getY(), dzp.getZ())));
    }
    
    public class Subtitle {
        private final String text;
        private long time;
        private Vec3 location;
        
        public Subtitle(final String string, final Vec3 csi) {
            this.text = string;
            this.location = csi;
            this.time = Util.getMillis();
        }
        
        public String getText() {
            return this.text;
        }
        
        public long getTime() {
            return this.time;
        }
        
        public Vec3 getLocation() {
            return this.location;
        }
        
        public void refresh(final Vec3 csi) {
            this.location = csi;
            this.time = Util.getMillis();
        }
    }
}

package net.minecraft.client.gui.components.toasts;

import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.advancements.FrameType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.Advancement;

public class AdvancementToast implements Toast {
    private final Advancement advancement;
    private boolean playedSound;
    
    public AdvancementToast(final Advancement q) {
        this.advancement = q;
    }
    
    public Visibility render(final ToastComponent dan, final long long2) {
        dan.getMinecraft().getTextureManager().bind(AdvancementToast.TEXTURE);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final DisplayInfo z5 = this.advancement.getDisplay();
        dan.blit(0, 0, 0, 0, 160, 32);
        if (z5 != null) {
            final List<String> list6 = dan.getMinecraft().font.split(z5.getTitle().getColoredString(), 125);
            final int integer7 = (z5.getFrame() == FrameType.CHALLENGE) ? 16746751 : 16776960;
            if (list6.size() == 1) {
                dan.getMinecraft().font.draw(I18n.get("advancements.toast." + z5.getFrame().getName()), 30.0f, 7.0f, integer7 | 0xFF000000);
                dan.getMinecraft().font.draw(z5.getTitle().getColoredString(), 30.0f, 18.0f, -1);
            }
            else {
                final int integer8 = 1500;
                final float float9 = 300.0f;
                if (long2 < 1500L) {
                    final int integer9 = Mth.floor(Mth.clamp((1500L - long2) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    dan.getMinecraft().font.draw(I18n.get("advancements.toast." + z5.getFrame().getName()), 30.0f, 11.0f, integer7 | integer9);
                }
                else {
                    final int integer9 = Mth.floor(Mth.clamp((long2 - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    final int n = 16;
                    final int size = list6.size();
                    dan.getMinecraft().font.getClass();
                    int integer10 = n - size * 9 / 2;
                    for (final String string13 : list6) {
                        dan.getMinecraft().font.draw(string13, 30.0f, (float)integer10, 0xFFFFFF | integer9);
                        final int n2 = integer10;
                        dan.getMinecraft().font.getClass();
                        integer10 = n2 + 9;
                    }
                }
            }
            if (!this.playedSound && long2 > 0L) {
                this.playedSound = true;
                if (z5.getFrame() == FrameType.CHALLENGE) {
                    dan.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            Lighting.turnOnGui();
            dan.getMinecraft().getItemRenderer().renderAndDecorateItem(null, z5.getIcon(), 8, 8);
            return (long2 >= 5000L) ? Visibility.HIDE : Visibility.SHOW;
        }
        return Visibility.HIDE;
    }
}

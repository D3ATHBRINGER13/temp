package net.minecraft.client.sounds;

import java.util.Iterator;
import net.minecraft.network.chat.TranslatableComponent;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;
import java.util.List;
import net.minecraft.client.resources.sounds.Sound;

public class WeighedSoundEvents implements Weighted<Sound> {
    private final List<Weighted<Sound>> list;
    private final Random random;
    private final ResourceLocation location;
    private final Component subtitle;
    
    public WeighedSoundEvents(final ResourceLocation qv, @Nullable final String string) {
        this.list = (List<Weighted<Sound>>)Lists.newArrayList();
        this.random = new Random();
        this.location = qv;
        this.subtitle = ((string == null) ? null : new TranslatableComponent(string, new Object[0]));
    }
    
    public int getWeight() {
        int integer2 = 0;
        for (final Weighted<Sound> ear4 : this.list) {
            integer2 += ear4.getWeight();
        }
        return integer2;
    }
    
    public Sound getSound() {
        final int integer2 = this.getWeight();
        if (this.list.isEmpty() || integer2 == 0) {
            return SoundManager.EMPTY_SOUND;
        }
        int integer3 = this.random.nextInt(integer2);
        for (final Weighted<Sound> ear5 : this.list) {
            integer3 -= ear5.getWeight();
            if (integer3 < 0) {
                return ear5.getSound();
            }
        }
        return SoundManager.EMPTY_SOUND;
    }
    
    public void addSound(final Weighted<Sound> ear) {
        this.list.add(ear);
    }
    
    @Nullable
    public Component getSubtitle() {
        return this.subtitle;
    }
    
    public void preloadIfRequired(final SoundEngine eam) {
        for (final Weighted<Sound> ear4 : this.list) {
            ear4.preloadIfRequired(eam);
        }
    }
}

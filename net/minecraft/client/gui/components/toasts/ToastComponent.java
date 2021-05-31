package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import java.util.Arrays;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.mojang.blaze3d.platform.Lighting;
import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public class ToastComponent extends GuiComponent {
    private final Minecraft minecraft;
    private final ToastInstance<?>[] visible;
    private final Deque<Toast> queued;
    
    public ToastComponent(final Minecraft cyc) {
        this.visible = new ToastInstance[5];
        this.queued = (Deque<Toast>)Queues.newArrayDeque();
        this.minecraft = cyc;
    }
    
    public void render() {
        if (this.minecraft.options.hideGui) {
            return;
        }
        Lighting.turnOff();
        for (int integer2 = 0; integer2 < this.visible.length; ++integer2) {
            final ToastInstance<?> a3 = this.visible[integer2];
            if (a3 != null && a3.render(this.minecraft.window.getGuiScaledWidth(), integer2)) {
                this.visible[integer2] = null;
            }
            if (this.visible[integer2] == null && !this.queued.isEmpty()) {
                this.visible[integer2] = new ToastInstance<>((Toast)this.queued.removeFirst());
            }
        }
    }
    
    @Nullable
    public <T extends Toast> T getToast(final Class<? extends T> class1, final Object object) {
        for (final ToastInstance<?> a7 : this.visible) {
            if (a7 != null && class1.isAssignableFrom((Class)a7.getToast().getClass()) && ((Toast)a7.getToast()).getToken().equals(object)) {
                return (T)a7.getToast();
            }
        }
        for (final Toast dam5 : this.queued) {
            if (class1.isAssignableFrom(dam5.getClass()) && dam5.getToken().equals(object)) {
                return (T)dam5;
            }
        }
        return null;
    }
    
    public void clear() {
        Arrays.fill((Object[])this.visible, null);
        this.queued.clear();
    }
    
    public void addToast(final Toast dam) {
        this.queued.add(dam);
    }
    
    public Minecraft getMinecraft() {
        return this.minecraft;
    }
    
    class ToastInstance<T extends Toast> {
        private final T toast;
        private long animationTime;
        private long visibleTime;
        private Toast.Visibility visibility;
        
        private ToastInstance(final T dam) {
            this.animationTime = -1L;
            this.visibleTime = -1L;
            this.visibility = Toast.Visibility.SHOW;
            this.toast = dam;
        }
        
        public T getToast() {
            return this.toast;
        }
        
        private float getVisibility(final long long1) {
            float float4 = Mth.clamp((long1 - this.animationTime) / 600.0f, 0.0f, 1.0f);
            float4 *= float4;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - float4;
            }
            return float4;
        }
        
        public boolean render(final int integer1, final int integer2) {
            final long long4 = Util.getMillis();
            if (this.animationTime == -1L) {
                this.animationTime = long4;
                this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && long4 - this.animationTime <= 600L) {
                this.visibleTime = long4;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(integer1 - 160.0f * this.getVisibility(long4), (float)(integer2 * 32), (float)(500 + integer2));
            final Toast.Visibility a6 = this.toast.render(ToastComponent.this, long4 - this.visibleTime);
            GlStateManager.popMatrix();
            if (a6 != this.visibility) {
                this.animationTime = long4 - (int)((1.0f - this.getVisibility(long4)) * 600.0f);
                (this.visibility = a6).playSound(ToastComponent.this.minecraft.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && long4 - this.animationTime > 600L;
        }
    }
}

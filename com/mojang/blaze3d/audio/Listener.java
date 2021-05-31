package com.mojang.blaze3d.audio;

import org.lwjgl.openal.AL10;
import net.minecraft.world.phys.Vec3;

public class Listener {
    public static final Vec3 UP;
    private float gain;
    
    public Listener() {
        this.gain = 1.0f;
    }
    
    public void setListenerPosition(final Vec3 csi) {
        AL10.alListener3f(4100, (float)csi.x, (float)csi.y, (float)csi.z);
    }
    
    public void setListenerOrientation(final Vec3 csi1, final Vec3 csi2) {
        AL10.alListenerfv(4111, new float[] { (float)csi1.x, (float)csi1.y, (float)csi1.z, (float)csi2.x, (float)csi2.y, (float)csi2.z });
    }
    
    public void setGain(final float float1) {
        AL10.alListenerf(4106, float1);
        this.gain = float1;
    }
    
    public float getGain() {
        return this.gain;
    }
    
    public void reset() {
        this.setListenerPosition(Vec3.ZERO);
        this.setListenerOrientation(new Vec3(0.0, 0.0, -1.0), Listener.UP);
    }
    
    static {
        UP = new Vec3(0.0, 1.0, 0.0);
    }
}

package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;

public interface HttpTextureProcessor {
    NativeImage process(final NativeImage cuj);
    
    void onTextureDownloaded();
}

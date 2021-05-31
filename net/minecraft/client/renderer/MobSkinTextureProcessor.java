package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;

public class MobSkinTextureProcessor implements HttpTextureProcessor {
    public NativeImage process(NativeImage cuj) {
        final boolean boolean3 = cuj.getHeight() == 32;
        if (boolean3) {
            final NativeImage cuj2 = new NativeImage(64, 64, true);
            cuj2.copyFrom(cuj);
            cuj.close();
            cuj = cuj2;
            cuj.fillRect(0, 32, 64, 32, 0);
            cuj.copyRect(4, 16, 16, 32, 4, 4, true, false);
            cuj.copyRect(8, 16, 16, 32, 4, 4, true, false);
            cuj.copyRect(0, 20, 24, 32, 4, 12, true, false);
            cuj.copyRect(4, 20, 16, 32, 4, 12, true, false);
            cuj.copyRect(8, 20, 8, 32, 4, 12, true, false);
            cuj.copyRect(12, 20, 16, 32, 4, 12, true, false);
            cuj.copyRect(44, 16, -8, 32, 4, 4, true, false);
            cuj.copyRect(48, 16, -8, 32, 4, 4, true, false);
            cuj.copyRect(40, 20, 0, 32, 4, 12, true, false);
            cuj.copyRect(44, 20, -8, 32, 4, 12, true, false);
            cuj.copyRect(48, 20, -16, 32, 4, 12, true, false);
            cuj.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        setNoAlpha(cuj, 0, 0, 32, 16);
        if (boolean3) {
            doLegacyTransparencyHack(cuj, 32, 0, 64, 32);
        }
        setNoAlpha(cuj, 0, 16, 64, 32);
        setNoAlpha(cuj, 16, 48, 48, 64);
        return cuj;
    }
    
    public void onTextureDownloaded() {
    }
    
    private static void doLegacyTransparencyHack(final NativeImage cuj, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                final int integer8 = cuj.getPixelRGBA(integer6, integer7);
                if ((integer8 >> 24 & 0xFF) < 128) {
                    return;
                }
            }
        }
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                cuj.setPixelRGBA(integer6, integer7, cuj.getPixelRGBA(integer6, integer7) & 0xFFFFFF);
            }
        }
    }
    
    private static void setNoAlpha(final NativeImage cuj, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = integer2; integer6 < integer4; ++integer6) {
            for (int integer7 = integer3; integer7 < integer5; ++integer7) {
                cuj.setPixelRGBA(integer6, integer7, cuj.getPixelRGBA(integer6, integer7) | 0xFF000000);
            }
        }
    }
}

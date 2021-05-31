package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Random;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.entity.global.LightningBolt;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt> {
    public LightningBoltRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final LightningBolt atu, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final Tesselator cuz11 = Tesselator.getInstance();
        final BufferBuilder cuw12 = cuz11.getBuilder();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        final double[] arr13 = new double[8];
        final double[] arr14 = new double[8];
        double double5 = 0.0;
        double double6 = 0.0;
        final Random random19 = new Random(atu.seed);
        for (int integer20 = 7; integer20 >= 0; --integer20) {
            arr13[integer20] = double5;
            arr14[integer20] = double6;
            double5 += random19.nextInt(11) - 5;
            double6 += random19.nextInt(11) - 5;
        }
        for (int integer21 = 0; integer21 < 4; ++integer21) {
            final Random random20 = new Random(atu.seed);
            for (int integer22 = 0; integer22 < 3; ++integer22) {
                int integer23 = 7;
                int integer24 = 0;
                if (integer22 > 0) {
                    integer23 = 7 - integer22;
                }
                if (integer22 > 0) {
                    integer24 = integer23 - 2;
                }
                double double7 = arr13[integer23] - double5;
                double double8 = arr14[integer23] - double6;
                for (int integer25 = integer23; integer25 >= integer24; --integer25) {
                    final double double9 = double7;
                    final double double10 = double8;
                    if (integer22 == 0) {
                        double7 += random20.nextInt(11) - 5;
                        double8 += random20.nextInt(11) - 5;
                    }
                    else {
                        double7 += random20.nextInt(31) - 15;
                        double8 += random20.nextInt(31) - 15;
                    }
                    cuw12.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    final float float7 = 0.5f;
                    final float float8 = 0.45f;
                    final float float9 = 0.45f;
                    final float float10 = 0.5f;
                    double double11 = 0.1 + integer21 * 0.2;
                    if (integer22 == 0) {
                        double11 *= integer25 * 0.1 + 1.0;
                    }
                    double double12 = 0.1 + integer21 * 0.2;
                    if (integer22 == 0) {
                        double12 *= (integer25 - 1) * 0.1 + 1.0;
                    }
                    for (int integer26 = 0; integer26 < 5; ++integer26) {
                        double double13 = double2 - double11;
                        double double14 = double4 - double11;
                        if (integer26 == 1 || integer26 == 2) {
                            double13 += double11 * 2.0;
                        }
                        if (integer26 == 2 || integer26 == 3) {
                            double14 += double11 * 2.0;
                        }
                        double double15 = double2 - double12;
                        double double16 = double4 - double12;
                        if (integer26 == 1 || integer26 == 2) {
                            double15 += double12 * 2.0;
                        }
                        if (integer26 == 2 || integer26 == 3) {
                            double16 += double12 * 2.0;
                        }
                        cuw12.vertex(double15 + double7, double3 + integer25 * 16, double16 + double8).color(0.45f, 0.45f, 0.5f, 0.3f).endVertex();
                        cuw12.vertex(double13 + double9, double3 + (integer25 + 1) * 16, double14 + double10).color(0.45f, 0.45f, 0.5f, 0.3f).endVertex();
                    }
                    cuz11.end();
                }
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
    }
    
    @Nullable
    @Override
    protected ResourceLocation getTextureLocation(final LightningBolt atu) {
        return null;
    }
}

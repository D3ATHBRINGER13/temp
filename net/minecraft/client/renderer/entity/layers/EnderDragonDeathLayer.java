package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonDeathLayer extends RenderLayer<EnderDragon, DragonModel> {
    public EnderDragonDeathLayer(final RenderLayerParent<EnderDragon, DragonModel> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final EnderDragon asp, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (asp.dragonDeathTime <= 0) {
            return;
        }
        final Tesselator cuz10 = Tesselator.getInstance();
        final BufferBuilder cuw11 = cuz10.getBuilder();
        Lighting.turnOff();
        final float float9 = (asp.dragonDeathTime + float4) / 200.0f;
        float float10 = 0.0f;
        if (float9 > 0.8f) {
            float10 = (float9 - 0.8f) / 0.2f;
        }
        final Random random14 = new Random(432L);
        GlStateManager.disableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -1.0f, -2.0f);
        for (int integer15 = 0; integer15 < (float9 + float9 * float9) / 2.0f * 60.0f; ++integer15) {
            GlStateManager.rotatef(random14.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random14.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random14.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(random14.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(random14.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(random14.nextFloat() * 360.0f + float9 * 90.0f, 0.0f, 0.0f, 1.0f);
            final float float11 = random14.nextFloat() * 20.0f + 5.0f + float10 * 10.0f;
            final float float12 = random14.nextFloat() * 2.0f + 1.0f + float10 * 2.0f;
            cuw11.begin(6, DefaultVertexFormat.POSITION_COLOR);
            cuw11.vertex(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - float10))).endVertex();
            cuw11.vertex(-0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).endVertex();
            cuw11.vertex(0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).endVertex();
            cuw11.vertex(0.0, float11, 1.0f * float12).color(255, 0, 255, 0).endVertex();
            cuw11.vertex(-0.866 * float12, float11, -0.5f * float12).color(255, 0, 255, 0).endVertex();
            cuz10.end();
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.enableAlphaTest();
        Lighting.turnOn();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}

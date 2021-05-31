package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BaseSpawner;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer extends BlockEntityRenderer<SpawnerBlockEntity> {
    @Override
    public void render(final SpawnerBlockEntity buv, final double double2, final double double3, final double double4, final float float5, final int integer) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2 + 0.5f, (float)double3, (float)double4 + 0.5f);
        render(buv.getSpawner(), double2, double3, double4, float5);
        GlStateManager.popMatrix();
    }
    
    public static void render(final BaseSpawner bgy, final double double2, final double double3, final double double4, final float float5) {
        final Entity aio9 = bgy.getOrCreateDisplayEntity();
        if (aio9 != null) {
            float float6 = 0.53125f;
            final float float7 = Math.max(aio9.getBbWidth(), aio9.getBbHeight());
            if (float7 > 1.0) {
                float6 /= float7;
            }
            GlStateManager.translatef(0.0f, 0.4f, 0.0f);
            GlStateManager.rotatef((float)Mth.lerp(float5, bgy.getoSpin(), bgy.getSpin()) * 10.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.2f, 0.0f);
            GlStateManager.rotatef(-30.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.scalef(float6, float6, float6);
            aio9.moveTo(double2, double3, double4, 0.0f, 0.0f);
            Minecraft.getInstance().getEntityRenderDispatcher().render(aio9, 0.0, 0.0, 0.0, 0.0f, float5, false);
        }
    }
}

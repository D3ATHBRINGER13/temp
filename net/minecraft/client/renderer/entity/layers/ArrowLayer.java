package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.Cube;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

public class ArrowLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final EntityRenderDispatcher dispatcher;
    
    public ArrowLayer(final LivingEntityRenderer<T, M> dsz) {
        super(dsz);
        this.dispatcher = dsz.getDispatcher();
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final int integer10 = aix.getArrowCount();
        if (integer10 <= 0) {
            return;
        }
        final Entity aio11 = new Arrow(aix.level, aix.x, aix.y, aix.z);
        final Random random12 = new Random((long)aix.getId());
        Lighting.turnOff();
        for (int integer11 = 0; integer11 < integer10; ++integer11) {
            GlStateManager.pushMatrix();
            final ModelPart djv14 = this.getParentModel().getRandomModelPart(random12);
            final Cube djt15 = (Cube)djv14.cubes.get(random12.nextInt(djv14.cubes.size()));
            djv14.translateTo(0.0625f);
            float float9 = random12.nextFloat();
            float float10 = random12.nextFloat();
            float float11 = random12.nextFloat();
            final float float12 = Mth.lerp(float9, djt15.minX, djt15.maxX) / 16.0f;
            final float float13 = Mth.lerp(float10, djt15.minY, djt15.maxY) / 16.0f;
            final float float14 = Mth.lerp(float11, djt15.minZ, djt15.maxZ) / 16.0f;
            GlStateManager.translatef(float12, float13, float14);
            float9 = float9 * 2.0f - 1.0f;
            float10 = float10 * 2.0f - 1.0f;
            float11 = float11 * 2.0f - 1.0f;
            float9 *= -1.0f;
            float10 *= -1.0f;
            float11 *= -1.0f;
            final float float15 = Mth.sqrt(float9 * float9 + float11 * float11);
            aio11.yRot = (float)(Math.atan2((double)float9, (double)float11) * 57.2957763671875);
            aio11.xRot = (float)(Math.atan2((double)float10, (double)float15) * 57.2957763671875);
            aio11.yRotO = aio11.yRot;
            aio11.xRotO = aio11.xRot;
            final double double23 = 0.0;
            final double double24 = 0.0;
            final double double25 = 0.0;
            this.dispatcher.render(aio11, 0.0, 0.0, 0.0, 0.0f, float4, false);
            GlStateManager.popMatrix();
        }
        Lighting.turnOn();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}

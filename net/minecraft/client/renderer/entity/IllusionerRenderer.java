package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Illusioner;

public class IllusionerRenderer extends IllagerRenderer<Illusioner> {
    private static final ResourceLocation ILLUSIONER;
    
    public IllusionerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new IllagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer((RenderLayer<T, IllagerModel<T>>)new ItemInHandLayer<Illusioner, IllagerModel<Illusioner>>(this) {
            @Override
            public void render(final Illusioner auq, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (auq.isCastingSpell() || auq.isAggressive()) {
                    super.render(auq, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
        ((IllagerModel)this.model).getHat().visible = true;
    }
    
    protected ResourceLocation getTextureLocation(final Illusioner auq) {
        return IllusionerRenderer.ILLUSIONER;
    }
    
    public void render(final Illusioner auq, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (auq.isInvisible()) {
            final Vec3[] arr11 = auq.getIllusionOffsets(float6);
            final float float7 = ((LivingEntityRenderer<T, M>)this).getBob((T)auq, float6);
            for (int integer13 = 0; integer13 < arr11.length; ++integer13) {
                super.render((T)auq, double2 + arr11[integer13].x + Mth.cos(integer13 + float7 * 0.5f) * 0.025, double3 + arr11[integer13].y + Mth.cos(integer13 + float7 * 0.75f) * 0.0125, double4 + arr11[integer13].z + Mth.cos(integer13 + float7 * 0.7f) * 0.025, float5, float6);
            }
        }
        else {
            super.render((T)auq, double2, double3, double4, float5, float6);
        }
    }
    
    protected boolean isVisible(final Illusioner auq) {
        return true;
    }
    
    static {
        ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");
    }
}

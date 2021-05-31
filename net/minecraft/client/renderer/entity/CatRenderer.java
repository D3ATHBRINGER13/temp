package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.model.CatModel;
import net.minecraft.world.entity.animal.Cat;

public class CatRenderer extends MobRenderer<Cat, CatModel<Cat>> {
    public CatRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new CatModel(0.0f), 0.4f);
        this.addLayer(new CatCollarLayer(this));
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Cat arb) {
        return arb.getResourceLocation();
    }
    
    @Override
    protected void scale(final Cat arb, final float float2) {
        super.scale(arb, float2);
        GlStateManager.scalef(0.8f, 0.8f, 0.8f);
    }
    
    @Override
    protected void setupRotations(final Cat arb, final float float2, final float float3, final float float4) {
        super.setupRotations(arb, float2, float3, float4);
        final float float5 = arb.getLieDownAmount(float4);
        if (float5 > 0.0f) {
            GlStateManager.translatef(0.4f * float5, 0.15f * float5, 0.1f * float5);
            GlStateManager.rotatef(Mth.rotLerp(float5, 0.0f, 90.0f), 0.0f, 0.0f, 1.0f);
            final BlockPos ew7 = new BlockPos(arb);
            final List<Player> list8 = arb.level.<Player>getEntitiesOfClass((java.lang.Class<? extends Player>)Player.class, new AABB(ew7).inflate(2.0, 2.0, 2.0));
            for (final Player awg10 : list8) {
                if (awg10.isSleeping()) {
                    GlStateManager.translatef(0.15f * float5, 0.0f, 0.0f);
                    break;
                }
            }
        }
    }
}

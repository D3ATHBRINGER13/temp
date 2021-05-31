package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION;
    private static final ResourceLocation WITHER_LOCATION;
    
    public WitherBossRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new WitherBossModel(0.0f), 1.0f);
        this.addLayer(new WitherArmorLayer(this));
    }
    
    protected ResourceLocation getTextureLocation(final WitherBoss atj) {
        final int integer3 = atj.getInvulnerableTicks();
        if (integer3 <= 0 || (integer3 <= 80 && integer3 / 5 % 2 == 1)) {
            return WitherBossRenderer.WITHER_LOCATION;
        }
        return WitherBossRenderer.WITHER_INVULNERABLE_LOCATION;
    }
    
    @Override
    protected void scale(final WitherBoss atj, final float float2) {
        float float3 = 2.0f;
        final int integer5 = atj.getInvulnerableTicks();
        if (integer5 > 0) {
            float3 -= (integer5 - float2) / 220.0f * 0.5f;
        }
        GlStateManager.scalef(float3, float3, float3);
    }
    
    static {
        WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
        WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");
    }
}

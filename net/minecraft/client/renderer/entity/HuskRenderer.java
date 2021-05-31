package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.resources.ResourceLocation;

public class HuskRenderer extends ZombieRenderer {
    private static final ResourceLocation HUSK_LOCATION;
    
    public HuskRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    protected void scale(final Zombie avm, final float float2) {
        final float float3 = 1.0625f;
        GlStateManager.scalef(1.0625f, 1.0625f, 1.0625f);
        super.scale(avm, float2);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Zombie avm) {
        return HuskRenderer.HUSK_LOCATION;
    }
    
    static {
        HUSK_LOCATION = new ResourceLocation("textures/entity/zombie/husk.png");
    }
}

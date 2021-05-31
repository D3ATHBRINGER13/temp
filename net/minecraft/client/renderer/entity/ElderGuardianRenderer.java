package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.resources.ResourceLocation;

public class ElderGuardianRenderer extends GuardianRenderer {
    private static final ResourceLocation GUARDIAN_ELDER_LOCATION;
    
    public ElderGuardianRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, 1.2f);
    }
    
    @Override
    protected void scale(final Guardian auo, final float float2) {
        GlStateManager.scalef(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Guardian auo) {
        return ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION;
    }
    
    static {
        GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");
    }
}

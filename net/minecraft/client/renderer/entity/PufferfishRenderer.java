package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer extends MobRenderer<Pufferfish, EntityModel<Pufferfish>> {
    private static final ResourceLocation PUFFER_LOCATION;
    private int puffStateO;
    private final PufferfishSmallModel<Pufferfish> small;
    private final PufferfishMidModel<Pufferfish> mid;
    private final PufferfishBigModel<Pufferfish> big;
    
    public PufferfishRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new PufferfishBigModel(), 0.2f);
        this.small = new PufferfishSmallModel<Pufferfish>();
        this.mid = new PufferfishMidModel<Pufferfish>();
        this.big = new PufferfishBigModel<Pufferfish>();
        this.puffStateO = 3;
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Pufferfish arp) {
        return PufferfishRenderer.PUFFER_LOCATION;
    }
    
    @Override
    public void render(final Pufferfish arp, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final int integer11 = arp.getPuffState();
        if (integer11 != this.puffStateO) {
            if (integer11 == 0) {
                this.model = (M)this.small;
            }
            else if (integer11 == 1) {
                this.model = (M)this.mid;
            }
            else {
                this.model = (M)this.big;
            }
        }
        this.puffStateO = integer11;
        this.shadowRadius = 0.1f + 0.1f * integer11;
        super.render(arp, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected void setupRotations(final Pufferfish arp, final float float2, final float float3, final float float4) {
        GlStateManager.translatef(0.0f, Mth.cos(float2 * 0.05f) * 0.08f, 0.0f);
        super.setupRotations(arp, float2, float3, float4);
    }
    
    static {
        PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
    }
}

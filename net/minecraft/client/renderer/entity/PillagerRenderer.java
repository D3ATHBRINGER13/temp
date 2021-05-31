package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.PillagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

public class PillagerRenderer extends IllagerRenderer<Pillager> {
    private static final ResourceLocation PILLAGER;
    
    public PillagerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new PillagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer((RenderLayer<T, IllagerModel<T>>)new ItemInHandLayer<AbstractIllager, IllagerModel<T>>(this));
    }
    
    protected ResourceLocation getTextureLocation(final Pillager auw) {
        return PillagerRenderer.PILLAGER;
    }
    
    static {
        PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");
    }
}

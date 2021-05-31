package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaDecorLayer extends RenderLayer<Llama, LlamaModel<Llama>> {
    private static final ResourceLocation[] TEXTURE_LOCATION;
    private static final ResourceLocation TRADER_LLAMA;
    private final LlamaModel<Llama> model;
    
    public LlamaDecorLayer(final RenderLayerParent<Llama, LlamaModel<Llama>> dtr) {
        super(dtr);
        this.model = new LlamaModel<Llama>(0.5f);
    }
    
    @Override
    public void render(final Llama ase, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final DyeColor bbg10 = ase.getSwag();
        if (bbg10 != null) {
            this.bindTexture(LlamaDecorLayer.TEXTURE_LOCATION[bbg10.getId()]);
        }
        else {
            if (!ase.isTraderLlama()) {
                return;
            }
            this.bindTexture(LlamaDecorLayer.TRADER_LLAMA);
        }
        ((RenderLayer<T, LlamaModel<Llama>>)this).getParentModel().copyPropertiesTo(this.model);
        this.model.render(ase, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation[] { new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png") };
        TRADER_LLAMA = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
    }
}

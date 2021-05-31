package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class TridentModel extends Model {
    public static final ResourceLocation TEXTURE;
    private final ModelPart pole;
    
    public TridentModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        (this.pole = new ModelPart(this, 0, 0)).addBox(-0.5f, -4.0f, -0.5f, 1, 31, 1, 0.0f);
        final ModelPart djv2 = new ModelPart(this, 4, 0);
        djv2.addBox(-1.5f, 0.0f, -0.5f, 3, 2, 1);
        this.pole.addChild(djv2);
        final ModelPart djv3 = new ModelPart(this, 4, 3);
        djv3.addBox(-2.5f, -3.0f, -0.5f, 1, 4, 1);
        this.pole.addChild(djv3);
        final ModelPart djv4 = new ModelPart(this, 4, 3);
        djv4.mirror = true;
        djv4.addBox(1.5f, -3.0f, -0.5f, 1, 4, 1);
        this.pole.addChild(djv4);
    }
    
    public void render() {
        this.pole.render(0.0625f);
    }
    
    static {
        TEXTURE = new ResourceLocation("textures/entity/trident.png");
    }
}

package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class SignModel extends Model {
    private final ModelPart sign;
    private final ModelPart stick;
    
    public SignModel() {
        (this.sign = new ModelPart(this, 0, 0)).addBox(-12.0f, -14.0f, -1.0f, 24, 12, 2, 0.0f);
        (this.stick = new ModelPart(this, 0, 14)).addBox(-1.0f, -2.0f, -1.0f, 2, 14, 2, 0.0f);
    }
    
    public void render() {
        this.sign.render(0.0625f);
        this.stick.render(0.0625f);
    }
    
    public ModelPart getStick() {
        return this.stick;
    }
}

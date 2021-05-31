package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class ChestModel extends Model {
    protected ModelPart lid;
    protected ModelPart bottom;
    protected ModelPart lock;
    
    public ChestModel() {
        (this.lid = new ModelPart(this, 0, 0).setTexSize(64, 64)).addBox(0.0f, -5.0f, -14.0f, 14, 5, 14, 0.0f);
        this.lid.x = 1.0f;
        this.lid.y = 7.0f;
        this.lid.z = 15.0f;
        (this.lock = new ModelPart(this, 0, 0).setTexSize(64, 64)).addBox(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
        this.lock.x = 8.0f;
        this.lock.y = 7.0f;
        this.lock.z = 15.0f;
        (this.bottom = new ModelPart(this, 0, 19).setTexSize(64, 64)).addBox(0.0f, 0.0f, 0.0f, 14, 10, 14, 0.0f);
        this.bottom.x = 1.0f;
        this.bottom.y = 6.0f;
        this.bottom.z = 1.0f;
    }
    
    public void render() {
        this.lock.xRot = this.lid.xRot;
        this.lid.render(0.0625f);
        this.lock.render(0.0625f);
        this.bottom.render(0.0625f);
    }
    
    public ModelPart getLid() {
        return this.lid;
    }
}

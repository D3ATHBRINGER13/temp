package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;

public abstract class EntityModel<T extends Entity> extends Model {
    public float attackTime;
    public boolean riding;
    public boolean young;
    
    public EntityModel() {
        this.young = true;
    }
    
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
    }
    
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
    }
    
    public void prepareMobModel(final T aio, final float float2, final float float3, final float float4) {
    }
    
    public void copyPropertiesTo(final EntityModel<T> dhh) {
        dhh.attackTime = this.attackTime;
        dhh.riding = this.riding;
        dhh.young = this.young;
    }
}

package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.client.model.Model;
import java.util.List;

public class ModelPart {
    public float xTexSize;
    public float yTexSize;
    private int xTexOffs;
    private int yTexOffs;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    private boolean compiled;
    private int list;
    public boolean mirror;
    public boolean visible;
    public boolean neverRender;
    public final List<Cube> cubes;
    public List<ModelPart> children;
    public final String id;
    public float translateX;
    public float translateY;
    public float translateZ;
    
    public ModelPart(final Model dhy, final String string) {
        this.xTexSize = 64.0f;
        this.yTexSize = 32.0f;
        this.visible = true;
        this.cubes = (List<Cube>)Lists.newArrayList();
        dhy.cubes.add(this);
        this.id = string;
        this.setTexSize(dhy.texWidth, dhy.texHeight);
    }
    
    public ModelPart(final Model dhy) {
        this(dhy, null);
    }
    
    public ModelPart(final Model dhy, final int integer2, final int integer3) {
        this(dhy);
        this.texOffs(integer2, integer3);
    }
    
    public void copyFrom(final ModelPart djv) {
        this.xRot = djv.xRot;
        this.yRot = djv.yRot;
        this.zRot = djv.zRot;
        this.x = djv.x;
        this.y = djv.y;
        this.z = djv.z;
    }
    
    public void addChild(final ModelPart djv) {
        if (this.children == null) {
            this.children = (List<ModelPart>)Lists.newArrayList();
        }
        this.children.add(djv);
    }
    
    public void removeChild(final ModelPart djv) {
        if (this.children != null) {
            this.children.remove(djv);
        }
    }
    
    public ModelPart texOffs(final int integer1, final int integer2) {
        this.xTexOffs = integer1;
        this.yTexOffs = integer2;
        return this;
    }
    
    public ModelPart addBox(String string, final float float2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final float float8, final int integer9, final int integer10) {
        string = this.id + "." + string;
        this.texOffs(integer9, integer10);
        this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, float2, float3, float4, integer5, integer6, integer7, float8).setId(string));
        return this;
    }
    
    public ModelPart addBox(final float float1, final float float2, final float float3, final int integer4, final int integer5, final int integer6) {
        this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, float1, float2, float3, integer4, integer5, integer6, 0.0f));
        return this;
    }
    
    public ModelPart addBox(final float float1, final float float2, final float float3, final int integer4, final int integer5, final int integer6, final boolean boolean7) {
        this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, float1, float2, float3, integer4, integer5, integer6, 0.0f, boolean7));
        return this;
    }
    
    public void addBox(final float float1, final float float2, final float float3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, float1, float2, float3, integer4, integer5, integer6, float7));
    }
    
    public void addBox(final float float1, final float float2, final float float3, final int integer4, final int integer5, final int integer6, final float float7, final boolean boolean8) {
        this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, float1, float2, float3, integer4, integer5, integer6, float7, boolean8));
    }
    
    public void setPos(final float float1, final float float2, final float float3) {
        this.x = float1;
        this.y = float2;
        this.z = float3;
    }
    
    public void render(final float float1) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(float1);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.translateX, this.translateY, this.translateZ);
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x * float1, this.y * float1, this.z * float1);
            if (this.zRot != 0.0f) {
                GlStateManager.rotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yRot != 0.0f) {
                GlStateManager.rotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.xRot != 0.0f) {
                GlStateManager.rotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer3 = 0; integer3 < this.children.size(); ++integer3) {
                    ((ModelPart)this.children.get(integer3)).render(float1);
                }
            }
            GlStateManager.popMatrix();
        }
        else if (this.x != 0.0f || this.y != 0.0f || this.z != 0.0f) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x * float1, this.y * float1, this.z * float1);
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer3 = 0; integer3 < this.children.size(); ++integer3) {
                    ((ModelPart)this.children.get(integer3)).render(float1);
                }
            }
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer3 = 0; integer3 < this.children.size(); ++integer3) {
                    ((ModelPart)this.children.get(integer3)).render(float1);
                }
            }
        }
        GlStateManager.popMatrix();
    }
    
    public void renderRollable(final float float1) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(float1);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.x * float1, this.y * float1, this.z * float1);
        if (this.yRot != 0.0f) {
            GlStateManager.rotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (this.xRot != 0.0f) {
            GlStateManager.rotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (this.zRot != 0.0f) {
            GlStateManager.rotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.callList(this.list);
        GlStateManager.popMatrix();
    }
    
    public void translateTo(final float float1) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(float1);
        }
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            GlStateManager.translatef(this.x * float1, this.y * float1, this.z * float1);
            if (this.zRot != 0.0f) {
                GlStateManager.rotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yRot != 0.0f) {
                GlStateManager.rotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.xRot != 0.0f) {
                GlStateManager.rotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.x != 0.0f || this.y != 0.0f || this.z != 0.0f) {
            GlStateManager.translatef(this.x * float1, this.y * float1, this.z * float1);
        }
    }
    
    private void compile(final float float1) {
        GlStateManager.newList(this.list = MemoryTracker.genLists(1), 4864);
        final BufferBuilder cuw3 = Tesselator.getInstance().getBuilder();
        for (int integer4 = 0; integer4 < this.cubes.size(); ++integer4) {
            ((Cube)this.cubes.get(integer4)).compile(cuw3, float1);
        }
        GlStateManager.endList();
        this.compiled = true;
    }
    
    public ModelPart setTexSize(final int integer1, final int integer2) {
        this.xTexSize = (float)integer1;
        this.yTexSize = (float)integer2;
        return this;
    }
}

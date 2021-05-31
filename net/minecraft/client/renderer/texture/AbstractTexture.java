package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.GlStateManager;

public abstract class AbstractTexture implements TextureObject {
    protected int id;
    protected boolean blur;
    protected boolean mipmap;
    protected boolean oldBlur;
    protected boolean oldMipmap;
    
    public AbstractTexture() {
        this.id = -1;
    }
    
    public void setFilter(final boolean boolean1, final boolean boolean2) {
        this.blur = boolean1;
        this.mipmap = boolean2;
        int integer4;
        int integer5;
        if (boolean1) {
            integer4 = (boolean2 ? 9987 : 9729);
            integer5 = 9729;
        }
        else {
            integer4 = (boolean2 ? 9986 : 9728);
            integer5 = 9728;
        }
        GlStateManager.texParameter(3553, 10241, integer4);
        GlStateManager.texParameter(3553, 10240, integer5);
    }
    
    public void pushFilter(final boolean boolean1, final boolean boolean2) {
        this.oldBlur = this.blur;
        this.oldMipmap = this.mipmap;
        this.setFilter(boolean1, boolean2);
    }
    
    public void popFilter() {
        this.setFilter(this.oldBlur, this.oldMipmap);
    }
    
    public int getId() {
        if (this.id == -1) {
            this.id = TextureUtil.generateTextureId();
        }
        return this.id;
    }
    
    public void releaseId() {
        if (this.id != -1) {
            TextureUtil.releaseTextureId(this.id);
            this.id = -1;
        }
    }
}

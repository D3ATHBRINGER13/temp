package com.mojang.blaze3d.vertex;

import java.util.List;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

public class BufferUploader {
    public void end(final BufferBuilder cuw) {
        if (cuw.getVertexCount() > 0) {
            final VertexFormat cvc3 = cuw.getVertexFormat();
            final int integer4 = cvc3.getVertexSize();
            final ByteBuffer byteBuffer5 = cuw.getBuffer();
            final List<VertexFormatElement> list6 = cvc3.getElements();
            for (int integer5 = 0; integer5 < list6.size(); ++integer5) {
                final VertexFormatElement cvd8 = (VertexFormatElement)list6.get(integer5);
                final VertexFormatElement.Usage b9 = cvd8.getUsage();
                final int integer6 = cvd8.getType().getGlType();
                final int integer7 = cvd8.getIndex();
                byteBuffer5.position(cvc3.getOffset(integer5));
                switch (b9) {
                    case POSITION: {
                        GlStateManager.vertexPointer(cvd8.getCount(), integer6, integer4, byteBuffer5);
                        GlStateManager.enableClientState(32884);
                        break;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer7);
                        GlStateManager.texCoordPointer(cvd8.getCount(), integer6, integer4, byteBuffer5);
                        GlStateManager.enableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.colorPointer(cvd8.getCount(), integer6, integer4, byteBuffer5);
                        GlStateManager.enableClientState(32886);
                        break;
                    }
                    case NORMAL: {
                        GlStateManager.normalPointer(integer6, integer4, byteBuffer5);
                        GlStateManager.enableClientState(32885);
                        break;
                    }
                }
            }
            GlStateManager.drawArrays(cuw.getDrawMode(), 0, cuw.getVertexCount());
            for (int integer5 = 0, integer8 = list6.size(); integer5 < integer8; ++integer5) {
                final VertexFormatElement cvd9 = (VertexFormatElement)list6.get(integer5);
                final VertexFormatElement.Usage b10 = cvd9.getUsage();
                final int integer7 = cvd9.getIndex();
                switch (b10) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        break;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer7);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                        break;
                    }
                    case NORMAL: {
                        GlStateManager.disableClientState(32885);
                        break;
                    }
                }
            }
        }
        cuw.clear();
    }
}

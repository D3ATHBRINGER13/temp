package net.minecraft.client.renderer.texture;

import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.Closeable;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture {
    private static final Logger LOGGER;
    protected final ResourceLocation location;
    
    public SimpleTexture(final ResourceLocation qv) {
        this.location = qv;
    }
    
    public void load(final ResourceManager xi) throws IOException {
        try (final TextureImage a3 = this.getTextureImage(xi)) {
            boolean boolean5 = false;
            boolean boolean6 = false;
            a3.throwIfError();
            final TextureMetadataSection dym7 = a3.getTextureMetadata();
            if (dym7 != null) {
                boolean5 = dym7.isBlur();
                boolean6 = dym7.isClamp();
            }
            this.bind();
            TextureUtil.prepareImage(this.getId(), 0, a3.getImage().getWidth(), a3.getImage().getHeight());
            a3.getImage().upload(0, 0, 0, 0, 0, a3.getImage().getWidth(), a3.getImage().getHeight(), boolean5, boolean6, false);
        }
    }
    
    protected TextureImage getTextureImage(final ResourceManager xi) {
        return TextureImage.load(xi, this.location);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class TextureImage implements Closeable {
        private final TextureMetadataSection metadata;
        private final NativeImage image;
        private final IOException exception;
        
        public TextureImage(final IOException iOException) {
            this.exception = iOException;
            this.metadata = null;
            this.image = null;
        }
        
        public TextureImage(@Nullable final TextureMetadataSection dym, final NativeImage cuj) {
            this.exception = null;
            this.metadata = dym;
            this.image = cuj;
        }
        
        public static TextureImage load(final ResourceManager xi, final ResourceLocation qv) {
            try (final Resource xh3 = xi.getResource(qv)) {
                final NativeImage cuj5 = NativeImage.read(xh3.getInputStream());
                TextureMetadataSection dym6 = null;
                try {
                    dym6 = xh3.<TextureMetadataSection>getMetadata((MetadataSectionSerializer<TextureMetadataSection>)TextureMetadataSection.SERIALIZER);
                }
                catch (RuntimeException runtimeException7) {
                    SimpleTexture.LOGGER.warn("Failed reading metadata of: {}", qv, runtimeException7);
                }
                return new TextureImage(dym6, cuj5);
            }
            catch (IOException iOException3) {
                return new TextureImage(iOException3);
            }
        }
        
        @Nullable
        public TextureMetadataSection getTextureMetadata() {
            return this.metadata;
        }
        
        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }
        
        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }
        
        public void throwIfError() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.network.chat.Component;
import java.io.InputStream;
import java.io.IOException;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.Pack;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.repository.UnopenedPack;

public class UnopenedResourcePack extends UnopenedPack {
    @Nullable
    private NativeImage icon;
    @Nullable
    private ResourceLocation iconLocation;
    
    public UnopenedResourcePack(final String string, final boolean boolean2, final Supplier<Pack> supplier, final Pack wl, final PackMetadataSection wq, final Position a) {
        super(string, boolean2, supplier, wl, wq, a);
        NativeImage cuj8 = null;
        try (final InputStream inputStream9 = wl.getRootResource("pack.png")) {
            cuj8 = NativeImage.read(inputStream9);
        }
        catch (IOException ex) {}
        catch (IllegalArgumentException ex2) {}
        this.icon = cuj8;
    }
    
    public UnopenedResourcePack(final String string, final boolean boolean2, final Supplier<Pack> supplier, final Component jo4, final Component jo5, final PackCompatibility ww, final Position a, final boolean boolean8, @Nullable final NativeImage cuj) {
        super(string, boolean2, supplier, jo4, jo5, ww, a, boolean8);
        this.icon = cuj;
    }
    
    public void bindIcon(final TextureManager dxc) {
        if (this.iconLocation == null) {
            if (this.icon == null) {
                this.iconLocation = new ResourceLocation("textures/misc/unknown_pack.png");
            }
            else {
                this.iconLocation = dxc.register("texturepackicon", new DynamicTexture(this.icon));
            }
        }
        dxc.bind(this.iconLocation);
    }
    
    @Override
    public void close() {
        super.close();
        if (this.icon != null) {
            this.icon.close();
            this.icon = null;
        }
    }
}

package com.mojang.realmsclient.util;

import org.apache.logging.log4j.LogManager;
import java.util.HashMap;
import java.io.InputStream;
import java.nio.IntBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import org.apache.commons.codec.binary.Base64;
import java.awt.image.BufferedImage;
import javax.xml.bind.DatatypeConverter;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import javax.imageio.ImageIO;
import net.minecraft.realms.Realms;
import java.net.URL;
import java.net.HttpURLConnection;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> textures;
    private static final Map<String, Boolean> skinFetchStatus;
    private static final Map<String, String> fetchedSkins;
    private static final Logger LOGGER;
    
    public static void bindWorldTemplate(final String string1, final String string2) {
        if (string2 == null) {
            RealmsScreen.bind("textures/gui/presets/isles.png");
            return;
        }
        final int integer3 = getTextureId(string1, string2);
        GlStateManager.bindTexture(integer3);
    }
    
    public static void withBoundFace(final String string, final Runnable runnable) {
        GLX.withTextureRestore(() -> {
            bindFace(string);
            runnable.run();
        });
    }
    
    private static void bindDefaultFace(final UUID uUID) {
        RealmsScreen.bind(((uUID.hashCode() & 0x1) == 0x1) ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
    }
    
    private static void bindFace(final String string) {
        final UUID uUID2 = UUIDTypeAdapter.fromString(string);
        if (RealmsTextureManager.textures.containsKey(string)) {
            GlStateManager.bindTexture(((RealmsTexture)RealmsTextureManager.textures.get(string)).textureId);
            return;
        }
        if (RealmsTextureManager.skinFetchStatus.containsKey(string)) {
            if (!(boolean)RealmsTextureManager.skinFetchStatus.get(string)) {
                bindDefaultFace(uUID2);
            }
            else if (RealmsTextureManager.fetchedSkins.containsKey(string)) {
                final int integer3 = getTextureId(string, (String)RealmsTextureManager.fetchedSkins.get(string));
                GlStateManager.bindTexture(integer3);
            }
            else {
                bindDefaultFace(uUID2);
            }
            return;
        }
        RealmsTextureManager.skinFetchStatus.put(string, false);
        bindDefaultFace(uUID2);
        final Thread thread3 = new Thread("Realms Texture Downloader") {
            public void run() {
                final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map2 = RealmsUtil.getTextures(string);
                if (map2.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    final MinecraftProfileTexture minecraftProfileTexture3 = (MinecraftProfileTexture)map2.get(MinecraftProfileTexture.Type.SKIN);
                    final String string4 = minecraftProfileTexture3.getUrl();
                    HttpURLConnection httpURLConnection5 = null;
                    RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", string4);
                    try {
                        httpURLConnection5 = (HttpURLConnection)new URL(string4).openConnection(Realms.getProxy());
                        httpURLConnection5.setDoInput(true);
                        httpURLConnection5.setDoOutput(false);
                        httpURLConnection5.connect();
                        if (httpURLConnection5.getResponseCode() / 100 != 2) {
                            RealmsTextureManager.skinFetchStatus.remove(string);
                            return;
                        }
                        BufferedImage bufferedImage6;
                        try {
                            bufferedImage6 = ImageIO.read(httpURLConnection5.getInputStream());
                        }
                        catch (Exception exception7) {
                            RealmsTextureManager.skinFetchStatus.remove(string);
                            return;
                        }
                        finally {
                            IOUtils.closeQuietly(httpURLConnection5.getInputStream());
                        }
                        bufferedImage6 = new SkinProcessor().process(bufferedImage6);
                        final ByteArrayOutputStream byteArrayOutputStream7 = new ByteArrayOutputStream();
                        ImageIO.write((RenderedImage)bufferedImage6, "png", (OutputStream)byteArrayOutputStream7);
                        RealmsTextureManager.fetchedSkins.put(string, DatatypeConverter.printBase64Binary(byteArrayOutputStream7.toByteArray()));
                        RealmsTextureManager.skinFetchStatus.put(string, true);
                    }
                    catch (Exception exception6) {
                        RealmsTextureManager.LOGGER.error("Couldn't download http texture", (Throwable)exception6);
                        RealmsTextureManager.skinFetchStatus.remove(string);
                    }
                    finally {
                        if (httpURLConnection5 != null) {
                            httpURLConnection5.disconnect();
                        }
                    }
                    return;
                }
                RealmsTextureManager.skinFetchStatus.put(string, true);
            }
        };
        thread3.setDaemon(true);
        thread3.start();
    }
    
    private static int getTextureId(final String string1, final String string2) {
        int integer3;
        if (RealmsTextureManager.textures.containsKey(string1)) {
            final RealmsTexture a4 = (RealmsTexture)RealmsTextureManager.textures.get(string1);
            if (a4.image.equals(string2)) {
                return a4.textureId;
            }
            GlStateManager.deleteTexture(a4.textureId);
            integer3 = a4.textureId;
        }
        else {
            integer3 = GlStateManager.genTexture();
        }
        IntBuffer intBuffer4 = null;
        int integer4 = 0;
        int integer5 = 0;
        try {
            final InputStream inputStream8 = (InputStream)new ByteArrayInputStream(new Base64().decode(string2));
            BufferedImage bufferedImage7;
            try {
                bufferedImage7 = ImageIO.read(inputStream8);
            }
            finally {
                IOUtils.closeQuietly(inputStream8);
            }
            integer4 = bufferedImage7.getWidth();
            integer5 = bufferedImage7.getHeight();
            final int[] arr9 = new int[integer4 * integer5];
            bufferedImage7.getRGB(0, 0, integer4, integer5, arr9, 0, integer4);
            intBuffer4 = ByteBuffer.allocateDirect(4 * integer4 * integer5).order(ByteOrder.nativeOrder()).asIntBuffer();
            intBuffer4.put(arr9);
            intBuffer4.flip();
        }
        catch (IOException iOException7) {
            iOException7.printStackTrace();
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.bindTexture(integer3);
        TextureUtil.initTexture(intBuffer4, integer4, integer5);
        RealmsTextureManager.textures.put(string1, new RealmsTexture(string2, integer3));
        return integer3;
    }
    
    static {
        textures = (Map)new HashMap();
        skinFetchStatus = (Map)new HashMap();
        fetchedSkins = (Map)new HashMap();
        LOGGER = LogManager.getLogger();
    }
    
    public static class RealmsTexture {
        String image;
        int textureId;
        
        public RealmsTexture(final String string, final int integer) {
            this.image = string;
            this.textureId = integer;
        }
    }
}

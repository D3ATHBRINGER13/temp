package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.texture.TextureObject;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileNotFoundException;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import net.minecraft.server.ChainedJsonException;
import com.google.gson.JsonElement;
import java.io.Reader;
import net.minecraft.util.GsonHelper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.math.Matrix4f;
import java.util.Map;
import java.util.List;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.blaze3d.pipeline.RenderTarget;

public class PostChain implements AutoCloseable {
    private final RenderTarget screenTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostPass> passes;
    private final Map<String, RenderTarget> customRenderTargets;
    private final List<RenderTarget> fullSizedTargets;
    private Matrix4f shaderOrthoMatrix;
    private int screenWidth;
    private int screenHeight;
    private float time;
    private float lastStamp;
    
    public PostChain(final TextureManager dxc, final ResourceManager xi, final RenderTarget ctz, final ResourceLocation qv) throws IOException, JsonSyntaxException {
        this.passes = (List<PostPass>)Lists.newArrayList();
        this.customRenderTargets = (Map<String, RenderTarget>)Maps.newHashMap();
        this.fullSizedTargets = (List<RenderTarget>)Lists.newArrayList();
        this.resourceManager = xi;
        this.screenTarget = ctz;
        this.time = 0.0f;
        this.lastStamp = 0.0f;
        this.screenWidth = ctz.viewWidth;
        this.screenHeight = ctz.viewHeight;
        this.name = qv.toString();
        this.updateOrthoMatrix();
        this.load(dxc, qv);
    }
    
    private void load(final TextureManager dxc, final ResourceLocation qv) throws IOException, JsonSyntaxException {
        Resource xh4 = null;
        try {
            xh4 = this.resourceManager.getResource(qv);
            final JsonObject jsonObject5 = GsonHelper.parse((Reader)new InputStreamReader(xh4.getInputStream(), StandardCharsets.UTF_8));
            if (GsonHelper.isArrayNode(jsonObject5, "targets")) {
                final JsonArray jsonArray6 = jsonObject5.getAsJsonArray("targets");
                int integer7 = 0;
                for (final JsonElement jsonElement9 : jsonArray6) {
                    try {
                        this.parseTargetNode(jsonElement9);
                    }
                    catch (Exception exception10) {
                        final ChainedJsonException qy11 = ChainedJsonException.forException(exception10);
                        qy11.prependJsonKey(new StringBuilder().append("targets[").append(integer7).append("]").toString());
                        throw qy11;
                    }
                    ++integer7;
                }
            }
            if (GsonHelper.isArrayNode(jsonObject5, "passes")) {
                final JsonArray jsonArray6 = jsonObject5.getAsJsonArray("passes");
                int integer7 = 0;
                for (final JsonElement jsonElement9 : jsonArray6) {
                    try {
                        this.parsePassNode(dxc, jsonElement9);
                    }
                    catch (Exception exception10) {
                        final ChainedJsonException qy11 = ChainedJsonException.forException(exception10);
                        qy11.prependJsonKey(new StringBuilder().append("passes[").append(integer7).append("]").toString());
                        throw qy11;
                    }
                    ++integer7;
                }
            }
        }
        catch (Exception exception11) {
            final ChainedJsonException qy12 = ChainedJsonException.forException(exception11);
            qy12.setFilenameAndFlush(qv.getPath());
            throw qy12;
        }
        finally {
            IOUtils.closeQuietly((Closeable)xh4);
        }
    }
    
    private void parseTargetNode(final JsonElement jsonElement) throws ChainedJsonException {
        if (GsonHelper.isStringValue(jsonElement)) {
            this.addTempTarget(jsonElement.getAsString(), this.screenWidth, this.screenHeight);
        }
        else {
            final JsonObject jsonObject3 = GsonHelper.convertToJsonObject(jsonElement, "target");
            final String string4 = GsonHelper.getAsString(jsonObject3, "name");
            final int integer5 = GsonHelper.getAsInt(jsonObject3, "width", this.screenWidth);
            final int integer6 = GsonHelper.getAsInt(jsonObject3, "height", this.screenHeight);
            if (this.customRenderTargets.containsKey(string4)) {
                throw new ChainedJsonException(string4 + " is already defined");
            }
            this.addTempTarget(string4, integer5, integer6);
        }
    }
    
    private void parsePassNode(final TextureManager dxc, final JsonElement jsonElement) throws IOException {
        final JsonObject jsonObject4 = GsonHelper.convertToJsonObject(jsonElement, "pass");
        final String string5 = GsonHelper.getAsString(jsonObject4, "name");
        final String string6 = GsonHelper.getAsString(jsonObject4, "intarget");
        final String string7 = GsonHelper.getAsString(jsonObject4, "outtarget");
        final RenderTarget ctz8 = this.getRenderTarget(string6);
        final RenderTarget ctz9 = this.getRenderTarget(string7);
        if (ctz8 == null) {
            throw new ChainedJsonException("Input target '" + string6 + "' does not exist");
        }
        if (ctz9 == null) {
            throw new ChainedJsonException("Output target '" + string7 + "' does not exist");
        }
        final PostPass dnm10 = this.addPass(string5, ctz8, ctz9);
        final JsonArray jsonArray11 = GsonHelper.getAsJsonArray(jsonObject4, "auxtargets", (JsonArray)null);
        if (jsonArray11 != null) {
            int integer12 = 0;
            for (final JsonElement jsonElement2 : jsonArray11) {
                try {
                    final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement2, "auxtarget");
                    final String string8 = GsonHelper.getAsString(jsonObject5, "name");
                    final String string9 = GsonHelper.getAsString(jsonObject5, "id");
                    final RenderTarget ctz10 = this.getRenderTarget(string9);
                    if (ctz10 == null) {
                        final ResourceLocation qv19 = new ResourceLocation("textures/effect/" + string9 + ".png");
                        Resource xh20 = null;
                        try {
                            xh20 = this.resourceManager.getResource(qv19);
                        }
                        catch (FileNotFoundException fileNotFoundException21) {
                            throw new ChainedJsonException("Render target or texture '" + string9 + "' does not exist");
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)xh20);
                        }
                        dxc.bind(qv19);
                        final TextureObject dxd21 = dxc.getTexture(qv19);
                        final int integer13 = GsonHelper.getAsInt(jsonObject5, "width");
                        final int integer14 = GsonHelper.getAsInt(jsonObject5, "height");
                        final boolean boolean24 = GsonHelper.getAsBoolean(jsonObject5, "bilinear");
                        if (boolean24) {
                            GlStateManager.texParameter(3553, 10241, 9729);
                            GlStateManager.texParameter(3553, 10240, 9729);
                        }
                        else {
                            GlStateManager.texParameter(3553, 10241, 9728);
                            GlStateManager.texParameter(3553, 10240, 9728);
                        }
                        dnm10.addAuxAsset(string8, dxd21.getId(), integer13, integer14);
                    }
                    else {
                        dnm10.addAuxAsset(string8, ctz10, ctz10.width, ctz10.height);
                    }
                }
                catch (Exception exception15) {
                    final ChainedJsonException qy16 = ChainedJsonException.forException(exception15);
                    qy16.prependJsonKey(new StringBuilder().append("auxtargets[").append(integer12).append("]").toString());
                    throw qy16;
                }
                ++integer12;
            }
        }
        final JsonArray jsonArray12 = GsonHelper.getAsJsonArray(jsonObject4, "uniforms", (JsonArray)null);
        if (jsonArray12 != null) {
            int integer15 = 0;
            for (final JsonElement jsonElement3 : jsonArray12) {
                try {
                    this.parseUniformNode(jsonElement3);
                }
                catch (Exception exception16) {
                    final ChainedJsonException qy17 = ChainedJsonException.forException(exception16);
                    qy17.prependJsonKey(new StringBuilder().append("uniforms[").append(integer15).append("]").toString());
                    throw qy17;
                }
                ++integer15;
            }
        }
    }
    
    private void parseUniformNode(final JsonElement jsonElement) throws ChainedJsonException {
        final JsonObject jsonObject3 = GsonHelper.convertToJsonObject(jsonElement, "uniform");
        final String string4 = GsonHelper.getAsString(jsonObject3, "name");
        final Uniform cuv5 = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform(string4);
        if (cuv5 == null) {
            throw new ChainedJsonException("Uniform '" + string4 + "' does not exist");
        }
        final float[] arr6 = new float[4];
        int integer7 = 0;
        final JsonArray jsonArray8 = GsonHelper.getAsJsonArray(jsonObject3, "values");
        for (final JsonElement jsonElement2 : jsonArray8) {
            try {
                arr6[integer7] = GsonHelper.convertToFloat(jsonElement2, "value");
            }
            catch (Exception exception11) {
                final ChainedJsonException qy12 = ChainedJsonException.forException(exception11);
                qy12.prependJsonKey(new StringBuilder().append("values[").append(integer7).append("]").toString());
                throw qy12;
            }
            ++integer7;
        }
        switch (integer7) {
            case 1: {
                cuv5.set(arr6[0]);
                break;
            }
            case 2: {
                cuv5.set(arr6[0], arr6[1]);
                break;
            }
            case 3: {
                cuv5.set(arr6[0], arr6[1], arr6[2]);
                break;
            }
            case 4: {
                cuv5.set(arr6[0], arr6[1], arr6[2], arr6[3]);
                break;
            }
        }
    }
    
    public RenderTarget getTempTarget(final String string) {
        return (RenderTarget)this.customRenderTargets.get(string);
    }
    
    public void addTempTarget(final String string, final int integer2, final int integer3) {
        final RenderTarget ctz5 = new RenderTarget(integer2, integer3, true, Minecraft.ON_OSX);
        ctz5.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.customRenderTargets.put(string, ctz5);
        if (integer2 == this.screenWidth && integer3 == this.screenHeight) {
            this.fullSizedTargets.add(ctz5);
        }
    }
    
    public void close() {
        for (final RenderTarget ctz3 : this.customRenderTargets.values()) {
            ctz3.destroyBuffers();
        }
        for (final PostPass dnm3 : this.passes) {
            dnm3.close();
        }
        this.passes.clear();
    }
    
    public PostPass addPass(final String string, final RenderTarget ctz2, final RenderTarget ctz3) throws IOException {
        final PostPass dnm5 = new PostPass(this.resourceManager, string, ctz2, ctz3);
        this.passes.add(this.passes.size(), dnm5);
        return dnm5;
    }
    
    private void updateOrthoMatrix() {
        this.shaderOrthoMatrix = Matrix4f.orthographic((float)this.screenTarget.width, (float)this.screenTarget.height, 0.1f, 1000.0f);
    }
    
    public void resize(final int integer1, final int integer2) {
        this.screenWidth = this.screenTarget.width;
        this.screenHeight = this.screenTarget.height;
        this.updateOrthoMatrix();
        for (final PostPass dnm5 : this.passes) {
            dnm5.setOrthoMatrix(this.shaderOrthoMatrix);
        }
        for (final RenderTarget ctz5 : this.fullSizedTargets) {
            ctz5.resize(integer1, integer2, Minecraft.ON_OSX);
        }
    }
    
    public void process(final float float1) {
        if (float1 < this.lastStamp) {
            this.time += 1.0f - this.lastStamp;
            this.time += float1;
        }
        else {
            this.time += float1 - this.lastStamp;
        }
        this.lastStamp = float1;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (final PostPass dnm4 : this.passes) {
            dnm4.process(this.time / 20.0f);
        }
    }
    
    public final String getName() {
        return this.name;
    }
    
    private RenderTarget getRenderTarget(final String string) {
        if (string == null) {
            return null;
        }
        if (string.equals("minecraft:main")) {
            return this.screenTarget;
        }
        return (RenderTarget)this.customRenderTargets.get(string);
    }
}

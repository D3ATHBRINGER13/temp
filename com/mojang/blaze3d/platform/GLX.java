package com.mojang.blaze3d.platform;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.ARBShaderObjects;
import oshi.hardware.Processor;
import oshi.SystemInfo;
import java.util.Locale;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import org.lwjgl.glfw.GLFWErrorCallback;
import java.util.List;
import com.google.common.base.Joiner;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import com.google.common.collect.Lists;
import java.util.function.BiConsumer;
import java.util.function.LongSupplier;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GL;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class GLX {
    private static final Logger LOGGER;
    public static boolean isNvidia;
    public static boolean isAmd;
    public static int GL_FRAMEBUFFER;
    public static int GL_RENDERBUFFER;
    public static int GL_COLOR_ATTACHMENT0;
    public static int GL_DEPTH_ATTACHMENT;
    public static int GL_FRAMEBUFFER_COMPLETE;
    public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
    public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
    private static FboMode fboMode;
    public static final boolean useFbo = true;
    private static boolean hasShaders;
    private static boolean useShaderArb;
    public static int GL_LINK_STATUS;
    public static int GL_COMPILE_STATUS;
    public static int GL_VERTEX_SHADER;
    public static int GL_FRAGMENT_SHADER;
    private static boolean useMultitextureArb;
    public static int GL_TEXTURE0;
    public static int GL_TEXTURE1;
    public static int GL_TEXTURE2;
    private static boolean useTexEnvCombineArb;
    public static int GL_COMBINE;
    public static int GL_INTERPOLATE;
    public static int GL_PRIMARY_COLOR;
    public static int GL_CONSTANT;
    public static int GL_PREVIOUS;
    public static int GL_COMBINE_RGB;
    public static int GL_SOURCE0_RGB;
    public static int GL_SOURCE1_RGB;
    public static int GL_SOURCE2_RGB;
    public static int GL_OPERAND0_RGB;
    public static int GL_OPERAND1_RGB;
    public static int GL_OPERAND2_RGB;
    public static int GL_COMBINE_ALPHA;
    public static int GL_SOURCE0_ALPHA;
    public static int GL_SOURCE1_ALPHA;
    public static int GL_SOURCE2_ALPHA;
    public static int GL_OPERAND0_ALPHA;
    public static int GL_OPERAND1_ALPHA;
    public static int GL_OPERAND2_ALPHA;
    private static boolean separateBlend;
    public static boolean useSeparateBlendExt;
    public static boolean isOpenGl21;
    public static boolean usePostProcess;
    private static String capsString;
    private static String cpuInfo;
    public static final boolean useVbo = true;
    public static boolean needVbo;
    private static boolean useVboArb;
    public static int GL_ARRAY_BUFFER;
    public static int GL_STATIC_DRAW;
    private static final Map<Integer, String> LOOKUP_MAP;
    
    public static void populateSnooperWithOpenGL(final SnooperAccess cum) {
        cum.setFixedData("opengl_version", GlStateManager.getString(7938));
        cum.setFixedData("opengl_vendor", GlStateManager.getString(7936));
        final GLCapabilities gLCapabilities2 = GL.getCapabilities();
        cum.setFixedData("gl_caps[ARB_arrays_of_arrays]", gLCapabilities2.GL_ARB_arrays_of_arrays);
        cum.setFixedData("gl_caps[ARB_base_instance]", gLCapabilities2.GL_ARB_base_instance);
        cum.setFixedData("gl_caps[ARB_blend_func_extended]", gLCapabilities2.GL_ARB_blend_func_extended);
        cum.setFixedData("gl_caps[ARB_clear_buffer_object]", gLCapabilities2.GL_ARB_clear_buffer_object);
        cum.setFixedData("gl_caps[ARB_color_buffer_float]", gLCapabilities2.GL_ARB_color_buffer_float);
        cum.setFixedData("gl_caps[ARB_compatibility]", gLCapabilities2.GL_ARB_compatibility);
        cum.setFixedData("gl_caps[ARB_compressed_texture_pixel_storage]", gLCapabilities2.GL_ARB_compressed_texture_pixel_storage);
        cum.setFixedData("gl_caps[ARB_compute_shader]", gLCapabilities2.GL_ARB_compute_shader);
        cum.setFixedData("gl_caps[ARB_copy_buffer]", gLCapabilities2.GL_ARB_copy_buffer);
        cum.setFixedData("gl_caps[ARB_copy_image]", gLCapabilities2.GL_ARB_copy_image);
        cum.setFixedData("gl_caps[ARB_depth_buffer_float]", gLCapabilities2.GL_ARB_depth_buffer_float);
        cum.setFixedData("gl_caps[ARB_compute_shader]", gLCapabilities2.GL_ARB_compute_shader);
        cum.setFixedData("gl_caps[ARB_copy_buffer]", gLCapabilities2.GL_ARB_copy_buffer);
        cum.setFixedData("gl_caps[ARB_copy_image]", gLCapabilities2.GL_ARB_copy_image);
        cum.setFixedData("gl_caps[ARB_depth_buffer_float]", gLCapabilities2.GL_ARB_depth_buffer_float);
        cum.setFixedData("gl_caps[ARB_depth_clamp]", gLCapabilities2.GL_ARB_depth_clamp);
        cum.setFixedData("gl_caps[ARB_depth_texture]", gLCapabilities2.GL_ARB_depth_texture);
        cum.setFixedData("gl_caps[ARB_draw_buffers]", gLCapabilities2.GL_ARB_draw_buffers);
        cum.setFixedData("gl_caps[ARB_draw_buffers_blend]", gLCapabilities2.GL_ARB_draw_buffers_blend);
        cum.setFixedData("gl_caps[ARB_draw_elements_base_vertex]", gLCapabilities2.GL_ARB_draw_elements_base_vertex);
        cum.setFixedData("gl_caps[ARB_draw_indirect]", gLCapabilities2.GL_ARB_draw_indirect);
        cum.setFixedData("gl_caps[ARB_draw_instanced]", gLCapabilities2.GL_ARB_draw_instanced);
        cum.setFixedData("gl_caps[ARB_explicit_attrib_location]", gLCapabilities2.GL_ARB_explicit_attrib_location);
        cum.setFixedData("gl_caps[ARB_explicit_uniform_location]", gLCapabilities2.GL_ARB_explicit_uniform_location);
        cum.setFixedData("gl_caps[ARB_fragment_layer_viewport]", gLCapabilities2.GL_ARB_fragment_layer_viewport);
        cum.setFixedData("gl_caps[ARB_fragment_program]", gLCapabilities2.GL_ARB_fragment_program);
        cum.setFixedData("gl_caps[ARB_fragment_shader]", gLCapabilities2.GL_ARB_fragment_shader);
        cum.setFixedData("gl_caps[ARB_fragment_program_shadow]", gLCapabilities2.GL_ARB_fragment_program_shadow);
        cum.setFixedData("gl_caps[ARB_framebuffer_object]", gLCapabilities2.GL_ARB_framebuffer_object);
        cum.setFixedData("gl_caps[ARB_framebuffer_sRGB]", gLCapabilities2.GL_ARB_framebuffer_sRGB);
        cum.setFixedData("gl_caps[ARB_geometry_shader4]", gLCapabilities2.GL_ARB_geometry_shader4);
        cum.setFixedData("gl_caps[ARB_gpu_shader5]", gLCapabilities2.GL_ARB_gpu_shader5);
        cum.setFixedData("gl_caps[ARB_half_float_pixel]", gLCapabilities2.GL_ARB_half_float_pixel);
        cum.setFixedData("gl_caps[ARB_half_float_vertex]", gLCapabilities2.GL_ARB_half_float_vertex);
        cum.setFixedData("gl_caps[ARB_instanced_arrays]", gLCapabilities2.GL_ARB_instanced_arrays);
        cum.setFixedData("gl_caps[ARB_map_buffer_alignment]", gLCapabilities2.GL_ARB_map_buffer_alignment);
        cum.setFixedData("gl_caps[ARB_map_buffer_range]", gLCapabilities2.GL_ARB_map_buffer_range);
        cum.setFixedData("gl_caps[ARB_multisample]", gLCapabilities2.GL_ARB_multisample);
        cum.setFixedData("gl_caps[ARB_multitexture]", gLCapabilities2.GL_ARB_multitexture);
        cum.setFixedData("gl_caps[ARB_occlusion_query2]", gLCapabilities2.GL_ARB_occlusion_query2);
        cum.setFixedData("gl_caps[ARB_pixel_buffer_object]", gLCapabilities2.GL_ARB_pixel_buffer_object);
        cum.setFixedData("gl_caps[ARB_seamless_cube_map]", gLCapabilities2.GL_ARB_seamless_cube_map);
        cum.setFixedData("gl_caps[ARB_shader_objects]", gLCapabilities2.GL_ARB_shader_objects);
        cum.setFixedData("gl_caps[ARB_shader_stencil_export]", gLCapabilities2.GL_ARB_shader_stencil_export);
        cum.setFixedData("gl_caps[ARB_shader_texture_lod]", gLCapabilities2.GL_ARB_shader_texture_lod);
        cum.setFixedData("gl_caps[ARB_shadow]", gLCapabilities2.GL_ARB_shadow);
        cum.setFixedData("gl_caps[ARB_shadow_ambient]", gLCapabilities2.GL_ARB_shadow_ambient);
        cum.setFixedData("gl_caps[ARB_stencil_texturing]", gLCapabilities2.GL_ARB_stencil_texturing);
        cum.setFixedData("gl_caps[ARB_sync]", gLCapabilities2.GL_ARB_sync);
        cum.setFixedData("gl_caps[ARB_tessellation_shader]", gLCapabilities2.GL_ARB_tessellation_shader);
        cum.setFixedData("gl_caps[ARB_texture_border_clamp]", gLCapabilities2.GL_ARB_texture_border_clamp);
        cum.setFixedData("gl_caps[ARB_texture_buffer_object]", gLCapabilities2.GL_ARB_texture_buffer_object);
        cum.setFixedData("gl_caps[ARB_texture_cube_map]", gLCapabilities2.GL_ARB_texture_cube_map);
        cum.setFixedData("gl_caps[ARB_texture_cube_map_array]", gLCapabilities2.GL_ARB_texture_cube_map_array);
        cum.setFixedData("gl_caps[ARB_texture_non_power_of_two]", gLCapabilities2.GL_ARB_texture_non_power_of_two);
        cum.setFixedData("gl_caps[ARB_uniform_buffer_object]", gLCapabilities2.GL_ARB_uniform_buffer_object);
        cum.setFixedData("gl_caps[ARB_vertex_blend]", gLCapabilities2.GL_ARB_vertex_blend);
        cum.setFixedData("gl_caps[ARB_vertex_buffer_object]", gLCapabilities2.GL_ARB_vertex_buffer_object);
        cum.setFixedData("gl_caps[ARB_vertex_program]", gLCapabilities2.GL_ARB_vertex_program);
        cum.setFixedData("gl_caps[ARB_vertex_shader]", gLCapabilities2.GL_ARB_vertex_shader);
        cum.setFixedData("gl_caps[EXT_bindable_uniform]", gLCapabilities2.GL_EXT_bindable_uniform);
        cum.setFixedData("gl_caps[EXT_blend_equation_separate]", gLCapabilities2.GL_EXT_blend_equation_separate);
        cum.setFixedData("gl_caps[EXT_blend_func_separate]", gLCapabilities2.GL_EXT_blend_func_separate);
        cum.setFixedData("gl_caps[EXT_blend_minmax]", gLCapabilities2.GL_EXT_blend_minmax);
        cum.setFixedData("gl_caps[EXT_blend_subtract]", gLCapabilities2.GL_EXT_blend_subtract);
        cum.setFixedData("gl_caps[EXT_draw_instanced]", gLCapabilities2.GL_EXT_draw_instanced);
        cum.setFixedData("gl_caps[EXT_framebuffer_multisample]", gLCapabilities2.GL_EXT_framebuffer_multisample);
        cum.setFixedData("gl_caps[EXT_framebuffer_object]", gLCapabilities2.GL_EXT_framebuffer_object);
        cum.setFixedData("gl_caps[EXT_framebuffer_sRGB]", gLCapabilities2.GL_EXT_framebuffer_sRGB);
        cum.setFixedData("gl_caps[EXT_geometry_shader4]", gLCapabilities2.GL_EXT_geometry_shader4);
        cum.setFixedData("gl_caps[EXT_gpu_program_parameters]", gLCapabilities2.GL_EXT_gpu_program_parameters);
        cum.setFixedData("gl_caps[EXT_gpu_shader4]", gLCapabilities2.GL_EXT_gpu_shader4);
        cum.setFixedData("gl_caps[EXT_packed_depth_stencil]", gLCapabilities2.GL_EXT_packed_depth_stencil);
        cum.setFixedData("gl_caps[EXT_separate_shader_objects]", gLCapabilities2.GL_EXT_separate_shader_objects);
        cum.setFixedData("gl_caps[EXT_shader_image_load_store]", gLCapabilities2.GL_EXT_shader_image_load_store);
        cum.setFixedData("gl_caps[EXT_shadow_funcs]", gLCapabilities2.GL_EXT_shadow_funcs);
        cum.setFixedData("gl_caps[EXT_shared_texture_palette]", gLCapabilities2.GL_EXT_shared_texture_palette);
        cum.setFixedData("gl_caps[EXT_stencil_clear_tag]", gLCapabilities2.GL_EXT_stencil_clear_tag);
        cum.setFixedData("gl_caps[EXT_stencil_two_side]", gLCapabilities2.GL_EXT_stencil_two_side);
        cum.setFixedData("gl_caps[EXT_stencil_wrap]", gLCapabilities2.GL_EXT_stencil_wrap);
        cum.setFixedData("gl_caps[EXT_texture_array]", gLCapabilities2.GL_EXT_texture_array);
        cum.setFixedData("gl_caps[EXT_texture_buffer_object]", gLCapabilities2.GL_EXT_texture_buffer_object);
        cum.setFixedData("gl_caps[EXT_texture_integer]", gLCapabilities2.GL_EXT_texture_integer);
        cum.setFixedData("gl_caps[EXT_texture_sRGB]", gLCapabilities2.GL_EXT_texture_sRGB);
        cum.setFixedData("gl_caps[ARB_vertex_shader]", gLCapabilities2.GL_ARB_vertex_shader);
        cum.setFixedData("gl_caps[gl_max_vertex_uniforms]", GlStateManager.getInteger(35658));
        GlStateManager.getError();
        cum.setFixedData("gl_caps[gl_max_fragment_uniforms]", GlStateManager.getInteger(35657));
        GlStateManager.getError();
        cum.setFixedData("gl_caps[gl_max_vertex_attribs]", GlStateManager.getInteger(34921));
        GlStateManager.getError();
        cum.setFixedData("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.getInteger(35660));
        GlStateManager.getError();
        cum.setFixedData("gl_caps[gl_max_texture_image_units]", GlStateManager.getInteger(34930));
        GlStateManager.getError();
        cum.setFixedData("gl_caps[gl_max_array_texture_layers]", GlStateManager.getInteger(35071));
        GlStateManager.getError();
    }
    
    public static String getOpenGLVersionString() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
    }
    
    public static int getRefreshRate(final Window cuo) {
        long long2 = GLFW.glfwGetWindowMonitor(cuo.getWindow());
        if (long2 == 0L) {
            long2 = GLFW.glfwGetPrimaryMonitor();
        }
        final GLFWVidMode gLFWVidMode4 = (long2 == 0L) ? null : GLFW.glfwGetVideoMode(long2);
        return (gLFWVidMode4 == null) ? 0 : gLFWVidMode4.refreshRate();
    }
    
    public static String getLWJGLVersion() {
        return Version.getVersion();
    }
    
    public static LongSupplier initGlfw() {
        Window.checkGlfwError((BiConsumer<Integer, String>)((integer, string) -> {
            throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", new Object[] { integer, string }));
        }));
        final List<String> list1 = (List<String>)Lists.newArrayList();
        final GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback((integer, long3) -> list1.add(String.format("GLFW error during init: [0x%X]%s", new Object[] { integer, long3 })));
        if (GLFW.glfwInit()) {
            final LongSupplier longSupplier3 = () -> (long)(GLFW.glfwGetTime() * 1.0E9);
            for (final String string5 : list1) {
                GLX.LOGGER.error("GLFW error collected during initialization: {}", string5);
            }
            setGlfwErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback2);
            return longSupplier3;
        }
        throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join((Iterable)list1));
    }
    
    public static void setGlfwErrorCallback(final GLFWErrorCallbackI gLFWErrorCallbackI) {
        GLFW.glfwSetErrorCallback(gLFWErrorCallbackI).free();
    }
    
    public static boolean shouldClose(final Window cuo) {
        return GLFW.glfwWindowShouldClose(cuo.getWindow());
    }
    
    public static void pollEvents() {
        GLFW.glfwPollEvents();
    }
    
    public static String getOpenGLVersion() {
        return GlStateManager.getString(7938);
    }
    
    public static String getRenderer() {
        return GlStateManager.getString(7937);
    }
    
    public static String getVendor() {
        return GlStateManager.getString(7936);
    }
    
    public static void setupNvFogDistance() {
        if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.fogi(34138, 34139);
        }
    }
    
    public static boolean supportsOpenGL2() {
        return GL.getCapabilities().OpenGL20;
    }
    
    public static void withTextureRestore(final Runnable runnable) {
        GL11.glPushAttrib(270336);
        try {
            runnable.run();
        }
        finally {
            GL11.glPopAttrib();
        }
    }
    
    public static ByteBuffer allocateMemory(final int integer) {
        return MemoryUtil.memAlloc(integer);
    }
    
    public static void freeMemory(final Buffer buffer) {
        MemoryUtil.memFree(buffer);
    }
    
    public static void init() {
        final GLCapabilities gLCapabilities1 = GL.getCapabilities();
        GLX.useMultitextureArb = (gLCapabilities1.GL_ARB_multitexture && !gLCapabilities1.OpenGL13);
        GLX.useTexEnvCombineArb = (gLCapabilities1.GL_ARB_texture_env_combine && !gLCapabilities1.OpenGL13);
        if (GLX.useMultitextureArb) {
            GLX.capsString += "Using ARB_multitexture.\n";
            GLX.GL_TEXTURE0 = 33984;
            GLX.GL_TEXTURE1 = 33985;
            GLX.GL_TEXTURE2 = 33986;
        }
        else {
            GLX.capsString += "Using GL 1.3 multitexturing.\n";
            GLX.GL_TEXTURE0 = 33984;
            GLX.GL_TEXTURE1 = 33985;
            GLX.GL_TEXTURE2 = 33986;
        }
        if (GLX.useTexEnvCombineArb) {
            GLX.capsString += "Using ARB_texture_env_combine.\n";
            GLX.GL_COMBINE = 34160;
            GLX.GL_INTERPOLATE = 34165;
            GLX.GL_PRIMARY_COLOR = 34167;
            GLX.GL_CONSTANT = 34166;
            GLX.GL_PREVIOUS = 34168;
            GLX.GL_COMBINE_RGB = 34161;
            GLX.GL_SOURCE0_RGB = 34176;
            GLX.GL_SOURCE1_RGB = 34177;
            GLX.GL_SOURCE2_RGB = 34178;
            GLX.GL_OPERAND0_RGB = 34192;
            GLX.GL_OPERAND1_RGB = 34193;
            GLX.GL_OPERAND2_RGB = 34194;
            GLX.GL_COMBINE_ALPHA = 34162;
            GLX.GL_SOURCE0_ALPHA = 34184;
            GLX.GL_SOURCE1_ALPHA = 34185;
            GLX.GL_SOURCE2_ALPHA = 34186;
            GLX.GL_OPERAND0_ALPHA = 34200;
            GLX.GL_OPERAND1_ALPHA = 34201;
            GLX.GL_OPERAND2_ALPHA = 34202;
        }
        else {
            GLX.capsString += "Using GL 1.3 texture combiners.\n";
            GLX.GL_COMBINE = 34160;
            GLX.GL_INTERPOLATE = 34165;
            GLX.GL_PRIMARY_COLOR = 34167;
            GLX.GL_CONSTANT = 34166;
            GLX.GL_PREVIOUS = 34168;
            GLX.GL_COMBINE_RGB = 34161;
            GLX.GL_SOURCE0_RGB = 34176;
            GLX.GL_SOURCE1_RGB = 34177;
            GLX.GL_SOURCE2_RGB = 34178;
            GLX.GL_OPERAND0_RGB = 34192;
            GLX.GL_OPERAND1_RGB = 34193;
            GLX.GL_OPERAND2_RGB = 34194;
            GLX.GL_COMBINE_ALPHA = 34162;
            GLX.GL_SOURCE0_ALPHA = 34184;
            GLX.GL_SOURCE1_ALPHA = 34185;
            GLX.GL_SOURCE2_ALPHA = 34186;
            GLX.GL_OPERAND0_ALPHA = 34200;
            GLX.GL_OPERAND1_ALPHA = 34201;
            GLX.GL_OPERAND2_ALPHA = 34202;
        }
        GLX.useSeparateBlendExt = (gLCapabilities1.GL_EXT_blend_func_separate && !gLCapabilities1.OpenGL14);
        GLX.separateBlend = (gLCapabilities1.OpenGL14 || gLCapabilities1.GL_EXT_blend_func_separate);
        GLX.capsString += "Using framebuffer objects because ";
        if (gLCapabilities1.OpenGL30) {
            GLX.capsString += "OpenGL 3.0 is supported and separate blending is supported.\n";
            GLX.fboMode = FboMode.BASE;
            GLX.GL_FRAMEBUFFER = 36160;
            GLX.GL_RENDERBUFFER = 36161;
            GLX.GL_COLOR_ATTACHMENT0 = 36064;
            GLX.GL_DEPTH_ATTACHMENT = 36096;
            GLX.GL_FRAMEBUFFER_COMPLETE = 36053;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        }
        else if (gLCapabilities1.GL_ARB_framebuffer_object) {
            GLX.capsString += "ARB_framebuffer_object is supported and separate blending is supported.\n";
            GLX.fboMode = FboMode.ARB;
            GLX.GL_FRAMEBUFFER = 36160;
            GLX.GL_RENDERBUFFER = 36161;
            GLX.GL_COLOR_ATTACHMENT0 = 36064;
            GLX.GL_DEPTH_ATTACHMENT = 36096;
            GLX.GL_FRAMEBUFFER_COMPLETE = 36053;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        }
        else {
            if (!gLCapabilities1.GL_EXT_framebuffer_object) {
                throw new IllegalStateException("The driver does not appear to support framebuffer objects");
            }
            GLX.capsString += "EXT_framebuffer_object is supported.\n";
            GLX.fboMode = FboMode.EXT;
            GLX.GL_FRAMEBUFFER = 36160;
            GLX.GL_RENDERBUFFER = 36161;
            GLX.GL_COLOR_ATTACHMENT0 = 36064;
            GLX.GL_DEPTH_ATTACHMENT = 36096;
            GLX.GL_FRAMEBUFFER_COMPLETE = 36053;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        }
        GLX.isOpenGl21 = gLCapabilities1.OpenGL21;
        GLX.hasShaders = (GLX.isOpenGl21 || (gLCapabilities1.GL_ARB_vertex_shader && gLCapabilities1.GL_ARB_fragment_shader && gLCapabilities1.GL_ARB_shader_objects));
        GLX.capsString = GLX.capsString + "Shaders are " + (GLX.hasShaders ? "" : "not ") + "available because ";
        if (GLX.hasShaders) {
            if (gLCapabilities1.OpenGL21) {
                GLX.capsString += "OpenGL 2.1 is supported.\n";
                GLX.useShaderArb = false;
                GLX.GL_LINK_STATUS = 35714;
                GLX.GL_COMPILE_STATUS = 35713;
                GLX.GL_VERTEX_SHADER = 35633;
                GLX.GL_FRAGMENT_SHADER = 35632;
            }
            else {
                GLX.capsString += "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
                GLX.useShaderArb = true;
                GLX.GL_LINK_STATUS = 35714;
                GLX.GL_COMPILE_STATUS = 35713;
                GLX.GL_VERTEX_SHADER = 35633;
                GLX.GL_FRAGMENT_SHADER = 35632;
            }
        }
        else {
            GLX.capsString = GLX.capsString + "OpenGL 2.1 is " + (gLCapabilities1.OpenGL21 ? "" : "not ") + "supported, ";
            GLX.capsString = GLX.capsString + "ARB_shader_objects is " + (gLCapabilities1.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
            GLX.capsString = GLX.capsString + "ARB_vertex_shader is " + (gLCapabilities1.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
            GLX.capsString = GLX.capsString + "ARB_fragment_shader is " + (gLCapabilities1.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
        }
        GLX.usePostProcess = GLX.hasShaders;
        final String string2 = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
        GLX.isNvidia = string2.contains("nvidia");
        GLX.useVboArb = (!gLCapabilities1.OpenGL15 && gLCapabilities1.GL_ARB_vertex_buffer_object);
        GLX.capsString += "VBOs are available because ";
        if (GLX.useVboArb) {
            GLX.capsString += "ARB_vertex_buffer_object is supported.\n";
            GLX.GL_STATIC_DRAW = 35044;
            GLX.GL_ARRAY_BUFFER = 34962;
        }
        else {
            GLX.capsString += "OpenGL 1.5 is supported.\n";
            GLX.GL_STATIC_DRAW = 35044;
            GLX.GL_ARRAY_BUFFER = 34962;
        }
        GLX.isAmd = string2.contains("ati");
        if (GLX.isAmd) {
            GLX.needVbo = true;
        }
        try {
            final Processor[] arr3 = new SystemInfo().getHardware().getProcessors();
            GLX.cpuInfo = String.format("%dx %s", new Object[] { arr3.length, arr3[0] }).replaceAll("\\s+", " ");
        }
        catch (Throwable t) {}
    }
    
    public static boolean isNextGen() {
        return GLX.usePostProcess;
    }
    
    public static String getCapsString() {
        return GLX.capsString;
    }
    
    public static int glGetProgrami(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glGetObjectParameteriARB(integer1, integer2);
        }
        return GL20.glGetProgrami(integer1, integer2);
    }
    
    public static void glAttachShader(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glAttachObjectARB(integer1, integer2);
        }
        else {
            GL20.glAttachShader(integer1, integer2);
        }
    }
    
    public static void glDeleteShader(final int integer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glDeleteObjectARB(integer);
        }
        else {
            GL20.glDeleteShader(integer);
        }
    }
    
    public static int glCreateShader(final int integer) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glCreateShaderObjectARB(integer);
        }
        return GL20.glCreateShader(integer);
    }
    
    public static void glShaderSource(final int integer, final CharSequence charSequence) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glShaderSourceARB(integer, charSequence);
        }
        else {
            GL20.glShaderSource(integer, charSequence);
        }
    }
    
    public static void glCompileShader(final int integer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glCompileShaderARB(integer);
        }
        else {
            GL20.glCompileShader(integer);
        }
    }
    
    public static int glGetShaderi(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glGetObjectParameteriARB(integer1, integer2);
        }
        return GL20.glGetShaderi(integer1, integer2);
    }
    
    public static String glGetShaderInfoLog(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glGetInfoLogARB(integer1, integer2);
        }
        return GL20.glGetShaderInfoLog(integer1, integer2);
    }
    
    public static String glGetProgramInfoLog(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glGetInfoLogARB(integer1, integer2);
        }
        return GL20.glGetProgramInfoLog(integer1, integer2);
    }
    
    public static void glUseProgram(final int integer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUseProgramObjectARB(integer);
        }
        else {
            GL20.glUseProgram(integer);
        }
    }
    
    public static int glCreateProgram() {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glCreateProgramObjectARB();
        }
        return GL20.glCreateProgram();
    }
    
    public static void glDeleteProgram(final int integer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glDeleteObjectARB(integer);
        }
        else {
            GL20.glDeleteProgram(integer);
        }
    }
    
    public static void glLinkProgram(final int integer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glLinkProgramARB(integer);
        }
        else {
            GL20.glLinkProgram(integer);
        }
    }
    
    public static int glGetUniformLocation(final int integer, final CharSequence charSequence) {
        if (GLX.useShaderArb) {
            return ARBShaderObjects.glGetUniformLocationARB(integer, charSequence);
        }
        return GL20.glGetUniformLocation(integer, charSequence);
    }
    
    public static void glUniform1(final int integer, final IntBuffer intBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform1ivARB(integer, intBuffer);
        }
        else {
            GL20.glUniform1iv(integer, intBuffer);
        }
    }
    
    public static void glUniform1i(final int integer1, final int integer2) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform1iARB(integer1, integer2);
        }
        else {
            GL20.glUniform1i(integer1, integer2);
        }
    }
    
    public static void glUniform1(final int integer, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform1fvARB(integer, floatBuffer);
        }
        else {
            GL20.glUniform1fv(integer, floatBuffer);
        }
    }
    
    public static void glUniform2(final int integer, final IntBuffer intBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform2ivARB(integer, intBuffer);
        }
        else {
            GL20.glUniform2iv(integer, intBuffer);
        }
    }
    
    public static void glUniform2(final int integer, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform2fvARB(integer, floatBuffer);
        }
        else {
            GL20.glUniform2fv(integer, floatBuffer);
        }
    }
    
    public static void glUniform3(final int integer, final IntBuffer intBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform3ivARB(integer, intBuffer);
        }
        else {
            GL20.glUniform3iv(integer, intBuffer);
        }
    }
    
    public static void glUniform3(final int integer, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform3fvARB(integer, floatBuffer);
        }
        else {
            GL20.glUniform3fv(integer, floatBuffer);
        }
    }
    
    public static void glUniform4(final int integer, final IntBuffer intBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform4ivARB(integer, intBuffer);
        }
        else {
            GL20.glUniform4iv(integer, intBuffer);
        }
    }
    
    public static void glUniform4(final int integer, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniform4fvARB(integer, floatBuffer);
        }
        else {
            GL20.glUniform4fv(integer, floatBuffer);
        }
    }
    
    public static void glUniformMatrix2(final int integer, final boolean boolean2, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniformMatrix2fvARB(integer, boolean2, floatBuffer);
        }
        else {
            GL20.glUniformMatrix2fv(integer, boolean2, floatBuffer);
        }
    }
    
    public static void glUniformMatrix3(final int integer, final boolean boolean2, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniformMatrix3fvARB(integer, boolean2, floatBuffer);
        }
        else {
            GL20.glUniformMatrix3fv(integer, boolean2, floatBuffer);
        }
    }
    
    public static void glUniformMatrix4(final int integer, final boolean boolean2, final FloatBuffer floatBuffer) {
        if (GLX.useShaderArb) {
            ARBShaderObjects.glUniformMatrix4fvARB(integer, boolean2, floatBuffer);
        }
        else {
            GL20.glUniformMatrix4fv(integer, boolean2, floatBuffer);
        }
    }
    
    public static int glGetAttribLocation(final int integer, final CharSequence charSequence) {
        if (GLX.useShaderArb) {
            return ARBVertexShader.glGetAttribLocationARB(integer, charSequence);
        }
        return GL20.glGetAttribLocation(integer, charSequence);
    }
    
    public static int glGenBuffers() {
        if (GLX.useVboArb) {
            return ARBVertexBufferObject.glGenBuffersARB();
        }
        return GL15.glGenBuffers();
    }
    
    public static void glGenBuffers(final IntBuffer intBuffer) {
        if (GLX.useVboArb) {
            ARBVertexBufferObject.glGenBuffersARB(intBuffer);
        }
        else {
            GL15.glGenBuffers(intBuffer);
        }
    }
    
    public static void glBindBuffer(final int integer1, final int integer2) {
        if (GLX.useVboArb) {
            ARBVertexBufferObject.glBindBufferARB(integer1, integer2);
        }
        else {
            GL15.glBindBuffer(integer1, integer2);
        }
    }
    
    public static void glBufferData(final int integer1, final ByteBuffer byteBuffer, final int integer3) {
        if (GLX.useVboArb) {
            ARBVertexBufferObject.glBufferDataARB(integer1, byteBuffer, integer3);
        }
        else {
            GL15.glBufferData(integer1, byteBuffer, integer3);
        }
    }
    
    public static void glDeleteBuffers(final int integer) {
        if (GLX.useVboArb) {
            ARBVertexBufferObject.glDeleteBuffersARB(integer);
        }
        else {
            GL15.glDeleteBuffers(integer);
        }
    }
    
    public static void glDeleteBuffers(final IntBuffer intBuffer) {
        if (GLX.useVboArb) {
            ARBVertexBufferObject.glDeleteBuffersARB(intBuffer);
        }
        else {
            GL15.glDeleteBuffers(intBuffer);
        }
    }
    
    public static boolean useVbo() {
        return true;
    }
    
    public static void glBindFramebuffer(final int integer1, final int integer2) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glBindFramebuffer(integer1, integer2);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer(integer1, integer2);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT(integer1, integer2);
                break;
            }
        }
    }
    
    public static void glBindRenderbuffer(final int integer1, final int integer2) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glBindRenderbuffer(integer1, integer2);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindRenderbuffer(integer1, integer2);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindRenderbufferEXT(integer1, integer2);
                break;
            }
        }
    }
    
    public static void glDeleteRenderbuffers(final int integer) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glDeleteRenderbuffers(integer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteRenderbuffers(integer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteRenderbuffersEXT(integer);
                break;
            }
        }
    }
    
    public static void glDeleteFramebuffers(final int integer) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers(integer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers(integer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT(integer);
                break;
            }
        }
    }
    
    public static int glGenFramebuffers() {
        switch (GLX.fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
            default: {
                return -1;
            }
        }
    }
    
    public static int glGenRenderbuffers() {
        switch (GLX.fboMode) {
            case BASE: {
                return GL30.glGenRenderbuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenRenderbuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenRenderbuffersEXT();
            }
            default: {
                return -1;
            }
        }
    }
    
    public static void glRenderbufferStorage(final int integer1, final int integer2, final int integer3, final int integer4) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glRenderbufferStorage(integer1, integer2, integer3, integer4);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glRenderbufferStorage(integer1, integer2, integer3, integer4);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glRenderbufferStorageEXT(integer1, integer2, integer3, integer4);
                break;
            }
        }
    }
    
    public static void glFramebufferRenderbuffer(final int integer1, final int integer2, final int integer3, final int integer4) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glFramebufferRenderbuffer(integer1, integer2, integer3, integer4);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferRenderbuffer(integer1, integer2, integer3, integer4);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferRenderbufferEXT(integer1, integer2, integer3, integer4);
                break;
            }
        }
    }
    
    public static int glCheckFramebufferStatus(final int integer) {
        switch (GLX.fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus(integer);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus(integer);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(integer);
            }
            default: {
                return -1;
            }
        }
    }
    
    public static void glFramebufferTexture2D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        switch (GLX.fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D(integer1, integer2, integer3, integer4, integer5);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D(integer1, integer2, integer3, integer4, integer5);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT(integer1, integer2, integer3, integer4, integer5);
                break;
            }
        }
    }
    
    public static int getBoundFramebuffer() {
        switch (GLX.fboMode) {
            case BASE: {
                return GlStateManager.getInteger(36006);
            }
            case ARB: {
                return GlStateManager.getInteger(36006);
            }
            case EXT: {
                return GlStateManager.getInteger(36006);
            }
            default: {
                return 0;
            }
        }
    }
    
    public static void glActiveTexture(final int integer) {
        if (GLX.useMultitextureArb) {
            ARBMultitexture.glActiveTextureARB(integer);
        }
        else {
            GL13.glActiveTexture(integer);
        }
    }
    
    public static void glClientActiveTexture(final int integer) {
        if (GLX.useMultitextureArb) {
            ARBMultitexture.glClientActiveTextureARB(integer);
        }
        else {
            GL13.glClientActiveTexture(integer);
        }
    }
    
    public static void glMultiTexCoord2f(final int integer, final float float2, final float float3) {
        if (GLX.useMultitextureArb) {
            ARBMultitexture.glMultiTexCoord2fARB(integer, float2, float3);
        }
        else {
            GL13.glMultiTexCoord2f(integer, float2, float3);
        }
    }
    
    public static void glBlendFuncSeparate(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (GLX.separateBlend) {
            if (GLX.useSeparateBlendExt) {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT(integer1, integer2, integer3, integer4);
            }
            else {
                GL14.glBlendFuncSeparate(integer1, integer2, integer3, integer4);
            }
        }
        else {
            GL11.glBlendFunc(integer1, integer2);
        }
    }
    
    public static boolean isUsingFBOs() {
        return true;
    }
    
    public static String getCpuInfo() {
        return (GLX.cpuInfo == null) ? "<unknown>" : GLX.cpuInfo;
    }
    
    public static void renderCrosshair(final int integer) {
        renderCrosshair(integer, true, true, true);
    }
    
    public static void renderCrosshair(final int integer, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        final Tesselator cuz5 = Tesselator.getInstance();
        final BufferBuilder cuw6 = cuz5.getBuilder();
        GL11.glLineWidth(4.0f);
        cuw6.begin(1, DefaultVertexFormat.POSITION_COLOR);
        if (boolean2) {
            cuw6.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuw6.vertex(integer, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
        }
        if (boolean3) {
            cuw6.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuw6.vertex(0.0, integer, 0.0).color(0, 0, 0, 255).endVertex();
        }
        if (boolean4) {
            cuw6.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuw6.vertex(0.0, 0.0, integer).color(0, 0, 0, 255).endVertex();
        }
        cuz5.end();
        GL11.glLineWidth(2.0f);
        cuw6.begin(1, DefaultVertexFormat.POSITION_COLOR);
        if (boolean2) {
            cuw6.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
            cuw6.vertex(integer, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
        }
        if (boolean3) {
            cuw6.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
            cuw6.vertex(0.0, integer, 0.0).color(0, 255, 0, 255).endVertex();
        }
        if (boolean4) {
            cuw6.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).endVertex();
            cuw6.vertex(0.0, 0.0, integer).color(127, 127, 255, 255).endVertex();
        }
        cuz5.end();
        GL11.glLineWidth(1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }
    
    public static String getErrorString(final int integer) {
        return (String)GLX.LOOKUP_MAP.get(integer);
    }
    
    public static <T> T make(final Supplier<T> supplier) {
        return (T)supplier.get();
    }
    
    public static <T> T make(final T object, final Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GLX.capsString = "";
        LOOKUP_MAP = GLX.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put(0, "No error");
            hashMap.put(1280, "Enum parameter is invalid for this function");
            hashMap.put(1281, "Parameter is invalid for this function");
            hashMap.put(1282, "Current state is invalid for this function");
            hashMap.put(1283, "Stack overflow");
            hashMap.put(1284, "Stack underflow");
            hashMap.put(1285, "Out of memory");
            hashMap.put(1286, "Operation on incomplete framebuffer");
            hashMap.put(1286, "Operation on incomplete framebuffer");
        }));
    }
    
    enum FboMode {
        BASE, 
        ARB, 
        EXT;
    }
}

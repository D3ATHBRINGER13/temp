package com.mojang.blaze3d.platform;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import javax.annotation.Nullable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.io.IOException;
import org.lwjgl.stb.STBImage;
import org.lwjgl.glfw.GLFWImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryStack;
import java.util.function.BiConsumer;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFW;
import java.util.Optional;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.apache.logging.log4j.Logger;

public final class Window implements AutoCloseable {
    private static final Logger LOGGER;
    private final GLFWErrorCallback defaultErrorCallback;
    private final WindowEventHandler minecraft;
    private final ScreenManager screenManager;
    private final long window;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> preferredFullscreenVideoMode;
    private boolean fullscreen;
    private boolean actuallyFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int guiScaledWidth;
    private int guiScaledHeight;
    private double guiScale;
    private String errorSection;
    private boolean dirty;
    private double lastDrawTime;
    private int framerateLimit;
    private boolean vsync;
    
    public Window(final WindowEventHandler cup, final ScreenManager cul, final DisplayData cuc, final String string4, final String string5) {
        this.defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
        this.errorSection = "";
        this.lastDrawTime = Double.MIN_VALUE;
        this.screenManager = cul;
        this.setBootGlErrorCallback();
        this.setGlErrorSection("Pre startup");
        this.minecraft = cup;
        final Optional<VideoMode> optional7 = VideoMode.read(string4);
        if (optional7.isPresent()) {
            this.preferredFullscreenVideoMode = optional7;
        }
        else if (cuc.fullscreenWidth.isPresent() && cuc.fullscreenHeight.isPresent()) {
            this.preferredFullscreenVideoMode = (Optional<VideoMode>)Optional.of(new VideoMode(cuc.fullscreenWidth.getAsInt(), cuc.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
        }
        else {
            this.preferredFullscreenVideoMode = (Optional<VideoMode>)Optional.empty();
        }
        final boolean isFullscreen = cuc.isFullscreen;
        this.fullscreen = isFullscreen;
        this.actuallyFullscreen = isFullscreen;
        final Monitor cuh8 = cul.getMonitor(GLFW.glfwGetPrimaryMonitor());
        final int n = (cuc.width > 0) ? cuc.width : 1;
        this.width = n;
        this.windowedWidth = n;
        final int n2 = (cuc.height > 0) ? cuc.height : 1;
        this.height = n2;
        this.windowedHeight = n2;
        GLFW.glfwDefaultWindowHints();
        this.window = GLFW.glfwCreateWindow(this.width, this.height, (CharSequence)string5, (this.fullscreen && cuh8 != null) ? cuh8.getMonitor() : 0L, 0L);
        if (cuh8 != null) {
            final VideoMode cun9 = cuh8.getPreferredVidMode((Optional<VideoMode>)(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty()));
            final int n3 = cuh8.getX() + cun9.getWidth() / 2 - this.width / 2;
            this.x = n3;
            this.windowedX = n3;
            final int n4 = cuh8.getY() + cun9.getHeight() / 2 - this.height / 2;
            this.y = n4;
            this.windowedY = n4;
        }
        else {
            final int[] arr9 = { 0 };
            final int[] arr10 = { 0 };
            GLFW.glfwGetWindowPos(this.window, arr9, arr10);
            final int n5 = arr9[0];
            this.x = n5;
            this.windowedX = n5;
            final int n6 = arr10[0];
            this.y = n6;
            this.windowedY = n6;
        }
        GLFW.glfwMakeContextCurrent(this.window);
        GL.createCapabilities();
        this.setMode();
        this.refreshFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback(this.window, this::onFramebufferResize);
        GLFW.glfwSetWindowPosCallback(this.window, this::onMove);
        GLFW.glfwSetWindowSizeCallback(this.window, this::onResize);
        GLFW.glfwSetWindowFocusCallback(this.window, this::onFocus);
    }
    
    public static void checkGlfwError(final BiConsumer<Integer, String> biConsumer) {
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final PointerBuffer pointerBuffer4 = memoryStack2.mallocPointer(1);
            final int integer5 = GLFW.glfwGetError(pointerBuffer4);
            if (integer5 != 0) {
                final long long6 = pointerBuffer4.get();
                final String string8 = (long6 == 0L) ? "" : MemoryUtil.memUTF8(long6);
                biConsumer.accept(integer5, string8);
            }
        }
    }
    
    public void setupGuiState(final boolean boolean1) {
        GlStateManager.clear(256, boolean1);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.getWidth() / this.getGuiScale(), this.getHeight() / this.getGuiScale(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
    }
    
    public void setIcon(final InputStream inputStream1, final InputStream inputStream2) {
        try (final MemoryStack memoryStack4 = MemoryStack.stackPush()) {
            if (inputStream1 == null) {
                throw new FileNotFoundException("icons/icon_16x16.png");
            }
            if (inputStream2 == null) {
                throw new FileNotFoundException("icons/icon_32x32.png");
            }
            final IntBuffer intBuffer6 = memoryStack4.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack4.mallocInt(1);
            final IntBuffer intBuffer8 = memoryStack4.mallocInt(1);
            final GLFWImage.Buffer buffer9 = GLFWImage.mallocStack(2, memoryStack4);
            final ByteBuffer byteBuffer10 = this.readIconPixels(inputStream1, intBuffer6, intBuffer7, intBuffer8);
            if (byteBuffer10 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer9.position(0);
            buffer9.width(intBuffer6.get(0));
            buffer9.height(intBuffer7.get(0));
            buffer9.pixels(byteBuffer10);
            final ByteBuffer byteBuffer11 = this.readIconPixels(inputStream2, intBuffer6, intBuffer7, intBuffer8);
            if (byteBuffer11 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer9.position(1);
            buffer9.width(intBuffer6.get(0));
            buffer9.height(intBuffer7.get(0));
            buffer9.pixels(byteBuffer11);
            buffer9.position(0);
            GLFW.glfwSetWindowIcon(this.window, buffer9);
            STBImage.stbi_image_free(byteBuffer10);
            STBImage.stbi_image_free(byteBuffer11);
        }
        catch (IOException iOException4) {
            Window.LOGGER.error("Couldn't set icon", (Throwable)iOException4);
        }
    }
    
    @Nullable
    private ByteBuffer readIconPixels(final InputStream inputStream, final IntBuffer intBuffer2, final IntBuffer intBuffer3, final IntBuffer intBuffer4) throws IOException {
        ByteBuffer byteBuffer6 = null;
        try {
            byteBuffer6 = TextureUtil.readResource(inputStream);
            byteBuffer6.rewind();
            return STBImage.stbi_load_from_memory(byteBuffer6, intBuffer2, intBuffer3, intBuffer4, 0);
        }
        finally {
            if (byteBuffer6 != null) {
                MemoryUtil.memFree((Buffer)byteBuffer6);
            }
        }
    }
    
    public void setGlErrorSection(final String string) {
        this.errorSection = string;
    }
    
    private void setBootGlErrorCallback() {
        GLFW.glfwSetErrorCallback(Window::bootCrash);
    }
    
    private static void bootCrash(final int integer, final long long2) {
        throw new IllegalStateException(new StringBuilder().append("GLFW error ").append(integer).append(": ").append(MemoryUtil.memUTF8(long2)).toString());
    }
    
    public void defaultErrorCallback(final int integer, final long long2) {
        final String string5 = MemoryUtil.memUTF8(long2);
        Window.LOGGER.error("########## GL ERROR ##########");
        Window.LOGGER.error("@ {}", this.errorSection);
        Window.LOGGER.error("{}: {}", integer, string5);
    }
    
    public void setDefaultGlErrorCallback() {
        GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.defaultErrorCallback).free();
    }
    
    public void updateVsync(final boolean boolean1) {
        GLFW.glfwSwapInterval((int)((this.vsync = boolean1) ? 1 : 0));
    }
    
    public void close() {
        Callbacks.glfwFreeCallbacks(this.window);
        this.defaultErrorCallback.close();
        GLFW.glfwDestroyWindow(this.window);
        GLFW.glfwTerminate();
    }
    
    private void onMove(final long long1, final int integer2, final int integer3) {
        this.x = integer2;
        this.y = integer3;
    }
    
    private void onFramebufferResize(final long long1, final int integer2, final int integer3) {
        if (long1 != this.window) {
            return;
        }
        final int integer4 = this.getWidth();
        final int integer5 = this.getHeight();
        if (integer2 == 0 || integer3 == 0) {
            return;
        }
        this.framebufferWidth = integer2;
        this.framebufferHeight = integer3;
        if (this.getWidth() != integer4 || this.getHeight() != integer5) {
            this.minecraft.resizeDisplay();
        }
    }
    
    private void refreshFramebufferSize() {
        final int[] arr2 = { 0 };
        final int[] arr3 = { 0 };
        GLFW.glfwGetFramebufferSize(this.window, arr2, arr3);
        this.framebufferWidth = arr2[0];
        this.framebufferHeight = arr3[0];
    }
    
    private void onResize(final long long1, final int integer2, final int integer3) {
        this.width = integer2;
        this.height = integer3;
    }
    
    private void onFocus(final long long1, final boolean boolean2) {
        if (long1 == this.window) {
            this.minecraft.setWindowActive(boolean2);
        }
    }
    
    public void setFramerateLimit(final int integer) {
        this.framerateLimit = integer;
    }
    
    public int getFramerateLimit() {
        return this.framerateLimit;
    }
    
    public void updateDisplay(final boolean boolean1) {
        GLFW.glfwSwapBuffers(this.window);
        pollEventQueue();
        if (this.fullscreen != this.actuallyFullscreen) {
            this.actuallyFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync);
        }
    }
    
    public void limitDisplayFPS() {
        double double2;
        double double3;
        for (double2 = this.lastDrawTime + 1.0 / this.getFramerateLimit(), double3 = GLFW.glfwGetTime(); double3 < double2; double3 = GLFW.glfwGetTime()) {
            GLFW.glfwWaitEventsTimeout(double2 - double3);
        }
        this.lastDrawTime = double3;
    }
    
    public Optional<VideoMode> getPreferredFullscreenVideoMode() {
        return this.preferredFullscreenVideoMode;
    }
    
    public void setPreferredFullscreenVideoMode(final Optional<VideoMode> optional) {
        final boolean boolean3 = !optional.equals(this.preferredFullscreenVideoMode);
        this.preferredFullscreenVideoMode = optional;
        if (boolean3) {
            this.dirty = true;
        }
    }
    
    public void changeFullscreenVideoMode() {
        if (this.fullscreen && this.dirty) {
            this.dirty = false;
            this.setMode();
            this.minecraft.resizeDisplay();
        }
    }
    
    private void setMode() {
        final boolean boolean2 = GLFW.glfwGetWindowMonitor(this.window) != 0L;
        if (this.fullscreen) {
            final Monitor cuh3 = this.screenManager.findBestMonitor(this);
            if (cuh3 == null) {
                Window.LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            }
            else {
                final VideoMode cun4 = cuh3.getPreferredVidMode(this.preferredFullscreenVideoMode);
                if (!boolean2) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = cun4.getWidth();
                this.height = cun4.getHeight();
                GLFW.glfwSetWindowMonitor(this.window, cuh3.getMonitor(), this.x, this.y, this.width, this.height, cun4.getRefreshRate());
            }
        }
        else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor(this.window, 0L, this.x, this.y, this.width, this.height, -1);
        }
    }
    
    public void toggleFullScreen() {
        this.fullscreen = !this.fullscreen;
    }
    
    private void updateFullscreen(final boolean boolean1) {
        try {
            this.setMode();
            this.minecraft.resizeDisplay();
            this.updateVsync(boolean1);
            this.minecraft.updateDisplay(false);
        }
        catch (Exception exception3) {
            Window.LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception3);
        }
    }
    
    public int calculateScale(final int integer, final boolean boolean2) {
        int integer2;
        for (integer2 = 1; integer2 != integer && integer2 < this.framebufferWidth && integer2 < this.framebufferHeight && this.framebufferWidth / (integer2 + 1) >= 320 && this.framebufferHeight / (integer2 + 1) >= 240; ++integer2) {}
        if (boolean2 && integer2 % 2 != 0) {
            ++integer2;
        }
        return integer2;
    }
    
    public void setGuiScale(final double double1) {
        this.guiScale = double1;
        final int integer4 = (int)(this.framebufferWidth / double1);
        this.guiScaledWidth = ((this.framebufferWidth / double1 > integer4) ? (integer4 + 1) : integer4);
        final int integer5 = (int)(this.framebufferHeight / double1);
        this.guiScaledHeight = ((this.framebufferHeight / double1 > integer5) ? (integer5 + 1) : integer5);
    }
    
    public long getWindow() {
        return this.window;
    }
    
    public boolean isFullscreen() {
        return this.fullscreen;
    }
    
    public int getWidth() {
        return this.framebufferWidth;
    }
    
    public int getHeight() {
        return this.framebufferHeight;
    }
    
    public static void pollEventQueue() {
        GLFW.glfwPollEvents();
    }
    
    public int getScreenWidth() {
        return this.width;
    }
    
    public int getScreenHeight() {
        return this.height;
    }
    
    public int getGuiScaledWidth() {
        return this.guiScaledWidth;
    }
    
    public int getGuiScaledHeight() {
        return this.guiScaledHeight;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public double getGuiScale() {
        return this.guiScale;
    }
    
    @Nullable
    public Monitor findBestMonitor() {
        return this.screenManager.findBestMonitor(this);
    }
    
    public void updateRawMouseInput(final boolean boolean1) {
        InputConstants.updateRawMouseInput(this.window, boolean1);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

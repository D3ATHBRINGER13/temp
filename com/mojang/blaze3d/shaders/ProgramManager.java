package com.mojang.blaze3d.shaders;

import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import com.mojang.blaze3d.platform.GLX;
import org.apache.logging.log4j.Logger;

public class ProgramManager {
    private static final Logger LOGGER;
    private static ProgramManager instance;
    
    public static void createInstance() {
        ProgramManager.instance = new ProgramManager();
    }
    
    public static ProgramManager getInstance() {
        return ProgramManager.instance;
    }
    
    private ProgramManager() {
    }
    
    public void releaseProgram(final Effect cus) {
        cus.getFragmentProgram().close();
        cus.getVertexProgram().close();
        GLX.glDeleteProgram(cus.getId());
    }
    
    public int createProgram() throws IOException {
        final int integer2 = GLX.glCreateProgram();
        if (integer2 <= 0) {
            throw new IOException(new StringBuilder().append("Could not create shader program (returned program ID ").append(integer2).append(")").toString());
        }
        return integer2;
    }
    
    public void linkProgram(final Effect cus) throws IOException {
        cus.getFragmentProgram().attachToEffect(cus);
        cus.getVertexProgram().attachToEffect(cus);
        GLX.glLinkProgram(cus.getId());
        final int integer3 = GLX.glGetProgrami(cus.getId(), GLX.GL_LINK_STATUS);
        if (integer3 == 0) {
            ProgramManager.LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", cus.getVertexProgram().getName(), cus.getFragmentProgram().getName());
            ProgramManager.LOGGER.warn(GLX.glGetProgramInfoLog(cus.getId(), 32768));
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

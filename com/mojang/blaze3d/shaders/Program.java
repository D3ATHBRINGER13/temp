package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.InputStream;
import com.mojang.blaze3d.platform.GLX;

public class Program {
    private final Type type;
    private final String name;
    private final int id;
    private int references;
    
    private Program(final Type a, final int integer, final String string) {
        this.type = a;
        this.id = integer;
        this.name = string;
    }
    
    public void attachToEffect(final Effect cus) {
        ++this.references;
        GLX.glAttachShader(cus.getId(), this.id);
    }
    
    public void close() {
        --this.references;
        if (this.references <= 0) {
            GLX.glDeleteShader(this.id);
            this.type.getPrograms().remove(this.name);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public static Program compileShader(final Type a, final String string, final InputStream inputStream) throws IOException {
        final String string2 = TextureUtil.readResourceAsString(inputStream);
        if (string2 == null) {
            throw new IOException("Could not load program " + a.getName());
        }
        final int integer5 = GLX.glCreateShader(a.getGlType());
        GLX.glShaderSource(integer5, (CharSequence)string2);
        GLX.glCompileShader(integer5);
        if (GLX.glGetShaderi(integer5, GLX.GL_COMPILE_STATUS) == 0) {
            final String string3 = StringUtils.trim(GLX.glGetShaderInfoLog(integer5, 32768));
            throw new IOException("Couldn't compile " + a.getName() + " program: " + string3);
        }
        final Program cut6 = new Program(a, integer5, string);
        a.getPrograms().put(string, cut6);
        return cut6;
    }
    
    public enum Type {
        VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER), 
        FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);
        
        private final String name;
        private final String extension;
        private final int glType;
        private final Map<String, Program> programs;
        
        private Type(final String string3, final String string4, final int integer5) {
            this.programs = (Map<String, Program>)Maps.newHashMap();
            this.name = string3;
            this.extension = string4;
            this.glType = integer5;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getExtension() {
            return this.extension;
        }
        
        private int getGlType() {
            return this.glType;
        }
        
        public Map<String, Program> getPrograms() {
            return this.programs;
        }
    }
}

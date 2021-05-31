package net.minecraft.client.renderer;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonArray;
import net.minecraft.server.ChainedJsonException;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import java.util.Iterator;
import com.mojang.blaze3d.shaders.ProgramManager;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import net.minecraft.resources.ResourceLocation;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Uniform;
import java.util.List;
import java.util.Map;
import com.mojang.blaze3d.shaders.AbstractUniform;
import org.apache.logging.log4j.Logger;
import com.mojang.blaze3d.shaders.Effect;

public class EffectInstance implements Effect, AutoCloseable {
    private static final Logger LOGGER;
    private static final AbstractUniform DUMMY_UNIFORM;
    private static EffectInstance lastAppliedEffect;
    private static int lastProgramId;
    private final Map<String, Object> samplerMap;
    private final List<String> samplerNames;
    private final List<Integer> samplerLocations;
    private final List<Uniform> uniforms;
    private final List<Integer> uniformLocations;
    private final Map<String, Uniform> uniformMap;
    private final int programId;
    private final String name;
    private final boolean cull;
    private boolean dirty;
    private final BlendMode blend;
    private final List<Integer> attributes;
    private final List<String> attributeNames;
    private final Program vertexProgram;
    private final Program fragmentProgram;
    
    public EffectInstance(final ResourceManager xi, final String string) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.<init>:()V
        //     4: aload_0         /* this */
        //     5: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //     8: putfield        net/minecraft/client/renderer/EffectInstance.samplerMap:Ljava/util/Map;
        //    11: aload_0         /* this */
        //    12: invokestatic    com/google/common/collect/Lists.newArrayList:()Ljava/util/ArrayList;
        //    15: putfield        net/minecraft/client/renderer/EffectInstance.samplerNames:Ljava/util/List;
        //    18: aload_0         /* this */
        //    19: invokestatic    com/google/common/collect/Lists.newArrayList:()Ljava/util/ArrayList;
        //    22: putfield        net/minecraft/client/renderer/EffectInstance.samplerLocations:Ljava/util/List;
        //    25: aload_0         /* this */
        //    26: invokestatic    com/google/common/collect/Lists.newArrayList:()Ljava/util/ArrayList;
        //    29: putfield        net/minecraft/client/renderer/EffectInstance.uniforms:Ljava/util/List;
        //    32: aload_0         /* this */
        //    33: invokestatic    com/google/common/collect/Lists.newArrayList:()Ljava/util/ArrayList;
        //    36: putfield        net/minecraft/client/renderer/EffectInstance.uniformLocations:Ljava/util/List;
        //    39: aload_0         /* this */
        //    40: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //    43: putfield        net/minecraft/client/renderer/EffectInstance.uniformMap:Ljava/util/Map;
        //    46: new             Lnet/minecraft/resources/ResourceLocation;
        //    49: dup            
        //    50: new             Ljava/lang/StringBuilder;
        //    53: dup            
        //    54: invokespecial   java/lang/StringBuilder.<init>:()V
        //    57: ldc             "shaders/program/"
        //    59: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    62: aload_2         /* string */
        //    63: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    66: ldc             ".json"
        //    68: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    71: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    74: invokespecial   net/minecraft/resources/ResourceLocation.<init>:(Ljava/lang/String;)V
        //    77: astore_3        /* qv4 */
        //    78: aload_0         /* this */
        //    79: aload_2         /* string */
        //    80: putfield        net/minecraft/client/renderer/EffectInstance.name:Ljava/lang/String;
        //    83: aconst_null    
        //    84: astore          xh5
        //    86: aload_1         /* xi */
        //    87: aload_3         /* qv4 */
        //    88: invokeinterface net/minecraft/server/packs/resources/ResourceManager.getResource:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/Resource;
        //    93: astore          xh5
        //    95: new             Ljava/io/InputStreamReader;
        //    98: dup            
        //    99: aload           xh5
        //   101: invokeinterface net/minecraft/server/packs/resources/Resource.getInputStream:()Ljava/io/InputStream;
        //   106: getstatic       java/nio/charset/StandardCharsets.UTF_8:Ljava/nio/charset/Charset;
        //   109: invokespecial   java/io/InputStreamReader.<init>:(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
        //   112: invokestatic    net/minecraft/util/GsonHelper.parse:(Ljava/io/Reader;)Lcom/google/gson/JsonObject;
        //   115: astore          jsonObject6
        //   117: aload           jsonObject6
        //   119: ldc             "vertex"
        //   121: invokestatic    net/minecraft/util/GsonHelper.getAsString:(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;
        //   124: astore          string7
        //   126: aload           jsonObject6
        //   128: ldc             "fragment"
        //   130: invokestatic    net/minecraft/util/GsonHelper.getAsString:(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;
        //   133: astore          string8
        //   135: aload           jsonObject6
        //   137: ldc             "samplers"
        //   139: aconst_null    
        //   140: invokestatic    net/minecraft/util/GsonHelper.getAsJsonArray:(Lcom/google/gson/JsonObject;Ljava/lang/String;Lcom/google/gson/JsonArray;)Lcom/google/gson/JsonArray;
        //   143: astore          jsonArray9
        //   145: aload           jsonArray9
        //   147: ifnull          239
        //   150: iconst_0       
        //   151: istore          integer10
        //   153: aload           jsonArray9
        //   155: invokevirtual   com/google/gson/JsonArray.iterator:()Ljava/util/Iterator;
        //   158: astore          10
        //   160: aload           10
        //   162: invokeinterface java/util/Iterator.hasNext:()Z
        //   167: ifeq            239
        //   170: aload           10
        //   172: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   177: checkcast       Lcom/google/gson/JsonElement;
        //   180: astore          jsonElement12
        //   182: aload_0         /* this */
        //   183: aload           jsonElement12
        //   185: invokespecial   net/minecraft/client/renderer/EffectInstance.parseSamplerNode:(Lcom/google/gson/JsonElement;)V
        //   188: goto            233
        //   191: astore          exception13
        //   193: aload           exception13
        //   195: invokestatic    net/minecraft/server/ChainedJsonException.forException:(Ljava/lang/Exception;)Lnet/minecraft/server/ChainedJsonException;
        //   198: astore          qy14
        //   200: aload           qy14
        //   202: new             Ljava/lang/StringBuilder;
        //   205: dup            
        //   206: invokespecial   java/lang/StringBuilder.<init>:()V
        //   209: ldc             "samplers["
        //   211: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   214: iload           integer10
        //   216: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   219: ldc             "]"
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   227: invokevirtual   net/minecraft/server/ChainedJsonException.prependJsonKey:(Ljava/lang/String;)V
        //   230: aload           qy14
        //   232: athrow         
        //   233: iinc            integer10, 1
        //   236: goto            160
        //   239: aload           jsonObject6
        //   241: ldc             "attributes"
        //   243: aconst_null    
        //   244: invokestatic    net/minecraft/util/GsonHelper.getAsJsonArray:(Lcom/google/gson/JsonObject;Ljava/lang/String;Lcom/google/gson/JsonArray;)Lcom/google/gson/JsonArray;
        //   247: astore          jsonArray10
        //   249: aload           jsonArray10
        //   251: ifnull          381
        //   254: iconst_0       
        //   255: istore          integer11
        //   257: aload_0         /* this */
        //   258: aload           jsonArray10
        //   260: invokevirtual   com/google/gson/JsonArray.size:()I
        //   263: invokestatic    com/google/common/collect/Lists.newArrayListWithCapacity:(I)Ljava/util/ArrayList;
        //   266: putfield        net/minecraft/client/renderer/EffectInstance.attributes:Ljava/util/List;
        //   269: aload_0         /* this */
        //   270: aload           jsonArray10
        //   272: invokevirtual   com/google/gson/JsonArray.size:()I
        //   275: invokestatic    com/google/common/collect/Lists.newArrayListWithCapacity:(I)Ljava/util/ArrayList;
        //   278: putfield        net/minecraft/client/renderer/EffectInstance.attributeNames:Ljava/util/List;
        //   281: aload           jsonArray10
        //   283: invokevirtual   com/google/gson/JsonArray.iterator:()Ljava/util/Iterator;
        //   286: astore          11
        //   288: aload           11
        //   290: invokeinterface java/util/Iterator.hasNext:()Z
        //   295: ifeq            378
        //   298: aload           11
        //   300: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   305: checkcast       Lcom/google/gson/JsonElement;
        //   308: astore          jsonElement13
        //   310: aload_0         /* this */
        //   311: getfield        net/minecraft/client/renderer/EffectInstance.attributeNames:Ljava/util/List;
        //   314: aload           jsonElement13
        //   316: ldc             "attribute"
        //   318: invokestatic    net/minecraft/util/GsonHelper.convertToString:(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;
        //   321: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   326: pop            
        //   327: goto            372
        //   330: astore          exception14
        //   332: aload           exception14
        //   334: invokestatic    net/minecraft/server/ChainedJsonException.forException:(Ljava/lang/Exception;)Lnet/minecraft/server/ChainedJsonException;
        //   337: astore          qy15
        //   339: aload           qy15
        //   341: new             Ljava/lang/StringBuilder;
        //   344: dup            
        //   345: invokespecial   java/lang/StringBuilder.<init>:()V
        //   348: ldc             "attributes["
        //   350: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   353: iload           integer11
        //   355: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   358: ldc             "]"
        //   360: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   363: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   366: invokevirtual   net/minecraft/server/ChainedJsonException.prependJsonKey:(Ljava/lang/String;)V
        //   369: aload           qy15
        //   371: athrow         
        //   372: iinc            integer11, 1
        //   375: goto            288
        //   378: goto            391
        //   381: aload_0         /* this */
        //   382: aconst_null    
        //   383: putfield        net/minecraft/client/renderer/EffectInstance.attributes:Ljava/util/List;
        //   386: aload_0         /* this */
        //   387: aconst_null    
        //   388: putfield        net/minecraft/client/renderer/EffectInstance.attributeNames:Ljava/util/List;
        //   391: aload           jsonObject6
        //   393: ldc             "uniforms"
        //   395: aconst_null    
        //   396: invokestatic    net/minecraft/util/GsonHelper.getAsJsonArray:(Lcom/google/gson/JsonObject;Ljava/lang/String;Lcom/google/gson/JsonArray;)Lcom/google/gson/JsonArray;
        //   399: astore          jsonArray11
        //   401: aload           jsonArray11
        //   403: ifnull          495
        //   406: iconst_0       
        //   407: istore          integer12
        //   409: aload           jsonArray11
        //   411: invokevirtual   com/google/gson/JsonArray.iterator:()Ljava/util/Iterator;
        //   414: astore          12
        //   416: aload           12
        //   418: invokeinterface java/util/Iterator.hasNext:()Z
        //   423: ifeq            495
        //   426: aload           12
        //   428: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   433: checkcast       Lcom/google/gson/JsonElement;
        //   436: astore          jsonElement14
        //   438: aload_0         /* this */
        //   439: aload           jsonElement14
        //   441: invokespecial   net/minecraft/client/renderer/EffectInstance.parseUniformNode:(Lcom/google/gson/JsonElement;)V
        //   444: goto            489
        //   447: astore          exception15
        //   449: aload           exception15
        //   451: invokestatic    net/minecraft/server/ChainedJsonException.forException:(Ljava/lang/Exception;)Lnet/minecraft/server/ChainedJsonException;
        //   454: astore          qy16
        //   456: aload           qy16
        //   458: new             Ljava/lang/StringBuilder;
        //   461: dup            
        //   462: invokespecial   java/lang/StringBuilder.<init>:()V
        //   465: ldc             "uniforms["
        //   467: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   470: iload           integer12
        //   472: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   475: ldc             "]"
        //   477: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   480: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   483: invokevirtual   net/minecraft/server/ChainedJsonException.prependJsonKey:(Ljava/lang/String;)V
        //   486: aload           qy16
        //   488: athrow         
        //   489: iinc            integer12, 1
        //   492: goto            416
        //   495: aload_0         /* this */
        //   496: aload           jsonObject6
        //   498: ldc             "blend"
        //   500: aconst_null    
        //   501: invokestatic    net/minecraft/util/GsonHelper.getAsJsonObject:(Lcom/google/gson/JsonObject;Ljava/lang/String;Lcom/google/gson/JsonObject;)Lcom/google/gson/JsonObject;
        //   504: invokestatic    net/minecraft/client/renderer/EffectInstance.parseBlendNode:(Lcom/google/gson/JsonObject;)Lcom/mojang/blaze3d/shaders/BlendMode;
        //   507: putfield        net/minecraft/client/renderer/EffectInstance.blend:Lcom/mojang/blaze3d/shaders/BlendMode;
        //   510: aload_0         /* this */
        //   511: aload           jsonObject6
        //   513: ldc             "cull"
        //   515: iconst_1       
        //   516: invokestatic    net/minecraft/util/GsonHelper.getAsBoolean:(Lcom/google/gson/JsonObject;Ljava/lang/String;Z)Z
        //   519: putfield        net/minecraft/client/renderer/EffectInstance.cull:Z
        //   522: aload_0         /* this */
        //   523: aload_1         /* xi */
        //   524: getstatic       com/mojang/blaze3d/shaders/Program$Type.VERTEX:Lcom/mojang/blaze3d/shaders/Program$Type;
        //   527: aload           string7
        //   529: invokestatic    net/minecraft/client/renderer/EffectInstance.getOrCreate:(Lnet/minecraft/server/packs/resources/ResourceManager;Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/Program;
        //   532: putfield        net/minecraft/client/renderer/EffectInstance.vertexProgram:Lcom/mojang/blaze3d/shaders/Program;
        //   535: aload_0         /* this */
        //   536: aload_1         /* xi */
        //   537: getstatic       com/mojang/blaze3d/shaders/Program$Type.FRAGMENT:Lcom/mojang/blaze3d/shaders/Program$Type;
        //   540: aload           string8
        //   542: invokestatic    net/minecraft/client/renderer/EffectInstance.getOrCreate:(Lnet/minecraft/server/packs/resources/ResourceManager;Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/Program;
        //   545: putfield        net/minecraft/client/renderer/EffectInstance.fragmentProgram:Lcom/mojang/blaze3d/shaders/Program;
        //   548: aload_0         /* this */
        //   549: invokestatic    com/mojang/blaze3d/shaders/ProgramManager.getInstance:()Lcom/mojang/blaze3d/shaders/ProgramManager;
        //   552: invokevirtual   com/mojang/blaze3d/shaders/ProgramManager.createProgram:()I
        //   555: putfield        net/minecraft/client/renderer/EffectInstance.programId:I
        //   558: invokestatic    com/mojang/blaze3d/shaders/ProgramManager.getInstance:()Lcom/mojang/blaze3d/shaders/ProgramManager;
        //   561: aload_0         /* this */
        //   562: invokevirtual   com/mojang/blaze3d/shaders/ProgramManager.linkProgram:(Lcom/mojang/blaze3d/shaders/Effect;)V
        //   565: aload_0         /* this */
        //   566: invokespecial   net/minecraft/client/renderer/EffectInstance.updateLocations:()V
        //   569: aload_0         /* this */
        //   570: getfield        net/minecraft/client/renderer/EffectInstance.attributeNames:Ljava/util/List;
        //   573: ifnull          638
        //   576: aload_0         /* this */
        //   577: getfield        net/minecraft/client/renderer/EffectInstance.attributeNames:Ljava/util/List;
        //   580: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   585: astore          11
        //   587: aload           11
        //   589: invokeinterface java/util/Iterator.hasNext:()Z
        //   594: ifeq            638
        //   597: aload           11
        //   599: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   604: checkcast       Ljava/lang/String;
        //   607: astore          string13
        //   609: aload_0         /* this */
        //   610: getfield        net/minecraft/client/renderer/EffectInstance.programId:I
        //   613: aload           string13
        //   615: invokestatic    com/mojang/blaze3d/platform/GLX.glGetAttribLocation:(ILjava/lang/CharSequence;)I
        //   618: istore          integer14
        //   620: aload_0         /* this */
        //   621: getfield        net/minecraft/client/renderer/EffectInstance.attributes:Ljava/util/List;
        //   624: iload           integer14
        //   626: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   629: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   634: pop            
        //   635: goto            587
        //   638: aload           xh5
        //   640: invokestatic    org/apache/commons/io/IOUtils.closeQuietly:(Ljava/io/Closeable;)V
        //   643: goto            677
        //   646: astore          exception7
        //   648: aload           exception7
        //   650: invokestatic    net/minecraft/server/ChainedJsonException.forException:(Ljava/lang/Exception;)Lnet/minecraft/server/ChainedJsonException;
        //   653: astore          qy8
        //   655: aload           qy8
        //   657: aload_3         /* qv4 */
        //   658: invokevirtual   net/minecraft/resources/ResourceLocation.getPath:()Ljava/lang/String;
        //   661: invokevirtual   net/minecraft/server/ChainedJsonException.setFilenameAndFlush:(Ljava/lang/String;)V
        //   664: aload           qy8
        //   666: athrow         
        //   667: astore          16
        //   669: aload           xh5
        //   671: invokestatic    org/apache/commons/io/IOUtils.closeQuietly:(Ljava/io/Closeable;)V
        //   674: aload           16
        //   676: athrow         
        //   677: aload_0         /* this */
        //   678: invokevirtual   net/minecraft/client/renderer/EffectInstance.markDirty:()V
        //   681: return         
        //    Exceptions:
        //  throws java.io.IOException
        //    MethodParameters:
        //  Name    Flags  
        //  ------  -----
        //  xi      
        //  string  
        //    StackMapTable: 00 13 FF 00 A0 00 0B 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 01 07 00 A0 00 00 FF 00 1E 00 0A 00 00 00 07 00 55 07 00 73 00 00 00 00 01 00 01 07 00 38 FF 00 29 00 0B 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 01 07 00 A0 00 00 F8 00 05 FF 00 30 00 0C 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 01 07 00 A0 00 00 FF 00 29 00 0B 00 00 00 07 00 55 07 00 73 00 00 00 00 00 01 00 01 07 00 38 FF 00 29 00 0C 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 01 07 00 A0 00 00 FF 00 05 00 08 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 02 09 FF 00 18 00 0D 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 00 01 07 00 A0 00 00 FF 00 1E 00 0C 00 00 00 07 00 55 07 00 73 00 00 00 00 00 00 01 00 01 07 00 38 FF 00 29 00 0D 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 00 01 07 00 A0 00 00 FF 00 05 00 08 07 00 02 07 00 6B 00 07 00 55 07 00 73 07 00 9C 07 00 9E 07 00 9E 00 00 FF 00 5B 00 0C 07 00 02 00 00 07 00 55 07 00 73 00 00 00 00 00 00 07 00 A0 00 00 FF 00 32 00 05 07 00 02 00 00 00 07 00 73 00 00 FF 00 07 00 05 00 00 00 07 00 55 07 00 73 00 01 07 00 38 FF 00 14 00 05 00 00 00 00 07 00 73 00 01 07 01 2D FF 00 09 00 01 07 00 02 00 00
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  182    188    191    233    Ljava/lang/Exception;
        //  310    327    330    372    Ljava/lang/Exception;
        //  438    444    447    489    Ljava/lang/Exception;
        //  86     638    646    667    Ljava/lang/Exception;
        //  86     638    667    677    Any
        //  646    669    667    677    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 0
        //     at java.base/java.util.Vector.get(Vector.java:781)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:82)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:616)
        //     at com.strobel.assembler.metadata.MetadataHelper$8.visitClassType(MetadataHelper.java:2024)
        //     at com.strobel.assembler.metadata.MetadataHelper$8.visitClassType(MetadataHelper.java:1994)
        //     at com.strobel.assembler.metadata.TypeDefinition.accept(TypeDefinition.java:183)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.asSuper(MetadataHelper.java:727)
        //     at com.strobel.assembler.metadata.MetadataHelper$6.visitClassType(MetadataHelper.java:1853)
        //     at com.strobel.assembler.metadata.MetadataHelper$6.visitClassType(MetadataHelper.java:1815)
        //     at com.strobel.assembler.metadata.TypeDefinition.accept(TypeDefinition.java:183)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubType(MetadataHelper.java:1302)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubType(MetadataHelper.java:568)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubtypeUncheckedInternal(MetadataHelper.java:540)
        //     at com.strobel.assembler.metadata.MetadataHelper.isSubTypeUnchecked(MetadataHelper.java:520)
        //     at com.strobel.assembler.metadata.MetadataHelper.isConvertible(MetadataHelper.java:507)
        //     at com.strobel.assembler.metadata.MetadataHelper.isConvertible(MetadataHelper.java:488)
        //     at com.strobel.assembler.metadata.MetadataHelper.isAssignableFrom(MetadataHelper.java:557)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperTypeCore(MetadataHelper.java:237)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperType(MetadataHelper.java:200)
        //     at com.strobel.assembler.ir.Frame.merge(Frame.java:369)
        //     at com.strobel.assembler.ir.Frame.merge(Frame.java:254)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2206)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at cuchaz.enigma.source.procyon.ProcyonDecompiler.getSource(ProcyonDecompiler.java:77)
        //     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
        //     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
        //     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
        //     at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1655)
        //     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
        //     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
        //     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:746)
        //     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
        //     at java.base/java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:408)
        //     at java.base/java.util.concurrent.ForkJoinTask.invoke(ForkJoinTask.java:736)
        //     at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateParallel(ForEachOps.java:159)
        //     at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateParallel(ForEachOps.java:173)
        //     at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:233)
        //     at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497)
        //     at cuchaz.enigma.gui.GuiController.lambda$exportSource$7(GuiController.java:218)
        //     at cuchaz.enigma.gui.dialog.ProgressDialog.lambda$runOffThread$0(ProgressDialog.java:78)
        //     at java.base/java.lang.Thread.run(Thread.java:829)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Program getOrCreate(final ResourceManager xi, final Program.Type a, final String string) throws IOException {
        Program cut4 = (Program)a.getPrograms().get(string);
        if (cut4 == null) {
            final ResourceLocation qv5 = new ResourceLocation("shaders/program/" + string + a.getExtension());
            final Resource xh6 = xi.getResource(qv5);
            try {
                cut4 = Program.compileShader(a, string, xh6.getInputStream());
            }
            finally {
                IOUtils.closeQuietly((Closeable)xh6);
            }
        }
        return cut4;
    }
    
    public static BlendMode parseBlendNode(final JsonObject jsonObject) {
        if (jsonObject == null) {
            return new BlendMode();
        }
        int integer2 = 32774;
        int integer3 = 1;
        int integer4 = 0;
        int integer5 = 1;
        int integer6 = 0;
        boolean boolean7 = true;
        boolean boolean8 = false;
        if (GsonHelper.isStringValue(jsonObject, "func")) {
            integer2 = BlendMode.stringToBlendFunc(jsonObject.get("func").getAsString());
            if (integer2 != 32774) {
                boolean7 = false;
            }
        }
        if (GsonHelper.isStringValue(jsonObject, "srcrgb")) {
            integer3 = BlendMode.stringToBlendFactor(jsonObject.get("srcrgb").getAsString());
            if (integer3 != 1) {
                boolean7 = false;
            }
        }
        if (GsonHelper.isStringValue(jsonObject, "dstrgb")) {
            integer4 = BlendMode.stringToBlendFactor(jsonObject.get("dstrgb").getAsString());
            if (integer4 != 0) {
                boolean7 = false;
            }
        }
        if (GsonHelper.isStringValue(jsonObject, "srcalpha")) {
            integer5 = BlendMode.stringToBlendFactor(jsonObject.get("srcalpha").getAsString());
            if (integer5 != 1) {
                boolean7 = false;
            }
            boolean8 = true;
        }
        if (GsonHelper.isStringValue(jsonObject, "dstalpha")) {
            integer6 = BlendMode.stringToBlendFactor(jsonObject.get("dstalpha").getAsString());
            if (integer6 != 0) {
                boolean7 = false;
            }
            boolean8 = true;
        }
        if (boolean7) {
            return new BlendMode();
        }
        if (boolean8) {
            return new BlendMode(integer3, integer4, integer5, integer6, integer2);
        }
        return new BlendMode(integer3, integer4, integer2);
    }
    
    public void close() {
        for (final Uniform cuv3 : this.uniforms) {
            cuv3.close();
        }
        ProgramManager.getInstance().releaseProgram(this);
    }
    
    public void clear() {
        GLX.glUseProgram(0);
        EffectInstance.lastProgramId = -1;
        EffectInstance.lastAppliedEffect = null;
        for (int integer2 = 0; integer2 < this.samplerLocations.size(); ++integer2) {
            if (this.samplerMap.get(this.samplerNames.get(integer2)) != null) {
                GlStateManager.activeTexture(GLX.GL_TEXTURE0 + integer2);
                GlStateManager.bindTexture(0);
            }
        }
    }
    
    public void apply() {
        this.dirty = false;
        EffectInstance.lastAppliedEffect = this;
        this.blend.apply();
        if (this.programId != EffectInstance.lastProgramId) {
            GLX.glUseProgram(this.programId);
            EffectInstance.lastProgramId = this.programId;
        }
        if (this.cull) {
            GlStateManager.enableCull();
        }
        else {
            GlStateManager.disableCull();
        }
        for (int integer2 = 0; integer2 < this.samplerLocations.size(); ++integer2) {
            if (this.samplerMap.get(this.samplerNames.get(integer2)) != null) {
                GlStateManager.activeTexture(GLX.GL_TEXTURE0 + integer2);
                GlStateManager.enableTexture();
                final Object object3 = this.samplerMap.get(this.samplerNames.get(integer2));
                int integer3 = -1;
                if (object3 instanceof RenderTarget) {
                    integer3 = ((RenderTarget)object3).colorTextureId;
                }
                else if (object3 instanceof TextureObject) {
                    integer3 = ((TextureObject)object3).getId();
                }
                else if (object3 instanceof Integer) {
                    integer3 = (int)object3;
                }
                if (integer3 != -1) {
                    GlStateManager.bindTexture(integer3);
                    GLX.glUniform1i(GLX.glGetUniformLocation(this.programId, (CharSequence)this.samplerNames.get(integer2)), integer2);
                }
            }
        }
        for (final Uniform cuv3 : this.uniforms) {
            cuv3.upload();
        }
    }
    
    public void markDirty() {
        this.dirty = true;
    }
    
    @Nullable
    public Uniform getUniform(final String string) {
        return (Uniform)this.uniformMap.get(string);
    }
    
    public AbstractUniform safeGetUniform(final String string) {
        final Uniform cuv3 = this.getUniform(string);
        return (cuv3 == null) ? EffectInstance.DUMMY_UNIFORM : cuv3;
    }
    
    private void updateLocations() {
        for (int integer2 = 0, integer3 = 0; integer2 < this.samplerNames.size(); ++integer2, ++integer3) {
            final String string4 = (String)this.samplerNames.get(integer2);
            final int integer4 = GLX.glGetUniformLocation(this.programId, (CharSequence)string4);
            if (integer4 == -1) {
                EffectInstance.LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", this.name, string4);
                this.samplerMap.remove(string4);
                this.samplerNames.remove(integer3);
                --integer3;
            }
            else {
                this.samplerLocations.add(integer4);
            }
        }
        for (final Uniform cuv3 : this.uniforms) {
            final String string4 = cuv3.getName();
            final int integer4 = GLX.glGetUniformLocation(this.programId, (CharSequence)string4);
            if (integer4 == -1) {
                EffectInstance.LOGGER.warn("Could not find uniform named {} in the specified shader program.", string4);
            }
            else {
                this.uniformLocations.add(integer4);
                cuv3.setLocation(integer4);
                this.uniformMap.put(string4, cuv3);
            }
        }
    }
    
    private void parseSamplerNode(final JsonElement jsonElement) {
        final JsonObject jsonObject3 = GsonHelper.convertToJsonObject(jsonElement, "sampler");
        final String string4 = GsonHelper.getAsString(jsonObject3, "name");
        if (!GsonHelper.isStringValue(jsonObject3, "file")) {
            this.samplerMap.put(string4, null);
            this.samplerNames.add(string4);
            return;
        }
        this.samplerNames.add(string4);
    }
    
    public void setSampler(final String string, final Object object) {
        if (this.samplerMap.containsKey(string)) {
            this.samplerMap.remove(string);
        }
        this.samplerMap.put(string, object);
        this.markDirty();
    }
    
    private void parseUniformNode(final JsonElement jsonElement) throws ChainedJsonException {
        final JsonObject jsonObject3 = GsonHelper.convertToJsonObject(jsonElement, "uniform");
        final String string4 = GsonHelper.getAsString(jsonObject3, "name");
        final int integer5 = Uniform.getTypeFromString(GsonHelper.getAsString(jsonObject3, "type"));
        final int integer6 = GsonHelper.getAsInt(jsonObject3, "count");
        final float[] arr7 = new float[Math.max(integer6, 16)];
        final JsonArray jsonArray8 = GsonHelper.getAsJsonArray(jsonObject3, "values");
        if (jsonArray8.size() != integer6 && jsonArray8.size() > 1) {
            throw new ChainedJsonException(new StringBuilder().append("Invalid amount of values specified (expected ").append(integer6).append(", found ").append(jsonArray8.size()).append(")").toString());
        }
        int integer7 = 0;
        for (final JsonElement jsonElement2 : jsonArray8) {
            try {
                arr7[integer7] = GsonHelper.convertToFloat(jsonElement2, "value");
            }
            catch (Exception exception12) {
                final ChainedJsonException qy13 = ChainedJsonException.forException(exception12);
                qy13.prependJsonKey(new StringBuilder().append("values[").append(integer7).append("]").toString());
                throw qy13;
            }
            ++integer7;
        }
        if (integer6 > 1 && jsonArray8.size() == 1) {
            while (integer7 < integer6) {
                arr7[integer7] = arr7[0];
                ++integer7;
            }
        }
        final int integer8 = (integer6 > 1 && integer6 <= 4 && integer5 < 8) ? (integer6 - 1) : 0;
        final Uniform cuv11 = new Uniform(string4, integer5 + integer8, integer6, this);
        if (integer5 <= 3) {
            cuv11.setSafe((int)arr7[0], (int)arr7[1], (int)arr7[2], (int)arr7[3]);
        }
        else if (integer5 <= 7) {
            cuv11.setSafe(arr7[0], arr7[1], arr7[2], arr7[3]);
        }
        else {
            cuv11.set(arr7);
        }
        this.uniforms.add(cuv11);
    }
    
    public Program getVertexProgram() {
        return this.vertexProgram;
    }
    
    public Program getFragmentProgram() {
        return this.fragmentProgram;
    }
    
    public int getId() {
        return this.programId;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DUMMY_UNIFORM = new AbstractUniform();
        EffectInstance.lastProgramId = -1;
    }
}

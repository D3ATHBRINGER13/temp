package net.minecraft.commands.arguments.selector.options;

import net.minecraft.network.chat.TranslatableComponent;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import java.util.function.BiConsumer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Team;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.commands.SharedSuggestionProvider;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.AdvancementProgress;
import com.mojang.brigadier.StringReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Maps;
import java.util.Iterator;
import com.mojang.brigadier.Message;
import java.util.Locale;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import java.util.function.Predicate;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Map;

public class EntitySelectorOptions {
    private static final Map<String, Option> OPTIONS;
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION;
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION;
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE;
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE;
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL;
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN;
    public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID;
    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID;
    
    private static void register(final String string, final Modifier a, final Predicate<EntitySelectorParser> predicate, final Component jo) {
        EntitySelectorOptions.OPTIONS.put(string, new Option(a, (Predicate)predicate, jo));
    }
    
    public static void bootStrap() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: invokeinterface java/util/Map.isEmpty:()Z
        //     8: ifne            12
        //    11: return         
        //    12: ldc             "name"
        //    14: invokedynamic   BootstrapMethod #0, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //    19: invokedynamic   BootstrapMethod #1, test:()Ljava/util/function/Predicate;
        //    24: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //    27: dup            
        //    28: ldc             "argument.entity.options.name.description"
        //    30: iconst_0       
        //    31: anewarray       Ljava/lang/Object;
        //    34: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    37: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //    40: ldc             "distance"
        //    42: invokedynamic   BootstrapMethod #2, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //    47: invokedynamic   BootstrapMethod #3, test:()Ljava/util/function/Predicate;
        //    52: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //    55: dup            
        //    56: ldc             "argument.entity.options.distance.description"
        //    58: iconst_0       
        //    59: anewarray       Ljava/lang/Object;
        //    62: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    65: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //    68: ldc             "level"
        //    70: invokedynamic   BootstrapMethod #4, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //    75: invokedynamic   BootstrapMethod #5, test:()Ljava/util/function/Predicate;
        //    80: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //    83: dup            
        //    84: ldc             "argument.entity.options.level.description"
        //    86: iconst_0       
        //    87: anewarray       Ljava/lang/Object;
        //    90: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    93: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //    96: ldc             "x"
        //    98: invokedynamic   BootstrapMethod #6, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   103: invokedynamic   BootstrapMethod #7, test:()Ljava/util/function/Predicate;
        //   108: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   111: dup            
        //   112: ldc             "argument.entity.options.x.description"
        //   114: iconst_0       
        //   115: anewarray       Ljava/lang/Object;
        //   118: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   121: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   124: ldc             "y"
        //   126: invokedynamic   BootstrapMethod #8, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   131: invokedynamic   BootstrapMethod #9, test:()Ljava/util/function/Predicate;
        //   136: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   139: dup            
        //   140: ldc             "argument.entity.options.y.description"
        //   142: iconst_0       
        //   143: anewarray       Ljava/lang/Object;
        //   146: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   149: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   152: ldc             "z"
        //   154: invokedynamic   BootstrapMethod #10, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   159: invokedynamic   BootstrapMethod #11, test:()Ljava/util/function/Predicate;
        //   164: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   167: dup            
        //   168: ldc             "argument.entity.options.z.description"
        //   170: iconst_0       
        //   171: anewarray       Ljava/lang/Object;
        //   174: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   177: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   180: ldc             "dx"
        //   182: invokedynamic   BootstrapMethod #12, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   187: invokedynamic   BootstrapMethod #13, test:()Ljava/util/function/Predicate;
        //   192: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   195: dup            
        //   196: ldc             "argument.entity.options.dx.description"
        //   198: iconst_0       
        //   199: anewarray       Ljava/lang/Object;
        //   202: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   205: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   208: ldc             "dy"
        //   210: invokedynamic   BootstrapMethod #14, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   215: invokedynamic   BootstrapMethod #15, test:()Ljava/util/function/Predicate;
        //   220: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   223: dup            
        //   224: ldc             "argument.entity.options.dy.description"
        //   226: iconst_0       
        //   227: anewarray       Ljava/lang/Object;
        //   230: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   233: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   236: ldc             "dz"
        //   238: invokedynamic   BootstrapMethod #16, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   243: invokedynamic   BootstrapMethod #17, test:()Ljava/util/function/Predicate;
        //   248: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   251: dup            
        //   252: ldc             "argument.entity.options.dz.description"
        //   254: iconst_0       
        //   255: anewarray       Ljava/lang/Object;
        //   258: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   261: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   264: ldc             "x_rotation"
        //   266: invokedynamic   BootstrapMethod #18, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   271: invokedynamic   BootstrapMethod #19, test:()Ljava/util/function/Predicate;
        //   276: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   279: dup            
        //   280: ldc             "argument.entity.options.x_rotation.description"
        //   282: iconst_0       
        //   283: anewarray       Ljava/lang/Object;
        //   286: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   289: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   292: ldc             "y_rotation"
        //   294: invokedynamic   BootstrapMethod #20, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   299: invokedynamic   BootstrapMethod #21, test:()Ljava/util/function/Predicate;
        //   304: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   307: dup            
        //   308: ldc             "argument.entity.options.y_rotation.description"
        //   310: iconst_0       
        //   311: anewarray       Ljava/lang/Object;
        //   314: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   317: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   320: ldc             "limit"
        //   322: invokedynamic   BootstrapMethod #22, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   327: invokedynamic   BootstrapMethod #23, test:()Ljava/util/function/Predicate;
        //   332: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   335: dup            
        //   336: ldc_w           "argument.entity.options.limit.description"
        //   339: iconst_0       
        //   340: anewarray       Ljava/lang/Object;
        //   343: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   346: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   349: ldc_w           "sort"
        //   352: invokedynamic   BootstrapMethod #24, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   357: invokedynamic   BootstrapMethod #25, test:()Ljava/util/function/Predicate;
        //   362: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   365: dup            
        //   366: ldc_w           "argument.entity.options.sort.description"
        //   369: iconst_0       
        //   370: anewarray       Ljava/lang/Object;
        //   373: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   376: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   379: ldc_w           "gamemode"
        //   382: invokedynamic   BootstrapMethod #26, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   387: invokedynamic   BootstrapMethod #27, test:()Ljava/util/function/Predicate;
        //   392: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   395: dup            
        //   396: ldc_w           "argument.entity.options.gamemode.description"
        //   399: iconst_0       
        //   400: anewarray       Ljava/lang/Object;
        //   403: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   406: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   409: ldc_w           "team"
        //   412: invokedynamic   BootstrapMethod #28, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   417: invokedynamic   BootstrapMethod #29, test:()Ljava/util/function/Predicate;
        //   422: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   425: dup            
        //   426: ldc_w           "argument.entity.options.team.description"
        //   429: iconst_0       
        //   430: anewarray       Ljava/lang/Object;
        //   433: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   436: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   439: ldc_w           "type"
        //   442: invokedynamic   BootstrapMethod #30, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   447: invokedynamic   BootstrapMethod #31, test:()Ljava/util/function/Predicate;
        //   452: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   455: dup            
        //   456: ldc_w           "argument.entity.options.type.description"
        //   459: iconst_0       
        //   460: anewarray       Ljava/lang/Object;
        //   463: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   466: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   469: ldc_w           "tag"
        //   472: invokedynamic   BootstrapMethod #32, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   477: invokedynamic   BootstrapMethod #33, test:()Ljava/util/function/Predicate;
        //   482: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   485: dup            
        //   486: ldc_w           "argument.entity.options.tag.description"
        //   489: iconst_0       
        //   490: anewarray       Ljava/lang/Object;
        //   493: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   496: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   499: ldc_w           "nbt"
        //   502: invokedynamic   BootstrapMethod #34, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   507: invokedynamic   BootstrapMethod #35, test:()Ljava/util/function/Predicate;
        //   512: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   515: dup            
        //   516: ldc_w           "argument.entity.options.nbt.description"
        //   519: iconst_0       
        //   520: anewarray       Ljava/lang/Object;
        //   523: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   526: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   529: ldc_w           "scores"
        //   532: invokedynamic   BootstrapMethod #36, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   537: invokedynamic   BootstrapMethod #37, test:()Ljava/util/function/Predicate;
        //   542: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   545: dup            
        //   546: ldc_w           "argument.entity.options.scores.description"
        //   549: iconst_0       
        //   550: anewarray       Ljava/lang/Object;
        //   553: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   556: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   559: ldc_w           "advancements"
        //   562: invokedynamic   BootstrapMethod #38, handle:()Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;
        //   567: invokedynamic   BootstrapMethod #39, test:()Ljava/util/function/Predicate;
        //   572: new             Lnet/minecraft/network/chat/TranslatableComponent;
        //   575: dup            
        //   576: ldc_w           "argument.entity.options.advancements.description"
        //   579: iconst_0       
        //   580: anewarray       Ljava/lang/Object;
        //   583: invokespecial   net/minecraft/network/chat/TranslatableComponent.<init>:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   586: invokestatic    net/minecraft/commands/arguments/selector/options/EntitySelectorOptions.register:(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V
        //   589: return         
        //    StackMapTable: 00 01 0C
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
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
        //     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.helpCC(ForkJoinPool.java:1115)
        //     at java.base/java.util.concurrent.ForkJoinPool.externalHelpComplete(ForkJoinPool.java:1957)
        //     at java.base/java.util.concurrent.ForkJoinTask.tryExternalHelp(ForkJoinTask.java:378)
        //     at java.base/java.util.concurrent.ForkJoinTask.externalAwaitDone(ForkJoinTask.java:323)
        //     at java.base/java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:412)
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
    
    public static Modifier get(final EntitySelectorParser ed, final String string, final int integer) throws CommandSyntaxException {
        final Option b4 = (Option)EntitySelectorOptions.OPTIONS.get(string);
        if (b4 == null) {
            ed.getReader().setCursor(integer);
            throw EntitySelectorOptions.ERROR_UNKNOWN_OPTION.createWithContext((ImmutableStringReader)ed.getReader(), string);
        }
        if (b4.predicate.test(ed)) {
            return b4.modifier;
        }
        throw EntitySelectorOptions.ERROR_INAPPLICABLE_OPTION.createWithContext((ImmutableStringReader)ed.getReader(), string);
    }
    
    public static void suggestNames(final EntitySelectorParser ed, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final Map.Entry<String, Option> entry5 : EntitySelectorOptions.OPTIONS.entrySet()) {
            if (((Option)entry5.getValue()).predicate.test(ed) && ((String)entry5.getKey()).toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest((String)entry5.getKey() + '=', (Message)((Option)entry5.getValue()).description);
            }
        }
    }
    
    static {
        OPTIONS = (Map)Maps.newHashMap();
        ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.unknown", new Object[] { object }));
        ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.inapplicable", new Object[] { object }));
        ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.options.distance.negative", new Object[0]));
        ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.options.level.negative", new Object[0]));
        ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.options.limit.toosmall", new Object[0]));
        ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.sort.irreversible", new Object[] { object }));
        ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.mode.invalid", new Object[] { object }));
        ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.type.invalid", new Object[] { object }));
    }
    
    static class Option {
        public final Modifier modifier;
        public final Predicate<EntitySelectorParser> predicate;
        public final Component description;
        
        private Option(final Modifier a, final Predicate<EntitySelectorParser> predicate, final Component jo) {
            this.modifier = a;
            this.predicate = predicate;
            this.description = jo;
        }
    }
    
    public interface Modifier {
        void handle(final EntitySelectorParser ed) throws CommandSyntaxException;
    }
}

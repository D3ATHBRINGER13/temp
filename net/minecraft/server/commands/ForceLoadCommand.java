package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.server.level.ServerLevel;
import it.unimi.dsi.fastutil.longs.LongSet;
import com.google.common.base.Joiner;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ColumnPos;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class ForceLoadCommand {
    private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS;
    private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING;
    private static final SimpleCommandExceptionType ERROR_ALL_ADDED;
    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires(cd -> cd.hasPermission(2))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgument.columnPos()).executes(commandContext -> changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), true))).then(Commands.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgument.columnPos()).executes(commandContext -> changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgument.columnPos()).executes(commandContext -> changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), false))).then(Commands.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgument.columnPos()).executes(commandContext -> changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "to"), false))))).then(Commands.literal("all").executes(commandContext -> removeAll((CommandSourceStack)commandContext.getSource()))))).then(((LiteralArgumentBuilder)Commands.literal("query").executes(commandContext -> listForceLoad((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgument.columnPos()).executes(commandContext -> queryForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "pos"))))));
    }
    
    private static int queryForceLoad(final CommandSourceStack cd, final ColumnPos va) throws CommandSyntaxException {
        final ChunkPos bhd3 = new ChunkPos(va.x >> 4, va.z >> 4);
        final DimensionType byn4 = cd.getLevel().getDimension().getType();
        final boolean boolean5 = cd.getServer().getLevel(byn4).getForcedChunks().contains(bhd3.toLong());
        if (boolean5) {
            cd.sendSuccess(new TranslatableComponent("commands.forceload.query.success", new Object[] { bhd3, byn4 }), false);
            return 1;
        }
        throw ForceLoadCommand.ERROR_NOT_TICKING.create(bhd3, byn4);
    }
    
    private static int listForceLoad(final CommandSourceStack cd) {
        final DimensionType byn2 = cd.getLevel().getDimension().getType();
        final LongSet longSet3 = cd.getServer().getLevel(byn2).getForcedChunks();
        final int integer4 = longSet3.size();
        if (integer4 > 0) {
            final String string5 = Joiner.on(", ").join(longSet3.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (integer4 == 1) {
                cd.sendSuccess(new TranslatableComponent("commands.forceload.list.single", new Object[] { byn2, string5 }), false);
            }
            else {
                cd.sendSuccess(new TranslatableComponent("commands.forceload.list.multiple", new Object[] { integer4, byn2, string5 }), false);
            }
        }
        else {
            cd.sendFailure(new TranslatableComponent("commands.forceload.added.none", new Object[] { byn2 }));
        }
        return integer4;
    }
    
    private static int removeAll(final CommandSourceStack cd) {
        final DimensionType byn2 = cd.getLevel().getDimension().getType();
        final ServerLevel vk3 = cd.getServer().getLevel(byn2);
        final LongSet longSet4 = vk3.getForcedChunks();
        longSet4.forEach(long2 -> vk3.setChunkForced(ChunkPos.getX(long2), ChunkPos.getZ(long2), false));
        cd.sendSuccess(new TranslatableComponent("commands.forceload.removed.all", new Object[] { byn2 }), true);
        return 0;
    }
    
    private static int changeForceLoad(final CommandSourceStack cd, final ColumnPos va2, final ColumnPos va3, final boolean boolean4) throws CommandSyntaxException {
        final int integer5 = Math.min(va2.x, va3.x);
        final int integer6 = Math.min(va2.z, va3.z);
        final int integer7 = Math.max(va2.x, va3.x);
        final int integer8 = Math.max(va2.z, va3.z);
        if (integer5 < -30000000 || integer6 < -30000000 || integer7 >= 30000000 || integer8 >= 30000000) {
            throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
        }
        final int integer9 = integer5 >> 4;
        final int integer10 = integer6 >> 4;
        final int integer11 = integer7 >> 4;
        final int integer12 = integer8 >> 4;
        final long long13 = (integer11 - integer9 + 1L) * (integer12 - integer10 + 1L);
        if (long13 > 256L) {
            throw ForceLoadCommand.ERROR_TOO_MANY_CHUNKS.create(256, long13);
        }
        final DimensionType byn15 = cd.getLevel().getDimension().getType();
        final ServerLevel vk16 = cd.getServer().getLevel(byn15);
        ChunkPos bhd17 = null;
        int integer13 = 0;
        for (int integer14 = integer9; integer14 <= integer11; ++integer14) {
            for (int integer15 = integer10; integer15 <= integer12; ++integer15) {
                final boolean boolean5 = vk16.setChunkForced(integer14, integer15, boolean4);
                if (boolean5) {
                    ++integer13;
                    if (bhd17 == null) {
                        bhd17 = new ChunkPos(integer14, integer15);
                    }
                }
            }
        }
        if (integer13 == 0) {
            throw (boolean4 ? ForceLoadCommand.ERROR_ALL_ADDED : ForceLoadCommand.ERROR_NONE_REMOVED).create();
        }
        if (integer13 == 1) {
            cd.sendSuccess(new TranslatableComponent(new StringBuilder().append("commands.forceload.").append(boolean4 ? "added" : "removed").append(".single").toString(), new Object[] { bhd17, byn15 }), true);
        }
        else {
            final ChunkPos bhd18 = new ChunkPos(integer9, integer10);
            final ChunkPos bhd19 = new ChunkPos(integer11, integer12);
            cd.sendSuccess(new TranslatableComponent(new StringBuilder().append("commands.forceload.").append(boolean4 ? "added" : "removed").append(".multiple").toString(), new Object[] { integer13, byn15, bhd18, bhd19 }), true);
        }
        return integer13;
    }
    
    static {
        ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.forceload.toobig", new Object[] { object1, object2 }));
        ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.forceload.query.failure", new Object[] { object1, object2 }));
        ERROR_ALL_ADDED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.forceload.added.failure", new Object[0]));
        ERROR_NONE_REMOVED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.forceload.removed.failure", new Object[0]));
    }
}

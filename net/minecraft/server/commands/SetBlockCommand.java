package net.minecraft.server.commands;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.LevelReader;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SetBlockCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires(cd -> cd.hasPermission(2))).then(Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockStateArgument.block()).executes(commandContext -> setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))).then(Commands.literal("destroy").executes(commandContext -> setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.DESTROY, null)))).then(Commands.literal("keep").executes(commandContext -> setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)(bvx -> bvx.getLevel().isEmptyBlock(bvx.getPos())))))).then(Commands.literal("replace").executes(commandContext -> setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))))));
    }
    
    private static int setBlock(final CommandSourceStack cd, final BlockPos ew, final BlockInput de, final Mode b, @Nullable final Predicate<BlockInWorld> predicate) throws CommandSyntaxException {
        final ServerLevel vk6 = cd.getLevel();
        if (predicate != null && !predicate.test(new BlockInWorld(vk6, ew, true))) {
            throw SetBlockCommand.ERROR_FAILED.create();
        }
        boolean boolean7;
        if (b == Mode.DESTROY) {
            vk6.destroyBlock(ew, true);
            boolean7 = !de.getState().isAir();
        }
        else {
            final BlockEntity btw8 = vk6.getBlockEntity(ew);
            Clearable.tryClear(btw8);
            boolean7 = true;
        }
        if (boolean7 && !de.place(vk6, ew, 2)) {
            throw SetBlockCommand.ERROR_FAILED.create();
        }
        vk6.blockUpdated(ew, de.getState().getBlock());
        cd.sendSuccess(new TranslatableComponent("commands.setblock.success", new Object[] { ew.getX(), ew.getY(), ew.getZ() }), true);
        return 1;
    }
    
    static {
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.setblock.failed", new Object[0]));
    }
    
    public enum Mode {
        REPLACE, 
        DESTROY;
    }
    
    public interface Filter {
        @Nullable
        BlockInput filter(final BoundingBox cic, final BlockPos ew, final BlockInput de, final ServerLevel vk);
    }
}

package net.minecraft.server.commands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Set;
import java.util.Collections;
import net.minecraft.world.level.block.Blocks;
import com.mojang.brigadier.Message;
import net.minecraft.core.Vec3i;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.server.level.ServerLevel;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import java.util.function.Predicate;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.arguments.blocks.BlockInput;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class FillCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE;
    private static final BlockInput HOLLOW_CORE;
    private static final SimpleCommandExceptionType ERROR_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires(cd -> cd.hasPermission(2))).then(Commands.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(Commands.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockStateArgument.block()).executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)Commands.literal("replace").executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))).then(Commands.argument("filter", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPredicateArgument.blockPredicate()).executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter")))))).then(Commands.literal("keep").executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)(bvx -> bvx.getLevel().isEmptyBlock(bvx.getPos())))))).then(Commands.literal("outline").executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.OUTLINE, null)))).then(Commands.literal("hollow").executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.HOLLOW, null)))).then(Commands.literal("destroy").executes(commandContext -> fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.DESTROY, null)))))));
    }
    
    private static int fillBlocks(final CommandSourceStack cd, final BoundingBox cic, final BlockInput de, final Mode a, @Nullable final Predicate<BlockInWorld> predicate) throws CommandSyntaxException {
        final int integer6 = cic.getXSpan() * cic.getYSpan() * cic.getZSpan();
        if (integer6 > 32768) {
            throw FillCommand.ERROR_AREA_TOO_LARGE.create(32768, integer6);
        }
        final List<BlockPos> list7 = (List<BlockPos>)Lists.newArrayList();
        final ServerLevel vk8 = cd.getLevel();
        int integer7 = 0;
        for (final BlockPos ew11 : BlockPos.betweenClosed(cic.x0, cic.y0, cic.z0, cic.x1, cic.y1, cic.z1)) {
            if (predicate != null && !predicate.test(new BlockInWorld(vk8, ew11, true))) {
                continue;
            }
            final BlockInput de2 = a.filter.filter(cic, ew11, de, vk8);
            if (de2 == null) {
                continue;
            }
            final BlockEntity btw13 = vk8.getBlockEntity(ew11);
            Clearable.tryClear(btw13);
            if (!de2.place(vk8, ew11, 2)) {
                continue;
            }
            list7.add(ew11.immutable());
            ++integer7;
        }
        for (final BlockPos ew11 : list7) {
            final Block bmv12 = vk8.getBlockState(ew11).getBlock();
            vk8.blockUpdated(ew11, bmv12);
        }
        if (integer7 == 0) {
            throw FillCommand.ERROR_FAILED.create();
        }
        cd.sendSuccess(new TranslatableComponent("commands.fill.success", new Object[] { integer7 }), true);
        return integer7;
    }
    
    static {
        ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.fill.toobig", new Object[] { object1, object2 }));
        HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), (Set<Property<?>>)Collections.emptySet(), null);
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.fill.failed", new Object[0]));
    }
    
    enum Mode {
        REPLACE((cic, ew, de, vk) -> de), 
        OUTLINE((cic, ew, de, vk) -> {
            if (ew.getX() == cic.x0 || ew.getX() == cic.x1 || ew.getY() == cic.y0 || ew.getY() == cic.y1 || ew.getZ() == cic.z0 || ew.getZ() == cic.z1) {
                return de;
            }
            else {
                return null;
            }
        }), 
        HOLLOW((cic, ew, de, vk) -> {
            if (ew.getX() == cic.x0 || ew.getX() == cic.x1 || ew.getY() == cic.y0 || ew.getY() == cic.y1 || ew.getZ() == cic.z0 || ew.getZ() == cic.z1) {
                return de;
            }
            else {
                return FillCommand.HOLLOW_CORE;
            }
        }), 
        DESTROY((cic, ew, de, vk) -> {
            vk.destroyBlock(ew, true);
            return de;
        });
        
        public final SetBlockCommand.Filter filter;
        
        private Mode(final SetBlockCommand.Filter a) {
            this.filter = a;
        }
    }
}

package net.minecraft.server.commands;

import javax.annotation.Nullable;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Deque;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.List;
import java.util.Collection;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import com.google.common.collect.Lists;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import java.util.function.Predicate;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CloneCommands {
    private static final SimpleCommandExceptionType ERROR_OVERLAP;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE;
    private static final SimpleCommandExceptionType ERROR_FAILED;
    public static final Predicate<BlockInWorld> FILTER_AIR;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires(cd -> cd.hasPermission(2))).then(Commands.argument("begin", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(Commands.argument("end", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), (Predicate<BlockInWorld>)(bvx -> true), Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), (Predicate<BlockInWorld>)(bvx -> true), Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), (Predicate<BlockInWorld>)(bvx -> true), Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), (Predicate<BlockInWorld>)(bvx -> true), Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), (Predicate<BlockInWorld>)(bvx -> true), Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), CloneCommands.FILTER_AIR, Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), CloneCommands.FILTER_AIR, Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), CloneCommands.FILTER_AIR, Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), CloneCommands.FILTER_AIR, Mode.NORMAL))))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPredicateArgument.blockPredicate()).executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.NORMAL)))))))));
    }
    
    private static int clone(final CommandSourceStack cd, final BlockPos ew2, final BlockPos ew3, final BlockPos ew4, final Predicate<BlockInWorld> predicate, final Mode b) throws CommandSyntaxException {
        final BoundingBox cic7 = new BoundingBox(ew2, ew3);
        final BlockPos ew5 = ew4.offset(cic7.getLength());
        final BoundingBox cic8 = new BoundingBox(ew4, ew5);
        if (!b.canOverlap() && cic8.intersects(cic7)) {
            throw CloneCommands.ERROR_OVERLAP.create();
        }
        final int integer10 = cic7.getXSpan() * cic7.getYSpan() * cic7.getZSpan();
        if (integer10 > 32768) {
            throw CloneCommands.ERROR_AREA_TOO_LARGE.create(32768, integer10);
        }
        final ServerLevel vk11 = cd.getLevel();
        if (!vk11.hasChunksAt(ew2, ew3) || !vk11.hasChunksAt(ew4, ew5)) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
        final List<CloneBlockInfo> list12 = (List<CloneBlockInfo>)Lists.newArrayList();
        final List<CloneBlockInfo> list13 = (List<CloneBlockInfo>)Lists.newArrayList();
        final List<CloneBlockInfo> list14 = (List<CloneBlockInfo>)Lists.newArrayList();
        final Deque<BlockPos> deque15 = (Deque<BlockPos>)Lists.newLinkedList();
        final BlockPos ew6 = new BlockPos(cic8.x0 - cic7.x0, cic8.y0 - cic7.y0, cic8.z0 - cic7.z0);
        for (int integer11 = cic7.z0; integer11 <= cic7.z1; ++integer11) {
            for (int integer12 = cic7.y0; integer12 <= cic7.y1; ++integer12) {
                for (int integer13 = cic7.x0; integer13 <= cic7.x1; ++integer13) {
                    final BlockPos ew7 = new BlockPos(integer13, integer12, integer11);
                    final BlockPos ew8 = ew7.offset(ew6);
                    final BlockInWorld bvx22 = new BlockInWorld(vk11, ew7, false);
                    final BlockState bvt23 = bvx22.getState();
                    if (predicate.test(bvx22)) {
                        final BlockEntity btw24 = vk11.getBlockEntity(ew7);
                        if (btw24 != null) {
                            final CompoundTag id25 = btw24.save(new CompoundTag());
                            list13.add(new CloneBlockInfo(ew8, bvt23, id25));
                            deque15.addLast(ew7);
                        }
                        else if (bvt23.isSolidRender(vk11, ew7) || bvt23.isCollisionShapeFullBlock(vk11, ew7)) {
                            list12.add(new CloneBlockInfo(ew8, bvt23, null));
                            deque15.addLast(ew7);
                        }
                        else {
                            list14.add(new CloneBlockInfo(ew8, bvt23, null));
                            deque15.addFirst(ew7);
                        }
                    }
                }
            }
        }
        if (b == Mode.MOVE) {
            for (final BlockPos ew9 : deque15) {
                final BlockEntity btw25 = vk11.getBlockEntity(ew9);
                Clearable.tryClear(btw25);
                vk11.setBlock(ew9, Blocks.BARRIER.defaultBlockState(), 2);
            }
            for (final BlockPos ew9 : deque15) {
                vk11.setBlock(ew9, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        final List<CloneBlockInfo> list15 = (List<CloneBlockInfo>)Lists.newArrayList();
        list15.addAll((Collection)list12);
        list15.addAll((Collection)list13);
        list15.addAll((Collection)list14);
        final List<CloneBlockInfo> list16 = (List<CloneBlockInfo>)Lists.reverse((List)list15);
        for (final CloneBlockInfo a20 : list16) {
            final BlockEntity btw26 = vk11.getBlockEntity(a20.pos);
            Clearable.tryClear(btw26);
            vk11.setBlock(a20.pos, Blocks.BARRIER.defaultBlockState(), 2);
        }
        int integer13 = 0;
        for (final CloneBlockInfo a21 : list15) {
            if (vk11.setBlock(a21.pos, a21.state, 2)) {
                ++integer13;
            }
        }
        for (final CloneBlockInfo a21 : list13) {
            final BlockEntity btw27 = vk11.getBlockEntity(a21.pos);
            if (a21.tag != null && btw27 != null) {
                a21.tag.putInt("x", a21.pos.getX());
                a21.tag.putInt("y", a21.pos.getY());
                a21.tag.putInt("z", a21.pos.getZ());
                btw27.load(a21.tag);
                btw27.setChanged();
            }
            vk11.setBlock(a21.pos, a21.state, 2);
        }
        for (final CloneBlockInfo a21 : list16) {
            vk11.blockUpdated(a21.pos, a21.state.getBlock());
        }
        vk11.getBlockTicks().copy(cic7, ew6);
        if (integer13 == 0) {
            throw CloneCommands.ERROR_FAILED.create();
        }
        cd.sendSuccess(new TranslatableComponent("commands.clone.success", new Object[] { integer13 }), true);
        return integer13;
    }
    
    static {
        ERROR_OVERLAP = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.clone.overlap", new Object[0]));
        ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.clone.toobig", new Object[] { object1, object2 }));
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.clone.failed", new Object[0]));
        FILTER_AIR = (bvx -> !bvx.getState().isAir());
    }
    
    enum Mode {
        FORCE(true), 
        MOVE(true), 
        NORMAL(false);
        
        private final boolean canOverlap;
        
        private Mode(final boolean boolean3) {
            this.canOverlap = boolean3;
        }
        
        public boolean canOverlap() {
            return this.canOverlap;
        }
    }
    
    static class CloneBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag tag;
        
        public CloneBlockInfo(final BlockPos ew, final BlockState bvt, @Nullable final CompoundTag id) {
            this.pos = ew;
            this.state = bvt;
            this.tag = id;
        }
    }
}

package net.minecraft.server.commands.data;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BlockDataAccessor implements DataAccessor {
    private static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY;
    public static final Function<String, DataCommands.DataProvider> PROVIDER;
    private final BlockEntity entity;
    private final BlockPos pos;
    
    public BlockDataAccessor(final BlockEntity btw, final BlockPos ew) {
        this.entity = btw;
        this.pos = ew;
    }
    
    public void setData(final CompoundTag id) {
        id.putInt("x", this.pos.getX());
        id.putInt("y", this.pos.getY());
        id.putInt("z", this.pos.getZ());
        this.entity.load(id);
        this.entity.setChanged();
        final BlockState bvt3 = this.entity.getLevel().getBlockState(this.pos);
        this.entity.getLevel().sendBlockUpdated(this.pos, bvt3, bvt3, 3);
    }
    
    public CompoundTag getData() {
        return this.entity.save(new CompoundTag());
    }
    
    public Component getModifiedSuccess() {
        return new TranslatableComponent("commands.data.block.modified", new Object[] { this.pos.getX(), this.pos.getY(), this.pos.getZ() });
    }
    
    public Component getPrintSuccess(final Tag iu) {
        return new TranslatableComponent("commands.data.block.query", new Object[] { this.pos.getX(), this.pos.getY(), this.pos.getZ(), iu.getPrettyDisplay() });
    }
    
    public Component getPrintSuccess(final NbtPathArgument.NbtPath h, final double double2, final int integer) {
        return new TranslatableComponent("commands.data.block.get", new Object[] { h, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", new Object[] { double2 }), integer });
    }
    
    static {
        ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.block.invalid", new Object[0]));
        PROVIDER = (string -> new DataCommands.DataProvider() {
            final /* synthetic */ String val$argPrefix;
            
            public DataAccessor access(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                final BlockPos ew3 = BlockPosArgument.getLoadedBlockPos(commandContext, string + "Pos");
                final BlockEntity btw4 = ((CommandSourceStack)commandContext.getSource()).getLevel().getBlockEntity(ew3);
                if (btw4 == null) {
                    throw BlockDataAccessor.ERROR_NOT_A_BLOCK_ENTITY.create();
                }
                return new BlockDataAccessor(btw4, ew3);
            }
            
            public ArgumentBuilder<CommandSourceStack, ?> wrap(final ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, final Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
                return argumentBuilder.then(Commands.literal("block").then((ArgumentBuilder)function.apply(Commands.argument(string + "Pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()))));
            }
        });
    }
}

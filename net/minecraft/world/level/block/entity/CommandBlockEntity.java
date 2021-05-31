package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CommandBlock;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseCommandBlock;

public class CommandBlockEntity extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean sendToClient;
    private final BaseCommandBlock commandBlock;
    
    public CommandBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
        this.commandBlock = new BaseCommandBlock() {
            @Override
            public void setCommand(final String string) {
                super.setCommand(string);
                CommandBlockEntity.this.setChanged();
            }
            
            @Override
            public ServerLevel getLevel() {
                return (ServerLevel)CommandBlockEntity.this.level;
            }
            
            @Override
            public void onUpdated() {
                final BlockState bvt2 = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
                this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, bvt2, bvt2, 3);
            }
            
            @Override
            public Vec3 getPosition() {
                return new Vec3(CommandBlockEntity.this.worldPosition.getX() + 0.5, CommandBlockEntity.this.worldPosition.getY() + 0.5, CommandBlockEntity.this.worldPosition.getZ() + 0.5);
            }
            
            @Override
            public CommandSourceStack createCommandSourceStack() {
                return new CommandSourceStack(this, new Vec3(CommandBlockEntity.this.worldPosition.getX() + 0.5, CommandBlockEntity.this.worldPosition.getY() + 0.5, CommandBlockEntity.this.worldPosition.getZ() + 0.5), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), null);
            }
        };
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        this.commandBlock.save(id);
        id.putBoolean("powered", this.isPowered());
        id.putBoolean("conditionMet", this.wasConditionMet());
        id.putBoolean("auto", this.isAutomatic());
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.commandBlock.load(id);
        this.powered = id.getBoolean("powered");
        this.conditionMet = id.getBoolean("conditionMet");
        this.setAutomatic(id.getBoolean("auto"));
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        if (this.isSendToClient()) {
            this.setSendToClient(false);
            final CompoundTag id2 = this.save(new CompoundTag());
            return new ClientboundBlockEntityDataPacket(this.worldPosition, 2, id2);
        }
        return null;
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
    
    public BaseCommandBlock getCommandBlock() {
        return this.commandBlock;
    }
    
    public void setPowered(final boolean boolean1) {
        this.powered = boolean1;
    }
    
    public boolean isPowered() {
        return this.powered;
    }
    
    public boolean isAutomatic() {
        return this.auto;
    }
    
    public void setAutomatic(final boolean boolean1) {
        final boolean boolean2 = this.auto;
        this.auto = boolean1;
        if (!boolean2 && boolean1 && !this.powered && this.level != null && this.getMode() != Mode.SEQUENCE) {
            final Block bmv4 = this.getBlockState().getBlock();
            if (bmv4 instanceof CommandBlock) {
                this.markConditionMet();
                this.level.getBlockTicks().scheduleTick(this.worldPosition, bmv4, bmv4.getTickDelay(this.level));
            }
        }
    }
    
    public boolean wasConditionMet() {
        return this.conditionMet;
    }
    
    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            final BlockPos ew2 = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).<Direction>getValue((Property<Direction>)CommandBlock.FACING).getOpposite());
            if (this.level.getBlockState(ew2).getBlock() instanceof CommandBlock) {
                final BlockEntity btw3 = this.level.getBlockEntity(ew2);
                this.conditionMet = (btw3 instanceof CommandBlockEntity && ((CommandBlockEntity)btw3).getCommandBlock().getSuccessCount() > 0);
            }
            else {
                this.conditionMet = false;
            }
        }
        return this.conditionMet;
    }
    
    public boolean isSendToClient() {
        return this.sendToClient;
    }
    
    public void setSendToClient(final boolean boolean1) {
        this.sendToClient = boolean1;
    }
    
    public Mode getMode() {
        final Block bmv2 = this.getBlockState().getBlock();
        if (bmv2 == Blocks.COMMAND_BLOCK) {
            return Mode.REDSTONE;
        }
        if (bmv2 == Blocks.REPEATING_COMMAND_BLOCK) {
            return Mode.AUTO;
        }
        if (bmv2 == Blocks.CHAIN_COMMAND_BLOCK) {
            return Mode.SEQUENCE;
        }
        return Mode.REDSTONE;
    }
    
    public boolean isConditional() {
        final BlockState bvt2 = this.level.getBlockState(this.getBlockPos());
        return bvt2.getBlock() instanceof CommandBlock && bvt2.<Boolean>getValue((Property<Boolean>)CommandBlock.CONDITIONAL);
    }
    
    @Override
    public void clearRemoved() {
        this.clearCache();
        super.clearRemoved();
    }
    
    public enum Mode {
        SEQUENCE, 
        AUTO, 
        REDSTONE;
    }
}

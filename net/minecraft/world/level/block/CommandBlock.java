package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.util.StringUtil;
import java.util.Random;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BaseEntityBlock {
    private static final Logger LOGGER;
    public static final DirectionProperty FACING;
    public static final BooleanProperty CONDITIONAL;
    
    public CommandBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)CommandBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)CommandBlock.CONDITIONAL, false));
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        final CommandBlockEntity bub3 = new CommandBlockEntity();
        bub3.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
        return bub3;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew3);
        if (!(btw8 instanceof CommandBlockEntity)) {
            return;
        }
        final CommandBlockEntity bub9 = (CommandBlockEntity)btw8;
        final boolean boolean7 = bhr.hasNeighborSignal(ew3);
        final boolean boolean8 = bub9.isPowered();
        bub9.setPowered(boolean7);
        if (boolean8 || bub9.isAutomatic() || bub9.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
            return;
        }
        if (boolean7) {
            bub9.markConditionMet();
            bhr.getBlockTicks().scheduleTick(ew3, this, this.getTickDelay(bhr));
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        if (btw6 instanceof CommandBlockEntity) {
            final CommandBlockEntity bub7 = (CommandBlockEntity)btw6;
            final BaseCommandBlock bgx8 = bub7.getCommandBlock();
            final boolean boolean9 = !StringUtil.isNullOrEmpty(bgx8.getCommand());
            final CommandBlockEntity.Mode a10 = bub7.getMode();
            final boolean boolean10 = bub7.wasConditionMet();
            if (a10 == CommandBlockEntity.Mode.AUTO) {
                bub7.markConditionMet();
                if (boolean10) {
                    this.execute(bvt, bhr, ew, bgx8, boolean9);
                }
                else if (bub7.isConditional()) {
                    bgx8.setSuccessCount(0);
                }
                if (bub7.isPowered() || bub7.isAutomatic()) {
                    bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
                }
            }
            else if (a10 == CommandBlockEntity.Mode.REDSTONE) {
                if (boolean10) {
                    this.execute(bvt, bhr, ew, bgx8, boolean9);
                }
                else if (bub7.isConditional()) {
                    bgx8.setSuccessCount(0);
                }
            }
            bhr.updateNeighbourForOutputSignal(ew, this);
        }
    }
    
    private void execute(final BlockState bvt, final Level bhr, final BlockPos ew, final BaseCommandBlock bgx, final boolean boolean5) {
        if (boolean5) {
            bgx.performCommand(bhr);
        }
        else {
            bgx.setSuccessCount(0);
        }
        executeChain(bhr, ew, bvt.<Direction>getValue((Property<Direction>)CommandBlock.FACING));
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 1;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof CommandBlockEntity && awg.canUseGameMasterBlocks()) {
            awg.openCommandBlock((CommandBlockEntity)btw8);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof CommandBlockEntity) {
            return ((CommandBlockEntity)btw5).getCommandBlock().getSuccessCount();
        }
        return 0;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (!(btw7 instanceof CommandBlockEntity)) {
            return;
        }
        final CommandBlockEntity bub8 = (CommandBlockEntity)btw7;
        final BaseCommandBlock bgx9 = bub8.getCommandBlock();
        if (bcj.hasCustomHoverName()) {
            bgx9.setName(bcj.getHoverName());
        }
        if (!bhr.isClientSide) {
            if (bcj.getTagElement("BlockEntityTag") == null) {
                bgx9.setTrackOutput(bhr.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
                bub8.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
            }
            if (bub8.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
                final boolean boolean10 = bhr.hasNeighborSignal(ew);
                bub8.setPowered(boolean10);
            }
        }
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)CommandBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)CommandBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)CommandBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CommandBlock.FACING, CommandBlock.CONDITIONAL);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)CommandBlock.FACING, ban.getNearestLookingDirection().getOpposite());
    }
    
    private static void executeChain(final Level bhr, final BlockPos ew, Direction fb) {
        final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos(ew);
        final GameRules bhn5 = bhr.getGameRules();
        int integer6 = bhn5.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
        while (integer6-- > 0) {
            a4.move(fb);
            final BlockState bvt7 = bhr.getBlockState(a4);
            final Block bmv8 = bvt7.getBlock();
            if (bmv8 != Blocks.CHAIN_COMMAND_BLOCK) {
                break;
            }
            final BlockEntity btw9 = bhr.getBlockEntity(a4);
            if (!(btw9 instanceof CommandBlockEntity)) {
                break;
            }
            final CommandBlockEntity bub10 = (CommandBlockEntity)btw9;
            if (bub10.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
                break;
            }
            if (bub10.isPowered() || bub10.isAutomatic()) {
                final BaseCommandBlock bgx11 = bub10.getCommandBlock();
                if (bub10.markConditionMet()) {
                    if (!bgx11.performCommand(bhr)) {
                        break;
                    }
                    bhr.updateNeighbourForOutputSignal(a4, bmv8);
                }
                else if (bub10.isConditional()) {
                    bgx11.setSuccessCount(0);
                }
            }
            fb = bvt7.<Direction>getValue((Property<Direction>)CommandBlock.FACING);
        }
        if (integer6 <= 0) {
            final int integer7 = Math.max(bhn5.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
            CommandBlock.LOGGER.warn("Command Block chain tried to execute more than {} steps!", integer7);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        FACING = DirectionalBlock.FACING;
        CONDITIONAL = BlockStateProperties.CONDITIONAL;
    }
}

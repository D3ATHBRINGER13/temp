package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class JigsawBlock extends DirectionalBlock implements EntityBlock {
    protected JigsawBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)JigsawBlock.FACING, Direction.UP));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(JigsawBlock.FACING);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)JigsawBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)JigsawBlock.FACING)));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)JigsawBlock.FACING, ban.getClickedFace());
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new JigsawBlockEntity();
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof JigsawBlockEntity && awg.canUseGameMasterBlocks()) {
            awg.openJigsawBlock((JigsawBlockEntity)btw8);
            return true;
        }
        return false;
    }
    
    public static boolean canAttach(final StructureTemplate.StructureBlockInfo b1, final StructureTemplate.StructureBlockInfo b2) {
        return b1.state.<Comparable>getValue((Property<Comparable>)JigsawBlock.FACING) == b2.state.<Direction>getValue((Property<Direction>)JigsawBlock.FACING).getOpposite() && b1.nbt.getString("attachement_type").equals(b2.nbt.getString("attachement_type"));
    }
}

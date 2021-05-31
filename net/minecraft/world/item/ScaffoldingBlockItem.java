package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.chat.ChatType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class ScaffoldingBlockItem extends BlockItem {
    public ScaffoldingBlockItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Nullable
    @Override
    public BlockPlaceContext updatePlacementContext(final BlockPlaceContext ban) {
        final BlockPos ew3 = ban.getClickedPos();
        final Level bhr4 = ban.getLevel();
        BlockState bvt5 = bhr4.getBlockState(ew3);
        final Block bmv6 = this.getBlock();
        if (bvt5.getBlock() == bmv6) {
            Direction fb7;
            if (ban.isSneaking()) {
                fb7 = (ban.isInside() ? ban.getClickedFace().getOpposite() : ban.getClickedFace());
            }
            else {
                fb7 = ((ban.getClickedFace() == Direction.UP) ? ban.getHorizontalDirection() : Direction.UP);
            }
            int integer8 = 0;
            final BlockPos.MutableBlockPos a9 = new BlockPos.MutableBlockPos(ew3).move(fb7);
            while (integer8 < 7) {
                if (!bhr4.isClientSide && !Level.isInWorldBounds(a9)) {
                    final Player awg10 = ban.getPlayer();
                    final int integer9 = bhr4.getMaxBuildHeight();
                    if (awg10 instanceof ServerPlayer && a9.getY() >= integer9) {
                        final ClientboundChatPacket kv12 = new ClientboundChatPacket(new TranslatableComponent("build.tooHigh", new Object[] { integer9 }).withStyle(ChatFormatting.RED), ChatType.GAME_INFO);
                        ((ServerPlayer)awg10).connection.send(kv12);
                        break;
                    }
                    break;
                }
                else {
                    bvt5 = bhr4.getBlockState(a9);
                    if (bvt5.getBlock() != this.getBlock()) {
                        if (bvt5.canBeReplaced(ban)) {
                            return BlockPlaceContext.at(ban, a9, fb7);
                        }
                        break;
                    }
                    else {
                        a9.move(fb7);
                        if (!fb7.getAxis().isHorizontal()) {
                            continue;
                        }
                        ++integer8;
                    }
                }
            }
            return null;
        }
        if (ScaffoldingBlock.getDistance(bhr4, ew3) == 7) {
            return null;
        }
        return ban;
    }
    
    @Override
    protected boolean mustSurvive() {
        return false;
    }
}

package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Map;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Iterator;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;

public class BlockItem extends Item {
    @Deprecated
    private final Block block;
    
    public BlockItem(final Block bmv, final Properties a) {
        super(a);
        this.block = bmv;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final InteractionResult ahj3 = this.place(new BlockPlaceContext(bdu));
        if (ahj3 != InteractionResult.SUCCESS && this.isEdible()) {
            return this.use(bdu.level, bdu.player, bdu.hand).getResult();
        }
        return ahj3;
    }
    
    public InteractionResult place(final BlockPlaceContext ban) {
        if (!ban.canPlace()) {
            return InteractionResult.FAIL;
        }
        final BlockPlaceContext ban2 = this.updatePlacementContext(ban);
        if (ban2 == null) {
            return InteractionResult.FAIL;
        }
        final BlockState bvt4 = this.getPlacementState(ban2);
        if (bvt4 == null) {
            return InteractionResult.FAIL;
        }
        if (!this.placeBlock(ban2, bvt4)) {
            return InteractionResult.FAIL;
        }
        final BlockPos ew5 = ban2.getClickedPos();
        final Level bhr6 = ban2.getLevel();
        final Player awg7 = ban2.getPlayer();
        final ItemStack bcj8 = ban2.getItemInHand();
        BlockState bvt5 = bhr6.getBlockState(ew5);
        final Block bmv10 = bvt5.getBlock();
        if (bmv10 == bvt4.getBlock()) {
            bvt5 = this.updateBlockStateFromTag(ew5, bhr6, bcj8, bvt5);
            this.updateCustomBlockEntityTag(ew5, bhr6, awg7, bcj8, bvt5);
            bmv10.setPlacedBy(bhr6, ew5, bvt5, awg7, bcj8);
            if (awg7 instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)awg7, ew5, bcj8);
            }
        }
        final SoundType bry11 = bvt5.getSoundType();
        bhr6.playSound(awg7, ew5, this.getPlaceSound(bvt5), SoundSource.BLOCKS, (bry11.getVolume() + 1.0f) / 2.0f, bry11.getPitch() * 0.8f);
        bcj8.shrink(1);
        return InteractionResult.SUCCESS;
    }
    
    protected SoundEvent getPlaceSound(final BlockState bvt) {
        return bvt.getSoundType().getPlaceSound();
    }
    
    @Nullable
    public BlockPlaceContext updatePlacementContext(final BlockPlaceContext ban) {
        return ban;
    }
    
    protected boolean updateCustomBlockEntityTag(final BlockPos ew, final Level bhr, @Nullable final Player awg, final ItemStack bcj, final BlockState bvt) {
        return updateCustomBlockEntityTag(bhr, awg, ew, bcj);
    }
    
    @Nullable
    protected BlockState getPlacementState(final BlockPlaceContext ban) {
        final BlockState bvt3 = this.getBlock().getStateForPlacement(ban);
        return (bvt3 != null && this.canPlace(ban, bvt3)) ? bvt3 : null;
    }
    
    private BlockState updateBlockStateFromTag(final BlockPos ew, final Level bhr, final ItemStack bcj, final BlockState bvt) {
        BlockState bvt2 = bvt;
        final CompoundTag id7 = bcj.getTag();
        if (id7 != null) {
            final CompoundTag id8 = id7.getCompound("BlockStateTag");
            final StateDefinition<Block, BlockState> bvu9 = bvt2.getBlock().getStateDefinition();
            for (final String string11 : id8.getAllKeys()) {
                final Property<?> bww12 = bvu9.getProperty(string11);
                if (bww12 != null) {
                    final String string12 = id8.get(string11).getAsString();
                    bvt2 = BlockItem.updateState(bvt2, bww12, string12);
                }
            }
        }
        if (bvt2 != bvt) {
            bhr.setBlock(ew, bvt2, 2);
        }
        return bvt2;
    }
    
    private static <T extends Comparable<T>> BlockState updateState(final BlockState bvt, final Property<T> bww, final String string) {
        return (BlockState)bww.getValue(string).map(comparable -> ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Comparable>setValue((Property<Comparable>)bww, comparable)).orElse(bvt);
    }
    
    protected boolean canPlace(final BlockPlaceContext ban, final BlockState bvt) {
        final Player awg4 = ban.getPlayer();
        final CollisionContext csn5 = (awg4 == null) ? CollisionContext.empty() : CollisionContext.of(awg4);
        return (!this.mustSurvive() || bvt.canSurvive(ban.getLevel(), ban.getClickedPos())) && ban.getLevel().isUnobstructed(bvt, ban.getClickedPos(), csn5);
    }
    
    protected boolean mustSurvive() {
        return true;
    }
    
    protected boolean placeBlock(final BlockPlaceContext ban, final BlockState bvt) {
        return ban.getLevel().setBlock(ban.getClickedPos(), bvt, 11);
    }
    
    public static boolean updateCustomBlockEntityTag(final Level bhr, @Nullable final Player awg, final BlockPos ew, final ItemStack bcj) {
        final MinecraftServer minecraftServer5 = bhr.getServer();
        if (minecraftServer5 == null) {
            return false;
        }
        final CompoundTag id6 = bcj.getTagElement("BlockEntityTag");
        if (id6 != null) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 != null) {
                if (!bhr.isClientSide && btw7.onlyOpCanSetNbt() && (awg == null || !awg.canUseGameMasterBlocks())) {
                    return false;
                }
                final CompoundTag id7 = btw7.save(new CompoundTag());
                final CompoundTag id8 = id7.copy();
                id7.merge(id6);
                id7.putInt("x", ew.getX());
                id7.putInt("y", ew.getY());
                id7.putInt("z", ew.getZ());
                if (!id7.equals(id8)) {
                    btw7.load(id7);
                    btw7.setChanged();
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }
    
    @Override
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        if (this.allowdedIn(bba)) {
            this.getBlock().fillItemCategory(bba, fk);
        }
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        super.appendHoverText(bcj, bhr, list, bdr);
        this.getBlock().appendHoverText(bcj, bhr, list, bdr);
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public void registerBlocks(final Map<Block, Item> map, final Item bce) {
        map.put(this.getBlock(), bce);
    }
}

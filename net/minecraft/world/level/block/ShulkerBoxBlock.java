package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.function.Consumer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import java.util.Iterator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ShulkerBoxBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING;
    public static final ResourceLocation CONTENTS;
    @Nullable
    private final DyeColor color;
    
    public ShulkerBoxBlock(@Nullable final DyeColor bbg, final Properties c) {
        super(c);
        this.color = bbg;
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Direction, Direction>setValue(ShulkerBoxBlock.FACING, Direction.UP));
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new ShulkerBoxBlockEntity(this.color);
    }
    
    @Override
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return true;
    }
    
    @Override
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return true;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        if (awg.isSpectator()) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof ShulkerBoxBlockEntity) {
            final Direction fb9 = bvt.<Direction>getValue(ShulkerBoxBlock.FACING);
            final ShulkerBoxBlockEntity bur11 = (ShulkerBoxBlockEntity)btw8;
            boolean boolean10;
            if (bur11.getAnimationStatus() == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
                final AABB csc12 = Shapes.block().bounds().expandTowards(0.5f * fb9.getStepX(), 0.5f * fb9.getStepY(), 0.5f * fb9.getStepZ()).contract(fb9.getStepX(), fb9.getStepY(), fb9.getStepZ());
                boolean10 = bhr.noCollision(csc12.move(ew.relative(fb9)));
            }
            else {
                boolean10 = true;
            }
            if (boolean10) {
                awg.openMenu(bur11);
                awg.awardStat(Stats.OPEN_SHULKER_BOX);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Direction, Direction>setValue(ShulkerBoxBlock.FACING, ban.getClickedFace());
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ShulkerBoxBlock.FACING);
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        if (btw6 instanceof ShulkerBoxBlockEntity) {
            final ShulkerBoxBlockEntity bur7 = (ShulkerBoxBlockEntity)btw6;
            if (!bhr.isClientSide && awg.isCreative() && !bur7.isEmpty()) {
                final ItemStack bcj8 = getColoredItemStack(this.getColor());
                final CompoundTag id9 = bur7.saveToTag(new CompoundTag());
                if (!id9.isEmpty()) {
                    bcj8.addTagElement("BlockEntityTag", (Tag)id9);
                }
                if (bur7.hasCustomName()) {
                    bcj8.setHoverName(bur7.getCustomName());
                }
                final ItemEntity atx10 = new ItemEntity(bhr, ew.getX(), ew.getY(), ew.getZ(), bcj8);
                atx10.setDefaultPickUpDelay();
                bhr.addFreshEntity(atx10);
            }
            else {
                bur7.unpackLootTable(awg);
            }
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Override
    public List<ItemStack> getDrops(final BlockState bvt, LootContext.Builder a) {
        final BlockEntity btw4 = a.<BlockEntity>getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (btw4 instanceof ShulkerBoxBlockEntity) {
            final ShulkerBoxBlockEntity bur5 = (ShulkerBoxBlockEntity)btw4;
            int integer4;
            final ShulkerBoxBlockEntity shulkerBoxBlockEntity;
            a = a.withDynamicDrop(ShulkerBoxBlock.CONTENTS, (coy, consumer) -> {
                for (integer4 = 0; integer4 < shulkerBoxBlockEntity.getContainerSize(); ++integer4) {
                    consumer.accept(shulkerBoxBlockEntity.getItem(integer4));
                }
                return;
            });
        }
        return super.getDrops(bvt, a);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof ShulkerBoxBlockEntity) {
                ((ShulkerBoxBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof ShulkerBoxBlockEntity) {
            bhr.updateNeighbourForOutputSignal(ew, bvt1.getBlock());
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final BlockGetter bhb, final List<Component> list, final TooltipFlag bdr) {
        super.appendHoverText(bcj, bhb, list, bdr);
        final CompoundTag id6 = bcj.getTagElement("BlockEntityTag");
        if (id6 != null) {
            if (id6.contains("LootTable", 8)) {
                list.add(new TextComponent("???????"));
            }
            if (id6.contains("Items", 9)) {
                final NonNullList<ItemStack> fk7 = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(id6, fk7);
                int integer8 = 0;
                int integer9 = 0;
                for (final ItemStack bcj2 : fk7) {
                    if (!bcj2.isEmpty()) {
                        ++integer9;
                        if (integer8 > 4) {
                            continue;
                        }
                        ++integer8;
                        final Component jo12 = bcj2.getHoverName().deepCopy();
                        jo12.append(" x").append(String.valueOf(bcj2.getCount()));
                        list.add(jo12);
                    }
                }
                if (integer9 - integer8 > 0) {
                    list.add(new TranslatableComponent("container.shulkerBox.more", new Object[] { integer9 - integer8 }).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final BlockEntity btw6 = bhb.getBlockEntity(ew);
        if (btw6 instanceof ShulkerBoxBlockEntity) {
            return Shapes.create(((ShulkerBoxBlockEntity)btw6).getBoundingBox(bvt));
        }
        return Shapes.block();
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return false;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)bhr.getBlockEntity(ew));
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        final ItemStack bcj5 = super.getCloneItemStack(bhb, ew, bvt);
        final ShulkerBoxBlockEntity bur6 = (ShulkerBoxBlockEntity)bhb.getBlockEntity(ew);
        final CompoundTag id7 = bur6.saveToTag(new CompoundTag());
        if (!id7.isEmpty()) {
            bcj5.addTagElement("BlockEntityTag", (Tag)id7);
        }
        return bcj5;
    }
    
    @Nullable
    public static DyeColor getColorFromItem(final Item bce) {
        return getColorFromBlock(Block.byItem(bce));
    }
    
    @Nullable
    public static DyeColor getColorFromBlock(final Block bmv) {
        if (bmv instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)bmv).getColor();
        }
        return null;
    }
    
    public static Block getBlockByColor(@Nullable final DyeColor bbg) {
        if (bbg == null) {
            return Blocks.SHULKER_BOX;
        }
        switch (bbg) {
            case WHITE: {
                return Blocks.WHITE_SHULKER_BOX;
            }
            case ORANGE: {
                return Blocks.ORANGE_SHULKER_BOX;
            }
            case MAGENTA: {
                return Blocks.MAGENTA_SHULKER_BOX;
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_SHULKER_BOX;
            }
            case YELLOW: {
                return Blocks.YELLOW_SHULKER_BOX;
            }
            case LIME: {
                return Blocks.LIME_SHULKER_BOX;
            }
            case PINK: {
                return Blocks.PINK_SHULKER_BOX;
            }
            case GRAY: {
                return Blocks.GRAY_SHULKER_BOX;
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_SHULKER_BOX;
            }
            case CYAN: {
                return Blocks.CYAN_SHULKER_BOX;
            }
            default: {
                return Blocks.PURPLE_SHULKER_BOX;
            }
            case BLUE: {
                return Blocks.BLUE_SHULKER_BOX;
            }
            case BROWN: {
                return Blocks.BROWN_SHULKER_BOX;
            }
            case GREEN: {
                return Blocks.GREEN_SHULKER_BOX;
            }
            case RED: {
                return Blocks.RED_SHULKER_BOX;
            }
            case BLACK: {
                return Blocks.BLACK_SHULKER_BOX;
            }
        }
    }
    
    @Nullable
    public DyeColor getColor() {
        return this.color;
    }
    
    public static ItemStack getColoredItemStack(@Nullable final DyeColor bbg) {
        return new ItemStack(getBlockByColor(bbg));
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Direction, Direction>setValue(ShulkerBoxBlock.FACING, brg.rotate(bvt.<Direction>getValue(ShulkerBoxBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue(ShulkerBoxBlock.FACING)));
    }
    
    static {
        FACING = DirectionalBlock.FACING;
        CONTENTS = new ResourceLocation("contents");
    }
}
